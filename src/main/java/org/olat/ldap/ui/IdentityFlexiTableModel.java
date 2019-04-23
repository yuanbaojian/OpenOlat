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
package org.olat.ldap.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiTableDataModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.id.Identity;
import org.olat.user.propertyhandlers.UserPropertyHandler;

/**
 * 
 * Initial date: 14.11.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class IdentityFlexiTableModel extends DefaultFlexiTableDataModel<Identity> {
		
	protected static final int USERNAME_COL_INDEX = 10000;
	
	private final Locale locale;
	private final List<UserPropertyHandler> handlers;
	
	public IdentityFlexiTableModel(List<Identity> identities, FlexiTableColumnModel columnModel, List<UserPropertyHandler> handlers, Locale locale) {
		super(identities, columnModel);
		this.handlers = handlers;
		this.locale = locale;
	}
	
	@Override
	public DefaultFlexiTableDataModel<Identity> createCopyWithEmptyList() {
		return new IdentityFlexiTableModel(new ArrayList<Identity>(), getTableColumnModel(), handlers, locale);
	}

	@Override
	public Object getValueAt(int row, int col) {
		Identity identity = getObject(row);
		if(col == USERNAME_COL_INDEX) {
			return identity.getName();
		}
		
		UserPropertyHandler userPropertyHandler = handlers.get(col);
		String value = userPropertyHandler.getUserProperty(identity.getUser(), locale);
		return (value == null ? "n/a" : value);
	}
}