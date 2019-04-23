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
package org.olat.basesecurity.manager;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.olat.basesecurity.Grant;
import org.olat.basesecurity.Group;
import org.olat.basesecurity.GroupMembership;
import org.olat.basesecurity.GroupMembershipInheritance;
import org.olat.basesecurity.IdentityRef;
import org.olat.basesecurity.model.GrantImpl;
import org.olat.basesecurity.model.GroupImpl;
import org.olat.basesecurity.model.GroupMembershipImpl;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.resource.OLATResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 20.02.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service("groupDao")
public class GroupDAO {
	
	@Autowired
	private DB dbInstance;
	
	public Group createGroup() {
		GroupImpl group = new GroupImpl();
		group.setCreationDate(new Date());
		dbInstance.getCurrentEntityManager().persist(group);
		return group;
	}
	
	public Group createGroup(String name) {
		GroupImpl group = new GroupImpl();
		group.setCreationDate(new Date());
		group.setName(name);
		dbInstance.getCurrentEntityManager().persist(group);
		return group;
	}
	
	
	public Group removeGroup(Group group) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		GroupImpl reloadedGroup = em.getReference(GroupImpl.class, group.getKey());
		em.remove(reloadedGroup);
		return group;
	}
	
	public Group loadGroup(Long key) {
		return dbInstance.getCurrentEntityManager().find(GroupImpl.class, key);
	}
	
	/**
	 * Add a membership to the group, in the set of the group too which can be
	 * reloaded.
	 */
	public GroupMembership addMembershipTwoWay(Group group, Identity identity, String role) {
		return addMembershipTwoWay(group, identity, role, GroupMembershipInheritance.none);
	}

	public GroupMembership addMembershipTwoWay(Group group, Identity identity, String role, GroupMembershipInheritance inheritanceMode) {
		GroupMembershipImpl membership = new GroupMembershipImpl();
		membership.setCreationDate(new Date());
		membership.setLastModified(new Date());
		membership.setGroup(group);
		membership.setIdentity(identity);
		membership.setRole(role);
		membership.setInheritanceMode(inheritanceMode);
		dbInstance.getCurrentEntityManager().persist(membership);
		
		Set<GroupMembership> members = ((GroupImpl)group).getMembers();
		if(members == null) {
			members = new HashSet<>();
			((GroupImpl)group).setMembers(members);
		}
		members.add(membership);
		return membership;
	}
	
	/**
	 * Create a membership without updating the set in the group.
	 */
	public void addMembershipOneWay(Group group, Identity identity, String role) {
		addMembershipOneWay(group, identity, role, GroupMembershipInheritance.none);
	}
	
	/**
	 * Create a membership without updating the set in the group.
	 * 
	 * @param group
	 * @param identity
	 * @param role
	 * @param inheritanceMode
	 */
	public void addMembershipOneWay(Group group, Identity identity, String role, GroupMembershipInheritance inheritanceMode) {
		GroupMembershipImpl membership = new GroupMembershipImpl();
		membership.setCreationDate(new Date());
		membership.setLastModified(new Date());
		membership.setGroup(group);
		membership.setIdentity(identity);
		membership.setRole(role);
		membership.setInheritanceMode(inheritanceMode);
		dbInstance.getCurrentEntityManager().persist(membership);
	}
	
	public GroupMembership updateInheritanceMode(GroupMembership membership, GroupMembershipInheritance inheritanceMode) {
		((GroupMembershipImpl)membership).setInheritanceMode(inheritanceMode);
		return dbInstance.getCurrentEntityManager().merge(membership);
	}
	
	public int removeMemberships(Group group) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		List<GroupMembership> memberships = em.createNamedQuery("membershipsByGroup", GroupMembership.class)
			.setParameter("groupKey", group.getKey())
			.getResultList();
		for(GroupMembership membership:memberships) {
			em.remove(membership);
		}
		return memberships.size();
	}
	
	public int removeMemberships(Group group, String role) {
		return dbInstance.getCurrentEntityManager().createNamedQuery("deleteMembershipsByGroupAndRole")
				.setParameter("groupKey", group.getKey())
				.setParameter("role", role)
				.executeUpdate();
	}
	
	public int removeMembership(Group group, IdentityRef identity) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		List<GroupMembership> memberships = em.createNamedQuery("membershipsByGroupAndIdentity", GroupMembership.class)
			.setParameter("groupKey", group.getKey())
			.setParameter("identityKey", identity.getKey())
			.getResultList();
		for(GroupMembership membership:memberships) {
			em.remove(membership);
		}
		return memberships.size();
	}
	
	public int removeMembership(Group group, IdentityRef identity, String role) {
		EntityManager em = dbInstance.getCurrentEntityManager();
		List<GroupMembership> memberships = em.createNamedQuery("membershipsByGroupIdentityAndRole", GroupMembership.class)
			.setParameter("groupKey", group.getKey())
			.setParameter("identityKey", identity.getKey())
			.setParameter("role", role)
			.getResultList();
		for(GroupMembership membership:memberships) {
			em.remove(membership);
		}
		return memberships.size();
	}
	
	public int removeMemberships(IdentityRef identity) {
		String deleteQuery = "delete from bgroupmember as membership where membership.identity.key=:identityKey";
		
		return dbInstance.getCurrentEntityManager()
				.createQuery(deleteQuery).setParameter("identityKey", identity.getKey())
				.executeUpdate();
	}
	
	public void removeMembership(GroupMembership membership) {
		dbInstance.getCurrentEntityManager().remove(membership);
	}
	
	public int countMembers(Group group) {
		Number count = dbInstance.getCurrentEntityManager()
			.createNamedQuery("countMembersByGroup", Number.class)
			.setParameter("groupKey", group.getKey())
			.getSingleResult();
		return count == null ? 0 : count.intValue();
	}
	
	public boolean hasRole(Group group, Identity identity, String role) {
		Number count = dbInstance.getCurrentEntityManager()
			.createNamedQuery("hasRoleByGroupIdentityAndRole", Number.class)
			.setParameter("groupKey", group.getKey())
			.setParameter("identityKey", identity.getKey())
			.setParameter("role", role)
			.getSingleResult();
		return count == null ? false : count.intValue() > 0;
	}
	
	public List<Identity> getMembers(Group group, String role) {
		return dbInstance.getCurrentEntityManager()
			.createNamedQuery("membersByGroupAndRole", Identity.class)
			.setParameter("groupKey", group.getKey())
			.setParameter("role", role)
			.getResultList();
	}
	
	public List<GroupMembership> getMemberships(Group group) {
		return dbInstance.getCurrentEntityManager()
			.createNamedQuery("membershipsByGroup", GroupMembership.class)
			.setParameter("groupKey", group.getKey())
			.getResultList();
	}
	
	public List<GroupMembership> getMemberships(Group group, String role) {
		return dbInstance.getCurrentEntityManager()
			.createNamedQuery("membershipsByGroupAndRole", GroupMembership.class)
			.setParameter("groupKey", group.getKey())
			.setParameter("role", role)
			.getResultList();
	}
	
	public List<GroupMembership> getMemberships(Group group, IdentityRef identity) {
		return dbInstance.getCurrentEntityManager()
			.createNamedQuery("membershipsByGroupAndIdentity", GroupMembership.class)
			.setParameter("groupKey", group.getKey())
			.setParameter("identityKey", identity.getKey())
			.getResultList();
	}
	
	public GroupMembership getMembership(Group group, IdentityRef identity, String role) {
		List<GroupMembership> memberships = dbInstance.getCurrentEntityManager()
			.createNamedQuery("membershipByGroupIdentityAndRole", GroupMembership.class)
			.setParameter("groupKey", group.getKey())
			.setParameter("identityKey", identity.getKey())
			.setParameter("role", role)
			.getResultList();
		return memberships == null || memberships.isEmpty() ? null : memberships.get(0);
	}
	
	public boolean hasGrant(IdentityRef identity, String permission, OLATResource resource) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(grant) from bgrant as grant")
		  .append(" inner join grant.group as baseGroup")
		  .append(" inner join baseGroup.members as membership")
		  .append(" where membership.identity.key=:identityKey and grant.resource.key=:resourceKey")
		  .append("   and grant.permission=:permission and membership.role=grant.role");
		Number count = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Number.class)
				.setParameter("identityKey", identity.getKey())
				.setParameter("resourceKey", resource.getKey())
				.setParameter("permission", permission)
				.getSingleResult();
		return count == null ? false: count.intValue() > 0;
	}
	
	public List<String> getPermissions(IdentityRef identity, OLATResource resource) {
		return dbInstance.getCurrentEntityManager()
				.createNamedQuery("grantedPermissionByIdentityAndResource", String.class)
				.setParameter("identityKey", identity.getKey())
				.setParameter("resourceKey", resource.getKey())
				.getResultList();
	}
	
	public List<Grant> getGrants(Group group, String role) {
		StringBuilder sb = new StringBuilder();
		sb.append("select grant from bgrant as grant")
		  .append(" inner join fetch grant.group as baseGroup")
		  .append(" inner join fetch grant.resource as res")
		  .append(" where baseGroup.key=:groupKey and grant.role=:role");

		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Grant.class)
				.setParameter("groupKey", group.getKey())
				.setParameter("role", role)
				.getResultList();
	}
	
	public List<Grant> getGrants(List<Group> groups) {
		if(groups == null || groups.isEmpty()) return Collections.emptyList();
		
		StringBuilder sb = new StringBuilder();
		sb.append("select grant from bgrant as grant")
		  .append(" inner join fetch grant.group as baseGroup")
		  .append(" inner join fetch grant.resource as res")
		  .append(" where baseGroup in (:groups)");
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Grant.class)
				.setParameter("groups", groups)
				.getResultList();
	}
	
	public List<Grant> getGrants(List<Group> groups, OLATResource resource) {
		if(groups == null || resource == null || groups.isEmpty()) return Collections.emptyList();
		
		StringBuilder sb = new StringBuilder();
		sb.append("select grant from bgrant as grant")
		  .append(" inner join fetch grant.group as baseGroup")
		  .append(" inner join fetch grant.resource as res")
		  .append(" where baseGroup in (:groups) and res.key=:resourceKey");
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Grant.class)
				.setParameter("groups", groups)
				.setParameter("resourceKey", resource.getKey())
				.getResultList();
	}
	
	public List<Grant> getGrants(Group group, String role, OLATResource resource) {
		StringBuilder sb = new StringBuilder();
		sb.append("select grant from bgrant as grant")
		  .append(" inner join fetch grant.group as baseGroup")
		  .append(" inner join fetch grant.resource as res")
		  .append(" where baseGroup=:group and res.key=:resourceKey and grant.role=:role");

		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Grant.class)
				.setParameter("group", group)
				.setParameter("resourceKey", resource.getKey())
				.setParameter("role", role)
				.getResultList();
	}
	
	public void addGrant(Group group, String role, String permission, OLATResource resource) {
		GrantImpl grant = new GrantImpl();
		grant.setCreationDate(new Date());
		grant.setGroup(group);
		grant.setPermission(permission);
		grant.setRole(role);
		grant.setResource(resource);
		dbInstance.getCurrentEntityManager().persist(grant);
	}
	
	public void removeGrant(Group group, String role, String permission, OLATResource resource) {
		StringBuilder sb = new StringBuilder();
		sb.append("select grant from bgrant as grant")
		  .append(" inner join fetch grant.group as baseGroup")
		  .append(" inner join fetch grant.resource as res")
		  .append(" where baseGroup=:group and res.key=:resourceKey and grant.permission=:permission and grant.role=:role");

		EntityManager em = dbInstance.getCurrentEntityManager();
		List<Grant> grantToDelete = em.createQuery(sb.toString(), Grant.class)
				.setParameter("group", group)
				.setParameter("resourceKey", resource.getKey())
				.setParameter("role", role)
				.setParameter("permission", permission)
				.getResultList();
		
		for(Grant grant:grantToDelete) {
			em.remove(grant);
		}
	}
	
	public void removeGrants(Group group, String role, OLATResource resource) {
		StringBuilder sb = new StringBuilder();
		sb.append("select grant from bgrant as grant")
		  .append(" inner join fetch grant.group as baseGroup")
		  .append(" inner join fetch grant.resource as res")
		  .append(" where baseGroup=:group and res.key=:resourceKey and grant.role=:role");

		EntityManager em = dbInstance.getCurrentEntityManager();
		List<Grant> grantToDelete = em.createQuery(sb.toString(), Grant.class)
				.setParameter("group", group)
				.setParameter("resourceKey", resource.getKey())
				.setParameter("role", role)
				.getResultList();
		
		for(Grant grant:grantToDelete) {
			em.remove(grant);
		}
	}

}
