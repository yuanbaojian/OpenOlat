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
package org.olat.ims.qti21.ui.editor;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.form.flexible.impl.elements.FormSubmit;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.ims.qti21.ui.editor.events.AssessmentSectionEvent;

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;

/**
 * 
 * Initial date: 23 févr. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessmentSectionExpertOptionsEditorController extends ItemSessionControlController {

	private static final String[] yesnoKeys = new String[]{ "y", "n"};
	
	private SingleSelection visibleEl;
	
	private final AssessmentSection section;

	public AssessmentSectionExpertOptionsEditorController(UserRequest ureq, WindowControl wControl,
			AssessmentSection section, boolean restrictedEdit, boolean editable) {
		super(ureq, wControl, section, restrictedEdit, editable);
		this.section = section;
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		formLayout.setElementCssClass("o_sel_assessment_section_expert_options");
		setFormContextHelp("Test editor QTI 2.1 in detail#details_testeditor_section");
		if(!editable) {
			setFormWarning("warning.alien.assessment.test");
		}
		
		super.initForm(formLayout, listener, ureq);
		allowSkippingEl.setHelpUrlForManualPage("Test editor QTI 2.1 in detail#details_testeditor_section");
		allowCommentEl.setHelpUrlForManualPage("Test editor QTI 2.1 in detail#details_testeditor_section");
		allowReviewEl.setHelpUrlForManualPage("Test editor QTI 2.1 in detail#details_testeditor_section");
		showSolutionEl.setHelpUrlForManualPage("Test editor QTI 2.1 in detail#details_testeditor_section");
		
		//visible
		String[] yesnoValues = new String[]{ translate("yes"), translate("no") };
		visibleEl = uifactory.addRadiosHorizontal("visible", "form.section.visible", formLayout, yesnoKeys, yesnoValues);
		visibleEl.setElementCssClass("o_sel_assessment_section_visible");
		visibleEl.setEnabled(!restrictedEdit && editable);
		if (section.getVisible()) {
			visibleEl.select("y", true);
		} else {
			visibleEl.select("n", true);
		}
		
		FormLayoutContainer buttonsCont = FormLayoutContainer.createButtonLayout("butons", getTranslator());
		formLayout.add(buttonsCont);
		FormSubmit submit = uifactory.addFormSubmitButton("save", "save", buttonsCont);
		submit.setEnabled(editable);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = super.validateFormLogic(ureq);
		
		visibleEl.clearError();
		if(!visibleEl.isOneSelected()) {
			visibleEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}

		return allOk;
	}

	@Override
	protected void formOK(UserRequest ureq) {
		super.formOK(ureq);
		
		//visible
		boolean visible = visibleEl.isOneSelected() && visibleEl.isSelected(0);
		section.setVisible(visible);

		fireEvent(ureq, new AssessmentSectionEvent(AssessmentSectionEvent.ASSESSMENT_SECTION_CHANGED, section));
	}
}
