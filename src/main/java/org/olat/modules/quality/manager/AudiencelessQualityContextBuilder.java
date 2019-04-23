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
package org.olat.modules.quality.manager;

import java.util.List;

import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.OrganisationService;
import org.olat.core.CoreSpringFactory;
import org.olat.core.id.Organisation;
import org.olat.modules.forms.EvaluationFormParticipation;
import org.olat.modules.quality.QualityContext;
import org.olat.modules.quality.QualityDataCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 02.07.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
class AudiencelessQualityContextBuilder extends ForwardingQualityContextBuilder {

	@Autowired
	private QualityContextDAO qualityContextDao;
	@Autowired
	private OrganisationService organisationService;
	
	static final AudiencelessQualityContextBuilder builder(QualityDataCollection dataCollection,
			EvaluationFormParticipation evaluationFormParticipation) {
		return new AudiencelessQualityContextBuilder(dataCollection, evaluationFormParticipation);
	}

	private AudiencelessQualityContextBuilder(QualityDataCollection dataCollection,
			EvaluationFormParticipation evaluationFormParticipation) {
		super(dataCollection, evaluationFormParticipation);
		CoreSpringFactory.autowireObject(this);
		initBuilder(evaluationFormParticipation);
	}

	private void initBuilder(EvaluationFormParticipation evaluationFormParticipation) {
		List<QualityContext> contextToDelete = qualityContextDao.loadByWithoutAudience(evaluationFormParticipation);
		contextToDelete.forEach(c -> builder.addToDelete(c));
		
		List<Organisation> organisations = organisationService
				.getOrganisations(evaluationFormParticipation.getExecutor(), OrganisationRoles.user);
		for (Organisation organisation : organisations) {
			builder.addExecutorOrganisation(organisation);
		}
	}

}
