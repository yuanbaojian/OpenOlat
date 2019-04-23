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
package org.olat.core.commons.services.webdav.servlets;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * Initial date: 04.03.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@RunWith(Parameterized.class)
public class RequestUtilsTest {
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "Test", "Test" },
                { "Test\u00E9\u00E4", "Test\u00E9\u00E4" },
                { "%12 ?()_//", "_12 _()___" },
                { " Test  ", "Test" }
        });
    }
    
    private String filename;
    private String normalizedFilename;
    
	public RequestUtilsTest(String filename, String normalizedFilename) {
		this.filename = filename;
		this.normalizedFilename = normalizedFilename;
	}
	
	@Test
	public void testNormalizeFilename() {
		String correctedFilename = RequestUtil.normalizeFilename(filename);
		Assert.assertEquals(normalizedFilename, correctedFilename);
	}
}
