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
package org.olat.modules.video;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


/**
 * 
 * Initial date: 5 avr. 2019<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@RunWith(Parameterized.class)
public class VideoFormatTest {
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "mov", VideoFormat.mp4 },
                { "zip", VideoFormat.mp4 },
                { "somethingelse", VideoFormat.mp4 },
                { "youtube", VideoFormat.youtube },
                { "vimeo", VideoFormat.vimeo },
                { "panopto", VideoFormat.panopto },
                { "mp4", VideoFormat.mp4 },
                { null, null }
        });
    }
    
    private String format;
    private VideoFormat expectedFormat;
    
    public VideoFormatTest(String format, VideoFormat expectedFormat) {
    	this.format = format;
    	this.expectedFormat = expectedFormat;
    }
    
    @Test
    public void conve() {
    	VideoFormat formatEnum = VideoFormat.secureValueOf(format);
    	Assert.assertEquals(expectedFormat, formatEnum);
    }

}
