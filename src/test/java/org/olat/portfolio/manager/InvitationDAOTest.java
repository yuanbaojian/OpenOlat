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
package org.olat.portfolio.manager;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.Group;
import org.olat.basesecurity.Invitation;
import org.olat.basesecurity.manager.GroupDAO;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.portfolio.model.structel.PortfolioStructureMap;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 25.06.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class InvitationDAOTest extends OlatTestCase {
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private GroupDAO groupDao;
	@Autowired
	private InvitationDAO invitationDao;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private EPPolicyManager policyManager;
	@Autowired
	private EPFrontendManager epFrontendManager;
	
	
	@Test
	public void createAndPersistInvitation() {
		Invitation invitation = invitationDao.createAndPersistInvitation();
		Assert.assertNotNull(invitation);
		dbInstance.commit();
		
		Assert.assertNotNull(invitation);
		Assert.assertNotNull(invitation.getKey());
		Assert.assertNotNull(invitation.getBaseGroup());
		Assert.assertNotNull(invitation.getToken());
	}
	
	@Test
	public void findInvitation_group() {
		Invitation invitation = invitationDao.createAndPersistInvitation();
		Group baseGroup = invitation.getBaseGroup();
		Assert.assertNotNull(invitation);
		dbInstance.commitAndCloseSession();
		
		Invitation reloadedInvitation = invitationDao.findInvitation(baseGroup);
		Assert.assertNotNull(reloadedInvitation);
		Assert.assertNotNull(reloadedInvitation.getKey());
		Assert.assertEquals(baseGroup, reloadedInvitation.getBaseGroup());
		Assert.assertEquals(invitation, reloadedInvitation);
		Assert.assertEquals(invitation.getToken(), reloadedInvitation.getToken());
	}
	
	@Test
	public void findInvitation_token() {
		Invitation invitation = invitationDao.createAndPersistInvitation();
		Assert.assertNotNull(invitation);
		dbInstance.commitAndCloseSession();
		
		Invitation reloadedInvitation = invitationDao.findInvitation(invitation.getToken());
		Assert.assertNotNull(reloadedInvitation);
		Assert.assertNotNull(reloadedInvitation.getKey());
		Assert.assertNotNull(reloadedInvitation.getBaseGroup());
		Assert.assertEquals(invitation, reloadedInvitation);
		Assert.assertEquals(invitation.getToken(), reloadedInvitation.getToken());
	}
	
	
	@Test
	public void hasInvitationPolicies_testHQL() {
		String token = UUID.randomUUID().toString();
		Date atDate = new Date();
		boolean hasInvitation = invitationDao.hasInvitations(token, atDate);
		Assert.assertFalse(hasInvitation);
	}
	
	@Test
	public void createAndUpdateInvitation() {
		Invitation invitation = invitationDao.createAndPersistInvitation();
		dbInstance.commit();

		Invitation updatedInvitation = invitationDao.update(invitation, "Kanu", "Unchou", "kanu.unchou@frentix.com");
		dbInstance.commit();
		
		Assert.assertEquals("Kanu", updatedInvitation.getFirstName());
		Assert.assertEquals("Unchou", updatedInvitation.getLastName());
		Assert.assertEquals("kanu.unchou@frentix.com", updatedInvitation.getMail());
		
		Invitation reloadedInvitation = invitationDao.findInvitation(invitation.getToken());
		Assert.assertEquals("Kanu", reloadedInvitation.getFirstName());
		Assert.assertEquals("Unchou", reloadedInvitation.getLastName());
		Assert.assertEquals("kanu.unchou@frentix.com", reloadedInvitation.getMail());
	}
	
	@Test
	public void createIdentityFrom_invitation() {
		Invitation invitation = invitationDao.createAndPersistInvitation();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		invitation = invitationDao.update(invitation, "Clara", uuid, uuid + "@frentix.com");
		dbInstance.commit();
		
		// create the identity of the invitee
		Identity invitee = invitationDao.createIdentityFrom(invitation, Locale.ENGLISH);
		Assert.assertNotNull(invitee);
		Assert.assertNotNull(invitee.getKey());
		dbInstance.commitAndCloseSession();
		
		// reload and check
		Identity reloadIdentity = securityManager.loadIdentityByKey(invitee.getKey());
		Assert.assertNotNull(reloadIdentity);
		Assert.assertNotNull(reloadIdentity.getUser());
		Assert.assertEquals(invitee.getKey(), reloadIdentity.getKey());
		Assert.assertEquals("Clara", reloadIdentity.getUser().getFirstName());
		Assert.assertEquals(uuid, reloadIdentity.getUser().getLastName());
		Assert.assertEquals(uuid + "@frentix.com", reloadIdentity.getUser().getEmail());
	}
	
	@Test
	public void loadOrCreateIdentityAndPersistInvitation() {
		Invitation invitation = invitationDao.createAndPersistInvitation();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		invitation = invitationDao.update(invitation, "Flora", uuid, uuid + "@frentix.com");
		Group group = groupDao.createGroup();
		dbInstance.commit();
		
		// use the create part of the method
		Identity identity = invitationDao.loadOrCreateIdentityAndPersistInvitation(invitation, group, Locale.ENGLISH);
		Assert.assertNotNull(identity);
		Assert.assertNotNull(identity.getKey());
		dbInstance.commitAndCloseSession();
		
		// reload and check
		Identity reloadIdentity = securityManager.loadIdentityByKey(identity.getKey());
		Assert.assertNotNull(reloadIdentity);
		Assert.assertNotNull(reloadIdentity.getUser());
		Assert.assertEquals(identity.getKey(), reloadIdentity.getKey());
		Assert.assertEquals("Flora", reloadIdentity.getUser().getFirstName());
		Assert.assertEquals(uuid, reloadIdentity.getUser().getLastName());
		Assert.assertEquals(uuid + "@frentix.com", reloadIdentity.getUser().getEmail());
	}
	
	@Test
	public void countInvitations() {
		Invitation invitation = invitationDao.createAndPersistInvitation();
		dbInstance.commit();
		Assert.assertNotNull(invitation);
		
		long numOfInvitations = invitationDao.countInvitations();
		Assert.assertTrue(numOfInvitations > 0l);
	}
	
	/**
	 * Check the HQL code of the the method, and that it doesn't delete to much invitations
	 */
	@Test
	public void cleanUpInvitation() {
		Identity user = JunitTestHelper.createAndPersistIdentityAsRndUser("Policy-User-2-");
		PortfolioStructureMap map = epFrontendManager.createAndPersistPortfolioDefaultMap(user, "Title", "Description");
		Invitation invitation = invitationDao.createAndPersistInvitation();
		dbInstance.commit();
		
		invitation.setFirstName("John");
		invitation.setLastName("Smith Portfolio");
		EPMapPolicy policy = new EPMapPolicy();
		policy.setType(EPMapPolicy.Type.invitation);
		policy.setInvitation(invitation);
		
		policyManager.updateMapPolicies(map, Collections.singletonList(policy));
		dbInstance.commitAndCloseSession();
		
		//convert invitation to identity
		Identity invitee = invitationDao.createIdentityFrom(invitation, Locale.ENGLISH);
		dbInstance.commitAndCloseSession();

		//and check 
		boolean visible = epFrontendManager.isMapVisible(invitee, map.getOlatResource());
		Assert.assertTrue(visible);
		
		//clean the invitations
		invitationDao.cleanUpInvitations();
		dbInstance.commitAndCloseSession();
		
		//check that the invitation not was not deleted
		boolean afterVisible = epFrontendManager.isMapVisible(invitee, map.getOlatResource());
		Assert.assertTrue(afterVisible);
	}

}
