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

import org.olat.core.gui.components.table.DefaultColumnDescriptor;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.translator.Translator;

/**
 * 
 * Check the managed flags to present the link or not.
 * 
 * Initial date: 10.07.2013<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class LeaveColumnDescriptor extends DefaultColumnDescriptor {
	
	private final Translator translator;
	
	public LeaveColumnDescriptor(String headerKey, String action,  Translator translator) {
		super(headerKey, 1, action, translator.getLocale());
		this.translator = translator;
	}

	@Override
	public String getAction(int row) {
		int sortedRow = table.getSortedRow(row);
		MemberRow membership = (MemberRow)table.getTableDataModel()
				.getValueAt(sortedRow, MemberListTableModel.Cols.groups.ordinal());
		
		return membership.isFullyManaged() ? null : super.getAction(row);
	}

	@Override
	public void renderValue(StringOutput sb, int row, Renderer renderer) {
		int sortedRow = table.getSortedRow(row);
		MemberRow membership = (MemberRow)table.getTableDataModel()
				.getValueAt(sortedRow, MemberListTableModel.Cols.groups.ordinal());
		
		if(!membership.isFullyManaged()) {
			sb.append(translator.translate(getHeaderKey()));
		}
	}
}
