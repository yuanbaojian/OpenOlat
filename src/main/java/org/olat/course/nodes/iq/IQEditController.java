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

package org.olat.course.nodes.iq;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.stack.BreadcrumbPanel;
import org.olat.core.gui.components.tabbedpane.TabbedPane;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.ControllerEventListener;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.tabbable.ActivateableTabbableDefaultController;
import org.olat.core.logging.AssertException;
import org.olat.course.ICourse;
import org.olat.course.assessment.AssessmentHelper;
import org.olat.course.condition.Condition;
import org.olat.course.condition.ConditionEditController;
import org.olat.course.editor.NodeEditController;
import org.olat.course.highscore.ui.HighScoreEditController;
import org.olat.course.nodes.AbstractAccessableCourseNode;
import org.olat.course.nodes.IQSELFCourseNode;
import org.olat.course.nodes.IQSURVCourseNode;
import org.olat.course.nodes.IQTESTCourseNode;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.ims.qti.process.AssessmentInstance;
import org.olat.modules.ModuleConfiguration;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryManager;

/**
 * Description:<BR/>
 * Edit controller for the qti test, selftest and survey course node
 * <P/>
 * Initial Date:  Oct 13, 2004
 *
 * @author Felix Jost
 */
public class IQEditController extends ActivateableTabbableDefaultController implements ControllerEventListener {

	private static final String PANE_TAB_IQLAYOUTCONFIG = "pane.tab.iqconfig.layout";
	
	public static final String PANE_TAB_IQCONFIG_SURV = "pane.tab.iqconfig.surv";
	public static final String PANE_TAB_IQCONFIG_SELF = "pane.tab.iqconfig.self";
	public static final String PANE_TAB_IQCONFIG_TEST = "pane.tab.iqconfig.test";
	public static final String PANE_TAB_ACCESSIBILITY = "pane.tab.accessibility";
	private static final String PANE_TAB_HIGHSCORE = "pane.tab.highscore"; 

	/** configuration key: repository sof key reference to qti file*/
	public static final String CONFIG_KEY_REPOSITORY_SOFTKEY = "repoSoftkey";
	/** configuration key: the disclaimer text*/
	public static final String CONFIG_KEY_DISCLAIMER = "disc";
	/** configuration key: enable menu switch*/
	public static final String CONFIG_KEY_ENABLEMENU = "enableMenu";
	/** configuration key: display menu switch*/
	public static final String CONFIG_KEY_DISPLAYMENU = "displayMenu";
	/** configuration key: all questions, section titles only */
	public static final String CONFIG_KEY_RENDERMENUOPTION="renderMenu";
	/** configuration key: enable score progress switch*/
	public static final String CONFIG_KEY_SCOREPROGRESS = "displayScoreProgress";
	/** configuration key: suppress feedbacks */
	public static final String CONFIG_KEY_HIDE_FEEDBACKS = "hideFeedbacks";
	/** configuration key: enable cancel switch*/
	public static final String CONFIG_KEY_ENABLECANCEL = "enableCancel";
	/** configuration key: enable suspend switch*/
	public static final String CONFIG_KEY_ENABLESUSPEND = "enableSuspend";
	/** configuration key: enable question progress switch*/
	public static final String CONFIG_KEY_QUESTIONPROGRESS = "displayQuestionProgss";
	/** configuration key: enable question max score*/
	public static final String CONFIG_KEY_QUESTION_MAX_SCORE = "displayQuestionMaxScore";
	/** configuration key: enable question progress switch*/
	public static final String CONFIG_KEY_QUESTIONTITLE = "displayQuestionTitle";
	/** configuration key: enable automatic enumeration of "choice" options */
	public static final String CONFIG_KEY_AUTOENUM_CHOICES = "autoEnumerateChoices";
	/** configuration key: provide memo field */
	public static final String CONFIG_KEY_MEMO = "provideMemoField";
	/** configuration key: question sequence: item or selection */
	public static final String CONFIG_KEY_SEQUENCE = "sequence";
	/** configuration key: mode*/
	public static final String CONFIG_KEY_TYPE = "mode";
	/** configuration key: show summary: compact or detailed */
	public static final String CONFIG_KEY_SUMMARY = "summary";
	/** configuration key: max attempts*/
	public static final String CONFIG_KEY_ATTEMPTS = "attempts";
	/** configuration key: max attempts*/
	public static final String CONFIG_KEY_BLOCK_AFTER_SUCCESS = "blockAfterSuccess";
	/** configuration key: minimal score*/
	public static final String CONFIG_KEY_MINSCORE = "minscore";
	/** configuration key: maximal score*/
	public static final String CONFIG_KEY_MAXSCORE = "maxscore";
	/** configuration key: cut value (socre > cut = passed) */
	public static final String CONFIG_KEY_CUTVALUE = "cutvalue";
	/** configuration key for the filename */
	public static final String CONFIG_KEY_FILE = "file";
	/** configuration key: should relative links like ../otherfolder/my.css be allowed? **/
	public static final String CONFIG_KEY_ALLOW_RELATIVE_LINKS = "allowRelativeLinks";
	/** configuration key: enable 'show score infos' on start page */
	public static final String CONFIG_KEY_ENABLESCOREINFO = "enableScoreInfo";
	//<OLATCE-982>
	public static final String CONFIG_KEY_ALLOW_SHOW_SOLUTION = "showSolution";
	//</OLATCE-982>
	//<OLATCE-2009>
	public static final String CONFIG_KEY_ALLOW_SUSPENSION_ALLOWED = "suspendAllowed";
	//</OLATCE-2009>
	/** Test in full window mode*/
	public static final String CONFIG_FULLWINDOW = "fullwindow";
	/** Enable manual correction */
	public static final String CONFIG_CORRECTION_MODE = "correctionMode";
	/** Test in full window mode*/
	public static final String CONFIG_ALLOW_ANONYM = "allowAnonym";
	/** Digitally signed the assessment results */
	public static final String CONFIG_DIGITAL_SIGNATURE = "digitalSignature";
	/** Send the signature per mail */
	public static final String CONFIG_DIGITAL_SIGNATURE_SEND_MAIL = "digitalSignatureMail";
	/** configuration key: use configuration of the reference repository entry */
	public static final String CONFIG_KEY_CONFIG_REF = "configFromRef";
	/** configuration key: use a time limit for the test in seconds */
	public static final String CONFIG_KEY_TIME_LIMIT = "timeLimit";
	
	public static final String CORRECTION_AUTO = "auto";
	public static final String CORRECTION_MANUAL = "manual";

	public static final String CONFIG_KEY_DATE_DEPENDENT_RESULTS = "dateDependentResults";
	public static final String CONFIG_KEY_RESULTS_START_DATE = "resultsStartDate";
	public static final String CONFIG_KEY_RESULTS_END_DATE = "resultsEndDate";
	public static final String CONFIG_KEY_RESULT_ON_FINISH = "showResultsOnFinish";
	public static final String CONFIG_KEY_RESULT_ON_HOME_PAGE = "showResultsOnHomePage";

	public static final String CONFIG_KEY_DATE_DEPENDENT_TEST = "dateDependentTest";
	public static final String CONFIG_KEY_RESULTS_START_TEST_DATE = "resultsStartTestDate";
	public static final String CONFIG_KEY_RESULTS_END_TEST_DATE = "resultsEndTestDate";

	public static final String CONFIG_KEY_TEMPLATE = "templateid";
	public static final String CONFIG_KEY_TYPE_QTI = "qtitype";
	public static final String CONFIG_VALUE_QTI2 = "qti2";
	public static final String CONFIG_VALUE_QTI21 = "qti2w";
	public static final Object CONFIG_VALUE_QTI1 = "qti1";
	public static final String CONFIG_KEY_IS_SURVEY = "issurv";

	
	private final String[] paneKeys;
	private final String paneTabIQConfiguration;

	private ICourse course;
	
	private String type;
	private UserCourseEnvironment euce;
	private AbstractAccessableCourseNode courseNode;
	private ModuleConfiguration moduleConfiguration;

	private TabbedPane myTabbedPane;
	private Controller previewLayoutCtr;

	private final BreadcrumbPanel stackPanel;
	
	private ConditionEditController accessibilityCondContr;
	private IQConfigurationController configurationCtrl;
	private IQLayoutConfigurationController layoutConfigurationCtrl;
	private HighScoreEditController highScoreNodeConfigController;

	/**
	 * Constructor for the IMS QTI edit controller for a test course node
	 * 
	 * @param ureq The user request
	 * @param wControl The window controller
	 * @param course The course
	 * @param courseNode The test course node
	 * @param groupMgr
	 * @param euce
	 */
	public IQEditController(UserRequest ureq, WindowControl wControl, BreadcrumbPanel stackPanel, ICourse course, IQTESTCourseNode courseNode, UserCourseEnvironment euce) {
		super(ureq, wControl);
		this.stackPanel = stackPanel;
		this.moduleConfiguration = courseNode.getModuleConfiguration();
		//o_clusterOk by guido: save to hold reference to course inside editor
		this.course = course;
		this.courseNode = courseNode;
		this.euce = euce;
		
		
		type = AssessmentInstance.QMD_ENTRY_TYPE_ASSESS;
		this.paneTabIQConfiguration = PANE_TAB_IQCONFIG_TEST;
		paneKeys = new String[]{paneTabIQConfiguration,PANE_TAB_ACCESSIBILITY};
		// put some default values
		if (moduleConfiguration.get(CONFIG_KEY_ENABLECANCEL) == null) {
			moduleConfiguration.set(CONFIG_KEY_ENABLECANCEL, Boolean.FALSE);
		}
		if (moduleConfiguration.get(CONFIG_KEY_ENABLESUSPEND) == null) {
			moduleConfiguration.set(CONFIG_KEY_ENABLESUSPEND, Boolean.FALSE);
		}
		if(moduleConfiguration.get(CONFIG_KEY_RENDERMENUOPTION) == null) {
			moduleConfiguration.set(CONFIG_KEY_RENDERMENUOPTION, Boolean.FALSE);
		}
		
		init(ureq);
	}

	/**
	 * Constructor for the IMS QTI edit controller for a self-test course node
	 * 
	 * @param ureq The user request
	 * @param wControl The window controller
	 * @param course The course
	 * @param courseNode The self course node
	 * @param groupMgr
	 * @param euce
	 */
	 public IQEditController(UserRequest ureq, WindowControl wControl, BreadcrumbPanel stackPanel, ICourse course, IQSELFCourseNode courseNode , UserCourseEnvironment euce) {
		super(ureq, wControl);
		this.stackPanel = stackPanel;
		this.moduleConfiguration = courseNode.getModuleConfiguration();
		this.course = course;
		this.courseNode = courseNode;
		this.euce = euce;

		type = AssessmentInstance.QMD_ENTRY_TYPE_SELF;
		this.paneTabIQConfiguration = PANE_TAB_IQCONFIG_SELF;
		paneKeys = new String[]{paneTabIQConfiguration,PANE_TAB_ACCESSIBILITY};
		// put some default values
		if (moduleConfiguration.get(CONFIG_KEY_ENABLECANCEL) == null) {
			moduleConfiguration.set(CONFIG_KEY_ENABLECANCEL, Boolean.TRUE);
		}
		if (moduleConfiguration.get(CONFIG_KEY_ENABLESUSPEND) == null) {
			moduleConfiguration.set(CONFIG_KEY_ENABLESUSPEND, Boolean.TRUE);
		}

		init(ureq);
	}

	/**
	 * Constructor for the IMS QTI edit controller for a survey course node
	 * 
	 * @param ureq The user request
	 * @param wControl The window controller
	 * @param course The course
	 * @param courseNode The survey course node
	 * @param groupMgr
	 * @param euce
	 */
	 public IQEditController(UserRequest ureq, WindowControl wControl, BreadcrumbPanel stackPanel, ICourse course, IQSURVCourseNode courseNode, UserCourseEnvironment euce) {
		super(ureq, wControl);
		this.stackPanel = stackPanel;
		this.moduleConfiguration = courseNode.getModuleConfiguration();
		this.course = course;
		this.courseNode = courseNode;
		this.euce = euce;

		type = AssessmentInstance.QMD_ENTRY_TYPE_SURVEY;
		this.paneTabIQConfiguration = PANE_TAB_IQCONFIG_SURV;
		paneKeys = new String[]{paneTabIQConfiguration,PANE_TAB_ACCESSIBILITY};

		// put some default values
		if (moduleConfiguration.get(CONFIG_KEY_SCOREPROGRESS) == null){
			moduleConfiguration.set(CONFIG_KEY_SCOREPROGRESS, Boolean.FALSE);
		}
		if(moduleConfiguration.getBooleanEntry(CONFIG_KEY_ALLOW_RELATIVE_LINKS)==null){
			moduleConfiguration.setBooleanEntry(CONFIG_KEY_ALLOW_RELATIVE_LINKS,false);
		}
		
		init(ureq);
	}

	private void init(UserRequest ureq) {		
		configurationCtrl = new IQConfigurationController(ureq, getWindowControl(), stackPanel, course, courseNode, type);
		listenTo(configurationCtrl);
		layoutConfigurationCtrl = new IQLayoutConfigurationController(ureq, getWindowControl(), course, courseNode, type);
		listenTo(layoutConfigurationCtrl);	
		if (AssessmentInstance.QMD_ENTRY_TYPE_ASSESS.equals(type)) {
			highScoreNodeConfigController = new HighScoreEditController(ureq, getWindowControl(), moduleConfiguration);
			listenTo(highScoreNodeConfigController);
		}
		Condition accessCondition = courseNode.getPreConditionAccess();
		accessibilityCondContr = new ConditionEditController(ureq, getWindowControl(), euce, accessCondition,
				AssessmentHelper.getAssessableNodes(course.getEditorTreeModel(), courseNode));		
		listenTo(accessibilityCondContr);
	}

	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		//
	}

	@Override
	public void event(UserRequest urequest, Controller source, Event event) {
		if (source == accessibilityCondContr) {
			if (event == Event.CHANGED_EVENT) {
				Condition cond = accessibilityCondContr.getCondition();
				courseNode.setPreConditionAccess(cond);
				fireEvent(urequest, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		} else if (source == configurationCtrl) {
			if (event == NodeEditController.NODECONFIG_CHANGED_EVENT) {
				fireEvent(urequest, NodeEditController.NODECONFIG_CHANGED_EVENT);
				layoutConfigurationCtrl.updateEditController(urequest);
			}
		} else if (source == highScoreNodeConfigController){
			if (event == Event.DONE_EVENT) {
				fireEvent(urequest, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		} else if (source == layoutConfigurationCtrl) {
			if (event == NodeEditController.NODECONFIG_CHANGED_EVENT) {
				fireEvent(urequest, NodeEditController.NODECONFIG_CHANGED_EVENT);
				configurationCtrl.updateEditController(urequest, false);
				layoutConfigurationCtrl.updateEditController(urequest);
			}
		}
	}
	
	@Override
	public void addTabs(TabbedPane tabbedPane) {
		myTabbedPane = tabbedPane;
		tabbedPane.addTab(translate(PANE_TAB_ACCESSIBILITY), accessibilityCondContr.getWrappedDefaultAccessConditionVC(translate("condition.accessibility.title")));
		//PANE_TAB_IQCONFIG_XXX is set during construction time
		tabbedPane.addTab(translate(paneTabIQConfiguration), configurationCtrl.getInitialComponent());
		tabbedPane.addTab(translate(PANE_TAB_IQLAYOUTCONFIG), layoutConfigurationCtrl.getInitialComponent());
		if (AssessmentInstance.QMD_ENTRY_TYPE_ASSESS.equals(type)) {
			tabbedPane.addTab(translate(PANE_TAB_HIGHSCORE) , highScoreNodeConfigController.getInitialComponent());
		}
	}
	
	/**
	 * Ge the qti file soft key repository reference 
	 * @param config
	 * @param strict
	 * @return RepositoryEntry
	 */
	public static RepositoryEntry getIQReference(ModuleConfiguration config, boolean strict) {
		if (config == null) {
			if (strict) {
				throw new AssertException("missing config in IQ");
			} else {
				return null;
			}
		}
		String repoSoftkey = (String) config.get(CONFIG_KEY_REPOSITORY_SOFTKEY);
		if (repoSoftkey == null) {
			if (strict) {
				throw new AssertException("invalid config when being asked for references");
			} else {
				return null;
			}
		}
		RepositoryManager rm = RepositoryManager.getInstance();
		return rm.lookupRepositoryEntryBySoftkey(repoSoftkey, strict);
	}

	/**
	 * Set the referenced repository entry.
	 * 
	 * @param re
	 * @param moduleConfiguration
	 */
	public static void setIQReference(RepositoryEntry re, ModuleConfiguration moduleConfiguration) {
		moduleConfiguration.set(CONFIG_KEY_REPOSITORY_SOFTKEY, re.getSoftkey());
	}
	
	/**
	 * Remove the reference to the repository entry.
	 * 
	 * @param moduleConfiguration
	 */
	public static void removeIQReference(ModuleConfiguration moduleConfiguration) {
		moduleConfiguration.remove(IQEditController.CONFIG_KEY_REPOSITORY_SOFTKEY);
	}

	@Override
	protected void doDispose() {
    //child controllers registered with listenTo() get disposed in BasicController
		if (previewLayoutCtr != null) {
			previewLayoutCtr.dispose();
			previewLayoutCtr = null;
		}
	}
	
	@Override
	public String[] getPaneKeys() {
		return paneKeys;
	}

	@Override
	public TabbedPane getTabbedPane() {
		return myTabbedPane;
	}
}