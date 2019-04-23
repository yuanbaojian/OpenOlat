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
package org.olat.selenium.page.group;

import java.util.List;

import org.junit.Assert;
import org.olat.selenium.page.core.BookingPage;
import org.olat.selenium.page.graphene.OOGraphene;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Drive the groups site
 * 
 * Initial date: 03.07.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class GroupsPage {
	
	private WebDriver browser;
	
	public GroupsPage(WebDriver browser) {
		this.browser = browser;
	}
	
	public GroupsPage assertOnMyGroupsSelected() {
		By myGroupsBy = By.cssSelector("div.o_segments a.btn.btn-primary.o_sel_group_all_groups_seg");
		OOGraphene.waitElement(myGroupsBy, 5, browser);
		WebElement myGroupsEl = browser.findElement(myGroupsBy);
		Assert.assertTrue(myGroupsEl.isDisplayed());
		return this;
	}
	
	/**
	 * Select the tab "Published groups"
	 * 
	 * @return
	 */
	public GroupsPage publishedGroups() {
		By openGroupsBy = By.className("o_sel_group_open_groups_seg");
		browser.findElement(openGroupsBy).click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	public GroupPage createGroup(String name, String description) {
		//click create button
		By createBy = By.className("o_sel_group_create");
		browser.findElement(createBy).click();
		OOGraphene.waitModalDialog(browser);
		By popupBy = By.cssSelector("div.modal-content fieldset.o_sel_group_edit_group_form");
		OOGraphene.waitElement(popupBy, 5, browser);
		
		//fill the form
		By nameBy = By.cssSelector(".o_sel_group_edit_title input[type='text']");
		WebElement nameEl = browser.findElement(nameBy);
		nameEl.sendKeys(name);
		OOGraphene.tinymce(description, browser);
		
		//save
		By submitBy = By.cssSelector(".o_sel_group_edit_group_form button.btn-primary");
		WebElement submitButton = browser.findElement(submitBy);
		submitButton.click();
		OOGraphene.waitBusy(browser);
		By groupNameBy = By.xpath("//div[@id='o_main_center_content_inner']//div[contains(@class,'o_name')]//div[contains(text(),'" + name+ "')]");
		OOGraphene.waitElement(groupNameBy, 2, browser);
		
		return new GroupPage(browser);
	}
	
	/**
	 * Click on the book link
	 * 
	 * @param name
	 * @return
	 */
	public BookingPage bookGroup(String name) {
		By rowBy = By.cssSelector("div.o_table_wrapper tr");
		By colBy = By.cssSelector("td a");	
		WebElement groupLink = null;
		List<WebElement> rows = browser.findElements(rowBy);
		for(WebElement row:rows) {
			if(row.getText().contains(name)) {
				// take the last link of the row
				List<WebElement> links = row.findElements(colBy);
				if(links.size() > 0) {
					groupLink = links.get(links.size() - 1);
				}
			}
		}
		
		Assert.assertNotNull(groupLink);
		groupLink.click();
		OOGraphene.waitBusy(browser);
		
		By tokenEntryBy = By.className("o_sel_accesscontrol_token_entry");
		OOGraphene.waitElement(tokenEntryBy, browser);
		return new BookingPage(browser);
	}
	
	/**
	 * Select a group in the list by its name
	 * @param name
	 * @return
	 */
	public GroupPage selectGroup(String name) {
		selectGroupInTable(name);
		By groupNameBy = By.xpath("//div[@id='o_main_center_content_inner']//div[contains(@class,'o_name')]//div[contains(text(),'" + name+ "')]");
		OOGraphene.waitElement(groupNameBy, browser);
		return new GroupPage(browser);
	}
	
	private GroupsPage selectGroupInTable(String name) {
		By linkBy = By.cssSelector("div.o_table_wrapper td a");
		
		WebElement groupLink = null;
		List<WebElement> links = browser.findElements(linkBy);
		for(WebElement link:links) {
			if(link.getText().contains(name)) {
				groupLink = link;
			}
		}
		
		Assert.assertNotNull(groupLink);
		groupLink.click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	public GroupsPage deleteGroup(String name) {
		By groupNameBy = By.xpath("//table//td[//a[text()[contains(.,'" + name+ "')]]]//a[contains(@href,'bgTblDelete')]");
		browser.findElement(groupNameBy).click();
		OOGraphene.waitBusy(browser);
		
		//wait confirm dialog
		By popupBy = By.cssSelector("div.modal-dialog");
		OOGraphene.waitElement(popupBy, 2, browser);
		
		By okBy = By.cssSelector("div.modal-dialog button.btn.btn-primary");
		browser.findElement(okBy).click();
		OOGraphene.waitBusy(browser);
		OOGraphene.waitAndCloseBlueMessageWindow(browser);
		return this;
	}
	
	public GroupsPage assertDeleted(String name) {
		By groupNameBy = By.xpath("//table//td[//a[text()[contains(.,'" + name+ "')]]]");
		List<WebElement> groupEls = browser.findElements(groupNameBy);
		Assert.assertTrue(groupEls.isEmpty());
		return this;
	}
}
