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
package org.olat.modules.quality.ui;

import java.util.Date;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.DateChooser;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.modules.quality.QualityDataCollection;
import org.olat.modules.quality.QualityReminder;
import org.olat.modules.quality.QualityReminderType;
import org.olat.modules.quality.QualityService;
import org.olat.modules.quality.ui.security.DataCollectionSecurityCallback;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 10.07.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class RemindersController extends FormBasicController {

	private DateChooser invitationEl;
	private DateChooser reminder1El;
	private DateChooser reminder2El;
	private FormLayoutContainer buttonLayout;
	
	private DataCollectionSecurityCallback secCallback;
	private QualityDataCollection dataCollection;
	
	private QualityReminder invitation;
	private QualityReminder reminder1;
	private QualityReminder reminder2;
	
	@Autowired
	private QualityService qualityService;

	public RemindersController(UserRequest ureq, WindowControl wControl, DataCollectionSecurityCallback secCallback,
			QualityDataCollection dataCollection) {
		super(ureq, wControl);
		this.secCallback = secCallback;
		this.dataCollection = dataCollection;
		this.invitation = qualityService.loadReminder(dataCollection, QualityReminderType.INVITATION);
		this.reminder1 = qualityService.loadReminder(dataCollection, QualityReminderType.REMINDER1);
		this.reminder2 = qualityService.loadReminder(dataCollection, QualityReminderType.REMINDER2);
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		Date invitationDate = invitation != null? invitation.getSendPlaned(): null;
		invitationEl = uifactory.addDateChooser("reminder.invitation.date", invitationDate, formLayout);
		invitationEl.setDateChooserTimeEnabled(true);
		Date reminder1Date = reminder1 != null? reminder1.getSendPlaned(): null;
		reminder1El = uifactory.addDateChooser("reminder.reminder1.date", reminder1Date, formLayout);
		reminder1El.setDateChooserTimeEnabled(true);
		Date reminder2Date = reminder2 != null? reminder2.getSendPlaned(): null;
		reminder2El = uifactory.addDateChooser("reminder.reminder2.date", reminder2Date, formLayout);
		reminder2El.setDateChooserTimeEnabled(true);
		
		buttonLayout = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		buttonLayout.setRootForm(mainForm);
		formLayout.add(buttonLayout);
		uifactory.addFormSubmitButton("save", buttonLayout);
		
		updateUI();
	}

	protected void onChanged(QualityDataCollection dataCollection, DataCollectionSecurityCallback secCallback) {
		this.dataCollection = dataCollection;
		this.secCallback = secCallback;
		updateUI();
	}
	
	private void updateUI() {
		boolean canEditInvitation = secCallback.canEditReminder(invitation);
		invitationEl.setEnabled(canEditInvitation);
		boolean canEditReminder1 = secCallback.canEditReminder(reminder1);
		reminder1El.setEnabled(canEditReminder1);
		boolean canEditReminder2 = secCallback.canEditReminder(reminder2);
		reminder2El.setEnabled(canEditReminder2);
		buttonLayout.setVisible(canEditInvitation || canEditReminder1 || canEditReminder2);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		invitation = save(invitation, QualityReminderType.INVITATION, invitationEl);
		reminder1 = save(reminder1, QualityReminderType.REMINDER1, reminder1El);
		reminder2 = save(reminder2, QualityReminderType.REMINDER2, reminder2El);
		updateUI();
	}
	
	private QualityReminder save(QualityReminder reminder, QualityReminderType type, DateChooser reminderEl) {
		QualityReminder savedReminder = null;
		if (reminderEl.isEnabled()) {
			if (reminderEl.getDate() != null) {
				if (reminder != null) {
					savedReminder = qualityService.updateReminderDatePlaned(reminder, reminderEl.getDate());
				} else {
					savedReminder = qualityService.createReminder(dataCollection, reminderEl.getDate(), type);
				}
			} else {
				qualityService.deleteReminder(reminder);
			}
		}
		return savedReminder;
	}

	@Override
	protected void doDispose() {
		//
	}

}
