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
package org.olat.modules.assessment.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.olat.basesecurity.IdentityImpl;
import org.olat.core.id.CreateInfo;
import org.olat.core.id.Identity;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;
import org.olat.core.util.StringHelper;
import org.olat.modules.assessment.AssessmentEntry;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 20.07.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Entity(name="assessmententry")
@Table(name="o_as_entry")
@NamedQueries({
	@NamedQuery(name="loadAssessmentEntryById",
		query="select data from assessmententry data where data.key=:key"),
	@NamedQuery(name="loadAssessmentEntryByRepositoryEntryAndSubIdent",
		query="select data from assessmententry data where data.repositoryEntry.key=:repositoryEntryKey and data.subIdent=:subIdent")
})
public class AssessmentEntryImpl implements Persistable, ModifiedInfo, CreateInfo, AssessmentEntry {
	
	private static final long serialVersionUID = 2934783777645549412L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true, insertable=true, updatable=false)
	private Long key;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="creationdate", nullable=false, insertable=true, updatable=false)
	private Date creationDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lastmodified", nullable=false, insertable=true, updatable=true)
	private Date lastModified;
	
	@Column(name="lastcoachmodified", nullable=true, insertable=true, updatable=true)
	private Date lastCoachModified;
	@Column(name="lastusermodified", nullable=true, insertable=true, updatable=true)
	private Date lastUserModified;

	@Column(name="a_attemtps", nullable=true, insertable=true, updatable=true)
	private Integer attempts;
	@Column(name="a_score", nullable=true, insertable=true, updatable=true)
	private BigDecimal score;
	@Column(name="a_passed", nullable=true, insertable=true, updatable=true)
	private Boolean passed;
	@Column(name="a_status", nullable=true, insertable=true, updatable=true)
	private String status;
	@Column(name="a_details", nullable=true, insertable=true, updatable=true)
	private String details;
	@Column(name="a_user_visibility", nullable=true, insertable=true, updatable=true)
	private Boolean userVisibility;

	@Column(name="a_completion", nullable=true, insertable=true, updatable=true)
	private Double completion;
	@Column(name="a_current_run_completion", nullable=true, insertable=true, updatable=true)
	private Double currentRunCompletion;
	@Column(name="a_current_run_status", nullable=true, insertable=true, updatable=true)
	private String runStatus;

	@Column(name="a_num_assessment_docs", nullable=true, insertable=true, updatable=true)
	private int numberOfAssessmentDocuments;
	@Column(name="a_comment", nullable=true, insertable=true, updatable=true)
	private String comment;
	@Column(name="a_coach_comment", nullable=true, insertable=true, updatable=true)
	private String coachComment;
	
	// fully passed and assessment id are only for onyx
	@Column(name="a_fully_assessed", nullable=true, insertable=true, updatable=true)
	private Boolean fullyAssessed;
	@Column(name="a_assessment_id", nullable=true, insertable=true, updatable=true)
	private Long assessmentId;
	
	@ManyToOne(targetEntity=RepositoryEntry.class,fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="fk_entry", nullable=false, insertable=true, updatable=false)
    private RepositoryEntry repositoryEntry;
	
    @Column(name="a_subident", nullable=true, insertable=true, updatable=false)
	private String subIdent;
	
	@ManyToOne(targetEntity=RepositoryEntry.class,fetch=FetchType.LAZY,optional=true)
	@JoinColumn(name="fk_reference_entry", nullable=true, insertable=true, updatable=true)
    private RepositoryEntry referenceEntry;

    @Column(name="a_anon_identifier", nullable=true, insertable=true, updatable=false)
	private String anonymousIdentifier;

	@ManyToOne(targetEntity=IdentityImpl.class,fetch=FetchType.LAZY,optional=true)
	@JoinColumn(name="fk_identity", nullable=true, insertable=true, updatable=false)
    private Identity identity;
	
	public AssessmentEntryImpl() {
		//
	}
	
	@Override
	public Long getKey() {
		return key;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public Date getLastCoachModified() {
		return lastCoachModified;
	}

	public void setLastCoachModified(Date lastCoachModified) {
		this.lastCoachModified = lastCoachModified;
	}

	@Override
	public Date getLastUserModified() {
		return lastUserModified;
	}

	public void setLastUserModified(Date lastUserModified) {
		this.lastUserModified = lastUserModified;
	}

	@Override
	public Integer getAttempts() {
		return attempts;
	}

	@Override
	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}

	@Override
	public BigDecimal getScore() {
		return score;
	}

	@Override
	public void setScore(BigDecimal score) {
		this.score = score;
	}

	@Override
	public Boolean getPassed() {
		return passed;
	}

	@Override
	public void setPassed(Boolean passed) {
		this.passed = passed;
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public AssessmentEntryStatus getAssessmentStatus() {
		return StringHelper.containsNonWhitespace(status) ? AssessmentEntryStatus.valueOf(status) : null;
	}

	@Override
	public void setAssessmentStatus(AssessmentEntryStatus assessmentStatus) {
		if(assessmentStatus == null) {
			this.status = null;
		} else {
			this.status = assessmentStatus.name();
		}
	}

	@Override
	public Boolean getUserVisibility() {
		return userVisibility;
	}

	@Override
	public void setUserVisibility(Boolean visibility) {
		this.userVisibility = visibility;
	}

	@Override
	public Boolean getFullyAssessed() {
		return fullyAssessed;
	}

	@Override
	public void setFullyAssessed(Boolean fullyAssessed) {
		this.fullyAssessed = fullyAssessed;
	}

	@Override
	public Double getCompletion() {
		return completion;
	}

	@Override
	public void setCompletion(Double completion) {
		this.completion = completion;
	}

	@Override
	public Double getCurrentRunCompletion() {
		return currentRunCompletion;
	}

	@Override
	public void setCurrentRunCompletion(Double currentCompletion) {
		this.currentRunCompletion = currentCompletion;
	}
	
	public String getRunStatus() {
		return runStatus;
	}
	
	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}

	@Override
	@Transient
	public AssessmentRunStatus getCurrentRunStatus() {
		return StringHelper.containsNonWhitespace(runStatus) ? AssessmentRunStatus.valueOf(runStatus) : null;
	}

	@Override
	public void setCurrentRunStatus(AssessmentRunStatus status) {
		if(status == null) {
			runStatus = null;
		} else {
			runStatus = status.name();
		}
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public int getNumberOfAssessmentDocuments() {
		return numberOfAssessmentDocuments;
	}

	@Override
	public void setNumberOfAssessmentDocuments(int numOfDocuments) {
		numberOfAssessmentDocuments = numOfDocuments;
	}

	@Override
	public String getCoachComment() {
		return coachComment;
	}

	@Override
	public void setCoachComment(String coachComment) {
		this.coachComment = coachComment;
	}

	@Override
	public Long getAssessmentId() {
		return assessmentId;
	}

	@Override
	public void setAssessmentId(Long assessmentId) {
		this.assessmentId = assessmentId;
	}

	@Override
	public RepositoryEntry getRepositoryEntry() {
		return repositoryEntry;
	}
	
	public void setRepositoryEntry(RepositoryEntry entry) {
		this.repositoryEntry = entry;
	}

	@Override
	public String getSubIdent() {
		return subIdent;
	}

	public void setSubIdent(String subIdent) {
		this.subIdent = subIdent;
	}

	@Override
	public RepositoryEntry getReferenceEntry() {
		return referenceEntry;
	}

	public void setReferenceEntry(RepositoryEntry referenceEntry) {
		this.referenceEntry = referenceEntry;
	}

	public String getAnonymousIdentifier() {
		return anonymousIdentifier;
	}

	public void setAnonymousIdentifier(String identifier) {
		this.anonymousIdentifier = identifier;
	}

	@Override
	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	@Override
	public int hashCode() {
		return key == null ? -864687 : key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof AssessmentEntryImpl) {
			AssessmentEntryImpl session = (AssessmentEntryImpl)obj;
			return getKey() != null && getKey().equals(session.getKey());
		}
		return false;
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		return equals(persistable);
	}
}
