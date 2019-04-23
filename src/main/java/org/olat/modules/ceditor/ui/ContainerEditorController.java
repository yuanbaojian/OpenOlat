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
package org.olat.modules.ceditor.ui;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.modules.ceditor.ContentEditorXStream;
import org.olat.modules.ceditor.PageElementEditorController;
import org.olat.modules.ceditor.PageElementStore;
import org.olat.modules.ceditor.model.ContainerColumn;
import org.olat.modules.ceditor.model.ContainerElement;
import org.olat.modules.ceditor.model.ContainerSettings;

/**
 * 
 * Initial date: 10 sept. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class ContainerEditorController extends FormBasicController implements PageElementEditorController {

	private boolean editMode = false;
	private ContainerElement container;
	private final PageElementStore<ContainerElement> store;
	
	public ContainerEditorController(UserRequest ureq, WindowControl wControl, ContainerElement container, PageElementStore<ContainerElement> store) {
		super(ureq, wControl, "container_editor");
		this.container = container;
		this.store = store;
		
		initForm(ureq);
		setEditMode(editMode);
	}
	
	@Override
	public boolean isEditMode() {
		return editMode;
	}

	@Override
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
		flc.getFormItemComponent().contextPut("editMode", Boolean.valueOf(editMode));
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		//
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void formOK(UserRequest ureq) {
		//
	}
	
	protected ContainerElement setNumOfColumns(int numOfColumns) {
		ContainerSettings settings = container.getContainerSettings();
		settings.setNumOfColumns(numOfColumns);
		return save(settings);
	}
	
	protected ContainerElement setElementAt(String fragmentId, int slot, String sibling) {
		ContainerSettings settings = container.getContainerSettings();
		settings.setElementAt(fragmentId, slot, sibling);
		return save(settings);
	}
	
	protected ContainerElement setElementIn(String elementId, String collocatorId) {
		ContainerSettings settings = container.getContainerSettings();
		ContainerColumn column = settings.getColumn(collocatorId);
		if(column != null) {
			column.getElementIds().add(elementId);
		}
		return save(settings);
	}
	
	protected ContainerElement removeElement(String fragmentId) {
		ContainerSettings settings = container.getContainerSettings();
		settings.removeElement(fragmentId);
		return save(settings);
	}
	
	protected ContainerElement moveUp(String elementId) {
		ContainerSettings settings = container.getContainerSettings();
		settings.moveUp(elementId);
		return save(settings);
	}
	
	protected ContainerElement moveDown(String elementId) {
		ContainerSettings settings = container.getContainerSettings();
		settings.moveDown(elementId);
		return save(settings);
	}
	
	
	protected ContainerElement addElement(String elementId, String collocatorId, PageElementTarget target) {
		ContainerSettings settings = container.getContainerSettings();
		ContainerColumn column = settings.getColumn(collocatorId);
		if(column != null) {
			List<String> elementIds = column.getElementIds();
			int index = elementIds.indexOf(collocatorId);
			if(target == PageElementTarget.below) {
				index++;
			}
			if(index < 0) {
				index = 0;
			}
			
			if(index >= 0 && index < elementIds.size()) {
				elementIds.add(index, elementId);
			} else {
				elementIds.add(elementId);
			}
		}
		return save(settings);
	}
	
	private ContainerElement save(ContainerSettings settings) {
		String settingsXml = ContentEditorXStream.toXml(settings);
		container.setLayoutOptions(settingsXml);
		container = store.savePageElement(container);
		return container;
	}
}
