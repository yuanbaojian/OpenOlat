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
package org.olat.admin.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.BaseSecurityModule;
import org.olat.basesecurity.SearchIdentityParams;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.id.Organisation;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.id.context.StateMapped;
import org.olat.core.logging.activity.ThreadLocalUserActivityLogger;
import org.olat.core.util.resource.OresHelper;
import org.olat.user.UserManager;
import org.olat.user.ui.admin.UserSearchTableController;
import org.olat.util.logging.activity.LoggingResourceable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Initial Date: Jan 31, 2006
 * 
 * @author gnaegi
 * 
 * Description: This workflow has two constructors. The first one provides the
 * user an advanced user search form with many search criterias that can be
 * defined. The second one has the criterias in the constructor as attributes,
 * so the search form won't appear. The following is a list with the search
 * results. Form the list an identity can be selected which results in a
 * SingleIdentityChosenEvent Alternatively a Canceled Event is fired.
 * 
 */
public class UsermanagerUserSearchController extends BasicController implements Activateable2 {

	private TooledStackedPanel stackedPanel;

	private UsermanagerUserSearchForm searchFormCtrl;
	private UserSearchTableController tableCtr;

	private final boolean showEmailButton;
	private final boolean isAdministrativeUser;
	private List<Organisation> manageableOrganisations;
	private SearchIdentityParams identityQueryParams;
	
	@Autowired
	private UserManager userManager;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private BaseSecurityModule securityModule;

	/**
	 * Constructor to trigger the user search workflow using a generic search form
	 * 
	 * @param ureq
	 * @param wControl
	 */
	public UsermanagerUserSearchController(UserRequest ureq, WindowControl wControl, TooledStackedPanel stackedPanel,
			List<Organisation> manageableOrganisations) {
		super(ureq, wControl);
		setTranslator(userManager.getPropertyHandlerTranslator(getTranslator()));
		
		this.stackedPanel = stackedPanel;
		this.manageableOrganisations = manageableOrganisations;
		this.showEmailButton = true;
		
		isAdministrativeUser = securityModule.isUserAllowedAdminProps(ureq.getUserSession().getRoles());

		searchFormCtrl = new UsermanagerUserSearchForm(ureq, wControl, isAdministrativeUser, manageableOrganisations);
		listenTo(searchFormCtrl);
		
		VelocityContainer userSearchVC = createVelocityContainer("usermanagerUsersearch");
		userSearchVC.put("usersearch", searchFormCtrl.getInitialComponent());
		putInitialPanel(userSearchVC);
	}
	
	/**
	 * Constructor to trigger the user search workflow using the given attributes.
	 * The user has no possibility to manually search, the search will be
	 * performed using the constructor attributes.
	 * 
	 * @param ureq
	 * @param wControl
	 * @param searchGroups
	 * @param searchPermissionOnResources
	 * @param searchAuthProviders
	 * @param searchCreatedAfter
	 * @param searchCreatedBefore
	 */
	public UsermanagerUserSearchController(UserRequest ureq, WindowControl wControl, TooledStackedPanel stackedPanel,
			SearchIdentityParams predefinedQuery, boolean showEmailButton) {
		super(ureq, wControl);
		setTranslator(userManager.getPropertyHandlerTranslator(getTranslator()));
		this.stackedPanel = stackedPanel;
		this.showEmailButton = showEmailButton;

		identityQueryParams = predefinedQuery;
		isAdministrativeUser = securityModule.isUserAllowedAdminProps(ureq.getUserSession().getRoles());
		
		tableCtr = new UserSearchTableController(ureq, getWindowControl(), stackedPanel, showEmailButton, true);
		listenTo(tableCtr);
		tableCtr.loadModel(identityQueryParams);
		putInitialPanel(tableCtr.getInitialComponent());
	}
	
	/**
	 * Constructor to trigger the user search workflow using the predefined list of
	 * identities. The user has no possibility to manually search.
	 * 
	 * @param ureq
	 * @param wControl
	 * @param identitiesList
	 * @param status
	 * @param showEmailButton
	 */
	public UsermanagerUserSearchController(UserRequest ureq, WindowControl wControl, TooledStackedPanel stackedPanel,
			List<Identity> identitiesList, boolean showEmailButton, boolean showTitle) {
		super(ureq, wControl);
		setTranslator(userManager.getPropertyHandlerTranslator(getTranslator()));
		this.stackedPanel = stackedPanel;
		this.showEmailButton = showEmailButton;
		
		isAdministrativeUser = securityModule.isUserAllowedAdminProps(ureq.getUserSession().getRoles());

		tableCtr = new UserSearchTableController(ureq, getWindowControl(), stackedPanel, showEmailButton, true);
		listenTo(tableCtr);
		tableCtr.loadModel(identitiesList);
		
		if(showTitle) {
			VelocityContainer userSearchVC = createVelocityContainer("usermanagerUsersearch");
			userSearchVC.put("usersearch", tableCtr.getInitialComponent());
			putInitialPanel(userSearchVC);
		} else {
			putInitialPanel(tableCtr.getInitialComponent());
		}
	}
	
	public WindowControl getTableControl() {
		return tableCtr == null ? null : tableCtr.getWindowControlForDebug();
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		if(state instanceof StateMapped) {
			StateMapped searchState = (StateMapped)state;
			searchFormCtrl.setStateEntry(searchState);
			
			if(entries != null && !entries.isEmpty()) {
				String table = entries.get(0).getOLATResourceable().getResourceableTypeName();
				if("table".equalsIgnoreCase(table)) {
					entries.remove(0);
					event(ureq, searchFormCtrl, Event.DONE_EVENT);
				}
			}
		} else {
			if(entries != null && entries.size()> 1) {
				String table = entries.get(0).getOLATResourceable().getResourceableTypeName();
				String identity = entries.get(1).getOLATResourceable().getResourceableTypeName();
				if("table".equalsIgnoreCase(table) && "Identity".equalsIgnoreCase(identity)) {
					doActivateUser(ureq, entries.subList(1, entries.size()));
				}
			}
		}
	}

	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		//
	}
	
	private void doActivateUser(UserRequest ureq, List<ContextEntry> entries) {
		Long identityKey = entries.get(0).getOLATResourceable().getResourceableId();
		Identity searchedIdentity = securityManager.loadIdentityByKey(identityKey);
		
		StateMapped searchState = new StateMapped();
		Map<String,String> states = new HashMap<>();
		states.put("login", searchedIdentity.getName());
		searchState.setDelegate(states);
		searchFormCtrl.setStateEntry(searchState);
		
		doPushSearch(ureq);
		tableCtr.activate(ureq, entries, null);
	}
	
	private void doPushSearch(UserRequest ureq) {
		identityQueryParams = searchFormCtrl.getSearchIdentityParams();
		if(identityQueryParams.getOrganisations() == null || identityQueryParams.getOrganisations().isEmpty()) {
			identityQueryParams.setOrganisations(manageableOrganisations);
		}
		
		OLATResourceable ores = OresHelper.createOLATResourceableInstance("table", 0l);
		ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrapBusinessPath(ores));
		WindowControl bwControl = addToHistory(ureq, ores, null);
		tableCtr = new UserSearchTableController(ureq, bwControl, stackedPanel, showEmailButton, true);
		listenTo(tableCtr);
		tableCtr.loadModel(identityQueryParams);
		stackedPanel.pushController("Results", tableCtr);

		if(searchFormCtrl != null) {
			ContextEntry currentEntry = getWindowControl().getBusinessControl().getCurrentContextEntry();
			if(currentEntry != null) {
				currentEntry.setTransientState(searchFormCtrl.getStateEntry());
			}
		}
		addToHistory(ureq, tableCtr);
	}

	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == searchFormCtrl) {
			if (event == Event.DONE_EVENT) {
				doPushSearch(ureq);
			} else if (event == Event.CANCELLED_EVENT) {
				fireEvent(ureq, Event.CANCELLED_EVENT);
			}
		}
		super.event(ureq, source, event);
	}

	@Override
	protected void doDispose() {
		//
	}
}