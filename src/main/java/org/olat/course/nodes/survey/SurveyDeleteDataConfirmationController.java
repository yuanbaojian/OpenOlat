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
package org.olat.course.nodes.survey;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;

/**
 * 
 * Initial date: 03.05.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class SurveyDeleteDataConfirmationController extends FormBasicController {
	
	private static final String[] confirmationKeys = new String[] { "confirm.delete" };

	private MultipleSelectionElement confirmationEl;
	
	private final long numberOfSessions;
	
	public SurveyDeleteDataConfirmationController(UserRequest ureq, WindowControl wControl, long numberOfSessions) {
		super(ureq, wControl, "delete_data_confirmation");
		this.numberOfSessions = numberOfSessions;
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		FormLayoutContainer confirmCont = FormLayoutContainer.createDefaultFormLayout("confirm", getTranslator());
		formLayout.add("confirm", confirmCont);
		confirmCont.setRootForm(mainForm);
		
		flc.contextPut("numSessions", String.valueOf(numberOfSessions));
		
		String[] conformationValues = new String[] { translate("run.command.delete.data.all.check") };
		confirmationEl = uifactory.addCheckboxesHorizontal("confirm.delete", "", confirmCont, confirmationKeys, conformationValues);
		
		FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		confirmCont.add("buttons", buttonLayout);
		uifactory.addFormSubmitButton("run.command.delete.data.all.button", buttonLayout);
		uifactory.addFormCancelButton("cancel", buttonLayout, ureq, getWindowControl());
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		
		confirmationEl.clearError();
		if(!confirmationEl.isAtLeastSelected(1)) {
			confirmationEl.setErrorKey("run.command.delete.data.all.confirmation.error", null);
			allOk &= false;
		}
		
		return allOk & super.validateFormLogic(ureq);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		fireEvent(ureq, Event.DONE_EVENT);
	}
	
	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}

	@Override
	protected void doDispose() {
		//
	}

}
