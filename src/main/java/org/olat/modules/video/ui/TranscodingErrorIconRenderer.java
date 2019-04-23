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
package org.olat.modules.video.ui;

import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiCellRenderer;
import org.olat.core.gui.components.table.IconCssCellRenderer;

/**
 * Initial date: 06.02.2017
 * @author fkiefer, fabian.kiefer@frentix.com, http://www.frentix.com
 */
public class TranscodingErrorIconRenderer extends IconCssCellRenderer implements FlexiCellRenderer {
	

	@Override
	protected String getCssClass(Object val) {
		if(val == null) {
			return null;
		}
		if(val instanceof Object[]) {
			Object[] value = (Object[])val;
			Integer status = (Integer)value[0];
			switch(status) {
				case -2: return "o_icon o_icon_warn o_icon-fw";
				case -3: case -4: return "o_icon o_icon_error o_icon-fw";
				default: return null;
			}
		}
		return null;
	}

	@Override
	protected String getCellValue(Object val) {
		if(val == null) {
			return null;
		}
		if(val instanceof Object[]) {
			Object[] value = (Object[])val;
			String desc = (String)value[1];
			return desc;
		}
		return null;
	}

	@Override
	protected String getHoverText(Object val) {
		return getCellValue(val);
	}

}

