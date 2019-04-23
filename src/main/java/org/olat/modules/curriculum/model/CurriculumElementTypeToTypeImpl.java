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
package org.olat.modules.curriculum.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.olat.core.id.Persistable;
import org.olat.modules.curriculum.CurriculumElementType;
import org.olat.modules.curriculum.CurriculumElementTypeToType;

/**
 * 
 * Initial date: 9 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Entity(name="curriculumelementtypetotype")
@Table(name="o_cur_element_type_to_type")
public class CurriculumElementTypeToTypeImpl implements Persistable, CurriculumElementTypeToType {

	private static final long serialVersionUID = -7454973947275371839L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true, insertable=true, updatable=false)
	private Long key;
	
	@ManyToOne(targetEntity=CurriculumElementTypeImpl.class,fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="fk_type", nullable=false, insertable=true, updatable=false)
	private CurriculumElementType type;
	@ManyToOne(targetEntity=CurriculumElementTypeImpl.class,fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="fk_allowed_sub_type", nullable=false, insertable=true, updatable=false)
	private CurriculumElementType allowedSubType;

	@Override
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	@Override
	public CurriculumElementType getType() {
		return type;
	}
	
	public void setType(CurriculumElementType type) {
		this.type = type;
	}

	@Override
	public CurriculumElementType getAllowedSubType() {
		return allowedSubType;
	}
	
	public void setAllowedSubType(CurriculumElementType allowedSubType) {
		this.allowedSubType = allowedSubType;
	}

	@Override
	public int hashCode() {
		return getKey() == null ? 541318 : getKey().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof CurriculumElementTypeToTypeImpl) {
			CurriculumElementTypeToTypeImpl type2type = (CurriculumElementTypeToTypeImpl)obj;
			return getKey() != null && getKey().equals(type2type.getKey());
		}
		return false	;
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		return equals(persistable);
	}
}
