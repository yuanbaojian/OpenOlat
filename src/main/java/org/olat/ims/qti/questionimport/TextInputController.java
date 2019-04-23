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
package org.olat.ims.qti.questionimport;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.olat.core.dispatcher.mapper.Mapper;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.StepFormBasicController;
import org.olat.core.gui.control.generic.wizard.StepsEvent;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.core.gui.media.MediaResource;
import org.olat.core.gui.media.StreamedMediaResource;

/**
 * 
 * Initial date: 24.09.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class TextInputController extends StepFormBasicController {

	private String validatedInp;
	private TextElement inputElement;
	
	private List<ItemAndMetadata> parsedItems;
	private final ItemsPackage importedItems;
	private final ImportOptions options;
	
	public TextInputController(UserRequest ureq, WindowControl wControl, StepsRunContext runContext, Form rootForm,
			ItemsPackage importedItems, ImportOptions options) {
		super(ureq, wControl, rootForm, runContext, LAYOUT_VERTICAL, null);
		this.importedItems = importedItems;
		this.options = options;
		initForm(ureq);
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		setFormDescription("wizard.import.input.description");
		setFormContextHelp("Data Management#qb_import");
		
		FormLayoutContainer textContainer = FormLayoutContainer.createCustomFormLayout("index", getTranslator(), velocity_root + "/example.html");
		formLayout.add(textContainer);
		String mapperURI = registerMapper(ureq, new ExampleMapper());
		textContainer.contextPut("mapperURI", mapperURI);
		
		inputElement = uifactory.addTextAreaElement("importform", "form.importdata", -1, 10, 100, false, false, "", formLayout);
		inputElement.setMandatory(true);
		inputElement.setNotEmptyCheck("form.legende.mandatory");
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		
		String inp = inputElement.getValue();
		if(validatedInp == null || !validatedInp.equals(inp)) {
			boolean errors = convertInputField();
			allOk &= !errors;
		}
		
		return allOk & super.validateFormLogic(ureq);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		importedItems.setItems(parsedItems);
		fireEvent(ureq, StepsEvent.ACTIVATE_NEXT);
	}
	
	private boolean convertInputField() {
		boolean importDataError = false;

		try {
			CSVToQuestionConverter converter = new CSVToQuestionConverter(getTranslator(), options);
			converter.parse(inputElement.getValue());
			parsedItems = converter.getItems();
		} catch (NumberFormatException e) {
			logError("", e);
			inputElement.setErrorKey("error.format.number", new String[] { e.getMessage() } );
			importDataError = true;
		}

		return importDataError;
	}
	
	private static class ExampleMapper implements Mapper {
		@Override
		public MediaResource handle(String relPath, HttpServletRequest request) {
			InputStream in = TextInputController.class.getResourceAsStream("qti-import-metadata.xlsx");
			return new StreamedMediaResource(in, "ImportExample.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		}
	}
}