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
package org.olat.selenium.page.qti;

import java.util.List;

import org.junit.Assert;
import org.olat.selenium.page.graphene.OOGraphene;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 
 * Initial date: 03 may 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public abstract class QTI21AssessmentItemEditorPage {
	
	public static final By tabBarBy = By.cssSelector("ul.o_sel_assessment_item_config>li>a");
	
	protected final WebDriver browser;
	
	protected QTI21AssessmentItemEditorPage(WebDriver browser) {
		this.browser = browser;
	}
	
	protected QTI21AssessmentItemEditorPage selectTab(By tabBy) {
		List<WebElement> tabLinks = browser.findElements(tabBarBy);

		boolean found = false;
		a_a:
		for(WebElement tabLink:tabLinks) {
			tabLink.click();
			OOGraphene.waitBusy(browser);
			List<WebElement> tabEls = browser.findElements(tabBy);
			if(tabEls.size() > 0) {
				found = true;
				break a_a;
			}
		}

		Assert.assertTrue("Found the tab", found);
		return this;
	}
}
