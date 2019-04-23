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

package org.olat.course.nodes;

import java.io.File;
import java.util.List;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.stack.BreadcrumbPanel;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.iframe.DeliveryOptions;
import org.olat.core.gui.control.generic.messages.MessageUIFactory;
import org.olat.core.gui.control.generic.tabbable.TabbableController;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.id.Roles;
import org.olat.core.logging.OLATRuntimeException;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.course.ICourse;
import org.olat.course.assessment.AssessmentManager;
import org.olat.course.assessment.ui.tool.AssessmentCourseNodeController;
import org.olat.course.assessment.ui.tool.IdentityListCourseNodeController;
import org.olat.course.auditing.UserNodeAuditManager;
import org.olat.course.editor.CourseEditorEnv;
import org.olat.course.editor.NodeEditController;
import org.olat.course.editor.StatusDescription;
import org.olat.course.nodes.basiclti.LTIConfigForm;
import org.olat.course.nodes.basiclti.LTIEditController;
import org.olat.course.nodes.basiclti.LTIRunController;
import org.olat.course.run.navigation.NodeRunConstructionResult;
import org.olat.course.run.scoring.AssessmentEvaluation;
import org.olat.course.run.scoring.ScoreEvaluation;
import org.olat.course.run.userview.NodeEvaluation;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.group.BusinessGroup;
import org.olat.ims.lti.LTIDisplayOptions;
import org.olat.ims.lti.LTIManager;
import org.olat.ims.lti.ui.LTIResultDetailsController;
import org.olat.modules.ModuleConfiguration;
import org.olat.modules.assessment.AssessmentEntry;
import org.olat.modules.assessment.Role;
import org.olat.modules.assessment.model.AssessmentRunStatus;
import org.olat.modules.assessment.ui.AssessmentToolContainer;
import org.olat.modules.assessment.ui.AssessmentToolSecurityCallback;
import org.olat.repository.RepositoryEntry;
import org.olat.resource.OLATResource;

/**
 * @author guido
 * @author Charles Severance
 */
public class BasicLTICourseNode extends AbstractAccessableCourseNode implements PersistentAssessableCourseNode {

	private static final long serialVersionUID = 2210572148308757127L;
	private static final String translatorPackage = Util.getPackageName(LTIEditController.class);
	private static final String TYPE = "lti";
	
	public static final int CURRENT_VERSION = 3;
	public static final String CONFIG_KEY_AUTHORROLE = "authorRole";
	public static final String CONFIG_KEY_COACHROLE = "coachRole";
	public static final String CONFIG_KEY_PARTICIPANTROLE = "participantRole";
	public static final String CONFIG_KEY_SCALEVALUE = "scaleFactor";
	public static final String CONFIG_KEY_HAS_SCORE_FIELD = MSCourseNode.CONFIG_KEY_HAS_SCORE_FIELD;
	public static final String CONFIG_KEY_HAS_PASSED_FIELD = MSCourseNode.CONFIG_KEY_HAS_PASSED_FIELD;
	public static final String CONFIG_KEY_PASSED_CUT_VALUE = MSCourseNode.CONFIG_KEY_PASSED_CUT_VALUE;
	public static final String CONFIG_SKIP_LAUNCH_PAGE = "skiplaunchpage";
	public static final String CONFIG_SKIP_ACCEPT_LAUNCH_PAGE = "skipacceptlaunchpage";
	public static final String CONFIG_HEIGHT = "displayHeight";
	public static final String CONFIG_WIDTH = "displayWidth";
	public static final String CONFIG_HEIGHT_AUTO = DeliveryOptions.CONFIG_HEIGHT_AUTO;
	public static final String CONFIG_DISPLAY = "display";
	
	
	// NLS support:
	
	private static final String NLS_ERROR_HOSTMISSING_SHORT = "error.hostmissing.short";
	private static final String NLS_ERROR_HOSTMISSING_LONG = "error.hostmissing.long";

	/**
	 * Constructor for a course node of type learning content tunneling
	 */
	public BasicLTICourseNode() {
		super(TYPE);
		updateModuleConfigDefaults(true);
	}

	/**
	 * @see org.olat.course.nodes.CourseNode#createEditController(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.control.WindowControl, org.olat.course.ICourse)
	 */
	@Override
	public TabbableController createEditController(UserRequest ureq, WindowControl wControl, BreadcrumbPanel stackPanel, ICourse course, UserCourseEnvironment euce) {
		updateModuleConfigDefaults(false);
		LTIEditController childTabCntrllr = new LTIEditController(getModuleConfiguration(), ureq, wControl, this, course, euce);
		CourseNode chosenNode = course.getEditorTreeModel().getCourseNode(euce.getCourseEditorEnv().getCurrentCourseNodeId());
		return new NodeEditController(ureq, wControl, course.getEditorTreeModel(), course, chosenNode, euce, childTabCntrllr);
	}

	/**
	 * @see org.olat.course.nodes.CourseNode#createNodeRunConstructionResult(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.control.WindowControl,
	 *      org.olat.course.run.userview.UserCourseEnvironment,
	 *      org.olat.course.run.userview.NodeEvaluation)
	 */
	@Override
	public NodeRunConstructionResult createNodeRunConstructionResult(UserRequest ureq, WindowControl wControl,
			UserCourseEnvironment userCourseEnv, NodeEvaluation ne, String nodecmd) {
		updateModuleConfigDefaults(false);
		
		Controller runCtrl;
		if(userCourseEnv.isCourseReadOnly()) {
			Translator trans = Util.createPackageTranslator(BasicLTICourseNode.class, ureq.getLocale());
            String title = trans.translate("freezenoaccess.title");
            String message = trans.translate("freezenoaccess.message");
            runCtrl = MessageUIFactory.createInfoMessage(ureq, wControl, title, message);
		} else {
			Roles roles = ureq.getUserSession().getRoles();
			if (roles.isGuestOnly()) {
				if(isGuestAllowed()) {
					Translator trans = Util.createPackageTranslator(BasicLTICourseNode.class, ureq.getLocale());
					String title = trans.translate("guestnoaccess.title");
					String message = trans.translate("guestnoaccess.message");
					runCtrl = MessageUIFactory.createInfoMessage(ureq, wControl, title, message);
				} else {
					runCtrl = new LTIRunController(wControl, getModuleConfiguration(), ureq, this, userCourseEnv);
				}
			} else {
				runCtrl = new LTIRunController(wControl, getModuleConfiguration(), ureq, this, userCourseEnv);
			}
		}
		Controller ctrl = TitledWrapperHelper.getWrapper(ureq, wControl, runCtrl, this, "o_lti_icon");
		return new NodeRunConstructionResult(ctrl);
	}
	
	public boolean isGuestAllowed() {
		ModuleConfiguration config = getModuleConfiguration();
		boolean assessable = config.getBooleanSafe(BasicLTICourseNode.CONFIG_KEY_HAS_SCORE_FIELD, false);
		boolean sendName = config.getBooleanSafe(LTIConfigForm.CONFIG_KEY_SENDNAME, false);
		boolean sendEmail = config.getBooleanSafe(LTIConfigForm.CONFIG_KEY_SENDEMAIL, false);
		boolean customValues = StringHelper.containsNonWhitespace(config.getStringValue(LTIConfigForm.CONFIG_KEY_CUSTOM));
		return !assessable && !sendName && !sendEmail && !customValues;
	}

	/**
	 * @see org.olat.course.nodes.GenericCourseNode#createPreviewController(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.control.WindowControl,
	 *      org.olat.course.run.userview.UserCourseEnvironment,
	 *      org.olat.course.run.userview.NodeEvaluation)
	 */
	@Override
	public Controller createPreviewController(UserRequest ureq, WindowControl wControl, UserCourseEnvironment userCourseEnv, NodeEvaluation ne) {
		return createNodeRunConstructionResult(ureq, wControl, userCourseEnv, ne, null).getRunController();
	}

	/**
	 * @see org.olat.course.nodes.CourseNode#isConfigValid()
	 */
	@Override
	public StatusDescription isConfigValid() {

		/*
		 * first check the one click cache
		 */
		if (oneClickStatusCache != null) { return oneClickStatusCache[0]; }

		String host = (String) getModuleConfiguration().get(LTIConfigForm.CONFIGKEY_HOST);
		boolean isValid = host != null;
		StatusDescription sd = StatusDescription.NOERROR;
		if (!isValid) {
			// FIXME: refine statusdescriptions
			String[] params = new String[] { this.getShortTitle() };
			sd = new StatusDescription(StatusDescription.ERROR, NLS_ERROR_HOSTMISSING_SHORT, NLS_ERROR_HOSTMISSING_LONG, params, translatorPackage);
			sd.setDescriptionForUnit(getIdent());
			// set which pane is affected by error
			sd.setActivateableViewIdentifier(LTIEditController.PANE_TAB_LTCONFIG);
		}
		return sd;
	}

	/**
	 * @see org.olat.course.nodes.CourseNode#isConfigValid(org.olat.course.run.userview.UserCourseEnvironment)
	 */
	@Override
	public StatusDescription[] isConfigValid(CourseEditorEnv cev) {
		oneClickStatusCache = null;
		// only here we know which translator to take for translating condition
		// error messages
		List<StatusDescription> sds =  isConfigValidWithTranslator(cev, translatorPackage, getConditionExpressions());
		oneClickStatusCache = StatusDescriptionHelper.sort(sds);
		return oneClickStatusCache;
	}

	/**
	 * @see org.olat.course.nodes.CourseNode#getReferencedRepositoryEntry()
	 */
	@Override
	public RepositoryEntry getReferencedRepositoryEntry() {
		return null;
	}

	/**
	 * @see org.olat.course.nodes.CourseNode#needsReferenceToARepositoryEntry()
	 */
	@Override
	public boolean needsReferenceToARepositoryEntry() {
		return false;
	}
	
	/**
	 * @see org.olat.course.nodes.CourseNode#cleanupOnDelete(org.olat.course.ICourse)
	 */
	@Override
	public void cleanupOnDelete(ICourse course) {
		super.cleanupOnDelete(course);
		OLATResource resource = course.getCourseEnvironment().getCourseGroupManager().getCourseResource();
		CoreSpringFactory.getImpl(LTIManager.class).deleteOutcomes(resource);
	}

	/**
	 * Update the module configuration to have all mandatory configuration flags
	 * set to usefull default values
	 * 
	 * @param isNewNode true: an initial configuration is set; false: upgrading
	 *          from previous node configuration version, set default to maintain
	 *          previous behaviour
	 */
	@Override
	public void updateModuleConfigDefaults(boolean isNewNode) {
		ModuleConfiguration config = getModuleConfiguration();
		if (isNewNode) {
			// use defaults for new course building blocks
			config.setBooleanEntry(NodeEditController.CONFIG_STARTPAGE, Boolean.FALSE.booleanValue());
			config.setBooleanEntry(CONFIG_SKIP_LAUNCH_PAGE, Boolean.FALSE.booleanValue());
			config.setBooleanEntry(CONFIG_SKIP_ACCEPT_LAUNCH_PAGE, Boolean.FALSE.booleanValue());
		} else {
			// clear old popup configuration
			config.remove(NodeEditController.CONFIG_INTEGRATION);
			config.remove("width");
			config.remove("height");
			if (config.getConfigurationVersion() < 2) {
				// update new configuration options using default values for existing nodes
				config.setBooleanEntry(NodeEditController.CONFIG_STARTPAGE, Boolean.TRUE.booleanValue());
			}
			if (config.getConfigurationVersion() < 3) {
				if (BasicLTICourseNode.CONFIG_DISPLAY.equals(LTIDisplayOptions.window.name())) {
					config.setBooleanEntry(CONFIG_SKIP_LAUNCH_PAGE, Boolean.FALSE.booleanValue());
				} else {
					config.setBooleanEntry(CONFIG_SKIP_LAUNCH_PAGE, Boolean.TRUE.booleanValue());
				}
				config.setBooleanEntry(CONFIG_SKIP_ACCEPT_LAUNCH_PAGE, Boolean.FALSE.booleanValue());
			}
		}
		config.setConfigurationVersion(CURRENT_VERSION);
	}

	@Override
	public boolean isAssessedBusinessGroups() {
		return false;
	}

	@Override
	public Float getMaxScoreConfiguration() {
		if (!hasScoreConfigured()) {
			throw new OLATRuntimeException(MSCourseNode.class, "getMaxScore not defined when hasScore set to false", null);
		}
		ModuleConfiguration config = getModuleConfiguration();
		Float scaleFactor = (Float) config.get(CONFIG_KEY_SCALEVALUE);
		if(scaleFactor == null || scaleFactor.floatValue() < 0.0000001f) {
			return 1.0f;
		}
		return 1.0f * scaleFactor.floatValue();//LTI 1.1 return between 0.0 - 1.0
	}

	@Override
	public Float getMinScoreConfiguration() {
		if (!hasScoreConfigured()) { 
			throw new OLATRuntimeException(MSCourseNode.class, "getMaxScore not defined when hasScore set to false", null);
		}
		return 0.0f;
	}

	@Override
	public Float getCutValueConfiguration() {
		if (!hasPassedConfigured()) { 
			throw new OLATRuntimeException(MSCourseNode.class, "getCutValue not defined when hasPassed set to false", null);
		}
		ModuleConfiguration config = getModuleConfiguration();
		return config.getFloatEntry(CONFIG_KEY_PASSED_CUT_VALUE);
	}

	@Override
	public boolean hasScoreConfigured() {
		ModuleConfiguration config = getModuleConfiguration();
		Boolean score = config.getBooleanEntry(CONFIG_KEY_HAS_SCORE_FIELD);
		return (score == null) ? false : score.booleanValue();
	}

	@Override
	public boolean hasPassedConfigured() {
		ModuleConfiguration config = getModuleConfiguration();
		Boolean passed = config.getBooleanEntry(CONFIG_KEY_HAS_PASSED_FIELD);
		return (passed == null) ? false : passed.booleanValue();
	}

	@Override
	public boolean hasCommentConfigured() {
		return false;
	}

	@Override
	public boolean hasIndividualAsssessmentDocuments() {
		return getModuleConfiguration().getBooleanSafe(MSCourseNode.CONFIG_KEY_HAS_INDIVIDUAL_ASSESSMENT_DOCS, false);
	}

	@Override
	public boolean hasAttemptsConfigured() {
		// having score defined means the node is assessable
		ModuleConfiguration config = getModuleConfiguration();
		Boolean score = config.getBooleanEntry(CONFIG_KEY_HAS_SCORE_FIELD);
		return (score == null) ? false : score.booleanValue();
	}

	@Override
	public boolean hasDetails() {
		// having score defined means the node is assessable
		ModuleConfiguration config = getModuleConfiguration();
		Boolean score = config.getBooleanEntry(CONFIG_KEY_HAS_SCORE_FIELD);
		return (score == null) ? false : score.booleanValue();
	}

	@Override
	public boolean hasStatusConfigured() {
		return false;
	}
	
	@Override
	public boolean hasCompletion() {
		return false;
	}

	@Override
	public boolean isEditableConfigured() {
		// having score defined means the node is assessable
		ModuleConfiguration config = getModuleConfiguration();
		Boolean score = config.getBooleanEntry(CONFIG_KEY_HAS_SCORE_FIELD);
		return (score == null) ? false : score.booleanValue();
	}
	
	@Override
	public AssessmentEntry getUserAssessmentEntry(UserCourseEnvironment userCourseEnv) {
		AssessmentManager am = userCourseEnv.getCourseEnvironment().getAssessmentManager();
		return am.getAssessmentEntry(this, userCourseEnv.getIdentityEnvironment().getIdentity());
	}

	@Override
	public AssessmentEvaluation getUserScoreEvaluation(UserCourseEnvironment userCourseEnv) {
		AssessmentManager am = userCourseEnv.getCourseEnvironment().getAssessmentManager();
		Identity mySelf = userCourseEnv.getIdentityEnvironment().getIdentity();
		AssessmentEntry entry = am.getAssessmentEntry(this, mySelf);
		return getUserScoreEvaluation(entry) ;
	}

	@Override
	public AssessmentEvaluation getUserScoreEvaluation(AssessmentEntry entry) {
		return AssessmentEvaluation.toAssessmentEvalutation(entry, this);
	}

	@Override
	public String getUserUserComment(UserCourseEnvironment userCourseEnvironment) {
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		return am.getNodeComment(this, mySelf);
	}

	@Override
	public List<File> getIndividualAssessmentDocuments(UserCourseEnvironment userCourseEnvironment) {
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		return am.getIndividualAssessmentDocuments(this, mySelf);
	}

	@Override
	public String getUserCoachComment(UserCourseEnvironment userCourseEnvironment) {
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		return am.getNodeCoachComment(this, mySelf);
	}

	@Override
	public String getUserLog(UserCourseEnvironment userCourseEnvironment) {
		// having score defined means the node is assessable
 		UserNodeAuditManager am = userCourseEnvironment.getCourseEnvironment().getAuditManager();
		Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		return am.getUserNodeLog(this, mySelf);
	}

	@Override
	public Integer getUserAttempts(UserCourseEnvironment userCourseEnvironment) {
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		return am.getNodeAttempts(this, mySelf);
	}

	@Override
	public Double getUserCurrentRunCompletion(UserCourseEnvironment userCourseEnvironment) {
		return null;
	}

	@Override
	public String getDetailsListView(UserCourseEnvironment userCourseEnvironment) {
		return null;
	}

	@Override
	public String getDetailsListViewHeaderKey() {
		return null;
	}

	@Override
	public Controller getDetailsEditController(UserRequest ureq, WindowControl wControl,
			BreadcrumbPanel stackPanel, UserCourseEnvironment coachCourseEnv, UserCourseEnvironment assessedUserCourseEnv) {
		Identity assessedIdentity = assessedUserCourseEnv.getIdentityEnvironment().getIdentity();
		OLATResource resource = assessedUserCourseEnv.getCourseEnvironment().getCourseGroupManager().getCourseResource();
		return new LTIResultDetailsController(ureq, wControl, assessedIdentity, resource, getIdent());
	}
	
	@Override
	public AssessmentCourseNodeController getIdentityListController(UserRequest ureq, WindowControl wControl, TooledStackedPanel stackPanel,
			RepositoryEntry courseEntry, BusinessGroup group, UserCourseEnvironment coachCourseEnv,
			AssessmentToolContainer toolContainer, AssessmentToolSecurityCallback assessmentCallback) {
		return new IdentityListCourseNodeController(ureq, wControl, stackPanel,
				courseEntry, group, this, coachCourseEnv, toolContainer, assessmentCallback);
	}

	@Override
	public void updateUserScoreEvaluation(ScoreEvaluation scoreEvaluation, UserCourseEnvironment userCourseEnvironment,
			Identity coachingIdentity, boolean incrementAttempts, Role by) {
		
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		am.saveScoreEvaluation(this, coachingIdentity, mySelf, new ScoreEvaluation(scoreEvaluation), userCourseEnvironment, incrementAttempts, by);
	}

	@Override
	public void updateUserUserComment(String userComment, UserCourseEnvironment userCourseEnvironment, Identity coachingIdentity) {
		if (userComment != null) {
			AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
			Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
			am.saveNodeComment(this, coachingIdentity, mySelf, userComment);
		}
	}

	@Override
	public void addIndividualAssessmentDocument(File document, String filename, UserCourseEnvironment userCourseEnvironment, Identity coachingIdentity) {
		if(document != null) {
			AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
			Identity assessedIdentity = userCourseEnvironment.getIdentityEnvironment().getIdentity();
			am.addIndividualAssessmentDocument(this, coachingIdentity, assessedIdentity, document, filename);
		}
	}

	@Override
	public void removeIndividualAssessmentDocument(File document, UserCourseEnvironment userCourseEnvironment, Identity coachingIdentity) {
		if(document != null) {
			AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
			Identity assessedIdentity = userCourseEnvironment.getIdentityEnvironment().getIdentity();
			am.removeIndividualAssessmentDocument(this, coachingIdentity, assessedIdentity, document);
		}
	}

	@Override
	public void incrementUserAttempts(UserCourseEnvironment userCourseEnvironment, Role by) {
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		am.incrementNodeAttempts(this, mySelf, userCourseEnvironment, by);
	}

	@Override
	public void updateUserAttempts(Integer userAttempts, UserCourseEnvironment userCourseEnvironment, Identity coachingIdentity, Role by) {
		if (userAttempts != null) {
			AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
			Identity mySelf = userCourseEnvironment.getIdentityEnvironment().getIdentity();
			am.saveNodeAttempts(this, coachingIdentity, mySelf, userAttempts, by);
		}
	}
	
	@Override
	public void updateCurrentCompletion(UserCourseEnvironment userCourseEnvironment, Identity identity,
			Double currentCompletion, AssessmentRunStatus status, Role doneBy) {
		throw new OLATRuntimeException(BasicLTICourseNode.class, "Completion variable can't be updated in LTI nodes", null);
	}
	
	@Override
	public void updateLastModifications(UserCourseEnvironment userCourseEnvironment, Identity identity, Role by) {
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		Identity assessedIdentity = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		am.updateLastModifications(this, assessedIdentity, userCourseEnvironment, by);
	}

	@Override
	public void updateUserCoachComment(String coachComment, UserCourseEnvironment userCourseEnvironment) {
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		if (coachComment != null) {
			am.saveNodeCoachComment(this, userCourseEnvironment.getIdentityEnvironment().getIdentity(), coachComment);
		}
	}
}