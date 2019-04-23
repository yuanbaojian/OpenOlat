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
package org.olat.restapi.system;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.stat.Statistics;
import org.olat.admin.sysinfo.manager.DatabaseStatsManager;
import org.olat.admin.sysinfo.model.DatabaseConnectionVO;
import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.persistence.DBFactory;
import org.olat.restapi.system.vo.DatabaseVO;
import org.olat.restapi.system.vo.HibernateStatisticsVO;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class DatabaseWebService {
	
	/**
	 * Return the statistics about database and hibernate
	 * 
	 * @response.representation.200.qname {http://www.example.com}runtimeVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc The version of the instance
	 * @response.representation.200.example {@link org.olat.restapi.system.vo.Examples#SAMPLE_DATABASEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @param request The HTTP request
	 * @return The informations about runtime, uptime, classes loaded, memory summary...
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getDatabaseStatistics() {
		DatabaseConnectionVO connections = CoreSpringFactory.getImpl(DatabaseStatsManager.class)
				.getConnectionInfos();
		HibernateStatisticsVO hibernateStats = getHibernateStatistics();
		
		DatabaseVO vo = new DatabaseVO();
		vo.setConnectionInfos(connections);
		vo.setHibernateStatistics(hibernateStats);
		return Response.ok(vo).build();
	}
	
	private HibernateStatisticsVO getHibernateStatistics() {
		
		Statistics statistics = DBFactory.getInstance().getStatistics();
		if(!statistics.isStatisticsEnabled()) {
			return null;
		}
		
		HibernateStatisticsVO stats = new HibernateStatisticsVO();
		stats.setOpenSessionsCount(statistics.getSessionOpenCount());
		stats.setTransactionsCount(statistics.getTransactionCount());
		stats.setSuccessfulTransactionCount(statistics.getSuccessfulTransactionCount());
		stats.setFailedTransactionsCount(statistics.getTransactionCount() - statistics.getSuccessfulTransactionCount());
		stats.setOptimisticFailureCount(statistics.getOptimisticFailureCount());
		stats.setQueryExecutionCount(statistics.getQueryExecutionCount());
		stats.setQueryExecutionMaxTime(statistics.getQueryExecutionMaxTime());
		stats.setQueryExecutionMaxTimeQueryString(statistics.getQueryExecutionMaxTimeQueryString());
		return stats;
	}
}
