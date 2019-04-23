/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.  
* <p>
*/ 

package org.olat.core.gui.components.progressbar;

import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.DefaultComponentRenderer;
import org.olat.core.gui.render.RenderResult;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.render.URLBuilder;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.StringHelper;

/**
 * Initial Date: Feb 2, 2004 A <b>ChoiceRenderer </b> is
 * 
 * @author Andreas Ch. Kapp
 */
public class ProgressBarRenderer extends DefaultComponentRenderer {

	@Override
	public void render(Renderer renderer, StringOutput target, Component source, URLBuilder urlBuilder, Translator translator,
			RenderResult renderResult, String[] args) {

		ProgressBar ubar = (ProgressBar) source;
		boolean renderLabels = (args == null) ? true : false;
		float percent = 100;
		if (!ubar.getIsNoMax()) {
			percent = 100 * ubar.getActual() / ubar.getMax();
		}
		if (percent < 0) {
			percent = 0;
		}
		if (percent > 100) {
			percent = 100;
		}
		target.append("<div class='progress").append(" o_progress_label_right", ubar.isRenderLabelRights()).append("' style=\"width:")
			.append(ubar.getWidth())
			.append("%", "px", ubar.isWidthInPercent())
			.append(";\"><div class='progress-bar' style=\"width:")
			.append(Math.round(percent * ubar.getWidth() / 100))
			.append("%", "px", ubar.isWidthInPercent()).append("\" title=\"")
			.append(Math.round(percent))
			.append("%\">");
		if (renderLabels) {
			if (ubar.isPercentagesEnabled()) {
				target.append(Math.round(percent));
				target.append("%");
				
			}
			if(!ubar.isRenderLabelRights()) {
				target.append(" (", ubar.isPercentagesEnabled());
				renderLabel(target, ubar);
				target.append(")", ubar.isPercentagesEnabled());
			}
		}
		String info = ubar.getInfo();
		if(StringHelper.containsNonWhitespace(info)) {
			target.append(info);
		}
		target.append("</div></div>");
		
		if (ubar.isRenderLabelRights()) {
			target.append("<div class='o_progress_label'>");
			renderLabel(target, ubar);
			target.append("</div>");			
		}
	}
	
	private void renderLabel(StringOutput target, ProgressBar ubar) {
		target.append(Math.round(ubar.getActual()));
		target.append("/");
		if (ubar.getIsNoMax()) target.append("-");
		else target.append(Math.round(ubar.getMax()));
		target.append(" ");
		target.append(ubar.getUnitLabel());
	}
}