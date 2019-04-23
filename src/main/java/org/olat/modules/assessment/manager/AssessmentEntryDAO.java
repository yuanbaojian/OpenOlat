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
package org.olat.modules.assessment.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.olat.basesecurity.Group;
import org.olat.basesecurity.IdentityRef;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.modules.assessment.AssessmentEntry;
import org.olat.modules.assessment.model.AssessmentEntryImpl;
import org.olat.modules.assessment.model.AssessmentEntryStatus;
import org.olat.modules.vitero.model.GroupRole;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryEntryRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 20.07.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class AssessmentEntryDAO {
	
	@Autowired
	private DB dbInstance;
	
	public AssessmentEntry createAssessmentEntry(Identity assessedIdentity, String anonymousIdentifier,
			RepositoryEntry entry, String subIdent, RepositoryEntry referenceEntry) {
		
		AssessmentEntryImpl data = new AssessmentEntryImpl();
		data.setCreationDate(new Date());
		data.setLastModified(data.getCreationDate());
		data.setIdentity(assessedIdentity);
		data.setAnonymousIdentifier(anonymousIdentifier);
		data.setRepositoryEntry(entry);
		data.setSubIdent(subIdent);
		data.setReferenceEntry(referenceEntry);
		data.setUserVisibility(Boolean.TRUE);
		dbInstance.getCurrentEntityManager().persist(data);
		return data;
	}
	
	public AssessmentEntry createAssessmentEntry(Identity assessedIdentity, String anonymousIdentifier,
			RepositoryEntry entry, String subIdent, RepositoryEntry referenceEntry,
			Float score, Boolean passed, Date lastUserModified, Date lastCoachModified) {
		
		AssessmentEntryImpl data = new AssessmentEntryImpl();
		data.setCreationDate(new Date());
		data.setLastModified(data.getCreationDate());
		data.setLastUserModified(lastUserModified);
		data.setLastCoachModified(lastCoachModified);
		data.setIdentity(assessedIdentity);
		data.setAnonymousIdentifier(anonymousIdentifier);
		data.setRepositoryEntry(entry);
		data.setSubIdent(subIdent);
		data.setReferenceEntry(referenceEntry);
		data.setUserVisibility(Boolean.TRUE);
		if(score != null) {
			data.setScore(new BigDecimal(score));
		}
		data.setPassed(passed);
		dbInstance.getCurrentEntityManager().persist(data);
		return data;
	}
	
	public AssessmentEntry loadAssessmentEntryById(Long id) {
		List<AssessmentEntry> nodeAssessment = dbInstance.getCurrentEntityManager()
				.createNamedQuery("loadAssessmentEntryById", AssessmentEntry.class)
				.setParameter("key", id)
				.getResultList();
		return nodeAssessment.isEmpty() ? null : nodeAssessment.get(0);
	}

	public AssessmentEntry loadAssessmentEntry(IdentityRef assessedIdentity, String anonymousIdentifier, RepositoryEntryRef entry, String subIdent) {
		StringBuilder sb = new StringBuilder();
		sb.append("select data from assessmententry data")
		  .append(" left join fetch data.identity ident")
		  .append(" where data.repositoryEntry.key=:repositoryEntryKey");

		if(subIdent != null) {
			sb.append(" and data.subIdent=:subIdent");
		} else {
			sb.append(" and data.subIdent is null");
		}
		
		if(anonymousIdentifier != null) {
			sb.append(" and data.anonymousIdentifier=:anonymousIdentifier");
		} else {
			sb.append(" and data.identity.key=:identityKey")
			  .append(" and data.anonymousIdentifier is null");
		}

		TypedQuery<AssessmentEntry> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentEntry.class)
				.setParameter("repositoryEntryKey", entry.getKey());
		if(subIdent != null) {
			query.setParameter("subIdent", subIdent);
		} 
		if(anonymousIdentifier != null) {
			query.setParameter("anonymousIdentifier", anonymousIdentifier);
		} else {
			query.setParameter("identityKey", assessedIdentity.getKey());
		}

		List<AssessmentEntry> entries = query.getResultList();
		return entries.isEmpty() ? null : entries.get(0);
	}
	
	public AssessmentEntry loadAssessmentEntry(IdentityRef assessedIdentity, RepositoryEntryRef entry, String subIdent) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("select data from assessmententry data");
		sb.append(" where data.repositoryEntry.key=:repositoryEntryKey and data.identity.key=:identityKey");
		if(subIdent != null) {
			sb.append(" and data.subIdent=:subIdent");
		} else {
			sb.append(" and data.subIdent is null");
		}

		TypedQuery<AssessmentEntry> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentEntry.class)
				.setParameter("repositoryEntryKey", entry.getKey())
				.setParameter("identityKey", assessedIdentity.getKey());
		if(subIdent != null) {
			query.setParameter("subIdent", subIdent);
		}
		List<AssessmentEntry> entries = query.getResultList();
		return entries.isEmpty() ? null : entries.get(0);
	}
	
	public AssessmentEntry loadAssessmentEntry(IdentityRef assessedIdentity, RepositoryEntryRef entry, String subIdent, RepositoryEntryRef referenceEntry) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("select data from assessmententry data");
		if(referenceEntry != null) {
			sb.append(" inner join data.referenceEntry referenceEntry");
		}
		
		sb.append(" where data.repositoryEntry.key=:repositoryEntryKey and data.identity.key=:identityKey");
		if(subIdent != null) {
			sb.append(" and data.subIdent=:subIdent");
		} else {
			sb.append(" and data.subIdent is null");
		}
		
		if(referenceEntry != null) {
			sb.append(" and referenceEntry.key=:referenceEntryKey");
		} else {
			sb.append(" and data.referenceEntry is null");
		}

		TypedQuery<AssessmentEntry> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentEntry.class)
				.setParameter("repositoryEntryKey", entry.getKey())
				.setParameter("identityKey", assessedIdentity.getKey());
		if(subIdent != null) {
			query.setParameter("subIdent", subIdent);
		}
		if(referenceEntry != null) {
			query.setParameter("referenceEntryKey", referenceEntry.getKey());
		}
		List<AssessmentEntry> entries = query.getResultList();
		return entries.isEmpty() ? null : entries.get(0);
	}
	
	public AssessmentEntry resetAssessmentEntry(AssessmentEntry nodeAssessment) {
		nodeAssessment.setScore(null);
		nodeAssessment.setPassed(null);
		nodeAssessment.setAttempts(0);
		nodeAssessment.setCompletion(null);
		nodeAssessment.setAssessmentStatus(AssessmentEntryStatus.notStarted);
		((AssessmentEntryImpl)nodeAssessment).setLastModified(new Date());
		return dbInstance.getCurrentEntityManager().merge(nodeAssessment);
	}
	
	public AssessmentEntry updateAssessmentEntry(AssessmentEntry nodeAssessment) {
		((AssessmentEntryImpl)nodeAssessment).setLastModified(new Date());
		return dbInstance.getCurrentEntityManager().merge(nodeAssessment);
	}
	
	/**
	 * Load all assessment entries for the specific assessed repository entry with
	 * the specific sub identifier (it is mandatory). The anonym users are excluded
	 * by the query.
	 * 
	 * @param entry The entry (mandatory)
	 * @param subIdent The subIdent (mandatory)
	 * @return A list of assessment entries
	 */
	public List<AssessmentEntry> loadAssessmentEntryBySubIdent(RepositoryEntryRef entry, String subIdent) {
		StringBuilder sb = new StringBuilder();
		sb.append("select data from assessmententry data ")
		   .append(" inner join fetch data.identity ident") 
		   .append(" inner join fetch ident.user identuser")
		   .append(" where data.repositoryEntry.key=:repositoryEntryKey and data.subIdent=:subIdent");

		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentEntry.class)
				.setParameter("repositoryEntryKey", entry.getKey())
				.setParameter("subIdent", subIdent)
				.getResultList();
	}
	
	/**
	 * Load all assessment entries for the specific assessed repository entry with
	 * the specific sub identifier (it is mandatory). The anonym users are excluded
	 * by the query. The status of the assessment entry is optional 
	 * 
	 * @param entry The entry (mandatory)
	 * @param subIdent The subIdent (mandatory)
	 * @param status The status of the assessment entry (optional)
	 * @param excludeZeroScore disallow zero (0) scores
	 * @return A list of assessment entries
	 */
	public List<AssessmentEntry> loadAssessmentEntryBySubIdentWithStatus(RepositoryEntryRef entry, String subIdent,
			AssessmentEntryStatus status, boolean excludeZeroScore) {
		StringBuilder sb = new StringBuilder();
		sb.append("select data from assessmententry data ")
		   .append(" inner join fetch data.identity ident") 
		   .append(" inner join fetch ident.user identuser")
		   .append(" where data.repositoryEntry.key=:repositoryEntryKey")
		   .append(" and data.subIdent=:subIdent")
		   .append(" and data.userVisibility is true")
		   .append(" and data.score is not null")
		   .append(" and ident.key in ( select membership.identity.key from repoentrytogroup as rel, bgroupmember membership ")
		   .append(" where rel.entry.key=:repositoryEntryKey and rel.group.key=membership.group.key and membership.role='")
		   .append(GroupRole.participant).append("'")
		   .append(" )");
		
		if (status != null) {
			sb.append(" and data.status=:status");
		}		
		if(excludeZeroScore) {
			sb.append(" and data.score > 0");
		}
		
		TypedQuery<AssessmentEntry> typedQuery = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentEntry.class)
				.setParameter("repositoryEntryKey", entry.getKey())
				.setParameter("subIdent", subIdent);
		
		if (status != null) {
			typedQuery.setParameter("status", status.name());	
		}	
		
		return typedQuery.getResultList();
	}
	
	public List<Identity> getAllIdentitiesWithAssessmentData(RepositoryEntryRef entry) {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct data.identity from assessmententry data where data.repositoryEntry.key=:repositoryEntryKey");
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Identity.class)
				.setParameter("repositoryEntryKey", entry.getKey())
				.getResultList();
	}

	/**
	 * Load all the assessment entries for a specific user and a specific assessed repository entry
	 * (typically a course).
	 * 
	 * @param assessedIdentity The assessed user
	 * @param entry The assessed course / repository entry
	 * @return A list of assessment entries
	 */
	public List<AssessmentEntry> loadAssessmentEntriesByAssessedIdentity(Identity assessedIdentity, RepositoryEntry entry) {
		StringBuilder sb = new StringBuilder();
		sb.append("select data from assessmententry data where data.repositoryEntry.key=:repositoryEntryKey and data.identity.key=:identityKey");
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentEntry.class)
				.setParameter("repositoryEntryKey", entry.getKey())
				.setParameter("identityKey", assessedIdentity.getKey())
				.getResultList();
	}

	/**
	 * Load all the assessment entry of the specific group. But aware that the query exclude the default group
	 * of the repository entry! The query doesn't check the member ship but only the relation to the course.
	 * 
	 * @param assessedGroup The group (mandatory)
	 * @param entry The repository entry (mandatory)
	 * @param subIdent The sub identifier (mandatory)
	 * @return A list of assessment entries
	 */
	public List<AssessmentEntry> loadAssessmentEntryByGroup(Group assessedGroup, RepositoryEntry entry, String subIdent) {
		StringBuilder sb = new StringBuilder();
		sb.append("select data from assessmententry data")
		  .append(" inner join data.repositoryEntry v")
		  .append(" inner join v.groups as relGroup on relGroup.defaultGroup=false")
		  .append(" where relGroup.group.key=:groupKey and v.key=:repositoryEntryKey and data.subIdent=:subIdent");
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentEntry.class)
				.setParameter("repositoryEntryKey", entry.getKey())
				.setParameter("groupKey", assessedGroup.getKey())
				.setParameter("subIdent", subIdent)
				.getResultList();
	}
	
	/**
	 * Delete all the entry where the specified repository entry is
	 * referenced as a test.
	 * 
	 * @param entry
	 * @return
	 */
	public int removeEntryForReferenceEntry(RepositoryEntryRef entry) {
		StringBuilder sb = new StringBuilder();
		sb.append("update assessmententry data set data.referenceEntry.key=null where data.referenceEntry.key=:referenceKey");
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString())
				.setParameter("referenceKey", entry.getKey())
				.executeUpdate();
	}
	
	/**
	 * Delete all entries where the specified repository entry (typically
	 * a course) is linked to them.
	 * 
	 * @param entry
	 * @return
	 */
	public int deleteEntryForRepositoryEntry(RepositoryEntryRef entry) {
		String query = "delete from assessmententry data where data.repositoryEntry.key=:entryKey";
		return dbInstance.getCurrentEntityManager()
				.createQuery(query)
				.setParameter("entryKey", entry.getKey())
				.executeUpdate();
	}
	
	public int deleteEntryForIdentity(IdentityRef identity) {
		String query = "delete from assessmententry data where data.identity.key=:identityKey";
		return dbInstance.getCurrentEntityManager()
				.createQuery(query)
				.setParameter("identityKey", identity.getKey())
				.executeUpdate();
	}
}
