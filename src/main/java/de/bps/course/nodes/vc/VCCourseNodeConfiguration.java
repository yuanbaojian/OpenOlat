//<OLATCE-103>
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
 * BPS Bildungsportal Sachsen GmbH, http://www.bps-system.de
 * <p>
 */
package de.bps.course.nodes.vc;

import java.util.Locale;

import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Util;
import org.olat.course.nodes.AbstractCourseNodeConfiguration;
import org.olat.course.nodes.CourseNode;
import org.olat.course.nodes.CourseNodeConfiguration;
import org.olat.course.nodes.CourseNodeGroup;

import de.bps.course.nodes.VCCourseNode;

/**
 * Description:<br>
 * Configuration for date lists - Virtual Classroom dates.
 *
 * <P>
 * Initial Date: 04.07.2010 <br>
 *
 * @author Jens Lindner(jlindne4@hs-mittweida.de)
 * @author skoeber
 */
public class VCCourseNodeConfiguration extends AbstractCourseNodeConfiguration {

	@Override
	public String getAlias() {
		return "vc";
	}
	
	@Override
	public String getGroup() {
		return CourseNodeGroup.collaboration.name();
	}

	@Override
	public String getIconCSSClass() {
		return "o_vc_icon";
	}

	@Override
	public CourseNode getInstance() {
		return new VCCourseNode();
	}

	@Override
	public String getLinkText(Locale locale) {
		Translator fallback = Util.createPackageTranslator(CourseNodeConfiguration.class, locale);
		Translator translator = Util.createPackageTranslator(this.getClass(), locale, fallback);
		return translator.translate("title_vc");
	}
}
//</OLATCE-103>