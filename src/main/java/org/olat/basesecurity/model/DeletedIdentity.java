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
package org.olat.basesecurity.model;

import java.util.Date;

/**
 * 
 * Initial date: 22 mai 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class DeletedIdentity {
	
	private final Long identityKey;
	private final String identityName;
	private final String identityFirstName;
	private final String identityLastName;
	
	private final Date deletedDate;
	private final Date lastLogin;
	private final Date creationDate;
	private final String deletedRoles;
	private final String deletedBy;
	
	public DeletedIdentity(Long identityKey, String identityName, String identityFirstName, String identityLastName,
			Date deletedDate, Date lastLogin, Date creationDate, String deletedRoles, String deletedBy) {
		this.identityKey = identityKey;
		this.identityName = identityName;
		this.identityFirstName = identityFirstName;
		this.identityLastName = identityLastName;
		this.deletedDate = deletedDate;
		this.lastLogin = lastLogin;
		this.creationDate = creationDate;
		this.deletedRoles = deletedRoles;
		this.deletedBy = deletedBy;
	}

	public Long getIdentityKey() {
		return identityKey;
	}

	public String getIdentityName() {
		return identityName;
	}

	public String getIdentityFirstName() {
		return identityFirstName;
	}

	public String getIdentityLastName() {
		return identityLastName;
	}

	public Date getDeletedDate() {
		return deletedDate;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getDeletedRoles() {
		return deletedRoles;
	}

	public String getDeletedBy() {
		return deletedBy;
	}
}
