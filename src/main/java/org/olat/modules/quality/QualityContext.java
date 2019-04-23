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
package org.olat.modules.quality;

import java.util.Set;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;
import org.olat.modules.curriculum.CurriculumElement;
import org.olat.modules.forms.EvaluationFormParticipation;
import org.olat.modules.forms.EvaluationFormSession;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 22.06.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public interface QualityContext extends QualityContextRef, CreateInfo, ModifiedInfo {

	public QualityContextRole getRole();
	
	public String getLocation();
	
	public QualityDataCollection getDataCollection();
	
	public EvaluationFormSession getEvaluationFormSession();
	
	public EvaluationFormParticipation getEvaluationFormParticipation();
	
	public RepositoryEntry getAudienceRepositoryEntry();
	
	public CurriculumElement getAudienceCurriculumElement();
	
	public Set<QualityContextToCurriculum> getContextToCurriculum();
	
	public Set<QualityContextToCurriculumElement> getContextToCurriculumElement();
	
	public Set<QualityContextToOrganisation> getContextToOrganisation();
	
	public Set<QualityContextToTaxonomyLevel> getContextToTaxonomyLevel();
	
}
