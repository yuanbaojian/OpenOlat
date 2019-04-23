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
package org.olat.group.ui.main;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.olat.core.commons.persistence.SortKey;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SortableFlexiTableDataModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SortableFlexiTableModelDelegate;
import org.olat.group.ui.main.MemberListTableModel.Cols;

/**
 * 
 * Initial date: 18.05.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class MemberListTableSort extends SortableFlexiTableModelDelegate<MemberRow> {

	public MemberListTableSort(SortKey orderBy, SortableFlexiTableDataModel<MemberRow> tableModel, Locale locale) {
		super(orderBy, tableModel, locale);
	}

	@Override
	protected void sort(List<MemberRow> rows) {
		int columnIndex = getColumnIndex();
		if(columnIndex >= AbstractMemberListController.USER_PROPS_OFFSET) {
			super.sort(rows);
		} else {
			Cols column = Cols.values()[columnIndex];
			switch(column) {
				case role:
					Collections.sort(rows, new RoleMemberViewComparator());
					break;
				case groups:
					Collections.sort(rows, new GroupMemberViewComparator(getCollator()));
					break;
				default: {
					super.sort(rows);
				}
			}
		}
	}

	private static class RoleMemberViewComparator implements Comparator<MemberRow> {
		
		private final CourseMembershipComparator comparator = new CourseMembershipComparator();

		@Override
		public int compare(MemberRow o1, MemberRow o2) {
			return comparator.compare(o1.getMembership(), o2.getMembership());
		}
	}
}
