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

package org.olat.course.run.preview;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.logging.AssertException;
import org.olat.core.manager.BasicManager;
import org.olat.core.util.event.GenericEventListener;
import org.olat.course.assessment.AssessmentManager;
import org.olat.course.nodes.AssessableCourseNode;
import org.olat.course.nodes.CourseNode;
import org.olat.course.run.scoring.ScoreEvaluation;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.group.BusinessGroup;
import org.olat.modules.assessment.AssessmentEntry;
import org.olat.modules.assessment.Role;
import org.olat.modules.assessment.model.AssessmentEntryStatus;
import org.olat.modules.assessment.model.AssessmentRunStatus;

/**
 * Initial Date:  08.02.2005
 *
 * @author Mike Stock
 */
final class PreviewAssessmentManager extends BasicManager implements AssessmentManager {
	private Map<String,Float> nodeScores = new HashMap<String,Float>();
	private Map<String,Boolean> nodePassed = new HashMap<String,Boolean>();
	private Map<String,Integer> nodeAttempts = new HashMap<String,Integer>();
	private Map<String,Long> nodeAssessmentID = new HashMap<String,Long>();

	/**
	 * @see org.olat.course.assessment.AssessmentManager#saveNodeScore(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity, org.olat.core.id.Identity, java.lang.Float)
	 */
	private void saveNodeScore(CourseNode courseNode, Float score) {
		nodeScores.put(courseNode.getIdent(), score);
	}

	@Override
	public List<AssessmentEntry> getAssessmentEntries(CourseNode courseNode) {
		return Collections.emptyList();
	}
	
	@Override
	public List<AssessmentEntry> getAssessmentEntriesWithStatus(CourseNode courseNode, AssessmentEntryStatus status, boolean excludeZeroScore) {
		return Collections.emptyList();
	}

	@Override
	public AssessmentEntry getAssessmentEntry(CourseNode courseNode, Identity assessedIdentity) {
		return null;
	}

	@Override
	public List<AssessmentEntry> getAssessmentEntries(Identity assessedIdentity) {
		return Collections.emptyList();
	}

	@Override
	public List<AssessmentEntry> getAssessmentEntries(BusinessGroup group, CourseNode courseNode) {
		return Collections.emptyList();
	}

	@Override
	public AssessmentEntry createAssessmentEntry(CourseNode courseNode, Identity assessedIdentity, ScoreEvaluation scoreEvaluation) {
		return null;
	}

	@Override
	public AssessmentEntry updateAssessmentEntry(AssessmentEntry assessmentEntry) {
		return assessmentEntry;
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#saveNodeAttempts(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity, org.olat.core.id.Identity, java.lang.Integer)
	 */
	@Override
	public void saveNodeAttempts(CourseNode courseNode, Identity identity, Identity assessedIdentity, Integer attempts, Role by) {
		nodeAttempts.put(courseNode.getIdent(), attempts);
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#saveNodeComment(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity, org.olat.core.id.Identity, java.lang.String)
	 */
	@Override
	public void saveNodeComment(CourseNode courseNode, Identity identity, Identity assessedIdentity, String comment) {
		throw new AssertException("Not implemented for preview.");
	}

	@Override
	public void addIndividualAssessmentDocument(CourseNode courseNode, Identity identity, Identity assessedIdentity,
			File document, String filename) {
		// do nothing
	}

	@Override
	public void removeIndividualAssessmentDocument(CourseNode courseNode, Identity identity, Identity assessedIdentity, File document) {
		// do nothing
	}

	@Override
	public void deleteIndividualAssessmentDocuments(CourseNode courseNode) {
		//
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#saveNodeCoachComment(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity, java.lang.String)
	 */
	@Override
	public void saveNodeCoachComment(CourseNode courseNode, Identity assessedIdentity, String comment) {
		throw new AssertException("Not implemented for preview.");
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#saveNodePassed(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity, org.olat.core.id.Identity, java.lang.Boolean)
	 */
	private void saveNodePassed(CourseNode courseNode, Boolean passed) {
		nodePassed.put(courseNode.getIdent(), passed);
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#incrementNodeAttempts(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity)
	 */
	@Override
	public void incrementNodeAttempts(CourseNode courseNode, Identity identity, UserCourseEnvironment userCourseEnvironment, Role by) {
		Integer attempts = nodeAttempts.get(courseNode.getIdent());
		if (attempts == null) attempts = new Integer(0);
		int iAttempts = attempts.intValue();
		iAttempts++;
		nodeAttempts.put(courseNode.getIdent(), new Integer(iAttempts));
	}
	
	/**
	 * @see org.olat.course.assessment.AssessmentManager#incrementNodeAttemptsInBackground(org.olat.course.nodes.CourseNode,
	 *      org.olat.core.id.Identity, org.olat.course.run.userview.UserCourseEnvironment)
	 */
	@Override
	public void incrementNodeAttemptsInBackground(CourseNode courseNode, Identity identity, UserCourseEnvironment userCourseEnvironment) {
		incrementNodeAttempts(courseNode, identity, userCourseEnvironment, Role.auto);
	}

	@Override
	public void updateCurrentCompletion(CourseNode courseNode, Identity assessedIdentity, UserCourseEnvironment userCourseEnvironment,
			Double currentCompletion, AssessmentRunStatus runStatus, Role by) {
		//
	}

	@Override
	public void updateLastModifications(CourseNode courseNode, Identity assessedIdentity, UserCourseEnvironment userCourseEnvironment, Role by) {
		//
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#getNodeScore(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity)
	 */
	@Override
	public Float getNodeScore(CourseNode courseNode, Identity identity) {
		return nodeScores.get(courseNode.getIdent());
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#getNodeComment(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity)
	 */
	@Override
	public String getNodeComment(CourseNode courseNode, Identity identity) {
		return "This is a preview"; //default comment for preview
	}
	
	@Override
	public List<File> getIndividualAssessmentDocuments(CourseNode courseNode, Identity identity) {
		return Collections.emptyList();
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#getNodeCoachComment(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity)
	 */
	@Override
	public String getNodeCoachComment(CourseNode courseNode, Identity identity) {
		return "This is a preview"; //default comment for preview
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#getNodePassed(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity)
	 */
	@Override
	public Boolean getNodePassed(CourseNode courseNode, Identity identity) {
		return nodePassed.get(courseNode.getIdent());
	}

	@Override
	public Boolean getNodeFullyAssessed(CourseNode courseNode, Identity identity) {
		return nodePassed.get(courseNode.getIdent());
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#getNodeAttempts(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity)
	 */
	@Override
	public Integer getNodeAttempts(CourseNode courseNode, Identity identity) {
		Integer attempts = nodeAttempts.get(courseNode.getIdent());
		return (attempts == null ? new Integer(0) : attempts);
	}

	@Override
	public Double getNodeCompletion(CourseNode courseNode, Identity identity) {
		return null;
	}

	@Override
	public Double getNodeCurrentRunCompletion(CourseNode courseNode, Identity identity) {
		return null;
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#registerForAssessmentChangeEvents(org.olat.core.util.event.GenericEventListener, org.olat.core.id.Identity)
	 */
	@Override
	public void registerForAssessmentChangeEvents(GenericEventListener gel, Identity identity) {
		throw new AssertException("Not implemented for preview.");
	}

	/**
	 * @see org.olat.course.assessment.AssessmentManager#deregisterFromAssessmentChangeEvents(org.olat.core.util.event.GenericEventListener)
	 */
	@Override
	public void deregisterFromAssessmentChangeEvents(GenericEventListener gel) {
		throw new AssertException("Not implemented for preview.");
	}

	/**
	 * 
	 * @see org.olat.course.assessment.AssessmentManager#saveAssessmentID(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity, java.lang.String)
	 */
	private void saveAssessmentID(CourseNode courseNode, Long assessmentID) {
		nodeAssessmentID.put(courseNode.getIdent(), assessmentID);
	}
	
	/**
	 * 
	 * @param courseNode
	 * @param identity
	 * @return
	 */
	public Long getAssessmentID(CourseNode courseNode, Identity identity) {
		return nodeAssessmentID.get(courseNode.getIdent());
	}

	@Override
	public Date getScoreLastModifiedDate(CourseNode courseNode, Identity identity) {
		return null;
	}

	/**
	 * 
	 * @see org.olat.course.assessment.AssessmentManager#saveScoreEvaluation(org.olat.course.nodes.CourseNode, org.olat.core.id.Identity, org.olat.core.id.Identity, org.olat.course.run.scoring.ScoreEvaluation)
	 */
	@Override
	public void saveScoreEvaluation(AssessableCourseNode courseNode, Identity identity, Identity assessedIdentity, ScoreEvaluation scoreEvaluation, 
			UserCourseEnvironment userCourseEnvironment, boolean incrementUserAttempts, Role by) {
		
		saveNodeScore(courseNode, scoreEvaluation.getScore());
		saveNodePassed(courseNode, scoreEvaluation.getPassed());
		saveAssessmentID(courseNode, scoreEvaluation.getAssessmentID());
		if(incrementUserAttempts) {
			incrementNodeAttempts(courseNode, identity, userCourseEnvironment, by);
		}
	}

	@Override
	public OLATResourceable createOLATResourceableForLocking(Identity assessedIdentity) {				
		throw new AssertException("Not implemented for preview.");
	}
}