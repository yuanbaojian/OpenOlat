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
package org.olat.group.ui.wizard;

import java.util.Locale;

import org.olat.core.gui.components.table.CustomCellRenderer;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;

/**
 * 
 * Render the status of a user (new, removed, current)
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class BGUserStatusCellRenderer implements CustomCellRenderer {

	@Override
	public void render(StringOutput sb, Renderer renderer, Object val, Locale locale, int alignment, String action) {
		if(val instanceof BGUserManagementGroupTableDataModel.Status) {
			BGUserManagementGroupTableDataModel.Status status = (BGUserManagementGroupTableDataModel.Status)val;
			sb.append("<div class='o_status'><span>");
			switch(status) {
				case newOwner: {
					sb.append("+O"); break;
				}
				case newParticipant: {
					sb.append("+P"); break;
				}
				case newWaiting: {
					sb.append("+W"); break;
				}
				case removed: {
					sb.append("-"); break;
				} case current: {
					break;
				}
			}
			sb.append("</span></div>");
		}
	}
}
