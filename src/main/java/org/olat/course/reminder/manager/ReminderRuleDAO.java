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
package org.olat.course.reminder.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.PersistenceHelper;
import org.olat.core.id.Identity;
import org.olat.course.nodes.CourseNode;
import org.olat.repository.RepositoryEntryRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 09.04.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class ReminderRuleDAO {

	@Autowired
	private DB dbInstance;


	public Map<Long,Float> getScores(RepositoryEntryRef entry, CourseNode node, List<Identity> identities) {
		if(identities == null || identities.isEmpty()) {
			return new HashMap<>();
		}

		Set<Long> identityKeySet = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select data.identity.key, data.score from assessmententry data")
		  .append(" where data.repositoryEntry.key=:courseEntryKey and data.subIdent=:subIdent");
		if(identities.size() < 50) {
			sb.append(" and data.identity.key in (:identityKeys)");
		}

		TypedQuery<Object[]> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Object[].class)
				.setParameter("courseEntryKey", entry.getKey())
				.setParameter("subIdent", node.getIdent());
		if(identities.size() < 50) {
			query.setParameter("identityKeys", PersistenceHelper.toKeys(identities));
		} else {
			identityKeySet = new HashSet<>(PersistenceHelper.toKeys(identities));
		}

		List<Object[]> infoList = query.getResultList();
		Map<Long,Float> dateMap = new HashMap<>();
		for(Object[] infos:infoList) {
			Long identityKey = (Long)infos[0];
			if(identityKeySet == null || identityKeySet.contains(identityKey)) {
				Number score = (Number)infos[1];
				if(score != null) {
					dateMap.put(identityKey, score.floatValue());
				}
			}
		}
		return dateMap;
	}
	
	public Map<Long,Integer> getAttempts(RepositoryEntryRef entry, CourseNode node, List<Identity> identities) {
		if(identities == null || identities.isEmpty()) {
			return new HashMap<>();
		}

		Set<Long> identityKeySet = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select data.identity.key, data.attempts from assessmententry data")
		  .append(" where data.repositoryEntry.key=:courseEntryKey and data.subIdent=:subIdent");
		if(identities.size() < 50) {
			sb.append(" and data.identity.key in (:identityKeys)");
		}

		TypedQuery<Object[]> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Object[].class)
				.setParameter("courseEntryKey", entry.getKey())
				.setParameter("subIdent", node.getIdent());
		if(identities.size() < 50) {
			query.setParameter("identityKeys", PersistenceHelper.toKeys(identities));
		} else {
			identityKeySet = new HashSet<>(PersistenceHelper.toKeys(identities));
		}

		List<Object[]> infoList = query.getResultList();
		Map<Long,Integer> dateMap = new HashMap<>();
		for(Object[] infos:infoList) {
			Long identityKey = (Long)infos[0];
			if(identityKeySet == null || identityKeySet.contains(identityKey)) {
				Number attempts = (Number)infos[1];
				if(attempts != null) {
					dateMap.put(identityKey, Integer.valueOf(attempts.intValue()));
				} else {
					dateMap.put(identityKey, Integer.valueOf(0));
				}
			}
		}
		return dateMap;
	}
	
	public Map<Long,Date> getInitialAttemptDates(RepositoryEntryRef entry, CourseNode node, List<Identity> identities) {
		if(identities == null || identities.isEmpty()) {
			return new HashMap<>();
		}

		Set<Long> identityKeySet = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select data.identity.key, data.creationDate from assessmententry data")
		  .append(" where data.repositoryEntry.key=:courseEntryKey and data.subIdent=:subIdent");
		if(identities.size() < 50) {
			sb.append(" and data.identity.key in (:identityKeys)");
		}

		TypedQuery<Object[]> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Object[].class)
				.setParameter("courseEntryKey", entry.getKey())
				.setParameter("subIdent", node.getIdent());
		if(identities.size() < 50) {
			query.setParameter("identityKeys", PersistenceHelper.toKeys(identities));
		} else {
			identityKeySet = new HashSet<>(PersistenceHelper.toKeys(identities));
		}

		List<Object[]> infoList = query.getResultList();
		Map<Long,Date> dateMap = new HashMap<>();
		for(Object[] infos:infoList) {
			Long identityKey = (Long)infos[0];
			if(identityKeySet == null || identityKeySet.contains(identityKey)) {
				Date attempts = (Date)infos[1];
				dateMap.put(identityKey, attempts);
			}
		}
		return dateMap;
	}
	
	public Map<Long,Boolean> getPassed(RepositoryEntryRef entry, CourseNode node, List<Identity> identities) {
		if(identities == null || identities.isEmpty()) {
			return new HashMap<>();
		}

		Set<Long> identityKeySet = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select data.identity.key, data.passed from assessmententry data")
		  .append(" where data.repositoryEntry.key=:courseEntryKey and data.subIdent=:subIdent");
		if(identities.size() < 50) {
			sb.append(" and data.identity.key in (:identityKeys)");
		}

		TypedQuery<Object[]> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), Object[].class)
				.setParameter("courseEntryKey", entry.getKey())
				.setParameter("subIdent", node.getIdent());
		if(identities.size() < 50) {
			query.setParameter("identityKeys", PersistenceHelper.toKeys(identities));
		} else {
			identityKeySet = new HashSet<>(PersistenceHelper.toKeys(identities));
		}

		List<Object[]> infoList = query.getResultList();
		Map<Long,Boolean> dateMap = new HashMap<>();
		for(Object[] infos:infoList) {
			Long identityKey = (Long)infos[0];
			if(identityKeySet == null || identityKeySet.contains(identityKey)) {
				Boolean passed = (Boolean)infos[1];
				dateMap.put(identityKey, passed);
			}
		}
		return dateMap;
	}
}