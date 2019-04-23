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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FlexiTableElement;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataModelFactory;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.modules.video.VideoManager;
import org.olat.modules.video.ui.VideoTracksTableModel.TrackTableCols;
import org.olat.modules.video.ui.event.TrackUploadEvent;
import org.olat.resource.OLATResource;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 
 * @author dfurrer, dirk.furrer@frentix.com, http://www.frentix.com
 *
 */
public class VideoTrackEditController extends FormBasicController {

	private FlexiTableElement tableEl;
	private VideoTracksTableModel tableModel;
	private FormLink addButton;
	private VideoTrackUploadForm trackUploadForm;
	private CloseableModalController cmc;

	private OLATResource videoResource;
	
	@Autowired
	private VideoManager videoManager;

	public VideoTrackEditController(UserRequest ureq, WindowControl wControl, OLATResource videoResource) {
		super(ureq, wControl, LAYOUT_BAREBONE);
		this.videoResource = videoResource;
		initForm(ureq);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		FormLayoutContainer generalCont = FormLayoutContainer.createVerticalFormLayout("general", getTranslator());
		generalCont.setFormTitle(translate("tab.video.trackConfig"));
		generalCont.setRootForm(mainForm);
		generalCont.setFormContextHelp("Learning resource: Video#_video_subtitle");
		formLayout.add(generalCont);

		FlexiTableColumnModel columnsModel = FlexiTableDataModelFactory.createFlexiTableColumnModel();
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(true, TrackTableCols.file.i18nKey(), TrackTableCols.file.ordinal(), true, TrackTableCols.file.name()));
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(TrackTableCols.language.i18nKey(), TrackTableCols.language.ordinal(), true, TrackTableCols.language.name()));
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(true, TrackTableCols.delete.i18nKey(), TrackTableCols.delete.ordinal(),false, TrackTableCols.delete.name()));
		tableModel = new VideoTracksTableModel(columnsModel, getLocale());

		tableEl = uifactory.addTableElement(getWindowControl(), "tracks", tableModel, getTranslator(), generalCont);
		tableEl.setCustomizeColumns(false);
		Map<String, VFSLeaf> tracks = videoManager.getAllTracks(videoResource);
		List<TrackTableRow> rows = new ArrayList<>(tracks.size());
		for (Map.Entry<String, VFSLeaf> entry : tracks.entrySet()) {
			rows.add(forgeRow(entry.getKey(), entry.getValue()));
		}
		tableModel.setObjects(rows);
		tableEl.setEmtpyTableMessageKey("track.notrack");

		addButton = uifactory.addFormLink("add.track", generalCont, Link.BUTTON);
	}
	
	private TrackTableRow forgeRow(String language, VFSLeaf track) {
		FormLink delButton = uifactory.addFormLink("lang_".concat(language), "deleteTrack", "track.delete", "track.delete", null, Link.LINK);
		delButton.setIconLeftCSS("o_icon o_icon-fw o_icon_delete_item");
		TrackTableRow row = new TrackTableRow(language, track, delButton);
		delButton.setUserObject(row);
		return row;
	}

	@Override
	protected void formOK(UserRequest ureq) {
		//
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if (addButton == source) {
			doAddTrack(ureq);
		} else if (source instanceof FormLink) {
			FormLink link = (FormLink)source;
			if("deleteTrack".equals(link.getCmd())) {
				TrackTableRow row = (TrackTableRow)link.getUserObject();
				videoManager.removeTrack(videoResource, row.getLanguage());
				List<TrackTableRow> rows = tableModel.getObjects();
				rows.remove(row);
				tableModel.setObjects(rows);
				tableEl.reset(true, true, true);
			}
		}
	}

	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if(source == trackUploadForm) {
			if(event instanceof TrackUploadEvent) {
				TrackUploadEvent fEvent = (TrackUploadEvent)event;
				TrackTableRow row = forgeRow(fEvent.getLang(), fEvent.getTrack());
				List<TrackTableRow> rows = tableModel.getObjects();
				rows.add(row);
				tableModel.setObjects(rows);
				tableEl.reset(true, true, true);
			}
			cmc.deactivate();
			cleanUp();
		} else if(cmc == source){
			cleanUp();
		}
	}
	
	private void cleanUp() {
		removeAsListenerAndDispose(trackUploadForm);
		removeAsListenerAndDispose(cmc);
		trackUploadForm = null;
		cmc = null;
	}

	private void doAddTrack(UserRequest ureq) {
		trackUploadForm = new VideoTrackUploadForm(ureq, getWindowControl(), videoResource);
		listenTo(trackUploadForm);
		
		cmc = new CloseableModalController(getWindowControl(), "close", trackUploadForm.getInitialComponent());
		listenTo(cmc);
		cmc.activate();
	}
}