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
package org.olat.modules.forms.ui.model;

import java.util.List;

import org.olat.core.CoreSpringFactory;
import org.olat.modules.forms.EvaluationFormResponse;
import org.olat.modules.forms.EvaluationFormsModule;
import org.olat.modules.forms.Limit;
import org.olat.modules.forms.SessionFilter;
import org.olat.modules.forms.manager.EvaluationFormReportDAO;

/**
 * 
 * Initial date: 06.05.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class RawResponsesDataSource implements ResponseDataSource {

	private final String responseIdentifier;
	private final SessionFilter filter;
	
	private EvaluationFormReportDAO reportDAO;
	private EvaluationFormsModule evaluationFormsModule;
	
	public RawResponsesDataSource(String responseIdentifier, SessionFilter filter) {
		super();
		this.responseIdentifier = responseIdentifier;
		this.filter = filter;
		this.reportDAO = CoreSpringFactory.getImpl(EvaluationFormReportDAO.class);
		this.evaluationFormsModule = CoreSpringFactory.getImpl(EvaluationFormsModule.class);
	}

	@Override
	public List<EvaluationFormResponse> getAllResponses() {
		return reportDAO.getResponses(responseIdentifier, filter, Limit.all());
	}

	@Override
	public List<EvaluationFormResponse> getLimitedResponses() {
		return reportDAO.getResponses(responseIdentifier, filter, getLimitMax());
	}

	@Override
	public Long getAllResponsesCount() {
		return reportDAO.getResponsesCount(responseIdentifier, filter, Limit.all());
	}

	@Override
	public Long getLimitedResponsesCount() {
		return reportDAO.getResponsesCount(responseIdentifier, filter, getLimitMax());
	}
	
	private Limit getLimitMax() {
		return Limit.max(evaluationFormsModule.getReportMaxSessions());
	}

}
