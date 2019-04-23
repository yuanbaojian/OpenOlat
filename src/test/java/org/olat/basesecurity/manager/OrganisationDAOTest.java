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
package org.olat.basesecurity.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.OrganisationService;
import org.olat.basesecurity.OrganisationStatus;
import org.olat.basesecurity.OrganisationType;
import org.olat.basesecurity.model.OrganisationImpl;
import org.olat.basesecurity.model.OrganisationMember;
import org.olat.basesecurity.model.OrganisationNode;
import org.olat.basesecurity.model.OrganisationRefImpl;
import org.olat.basesecurity.model.SearchMemberParameters;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.core.id.Organisation;
import org.olat.core.id.OrganisationRef;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 9 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class OrganisationDAOTest extends OlatTestCase {
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private OrganisationDAO organisationDao;
	@Autowired
	private OrganisationTypeDAO organisationTypeDao;
	@Autowired
	private OrganisationService organisationService;
	
	@Test
	public void createOrganisation() {
		Organisation organisation = organisationDao.createAndPersistOrganisation("Org-4", null, null, null, null);
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(organisation);
		Assert.assertNotNull(organisation.getKey());
		Assert.assertNotNull(organisation.getCreationDate());
		Assert.assertNotNull(organisation.getLastModified());
	}
	
	@Test
	public void createOrganisation_allAttributes() {
		OrganisationType type = organisationTypeDao.createAndPersist("Org-Type", "OT", null);
		Organisation organisation = organisationDao
				.createAndPersistOrganisation("Org-5", "ORG-5", null, null, type);
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(organisation);
		
		OrganisationImpl reloadedOrganisation = (OrganisationImpl)organisationDao.loadByKey(organisation.getKey());
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(reloadedOrganisation.getKey());
		Assert.assertNotNull(reloadedOrganisation.getCreationDate());
		Assert.assertNotNull(reloadedOrganisation.getLastModified());
		Assert.assertNotNull(reloadedOrganisation.getGroup());
		Assert.assertEquals("Org-5", reloadedOrganisation.getDisplayName());
		Assert.assertEquals("ORG-5", reloadedOrganisation.getIdentifier());
		Assert.assertEquals(type, reloadedOrganisation.getType());
	}
	
	@Test
	public void createOrganisationWithParent() {
		Organisation parentOrganisation = organisationDao.createAndPersistOrganisation("Org-10", null, null, null, null);
		Organisation organisation = organisationDao.createAndPersistOrganisation("Org-10", null, null, parentOrganisation, null);
		dbInstance.commitAndCloseSession();

		Assert.assertNotNull(organisation);
		Assert.assertNotNull(organisation.getKey());
		Assert.assertNotNull(organisation.getCreationDate());
		Assert.assertNotNull(organisation.getLastModified());
		
		// check the ad-hoc parent line
		List<OrganisationRef> parentLine = organisation.getParentLine();
		Assert.assertNotNull(parentLine);
		Assert.assertEquals(1, parentLine.size());
		Assert.assertEquals(parentOrganisation.getKey(), parentLine.get(0).getKey());
		
	}
	
	@Test
	public void loadDefaultOrganisation() {
		List<Organisation> organisations = organisationDao.loadDefaultOrganisation();
		Assert.assertNotNull(organisations);
		Assert.assertEquals(1, organisations.size());
		Assert.assertEquals(OrganisationService.DEFAULT_ORGANISATION_IDENTIFIER, organisations.get(0).getIdentifier());
	}
	
	@Test
	public void findAllOrganisations() {
		String identifier = UUID.randomUUID().toString();
		Organisation organisation = organisationDao.createAndPersistOrganisation("Org-1", identifier, null, null, null);
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(organisation);
		
		List<Organisation> allOrganisations = organisationDao.find(OrganisationStatus.values());
		Assert.assertNotNull(allOrganisations);
		Assert.assertFalse(allOrganisations.isEmpty());
		Assert.assertTrue(allOrganisations.contains(organisation));
	}
	
	@Test
	public void getMembers() {
		Identity member = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-1");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation = organisationDao.createAndPersistOrganisation("OpenOLAT EE", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation, member, OrganisationRoles.user);
		dbInstance.commitAndCloseSession();
		
		SearchMemberParameters params = new SearchMemberParameters();
		List<OrganisationMember> members = organisationDao.getMembers(organisation, params);
		Assert.assertNotNull(members);
		Assert.assertEquals(1, members.size());
		OrganisationMember organisationMember = members.get(0);
		Assert.assertEquals(member, organisationMember.getIdentity());
		Assert.assertEquals(OrganisationRoles.user.name(), organisationMember.getRole());
	}
	
	@Test
	public void getMembersIdentity() {
		Identity member = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-1");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation = organisationDao.createAndPersistOrganisation("OpenOLAT EI", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation, member, OrganisationRoles.user);
		dbInstance.commitAndCloseSession();
		
		// get users
		List<Identity> members = organisationDao.getMembersIdentity(organisation, OrganisationRoles.user.name());
		Assert.assertNotNull(members);
		Assert.assertEquals(1, members.size());
		
		// but there is no managers
		List<Identity> rolesManagers = organisationDao.getMembersIdentity(organisation, OrganisationRoles.rolesmanager.name());
		Assert.assertNotNull(rolesManagers);
		Assert.assertTrue(rolesManagers.isEmpty());
	}
	
	@Test
	public void getIdentities_organisationIdentifier() {
		Identity member1 = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-2");
		Identity member2 = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-3");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation = organisationDao.createAndPersistOrganisation("Org 6", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation, member1, OrganisationRoles.groupmanager);
		organisationService.addMember(organisation, member2, OrganisationRoles.usermanager);
		dbInstance.commitAndCloseSession();
		
		List<Identity> userManagers = organisationDao.getIdentities(identifier, OrganisationRoles.usermanager.name());
		Assert.assertNotNull(userManagers);
		Assert.assertEquals(1, userManagers.size());
		Assert.assertEquals(member2, userManagers.get(0));
		Assert.assertFalse(userManagers.contains(member1));
	}
	
	@Test
	public void getIdentities_role() {
		Identity member1 = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-4");
		Identity member2 = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-5");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation1 = organisationDao.createAndPersistOrganisation("Org 8", identifier, null, null, null);
		Organisation organisation2 = organisationDao.createAndPersistOrganisation("Org 9", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation1, member1, OrganisationRoles.groupmanager);
		organisationService.addMember(organisation1, member2, OrganisationRoles.usermanager);
		organisationService.addMember(organisation2, member1, OrganisationRoles.usermanager);
		organisationService.addMember(organisation2, member2, OrganisationRoles.usermanager);
		dbInstance.commitAndCloseSession();
		
		List<Identity> userManagers = organisationDao.getIdentities(OrganisationRoles.usermanager.name());
		Assert.assertNotNull(userManagers);
		Assert.assertTrue(userManagers.contains(member1));
		Assert.assertTrue(userManagers.contains(member2));
		
		List<Identity> groupManagers = organisationDao.getIdentities(OrganisationRoles.groupmanager.name());
		Assert.assertNotNull(groupManagers);
		Assert.assertTrue(groupManagers.contains(member1));
		Assert.assertFalse(groupManagers.contains(member2));
		
		List<Identity> poolManagers = organisationDao.getIdentities(OrganisationRoles.poolmanager.name());
		Assert.assertNotNull(poolManagers);
		Assert.assertFalse(poolManagers.contains(member1));
		Assert.assertFalse(poolManagers.contains(member2));
	}
	
	@Test
	public void getOrganisations_identity() {
		Identity member = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-6");
		String identifier = UUID.randomUUID().toString();
		Organisation defOrganisation = organisationService.getDefaultOrganisation();
		Organisation organisation1 = organisationDao.createAndPersistOrganisation("Org 10", identifier, null, null, null);
		Organisation organisation2 = organisationDao.createAndPersistOrganisation("Org 11", identifier, null, null, null);
		Organisation organisation3 = organisationDao.createAndPersistOrganisation("Org 12", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation1, member, OrganisationRoles.user);
		organisationService.addMember(organisation1, member, OrganisationRoles.usermanager);
		organisationService.addMember(organisation2, member, OrganisationRoles.user);
		organisationService.addMember(organisation3, member, OrganisationRoles.user);
		organisationService.addMember(organisation3, member, OrganisationRoles.poolmanager);
		dbInstance.commitAndCloseSession();
		
		List<String> managerRoles = new ArrayList<>();
		managerRoles.add(OrganisationRoles.usermanager.name());
		managerRoles.add(OrganisationRoles.groupmanager.name());
		managerRoles.add(OrganisationRoles.poolmanager.name());
		List<Organisation> managedOrganisations = organisationDao.getOrganisations(member, managerRoles);
		Assert.assertEquals(2, managedOrganisations.size());
		Assert.assertTrue(managedOrganisations.contains(organisation1));
		Assert.assertTrue(managedOrganisations.contains(organisation3));
		
		List<String> userRole = Collections.singletonList(OrganisationRoles.user.name());
		List<Organisation> organisations = organisationDao.getOrganisations(member, userRole);
		Assert.assertEquals(4, organisations.size());
		Assert.assertTrue(organisations.contains(organisation1));
		Assert.assertTrue(organisations.contains(organisation2));
		Assert.assertTrue(organisations.contains(organisation3));
		Assert.assertTrue(organisations.contains(defOrganisation));	
	}
	
	@Test
	public void getOrganisations_references() {
		String identifier = UUID.randomUUID().toString();
		Organisation organisation1 = organisationDao.createAndPersistOrganisation("Org 13", identifier, null, null, null);
		Organisation organisation2 = organisationDao.createAndPersistOrganisation("Org 14", identifier, null, null, null);
		Organisation organisation3 = organisationDao.createAndPersistOrganisation("Org 15", identifier, null, null, null);
		dbInstance.commitAndCloseSession();
		
		List<OrganisationRef> twoOrganisationRefs = new ArrayList<>();
		twoOrganisationRefs.add(new OrganisationRefImpl(organisation1.getKey()));
		twoOrganisationRefs.add(new OrganisationRefImpl(organisation3.getKey()));
		
		List<Organisation> organisations = organisationDao.getOrganisations(twoOrganisationRefs);
		Assert.assertEquals(2, organisations.size());
		Assert.assertTrue(organisations.contains(organisation1));
		Assert.assertFalse(organisations.contains(organisation2));
		Assert.assertTrue(organisations.contains(organisation3));
	}
	
	@Test
	public void getOrganisations_referencesEmpty() {
		List<OrganisationRef> noOrganisationRefs = new ArrayList<>();
		List<Organisation> organisations = organisationDao.getOrganisations(noOrganisationRefs);
		Assert.assertNotNull(organisations);
		Assert.assertTrue(organisations.isEmpty());
	}
	
	@Test
	public void getOrganisations_referencesNull() {
		List<Organisation> organisations = organisationDao.getOrganisations(null);
		Assert.assertNotNull(organisations);
		Assert.assertTrue(organisations.isEmpty());
	}
	
	@Test
	public void getChildren() {
		String identifier = UUID.randomUUID().toString();
		Organisation rootOrganisation = organisationDao.createAndPersistOrganisation("Root", identifier, null, null, null);
		Organisation organisation_1 = organisationDao.createAndPersistOrganisation("Level 1.1", identifier + ".1", null, rootOrganisation, null);
		Organisation organisation_2 = organisationDao.createAndPersistOrganisation("Level 1.2", identifier + ".2", null, rootOrganisation, null);
		dbInstance.commitAndCloseSession();
		Organisation organisation2_1 = organisationDao.createAndPersistOrganisation("Level 1.2.1", identifier, null, organisation_2, null);
		dbInstance.commitAndCloseSession();
		
		// get the children
		List<Organisation> children = organisationDao.getChildren(rootOrganisation, new OrganisationStatus[] { OrganisationStatus.active });
		Assert.assertNotNull(children);
		Assert.assertTrue(children.contains(organisation_1));
		Assert.assertTrue(children.contains(organisation_2));
		Assert.assertFalse(children.contains(organisation2_1));
	}
	
	@Test
	public void getDescendants() {
		String identifier = UUID.randomUUID().toString();
		Organisation defOrganisation = organisationService.getDefaultOrganisation();
		Organisation rootOrganisation = organisationDao.createAndPersistOrganisation("Root 1", identifier, null, defOrganisation, null);
		Organisation organisation2_1 = organisationDao.createAndPersistOrganisation("Level 2.1", identifier, null, rootOrganisation, null);
		Organisation organisation2_2 = organisationDao.createAndPersistOrganisation("Level 2.2", identifier, null, rootOrganisation, null);
		dbInstance.commitAndCloseSession();
		Organisation organisation2_1_1 = organisationDao.createAndPersistOrganisation("Level 3.1", identifier, null, organisation2_1, null);
		Organisation organisation2_1_2 = organisationDao.createAndPersistOrganisation("Level 3.2", identifier, null, organisation2_1, null);
		Organisation organisation2_1_3 = organisationDao.createAndPersistOrganisation("Level 3.3", identifier, null, organisation2_1, null);
		dbInstance.commitAndCloseSession();

		List<Organisation> rootDescendants = organisationDao.getDescendants(rootOrganisation);
		Assert.assertNotNull(rootDescendants);
		Assert.assertEquals(5, rootDescendants.size());
		Assert.assertTrue(rootDescendants.contains(organisation2_1));
		Assert.assertTrue(rootDescendants.contains(organisation2_2));
		Assert.assertTrue(rootDescendants.contains(organisation2_1_1));
		Assert.assertTrue(rootDescendants.contains(organisation2_1_2));
		Assert.assertTrue(rootDescendants.contains(organisation2_1_3));	
	}
	
	@Test
	public void getDescendants_leaf() {
		String identifier = UUID.randomUUID().toString();
		Organisation defOrganisation = organisationService.getDefaultOrganisation();
		Organisation organisation = organisationDao.createAndPersistOrganisation("Root 1", identifier, null, defOrganisation, null);
		dbInstance.commitAndCloseSession();

		List<Organisation> descendants = organisationDao.getDescendants(organisation);
		Assert.assertNotNull(descendants);
		Assert.assertTrue(descendants.isEmpty());
	}
	
	@Test
	public void getDescendantTree() {
		String identifier = UUID.randomUUID().toString();
		Organisation defOrganisation = organisationService.getDefaultOrganisation();
		Organisation rootOrganisation = organisationDao.createAndPersistOrganisation("Root 2", identifier, null, defOrganisation, null);
		Organisation organisation2_1 = organisationDao.createAndPersistOrganisation("Tree2 2.1", identifier, null, rootOrganisation, null);
		Organisation organisation2_2 = organisationDao.createAndPersistOrganisation("Tree2 2.2", identifier, null, rootOrganisation, null);
		dbInstance.commitAndCloseSession();
		Organisation organisation2_1_1 = organisationDao.createAndPersistOrganisation("Tree2 3.1", identifier, null, organisation2_1, null);
		Organisation organisation2_1_2 = organisationDao.createAndPersistOrganisation("Tree2 3.2", identifier, null, organisation2_1, null);
		Organisation organisation2_1_3 = organisationDao.createAndPersistOrganisation("Tree2 3.3", identifier, null, organisation2_1, null);
		Organisation organisation2_2_1 = organisationDao.createAndPersistOrganisation("Tree2 3.4", identifier, null, organisation2_2, null);
		dbInstance.commitAndCloseSession();
		Organisation organisation2_2_1_1 = organisationDao.createAndPersistOrganisation("Tree2 4.1", identifier, null, organisation2_2_1, null);
		
		// load the tree
		OrganisationNode rootNode = organisationDao.getDescendantTree(rootOrganisation);
		Assert.assertNotNull(rootNode);
		// level 2
		OrganisationNode node2_1 = rootNode.getChild(organisation2_1);
		OrganisationNode node2_2 = rootNode.getChild(organisation2_2);
		Assert.assertNotNull(node2_1);
		Assert.assertNotNull(node2_2);
		// level 3
		OrganisationNode node2_1_1 = node2_1.getChild(organisation2_1_1);
		OrganisationNode node2_1_2 = node2_1.getChild(organisation2_1_2);
		OrganisationNode node2_1_3 = node2_1.getChild(organisation2_1_3);
		Assert.assertNotNull(node2_1_1);
		Assert.assertNotNull(node2_1_2);
		Assert.assertNotNull(node2_1_3);
		OrganisationNode node2_2_1 = node2_2.getChild(organisation2_2_1);
		Assert.assertNotNull(node2_2_1);
		// level 4
		OrganisationNode node2_2_1_1 = node2_2_1.getChild(organisation2_2_1_1);
		Assert.assertNotNull(node2_2_1_1);
	}
	
	@Test
	public void getDescendantTree_leaf() {
		String identifier = UUID.randomUUID().toString();
		Organisation defOrganisation = organisationService.getDefaultOrganisation();
		Organisation organisation = organisationDao.createAndPersistOrganisation("Root 3", identifier, null, defOrganisation, null);
		dbInstance.commitAndCloseSession();

		OrganisationNode rootNode = organisationDao.getDescendantTree(organisation);
		Assert.assertNotNull(rootNode);
		Assert.assertTrue(rootNode.getChildrenNode().isEmpty());
	}
	
	@Test
	public void getParentLine() {
		String identifier = UUID.randomUUID().toString();
		Organisation defOrganisation = organisationService.getDefaultOrganisation();
		Organisation rootOrganisation = organisationDao.createAndPersistOrganisation("Root 4", identifier, null, defOrganisation, null);
		Organisation organisation2_1 = organisationDao.createAndPersistOrganisation("Tree4 2.1", identifier, null, rootOrganisation, null);
		Organisation organisation2_2 = organisationDao.createAndPersistOrganisation("Tree4 2.2", identifier, null, rootOrganisation, null);
		dbInstance.commitAndCloseSession();
		Organisation organisation2_1_1 = organisationDao.createAndPersistOrganisation("Tree4 3.1", identifier, null, organisation2_1, null);
		Organisation organisation2_1_2 = organisationDao.createAndPersistOrganisation("Tree4 3.2", identifier, null, organisation2_1, null);
		Organisation organisation2_2_1 = organisationDao.createAndPersistOrganisation("Tree4 3.4", identifier, null, organisation2_2, null);
		dbInstance.commitAndCloseSession();
		Organisation organisation2_2_1_1 = organisationDao.createAndPersistOrganisation("Tree4 4-1", identifier, null, organisation2_2_1, null);
		dbInstance.commitAndCloseSession();
		
		// check parent line of the deepest node
		List<Organisation> parentLine2_2_1_1 = organisationDao.getParentLine(organisation2_2_1_1);
		Assert.assertNotNull(parentLine2_2_1_1);
		Assert.assertEquals(5, parentLine2_2_1_1.size());
		Assert.assertEquals(defOrganisation, parentLine2_2_1_1.get(0));
		Assert.assertEquals(rootOrganisation, parentLine2_2_1_1.get(1));
		Assert.assertEquals(organisation2_2, parentLine2_2_1_1.get(2));
		Assert.assertEquals(organisation2_2_1, parentLine2_2_1_1.get(3));
		Assert.assertEquals(organisation2_2_1_1, parentLine2_2_1_1.get(4));
		
		// check parent line of other
		List<Organisation> parentLine2_1_2 = organisationDao.getParentLine(organisation2_1_2);
		Assert.assertNotNull(parentLine2_1_2);
		Assert.assertEquals(4, parentLine2_1_2.size());
		Assert.assertEquals(defOrganisation, parentLine2_1_2.get(0));
		Assert.assertEquals(rootOrganisation, parentLine2_1_2.get(1));
		Assert.assertEquals(organisation2_1, parentLine2_1_2.get(2));
		Assert.assertEquals(organisation2_1_2, parentLine2_1_2.get(3));
		
		// check parent line of other
		List<Organisation> parentLine2_1_1 = organisationDao.getParentLine(organisation2_1_1);
		Assert.assertNotNull(parentLine2_1_1);
		Assert.assertEquals(4, parentLine2_1_1.size());
		Assert.assertEquals(defOrganisation, parentLine2_1_1.get(0));
		Assert.assertEquals(rootOrganisation, parentLine2_1_1.get(1));
		Assert.assertEquals(organisation2_1, parentLine2_1_1.get(2));
		Assert.assertEquals(organisation2_1_1, parentLine2_1_1.get(3));
		
		// check parent line of def
		List<Organisation> parentLineDef = organisationDao.getParentLine(defOrganisation);
		Assert.assertNotNull(parentLineDef);
		Assert.assertEquals(1, parentLineDef.size());
		Assert.assertEquals(defOrganisation, parentLineDef.get(0));
	}

	@Test
	public void hasRole_identifier() {
		Identity member = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-7");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation = organisationDao.createAndPersistOrganisation("OpenOLAT E2E", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation, member, OrganisationRoles.poolmanager);
		dbInstance.commitAndCloseSession();
		
		boolean isPoolManager = organisationDao.hasRole(member, identifier, null, OrganisationRoles.poolmanager.name());
		Assert.assertTrue(isPoolManager);
		boolean isUserManager = organisationDao.hasRole(member, identifier, null, OrganisationRoles.usermanager.name());
		Assert.assertFalse(isUserManager);
		boolean isNotPoolManager = organisationDao.hasRole(member, "something else", null, OrganisationRoles.poolmanager.name());
		Assert.assertFalse(isNotPoolManager);
	}
	
	@Test
	public void hasRole_organisation() {
		Identity member = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-7b");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation = organisationDao.createAndPersistOrganisation("OpenOLAT E3E", identifier, null, null, null);
		Organisation otherOrganisation = organisationDao.createAndPersistOrganisation("OpenOLAT other one", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation, member, OrganisationRoles.poolmanager);
		dbInstance.commitAndCloseSession();
		
		boolean isPoolManager = organisationDao.hasRole(member, null, organisation, OrganisationRoles.poolmanager.name());
		Assert.assertTrue(isPoolManager);
		boolean isUserManager = organisationDao.hasRole(member, null, organisation, OrganisationRoles.usermanager.name());
		Assert.assertFalse(isUserManager);
		boolean isNotPoolManager = organisationDao.hasRole(member, null, otherOrganisation, OrganisationRoles.poolmanager.name());
		Assert.assertFalse(isNotPoolManager);
	}
	
	@Test
	public void hasRole() {
		Identity member = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-7c");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation = organisationDao.createAndPersistOrganisation("OpenOLAT E4E", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation, member, OrganisationRoles.poolmanager);
		dbInstance.commitAndCloseSession();
		
		boolean isManager = organisationDao.hasRole(member, identifier, organisation,
				OrganisationRoles.poolmanager.name(), OrganisationRoles.usermanager.name());
		Assert.assertTrue(isManager);
	}
	
	@Test
	public void hasRole_administrator() {
		Identity administrator = securityManager.findIdentityByName("administrator");
		Organisation defOrganisation = organisationService.getDefaultOrganisation();
		dbInstance.commit();

		boolean isManager = organisationDao.hasRole(administrator, null, defOrganisation,
				OrganisationRoles.administrator.name(), OrganisationRoles.usermanager.name());
		Assert.assertTrue(isManager);
	}
	
	@Test
	public void hasAnyRole() {
		Identity member = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-8");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation = organisationDao.createAndPersistOrganisation("Org. 8", identifier, null, null, null);
		dbInstance.commit();
		organisationService.addMember(organisation, member, OrganisationRoles.user);
		dbInstance.commitAndCloseSession();
		
		boolean hasNot = organisationDao.hasAnyRole(member, OrganisationRoles.user.name());
		Assert.assertFalse(hasNot);
		boolean has = organisationDao.hasAnyRole(member, OrganisationRoles.usermanager.name());
		Assert.assertTrue(has);
	}
	
	@Test
	public void getIdentitiesWithoutOrganisations() {
		Identity member = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-9");
		Identity notMember = JunitTestHelper.createAndPersistIdentityAsRndUser("Member-10");
		String identifier = UUID.randomUUID().toString();
		Organisation organisation1 = organisationDao.createAndPersistOrganisation("Org 12", identifier, null, null, null);
		dbInstance.commit();
		// remove def. organisation and make some noise
		Organisation defOrganisation = organisationService.getDefaultOrganisation();
		organisationService.removeMember(defOrganisation, notMember);
		organisationService.addMember(organisation1, member, OrganisationRoles.user);
		dbInstance.commitAndCloseSession();
		
		// check
		List<Identity> identities = organisationDao.getIdentitiesWithoutOrganisations();
		Assert.assertFalse(identities.isEmpty());
		Assert.assertFalse(identities.contains(member));
		Assert.assertTrue(identities.contains(notMember));
	}
}
