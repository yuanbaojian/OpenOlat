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
package org.olat.user.ui.admin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.olat.basesecurity.IdentityPowerSearchQueries;
import org.olat.basesecurity.SearchIdentityParams;
import org.olat.basesecurity.model.IdentityPropertiesRow;
import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.persistence.DefaultResultInfos;
import org.olat.core.commons.persistence.ResultInfos;
import org.olat.core.commons.persistence.SortKey;
import org.olat.core.gui.components.form.flexible.elements.FlexiTableFilter;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataSourceDelegate;
import org.olat.user.propertyhandlers.UserPropertyHandler;

/**
 * 
 * Initial date: 21 mars 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class UserSearchDataSource implements FlexiTableDataSourceDelegate<IdentityPropertiesRow> {
	
	private final SearchIdentityParams searchParams;
	private final IdentityPowerSearchQueries searchQuery;

	private final Locale locale;
	private final List<UserPropertyHandler> userPropertyHandlers;
	
	public UserSearchDataSource(SearchIdentityParams searchParams, List<UserPropertyHandler> userPropertyHandlers, Locale locale) {
		this.locale = locale;
		this.searchParams = searchParams;
		this.userPropertyHandlers = userPropertyHandlers;
		searchQuery = CoreSpringFactory.getImpl(IdentityPowerSearchQueries.class);
	}

	@Override
	public int getRowCount() {
		return searchQuery.countIdentitiesByPowerSearch(searchParams);
	}

	@Override
	public List<IdentityPropertiesRow> reload(List<IdentityPropertiesRow> rows) {
		return Collections.emptyList();
	}

	@Override
	public ResultInfos<IdentityPropertiesRow> getRows(String query, List<FlexiTableFilter> filters,
			List<String> condQueries, int firstResult, int maxResults, SortKey... orderBy) {
		SortKey sortKey = null;
		if(orderBy != null && orderBy.length > 0 && orderBy[0] != null) {
			sortKey = orderBy[0];
		}
		List<IdentityPropertiesRow> rows = searchQuery
				.getIdentitiesByPowerSearch(searchParams, userPropertyHandlers, locale, sortKey, firstResult, maxResults);
		return new DefaultResultInfos<>(firstResult + rows.size(), -1, rows);
	}
}
