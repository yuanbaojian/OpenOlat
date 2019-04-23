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
import org.olat.user.restapi.UserVO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 
 * Initial date: 12.02.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessmentToolPage {
	
	private final WebDriver browser;
	
	public AssessmentToolPage(WebDriver browser) {
		this.browser = browser;
	}
	
	public AssessmentToolPage users() {
		By usersBy = By.cssSelector("a.o_sel_assessment_tool_assessed_users");
		browser.findElement(usersBy).click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	public AssessmentToolPage assertOnUsers(UserVO user) {
		By usersCellsBy = By.cssSelector("div.o_table_flexi table tr td.text-left");
		List<WebElement> usersCellsList = browser.findElements(usersCellsBy);
		Assert.assertFalse(usersCellsList.isEmpty());
		
		boolean found = false;
		for(WebElement usersCell:usersCellsList) {
			found |= usersCell.getText().contains(user.getFirstName());
		}
		Assert.assertTrue(found);
		return this;
	}
	
	/**
	 * Select a user in "Users".
	 * 
	 * @param user
	 * @return
	 */
	public AssessmentToolPage selectUser(UserVO user) {
		By userLinksBy = By.xpath("//div[contains(@class,'o_table_flexi')]//table//tr//td//a[text()[contains(.,'" + user.getFirstName() + "')]]");
		browser.findElement(userLinksBy).click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	/**
	 * To see the list of course elements
	 * @return Itself
	 */
	public AssessmentToolPage courseElements() {
		By elementsBy = By.cssSelector("a.o_sel_assessment_tool_assessable_course_nodes");
		browser.findElement(elementsBy).click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	/**
	 * Select the course node in "Users" > "Course nodes".
	 * 
	 * @param nodeTitle The title of the course node
	 * @return Itself
	 */
	public AssessmentToolPage selectUsersCourseNode(String nodeTitle) {
		By rowsBy = By.xpath("//div[contains(@class,'o_table_wrapper')]//table//tr[td/span[contains(text(),'" + nodeTitle + "')]]/td/a[contains(@href,'cmd.select.node')]");
		List<WebElement> rowEls = browser.findElements(rowsBy);
		Assert.assertEquals(1, rowEls.size());
		rowEls.get(0).click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	/**
	 * Select the course node in the tree > "Course nodes".
	 * 
	 * @param nodeTitle The title of the course node
	 * @return Itself
	 */
	public AssessmentToolPage selectElementsCourseNode(String nodeTitle) {
		By elementBy = By.xpath("//div[contains(@class,'o_tree')]//ul//li[div/span/a/span[@class='o_tree_item'][contains(text(),'" + nodeTitle + "')]]/div/span/a[contains(@onclick,'nidle')]");
		OOGraphene.waitElement(elementBy, browser);
		browser.findElement(elementBy).click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	/**
	 * Check in "Users" > "Course nodes" if a specific course node
	 * is passed.
	 * 
	 * @param nodeTitle
	 * @return
	 */
	public AssessmentToolPage assertUserPassedCourseNode(String nodeTitle) {
		By rowsBy = By.xpath("//div[contains(@class,'o_table_wrapper')]//table//tr[td/span[contains(text(),'" + nodeTitle + "')]]");
		List<WebElement> rowEls = browser.findElements(rowsBy);
		Assert.assertEquals(1, rowEls.size());
		By passedBy = By.cssSelector("td span.o_state.o_passed");
		WebElement passedEl = rowEls.get(0).findElement(passedBy);
		Assert.assertTrue(passedEl.isDisplayed());
		return this;
	}
	
	/**
	 * Set the score in the assessment form
	 * @param score
	 * @return
	 */
	public AssessmentToolPage setAssessmentScore(float score) {
		By scoreBy = By.cssSelector(".o_sel_assessment_form_score input[type='text']");
		browser.findElement(scoreBy).sendKeys(Float.toString(score));
		
		By saveBy = By.cssSelector("button.btn.o_sel_assessment_form_save_and_close");
		browser.findElement(saveBy).click();
		OOGraphene.waitBusy(browser);
		return this;
	}
	
	public AssessmentToolPage assertPassed(UserVO user) {
		By userInfosBy = By.cssSelector("div.o_user_infos div.o_user_infos_inner table tr td");
		List<WebElement> userInfoList = browser.findElements(userInfosBy);
		Assert.assertFalse(userInfoList.isEmpty());
		boolean foundFirstName = false;
		for(WebElement userInfo:userInfoList) {
			foundFirstName |= userInfo.getText().contains(user.getFirstName());
		}
		Assert.assertTrue(foundFirstName);
		
		By passedBy = By.cssSelector("div.o_table_wrapper table tr td.text-left span.o_state.o_passed");
		List<WebElement> passedEl = browser.findElements(passedBy);
		Assert.assertFalse(passedEl.isEmpty());
		Assert.assertTrue(passedEl.get(0).isDisplayed());
		return this;
	}
	
	/**
	 * 
	 * @param user The user to overview
	 * @param progress The progress in percent
	 * @return Itself
	 */
	public AssessmentToolPage assertProgress(UserVO user, int progress) {
		By progressBy = By.xpath("//div[contains(@class,'o_table_wrapper')]/table//tr[td/a[contains(.,'" + user.getFirstName() + "')]]/td/div[@class='progress']/div[@title='" + progress + "%']");
		OOGraphene.waitElement(progressBy, 10, browser);
		return this;
	}
	
	public AssessmentToolPage assertStatusDone(UserVO user) {
		By doneBy = By.xpath("//div[contains(@class,'o_table_wrapper')]/table//tr[td/a[contains(.,'" + user.getFirstName() + "')]]/td/i[contains(@class,'o_icon_status_done')]");
		OOGraphene.waitElement(doneBy, 10, browser);
		return this;
	}
	
	public AssessmentToolPage assertProgressEnded(UserVO user) {
		By progressBy = By.xpath("//div[contains(@class,'o_table_wrapper')]/table//tr[td/a[contains(.,'" + user.getFirstName() + "')]]/td/div[@class='o_sel_ended']");
		OOGraphene.waitElement(progressBy, 10, browser);
		return this;
	}
	
	public AssessmentToolPage generateCertificate() {
		By userLinksBy = By.className("o_sel_certificate_generate");
		browser.findElement(userLinksBy).click();
		OOGraphene.waitBusy(browser);
		OOGraphene.waitAndCloseBlueMessageWindow(browser);

		By certificateBy = By.cssSelector("ul.o_certificates a>i.o_icon.o_filetype_pdf");
		OOGraphene.waitElement(certificateBy, 15, browser);
		return this;
	}
	
	public BulkAssessmentPage bulk() {
		By bulkBy = By.cssSelector("li.o_tool a.o_sel_assessment_tool_bulk");
		browser.findElement(bulkBy).click();
		OOGraphene.waitBusy(browser);
		
		By newBy = By.cssSelector("a.o_sel_assessment_tool_new_bulk_assessment");
		browser.findElement(newBy).click();
		OOGraphene.waitBusy(browser);
		OOGraphene.waitElement(By.cssSelector("fieldset.o_sel_bulk_assessment_data"), browser);
		return new BulkAssessmentPage(browser);
	}
	
	public AssessmentToolPage makeAllVisible() {
		OOGraphene.flexiTableSelectAll(browser);
		
		By bulkBy = By.cssSelector("a.btn.o_sel_assessment_bulk_visible");
		browser.findElement(bulkBy).click();
		OOGraphene.waitModalDialog(browser);
		
		By visibleBy = By.xpath("//div[contains(@class,'modal-body')]//input[@name='user.visibility'][@value='visible']");
		browser.findElement(visibleBy).click();
		
		By saveBy = By.cssSelector("div.modal-body button.btn-primary");
		browser.findElement(saveBy).click();
		OOGraphene.waitBusy(browser);
		
		return this;
	}
	
	/**
	 * Click back to the course
	 * 
	 * @return
	 */
	public CoursePageFragment clickToolbarRootCrumb() {
		OOGraphene.closeBlueMessageWindow(browser);
		By toolbarBackBy = By.xpath("//li[contains(@class,'o_breadcrumb_back')]/following-sibling::li/a");
		browser.findElement(toolbarBackBy).click();
		OOGraphene.waitBusy(browser);
		return new CoursePageFragment(browser);
	}
}