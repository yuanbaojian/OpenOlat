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
package org.olat.group.ui.main;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.id.Identity;
import org.olat.group.BusinessGroup;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 30.01.2013<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class EditSingleMembershipController extends BasicController {
	
	private final MemberInfoController infoCtrl;
	private final EditMembershipController membershipCtrl;
	
	private final RepositoryEntry repoEntry;

	public EditSingleMembershipController(UserRequest ureq, WindowControl wControl,  Identity identity,
			RepositoryEntry repoEntry, BusinessGroup group, boolean withUserLinks, boolean overrideManaged) {
		super(ureq, wControl);
		this.repoEntry = repoEntry;
		
		VelocityContainer mainVC = createVelocityContainer("edit_single_member");
		infoCtrl = new MemberInfoController(ureq, wControl, identity, repoEntry, withUserLinks);
		listenTo(infoCtrl);
		mainVC.put("infos", infoCtrl.getInitialComponent());
		
		membershipCtrl = new EditMembershipController(ureq, wControl, identity, repoEntry, group, overrideManaged);
		listenTo(membershipCtrl);
		mainVC.put("edit", membershipCtrl.getInitialComponent());
		
		putInitialPanel(mainVC);
	}
	
	public RepositoryEntry getRepositoryEntry() {
		return repoEntry;
	}
	
	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		fireEvent(ureq, event);
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		fireEvent(ureq, event);
	}
}
