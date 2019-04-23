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
package org.olat.core.commons.services.help.spi;

import java.util.Locale;

import org.olat.admin.user.tools.UserTool;
import org.olat.core.commons.fullWebApp.popup.BaseFullWebappPopupLayoutFactory;
import org.olat.core.commons.services.help.ConfluenceHelper;
import org.olat.core.commons.services.help.HelpLinkSPI;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.ComponentEventListener;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.creator.ControllerCreator;
import org.olat.course.CourseFactory;
import org.springframework.stereotype.Service;

/**
 * Legacy course help system which uses the old help course when clicking the
 * manual button. Since the course can not support the context help we use the
 * confluence context help for context help.
 * 
 * Initial date: 07.01.2015<br>
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service("courseHelp")
public class HelpCourseSPI implements HelpLinkSPI  {

	@Override
	public UserTool getHelpUserTool(WindowControl wControl) {
		return new HelCourseUserTool(wControl);
	}
	
	public class HelCourseUserTool implements UserTool, ComponentEventListener {
		
		private final WindowControl wControl;
		
		public HelCourseUserTool(WindowControl wControl) {
			this.wControl = wControl;
		}

		@Override
		public Component getMenuComponent(UserRequest ureq, VelocityContainer container) {
			Link helpLink = LinkFactory.createLink("topnav.help", container, this);
			helpLink.setIconLeftCSS("o_icon o_icon_help o_icon-lg");
			helpLink.setTooltip("topnav.help.alt");
			helpLink.setTarget("oohelp");
			return helpLink;
		}

		@Override
		public void dispatchEvent(UserRequest ureq, Component source, Event event) {
			ControllerCreator ctrlCreator = new ControllerCreator() {
				public Controller createController(UserRequest lureq, WindowControl lwControl) {
					return CourseFactory.createHelpCourseLaunchController(lureq, lwControl);
				}					
			};
			//wrap the content controller into a full header layout
			ControllerCreator layoutCtrlr = BaseFullWebappPopupLayoutFactory.createAuthMinimalPopupLayout(ureq, ctrlCreator);
			//open in new browser window
			wControl.getWindowBackOffice().getWindowManager()
					.createNewPopupBrowserWindowFor(ureq, layoutCtrlr)
					.open(ureq);
		}

		@Override
		public void dispose() {
			//
		}
	}
	
	@Override
	public String getURL(Locale locale, String page) {
		// Fallback to confluence context help
		return ConfluenceHelper.getURL(locale, page);
	}

	@Override
	public Component getHelpPageLink(UserRequest ureq, String title, String tooltip, String iconCSS, String elementCSS,
			String page) {
		// Fallback to confluence context help
		return ConfluenceHelper.createHelpPageLink(ureq, title, tooltip, iconCSS, elementCSS, page);
	}
}
