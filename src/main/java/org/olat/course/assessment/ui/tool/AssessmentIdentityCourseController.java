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

import java.util.List;

import org.olat.basesecurity.BaseSecurity;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.components.stack.TooledStackedPanel.Align;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.closablewrapper.CloseableCalloutWindowController;
import org.olat.core.id.Identity;
import org.olat.core.id.IdentityEnvironment;
import org.olat.core.id.Roles;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.course.CourseFactory;
import org.olat.course.ICourse;
import org.olat.course.assessment.ui.tool.event.CourseNodeEvent;
import org.olat.course.config.CourseConfig;
import org.olat.course.nodes.AssessableCourseNode;
import org.olat.course.nodes.CourseNode;
import org.olat.course.nodes.CourseNodeFactory;
import org.olat.course.nodes.STCourseNode;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.course.run.userview.UserCourseEnvironmentImpl;
import org.olat.modules.assessment.ui.AssessedIdentityController;
import org.olat.modules.assessment.ui.event.AssessmentFormEvent;
import org.olat.repository.RepositoryEntry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 09.10.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessmentIdentityCourseController extends BasicController implements AssessedIdentityController {
	

	private final TooledStackedPanel stackPanel;
	private final VelocityContainer identityAssessmentVC;
	private Link nextLink, previousLink, courseNodeSelectionLink;
	
	private IdentityCertificatesController certificateCtrl;
	private AssessedIdentityLargeInfosController infosController;
	private IdentityAssessmentOverviewController treeOverviewCtrl;
	private AssessmentIdentityCourseNodeController currentNodeCtrl;
	private CourseNodeSelectionController courseNodeChooserCtrl;
	private CloseableCalloutWindowController courseNodeChooserCalloutCtrl;
	
	private CourseNode currentCourseNode;
	private final Identity assessedIdentity;
	private final RepositoryEntry courseEntry;
	private final UserCourseEnvironment coachCourseEnv;
	
	@Autowired
	private BaseSecurity securityManager;
	
	public AssessmentIdentityCourseController(UserRequest ureq, WindowControl wControl, TooledStackedPanel stackPanel,
			RepositoryEntry courseEntry, UserCourseEnvironment coachCourseEnv, Identity assessedIdentity) {
		super(ureq, wControl);
		
		this.stackPanel = stackPanel;
		this.courseEntry = courseEntry;
		this.coachCourseEnv = coachCourseEnv;
		this.assessedIdentity = assessedIdentity;
		
		identityAssessmentVC = createVelocityContainer("identity_personal_infos");
		identityAssessmentVC.contextPut("user", assessedIdentity.getUser());
		
		infosController = new AssessedIdentityLargeInfosController(ureq, wControl, assessedIdentity);
		listenTo(infosController);
		identityAssessmentVC.put("identityInfos", infosController.getInitialComponent());
		
		ICourse course = CourseFactory.loadCourse(courseEntry);
		CourseConfig courseConfig = course.getCourseConfig();
		Roles roles = securityManager.getRoles(assessedIdentity);
		IdentityEnvironment identityEnv = new IdentityEnvironment(assessedIdentity, roles);
		UserCourseEnvironment assessedUserCourseEnv = new UserCourseEnvironmentImpl(identityEnv, course.getCourseEnvironment(), coachCourseEnv.isCourseReadOnly());

		if(courseConfig.isAutomaticCertificationEnabled() || courseConfig.isManualCertificationEnabled()) {
			certificateCtrl = new IdentityCertificatesController(ureq, wControl, coachCourseEnv, courseEntry, assessedIdentity);
			identityAssessmentVC.put("certificateInfos", certificateCtrl.getInitialComponent());
			listenTo(certificateCtrl);
		}

		treeOverviewCtrl = new IdentityAssessmentOverviewController(ureq, getWindowControl(), assessedUserCourseEnv, true, false, true);
		listenTo(treeOverviewCtrl);
		identityAssessmentVC.put("courseOverview", treeOverviewCtrl.getInitialComponent());

		putInitialPanel(identityAssessmentVC);
	}

	@Override
	public Identity getAssessedIdentity() {
		return assessedIdentity;
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		if(entries != null || state != null) {
			treeOverviewCtrl.activate(ureq, entries, state);
		}
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if(treeOverviewCtrl == source) {
			if(IdentityAssessmentOverviewController.EVENT_NODE_SELECTED.equals(event)) {
				doSelectCourseNode(ureq, treeOverviewCtrl.getSelectedCourseNode());
			}
		} else if(courseNodeChooserCtrl == source) {
			if(CourseNodeEvent.SELECT_COURSE_NODE.equals(event.getCommand())
					&& event instanceof CourseNodeEvent) {
				CourseNodeEvent cne = (CourseNodeEvent)event;
				CourseNode selectedNode = treeOverviewCtrl.getNodeByIdent(cne.getIdent());
				if(selectedNode != null) {
					doSelectCourseNode(ureq, selectedNode);
				}
			}
			courseNodeChooserCalloutCtrl.deactivate();
			cleanUp();
		} else if(courseNodeChooserCalloutCtrl == source) {
			cleanUp();
		} else if(currentNodeCtrl == source) {
			if(event instanceof AssessmentFormEvent) {
				AssessmentFormEvent aee = (AssessmentFormEvent)event;
				treeOverviewCtrl.loadModel();
				if(aee.isClose()) {
					stackPanel.popController(currentNodeCtrl);
				}
				fireEvent(ureq, aee.cloneNotClose());
			} else if(event == Event.CANCELLED_EVENT) {
				stackPanel.popController(currentNodeCtrl);
			} else if(event == Event.CHANGED_EVENT) {
				treeOverviewCtrl.loadModel();
				fireEvent(ureq, event);
			}
		}
		super.event(ureq, source, event);
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if(previousLink == source) {
			doPreviousNode(ureq);
		} else if(nextLink == source) {
			doNextNode(ureq);
		} else if(courseNodeSelectionLink == source) {
			doSelectCourseNode(ureq);
		}
	}
	
	private void cleanUp() {
		removeAsListenerAndDispose(courseNodeChooserCtrl);
		removeAsListenerAndDispose(courseNodeChooserCalloutCtrl);
		courseNodeChooserCalloutCtrl = null;
		courseNodeChooserCtrl = null;
	}
	
	private void doSelectCourseNode(UserRequest ureq) {
		removeAsListenerAndDispose(courseNodeChooserCtrl);
		removeAsListenerAndDispose(courseNodeChooserCalloutCtrl);
		
		courseNodeChooserCtrl = new CourseNodeSelectionController(ureq, getWindowControl(), courseEntry);
		listenTo(courseNodeChooserCtrl);
		if(currentCourseNode != null) {
			courseNodeChooserCtrl.selectedCourseNode(currentCourseNode);
		} else {
			courseNodeChooserCtrl.selectedCourseNode(treeOverviewCtrl.getSelectedCourseNode());
		}
		
		courseNodeChooserCalloutCtrl = new CloseableCalloutWindowController(ureq, getWindowControl(),
				courseNodeChooserCtrl.getInitialComponent(), courseNodeSelectionLink, "", true, "");
		listenTo(courseNodeChooserCalloutCtrl);
		courseNodeChooserCalloutCtrl.activate();
	}
	
	private void doNextNode(UserRequest ureq) {
		stackPanel.popController(currentNodeCtrl);
		
		CourseNode nextNode = treeOverviewCtrl.getNextNode(currentNodeCtrl.getCourseNode());
		if(nextNode != null && nextNode.getParent() != null) {
			if(nextNode instanceof STCourseNode) {
				int count = 0;
				for(nextNode=treeOverviewCtrl.getNextNode(nextNode); nextNode instanceof STCourseNode; nextNode=treeOverviewCtrl.getNextNode(nextNode)) {
					//search the next node which is not a structure node
					if(count++ > 500) {
						break;
					}
				}
			}
			
			if(nextNode.getParent() != null && !(nextNode instanceof STCourseNode)) {
				doSelectCourseNode(ureq, nextNode);
			}
		}
	}
	
	private void doPreviousNode(UserRequest ureq) {
		stackPanel.popController(currentNodeCtrl);
		
		CourseNode previousNode = treeOverviewCtrl.getPreviousNode(currentNodeCtrl.getCourseNode());
		if(previousNode != null && previousNode.getParent() != null) {
			if(previousNode instanceof STCourseNode) {
				CourseNode node = previousNode;
				for(previousNode=treeOverviewCtrl.getPreviousNode(previousNode); previousNode instanceof STCourseNode && node != previousNode; previousNode=treeOverviewCtrl.getPreviousNode(previousNode)) {
					//search the previous node which is not a structure node
					node = previousNode;
				}
			}
			
			if(!(previousNode instanceof STCourseNode)) {
				doSelectCourseNode(ureq, previousNode);
			}
		}
	}
	
	private void doSelectCourseNode(UserRequest ureq, CourseNode courseNode) {
		if(courseNode == null || courseNode.getParent() == null) {
			return;
		}
		currentCourseNode = courseNode;
		stackPanel.popUpToController(this);
		if(treeOverviewCtrl.isRoot(courseNode)) {
			return;
		}
		
		removeAsListenerAndDispose(currentNodeCtrl);

		currentNodeCtrl = new AssessmentIdentityCourseNodeController(ureq, getWindowControl(), stackPanel,
				courseEntry, courseNode, coachCourseEnv, assessedIdentity, true);

		listenTo(currentNodeCtrl);
		stackPanel.pushController(courseNode.getShortTitle(), currentNodeCtrl);
		
		previousLink = LinkFactory.createToolLink("previouselement", translate("previous"), this, "o_icon_previous");
		previousLink.setTitle(translate("command.previous"));
		previousLink.setEnabled(hasPrevious(courseNode));
		stackPanel.addTool(previousLink, Align.rightEdge, false);

		courseNodeSelectionLink =  LinkFactory.createToolLink("node.select", "node.select", courseNode.getShortTitle(), this);
		String courseNodeCssClass = CourseNodeFactory.getInstance()
				.getCourseNodeConfigurationEvenForDisabledBB(courseNode.getType()).getIconCSSClass();
		courseNodeSelectionLink.setElementCssClass("dropdown-toggle ");
		courseNodeSelectionLink.setIconLeftCSS("o_icon " + courseNodeCssClass);
		courseNodeSelectionLink.setIconRightCSS("o_icon o_icon_caret");
		stackPanel.addTool(courseNodeSelectionLink, Align.rightEdge, false, "o_tool_dropdown dropdown");
		
		nextLink = LinkFactory.createToolLink("nextelement", translate("next"), this, "o_icon_next");
		nextLink.setTitle(translate("command.next"));
		CourseNode nextNode = treeOverviewCtrl.getNextNode(courseNode);
		boolean hasNext = (nextNode != null && nextNode.getParent() != null);
		nextLink.setEnabled(hasNext);
		stackPanel.addTool(nextLink, Align.rightEdge, false);
	}
	
	private boolean hasPrevious(CourseNode courseNode) {
		int index = treeOverviewCtrl.getIndexOf(courseNode);
		int numOfNodes = treeOverviewCtrl.getNumberOfNodes();
		if(index > 0 && index <= numOfNodes) {
			for(int i=index; i-->0; ) {
				CourseNode previousNode = treeOverviewCtrl.getNode(i);
				if(previousNode instanceof AssessableCourseNode && !(previousNode instanceof STCourseNode)) {
					return true;
				}
			}
		}
		return false;
	}
}