/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.
*/

package org.olat.modules.co;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.modules.bc.FileUploadController;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FileElement;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.elements.RichTextElement;
import org.olat.core.gui.components.form.flexible.elements.SelectionElement;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.util.CSSHelper;
import org.olat.core.id.Identity;
import org.olat.core.util.FileUtils;
import org.olat.core.util.Formatter;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.core.util.filter.FilterFactory;
import org.olat.core.util.mail.ContactList;
import org.olat.core.util.mail.EmailAddressValidator;
import org.olat.core.util.mail.MailModule;
import org.olat.user.UserManager;

/**
 * highly configurable contact form. Depending on each field value the
 * corresponding fields become editable or not.
 * <p>
 * By creation time the defaults are: a FROM: field containing the logged in
 * users e-mail, an empty TO: field not editable, empty SUBJECT: and BODY:
 * fields both editable, and last but not least a submit cancel buttons pair.
 * <P>
 * <ul>
 * Send-Limitations:
 * <li>maxLength for 'body' TextAreaElement -> 10000 characters, about 4 pages
 * </li>>
 * <li>maxLength for 'to' TextAreaElement -> 30000 characters, enough space for
 * a lot of mail group names, each mail group containing umlimited ammount of
 * e-mail addresses</li>
 * </ul>
 * 
 * Initial Date: Jul 19, 2004
 * @author patrick
 */

public class ContactForm extends FormBasicController {
	//
	private static final String NLS_CONTACT_TO = "contact.to";
	private TextElement tto = null;
	private TextElement ttoBig = null;
	private static final String NLS_CONTACT_FROM = "contact.from";
	private TextElement tfrom;
	private static final String NLS_CONTACT_SUBJECT = "contact.subject";
	private TextElement tsubject;
	private static final String NLS_CONTACT_BODY = "contact.body";
	private RichTextElement tbody;
	private static final String NLS_CONTACT_ATTACHMENT = "contact.attachment";
	private static final String NLS_CONTACT_ATTACHMENT_EXPL = "contact.attachment.maxsize";
	private int contactAttachmentMaxSizeInMb = 5;
	private FileElement attachmentEl;
	private List<FormLink> attachmentLinks = new ArrayList<>();
	private FormLayoutContainer uploadCont;
	private boolean recipientsAreEditable = false;
	private static final int emailCols = 60;
	private boolean readOnly=false;
	private boolean hasMsgCancel=false;
	private boolean hasMsgSave=true;
	private static final String NLS_CONTACT_SEND_CP_FROM = "contact.cp.from";
	private SelectionElement tcpfrom;
	private Identity emailFrom;
	private File attachementTempDir;
	private long attachmentSize = 0l;
	private Map<String,String> attachmentCss = new HashMap<>();
	private Map<String,String> attachmentNames = new HashMap<>();
	private Map<String,ContactList> contactLists = new Hashtable<>();
	
	private final UserManager userManager;

	public ContactForm(UserRequest ureq, WindowControl wControl, Identity emailFrom, boolean readOnly, boolean isCancellable, boolean hasRecipientsEditable) {
		super(ureq, wControl);
		this.emailFrom = emailFrom;
		this.readOnly = readOnly;
		this.recipientsAreEditable = hasRecipientsEditable;
		this.hasMsgCancel = isCancellable;
		this.contactAttachmentMaxSizeInMb = CoreSpringFactory.getImpl(MailModule.class).getMaxSizeForAttachement();
		userManager = CoreSpringFactory.getImpl(UserManager.class);
		initForm(ureq);
	}
	
	public ContactForm(UserRequest ureq, WindowControl wControl, Form rootForm, Identity emailFrom, boolean readOnly, boolean isCancellable, boolean isSaveable, boolean hasRecipientsEditable) {
		super(ureq, wControl, LAYOUT_DEFAULT, null, rootForm);
		this.emailFrom = emailFrom;
		this.readOnly = readOnly;
		this.recipientsAreEditable = hasRecipientsEditable;
		this.hasMsgCancel = isCancellable;
		this.hasMsgSave = isSaveable;
		this.contactAttachmentMaxSizeInMb = CoreSpringFactory.getImpl(MailModule.class).getMaxSizeForAttachement();
		userManager = CoreSpringFactory.getImpl(UserManager.class);
		initForm(ureq);
	}
		

	protected void setSubject(final String defaultSubject) {
		tsubject.setValue(defaultSubject);
		tsubject.setEnabled(!readOnly);
		tsubject.setMandatory(tsubject.isEnabled());
	}

	/**
	 * add a ContactList as EmailTo:
	 * 
	 * @param emailList
	 */
	public void addEmailTo(ContactList emailList) {
		if (contactLists.containsKey(emailList.getName())) {
			//there is already a ContactList with this name...
			ContactList existing = contactLists.get(emailList.getName());
			//, merge their values.
			existing.add(emailList);
			//the form itself must not be updated, because it already displays
			// the name.
		} else {
			//a new ContactList, put it into contactLists
			contactLists.put(emailList.getName(), emailList);
			//and add its name in the form
			addContactFormEmailTo("[" + emailList.getName() + "]");
		}
	}

	private void addContactFormEmailTo(String defaultEmailTo) {
		defaultEmailTo += tto.getValue();
		tto.setValue(defaultEmailTo);
		ttoBig.setValue(defaultEmailTo);
		
		tto.setVisible(!recipientsAreEditable);
		ttoBig.setVisible(recipientsAreEditable);
	}

	public void setBody(String defaultBody) {
		tbody.setValue(defaultBody);
		tbody.setEnabled(!readOnly);
		tbody.setVisible(true);
		tbody.setMandatory(!readOnly);
	}
	
	@Override
	public boolean validateFormLogic(UserRequest ureq) {
		
		if(readOnly){
			return true;
		}
		boolean fromMailAddOk = true;
		if(tfrom.isEnabled()) {
			String mailInputValue = tfrom.getValue().trim();
			fromMailAddOk = EmailAddressValidator.isValidEmailAddress(mailInputValue);
			if(!fromMailAddOk){
				tfrom.setErrorKey("error.field.not.valid.email",null);
			}
		}
		boolean subjectOk = !tsubject.isEmpty("error.field.not.empty");
		boolean bodyOk = !tbody.isEmpty("error.field.not.empty");
		boolean toOk = false;
		if (tto != null) {
			toOk = !tto.isEmpty("error.field.not.empty");
		} else {
			toOk = !ttoBig.isEmpty("error.field.not.empty");
		}
		boolean fromOk = !tfrom.isEmpty("error.field.not.empty");
		return subjectOk && bodyOk && toOk && fromOk && fromMailAddOk;
	}

	public String getEmailFrom() {
		return tfrom.getValue().trim();
	}

	/**
	 * a List with ContactLists as elements is returned
	 * 
	 * @return
	 */
	public List<ContactList> getEmailToContactLists() {
		List<ContactList> retVal = new ArrayList<>();
		retVal.addAll(contactLists.values());
		return retVal;
	}

	/**
	 * retrieve the contact list names from the to field, and map them back to the
	 * stored contact lists names.
	 * 
	 * @return
	 */
	protected String getEmailTo() {
		String retVal = "";
		String value;
		if (tto != null) value = tto.getValue();
		else value = ttoBig.getValue();

		String sep = "";
		int i = 0;
		int j = -1;
		i = value.indexOf("[", j + 1);
		j = value.indexOf("]", j + 2);
		while (i > -1 && j > 0) {
			String contactListName = value.substring(i + 1, j);
			i = value.indexOf("[", j + 1);
			j = value.indexOf("]", j + 2);
			if (contactLists.containsKey(contactListName)) {
				ContactList found = contactLists.get(contactListName);
				retVal += sep + found.toString();
				sep = ", ";
			}
		}
		return retVal;
	}

	public String getSubject() {
		return tsubject.getValue();
	}

 	public String getBody() {
 		return tbody.getValue(FilterFactory.getSmileysCssToDataUriFilter());
	}
 	
 	public File[] getAttachments() {
 		List<File> attachments = new ArrayList<>();
 		for(FormLink removeLink : attachmentLinks) {
 			attachments.add((File)removeLink.getUserObject());
 		}
 		return attachments.toArray(new File[attachments.size()]);
 	}
 	
 	public void cleanUpAttachments() {
 		if(attachementTempDir != null && attachementTempDir.exists()) {
			FileUtils.deleteDirsAndFiles(attachementTempDir, true, true);
			attachementTempDir = null;
		}
 	}
 	
 	public boolean isTcpFrom() {
 		return tcpfrom.isSelected(0);
 	}
 	
 	protected void setDisplayOnly(boolean readOnly) {
 		this.readOnly = readOnly;
 		if (readOnly) {
 			flc.setEnabled(false);
 		}
 	}

	@Override
	protected void setFormTranslatedTitle(String translatedTitle) {
		super.setFormTranslatedTitle(translatedTitle);
	}
	
	@Override
	protected void setFormTranslatedDescription(String translatedDescription) {
		super.setFormTranslatedDescription(translatedDescription);
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
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(source == attachmentEl) {
			String filename = attachmentEl.getUploadFileName();
			if(attachementTempDir == null) {
				attachementTempDir = FileUtils.createTempDir("attachements", null, null);
			}
			
			long size = attachmentEl.getUploadSize();
			if(size + attachmentSize > (contactAttachmentMaxSizeInMb  * 1024 * 1024)) {
				showWarning(NLS_CONTACT_ATTACHMENT_EXPL, Integer.toString(contactAttachmentMaxSizeInMb));
				attachmentEl.reset();
			} else {
				File attachment = attachmentEl.moveUploadFileTo(attachementTempDir);
				// OO-48  somehow file-move can fail, check for it, display error-dialog if it failed
				if(attachment == null){
					attachmentEl.reset();
					logError("Could not move contact-form attachment to " + attachementTempDir.getAbsolutePath(), null);
					setTranslator(Util.createPackageTranslator(FileUploadController.class, getLocale(),getTranslator()));
					showError("FileMoveCopyFailed","");
					return;
				}
				attachmentEl.reset();
				attachmentSize += size;
				FormLink removeFile = uifactory.addFormLink(attachment.getName(), "delete", null, uploadCont, Link.BUTTON_XSMALL);
				removeFile.setIconLeftCSS("o_icon o_icon-fw o_icon_delete");
				removeFile.setUserObject(attachment);
				attachmentLinks.add(removeFile);
				//pretty labels
				uploadCont.setLabel(NLS_CONTACT_ATTACHMENT, null);
				attachmentNames.put(attachment.getName(), filename + " <span class='text-muted'>(" + Formatter.formatBytes(size) + ")</span>");
				attachmentCss.put(attachment.getName(), CSSHelper.createFiletypeIconCssClassFor(filename));
				uploadCont.contextPut("attachments", attachmentLinks);
				uploadCont.contextPut("attachmentNames", attachmentNames);
				uploadCont.contextPut("attachmentCss", attachmentCss);
				attachmentEl.setLabel(null, null);
			}
		} else if (attachmentLinks.contains(source)) {
			File uploadedFile = (File)source.getUserObject();
			if(uploadedFile != null && uploadedFile.exists()) {
				attachmentSize -= uploadedFile.length();
				uploadedFile.delete();
			}
			attachmentLinks.remove(source);
			uploadCont.remove(source);
			if(attachmentLinks.isEmpty()) {
				uploadCont.setLabel(null, null);
				attachmentEl.setLabel(NLS_CONTACT_ATTACHMENT, null);
			}
		}
		super.formInnerEvent(ureq, source, event);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		formLayout.setElementCssClass("o_sel_contact_form");
		
		setFormTitle("header.newcntctmsg");
		String fullName = userManager.getUserDisplayName(emailFrom);
		if(StringHelper.containsNonWhitespace(fullName)) {
			fullName = "[" + fullName + "]";
		}
		tfrom = uifactory.addTextElement("ttfrom", NLS_CONTACT_FROM, 255, fullName, formLayout);
		tfrom.setElementCssClass("o_sel_contact_to");
		// When no identity is set, let user enter a valid email address
		tfrom.setEnabled((this.emailFrom == null));
		
		tto = uifactory.addTextElement("tto", NLS_CONTACT_TO, 255, "", formLayout);
		tto.setElementCssClass("o_sel_contact_to");
		tto.setEnabled(false);
		tto.setVisible(false);
	
		ttoBig = uifactory.addTextAreaElement("ttoBig", NLS_CONTACT_TO, -1, 2, emailCols, true, false, "", formLayout);
		ttoBig.setEnabled(false);
		ttoBig.setVisible(false);
		
		tsubject = uifactory.addTextElement("tsubject", NLS_CONTACT_SUBJECT, 255, "", formLayout);
		tsubject.setElementCssClass("o_sel_contact_subject");
		tsubject.setDisplaySize(emailCols);
		tbody = uifactory.addRichTextElementForStringDataMinimalistic("tbody", NLS_CONTACT_BODY, "", 15, emailCols, formLayout, getWindowControl());
		tbody.setElementCssClass("o_sel_contact_body");
		tbody.setEnabled(!readOnly);
		tbody.getEditorConfiguration().setRelativeUrls(false);
		tbody.getEditorConfiguration().setRemoveScriptHost(false);
		
		String VELOCITY_ROOT = Util.getPackageVelocityRoot(this.getClass());
		uploadCont = FormLayoutContainer.createCustomFormLayout("file_upload_inner", getTranslator(), VELOCITY_ROOT + "/attachments.html");
		uploadCont.setRootForm(mainForm);
		formLayout.add(uploadCont);
		
		attachmentEl = uifactory.addFileElement(getWindowControl(), "file_upload_1", NLS_CONTACT_ATTACHMENT, formLayout);
		attachmentEl.setLabel(NLS_CONTACT_ATTACHMENT, null);
		attachmentEl.addActionListener(FormEvent.ONCHANGE);
		attachmentEl.setExampleKey(NLS_CONTACT_ATTACHMENT_EXPL, new String[]{Integer.toString(contactAttachmentMaxSizeInMb)});
		

		tcpfrom = uifactory.addCheckboxesVertical("tcpfrom", "", formLayout, new String[]{"xx"}, new String[]{translate(NLS_CONTACT_SEND_CP_FROM)}, 1);
		
		FormLayoutContainer buttonGroupLayout = FormLayoutContainer.createButtonLayout("buttonGroupLayout", getTranslator());
		formLayout.add(buttonGroupLayout);
		
		if(hasMsgSave) {
			uifactory.addFormSubmitButton("msg.save", buttonGroupLayout);
		}
		if (hasMsgCancel) {
			uifactory.addFormCancelButton("msg.cancel", buttonGroupLayout, ureq, getWindowControl());
		}
	}

	@Override
	protected void doDispose() {
		cleanUpAttachments();
	}
 	
}