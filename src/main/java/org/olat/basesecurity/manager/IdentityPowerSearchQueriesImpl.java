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
package org.olat.basesecurity.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.olat.basesecurity.AuthenticationImpl;
import org.olat.basesecurity.GroupMembershipInheritance;
import org.olat.basesecurity.IdentityImpl;
import org.olat.basesecurity.IdentityPowerSearchQueries;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.SearchIdentityParams;
import org.olat.basesecurity.model.IdentityPropertiesRow;
import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.SortKey;
import org.olat.core.id.Identity;
import org.olat.core.id.OrganisationRef;
import org.olat.core.util.StringHelper;
import org.olat.user.propertyhandlers.UserPropertyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 20 mars 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class IdentityPowerSearchQueriesImpl implements IdentityPowerSearchQueries {
	
	@Autowired
	private DB dbInstance;
	
	@Override
	public int countIdentitiesByPowerSearch(SearchIdentityParams params) {
		StringBuilder sb = new StringBuilder(5000);
		sb.append("select count(ident.key) from org.olat.core.id.Identity as ident ")
		  .append(" inner join ident.user as user ");
		Number count = createIdentitiesByPowerQuery(params, null, sb, Number.class).getSingleResult();
		return count.intValue();
	}

	@Override
	public List<Identity> getIdentitiesByPowerSearch(SearchIdentityParams params, int firstResult, int maxResults) {
		StringBuilder sb = new StringBuilder(5000);
		sb.append("select distinct ident from org.olat.core.id.Identity as ident ")
		  .append(" inner join fetch ident.user as user ");
		TypedQuery<Identity> dbq = createIdentitiesByPowerQuery(params, null, sb, Identity.class);
		if(firstResult >= 0) {
			dbq.setFirstResult(firstResult);
		}
		if(maxResults > 0) {
			dbq.setMaxResults(maxResults);
		}
		return dbq.getResultList();
	}

	@Override
	public List<IdentityPropertiesRow> getIdentitiesByPowerSearch(SearchIdentityParams params,
			List<UserPropertyHandler> userPropertyHandlers, Locale locale, SortKey orderBy, int firstResult, int maxResults) {

		StringBuilder sb = new StringBuilder(5000);
		sb.append("select")
		  .append(" ident.id as ident_id,")
		  .append(" ident.name as ident_name,")
		  .append(" ident.creationDate as ident_cDate,")
		  .append(" ident.lastLogin as ident_lDate,")
		  .append(" ident.status as ident_Status,");
		writeUserProperties("user", sb, userPropertyHandlers);
		sb.append(" user.key as ident_user_id")
		  .append(" from ").append(IdentityImpl.class.getCanonicalName()).append(" as ident ")
		  .append(" inner join ident.user as user ");
		
		TypedQuery<Object[]> dbq = createIdentitiesByPowerQuery(params, orderBy, sb, Object[].class);
		if(firstResult >= 0) {
			dbq.setFirstResult(firstResult);
		}
		if(maxResults > 0) {
			dbq.setMaxResults(maxResults);
		}
		List<Object[]> rawList = dbq.getResultList();
		List<IdentityPropertiesRow> rows = new  ArrayList<>(rawList.size());
		int numOfProperties = userPropertyHandlers.size();
		for(Object rawObject:rawList) {
			Object[] rawStat = (Object[])rawObject;
			int pos = 0;
			
			Long identityKey = ((Number)rawStat[pos++]).longValue();
			String identityName = (String)rawStat[pos++];
			Date creationDate = (Date)rawStat[pos++];
			Date lastLogin = (Date)rawStat[pos++];
			Integer status = (Integer)rawStat[pos++];

			String[] userProperties = new String[numOfProperties];
			for(int i=0; i<numOfProperties; i++) {
				userProperties[i] = (String)rawStat[pos++];
			}

			rows.add(new IdentityPropertiesRow(identityKey, identityName, creationDate, lastLogin, status, userProperties));
		}
		return rows;
	}
	
	private void writeUserProperties(String user, StringBuilder sb, List<UserPropertyHandler> userPropertyHandlers) {
		for(UserPropertyHandler handler:userPropertyHandlers) {
			sb.append(" ").append(user).append(".").append(handler.getName()).append(" as ")
			  .append("ident_user_").append(handler.getDatabaseColumnName()).append(",");
		}	
	}

	private <U> TypedQuery<U> createIdentitiesByPowerQuery(SearchIdentityParams params, SortKey orderBy, StringBuilder sb, Class<U> resultClass) {
		if (hasWhereClause(params)) {
			sb.append(" where ");
			boolean needsAnd = createUserPropertiesQueryPart(params, sb);
			needsAnd = createQueryPart(params, sb, needsAnd);
			needsAnd = createAuthenticationProviderQueryPart(params, sb, needsAnd);
			createDatesQueryPart(params, sb, needsAnd);
		}
		
		if(orderBy != null) {
			orderBy(sb, orderBy);
		}
		
		TypedQuery<U> dbq = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), resultClass);
		fillParameters(params, dbq);
		return dbq;
	}
	
	private boolean hasWhereClause(SearchIdentityParams params) {
		return params.getLogin() != null || params.hasUserProperties() || params.hasIdentityKeys()
				|| params.getCreatedAfter() != null	|| params.getCreatedBefore() != null
				|| params.getUserLoginAfter() != null || params.getUserLoginBefore() != null
				|| params.hasAuthProviders() || params.getManaged() != null
				|| params.getStatus() != null || (params.getExactStatusList() != null && !params.getExactStatusList().isEmpty())
				|| params.hasRoles() || params.hasExcludedRoles() || params.isAuthorAndCoAuthor()
				|| params.getRepositoryEntryRole() != null || params.getBusinessGroupRole() != null
				|| params.hasOrganisations() || params.hasOrganisationParents()
				|| StringHelper.containsNonWhitespace(params.getIdAndExternalIds());
	}
	
	private boolean createQueryPart(SearchIdentityParams params, StringBuilder sb, boolean needsAnd) {	
		// authors and co-authors is essentially an OR
		if(params.isAuthorAndCoAuthor()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.key in (select membership.identity.key from bgroupmember membership ")
			  .append("   left join repoentrytogroup as relGroup on (relGroup.group.key=membership.group.key) ")
			  .append("   where  membership.role in (:roles) or (relGroup.group.key is not null and membership.role=:repositoryEntryRole))");
			if(params.hasOrganisations()) {
				sb.append(" and ident.key in  (select orgtomember.identity.key from bgroupmember as orgtomember ")
				  .append("  inner join organisation as org on (org.group.key=orgtomember.group.key)")
				  .append("  where orgtomember.identity.key=ident.key and orgtomember.inheritanceModeString in ('").append(GroupMembershipInheritance.none).append("','").append(GroupMembershipInheritance.root).append("')")
				  .append("  and org.key in (:organisationKey))");
			}
		} else if(params.hasRoles() && params.hasOrganisations()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" exists (select orgtomember.key from bgroupmember as orgtomember ")
			  .append("  inner join organisation as org on (org.group.key=orgtomember.group.key)")
			  .append("  where orgtomember.identity.key=ident.key and org.key in (:organisationKey) and orgtomember.role in (:roles))");
		} else if(params.hasRoles()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.key in (select membership.identity.key from bgroupmember membership ")
			  .append("  where  membership.role in (:roles))");
		} else if(params.hasOrganisations()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" exists (select orgtomember.key from bgroupmember as orgtomember ")
			  .append("  inner join organisation as org on (org.group.key=orgtomember.group.key)")
			  .append("  where orgtomember.identity.key=ident.key and orgtomember.inheritanceModeString in ('").append(GroupMembershipInheritance.none).append("','").append(GroupMembershipInheritance.root).append("')")
			  .append("  and org.key in (:organisationKey))");
		}
		
		if(params.hasExcludedRoles()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.key not in (select membership.identity.key from organisation as orgRole  ")
			  .append("  inner join bgroupmember membership on (orgRole.group.key=membership.group.key)")
			  .append("  where membership.role in (:excludedRoles))");
		}
			
		if(params.hasOrganisationParents()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.key in (select orgmember.identity.key from bgroupmember as orgmember ")
			  .append("  inner join organisation as org on (org.group.key=orgmember.group.key)")
			  .append("  where ");
			sb.append("(");
			for(int i=0; i<params.getOrganisationParents().size(); i++) {
				if(i > 0) sb.append(" or ");
				sb.append(" org.materializedPathKeys like :organisationParent_").append(i);
			}
			sb.append("))");
		}
		
		if(params.isWithoutBusinessGroup()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" not exists (select bgroup from businessgroup bgroup, bgroupmember as me")
			  .append("   where me.group.key=bgroup.baseGroup.key and me.identity.key=ident.key")
			  .append(" )");
		}
		
		if(params.getBusinessGroupRole() != null) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" exists (select bgi.key from businessgroup bgi")
			  .append("  inner join bgi.baseGroup as bgGroup")
			  .append("  inner join bgGroup.members as bmember")
			  .append("  where bmember.identity.key=ident.key and  bmember.role=:businessGroupRole)");
		}
		
		if(params.getRepositoryEntryRole() != null && !params.isAuthorAndCoAuthor()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" exists (select rmember.key from repoentrytogroup as relGroup")
			  .append("  inner join relGroup.group as rGroup")
			  .append("  inner join rGroup.members as rmember")
			  .append("  where rmember.identity.key=ident.key and  rmember.role=:repositoryEntryRole)");
		}
		
		// append query for identity primary keys
		if(params.getIdentityKeys() != null && !params.getIdentityKeys().isEmpty()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append("ident.key in (:identityKeys)");
		}
		
		if(params.getIdAndExternalIds() != null) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append("(");
			if(StringHelper.isLong(params.getIdAndExternalIds())) {
				sb.append("ident.key=:idKey or user.key=:idKey or ");
			}
			sb.append("ident.externalId=:idAndRefs)");
		}
		
		if(params.getManaged() != null) {
			needsAnd = checkAnd(sb, needsAnd);
			if(params.getManaged().booleanValue()) {
				sb.append("ident.externalId is not null");
			} else {
				sb.append("ident.externalId is null");
			}	
		}
		
		if (params.getExactStatusList() != null && !params.getExactStatusList().isEmpty()) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.status in (:statusList)");
		} else if (params.getStatus() != null) {
			if (params.getStatus().equals(Identity.STATUS_VISIBLE_LIMIT)) {
				// search for all status smaller than visible limit 
				needsAnd = checkAnd(sb, needsAnd);
				sb.append(" ident.status < :status ");
			} else {
				// search for certain status
				needsAnd = checkAnd(sb, needsAnd);
				sb.append(" ident.status = :status ");
			}
		}
		return needsAnd;
	}
	
	private boolean createAuthenticationProviderQueryPart(SearchIdentityParams params, StringBuilder sb, boolean needsAnd) {	
		// append query for authentication providers
		if (params.hasAuthProviders()) {
			boolean hasNull = false;
			boolean hasAuth = false;
			String[] authProviders = params.getAuthProviders();
			for (int i = 0; i < authProviders.length; i++) {
				// special case for null auth provider
				if (authProviders[i] == null) {
					hasNull = true;
				} else {
					hasAuth = true;
				}
			}

			needsAnd = checkAnd(sb, needsAnd);
			sb.append("(");
			if(hasNull) {
				sb.append(" not exists (select auth.key from ").append(AuthenticationImpl.class.getCanonicalName()).append(" as auth")
				  .append("  where auth.identity.key=ident.key)");
			}
			if(hasAuth) {
				if(hasNull) {
					sb.append(" or ");
				}
				sb.append(" exists (select auth.key from ").append(AuthenticationImpl.class.getCanonicalName()).append(" as auth")
				  .append("  where auth.identity.key=ident.key and auth.provider in (:authProviders))");
			}
			sb.append(")");
		}
		
		return needsAnd;
	}
	
	public boolean createUserPropertiesQueryPart(SearchIdentityParams params, StringBuilder sb) {	
		boolean needsAnd = false;
		boolean needsUserPropertiesJoin = false;
		
		// treat login and userProperties as one element in this query
		if (params.getLogin() != null && (params.getUserProperties() != null && !params.getUserProperties().isEmpty())) {
			sb.append(" ( ");			
		}
		// append query for login
		if (params.getLogin() != null) {
			if (params.getLogin().contains("_") && dbInstance.isOracle()) {
				//oracle needs special ESCAPE sequence to search for escaped strings
				sb.append(" lower(ident.name) like :login ESCAPE '\\'");
			} else if (dbInstance.isMySQL()) {
				sb.append(" ident.name like :login");
			} else {
				sb.append(" lower(ident.name) like :login");
			}
			// if user fields follow a join element is needed
			needsUserPropertiesJoin = true;
			// at least one user field used, after this and is required
			needsAnd = true;
		}

		// append queries for user fields
		if (params.getUserProperties() != null && !params.getUserProperties().isEmpty()) {
			Map<String, String> emailProperties = new HashMap<>();
			Map<String, String> otherProperties = new HashMap<>();

			// split the user fields into two groups
			for (Map.Entry<String, String> entry : params.getUserProperties().entrySet()) {
				String key = entry.getKey();
				if (key.toLowerCase().contains("email")) {
					emailProperties.put(key, entry.getValue());
				} else {
					otherProperties.put(key, entry.getValue());
				}
			}

			// handle email fields special: search in all email fields
			if (!emailProperties.isEmpty()) {
				needsUserPropertiesJoin = checkIntersectionInUserProperties(sb, needsUserPropertiesJoin, params.isUserPropertiesAsIntersectionSearch());
				boolean moreThanOne = emailProperties.size() > 1;
				if (moreThanOne) sb.append("(");
				boolean needsOr = false;
				for (String key : emailProperties.keySet()) {
					if (needsOr) sb.append(" or ");
					if(dbInstance.isMySQL()) {
						sb.append(" user.").append(key).append(" like :").append(key).append("_value ");
					} else {
						sb.append(" lower(user.").append(key).append(") like :").append(key).append("_value ");
					}
					if(dbInstance.isOracle()) {
						sb.append(" escape '\\'");
					}
					needsOr = true;
				}
				if (moreThanOne) sb.append(")");
				// cleanup
				emailProperties.clear();
			}

			// add other fields
			for (String key : otherProperties.keySet()) {
				needsUserPropertiesJoin = checkIntersectionInUserProperties(sb, needsUserPropertiesJoin, params.isUserPropertiesAsIntersectionSearch());
				
				if(dbInstance.isMySQL()) {
					sb.append(" user.").append(key).append(" like :").append(key).append("_value ");
				} else {
					sb.append(" lower(user.").append(key).append(") like :").append(key).append("_value ");
				}
				if(dbInstance.isOracle()) {
					sb.append(" escape '\\'");
				}
				needsAnd = true;
			}
			// cleanup
			otherProperties.clear();
			// at least one user field used, after this and is required
			needsAnd = true;
		}
		// end of user fields and login part
		if (params.getLogin() != null && (params.getUserProperties() != null && !params.getUserProperties().isEmpty())) {
			sb.append(" ) ");
		}
		return needsAnd;
	}
	
	private boolean createDatesQueryPart(SearchIdentityParams params, StringBuilder sb, boolean needsAnd) {		
		// append query for creation date restrictions
		if (params.getCreatedAfter() != null) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.creationDate >= :createdAfter ");
		}
		if (params.getCreatedBefore() != null) {
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.creationDate <= :createdBefore ");
		}
		if(params.getUserLoginAfter() != null){
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.lastLogin >= :lastloginAfter ");
		}
		if(params.getUserLoginBefore() != null){
			needsAnd = checkAnd(sb, needsAnd);
			sb.append(" ident.lastLogin <= :lastloginBefore ");
		}
		return needsAnd;
	}
	
	private void orderBy(StringBuilder sb, SortKey orderBy) {
		if(orderBy == null) return;
		
		switch(orderBy.getKey()) {
			case "id":
			case "key":
				sb.append(" order by ident.key ").append(orderBy.isAsc() ? "asc" : "desc");
				break;
			case "creationDate":
				sb.append(" order by ident.creationDate ").append(orderBy.isAsc() ? "asc" : "desc");
				break;
			case "lastLogin":
				sb.append(" order by ident.lastLogin ").append(orderBy.isAsc() ? "asc" : "desc");
				break;
			case "name":
			case "username":
				sb.append(" order by lower(ident.name) ").append(orderBy.isAsc() ? "asc" : "desc");
				break;
			default:
				sb.append(" order by lower(user.").append(orderBy.getKey()).append(") ").append(orderBy.isAsc() ? "asc" : "desc");
				break;
		}
	}
	
	private void fillParameters(SearchIdentityParams params, TypedQuery<?> dbq) {
		// add user attributes
		if (params.getLogin() != null) {
			String login = makeFuzzyQueryString(params.getLogin());
			dbq.setParameter("login", login.toLowerCase());
		}
		
		if(params.getIdentityKeys() != null && !params.getIdentityKeys().isEmpty()) {
			dbq.setParameter("identityKeys", params.getIdentityKeys());
		}

		//	 add user properties attributes
		if (params.getUserProperties() != null && !params.getUserProperties().isEmpty()) {
			for (Map.Entry<String, String> entry : params.getUserProperties().entrySet()) {
				String value = entry.getValue();
				value = makeFuzzyQueryString(value);
				dbq.setParameter(entry.getKey() + "_value", value.toLowerCase());
			}
		}
		
		// add policies
		if (params.hasRoles()) {
			OrganisationRoles[] roles = params.getRoles();
			List<String> roleList = new ArrayList<>(roles.length);
			for(OrganisationRoles role:roles) {
				roleList.add(role.name());
			}
			dbq.setParameter("roles", roleList);
		}
		
		if (params.hasExcludedRoles()) {
			OrganisationRoles[] roles = params.getExcludedRoles();
			List<String> roleList = new ArrayList<>(roles.length);
			for(OrganisationRoles role:roles) {
				roleList.add(role.name());
			}
			dbq.setParameter("excludedRoles", roleList);
		}
		
		if(params.getBusinessGroupRole() != null) {
			dbq.setParameter("businessGroupRole", params.getBusinessGroupRole().name());
		}
		
		if(params.getRepositoryEntryRole() != null) {
			dbq.setParameter("repositoryEntryRole", params.getRepositoryEntryRole().name());
		}

		// add authentication providers
		if (params.hasAuthProviders()) {
			String[] authProviders = params.getAuthProviders();
			List<String> authProviderList = new ArrayList<>(authProviders.length);
			for (int i = 0; i < authProviders.length; i++) {
				String authProvider = authProviders[i];
				if (authProvider != null) {
					authProviderList.add(authProvider);
				}
			}
			if(!authProviderList.isEmpty()) {
				dbq.setParameter("authProviders", authProviderList);
			}
			
		}
		
		if(params.hasOrganisationParents()) {
			for(int i=0; i<params.getOrganisationParents().size(); i++) {
				String org = params.getOrganisationParents().get(i).getMaterializedPathKeys();
				dbq.setParameter("organisationParent_" + i, org + "%");
			}
		}
		
		if(params.hasOrganisations()) {
			List<Long> organisationKeys = params.getOrganisations()
					.stream().map(OrganisationRef::getKey).collect(Collectors.toList());
			dbq.setParameter("organisationKey", organisationKeys);
		}
		
		// add date restrictions
		if (params.getCreatedAfter() != null) {
			dbq.setParameter("createdAfter", params.getCreatedAfter(), TemporalType.TIMESTAMP);
		}
		if (params.getCreatedBefore() != null) {
			dbq.setParameter("createdBefore", params.getCreatedBefore(), TemporalType.TIMESTAMP);
		}
		if(params.getUserLoginAfter() != null){
			dbq.setParameter("lastloginAfter", params.getUserLoginAfter(), TemporalType.TIMESTAMP);
		}
		if(params.getUserLoginBefore() != null){
			dbq.setParameter("lastloginBefore", params.getUserLoginBefore(), TemporalType.TIMESTAMP);
		}
		
		if(params.getExactStatusList() != null && !params.getExactStatusList().isEmpty()) {
			dbq.setParameter("statusList", params.getExactStatusList());
		} else if (params.getStatus() != null) {
			dbq.setParameter("status", params.getStatus());
		}
		
		if(params.getIdAndExternalIds() != null) {
			if(StringHelper.isLong(params.getIdAndExternalIds())) {
				dbq.setParameter("idKey", Long.valueOf(params.getIdAndExternalIds()));
			}
			dbq.setParameter("idAndRefs", params.getIdAndExternalIds());
		}
	}
	
	private boolean checkAnd(StringBuilder sb, boolean needsAnd) {
		if (needsAnd) sb.append(" and ");
		return true;
	}

	private boolean checkIntersectionInUserProperties(StringBuilder sb, boolean needsJoin, boolean userPropertiesAsIntersectionSearch) {
		if (needsJoin) 	{
			if (userPropertiesAsIntersectionSearch) {
				sb.append(" and ");								
			} else {
				sb.append(" or ");				
			}
		}
		return true;
	}
	
	/**
	 * Helper method that replaces * with % and appends and
	 * prepends % to the string to make fuzzy SQL match when using like 
	 * @param email
	 * @return fuzzized string
	 */
	private String makeFuzzyQueryString(String string) {
		// By default only fuzzyfy at the end. Usually it makes no sense to do a
		// fuzzy search with % at the beginning, but it makes the query very very
		// slow since it can not use any index and must perform a fulltext search.
		// User can always use * to make it a really fuzzy search query
		// fxdiff FXOLAT-252: use "" to disable this feature and use exact match
		if (string.length() > 1 && string.startsWith("\"") && string.endsWith("\"")) {			
			string = string.substring(1, string.length()-1);
		} else {
			string = string + "%";
			string = string.replace('*', '%');
		}
		// with 'LIKE' the character '_' is a wildcard which matches exactly one character.
		// To test for literal instances of '_', we have to escape it.
		string = string.replace("_", "\\_");
		return string;
	}
}
