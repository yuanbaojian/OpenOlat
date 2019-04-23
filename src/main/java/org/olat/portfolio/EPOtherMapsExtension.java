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
package org.olat.portfolio;

import java.util.List;

import org.olat.NewControllerFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.ContextEntryControllerCreator;
import org.olat.core.id.context.DefaultContextEntryControllerCreator;
import org.olat.home.HomeSite;

/**
 * Description:<br>
 * load my maps menu-entry. config here instead of xml allows en-/disabling at
 * runtime
 * 
 * <P>
 * Initial Date: 03.08.2010 <br>
 * 
 * @author Roman Haag, roman.haag@frentix.com, http://www.frentix.com
 */
public class EPOtherMapsExtension {

	public EPOtherMapsExtension() {
		NewControllerFactory.getInstance().addContextEntryControllerCreator("Map", new OtherMapsContextEntryControllerCreator());
	}
	
	private static class OtherMapsContextEntryControllerCreator extends DefaultContextEntryControllerCreator {

		@Override
		public ContextEntryControllerCreator clone() {
			return this;
		}

		@Override
		public String getSiteClassName(List<ContextEntry> ces, UserRequest ureq) {
			return HomeSite.class.getName();
		}
	}
}