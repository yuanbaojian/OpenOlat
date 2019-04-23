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
package org.olat.user;

import java.util.List;
import java.util.Locale;

import org.olat.core.id.Identity;
import org.olat.user.propertyhandlers.UserPropertyHandler;

/**
 * 
 * This is to build table
 * 
 * Initial date: 08.04.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class UserPropertiesRow {
	
	private final Long identityKey;
	private final String identityName;
	private final String[] identityProps;
	

	public UserPropertiesRow(Long identityKey, String identityName, String[] identityProps) {
		this.identityKey = identityKey;
		this.identityName = identityName;
		this.identityProps = identityProps;
	}
	
	public UserPropertiesRow(Identity identity, List<UserPropertyHandler> userPropertyHandlers, Locale locale) {
		identityProps = new String[userPropertyHandlers.size()];
		if(identity == null) {
			identityKey = null;
			identityName = null;
		} else {
			identityKey = identity.getKey();
			identityName = identity.getName();
			for(int i=userPropertyHandlers.size(); i-->0; ) {
				identityProps[i] = userPropertyHandlers.get(i).getUserProperty(identity.getUser(), locale);
			}
		}
	}
	
	public Long getIdentityKey() {
		return identityKey;
	}

	public String getIdentityName() {
		return identityName;
	}
	
	public String[] getIdentityProps() {
		return identityProps;
	}
	
	public String getIdentityProp(int index) {
		return identityProps[index];
	}

}
