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

import java.util.Collections;
import java.util.List;

import org.olat.basesecurity.GroupRoles;
import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.ExtendedFlexiTableSearchController;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.util.StringHelper;
import org.olat.group.BusinessGroupModule;

/**
 * 
 * Description:<br>
 * Search form for Business groups
 * 
 * <P>
 * Initial Date:  21 avr. 2011 <br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class BusinessGroupSearchController extends FormBasicController implements Activateable2, ExtendedFlexiTableSearchController {

	private TextElement id;
	private TextElement displayName;
	private TextElement owner;
	private TextElement description;
	private TextElement courseTitle;
	private FormLink searchButton;
	private SingleSelection rolesEl;
	private SingleSelection publicEl;
	private SingleSelection resourceEl;
	private MultipleSelectionElement headlessEl;
	
	private final boolean showRoles;
	private final boolean showAdminTools;
	private String limitUsername;
	
	private String[] roleKeys = {"all", "owner", "attendee", "waiting"};
	private String[] adminRoleKeys = {"none", "all", "owner", "attendee", "waiting"};
	private String[] openKeys = {"all", "yes", "no"};
	private String[] resourceKeys = {"all", "yes", "no"};
	
	private final boolean admin;
	private final boolean showId;
	private final boolean managedEnable;
	private boolean enabled = false;

	/**
	 * Generic search form.
	 * @param name Internal form name.
	 * @param translator Translator
	 * @param withCancel Display a cancel button?
	 * @param isAdmin Is calling identity an administrator? If yes, allow search by ID
	 * @param limitTypes Limit searches to specific types.
	 */
	public BusinessGroupSearchController(UserRequest ureq, WindowControl wControl,
			boolean showId, boolean showRoles, boolean showAdminTools, boolean admin, Form mainForm) {
		super(ureq, wControl, LAYOUT_CUSTOM, "group_search", mainForm);
		this.admin = admin;
		this.showId = showId;
		this.showRoles = showRoles;
		this.showAdminTools = showAdminTools;
		managedEnable = CoreSpringFactory.getImpl(BusinessGroupModule.class).isManagedBusinessGroups();
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		FormLayoutContainer leftContainer = FormLayoutContainer.createDefaultFormLayout("left_1", getTranslator());
		leftContainer.setRootForm(mainForm);
		formLayout.add(leftContainer);
		
		id = uifactory.addTextElement("cif_id", "cif.id", 12, "", leftContainer);
		id.setElementCssClass("o_sel_group_search_id_field");
		id.setDisplaySize(28);
		id.setVisible(showId || managedEnable);

		displayName = uifactory.addTextElement("cif_displayname", "cif.displayname", 255, "", leftContainer);
		displayName.setElementCssClass("o_sel_group_search_name_field");
		displayName.setFocus(true);
		displayName.setDisplaySize(28);
		
		owner = uifactory.addTextElement("cif_owner", "cif.owner", 255, "", leftContainer);
		owner.setElementCssClass("o_sel_group_search_owner_field");
		if (limitUsername != null) {
			owner.setValue(limitUsername);
			owner.setEnabled(false);
		}
		owner.setDisplaySize(28);
		description = uifactory.addTextElement("cif_description", "cif.description", 255, "", leftContainer);
		description.setElementCssClass("o_sel_group_search_description_field");
		description.setDisplaySize(28);
		
		courseTitle = uifactory.addTextElement("cif_coursetitle", "cif.coursetitle", 255, "", leftContainer);
		courseTitle.setElementCssClass("o_sel_group_search_course_field");
		courseTitle.setDisplaySize(28);

		FormLayoutContainer rightContainer = FormLayoutContainer.createDefaultFormLayout("right_1", getTranslator());
		rightContainer.setRootForm(mainForm);
		formLayout.add(rightContainer);
		
		String[] rKeys = showAdminTools ? this.adminRoleKeys : this.roleKeys;
		String[] rValues = new String[rKeys.length];
		for(int i=rKeys.length; i-->0; ) {
			rValues[i] = translate("search." + rKeys[i]);
		}
		rolesEl = uifactory.addRadiosHorizontal("roles", "search.roles", rightContainer, rKeys, rValues);
		rolesEl.setElementCssClass("o_sel_group_search_roles_field");
		if(showAdminTools) {
			rolesEl.select("none", true);
		} else {
			rolesEl.select("all", true);
		}
		
		rolesEl.setVisible(showRoles);

		//public
		String[] openValues = new String[openKeys.length];
		for(int i=openKeys.length; i-->0; ) {
			openValues[i] = translate("search." + openKeys[i]);
		}
		publicEl = uifactory.addRadiosHorizontal("openBg", "search.open", rightContainer, openKeys, openValues);
		publicEl.setElementCssClass("o_sel_group_search_public_field");
		publicEl.select("all", true);

		//resources
		String[] resourceValues = new String[resourceKeys.length];
		for(int i=resourceKeys.length; i-->0; ) {
			resourceValues[i] = translate("search." + resourceKeys[i]);
		}
		resourceEl = uifactory.addRadiosHorizontal("resourceBg", "search.resources", rightContainer, resourceKeys, resourceValues);
		resourceEl.setElementCssClass("o_sel_group_search_resource_field");
		resourceEl.select("all", true);
		
		String[] keys = new String[] { "headless" };
		String[] values = new String[] { translate("search.headless.check") };
		headlessEl = uifactory.addCheckboxesHorizontal("headless.groups", "search.headless", rightContainer, keys, values);
		headlessEl.setElementCssClass("o_sel_group_search_headless_field");
		headlessEl.setVisible(showAdminTools);

		FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("button_layout", getTranslator());
		formLayout.add(buttonLayout);
		buttonLayout.setElementCssClass("o_sel_group_search_groups_buttons");
		searchButton = uifactory.addFormLink("search", buttonLayout, Link.BUTTON);
		searchButton.setElementCssClass("o_sel_group_search_button");
		searchButton.setCustomEnabledLinkCSS("btn btn-primary");
		
		uifactory.addFormCancelButton("cancel", buttonLayout, ureq, getWindowControl());
	}

	@Override
	protected void doDispose() {
		//
	}
	
	@Override
	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	public void enableId(boolean enable) {
		id.setVisible(enable);
	}
	
	public void enableRoles(boolean enable) {
		rolesEl.setVisible(enable);
	}
	
	public void enableHeadless(boolean enable) {
		headlessEl.setVisible(enable);
	}
	
	public void enablePublic(boolean enable) {
		publicEl.setVisible(enable);
	}
	
	public void setPreselectedRoles(GroupRoles role) {
		if(rolesEl != null) {
			if(role == GroupRoles.coach) {
				rolesEl.select("owner", true);
			} else if(role == GroupRoles.participant) {
				rolesEl.select("attendee", true);
			} else if(role == GroupRoles.waiting) {
				rolesEl.select("waiting", true);
			}
		}
	}
	
	/**
	 * @return True if the text search fields are empty
	 */
	public boolean isEmpty() {
		return displayName.isEmpty() && owner.isEmpty() && description.isEmpty()
				&& id.isEmpty() && courseTitle.isEmpty();
	}
	
	@Override
	public List<String> getConditionalQueries() {
		return Collections.emptyList();
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		if(!enabled) return true;
		
		return super.validateFormLogic(ureq);
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		if(state instanceof SearchEvent) {
			setSearchEvent((SearchEvent)state);
		}
	}

	@Override
	protected void formOK(UserRequest ureq) {
		if(enabled) {
			fireSearchEvent(ureq);
		}
	}
	
	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}

	@Override
	protected void formInnerEvent (UserRequest ureq, FormItem source, FormEvent event) {
		if (source == searchButton) {
			fireSearchEvent(ureq);
		}
	}
	
	private void setSearchEvent(SearchEvent e) {
		if(e.getIdRef() != null && id != null) {
			id.setValue(e.getIdRef());
		}
		if(StringHelper.containsNonWhitespace(e.getName())) {
			displayName.setValue(e.getName());
		}
		if(StringHelper.containsNonWhitespace(e.getDescription())) {
			description.setValue(e.getDescription());
		}
		if(StringHelper.containsNonWhitespace(e.getOwnerName())) {
			owner.setValue(e.getOwnerName());
		}
		if(StringHelper.containsNonWhitespace(e.getCourseTitle())) {
			courseTitle.setValue(e.getCourseTitle());
		}
		if(e.isHeadless() && headlessEl != null) {
			headlessEl.select("headless", true);
		}
		if(rolesEl != null) {
			if(e.isOwner() && e.isAttendee() && e.isWaiting()) {
				rolesEl.select("all", true);
			} else if(e.isOwner()) {
				rolesEl.select("owner", true);
			} else if(e.isAttendee()) {
				rolesEl.select("attendee", true);
			} else if(e.isWaiting()) {
				rolesEl.select("waiting", true);
			}
		}
		if(e.getPublicGroups() != null && publicEl != null) {
			if(e.getPublicGroups().booleanValue()) {
				publicEl.select("yes", true);
			} else {
				publicEl.select("no", true);
			}
		}
		
		if(e.getResources() != null && resourceEl != null) {
			if(e.getResources().booleanValue()) {
				resourceEl.select("yes", true);
			} else {
				resourceEl.select("no", true);
			}
		}
	}

	private void fireSearchEvent(UserRequest ureq) {
		SearchEvent e = new SearchEvent();
		e.setName(displayName.getValue());
		if(id.isVisible()) {
			e.setIdRef(id.getValue());
		}
		e.setDescription(description.getValue());
		e.setOwnerName(owner.getValue());
		e.setCourseTitle(courseTitle.getValue());
		
		if(headlessEl.isVisible() && headlessEl.isAtLeastSelected(1)) {
			e.setHeadless(true);
		} else {
			e.setHeadless(false);
		}
		
		if(rolesEl.isVisible() && rolesEl.isOneSelected()) {
			String selectedRole = rolesEl.getSelectedKey();
			if(admin && "all".equals(selectedRole)) {
				//don't limit the query
			} else {
				e.setOwner("all".equals(selectedRole) || "owner".equals(selectedRole));
				e.setAttendee("all".equals(selectedRole) || "attendee".equals(selectedRole));
				e.setWaiting("all".equals(selectedRole) || "waiting".equals(selectedRole));
			}
		}
		
		if(publicEl.isVisible() && publicEl.isOneSelected()) {
			if(publicEl.isSelected(1)) {
				e.setPublicGroups(Boolean.TRUE);
			} else if(publicEl.isSelected(2)) {
				e.setPublicGroups(Boolean.FALSE);
			}
		}
		
		if(resourceEl.isOneSelected()) {
			if(resourceEl.isSelected(1)) {
				e.setResources(Boolean.TRUE);
			} else if(resourceEl.isSelected(2)) {
				e.setResources(Boolean.FALSE);
			}
		}
		fireEvent(ureq, e);
	}
}