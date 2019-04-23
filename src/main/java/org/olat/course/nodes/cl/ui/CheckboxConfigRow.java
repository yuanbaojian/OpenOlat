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
package org.olat.course.nodes.cl.ui;

import org.olat.core.gui.components.form.flexible.elements.DownloadLink;
import org.olat.course.nodes.cl.model.Checkbox;

/**
 * 
 * Initial date: 06.03.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class CheckboxConfigRow {
	
	private final Checkbox checkbox;
	private final DownloadLink download;
	
	public CheckboxConfigRow(Checkbox checkbox, DownloadLink download) {
		this.checkbox = checkbox;
		this.download = download;
	}
	
	public Checkbox getCheckbox() {
		return checkbox;
	}

	public String getTitle() {
		return checkbox.getTitle();
	}

	public Float getPoints() {
		return checkbox.getPoints();
	}

	public CheckboxReleaseEnum getRelease() {
		return checkbox.getRelease();
	}

	public DownloadLink getDownload() {
		return download;
	}
}
