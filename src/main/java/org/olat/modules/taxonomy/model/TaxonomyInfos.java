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
package org.olat.modules.taxonomy.model;

import java.util.Date;

import org.olat.core.id.CreateInfo;
import org.olat.modules.taxonomy.Taxonomy;
import org.olat.modules.taxonomy.TaxonomyRef;

/**
 * 
 * Initial date: 13 nov. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class TaxonomyInfos implements TaxonomyRef, CreateInfo {
	
	private Long key;
	private Date creationDate;
	
	private String identifier;
	private String displayName;
	private String description;
	
	private int numOfLevels;
	
	public TaxonomyInfos(Taxonomy taxonomy, int numOfLevels) {
		key = taxonomy.getKey();
		creationDate = taxonomy.getCreationDate();
		identifier = taxonomy.getIdentifier();
		displayName = taxonomy.getDisplayName();
		description = taxonomy.getDescription();
		this.numOfLevels = numOfLevels;
	}

	@Override
	public Long getKey() {
		return key;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public int getNumOfLevels() {
		return numOfLevels;
	}
	
	

}
