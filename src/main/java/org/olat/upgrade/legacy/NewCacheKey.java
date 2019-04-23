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
package org.olat.upgrade.legacy;

import java.io.Serializable;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class NewCacheKey implements Serializable {

	private static final long serialVersionUID = 4257100091138814545L;
	
	private Long courseId;
	private Long identityKey;
	
	public NewCacheKey() {
		//
	}
	
	public NewCacheKey(Long courseId, Long identityKey) {
		this.courseId = courseId;
		this.identityKey = identityKey;
	}
	
	public Long getCourseId() {
		return courseId;
	}
	
	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}
	
	public Long getIdentityKey() {
		return identityKey;
	}
	
	public void setIdentityKey(Long identityKey) {
		this.identityKey = identityKey;
	}

	@Override
	public int hashCode() {
		return (courseId == null ? 32876 : courseId.hashCode())
				+ (identityKey == null ? 7525 : identityKey.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof NewCacheKey) {
			NewCacheKey key = (NewCacheKey)obj;
			return courseId != null && courseId.equals(key.courseId)
					&& identityKey != null && identityKey.equals(key.identityKey);	
		}	
		return false;
	}
}