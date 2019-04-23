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
package org.olat.selenium.page.taxonomy;

import java.util.List;

import org.junit.Assert;
import org.olat.modules.taxonomy.TaxonomyCompetenceTypes;
import org.olat.selenium.page.graphene.OOGraphene;
import org.olat.selenium.page.group.MembersWizardPage;
import org.olat.user.restapi.UserVO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 
 * Initial date: 22 mai 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class TaxonomyLevelPage {
	
	private final WebDriver browser;
	
	public TaxonomyLevelPage(WebDriver browser) {
		this.browser = browser;
	}
	
	public TaxonomyLevelPage assertOnTaxonomyLevel() {
		By overviewBy = By.cssSelector("div.o_sel_taxonomy_level_overview");
		OOGraphene.waitElement(overviewBy, browser);
		return this;
	}
	
	public TaxonomyLevelPage selectCompetence() {
		By configBy = By.className("o_sel_taxonomy_level_competences");
		return selectTab(configBy);
	}
	
	public TaxonomyLevelPage addCompetence(UserVO user, TaxonomyCompetenceTypes competence) {
		// open callout
		By addCompetenceBy = By.cssSelector("a.o_sel_competence_add");
		browser.findElement(addCompetenceBy).click();
		OOGraphene.waitBusy(browser);
		//choose competence
		OOGraphene.waitElement(By.cssSelector("div.popover-content ul.o_sel_tools"), browser);
		By competenceBy = By.xpath("//ul[contains(@class,'o_sel_tools')]/li/a[contains(@onclick,'add.competence." + competence + "')]");
		browser.findElement(competenceBy).click();
		OOGraphene.waitBusy(browser);
		OOGraphene.waitModalDialog(browser);
		
		MembersWizardPage members = new MembersWizardPage(browser);
		members.searchMember(user, true);
		OOGraphene.waitBusy(browser);
		
		By chooseBy = By.xpath("//fieldset[@class='o_sel_usersearch_searchform']//div[@class='o_table_buttons']/button[@name='msc']");
		browser.findElement(chooseBy).click();
		OOGraphene.waitModalDialogDisappears(browser);
		
		return this;
	}
	
	private TaxonomyLevelPage selectTab(By tabBy) {
		By tabLinkBy = By.cssSelector("ul.o_sel_taxonomy_level_tabs>li>a");
		List<WebElement> tabLinks = browser.findElements(tabLinkBy);

		boolean found = false;
		a_a:
		for(WebElement tabLink:tabLinks) {
			tabLink.click();
			OOGraphene.waitBusy(browser);
			List<WebElement> chooseRepoEntry = browser.findElements(tabBy);
			if(chooseRepoEntry.size() > 0) {
				found = true;
				break a_a;
			}
		}

		Assert.assertTrue("Found the tab", found);
		return this;
	}

}
