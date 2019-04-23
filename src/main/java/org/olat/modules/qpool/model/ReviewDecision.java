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
package org.olat.modules.qpool.model;

import org.olat.modules.qpool.QuestionStatus;

/**
 * 
 * Initial date: 08.12.2017<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class ReviewDecision {
	
	private final boolean statusChanged;
	private final QuestionStatus status;
	
	public ReviewDecision(boolean statusChanged, QuestionStatus status) {
		super();
		this.statusChanged = statusChanged;
		this.status = status;
	}

	public boolean isStatusChanged() {
		return statusChanged;
	}

	public QuestionStatus getStatus() {
		return status;
	}

}
