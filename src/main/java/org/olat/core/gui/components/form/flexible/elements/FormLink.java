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
package org.olat.core.gui.components.form.flexible.elements;

import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.link.Link;

public interface FormLink extends FormItem {
	
	public String getCmd();
	
	@Override
	public Link getComponent();

	/**
	 * @param customEnabledLinkCSS The customEnabledLinkCSS to set.
	 */
	public void setCustomEnabledLinkCSS(String customEnabledLinkCSS);

	/**
	 * Set the css that is used for the disabled link status
	 * @param customDisabledLinkCSS
	 */
	public void setCustomDisabledLinkCSS(String customDisabledLinkCSS);

	/**
	 * Set the icon class for the left sided icon
	 * @param iconCSS
	 */
	public void setIconLeftCSS(String iconCSS);

	/**
	 * Set the icon class for the right sided icon
	 * @param iconCSS
	 */
	public void setIconRightCSS(String iconCSS);

	/**
	 * Set the title used as hover text on link
	 * @param linkTitle
	 */
	public void setTitle(String linkTitle);


	public void setDomReplacementWrapperRequired(boolean required);
	
	/**
	 * @return The i18n key for the link text
	 */
	public String getI18nKey();

	/**
	 * Set the i18n key for the link text
	 * @param i18n
	 */
	public void setI18nKey(String i18n);
	

	/**
	 * Set the i18n key for the link text
	 * @param i18n
	 */
	public void setI18nKey(String i18n, String[] args);

	/**
	 * Set the i18n key for the link title or the translated title, depending on display mode
	 * @param i18nKey
	 */
	public void setLinkTitle(String i18nKey);
	
	public String getLinkTitleText();
	
	/**
	 * 
	 * @return The title of the link if disabled.
	 */
	public String getTextReasonForDisabling();
	
	/**
	 * Add a title (tooltip) to the link if it's disabled.
	 *  
	 * @param textReasonForDisabling
	 */
	public void setTextReasonForDisabling(String textReasonForDisabling);
	
	/**
	 * @param true: set link to active state (only render issue); false set to not active (default)
	 */
	public void setActive(boolean isActive);

	/**
	 * @param true: link is rendered as a primary link; false: rendered as secondary link
	 */
	public void setPrimary(boolean isPrimary);
	
	/**
	 * 
	 * @return true if the flexi form link will check if the form is dirty
	 */
	public boolean isForceOwnDirtyFormWarning();
	
	/**
	 * If warning is true, the link will check if the form is dirty.
	 * 
	 * @param warning
	 */
	public void setForceOwnDirtyFormWarning(boolean warning);

}