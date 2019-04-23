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
package org.olat.course.nodes.dialog.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.olat.basesecurity.BaseSecurityModule;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.DownloadLink;
import org.olat.core.gui.components.form.flexible.elements.FlexiTableElement;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataModelFactory;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SelectionEvent;
import org.olat.core.gui.components.form.flexible.impl.elements.table.StaticFlexiCellRenderer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.gui.control.generic.modal.DialogBoxController;
import org.olat.core.gui.control.generic.modal.DialogBoxUIFactory;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.logging.activity.CourseLoggingAction;
import org.olat.core.logging.activity.ThreadLocalUserActivityLogger;
import org.olat.core.util.Util;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSItem;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.core.util.vfs.VFSMediaResource;
import org.olat.core.util.vfs.filters.VFSLeafFilter;
import org.olat.course.CourseFactory;
import org.olat.course.nodes.DialogCourseNode;
import org.olat.course.nodes.dialog.DialogElement;
import org.olat.course.nodes.dialog.DialogElementsManager;
import org.olat.course.nodes.dialog.ui.DialogElementsTableModel.DialogCols;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.modules.fo.ForumCallback;
import org.olat.modules.fo.manager.ForumManager;
import org.olat.repository.RepositoryEntry;
import org.olat.user.UserManager;
import org.olat.user.propertyhandlers.UserPropertyHandler;
import org.olat.util.logging.activity.LoggingResourceable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * The list of files to discuss.
 * 
 * Initial date: 3 janv. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class DialogElementListController extends FormBasicController implements Activateable2 {
	
	public static final int USER_PROPS_OFFSET = 500;
	public static final String usageIdentifyer = DialogElementsTableModel.class.getCanonicalName();
	
	private FlexiTableElement tableEl;
	private DialogElementsTableModel tableModel;
	
	private int counter = 0;
	private final boolean showForum;
	private final DialogCourseNode courseNode;
	private final ForumCallback forumCallback;
	private final UserCourseEnvironment userCourseEnv;
	private final boolean isAdministrativeUser;
	private final List<UserPropertyHandler> userPropertyHandlers;

	private DialogBoxController confirmDeletionCtr;

	
	@Autowired
	private UserManager userManager;
	@Autowired
	private ForumManager forumManager;
	@Autowired
	private BaseSecurityModule securityModule;
	@Autowired
	private DialogElementsManager dialogElementsManager;
	
	public DialogElementListController(UserRequest ureq, WindowControl wControl, UserCourseEnvironment userCourseEnv,
			DialogCourseNode courseNode, ForumCallback forumCallback, boolean showForum) {
		super(ureq, wControl, "element_list");
		setTranslator(Util.createPackageTranslator(DialogCourseNodeRunController.class, getLocale()));
		setTranslator(userManager.getPropertyHandlerTranslator(getTranslator()));

		this.showForum = showForum;
		this.courseNode = courseNode;
		this.userCourseEnv = userCourseEnv;
		this.forumCallback = forumCallback;

		isAdministrativeUser = securityModule.isUserAllowedAdminProps(ureq.getUserSession().getRoles());
		userPropertyHandlers = userManager.getUserPropertyHandlersFor(usageIdentifyer, isAdministrativeUser);
		
		initForm(ureq);
		loadModel();
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		FlexiTableColumnModel columnsModel = FlexiTableDataModelFactory.createFlexiTableColumnModel();
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DialogCols.filename));

		//list of user properties
		if(isAdministrativeUser) {
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DialogCols.authorUsername));
		}
		
		int colIndex = USER_PROPS_OFFSET;
		for (int i = 0; i < userPropertyHandlers.size(); i++) {
			UserPropertyHandler userPropertyHandler	= userPropertyHandlers.get(i);
			boolean visible = UserManager.getInstance().isMandatoryUserProperty(usageIdentifyer , userPropertyHandler);
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(visible, userPropertyHandler.i18nColumnDescriptorLabelKey(), colIndex, null, true, "userProp-" + colIndex));
			colIndex++;
		}
		
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DialogCols.date));
		
		if(showForum) {
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DialogCols.messages));
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DialogCols.newMessages));
			StaticFlexiCellRenderer forumRenderer = new StaticFlexiCellRenderer(translate("dialog.start"), "forum", null, null);
			forumRenderer.setIconRightCSS("o_icon o_icon-fw o_icon_start");
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel("table.header.forum", -1, "forum", forumRenderer));
		}
		
		if (!userCourseEnv.isCourseReadOnly() && forumCallback != null && forumCallback.mayDeleteMessageAsModerator()) {
			StaticFlexiCellRenderer deleteRenderer = new StaticFlexiCellRenderer(translate("delete"), "delete", null, "o_icon o_icon_delete_item");
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel("table.header.action", -1, "delete", deleteRenderer));
		}
				
		tableModel = new DialogElementsTableModel(columnsModel);
		tableEl = uifactory.addTableElement(getWindowControl(), "table", tableModel, 25, false, getTranslator(), formLayout);
		tableEl.setPageSize(25);
		tableEl.setAndLoadPersistedPreferences(ureq, "dialog.elements.v2");
	}
	
	protected void loadModel() {
		RepositoryEntry entry = userCourseEnv.getCourseEnvironment().getCourseGroupManager().getCourseEntry();
		List<DialogElement> elements = dialogElementsManager.getDialogElements(entry, courseNode.getIdent());
		List<DialogElementRow> rows = new ArrayList<>(elements.size());

		for (DialogElement element : elements) {
			DialogElementRow row = new DialogElementRow(element, userPropertyHandlers, getLocale());

			int msgCount = forumManager.countMessagesByForumID(element.getForum().getKey());
			int newMsg = msgCount - forumManager.countReadMessagesByUserAndForum(getIdentity(), element.getForum().getKey());
			row.setNumOfMessages(msgCount);
			row.setNumOfUnreadMessages(newMsg);
			
			VFSLeaf item = dialogElementsManager.getDialogLeaf(element);
			if(item != null) {
				DownloadLink downloadLink = uifactory.addDownloadLink("file_" + (++counter), row.getFilename(), null, item, flc);
				row.setDownloadLink(downloadLink);
			}
			rows.add(row);
		}
		tableModel.setObjects(rows);
		tableEl.reset(true, true, true);
	}
	
	protected void load(DialogElement element) {
		if(element == null) return;
		
		List<DialogElementRow> rows = tableModel.getObjects();
		for(DialogElementRow row:rows) {
			if(element.getKey().equals(row.getDialogElementKey())) {
				int msgCount = forumManager.countMessagesByForumID(element.getForum().getKey());
				int newMsg = msgCount - forumManager.countReadMessagesByUserAndForum(getIdentity(), element.getForum().getKey());
				row.setNumOfMessages(msgCount);
				row.setNumOfUnreadMessages(newMsg);
				tableEl.reset(false, false, true);
			}
		}
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		//
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if (source == confirmDeletionCtr) {
			if (DialogBoxUIFactory.isYesEvent(event) || DialogBoxUIFactory.isOkEvent(event)) {
				doDelete((DialogElementRow)confirmDeletionCtr.getUserObject());
				loadModel();
			}
		}
		super.event(ureq, source, event);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		//
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(source == tableEl) {
			if(event instanceof SelectionEvent) {
				SelectionEvent se = (SelectionEvent)event;
				DialogElementRow row = tableModel.getObject(se.getIndex());
				if("forum".equals(se.getCommand())) {
					fireEvent(ureq, new SelectRowEvent(row));
				} else if("delete".equals(se.getCommand())) {
					doConfirmDelete(ureq, row);
				}
			}
		} else if(source instanceof FormLink) {
			FormLink link = (FormLink)source;
			String cmd = link.getCmd();
			if("download".equals(cmd)) {
				doFileDelivery(ureq, (DialogElement)link.getUserObject());
			}
		}
		super.formInnerEvent(ureq, source, event);
	}
	
	private void doConfirmDelete(UserRequest ureq, DialogElementRow row) {
		String msg = translate("element.delete", row.getFilename());
		confirmDeletionCtr = activateYesNoDialog(ureq, translate("delete"), msg, confirmDeletionCtr);
		confirmDeletionCtr.setUserObject(row);
	}
	
	private void doDelete(DialogElementRow rowToDelete) {
		DialogElement elementToDelete = dialogElementsManager.getDialogElementByKey(rowToDelete.getDialogElementKey());
		// archive data to personal folder
		File exportDir = CourseFactory.getOrCreateDataExportDirectory(getIdentity(), courseNode.getShortTitle());
		courseNode.doArchiveElement(elementToDelete, exportDir, getLocale());

		dialogElementsManager.deleteDialogElement(elementToDelete);
		//do logging
		ThreadLocalUserActivityLogger.log(CourseLoggingAction.DIALOG_ELEMENT_FILE_DELETED, getClass(),
				LoggingResourceable.wrapUploadFile(elementToDelete.getFilename()));
	}
	
	/**
	 * deliver the selected file and show in a popup
	 * 
	 * @param ureq
	 * @param command
	 */
	private void doFileDelivery(UserRequest ureq, DialogElement element) {
		VFSContainer forumContainer = dialogElementsManager.getDialogContainer(element);
		List<VFSItem> items = forumContainer.getItems(new VFSLeafFilter());
		if(items.size() > 0 && items.get(0) instanceof VFSLeaf) {
			VFSLeaf vl = (VFSLeaf)items.get(0);
			ureq.getDispatchResult().setResultingMediaResource(new VFSMediaResource(vl));
			ThreadLocalUserActivityLogger.log(CourseLoggingAction.DIALOG_ELEMENT_FILE_DOWNLOADED, getClass(),
					LoggingResourceable.wrapBCFile(vl.getName()));
		} else {
			logError("No file to discuss: " + forumContainer, null);
		}
	}
}
