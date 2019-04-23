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
package org.olat.admin.sysinfo;

import org.hibernate.stat.Statistics;
import org.olat.admin.sysinfo.manager.DatabaseStatsManager;
import org.olat.admin.sysinfo.model.DatabaseConnectionVO;
import org.olat.core.commons.persistence.DB;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 16.11.2012<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class HibernateStatisticsController extends BasicController {
	
	private final VelocityContainer mainVC;
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private DatabaseStatsManager databaseStatsManager;

	public HibernateStatisticsController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		
		mainVC = createVelocityContainer("hibernateinfo");
		
		loadModel();
		putInitialPanel(mainVC);
	}
	
	protected void loadModel() {
		Statistics statistics = dbInstance.getStatistics();
		mainVC.contextPut("isStatisticsEnabled", statistics.isStatisticsEnabled());
		mainVC.contextPut("hibernateStatistics", statistics);
		
		DatabaseConnectionVO connectionInfos = databaseStatsManager.getConnectionInfos();
		mainVC.contextPut("connectionInfos", connectionInfos);
	}
	
	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		//
	}
}