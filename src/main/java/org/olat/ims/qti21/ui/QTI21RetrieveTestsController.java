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
package org.olat.ims.qti21.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.olat.basesecurity.GroupRoles;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.id.Identity;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.course.CourseFactory;
import org.olat.course.archiver.ScoreAccountingHelper;
import org.olat.course.assessment.AssessmentHelper;
import org.olat.course.nodes.IQTESTCourseNode;
import org.olat.course.nodes.iq.IQEditController;
import org.olat.course.nodes.iq.QTI21AssessmentRunController;
import org.olat.course.run.environment.CourseEnvironment;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.group.BusinessGroupService;
import org.olat.ims.qti.QTIResultManager;
import org.olat.ims.qti21.AssessmentTestSession;
import org.olat.ims.qti21.QTI21DeliveryOptions;
import org.olat.ims.qti21.QTI21Service;
import org.olat.ims.qti21.model.DigitalSignatureOptions;
import org.olat.modules.ModuleConfiguration;
import org.olat.modules.assessment.AssessmentToolOptions;
import org.olat.modules.assessment.Role;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryEntryRelationType;
import org.olat.repository.RepositoryService;
import org.olat.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 19 déc. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class QTI21RetrieveTestsController extends FormBasicController {

	private RepositoryEntry assessedEntry;
	private IQTESTCourseNode courseNode;
	
	private List<Identity> identities;
	private List<AssessmentTestSession> sessions;
	
	@Autowired
	private UserManager userManager;
	@Autowired
	private QTI21Service qtiService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private BusinessGroupService businessGroupService;
	
	public QTI21RetrieveTestsController(UserRequest ureq, WindowControl wControl, CourseEnvironment courseEnv,
			AssessmentToolOptions asOptions, IQTESTCourseNode courseNode) {
		super(ureq, wControl, "retrieve_tests");
		setTranslator(Util.createPackageTranslator(QTIResultManager.class, getLocale(), getTranslator()));

		this.courseNode = courseNode;
		identities = getIdentities(asOptions, courseEnv);
		sessions = qtiService
				.getRunningAssessmentTestSession(courseEnv.getCourseGroupManager().getCourseEntry(), courseNode.getIdent(), courseNode.getReferencedRepositoryEntry());
		
		initForm(ureq);
	}
	
	public QTI21RetrieveTestsController(UserRequest ureq, WindowControl wControl,
			AssessmentTestSession session, IQTESTCourseNode courseNode) {
		super(ureq, wControl, "retrieve_tests");
		setTranslator(Util.createPackageTranslator(QTIResultManager.class, getLocale(), getTranslator()));
		this.courseNode = courseNode;
		identities = Collections.singletonList(session.getIdentity());
		sessions = Collections.singletonList(session);
		initForm(ureq);
	}
	
	public QTI21RetrieveTestsController(UserRequest ureq, WindowControl wControl, RepositoryEntry  assessedEntry,
			AssessmentToolOptions asOptions) {
		super(ureq, wControl, "retrieve_tests");
		setTranslator(Util.createPackageTranslator(QTIResultManager.class, getLocale(), getTranslator()));
		this.assessedEntry = assessedEntry;
		identities = getIdentities(asOptions, null);
		sessions = qtiService.getRunningAssessmentTestSession(assessedEntry, null, assessedEntry);
		initForm(ureq);
	}
	
	private List<Identity> getIdentities(AssessmentToolOptions asOptions, CourseEnvironment courseEnv) {
		List<Identity> identityList;
		if(asOptions.getGroup() == null && asOptions.getIdentities() == null) {
			if(courseEnv != null) {
				identityList = ScoreAccountingHelper.loadUsers(courseEnv);
			} else {
				identityList = repositoryService.getMembers(assessedEntry, RepositoryEntryRelationType.entryAndCurriculums, GroupRoles.participant.name());
			}
		} else if (asOptions.getIdentities() != null) {
			identityList = asOptions.getIdentities();
		} else {
			identityList = businessGroupService.getMembers(asOptions.getGroup());
		}
		return identityList;
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		StringBuilder fullnames = new StringBuilder(256);

		List<AssessmentTestSession> sessionsToRetrieve = new ArrayList<>();
		Set<Identity> assessedIdentites = new HashSet<>(identities);
		for(AssessmentTestSession session:sessions) {
			if(assessedIdentites.contains(session.getIdentity())) {
				if(fullnames.length() > 0) fullnames.append(", ");
				String name = userManager.getUserDisplayName(session.getIdentity());
				if(StringHelper.containsNonWhitespace(name)) {
					fullnames.append(name);
					sessionsToRetrieve.add(session);
				}
			}
		}
		
		String msg;
		if(sessionsToRetrieve.size() == 0) {
			msg = translate("retrievetest.nothing.todo");
		} else if(sessionsToRetrieve.size() == 1) {
			msg = translate("retrievetest.confirm.text", new String[]{ fullnames.toString() });
		} else  {
			msg = translate("retrievetest.confirm.text.plural", new String[]{ fullnames.toString() });
		}
		if(formLayout instanceof FormLayoutContainer) {
			FormLayoutContainer layoutCont = (FormLayoutContainer)formLayout;
			layoutCont.contextPut("msg", msg);
		}
		
		uifactory.addFormCancelButton("cancel", formLayout, ureq, getWindowControl());
		uifactory.addFormSubmitButton("menu.retrieve.tests.title", formLayout);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void formOK(UserRequest ureq) {
		for(AssessmentTestSession session:sessions) {
			doRetrieveTest(session);
		}
		fireEvent(ureq, Event.DONE_EVENT);
	}

	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}

	private void doRetrieveTest(AssessmentTestSession session) {
		session = qtiService.getAssessmentTestSession(session.getKey());
		session = qtiService.pullSession(session, getSignatureOptions(session), getIdentity());
		if(courseNode != null) {
			RepositoryEntry courseEntry = session.getRepositoryEntry();
			CourseEnvironment courseEnv = CourseFactory.loadCourse(courseEntry).getCourseEnvironment();
			UserCourseEnvironment assessedUserCourseEnv = AssessmentHelper
					.createAndInitUserCourseEnvironment(session.getIdentity(), courseEnv);
			courseNode.pullAssessmentTestSession(session, assessedUserCourseEnv, getIdentity(), Role.coach);
		}
	}
	
	private DigitalSignatureOptions getSignatureOptions(AssessmentTestSession session) {
		if(courseNode == null) return null;
		
		RepositoryEntry testEntry = session.getTestEntry();
		RepositoryEntry courseEntry = session.getRepositoryEntry();
		QTI21DeliveryOptions deliveryOptions = qtiService.getDeliveryOptions(testEntry);
		
		boolean digitalSignature = deliveryOptions.isDigitalSignature();
		boolean sendMail = deliveryOptions.isDigitalSignatureMail();

		ModuleConfiguration config = courseNode.getModuleConfiguration();
		digitalSignature = config.getBooleanSafe(IQEditController.CONFIG_DIGITAL_SIGNATURE,
			deliveryOptions.isDigitalSignature());
		sendMail = config.getBooleanSafe(IQEditController.CONFIG_DIGITAL_SIGNATURE_SEND_MAIL,
			deliveryOptions.isDigitalSignatureMail());

		DigitalSignatureOptions options = new DigitalSignatureOptions(digitalSignature, sendMail, courseEntry, testEntry);
		if(digitalSignature) {
			CourseEnvironment courseEnv = CourseFactory.loadCourse(courseEntry).getCourseEnvironment();
			QTI21AssessmentRunController.decorateCourseConfirmation(session, options, courseEnv, courseNode, testEntry, null, getLocale());
		}
		return options;
	}
}