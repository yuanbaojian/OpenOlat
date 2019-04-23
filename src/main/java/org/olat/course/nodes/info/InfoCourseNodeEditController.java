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


package org.olat.course.nodes.info;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.tabbedpane.TabbedPane;
import org.olat.core.gui.components.velocity.VelocityContainer;
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
import org.olat.course.nodes.InfoCourseNode;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.course.tree.CourseEditorTreeModel;
import org.olat.modules.ModuleConfiguration;

/**
 * 
 * Description:<br>
 * Edit the configuration of the info message course node
 * 
 * <P>
 * Initial Date:  3 aug. 2010 <br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class InfoCourseNodeEditController extends ActivateableTabbableDefaultController implements ControllerEventListener {
	private static final String PANE_TAB_CONFIG = "pane.tab.infos_config";
	private static final String PANE_TAB_ACCESSIBILITY = "pane.tab.accessibility";
	private static final String[] paneKeys = {PANE_TAB_ACCESSIBILITY, PANE_TAB_CONFIG};
	
	private final InfoCourseNode courseNode;

	private TabbedPane myTabbedPane;
	private VelocityContainer configContent;
	private InfoConfigForm infoConfigForm;
	private VelocityContainer editAccessVc;
	private ConditionEditController accessCondContr;
	private ConditionEditController editCondContr;
	private ConditionEditController adminCondContr;
	
	public InfoCourseNodeEditController(UserRequest ureq, WindowControl wControl, ModuleConfiguration config, InfoCourseNode courseNode, ICourse course,
			UserCourseEnvironment euce) {
		super(ureq,wControl);
		
		this.courseNode = courseNode;
		
		infoConfigForm = new InfoConfigForm(ureq, wControl, config);
		listenTo(infoConfigForm);
		
		editAccessVc = createVelocityContainer("edit_access");
		CourseEditorTreeModel editorModel = course.getEditorTreeModel();
		// Accessibility precondition
		Condition accessCondition = courseNode.getPreConditionAccess();
		accessCondContr = new ConditionEditController(ureq, getWindowControl(), euce, accessCondition,
				AssessmentHelper.getAssessableNodes(editorModel, courseNode));
		listenTo(accessCondContr);
		editAccessVc.put("readerCondition", accessCondContr.getInitialComponent());

		// read / write preconditions
		Condition editCondition = courseNode.getPreConditionEdit();
		editCondContr = new ConditionEditController(ureq, getWindowControl(), euce, editCondition, AssessmentHelper
				.getAssessableNodes(editorModel, courseNode));
		listenTo(editCondContr);
		editAccessVc.put("editCondition", editCondContr.getInitialComponent());
		
		// administration preconditions
		Condition adminCondition = courseNode.getPreConditionAdmin();
		adminCondContr = new ConditionEditController(ureq, getWindowControl(), euce, adminCondition, AssessmentHelper
				.getAssessableNodes(editorModel, courseNode));
		listenTo(adminCondContr);
		editAccessVc.put("adminCondition", adminCondContr.getInitialComponent());
		
		configContent = createVelocityContainer("edit");
		configContent.put("infoConfigForm", infoConfigForm.getInitialComponent());
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

	@Override
	public void addTabs(TabbedPane tabbedPane) {
		myTabbedPane = tabbedPane;
		tabbedPane.addTab(translate(PANE_TAB_ACCESSIBILITY), editAccessVc);
		tabbedPane.addTab(translate(PANE_TAB_CONFIG), configContent);
	}


	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		//
	}

	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == infoConfigForm) {
			if (event == Event.DONE_EVENT) {
				infoConfigForm.getUpdatedConfig();
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		} else if (source == accessCondContr) {
			if (event == Event.CHANGED_EVENT) {
				Condition cond = accessCondContr.getCondition();
				courseNode.setPreConditionAccess(cond);
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		} else if (source == editCondContr) {
			if (event == Event.CHANGED_EVENT) {
				Condition cond = editCondContr.getCondition();
				courseNode.setPreConditionEdit(cond);
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		} else if (source == adminCondContr) {
			if (event == Event.CHANGED_EVENT) {
				Condition cond = adminCondContr.getCondition();
				courseNode.setPreConditionAdmin(cond);
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		}
	}
	
	public static boolean getAutoSubscribe(ModuleConfiguration config) {
		String autoStr = config.getStringValue(InfoCourseNodeConfiguration.CONFIG_AUTOSUBSCRIBE);
		return ("on".equals(autoStr));
	}
}
