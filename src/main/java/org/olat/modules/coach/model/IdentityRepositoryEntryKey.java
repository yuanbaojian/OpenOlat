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
package org.olat.modules.coach.model;

/**
 * 
 * Initial date: 19 juin 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class IdentityRepositoryEntryKey {
	
	private final Long identityKey;
	private final Long repositoryEntryKey;
	
	public IdentityRepositoryEntryKey(Long identityKey, Long repositoryEntryKey) {
		this.identityKey = identityKey;
		this.repositoryEntryKey = repositoryEntryKey;
	}
	
	public IdentityRepositoryEntryKey(EfficiencyStatementEntry entry) {
		identityKey = entry.getIdentityKey();
		repositoryEntryKey = entry.getCourse().getKey();
	}
	
	public Long getIdentityKey() {
		return identityKey;
	}
	
	public Long getRepositoryEntryKey() {
		return repositoryEntryKey;
	}
	
	@Override
	public int hashCode() {
		return (identityKey == null ? 98268 : identityKey.hashCode())
				+ (repositoryEntryKey == null ? -2634785 : repositoryEntryKey.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof IdentityRepositoryEntryKey) {
			IdentityRepositoryEntryKey key = (IdentityRepositoryEntryKey)obj;
			return identityKey != null && identityKey.equals(key.getIdentityKey())
					&& repositoryEntryKey != null && repositoryEntryKey.equals(key.getRepositoryEntryKey());
		}
		return false;
	}
}