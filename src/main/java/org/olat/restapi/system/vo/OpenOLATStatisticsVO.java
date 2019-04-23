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
package org.olat.restapi.system.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "openolatStatisticsVO")
public class OpenOLATStatisticsVO {

	private SessionsVO sessions;
	private UserStatisticsVO userStatistics;
	private IndexerStatisticsVO indexerStatistics;
	private RepositoryStatisticsVO repositoryStatistics;

	public OpenOLATStatisticsVO() {
		//
	}

	public SessionsVO getSessions() {
		return sessions;
	}

	public void setSessions(SessionsVO sessions) {
		this.sessions = sessions;
	}

	public UserStatisticsVO getUserStatistics() {
		return userStatistics;
	}

	public void setUserStatistics(UserStatisticsVO userStatistics) {
		this.userStatistics = userStatistics;
	}

	public IndexerStatisticsVO getIndexerStatistics() {
		return indexerStatistics;
	}

	public void setIndexerStatistics(IndexerStatisticsVO indexerStatistics) {
		this.indexerStatistics = indexerStatistics;
	}

	public RepositoryStatisticsVO getRepositoryStatistics() {
		return repositoryStatistics;
	}

	public void setRepositoryStatistics(RepositoryStatisticsVO repositoryStatistics) {
		this.repositoryStatistics = repositoryStatistics;
	}
	
	

	
}
