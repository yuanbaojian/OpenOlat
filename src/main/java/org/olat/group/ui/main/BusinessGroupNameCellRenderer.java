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

import java.util.Locale;

import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiCellRenderer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableComponent;
import org.olat.core.gui.components.table.CustomCellRenderer;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.render.URLBuilder;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.StringHelper;
import org.olat.group.BusinessGroupShort;

/**
 * 
 * Render an icon around the link
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class BusinessGroupNameCellRenderer implements CustomCellRenderer, FlexiCellRenderer {

	@Override
	public void render(Renderer renderer, StringOutput sb, Object cellValue,
			int row, FlexiTableComponent source, URLBuilder ubu, Translator translator) {
		if(cellValue instanceof BusinessGroupShort) {
			render(sb, (BusinessGroupShort)cellValue, renderer);
		}
	}
	
	@Override
	public void render(StringOutput sb, Renderer renderer, Object val, Locale locale,
			int alignment, String action) {
		if(val instanceof BusinessGroupShort) {
			render(sb, (BusinessGroupShort)val, renderer);		
		}
	}
	
	private void render(StringOutput sb, BusinessGroupShort group, Renderer renderer) {
		if(renderer != null) {
			sb.append("<i class='o_icon o_icon_group'> </i> ");
			if(group.getManagedFlags() != null && group.getManagedFlags().length > 0) {
				sb.append("<i class='o_icon o_icon_managed'> </i> ");
			}
			sb.append(StringHelper.escapeHtml(group.getName()));
		} else {
			sb.append(group.getName());
		}
	}
}