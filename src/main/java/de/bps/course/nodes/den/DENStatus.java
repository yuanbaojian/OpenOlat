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
package de.bps.course.nodes.den;

/**
 * Hosts some informations about the enrollment process
 * @author skoeber
 */
public class DENStatus {

	public static final String ERROR_ALREADY_ENROLLED = "alreadyEnrolled";
	public static final String ERROR_NOT_ENROLLED = "notEnrolled";
	public static final String ERROR_GENERAL = "generalError";
	public static final String ERROR_PERSISTING = "persistingError";
	public static final String ERROR_FULL = "isFull";
	
	private String errorMessage;
	private boolean isEnrolled, isCancelled;
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isEnrolled() {
		return isEnrolled;
	}
	public void setEnrolled(boolean isEnrolled) {
		this.isEnrolled = isEnrolled;
	}
	public boolean isCancelled() {
		return isCancelled;
	}
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

}
