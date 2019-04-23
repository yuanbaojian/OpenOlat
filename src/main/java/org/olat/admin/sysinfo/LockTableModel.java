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
*/

package org.olat.admin.sysinfo;

import java.util.Date;
import java.util.List;

import org.olat.core.gui.components.table.DefaultTableDataModel;
import org.olat.core.util.Formatter;
import org.olat.core.util.coordinate.LockEntry;
import org.olat.user.UserManager;

/**
 * 
 * @author Christian Guretzki
 */

public class LockTableModel extends DefaultTableDataModel<LockEntry> {
	
	private final UserManager userManager;
	
	/**
	 * @param list of locks
	 */
	public LockTableModel(List<LockEntry> locks) {
		super(locks);
		userManager = UserManager.getInstance();
	}
	
	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}

	/**
	 * @see org.olat.core.gui.components.table.TableDataModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		LockEntry lock = getObject(row); 
		switch (Cols.values()[col]) {
			case key: return lock.getKey();
			case ownerName: return lock.getOwner().getName();
			case ownerFullname: return	userManager.getUserDisplayName(lock.getOwner());	
			case acquiredTime: return Formatter.formatDatetime(new Date(lock.getLockAquiredTime()));
			default: return "Error";
		}
	}
	
	public enum Cols {
		key,
		ownerName,
		ownerFullname,
		acquiredTime	
	}
}
