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
package org.olat.admin.sysinfo.model;


/**
 * 
 * <h3>Description:</h3>
 * 
 * Initial Date:  21 juin 2010 <br>
 * @author srosse, stephane.rosse@frentix.com, www.frentix.com
 */
public class SessionStatsSample {
	
	private final long timestamp;
	private long requests = 0l;
	private long numOfSessions = 0l;
	private long authenticatedClicks = 0l;
	private long authenticatedPollerCalls = 0l;
	
	public SessionStatsSample(long numOfSessions) {
		timestamp = System.currentTimeMillis();
		this.numOfSessions = numOfSessions;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public long getRequests() {
		return requests;
	}
	
	public long getAuthenticatedClick() {
		return authenticatedClicks;
	}

	public long getAuthenticatedPollerCalls() {
		return authenticatedPollerCalls;
	}

	public long getNumOfSessions() {
		return numOfSessions;
	}

	public void incrementAuthenticatedClick() {
		authenticatedClicks++;
	}
	
	public void incrementAuthenticatedPollerCalls() {
		authenticatedPollerCalls++;
	}
	
	public void incrementRequest() {
		requests++;
	}
}
