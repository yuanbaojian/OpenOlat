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
package org.olat.modules.portfolio.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.BaseSecurityModule;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.OrganisationService;
import org.olat.core.commons.persistence.PersistenceHelper;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FlexiTableElement;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataModelFactory;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.StepFormBasicController;
import org.olat.core.gui.control.generic.wizard.StepsEvent;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.id.UserConstants;
import org.olat.core.util.Util;
import org.olat.modules.portfolio.ui.PortfolioHomeController;
import org.olat.modules.portfolio.ui.PublishController;
import org.olat.user.UserManager;
import org.olat.user.propertyhandlers.UserPropertyHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class MembersOverviewIdentitiesController extends StepFormBasicController {

	protected static final String USER_PROPS_ID = PortfolioHomeController.class.getCanonicalName();
	
	private List<Identity> oks;
	private List<String> notfounds;
	private boolean isAdministrativeUser;
	
	@Autowired
	private UserManager userManager;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private BaseSecurityModule securityModule;
	@Autowired
	private OrganisationService organisationService;

	public MembersOverviewIdentitiesController(UserRequest ureq, WindowControl wControl, Form rootForm, StepsRunContext runContext) {
		super(ureq, wControl, rootForm, runContext, LAYOUT_VERTICAL, null);
		setTranslator(userManager.getPropertyHandlerTranslator(getTranslator()));
		setTranslator(Util.createPackageTranslator(PublishController.class, getLocale(), getTranslator()));

		oks = null;
		if(containsRunContextKey("logins")) {
			String logins = (String)runContext.get("logins");
			loadModel(logins);
		} else if(containsRunContextKey("keys")) {
			@SuppressWarnings("unchecked")
			List<String> keys = (List<String>)runContext.get("keys");
			loadModel(keys);
		} else if(containsRunContextKey("identities")) {
			@SuppressWarnings("unchecked")
			List<Identity> identities = (List<Identity>)runContext.get("identities");
			loadModelByIdentities(identities);
		}

		isAdministrativeUser = securityModule.isUserAllowedAdminProps(ureq.getUserSession().getRoles());

		initForm (ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		formLayout.setElementCssClass("o_sel_user_import_overview");
		if(notfounds != null && !notfounds.isEmpty()) {
			String page = velocity_root + "/warn_notfound.html";
			FormLayoutContainer warnLayout = FormLayoutContainer.createCustomFormLayout("warnNotFounds", getTranslator(), page);
			warnLayout.setRootForm(mainForm);
			formLayout.add(warnLayout);
			
			StringBuilder sb = new StringBuilder();
			for(String notfound:notfounds) {
				if(sb.length() > 0) sb.append(", ");
				sb.append(notfound);
			}
			String msg = translate("user.notfound", new String[]{sb.toString()});
			addToRunContext("notFounds", sb.toString());
			warnLayout.contextPut("notFounds", msg);
		}
		
		//add the table
		FlexiTableColumnModel tableColumnModel = FlexiTableDataModelFactory.createFlexiTableColumnModel();
		int colIndex = 0;
		if(isAdministrativeUser) {
			tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel("table.user.login", colIndex++));
		}
		List<UserPropertyHandler> userPropertyHandlers = userManager.getUserPropertyHandlersFor(USER_PROPS_ID, isAdministrativeUser);
		List<UserPropertyHandler> resultingPropertyHandlers = new ArrayList<>();
		// followed by the users fields
		for (int i = 0; i < userPropertyHandlers.size(); i++) {
			UserPropertyHandler userPropertyHandler	= userPropertyHandlers.get(i);
			boolean visible = UserManager.getInstance().isMandatoryUserProperty(USER_PROPS_ID , userPropertyHandler);
			if(visible) {
				resultingPropertyHandlers.add(userPropertyHandler);
				tableColumnModel.addFlexiColumnModel(new DefaultFlexiColumnModel(userPropertyHandler.i18nColumnDescriptorLabelKey(), colIndex++));
			}
		}
		
		Translator myTrans = userManager.getPropertyHandlerTranslator(getTranslator());
		MembersOverviewDataModel userTableModel = new MembersOverviewDataModel(oks, resultingPropertyHandlers,
				isAdministrativeUser, getLocale(), tableColumnModel);
		FlexiTableElement tableEl = uifactory.addTableElement(getWindowControl(), "users", userTableModel, myTrans, formLayout);
		tableEl.setCustomizeColumns(false);
	}
	
	private void loadModelByIdentities(List<Identity> identities) {
		oks = new ArrayList<>();
		List<String> isanonymous = new ArrayList<>();
		notfounds = new ArrayList<>();

		for (Identity ident : identities) {
			if (organisationService.hasRole(ident, OrganisationRoles.guest)) {
				isanonymous.add(ident.getKey().toString());
			} else if (!PersistenceHelper.containsPersistable(oks, ident)) {
				oks.add(ident);
			}
		}
	}
	
	private void loadModel(List<String> keys) {
		oks = new ArrayList<>();
		List<String> isanonymous = new ArrayList<>();
		notfounds = new ArrayList<>();

		for (String identityKey : keys) {
			Identity ident = securityManager.loadIdentityByKey(Long.parseLong(identityKey));
			if (ident == null) { // not found, add to not-found-list
				notfounds.add(identityKey);
			} else if (organisationService.hasRole(ident, OrganisationRoles.guest)) {
				isanonymous.add(identityKey);
			} else if (!PersistenceHelper.containsPersistable(oks, ident)) {
				oks.add(ident);
			}
		}
	}
	
	private void loadModel(String inp) {
		oks = new ArrayList<>();
		notfounds = new ArrayList<>();

		List<String> identList = new ArrayList<>();
		String[] lines = inp.split("\r?\n");
		for (int i = 0; i < lines.length; i++) {
			String username = lines[i].trim();
			if(username.length() > 0) {
				identList.add(username);
			}
		}
		
		//search by institutionalUserIdentifier, case sensitive
		List<Identity> institutIdentities = securityManager.findIdentitiesByNumber(identList);
		for(Identity identity:institutIdentities) {
			String userIdent = identity.getUser().getProperty(UserConstants.INSTITUTIONALUSERIDENTIFIER, null);
			if(userIdent != null) {
				identList.remove(userIdent);
			}
			if (!PersistenceHelper.containsPersistable(oks, identity) && !organisationService.hasRole(identity, OrganisationRoles.guest)) {
				oks.add(identity);
			}
		}
		// make a lowercase copy of identList for processing username and email
		List<String> identListLowercase = new ArrayList<>(identList.size());
		for (String ident:identList) {
			identListLowercase.add(ident.toLowerCase());
		}
		//search by names, must be lower case
		List<Identity> identities = securityManager.findIdentitiesByNameCaseInsensitive(identListLowercase);
		for(Identity identity:identities) {
			identListLowercase.remove(identity.getName().toLowerCase());
			if (!PersistenceHelper.containsPersistable(oks, identity)
					&& !organisationService.hasRole(identity, OrganisationRoles.guest)) {
				oks.add(identity);
			}
		}
		
		//search by email, case insensitive
		List<Identity> mailIdentities = userManager.findIdentitiesByEmail(identListLowercase);
		for(Identity identity:mailIdentities) {
			String email = identity.getUser().getProperty(UserConstants.EMAIL, null);
			if(email != null) {
				identListLowercase.remove(email.toLowerCase());
			}
			String institutEmail = identity.getUser().getProperty(UserConstants.INSTITUTIONALEMAIL, null);
			if(institutEmail != null) {
				identListLowercase.remove(institutEmail.toLowerCase());
			}
			if (!PersistenceHelper.containsPersistable(oks, identity)
					&& !organisationService.hasRole(identity, OrganisationRoles.guest)) {
				oks.add(identity);
			}
		}
		
		notfounds.addAll(identListLowercase);
	}

	public boolean validate() {
		return true;
	}

	@Override
	protected void formOK(UserRequest ureq) {
		AccessRightsContext rightsContext = (AccessRightsContext)getFromRunContext("rightsContext");
		rightsContext.setIdentities(oks);
		fireEvent(ureq, StepsEvent.ACTIVATE_NEXT);
	}

	@Override
	protected void doDispose() {
		//
	}
}