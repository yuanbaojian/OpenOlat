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
package org.olat.admin.restapi;

import org.olat.basesecurity.BaseSecurityModule;
import org.olat.commons.calendar.CalendarModule;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.helpers.Settings;
import org.olat.group.BusinessGroupModule;
import org.olat.repository.RepositoryModule;
import org.olat.restapi.RestModule;
import org.olat.restapi.security.RestSecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Description:<br>
 * This is a controller to configure the REST API and the
 * managed courses, groups and calendars.
 * 
 * <P>
 * Initial Date:  21 sept. 2009 <br>
 *
 * @author srosse, stephane.rosse@frentix.com, https://ww.frentix.com
 */
public class RestapiAdminController extends FormBasicController {
	
	private MultipleSelectionElement enabled;
	private MultipleSelectionElement managedRepoEl;
	private MultipleSelectionElement managedGroupsEl;
	private MultipleSelectionElement managedCalendarEl;
	private MultipleSelectionElement managedRelationRole;
	private FormLayoutContainer docLinkFlc;
	
	private static final String[] keys = {"on"};
	
	@Autowired
	private RestModule restModule;
	@Autowired
	private CalendarModule calendarModule;
	@Autowired
	private BusinessGroupModule groupModule;
	@Autowired
	private RepositoryModule repositoryModule;
	@Autowired
	private BaseSecurityModule securityModule;

	public RestapiAdminController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl, "rest");
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		setFormTitle("rest.title");
		setFormContextHelp("REST API");

		if(formLayout instanceof FormLayoutContainer) {
			FormLayoutContainer layoutContainer = (FormLayoutContainer)formLayout;
			
			boolean restEnabled = restModule.isEnabled();
			docLinkFlc = FormLayoutContainer.createCustomFormLayout("doc_link", getTranslator(), velocity_root + "/docLink.html");
			layoutContainer.add(docLinkFlc);
			docLinkFlc.setVisible(restEnabled);
			
			String link = Settings.getServerContextPathURI() + RestSecurityHelper.SUB_CONTEXT + "/api/doc";
			docLinkFlc.contextPut("docLink", link);
			
			FormLayoutContainer accessDataFlc = FormLayoutContainer.createDefaultFormLayout("flc_access_data", getTranslator());
			layoutContainer.add(accessDataFlc);

			String[] valueOn = new String[] { getTranslator().translate("rest.on") };
			enabled = uifactory.addCheckboxesHorizontal("rest.enabled", accessDataFlc, keys, valueOn);
			enabled.select(keys[0], restEnabled);
			enabled.addActionListener(FormEvent.ONCHANGE);
			
			accessDataFlc.setVisible(true);
			formLayout.add(accessDataFlc);
			
			FormLayoutContainer managedFlc = FormLayoutContainer.createDefaultFormLayout("flc_managed", getTranslator());
			layoutContainer.add(managedFlc);
			
			managedGroupsEl = uifactory.addCheckboxesHorizontal("managed.group", managedFlc, keys, valueOn);
			managedGroupsEl.addActionListener(FormEvent.ONCHANGE);
			managedGroupsEl.select(keys[0], groupModule.isManagedBusinessGroups());
			
			managedRepoEl = uifactory.addCheckboxesHorizontal("managed.repo", managedFlc, keys, valueOn);
			managedRepoEl.addActionListener(FormEvent.ONCHANGE);
			managedRepoEl.select(keys[0], repositoryModule.isManagedRepositoryEntries());
			
			managedCalendarEl = uifactory.addCheckboxesHorizontal("managed.cal", managedFlc, keys, valueOn);
			managedCalendarEl.addActionListener(FormEvent.ONCHANGE);
			managedCalendarEl.select(keys[0], calendarModule.isManagedCalendars());
			
			managedRelationRole = uifactory.addCheckboxesHorizontal("managed.relation.role", managedFlc, keys, valueOn);
			managedRelationRole.addActionListener(FormEvent.ONCHANGE);
			managedRelationRole.select(keys[0], securityModule.isRelationRoleManaged());
		}
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void formOK(UserRequest ureq) {
		//
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(source == enabled) {
			boolean on = enabled.isAtLeastSelected(1);
			restModule.setEnabled(on);
			docLinkFlc.setVisible(on);
			getWindowControl().setInfo("saved");
		} else if(source == managedGroupsEl) {
			boolean enable = managedGroupsEl.isAtLeastSelected(1);
			groupModule.setManagedBusinessGroups(enable);
		} else if (source == managedRepoEl) {
			boolean enable = managedRepoEl.isAtLeastSelected(1);
			repositoryModule.setManagedRepositoryEntries(enable);
		} else if (source == managedCalendarEl) {
			boolean enable = managedCalendarEl.isAtLeastSelected(1);
			calendarModule.setManagedCalendars(enable);
		} else if (source == managedRelationRole) {
			boolean enable = managedRelationRole.isAtLeastSelected(1);
			securityModule.setRelationRoleManaged(enable);
		}
		super.formInnerEvent(ureq, source, event);
	}
}
