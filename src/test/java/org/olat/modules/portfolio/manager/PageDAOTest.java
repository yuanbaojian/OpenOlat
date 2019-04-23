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
package org.olat.modules.portfolio.manager;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.olat.core.commons.persistence.DB;
import org.olat.core.id.Identity;
import org.olat.modules.portfolio.Binder;
import org.olat.modules.portfolio.Page;
import org.olat.modules.portfolio.PageBody;
import org.olat.modules.portfolio.PagePart;
import org.olat.modules.portfolio.PortfolioRoles;
import org.olat.modules.portfolio.PortfolioService;
import org.olat.modules.portfolio.Section;
import org.olat.modules.portfolio.model.BinderImpl;
import org.olat.modules.portfolio.model.HTMLPart;
import org.olat.modules.portfolio.model.SpacerPart;
import org.olat.modules.portfolio.model.TitlePart;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 08.06.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class PageDAOTest extends OlatTestCase {
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private PageDAO pageDao;
	@Autowired
	private BinderDAO binderDao;
	@Autowired
	private PortfolioService portfolioService;
	
	
	@Test
	public void createBinderWithSectionAndPage() {
		BinderImpl binder = binderDao.createAndPersist("Binder p1", "A binder with a page", null, null);
		Section section = binderDao.createSection("Section", "First section", null, null, binder);
		dbInstance.commitAndCloseSession();
		
		Section reloadedSection = binderDao.loadSectionByKey(section.getKey());
		Page page = pageDao.createAndPersist("New page", "A brand new page.", null, null, true, reloadedSection, null);
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getKey());
		Assert.assertNotNull(page.getCreationDate());
		Assert.assertNotNull(page.getLastModified());
		Assert.assertEquals("New page", page.getTitle());
		Assert.assertEquals("A brand new page.", page.getSummary());
	}
	
	@Test
	public void createBinderWithSectionAndPages() {
		BinderImpl binder = binderDao.createAndPersist("Binder p1", "A binder with a page", null, null);
		Section section = binderDao.createSection("Section", "First section", null, null, binder);
		dbInstance.commitAndCloseSession();
		
		Section reloadedSection = binderDao.loadSectionByKey(section.getKey());
		for(int i=0; i<5; i++) {
			pageDao.createAndPersist("New page " + i, "A brand new page.", null, null, true, reloadedSection, null);
		}
		dbInstance.commitAndCloseSession();
		
		List<Page> pages = pageDao.getPages(binder, null);
		Assert.assertNotNull(pages);
		Assert.assertEquals(5, pages.size());

	}
	
	@Test
	public void createBinderWithSectionAndPageAndPart() {
		BinderImpl binder = binderDao.createAndPersist("Binder p1", "A binder with a page", null, null);
		Section section = binderDao.createSection("Section", "First section", null, null, binder);
		dbInstance.commitAndCloseSession();
		
		Section reloadedSection = binderDao.loadSectionByKey(section.getKey());
		Page page = pageDao.createAndPersist("Page 1", "A page with content.", null, null, true, reloadedSection, null);
		dbInstance.commitAndCloseSession();
		
		HTMLPart htmlPart = new HTMLPart();
		PageBody reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.persistPart(reloadedBody, htmlPart);
		dbInstance.commitAndCloseSession();
		
		//reload
		Page reloadedPage = pageDao.loadByKey(page.getKey());
		Assert.assertNotNull(reloadedPage);
		List<PagePart> parts = reloadedPage.getBody().getParts();
		Assert.assertNotNull(parts);
		Assert.assertEquals(1, parts.size());
		Assert.assertEquals(htmlPart, parts.get(0));
		
		//reload only pages
		List<PagePart> onlyParts = pageDao.getParts(page.getBody());
		Assert.assertNotNull(onlyParts);
		Assert.assertEquals(1, onlyParts.size());
		Assert.assertEquals(htmlPart, onlyParts.get(0));
	}
	
	@Test
	public void loadPageByBody() {
		BinderImpl binder = binderDao.createAndPersist("Binder body", "A binder with a page and a page body", null, null);
		Section section = binderDao.createSection("Section", "Body section", null, null, binder);
		dbInstance.commitAndCloseSession();
		
		Section reloadedSection = binderDao.loadSectionByKey(section.getKey());
		Page page = pageDao.createAndPersist("Page 1", "A page with body.", null, null, true, reloadedSection, null);
		dbInstance.commitAndCloseSession();

		Page reloadedPage = pageDao.loadByBody(page.getBody());
		Assert.assertNotNull(reloadedPage);
		Assert.assertEquals(page, reloadedPage);
	}
	
	@Test
	public void getPages_binder() {
		BinderImpl binder = binderDao.createAndPersist("Binder p2", "A binder with 2 page", null, null);
		Section section = binderDao.createSection("Section", "First section", null, null, binder);
		dbInstance.commitAndCloseSession();
		
		Section reloadedSection = binderDao.loadSectionByKey(section.getKey());
		Page page1 = pageDao.createAndPersist("Page 1", "A page with content.", null, null, true, reloadedSection, null);
		Page page2 = pageDao.createAndPersist("Page 2", "A page with content.", null, null, true, reloadedSection, null);
		Page page3 = pageDao.createAndPersist("Juno", "Juno is a spacecraft.", null, null, true, reloadedSection, null);
		dbInstance.commitAndCloseSession();

		//reload
		List<Page> sectionPages = pageDao.getPages(binder, null);
		Assert.assertNotNull(sectionPages);
		Assert.assertEquals(3, sectionPages.size());
		Assert.assertTrue(sectionPages.contains(page1));
		Assert.assertTrue(sectionPages.contains(page2));
		Assert.assertTrue(sectionPages.contains(page3));
		
		//reload
		List<Page> searchedPages = pageDao.getPages(binder, "juno");
		Assert.assertNotNull(searchedPages);
		Assert.assertEquals(1, searchedPages.size());
		Assert.assertFalse(searchedPages.contains(page1));
		Assert.assertFalse(searchedPages.contains(page2));
		Assert.assertTrue(searchedPages.contains(page3));
	}
	
	@Test
	public void getPages_section() {
		BinderImpl binder = binderDao.createAndPersist("Binder p2", "A binder with 2 page", null, null);
		Section section = binderDao.createSection("Section", "First section", null, null, binder);
		dbInstance.commitAndCloseSession();
		
		Section reloadedSection = binderDao.loadSectionByKey(section.getKey());
		Page page1 = pageDao.createAndPersist("Page 1", "A page with content.", null, null, true, reloadedSection, null);
		Page page2 = pageDao.createAndPersist("Page 2", "A page with content.", null, null, true, reloadedSection, null);
		Page page3 = pageDao.createAndPersist("Page 3", "A page with the demonstration of Hawking about black hole'evaporation.", null, null, true, reloadedSection, null);
		dbInstance.commitAndCloseSession();

		//reload
		List<Page> sectionPages = pageDao.getPages(reloadedSection);
		Assert.assertNotNull(sectionPages);
		Assert.assertEquals(3, sectionPages.size());
		Assert.assertTrue(sectionPages.contains(page1));
		Assert.assertTrue(sectionPages.contains(page2));
		Assert.assertTrue(sectionPages.contains(page3));
	}
	
	@Test
	public void getOwnedPages() {
		//an owned binder
		Identity author = JunitTestHelper.createAndPersistIdentityAsRndUser("pf-1");
		Binder binder = portfolioService.createNewBinder("Binder p2", "A binder with 2 page", null, author);
		Section section = binderDao.createSection("Section", "First section", null, null, binder);
		dbInstance.commitAndCloseSession();

		Section reloadedSection = binderDao.loadSectionByKey(section.getKey());
		Page page1 = pageDao.createAndPersist("Page 1", "A page with content.", null, null, true, reloadedSection, null);
		Page page2 = pageDao.createAndPersist("Page 2", "A page with content.", null, null, true, reloadedSection, null);
		Page page3 = pageDao.createAndPersist("Anime", "Princess Mononoke is the second most famous anime of Miazaki.", null, null, true, reloadedSection, null);
		dbInstance.commitAndCloseSession();
		
		//a not owned binder
		BinderImpl binderAlt = binderDao.createAndPersist("Not my binder", "A binder that I don't own", null, null);
		Section sectionAlt = binderDao.createSection("Section", "First section", null, null, binderAlt);
		dbInstance.commitAndCloseSession();
		
		Section reloadedSectionAlt = binderDao.loadSectionByKey(sectionAlt.getKey());
		Page pageAlt = pageDao.createAndPersist("Page alt", "A page with alternative content.", null, null, true, reloadedSectionAlt, null);
		dbInstance.commitAndCloseSession();

		//reload
		List<Page> sectionPages = pageDao.getOwnedPages(author, null);
		Assert.assertNotNull(sectionPages);
		Assert.assertEquals(3, sectionPages.size());
		Assert.assertTrue(sectionPages.contains(page1));
		Assert.assertTrue(sectionPages.contains(page2));
		Assert.assertTrue(sectionPages.contains(page3));
		Assert.assertFalse(sectionPages.contains(pageAlt));
		
		//reload
		List<Page> searchedPages = pageDao.getOwnedPages(author, "Miazaki");
		Assert.assertNotNull(searchedPages);
		Assert.assertEquals(1, searchedPages.size());
		Assert.assertFalse(searchedPages.contains(page1));
		Assert.assertFalse(searchedPages.contains(page2));
		Assert.assertTrue(searchedPages.contains(page3));
		Assert.assertFalse(sectionPages.contains(pageAlt));
	}
	
	@Test
	public void getMembers() {
		Identity author = JunitTestHelper.createAndPersistIdentityAsRndUser("pf-1");
		//an owned binder
		Page page = portfolioService.appendNewPage(author, "Page 3", "A page with content.", null, null, null);
		dbInstance.commitAndCloseSession();

		//reload
		List<Identity> owners = pageDao.getMembers(page, PortfolioRoles.owner.name());
		Assert.assertNotNull(owners);
		Assert.assertEquals(1, owners.size());
		Assert.assertEquals(author, owners.get(0));
	}
	
	@Test
	public void getLastPages() {
		//an owned binder
		Identity author = JunitTestHelper.createAndPersistIdentityAsRndUser("pf-1");
		Binder binder = portfolioService.createNewBinder("Binder p2", "A binder with 2 page", null, author);
		Section section = binderDao.createSection("Section", "First section", null, null, binder);
		dbInstance.commitAndCloseSession();

		Section reloadedSection = binderDao.loadSectionByKey(section.getKey());
		Page page1 = pageDao.createAndPersist("Page 1", "A page with content.", null, null, true, reloadedSection, null);
		sleep(1500);
		Page page2 = pageDao.createAndPersist("Page 2", "A page with content.", null, null, true, reloadedSection, null);
		sleep(1500);
		Page page3 = portfolioService.appendNewPage(author, "Page 3", "A page with content.", null, null, null);
		dbInstance.commitAndCloseSession();
		Assert.assertNotNull(page1);
		
		//reload
		List<Page> lastPage = pageDao.getLastPages(author, 1);
		Assert.assertNotNull(lastPage);
		Assert.assertEquals(1, lastPage.size());
		Assert.assertEquals(page3, lastPage.get(0));
		
		//reload
		List<Page> lastPages = pageDao.getLastPages(author, 2);
		Assert.assertNotNull(lastPages);
		Assert.assertEquals(2, lastPages.size());
		Assert.assertTrue(lastPages.contains(page3));
		Assert.assertTrue(lastPages.contains(page2));
	}
	
	@Test
	public void persistPart() {
		Page page = pageDao.createAndPersist("Page 1", "A page with content.", null, null, true, null, null);
		dbInstance.commitAndCloseSession();
		
		HTMLPart htmlPart = new HTMLPart();
		PageBody reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.persistPart(reloadedBody, htmlPart);
		dbInstance.commitAndCloseSession();
		
		TitlePart titlePart = new TitlePart();
		reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.persistPart(reloadedBody, titlePart, 0);
		dbInstance.commitAndCloseSession();
		
		//reload
		List<PagePart> reloadedPageParts = pageDao.getParts(reloadedBody);
		Assert.assertNotNull(reloadedPageParts);
		Assert.assertEquals(2, reloadedPageParts.size());
		Assert.assertEquals(titlePart, reloadedPageParts.get(0));
		Assert.assertEquals(htmlPart, reloadedPageParts.get(1));
	}
	
	@Test
	public void moveParts() {
		Page page = pageDao.createAndPersist("Page 1", "A page with content.", null, null, true, null, null);
		dbInstance.commitAndCloseSession();
		
		HTMLPart htmlPart = new HTMLPart();
		PageBody reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.persistPart(reloadedBody, htmlPart);
		dbInstance.commitAndCloseSession();
		
		TitlePart titlePart = new TitlePart();
		reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.persistPart(reloadedBody, titlePart, 0);
		dbInstance.commitAndCloseSession();
		
		SpacerPart spacePart = new SpacerPart();
		reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.persistPart(reloadedBody, spacePart, 0);
		dbInstance.commitAndCloseSession();
		
		//check the order
		List<PagePart> reloadedPageParts = pageDao.getParts(reloadedBody);
		Assert.assertNotNull(reloadedPageParts);
		Assert.assertEquals(3, reloadedPageParts.size());
		Assert.assertEquals(spacePart, reloadedPageParts.get(0));
		Assert.assertEquals(titlePart, reloadedPageParts.get(1));
		Assert.assertEquals(htmlPart, reloadedPageParts.get(2));
		
		//move title part up
		reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.moveUpPart(reloadedBody, titlePart);
		dbInstance.commitAndCloseSession();
		
		List<PagePart> moveUpPageParts = pageDao.getParts(reloadedBody);
		Assert.assertNotNull(moveUpPageParts);
		Assert.assertEquals(3, moveUpPageParts.size());
		Assert.assertEquals(titlePart, moveUpPageParts.get(0));
		Assert.assertEquals(spacePart, moveUpPageParts.get(1));
		Assert.assertEquals(htmlPart, moveUpPageParts.get(2));
		
		//move space part down
		reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.moveDownPart(reloadedBody, spacePart);
		dbInstance.commitAndCloseSession();
		
		List<PagePart> moveDownPageParts = pageDao.getParts(reloadedBody);
		Assert.assertNotNull(moveDownPageParts);
		Assert.assertEquals(3, moveDownPageParts.size());
		Assert.assertEquals(titlePart, moveDownPageParts.get(0));
		Assert.assertEquals(htmlPart, moveDownPageParts.get(1));
		Assert.assertEquals(spacePart, moveDownPageParts.get(2));

		//not useful move space part down
		reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.moveDownPart(reloadedBody, spacePart);
		dbInstance.commitAndCloseSession();
		
		List<PagePart> moveDownPageParts2 = pageDao.getParts(reloadedBody);
		Assert.assertNotNull(moveDownPageParts2);
		Assert.assertEquals(3, moveDownPageParts2.size());
		Assert.assertEquals(titlePart, moveDownPageParts2.get(0));
		Assert.assertEquals(htmlPart, moveDownPageParts2.get(1));
		Assert.assertEquals(spacePart, moveDownPageParts2.get(2));
	}

	@Test
	public void deletePart() {
		Page page = pageDao.createAndPersist("Page 10", "A page with content.", null, null, true, null, null);
		dbInstance.commitAndCloseSession();
		
		HTMLPart htmlPart = new HTMLPart();
		PageBody reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.persistPart(reloadedBody, htmlPart);
		dbInstance.commitAndCloseSession();
		
		TitlePart titlePart = new TitlePart();
		reloadedBody = pageDao.loadPageBodyByKey(page.getBody().getKey());
		pageDao.persistPart(reloadedBody, titlePart, 0);
		dbInstance.commitAndCloseSession();
		
		//reload
		Page reloadedPage = pageDao.loadByKey(page.getKey());
		pageDao.deletePage(reloadedPage);
		dbInstance.commit();
	}
}
