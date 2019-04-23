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
package org.olat.course.assessment.ui.tool;

import java.util.Date;
import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.stack.ButtonGroupComponent;
import org.olat.core.gui.components.stack.PopEvent;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.components.stack.TooledStackedPanel.Align;
import org.olat.core.gui.components.tree.TreeNode;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.MainLayoutBasicController;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.OLATResourceable;
import org.olat.core.id.context.BusinessControlFactory;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.util.Util;
import org.olat.core.util.resource.OresHelper;
import org.olat.course.assessment.AssessmentModeManager;
import org.olat.course.assessment.AssessmentModule;
import org.olat.course.assessment.EfficiencyStatementAssessmentController;
import org.olat.course.assessment.bulk.BulkAssessmentOverviewController;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.modules.assessment.ui.AssessedIdentityListState;
import org.olat.modules.assessment.ui.AssessmentToolContainer;
import org.olat.modules.assessment.ui.AssessmentToolSecurityCallback;
import org.olat.modules.assessment.ui.event.UserSelectionEvent;
import org.olat.repository.RepositoryEntry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 21.07.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessmentToolController extends MainLayoutBasicController implements Activateable2 {


	private RepositoryEntry courseEntry;
	private final AssessmentToolSecurityCallback assessmentCallback;
	
	private Link overviewLink, usersLink, groupsLink;
	private Link efficiencyStatementsLink, bulkAssessmentLink;
	private final TooledStackedPanel stackPanel;
	private final AssessmentToolContainer toolContainer;
	private final ButtonGroupComponent segmentButtonsCmp;
	
	private AssessmentCourseTreeController courseTreeCtrl;
	private AssessmentCourseOverviewController overviewCtrl;
	private BulkAssessmentOverviewController bulkAssessmentOverviewCtrl;
	private EfficiencyStatementAssessmentController efficiencyStatementCtrl;
	
	private UserCourseEnvironment coachUserEnv;
	
	@Autowired
	private AssessmentModeManager assessmentModeManager;
	
	public AssessmentToolController(UserRequest ureq, WindowControl wControl, TooledStackedPanel stackPanel,
			RepositoryEntry courseEntry, UserCourseEnvironment coachUserEnv, AssessmentToolSecurityCallback assessmentCallback) {
		super(ureq, wControl);
		setTranslator(Util.createPackageTranslator(AssessmentModule.class, getLocale(), getTranslator()));
		this.courseEntry = courseEntry;
		this.stackPanel = stackPanel;
		this.coachUserEnv = coachUserEnv;
		this.assessmentCallback = assessmentCallback;
		
		toolContainer = new AssessmentToolContainer();

		stackPanel.addListener(this);
		segmentButtonsCmp = new ButtonGroupComponent("segments");
		
		overviewCtrl = new AssessmentCourseOverviewController(ureq, getWindowControl(), courseEntry, assessmentCallback);
		listenTo(overviewCtrl);
		putInitialPanel(overviewCtrl.getInitialComponent());
	}
	
	public void assessmentModeMessage() {
		if(assessmentModeManager.isInAssessmentMode(courseEntry, new Date())) {
			stackPanel.setMessage(translate("assessment.mode.now"));
			stackPanel.setMessageCssClass("o_warning");
		}
	}
	
	public void initToolbar() {
		overviewLink = LinkFactory.createToolLink("overview", translate("overview"), this/*, "o_icon_user"*/);
		overviewLink.setElementCssClass("o_sel_assessment_tool_overview");
		segmentButtonsCmp.addButton(overviewLink, true);
		
		usersLink = LinkFactory.createToolLink("users", translate("users"), this/*, "o_icon_user"*/);
		usersLink.setElementCssClass("o_sel_assessment_tool_users");
		segmentButtonsCmp.addButton(usersLink, false);
		
		if(overviewCtrl.getNumOfBusinessGroups() > 0) {
			groupsLink = LinkFactory.createToolLink("groups", translate("groups"), this/*, "o_icon_group"*/);
			groupsLink.setElementCssClass("o_sel_assessment_tool_groups");
			segmentButtonsCmp.addButton(groupsLink, false);
		}
		stackPanel.addTool(segmentButtonsCmp, Align.segment, true);
		
		efficiencyStatementsLink = LinkFactory.createToolLink("efficiencyStatements", translate("menu.efficiency.statment"), this, "o_icon_certificate");
		efficiencyStatementsLink.setElementCssClass("o_sel_assessment_tool_efficiency_statements");
		stackPanel.addTool(efficiencyStatementsLink, Align.right);
		
		bulkAssessmentLink = LinkFactory.createToolLink("bulkAssessment", translate("menu.bulkfocus"), this, "o_icon_group");
		bulkAssessmentLink.setElementCssClass("o_sel_assessment_tool_bulk");
		stackPanel.addTool(bulkAssessmentLink, Align.right);
	}

	@Override
	protected void doDispose() {
		//
	}
	
	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		if(entries == null || entries.size() == 0) return;
		
		String resName = entries.get(0).getOLATResourceable().getResourceableTypeName();
		if("Users".equalsIgnoreCase(resName)) {
			List<ContextEntry> subEntries = entries.subList(1, entries.size());
			doSelectUsersView(ureq, null).activate(ureq, subEntries, entries.get(0).getTransientState());
		} else if("BusinessGroups".equalsIgnoreCase(resName) || "Groups".equalsIgnoreCase(resName)) {
			List<ContextEntry> subEntries = entries.subList(1, entries.size());
			doSelectGroupsView(ureq).activate(ureq, subEntries, entries.get(0).getTransientState());
		}
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if (overviewLink == source) {
			cleanUp();
			stackPanel.popUpToController(this);
			addToHistory(ureq, getWindowControl());
		} else if (source == usersLink) {
			cleanUp();
			doSelectUsersView(ureq, null);
		} else if (groupsLink == source) {
			cleanUp();
			doSelectGroupsView(ureq);
		} else if(efficiencyStatementsLink == source) {
			cleanUp();
			doEfficiencyStatementView(ureq);
		} else if(bulkAssessmentLink == source) {
			cleanUp();
			doBulkAssessmentView(ureq);
		} else if(stackPanel == source) {
			if(event instanceof PopEvent) {
				PopEvent pe = (PopEvent)event;
				if(pe.isClose()) {
					stackPanel.popUpToRootController(ureq);
				} else if(pe.getController() == courseTreeCtrl) {
					removeAsListenerAndDispose(courseTreeCtrl);
					courseTreeCtrl = null;
					segmentButtonsCmp.setSelectedButton(overviewLink);
				}
			}
		}
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if(overviewCtrl == source) {
			if(event == AssessmentCourseOverviewController.SELECT_USERS_EVENT) {
				doSelectUsersView(ureq, null);
			} else if(event == AssessmentCourseOverviewController.SELECT_NODES_EVENT) {
				doSelectUsersView(ureq, null);
			} else if(event == AssessmentCourseOverviewController.SELECT_GROUPS_EVENT) {
				doSelectGroupsView(ureq);
			} else if(event == AssessmentCourseOverviewController.SELECT_PASSED_EVENT) {
				doSelectUsersView(ureq, new AssessedIdentityListState("passed"));
			} else if(event == AssessmentCourseOverviewController.SELECT_FAILED_EVENT) {
				doSelectUsersView(ureq, new AssessedIdentityListState("failed"));
			} else if (event instanceof UserSelectionEvent) {
				UserSelectionEvent use = (UserSelectionEvent)event;
				if(use.getCourseNodeIdents() == null || use.getCourseNodeIdents().isEmpty() || use.getCourseNodeIdents().size() > 1) {
					OLATResourceable resource = OresHelper.createOLATResourceableInstance("Identity", use.getIdentityKey());
					List<ContextEntry> entries = BusinessControlFactory.getInstance()
							.createCEListFromResourceable(resource, new AssessedIdentityListState("inReview"));
					doSelectUsersView(ureq, null).activate(ureq, entries, null);
				} else {
					OLATResourceable nodeRes = OresHelper.createOLATResourceableInstance("Node", new Long(use.getCourseNodeIdents().get(0)));
					OLATResourceable idRes = OresHelper.createOLATResourceableInstance("Identity", use.getIdentityKey());
					List<ContextEntry> entries = BusinessControlFactory.getInstance().createCEListFromString(nodeRes, idRes);
					doSelectUsersView(ureq, null).activate(ureq, entries, null);
				}
			}
		}
		super.event(ureq, source, event);
	}
	
	private void cleanUp() {
		removeAsListenerAndDispose(bulkAssessmentOverviewCtrl);
		bulkAssessmentOverviewCtrl = null;
	}
	
	private void doBulkAssessmentView(UserRequest ureq) {
		bulkAssessmentOverviewCtrl = new BulkAssessmentOverviewController(ureq, getWindowControl(), courseEntry);
		listenTo(bulkAssessmentOverviewCtrl);
		stackPanel.pushController(translate("menu.bulkfocus"), bulkAssessmentOverviewCtrl);
	}
	
	private void doEfficiencyStatementView(UserRequest ureq) {
		efficiencyStatementCtrl = new EfficiencyStatementAssessmentController(ureq, getWindowControl(), courseEntry);
		listenTo(efficiencyStatementCtrl);
		stackPanel.pushController(translate("menu.efficiency.statment"), efficiencyStatementCtrl);
	}
	

	private AssessmentCourseTreeController doSelectGroupsView(UserRequest ureq) {
		if(courseTreeCtrl == null || courseTreeCtrl.isDisposed()) {
			stackPanel.popUpToController(this);
			
			courseTreeCtrl = new AssessmentCourseTreeController(ureq, getWindowControl(), stackPanel, courseEntry, coachUserEnv, toolContainer, assessmentCallback);
			listenTo(courseTreeCtrl);
			TreeNode node = courseTreeCtrl.getSelectedCourseNode();
			stackPanel.pushController(node.getTitle(), "o_icon " + node.getIconCssClass(), courseTreeCtrl);
		}
		courseTreeCtrl.switchToBusinessGroupsView(ureq);
		segmentButtonsCmp.setSelectedButton(groupsLink);
		return courseTreeCtrl;
	}

	private AssessmentCourseTreeController doSelectUsersView(UserRequest ureq, StateEntry stateUserList) {
		if(courseTreeCtrl == null || courseTreeCtrl.isDisposed()) {
			stackPanel.popUpToController(this);
			
			courseTreeCtrl = new AssessmentCourseTreeController(ureq, getWindowControl(), stackPanel, courseEntry, coachUserEnv, toolContainer, assessmentCallback);
			listenTo(courseTreeCtrl);
			TreeNode node = courseTreeCtrl.getSelectedCourseNode();
			stackPanel.pushController(node.getTitle(), "o_icon " + node.getIconCssClass(), courseTreeCtrl);
		}
		courseTreeCtrl.switchToUsersView(ureq, stateUserList);
		segmentButtonsCmp.setSelectedButton(usersLink);
		return courseTreeCtrl;
	}
}
