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
package org.olat.modules.taxonomy.ui;

import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTreeTableNode;
import org.olat.modules.taxonomy.TaxonomyLevel;
import org.olat.modules.taxonomy.TaxonomyLevelManagedFlag;
import org.olat.modules.taxonomy.TaxonomyLevelRef;
import org.olat.modules.taxonomy.TaxonomyLevelType;

/**
 * 
 * Initial date: 14 nov. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class TaxonomyLevelRow implements TaxonomyLevelRef, FlexiTreeTableNode {

	private final TaxonomyLevelType type;
	private final TaxonomyLevel taxonomyLevel;
	private final Long parentLevelKey;
	
	private int numOfChildren = 0;
	private FormLink toolsLink;
	private TaxonomyLevelRow parent;
	
	public TaxonomyLevelRow(TaxonomyLevel taxonomyLevel, FormLink toolsLink) {
		type = taxonomyLevel.getType();
		this.taxonomyLevel = taxonomyLevel;
		this.toolsLink = toolsLink;
		parentLevelKey = taxonomyLevel.getParent() == null ? null : taxonomyLevel.getParent().getKey();
	}
	
	@Override
	public Long getKey() {
		return taxonomyLevel.getKey();
	}

	@Override
	public TaxonomyLevelRow getParent() {
		return parent;
	}
	
	@Override
	public String getCrump() {
		return taxonomyLevel.getDisplayName();
	}
	
	public TaxonomyLevelManagedFlag[] getManagedFlags() {
		return taxonomyLevel.getManagedFlags();
	}

	public Long getParentLevelKey() {
		return parentLevelKey;
	}
	
	public void setParent(TaxonomyLevelRow parent) {
		this.parent = parent;
	}
	
	public String getDisplayName() {
		return taxonomyLevel.getDisplayName();
	}
	
	public String getIdentifier() {
		return taxonomyLevel.getIdentifier();
	}
	
	public Long getTypeKey() {
		return type == null ? null : type.getKey();
	}
	
	public String getTypeIdentifier() {
		return type == null ? "" : type.getIdentifier();
	}
	
	public int getNumberOfChildren() {
		return numOfChildren;
	}
	
	public void incrementNumberOfChildren() {
		numOfChildren++;
	}

	public FormLink getToolsLink() {
		return toolsLink;
	}
	
	@Override
	public int hashCode() {
		return taxonomyLevel.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof TaxonomyLevelRow) {
			TaxonomyLevelRow row = (TaxonomyLevelRow)obj;
			return taxonomyLevel != null && taxonomyLevel.equals(row.taxonomyLevel);
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("row[name=").append(taxonomyLevel.getDisplayName() == null ? "" : taxonomyLevel.getDisplayName())
		  .append("]").append(super.toString());
		return sb.toString();
	}
}
