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
package org.olat.repository.ui;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.olat.core.commons.modules.bc.FolderConfig;
import org.olat.core.dispatcher.mapper.Mapper;
import org.olat.core.gui.media.MediaResource;
import org.olat.core.util.vfs.LocalFolderImpl;
import org.olat.core.util.vfs.VFSConstants;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSItem;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.core.util.vfs.VFSMediaResource;
import org.olat.core.util.vfs.meta.MetaInfo;

/**
 * 
 * Initial date: 20.11.2012<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class RepositoryEntryImageMapper implements Mapper {
	
	private VFSContainer rootContainer;

	@Override
	public MediaResource handle(String relPath, HttpServletRequest request) {
		if(rootContainer == null) {
			rootContainer = new LocalFolderImpl(new File(FolderConfig.getCanonicalRepositoryHome()));
		}
		
		if(relPath.startsWith("/")) {
			relPath = relPath.substring(1, relPath.length());
		}
		MediaResource resource = null;
		VFSItem image = rootContainer.resolve(relPath);
		if(image instanceof VFSLeaf) {
			if(image.canMeta() == VFSConstants.YES) {
				MetaInfo info = image.getMetaInfo();
				//121 is needed to fill the div
				VFSLeaf thumbnail = info.getThumbnail(180, 120, true);
				if(thumbnail != null) {
					resource = new VFSMediaResource(thumbnail);
				}	
			}
			
			if(resource == null) {
				resource = new VFSMediaResource((VFSLeaf)image);
			}
		}
		return resource;
	}
}
