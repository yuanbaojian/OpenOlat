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
package org.olat.course.config.ui;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.elements.SelectionElement;
import org.olat.core.gui.components.form.flexible.elements.StaticTextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.gui.control.generic.modal.DialogBoxController;
import org.olat.core.gui.control.generic.modal.DialogBoxUIFactory;
import org.olat.core.id.OLATResourceable;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.logging.activity.ILoggingAction;
import org.olat.core.logging.activity.LearningResourceLoggingAction;
import org.olat.core.logging.activity.StringResourceableType;
import org.olat.core.logging.activity.ThreadLocalUserActivityLogger;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.olat.core.util.coordinate.LockResult;
import org.olat.core.util.nodes.INode;
import org.olat.core.util.tree.TreeVisitor;
import org.olat.core.util.tree.Visitor;
import org.olat.course.CourseFactory;
import org.olat.course.ICourse;
import org.olat.course.config.CourseConfig;
import org.olat.course.config.CourseConfigEvent;
import org.olat.course.config.CourseConfigEvent.CourseConfigType;
import org.olat.course.nodes.BCCourseNode;
import org.olat.course.nodes.CourseNode;
import org.olat.course.run.RunMainController;
import org.olat.course.tree.CourseEditorTreeNode;
import org.olat.fileresource.types.GlossaryResource;
import org.olat.fileresource.types.SharedFolderFileResource;
import org.olat.modules.glossary.GlossaryManager;
import org.olat.modules.sharedfolder.SharedFolderManager;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryEntryManagedFlag;
import org.olat.repository.RepositoryManager;
import org.olat.repository.controllers.ReferencableEntriesSearchController;
import org.olat.repository.model.RepositoryEntrySecurity;
import org.olat.resource.references.Reference;
import org.olat.resource.references.ReferenceManager;
import org.olat.user.UserManager;
import org.olat.util.logging.activity.LoggingResourceable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 14.08.2014<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class CourseOptionsController extends FormBasicController {
	private static final OLog log = Tracing.createLoggerFor(CourseOptionsController.class);
	private static final String COMMAND_REMOVE = "command.glossary.remove";
	private static final String COMMAND_ADD = "command.glossary.add";
	
	private static final String[] onKeys = new String[] {"xx"};

	private FormLink addGlossaryCommand;
	private FormLink removeGlossaryCommand;
	private StaticTextElement glossaryNameEl;
	private FormLink saveButton;
	private FormLayoutContainer saveCont;
	private FormLayoutContainer glossaryCont;
	private FormLayoutContainer sharedFolderCont;
	
	private FormLink addFolderCommand;
	private FormLink removeFolderCommand;
	private StaticTextElement folderNameEl;
	private MultipleSelectionElement folderReadOnlyEl;

	private LockResult lockEntry;
	private final boolean editable;
	private CourseConfig courseConfig;
	private final RepositoryEntry entry;

	private CloseableModalController cmc;
	private DialogBoxController folderRefAddWarnBox;
	private DialogBoxController folderRefRemoveWarnBox;
	private ReferencableEntriesSearchController folderSearchCtr;
	private ReferencableEntriesSearchController glossarySearchCtr;

	@Autowired
	private UserManager userManager;
	@Autowired
	private ReferenceManager referenceManager;
	@Autowired
	private RepositoryManager repositoryService;
	

	/**
	 * @param name
	 * @param chatEnabled
	 */
	public CourseOptionsController(UserRequest ureq, WindowControl wControl,
			RepositoryEntry entry, CourseConfig courseConfig, boolean canEdit) {
		super(ureq, wControl, "course_options");
		setTranslator(Util.createPackageTranslator(RunMainController.class, getLocale(), getTranslator()));
		this.courseConfig = courseConfig;
		this.entry = entry;
		
		lockEntry = CoordinatorManager.getInstance().getCoordinator().getLocker()
				.acquireLock(entry.getOlatResource(), getIdentity(), CourseFactory.COURSE_EDITOR_LOCK);
		editable = (lockEntry != null && lockEntry.isSuccess()) && canEdit;

		initForm(ureq);
		updateToolbar();

		//glossary setup
		boolean managedGlossary = RepositoryEntryManagedFlag.isManaged(entry, RepositoryEntryManagedFlag.glossary);
		if (courseConfig.hasGlossary()) {
			RepositoryEntry repoEntry = repositoryService.lookupRepositoryEntryBySoftkey(courseConfig.getGlossarySoftKey(), false);
			if (repoEntry == null) {
				// Something is wrong here, maybe the glossary has been deleted. Try to
				// remove glossary from configuration
				doRemoveGlossary();
				log.warn("Course with ID::" + entry.getOlatResource().getResourceableId() + " had a config for a glossary softkey::"
						+ courseConfig.getGlossarySoftKey() + " but no such glossary was found");				
			} else if(editable) {
				glossaryNameEl.setValue(StringHelper.escapeHtml(repoEntry.getDisplayname()));
				glossaryNameEl.setUserObject(repoEntry);
				removeGlossaryCommand.setVisible(editable && !managedGlossary);
			}
		} else if(editable && !managedGlossary) {
			removeGlossaryCommand.setVisible(false);
			addGlossaryCommand.setVisible(editable);
		}
		
		//shared folder
		boolean managedFolder = RepositoryEntryManagedFlag.isManaged(entry, RepositoryEntryManagedFlag.resourcefolder);
		if (courseConfig.hasCustomSharedFolder()) {
			RepositoryEntry repoEntry = repositoryService.lookupRepositoryEntryBySoftkey(courseConfig.getSharedFolderSoftkey(), false);
			if (repoEntry == null) {
				// Something is wrong here, maybe the glossary has been deleted.
				// Try to remove shared folder from configuration
				doRemoveSharedFolder();			
			} else if(editable) {
				folderNameEl.setValue(StringHelper.escapeHtml(repoEntry.getDisplayname()));
				folderNameEl.setUserObject(repoEntry);
				removeFolderCommand.setVisible(editable && !managedFolder);
				
				RepositoryEntrySecurity reSecurity = repositoryService.isAllowed(ureq, repoEntry);
				folderReadOnlyEl.setVisible(true);
				folderReadOnlyEl.setEnabled(editable && reSecurity.isEntryAdmin());
			}
		} else if(editable && !managedFolder) {
			removeFolderCommand.setVisible(false);
			addFolderCommand.setVisible(editable);
			folderReadOnlyEl.setVisible(false);
		}
		
		if(lockEntry != null && !lockEntry.isSuccess()) {
			String lockerName = "???";
			if(lockEntry.getOwner() != null) {
				lockerName = userManager.getUserDisplayName(lockEntry.getOwner());
			}
			showWarning("error.editoralreadylocked", new String[] { lockerName });
		}
	}
	
	@Override
	protected void doDispose() {
		if (lockEntry != null && lockEntry.isSuccess()) {
			CoordinatorManager.getInstance().getCoordinator().getLocker().releaseLock(lockEntry);
			lockEntry = null;
		}
	}
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {

		//glossary
		glossaryCont = FormLayoutContainer.createDefaultFormLayout("glossary", getTranslator());
		glossaryCont.setRootForm(mainForm);
		formLayout.add(glossaryCont);

		glossaryNameEl = uifactory.addStaticTextElement("glossaryName", "glossary.isconfigured",
				translate("glossary.no.glossary"), glossaryCont);

		boolean managedGlossary = RepositoryEntryManagedFlag.isManaged(entry, RepositoryEntryManagedFlag.glossary);
		FormLayoutContainer buttonsCont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		glossaryCont.add(buttonsCont);
		removeGlossaryCommand = uifactory.addFormLink(COMMAND_REMOVE, buttonsCont, Link.BUTTON);
		removeGlossaryCommand.setVisible(editable && !managedGlossary);
		addGlossaryCommand = uifactory.addFormLink(COMMAND_ADD, buttonsCont, Link.BUTTON);
		addGlossaryCommand.setVisible(editable && !managedGlossary);

		//shared folder
		boolean managedFolder = RepositoryEntryManagedFlag.isManaged(entry, RepositoryEntryManagedFlag.resourcefolder);
		sharedFolderCont = FormLayoutContainer.createDefaultFormLayout("sharedfolder", getTranslator());
		sharedFolderCont.setRootForm(mainForm);
		formLayout.add(sharedFolderCont);

		folderNameEl = uifactory.addStaticTextElement("folderName", "sf.resourcetitle",
				translate("sf.notconfigured"), sharedFolderCont);
		folderNameEl.setHelpText(translate("sf.resourcetitle.helptext"));
		folderNameEl.setHelpUrlForManualPage("Course Settings#_detail_ressourcen");
		
		String[] readOnlyValues = new String[]{ translate("sf.resource.readonly") };
		folderReadOnlyEl = uifactory.addCheckboxesHorizontal("sf.resource.readonly", sharedFolderCont, onKeys, readOnlyValues);
		folderReadOnlyEl.addActionListener(FormEvent.ONCHANGE);
		folderReadOnlyEl.setLabel(null, null);
		folderReadOnlyEl.setEnabled(false);
		if(courseConfig.isSharedFolderReadOnlyMount()) {
			folderReadOnlyEl.select(onKeys[0], true);
		}
		
		FormLayoutContainer buttons2Cont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		sharedFolderCont.add(buttons2Cont);
		
		removeFolderCommand = uifactory.addFormLink("sf.unselectsfresource", buttons2Cont, Link.BUTTON);
		removeFolderCommand.setVisible(editable && !managedFolder);
		addFolderCommand = uifactory.addFormLink("sf.changesfresource", buttons2Cont, Link.BUTTON);
		addFolderCommand.setVisible(editable && !managedFolder);

		if(editable) {
			saveCont = FormLayoutContainer.createDefaultFormLayout("buttons", getTranslator());
			saveCont.setRootForm(mainForm);
			formLayout.add(saveCont);
			saveButton = uifactory.addFormLink("save", saveCont, Link.BUTTON);
			saveButton.setElementCssClass("o_sel_settings_save");
			saveButton.setPrimary(true);
		}
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if (source == glossarySearchCtr) {
			cmc.deactivate();
			if (event == ReferencableEntriesSearchController.EVENT_REPOSITORY_ENTRY_SELECTED) {
				RepositoryEntry repoEntry = glossarySearchCtr.getSelectedEntry();
				doSelectGlossary(repoEntry);
				setSaveButtonDirty();
			}
			cleanUp();
		} else if (source == folderSearchCtr) {
			cmc.deactivate();
			if (event == ReferencableEntriesSearchController.EVENT_REPOSITORY_ENTRY_SELECTED) {
				RepositoryEntry repoEntry = folderSearchCtr.getSelectedEntry();
				doSelectSharedFolder(ureq, repoEntry);
				setSaveButtonDirty();
			}
			cleanUp();
		} else if(cmc == source) {
			cleanUp();
		} else if(source == folderRefRemoveWarnBox) {
			if (DialogBoxUIFactory.isYesEvent(event)) {
				doRemoveSharedFolder();
				setSaveButtonDirty();
			}
		} else if(source == folderRefAddWarnBox) {
			if (DialogBoxUIFactory.isYesEvent(event)) {
				folderSearchCtr = new ReferencableEntriesSearchController(getWindowControl(), ureq, SharedFolderFileResource.TYPE_NAME, translate("select"));
				listenTo(folderSearchCtr);
				cmc = new CloseableModalController(getWindowControl(), translate("close"), folderSearchCtr.getInitialComponent());
				listenTo(cmc);
				cmc.activate();
			}
		}
	}
	
	private void cleanUp() {
		removeAsListenerAndDispose(glossarySearchCtr);
		removeAsListenerAndDispose(folderSearchCtr);
		removeAsListenerAndDispose(cmc);
		glossarySearchCtr = null;
		folderSearchCtr = null;
		cmc = null;
	}
	
	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if (source == addGlossaryCommand) {
			glossarySearchCtr = new ReferencableEntriesSearchController(getWindowControl(), ureq, GlossaryResource.TYPE_NAME, translate("select"));			
			listenTo(glossarySearchCtr);
			cmc = new CloseableModalController(getWindowControl(), translate("close"), glossarySearchCtr.getInitialComponent());
			listenTo(cmc);
			cmc.activate();
		} else if (source == removeGlossaryCommand) {
			doRemoveGlossary();
			setSaveButtonDirty();
		} else if (source == addFolderCommand) {
			if(checkForFolderNodesAdd(ureq)  ){
				folderSearchCtr = new ReferencableEntriesSearchController(getWindowControl(), ureq, SharedFolderFileResource.TYPE_NAME, translate("select"));
				listenTo(folderSearchCtr);
				cmc = new CloseableModalController(getWindowControl(), translate("close"), folderSearchCtr.getInitialComponent());
				listenTo(cmc);
				cmc.activate();
			}
		} else if (source == removeFolderCommand) {
			if(checkForFolderNodesRemove(ureq)){
				doRemoveSharedFolder();
				setSaveButtonDirty();
			}
		} else if (source instanceof SelectionElement || source == folderReadOnlyEl) {
			setSaveButtonDirty();
		}  else if(saveButton == source) {
			doSave(ureq);
		}
	}
	
	private void updateToolbar() {
		//
	}

	@Override
	protected void formOK(UserRequest ureq) {
		doSave(ureq);
	}

	private boolean checkForFolderNodesAdd(UserRequest ureq) {
		OLATResourceable courseOres = entry.getOlatResource();
		ICourse course = CourseFactory.loadCourse(courseOres.getResourceableId());
		CourseNode rootNode = course.getCourseEnvironment().getRunStructure().getRootNode();
		if(checkFolderNodes(rootNode, course)&& folderNameEl.getUserObject() != null){
			folderRefAddWarnBox = activateYesNoDialog(ureq, translate("warning.folderRef.title"),	"<div class=\"o_error\">"+translate("warning.folderRefAdd")+"</div>", folderRefAddWarnBox);
			folderRefAddWarnBox.setCssClass("o_icon_warn");
			return false;
		}
		return true;
	}

	private boolean checkForFolderNodesRemove(UserRequest ureq) {
		OLATResourceable courseOres = entry.getOlatResource();
		ICourse course = CourseFactory.loadCourse(courseOres.getResourceableId());
		CourseNode rootNode = course.getCourseEnvironment().getRunStructure().getRootNode();

		if(checkFolderNodes(rootNode, course)) {
			folderRefRemoveWarnBox = activateYesNoDialog(ureq, translate("warning.folderRef.title"),	"<div class=\"o_error\">"+translate("warning.folderRef")+"</div>", folderRefRemoveWarnBox);
			return false;
		}
		return true;
	}

	private boolean checkFolderNodes(INode rootNode, ICourse course){
		AtomicBoolean hasFolderNode = new AtomicBoolean(false);
		Visitor visitor = node -> {
			CourseEditorTreeNode courseNode = (CourseEditorTreeNode) course.getEditorTreeModel().getNodeById(node.getIdent());
			if(!courseNode.isDeleted() && courseNode.getCourseNode() instanceof BCCourseNode){
				BCCourseNode bcNode = (BCCourseNode) courseNode.getCourseNode();
				if (bcNode.isSharedFolder()) {
					hasFolderNode.getAndSet(true);
				}
			}
		};

		TreeVisitor v = new TreeVisitor(visitor, rootNode, false);
		v.visitAll();
		return hasFolderNode.get();
	}

	private void setSaveButtonDirty() {
		if(saveButton != null) {
			saveButton.setCustomEnabledLinkCSS("btn btn-primary o_button_dirty");
		}
	}
	
	private void doSave(UserRequest ureq) {
		doChangeConfig(ureq);
		saveButton.setCustomEnabledLinkCSS("btn btn-primary");
	}
	
	private void doChangeConfig(UserRequest ureq) {
		OLATResourceable courseOres = entry.getOlatResource();
		ICourse course = CourseFactory.openCourseEditSession(courseOres.getResourceableId());
		courseConfig = course.getCourseEnvironment().getCourseConfig();
		

		String currentGlossarySoftKey = courseConfig.getGlossarySoftKey();
		RepositoryEntry glossary = (RepositoryEntry)glossaryNameEl.getUserObject();
		String newGlossarySoftKey = (glossary == null) ? null : glossary.getSoftkey();
		boolean updateGlossary = (currentGlossarySoftKey == null && newGlossarySoftKey != null)
			|| (currentGlossarySoftKey != null && newGlossarySoftKey == null)
			|| (newGlossarySoftKey != null && !newGlossarySoftKey.equals(currentGlossarySoftKey));

		courseConfig.setGlossarySoftKey(newGlossarySoftKey);
		
		
		String currentFolderSoftKey = courseConfig.getSharedFolderSoftkey();
		RepositoryEntry folder = (RepositoryEntry)folderNameEl.getUserObject();
		String newFolderSoftKey = folder == null ? null : folder.getSoftkey();
		boolean updateFolder = (currentFolderSoftKey == null && newFolderSoftKey != null)
				|| (currentFolderSoftKey != null && newFolderSoftKey == null)
				|| (currentFolderSoftKey != null && !currentFolderSoftKey.equals(newFolderSoftKey));

		courseConfig.setSharedFolderSoftkey(newFolderSoftKey);
		if(folderReadOnlyEl.isEnabled()) {
			courseConfig.setSharedFolderReadOnlyMount(folderReadOnlyEl.isAtLeastSelected(1));
		} else {
			courseConfig.setSharedFolderReadOnlyMount(true);
		}

		CourseFactory.setCourseConfig(course.getResourceableId(), courseConfig);
		CourseFactory.closeCourseEditSession(course.getResourceableId(), true);
		
		if(updateGlossary) {
			ILoggingAction loggingAction = (newGlossarySoftKey == null) ?
					LearningResourceLoggingAction.REPOSITORY_ENTRY_PROPERTIES_GLOSSARY_DISABLED :
					LearningResourceLoggingAction.REPOSITORY_ENTRY_PROPERTIES_GLOSSARY_ENABLED;
			
			LoggingResourceable lri = null;
			if(newGlossarySoftKey != null) {
				lri = LoggingResourceable.wrapNonOlatResource(StringResourceableType.glossarySoftKey, newGlossarySoftKey, newGlossarySoftKey);
			} else if (currentGlossarySoftKey != null) {
				lri = LoggingResourceable.wrapNonOlatResource(StringResourceableType.glossarySoftKey, currentGlossarySoftKey, currentGlossarySoftKey);
			}
			if (lri != null) {
				ThreadLocalUserActivityLogger.log(loggingAction, getClass(), lri);
			}

			// remove references
			List<Reference> repoRefs = referenceManager.getReferences(course);
			for (Reference ref:repoRefs) {
				if (ref.getUserdata().equals(GlossaryManager.GLOSSARY_REPO_REF_IDENTIFYER)) {
					referenceManager.delete(ref);
				}
			}
			// update references
			if(glossary != null) {
				referenceManager.addReference(course, glossary.getOlatResource(), GlossaryManager.GLOSSARY_REPO_REF_IDENTIFYER); 
			}

	        CoordinatorManager.getInstance().getCoordinator().getEventBus()
	        	.fireEventToListenersOf(new CourseConfigEvent(CourseConfigType.glossary, course.getResourceableId()), course);
		}
		
		if(updateFolder) {
			List<Reference> repoRefs = referenceManager.getReferences(course);
			for (Reference ref:repoRefs) {
				if (ref.getUserdata().equals(SharedFolderManager.SHAREDFOLDERREF)) {
					referenceManager.delete(ref);
				}
			}

			if(folder != null) {
				referenceManager.addReference(course, folder.getOlatResource(), SharedFolderManager.SHAREDFOLDERREF);
				ThreadLocalUserActivityLogger.log(LearningResourceLoggingAction.REPOSITORY_ENTRY_PROPERTIES_SHARED_FOLDER_ADDED,
						getClass(), LoggingResourceable.wrapBCFile(folder.getDisplayname()));
			} else {
				ThreadLocalUserActivityLogger.log(LearningResourceLoggingAction.REPOSITORY_ENTRY_PROPERTIES_SHARED_FOLDER_REMOVED,
						getClass(), LoggingResourceable.wrapBCFile(""));
			}
		}
		
		fireEvent(ureq, Event.CHANGED_EVENT);
	}

	/**
	 * Updates config with selected glossary
	 * 
	 * @param repoEntry
	 * @param ureq
	 */
	private void doSelectGlossary(RepositoryEntry repoEntry) {
		glossaryNameEl.setValue(StringHelper.escapeHtml(repoEntry.getDisplayname()));
		glossaryNameEl.setUserObject(repoEntry);
		removeGlossaryCommand.setVisible(true);
	}

	/**
	 * Removes the current glossary from the configuration
	 * 
	 * @param ureq
	 */
	private void doRemoveGlossary() {			
		glossaryNameEl.setValue(translate("glossary.no.glossary"));
		glossaryNameEl.setUserObject(null);
		removeGlossaryCommand.setVisible(false);
	}
	
	private void doSelectSharedFolder(UserRequest ureq, RepositoryEntry repoEntry) {
		folderNameEl.setValue(StringHelper.escapeHtml(repoEntry.getDisplayname()));
		folderNameEl.setUserObject(repoEntry);
		removeFolderCommand.setVisible(true);
		
		RepositoryEntrySecurity reSecurity = repositoryService.isAllowed(ureq, repoEntry);
		folderReadOnlyEl.setVisible(true);
		folderReadOnlyEl.setEnabled(reSecurity.isEntryAdmin());
		folderReadOnlyEl.select(onKeys[0], true);
		sharedFolderCont.setDirty(true);
	}
	
	private void doRemoveSharedFolder() {			
		folderNameEl.setValue(translate("sf.notconfigured"));
		folderNameEl.setUserObject(null);
		removeFolderCommand.setVisible(false);
		folderReadOnlyEl.setVisible(false);
	}

}