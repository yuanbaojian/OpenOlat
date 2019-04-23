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
package org.olat.admin.layout;

import org.olat.core.helpers.Settings;
import org.olat.core.util.StringHelper;

/**
 * 
 * Wrapper to get the logo informations directly from the module.
 * 
 * Initial date: 21.08.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class FooterInformations {

	private LayoutModule layoutModule;
	
	public FooterInformations(LayoutModule layoutModule) {
		this.layoutModule = layoutModule;
	}
	
	public boolean isHasFooterLine() {
		String line = layoutModule.getFooterLine();
		return StringHelper.containsNonWhitespace(line);
	}
	
	public String getFooterLine() {
		String line = layoutModule.getFooterLine();
		if(!StringHelper.containsNonWhitespace(line)) {
			line = null;
		}
		return convertFooterLine(line);
	}
	
	public String getFooterUrl() {
		String uri = layoutModule.getFooterLinkUri();
		if(!StringHelper.containsNonWhitespace(uri)) {
			uri = Settings.getApplicationName();
		}
		return uri;
	}
	
	private String convertFooterLine(String dbFooterLine) {
		if (dbFooterLine == null) {
			return "";
		}
		
		String parsedFooterLine = null;
		String mailregex = "(\\w+)@([\\w\\.]+)";
		parsedFooterLine = dbFooterLine.replaceAll(mailregex, "<a href=\"mailto:$0\">$0</a>");
		String urlregex = "((http(s?)://)|(www.))(([\\w-.]+)*(/[^[:space:]]+)*)";
		parsedFooterLine = parsedFooterLine.replaceAll(urlregex, "<a href=\"http$3://$4$5\" target=\"_blank\">$2$4$5</a>");
		return parsedFooterLine;
	}
}