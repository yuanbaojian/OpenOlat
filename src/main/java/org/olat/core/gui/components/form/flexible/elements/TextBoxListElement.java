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
package org.olat.core.gui.components.form.flexible.elements;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.textboxlist.ResultMapProvider;

/**
 * Description:<br>
 * This interface wraps the TextBoxListElementComponent as a FormItem.
 * 
 * <P>
 * Initial Date: 27.08.2010 <br>
 * 
 * @author Roman Haag, roman.haag@frentix.com, http://www.frentix.com
 */
public interface TextBoxListElement extends FormItem {

	public String getValue();

	public List<String> getValueList();

	public void setAutoCompleteContent(Set<String> autoCompletionValues);

	public void setMapperProvider(ResultMapProvider provider);

	public void setAutoCompleteContent(Map<String, String> autoCompletionValues);

	
	/**
	 * set the maximal number of results that should be shown by the
	 * auto-completion list
	 * 
	 * @param maxResults
	 */
	public void setMaxResults(int maxResults);

	/**
	 * @return Returns the allowDuplicates.
	 */
	public boolean isAllowDuplicates();

	/**
	 * @param allowDuplicates
	 *            if set to false (default) duplicates will be filtered
	 *            automatically
	 */
	public void setAllowDuplicates(boolean allowDuplicates);

	/**
	 * 
	 * @return returns true if this textBoxListElement is allowed to add new
	 *         values (values, that are not in autocompletion set)
	 */
	public boolean isAllowNewValues();

	/**
	 * configures if this textBoxListElement is allowed to add new values
	 * 
	 * @param allowNewValues
	 */
	public void setAllowNewValues(boolean allowNewValues);
}
