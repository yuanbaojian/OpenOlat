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
package org.olat.modules.forms;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.OLATResourceable;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 29.04.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public interface EvaluationFormSurvey extends EvaluationFormSurveyRef, CreateInfo, ModifiedInfo {

	public OLATResourceable getOLATResourceable();
	
	public String getSubident();

	/**
	 *
	 * @return the ReositoryEntry of the evaluation form (questionnaire)
	 */
	public RepositoryEntry getFormEntry();

	/**
	 * Get the key of a series. All surveys with the same series key are together one series.
	 *
	 * @return the key of the series
	 */
	public Long getSeriesKey();
	
	/**
	 * The index in a series. The first element in a series has index 1.
	 *
	 * @return the index 
	 */
	public Integer getSeriesIndex();

	/**
	 *
	 * @return the previous survey in a series.
	 */
	public EvaluationFormSurvey getSeriesPrevious();


}
