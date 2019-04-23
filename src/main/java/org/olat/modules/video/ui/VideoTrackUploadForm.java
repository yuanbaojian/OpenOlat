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
package org.olat.modules.video.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FileElement;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.modules.video.VideoManager;
import org.olat.modules.video.manager.VideoManagerImpl;
import org.olat.modules.video.ui.event.TrackUploadEvent;
import org.olat.resource.OLATResource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Uploadform to uplaod track files and save the corresponding language
 * 
 * Initial date: 01.04.2015<br>
 * @author dfurrer, dirk.furrer@frentix.com, http://www.frentix.com
 *
 */

public class VideoTrackUploadForm extends FormBasicController {
	
	private FileElement fileEl;
	private SingleSelection langsItem;
	private VFSContainer mediaContainer;
	
	@Autowired
	private VideoManager videoManager;

	public VideoTrackUploadForm(UserRequest ureq, WindowControl wControl, OLATResource videoResource) {
		super(ureq, wControl);
		mediaContainer = videoManager.getMasterContainer(videoResource);
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		List<String> langs = new ArrayList<>();
		List<String> dispLangs = new ArrayList<>();
		for(Locale locale : SimpleDateFormat.getAvailableLocales()){
			if(locale.hashCode() != 0){
				langs.add(locale.getLanguage());
				dispLangs.add(locale.getDisplayLanguage(getTranslator().getLocale()));
			}
		}

		List<String> langsWithoutDup = langs.parallelStream().distinct().collect(Collectors.toList());
		List<String> dispLangsWithoutDup = dispLangs.parallelStream().distinct().collect(Collectors.toList());

		langsItem = uifactory.addDropdownSingleselect("track.langs", formLayout,
				langsWithoutDup.toArray(new String[langsWithoutDup.size()]),
				dispLangsWithoutDup.toArray(new String[dispLangsWithoutDup.size()]), null);
		fileEl = uifactory.addFileElement(getWindowControl(), "track.upload", formLayout);
		langsItem.setMandatory(true);

		FormLayoutContainer buttonGroupLayout = FormLayoutContainer.createButtonLayout("buttonGroupLayout", getTranslator());
		formLayout.add(buttonGroupLayout);
		buttonGroupLayout.setElementCssClass("o_sel_upload_buttons");
		
		uifactory.addFormCancelButton("cancel", buttonGroupLayout, ureq, getWindowControl());
		uifactory.addFormSubmitButton("track.upload", buttonGroupLayout);
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		
		fileEl.clearError();
		if (fileEl.isUploadSuccess()) {
			String filename = fileEl.getUploadFileName();
			if(!filename.endsWith(VideoManager.FILETYPE_SRT)) {
				fileEl.setErrorKey("track.upload.error.filetype", null);
				allOk &= false;
			}
		} else {
			fileEl.setErrorKey("track.upload.error.nofile", null);
			allOk &= false;
		}

		return allOk & super.validateFormLogic(ureq);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		if (fileEl.isUploadSuccess()) {
			String uploadfilename = VideoManagerImpl.TRACK + langsItem.getSelectedKey()
				+ VideoManager.DOT + VideoManager.FILETYPE_SRT;
			fileEl.setUploadFileName(uploadfilename);
			VFSLeaf track = fileEl.moveUploadFileTo(mediaContainer);
			fireEvent(ureq, new TrackUploadEvent(langsItem.getSelectedKey(), track));
		}
	}

	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}

	@Override
	protected void doDispose() {
		// nothing to dispose
	}
}