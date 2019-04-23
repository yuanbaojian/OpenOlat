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
* <p>
*/  

package org.olat.course.statistic.weekly;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.olat.core.id.Persistable;

/**
 * Hibernate object representing an entry in the o_stat_weekly table.
 * <P>
 * Initial Date:  12.02.2010 <br>
 * @author Stefan
 */
@Entity(name="weeklystat")
@Table(name="o_stat_weekly")
public class WeeklyStat implements Persistable {

	private static final long serialVersionUID = 8424069134569751417L;
	
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
	@Column(name="businesspath", nullable=false, unique=false, insertable=true, updatable=true)
	private String businessPath;
	@Column(name="week", nullable=false, unique=false, insertable=true, updatable=true)
	private String week;
	@Column(name="value", nullable=false, unique=false, insertable=true, updatable=true)
	private int value;
	@Column(name="resid", nullable=false, unique=false, insertable=true, updatable=true)
	private long resId;
	
	public WeeklyStat(){
	// for hibernate	
	}
	
	@Override
	public Long getKey() {
		return key;
	}
	
	public long getResId() {
		return resId;
	}
	
	public void setResId(long resId) {
		this.resId = resId;
	}
	
	public String getBusinessPath() {
		return businessPath;
	}

	public void setBusinessPath(String businessPath) {
		this.businessPath = businessPath;
	}

	public String getWeek() {
		return week;
	}
	
	public void setWeek(String week) {
		this.week = week;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return getKey() == null ? 39563 : getKey().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj instanceof WeeklyStat) {
			WeeklyStat stat = (WeeklyStat)obj;
			return getKey() != null && getKey().equals(stat.getKey());
		}
		return super.equals(obj);
	}

	@Override
	public boolean equalsByPersistableKey(Persistable persistable) {
		return equals(persistable);
	}
	
}
