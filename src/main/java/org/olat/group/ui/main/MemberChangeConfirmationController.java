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
package org.olat.group.ui.main;

import java.util.List;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.id.Identity;
import org.olat.group.BusinessGroupModule;
import org.olat.user.UserManager;


/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class MemberChangeConfirmationController extends FormBasicController {
	
	private static final String[] keys = { "mail" };
	
	private final List<Identity> identities;
	private MultipleSelectionElement mailEl;
	
	private final UserManager userManager;
	private final BusinessGroupModule groupModule;
	
	public MemberChangeConfirmationController(UserRequest ureq, WindowControl wControl, List<Identity> identities) {
		super(ureq, wControl, "confirm_delete");
		this.identities = identities;
		userManager = CoreSpringFactory.getImpl(UserManager.class);
		groupModule = CoreSpringFactory.getImpl(BusinessGroupModule.class);
		initForm(ureq);
	}

	public List<Identity> getIdentities() {
		return identities;
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		if(identities != null && formLayout instanceof FormLayoutContainer) {
			StringBuilder sb = new StringBuilder(identities.size() * 25);
			for(Identity id:identities) {
				if(sb.length() > 0) sb.append(" / ");
				sb.append(userManager.getUserDisplayName(id));
			}
			((FormLayoutContainer)formLayout).contextPut("identities", sb.toString());
		}
		
		boolean mandatoryEmail = groupModule.isMandatoryEnrolmentEmail(ureq.getUserSession().getRoles());
		FormLayoutContainer optionsCont = FormLayoutContainer.createDefaultFormLayout("options", getTranslator());
		formLayout.add(optionsCont);
		formLayout.add("options", optionsCont);
		String[] values = new String[] {
				translate("remove.send.mail")
		};
		mailEl = uifactory.addCheckboxesHorizontal("typ", "remove.send.mail.label", optionsCont, keys, values);
		mailEl.select(keys[0], true);
		mailEl.setEnabled(!mandatoryEmail);
		
		FormLayoutContainer buttonCont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		formLayout.add(buttonCont);
		formLayout.add("buttons", buttonCont);
		uifactory.addFormSubmitButton("ok", buttonCont);
		uifactory.addFormCancelButton("cancel", buttonCont, ureq, getWindowControl());
	}
	
	public boolean isSendMail() {
		return mailEl.isSelected(0);
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
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}
}