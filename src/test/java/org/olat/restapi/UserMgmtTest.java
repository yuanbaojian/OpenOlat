/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.  
* <p>
*/

package org.olat.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.GroupRoles;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.OrganisationService;
import org.olat.collaboration.CollaborationTools;
import org.olat.collaboration.CollaborationToolsFactory;
import org.olat.core.commons.modules.bc.FolderConfig;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.id.Organisation;
import org.olat.core.id.Roles;
import org.olat.core.id.User;
import org.olat.core.id.UserConstants;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.CodeHelper;
import org.olat.core.util.nodes.INode;
import org.olat.core.util.resource.OresHelper;
import org.olat.core.util.tree.TreeVisitor;
import org.olat.core.util.tree.Visitor;
import org.olat.core.util.vfs.LocalFolderImpl;
import org.olat.core.util.vfs.LocalImpl;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSItem;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.core.util.vfs.VFSManager;
import org.olat.course.CourseFactory;
import org.olat.course.ICourse;
import org.olat.course.nodes.BCCourseNode;
import org.olat.course.nodes.FOCourseNode;
import org.olat.group.BusinessGroup;
import org.olat.group.BusinessGroupService;
import org.olat.group.manager.BusinessGroupRelationDAO;
import org.olat.modules.fo.Forum;
import org.olat.modules.fo.Message;
import org.olat.modules.fo.manager.ForumManager;
import org.olat.modules.fo.restapi.ForumVO;
import org.olat.modules.fo.restapi.ForumVOes;
import org.olat.modules.fo.restapi.MessageVOes;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryService;
import org.olat.resource.OLATResource;
import org.olat.resource.OLATResourceManager;
import org.olat.restapi.support.vo.ErrorVO;
import org.olat.restapi.support.vo.FileVO;
import org.olat.restapi.support.vo.FolderVO;
import org.olat.restapi.support.vo.FolderVOes;
import org.olat.restapi.support.vo.GroupInfoVOes;
import org.olat.restapi.support.vo.GroupVO;
import org.olat.restapi.support.vo.GroupVOes;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatJerseyTestCase;
import org.olat.user.DisplayPortraitManager;
import org.olat.user.UserManager;
import org.olat.user.restapi.ManagedUserVO;
import org.olat.user.restapi.PreferencesVO;
import org.olat.user.restapi.RolesVO;
import org.olat.user.restapi.StatusVO;
import org.olat.user.restapi.UserVO;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Description:<br>
 * Test the <code>UserWebservice</code>
 * 
 * <P>
 * Initial Date:  15 apr. 2010 <br>
 * @author srosse, stephane.rosse@frentix.com
 */
public class UserMgmtTest extends OlatJerseyTestCase {
	
	private static final OLog log = Tracing.createLoggerFor(UserMgmtTest.class);
	
	private static Identity owner1, id1, id2, id3;
	private static BusinessGroup g1, g2, g3, g4;
	private static String g1externalId, g3ExternalId;
	
	private static ICourse demoCourse;
	private static FOCourseNode demoForumNode;
	private static BCCourseNode demoBCCourseNode;
	
	private static boolean setuped = false;

	
	@Autowired
	private DB dbInstance;
	@Autowired
	private ForumManager forumManager;
	@Autowired
	private BusinessGroupRelationDAO businessGroupRelationDao;
	@Autowired
	private BusinessGroupService businessGroupService;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private OrganisationService organisationService;
	@Autowired
	private UserManager userManager;
	@Autowired
	private DisplayPortraitManager portraitManager;
	
	@Before
	public void setUp() throws Exception {
		if(setuped) return;
		
		//create identities
		owner1 = JunitTestHelper.createAndPersistIdentityAsUser("user-rest-zero");
		assertNotNull(owner1);
		id1 = JunitTestHelper.createAndPersistIdentityAsUser("user-rest-one-" + UUID.randomUUID().toString());
		id2 = JunitTestHelper.createAndPersistIdentityAsUser("user-rest-two");
		dbInstance.intermediateCommit();
		id2.getUser().setProperty("telMobile", "39847592");
		id2.getUser().setProperty("gender", "female");
		id2.getUser().setProperty("birthDay", "20091212");
		dbInstance.updateObject(id2.getUser());
		dbInstance.intermediateCommit();
		
		id3 = JunitTestHelper.createAndPersistIdentityAsUser("user-rest-three");
		VFSContainer id3HomeFolder = VFSManager.olatRootContainer(FolderConfig.getUserHome(id3.getName()), null);
		VFSContainer id3PublicFolder = (VFSContainer)id3HomeFolder.resolve("public");
		if(id3PublicFolder == null) {
			id3PublicFolder = id3HomeFolder.createChildContainer("public");
		}
		VFSItem portrait = id3PublicFolder.resolve("portrait.jpg");
		if(portrait == null) {
			URL portraitUrl = CoursesElementsTest.class.getResource("portrait.jpg");
			File ioPortrait = new File(portraitUrl.toURI());
			FileUtils.copyFileToDirectory(ioPortrait, ((LocalImpl)id3PublicFolder).getBasefile(), false);
		}

		OLATResourceManager rm = OLATResourceManager.getInstance();
		// create course and persist as OLATResourceImpl
		OLATResourceable resourceable = OresHelper.createOLATResourceableInstance("junitcourse",System.currentTimeMillis());
		OLATResource course =  rm.createOLATResourceInstance(resourceable);
		dbInstance.saveObject(course);
		dbInstance.intermediateCommit();
		
		//create learn group	
		// 1) context one: learning groups
		RepositoryEntry c1 = JunitTestHelper.createAndPersistRepositoryEntry();
		// create groups without waiting list
		g1externalId = UUID.randomUUID().toString();
		g1 = businessGroupService.createBusinessGroup(null, "user-rest-g1", null, g1externalId, "all", 0, 10, false, false, c1);
		g2 = businessGroupService.createBusinessGroup(null, "user-rest-g2", null, 0, 10, false, false, c1);
		// members g1
		businessGroupRelationDao.addRole(id1, g1, GroupRoles.coach.name());
		businessGroupRelationDao.addRole(id2, g1, GroupRoles.participant.name());
		// members g2
		businessGroupRelationDao.addRole(id2, g2, GroupRoles.coach.name());
		businessGroupRelationDao.addRole(id1, g2, GroupRoles.participant.name());

		// 2) context two: right groups
		RepositoryEntry c2 = JunitTestHelper.createAndPersistRepositoryEntry();
		// groups
		g3ExternalId = UUID.randomUUID().toString();
		g3 = businessGroupService.createBusinessGroup(null, "user-rest-g3", null, g3ExternalId, "all", -1, -1, false, false, c2);
		g4 = businessGroupService.createBusinessGroup(null, "user-rest-g4", null, -1, -1, false, false, c2);
		// members
		businessGroupRelationDao.addRole(id1, g3, GroupRoles.participant.name());
		businessGroupRelationDao.addRole(id2, g4, GroupRoles.participant.name());
		dbInstance.closeSession();
		
		//add some collaboration tools
		CollaborationTools g1CTSMngr = CollaborationToolsFactory.getInstance().getOrCreateCollaborationTools(g1);
		g1CTSMngr.setToolEnabled(CollaborationTools.TOOL_FORUM, true);
		Forum g1Forum = g1CTSMngr.getForum();//create the forum
		Message m1 = forumManager.createMessage(g1Forum, id1, false);
		m1.setTitle("Thread-1");
		m1.setBody("Body of Thread-1");
		forumManager.addTopMessage(m1);
		
		dbInstance.commitAndCloseSession();
		
		//add some folder tool
		CollaborationTools g2CTSMngr = CollaborationToolsFactory.getInstance().getOrCreateCollaborationTools(g2);
		g2CTSMngr.setToolEnabled(CollaborationTools.TOOL_FOLDER, true);
		LocalFolderImpl g2Folder = VFSManager.olatRootContainer(g2CTSMngr.getFolderRelPath(), null);
		g2Folder.getBasefile().mkdirs();
		VFSItem groupPortrait = g2Folder.resolve("portrait.jpg");
		if(groupPortrait == null) {
			URL portraitUrl = UserMgmtTest.class.getResource("portrait.jpg");
			File ioPortrait = new File(portraitUrl.toURI());
			FileUtils.copyFileToDirectory(ioPortrait, g2Folder.getBasefile(), false);
		}
		
		dbInstance.commitAndCloseSession();
		
		//prepare some courses
		Identity author = JunitTestHelper.createAndPersistIdentityAsUser("auth-" + UUID.randomUUID().toString());
		RepositoryEntry entry = JunitTestHelper.deployDemoCourse(author);
		if (!repositoryService.hasRole(id1, entry, GroupRoles.participant.name())){
			repositoryService.addRole(id1, entry, GroupRoles.participant.name());
		}
		
		demoCourse = CourseFactory.loadCourse(entry);
		TreeVisitor visitor = new TreeVisitor(new Visitor() {
			@Override
			public void visit(INode node) {
				if(node instanceof FOCourseNode) {
					if(demoForumNode == null) {
						demoForumNode = (FOCourseNode)node;
						Forum courseForum = demoForumNode.loadOrCreateForum(demoCourse.getCourseEnvironment());
						Message message1 = forumManager.createMessage(courseForum, id1, false);
						message1.setTitle("Thread-1");
						message1.setBody("Body of Thread-1");
						forumManager.addTopMessage(message1);
					}	
				} else if (node instanceof BCCourseNode) {
					if(demoBCCourseNode == null) {
						demoBCCourseNode = (BCCourseNode)node;
						VFSContainer container = BCCourseNode.getNodeFolderContainer(demoBCCourseNode, demoCourse.getCourseEnvironment());
						VFSItem example = container.resolve("singlepage.html");
						if(example == null) {
							try(InputStream htmlUrl = UserMgmtTest.class.getResourceAsStream("singlepage.html")) {
								VFSLeaf htmlLeaf = container.createChildLeaf("singlepage.html");
								IOUtils.copy(htmlUrl, htmlLeaf.getOutputStream(false));
							} catch (IOException e) {
								log.error("", e);
							}
						}
					}
				}
			}
		}, demoCourse.getRunStructure().getRootNode(), false);
		visitor.visitAll();

		dbInstance.commitAndCloseSession();
		setuped = true;
	}

	@Test
	public void testGetUsers() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		List<UserVO> vos = parseUserArray(response.getEntity());
		assertNotNull(vos);
		assertFalse(vos.isEmpty());
		int voSize = vos.size();
		vos = null;
		
		List<Identity> identities = securityManager
				.getIdentitiesByPowerSearch(null, null, true, null, null, null, null, null, null, Identity.STATUS_VISIBLE_LIMIT);
		assertEquals(voSize, identities.size());

		conn.shutdown();
	}
	
	@Test
	public void testFindUsersByLogin() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users")
				.queryParam("login","administrator")
				.queryParam("authProvider","OLAT").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		List<UserVO> vos = parseUserArray(response.getEntity());
		
		String[] authProviders = new String[]{"OLAT"};
		List<Identity> identities = securityManager
				.getIdentitiesByPowerSearch("administrator", null, true, null, authProviders, null, null, null, null, Identity.STATUS_VISIBLE_LIMIT);

		assertNotNull(vos);
		assertFalse(vos.isEmpty());
		assertEquals(vos.size(), identities.size());
		boolean onlyLikeAdmin = true;
		for(UserVO vo:vos) {
			if(!vo.getLogin().startsWith("administrator")) {
				onlyLikeAdmin = false;
			}
		}
		assertTrue(onlyLikeAdmin);
		conn.shutdown();
	}
	
	@Test
	public void testFindUsersByLogin_notFuzzy() throws IOException, URISyntaxException {
		//there is user-rest-...
		Identity id = JunitTestHelper.createAndPersistIdentityAsUser("user-rest");
		Assert.assertNotNull(id);

		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users")
				.queryParam("login","\"user-rest\"").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		List<UserVO> vos = parseUserArray(response.getEntity());

		assertNotNull(vos);
		assertEquals(1, vos.size());
		assertEquals("user-rest", vos.get(0).getLogin());
		conn.shutdown();
	}
	
	@Test
	public void testFindUsersByProperty() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users")
				.queryParam("telMobile","39847592")
				.queryParam("gender","Female")
				.queryParam("birthDay", "12/12/2009").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		method.addHeader("Accept-Language", "en");
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		List<UserVO> vos = parseUserArray(response.getEntity());
	
		assertNotNull(vos);
		assertFalse(vos.isEmpty());
		conn.shutdown();
	}
	
	@Test
	public void testFindAdminByAuth() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users")
				.queryParam("authUsername","administrator")
				.queryParam("authProvider","OLAT").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		List<UserVO> vos = parseUserArray(response.getEntity());
	
		assertNotNull(vos);
		assertFalse(vos.isEmpty());
		assertEquals(1, vos.size());
		assertEquals("administrator",vos.get(0).getLogin());
		conn.shutdown();
	}
	
	@Test
	public void testGetUser() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + id1.getKey()).build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		UserVO vo = conn.parse(response, UserVO.class);

		assertNotNull(vo);
		assertEquals(vo.getKey(), id1.getKey());
		assertEquals(vo.getLogin(), id1.getName());
		//are the properties there?
		assertFalse(vo.getProperties().isEmpty());
		conn.shutdown();
	}
	
	@Test
	public void testGetUserNotAdmin() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + id2.getKey()).build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		UserVO vo = conn.parse(response, UserVO.class);

		assertNotNull(vo);
		assertEquals(vo.getKey(), id2.getKey());
		assertEquals(vo.getLogin(), id2.getName());
		//no properties for security reason
		assertTrue(vo.getProperties().isEmpty());
		conn.shutdown();
	}
	
	@Test
	public void testGetManagedUser() throws IOException, URISyntaxException {
		String externalId = UUID.randomUUID().toString();
		Identity managedId = JunitTestHelper.createAndPersistIdentityAsRndUser("managed-1");
		dbInstance.commitAndCloseSession();
		securityManager.setExternalId(managedId, externalId);
		dbInstance.commitAndCloseSession();

		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users").path("managed").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		List<ManagedUserVO> managedUsers = parseManagedUserArray(response.getEntity());

		boolean found = false;
		for(ManagedUserVO managedUser:managedUsers) {
			if(managedUser.getKey().equals(managedId.getKey())) {
				found = true;
				Assert.assertEquals(externalId, managedUser.getExternalId());
			}
			Assert.assertNotNull(managedUser.getExternalId());
		}
		Assert.assertTrue(found);
		
		conn.shutdown();
	}
	
	@Test
	public void testGetManagedUser_onlyUserManagers() throws IOException, URISyntaxException {
		String externalId = UUID.randomUUID().toString();
		Identity managedId = JunitTestHelper.createAndPersistIdentityAsRndUser("managed-1");
		dbInstance.commitAndCloseSession();
		securityManager.setExternalId(managedId, externalId);
		dbInstance.commitAndCloseSession();

		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users").path("managed").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(401, response.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());
		
		conn.shutdown();
	}
		
	/**
	 * Only print out the raw body of the response
	 * @throws IOException
	 */
	@Test	
	public void testGetRawJsonUser() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + id1.getKey()).build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		String bodyJson = EntityUtils.toString(response.getEntity());
		System.out.println("User");
		System.out.println(bodyJson);
		System.out.println("User");
		conn.shutdown();
	}
		
	/**
	 * Only print out the raw body of the response
	 * @throws IOException
	 */
	@Test	
	public void testGetRawXmlUser() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + id1.getKey()).build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_XML, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		String bodyXml = EntityUtils.toString(response.getEntity());
		System.out.println("User");
		System.out.println(bodyXml);
		System.out.println("User");
		conn.shutdown();
	}
	
	@Test
	public void testCreateUser() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		UserVO vo = new UserVO();
		String username = UUID.randomUUID().toString();
		vo.setLogin(username);
		vo.setFirstName("John");
		vo.setLastName("Smith");
		vo.setEmail(username + "@frentix.com");
		vo.putProperty("telOffice", "39847592");
		vo.putProperty("telPrivate", "39847592");
		vo.putProperty("telMobile", "39847592");
		vo.putProperty("gender", "Female");//male or female
		vo.putProperty("birthDay", "12/12/2009");

		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		method.addHeader("Accept-Language", "en");
		
		HttpResponse response = conn.execute(method);
		assertTrue(response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201);
		UserVO savedVo = conn.parse(response, UserVO.class);
		Identity savedIdent = securityManager.findIdentityByName(username);

		assertNotNull(savedVo);
		assertNotNull(savedIdent);
		assertEquals(savedVo.getKey(), savedIdent.getKey());
		assertEquals(savedVo.getLogin(), savedIdent.getName());
		assertEquals("Female", savedIdent.getUser().getProperty("gender", Locale.ENGLISH));
		assertEquals("39847592", savedIdent.getUser().getProperty("telPrivate", Locale.ENGLISH));
		assertEquals("12/12/2009", savedIdent.getUser().getProperty("birthDay", Locale.ENGLISH));
		conn.shutdown();
	}
	
	@Test
	public void testCreateUser_emptyLogin() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		UserVO vo = new UserVO();
		vo.setLogin("");
		vo.setFirstName("John");
		vo.setLastName("Smith");
		vo.setEmail(UUID.randomUUID() + "@frentix.com");
		vo.putProperty("telOffice", "39847592");
		vo.putProperty("telPrivate", "39847592");
		vo.putProperty("telMobile", "39847592");
		vo.putProperty("gender", "Female");//male or female
		vo.putProperty("birthDay", "12/12/2009");
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		method.addHeader("Accept-Language", "en");
		
		HttpResponse response = conn.execute(method);
		int statusCode = response.getStatusLine().getStatusCode();
		EntityUtils.consume(response.getEntity());
		Assert.assertEquals(406, statusCode);
	}
	
	@Test
	public void testCreateUser_special() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		UserVO vo = new UserVO();
		vo.setLogin("tes @-()_" + CodeHelper.getForeverUniqueID());
		vo.setFirstName("John");
		vo.setLastName("Smith");
		vo.setEmail(UUID.randomUUID() + "@frentix.com");
		vo.putProperty("telOffice", "39847592");
		vo.putProperty("telPrivate", "39847592");
		vo.putProperty("telMobile", "39847592");
		vo.putProperty("gender", "Female");//male or female
		vo.putProperty("birthDay", "12/12/2009");
		
		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		method.addHeader("Accept-Language", "en");
		
		HttpResponse response = conn.execute(method);
		int statusCode = response.getStatusLine().getStatusCode();
		EntityUtils.consume(response.getEntity());
		Assert.assertEquals(200, statusCode);
	}
	
	/**
	 * Test machine format for gender and date
	 */
	@Test
	public void testCreateUser2() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		UserVO vo = new UserVO();
		String username = UUID.randomUUID().toString();
		vo.setLogin(username);
		vo.setFirstName("John");
		vo.setLastName("Smith");
		vo.setEmail(username + "@frentix.com");
		vo.putProperty("telOffice", "39847592");
		vo.putProperty("telPrivate", "39847592");
		vo.putProperty("telMobile", "39847592");
		vo.putProperty("gender", "female");//male or female
		vo.putProperty("birthDay", "20091212");

		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		method.addHeader("Accept-Language", "en");
		
		HttpResponse response = conn.execute(method);
		assertTrue(response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201);
		UserVO savedVo = conn.parse(response, UserVO.class);
		Identity savedIdent = securityManager.findIdentityByName(username);

		assertNotNull(savedVo);
		assertNotNull(savedIdent);
		assertEquals(savedVo.getKey(), savedIdent.getKey());
		assertEquals(savedVo.getLogin(), savedIdent.getName());
		assertEquals("Female", savedIdent.getUser().getProperty("gender", Locale.ENGLISH));
		assertEquals("39847592", savedIdent.getUser().getProperty("telPrivate", Locale.ENGLISH));
		assertEquals("12/12/2009", savedIdent.getUser().getProperty("birthDay", Locale.ENGLISH));
		conn.shutdown();
	}
	
	/**
	 * Test the trim of email
	 */
	@Test
	public void testCreateUser_emailWithTrailingSpace() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		UserVO vo = new UserVO();
		String username = UUID.randomUUID().toString();
		vo.setLogin(username);
		vo.setFirstName("John");
		vo.setLastName("Smith");
		vo.setEmail(username + "@frentix.com ");
		vo.putProperty("gender", "male");//male or female


		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		method.addHeader("Accept-Language", "en");
		
		HttpResponse response = conn.execute(method);
		assertTrue(response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201);
		UserVO savedVo = conn.parse(response, UserVO.class);
		Identity savedIdent = securityManager.findIdentityByName(username);

		assertNotNull(savedVo);
		assertNotNull(savedIdent);
		assertEquals(savedVo.getKey(), savedIdent.getKey());
		assertEquals(savedVo.getLogin(), savedIdent.getName());
		assertEquals(username + "@frentix.com", savedIdent.getUser().getProperty(UserConstants.EMAIL, null));

		conn.shutdown();
	}
	
	/**
	 * Test if we can create two users with the same email addresses.
	 */
	@Test
	public void testCreateUser_same_email() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));

		String email = UUID.randomUUID() + "@frentix.com";
		
		UserVO vo = new UserVO();
		String username = UUID.randomUUID().toString();
		vo.setLogin(username);
		vo.setFirstName("John");
		vo.setLastName("Smith");
		vo.setEmail(email);
		vo.putProperty("gender", "male");//male or female

		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		method.addHeader("Accept-Language", "en");
		
		HttpResponse response = conn.execute(method);
		assertTrue(response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201);
		UserVO savedVo = conn.parse(response, UserVO.class);
		Identity savedIdent = securityManager.findIdentityByName(username);

		assertNotNull(savedVo);
		assertNotNull(savedIdent);
		assertEquals(savedVo.getKey(), savedIdent.getKey());
		
		//second 
		UserVO vo2 = new UserVO();
		vo2.setLogin(UUID.randomUUID().toString());
		vo2.setFirstName("Eva");
		vo2.setLastName("Smith");
		vo2.setEmail(email);
		vo2.putProperty("gender", "female");

		URI request2 = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method2 = conn.createPut(request2, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method2, vo2);
		method2.addHeader("Accept-Language", "en");
		
		HttpResponse response2 = conn.execute(method2);
		int  statusCode2 = response2.getStatusLine().getStatusCode();
		Assert.assertEquals(406, statusCode2);
		String errorMessage = EntityUtils.toString(response2.getEntity());
		Assert.assertNotNull(errorMessage);

		conn.shutdown();
	}
	
	@Test
	public void testCreateUserWithValidationError() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		String login = "rest-809-" + UUID.randomUUID();
		
		UserVO vo = new UserVO();
		vo.setLogin(login);
		vo.setFirstName("John");
		vo.setLastName("Smith");
		vo.setEmail("");
		vo.putProperty("gender", "lu");

		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		
		HttpResponse response = conn.execute(method);
		assertEquals(406, response.getStatusLine().getStatusCode());
		List<ErrorVO> errors = parseErrorArray(response.getEntity());
 		assertNotNull(errors);
		assertFalse(errors.isEmpty());
		assertTrue(errors.size() >= 2);
		assertNotNull(errors.get(0).getCode());
		assertNotNull(errors.get(0).getTranslation());
		assertNotNull(errors.get(1).getCode());
		assertNotNull(errors.get(1).getTranslation());
		
		Identity savedIdent = securityManager.findIdentityByName(login);
		assertNull(savedIdent);
		conn.shutdown();
	}
	
	/**
	 * Test if we can create two users with the same email addresses.
	 */
	@Test
	public void testCreateAndUpdateUser() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));

		String username = UUID.randomUUID().toString();
		String email = UUID.randomUUID() + "@frentix.com";
		
		UserVO vo = new UserVO();
		vo.setLogin(username);
		vo.setFirstName("Terence");
		vo.setLastName("Smith");
		vo.setEmail(email);
		vo.putProperty("gender", "male");//male or female

		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		method.addHeader("Accept-Language", "en");
		
		HttpResponse response = conn.execute(method);
		assertTrue(response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201);
		UserVO savedVo = conn.parse(response, UserVO.class);
		Identity savedIdent = securityManager.findIdentityByName(username);

		Assert.assertNotNull(savedVo);
		Assert.assertNotNull(savedIdent);
		Assert.assertEquals(savedVo.getKey(), savedIdent.getKey());
		Assert.assertEquals(username, savedIdent.getName());
		Assert.assertEquals("Terence", savedIdent.getUser().getFirstName());
		
		//second 
		UserVO updateVo = new UserVO();
		updateVo.setKey(savedVo.getKey());
		updateVo.setLogin(username);
		updateVo.setFirstName("Maximilien");
		updateVo.setLastName("Smith");
		updateVo.setEmail(email);
		updateVo.putProperty("gender", "male");

		URI updateRequest = UriBuilder.fromUri(getContextURI()).path("users").path(savedVo.getKey().toString()).build();
		HttpPost updateMethod = conn.createPost(updateRequest, MediaType.APPLICATION_JSON);
		conn.addJsonEntity(updateMethod, updateVo);
		updateMethod.addHeader("Accept-Language", "en");
		
		HttpResponse updateResponse = conn.execute(updateMethod);
		int  statusCode = updateResponse.getStatusLine().getStatusCode();
		Assert.assertEquals(200, statusCode);
		UserVO updatedVo = conn.parse(updateResponse, UserVO.class);
		dbInstance.commitAndCloseSession();
		Identity updatedIdent = securityManager.loadIdentityByKey(savedVo.getKey());
		
		Assert.assertNotNull(updatedVo);
		Assert.assertNotNull(updatedIdent);
		Assert.assertEquals(updatedVo.getKey(), savedIdent.getKey());
		Assert.assertEquals(updatedVo.getKey(), updatedIdent.getKey());
		Assert.assertEquals(username, updatedIdent.getName());
		Assert.assertEquals("Maximilien", updatedIdent.getUser().getFirstName());

		conn.shutdown();
	}
	
	/**
	 * Test if we can create two users with the same email addresses.
	 */
	@Test
	public void testCreateAndUpdateUser_same_email() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));

		String email = UUID.randomUUID() + "@frentix.com";
		
		UserVO vo = new UserVO();
		String username = UUID.randomUUID().toString();
		vo.setLogin(username);
		vo.setFirstName("John");
		vo.setLastName("Smith");
		vo.setEmail(email);
		vo.putProperty("gender", "male");//male or female

		URI request = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut method = conn.createPut(request, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(method, vo);
		method.addHeader("Accept-Language", "en");
		
		HttpResponse response = conn.execute(method);
		assertTrue(response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201);
		UserVO savedVo = conn.parse(response, UserVO.class);
		Identity savedIdent = securityManager.findIdentityByName(username);

		Assert.assertNotNull(savedVo);
		Assert.assertNotNull(savedIdent);
		Assert.assertEquals(savedVo.getKey(), savedIdent.getKey());
		
		//second user
		String secondEmail = UUID.randomUUID() + "@frentix.com";
		String secondUsername = UUID.randomUUID().toString();
		
		UserVO secondVo = new UserVO();
		secondVo.setLogin(secondUsername);
		secondVo.setFirstName("Eva");
		secondVo.setLastName("Smith");
		secondVo.setEmail(secondEmail);
		secondVo.putProperty("gender", "female");

		URI secondRequest = UriBuilder.fromUri(getContextURI()).path("users").build();
		HttpPut secondMethod = conn.createPut(secondRequest, MediaType.APPLICATION_JSON, true);
		conn.addJsonEntity(secondMethod, secondVo);
		secondMethod.addHeader("Accept-Language", "en");
		
		HttpResponse secondResponse = conn.execute(secondMethod);
		int secondStatusCode = secondResponse.getStatusLine().getStatusCode();
		Assert.assertEquals(200, secondStatusCode);
		UserVO secondSavedVo = conn.parse(secondResponse, UserVO.class);
		Assert.assertNotNull(secondSavedVo);
		
		dbInstance.commitAndCloseSession();
		Identity secondSavedIdent = securityManager.findIdentityByName(secondUsername);
		Assert.assertNotNull(secondSavedIdent);
		Assert.assertEquals(secondSavedVo.getKey(), secondSavedIdent.getKey());
		Assert.assertEquals(secondEmail, secondSavedIdent.getUser().getEmail());

		// update second with new first name and the mail of the first user
		UserVO updateVo = new UserVO();
		updateVo.setKey(secondSavedVo.getKey());
		updateVo.setLogin(secondUsername);
		updateVo.setFirstName("Caprice");
		updateVo.setLastName("Smith");
		updateVo.setEmail(email);
		updateVo.putProperty("gender", "female");

		URI updateRequest = UriBuilder.fromUri(getContextURI()).path("users").path(secondSavedVo.getKey().toString()).build();
		HttpPost updateMethod = conn.createPost(updateRequest, MediaType.APPLICATION_JSON);
		conn.addJsonEntity(updateMethod, updateVo);
		updateMethod.addHeader("Accept-Language", "en");
		
		HttpResponse updateResponse = conn.execute(updateMethod);
		int  statusCode = updateResponse.getStatusLine().getStatusCode();
		Assert.assertEquals(406, statusCode);
		String errorMessage = EntityUtils.toString(updateResponse.getEntity());
		Assert.assertNotNull(errorMessage);
		
		// check that nothing has changed for the second user
		dbInstance.commitAndCloseSession();
		Identity notUpdatedIdent = securityManager.findIdentityByName(secondUsername);
		Assert.assertNotNull(notUpdatedIdent);
		Assert.assertEquals(secondSavedVo.getKey(), notUpdatedIdent.getKey());
		Assert.assertEquals("Eva", notUpdatedIdent.getUser().getFirstName());
		Assert.assertEquals("Smith", notUpdatedIdent.getUser().getLastName());
		Assert.assertEquals(secondEmail, notUpdatedIdent.getUser().getEmail());

		conn.shutdown();
	}
	
	@Test
	public void testUpdateUser_emptyInstitutionalEmail() throws IOException, URISyntaxException {
		String login = "update-" + UUID.randomUUID();
		User user = userManager.createUser(login, login, login + "@openolat.com");
		user.setProperty(UserConstants.INSTITUTIONALEMAIL, "inst" + login + "@openolat.com");
		Identity id = securityManager.createAndPersistIdentityAndUser(login, null, user, "OLAT", login,"secret");
		Organisation organisation = organisationService.getDefaultOrganisation();
		organisationService.addMember(organisation, id, OrganisationRoles.user);
		dbInstance.commitAndCloseSession();
		Assert.assertEquals("inst" + login + "@openolat.com", id.getUser().getInstitutionalEmail());
		
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));

		// set the institutional email empty 
		UserVO updateVo = new UserVO();
		updateVo.setKey(id.getKey());
		updateVo.setLogin(id.getName());
		updateVo.setFirstName(id.getUser().getFirstName());
		updateVo.setLastName(id.getUser().getLastName());
		updateVo.setEmail(id.getUser().getEmail());
		updateVo.putProperty(UserConstants.INSTITUTIONALEMAIL, "");
		
		URI updateRequest = UriBuilder.fromUri(getContextURI()).path("users").path(id.getKey().toString()).build();
		HttpPost updateMethod = conn.createPost(updateRequest, MediaType.APPLICATION_JSON);
		conn.addJsonEntity(updateMethod, updateVo);
		updateMethod.addHeader("Accept-Language", "en");
		
		HttpResponse updateResponse = conn.execute(updateMethod);
		int  statusCode = updateResponse.getStatusLine().getStatusCode();
		Assert.assertEquals(200, statusCode);
		EntityUtils.consume(updateResponse.getEntity());
		
		Identity identity = securityManager.loadIdentityByKey(id.getKey());
		String institutionalEmail = identity.getUser().getInstitutionalEmail();
		Assert.assertTrue(institutionalEmail == null || institutionalEmail.isEmpty());
	}	
	
	@Test
	public void testDeleteUser() throws IOException, URISyntaxException {
		Identity idToDelete = JunitTestHelper.createAndPersistIdentityAsUser("user-to-delete-" + UUID.randomUUID());
		dbInstance.commitAndCloseSession();
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));

		//delete an authentication token
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + idToDelete.getKey()).build();
		HttpDelete method = conn.createDelete(request, MediaType.APPLICATION_XML);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());
		
		Identity deletedIdent = securityManager.loadIdentityByKey(idToDelete.getKey());
		assertNotNull(deletedIdent);//Identity aren't deleted anymore
		assertEquals(Identity.STATUS_DELETED, deletedIdent.getStatus());
		conn.shutdown();
	}
	
	@Test
	public void testGetRoles() throws IOException, URISyntaxException {
		//create an author
		Identity author = JunitTestHelper.createAndPersistIdentityAsRndAuthor("author-");
		dbInstance.commitAndCloseSession();
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//get roles of author
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + author.getKey() + "/roles").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		RolesVO roles = conn.parse(response, RolesVO.class);
		Assert.assertNotNull(roles);
		Assert.assertTrue(roles.isAuthor());
		Assert.assertFalse(roles.isGroupManager());
		Assert.assertFalse(roles.isGuestOnly());
		Assert.assertFalse(roles.isInstitutionalResourceManager());
		Assert.assertFalse(roles.isInvitee());
		Assert.assertFalse(roles.isOlatAdmin());
		Assert.assertFalse(roles.isUserManager());
		conn.shutdown();
	}
	
	@Test
	public void testGetRoles_xml() throws IOException, URISyntaxException {
		//create an author
		Identity author = JunitTestHelper.createAndPersistIdentityAsRndAuthor("author-");
		dbInstance.commitAndCloseSession();
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//get roles of author
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + author.getKey() + "/roles").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_XML, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		String xmlOutput = EntityUtils.toString(response.getEntity());
		Assert.assertTrue(xmlOutput.contains("<rolesVO>"));
		Assert.assertTrue(xmlOutput.contains("<olatAdmin>"));
		conn.shutdown();
	}
	
	@Test
	public void testUpdateRoles() throws IOException, URISyntaxException {
		//create an author
		Identity author = JunitTestHelper.createAndPersistIdentityAsAuthor("author-" + UUID.randomUUID().toString());
		dbInstance.commitAndCloseSession();
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		RolesVO roles = new RolesVO();
		roles.setAuthor(true);
		roles.setUserManager(true);
		
		//get roles of author
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + author.getKey() + "/roles").build();
		HttpPost method = conn.createPost(request, MediaType.APPLICATION_JSON);
		conn.addJsonEntity(method, roles);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		RolesVO modRoles = conn.parse(response, RolesVO.class);
		Assert.assertNotNull(modRoles);
		
		//check the roles
		Roles reloadRoles = securityManager.getRoles(author);
		Assert.assertTrue(reloadRoles.isAuthor());
		Assert.assertFalse(reloadRoles.isGroupManager());
		Assert.assertFalse(reloadRoles.isGuestOnly());
		Assert.assertFalse(reloadRoles.isLearnResourceManager());
		Assert.assertFalse(reloadRoles.isInvitee());
		Assert.assertFalse(reloadRoles.isAdministrator());
		Assert.assertFalse(reloadRoles.isPoolManager());
		Assert.assertTrue(reloadRoles.isUserManager());
		conn.shutdown();
	}
	
	@Test
	public void testGetStatus() throws IOException, URISyntaxException {
		//create an author
		Identity user = JunitTestHelper.createAndPersistIdentityAsUser("status-" + UUID.randomUUID().toString());
		dbInstance.commitAndCloseSession();
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//get roles of author
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + user.getKey() + "/status").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		StatusVO status = conn.parse(response, StatusVO.class);
		Assert.assertNotNull(status);
		Assert.assertNotNull(status.getStatus());
		Assert.assertEquals(2, status.getStatus().intValue());
		conn.shutdown();
	}
	
	@Test
	public void testUpdateStatus() throws IOException, URISyntaxException {
		//create a user
		Identity user = JunitTestHelper.createAndPersistIdentityAsUser("login-denied-1-" + UUID.randomUUID().toString());
		dbInstance.commitAndCloseSession();
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		StatusVO status = new StatusVO();
		status.setStatus(101);
		
		//get roles of author
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + user.getKey() + "/status").build();
		HttpPost method = conn.createPost(request, MediaType.APPLICATION_JSON);
		conn.addJsonEntity(method, status);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		StatusVO modStatus = conn.parse(response, StatusVO.class);
		Assert.assertNotNull(modStatus);
		Assert.assertNotNull(modStatus.getStatus());
		Assert.assertEquals(101, modStatus.getStatus().intValue());
		
		//check the roles
		Identity reloadIdentity = securityManager.loadIdentityByKey(user.getKey());
		Assert.assertNotNull(reloadIdentity);
		Assert.assertNotNull(reloadIdentity.getStatus());
		Assert.assertEquals(101, reloadIdentity.getStatus().intValue());
		conn.shutdown();
	}
	
	/**
	 * Test if a standard user can change the status of someone else
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Test
	public void testUpdateStatus_denied() throws IOException, URISyntaxException {
		//create a user
		Identity user = JunitTestHelper.createAndPersistIdentityAsUser("login-denied-2-" + UUID.randomUUID().toString());
		Identity hacker = JunitTestHelper.createAndPersistIdentityAsUser("login-denied-2-" + UUID.randomUUID().toString());
		dbInstance.commitAndCloseSession();
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(hacker.getName(), JunitTestHelper.PWD));
		
		StatusVO status = new StatusVO();
		status.setStatus(101);
		
		//get roles of author
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + user.getKey() + "/status").build();
		HttpPost method = conn.createPost(request, MediaType.APPLICATION_JSON);
		conn.addJsonEntity(method, status);
		HttpResponse response = conn.execute(method);
		assertEquals(403, response.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());

		conn.shutdown();
	}
	
	@Test
	public void testGetPreferences() throws IOException, URISyntaxException {
		//create an author
		Identity prefsId = JunitTestHelper.createAndPersistIdentityAsAuthor("prefs-1-" + UUID.randomUUID().toString());
		dbInstance.commitAndCloseSession();
		prefsId.getUser().getPreferences().setLanguage("fr");
		prefsId.getUser().getPreferences().setFontsize("11");
		userManager.updateUserFromIdentity(prefsId);
		dbInstance.commitAndCloseSession();
		
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//get preferences of author
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + prefsId.getKey() + "/preferences").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		PreferencesVO prefsVo = conn.parse(response, PreferencesVO.class);
		Assert.assertNotNull(prefsVo);
		Assert.assertEquals("fr", prefsVo.getLanguage());
		conn.shutdown();
	}
	
	@Test
	public void testUpdatePreferences() throws IOException, URISyntaxException {
		//create an author
		Identity prefsId = JunitTestHelper.createAndPersistIdentityAsAuthor("prefs-1-" + UUID.randomUUID().toString());
		dbInstance.commitAndCloseSession();
		prefsId.getUser().getPreferences().setLanguage("de");
		userManager.updateUserFromIdentity(prefsId);
		dbInstance.commitAndCloseSession();
		
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		PreferencesVO prefsVo = new PreferencesVO();
		prefsVo.setLanguage("fr");
		
		//get roles of author
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + prefsId.getKey() + "/preferences").build();
		HttpPost method = conn.createPost(request, MediaType.APPLICATION_JSON);
		conn.addJsonEntity(method, prefsVo);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		PreferencesVO modPrefs = conn.parse(response, PreferencesVO.class);
		Assert.assertNotNull(modPrefs);
		Assert.assertEquals("fr", prefsVo.getLanguage());
		
		//double check
		Identity reloadedPrefsId = securityManager.loadIdentityByKey(prefsId.getKey());
		Assert.assertNotNull(reloadedPrefsId);
		Assert.assertEquals("fr", reloadedPrefsId.getUser().getPreferences().getLanguage());
		
		conn.shutdown();
	}
	
	@Test
	public void testUserForums() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("forums")
				.queryParam("start", 0).queryParam("limit", 20).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		ForumVOes forums = conn.parse(response, ForumVOes.class);
		
		assertNotNull(forums);
		assertNotNull(forums.getForums());
		assertTrue(forums.getForums().length > 0);

		for(ForumVO forum:forums.getForums()) {
			Long groupKey = forum.getGroupKey();
			if(groupKey != null) {
				BusinessGroup bg = businessGroupService.loadBusinessGroup(groupKey);
				assertNotNull(bg);
				CollaborationTools bgCTSMngr = CollaborationToolsFactory.getInstance().getOrCreateCollaborationTools(bg);
				assertTrue(bgCTSMngr.isToolEnabled(CollaborationTools.TOOL_FORUM));
				
				assertNotNull(forum.getForumKey());
				assertEquals(bg.getName(), forum.getName());
				assertEquals(bg.getKey(), forum.getGroupKey());
				assertTrue(businessGroupService.isIdentityInBusinessGroup(id1, bg));
			} else {
				assertNotNull(forum.getCourseKey());
			}
		}
		conn.shutdown();
	}
	
	@Test
	public void testUserGroupForum() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("forums")
				.path("group").path(g1.getKey().toString())
				.path("threads").queryParam("start", "0").queryParam("limit", "25").build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		MessageVOes threads = conn.parse(response, MessageVOes.class);
		
		assertNotNull(threads);
		assertNotNull(threads.getMessages());
		assertTrue(threads.getMessages().length > 0);
		conn.shutdown();
	}
	
	@Test
	public void testUserCourseForum() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("forums")
				.path("course").path(demoCourse.getResourceableId().toString()).path(demoForumNode.getIdent())
				.path("threads").queryParam("start", "0").queryParam("limit", 25).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		MessageVOes threads = conn.parse(response, MessageVOes.class);
		
		assertNotNull(threads);
		assertNotNull(threads.getMessages());
		assertTrue(threads.getMessages().length > 0);
		conn.shutdown();
	}
	
	@Test
	public void testUserFolders() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("folders").build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		FolderVOes folders = conn.parse(response, FolderVOes.class);
		
		assertNotNull(folders);
		assertNotNull(folders.getFolders());
		assertTrue(folders.getFolders().length > 0);

		boolean matchG2 = false;
		
		for(FolderVO folder:folders.getFolders()) {
			Long groupKey = folder.getGroupKey();
			if(groupKey != null) {
				BusinessGroup bg = businessGroupService.loadBusinessGroup(groupKey);
				assertNotNull(bg);
				CollaborationTools bgCTSMngr = CollaborationToolsFactory.getInstance().getOrCreateCollaborationTools(bg);
				assertTrue(bgCTSMngr.isToolEnabled(CollaborationTools.TOOL_FOLDER));
				
				assertEquals(bg.getName(), folder.getName());
				assertEquals(bg.getKey(), folder.getGroupKey());
				assertTrue(businessGroupService.isIdentityInBusinessGroup(id1, bg));
				if(g2.getKey().equals(groupKey)) {
					matchG2 = true;
				}
			} else {
				assertNotNull(folder.getCourseKey());
			}
		}
		
		//id1 is participant of g2. Make sure it found the folder
		assertTrue(matchG2);
		conn.shutdown();
	}
	
	@Test
	public void testUserGroupFolder() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("folders")
				.path("group").path(g2.getKey().toString()).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		List<FileVO> folders = parseFileArray(response.getEntity());

		assertNotNull(folders);
		assertFalse(folders.isEmpty());
		assertEquals(1, folders.size()); //private and public
		
		FileVO portrait = folders.get(0);
		assertEquals("portrait.jpg", portrait.getTitle());
		conn.shutdown();
	}
	
	@Test
	public void testUserBCCourseNodeFolder() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("folders")
				.path("course").path(demoCourse.getResourceableId().toString()).path(demoBCCourseNode.getIdent()).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());

		List<FileVO> folders = parseFileArray(response.getEntity());

		assertNotNull(folders);
		assertFalse(folders.isEmpty());
		assertEquals(1, folders.size()); //private and public
		
		FileVO singlePage = folders.get(0);
		assertEquals("singlepage.html", singlePage.getTitle());
		conn.shutdown();
	}
	
	@Test
	public void testUserPersonalFolder() throws Exception {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("folders").path("personal").build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		
		List<FileVO> files = parseFileArray(response.getEntity());
		
		assertNotNull(files);
		assertFalse(files.isEmpty());
		assertEquals(2, files.size()); //private and public
		conn.shutdown();
	}
	
	@Test
	public void testOtherUserPersonalFolder() throws Exception {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id2.getKey().toString()).path("folders").path("personal").build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		
		List<FileVO> files = parseFileArray(response.getEntity());
		
		assertNotNull(files);
		assertTrue(files.isEmpty());
		assertEquals(0, files.size()); //private and public
		conn.shutdown();
	}
	
	@Test
	public void testOtherUserPersonalFolderOfId3() throws Exception {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		URI uri = UriBuilder.fromUri(getContextURI()).path("users").path(id3.getKey().toString()).path("folders").path("personal").build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		
		List<FileVO> files = parseFileArray(response.getEntity());
		
		assertNotNull(files);
		assertFalse(files.isEmpty());
		assertEquals(1, files.size()); //private and public
		
		FileVO portrait = files.get(0);
		assertEquals("portrait.jpg", portrait.getTitle());
		conn.shutdown();
	}
	
	@Test
	public void testUserGroup() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//retrieve all groups
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + id1.getKey() + "/groups").build();
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());

		List<GroupVO> groups = parseGroupArray(response.getEntity());
		assertNotNull(groups);
		assertEquals(3, groups.size());//g1, g2 and g3
		conn.shutdown();
	}
	
	@Test
	public void testUserGroup_managed() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//retrieve managed groups
		URI request = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("groups")
				.queryParam("managed", "true").build();
		
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());

		List<GroupVO> groups = parseGroupArray(response.getEntity());
		assertNotNull(groups);
		assertEquals(2, groups.size());//g1 and g3
		conn.shutdown();
	}
	
	@Test
	public void testUserGroup_notManaged() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//retrieve free groups
		URI request = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("groups")
				.queryParam("managed", "false").build();
		
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());

		List<GroupVO> groups = parseGroupArray(response.getEntity());
		assertNotNull(groups);
		assertEquals(1, groups.size());//g2
		conn.shutdown();
	}
	
	@Test
	public void testUserGroup_externalId() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//retrieve g1
		URI request = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("groups")
				.queryParam("externalId", g1externalId).build();
		
		HttpGet method = conn.createGet(request, MediaType.APPLICATION_JSON, true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());

		List<GroupVO> groups = parseGroupArray(response.getEntity());
		assertNotNull(groups);
		assertEquals(1, groups.size());
		assertEquals(g1.getKey(), groups.get(0).getKey());
		assertEquals(g1externalId, groups.get(0).getExternalId());

		conn.shutdown();
	}
	
	@Test
	public void testUserGroupWithPaging() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//retrieve all groups
		URI uri =UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("groups")
			.queryParam("start", 0).queryParam("limit", 1).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		GroupVOes groups = conn.parse(response, GroupVOes.class);
		
		assertNotNull(groups);
		assertNotNull(groups.getGroups());
		assertEquals(1, groups.getGroups().length);
		assertEquals(3, groups.getTotalCount());//g1, g2 and g3
		conn.shutdown();
	}
	
	@Test
	public void testUserGroup_checkRefusedAccess() throws IOException, URISyntaxException {
		Identity alien = JunitTestHelper.createAndPersistIdentityAsRndUser("user-group-alien-");
		dbInstance.commitAndCloseSession();
		
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(alien.getName(), JunitTestHelper.PWD));
		
		//retrieve all groups
		URI uri =UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("groups")
			.queryParam("start", 0).queryParam("limit", 1).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(401, response.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());

		conn.shutdown();
	}
	
	@Test
	public void testUserGroup_checkAllowedAccess() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), JunitTestHelper.PWD));
		
		//retrieve all groups
		URI uri =UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("groups")
			.queryParam("start", 0).queryParam("limit", 1).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		GroupVOes groups = conn.parse(response, GroupVOes.class);
		
		assertNotNull(groups);
		assertNotNull(groups.getGroups());

		conn.shutdown();
	}
	
	@Test
	public void testUserGroup_owner() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//retrieve all groups
		URI uri =UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString())
			.path("groups").path("owner").queryParam("start", 0).queryParam("limit", 1).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		GroupVOes groups = conn.parse(response, GroupVOes.class);
		
		assertNotNull(groups);
		assertNotNull(groups.getGroups());
		assertEquals(1, groups.getGroups().length);
		assertEquals(1, groups.getTotalCount());//g1
		conn.shutdown();
	}
	
	@Test
	public void testUserGroup_participant() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//retrieve all groups
		URI uri =UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString())
			.path("groups").path("participant").queryParam("start", 0).queryParam("limit", 1).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		GroupVOes groups = conn.parse(response, GroupVOes.class);
		
		assertNotNull(groups);
		assertNotNull(groups.getGroups());
		assertEquals(1, groups.getGroups().length);
		assertEquals(2, groups.getTotalCount());//g2 and g3
		conn.shutdown();
	}
	
	@Test
	public void testUserGroupInfosWithPaging() throws IOException, URISyntaxException {
		RestConnection conn = new RestConnection();
		assertTrue(conn.login("administrator", "openolat"));
		
		//retrieve all groups
		URI uri =UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).path("groups").path("infos")
			.queryParam("start", 0).queryParam("limit", 1).build();

		HttpGet method = conn.createGet(uri, MediaType.APPLICATION_JSON + ";pagingspec=1.0", true);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		GroupInfoVOes groups = conn.parse(response, GroupInfoVOes.class);
		
		assertNotNull(groups);
		assertNotNull(groups.getGroups());
		assertEquals(1, groups.getGroups().length);
		assertEquals(3, groups.getTotalCount());//g1, g2 and g3
		conn.shutdown();
	}
	
	@Test
	public void testPortrait() throws IOException, URISyntaxException {
		URL portraitUrl = UserMgmtTest.class.getResource("portrait.jpg");
		assertNotNull(portraitUrl);
		File portrait = new File(portraitUrl.toURI());
		RestConnection conn = new RestConnection();
		assertTrue(conn.login(id1.getName(), "A6B7C8"));
		
		//upload portrait
		URI request = UriBuilder.fromUri(getContextURI()).path("/users/" + id1.getKey() + "/portrait").build();
		HttpPost method = conn.createPost(request, MediaType.APPLICATION_JSON);
		conn.addMultipart(method, "portrait.jpg", portrait);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());

		
		//check if big and small portraits exist
		File bigPortrait = portraitManager.getBigPortrait(id1.getName());
		assertNotNull(bigPortrait);
		assertTrue(bigPortrait.exists());
		assertTrue(bigPortrait.exists());
		
		//check get portrait
		URI getRequest = UriBuilder.fromUri(getContextURI()).path("/users/" + id1.getKey() + "/portrait").build();
		HttpGet getMethod = conn.createGet(getRequest, MediaType.APPLICATION_OCTET_STREAM, true);
		HttpResponse getResponse = conn.execute(getMethod);
		assertEquals(200, getResponse.getStatusLine().getStatusCode());
		InputStream in = getResponse.getEntity().getContent();
		int b = 0;
		int count = 0;
		while((b = in.read()) > -1) {
			count++;
		}
		
		assertEquals(-1, b);//up to end of file
		assertTrue(count > 1000);//enough bytes
		bigPortrait = portraitManager.getBigPortrait(id1.getName());
		assertNotNull(bigPortrait);
		assertEquals(count, bigPortrait.length());

		//check get portrait as Base64
		UriBuilder getRequest2 = UriBuilder.fromUri(getContextURI()).path("users").path(id1.getKey().toString()).queryParam("withPortrait", "true");
		HttpGet getMethod2 = conn.createGet(getRequest2.build(), MediaType.APPLICATION_JSON, true);
		HttpResponse getCode2 = conn.execute(getMethod2);
		assertEquals(200, getCode2.getStatusLine().getStatusCode());
		UserVO userVo = conn.parse(getCode2, UserVO.class);
		assertNotNull(userVo);
		assertNotNull(userVo.getPortrait());
		byte[] datas = Base64.decodeBase64(userVo.getPortrait().getBytes());
		assertNotNull(datas);
		assertTrue(datas.length > 0);
		
		File smallPortrait = portraitManager.getSmallPortrait(id1.getName());
		assertNotNull(smallPortrait);
		assertEquals(datas.length, smallPortrait.length());
		
		try {
			ImageIO.read(new ByteArrayInputStream(datas));
		} catch (Exception e) {
			assertFalse("Cannot read the portrait after Base64 encoding/decoding", false);
		}
		
		conn.shutdown();
	}
	
	@Test
	public void testPortrait_HEAD() throws IOException, URISyntaxException {
		Identity id = JunitTestHelper.createAndPersistIdentityAsRndUser("portrait-1");
		Identity idWithoutPortrait = JunitTestHelper.createAndPersistIdentityAsRndUser("portrait-2");
		
		URL portraitUrl = UserMgmtTest.class.getResource("portrait.jpg");
		Assert.assertNotNull(portraitUrl);
		File portrait = new File(portraitUrl.toURI());
		RestConnection conn = new RestConnection();
		Assert.assertTrue(conn.login(id.getName(), "A6B7C8"));
		
		//upload portrait
		URI request = UriBuilder.fromUri(getContextURI())
				.path("users").path(id.getKey().toString()).path("portrait").build();
		HttpPost method = conn.createPost(request, MediaType.APPLICATION_JSON);
		conn.addMultipart(method, "portrait.jpg", portrait);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());
		
		//check 200
		URI headRequest = UriBuilder.fromUri(getContextURI())
				.path("users").path(id.getKey().toString()).path("portrait").build();
		HttpHead headMethod = conn.createHead(headRequest, MediaType.APPLICATION_OCTET_STREAM, true);
		HttpResponse headResponse = conn.execute(headMethod);
		assertEquals(200, headResponse.getStatusLine().getStatusCode());
		EntityUtils.consume(headResponse.getEntity());
		
		//check 404
		URI headNoRequest = UriBuilder.fromUri(getContextURI())
				.path("users").path(idWithoutPortrait.getKey().toString()).path("portrait").build();
		HttpHead headNoMethod = conn.createHead(headNoRequest, MediaType.APPLICATION_OCTET_STREAM, true);
		HttpResponse headNoResponse = conn.execute(headNoMethod);
		assertEquals(404, headNoResponse.getStatusLine().getStatusCode());
		EntityUtils.consume(headNoResponse.getEntity());
	}
	
	/**
	 * Check the 3 sizes
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Test
	public void testPortrait_HEAD_sizes() throws IOException, URISyntaxException {
		Identity id = JunitTestHelper.createAndPersistIdentityAsRndUser("portrait-3");
		
		URL portraitUrl = UserMgmtTest.class.getResource("portrait.jpg");
		Assert.assertNotNull(portraitUrl);
		File portrait = new File(portraitUrl.toURI());
		RestConnection conn = new RestConnection();
		Assert.assertTrue(conn.login(id.getName(), "A6B7C8"));
		
		//upload portrait
		URI request = UriBuilder.fromUri(getContextURI())
				.path("users").path(id.getKey().toString()).path("portrait").build();
		HttpPost method = conn.createPost(request, MediaType.APPLICATION_JSON);
		conn.addMultipart(method, "portrait.jpg", portrait);
		HttpResponse response = conn.execute(method);
		assertEquals(200, response.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());
		
		//check 200
		URI headMasterRequest = UriBuilder.fromUri(getContextURI())
				.path("users").path(id.getKey().toString()).path("portrait").path("master").build();
		HttpHead headMasterMethod = conn.createHead(headMasterRequest, MediaType.APPLICATION_OCTET_STREAM, true);
		HttpResponse headMasterResponse = conn.execute(headMasterMethod);
		assertEquals(200, headMasterResponse.getStatusLine().getStatusCode());
		EntityUtils.consume(headMasterResponse.getEntity());
		
		//check 200
		URI headBigRequest = UriBuilder.fromUri(getContextURI())
				.path("users").path(id.getKey().toString()).path("portrait").path("big").build();
		HttpHead headBigMethod = conn.createHead(headBigRequest, MediaType.APPLICATION_OCTET_STREAM, true);
		HttpResponse headBigResponse = conn.execute(headBigMethod);
		assertEquals(200, headBigResponse.getStatusLine().getStatusCode());
		EntityUtils.consume(headBigResponse.getEntity());
		
		//check 200
		URI headSmallRequest = UriBuilder.fromUri(getContextURI())
				.path("users").path(id.getKey().toString()).path("portrait").path("small").build();
		HttpHead headSmallMethod = conn.createHead(headSmallRequest, MediaType.APPLICATION_OCTET_STREAM, true);
		HttpResponse headSmallResponse = conn.execute(headSmallMethod);
		assertEquals(200, headSmallResponse.getStatusLine().getStatusCode());
		EntityUtils.consume(headSmallResponse.getEntity());
	}
	
	protected List<UserVO> parseUserArray(HttpEntity entity) {
		try(InputStream in=entity.getContent()) {
			ObjectMapper mapper = new ObjectMapper(jsonFactory); 
			return mapper.readValue(in, new TypeReference<List<UserVO>>(){/* */});
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
	}
	
	protected List<ManagedUserVO> parseManagedUserArray(HttpEntity entity) {
		try(InputStream in=entity.getContent()) {
			ObjectMapper mapper = new ObjectMapper(jsonFactory); 
			return mapper.readValue(in, new TypeReference<List<ManagedUserVO>>(){/* */});
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
	}
	
	protected List<GroupVO> parseGroupArray(HttpEntity entity) {
		try(InputStream in=entity.getContent()) {
			ObjectMapper mapper = new ObjectMapper(jsonFactory); 
			return mapper.readValue(in, new TypeReference<List<GroupVO>>(){/* */});
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
	}
}