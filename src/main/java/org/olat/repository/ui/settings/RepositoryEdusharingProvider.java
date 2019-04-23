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
package org.olat.repository.ui.settings;

import org.olat.core.id.OLATResourceable;
import org.olat.modules.edusharing.EdusharingProvider;
import org.olat.modules.edusharing.UsageMetadata;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 11 Dec 2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class RepositoryEdusharingProvider implements EdusharingProvider {

	private final RepositoryEntry repositoryEntry;
	private final String subPath;

	public RepositoryEdusharingProvider(RepositoryEntry repositoryEntry, String subPath) {
		this.repositoryEntry = repositoryEntry;
		this.subPath = subPath;
	}

	@Override
	public OLATResourceable getOlatResourceable() {
		return repositoryEntry;
	}

	@Override
	public String getSubPath() {
		return subPath;
	}

	@Override
	public UsageMetadata getUsageMetadata() {
		UsageMetadata metadata = new UsageMetadata();
		metadata.setCourseId(repositoryEntry.getKey().toString());
		metadata.setFullname(repositoryEntry.getDisplayname());
		metadata.setShortname(repositoryEntry.getExternalRef());
		return metadata;
	}

}
