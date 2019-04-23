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
package org.olat.resource.accesscontrol.provider.auto.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.resource.accesscontrol.manager.ACMethodDAO;
import org.olat.resource.accesscontrol.model.AccessMethod;
import org.olat.resource.accesscontrol.model.FreeAccessMethod;
import org.olat.resource.accesscontrol.model.TokenAccessMethod;
import org.olat.resource.accesscontrol.provider.auto.AdvanceOrder;
import org.olat.resource.accesscontrol.provider.auto.AdvanceOrder.Status;
import org.olat.resource.accesscontrol.provider.auto.IdentifierKey;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * Initial date: 14.08.2017<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class AdvanceOrderDAOTest extends OlatTestCase {

	private static final IdentifierKey IDENTIFIER_KEY = IdentifierKey.externalId;
	private static final String IDENTIFIER_VALUE = "identifierValue";
	private Identity identity;
	private AccessMethod freeMethod;
	private AccessMethod tokenMethod;

	@Autowired
	private DB dbInstance;
	@Autowired
	private ACMethodDAO acMethodDAO;

	@Autowired
	private AdvanceOrderDAO sut;

	@Before
	public void emptyTable() {
		String statement = "delete from advanceOrder";
		dbInstance.getCurrentEntityManager().createQuery(statement).executeUpdate();
	}

	@Before
	public void setUp() {
		acMethodDAO.enableMethod(FreeAccessMethod.class, true);
		List<AccessMethod> freeMethods = acMethodDAO.getAvailableMethodsByType(FreeAccessMethod.class);
		freeMethod = freeMethods.get(0);
		acMethodDAO.enableMethod(TokenAccessMethod.class, true);
		List<AccessMethod> tokenMethods = acMethodDAO.getAvailableMethodsByType(TokenAccessMethod.class);
		tokenMethod = tokenMethods.get(0);

		identity = JunitTestHelper.createAndPersistIdentityAsRndUser("user");
	}

	@Test
	public void shouldCreateAdvanceOrder() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);

		assertThat(advanceOrder.getKey()).isNull();
		assertThat(advanceOrder.getCreationDate()).isNotNull();
		assertThat(advanceOrder.getLastModified()).isNotNull();
		assertThat(advanceOrder.getIdentity()).isEqualTo(identity);
		assertThat(advanceOrder.getIdentifierKey()).isEqualTo(IDENTIFIER_KEY);
		assertThat(advanceOrder.getIdentifierValue()).isEqualTo(IDENTIFIER_VALUE);
		assertThat(advanceOrder.getMethod()).isEqualTo(freeMethod);
		assertThat(advanceOrder.getStatus()).isEqualTo(Status.PENDING);
		assertThat(advanceOrder.getStatusModified()).isNotNull();
	}

	@Test
	public void shouldSaveNewAdvanceOrder() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		dbInstance.commitAndCloseSession();

		AdvanceOrder savedAdvanceOrder = sut.save(advanceOrder);
		dbInstance.commitAndCloseSession();

		assertThat(savedAdvanceOrder.getKey()).isNotNull();
	}

	@Test
	public void shouldSaveUpdatedAdvanceOrder() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		advanceOrder = sut.save(advanceOrder);
		advanceOrder.setStatus(Status.DONE);

		dbInstance.commitAndCloseSession();
		AdvanceOrder savedAdvanceOrder = sut.save(advanceOrder);
		dbInstance.commitAndCloseSession();

		assertThat(savedAdvanceOrder.getStatus()).isEqualTo(Status.DONE);
	}

	@Test
	public void shouldFindPendingAdvanceOrderForIdentity() {
		AdvanceOrder firstPendingAdvanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.save(firstPendingAdvanceOrder);
		AdvanceOrder secondPendingAdvanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.save(secondPendingAdvanceOrder);
		Identity otherIdentity = JunitTestHelper.createAndPersistIdentityAsRndUser("other");
		AdvanceOrder advanceOrderWithOtherIdentity = sut.create(otherIdentity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.save(advanceOrderWithOtherIdentity);
		AdvanceOrder doneAdvanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.accomplishAndSave(doneAdvanceOrder);
		dbInstance.commitAndCloseSession();

		Collection<AdvanceOrder> pendingAdvanceOrders = sut.loadPendingAdvanceOrders(identity);

		assertThat(pendingAdvanceOrders).hasSize(2);
	}

	@Test
	public void shouldFindPendingAdvaceOrderForIdentifiers() {
		AdvanceOrder aoMatchingInternalId = sut.create(identity, IdentifierKey.internalId, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoMatchingInternalId);
		AdvanceOrder aoMatchingExternalId = sut.create(identity, IdentifierKey.externalId, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoMatchingExternalId);
		AdvanceOrder aoNotMatchingRef = sut.create(identity, IdentifierKey.externalRef, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoNotMatchingRef);
		AdvanceOrder aoNotMatchingValue = sut.create(identity, IdentifierKey.internalId, "not matching", freeMethod);
		sut.save(aoNotMatchingValue);
		dbInstance.commitAndCloseSession();

		Map<IdentifierKey, String> identifiers = new HashMap<>();
		identifiers.put(IdentifierKey.internalId, IDENTIFIER_VALUE);
		identifiers.put(IdentifierKey.externalId, IDENTIFIER_VALUE);
		Collection<AdvanceOrder> advanceOrders = sut.loadPendingAdvanceOrders(identifiers);

		assertThat(advanceOrders).hasSize(2).contains(aoMatchingInternalId, aoMatchingExternalId);
	}

	@Test
	public void shouldFindPendingAdvaceOrderForIdentifiersIfNullValues() {
		AdvanceOrder aoMatchingInternalId = sut.create(identity, IdentifierKey.internalId, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoMatchingInternalId);
		AdvanceOrder aoMatchingExternalId = sut.create(identity, IdentifierKey.externalId, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoMatchingExternalId);
		AdvanceOrder aoNotMatchingRef = sut.create(identity, IdentifierKey.externalRef, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoNotMatchingRef);
		AdvanceOrder aoNotMatchingValue = sut.create(identity, IdentifierKey.internalId, "not matching", freeMethod);
		sut.save(aoNotMatchingValue);
		dbInstance.commitAndCloseSession();

		Map<IdentifierKey, String> identifiers = new HashMap<>();
		identifiers.put(IdentifierKey.internalId, IDENTIFIER_VALUE);
		identifiers.put(null, IDENTIFIER_VALUE);
		identifiers.put(IdentifierKey.externalId, "");
		Collection<AdvanceOrder> advanceOrders = sut.loadPendingAdvanceOrders(identifiers);

		assertThat(advanceOrders).hasSize(1).contains(aoMatchingInternalId);
	}

	@Test
	public void shouldDeleteAdvaceOrderByKey() {
		AdvanceOrder aoToKeep1 = sut.create(identity, IdentifierKey.internalId, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoToKeep1);
		AdvanceOrder aoToDelete = sut.create(identity, IdentifierKey.externalId, IDENTIFIER_VALUE, freeMethod);
		aoToDelete = sut.save(aoToDelete);
		AdvanceOrder aoToKeep2 = sut.create(identity, IdentifierKey.externalRef, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoToKeep2);
		AdvanceOrder aoToKeep3 = sut.create(identity, IdentifierKey.internalId, "not matching", freeMethod);
		sut.save(aoToKeep3);
		dbInstance.commitAndCloseSession();

		sut.deleteAdvanceOrder(aoToDelete);

		assertThat(sut.loadPendingAdvanceOrders(identity)).hasSize(3).doesNotContain(aoToDelete);
	}

	@Test
	public void shouldDeleteAdvanceOrdersByIdentity() {
		AdvanceOrder aoPending = sut.create(identity, IdentifierKey.internalId, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoPending);
		AdvanceOrder aoDone = sut.create(identity, IdentifierKey.externalId, IDENTIFIER_VALUE, freeMethod);
		sut.save(aoDone);
		sut.accomplishAndSave(aoDone);
		Identity otherIdentity = JunitTestHelper.createAndPersistIdentityAsRndUser("otheruser");
		AdvanceOrder aoOtherIdentity = sut.create(otherIdentity, IdentifierKey.internalId, "not matching", freeMethod);
		sut.save(aoOtherIdentity);
		dbInstance.commitAndCloseSession();

		sut.deleteAdvanceOrders(identity);

		Collection<AdvanceOrder> aoDeletedUser = loadAllAdvanceOrders(identity);
		assertThat(aoDeletedUser).hasSize(0);
		Collection<AdvanceOrder> aoActiveUser = loadAllAdvanceOrders(otherIdentity);
		assertThat(aoActiveUser).hasSize(1);
	}

	private Collection<AdvanceOrder> loadAllAdvanceOrders(Identity identity) {
		if (identity == null) return new ArrayList<>(0);

		StringBuilder sb = new StringBuilder();
		sb.append("select advanceOrder from advanceOrder advanceOrder")
		  .append(" where advanceOrder.identity.key=:identityKey");

		List<AdvanceOrder> advanceOrder = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AdvanceOrder.class)
				.setParameter("identityKey", identity.getKey())
				.getResultList();

		return advanceOrder;
	}

	@Test
	public void shouldMarkAsDoneWhenAccomplished() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		advanceOrder = sut.save(advanceOrder);

		AdvanceOrder accomplishedAdvanceOrder = sut.accomplishAndSave(advanceOrder);

		assertThat(accomplishedAdvanceOrder.getStatus()).isEqualTo(Status.DONE);
	}

	@Test
	public void shouldNotMarkedAsDoneIfNoOffer() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		advanceOrder = sut.save(advanceOrder);

		AdvanceOrder accomplishedAdvanceOrder = sut.accomplishAndSave(advanceOrder);

		assertThat(accomplishedAdvanceOrder.getStatus()).isEqualTo(advanceOrder.getStatus());
	}

	@Test
	public void shouldExistIfAllValuesTheSame() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.save(advanceOrder);
		dbInstance.commitAndCloseSession();

		boolean exists = sut.exists(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);

		assertThat(exists).isTrue();
	}

	@Test
	public void shouldNotExistIfTheIdentityIsDifferent() {
		Identity otherIdentity = JunitTestHelper.createAndPersistIdentityAsRndUser("other");
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.save(advanceOrder);
		dbInstance.commitAndCloseSession();

		boolean exists = sut.exists(otherIdentity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);

		assertThat(exists).isFalse();
	}


	@Test
	public void shouldNotExistIfTheIdentifierKeyIsDifferent() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.save(advanceOrder);
		dbInstance.commitAndCloseSession();

		boolean exists = sut.exists(identity, IdentifierKey.internalId, IDENTIFIER_VALUE, freeMethod);

		assertThat(exists).isFalse();
	}

	@Test
	public void shouldNotExistIfTheIdentifierValueIsDifferent() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.save(advanceOrder);
		dbInstance.commitAndCloseSession();

		boolean exists = sut.exists(identity, IDENTIFIER_KEY, "otherValue", freeMethod);

		assertThat(exists).isFalse();
	}

	@Test
	public void shouldNotExistIfTheHandlerTypeIsDifferent() {
		AdvanceOrder advanceOrder = sut.create(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, freeMethod);
		sut.save(advanceOrder);
		dbInstance.commitAndCloseSession();

		boolean exists = sut.exists(identity, IDENTIFIER_KEY, IDENTIFIER_VALUE, tokenMethod);

		assertThat(exists).isFalse();
	}
}
