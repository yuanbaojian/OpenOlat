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
package org.olat.modules.quality.ui;

import java.util.Date;

import org.olat.modules.quality.QualityExecutorParticipation;
import org.olat.modules.quality.QualityExecutorParticipationStatus;

/**
 * 
 * Initial date: 20.06.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class ExecutorParticipationRow {

	private final QualityExecutorParticipation participation;

	public ExecutorParticipationRow(QualityExecutorParticipation participation) {
		this.participation = participation;
	}
	
	public QualityExecutorParticipation getParticipation() {
		return participation;
	}
	
	public Long getParticipationKey() {
		return participation.getParticipationRef().getKey();
	}

	public QualityExecutorParticipationStatus getExecutionStatus() {
		return participation.getExecutionStatus();
	}

	public Date getStart() {
		return participation.getStart();
	}

	public Date getDeadine() {
		return participation.getDeadline();
	}

	public String getTitle() {
		return participation.getTitle();
	}
	
	public String getTranslatedtopicType() {
		return participation.getTranslatedTopicType();
	}
	
	public String getTopic() {
		return participation.getTopic();
	}

}
