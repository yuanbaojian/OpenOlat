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
package org.olat.selenium.page.course;

import java.util.List;

import org.junit.Assert;
import org.olat.selenium.page.graphene.OOGraphene;
import org.olat.selenium.page.repository.UserAccess;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * 
 * Page fragment to control the publish process.
 * 
 * 
 * Initial date: 20.06.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class PublisherPageFragment {
	
	public static final By nextBy = By.className("o_wizard_button_next");
	public static final By finishBy = By.className("o_wizard_button_finish");
	public static final By selectAccessBy = By.cssSelector("div.o_sel_course_publish_wizard select");
	public static final By selectCatalogYesNoBy = By.cssSelector("div.o_sel_course_publish_wizard select");
	
	private final WebDriver browser;
	
	public PublisherPageFragment(WebDriver browser) {
		this.browser = browser;
	}
	
	public PublisherPageFragment assertOnPublisher() {
		OOGraphene.waitModalWizard(browser);
		By publishWizardBy = By.className("o_sel_course_publish_wizard");
		OOGraphene.waitElement(publishWizardBy, 5, browser);
		return this;
	}

	public void quickPublish() {
		quickPublish(UserAccess.guest);
	}
	
	public void quickPublish(UserAccess access) {
		assertOnPublisher()
			.nextSelectNodes()
			.selectAccess(access)
			.nextAccess()
			.selectCatalog(false)
			.nextCatalog() // -> no problem found
			.finish();
	}
	
	public PublisherPageFragment nextSelectNodes() {
		OOGraphene.nextStep(browser);
		OOGraphene.waitElement(By.cssSelector("fieldset.o_sel_repo_access_configuration"), 5, browser);
		return this;
	}
	
	public PublisherPageFragment nextAccess() {
		OOGraphene.nextStep(browser);
		OOGraphene.waitElement(By.cssSelector("div.o_course_editor_publish"), 5, browser);
		return this;
	}
	
	public PublisherPageFragment nextCatalog() {
		OOGraphene.nextStep(browser);
		OOGraphene.waitElement(By.cssSelector("div.o_sel_publish_warnings"), 5, browser);
		return this;
	}
	
	public PublisherPageFragment finish() {
		WebElement finish = browser.findElement(finishBy);
		Assert.assertTrue(finish.isDisplayed());
		Assert.assertTrue(finish.isEnabled());
		finish.click();
		OOGraphene.waitBusy(browser);
		OOGraphene.waitAndCloseBlueMessageWindow(browser);
		return this;
	}
	
	public PublisherPageFragment selectAccess(UserAccess access) {
		By publishStatusBy = By.id("o_fiopublishedStatus_SELBOX");
		OOGraphene.scrollTo(publishStatusBy, browser);
		WebElement publishStatusEl = browser.findElement(publishStatusBy);
		Select publishStatusSelect = new Select(publishStatusEl);
		publishStatusSelect.selectByValue("published");

		if(access == UserAccess.registred || access == UserAccess.guest) {
			By allUsersBy = By.xpath("//div[@id='o_coentry_access_type']/div/label/input[@name='entry.access.type' and @value='shared']");
			browser.findElement(allUsersBy).click();
			OOGraphene.waitBusy(browser);
			
			By guestsBy = By.xpath("//div[contains(@class,'o_sel_repositoryentry_access_guest')]//label[input[@name='entry.access.guest' and @value='on']]");
			OOGraphene.waitElement(guestsBy, browser);
			
			if(access == UserAccess.guest) {
				By labelGuestsBy = By.xpath("//div[contains(@class,'o_sel_repositoryentry_access_guest')]//label/input[@name='entry.access.guest' and @value='on']");

				WebElement guestsEl = browser.findElement(guestsBy);
				WebElement labelGuestsEl = browser.findElement(labelGuestsBy);
				OOGraphene.check(labelGuestsEl, guestsEl, Boolean.TRUE);
			}
		} else if(access == UserAccess.membersOnly) {
			By allUsersBy = By.xpath("//div[@id='o_coentry_access_type']/div/label/input[@name='entry.access.type' and @value='private']");
			browser.findElement(allUsersBy).click();
			OOGraphene.waitBusy(browser);
		} else if(access == UserAccess.booking) {
			By allUsersBy = By.xpath("//div[@id='o_coentry_access_type']/div/label/input[@name='entry.access.type' and @value='private']");
			browser.findElement(allUsersBy).click();
			OOGraphene.waitBusy(browser);
			
			By accessConfigurationBy = By.cssSelector("fieldset.o_ac_configuration");
			OOGraphene.waitElement(accessConfigurationBy, browser);
		}
		return this;
	}
	
	public PublisherPageFragment selectCatalog(boolean access) {
		OOGraphene.waitElement(selectCatalogYesNoBy, 5, browser);
		WebElement select = browser.findElement(selectCatalogYesNoBy);
		new Select(select).selectByValue(access ? "yes" : "no");
		return this;
	}
	
	public PublisherPageFragment selectCategory(String parentNode, String title) {
		By addToCatalogBy = By.className("o_sel_publish_add_to_catalog");
		WebElement addToCatalogButton = browser.findElement(addToCatalogBy);
		addToCatalogButton.click();
		OOGraphene.waitBusy(browser);
		
		if(parentNode != null) {
			selectCatalogNode(parentNode);
		}
		selectCatalogNode(title);
		
		By selectBy = By.cssSelector(".o_sel_catalog_chooser_tree a.o_sel_catalog_add_select");
		WebElement selectButton = browser.findElement(selectBy);
		selectButton.click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	private void selectCatalogNode(String name) {
		By nodeBy = By.cssSelector("span.o_tree_link>a");
		
		WebElement namedNode = null;
		List<WebElement> nodes = browser.findElements(nodeBy);
		for(WebElement node:nodes) {
			if(node.getText().contains(name)) {
				namedNode = node;
			}
		}
		Assert.assertNotNull(namedNode);
		namedNode.click();
		OOGraphene.waitBusy(browser);
	}
	
	public enum Access {
		owner("1"),
		authors("2"),
		users("3"),
		guests("4"),
		membersOnly("membersonly");

		private final String value;
		
		private Access(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
}
