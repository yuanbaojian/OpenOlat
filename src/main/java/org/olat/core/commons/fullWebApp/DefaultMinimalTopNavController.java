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
package org.olat.core.commons.fullWebApp;

import org.olat.core.dispatcher.impl.StaticMediaDispatcher;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.Windows;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.Window;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowBackOffice;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.media.RedirectMediaResource;

/**
 * Description:<br>
 * The default minimal top navigation offers a close and print link
 * <P>
 * Initial Date: 24.09.2009 <br>
 * 
 * @author Florian Gnägi
 */
public class DefaultMinimalTopNavController extends BasicController {
	private VelocityContainer topNavVC;
	private Link closeLink;

	/**
	 * Default constructor
	 * 
	 * @param ureq
	 * @param wControl
	 */
	public DefaultMinimalTopNavController(UserRequest ureq,
			WindowControl wControl) {
		super(ureq, wControl);
		topNavVC = createVelocityContainer("defaulttopnavminimal");
		closeLink = LinkFactory.createLink("header.topnav.close", topNavVC, this);
		closeLink.setIconLeftCSS("o_icon o_icon_close");
		putInitialPanel(topNavVC);
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#doDispose()
	 */
	@Override
	protected void doDispose() {
		// nothing to dispose
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.components.Component,
	 *      org.olat.core.gui.control.Event)
	 */
	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if (source == closeLink) {
			// close window (a html page which calls Window.close onLoad
			ureq.getDispatchResult().setResultingMediaResource(
					new RedirectMediaResource(StaticMediaDispatcher.createStaticURIFor("closewindow.html")));
			// release all resources and close window
			WindowBackOffice wbo = getWindowControl().getWindowBackOffice();
			Window w = wbo.getWindow();
			Windows.getWindows(ureq).deregisterWindow(w);
			wbo.dispose();
		}
	}

}
