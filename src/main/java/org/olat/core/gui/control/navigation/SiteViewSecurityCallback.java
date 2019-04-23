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
package org.olat.core.gui.control.navigation;

import org.olat.core.gui.UserRequest;

/**
 * <h3>Description:</h3>
 * SiteSecurityCallback used to define who can see a the presence of a site.
 * 
 * Initial Date:  24.11.2009 <br>
 * @author Roman Haag, roman.haag@frentix.com, www.frentix.com
 */
public interface SiteViewSecurityCallback extends SiteSecurityCallback {
	
	/**
	 * decides if the Site itself will be visible as a tab
	 * @param ureq
	 * @return
	 */
	public boolean isAllowedToViewSite(UserRequest ureq);

}
