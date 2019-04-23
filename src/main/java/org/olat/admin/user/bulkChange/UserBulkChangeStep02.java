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
package org.olat.admin.user.bulkChange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.olat.admin.user.groups.GroupSearchController;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.BaseSecurityModule;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.EscapeMode;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataModelFactory;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataModelImpl;
import org.olat.core.gui.components.form.flexible.impl.elements.table.TextFlexiCellRenderer;
import org.olat.core.gui.components.table.TableDataModel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.BasicStep;
import org.olat.core.gui.control.generic.wizard.PrevNextFinishConfig;
import org.olat.core.gui.control.generic.wizard.Step;
import org.olat.core.gui.control.generic.wizard.StepFormBasicController;
import org.olat.core.gui.control.generic.wizard.StepFormController;
import org.olat.core.gui.control.generic.wizard.StepsEvent;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.util.Util;
import org.olat.group.BusinessGroup;
import org.olat.group.BusinessGroupService;
import org.olat.group.ui.BusinessGroupFormController;
import org.olat.user.UserManager;
import org.olat.user.propertyhandlers.UserPropertyHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description:<br>
 * last step presenting an overview of every change per selected user 
 * which will be done after finish has been pressed
 * 
 * <P>
 * Initial Date: 30.01.2008 <br>
 * 
 * @author rhaag
 */
class UserBulkChangeStep02 extends BasicStep {

	private static final String usageIdentifyer = UserBulkChangeStep00.class.getCanonicalName();
	
	private final UserBulkChanges userBulkChanges;

	public UserBulkChangeStep02(UserRequest ureq, UserBulkChanges userBulkChanges) {
		super(ureq);
		this.userBulkChanges = userBulkChanges;
		setI18nTitleAndDescr("step2.description", null);
		setNextStep(Step.NOSTEP);
	}

	@Override
	public PrevNextFinishConfig getInitialPrevNextFinishConfig() {
		return new PrevNextFinishConfig(true, false, true);
	}

	@Override
	public StepFormController getStepController(UserRequest ureq, WindowControl windowControl, StepsRunContext stepsRunContext, Form form) {
		return new UserBulkChangeStepForm02(ureq, windowControl, form, stepsRunContext);
	}
	
	private final class UserBulkChangeStepForm02 extends StepFormBasicController {

		private final boolean isAdministrativeUser;
		private final List<UserPropertyHandler> userPropertyHandlers;
		
		@Autowired
		private UserManager userManager;
		@Autowired
		private BaseSecurity securityManager;
		@Autowired
		private BaseSecurityModule securityModule;
		@Autowired
		private UserBulkChangeManager ubcMan;
		@Autowired
		private BusinessGroupService businessGroupService;

		public UserBulkChangeStepForm02(UserRequest ureq, WindowControl control, Form rootForm, StepsRunContext runContext) {
			super(ureq, control, rootForm, runContext, LAYOUT_VERTICAL, null);
			// use custom translator with fallback to user properties translator
			Translator pt1 = userManager.getPropertyHandlerTranslator(getTranslator());
			Translator pt2 = Util.createPackageTranslator(BusinessGroupFormController.class, ureq.getLocale(), pt1);
			Translator pt3 = Util.createPackageTranslator(GroupSearchController.class, ureq.getLocale(), pt2);
			setTranslator(pt3);
			flc.setTranslator(pt3);
			isAdministrativeUser = securityModule.isUserAllowedAdminProps(ureq.getUserSession().getRoles());
			userPropertyHandlers = userManager.getUserPropertyHandlersFor(usageIdentifyer, isAdministrativeUser);
			
			initForm(ureq);
		}

		@Override
		protected void doDispose() {
			//
		}

		@Override
		protected void formOK(UserRequest ureq) {
			fireEvent(ureq, StepsEvent.ACTIVATE_NEXT);
		}

		@Override
		protected boolean validateFormLogic(UserRequest ureq) {
			return true;
		}

		@Override
		protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
			FormLayoutContainer formLayoutVertical = FormLayoutContainer.createVerticalFormLayout("vertical", getTranslator());
			formLayout.add(formLayoutVertical);

			setFormTitle("title");

			FormLayoutContainer textContainer = FormLayoutContainer.createCustomFormLayout("index", getTranslator(), this.velocity_root + "/step2.html");
			formLayoutVertical.add(textContainer);
			boolean validChange = userBulkChanges.isValidChange();
			textContainer.contextPut("validChange", validChange);
			if (!validChange) {
				return;
			}

			List<List<String>> mergedDataChanges = loadModel();	
			TextFlexiCellRenderer textRenderer = new TextFlexiCellRenderer(EscapeMode.none);
			FlexiTableColumnModel tableColumnModel = FlexiTableDataModelFactory.createFlexiTableColumnModel();
			// fixed fields:
			int colPos = 0;
			tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel("table.user.login", colPos++));
			tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel("form.name.pwd", colPos++));
			tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel(true, "form.name.language", colPos++, false, null, FlexiColumnModel.ALIGNMENT_LEFT, textRenderer));
			for (int j = 0; j < userPropertyHandlers.size(); j++) {
				UserPropertyHandler userPropertyHandler = userPropertyHandlers.get(j);
				tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel(true, userPropertyHandler.i18nColumnDescriptorLabelKey(), colPos++, false, null, FlexiColumnModel.ALIGNMENT_LEFT, textRenderer));
			}
			tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel(true, "table.role.added", colPos++, false, null, FlexiColumnModel.ALIGNMENT_LEFT, textRenderer));
			tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel(true, "table.role.removed", colPos++, false, null, FlexiColumnModel.ALIGNMENT_LEFT, textRenderer));
			tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel(true, "table.role.status", colPos++, false, null, FlexiColumnModel.ALIGNMENT_LEFT, textRenderer));

			FlexiTableDataModel<List<String>> tableDataModel = new FlexiTableDataModelImpl<>(new OverviewModel(mergedDataChanges, colPos), tableColumnModel);
			uifactory.addTableElement(getWindowControl(), "newUsers", tableDataModel, getTranslator(), formLayoutVertical);

			Set<Long> allGroups = new HashSet<>(); 
			List<Long> ownGroups = userBulkChanges.getOwnerGroups();
			List<Long> partGroups = userBulkChanges.getParticipantGroups();
			allGroups.addAll(ownGroups);
			allGroups.addAll(partGroups);
			List<Long> mailGroups = userBulkChanges.getMailGroups();
			
			if (!allGroups.isEmpty()) {
				uifactory.addSpacerElement("space", formLayout, true);
				uifactory.addStaticTextElement("add.to.groups", "", formLayout);
				FlexiTableColumnModel groupColumnModel = FlexiTableDataModelFactory.createFlexiTableColumnModel();
				groupColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel("table.group.name", 0));
				groupColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel("description", 1));
				groupColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel("table.user.role", 2));
				groupColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel("send.email", 3));

				List<BusinessGroup> groups = businessGroupService.loadBusinessGroups(allGroups);
				TableDataModel<BusinessGroup> model = new GroupAddOverviewModel(groups, ownGroups, partGroups, mailGroups, getTranslator()); 
				FlexiTableDataModel<BusinessGroup> groupDataModel = new FlexiTableDataModelImpl<>(model, groupColumnModel);
				
				uifactory.addTableElement(getWindowControl(), "groupOverview", groupDataModel, getTranslator(), formLayout);
			}
		}
		
		private List<List<String>> loadModel() {
			List<List<String>> mergedDataChanges = new ArrayList<>();
			OrganisationRoles[] organisationRoles = OrganisationRoles.values();
			List<Identity> selectedIdentities = userBulkChanges.getIdentitiesToEdit();
			Map<String, String> attributeChangeMap = userBulkChanges.getAttributeChangeMap();
			Map<OrganisationRoles, String> roleChangeMap = userBulkChanges.getRoleChangeMap();

			// loop over users to be edited:
			for (Identity identity : selectedIdentities) {
				List<String> userDataArray = new ArrayList<>();

				// add column for login
				userDataArray.add(identity.getName());
				// add columns for password
				if (attributeChangeMap.containsKey(UserBulkChangeManager.CRED_IDENTIFYER)) {
					userDataArray.add(attributeChangeMap.get(UserBulkChangeManager.CRED_IDENTIFYER));
				} else {
					userDataArray.add("***");
				}
				// add column for language
				String userLanguage = identity.getUser().getPreferences().getLanguage();
				if (attributeChangeMap.containsKey(UserBulkChangeManager.LANG_IDENTIFYER)) {
					String inputLanguage = attributeChangeMap.get(UserBulkChangeManager.LANG_IDENTIFYER);
					if (userLanguage.equals(inputLanguage)) {
						userDataArray.add(userLanguage);
					} else {
						userDataArray.add(decorateChangedCell(inputLanguage));
					}
				} else {
					userDataArray.add(userLanguage);
				}

				Context vcContext = new VelocityContext();
				// set all properties as context
				ubcMan.setUserContext(identity, vcContext);
				// loop for each property configured in
				// src/serviceconfig/org/olat/_spring/olat_userconfig.xml -> Key:
				// org.olat.admin.user.bulkChange.UserBulkChangeStep00
				for (int k = 0; k < userPropertyHandlers.size(); k++) {
					String propertyName = userPropertyHandlers.get(k).getName();
					String userValue = identity.getUser().getProperty(propertyName, null);

					String inputFieldValue = "";
					if (attributeChangeMap.containsKey(propertyName)) {
						inputFieldValue = attributeChangeMap.get(propertyName);
						inputFieldValue = inputFieldValue.replace("$", "$!");
						String evaluatedInputFieldValue = ubcMan.evaluateValueWithUserContext(inputFieldValue, vcContext);

						if (evaluatedInputFieldValue.equals(userValue)) {
							userDataArray.add(userValue);
						} else {
							// style italic:
							userDataArray.add(decorateChangedCell(evaluatedInputFieldValue));
						}
					} else {
						// property has not been checked in step00 but should be in
						// overview-table
						userDataArray.add(userValue);
					}

				} // for

				// add columns with roles
				// loop over securityGroups and get result...
				List<String> identityRoles = securityManager.getRolesAsString(identity);
				StringBuilder addedRole = new StringBuilder();
				StringBuilder removedRole = new StringBuilder();
				for (OrganisationRoles organisationRole : organisationRoles) {
					getRoleStatusForIdentity(organisationRole, identityRoles, roleChangeMap, addedRole, removedRole);
				}
				
				String addedRolesString = addedRole.toString();
				if(addedRolesString.length() > 0) {
					addedRolesString = decorateChangedCell(addedRolesString);
				}
				userDataArray.add(addedRolesString);
				String removedRolesString = removedRole.toString();
				if(removedRolesString.length() > 0) {
					removedRolesString = decorateChangedCell(removedRolesString);
				}
				userDataArray.add(removedRolesString);
	
				// add column with status
				userDataArray.add(userBulkChanges.getStatus() == null ? "" : userBulkChanges.getStatus().toString());

				// add each user:
				mergedDataChanges.add(userDataArray);
			}
			return mergedDataChanges;
		}

		/**
		 * compare roles of given identity with changes to be applied from
		 * wizard-step 01
		 * 
		 * @param identity
		 * @param securityGroup
		 * @param roleChangeMap
		 * @return
		 */
		private void getRoleStatusForIdentity(OrganisationRoles role, List<String> currentRoles, Map<OrganisationRoles, String> roleChangeMap,
				StringBuilder addedRole, StringBuilder removedRole) {
			if(role == OrganisationRoles.user || role == OrganisationRoles.invitee || role == OrganisationRoles.guest
					|| !roleChangeMap.containsKey(role)) return;

			boolean isInGroup = currentRoles.contains(role.name());
			String thisRoleAction = roleChangeMap.get(role);
			if (isInGroup && thisRoleAction.equals("remove")) {
				if(removedRole.length() > 0) removedRole.append(", ");
				removedRole.append(translate("table.role.".concat(role.name())));
			} else if(!isInGroup && thisRoleAction.equals("add")) {
				if(addedRole.length() > 0) addedRole.append(", ");
				addedRole.append(translate("table.role.".concat(role.name())));
			}
		}

		
		private String decorateChangedCell(String val) {
			return "<span class='o_userbulk_changedcell'><i class='o_icon o_icon_new'> </i> " + val + "</span>";
		}
	}
}