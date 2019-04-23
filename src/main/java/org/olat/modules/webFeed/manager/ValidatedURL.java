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
package org.olat.modules.webFeed.manager;

/**
 * <P>
 * Initial Date:  25 feb. 2010 <br>
 * @author srosse, stephane.rosse@frentix.com
 */
public class ValidatedURL {
	private final String url;
	private final String title;
	private final State state;
	
	public ValidatedURL(String url, String title, State state) {
		this.url = url;
		this.title = title;
		this.state = state;
	}
	
	public String getUrl() {
		return url;
	}

	public State getState() {
		return state;
	}
	
	public String getTitle() {
		return title;
	}

	public enum State {
		VALID,
		NO_ENCLOSURE,
		NOT_FOUND,
		MALFORMED
	}
}
