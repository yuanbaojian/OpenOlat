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
package org.olat.shibboleth.manager;

import java.util.Set;

import org.olat.core.id.Identity;
import org.olat.resource.accesscontrol.provider.auto.AdvanceOrderInput;
import org.olat.resource.accesscontrol.provider.auto.IdentifierKey;
import org.olat.resource.accesscontrol.provider.auto.model.AutoAccessMethod;
import org.olat.shibboleth.ShibbolethModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * Initial date: 17.08.2017<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
@Component
@Scope("prototype")
public class ShibbolethAdvanceOrderInput implements AdvanceOrderInput {

	@Autowired
	private ShibbolethModule shibbolethModule;

	private Identity identity;
	private String rawValues;

	@Override
	public Class<? extends AutoAccessMethod> getMethodClass() {
		return ShibbolethAutoAccessMethod.class;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	@Override
	public Identity getIdentity() {
		return identity;
	}

	@Override
	public Set<IdentifierKey> getKeys() {
		return shibbolethModule.getAcAutoIdentifiers();
	}

	public void setRawValues(String rawValues) {
		this.rawValues = rawValues;
	}

	@Override
	public String getRawValues() {
		return rawValues;
	}

	@Override
	public String getSplitterType() {
		return shibbolethModule.getAcAutoSplitter();
	}

}
