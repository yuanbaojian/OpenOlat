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
package org.olat.ims.qti.statistics.ui;

import java.util.Collections;
import java.util.List;

import org.olat.core.util.StringHelper;
import org.olat.course.assessment.AssessmentHelper;

/**
 * 
 * Initial date: 10.03.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class ResponseInfos {
	
	private final String label;
	private final String text;
	private final Float points;
	private final boolean correct;
	private final boolean survey;
	private final boolean kprim;
	
	private final List<String> wrongAnswers;
	
	public ResponseInfos(String label, String text, Float points, boolean correct, boolean survey, boolean kprim) {
		this(label, text, Collections.<String>emptyList(),  points, correct, survey, kprim);
	}
	
	public ResponseInfos(String label, String text, List<String> wrongAnswers, Float points,
			boolean correct, boolean survey, boolean kprim) {
		this.label = label;
		this.text = text;
		this.points = points;
		this.survey = survey;
		this.kprim = kprim;
		this.correct = correct;
		this.wrongAnswers = wrongAnswers;
	}

	public String getLabel() {
		return label;
	}

	public String getText() {
		return text;
	}

	public Float getPoints() {
		return points;
	}
	
	public String getFormattedPoints() {
		if(points == null) {
			return "";
		}
		return AssessmentHelper.getRoundedScore(points);
	}
	
	public boolean isWrongAnswersAvailable() {
		return wrongAnswers != null && wrongAnswers.size() > 0;
	}

	public List<String> getWrongAnswers() {
		return wrongAnswers;
	}
	
	public String getFormattedWrongAnswers() {
		if(wrongAnswers != null && wrongAnswers.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for(String answer:wrongAnswers) {
				if(sb.length() > 0) sb.append(", ");
				if(StringHelper.containsNonWhitespace(answer)) {
					sb.append(StringHelper.escapeHtml(answer));
				} else {
					sb.append("\"\"");
				}
			}
			
			return sb.toString();
		}
		return "";
	}

	public boolean isSurvey() {
		return survey;
	}

	public boolean isKprim() {
		return kprim;
	}

	public boolean isCorrect() {
		return correct;
	}
}