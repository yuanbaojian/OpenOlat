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
package org.olat.ims.qti21.manager;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.olat.basesecurity.GroupRoles;
import org.olat.core.commons.persistence.DB;
import org.olat.ims.qti21.AssessmentItemSession;
import org.olat.ims.qti21.AssessmentResponse;
import org.olat.ims.qti21.AssessmentTestSession;
import org.olat.ims.qti21.model.QTI21StatisticSearchParams;
import org.olat.ims.qti21.model.ResponseLegality;
import org.olat.ims.qti21.model.jpa.AssessmentResponseImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;

/**
 * 
 * Initial date: 29.01.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class AssessmentResponseDAO {

	@Autowired
	private DB dbInstance;
	
	public AssessmentResponse createAssessmentResponse(AssessmentTestSession assessmentTestSession, AssessmentItemSession assessmentItemSession,
			String responseIdentifier, ResponseLegality legality, ResponseDataType type) {
		AssessmentResponseImpl response = new AssessmentResponseImpl();
		Date now = new Date();
		response.setCreationDate(now);
		response.setLastModified(now);
		response.setResponseDataType(type.name());
		response.setResponseLegality(legality.name());
		response.setAssessmentItemSession(assessmentItemSession);
		response.setAssessmentTestSession(assessmentTestSession);
		response.setResponseIdentifier(responseIdentifier);
		return response;
	}
	
	public List<AssessmentResponse> getResponses(AssessmentItemSession assessmentItemSession) {
		StringBuilder sb = new StringBuilder();
		sb.append("select response from qtiassessmentresponse response where")
		  .append(" response.assessmentItemSession.key=:assessmentItemSessionKey");
		return dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentResponse.class)
				.setParameter("assessmentItemSessionKey", assessmentItemSession.getKey())
				.getResultList();
	}
	
	public void save(Collection<AssessmentResponse> responses) {
		if(responses != null && responses.isEmpty()) return;
		
		for(AssessmentResponse response:responses) {
			if(response.getKey() != null) {
				dbInstance.getCurrentEntityManager().merge(response);
			} else {
				dbInstance.getCurrentEntityManager().persist(response);
			}
		}
	}
	
	/**
	 * Check if there are some responses from a terminated session.
	 * 
	 * @param courseEntry
	 * @param subIdent
	 * @param testEntry
	 * @return
	 */
	public boolean hasResponses(QTI21StatisticSearchParams searchParams) {
		StringBuilder sb = new StringBuilder();
		sb.append("select response.key from qtiassessmentresponse response ")
		  .append(" inner join response.assessmentItemSession itemSession")
		  .append(" inner join itemSession.assessmentTestSession testSession")
		  .append(" where testSession.repositoryEntry.key=:repoEntryKey")
		  .append("  and testSession.testEntry.key=:testEntryKey")
		  .append("  and testSession.subIdent=:subIdent")
		  .append("  and testSession.finishTime is not null and testSession.authorMode=false")
		  .append("  and (");

		if(searchParams.isViewAllUsers()) {
			sb.append(" testSession.identity.key is not null");
		} else if(searchParams.getLimitToGroups() != null) {
			sb.append(" testSession.identity.key in (select membership.identity.key from  bgroupmember as membership, repoentrytogroup as rel")
			  .append("   where rel.entry.key=:repoEntryKey and rel.group.key=membership.group.key and rel.group.key in (:limitGroupKeys)")
			  .append("   and membership.role='").append(GroupRoles.participant.name()).append("'")
			  .append(" )");
		} else if(searchParams.getLimitToIdentities() != null) {
			sb.append(" testSession.identity.key in (select membership.identity.key from  bgroupmember as membership, repoentrytogroup as rel")
			  .append("   where rel.entry.key=:repoEntryKey and rel.group.key=membership.group.key and membership.identity.key in (:limitIdentityKeys)")
			  .append("   and membership.role='").append(GroupRoles.participant.name()).append("'")
			  .append(" )");
		} else {
			sb.append(" testSession.identity.key in (select membership.identity.key from  bgroupmember as membership, repoentrytogroup as rel")
			  .append("   where rel.entry.key=:repoEntryKey and rel.group.key=membership.group.key ")
			  .append("   and membership.role='").append(GroupRoles.participant.name()).append("'")
			  .append(" )");
		}
		if(searchParams.isViewAnonymUsers()) {
			sb.append(" or testSession.anonymousIdentifier is not null");
		}
		sb.append(")");
		
		List<Long> responses = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Long.class)
				.setParameter("repoEntryKey", searchParams.getCourseEntry().getKey())
				.setParameter("testEntryKey", searchParams.getTestEntry().getKey())
				.setParameter("subIdent", searchParams.getNodeIdent())
				.setFirstResult(0)
				.setMaxResults(1)
				.getResultList();
		return responses.size() > 0 && responses.get(0) != null;
	}
	
	/**
	 * @param searchParams
	 * @return The returned list is order by user name, test session key and item session key
	 */
	public List<AssessmentResponse> getResponse(QTI21StatisticSearchParams searchParams) {
		StringBuilder sb = new StringBuilder();
		sb.append("select response from qtiassessmentresponse response ")
		  .append(" inner join fetch response.assessmentItemSession itemSession")
		  .append(" inner join fetch itemSession.assessmentTestSession testSession")
		  .append(" inner join fetch testSession.assessmentEntry assessmentEntry")
		  .append(" left join assessmentEntry.identity as ident")
		  .append(" left join ident.user as usr");
		
		AssessmentTestSessionDAO.decorateTestSessionPermission(sb, searchParams);

		//need to be anonymized
		sb.append(" order by usr.lastName, testSession.key, itemSession.key");
		
		TypedQuery<AssessmentResponse> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AssessmentResponse.class);
		AssessmentTestSessionDAO.decorateTestSessionPermission(query, searchParams);
		return query.getResultList();
	}
}
