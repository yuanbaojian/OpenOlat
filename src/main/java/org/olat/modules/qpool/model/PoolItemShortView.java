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
package org.olat.modules.qpool.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.olat.core.id.Persistable;
import org.olat.modules.qpool.QuestionItem2Pool;

/**
 * 
 * Initial date: 16.04.2013<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Entity(name="qpool2itemshort")
@Table(name="o_qp_pool_2_item_short_v")
public class PoolItemShortView implements QuestionItem2Pool {

	private static final long serialVersionUID = 2434091642402984803L;
	
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
	@Column(name="item_to_pool_id", nullable=false, unique=true, insertable=true, updatable=false)
	private Long key;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="item_to_pool_creationdate", nullable=false, insertable=false, updatable=false)
	private Date creationDate;
	@Column(name="item_id", nullable=false, insertable=false, updatable=false)
	private Long itemKey;
	@Column(name="item_editable", nullable=false, insertable=false, updatable=false)
	private boolean editable;
	@Column(name="item_pool", nullable=false, insertable=false, updatable=false)
	private Long poolKey;
	@Column(name="item_pool_name", nullable=false, insertable=false, updatable=false)
	private String poolName;

	@Override
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public Long getItemKey() {
		return itemKey;
	}

	public void setItemKey(Long itemKey) {
		this.itemKey = itemKey;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public Long getPoolKey() {
		return poolKey;
	}

	public void setPoolKey(Long poolKey) {
		this.poolKey = poolKey;
	}

	@Override
	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	
	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		return equals(persistable);
	}
	
	@Override
	public int hashCode() {
		return getKey() == null ? 56477 : getKey().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof PoolItemShortView) {
			PoolItemShortView q = (PoolItemShortView)obj;
			return getKey() != null && getKey().equals(q.getKey());
		}
		return false;
	}
}
