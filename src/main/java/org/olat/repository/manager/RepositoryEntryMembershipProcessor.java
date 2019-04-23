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
package org.olat.repository.manager;

import java.util.List;

import org.olat.basesecurity.IdentityRef;
import org.olat.basesecurity.model.IdentityRefImpl;
import org.olat.core.commons.services.notifications.NotificationsManager;
import org.olat.core.gui.control.Event;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.olat.core.util.event.GenericEventListener;
import org.olat.core.util.resource.OresHelper;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryEntryRef;
import org.olat.repository.RepositoryManager;
import org.olat.repository.model.RepositoryEntryMembershipModifiedEvent;
import org.olat.repository.model.RepositoryEntryRefImpl;
import org.olat.resource.OLATResource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Process the removed membership of repository entries.
 * 
 * Initial date: 09.02.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class RepositoryEntryMembershipProcessor implements InitializingBean, GenericEventListener {
	
	@Autowired
	private CoordinatorManager coordinator;
	@Autowired
	private NotificationsManager notificationsManager;
	@Autowired
	private RepositoryManager repositoryManager;
	@Autowired
	private RepositoryEntryRelationDAO repositoryEntryRelationDao;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		coordinator.getCoordinator().getEventBus().registerFor(this, null, OresHelper.lookupType(RepositoryEntry.class));
	}

	@Override
	public void event(Event event) {
		if(event instanceof RepositoryEntryMembershipModifiedEvent) {
			RepositoryEntryMembershipModifiedEvent e = (RepositoryEntryMembershipModifiedEvent)event;
			if(RepositoryEntryMembershipModifiedEvent.IDENTITY_REMOVED.equals(e.getCommand())) {
				processIdentityRemoved(e.getRepositoryEntryKey(), e.getIdentityKey());
			}
		}
	}
	
	private void processIdentityRemoved(Long repoKey, Long identityKey) {
		IdentityRef identity = new IdentityRefImpl(identityKey);
		RepositoryEntryRef re = new RepositoryEntryRefImpl(repoKey);
		
		List<String> remainingRoles = repositoryEntryRelationDao.getRoles(identity, re);
		if(remainingRoles.isEmpty()) {
			OLATResource resource = repositoryManager.lookupRepositoryEntryResource(repoKey);
			notificationsManager.unsubscribeAllForIdentityAndResId(identity, resource.getResourceableId());
		}
	}
}