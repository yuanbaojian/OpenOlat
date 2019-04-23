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
package org.olat.group;

import org.olat.NewControllerFactory;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.core.configuration.AbstractSpringModule;
import org.olat.core.id.Roles;
import org.olat.core.id.context.SiteContextEntryControllerCreator;
import org.olat.core.util.StringHelper;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.olat.core.util.resource.OresHelper;
import org.olat.group.site.GroupsSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Description:<br>
 * The business group module initializes the OLAT groups environment.
 * Configurations are loaded from here.
 * <P>
 * Initial Date: 04.11.2009 <br>
 * 
 * @author gnaegi
 */
@Service("businessGroupModule")
public class BusinessGroupModule extends AbstractSpringModule {

	public static final String ORES_TYPE_GROUP = OresHelper.calculateTypeName(BusinessGroup.class);
	
	/**
	 * Graduate from administrator to user by importance
	 */
	private static final OrganisationRoles[] privacyRoles = new OrganisationRoles[]{
			OrganisationRoles.administrator, OrganisationRoles.sysadmin,
			OrganisationRoles.rolesmanager, OrganisationRoles.usermanager, 
			OrganisationRoles.learnresourcemanager, OrganisationRoles.groupmanager, 
			OrganisationRoles.poolmanager, OrganisationRoles.curriculummanager,
			OrganisationRoles.lecturemanager, OrganisationRoles.qualitymanager,
			OrganisationRoles.linemanager, OrganisationRoles.principal,
			OrganisationRoles.author, OrganisationRoles.user,
	};
	
	private static final String USER_ALLOW_CREATE_BG = "user.allowed.create";
	private static final String AUTHOR_ALLOW_CREATE_BG = "author.allowed.create";
	private static final String CONTACT_BUSINESS_CARD = "contact.business.card";
	private static final String USER_LIST_DOWNLOAD = "userlist.download.default.allowed";
	
	public static final String CONTACT_BUSINESS_CARD_NEVER = "never";
	public static final String CONTACT_BUSINESS_CARD_ALWAYS = "always";
	public static final String CONTACT_BUSINESS_CARD_GROUP_CONFIG = "groupconfig";
	
	private static final String MANDATORY_ENROLMENT_EMAIL_USERS = "mandatoryEnrolmentEmailForUsers";
	private static final String MANDATORY_ENROLMENT_EMAIL_AUTHORS = "mandatoryEnrolmentEmailForAuthors";
	private static final String MANDATORY_ENROLMENT_EMAIL_USERMANAGERS = "mandatoryEnrolmentEmailForUsermanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_ROLESMANAGERS = "mandatoryEnrolmentEmailForRolesmanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_GROUPMANAGERS = "mandatoryEnrolmentEmailForGroupmanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_LEARNRESOURCEMANAGERS = "mandatoryEnrolmentEmailForLearnresourcemanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_POOLMANAGERS = "mandatoryEnrolmentEmailForPoolmanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_CURRICULUMMANAGERS = "mandatoryEnrolmentEmailForCurriculummanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_LECTUREMANAGERS = "mandatoryEnrolmentEmailForLecturemanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_QUALITYMANAGERS = "mandatoryEnrolmentEmailForQualitymanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_LINEMANAGERS = "mandatoryEnrolmentEmailForLinemanagers";
	private static final String MANDATORY_ENROLMENT_EMAIL_PRINCIPALS = "mandatoryEnrolmentEmailForPrincipals";
	private static final String MANDATORY_ENROLMENT_EMAIL_ADMINISTRATORS = "mandatoryEnrolmentEmailForAdministrators";
	private static final String MANDATORY_ENROLMENT_EMAIL_SYSTEMADMINS = "mandatoryEnrolmentEmailForSystemAdmins";

	private static final String ACCEPT_MEMBERSHIP_USERS = "acceptMembershipForUsers";
	private static final String ACCEPT_MEMBERSHIP_AUTHORS = "acceptMembershipForAuthors";
	private static final String ACCEPT_MEMBERSHIP_USERMANAGERS = "acceptMembershipForUsermanagers";
	private static final String ACCEPT_MEMBERSHIP_ROLESMANAGERS = "acceptMembershipForRolesmanagers";
	private static final String ACCEPT_MEMBERSHIP_GROUPMANAGERS = "acceptMembershipForGroupmanagers";
	private static final String ACCEPT_MEMBERSHIP_LEARNRESOURCEMANAGERS = "acceptMembershipForLearnresourcemanagers";
	private static final String ACCEPT_MEMBERSHIP_POOLMANAGERS = "acceptMembershipForPoolmanagers";
	private static final String ACCEPT_MEMBERSHIP_CURRICULUMMANAGERS = "acceptMembershipForCurriculummanagers";
	private static final String ACCEPT_MEMBERSHIP_LECTUREMANAGERS = "acceptMembershipForLecturemanagers";
	private static final String ACCEPT_MEMBERSHIP_QUALITYMANAGERS = "acceptMembershipForQualitymanagers";
	private static final String ACCEPT_MEMBERSHIP_LINEMANAGERS = "acceptMembershipForLinemanagers";
	private static final String ACCEPT_MEMBERSHIP_PRINCIPALS = "acceptMembershipForPrincipals";
	private static final String ACCEPT_MEMBERSHIP_ADMINISTRATORS = "acceptMembershipForAdministrators";
	private static final String ACCEPT_MEMBERSHIP_SYSTEMADMINS = "acceptMembershipForSystemAdmins";
	
	private static final String ALLOW_LEAVING_GROUP_BY_LEARNERS = "allowLeavingGroupCreatedByLearners";
	private static final String ALLOW_LEAVING_GROUP_BY_AUTHORS = "allowLeavingGroupCreatedByAuthors";
	private static final String ALLOW_LEAVING_GROUP_OVERRIDE = "allowLeavingGroupOverride";
	
	private static final String GROUP_MGR_LINK_COURSE_ALLOWED = "groupManagersAllowedToLinkCourses";
	private static final String RESOURCE_MGR_LINK_GROUP_ALLOWED = "resourceManagersAllowedToLinkGroups";
	
	private static final String MANAGED_GROUPS_ENABLED = "managedBusinessGroups";
	
	@Value("${group.user.create:true}")
	private boolean userAllowedCreate;
	@Value("${group.author.create}")
	private boolean authorAllowedCreate;
	@Value("${group.userlist.download.default.allowed}")
	private boolean userListDownloadDefaultAllowed;
	@Value("${group.card.contact}")
	private String contactBusinessCard;
	
	@Value("${group.mandatory.enrolment.email.users}")
	private String mandatoryEnrolmentEmailForUsers;
	@Value("${group.mandatory.enrolment.email.authors}")
	private String mandatoryEnrolmentEmailForAuthors;
	@Value("${group.mandatory.enrolment.email.usermanagers}")
	private String mandatoryEnrolmentEmailForUsermanagers;
	@Value("${group.mandatory.enrolment.email.rolesmanagers}")
	private String mandatoryEnrolmentEmailForRolesmanagers;
	@Value("${group.mandatory.enrolment.email.groupmanagers}")
	private String mandatoryEnrolmentEmailForGroupmanagers;
	@Value("${group.mandatory.enrolment.email.learnresourcemanagers}")
	private String mandatoryEnrolmentEmailForLearnresourcemanagers;
	@Value("${group.mandatory.enrolment.email.poolmanagers}")
	private String mandatoryEnrolmentEmailForPoolmanagers;
	@Value("${group.mandatory.enrolment.email.curriculummanagers}")
	private String mandatoryEnrolmentEmailForCurriculummanagers;
	@Value("${group.mandatory.enrolment.email.lecturemanagers}")
	private String mandatoryEnrolmentEmailForLecturemanagers;
	@Value("${group.mandatory.enrolment.email.qualitymanagers}")
	private String mandatoryEnrolmentEmailForQualitymanagers;
	@Value("${group.mandatory.enrolment.email.linemanagers}")
	private String mandatoryEnrolmentEmailForLinemanagers;
	@Value("${group.mandatory.enrolment.email.principals}")
	private String mandatoryEnrolmentEmailForPrincipals;
	@Value("${group.mandatory.enrolment.email.administrators}")
	private String mandatoryEnrolmentEmailForAdministrators;
	@Value("${group.mandatory.enrolment.email.systemadmins}")
	private String mandatoryEnrolmentEmailForSystemAdmins;
	
	@Value("${group.accept.membership.users}")
	private String acceptMembershipForUsers;
	@Value("${group.accept.membership.authors}")
	private String acceptMembershipForAuthors;
	@Value("${group.accept.membership.usermanagers}")
	private String acceptMembershipForUsermanagers;
	@Value("${group.accept.membership.rolesmanagers}")
	private String acceptMembershipForRolesmanagers;
	@Value("${group.accept.membership.groupmanagers}")
	private String acceptMembershipForGroupmanagers;
	@Value("${group.accept.membership.learnresourcemanagers}")
	private String acceptMembershipForLearnresourcemanagers;
	@Value("${group.accept.membership.poolmanagers}")
	private String acceptMembershipForPoolmanagers;
	@Value("${group.accept.membership.curriculummanagers}")
	private String acceptMembershipForCurriculummanagers;
	@Value("${group.accept.membership.lecturemanagers}")
	private String acceptMembershipForLecturemanagers;
	@Value("${group.accept.membership.qualitymanagers}")
	private String acceptMembershipForQualitymanagers;
	@Value("${group.accept.membership.linemanagers}")
	private String acceptMembershipForLinemanagers;
	@Value("${group.accept.membership.principals}")
	private String acceptMembershipForPrincipals;
	@Value("${group.accept.membership.administrators}")
	private String acceptMembershipForAdministrators;
	@Value("${group.accept.membership.systemadmins}")
	private String acceptMembershipForSystemAdmins;
	
	@Value("${group.leaving.group.created.by.learners:true}")
	private boolean allowLeavingGroupCreatedByLearners;
	@Value("${group.leaving.group.created.by.authors:true}")
	private boolean allowLeavingGroupCreatedByAuthors;
	@Value("${group.leaving.group.override:true}")
	private boolean allowLeavingGroupOverride;

	private boolean groupManagersAllowedToLinkCourses;
	private boolean resourceManagersAllowedToLinkGroups;
	@Value("${group.managed}")
	private boolean managedBusinessGroups;

	@Autowired
	public BusinessGroupModule(CoordinatorManager coordinatorManager) {
		super(coordinatorManager);
	}

	/**
	 * @see org.olat.core.configuration.AbstractOLATModule#init()
	 */
	@Override
	public void init() {
		// Add controller factory extension point to launch groups
		NewControllerFactory.getInstance().addContextEntryControllerCreator(BusinessGroup.class.getSimpleName(),
				new BusinessGroupContextEntryControllerCreator());
		NewControllerFactory.getInstance().addContextEntryControllerCreator("GroupCard",
				new BusinessGroupCardContextEntryControllerCreator());
		NewControllerFactory.getInstance().addContextEntryControllerCreator(GroupsSite.class.getSimpleName(),
				new SiteContextEntryControllerCreator(GroupsSite.class));
		
		updateProperties();
	}

	@Override
	protected void initFromChangedProperties() {
		updateProperties();
	}
	
	private void updateProperties() {
		//set properties
		String userAllowed = getStringPropertyValue(USER_ALLOW_CREATE_BG, true);
		if(StringHelper.containsNonWhitespace(userAllowed)) {
			userAllowedCreate = "true".equals(userAllowed);
		}
		String authorAllowed = getStringPropertyValue(AUTHOR_ALLOW_CREATE_BG, true);
		if(StringHelper.containsNonWhitespace(authorAllowed)) {
			authorAllowedCreate = "true".equals(authorAllowed);
		}
		
		String contactAllowed = getStringPropertyValue(CONTACT_BUSINESS_CARD, true);
		if(StringHelper.containsNonWhitespace(contactAllowed)) {
			contactBusinessCard = contactAllowed;
		}
		
		String downloadAllowed = getStringPropertyValue(USER_LIST_DOWNLOAD, true);
		if(StringHelper.containsNonWhitespace(downloadAllowed)) {
			userListDownloadDefaultAllowed = "true".equals(downloadAllowed);
		}

		mandatoryEnrolmentEmailForUsers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_USERS, mandatoryEnrolmentEmailForUsers);
		mandatoryEnrolmentEmailForAuthors = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_AUTHORS, mandatoryEnrolmentEmailForAuthors);
		mandatoryEnrolmentEmailForUsermanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_USERMANAGERS, mandatoryEnrolmentEmailForUsermanagers);
		mandatoryEnrolmentEmailForRolesmanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_ROLESMANAGERS, mandatoryEnrolmentEmailForRolesmanagers);
		mandatoryEnrolmentEmailForGroupmanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_GROUPMANAGERS, mandatoryEnrolmentEmailForGroupmanagers);
		mandatoryEnrolmentEmailForLearnresourcemanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_LEARNRESOURCEMANAGERS, mandatoryEnrolmentEmailForLearnresourcemanagers);
		mandatoryEnrolmentEmailForPoolmanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_POOLMANAGERS, mandatoryEnrolmentEmailForPoolmanagers);
		mandatoryEnrolmentEmailForCurriculummanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_CURRICULUMMANAGERS, mandatoryEnrolmentEmailForCurriculummanagers);
		mandatoryEnrolmentEmailForLecturemanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_LECTUREMANAGERS, mandatoryEnrolmentEmailForLecturemanagers);
		mandatoryEnrolmentEmailForQualitymanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_QUALITYMANAGERS, mandatoryEnrolmentEmailForQualitymanagers);
		mandatoryEnrolmentEmailForLinemanagers = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_LINEMANAGERS, mandatoryEnrolmentEmailForLinemanagers);
		mandatoryEnrolmentEmailForPrincipals = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_PRINCIPALS, mandatoryEnrolmentEmailForPrincipals);
		mandatoryEnrolmentEmailForAdministrators = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_ADMINISTRATORS, mandatoryEnrolmentEmailForAdministrators);
		mandatoryEnrolmentEmailForSystemAdmins = getStringPropertyValue(MANDATORY_ENROLMENT_EMAIL_SYSTEMADMINS, mandatoryEnrolmentEmailForSystemAdmins);

		acceptMembershipForUsers = getStringPropertyValue(ACCEPT_MEMBERSHIP_USERS, acceptMembershipForUsers);
		acceptMembershipForAuthors = getStringPropertyValue(ACCEPT_MEMBERSHIP_AUTHORS, acceptMembershipForAuthors);
		acceptMembershipForUsermanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_USERMANAGERS, acceptMembershipForUsermanagers);
		acceptMembershipForRolesmanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_ROLESMANAGERS, acceptMembershipForRolesmanagers);
		acceptMembershipForGroupmanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_GROUPMANAGERS, acceptMembershipForGroupmanagers);
		acceptMembershipForLearnresourcemanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_LEARNRESOURCEMANAGERS, acceptMembershipForLearnresourcemanagers);
		acceptMembershipForPoolmanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_POOLMANAGERS, acceptMembershipForPoolmanagers);
		acceptMembershipForCurriculummanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_CURRICULUMMANAGERS, acceptMembershipForCurriculummanagers);
		acceptMembershipForLecturemanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_LECTUREMANAGERS, acceptMembershipForLecturemanagers);
		acceptMembershipForQualitymanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_QUALITYMANAGERS, acceptMembershipForQualitymanagers);
		acceptMembershipForLinemanagers = getStringPropertyValue(ACCEPT_MEMBERSHIP_LINEMANAGERS, acceptMembershipForLinemanagers);
		acceptMembershipForPrincipals = getStringPropertyValue(ACCEPT_MEMBERSHIP_PRINCIPALS, acceptMembershipForPrincipals);
		acceptMembershipForAdministrators = getStringPropertyValue(ACCEPT_MEMBERSHIP_ADMINISTRATORS, acceptMembershipForAdministrators);
		acceptMembershipForSystemAdmins = getStringPropertyValue(ACCEPT_MEMBERSHIP_SYSTEMADMINS, acceptMembershipForSystemAdmins);
		
		String linkCourseAllowed = getStringPropertyValue(GROUP_MGR_LINK_COURSE_ALLOWED, true);
		if(StringHelper.containsNonWhitespace(linkCourseAllowed)) {
			groupManagersAllowedToLinkCourses = "true".equals(linkCourseAllowed);
		}
		String linkGroupAllowed = getStringPropertyValue(RESOURCE_MGR_LINK_GROUP_ALLOWED, true);
		if(StringHelper.containsNonWhitespace(linkGroupAllowed)) {
			resourceManagersAllowedToLinkGroups = "true".equals(linkGroupAllowed);
		}
		
		String allowLeavingIfCreatedByLearners = getStringPropertyValue(ALLOW_LEAVING_GROUP_BY_LEARNERS, true);
		if(StringHelper.containsNonWhitespace(allowLeavingIfCreatedByLearners)) {
			allowLeavingGroupCreatedByLearners = "true".equals(allowLeavingIfCreatedByLearners);
		}
		String allowLeavingIfCreatedByAuthors = getStringPropertyValue(ALLOW_LEAVING_GROUP_BY_AUTHORS, true);
		if(StringHelper.containsNonWhitespace(allowLeavingIfCreatedByAuthors)) {
			allowLeavingGroupCreatedByAuthors = "true".equals(allowLeavingIfCreatedByAuthors);
		}
		String allowLeavingOverride = getStringPropertyValue(ALLOW_LEAVING_GROUP_OVERRIDE, true);
		if(StringHelper.containsNonWhitespace(allowLeavingOverride)) {
			allowLeavingGroupOverride = "true".equals(allowLeavingOverride);
		}
		
		String managedGroups = getStringPropertyValue(MANAGED_GROUPS_ENABLED, true);
		if(StringHelper.containsNonWhitespace(managedGroups)) {
			managedBusinessGroups = "true".equals(managedGroups);
		}
	}

	public boolean isAllowedCreate(Roles roles) {
		return roles.isAdministrator() || roles.isGroupManager()
				|| (roles.isAuthor() && isAuthorAllowedCreate())
				|| (!roles.isGuestOnly() && !roles.isInvitee() && isUserAllowedCreate());
	}

	public boolean isUserAllowedCreate() {
		return userAllowedCreate;
	}

	public void setUserAllowedCreate(boolean userAllowedCreate) {
		setStringProperty(USER_ALLOW_CREATE_BG, Boolean.toString(userAllowedCreate), true);
	}

	public boolean isAuthorAllowedCreate() {
		return authorAllowedCreate;
	}

	public void setAuthorAllowedCreate(boolean authorAllowedCreate) {
		setStringProperty(AUTHOR_ALLOW_CREATE_BG, Boolean.toString(authorAllowedCreate), true);
	}

	public String getContactBusinessCard() {
		return contactBusinessCard;
	}

	public void setContactBusinessCard(String contactBusinessCard) {
		setStringProperty(CONTACT_BUSINESS_CARD, contactBusinessCard, true);
	}

	public boolean isUserListDownloadDefaultAllowed() {
		return userListDownloadDefaultAllowed;
	}

	public void setUserListDownloadDefaultAllowed(boolean userListDownload) {
		setStringProperty(USER_LIST_DOWNLOAD, Boolean.toString(userListDownload), true);
	}

	public boolean isMandatoryEnrolmentEmail(Roles roles) {
		if(roles == null || roles.isGuestOnly() || roles.isInvitee()) return false;
		
		for(OrganisationRoles role:privacyRoles) {
			if(roles.hasRole(role)) {
				return Boolean.parseBoolean(getMandatoryEnrolmentEmailFor(role));
			}
		}
		return Boolean.parseBoolean(getMandatoryEnrolmentEmailFor(OrganisationRoles.user));
	}
	
	public String getMandatoryEnrolmentEmailFor(OrganisationRoles role) {
		switch(role) {
			case user: return mandatoryEnrolmentEmailForUsers;
			case author: return mandatoryEnrolmentEmailForAuthors;
			case usermanager: return mandatoryEnrolmentEmailForUsermanagers;
			case rolesmanager: return mandatoryEnrolmentEmailForRolesmanagers;
			case groupmanager: return mandatoryEnrolmentEmailForGroupmanagers;
			case learnresourcemanager: return mandatoryEnrolmentEmailForLearnresourcemanagers;
			case poolmanager: return mandatoryEnrolmentEmailForPoolmanagers;
			case curriculummanager: return mandatoryEnrolmentEmailForCurriculummanagers;
			case lecturemanager: return mandatoryEnrolmentEmailForLecturemanagers;
			case qualitymanager: return mandatoryEnrolmentEmailForQualitymanagers;
			case linemanager: return mandatoryEnrolmentEmailForLinemanagers;
			case principal: return mandatoryEnrolmentEmailForPrincipals;
			case administrator: return mandatoryEnrolmentEmailForAdministrators;
			case sysadmin: return mandatoryEnrolmentEmailForSystemAdmins;
			default: return "disabled";
		}
	}
	
	public void setMandatoryEnrolmentEmailFor(OrganisationRoles role, String enable) {
		switch(role) {
			case user:
				mandatoryEnrolmentEmailForUsers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_USERS, enable, true);
				break;
			case author:
				mandatoryEnrolmentEmailForAuthors = setStringProperty(MANDATORY_ENROLMENT_EMAIL_AUTHORS, enable, true);
				break;
			case usermanager:
				mandatoryEnrolmentEmailForUsermanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_USERMANAGERS, enable, true);
				break;
			case rolesmanager:
				mandatoryEnrolmentEmailForRolesmanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_ROLESMANAGERS, enable, true);
				break;
			case groupmanager:
				mandatoryEnrolmentEmailForGroupmanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_GROUPMANAGERS, enable, true);
				break;
			case learnresourcemanager:
				mandatoryEnrolmentEmailForLearnresourcemanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_LEARNRESOURCEMANAGERS, enable, true);
				break;
			case poolmanager:
				mandatoryEnrolmentEmailForPoolmanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_POOLMANAGERS, enable, true);
				break;
			case curriculummanager:
				mandatoryEnrolmentEmailForCurriculummanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_CURRICULUMMANAGERS, enable, true);
				break;
			case lecturemanager:
				mandatoryEnrolmentEmailForLecturemanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_LECTUREMANAGERS, enable, true);
				break;
			case qualitymanager:
				mandatoryEnrolmentEmailForQualitymanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_QUALITYMANAGERS, enable, true);
				break;
			case linemanager:
				mandatoryEnrolmentEmailForLinemanagers = setStringProperty(MANDATORY_ENROLMENT_EMAIL_LINEMANAGERS, enable, true);
				break;
			case principal:
				mandatoryEnrolmentEmailForPrincipals = setStringProperty(MANDATORY_ENROLMENT_EMAIL_PRINCIPALS, enable, true);
				break;
			case administrator:
				mandatoryEnrolmentEmailForAdministrators = setStringProperty(MANDATORY_ENROLMENT_EMAIL_ADMINISTRATORS, enable, true);
				break;
			case sysadmin:
				mandatoryEnrolmentEmailForSystemAdmins = setStringProperty(MANDATORY_ENROLMENT_EMAIL_SYSTEMADMINS, enable, true);
				break;
			default: /* Ignore the other roles */
		}
	}
	
	public boolean isAcceptMembership(Roles roles) {
		if(roles == null || roles.isGuestOnly() || roles.isInvitee()) return false;
	
		for(OrganisationRoles role:privacyRoles) {
			if(roles.hasRole(role)) {
				return Boolean.parseBoolean(getAcceptMembershipFor(role));
			}
		}
		return Boolean.parseBoolean(getAcceptMembershipFor(OrganisationRoles.user));
	}
	
	public String getAcceptMembershipFor(OrganisationRoles role) {
		switch(role) {
			case user: return acceptMembershipForUsers;
			case author: return acceptMembershipForAuthors;
			case usermanager: return acceptMembershipForUsermanagers;
			case rolesmanager: return acceptMembershipForRolesmanagers;
			case groupmanager: return acceptMembershipForGroupmanagers;
			case learnresourcemanager: return acceptMembershipForLearnresourcemanagers;
			case poolmanager: return acceptMembershipForPoolmanagers;
			case curriculummanager: return acceptMembershipForCurriculummanagers;
			case lecturemanager: return acceptMembershipForLecturemanagers;
			case qualitymanager: return acceptMembershipForQualitymanagers;
			case linemanager: return acceptMembershipForLinemanagers;
			case principal: return acceptMembershipForPrincipals;
			case administrator: return acceptMembershipForAdministrators;
			case sysadmin: return acceptMembershipForSystemAdmins;
			default: return "disabled";
		}
	}

	public void setAcceptMembershipFor(OrganisationRoles role, String enable) {
		switch(role) {
			case user:
				acceptMembershipForUsers = setStringProperty(ACCEPT_MEMBERSHIP_USERS, enable, true);
				break;
			case author:
				acceptMembershipForAuthors = setStringProperty(ACCEPT_MEMBERSHIP_AUTHORS, enable, true);
				break;
			case usermanager:
				acceptMembershipForUsermanagers = setStringProperty(ACCEPT_MEMBERSHIP_USERMANAGERS, enable, true);
				break;
			case rolesmanager:
				acceptMembershipForRolesmanagers = setStringProperty(ACCEPT_MEMBERSHIP_ROLESMANAGERS, enable, true);
				break;
			case groupmanager:
				acceptMembershipForGroupmanagers = setStringProperty(ACCEPT_MEMBERSHIP_GROUPMANAGERS, enable, true);
				break;
			case learnresourcemanager:
				acceptMembershipForLearnresourcemanagers = setStringProperty(ACCEPT_MEMBERSHIP_LEARNRESOURCEMANAGERS, enable, true);
				break;
			case poolmanager:
				acceptMembershipForPoolmanagers = setStringProperty(ACCEPT_MEMBERSHIP_POOLMANAGERS, enable, true);
				break;
			case curriculummanager:
				acceptMembershipForCurriculummanagers = setStringProperty(ACCEPT_MEMBERSHIP_CURRICULUMMANAGERS, enable, true);
				break;
			case lecturemanager:
				acceptMembershipForLecturemanagers = setStringProperty(ACCEPT_MEMBERSHIP_LECTUREMANAGERS, enable, true);
				break;
			case qualitymanager:
				acceptMembershipForQualitymanagers = setStringProperty(ACCEPT_MEMBERSHIP_QUALITYMANAGERS, enable, true);
				break;
			case linemanager:
				acceptMembershipForLinemanagers = setStringProperty(ACCEPT_MEMBERSHIP_LINEMANAGERS, enable, true);
				break;
			case principal:
				acceptMembershipForPrincipals = setStringProperty(ACCEPT_MEMBERSHIP_PRINCIPALS, enable, true);
				break;
			case administrator:
				acceptMembershipForAdministrators = setStringProperty(ACCEPT_MEMBERSHIP_ADMINISTRATORS, enable, true);
				break;
			case sysadmin:
				acceptMembershipForSystemAdmins = setStringProperty(ACCEPT_MEMBERSHIP_SYSTEMADMINS, enable, true);
				break;
			default: /* Ignore the other roles */
		}
	}
	
	public boolean isGroupManagersAllowedToLinkCourses() {
		return groupManagersAllowedToLinkCourses;
	}

	public void setGroupManagersAllowedToLinkCourses(boolean enabled) {
		setStringProperty(GROUP_MGR_LINK_COURSE_ALLOWED, Boolean.toString(enabled), true);
	}

	public boolean isResourceManagersAllowedToLinkGroups() {
		return resourceManagersAllowedToLinkGroups;
	}

	public void setResourceManagersAllowedToLinkGroups(boolean enabled) {
		setStringProperty(RESOURCE_MGR_LINK_GROUP_ALLOWED, Boolean.toString(enabled), true);
	}

	public boolean isAllowLeavingGroupCreatedByLearners() {
		return allowLeavingGroupCreatedByLearners;
	}

	public void setAllowLeavingGroupCreatedByLearners(boolean allow) {
		this.allowLeavingGroupCreatedByLearners = allow;
		setStringProperty(ALLOW_LEAVING_GROUP_BY_LEARNERS, Boolean.toString(allow), true);
	}

	public boolean isAllowLeavingGroupCreatedByAuthors() {
		return allowLeavingGroupCreatedByAuthors;
	}

	public void setAllowLeavingGroupCreatedByAuthors(boolean allow) {
		this.allowLeavingGroupCreatedByAuthors = allow;
		setStringProperty(ALLOW_LEAVING_GROUP_BY_AUTHORS, Boolean.toString(allow), true);
	}

	public boolean isAllowLeavingGroupOverride() {
		return allowLeavingGroupOverride;
	}

	public void setAllowLeavingGroupOverride(boolean allow) {
		this.allowLeavingGroupOverride = allow;
		setStringProperty(ALLOW_LEAVING_GROUP_OVERRIDE, Boolean.toString(allow), true);
	}

	public boolean isManagedBusinessGroups() {
		return managedBusinessGroups;
	}

	public void setManagedBusinessGroups(boolean enabled) {
		setStringProperty(MANAGED_GROUPS_ENABLED, Boolean.toString(enabled), true);
	}
}