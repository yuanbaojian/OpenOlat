/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.  
* <p>
*/ 

package org.olat.core.gui.components.form.flexible.impl.elements;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.ComponentRenderer;
import org.olat.core.gui.components.form.flexible.impl.FormBaseComponentImpl;
import org.olat.core.gui.control.JSAndCSSAdder;
import org.olat.core.gui.render.ValidationResult;

/**
 * Description:<br>
 * 
 * Initial Date: 31.01.08 <br>
 * 
 * @author rhaag
 */
class TextAreaElementComponent extends FormBaseComponentImpl {
	private ComponentRenderer RENDERER = new TextAreaElementRenderer();
	private TextAreaElementImpl element;
	private int cols;
	private int rows;
	private boolean autoHeightEnabled = false;
	private boolean fixedFontWidth = false;

	/**
	 * Constructor for a text area element
	 * 
	 * @param element
	 * @param rows the number of lines or -1 to use default value
	 * @param cols the number of characters per line or -1 to use 100% of the
	 *          available space
	 * @param isAutoHeightEnabled true: element expands to fit content height,
	 *          (max 100 lines); false: specified rows used
	 * @param fixedFontWidth 
	 */
	public TextAreaElementComponent(TextAreaElementImpl element, int rows, int cols, boolean isAutoHeightEnabled, boolean fixedFontWidth) {
		super(element.getName());
		this.element = element;
		setCols(cols);
		setRows(rows);
		this.autoHeightEnabled = isAutoHeightEnabled;
		this.fixedFontWidth = fixedFontWidth;
	}

	TextAreaElementImpl getTextAreaElementImpl() {
		return element;
	}

	/**
	 * @see org.olat.core.gui.components.Component#getHTMLRendererSingleton()
	 */
	@Override
	public ComponentRenderer getHTMLRendererSingleton() {
		return RENDERER;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
		setDirty(true);
	}

	public boolean isAutoHeightEnabled() {
		return autoHeightEnabled;
	}

	public boolean isFixedFontWidth() {
		return fixedFontWidth;
	}

	@Override
	public void validate(UserRequest ureq, ValidationResult vr) {
		super.validate(ureq, vr);
		if (fixedFontWidth) {
			JSAndCSSAdder jsa = vr.getJsAndCSSAdder();
			jsa.addRequiredStaticJsFile("js/jquery/taboverride/taboverride-4.0.0.min.js");
			jsa.addRequiredStaticJsFile("js/jquery/taboverride/jquery.taboverride.min.js");
		}
	}

}
