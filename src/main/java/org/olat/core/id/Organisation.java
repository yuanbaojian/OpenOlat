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
package org.olat.core.id;

import java.util.List;

import org.olat.basesecurity.Group;
import org.olat.basesecurity.OrganisationManagedFlag;
import org.olat.basesecurity.OrganisationStatus;
import org.olat.basesecurity.OrganisationType;

/**
 * 
 * Initial date: 9 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public interface Organisation extends CreateInfo, ModifiedInfo, OrganisationRef {
	
	public boolean isDefault();
	
	public String getMaterializedPathKeys();
	
	public String getIdentifier();
	
	public void setIdentifier(String identifier);
	
	public String getDisplayName();
	
	public void setDisplayName(String displayName);
	
	public String getCssClass();
	
	public void setCssClass(String css);
	
	public String getDescription();
	
	public void setDescription(String description);
	
	public String getExternalId();
	
	public void setExternalId(String externalId);
	
	public OrganisationStatus getOrganisationStatus();
	
	public void setOrganisationStatus(OrganisationStatus status);
	
	public OrganisationManagedFlag[] getManagedFlags();
	
	public void setManagedFlags(OrganisationManagedFlag[] flags);
	
	public Organisation getParent();
	
	public List<OrganisationRef> getParentLine();
	
	public Organisation getRoot();
	
	public OrganisationType getType();
	
	public void setType(OrganisationType type);
	
	public Group getGroup();

}
