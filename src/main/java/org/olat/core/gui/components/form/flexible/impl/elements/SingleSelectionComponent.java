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
package org.olat.core.gui.components.form.flexible.impl.elements;

import org.olat.core.gui.components.ComponentRenderer;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormBaseComponentImpl;

/**
 * 
 * Initial date: 12.06.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
class SingleSelectionComponent extends FormBaseComponentImpl {
	
	private static final ComponentRenderer RENDERER = new SingleSelectionRenderer();
	private final SingleSelectionImpl element;
	
	private int widthInPercent;
	private boolean escapeHtml;
	private boolean trailingSpace;
	private RadioElementComponent[] radioComponents;
	
	/**
	 * @param id A fix identifier for state-less behavior, must be unique or null
	 */
	public SingleSelectionComponent(String id, SingleSelectionImpl element) {
		super(id, element.getName(), null);
		this.element = element;
	}
	
	SingleSelectionImpl getSingleSelectionImpl(){
		return element;
	}
	
	
	RadioElementComponent[] getRadioComponents() {
		return radioComponents;
	}

	void setRadioComponents(RadioElementComponent[] radioComponents) {
		this.radioComponents = radioComponents;
	}

	public int getWidthInPercent() {
		return widthInPercent;
	}
	
	public boolean isTrailingSpace() {
		return trailingSpace;
	}
	
	public boolean isEscapeHtml() {
		return escapeHtml;
	}
	
	public void setEscapeHtml(boolean escapeHtml) {
		this.escapeHtml = escapeHtml;
	}

	/**
	 * Set a fix width to the enclosing div of vertical radios. Spaced
	 * had a space after the end div.
	 * 
	 * @param widthInPercent The width (example: 9%)
	 * @param spaced If true had a trailing space
	 */
	public void setWidthInPercent(int widthInPercent, boolean trailingSpace) {
		this.widthInPercent = widthInPercent;
		this.trailingSpace = trailingSpace;
	}

	@Override
	public ComponentRenderer getHTMLRendererSingleton() {
		return RENDERER;
	}
	
	public static class RadioElementComponent {

		private SingleSelection selectionWrapper;
		private int which;

		RadioElementComponent(SingleSelection selectionWrapper, int which) {
			this.selectionWrapper = selectionWrapper;
			this.which = which;
		}

		String getGroupingName(){
			return selectionWrapper.getName();
		}
		
		int getWhichWeAre(){
			return which;
		}

		String getKey() {
			return selectionWrapper.getKey(which);
		}
		
		String getFormDispatchId() {
			return selectionWrapper.getFormDispatchId() + "_R_" + which;
		}

		public String getValue() {
			return selectionWrapper.getValue(which);
		}

		public boolean isSelected() {
			return selectionWrapper.isSelected(which);
		}
		
		public int getAction(){
			return selectionWrapper.getAction();
		}
		
		public Form getRootForm(){
			return selectionWrapper.getRootForm();
		}
		
		public String getSelectionElementFormDisId(){
			return selectionWrapper.getFormDispatchId();
		}
		
	}
}