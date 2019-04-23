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
package org.olat.selenium.page.user;

import org.junit.Assert;
import org.olat.selenium.page.graphene.OOGraphene;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 
 * Initial date: 20.06.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class UserSettingsPage {

	private final WebDriver browser;
	
	public UserSettingsPage(WebDriver browser) {
		this.browser = browser;
	}

	/**
	 * Check that the user settings is displayed.
	 * @return
	 */
	public UserSettingsPage assertOnUserSettings() {
		By segementViewBy = By.className("o_segments");
		WebElement segmentView = browser.findElement(segementViewBy);
		Assert.assertTrue(segmentView.isDisplayed());
		return this;
	}
	
	/**
	 * Open the user preferences
	 * @return
	 */
	public UserPreferencesPageFragment openPreferences() {
		By preferencesSegmentBy = By.className("o_sel_user_settings_prefs");
		browser.findElement(preferencesSegmentBy).click();
		OOGraphene.waitBusy(browser);
		return new UserPreferencesPageFragment(browser);
	}
}
