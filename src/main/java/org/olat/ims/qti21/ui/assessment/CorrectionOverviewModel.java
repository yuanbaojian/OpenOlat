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
package org.olat.ims.qti21.ui.assessment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.olat.core.CoreSpringFactory;
import org.olat.core.id.Identity;
import org.olat.course.CourseFactory;
import org.olat.course.nodes.IQTESTCourseNode;
import org.olat.course.run.environment.CourseEnvironment;
import org.olat.ims.qti21.AssessmentTestSession;
import org.olat.ims.qti21.QTI21Service;
import org.olat.ims.qti21.model.xml.ManifestBuilder;
import org.olat.ims.qti21.model.xml.ManifestMetadataBuilder;
import org.olat.repository.RepositoryEntry;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.DrawingInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ExtendedTextInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UploadInteraction;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;

/**
 * 
 * Initial date: 26 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class CorrectionOverviewModel {
	
	private CourseEnvironment courseEnv;
	
	private final RepositoryEntry testEntry;
	private final RepositoryEntry courseEntry;
	private final IQTESTCourseNode courseNode;
	private final List<Identity> assessedIdentities;
	private final ManifestBuilder manifestBuilder;
	private final ResolvedAssessmentTest resolvedAssessmentTest;
	private final Map<Identity, TestSessionState> testSessionStates;
	private final Map<String,Boolean> manualCorrections = new ConcurrentHashMap<>();

	private Map<Identity,AssessmentTestSession> lastSessions;
	private final Map<AssessmentTestSession,Identity> reversedLastSessions = new HashMap<>();
	
	private final QTI21Service qtiService;
	
	public CorrectionOverviewModel(RepositoryEntry courseEntry, IQTESTCourseNode courseNode, RepositoryEntry testEntry,
			ResolvedAssessmentTest resolvedAssessmentTest, ManifestBuilder manifestBuilder,
			Map<Identity,AssessmentTestSession> lastSessions, Map<Identity, TestSessionState> testSessionStates) {
		qtiService = CoreSpringFactory.getImpl(QTI21Service.class);
		this.courseEntry = courseEntry;
		this.courseNode = courseNode;
		this.testEntry = testEntry;
		this.manifestBuilder = manifestBuilder;
		this.resolvedAssessmentTest = resolvedAssessmentTest;
		this.lastSessions = lastSessions;
		this.testSessionStates = testSessionStates;
		assessedIdentities = new ArrayList<>(lastSessions.keySet());
		
		for(Map.Entry<Identity, AssessmentTestSession> entry:lastSessions.entrySet()) {
			reversedLastSessions.put(entry.getValue(), entry.getKey());
		}
	}
	
	public CorrectionOverviewModel(RepositoryEntry courseEntry, IQTESTCourseNode courseNode, RepositoryEntry testEntry,
			ResolvedAssessmentTest resolvedAssessmentTest, ManifestBuilder manifestBuilder,
			List<Identity> assessedIdentities) {
		qtiService = CoreSpringFactory.getImpl(QTI21Service.class);
		this.courseEntry = courseEntry;
		this.courseNode = courseNode;
		this.testEntry = testEntry;
		this.manifestBuilder = manifestBuilder;
		this.resolvedAssessmentTest = resolvedAssessmentTest;
		this.assessedIdentities = new ArrayList<>(assessedIdentities);
		lastSessions = loadLastSessions();
		testSessionStates = getTestSessionStates(lastSessions);
	}

	public String getSubIdent() {
		return courseNode == null ? null : courseNode.getIdent();
	}
	
	public IQTESTCourseNode getCourseNode() {
		return courseNode;
	}
	
	public CourseEnvironment getCourseEnvironment() {
		if(courseEnv != null) {
			return courseEnv;
		}
		
		if(courseEntry != null && "CourseModule".equals(courseEntry.getOlatResource().getResourceableTypeName())) {
			courseEnv = CourseFactory.loadCourse(courseEntry).getCourseEnvironment();
		}
		return courseEnv;
	}

	public RepositoryEntry getTestEntry() {
		return testEntry;
	}

	public RepositoryEntry getCourseEntry() {
		return courseEntry;
	}

	public ResolvedAssessmentTest getResolvedAssessmentTest() {
		return resolvedAssessmentTest;
	}
	
	public List<Identity> getAssessedIdentities() {
		return assessedIdentities;
	}
	
	public int getNumberOfAssessedIdentities() {
		return assessedIdentities == null ? 0 : assessedIdentities.size();
	}

	public Map<Identity, AssessmentTestSession> getLastSessions() {
		return lastSessions;
	}
	
	public void updateLastSession(Identity identity, AssessmentTestSession lastSession) {
		lastSessions.put(identity, lastSession);
		reversedLastSessions.put(lastSession, identity);
	}
	
	public void setLastSessions(Map<Identity, AssessmentTestSession> lastSessions) {
		this.lastSessions = lastSessions;
		
		reversedLastSessions.clear();
		for(Map.Entry<Identity, AssessmentTestSession> entry:lastSessions.entrySet()) {
			reversedLastSessions.put(entry.getValue(), entry.getKey());
		}
	}
	
	public Map<AssessmentTestSession, Identity> getReversedLastSessions() {
		return reversedLastSessions;
	}
	
	public Map<Identity,AssessmentTestSession> loadLastSessions() {
		Set<Identity> identitiesSet = new HashSet<>(assessedIdentities);
		List<AssessmentTestSession> sessions = CoreSpringFactory.getImpl(QTI21Service.class)
				.getAssessmentTestSessions(courseEntry, getSubIdent(), testEntry);
		Map<Identity,AssessmentTestSession> identityToSessions = new HashMap<>();
		for(AssessmentTestSession session:sessions) {
			//filter last session / user
			Identity assessedIdentity = session.getIdentity();
			if(!identitiesSet.contains(assessedIdentity)) {
				continue;
			}
			
			Date fDate = session.getFinishTime();
			if(fDate == null) {
				//not terminated
			} else {
				if(identityToSessions.containsKey(assessedIdentity)) {
					AssessmentTestSession currentSession = identityToSessions.get(assessedIdentity);

					Date currentFDate = currentSession.getFinishTime();
					if(fDate.after(currentFDate)) {
						identityToSessions.put(assessedIdentity, session);
					}
				} else {
					identityToSessions.put(assessedIdentity, session);
				}
			}	
		}
		setLastSessions(identityToSessions);
		return identityToSessions;
	}
	
	private Map<Identity, TestSessionState> getTestSessionStates(Map<Identity,AssessmentTestSession> sessions) {
		Map<Identity, TestSessionState> identityToStates = new HashMap<>();
		for(Map.Entry<Identity, AssessmentTestSession> entry:sessions.entrySet()) {
			TestSessionState sessionState = qtiService.loadTestSessionState(entry.getValue());
			if(sessionState != null) {
				identityToStates.put(entry.getKey(), sessionState);
			}
		}
		return identityToStates;
	}
	
	public Map<Identity, TestSessionState> getTestSessionStates() {
		return testSessionStates;
	}

	public boolean isManualCorrection(AssessmentItemRef itemRef) {
		String identifier = itemRef.getIdentifier().toString();
		return manualCorrections.computeIfAbsent(identifier, id -> {
			ResolvedAssessmentItem resolvedAssessmentItem = resolvedAssessmentTest.getResolvedAssessmentItem(itemRef);
			AssessmentItem item = resolvedAssessmentItem.getRootNodeLookup().extractIfSuccessful();
			List<Interaction> interactions = item.getItemBody().findInteractions();
			for(Interaction interaction:interactions) {
				if(interaction instanceof UploadInteraction
						|| interaction instanceof DrawingInteraction
						|| interaction instanceof ExtendedTextInteraction) {
					return true;
				}
			}
			return false;
		});
	}
	
	public ManifestMetadataBuilder getMetadata(AssessmentItemRef itemRef) {
		return manifestBuilder.getResourceBuilderByHref(itemRef.getHref().toString());
	}
}