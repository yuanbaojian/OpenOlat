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
package org.olat.modules.assessment.ui;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.util.Util;
import org.olat.course.assessment.AssessmentModule;
import org.olat.group.BusinessGroupService;
import org.olat.group.model.SearchBusinessGroupParams;
import org.olat.modules.assessment.ui.event.UserSelectionEvent;
import org.olat.repository.RepositoryEntry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 24.05.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessmentOverviewController extends BasicController implements Activateable2 {
		
	protected static final Event SELECT_USERS_EVENT = new Event("assessment-tool-select-users");
	protected static final Event SELECT_GROUPS_EVENT = new Event("assessment-tool-select-groups");
	protected static final Event SELECT_PASSED_EVENT = new Event("assessment-tool-select-passed");
	protected static final Event SELECT_FAILED_EVENT = new Event("assessment-tool-select-failed");
	
	private final VelocityContainer mainVC;
	private final AssessmentToReviewSmallController toReviewCtrl;
	private final AssessmentStatisticsSmallController statisticsCtrl;

	private Link assessedIdentitiesLink, assessedGroupsLink, passedLink, failedLink;

	@Autowired
	private BusinessGroupService businessGroupService;
		
	public AssessmentOverviewController(UserRequest ureq, WindowControl wControl,
			RepositoryEntry testEntry, AssessmentToolSecurityCallback assessmentCallback) {
		super(ureq, wControl, Util.createPackageTranslator(AssessmentModule.class, ureq.getLocale()));
		
		mainVC = createVelocityContainer("overview");
		
		toReviewCtrl = new AssessmentToReviewSmallController(ureq, getWindowControl(), testEntry, assessmentCallback);
		listenTo(toReviewCtrl);
		mainVC.put("toReview", toReviewCtrl.getInitialComponent());
		
		statisticsCtrl = new AssessmentStatisticsSmallController(ureq, getWindowControl(), testEntry, assessmentCallback);
		listenTo(statisticsCtrl);
		mainVC.put("statistics", statisticsCtrl.getInitialComponent());
		
		int numOfAssessedIdentities = statisticsCtrl.getNumOfAssessedIdentities();
		assessedIdentitiesLink = LinkFactory.createLink("assessed.identities", "assessed.identities", getTranslator(), mainVC, this, Link.NONTRANSLATED);
		assessedIdentitiesLink.setCustomDisplayText(translate("assessment.tool.numOfAssessedIdentities", new String[]{ Integer.toString(numOfAssessedIdentities) }));
		assessedIdentitiesLink.setIconLeftCSS("o_icon o_icon_user o_icon-fw");
		
		int numOfPassed = statisticsCtrl.getNumOfPassed();
		passedLink = LinkFactory.createLink("passed.identities", "passed.identities", getTranslator(), mainVC, this, Link.NONTRANSLATED);
		passedLink.setCustomDisplayText(translate("assessment.tool.numOfPassed", new String[]{ Integer.toString(numOfPassed) }));
		passedLink.setIconLeftCSS("o_passed o_icon o_icon_passed o_icon-fw");

		int numOfFailed = statisticsCtrl.getNumOfFailed();
		failedLink = LinkFactory.createLink("failed.identities", "failed.identities", getTranslator(), mainVC, this, Link.NONTRANSLATED);
		failedLink.setCustomDisplayText(translate("assessment.tool.numOfFailed", new String[]{ Integer.toString(numOfFailed) }));
		failedLink.setIconLeftCSS("o_failed o_icon o_icon_failed o_icon-fw");
		
		int numOfGroups = 0;
		if(assessmentCallback.canAssessBusinessGoupMembers()) {
			SearchBusinessGroupParams params = new SearchBusinessGroupParams();
			if(assessmentCallback.isAdmin()) {
				//all groups
			} else {
				params.setOwner(true);
				params.setIdentity(getIdentity());
			}
			numOfGroups = businessGroupService.countBusinessGroups(params, testEntry);
		}
		
		if(numOfGroups > 0) {
			assessedGroupsLink = LinkFactory.createLink("assessed.groups", "assessed.groups", getTranslator(), mainVC, this, Link.NONTRANSLATED);
			assessedGroupsLink.setCustomDisplayText(translate("assessment.tool.numOfAssessedGroups", new String[]{ Integer.toString(numOfGroups) }));
			assessedGroupsLink.setIconLeftCSS("o_icon o_icon_group o_icon-fw");
		}

		putInitialPanel(mainVC);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		//
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if(toReviewCtrl == source) {
			if(event instanceof UserSelectionEvent) {
				fireEvent(ureq, event);
			}
		}
		super.event(ureq, source, event);
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if(assessedIdentitiesLink == source) {
			fireEvent(ureq, SELECT_USERS_EVENT);
		} else if(assessedGroupsLink == source) {
			fireEvent(ureq, SELECT_GROUPS_EVENT);
		} else if(passedLink == source) {
			fireEvent(ureq, SELECT_PASSED_EVENT);
		} else if(failedLink == source) {
			fireEvent(ureq, SELECT_FAILED_EVENT);
		}
	}
}
