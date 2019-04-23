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
package org.olat.core.commons.services.webdav.manager;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.olat.basesecurity.Authentication;
import org.olat.basesecurity.BaseSecurity;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.core.id.UserConstants;
import org.olat.core.util.Encoder;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.olat.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 20 nov. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class WebDAVAuthManagerTest extends OlatTestCase {
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private UserManager userManager;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private WebDAVAuthManager webdavAuthManager;
	
	@Test
	public void updatePassword() {
		// create an identity
		Identity id = JunitTestHelper.createAndPersistIdentityAsRndUser("update-wedbav-1");
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(id);
		id.getUser().setProperty(UserConstants.INSTITUTIONALEMAIL, "inst_" + id.getUser().getEmail());
		userManager.updateUser(id.getUser());
		dbInstance.commitAndCloseSession();
		
		// update its password
		webdavAuthManager.upgradePassword(id, id.getName(), "secret");
		
		// check digest providers
		Authentication ha1Authentication = securityManager.findAuthentication(id, WebDAVAuthManager.PROVIDER_HA1_EMAIL);
		Assert.assertNotNull(ha1Authentication);
		String digestEmailToken = Encoder.md5hash(id.getUser().getEmail() + ":" + WebDAVManagerImpl.BASIC_AUTH_REALM + ":secret");
		Assert.assertEquals(digestEmailToken, ha1Authentication.getCredential());
		
		Authentication ha1InstAuthentication = securityManager.findAuthentication(id, WebDAVAuthManager.PROVIDER_HA1_INSTITUTIONAL_EMAIL);
		Assert.assertNotNull(ha1InstAuthentication);
		String digestInstEmailToken = Encoder.md5hash(id.getUser().getInstitutionalEmail() + ":" + WebDAVManagerImpl.BASIC_AUTH_REALM + ":secret");
		Assert.assertEquals(digestInstEmailToken, ha1InstAuthentication.getCredential());
	}
	
	/**
	 * Check the case of bad data quality and duplicate institutional email
	 * adresss.
	 */
	@Test
	public void updatePassword_duplicate() {
		// create an identity
		Identity id1 = JunitTestHelper.createAndPersistIdentityAsRndUser("update-wedbav-2");
		Identity id2 = JunitTestHelper.createAndPersistIdentityAsRndUser("update-wedbav-3");
		dbInstance.commit();

		String uuid = UUID.randomUUID().toString();
		id1.getUser().setProperty(UserConstants.INSTITUTIONALEMAIL, uuid);
		id2.getUser().setProperty(UserConstants.INSTITUTIONALEMAIL, uuid);
		userManager.updateUser(id1.getUser());
		userManager.updateUser(id2.getUser());
		dbInstance.commitAndCloseSession();
		
		// update  password id 1
		webdavAuthManager.upgradePassword(id1, id1.getName(), "secret");
		dbInstance.commitAndCloseSession();
		
		// update  password id 2
		//this one will have a problem to update the password, but it need to be silent
		webdavAuthManager.upgradePassword(id2, id2.getName(), "secret");
		
		//check the authentication
		//check the connection is useable
		Authentication ha1InstAuthentication1 = securityManager.findAuthentication(id1, WebDAVAuthManager.PROVIDER_HA1_INSTITUTIONAL_EMAIL);
		Assert.assertNotNull(ha1InstAuthentication1);
		Authentication ha1InstAuthentication2 = securityManager.findAuthentication(id2, WebDAVAuthManager.PROVIDER_HA1_INSTITUTIONAL_EMAIL);
		Assert.assertNull(ha1InstAuthentication2);
		
		//check the connection is clean
		dbInstance.commit();
	}

}
