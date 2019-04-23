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
package org.olat.admin.user.tools;

import java.util.Locale;

import org.olat.NewControllerFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.ComponentEventListener;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.CodeHelper;

/**
 * 
 * Initial date: 29.10.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class UserToolImpl implements UserTool, ComponentEventListener {

	private Locale locale;
	private UserToolExtension extension;
	private WindowControl wControl;

	public UserToolImpl(UserToolExtension extension, WindowControl wControl, Locale locale) {
		this.locale = locale;
		this.wControl = wControl;
		this.extension = extension;
	}

	@Override
	public Component getMenuComponent(UserRequest ureq, VelocityContainer container) {
		String label = extension.getLabel(locale);
		String iconCssClass = extension.getIconCssClass();
		String linkName = "personal.tool.alt." + CodeHelper.getRAMUniqueID();
		Link link = LinkFactory.createLink(linkName, linkName, container.getTranslator(), container, this, Link.LINK | Link.NONTRANSLATED);
		link.setUserObject(this);
		link.setCustomDisplayText(label);
		link.setElementCssClass("o_sel_user_tools-" + extension.getNavigationKey());
		link.setIconLeftCSS(iconCssClass + " o_icon-lg");
		link.setTooltip(label);
		link.setTitle(label);
		return link;
	}

	@Override
	public void dispatchEvent(UserRequest ureq, Component source, Event event) {
		String navKey = extension.getNavigationKey();
		Long identityKey = ureq.getUserSession().getIdentity().getKey();
		String businessPath = "[HomeSite:" + identityKey + "][" + navKey + ":0]";
		NewControllerFactory.getInstance().launch(businessPath, ureq, wControl);
	}

	@Override
	public void dispose() {
		//
	}
}
