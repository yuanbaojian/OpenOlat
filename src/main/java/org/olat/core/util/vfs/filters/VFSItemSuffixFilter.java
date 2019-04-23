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

package org.olat.core.util.vfs.filters;

import java.util.Hashtable;

import org.olat.core.util.vfs.VFSItem;

/**
 * <h3>Description:</h3>
 * The VFSItemSuffixFilter filters VFSItems that end in the given suffixes.
 * <p>
 * Note that this is not restricted to VFSLeaves, it does also filter
 * VFSContainers!
 * 
 */
public class VFSItemSuffixFilter implements VFSItemFilter {
	private Hashtable<String,String> suffixes = new Hashtable<String,String>();

	/**
	 * Constructor
	 * @param suffixes Array of at suffixes to be added to this filter
	 */
	public VFSItemSuffixFilter(String[] suffixes) {
		for (int i = 0; i < suffixes.length; i++) {
			addSuffix(suffixes[i]);
		}
	}
	
	/**
	 * @param suffix
	 */
	public void addSuffix(String suffix) {
		suffix = suffix.toLowerCase();
		suffixes.put(suffix, suffix);
	}

	/**
	 * @param suffix
	 */
	public void removeSuffix(String suffix) {
		suffixes.remove(suffix.toLowerCase());
	}

	/**
	 * @see org.olat.core.util.vfs.filters.VFSItemFilter#accept(org.olat.core.util.vfs.VFSItem)
	 */
	public boolean accept(VFSItem vfsItem) {
		String name = vfsItem.getName().toLowerCase();
		int idx = name.lastIndexOf('.');
		if (idx >= 0) { 
			return suffixes.containsKey(name.substring(idx + 1));
		}
		return suffixes.containsKey(name);
	}

}
