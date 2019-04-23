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
package org.olat.core.commons.services.pdf.model;

import java.io.Serializable;

import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.creator.ControllerCreator;
import org.olat.core.id.Identity;

/**
 * 
 * Initial date: 6 févr. 2019<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class PdfDelivery implements Serializable {

	private static final long serialVersionUID = -8032606199374880702L;
	private final String key;
	private String directory;
	private Identity identity;
	private ControllerCreator controllerCreator;
	private WindowControl windowControl;
	
	public PdfDelivery(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public WindowControl getWindowControl() {
		return windowControl;
	}

	public void setWindowControl(WindowControl windowControl) {
		this.windowControl = windowControl;
	}

	public ControllerCreator getControllerCreator() {
		return controllerCreator;
	}

	public void setControllerCreator(ControllerCreator controllerCreator) {
		this.controllerCreator = controllerCreator;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof PdfDelivery) {
			PdfDelivery delivery = (PdfDelivery)obj;
			return key != null && key.equals(delivery.key);
		}
		return false;
	}
}
