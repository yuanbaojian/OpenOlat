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
package org.olat.course.nodes.gta.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.elements.RichTextElement;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.StringHelper;
import org.olat.course.nodes.GTACourseNode;
import org.olat.modules.ModuleConfiguration;

/**
 * 
 * Initial date: 25.02.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class GTASubmissionEditController extends FormBasicController {
	
	private static final String[] enableKeys = new String[] { "on" };
	
	private RichTextElement textEl;
	private TextElement maxNumberOfDocsEl;
	private MultipleSelectionElement externalEditorEl, embeddedEditorEl, emailConfirmationEl;
	
	private final ModuleConfiguration config;
	
	public GTASubmissionEditController(UserRequest ureq, WindowControl wControl, ModuleConfiguration config) {
		super(ureq, wControl, LAYOUT_BAREBONE);
		
		this.config = config;
		
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		//configuration
		FormLayoutContainer configCont = FormLayoutContainer.createDefaultFormLayout("config", getTranslator());
		configCont.setRootForm(mainForm);
		configCont.setFormTitle(translate("editor.title"));
		configCont.setFormContextHelp("Assessment#_task_abgabe");
		formLayout.add(configCont);
		
		String[] enableValues = new String[]{ translate("enabled") };
		externalEditorEl = uifactory.addCheckboxesHorizontal("external.editor", "external.editor", configCont, enableKeys, enableValues);
		boolean external = config.getBooleanSafe(GTACourseNode.GTASK_EXTERNAL_EDITOR);
		externalEditorEl.select(enableKeys[0], external);
	
		embeddedEditorEl = uifactory.addCheckboxesHorizontal("embedded.editor", "embedded.editor", configCont, enableKeys, enableValues);
		boolean embbeded = config.getBooleanSafe(GTACourseNode.GTASK_EMBBEDED_EDITOR);
		embeddedEditorEl.select(enableKeys[0], embbeded);
		
		int maxDocs = config.getIntegerSafe(GTACourseNode.GTASK_MAX_SUBMITTED_DOCS, -1);
		String maxVal = "";
		if(maxDocs > 0) {
			maxVal = Integer.toString(maxDocs);
		}
		maxNumberOfDocsEl = uifactory.addTextElement("max.documents", "max.documents", 5, maxVal, configCont);
		
		//confirmation
		FormLayoutContainer confirmationCont = FormLayoutContainer.createDefaultFormLayout("confirmation", getTranslator());
		confirmationCont.setFormTitle(translate("confirmation.title"));
		confirmationCont.setRootForm(mainForm);
		formLayout.add(confirmationCont);
		
		String text = config.getStringValue(GTACourseNode.GTASK_SUBMISSION_TEXT);
		if(!StringHelper.containsNonWhitespace(text)) {
			text = translate("submission.confirmation");
		}
		textEl = uifactory.addRichTextElementForStringDataMinimalistic("text", "submission.text", text, 10, -1, confirmationCont, getWindowControl());
		textEl.setMandatory(true);
		
		emailConfirmationEl = uifactory.addCheckboxesHorizontal("confirmation", "submission.email.confirmation", confirmationCont, enableKeys, enableValues);
		boolean confirm = config.getBooleanSafe(GTACourseNode.GTASK_SUBMISSION_MAIL_CONFIRMATION);
		emailConfirmationEl.select(enableKeys[0], confirm);
		
		//save
		FormLayoutContainer buttonsCont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		buttonsCont.setRootForm(mainForm);
		confirmationCont.add(buttonsCont);
		uifactory.addFormSubmitButton("save", buttonsCont);
		uifactory.addFormCancelButton("cancel", buttonsCont, ureq, getWindowControl());
	}
	
	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		
		embeddedEditorEl.clearError();
		externalEditorEl.clearError();
		if(!externalEditorEl.isAtLeastSelected(1) && !embeddedEditorEl.isAtLeastSelected(1)) {
			externalEditorEl.setErrorKey("error.editor.atLeastOne", null);
			embeddedEditorEl.setErrorKey("error.editor.atLeastOne", null);
			allOk &= false;
		}
		
		maxNumberOfDocsEl.clearError();
		String maxVal = maxNumberOfDocsEl.getValue();
		if(StringHelper.containsNonWhitespace(maxVal)) {
			try {
				int val = Integer.parseInt(maxVal);
				if(val <= 0 || val > 12) {
					maxNumberOfDocsEl.setErrorKey("error.number.format", null);
					allOk &= false;
				}
			} catch (NumberFormatException e) {
				//can happen
				allOk &= false;
				maxNumberOfDocsEl.setErrorKey("error.number.format", null);
			}
		}
		
		return allOk & super.validateFormLogic(ureq);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		boolean externalEditor = externalEditorEl.isAtLeastSelected(1);
		config.setBooleanEntry(GTACourseNode.GTASK_EXTERNAL_EDITOR, externalEditor);
		boolean embeddedEditor = embeddedEditorEl.isAtLeastSelected(1);
		config.setBooleanEntry(GTACourseNode.GTASK_EMBBEDED_EDITOR, embeddedEditor);
		
		String maxVal = maxNumberOfDocsEl.getValue();
		if(StringHelper.isLong(maxVal)) {
			try {
				int val = Integer.parseInt(maxVal);
				config.setIntValue(GTACourseNode.GTASK_MAX_SUBMITTED_DOCS, val);
			} catch (NumberFormatException e) {
				//can happen
			}
		} else {
			config.remove(GTACourseNode.GTASK_MAX_SUBMITTED_DOCS);
		}
		
		String text = textEl.getValue();
		config.setStringValue(GTACourseNode.GTASK_SUBMISSION_TEXT, text);
		boolean emailConfirmation = emailConfirmationEl.isAtLeastSelected(1);
		config.setBooleanEntry(GTACourseNode.GTASK_SUBMISSION_MAIL_CONFIRMATION, emailConfirmation);
		
		fireEvent(ureq, Event.DONE_EVENT);
	}
}
