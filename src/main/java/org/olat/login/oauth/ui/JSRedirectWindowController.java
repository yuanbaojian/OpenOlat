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

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.WindowSettings;
import org.olat.core.gui.Windows;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.Window;
import org.olat.core.gui.components.htmlheader.jscss.CustomCSS;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.DefaultChiefController;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowBackOffice;
import org.olat.core.gui.control.navigation.SiteInstance;
import org.olat.core.gui.translator.Translator;
import org.olat.core.helpers.Settings;
import org.olat.core.util.Util;
import org.olat.login.oauth.OAuthConstants;

/**
 * 
 * redirect for the OpenID connect Implicit Workflow.
 * 
 * Initial date: 19.07.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class JSRedirectWindowController extends DefaultChiefController {
	private static final String VELOCITY_ROOT = Util.getPackageVelocityRoot(JSRedirectWindowController.class);

	public JSRedirectWindowController(UserRequest ureq) {
		Translator trans = Util.createPackageTranslator(JSRedirectWindowController.class, ureq.getLocale());
		VelocityContainer msg = new VelocityContainer("jsredirect", VELOCITY_ROOT + "/js_redirect.html", trans, this);

		String callbackUrl = Settings.getServerContextPathURI() + OAuthConstants.CALLBACK_PATH;
		msg.contextPut("callbackUrl", callbackUrl);
		
		Windows ws = Windows.getWindows(ureq);
		WindowBackOffice wbo = ws.getWindowManager().createWindowBackOffice("jsredirectwindow", this, new WindowSettings());
		Window w = wbo.getWindow();
		msg.contextPut("theme", w.getGuiTheme());
		w.setContentPane(msg);
		setWindow(w);
	}
	
	@Override
	public boolean isLoginInterceptionInProgress() {
		return false;
	}
	
	@Override
	public boolean hasStaticSite(Class<? extends SiteInstance> type) {
		return false;
	}

	@Override
	public void addBodyCssClass(String cssClass) {
		//
	}

	@Override
	public void removeBodyCssClass(String cssClass) {
		//
	}
	
	@Override
	public void addCurrentCustomCSSToView(CustomCSS customCSS) {
		//
	}
	
	@Override
	public void removeCurrentCustomCSSFromView() {
		//
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.components.Component, org.olat.core.gui.control.Event)
	 */
	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		//
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#doDispose(boolean)
	 */
	@Override
	protected void doDispose() {
		//
	}
}