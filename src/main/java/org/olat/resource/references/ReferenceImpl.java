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
*/

package org.olat.resource.references;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.olat.core.id.Persistable;
import org.olat.core.logging.AssertException;
import org.olat.resource.OLATResource;
import org.olat.resource.OLATResourceImpl;


/**
 * Initial Date:  May 27, 2004
 *
 * @author Mike Stock
 * 
 * Comment:  
 * 
 */
@Entity(name="references")
@Table(name="o_references")
@NamedQueries({
	@NamedQuery(name="referencesBySourceId", query="select v from references as v where v.source.key=:sourceKey"),
	@NamedQuery(name="referencesByTargetId", query="select v from references as v where v.target.key=:targetKey")
})
public class ReferenceImpl implements Persistable, Reference {

	private static final long serialVersionUID = -6861263748211168112L;

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
	@Column(name="reference_id", nullable=false, unique=true, insertable=true, updatable=false)
	private Long key;

	@Version
	private int version = 0;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="creationdate", nullable=false, insertable=true, updatable=false)
	private Date creationDate;

	@ManyToOne(targetEntity=OLATResourceImpl.class,fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="source_id", nullable=false, insertable=true, updatable=false)
	private OLATResource source;
	@ManyToOne(targetEntity=OLATResourceImpl.class,fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="target_id", nullable=false, insertable=true, updatable=false)
	private OLATResource target;
	@Column(name="userdata", nullable=true, insertable=true, updatable=false)
	private String userdata;
	
	private static final int USERDATA_MAXLENGTH = 64;
	
	@Override
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * @return Returns the source.
	 */
	@Override
	public OLATResource getSource() {
		return source;
	}
	
	/**
	 * @param source The source to set.
	 */
	public void setSource(OLATResource source) {
		this.source = source;
	}
	
	/**
	 * @return Returns the target.
	 */
	@Override
	public OLATResource getTarget() {
		return target;
	}
	
	/**
	 * @param target The target to set.
	 */
	public void setTarget(OLATResource target) {
		this.target = target;
	}

	/**
	 * @return Returns the userdata.
	 */
	@Override
	public String getUserdata() {
		return userdata;
	}
	
	/**
	 * @param userdata The userdata to set.
	 */
	public void setUserdata(String userdata) {
		if (userdata != null && userdata.length() > USERDATA_MAXLENGTH) {
			throw new AssertException("field userdata of table o_reference too long");
		}
		this.userdata = userdata;
	}
	
	@Override
	public int hashCode() {
		return getKey() == null ? -3775423 : getKey().hashCode();
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		return equals(persistable);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof ReferenceImpl) {
			ReferenceImpl mode = (ReferenceImpl)obj;
			return key != null && key.equals(mode.getKey());	
		}
		return false;
	}
}