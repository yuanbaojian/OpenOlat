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
package org.olat.course.assessment.ui.tool;

import java.util.Locale;

import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiCellRenderer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableComponent;
import org.olat.core.gui.components.table.CustomCellRenderer;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.render.URLBuilder;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Util;
import org.olat.modules.assessment.model.AssessmentEntryStatus;

/**
 * 
 * Initial date: 08.10.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessmentStatusCellRenderer implements FlexiCellRenderer, CustomCellRenderer {
	
	private final Translator trans;
	
	public AssessmentStatusCellRenderer(Locale locale) {
		trans = Util.createPackageTranslator(AssessmentStatusCellRenderer.class, locale);
	}
	
	@Override
	public void render(StringOutput sb, Renderer renderer, Object val, Locale locale, int alignment, String action) {
		render(renderer, sb, val, -1, null, null, renderer.getTranslator());
	}

	@Override
	public void render(Renderer renderer, StringOutput target, Object cellValue,
			int row, FlexiTableComponent source, URLBuilder ubu, Translator translator) {
		if(cellValue instanceof AssessmentEntryStatus) {
			AssessmentEntryStatus status = (AssessmentEntryStatus)cellValue;
			switch(status) {
				case notStarted: target.append("<i class='o_icon o_icon_status_not_started o_icon-fw'> </i> ").append(trans.translate("assessment.status.notStart")); break;
				case inProgress: target.append("<i class='o_icon o_icon_status_in_progress o_icon-fw'> </i> ").append(trans.translate("assessment.status.inProgress")); break;
				case inReview: target.append("<i class='o_icon o_icon_status_in_review o_icon-fw'> </i> ").append(trans.translate("assessment.status.inReview")); break;
				case done: target.append("<i class='o_icon o_icon_status_done o_icon-fw'> </i> ").append(trans.translate("assessment.status.done")); break;
			}	
		} else {
			target.append("-");
		}
	}
}