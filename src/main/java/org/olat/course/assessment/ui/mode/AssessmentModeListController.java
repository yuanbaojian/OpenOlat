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
package org.olat.course.assessment.ui.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FlexiTableElement;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.elements.table.BooleanCellRenderer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataModelFactory;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SelectionEvent;
import org.olat.core.gui.components.form.flexible.impl.elements.table.StaticFlexiCellRenderer;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.stack.PopEvent;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.modal.DialogBoxController;
import org.olat.core.gui.control.generic.modal.DialogBoxUIFactory;
import org.olat.core.util.StringHelper;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.olat.core.util.event.GenericEventListener;
import org.olat.course.assessment.AssessmentMode;
import org.olat.course.assessment.AssessmentMode.Status;
import org.olat.course.assessment.AssessmentModeCoordinationService;
import org.olat.course.assessment.AssessmentModeManager;
import org.olat.course.assessment.AssessmentModeNotificationEvent;
import org.olat.course.assessment.model.TransientAssessmentMode;
import org.olat.course.assessment.ui.mode.AssessmentModeListModel.Cols;
import org.olat.repository.RepositoryEntry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 12.12.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessmentModeListController extends FormBasicController implements GenericEventListener {

	private FormLink addLink;
	private FormLink deleteLink;
	private FlexiTableElement tableEl;
	private AssessmentModeListModel model;
	private final TooledStackedPanel toolbarPanel;

	private DialogBoxController startDialogBox, stopDialogBox, deleteDialogBox;
	private AssessmentModeEditController editCtrl;
	
	private final RepositoryEntry entry;
	private final AssessmentModeSecurityCallback secCallback;
	
	@Autowired
	private AssessmentModeManager assessmentModeMgr;
	@Autowired
	private AssessmentModeCoordinationService assessmentModeCoordinationService;
	
	public AssessmentModeListController(UserRequest ureq, WindowControl wControl, TooledStackedPanel toolbarPanel,
			RepositoryEntry entry, AssessmentModeSecurityCallback secCallback) {
		super(ureq, wControl, "mode_list");
		this.entry = entry;
		this.secCallback = secCallback;
		this.toolbarPanel = toolbarPanel;
		toolbarPanel.addListener(this);
		
		initForm(ureq);
		loadModel();
		
		CoordinatorManager.getInstance().getCoordinator().getEventBus()
			.registerFor(this, getIdentity(), AssessmentModeNotificationEvent.ASSESSMENT_MODE_NOTIFICATION);
	}
	
	@Override
	protected void doDispose() {
		toolbarPanel.removeListener(this);
		//deregister for assessment mode
		CoordinatorManager.getInstance().getCoordinator().getEventBus()
			.deregisterFor(this, AssessmentModeNotificationEvent.ASSESSMENT_MODE_NOTIFICATION);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		if(secCallback.canEditAssessmentMode()) {
			addLink = uifactory.addFormLink("add", "add", "add.mode", null, formLayout, Link.BUTTON);
			addLink.setElementCssClass("o_sel_assessment_mode_add");
			addLink.setIconLeftCSS("o_icon o_icon_add");
			
			deleteLink = uifactory.addFormLink("delete", "delete", "delete.mode", null, formLayout, Link.BUTTON);
			deleteLink.setIconLeftCSS("o_icon o_icon_delete");
		}
		
		//add the table
		FlexiTableColumnModel columnsModel = FlexiTableDataModelFactory.createFlexiTableColumnModel();
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(Cols.status.i18nKey(), Cols.status.ordinal(),
				true, Cols.status.name(), new ModeStatusCellRenderer()));
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(Cols.name.i18nKey(), Cols.name.ordinal(), true, Cols.name.name()));
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(Cols.begin.i18nKey(), Cols.begin.ordinal(), true, Cols.begin.name()));
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(Cols.end.i18nKey(), Cols.end.ordinal(), true, Cols.end.name()));
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(Cols.leadTime.i18nKey(), Cols.leadTime.ordinal(),
				true, Cols.leadTime.name(), new TimeCellRenderer(getTranslator())));
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(Cols.followupTime.i18nKey(), Cols.followupTime.ordinal(),
				true, Cols.followupTime.name(), new TimeCellRenderer(getTranslator())));
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(Cols.target.i18nKey(), Cols.target.ordinal(),
				true, Cols.target.name(), new TargetAudienceCellRenderer(getTranslator())));
		
		if(secCallback.canStartStopAssessment()) {
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel("start", Cols.start.ordinal(), "start",
				new BooleanCellRenderer(new StaticFlexiCellRenderer(translate("start"), "start"), null)));
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel("stop", Cols.stop.ordinal(), "stop",
				new BooleanCellRenderer(new StaticFlexiCellRenderer(translate("stop"), "stop"), null)));
		}
		if(secCallback.canEditAssessmentMode()) {
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel("edit", translate("edit"), "edit"));
		}
		
		model = new AssessmentModeListModel(columnsModel, getTranslator(), assessmentModeCoordinationService);
		tableEl = uifactory.addTableElement(getWindowControl(), "table", model, 20, false, getTranslator(), formLayout);
		tableEl.setMultiSelect(secCallback.canEditAssessmentMode());
		tableEl.setSelectAllEnable(secCallback.canEditAssessmentMode());
	}
	
	private void loadModel() {
		List<AssessmentMode> modes = assessmentModeMgr.getAssessmentModeFor(entry);
		model.setObjects(modes);
		tableEl.reloadData();
		// don't show table and button if there is nothing
		tableEl.setVisible(!modes.isEmpty());
		if(deleteLink != null) {
			deleteLink.setVisible(!modes.isEmpty());
		}
	}

	@Override
	public void event(Event event) {
		 if (event instanceof AssessmentModeNotificationEvent) {
			 AssessmentModeNotificationEvent amne = (AssessmentModeNotificationEvent)event;
			 TransientAssessmentMode mode = amne.getAssessementMode();
			 if(mode.getRepositoryEntryKey().equals(entry.getKey())
					 && model.updateModeStatus(amne.getAssessementMode())) {
				 tableEl.getComponent().setDirty(true);
			 }
		}
	}

	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if(editCtrl == source) {
			loadModel();
			toolbarPanel.popUpToController(this);
			removeAsListenerAndDispose(editCtrl);
			editCtrl = null;
		} else if(deleteDialogBox == source) {
			if(DialogBoxUIFactory.isYesEvent(event) || DialogBoxUIFactory.isOkEvent(event)) {
				@SuppressWarnings("unchecked")
				List<AssessmentMode> rows = (List<AssessmentMode>)deleteDialogBox.getUserObject();
				doDelete(rows);
			}
		} else if(startDialogBox == source) {
			if(DialogBoxUIFactory.isYesEvent(event) || DialogBoxUIFactory.isOkEvent(event)) {
				AssessmentMode row = (AssessmentMode)startDialogBox.getUserObject();
				doStart(row);
			}
		} else if(stopDialogBox == source) {
			if(DialogBoxUIFactory.isYesEvent(event) || DialogBoxUIFactory.isOkEvent(event)) {
				AssessmentMode row = (AssessmentMode)stopDialogBox.getUserObject();
				doStop(row);
			}
		}
		super.event(ureq, source, event);
	}

	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		if(source == toolbarPanel) {
			if(event instanceof PopEvent) {
				PopEvent pe = (PopEvent)event;
				if(pe.getController() == editCtrl) {
					loadModel();
				}
			}
		} else {
			super.event(ureq, source, event);
		}
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(addLink == source) {
			doAdd(ureq);
		} else if(deleteLink == source) {
			Set<Integer> index = tableEl.getMultiSelectedIndex();
			if(index == null || index.isEmpty()) {
				showWarning("error.atleastone");
			} else {
				List<AssessmentMode> rows = new ArrayList<>(index.size());
				for(Integer i:index) {
					rows.add(model.getObject(i.intValue()));
				}
				doConfirmDelete(ureq, rows);
			}
		} else if(tableEl == source) {
			if(event instanceof SelectionEvent) {
				SelectionEvent se = (SelectionEvent)event;
				String cmd = se.getCommand();
				AssessmentMode row = model.getObject(se.getIndex());
				if("edit".equals(cmd)) {
					doEdit(ureq, row);
				} else if("start".equals(cmd)) {
					doConfirmStart(ureq, row);
				} else if("stop".equals(cmd)) {
					doConfirmStop(ureq, row);
				}
			}
		}
		super.formInnerEvent(ureq, source, event);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		//
	}

	private void doAdd(UserRequest ureq) {
		removeAsListenerAndDispose(editCtrl);
		AssessmentMode newMode = assessmentModeMgr.createAssessmentMode(entry);
		editCtrl = new AssessmentModeEditController(ureq, getWindowControl(), entry.getOlatResource(), newMode);
		listenTo(editCtrl);
		toolbarPanel.pushController(translate("new.mode"), editCtrl);
	}
	
	private void doConfirmDelete(UserRequest ureq, List<AssessmentMode> modeToDelete) {
		StringBuilder sb = new StringBuilder();
		boolean canDelete = true;
		for(AssessmentMode mode:modeToDelete) {
			if(sb.length() > 0) sb.append(", ");
			sb.append(mode.getName());
			
			Status status = mode.getStatus();
			if(status == Status.leadtime || status == Status.assessment || status == Status.followup) {
				canDelete = false;
			}
		}
		
		if(canDelete) {
			String names = StringHelper.escapeHtml(sb.toString());
			String title = translate("confirm.delete.title");
			String text = translate("confirm.delete.text", names);
			deleteDialogBox = activateYesNoDialog(ureq, title, text, deleteDialogBox);
			deleteDialogBox.setUserObject(modeToDelete);
		} else {
			showWarning("error.in.assessment");
		}
	}
	
	private void doDelete(List<AssessmentMode> modesToDelete) {
		for(AssessmentMode modeToDelete:modesToDelete) {
			assessmentModeMgr.delete(modeToDelete);
		}
		loadModel();
		tableEl.deselectAll();
	}
	
	private void doEdit(UserRequest ureq, AssessmentMode mode) {
		removeAsListenerAndDispose(editCtrl);
		editCtrl = new AssessmentModeEditController(ureq, getWindowControl(), entry.getOlatResource(), mode);
		listenTo(editCtrl);
		
		String title = translate("form.mode.title", new String[]{ mode.getName() });
		toolbarPanel.pushController(title, editCtrl);
	}
	
	private void doConfirmStart(UserRequest ureq, AssessmentMode mode) {
		String title = translate("confirm.start.title");
		String text = translate("confirm.start.text");
		startDialogBox = activateYesNoDialog(ureq, title, text, startDialogBox);
		startDialogBox.setUserObject(mode);
	}

	private void doStart(AssessmentMode mode) {
		assessmentModeCoordinationService.startAssessment(mode);
		loadModel();
	}
	
	private void doConfirmStop(UserRequest ureq, AssessmentMode mode) {
		String title = translate("confirm.stop.title");
		String text = translate("confirm.stop.text");
		stopDialogBox = activateYesNoDialog(ureq, title, text, stopDialogBox);
		stopDialogBox.setUserObject(mode);
	}
	
	private void doStop(AssessmentMode mode) {
		assessmentModeCoordinationService.stopAssessment(mode);
		loadModel();
	}
}
