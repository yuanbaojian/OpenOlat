/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.
*/

package org.olat.admin.user.delete.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TemporalType;

import org.olat.admin.user.delete.SelectionController;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.IdentityImpl;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.manager.GroupDAO;
import org.olat.commons.lifecycle.LifeCycleEntry;
import org.olat.commons.lifecycle.LifeCycleManager;
import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.persistence.DB;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.id.OrganisationRef;
import org.olat.core.id.UserConstants;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.Util;
import org.olat.core.util.i18n.I18nManager;
import org.olat.core.util.mail.MailBundle;
import org.olat.core.util.mail.MailManager;
import org.olat.core.util.mail.MailTemplate;
import org.olat.core.util.mail.MailerResult;
import org.olat.core.util.session.UserSessionManager;
import org.olat.properties.Property;
import org.olat.properties.PropertyManager;
import org.olat.repository.RepositoryDeletionModule;
import org.olat.user.UserDataDeletable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Manager for user-deletion. <br />
 * User deletion works by implementing the UserDataDeletable interface and
 * setting an optional delete priority. Use a high priority (890 - 510) to
 * delete things first. The default priority is 500. Use a low priority (110 -
 * 490) to delete at the.
 *
 * @author Christian Guretzki
 */
@Service("userDeletionManager")
public class UserDeletionManager {
	
	private static final OLog log = Tracing.createLoggerFor(UserDeletionManager.class);

	/** Default value for last-login duration in month. */
	private static final int DEFAULT_LAST_LOGIN_DURATION = 24;
	/** Default value for delete-email duration in days. */
	private static final int DEFAULT_DELETE_EMAIL_DURATION = 30;
	private static final String LAST_LOGIN_DURATION_PROPERTY_NAME = "LastLoginDuration";
	private static final String DELETE_EMAIL_DURATION_PROPERTY_NAME = "DeleteEmailDuration";
	private static final String PROPERTY_CATEGORY = "UserDeletion";
	public static final String SEND_DELETE_EMAIL_ACTION = "sendDeleteEmail";
	private static final String USER_DELETED_ACTION = "userdeleted";


	// Flag used in user-delete to indicate that all deletable managers are initialized
	@Autowired
	private RepositoryDeletionModule deletionModule;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private UserSessionManager userSessionManager;
	@Autowired
	private GroupDAO groupDao;
	@Autowired
	private DB dbInstance;


	/**
	 * Send 'delete'- emails to a list of identities. The delete email is an announcement for the user-deletion.
	 *
	 * @param selectedIdentities
	 * @return String with warning message (e.g. email-address not valid, could not send email).
	 *         If there is no warning, the return String is empty ("").
	 */
	public String sendUserDeleteEmailTo(List<Identity> selectedIdentities, MailTemplate template,
			boolean isTemplateChanged, String keyEmailSubject, String keyEmailBody, Identity sender, Translator pT ) {
		StringBuilder buf = new StringBuilder();
		if (template != null) {
			template.addToContext("responseTo", deletionModule.getEmailResponseTo());
			for (Iterator<Identity> iter = selectedIdentities.iterator(); iter.hasNext();) {
				Identity identity = iter.next();
				if (!isTemplateChanged) {
					// Email template has NOT changed => take translated version of subject and body text
					Translator identityTranslator = Util.createPackageTranslator(SelectionController.class, I18nManager.getInstance().getLocaleOrDefault(identity.getUser().getPreferences().getLanguage()));
					template.setSubjectTemplate(identityTranslator.translate(keyEmailSubject));
					template.setBodyTemplate(identityTranslator.translate(keyEmailBody));
				}
				template.putVariablesInMailContext(template.getContext(), identity);
				log.debug(" Try to send Delete-email to identity=" + identity.getKey() + " with email=" + identity.getUser().getProperty(UserConstants.EMAIL, null));

				MailerResult result = new MailerResult();
				MailBundle bundle = mailManager.makeMailBundle(null, identity, template, null, null, result);
				if(bundle != null) {
					mailManager.sendMessage(bundle);
				}
				if(template.getCpfrom()) {
					MailBundle ccBundle = mailManager.makeMailBundle(null, sender, template, sender, null, result);
					if(ccBundle != null) {
						mailManager.sendMessage(ccBundle);
					}
				}

				if (result.getReturnCode() != MailerResult.OK) {
					buf.append(pT.translate("email.error.send.failed", new String[] {identity.getUser().getProperty(UserConstants.EMAIL, null), identity.getName()} )).append("\n");
				}
				log.audit("User-Deletion: Delete-email send to identity=" + identity.getKey() + " with email=" + identity.getUser().getProperty(UserConstants.EMAIL, null));
				markSendEmailEvent(identity);
			}
		} else {
			// no template => User decides to sending no delete-email, mark only in lifecycle table 'sendEmail'
			for (Iterator<Identity> iter = selectedIdentities.iterator(); iter.hasNext();) {
				Identity identity = iter.next();
				log.audit("User-Deletion: Move in 'Email sent' section without sending email, identity=" + identity.getKey());
				markSendEmailEvent(identity);
			}
		}
		return buf.toString();
	}

	private void markSendEmailEvent(Identity identity) {
		LifeCycleManager.createInstanceFor(identity).markTimestampFor(SEND_DELETE_EMAIL_ACTION);
	}

	/**
	 * Return list of identities which have last-login older than 'lastLoginDuration' parameter.
	 * This user are ready to start with user-deletion process.
	 * @param lastLoginDuration  last-login duration in month
	 * @return List of Identity objects
	 */
	public List<Identity> getDeletableIdentities(int lastLoginDuration, List<OrganisationRef> organisations) {
		List<Long> organisationKeys = organisations.stream().map(OrganisationRef::getKey).collect(Collectors.toList());
		
		Calendar lastLoginLimit = Calendar.getInstance();
		lastLoginLimit.add(Calendar.MONTH, - lastLoginDuration);
		log.debug("lastLoginLimit=" + lastLoginLimit);
		// 1. get all 'active' identities with lastlogin > x
		StringBuilder sb = new StringBuilder(512);
		sb.append("select ident from ").append(IdentityImpl.class.getName()).append(" as ident")
		  .append(" inner join fetch ident.user as user")
		  .append(" where ident.status=").append(Identity.STATUS_ACTIV).append(" and ((ident.lastLogin = null and ident.creationDate < :lastLogin) or ident.lastLogin < :lastLogin)")
		  .append(" and exists (select orgtomember.key from bgroupmember as orgtomember ")
		  .append("  inner join organisation as org on (org.group.key=orgtomember.group.key)")
		  .append("  where orgtomember.identity.key=ident.key and org.key in (:organisationKeys) and orgtomember.role='").append(OrganisationRoles.user).append("')");

		List<Identity> identities = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Identity.class)
				.setParameter("lastLogin", lastLoginLimit.getTime(), TemporalType.TIMESTAMP)
				.setParameter("organisationKeys", organisationKeys)
				.getResultList();

		// 2. get all 'active' identities in deletion process to remove from 1.
		StringBuilder sc = new StringBuilder(512);
		sc.append("select le.persistentRef from ").append(LifeCycleEntry.class.getName()).append(" as le")
		  .append(" where le.persistentTypeName ='").append(IdentityImpl.class.getName()).append("'")
		  .append(" and le.action ='").append(SEND_DELETE_EMAIL_ACTION).append("'");
		List<Long> identitiesInProcess = dbInstance.getCurrentEntityManager()
				.createQuery(sc.toString(), Long.class)
				.getResultList();
		
		Set<Long> identitiesInProcessSet = new HashSet<>(identitiesInProcess);
		List<Identity> deletableIdentities = new ArrayList<>(identities.size());
		for(Identity identity:identities) {
			if(!identitiesInProcessSet.contains(identity.getKey())) {
				deletableIdentities.add(identity);
			}
		}
		return deletableIdentities;
	}

	/**
	 * Return list of identities which are in user-deletion-process.
	 * user-deletion-process means delete-announcement.email send, duration of waiting for response is not expired.
	 * @param deleteEmailDuration  Duration of user-deletion-process in days
	 * @return List of Identity objects
	 */
	public List<Identity> getIdentitiesInDeletionProcess(int deleteEmailDuration, List<OrganisationRef> organisations) {
		List<Long> organisationKeys = organisations.stream().map(OrganisationRef::getKey).collect(Collectors.toList());
		Calendar deleteEmailLimit = Calendar.getInstance();
		deleteEmailLimit.add(Calendar.DAY_OF_MONTH, - (deleteEmailDuration - 1));
		
		StringBuilder sb = new StringBuilder(1024);
		sb.append( "select ident from ").append(IdentityImpl.class.getName()).append(" as ident")
		  .append(" inner join fetch ident.user identUser")
		  .append(" inner join ").append(LifeCycleEntry.class.getName()).append(" as le on (ident.key=le.persistentRef)")
		  .append(" and ident.status=").append(Identity.STATUS_ACTIV)
		  .append(" and le.persistentTypeName='").append(IdentityImpl.class.getName()).append("'")
		  .append(" and le.action='").append(SEND_DELETE_EMAIL_ACTION).append("' and le.lcTimestamp>=:deleteEmailDate")
		  .append(" and exists (select orgtomember.key from bgroupmember as orgtomember ")
		  .append("  inner join organisation as org on (org.group.key=orgtomember.group.key)")
		  .append("  where orgtomember.identity.key=ident.key and org.key in (:organisationKeys) and orgtomember.role='").append(OrganisationRoles.user).append("')");

		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Identity.class)
				.setParameter("deleteEmailDate", deleteEmailLimit.getTime(), TemporalType.TIMESTAMP)
				.setParameter("organisationKeys", organisationKeys)
				.getResultList();
	}

	/**
	 * Return list of identities which are ready-to-delete in user-deletion-process.
	 * (delete-announcement.email send, duration of waiting for response is expired).
	 * @param deleteEmailDuration  Duration of user-deletion-process in days
	 * @return List of Identity objects
	 */
	public List<Identity> getIdentitiesReadyToDelete(int deleteEmailDuration, List<OrganisationRef> organisations) {
		List<Long> organisationKeys = organisations.stream().map(OrganisationRef::getKey).collect(Collectors.toList());
		Calendar deleteEmailLimit = Calendar.getInstance();
		deleteEmailLimit.add(Calendar.DAY_OF_MONTH, - (deleteEmailDuration - 1));
		
		StringBuilder sb = new StringBuilder(1024);
		sb.append("select ident from ").append(IdentityImpl.class.getName()).append(" as ident")
		  .append(" inner join fetch ident.user identUser")
		  .append(" inner join ").append(LifeCycleEntry.class.getName()).append(" as le on (ident.key=le.persistentRef)")
		  .append(" where ident.status=").append(Identity.STATUS_ACTIV)
		  .append(" and le.persistentTypeName='").append(IdentityImpl.class.getName()).append("'")
		  .append(" and le.action='").append(SEND_DELETE_EMAIL_ACTION).append("' and le.lcTimestamp<:deleteEmailDate")
		  .append(" and exists (select orgtomember.key from bgroupmember as orgtomember ")
		  .append("  inner join organisation as org on (org.group.key=orgtomember.group.key)")
		  .append("  where orgtomember.identity.key=ident.key and org.key in (:organisationKeys) and orgtomember.role='").append(OrganisationRoles.user).append("')");

		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Identity.class)
				.setParameter("deleteEmailDate", deleteEmailLimit.getTime(), TemporalType.TIMESTAMP)
				.setParameter("organisationKeys", organisationKeys)
				.getResultList();
	}

	/**
	 * Delete all user-data in registered deleteable resources.
	 * 
	 * @param identity
	 * @return true: delete was successful; false: delete could not finish
	 */
	public boolean deleteIdentity(Identity identity, Identity doer) {
		log.info("Start deleteIdentity for identity=" + identity);		
		if(Identity.STATUS_PERMANENT.equals(identity.getStatus())) {
			log.info("Aborted deletion of identity=" + identity + ", identity is flagged as PERMANENT");					
			return false;
		}
		// Logout user and start with delete process
		userSessionManager.signOffAndClearAll(identity);
		// set some data
		identity = securityManager.saveDeletedByData(identity, doer);
		dbInstance.commit();
		
		
		// Delete data of modules that implement the user data deletable
		String anonymisedIdentityName = "del_" + identity.getKey().toString();
		Map<String,UserDataDeletable> userDataDeletableResourcesMap = CoreSpringFactory.getBeansOfType(UserDataDeletable.class);
		List<UserDataDeletable> userDataDeletableResources = new ArrayList<>(userDataDeletableResourcesMap.values());
		// Start with high priorities (900: user manager), then continue with
		// others. Default priority is 500. End with low priorities (100: base
		// security)
		Collections.sort(userDataDeletableResources, new UserDataDeletableComparator());
		for (UserDataDeletable element : userDataDeletableResources) {
			try {
				log.info("UserDataDeletable-Loop for identity::" + identity.getKey() + " and element::" + element.getClass().getSimpleName());
				element.deleteUserData(identity, anonymisedIdentityName);				
			} catch (Exception e) {
				log.error("Error while deleting identity::" + identity.getKey() + " data for and element::"
						+ element.getClass().getSimpleName()
						+ ". Aboring delete process, user partially deleted, but not yet marked as deleted", e);
				dbInstance.rollbackAndCloseSession();
				return false;
			}
		}

		// Done with all modules that keep user data, now finish delete process
		dbInstance.commit();
		
		// Remove identity from all remaining groups and remove roles
		int count = groupDao.removeMemberships(identity);
		log.info("Delete " + count + " group memberships/roles for identity::" + identity.getKey());

		// Cleanup lifecycle data
		LifeCycleManager.createInstanceFor(identity).markTimestampFor(USER_DELETED_ACTION, null);
		LifeCycleManager.createInstanceFor(identity).deleteTimestampFor(SEND_DELETE_EMAIL_ACTION);

		// Anonymise identity to conform with data privacy law. The username is removed
		// by default and replaced with an anonymous database key. The identity
		// object itself must remain in the database since there are referenced
		// objects such as undeletable forum entries linked to it
		identity = securityManager.saveIdentityName(identity, anonymisedIdentityName, null);
		log.info("Replaced username with database key for identity::" + identity.getKey());

		// Finally mark user as deleted and we are done
		identity = securityManager.saveIdentityStatus(identity, Identity.STATUS_DELETED, doer);
		log.info("Data of identity deleted and state of identity::" + identity.getKey() + " changed to 'deleted'");

		dbInstance.commit();
		log.audit("User-Deletion: Deleted identity::" + identity.getKey());
		return true;
	}	

	/**
	 * Re-activate an identity, lastLogin = now, reset deleteemaildate = null.
	 * @param identity
	 */
	public Identity setIdentityAsActiv(final Identity identity) {
		securityManager.setIdentityLastLogin(identity);
		LifeCycleManager lifeCycleManagerForIdenitiy = LifeCycleManager.createInstanceFor(identity);
		if (lifeCycleManagerForIdenitiy.hasLifeCycleEntry(SEND_DELETE_EMAIL_ACTION)) {
			log.audit("User-Deletion: Remove from delete-list identity=" + identity);
			lifeCycleManagerForIdenitiy.deleteTimestampFor(SEND_DELETE_EMAIL_ACTION);
		}
		return identity;
	}

	/**
	 * @return  Return duration in days for waiting for reaction on delete-email.
	 */
	public int getDeleteEmailDuration() {
		return getPropertyByName(DELETE_EMAIL_DURATION_PROPERTY_NAME, DEFAULT_DELETE_EMAIL_DURATION);
	}

	/**
	 * @return  Return last-login duration in month for user on delete-selection list.
	 */
	public int getLastLoginDuration() {
		return getPropertyByName(LAST_LOGIN_DURATION_PROPERTY_NAME, DEFAULT_LAST_LOGIN_DURATION);
	}

	private int getPropertyByName(String name, int defaultValue) {
		List<Property> properties = PropertyManager.getInstance().findProperties(null, null, null, PROPERTY_CATEGORY, name);
		if (properties.isEmpty()) {
			return defaultValue;
		} else {
			return properties.get(0).getLongValue().intValue();
		}
	}

	public void setLastLoginDuration(int lastLoginDuration) {
		setProperty(LAST_LOGIN_DURATION_PROPERTY_NAME, lastLoginDuration);
	}

	public void setDeleteEmailDuration(int deleteEmailDuration) {
		setProperty(DELETE_EMAIL_DURATION_PROPERTY_NAME, deleteEmailDuration);
	}

	private void setProperty(String propertyName, int value) {
		List<Property> properties = PropertyManager.getInstance().findProperties(null, null, null, PROPERTY_CATEGORY, propertyName);
		Property property = null;
		if (properties.isEmpty()) {
			property = PropertyManager.getInstance().createPropertyInstance(null, null, null, PROPERTY_CATEGORY, propertyName, null,  new Long(value), null, null);
		} else {
			property = properties.get(0);
			property.setLongValue(Long.valueOf(value));
		}
		PropertyManager.getInstance().saveProperty(property);
	}
	
	public static class UserDataDeletableComparator implements Comparator<UserDataDeletable> {
		@Override
		public int compare(UserDataDeletable o1, UserDataDeletable o2) {
			int p1 = o1.deleteUserDataPriority();
			int p2 = o2.deleteUserDataPriority();
			return -Integer.compare(p1, p2);
		}
	}
}
