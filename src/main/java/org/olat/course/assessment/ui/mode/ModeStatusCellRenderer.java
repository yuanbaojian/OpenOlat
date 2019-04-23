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
package org.olat.course.assessment.ui.mode;

import java.util.List;

import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiCellRenderer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableComponent;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.render.URLBuilder;
import org.olat.core.gui.translator.Translator;
import org.olat.course.assessment.AssessmentMode.Status;
import org.olat.course.assessment.model.EnhancedStatus;

/**
 * 
 * Initial date: 08.01.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class ModeStatusCellRenderer implements FlexiCellRenderer {

	@Override
	public void render(Renderer renderer, StringOutput sb, Object cellValue,
			int row, FlexiTableComponent source, URLBuilder ubu, Translator translator) {

		if(cellValue instanceof Status) {
			Status status = (Status)cellValue;
			renderStatus(status, sb);
		} else if(cellValue instanceof EnhancedStatus) {
			EnhancedStatus enStatus = (EnhancedStatus)cellValue;
			renderWarning(enStatus.getWarnings(), sb);
			renderStatus(enStatus.getStatus(), sb);
		}
	}
	
	private void renderWarning(List<String> warnings, StringOutput sb) {
		if(warnings != null && warnings.size() > 0) {
			sb.append("<i class='o_icon o_icon_warn' title='");
			for(String warning:warnings) {
				sb.append(warning).append(" ");
			}
			sb.append("'> </i> ");
		}
	}
	
	private void renderStatus(Status status, StringOutput sb) {
		switch(status) {
			case none: render("o_as_mode_none", sb); break;
			case leadtime: render("o_as_mode_leadtime", sb); break;
			case assessment: render("o_as_mode_assessment", sb); break;
			case followup: render("o_as_mode_followup", sb); break;
			case end: render("o_as_mode_closed", sb); break;
		}
	}
	
	private void render(String iconCss, StringOutput sb) {
		sb.append("<i class='o_icon ").append(iconCss).append("'> </i>");
	}
}