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
package org.olat.core.commons.services.csp.ui;

import org.olat.core.commons.services.csp.CSPLog;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiTableDataSourceModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiSortableColumnDef;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataSourceDelegate;

/**
 * 
 * Initial date: 19 avr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class CSPLogTableModel extends DefaultFlexiTableDataSourceModel<CSPLog> {
	
	public CSPLogTableModel(FlexiTableDataSourceDelegate<CSPLog> source, FlexiTableColumnModel columnModel) {
		super(source, columnModel);
	}

	@Override
	public Object getValueAt(int row, int col) {
		CSPLog logEntry = getObject(row);
		switch(CSPCols.values()[col]) {
			case key: return logEntry.getKey();
			case creationDate: return logEntry.getCreationDate();
			case blockedUri: return logEntry.getBlockedUri();
			case documentUri: return logEntry.getDocumentUri();
			case referrer: return logEntry.getReferrer();
			case effectiveDirective: return logEntry.getEffectiveDirective();
			case originalPolicy: return logEntry.getOriginalPolicy();
			case sourceFile: return logEntry.getSourceFile();
			case lineNumber: return logEntry.getLineNumber();
			case columnNumber: return logEntry.getColumnNumber();
		}
		return null;
	}

	@Override
	public CSPLogTableModel createCopyWithEmptyList() {
		return new CSPLogTableModel(getSourceDelegate(), getTableColumnModel());
	}
	
	public enum CSPCols implements FlexiSortableColumnDef {
		key("table.header.key"),
		creationDate("table.header.date"),
		blockedUri("table.header.blocked.uri"),
		documentUri("table.header.document.uri"),
		referrer("table.header.referrer"),
		effectiveDirective("table.header.effective.directive"),
		originalPolicy("table.header.original.policy"),
		sourceFile("table.header.source.file"),
		lineNumber("table.header.line.number"),
		columnNumber("table.header.column.number");
		
		private final String i18n;
		
		private CSPCols(String i18n) {
			this.i18n = i18n;
		}
		
		public String i18n() {
			return i18n;
		}

		@Override
		public String i18nHeaderKey() {
			return i18n;
		}

		@Override
		public boolean sortable() {
			return false;
		}
		@Override
		public String sortKey() {
			return i18n;
		}
	}
}
