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
package org.olat.core.gui.components.form.flexible.elements;

import org.olat.core.id.context.StateEntry;

/**
 * 
 * Initial date: 02.09.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class FlexiTableStateEntry implements StateEntry {

	private static final long serialVersionUID = 3552983520351850831L;
	
	private int page;
	private String searchString;
	private boolean expendedSearch;
	
	public FlexiTableStateEntry() {
		//
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public boolean isExpendedSearch() {
		return expendedSearch;
	}

	public void setExpendedSearch(boolean expendedSearch) {
		this.expendedSearch = expendedSearch;
	}

	@Override
	public FlexiTableStateEntry clone() {
		FlexiTableStateEntry clone = new FlexiTableStateEntry();
		clone.page = page;
		clone.searchString = searchString;
		clone.expendedSearch = expendedSearch;
		return clone;
	}
}