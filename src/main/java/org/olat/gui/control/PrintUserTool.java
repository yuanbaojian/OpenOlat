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
package org.olat.gui.control;

import org.olat.admin.user.tools.UserTool;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.ComponentEventListener;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Event;
import org.olat.core.util.CodeHelper;
import org.olat.core.util.Util;

/**
 * 
 * Initial date: 29.10.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class PrintUserTool implements UserTool, ComponentEventListener {

	@Override
	public Component getMenuComponent(UserRequest ureq, VelocityContainer container) {
		String componentName = "print-" + CodeHelper.getRAMUniqueID();
		String velocity_root = Util.getPackageVelocityRoot(PrintUserTool.class);
		String pagePath = velocity_root + "/print.html";
		VelocityContainer print = new VelocityContainer(componentName, pagePath, container.getTranslator(), this);
		print.setDomReplacementWrapperRequired(false);
		container.put(componentName, print);
		return print;
	}

	@Override
	public void dispatchEvent(UserRequest ureq, Component source, Event event) {
		//
	}

	@Override
	public void dispose() {
		//
	}
}
