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
package org.olat.course.nodes.pf.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.olat.basesecurity.BaseSecurity;
import org.olat.basesecurity.BaseSecurityModule;
import org.olat.core.commons.persistence.SortKey;
import org.olat.core.commons.services.notifications.PublisherData;
import org.olat.core.commons.services.notifications.SubscriptionContext;
import org.olat.core.commons.services.notifications.ui.ContextualSubscriptionController;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FlexiTableElement;
import org.olat.core.gui.components.form.flexible.elements.FlexiTableFilter;
import org.olat.core.gui.components.form.flexible.elements.FlexiTableSortOptions;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableDataModelFactory;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableSearchEvent;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SelectionEvent;
import org.olat.core.gui.components.form.flexible.impl.elements.table.StaticFlexiCellRenderer;
import org.olat.core.gui.components.form.flexible.impl.elements.table.TextFlexiCellRenderer;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.ControllerEventListener;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.gui.media.MediaResource;
import org.olat.core.id.Identity;
import org.olat.core.id.Roles;
import org.olat.core.id.UserConstants;
import org.olat.core.util.StringHelper;
import org.olat.core.util.resource.OresHelper;
import org.olat.course.nodes.PFCourseNode;
import org.olat.course.nodes.pf.manager.FileSystemExport;
import org.olat.course.nodes.pf.manager.PFManager;
import org.olat.course.nodes.pf.manager.PFView;
import org.olat.course.nodes.pf.ui.DropBoxTableModel.DropBoxCols;
import org.olat.course.run.environment.CourseEnvironment;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.group.BusinessGroup;
import org.olat.group.BusinessGroupRef;
import org.olat.group.model.BusinessGroupRefImpl;
import org.olat.modules.curriculum.CurriculumElement;
import org.olat.modules.curriculum.CurriculumElementRef;
import org.olat.modules.curriculum.model.CurriculumElementRefImpl;
import org.olat.resource.OLATResource;
import org.olat.user.HomePageConfig;
import org.olat.user.HomePageDisplayController;
import org.olat.user.UserManager;
import org.olat.user.UserPropertiesRow;
import org.olat.user.propertyhandlers.UserPropertyHandler;
import org.springframework.beans.factory.annotation.Autowired;
/**
*
* @author Fabian Kiefer, fabian.kiefer@frentix.com, http://www.frentix.com
*
*/
public class PFCoachController extends FormBasicController implements ControllerEventListener {
	
	protected static final String USER_PROPS_ID = PFCoachController.class.getCanonicalName();

	protected static final int USER_PROPS_OFFSET = 500;
	private static final String CURRICULUM_EL_PREFIX = "curriculumelement-";
	private static final String BUSINESS_GROUP_PREFIX = "businessgroup-";
	
	private PFCourseNode pfNode;
	
	private FormLink downloadLink, uploadLink, uploadAllLink;
	private Link backLink;
	private DropBoxTableModel tableModel;
	private FlexiTableElement dropboxTable;
	
	private CloseableModalController cmc;
	private PFFileUploadController pfFileUploadCtr;
	private PFParticipantController pfParticipantController; 
	private HomePageDisplayController homePageDisplayController;
	private ContextualSubscriptionController contextualSubscriptionCtr;

	private final List<UserPropertyHandler> userPropertyHandlers;
	private final boolean isAdministrativeUser;

	private UserCourseEnvironment userCourseEnv;
	private CourseEnvironment courseEnv;

	private PFView pfView;
	@Autowired
	private PFManager pfManager; 
	@Autowired
	private UserManager userManager;
	@Autowired
	private BaseSecurity securityManager;
	@Autowired
	private BaseSecurityModule securityModule;
	
	public PFCoachController(UserRequest ureq, WindowControl wControl, PFCourseNode sfNode, 
			UserCourseEnvironment userCourseEnv, PFView pfView) {
		super(ureq, wControl, "coach");
		
		this.userCourseEnv = userCourseEnv;
		this.courseEnv = userCourseEnv.getCourseEnvironment();
		this.pfNode = sfNode;
		this.pfView = pfView;
		
		Roles roles = ureq.getUserSession().getRoles();
		isAdministrativeUser = securityModule.isUserAllowedAdminProps(roles);
		userPropertyHandlers = userManager.getUserPropertyHandlersFor(USER_PROPS_ID, isAdministrativeUser);
		setTranslator(userManager.getPropertyHandlerTranslator(getTranslator()));
		
		initForm(ureq);
		loadModel(true);
	}

	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		if (source == backLink) {
			back();
		}
		super.event(ureq, source, event);		
	}
	
	@Override 
	protected void event(UserRequest ureq, Controller source, Event event) {
		if (source == pfFileUploadCtr) {
			if (event == Event.DONE_EVENT) {
				if (pfFileUploadCtr.isUploadToAll()) {
					uploadToSelection(pfFileUploadCtr.getUpLoadFile(), pfFileUploadCtr.getUploadFileName());
					showInfo("upload.success");
					fireEvent(ureq, Event.CHANGED_EVENT);
				} else {
					pfManager.uploadFileToDropBox(pfFileUploadCtr.getUpLoadFile(),
							pfFileUploadCtr.getUploadFileName(), 4, courseEnv, pfNode, getIdentity());
				}
				cmc.deactivate();
				cleanUpCMC();
			}			
		} else if (source == cmc) {
			cleanUpCMC();
		}
		super.event(ureq, source, event);
	}

	@Override
	protected void doDispose() {
		// nothing to dispose
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if (source == uploadLink) {
			doOpenUploadController(ureq, false);
		} else if (source == uploadAllLink) {
			if (dropboxTable.getMultiSelectedIndex().size() > 0) {				
				doOpenUploadController(ureq, true);
			} else {
				showWarning("table.no.selection");
			}
		} else if (source == downloadLink) {
			if (dropboxTable.getMultiSelectedIndex().size() > 0) {
				downloadFromSelection(ureq);				
			} else {
				showWarning("table.no.selection");
			}
		} else if(source == dropboxTable) {
			if(event instanceof SelectionEvent) {
				SelectionEvent se = (SelectionEvent)event;
				DropBoxRow currentObject = tableModel.getObject(se.getIndex());
				if ("drop.box".equals(se.getCommand())){
					doSelectParticipantFolder (ureq, currentObject.getIdentity(), PFView.displayDrop);
				} else if ("return.box".equals(se.getCommand())){
					doSelectParticipantFolder (ureq, currentObject.getIdentity(), PFView.displayReturn);
				} else if ("open.box".equals(se.getCommand())){
					doSelectParticipantFolder (ureq, currentObject.getIdentity(), PFView.dropAndReturn);
				} else if ("firstName".equals(se.getCommand()) || "lastName".equals(se.getCommand())) {
					doOpenHomePage(ureq, currentObject.getIdentity());
				} 
			} else if(event instanceof FlexiTableSearchEvent) {
				loadModel(true);
			}
		}
	}
	
	private void doSelectParticipantFolder (UserRequest ureq, UserPropertiesRow row, PFView view) {
		Identity assessedIdentity = securityManager.loadIdentityByKey(row.getIdentityKey());
		removeAsListenerAndDispose(pfParticipantController);
		pfParticipantController = new PFParticipantController(ureq, getWindowControl(), pfNode,
				userCourseEnv, assessedIdentity, view, true, false);
		listenTo(pfParticipantController);
		flc.put("single", pfParticipantController.getInitialComponent());
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		if(formLayout instanceof FormLayoutContainer) {
			FormLayoutContainer layoutCont = (FormLayoutContainer)formLayout;
			OLATResource course = courseEnv.getCourseGroupManager().getCourseResource();
			String businessPath = getWindowControl().getBusinessControl().getAsString();
			SubscriptionContext subsContext = new SubscriptionContext(course, pfNode.getIdent());
			PublisherData publisherData = new PublisherData(OresHelper.calculateTypeName(PFCourseNode.class),
					String.valueOf(course.getResourceableId()), businessPath);
			contextualSubscriptionCtr = new ContextualSubscriptionController(ureq, getWindowControl(), subsContext,
					publisherData);
			listenTo(contextualSubscriptionCtr);
			layoutCont.put("contextualSubscription", contextualSubscriptionCtr.getInitialComponent());
			backLink = LinkFactory.createLinkBack(layoutCont.getFormItemComponent(), this);
		}
		
		FlexiTableColumnModel columnsModel = FlexiTableDataModelFactory.createFlexiTableColumnModel();

		int i = 0;
		FlexiTableSortOptions options = new FlexiTableSortOptions();
		for (UserPropertyHandler userPropertyHandler : userPropertyHandlers) {
			int colIndex = USER_PROPS_OFFSET + i++;
			if (userPropertyHandler == null) continue;
			
			String propName = userPropertyHandler.getName();
			boolean visible = userManager.isMandatoryUserProperty(USER_PROPS_ID , userPropertyHandler);

			FlexiColumnModel col;
			if(UserConstants.FIRSTNAME.equals(propName)
					|| UserConstants.LASTNAME.equals(propName)) {
				col = new DefaultFlexiColumnModel(userPropertyHandler.i18nColumnDescriptorLabelKey(),
						colIndex, userPropertyHandler.getName(), true, propName,
						new StaticFlexiCellRenderer(userPropertyHandler.getName(), new TextFlexiCellRenderer()));
			} else {
				col = new DefaultFlexiColumnModel(visible, userPropertyHandler.i18nColumnDescriptorLabelKey(), colIndex, true, propName);
			}
			columnsModel.addFlexiColumnModel(col);
			
			if(!options.hasDefaultOrderBy()) {
				options.setDefaultOrderBy(new SortKey(propName, true));
			} else if(UserConstants.LASTNAME.equals(propName)) {
				options.setDefaultOrderBy(new SortKey(propName, true));
			}
		}

		if (pfNode.hasParticipantBoxConfigured()){
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DropBoxCols.numberFiles,"drop.box"));
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DropBoxCols.lastUpdate));
		}
		if (pfNode.hasCoachBoxConfigured()) {
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DropBoxCols.numberFilesReturn,"return.box"));
			columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DropBoxCols.lastUpdateReturn));
		}
		StaticFlexiCellRenderer openCellRenderer = new StaticFlexiCellRenderer(translate("open.box"), "open.box");
		openCellRenderer.setIconRightCSS("o_icon_start o_icon-fw");
		columnsModel.addFlexiColumnModel(new DefaultFlexiColumnModel(DropBoxCols.openbox,
				"open.box", openCellRenderer));	
		tableModel = new DropBoxTableModel(columnsModel, getTranslator());
		
		dropboxTable = uifactory.addTableElement(getWindowControl(), "table", tableModel, getTranslator(), formLayout);
		dropboxTable.setMultiSelect(true);
		dropboxTable.setSelectAllEnable(true);
		dropboxTable.setExportEnabled(true);
		dropboxTable.setSortSettings(options);
		initFilters();
		dropboxTable.setAndLoadPersistedPreferences(ureq, "participant-folder_coach_" + pfView.name());
		dropboxTable.setEmtpyTableMessageKey("table.empty");
		
		
		FormLayoutContainer buttonGroupLayout = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		buttonGroupLayout.setElementCssClass("o_button_group");
		formLayout.add(buttonGroupLayout);
		
		downloadLink = uifactory.addFormLink("download.link", buttonGroupLayout, Link.BUTTON);
		uploadAllLink = uifactory.addFormLink("upload.link", buttonGroupLayout, Link.BUTTON);
	}
	
	private void initFilters() {
		List<FlexiTableFilter> filters = new ArrayList<>();
		
		List<BusinessGroup> coachedGroups = userCourseEnv.isAdmin()
				? courseEnv.getCourseGroupManager().getAllBusinessGroups() : userCourseEnv.getCoachedGroups(); 
		List<CurriculumElement> coachedElements = userCourseEnv.isAdmin()
				? courseEnv.getCourseGroupManager().getAllCurriculumElements() : userCourseEnv.getCoachedCurriculumElements();
		
		if(coachedGroups != null) {
			for(BusinessGroup coachedGroup:coachedGroups) {
				String groupName = StringHelper.escapeHtml(coachedGroup.getName());
				filters.add(new FlexiTableFilter(groupName, BUSINESS_GROUP_PREFIX.concat(coachedGroup.getKey().toString()), "o_icon o_icon_group"));
			}
		}
		
		if(!coachedElements.isEmpty()) {
			if(!filters.isEmpty()) {
				filters.add(FlexiTableFilter.SPACER);
			}

			for(CurriculumElement coachedElement: coachedElements) {
				String groupName = StringHelper.escapeHtml(coachedElement.getDisplayName());
				filters.add(new FlexiTableFilter(groupName, CURRICULUM_EL_PREFIX.concat(coachedElement.getKey().toString()), "o_icon o_icon_curriculum_element"));
			}
		}
		
		if(!filters.isEmpty()) {
			filters.add(FlexiTableFilter.SPACER);
			filters.add(new FlexiTableFilter(translate("show.all"), "", true));
			dropboxTable.setExtendedFilterButton(translate("filter.groups"), filters);
		}
	}
	
	private void loadModel(boolean full) {
		List<FlexiTableFilter> extendedFilters = dropboxTable.getSelectedExtendedFilters();
		List<DropBoxRow> rows;
		if(extendedFilters == null || extendedFilters.isEmpty() || extendedFilters.get(0).isShowAll()) {	
			rows = pfManager.getParticipants(getIdentity(), pfNode, userPropertyHandlers, getLocale(), courseEnv, userCourseEnv.isAdmin());
		} else {
			List<BusinessGroupRef> businessGroups = new ArrayList<>(extendedFilters.size());
			List<CurriculumElementRef> curriculumElements = new ArrayList<>(extendedFilters.size());
			for(FlexiTableFilter extendedFilter:extendedFilters) {
				String filter = extendedFilter.getFilter();
				if(filter.startsWith(BUSINESS_GROUP_PREFIX)) {
					String key = filter.substring(BUSINESS_GROUP_PREFIX.length(), filter.length());
					businessGroups.add(new BusinessGroupRefImpl(Long.valueOf(key)));
				} else if(filter.startsWith(CURRICULUM_EL_PREFIX)) {
					String key = filter.substring(CURRICULUM_EL_PREFIX.length(), filter.length());
					curriculumElements.add(new CurriculumElementRefImpl(Long.valueOf(key)));
				}
			}
			rows = pfManager.getParticipants(businessGroups, curriculumElements, pfNode, userPropertyHandlers, getLocale(), courseEnv);
		}
		
		tableModel.setObjects(rows);
		dropboxTable.reset(full, full, true);
		flc.contextPut("hasParticipants", tableModel.getRowCount() > 0);
	}
	
	private void uploadToSelection (File uploadFile, String fileName) {
		List<Long> identitykeys = new ArrayList<>();
		for (int i : dropboxTable.getMultiSelectedIndex()) {			
			identitykeys.add(tableModel.getObject(i).getIdentity().getIdentityKey());
		}
		List<Identity> identities = securityManager.loadIdentityByKeys(identitykeys);

		pfManager.uploadFileToAllReturnBoxes(uploadFile, fileName, courseEnv, pfNode, identities);
	}
	
	private void downloadFromSelection (UserRequest ureq) {
		List<Long> identitykeys = new ArrayList<>();
		for (Integer i : dropboxTable.getMultiSelectedIndex()) {			
			identitykeys.add(tableModel.getObject(i).getIdentity().getIdentityKey());
		}
		List<Identity> identities = securityManager.loadIdentityByKeys(identitykeys);
		MediaResource resource = new FileSystemExport (identities, pfNode, courseEnv, getLocale());
		ureq.getDispatchResult().setResultingMediaResource(resource);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		//
	}
	
	private void doOpenUploadController (UserRequest ureq, boolean uploadToAll) {
		pfFileUploadCtr = new PFFileUploadController(ureq, getWindowControl(), uploadToAll);
		listenTo(pfFileUploadCtr);
		
		cmc = new CloseableModalController(getWindowControl(), translate("close"), 
				pfFileUploadCtr.getInitialComponent(), true, translate("upload.link"));
		listenTo(cmc);
		cmc.activate();
	}
	
	private void doOpenHomePage (UserRequest ureq, UserPropertiesRow row) {		
		Identity assessedIdentity = securityManager.loadIdentityByKey(row.getIdentityKey());
		homePageDisplayController = new HomePageDisplayController(ureq, getWindowControl(), assessedIdentity, new HomePageConfig());
		listenTo(homePageDisplayController);
		
		cmc = new CloseableModalController(getWindowControl(), translate("close"), 
				homePageDisplayController.getInitialComponent(), true, translate("upload.link"));
		listenTo(cmc);
		cmc.activate();
	}
	
	private void cleanUpCMC(){
		removeAsListenerAndDispose(cmc);
		removeAsListenerAndDispose(pfFileUploadCtr);
		cmc = null;
		pfFileUploadCtr = null;
	}
	
	private void back() {
		if(pfParticipantController != null) {
			flc.remove(pfParticipantController.getInitialComponent());
			removeAsListenerAndDispose(pfParticipantController);
			pfParticipantController = null;
			loadModel(false);
		}
	}
}
