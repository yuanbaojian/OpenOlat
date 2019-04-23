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

import org.olat.selenium.page.graphene.OOGraphene;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * 
 * Initial date: 11 mai 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class TaxonomyAdminPage {

	private final WebDriver browser;
	
	public TaxonomyAdminPage(WebDriver browser) {
		this.browser = browser;
	}
	
	public TaxonomyAdminPage assertOnTaxonomyList() {
		By taxonomyListBy = By.className("o_taxonomy_listing");
		OOGraphene.waitElement(taxonomyListBy, browser);
		return this;
	}
	
	public TaxonomyPage selectTaxonomy(String identifier) {
		OOGraphene.scrollTop(browser);//scroll top if the settings were set
		OOGraphene.waitingALittleBit();
		
		By selectBy = By.xpath("//div[@class='o_taxonomy_row'][div/div/h4/small[text()[contains(.,'" + identifier + "')]]]/div/div[@class='panel-body']/div[@class='pull-right']/a");
		OOGraphene.waitElement(selectBy, browser);
		browser.findElement(selectBy).click();
		OOGraphene.waitBusy(browser);
		return new TaxonomyPage(browser);
	}

}
