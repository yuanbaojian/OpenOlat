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
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.control.WindowControl;
import org.olat.group.model.BusinessGroupQueryParams;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class SelectSearchBusinessGroupController extends AbstractSelectBusinessGroupListController {
	
	private final boolean restricted;
	
	public SelectSearchBusinessGroupController(UserRequest ureq, WindowControl wControl, boolean restricted) {
		super(ureq, wControl, "group_list", "sel-search");
		this.restricted = restricted;
	}

	@Override
	protected boolean canCreateBusinessGroup(UserRequest ureq) {
		return false;
	}

	@Override
	protected void initButtons(FormItemContainer formLayout, UserRequest ureq) {
		initButtons(formLayout, ureq, false, true, false);
	}

	@Override
	protected BusinessGroupQueryParams getSearchParams(SearchEvent event) {
		BusinessGroupQueryParams params = event.convertToBusinessGroupQueriesParams();
		//security
		if(restricted && !params.isAttendee() && !params.isOwner() && !params.isWaiting()
				&& (params.getPublicGroups() == null || !params.getPublicGroups().booleanValue())) {
			params.setOwner(true);
			params.setAttendee(true);
			params.setWaiting(true);
		}
		return params;
	}

	@Override
	protected BusinessGroupQueryParams getDefaultSearchParams() {
		BusinessGroupQueryParams params = new BusinessGroupQueryParams();
		//security
		if(restricted) {
			params.setOwner(true);
			params.setAttendee(true);
			params.setWaiting(true);
		}
		return params;
	}

	protected void updateSearch(UserRequest ureq) {
		doSearch(ureq, null);
	}
}
