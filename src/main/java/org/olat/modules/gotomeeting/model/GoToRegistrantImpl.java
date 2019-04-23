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
package org.olat.modules.gotomeeting.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.olat.basesecurity.IdentityImpl;
import org.olat.core.id.Identity;
import org.olat.core.id.ModifiedInfo;
import org.olat.core.id.Persistable;
import org.olat.modules.gotomeeting.GoToMeeting;
import org.olat.modules.gotomeeting.GoToRegistrant;

/**
 * 
 * Save all the data in our database.
 * 
 * Initial date: 22.03.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Entity(name="gotoregistrant")
@Table(name="o_goto_registrant")
public class GoToRegistrantImpl implements GoToRegistrant, Persistable, ModifiedInfo {

	private static final long serialVersionUID = 4285228843020012527L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "enhanced-sequence", parameters={
		@Parameter(name="sequence_name", value="hibernate_unique_key"),
		@Parameter(name="force_table_use", value="true"),
		@Parameter(name="optimizer", value="legacy-hilo"),
		@Parameter(name="value_column", value="next_hi"),
		@Parameter(name="increment_size", value="32767"),
		@Parameter(name="initial_value", value="32767")
	})
	@Column(name="id", nullable=false, unique=true, insertable=true, updatable=false)
	private Long key;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="creationdate", nullable=false, insertable=true, updatable=false)
	private Date creationDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lastmodified", nullable=false, insertable=true, updatable=true)
	private Date lastModified;
	
	@Column(name="g_status", nullable=true, insertable=true, updatable=false)
	private String status;
	@Column(name="g_join_url", nullable=true, insertable=true, updatable=false)
	private String joinUrl;
	@Column(name="g_confirm_url", nullable=true, insertable=true, updatable=false)
	private String confirmUrl;
	@Column(name="g_registrant_key", nullable=true, insertable=true, updatable=false)
	private String registrantKey;

	
	@ManyToOne(targetEntity=GoToMeetingImpl.class, fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="fk_meeting_id", nullable=false, insertable=true, updatable=true)
	private GoToMeeting meeting;
	@ManyToOne(targetEntity=IdentityImpl.class,fetch=FetchType.LAZY,optional=true)
	@JoinColumn(name="fk_identity_id", nullable=true, insertable=true, updatable=false)
	private Identity identity;
	
	@Override
	public Long getKey() {
		return key;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public void setLastModified(Date date) {
		this.lastModified = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getJoinUrl() {
		return joinUrl;
	}

	public void setJoinUrl(String joinUrl) {
		this.joinUrl = joinUrl;
	}

	@Override
	public String getConfirmUrl() {
		return confirmUrl;
	}

	public void setConfirmUrl(String confirmUrl) {
		this.confirmUrl = confirmUrl;
	}

	@Override
	public String getRegistrantKey() {
		return registrantKey;
	}

	public void setRegistrantKey(String registrantKey) {
		this.registrantKey = registrantKey;
	}

	public GoToMeeting getMeeting() {
		return meeting;
	}

	public void setMeeting(GoToMeeting meeting) {
		this.meeting = meeting;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	@Override
	public int hashCode() {
		return key == null ? -794215 : key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof GoToRegistrantImpl) {
			GoToRegistrantImpl registrant = (GoToRegistrantImpl)obj;
			return key != null && key.equals(registrant.getKey());
		}
		return false;
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		return equals(persistable);
	}
}
