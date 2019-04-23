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
package org.olat.course.nodes;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.stack.BreadcrumbPanel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.messages.MessageUIFactory;
import org.olat.core.gui.control.generic.tabbable.TabbableController;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Util;
import org.olat.course.ICourse;
import org.olat.course.condition.ConditionEditController;
import org.olat.course.editor.CourseEditorEnv;
import org.olat.course.editor.NodeEditController;
import org.olat.course.editor.StatusDescription;
import org.olat.course.nodes.edubase.EdubaseEditController;
import org.olat.course.nodes.edubase.EdubasePeekViewController;
import org.olat.course.nodes.edubase.EdubaseRunController;
import org.olat.course.run.navigation.NodeRunConstructionResult;
import org.olat.course.run.userview.NodeEvaluation;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.modules.ModuleConfiguration;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 21.06.2017<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class EdubaseCourseNode extends AbstractAccessableCourseNode {

	private static final long serialVersionUID = -2370512428049915735L;
	
	private static final String TYPE = "edubase";
	
	public static final int CURRENT_VERSION = 1;
	public static final String CONFIG_DESCRIPTION_ENABLED = "descriptionEnabled";
	public static final String CONFIG_BOOK_SECTIONS = "bookSections";
	
	public EdubaseCourseNode() {
		super(TYPE);
		updateModuleConfigDefaults(true);
	}

	@Override
	public TabbableController createEditController(UserRequest ureq, WindowControl wControl, BreadcrumbPanel stackPanel,
			ICourse course, UserCourseEnvironment euce) {
		updateModuleConfigDefaults(false);
		EdubaseEditController childTabCntrllr = new EdubaseEditController(ureq, wControl, this, course, euce);
		CourseNode chosenNode = course.getEditorTreeModel().getCourseNode(euce.getCourseEditorEnv().getCurrentCourseNodeId());
		return new NodeEditController(ureq, wControl, course.getEditorTreeModel(), course, chosenNode, euce, childTabCntrllr);
	}

	@Override
	public NodeRunConstructionResult createNodeRunConstructionResult(UserRequest ureq, WindowControl wControl,
			UserCourseEnvironment userCourseEnv, NodeEvaluation ne, String nodecmd) {
		updateModuleConfigDefaults(false);
		Controller runCtrl;
		if(userCourseEnv.isCourseReadOnly()) {
			Translator trans = Util.createPackageTranslator(EdubaseCourseNode.class, ureq.getLocale());
		    String title = trans.translate("freezenoaccess.title");
		    String message = trans.translate("freezenoaccess.message");
		    runCtrl = MessageUIFactory.createInfoMessage(ureq, wControl, title, message);
		} else if (userCourseEnv.getIdentityEnvironment().getRoles().isGuestOnly()) {
			Translator trans = Util.createPackageTranslator(EdubaseCourseNode.class, ureq.getLocale());
			String title = trans.translate("guestnoaccess.title");
			String message = trans.translate("guestnoaccess.message");
		    runCtrl = MessageUIFactory.createInfoMessage(ureq, wControl, title, message);
		} else {
			runCtrl = new EdubaseRunController(ureq, wControl, this.getModuleConfiguration());
		}
		Controller ctrl = TitledWrapperHelper.getWrapper(ureq, wControl, runCtrl, this, "o_edubase_icon");
		return new NodeRunConstructionResult(ctrl);
	}
	
	@Override
	public Controller createPeekViewRunController(UserRequest ureq, WindowControl wControl, UserCourseEnvironment userCourseEnv, NodeEvaluation ne) {
		return new EdubasePeekViewController(ureq, wControl, getModuleConfiguration(), this.getIdent());
	}

	@SuppressWarnings("deprecation")
	@Override
	public StatusDescription[] isConfigValid(CourseEditorEnv cev) {
		String translatorStr = Util.getPackageName(ConditionEditController.class);
		List<StatusDescription> statusDescs = isConfigValidWithTranslator(cev, translatorStr, getConditionExpressions());
		return StatusDescriptionHelper.sort(statusDescs);
	}
	
	@Override
	public StatusDescription isConfigValid() {
		return  StatusDescription.NOERROR;
	}
	
	@Override
	public boolean needsReferenceToARepositoryEntry() {
		return false;
	}
	
	@Override
	public RepositoryEntry getReferencedRepositoryEntry() {
		return null;
	}
	
	@Override
	public void updateModuleConfigDefaults(boolean isNewNode) {
		ModuleConfiguration config = getModuleConfiguration();
		if (isNewNode) {
			config.setBooleanEntry(CONFIG_DESCRIPTION_ENABLED, Boolean.TRUE.booleanValue());
		}
		config.setConfigurationVersion(CURRENT_VERSION);
	}

}
