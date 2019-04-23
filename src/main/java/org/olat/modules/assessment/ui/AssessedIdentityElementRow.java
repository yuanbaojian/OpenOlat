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
package org.olat.modules.assessment.ui;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.id.Identity;
import org.olat.modules.assessment.AssessmentEntry;
import org.olat.modules.assessment.model.AssessmentEntryStatus;
import org.olat.modules.assessment.ui.component.CompletionItem;
import org.olat.user.UserPropertiesRow;
import org.olat.user.propertyhandlers.UserPropertyHandler;

/**
 * 
 * Initial date: 06.10.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessedIdentityElementRow extends UserPropertiesRow {
	
	private Integer attempts;
	private Boolean userVisibility;
	private BigDecimal score;
	private Boolean passed;
	private Date lastModified, lastUserModified, lastCoachModified;
	private int numOfAssessmentDocs;
	private AssessmentEntryStatus status;
	
	private Object details;
	private Date initialCourseLaunchDate;
	
	private FormLink toolsLink;
	private CompletionItem currentCompletion;
	
	public AssessedIdentityElementRow(Identity identity, AssessmentEntry entry,
			CompletionItem currentCompletion, FormLink toolsLink, List<UserPropertyHandler> userPropertyHandlers, Locale locale) {
		super(identity, userPropertyHandlers, locale);
		this.currentCompletion = currentCompletion;
		this.toolsLink = toolsLink;
		setAssessmentEntry(entry);
	}
	
	public void setAssessmentEntry(AssessmentEntry entry) {
		if(entry != null) {
			attempts = entry.getAttempts();
			score = entry.getScore();
			passed = entry.getPassed();
			userVisibility = entry.getUserVisibility();
			lastModified = entry.getLastModified();
			lastUserModified = entry.getLastUserModified();
			lastCoachModified = entry.getLastCoachModified();
			status = entry.getAssessmentStatus();
			numOfAssessmentDocs = entry.getNumberOfAssessmentDocuments();
		} else {
			attempts = null;
			score = null;
			passed = null;
			userVisibility = null;
			lastModified = lastUserModified = lastCoachModified = null;
			status = null;
			numOfAssessmentDocs = 0;
		}
	}

	public Integer getAttempts() {
		return attempts;
	}

	public BigDecimal getScore() {
		return score;
	}

	public Boolean getPassed() {
		return passed;
	}

	public Date getInitialCourseLaunchDate() {
		return initialCourseLaunchDate;
	}
	
	public void setInitialCourseLaunchDate(Date initialCourseLaunchDate) {
		this.initialCourseLaunchDate = initialCourseLaunchDate;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
	public Date getLastUserModified() {
		return lastUserModified;
	}
	
	public Date getLastCoachModified() {
		return lastCoachModified;
	}

	public int getNumOfAssessmentDocs() {
		return numOfAssessmentDocs;
	}

	public AssessmentEntryStatus getAssessmentStatus() {
		return status;
	}
	
	public CompletionItem getCurrentCompletion() {
		return currentCompletion;
	}
	
	public FormLink getToolsLink() {
		return toolsLink;
	}

	public Boolean getUserVisibility() {
		return userVisibility;
	}

	public Object getDetails() {
		return details;
	}

	public void setDetails(Object details) {
		this.details = details;
	}
}