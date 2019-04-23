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
package org.olat.ims.qti21.ui.assessment;

import java.io.File;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FileElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.Util;
import org.olat.ims.qti21.QTI21Service;
import org.olat.ims.qti21.model.DigitalSignatureValidation;
import org.olat.ims.qti21.ui.QTI21RuntimeController;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 28 févr. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class ValidationXmlSignatureController extends FormBasicController {
	
	private FileElement uploadEl;
	private FormLayoutContainer validationMessage;
	
	@Autowired
	private QTI21Service qtiService;
	
	public ValidationXmlSignatureController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl, Util.createPackageTranslator(QTI21RuntimeController.class, ureq.getLocale()));
		
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		
		String page = velocity_root + "/validation.html";
		validationMessage = FormLayoutContainer.createCustomFormLayout("validation", getTranslator(), page);
		validationMessage.setVisible(false);
		validationMessage.setLabel(null, null);
		formLayout.add(validationMessage);
		
		uploadEl = uifactory.addFileElement(getWindowControl(), "validate.xml.signature.file", formLayout);
		uploadEl.addActionListener(FormEvent.ONCHANGE);
		
		FormLayoutContainer buttonsCont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		formLayout.add(buttonsCont);
		uifactory.addFormSubmitButton("ok", buttonsCont);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void formOK(UserRequest ureq) {
		fireEvent(ureq, Event.DONE_EVENT);
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(source == uploadEl) {
			doValidate();
		}
	}
	
	private void doValidate() {
		File xmlSignature = uploadEl.getUploadFile();
		if(xmlSignature != null && xmlSignature.exists()) {
			DigitalSignatureValidation validation = qtiService.validateAssessmentResult(xmlSignature);
			validationMessage.setVisible(true);
			validationMessage.contextPut("valid", validation.isValid());
			
			String msg;
			if(validation.isValid()) {
				msg = translate("validate.xml.signature.ok");
			} else {
				if(validation.getMessage() == DigitalSignatureValidation.Message.sessionNotFound) {
					msg = translate("warning.xml.signature.session.not.found");
				} else {
					msg = translate("warning.xml.signature.notok");
				}
			}
			validationMessage.contextPut("message", msg);
			flc.setDirty(true);
		}
	}
}
