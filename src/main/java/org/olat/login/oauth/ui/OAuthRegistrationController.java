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
package org.olat.login.oauth.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olat.admin.user.delete.service.UserDeletionManager;
import org.olat.basesecurity.AuthHelper;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.OrganisationService;
import org.olat.core.dispatcher.DispatcherModule;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.id.Identity;
import org.olat.core.id.User;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.core.util.WebappHelper;
import org.olat.core.util.i18n.I18nManager;
import org.olat.login.oauth.model.OAuthRegistration;
import org.olat.login.oauth.model.OAuthUser;
import org.olat.registration.DisclaimerController;
import org.olat.registration.RegistrationForm2;
import org.olat.registration.RegistrationManager;
import org.olat.user.UserManager;
import org.olat.user.UserModule;
import org.olat.user.propertyhandlers.UserPropertyHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 04.11.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class OAuthRegistrationController extends FormBasicController {
	
	public static final String USERPROPERTIES_FORM_IDENTIFIER = OAuthRegistrationController.class.getCanonicalName();
	
	private final OAuthRegistration registration;
	private final List<UserPropertyHandler> userPropertyHandlers;

	private TextElement usernameEl;
	private SingleSelection langEl;
	private Map<String,FormItem> propFormItems = new HashMap<>();
	private CloseableModalController cmc;
	private DisclaimerController disclaimerController;
	
	private Identity authenticatedIdentity;
	
	@Autowired
	private UserModule userModule;
	@Autowired
	private UserManager userManager;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private UserDeletionManager userDeletionManager;
	@Autowired
	private OrganisationService organisationService;
	@Autowired
	private RegistrationManager registrationManager;
	
	public OAuthRegistrationController(UserRequest ureq, WindowControl wControl, OAuthRegistration registration) {
		super(ureq, wControl);
		setTranslator(Util.createPackageTranslator(RegistrationForm2.class, getLocale(), getTranslator()));
		setTranslator(Util.createPackageTranslator(UserPropertyHandler.class, getLocale(), getTranslator()));
		userPropertyHandlers = userManager.getUserPropertyHandlersFor(USERPROPERTIES_FORM_IDENTIFIER, false);
		
		this.registration = registration;

		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener,	UserRequest ureq) {
		OAuthUser oauthUser = registration.getOauthUser();
		
		usernameEl = uifactory.addTextElement("username",  "user.login", 128, "", formLayout);
		usernameEl.setMandatory(true);
		if(StringHelper.containsNonWhitespace(oauthUser.getId())) {
			usernameEl.setValue(oauthUser.getId());
		}

		// Add all available user fields to this form
		for (UserPropertyHandler userPropertyHandler : userPropertyHandlers) {
			if (userPropertyHandler != null) {
				FormItem fi = userPropertyHandler.addFormItem(getLocale(), null, USERPROPERTIES_FORM_IDENTIFIER, false, formLayout);
				propFormItems.put(userPropertyHandler.getName(), fi);
				if(fi instanceof TextElement) {
					String value = oauthUser.getProperty(userPropertyHandler.getName());
					if(StringHelper.containsNonWhitespace(value)) {
						((TextElement)fi).setValue(value);
					}
				}
			}
		}
		
		uifactory.addSpacerElement("lang", formLayout, true);
		// second the user language
		Map<String, String> languages = I18nManager.getInstance().getEnabledLanguagesTranslated();
		String[] langKeys = StringHelper.getMapKeysAsStringArray(languages);
		String[] langValues = StringHelper.getMapValuesAsStringArray(languages);
		langEl = uifactory.addDropdownSingleselect("user.language", formLayout, langKeys, langValues, null);
		
		FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("button_layout", getTranslator());
		formLayout.add(buttonLayout);
		uifactory.addFormSubmitButton("save", buttonLayout);
	}

	@Override
	protected void doDispose() {
		//
	}
	
	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if(disclaimerController == source) {
			cmc.deactivate();
			
			if (event == Event.DONE_EVENT) {
				// User accepted disclaimer, do login now
				registrationManager.setHasConfirmedDislaimer(authenticatedIdentity);
				doLoginAndRegister(authenticatedIdentity, ureq);
			} else if (event == Event.CANCELLED_EVENT) {
				// User did not accept, workflow ends here
				showWarning("disclaimer.form.cancelled");
			}
			cleanUp();
		} else if(cmc == source) {
			cleanUp();
		}
		super.event(ureq, source, event);
	}
	
	private void cleanUp() {
		removeAsListenerAndDispose(disclaimerController);
		removeAsListenerAndDispose(cmc);
		disclaimerController = null;
		cmc = null;
	}
	
	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = super.validateFormLogic(ureq);
		// validate each user field
		for (UserPropertyHandler userPropertyHandler : userPropertyHandlers) {
			FormItem fi = propFormItems.get(userPropertyHandler.getName());
			if (!userPropertyHandler.isValid(null, fi, null)) {
				allOk &= false;
			}
		}
		
		String login = usernameEl.getValue();
		usernameEl.clearError();
		if (!userManager.syntaxCheckOlatLogin(login)) {
			usernameEl.setErrorKey("form.check3", null);
			allOk &= false;
		} else if (userModule.isLoginOnBlacklist(login)) {
			usernameEl.setErrorKey("form.check6", null);
			allOk &= false;
		} else {
			Identity s = securityManager.findIdentityByName(login);
			if (s != null) {
				usernameEl.setErrorKey("form.check6", null);
				allOk &= false;
			}
		}
		return allOk;
	}

	@Override
	protected void formOK(UserRequest ureq) {
		String lang = langEl.getSelectedKey();
		String username = usernameEl.getValue();
		OAuthUser oauthUser = registration.getOauthUser();

		User newUser = userManager.createUser(null, null, null);
		for (UserPropertyHandler userPropertyHandler : userPropertyHandlers) {
			FormItem propertyItem = this.flc.getFormComponent(userPropertyHandler.getName());
			userPropertyHandler.updateUserFromFormItem(newUser, propertyItem);
		}
		
		// Init preferences
		newUser.getPreferences().setLanguage(lang);
		newUser.getPreferences().setInformSessionTimeout(true);
		
		String id;
		if(StringHelper.containsNonWhitespace(oauthUser.getId())) {
			id = oauthUser.getId();
		} else if(StringHelper.containsNonWhitespace(oauthUser.getEmail())) {
			id = oauthUser.getEmail();
		} else {
			id = username;
		}
		authenticatedIdentity = securityManager.createAndPersistIdentityAndUser(username, null, newUser, registration.getAuthProvider(), id, null);
		// Add user to default organization as user
		organisationService.addMember(authenticatedIdentity, OrganisationRoles.user);
		
		//open disclaimer
		removeAsListenerAndDispose(disclaimerController);
		disclaimerController = new DisclaimerController(ureq, getWindowControl(), authenticatedIdentity, false);
		listenTo(disclaimerController);
		
		cmc = new CloseableModalController(getWindowControl(), translate("close"), disclaimerController.getInitialComponent(),
				true, translate("disclaimer.title"));
		cmc.activate();
		listenTo(cmc);
	}
	
	private void doLoginAndRegister(Identity authIdentity, UserRequest ureq) {
		// prepare redirects to home etc, set status
		int loginStatus = AuthHelper.doLogin(authIdentity, registration.getAuthProvider(), ureq);
		if (loginStatus == AuthHelper.LOGIN_OK) {
			//update last login date and register active user
			userDeletionManager.setIdentityAsActiv(authIdentity);
		} else if (loginStatus == AuthHelper.LOGIN_NOTAVAILABLE){
			DispatcherModule.redirectToServiceNotAvailable( ureq.getHttpResp() );
		} else {
			getWindowControl().setError(translate("login.error", WebappHelper.getMailConfig("mailSupport")));
		}
	}
}