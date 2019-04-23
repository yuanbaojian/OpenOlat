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
package org.olat.course.certificate.ui;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.StringHelper;
import org.olat.core.util.mail.EmailAddressValidator;
import org.olat.course.certificate.CertificatesModule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 31 janv. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class CertificatesSettingsAdminController extends FormBasicController {
	
	private static final String[] onKeys = new String[]{ "on" };
	private static final String[] onValues = new String[]{ "" };

	private TextElement bccEl;
	private MultipleSelectionElement enableBccEl;
	
	@Autowired
	private CertificatesModule certificatesModule;
	
	/**
	 * @param name
	 * @param chatEnabled
	 */
	public CertificatesSettingsAdminController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		initForm (ureq);
	}
	
	@Override
	protected void doDispose() {
		//
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		setFormTitle("admin.certificates.options.title");
		setFormDescription("admin.certificates.options.descr", null);

		String bcc = certificatesModule.getCertificateBcc();
		enableBccEl = uifactory.addCheckboxesHorizontal("enableBcc", "admin.certificates.bcc.enable", formLayout, onKeys, onValues);
		enableBccEl.addActionListener(FormEvent.ONCHANGE);
		if(StringHelper.containsNonWhitespace(bcc)) {
			enableBccEl.select(onKeys[0], true);
		}
		
		bccEl = uifactory.addTextElement("bcc", "admin.certificates.bcc", 1024, bcc, formLayout);
		bccEl.setVisible(enableBccEl.isAtLeastSelected(1));
		
		FormLayoutContainer buttonsCont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		formLayout.add(buttonsCont);
		uifactory.addFormSubmitButton("save", buttonsCont);
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		
		bccEl.clearError();
		if(enableBccEl.isAtLeastSelected(1)) {
			String emails = bccEl.getValue();
			if(!StringHelper.containsNonWhitespace(emails)) {
				bccEl.setErrorKey("form.legende.mandatory", null);
				allOk &= false;
			} else {
				List<String> emailList = certificatesModule.splitEmails(bccEl.getValue());
				for(String email:emailList) {
					if(!EmailAddressValidator.isValidEmailAddress(email)) {
						bccEl.setErrorKey("error.mail.invalid", null);
						allOk &= false;
					}
				}
			}
		}

		return allOk & super.validateFormLogic(ureq);
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(enableBccEl == source) {
			bccEl.setVisible(enableBccEl.isAtLeastSelected(1));
		}
		super.formInnerEvent(ureq, source, event);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		if(enableBccEl.isAtLeastSelected(1)) {
			certificatesModule.setCertificateBcc(bccEl.getValue());
		} else {
			certificatesModule.setCertificateBcc("");
		}
	}
}