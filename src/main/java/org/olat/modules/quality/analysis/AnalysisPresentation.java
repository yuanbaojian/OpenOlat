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
package org.olat.modules.quality.analysis;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;
import org.olat.modules.quality.analysis.ui.TrendDifference;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 28.09.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public interface AnalysisPresentation extends AnalysisPresentationRef, CreateInfo, ModifiedInfo {
	
	public String getName();
	
	public void setName(String name);
	
	public AnalysisSegment getAnalysisSegment();
	
	public void setAnalysisSegment(AnalysisSegment segment);
	
	public AnalysisSearchParameter getSearchParams();
	
	public void setSearchParams(AnalysisSearchParameter searchParams);
	
	public MultiGroupBy getHeatMapGrouping();
	
	public void setHeatMapGrouping(MultiGroupBy groupBy);
	
	public Boolean getHeatMapInsufficientOnly();
	
	public void setHeatMapInsufficientOnly(Boolean insufficientOnly);
	
	public TemporalGroupBy getTemporalGroupBy();
	
	public void setTemporalGroupBy(TemporalGroupBy temporalGroupBy);
	
	public TrendDifference getTrendDifference();
	
	public void setTrendDifference(TrendDifference trendDifference);
	
	public String getRubricId();
	
	public void setRubricId(String rubricId);
	
	public RepositoryEntry getFormEntry();
	
}
