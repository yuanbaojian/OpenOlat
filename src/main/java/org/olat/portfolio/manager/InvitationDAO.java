/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.portfolio.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.TypedQuery;

import org.olat.admin.user.delete.service.UserDeletionManager;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.Group;
import org.olat.basesecurity.GroupRoles;
import org.olat.basesecurity.IdentityRef;
import org.olat.basesecurity.Invitation;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.OrganisationService;
import org.olat.basesecurity.manager.GroupDAO;
import org.olat.basesecurity.manager.OrganisationDAO;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.core.id.User;
import org.olat.core.id.UserConstants;
import org.olat.portfolio.model.InvitationImpl;
import org.olat.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is only for e-Portfolio. For wider useage, need a refactor of the datamodel
 * and of process and workflow. Don't be afraid of reference ot e-Portfolio datamodel
 * here.
 * 
 * 
 * Initial date: 25.06.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service(value="invitationDao")
public class InvitationDAO {
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private GroupDAO groupDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private OrganisationDAO organisationDao;
	@Autowired
	private UserDeletionManager userDeletionManager;
	@Autowired
	private OrganisationService organisationService;
	
	public Invitation createInvitation() {
		InvitationImpl invitation = new InvitationImpl();
		invitation.setToken(UUID.randomUUID().toString());
		return invitation;
	}
	
	/**
	 * Create and persist an invitation with its security group and security token.
	 * @return
	 */
	public Invitation createAndPersistInvitation() {
		Group group = groupDao.createGroup();
		
		InvitationImpl invitation = new InvitationImpl();
		invitation.setToken(UUID.randomUUID().toString());
		invitation.setBaseGroup(group);
		dbInstance.getCurrentEntityManager().persist(invitation);
		return invitation;
	}
	
	public Invitation update(Invitation invitation, String firstName, String lastName, String email) {
		List<Identity> identities = groupDao.getMembers(invitation.getBaseGroup(), GroupRoles.invitee.name());
		for(Identity identity:identities) {
			User user = identity.getUser();
			if(email.equals(user.getEmail())) {
				user.setProperty(UserConstants.FIRSTNAME, firstName);
				user.setProperty(UserConstants.LASTNAME, lastName);
				user.setProperty(UserConstants.EMAIL, email);
				userManager.updateUserFromIdentity(identity);
			}
		}
		
		invitation.setFirstName(firstName);
		invitation.setLastName(lastName);
		invitation.setMail(email);
		return dbInstance.getCurrentEntityManager().merge(invitation);
	}
	
	public Identity createIdentityFrom(Invitation invitation, Locale locale) {
		if(invitation.getIdentity() != null) {
			return securityManager.loadIdentityByKey(invitation.getIdentity().getKey());
		}
		
		String tempUsername = UUID.randomUUID().toString();
		User user = userManager.createUser(invitation.getFirstName(), invitation.getLastName(), invitation.getMail());
		user.getPreferences().setLanguage(locale.toString());
		Identity invitee = securityManager.createAndPersistIdentityAndUser(tempUsername, null, user, null, null);
		groupDao.addMembershipTwoWay(invitation.getBaseGroup(), invitee, GroupRoles.invitee.name());
		organisationService.addMember(invitee, OrganisationRoles.invitee);
		return invitee;
	}
	
	public Identity loadOrCreateIdentityAndPersistInvitation(Invitation invitation, Group group, Locale locale) {
		// create identity only if such a user does not already exist

		Identity invitee;
		if(invitation.getIdentity() != null) {
			invitee = invitation.getIdentity();
		} else {
			invitee = userManager.findUniqueIdentityByEmail(invitation.getMail());
			if (invitee == null) {
				String tempUsername = UUID.randomUUID().toString();
				User user = userManager.createUser(invitation.getFirstName(), invitation.getLastName(), invitation.getMail());
				user.getPreferences().setLanguage(locale.toString());
				invitee = securityManager.createAndPersistIdentityAndUser(tempUsername, null, user, null, null, null);
			}
		}
		
		// create the invitation
		group = groupDao.loadGroup(group.getKey());
		((InvitationImpl)invitation).setCreationDate(new Date());
		((InvitationImpl)invitation).setBaseGroup(group);
		((InvitationImpl)invitation).setIdentity(invitee);
		dbInstance.getCurrentEntityManager().persist(invitation);

		// add invitee to the security group of that portfolio element
		groupDao.addMembershipTwoWay(group, invitee, GroupRoles.invitee.name());
		organisationService.addMember(invitee, OrganisationRoles.invitee);			
		return invitee;
	}
	
	/**
	 * Is the invitation linked to any valid policies
	 * @param token
	 * @param atDate
	 * @return
	 */
	public boolean hasInvitations(String token, Date atDate) {
		StringBuilder sb = new StringBuilder();
		sb.append("select invitation.key from binvitation as invitation")
		  .append(" inner join invitation.baseGroup as baseGroup")
	      .append(" where invitation.token=:token and ")
	      .append(" (exists (select relation.key from structuretogroup as relation ")
	      .append("  where relation.group.key=baseGroup.key");
		if(atDate != null) {
			sb.append(" and (relation.validFrom is null or relation.validFrom<=:date)")
			  .append(" and (relation.validTo is null or relation.validTo>=:date)");
		}
	    sb.append(" ) or exists(select binder from pfbinder as binder")
	      .append("   where binder.baseGroup.key=baseGroup.key")
	      .append("))");

		TypedQuery<Long> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Long.class)
				.setParameter("token", token);
		if(atDate != null) {
		  	query.setParameter("date", atDate);
		}
		  
		List<Long> keys = query
				.setFirstResult(0)
				.setMaxResults(1)
				.getResultList();
	    return keys == null || keys.isEmpty() || keys.get(0) == null ? false : keys.get(0).intValue() > 0;
	}
	
	/**
	 * Find an invitation by its security token
	 * @param token
	 * @return The invitation or null if not found
	 */
	public Invitation loadByKey(Long key) {
		StringBuilder sb = new StringBuilder();
		sb.append("select invitation from binvitation as invitation ")
		  .append(" inner join fetch invitation.baseGroup bGroup")
		  .append(" where invitation.key=:key");

		List<Invitation> invitations = dbInstance.getCurrentEntityManager()
			.createQuery(sb.toString(), Invitation.class)
			.setParameter("key", key)
			.getResultList();
	    return invitations.isEmpty() ? null : invitations.get(0);
	}
	
	/**
	 * Find an invitation by its security group
	 * @param secGroup
	 * @return The invitation or null if not found
	 */
	public Invitation findInvitation(Group group) {
		StringBuilder sb = new StringBuilder();
		sb.append("select invitation from binvitation as invitation ")
		  .append(" inner join fetch invitation.baseGroup bGroup")
		  .append(" where bGroup=:group");

		List<Invitation> invitations = dbInstance.getCurrentEntityManager()
				  .createQuery(sb.toString(), Invitation.class)
				  .setParameter("group", group)
				  .getResultList();
		if(invitations.isEmpty()) return null;
		return invitations.get(0);
	}
	
	/**
	 * 
	 * Warning! The E-mail is used in this case as a foreign key to match
	 * the identity and the invitation on a base group which ca have several
	 * identities.
	 * 
	 * @param group
	 * @param identity
	 * @return
	 */
	public Invitation findInvitation(Group group, IdentityRef identity) {
		StringBuilder sb = new StringBuilder();
		sb.append("select invitation from binvitation as invitation ")
		  .append(" inner join fetch invitation.baseGroup bGroup")
		  .append(" inner join bGroup.members as members")
		  .append(" inner join members.identity as identity")
		  .append(" inner join identity.user as user")
		  .append(" where bGroup.key=:groupKey and identity.key=:inviteeKey and members.role=:role and invitation.mail=user.email");

		List<Invitation> invitations = dbInstance.getCurrentEntityManager()
				  .createQuery(sb.toString(), Invitation.class)
				  .setParameter("groupKey", group.getKey())
				  .setParameter("inviteeKey", identity.getKey())
				  .setParameter("role", GroupRoles.invitee.name())
				  .getResultList();
		if(invitations.isEmpty()) return null;
		return invitations.get(0);
	}
	
	/**
	 * Find an invitation by its security token
	 * @param token
	 * @return The invitation or null if not found
	 */
	public Invitation findInvitation(String token) {
		StringBuilder sb = new StringBuilder();
		sb.append("select invitation from binvitation as invitation")
		  .append(" inner join fetch invitation.baseGroup bGroup")
		  .append(" left join fetch invitation.identity ident")
		  .append(" where invitation.token=:token");

		List<Invitation> invitations = dbInstance.getCurrentEntityManager()
			.createQuery(sb.toString(), Invitation.class)
			.setParameter("token", token)
			.getResultList();
	    return invitations.isEmpty() ? null : invitations.get(0);
	}
	
	
	/**
	 * the number of invitations
	 * @return
	 */
	public long countInvitations() {
		String sb = "select count(invitation) from binvitation as invitation";
		Number invitations = dbInstance.getCurrentEntityManager()
				.createQuery(sb, Number.class)
				.getSingleResult();		
		return invitations == null ? 0l : invitations.longValue();
	}
	
	/**
	 * Check if the identity has an invitation, valid or not
	 * @param identity
	 * @return
	 */
	public boolean isInvitee(IdentityRef identity) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(invitation) from binvitation as invitation")
		  .append(" inner join invitation.baseGroup as baseGroup ")
		  .append(" inner join baseGroup.members as members")
		  .append(" inner join members.identity as ident")
		  .append(" where ident.key=:identityKey");
		  
		Number invitations = dbInstance.getCurrentEntityManager()
			.createQuery(sb.toString(), Number.class)
			.setParameter("identityKey", identity.getKey())
			.getSingleResult();
	    return invitations == null ? false : invitations.intValue() > 0;
	}
	
	/**
	 * Delete an invitation
	 * @param invitation
	 */
	public void deleteInvitation(Invitation invitation) {
		if(invitation == null || invitation.getKey() == null) return;
		
		Invitation refInvitation = dbInstance.getCurrentEntityManager()
			.getReference(InvitationImpl.class, invitation.getKey());
		dbInstance.getCurrentEntityManager().remove(refInvitation);
	}
	
	/**
	 * Clean up old invitation and set to deleted temporary users
	 */
	public void cleanUpInvitations() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Date currentTime = cal.getTime();
		cal.add(Calendar.HOUR, -6);
		Date dateLimit = cal.getTime();

		StringBuilder sb = new StringBuilder(512);
		sb.append("select invitation from ").append(InvitationImpl.class.getName()).append(" as invitation ")
		  .append(" inner join invitation.baseGroup baseGroup ")
		  .append(" where invitation.creationDate<:dateLimit")//someone can create an invitation but not add it to a policy within millisecond
		  .append(" and not exists (")
		  //select all valid policies from this security group
		  .append("  select policy.group from structuretogroup as policy ")
		  .append("   where policy.group=baseGroup ")
		  .append("   and (policy.validFrom is null or policy.validFrom<=:currentDate)")
		  .append("   and (policy.validTo is null or policy.validTo>=:currentDate)")
		  .append(" )");

		List<Invitation> oldInvitations = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Invitation.class)
				.setParameter("currentDate", currentTime)
				.setParameter("dateLimit", dateLimit)
				.getResultList();
		
		if(oldInvitations.isEmpty()) {
			return;
		}
	  
		for(Invitation invitation:oldInvitations) {
			List<Identity> identities = groupDao.getMembers(invitation.getBaseGroup(), GroupRoles.invitee.name());
			//normally only one identity
			for(Identity identity:identities) {
				if(identity.getStatus().compareTo(Identity.STATUS_VISIBLE_LIMIT) >= 0) {
					//already deleted
				} else if(organisationDao.hasAnyRole(identity, OrganisationRoles.invitee.name())) {
					//out of scope
				} else {
					//delete user
					userDeletionManager.deleteIdentity(identity, null);
				}
			}
			Invitation invitationRef = dbInstance.getCurrentEntityManager()
				.getReference(InvitationImpl.class, invitation.getKey());
			dbInstance.getCurrentEntityManager().remove(invitationRef);
			dbInstance.commit();
		}
	}
}
