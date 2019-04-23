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

import org.olat.modules.curriculum.CurriculumElementTypeRef;

/**
 * 
 * Initial date: 11 mai 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class CurriculumElementTypeRefImpl implements CurriculumElementTypeRef {
	
	private final Long key;
	
	public CurriculumElementTypeRefImpl(Long key) {
		this.key = key;
	}
	
	@Override
	public Long getKey() {
		return key;
	}

	@Override
	public int hashCode() {
		return getKey() == null ? 78254 : getKey().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof CurriculumElementTypeRefImpl) {
			CurriculumElementTypeRefImpl ref = (CurriculumElementTypeRefImpl)obj;
			return getKey() != null && getKey().equals(ref.getKey());
		}
		return super.equals(obj);
	}
}
