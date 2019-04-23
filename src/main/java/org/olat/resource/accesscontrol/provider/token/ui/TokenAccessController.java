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

package org.olat.resource.accesscontrol.provider.token.ui;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.StringHelper;
import org.olat.resource.accesscontrol.ACService;
import org.olat.resource.accesscontrol.AccessResult;
import org.olat.resource.accesscontrol.OfferAccess;
import org.olat.resource.accesscontrol.provider.token.TokenAccessHandler;
import org.olat.resource.accesscontrol.ui.AccessEvent;
import org.olat.resource.accesscontrol.ui.FormController;


/**
 * 
 * Description:<br>
 * Ask for the token
 * 
 * <P>
 * Initial Date:  15 avr. 2011 <br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class TokenAccessController extends FormBasicController implements FormController {
	
	private TextElement tokenEl;
	private final OfferAccess link;
	private final ACService acService;

	public TokenAccessController(UserRequest ureq, WindowControl wControl, OfferAccess link) {
		super(ureq, wControl);
		
		this.link = link;
		acService = CoreSpringFactory.getImpl(ACService.class);
			
		initForm(ureq);
	}
	
	public TokenAccessController(UserRequest ureq, WindowControl wControl, OfferAccess link, Form form) {
		super(ureq, wControl, LAYOUT_DEFAULT, null, form);
		
		this.link = link;
		acService = CoreSpringFactory.getImpl(ACService.class);
			
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		setFormTitle("access.token.title");
		setFormDescription("access.token.desc");
		setFormTitleIconCss("o_icon o_icon-fw " + TokenAccessHandler.METHOD_CSS_CLASS + "_icon");
		formLayout.setElementCssClass("o_sel_accesscontrol_form");
		
		String description = link.getOffer().getDescription();
		if(StringHelper.containsNonWhitespace(description)) {
			description = StringHelper.xssScan(description);
			uifactory.addStaticTextElement("offer.description", description, formLayout);
		}
			
		tokenEl = uifactory.addTextElement("token", "accesscontrol.token", 255, "", formLayout);
		tokenEl.setElementCssClass("o_sel_accesscontrol_token_entry");
			
		final FormLayoutContainer buttonGroupLayout = FormLayoutContainer.createButtonLayout("buttonLayout", getTranslator());
		buttonGroupLayout.setRootForm(mainForm);
		formLayout.add(buttonGroupLayout);
			
		uifactory.addFormSubmitButton("access.button", formLayout);
	}
		
	@Override
	protected void doDispose() {
			//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		
		String token = tokenEl.getValue();
		tokenEl.clearError();
		if(token == null || token.length() < 2) {
			tokenEl.setErrorKey("invalid.token.format", null);
			allOk = false;
		}
		
		return allOk && super.validateFormLogic(ureq);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		String token = tokenEl.getValue();
		AccessResult result = acService.accessResource(getIdentity(), link, token);
		
		if(result.isAccessible()) {
			fireEvent(ureq, AccessEvent.ACCESS_OK_EVENT);
		} else {
			String msg = translate("invalid.token");
			fireEvent(ureq, new AccessEvent(AccessEvent.ACCESS_FAILED, msg));
		}
	}

	@Override
	public FormItem getInitialFormItem() {
		return flc;
	}
}