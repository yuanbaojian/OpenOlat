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
package org.olat.modules.quality.analysis.model;

import org.olat.modules.forms.RubricRating;
import org.olat.modules.quality.analysis.GroupedStatistic;
import org.olat.modules.quality.analysis.MultiKey;
import org.olat.modules.quality.analysis.TemporalKey;
import org.olat.modules.quality.analysis.Trend;

/**
 * 
 * Initial date: 15 Jan 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class TrendImpl implements Trend {
	
	private final GroupedStatistic groupedStatistic;
	private final DIRECTION direction;
	private final Double avgDiffAbsolute;
	private final Double avgDiffRelative;
	
	public TrendImpl(GroupedStatistic groupedStatistic, DIRECTION direction, Double avgDiffAbsolute,
			Double avgDiffRelative) {
		this.groupedStatistic = groupedStatistic;
		this.direction = direction;
		this.avgDiffAbsolute = avgDiffAbsolute;
		this.avgDiffRelative = avgDiffRelative;
	}

	@Override
	public DIRECTION getDirection() {
		return direction;
	}

	@Override
	public TemporalKey getTemporalKey() {
		return groupedStatistic.getTemporalKey();
	}

	@Override
	public String getIdentifier() {
		return groupedStatistic.getIdentifier();
	}

	@Override
	public MultiKey getMultiKey() {
		return groupedStatistic.getMultiKey();
	}

	@Override
	public Long getCount() {
		return groupedStatistic.getCount();
	}

	@Override
	public Double getRawAvg() {
		return groupedStatistic.getRawAvg();
	}

	@Override
	public boolean isRawAvgMaxGood() {
		return groupedStatistic.isRawAvgMaxGood();
	}

	@Override
	public Double getAvg() {
		return groupedStatistic.getAvg();
	}

	@Override
	public RubricRating getRating() {
		return groupedStatistic.getRating();
	}

	@Override
	public int getSteps() {
		return groupedStatistic.getSteps();
	}

	@Override
	public Double getAvgDiffAbsolute() {
		return avgDiffAbsolute;
	}

	@Override
	public Double getAvgDiffRelative() {
		return avgDiffRelative;
	}
}
