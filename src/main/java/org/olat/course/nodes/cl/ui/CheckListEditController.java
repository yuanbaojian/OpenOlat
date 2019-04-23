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
package org.olat.course.nodes.cl.ui;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.tabbedpane.TabbedPane;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.ControllerEventListener;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.tabbable.ActivateableTabbableDefaultController;
import org.olat.course.ICourse;
import org.olat.course.assessment.AssessmentHelper;
import org.olat.course.condition.Condition;
import org.olat.course.condition.ConditionEditController;
import org.olat.course.editor.NodeEditController;
import org.olat.course.highscore.ui.HighScoreEditController;
import org.olat.course.nodes.CheckListCourseNode;
import org.olat.course.nodes.MSCourseNode;
import org.olat.course.nodes.cl.CheckboxManager;
import org.olat.course.run.userview.UserCourseEnvironment;

/**
 * 
 * Initial date: 04.02.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class CheckListEditController extends ActivateableTabbableDefaultController implements ControllerEventListener {

	public static final String PANE_TAB_CLCONFIG = "pane.tab.clconfig";
	private static final String PANE_TAB_ACCESSIBILITY = "pane.tab.accessibility";
	private static final String PANE_TAB_CHECKBOX = "pane.tab.checkbox";
	public static final String PANE_TAB_HIGHSCORE = "pane.tab.highscore";

	
	private ConditionEditController accessibilityCondCtrl;
	private CheckListBoxListEditController checkboxListEditCtrl;
	private CheckListConfigurationController configurationCtrl;
	private HighScoreEditController highScoreNodeConfigController;
	private CheckListCourseNode courseNode;

	private TabbedPane myTabbedPane;

	private static final String[] paneKeys = { PANE_TAB_CLCONFIG, PANE_TAB_CHECKBOX };

	/**
	 * @param cpNode
	 * @param ureq
	 * @param wControl
	 * @param course
	 */
	public CheckListEditController(CheckListCourseNode courseNode, UserRequest ureq, WindowControl wControl,
			ICourse course, UserCourseEnvironment euce) {
		super(ureq, wControl);
		this.courseNode = courseNode;
		
		// Accessibility precondition
		Condition accessCondition = courseNode.getPreConditionAccess();
		accessibilityCondCtrl = new ConditionEditController(ureq, getWindowControl(), euce,
				accessCondition, AssessmentHelper.getAssessableNodes(course.getEditorTreeModel(), courseNode));		
		listenTo(accessibilityCondCtrl);
		

		CheckboxManager checkboxManager = CoreSpringFactory.getImpl(CheckboxManager.class);
		int numOfChecks = checkboxManager.countChecks(course, courseNode.getIdent());
		
		checkboxListEditCtrl = new CheckListBoxListEditController(ureq, wControl, course, courseNode, numOfChecks > 0);
		listenTo(checkboxListEditCtrl);
		configurationCtrl = new CheckListConfigurationController(ureq, wControl, courseNode, numOfChecks > 0);
		listenTo(configurationCtrl);
		
		highScoreNodeConfigController = new HighScoreEditController(ureq, wControl, courseNode.getModuleConfiguration());
		listenTo(highScoreNodeConfigController);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	public String[] getPaneKeys() {
		return paneKeys;
	}

	@Override
	public TabbedPane getTabbedPane() {
		return myTabbedPane;
	}
	
	private void updateHighscoreTab() {
		Boolean sf = courseNode.getModuleConfiguration().getBooleanSafe(MSCourseNode.CONFIG_KEY_HAS_SCORE_FIELD,false);
		myTabbedPane.setEnabled(5, sf);
	}
	
	@Override
	public void addTabs(TabbedPane tabbedPane) {
		myTabbedPane = tabbedPane;

		tabbedPane.addTab(translate(PANE_TAB_ACCESSIBILITY),
				accessibilityCondCtrl.getWrappedDefaultAccessConditionVC(translate("condition.accessibility.title")));
		tabbedPane.addTab(translate(PANE_TAB_CLCONFIG), configurationCtrl.getInitialComponent());
		tabbedPane.addTab(translate(PANE_TAB_CHECKBOX), checkboxListEditCtrl.getInitialComponent());
		tabbedPane.addTab(translate(PANE_TAB_HIGHSCORE) , highScoreNodeConfigController.getInitialComponent());
		updateHighscoreTab();
	}
	
	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		//
	}

	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == accessibilityCondCtrl) {
			if (event == Event.DONE_EVENT || event == Event.CHANGED_EVENT) {
				Condition cond = accessibilityCondCtrl.getCondition();
				courseNode.setPreConditionAccess(cond);
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		} else if(source == configurationCtrl) {
			if (event == Event.DONE_EVENT || event == Event.CHANGED_EVENT) {
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
				checkboxListEditCtrl.dispatchEvent(ureq, configurationCtrl, event);
				updateHighscoreTab();
			}
		} else if(source == checkboxListEditCtrl) {
			if (event == Event.DONE_EVENT || event == Event.CHANGED_EVENT) {
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
				configurationCtrl.dispatchEvent(ureq, checkboxListEditCtrl, event);
			}
		} else if (source == highScoreNodeConfigController){
			if (event == Event.DONE_EVENT) {
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		}
	}
}