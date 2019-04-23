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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.olat.basesecurity.RelationRight;
import org.olat.core.id.Persistable;

/**
 * 
 * Initial date: 28 janv. 2019<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Entity(name="relationright")
@Table(name="o_bs_relation_right")
@NamedQuery(name="loadRelationRightByKey", query="select relRight from relationright relRight where relRight.key=:rightKey")
@NamedQuery(name="loadRelationRightByRight", query="select relRight from relationright relRight where relRight.right=:right")
public class RelationRightImpl implements RelationRight, Persistable {

	private static final long serialVersionUID = 2403839554972052354L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true, insertable=true, updatable=false)
	private Long key;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="creationdate", nullable=false, insertable=true, updatable=false)
	private Date creationDate;

	@Column(name="g_right", nullable=false, insertable=true, updatable=false)
	private String right;

	@Override
	public Long getKey() {
		return key;
	}
	
	public void setKey(Long key) {
		this.key = key;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String getRight() {
		return right;
	}
	
	public void setRight(String right) {
		this.right = right;
	}

	@Override
	public int hashCode() {
		return getKey() == null ? -74283 : getKey().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof RelationRightImpl) {
			RelationRightImpl relation = (RelationRightImpl)obj;
			return getKey() != null && getKey().equals(relation.getKey());
		}
		return false;
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		return equals(persistable);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256);
		sb.append("relationRight[id=").append(key == null ? "null" : key.toString()).append(";")
		  .append("right=").append(right == null ? "null" : right).append("]")
		  .append(super.toString());
		return sb.toString();
	}
}
