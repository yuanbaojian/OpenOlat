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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.core.util.StringHelper;
import org.olat.resource.accesscontrol.model.AccessMethod;
import org.olat.resource.accesscontrol.provider.auto.AdvanceOrder;
import org.olat.resource.accesscontrol.provider.auto.AdvanceOrder.Status;
import org.olat.resource.accesscontrol.provider.auto.IdentifierKey;
import org.olat.resource.accesscontrol.provider.auto.model.AdvanceOrderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * Initial date: 14.08.2017<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
@Service
class AdvanceOrderDAO {

	@Autowired
	private DB dbInstance;

	AdvanceOrder create(Identity identity, IdentifierKey key, String identifierValue, AccessMethod method) {
		AdvanceOrderImpl advanceOrder = new AdvanceOrderImpl();
		Date creationDate = new Date();
		advanceOrder.setCreationDate(creationDate);
		advanceOrder.setLastModified(creationDate);
		advanceOrder.setIdentity(identity);
		advanceOrder.setIdentifierKey(key);
		advanceOrder.setIdentifierValue(identifierValue);
		advanceOrder.setStatus(Status.PENDING);
		advanceOrder.setStatusModified(creationDate);
		advanceOrder.setMethod(method);
		return advanceOrder;
	}

	boolean exists(Identity identity, IdentifierKey identifierKey, String identifierValue, AccessMethod method) {
		Long numberOfAdvanceOrder = dbInstance.getCurrentEntityManager()
				.createNamedQuery("exists", Long.class)
				.setParameter("identityKey", identity.getKey())
				.setParameter("identifierKey", identifierKey)
				.setParameter("identifierValue", identifierValue)
				.setParameter("methodKey", method.getKey())
				.getSingleResult();
		return numberOfAdvanceOrder != null && numberOfAdvanceOrder > 0;
	}

	Collection<AdvanceOrder> loadPendingAdvanceOrders(Identity identity) {
		if (identity == null) return new ArrayList<>(0);

		StringBuilder sb = new StringBuilder();
		sb.append("select advanceOrder from advanceOrder advanceOrder")
		  .append(" where advanceOrder.identity.key=:identityKey")
		  .append("   and advanceOrder.status=:status");

		List<AdvanceOrder> advanceOrder = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AdvanceOrder.class)
				.setParameter("identityKey", identity.getKey())
				.setParameter("status", Status.PENDING)
				.getResultList();

		return advanceOrder;
	}

	Collection<AdvanceOrder> loadPendingAdvanceOrders(Map<IdentifierKey, String> identifiers) {
		if (identifiers == null || identifiers.isEmpty()) return new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		sb.append("select advanceOrder from advanceOrder advanceOrder")
		  .append(" where advanceOrder.status=:status")
		  .append("   and ((1 = 2)");

		for (Map.Entry<IdentifierKey, String> entry : identifiers.entrySet()) {
			if (entry.getKey() != null && StringHelper.containsNonWhitespace(entry.getValue())) {
				sb.append(" or (advanceOrder.identifierKey=:").append(entry.getKey());
				sb.append(" and advanceOrder.identifierValue=:").append(entry.getKey()).append(entry.getKey()).append(")");
			}
		}
		sb.append(")");

		TypedQuery<AdvanceOrder> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), AdvanceOrder.class)
				.setParameter("status", Status.PENDING);

		for (Map.Entry<IdentifierKey, String> entry : identifiers.entrySet()) {
			if (entry.getKey() != null && StringHelper.containsNonWhitespace(entry.getValue())) {
				query.setParameter(entry.getKey().toString(), entry.getKey());
				query.setParameter(entry.getKey().toString().concat(entry.getKey().toString()), entry.getValue());
			}
		}

		return query.getResultList();
	}

	public void deleteAdvanceOrder(AdvanceOrder advanceOrder) {
		dbInstance.getCurrentEntityManager()
		.createNamedQuery("deleteByKey")
		.setParameter("key", advanceOrder.getKey())
		.executeUpdate();
	}

	void deleteAdvanceOrders(Identity identity) {
		dbInstance.getCurrentEntityManager()
				.createNamedQuery("deleteByIdentity")
				.setParameter("identityKey", identity.getKey())
				.executeUpdate();
	}

	AdvanceOrder save(AdvanceOrder advanceOrder) {
		if(advanceOrder.getKey() == null) {
			dbInstance.getCurrentEntityManager().persist(advanceOrder);
		} else {
			advanceOrder.setLastModified(new Date());
			advanceOrder = dbInstance.getCurrentEntityManager().merge(advanceOrder);
		}
		return advanceOrder;
	}

	AdvanceOrder accomplishAndSave(AdvanceOrder advanceOrder) {
		if (advanceOrder == null) return advanceOrder;

		advanceOrder.setStatus(Status.DONE);
		advanceOrder.setStatusModified(new Date());
		advanceOrder = save(advanceOrder);

		return advanceOrder;
	}

}
