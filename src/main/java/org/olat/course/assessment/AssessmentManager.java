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

package org.olat.course.assessment;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.util.event.GenericEventListener;
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
 * Description:<BR>
 * The assessment manager is used by the assessable course nodes to store
 * and retrieve user assessment data from the database. The assessment Manager
 * should not be used directly from the controllers but only via the assessable
 * course nodes interface.<BR>
 * Exception are nodes that want to save or get node attempts variables for nodes
 * that are not assessable nodes (e.g. questionnaire)
 * 
 * <P>
 * Initial Date:  Nov 24, 2004
 *
 * @author gnaegi 
 */
public interface AssessmentManager {

	/**
	 * Save the users attempts for this node. If there is already an attempts property available, it will be
	 * overwritten with the new value
	 * @param courseNode
	 * @param identity The user who changes this score
	 * @param assessedIdentity The user whose score is changed
	 * @param attempts The new attempts
	 */
	public void saveNodeAttempts(CourseNode courseNode, Identity identity, Identity assessedIdentity, Integer attempts, Role by);
	
	/**
	 * Change the last modification dates.
	 * 
	 * @param courseNode The course node
	 * @param identity The identity which does the action
	 * @param assessedIdentity The assessed identity
	 * @param by The role of the identity which does the action.
	 */
	public void updateLastModifications(CourseNode courseNode, Identity assessedIdentity, UserCourseEnvironment userCourseEnvironment, Role by);

	/**
	 * Change the current completion.
	 * 
	 * @param courseNode The course node
	 * @param identity The identity which does the action
	 * @param assessedIdentity The assessed identity
	 * @param currentCompletion The completion of the current running taks
	 * @param by The role of the identity which does the action.
	 */
	public void updateCurrentCompletion(CourseNode courseNode, Identity assessedIdentity, UserCourseEnvironment userCourseEnvironment,
			Double currentCompletion, AssessmentRunStatus status, Role by);

	/**
	 * Save an assessment comment for this node for a user. If there is already a comment property available, 
	 * it will be overwritten with the new value
	 * @param courseNode
	 * @param identity The user who changes this comment
	 * @param assessedIdentity The user whose comment is changed
	 * @param comment
	 */
	public void saveNodeComment(CourseNode courseNode, Identity identity, Identity assessedIdentity, String comment);
	
	/**
	 * Add an individual document for assessment purpose
	 * 
	 * @param courseNode The course element
	 * @param identity The user who add the document
	 * @param assessedIdentity The assessed user
	 * @param document The document
	 */
	public void addIndividualAssessmentDocument(CourseNode courseNode, Identity identity, Identity assessedIdentity, File document, String filename);
	
	/**
	 * Remove a document
	 * 
	 * @param courseNode The course element
	 * @param identity The user who delete the document
	 * @param assessedIdentity The assessed user
	 * @param document The document to delete
	 */
	public void removeIndividualAssessmentDocument(CourseNode courseNode, Identity identity, Identity assessedIdentity, File document);

	/**
	 * Save an coach comment for this node for a user. If there is already a coach comment property available, 
	 * it will be overwritten with the new value.
	 * @param courseNode
	 * @param assessedIdentity The user whose coach comment is changed
	 * @param comment
	 */
	public void saveNodeCoachComment(CourseNode courseNode, Identity assessedIdentity, String comment);

	
	/**
	 * Increment the users attempts for this course node. 
	 * @param courseNode
	 * @param identity
	 */
	public void incrementNodeAttempts(CourseNode courseNode, Identity identity, UserCourseEnvironment userCourseEnvironment, Role by);
	
	/**
	 * Increment the users attempts for this course node, but without logging (aimed at background job).
	 * @param courseNode
	 * @param identity
	 * @param userCourseEnvironment
	 */
	public void incrementNodeAttemptsInBackground(CourseNode courseNode, Identity identity, UserCourseEnvironment userCourseEnvironment);

	/**
	 * @param courseNode The course node
	 * @param identity The identity 
	 * @return The achieved score or null if no score available
	 */
	public Float getNodeScore(CourseNode courseNode, Identity identity);

	/**
	 * @param courseNode The course node
	 * @param identity The identity 
	 * @return The achieved passed or null if no passed available
	 */
	public String getNodeComment(CourseNode courseNode, Identity identity);
	
	/**
	 * @param courseNode The course node
	 * @param identity The assessed identity.
	 * @return A list of documents
	 */
	public List<File> getIndividualAssessmentDocuments(CourseNode courseNode, Identity identity);
	
	/**
	 * 
	 * @param courseNode
	 */
	public void deleteIndividualAssessmentDocuments(CourseNode courseNode);

	/**
	 * @param courseNode The course node
	 * @param identity The identity 
	 * @return The coach comment or null if no coach comment available
	 */
	public String getNodeCoachComment(CourseNode courseNode, Identity identity);

	/**
	 * @param courseNode The course node
	 * @param identity The identity 
	 * @return whether passed or not, or null if there is no info yet
	 */
	public Boolean getNodePassed(CourseNode courseNode, Identity identity);

	/**
	 * @param courseNode The course node
	 * @param identity The identity 
	 * @return The number of attempts. If no Property is set, the method will return 0
	 */
	public Integer getNodeAttempts(CourseNode courseNode, Identity identity);
	

	public Double getNodeCompletion(CourseNode courseNode, Identity identity);

	/**
	 * @param courseNode The course element
	 * @param identity The identity
	 * @return The completion of the current running task which is not committed.
	 */
	public Double getNodeCurrentRunCompletion(CourseNode courseNode, Identity identity);

	/**
	 * Register the given event listener for all assessment changed events of this course
	 * @param gel
	 * @param identity
	 */
	public void registerForAssessmentChangeEvents(GenericEventListener gel, Identity identity);
	
	/**
	 * Deregister the given event listener from all assessment changed events of this course
	 * @param gel
	 */
	public void deregisterFromAssessmentChangeEvents(GenericEventListener gel);
	
		
	/**
	 * If this returns null, try get the assessmentID via the IQManager.getLastAssessmentID().
	 * @param courseNode
	 * @param identity
	 * @return null if none found
	 */
	public Long getAssessmentID(CourseNode courseNode, Identity identity);
	
	/**
	 * 
	 * @param courseNode
	 * @param identity
	 * @return
	 */
	public Date getScoreLastModifiedDate(CourseNode courseNode, Identity identity);

	/**
	 * Save the users achieved ScoreEvaluation for this node. If there is already a score property available, it will be
	 * overwritten with the new value.
	 * 
	 * @param courseNode The course element
	 * @param identity The identity who make the changes
	 * @param assessedIdentity The assessed identity
	 * @param scoreEvaluation The updated score evaluation
	 * @param userCourseEnvironment The user course env. of the assessed identity
	 * @param incrementUserAttempts
	 */
	
	public void saveScoreEvaluation(AssessableCourseNode courseNode, Identity identity, Identity assessedIdentity,
			ScoreEvaluation scoreEvaluation, UserCourseEnvironment userCourseEnvironment,
			boolean incrementUserAttempts, Role by);
	
	/**
	 * Provides an OLATResourceable for locking (of score/passed etc.) purposes (if doInSync is called on score/passed data)
	 * Or provides a lock token for assessment data of the assessedIdentity.
	 * @param assessedIdentity
	 * @return
	 */
	public OLATResourceable createOLATResourceableForLocking(Identity assessedIdentity);
	
	/**
	 * @param courseNode
	 *            The course node
	 * @param identity
	 *            The identity
	 * @return True if this node has been fully assessed, false if it had been
	 *         set to not fully assessed, null if no info is available
	 */
	public Boolean getNodeFullyAssessed(CourseNode courseNode, Identity identity);
	

	public AssessmentEntry getAssessmentEntry(CourseNode courseNode, Identity assessedIdentity);
	
	public AssessmentEntry createAssessmentEntry(CourseNode courseNode, Identity assessedIdentity, ScoreEvaluation scoreEvaluation);
	
	public AssessmentEntry updateAssessmentEntry(AssessmentEntry assessmentEntry);

	public List<AssessmentEntry> getAssessmentEntries(CourseNode courseNode);
	
	public List<AssessmentEntry> getAssessmentEntriesWithStatus(CourseNode courseNode, AssessmentEntryStatus status, boolean excludeZeroScore);
	
	public List<AssessmentEntry> getAssessmentEntries(BusinessGroup group, CourseNode courseNode);
	
	public List<AssessmentEntry> getAssessmentEntries(Identity assessedIdentity);

	
}