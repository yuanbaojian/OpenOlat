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
package org.olat.modules.assessment;

import java.math.BigDecimal;
import java.util.Date;

import org.olat.core.id.Identity;
import org.olat.modules.assessment.model.AssessmentEntryStatus;
import org.olat.modules.assessment.model.AssessmentRunStatus;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 20.07.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public interface AssessmentEntry extends AssessmentEntryLight {
	
	public Long getKey();
	
	public Date getCreationDate();
	
	public Date getLastModified();
	
	public Date getLastCoachModified();
	
	public void setLastCoachModified(Date date);
	
	public Date getLastUserModified();
	
	public void setLastUserModified(Date date);
	
	public Long getAssessmentId();
	
	public void setAssessmentId(Long assessmentId);
	
	public Integer getAttempts();

	public void setAttempts(Integer attempts);

	public void setScore(BigDecimal score);

	public void setPassed(Boolean passed);
	
	public Boolean getUserVisibility();
	
	public void setUserVisibility(Boolean visibility);
	
	public AssessmentEntryStatus getAssessmentStatus();
	
	public void setAssessmentStatus(AssessmentEntryStatus assessmentStatus);

	/**
	 * Used by Onyx
	 * @return 
	 */
	public Boolean getFullyAssessed();

	public void setFullyAssessed(Boolean fullyAssessed);

	public Double getCompletion();

	public void setCompletion(Double completion);
	
	/**
	 * @return Completion of the current running task (which can be temporary)
	 */
	public Double getCurrentRunCompletion();
	
	public void setCurrentRunCompletion(Double completion);
	
	public AssessmentRunStatus getCurrentRunStatus();
	
	public void setCurrentRunStatus(AssessmentRunStatus runStatus);

	public String getComment();

	public void setComment(String comment);
	
	public int getNumberOfAssessmentDocuments();
	
	public void setNumberOfAssessmentDocuments(int numOfDocuments);

	public String getCoachComment();

	public void setCoachComment(String coachComment);

	/**
	 * @return The course or learn resource where the user is assessed.
	 */
	public RepositoryEntry getRepositoryEntry();
	
	public String getSubIdent();
	
	/**
	 * @return The reference to the test
	 */
	public RepositoryEntry getReferenceEntry();
	
	public void setReferenceEntry(RepositoryEntry entry);
	
	/**
	 * @return The unique identifier for anonymous user (guest)
	 */
	public String getAnonymousIdentifier();

	/**
	 * If the anonymous identifier is set, this method return null, must return null.
	 * 
	 * @return The assessed identity
	 */
	public Identity getIdentity();

}
