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
package org.olat.admin.user.bulkChange;

import org.olat.basesecurity.BaseSecurity;
import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.FormUIFactory;
import org.olat.core.gui.components.form.flexible.elements.SelectionElement;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.panel.Panel;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.id.Identity;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.login.auth.OLATAuthManager;
import org.olat.registration.RegistrationManager;
import org.olat.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Description:<br>
 * This is an extension for the admin site for changing password for a user list 
 * (adding respectively OLAT authentication for the ones that doesn't have one).
 * 
 * <P>
 * Initial Date:  25.05.2010 <br>
 * @author Lavinia Dumitrescu
 */
public class UserBulkChangePasswordController extends BasicController {
	
	private static final OLog log = Tracing.createLoggerFor(UserBulkChangePasswordController.class);
	
	private ChangePasswordForm changePasswordForm;
	private final OLATAuthManager olatAuthenticationSpi;
	
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private RegistrationManager registrationManager;

	public UserBulkChangePasswordController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		
		olatAuthenticationSpi = CoreSpringFactory.getImpl(OLATAuthManager.class);

		Panel main = new Panel("changePsw");
		VelocityContainer mainVC = createVelocityContainer("index");
		
		changePasswordForm = new ChangePasswordForm(ureq, wControl);
		listenTo(changePasswordForm);
		mainVC.put("form", changePasswordForm.getInitialComponent());		
		
		main.setContent(mainVC);
		putInitialPanel(main);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		//
	}

	@Override
	public void event(UserRequest ureq, Controller source, Event event) {		
		if(event == Event.DONE_EVENT) {
			String[] usernames = changePasswordForm.getUsernames();
			String password = changePasswordForm.getPassword();
			boolean autodisc = changePasswordForm.getDisclaimerAccept();
			boolean langGerman = changePasswordForm.getLangGerman();
			
			int c = 0;
			
			for(String username:usernames) {
				if (username.length()==0) continue;
				try {
					Identity identity = securityManager.findIdentityByName(username);
					if(identity!=null) {
						if (password!=null && password.trim().length()>0) {
							olatAuthenticationSpi.changePassword(ureq.getIdentity(), identity, password);	
							log.info("changePassword for username: " + username);
						}
						if (autodisc) {
							registrationManager.setHasConfirmedDislaimer(identity);
							log.info("Disclaimer accepted for username: " + username);
						}
						if (langGerman) {
							identity.getUser().getPreferences().setLanguage("de");
							UserManager.getInstance().updateUserFromIdentity(identity);
							log.info("Set language German for username: " + username);
						}
						
						c++;
						
					}	else {							 
						log.warn("could find user with username: " + username);
					}
				} catch (Exception e) {
					log.error("Failed to change password/settings for username: " + username, e);
				}
			}				
			
			//notify done
			getWindowControl().setInfo(translate("bulk.psw.done", ""+c));
		}
	}

	/**
	 * ChangePasswordForm.
	 * 
	 * <P>
	 * Initial Date:  08.06.2010 <br>
	 * @author Lavinia Dumitrescu
	 */
	private class ChangePasswordForm extends FormBasicController {
		
		private TextElement olatPasswordAuthentication;
		private TextElement userListTextArea;
		private SelectionElement acceptDisclaimer;
		private SelectionElement langGerman;

		public ChangePasswordForm(UserRequest ureq, WindowControl wControl) {
			super(ureq, wControl);

			initForm(ureq);
		}

		@Override
		protected void formOK(UserRequest ureq) {			
			fireEvent(ureq, Event.DONE_EVENT);
		}
		
		@Override
		protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {			
			
			userListTextArea = uifactory.addTextAreaElement("bulk.psw.users", 10, 2, null, formLayout);
			olatPasswordAuthentication = uifactory.addTextElement("pswtextfield", "bulk.psw.newpsw", 255, "", formLayout);
			acceptDisclaimer = uifactory.addCheckboxesHorizontal("bulk.auto.disc", formLayout, new String[]{"xx"}, new String[]{null});
			langGerman = uifactory.addCheckboxesHorizontal("bulk.lang.german", formLayout, new String[]{"xx"}, new String[]{null});
			
			final FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("button_layout", getTranslator());
			formLayout.add(buttonLayout);
			FormUIFactory.getInstance().addFormSubmitButton("bulk.psw.submit", buttonLayout);
			
			acceptDisclaimer.select("xx", true);
			langGerman.select("xx", true);
		}
		
		private String[] getUsernames(){
			return userListTextArea.getValue().split("\r\n");
		}
		
		private String getPassword() {
			return olatPasswordAuthentication.getValue();
		}
		
		private boolean getDisclaimerAccept() {
			return acceptDisclaimer.isSelected(0);
		}
		
		private boolean getLangGerman() {
			return langGerman.isSelected(0);
		}
		
		@Override
		protected void doDispose() {
			if(olatPasswordAuthentication!=null) {
				olatPasswordAuthentication.setValue("");
			}
			if(userListTextArea!=null) {
				userListTextArea.setValue("");
			}
		}	
		
	}
	
}
