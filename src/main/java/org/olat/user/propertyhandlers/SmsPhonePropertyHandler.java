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
package org.olat.user.propertyhandlers;

import java.util.Locale;
import java.util.Map;

import org.olat.core.gui.components.form.ValidationError;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.id.User;
import org.olat.user.propertyhandlers.ui.SmsPhoneElement;

/**
 * 
 * Initial date: 3 févr. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class SmsPhonePropertyHandler extends PhonePropertyHandler {
	
	@Override
	public FormItem addFormItem(Locale locale, User user, String usageIdentifyer, boolean isAdministrativeUser,
			FormItemContainer formItemContainer) {
		String name = getName();
		SmsPhoneElement phoneEl = new SmsPhoneElement(name, this, user, locale);
		phoneEl.setLabel("form.name." + name, null);
		formItemContainer.add(phoneEl);
		return phoneEl;
	}
	
	@Override
	public boolean isValid(User user, FormItem formItem, Map<String,String> formContext) {
		return true;
	}

	@Override
	public boolean isValidValue(User user, String value, ValidationError validationError, Locale locale) {
		return true;
	}
	
	@Override
	public String getStringValue(FormItem formItem) {
		return ((SmsPhoneElement) formItem).getPhone();
	}

}
