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
package org.olat.modules.quality.ui;

import java.util.List;
import java.util.Locale;

import org.olat.core.commons.persistence.SortKey;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiTableDataModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiSortableColumnDef;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SortableFlexiTableDataModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SortableFlexiTableModelDelegate;
import org.olat.modules.curriculum.ui.CurriculumUserManagementTableModel.CurriculumMemberCols;
import org.olat.user.UserPropertiesRow;

/**
 * 
 * Initial date: 06.11.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class ReportMemberTableModel extends DefaultFlexiTableDataModel<UserPropertiesRow>
implements SortableFlexiTableDataModel<UserPropertiesRow> {
	
	private final Locale locale;
	
	public ReportMemberTableModel(FlexiTableColumnModel columnModel, Locale locale) {
		super(columnModel);
		this.locale = locale;
	}

	@Override
	public void sort(SortKey orderBy) {
		if(orderBy != null) {
			List<UserPropertiesRow> rows = new SortableFlexiTableModelDelegate<>(orderBy, this, locale).sort();
			super.setObjects(rows);
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		UserPropertiesRow member = getObject(row);
		return getValueAt(member, col);
	}

	@Override
	public Object getValueAt(UserPropertiesRow row, int col) {
		if(col >= 0 && col < CurriculumMemberCols.values().length) {
			switch(CurriculumMemberCols.values()[col]) {
				case username: return row.getIdentityName();
				default : return "ERROR";
			}
		}
		
		int propPos = col - ReportAccessController.USER_PROPS_OFFSET;
		return row.getIdentityProp(propPos);
	}

	@Override
	public DefaultFlexiTableDataModel<UserPropertiesRow> createCopyWithEmptyList() {
		return new ReportMemberTableModel(getTableColumnModel(), locale);
	}
	
	public enum ReportMemberCols implements FlexiSortableColumnDef {
		username("report.member.username");
		
		private final String i18nKey;
		
		private ReportMemberCols(String i18nKey) {
			this.i18nKey = i18nKey;
		}
		
		@Override
		public String i18nHeaderKey() {
			return i18nKey;
		}

		@Override
		public boolean sortable() {
			return true;
		}

		@Override
		public String sortKey() {
			return name();
		}
	}
}
