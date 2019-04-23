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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.olat.admin.registration.SystemRegistrationManager;
import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.persistence.DB;
import org.olat.core.util.StringHelper;
import org.olat.instantMessaging.InstantMessagingModule;
import org.olat.restapi.system.vo.MonitoringDependencyVO;
import org.olat.restapi.system.vo.MonitoringInfosVO;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class MonitoringWebService {
	
	private static final MemoryWebService memoryWebService = new MemoryWebService();
	private static final ThreadsWebService threadsWebService = new ThreadsWebService();
	private static final OpenOLATStatisticsWebService ooStatsWebService = new OpenOLATStatisticsWebService();
	
	public MonitoringWebService() {
		//make Spring happy
	}
	
	@Path("status")
	public StatusWebservice getStatus() {
		return new StatusWebservice();
	}

	@Path("runtime")
	public RuntimeWebService getCompilationXml() {
		return new RuntimeWebService();
	}
	
	@Path("database")
	public DatabaseWebService getDatabaseWS() {
		return new DatabaseWebService();
	}
	
	@Path("openolat")
	public OpenOLATStatisticsWebService getSessionsWS() {
		return ooStatsWebService;
	}
	
	@Path("memory")
	public MemoryWebService getMemoryWS() {
		return memoryWebService;
	}

	@Path("threads")
	public ThreadsWebService getThreadsWS() {
		return threadsWebService;
	}
	
	
	/**
	 * Return the configuration of the monitoring, which probes are available,
	 * which dependency...
	 * 
	 * @response.representation.200.qname {http://www.example.com}releaseVO
   * @response.representation.200.mediaType application/xml, application/json
   * @response.representation.200.doc The verison of the instance
   * @response.representation.200.example {@link org.olat.restapi.system.vo.Examples#SAMPLE_MONITORINGCONFIGVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
   * @param request The HTTP request
	 * @return The informations about the memory
	 */
	@GET
	@Path("configuration")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getImplementedProbes() {
		MonitoringInfosVO vo = new MonitoringInfosVO();

		MonitoringModule module = CoreSpringFactory.getImpl(MonitoringModule.class);
		String probesConfig = module.getMonitoredProbes();
		String[] probes;
		if(StringHelper.containsNonWhitespace(probesConfig)) {
			probes = probesConfig.split(",");
		} else {
			probes = new String[0];
		}
		vo.setProbes(probes);
		vo.setType(SystemRegistrationManager.PRODUCT);
		vo.setDescription(module.getDescription());
		
		List<MonitoringDependencyVO> dependencies = new ArrayList<>();
		InstantMessagingModule imConfig = CoreSpringFactory.getImpl(InstantMessagingModule.class);
		if(imConfig.isEnabled()) {
			MonitoringDependencyVO dependency = new MonitoringDependencyVO();
			dependency.setType("openfire");
			dependency.setUrl("");
			dependencies.add(dependency);	
		}
		
		DB dbInstance = CoreSpringFactory.getImpl(DB.class);
		MonitoringDependencyVO dependency = new MonitoringDependencyVO();
		dependency.setType(dbInstance.getDbVendor());
		dependency.setUrl(module.getDatabaseHost());
		dependencies.add(dependency);	
		
		MonitoringDependencyVO dependencyServer = new MonitoringDependencyVO();
		dependencyServer.setType("server");
		dependencyServer.setUrl(module.getServer());
		dependencies.add(dependencyServer);	
		
		
		vo.setDependencies(dependencies.toArray(new MonitoringDependencyVO[dependencies.size()]));
		return Response.ok(vo).build();
	}
	
	public static void takeSample() {
		memoryWebService.takeSample();
		threadsWebService.takeSample();
		ooStatsWebService.takeSample();
	}
}
