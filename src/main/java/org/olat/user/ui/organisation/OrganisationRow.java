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
package org.olat.user.ui.organisation;

import org.olat.basesecurity.OrganisationType;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTreeTableNode;
import org.olat.core.id.Organisation;
import org.olat.core.id.OrganisationRef;

/**
 * 
 * Initial date: 9 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class OrganisationRow implements OrganisationRef, FlexiTreeTableNode {
	
	private final Organisation organisation;
	private final OrganisationType type;
	private final Long parentOrganisationKey;

	private boolean hasChildren;
	private OrganisationRow parent;
	private final FormLink toolsLink;
	
	public OrganisationRow(Organisation organisation, FormLink toolsLink) {
		this.organisation = organisation;
		type = organisation.getType();
		this.toolsLink = toolsLink;
		parentOrganisationKey = organisation.getParent() == null ? null : organisation.getParent().getKey();
	}
	
	@Override
	public Long getKey() {
		return organisation.getKey();
	}
	
	@Override
	public OrganisationRow getParent() {
		return parent;
	}
	
	public void setParent(OrganisationRow parent) {
		this.parent = parent;
		if(parent != null) {
			parent.hasChildren = true;
		}
	}

	@Override
	public String getCrump() {
		return organisation.getDisplayName();
	}
	
	public Long getParentOrganisationKey() {
		return parentOrganisationKey;
	}

	public String getDisplayName() {
		return organisation.getDisplayName();
	}

	public String getIdentifier() {
		return organisation.getIdentifier();
	}
	
	public String getExternalId() {
		return organisation.getExternalId();
	}

	public Long getTypeKey() {
		return type == null ? null : type.getKey();
	}
	
	public String getTypeIdentifier() {
		return type == null ? null : type.getIdentifier();
	}
	
	public String getTypeDisplayName() {
		return type == null ? null : type.getDisplayName();
	}
	
	public FormLink getTools() {
		return toolsLink;
	}

	public boolean hasChildren() {
		return hasChildren;
	}	
}
