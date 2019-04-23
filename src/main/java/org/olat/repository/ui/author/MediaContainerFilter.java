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
package org.olat.repository.ui.author;

import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSItem;
import org.olat.core.util.vfs.filters.VFSItemFilter;

/**
 * The filter only show the folder media under the root folder and its
 * children.
 * 
 * Initial date: 01.04.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class MediaContainerFilter implements VFSItemFilter {
	
	private final VFSContainer root;
	
	public MediaContainerFilter(VFSContainer root) {
		this.root = root;
	}

	@Override
	public boolean accept(VFSItem vfsItem) {
		if(root.equals(vfsItem.getParentContainer())) {
			return "media".equals(vfsItem.getName());
		}
		return true;
	}
}
