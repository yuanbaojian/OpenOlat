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

package org.olat.collaboration;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.olat.admin.quota.QuotaConstants;
import org.olat.basesecurity.GroupRoles;
import org.olat.commons.calendar.CalendarManager;
import org.olat.commons.calendar.manager.ImportToCalendarManager;
import org.olat.commons.calendar.ui.CalendarController;
import org.olat.commons.calendar.ui.WeeklyCalendarController;
import org.olat.commons.calendar.ui.components.KalendarRenderWrapper;
import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.modules.bc.FolderRunController;
import org.olat.core.commons.services.notifications.SubscriptionContext;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.messages.MessageUIFactory;
import org.olat.core.gui.control.generic.title.TitleInfo;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.id.Roles;
import org.olat.core.id.context.BusinessControlFactory;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.logging.AssertException;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.logging.activity.ThreadLocalUserActivityLogger;
import org.olat.core.util.FileUtils;
import org.olat.core.util.Util;
import org.olat.core.util.ZipUtil;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.olat.core.util.coordinate.SyncerCallback;
import org.olat.core.util.coordinate.SyncerExecutor;
import org.olat.core.util.i18n.I18nModule;
import org.olat.core.util.mail.ContactMessage;
import org.olat.core.util.vfs.LocalFolderImpl;
import org.olat.core.util.vfs.NamedContainerImpl;
import org.olat.core.util.vfs.Quota;
import org.olat.core.util.vfs.QuotaManager;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.core.util.vfs.VFSManager;
import org.olat.core.util.vfs.callbacks.VFSSecurityCallback;
import org.olat.course.CorruptedCourseException;
import org.olat.course.CourseFactory;
import org.olat.course.CourseModule;
import org.olat.course.ICourse;
import org.olat.course.run.calendar.CourseLinkProviderController;
import org.olat.group.BusinessGroup;
import org.olat.group.BusinessGroupService;
import org.olat.group.ui.run.InfoGroupRunController;
import org.olat.instantMessaging.InstantMessagingModule;
import org.olat.instantMessaging.ui.ChatToolController;
import org.olat.modules.co.ContactFormController;
import org.olat.modules.fo.Forum;
import org.olat.modules.fo.ForumCallback;
import org.olat.modules.fo.ForumUIFactory;
import org.olat.modules.fo.archiver.ForumArchiveManager;
import org.olat.modules.fo.archiver.formatters.ForumFormatter;
import org.olat.modules.fo.archiver.formatters.ForumRTFFormatter;
import org.olat.modules.fo.manager.ForumManager;
import org.olat.modules.openmeetings.OpenMeetingsModule;
import org.olat.modules.openmeetings.manager.OpenMeetingsException;
import org.olat.modules.openmeetings.manager.OpenMeetingsManager;
import org.olat.modules.openmeetings.model.OpenMeetingsRoom;
import org.olat.modules.openmeetings.model.RoomType;
import org.olat.modules.openmeetings.ui.OpenMeetingsRunController;
import org.olat.modules.portfolio.Binder;
import org.olat.modules.portfolio.BinderConfiguration;
import org.olat.modules.portfolio.BinderSecurityCallback;
import org.olat.modules.portfolio.BinderSecurityCallbackFactory;
import org.olat.modules.portfolio.PortfolioLoggingAction;
import org.olat.modules.portfolio.PortfolioService;
import org.olat.modules.portfolio.PortfolioV2Module;
import org.olat.modules.portfolio.manager.BinderUserInformationsDAO;
import org.olat.modules.portfolio.ui.BinderController;
import org.olat.modules.wiki.WikiManager;
import org.olat.modules.wiki.WikiSecurityCallback;
import org.olat.modules.wiki.WikiSecurityCallbackImpl;
import org.olat.modules.wiki.WikiToZipUtils;
import org.olat.portfolio.EPSecurityCallback;
import org.olat.portfolio.EPSecurityCallbackImpl;
import org.olat.portfolio.EPUIFactory;
import org.olat.portfolio.manager.EPFrontendManager;
import org.olat.portfolio.model.structel.PortfolioStructureMap;
import org.olat.portfolio.ui.structel.EPCreateMapController;
import org.olat.properties.NarrowedPropertyManager;
import org.olat.properties.Property;
import org.olat.properties.PropertyManager;
import org.olat.repository.RepositoryEntry;
import org.olat.util.logging.activity.LoggingResourceable;

/**
 * Description:<BR>
 * The singleton used for retrieving a collaboration tools suite associated with
 * the supplied OLATResourceable.
 * <P>
 * Description: <br>
 * The CollaborationTools represents a suite of collaborative tools addeable to
 * any OLATResourceable. To get an instance of this suite, one has to use the
 * collaboration tools factory.
 * <p>
 * This collaboration tools class exposes the possibility to retrieve the
 * appropriate controllers for the desired tools. And also provides the means to
 * manage the configuration of the provided tools. Moreover it is already
 * shipped with a controller which can be used to display an administrative view
 * for enabling/disabling such tools for the supplied OLATResourceable.
 * <p>
 * All the future collaborative tools will be found here.
 * 
 * @see org.olat.collaboration.CollaborationToolsFactory
 * @author Felix Jost
 * @author guido
 */
public class CollaborationTools implements Serializable {

	private static final long serialVersionUID = -155629068939748789L;
	boolean dirty = false;
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	public static final String KEY_FORUM = "forumKey";
	public static final String KEY_PORTFOLIO = "portfolioMapKey";
	public static final String KEY_OPENMEETINGS = "openMeetingsKey";

	/**
	 * <code>PROP_CAT_BG_COLLABTOOLS</code> identifies properties concerning
	 * Collaboration Tools
	 */
	public static final String PROP_CAT_BG_COLLABTOOLS = "collabtools";
	/**
	 * constant used to identify the calendar for a BuddyGroup
	 */
	public static final String TOOL_CALENDAR = "hasCalendar";
	/**
	 * constant used to identify the forum for a BuddyGroup
	 */
	public static final String TOOL_FORUM = "hasForum";
	/**
	 * constant used to identify the folder for a BuddyGroup
	 */
	public static final String TOOL_FOLDER = "hasFolder";
	/**
	 * constant used to identify the chat for a BuddyGroup
	 */
	public static final String TOOL_CHAT = "hasChat";
	/**
	 * constant used to identify the contact form for a BuddyGroup
	 */
	public static final String TOOL_CONTACT = "hasContactForm";
	/**
	 * constant used to identify the contact form for a BuddyGroup
	 */
	public static final String TOOL_NEWS = "hasNews";
	/**
	 * constant used to identify the wiki for a BuddyGroup
	 */
	public static final String TOOL_WIKI = "hasWiki";
	
	/**
	 * constant used to identify the portfolio for a BuddyGroup
	 */
	public static final String TOOL_PORTFOLIO = "hasPortfolio";
	
	/**
	 * constant used to identify the open meetings for a group
	 */
	public static final String TOOL_OPENMEETINGS = "hasOpenMeetings";
	
	/**
	 * Only owners have write access to the calendar.
	 */
	public static final int CALENDAR_ACCESS_OWNERS = 0;
	/**
	 * Owners and members have write access to the calendar.
	 */
	public static final int CALENDAR_ACCESS_ALL = 1;
	/**
	 * Only owners have write access to the folder.
	 */
	public static final int FOLDER_ACCESS_OWNERS = 0;
	/**
	 * Owners and members have write access to the folder.
	 */
	public static final int FOLDER_ACCESS_ALL = 1;
	
	
	/**
	 * cache for Boolean Objects representing the State
	 */
	private static final String KEY_NEWS = "news";
	private static final String KEY_NEWS_ACCESS = "newsAccess";
	public static final String KEY_CALENDAR_ACCESS = "cal";
	public static final String KEY_FOLDER_ACCESS = "folder";

	//o_clusterOK by guido
	private Hashtable<String, Boolean> cacheToolStates;
	private final BusinessGroup ores;
	
	private static OLog log = Tracing.createLoggerFor(CollaborationTools.class);
	private transient CoordinatorManager coordinatorManager;

	/**
	 * package local constructor only
	 * 
	 * @param ores
	 */
	CollaborationTools(CoordinatorManager coordinatorManager, BusinessGroup ores) {
		this.coordinatorManager = coordinatorManager;
		this.ores = ores;
		this.cacheToolStates = new Hashtable<>();
	}

	/**
	 * @param ureq
	 * @return a news controller
	 */
	public Controller createNewsController(UserRequest ureq, WindowControl wControl) {
		String news = lookupNews();
		return new SimpleNewsController(ureq, wControl, news);
	}
	
	public Controller createInfoMessageController(UserRequest ureq, WindowControl wControl, boolean isAdmin) {
		String accessProperty = getNewsAccessProperty();
		boolean canAccess = "all".equals(accessProperty);
		return new InfoGroupRunController(ureq, wControl, ores, canAccess, isAdmin);
	}

	/**
	 * 
	 * @param ureq
	 * @param wControl
	 * @param isAdmin
	 * @param subsContext the subscriptioncontext if subscriptions to this forum
	 *          should be possible
	 * @return a forum controller
	 */
	public Controller createForumController(UserRequest ureq, WindowControl wControl, boolean isAdmin, boolean isGuestOnly,
			final SubscriptionContext subsContext) {
		
		final boolean isAdm = isAdmin;
		final boolean isGuest = isGuestOnly;
		Forum forum = getForum();
		
		Translator trans = Util.createPackageTranslator(this.getClass(), ureq.getLocale());
		TitleInfo titleInfo = new TitleInfo(null, trans.translate("collabtools.named.hasForum"));
		titleInfo.setSeparatorEnabled(true);
		return ForumUIFactory.getTitledForumController(ureq, wControl, forum, new ForumCallback() {
			@Override
			public boolean mayUsePseudonym() {
				return false;
			}

			@Override
			public boolean mayOpenNewThread() {
				return true;
			}

			@Override
			public boolean mayReplyMessage() {
				return true;
			}

			@Override
			public boolean mayEditOwnMessage() {
				return true;
			}

			@Override
			public boolean mayDeleteOwnMessage() {
				return true;
			}

			@Override
			public boolean mayEditMessageAsModerator() {
				return isAdm;
			}

			@Override
			public boolean mayDeleteMessageAsModerator() {
				return isAdm;
			}

			@Override
			public boolean mayArchiveForum() {
				return !isGuest;
			}

			@Override
			public boolean mayFilterForUser() {
				return isAdm;
			}

			@Override
			public SubscriptionContext getSubscriptionContext() {
				return subsContext;
			}
		}, titleInfo);
	}
	
	public Forum getForum() {
		final ForumManager fom = CoreSpringFactory.getImpl(ForumManager.class);
		final NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property forumProperty = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_FORUM);
		
		Forum forum;
		if(forumProperty != null) {
			forum = fom.loadForum(forumProperty.getLongValue());
		} else {
			forum = coordinatorManager.getCoordinator().getSyncer().doInSync(ores, new SyncerCallback<Forum>(){
				@Override
				public Forum execute() {
					Forum aforum;
					Long forumKey;
					Property forumKeyProperty = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_FORUM);
					if (forumKeyProperty == null) {
						// First call of forum, create new forum and save
						aforum = fom.addAForum();
						forumKey = aforum.getKey();
						if (log.isDebug()) {
							log.debug("created new forum in collab tools: foid::" + forumKey.longValue() + " for ores::"
									+ ores.getResourceableTypeName() + "/" + ores.getResourceableId());
						}
						forumKeyProperty = npm.createPropertyInstance(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_FORUM, null, forumKey, null, null);
						npm.saveProperty(forumKeyProperty);
					} else {
						// Forum does already exist, load forum with key from properties
						forumKey = forumKeyProperty.getLongValue();
						aforum = fom.loadForum(forumKey);
						if (aforum == null) { throw new AssertException("Unable to load forum with key " + forumKey.longValue() + " for ores "
								+ ores.getResourceableTypeName() + " with key " + ores.getResourceableId()); }
						if (log.isDebug()) {
							log.debug("loading forum in collab tools from properties: foid::" + forumKey.longValue() + " for ores::"
									+ ores.getResourceableTypeName() + "/" + ores.getResourceableId());
						}
					}
					return aforum;
				}
			});
		}
		return forum;
	}

	public String getFolderRelPath() {
		return "/cts/folders/" + ores.getResourceableTypeName() + "/" + ores.getResourceableId();
	}

	/**
	 * Creates a folder run controller with all rights enabled for everybody
	 * 
	 * @param ureq
	 * @param wControl
	 * @param subsContext
	 * @return Copnfigured FolderRunController
	 */
	public FolderRunController createFolderController(UserRequest ureq, WindowControl wControl,
			BusinessGroup businessGroup, boolean isAdmin, final SubscriptionContext subsContext) {
		// do not use a global translator since in the fututre a collaborationtools
		// may be shared among users
		Translator trans = Util.createPackageTranslator(this.getClass(), ureq.getLocale());
		VFSContainer rootContainer = getSecuredFolder(businessGroup, subsContext, ureq.getIdentity(), isAdmin);
		VFSContainer namedContainer = new NamedContainerImpl(trans.translate("folder"), rootContainer);
		return new FolderRunController(namedContainer, true, true, true, ureq, wControl);
	}
	
	/**
	 * Return the root VFS container with security callback set
	 * @return
	 */
	public VFSContainer getSecuredFolder(BusinessGroup businessGroup, SubscriptionContext subsContext,
			Identity identity, boolean isBusinessGroupAdmin) {
		if(!isToolEnabled(CollaborationTools.TOOL_FOLDER)) {
			return null;
		}

		boolean writeAccess;
		boolean isOwner = CoreSpringFactory.getImpl(BusinessGroupService.class).hasRoles(identity, businessGroup, GroupRoles.coach.name());
		if (!(isBusinessGroupAdmin || isOwner)) {
				// check if participants have read/write access
			int folderAccess = CollaborationTools.FOLDER_ACCESS_ALL;
			Long lFolderAccess = CollaborationToolsFactory.getInstance().getOrCreateCollaborationTools(businessGroup).lookupFolderAccess();
			if (lFolderAccess != null) {
				folderAccess = lFolderAccess.intValue();
			}
			writeAccess = (folderAccess == CollaborationTools.CALENDAR_ACCESS_ALL);
		} else {
			writeAccess = true;
		}

		String relPath = getFolderRelPath();
		VFSSecurityCallback secCallback = new CollabSecCallback(writeAccess, relPath, subsContext);
		VFSContainer rootContainer = VFSManager.olatRootContainer(relPath, null);
		rootContainer.setLocalSecurityCallback(secCallback);
		return rootContainer;
	}

	/**
	 * Creates a calendar controller
	 * @param ureq
	 * @param wControl
	 * @param resourceableId
	 * @return Configured WeeklyCalendarController
	 */
	public CalendarController createCalendarController(UserRequest ureq, WindowControl wControl, BusinessGroup businessGroup,
			boolean isAdmin, boolean isMember) {
		
		CollaborationManager collaborationManager = CoreSpringFactory.getImpl(CollaborationManager.class);
		KalendarRenderWrapper calRenderWrapper = collaborationManager.getCalendar(businessGroup, ureq, isAdmin);
		calRenderWrapper.setPrivateEventsVisible(isAdmin || isMember);
	
		// add linking
		List<RepositoryEntry> repoEntries = CoreSpringFactory.getImpl(BusinessGroupService.class).findRepositoryEntries(Collections.singleton(businessGroup), 0, -1);
		
		List<ICourse> courses = new ArrayList<>(repoEntries.size());
		for (RepositoryEntry repoEntry:repoEntries) {
			if (repoEntry.getOlatResource().getResourceableTypeName().equals(CourseModule.getCourseTypeName())) {
				try {
					ICourse course = CourseFactory.loadCourse(repoEntry);
					courses.add(course);
				} catch (CorruptedCourseException e) {
					log.error("Course corrupted: " + repoEntry.getKey() + " (" + repoEntry.getOlatResource().getResourceableId() + ")", e);
				}
			}
		}
		if(!courses.isEmpty()) {
			CourseLinkProviderController clp = new CourseLinkProviderController(null, courses, ureq, wControl);
			calRenderWrapper.setLinkProvider(clp);
		}

		List<KalendarRenderWrapper> calendars = new ArrayList<>();
		calendars.add(calRenderWrapper);
		
		return new WeeklyCalendarController(ureq, wControl, calendars,
				WeeklyCalendarController.CALLER_COLLAB, businessGroup, false);
	}

	/**
	 * @param ureq
	 * @param wControl
	 * @return a contact form controller
	 */
	public ContactFormController createContactFormController(UserRequest ureq, WindowControl wControl, ContactMessage cmsg) {
		return new ContactFormController(ureq, wControl, true, false, false, cmsg, null);
	}

	
	/**
	 * @param ureq
	 * @param wControl
	 * @param chatName
	 * @return Controller
	 */
	public ChatToolController createChatController(UserRequest ureq, WindowControl wControl, BusinessGroup grp, boolean isAdmin) {
		InstantMessagingModule imModule = CoreSpringFactory.getImpl(InstantMessagingModule.class);
		if (imModule.isEnabled() && imModule.isGroupEnabled()) {
			return new ChatToolController(ureq, wControl, grp, isAdmin);
		}
		return null;
	}
	
	/**
	 * return an controller for the wiki tool
	 * @param ureq
	 * @param wControl
	 * @return
	 */
	public Controller createWikiController(UserRequest ureq, WindowControl wControl) {
		// Check for jumping to certain wiki page
		ContextEntry ce = wControl.getBusinessControl().popLauncherContextEntry();

		Roles roles = ureq.getUserSession().getRoles();
		SubscriptionContext subContext = new SubscriptionContext(ores, WikiManager.WIKI_RESOURCE_FOLDER_NAME);
		boolean administrator = roles.isAdministrator() || roles.isGroupManager();
		WikiSecurityCallback callback = new WikiSecurityCallbackImpl(null, administrator, roles.isGuestOnly(), true, false, subContext);
		String initialPage = null;
		if (ce != null) { //jump to a certain context
			OLATResourceable ceOres = ce.getOLATResourceable();
			String typeName = ceOres.getResourceableTypeName();
			initialPage = typeName.substring("page=".length());
			if(initialPage != null && initialPage.endsWith(":0")) {
				initialPage = initialPage.substring(0, initialPage.length() - 2);
			}
		}
		return WikiManager.getInstance().createWikiMainController(ureq, wControl, ores, callback, initialPage);
	}
	
	/**
	 * return an controller for the wiki tool
	 * @param ureq
	 * @param wControl
	 * @return
	 */
	public Controller createPortfolioController(final UserRequest ureq, final WindowControl wControl,
			final TooledStackedPanel stackPanel, final BusinessGroup group) {
		final NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property mapProperty = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_PORTFOLIO);
		if(mapProperty != null) {
			return createPortfolioController(ureq, wControl, stackPanel, mapProperty);
		} else {
			return coordinatorManager.getCoordinator().getSyncer().doInSync(ores, () -> {
				Controller ctrl;
				Property mapKeyProperty = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_PORTFOLIO);
				if (mapKeyProperty == null) {
					PortfolioV2Module moduleV2 = CoreSpringFactory.getImpl(PortfolioV2Module.class);
					if(moduleV2.isEnabled()) {
						PortfolioService portfolioService = CoreSpringFactory.getImpl(PortfolioService.class);
						Binder binder = portfolioService.createNewBinder(group.getName(), group.getDescription(), null, null);
						CoreSpringFactory.getImpl(BinderUserInformationsDAO.class).updateBinderUserInformationsInSync(binder, ureq.getIdentity());
						mapKeyProperty = npm.createPropertyInstance(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_PORTFOLIO, null, binder.getKey(), "2", null);
						BinderSecurityCallback secCallback = BinderSecurityCallbackFactory.getCallbackForBusinessGroup();
						BinderController binderCtrl = new BinderController(ureq, wControl, stackPanel, secCallback, binder, BinderConfiguration.createBusinessGroupConfig());					
						List<ContextEntry> entries = BusinessControlFactory.getInstance().createCEListFromResourceType("Toc");
						binderCtrl.activate(ureq, entries, null);
						ctrl = binderCtrl;

						ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrap(binder));
						ThreadLocalUserActivityLogger.log(PortfolioLoggingAction.PORTFOLIO_BINDER_CREATED, getClass());
					} else {
						EPFrontendManager ePFMgr = CoreSpringFactory.getImpl(EPFrontendManager.class);
						PortfolioStructureMap map = ePFMgr.createAndPersistPortfolioDefaultMap(group.getName(), group.getDescription());					
						Translator pT = Util.createPackageTranslator(EPCreateMapController.class, ureq.getLocale());					
						// add a page, as each map should have at least one per default!
						ePFMgr.createAndPersistPortfolioPage(map, pT.translate("new.page.title"), pT.translate("new.page.desc"));
						mapKeyProperty = npm.createPropertyInstance(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_PORTFOLIO, null, map.getKey(), null, null);
						EPSecurityCallback secCallback = new EPSecurityCallbackImpl(true, true);
						ctrl = EPUIFactory.createMapViewController(ureq, wControl, map, secCallback);
					}
					npm.saveProperty(mapKeyProperty);
				} else {
					ctrl = createPortfolioController(ureq, wControl, stackPanel, mapProperty);
				}
				return ctrl;
			});
		}
	}
	
	/**
	 * 
	 * @param ureq
	 * @param wControl
	 * @param mapProperty The property is mandatory!
	 * @return
	 */
	private Controller createPortfolioController(UserRequest ureq, WindowControl wControl, TooledStackedPanel stackPanel,
			Property mapProperty) {
		Long key = mapProperty.getLongValue();
		String version = mapProperty.getStringValue();
		
		Controller ctrl;
		if("2".equals(version)) {
			PortfolioService portfolioService = CoreSpringFactory.getImpl(PortfolioService.class);
			Binder binder = portfolioService.getBinderByKey(key);
			if(binder == null) {
				Translator trans = Util.createPackageTranslator(this.getClass(), ureq.getLocale());
				String text = trans.translate("error.missing.map");
				ctrl = MessageUIFactory.createErrorMessage(ureq, wControl, "", text);
			} else {
				portfolioService.updateBinderUserInformations(binder, ureq.getIdentity());
				BinderSecurityCallback secCallback = BinderSecurityCallbackFactory.getCallbackForBusinessGroup();
				ctrl = new BinderController(ureq, wControl, stackPanel, secCallback, binder, BinderConfiguration.createBusinessGroupConfig());
			}
		} else {
			PortfolioStructureMap map = (PortfolioStructureMap) CoreSpringFactory.getImpl(EPFrontendManager.class)
					.loadPortfolioStructureByKey(key);
			if(map == null) {
				Translator trans = Util.createPackageTranslator(this.getClass(), ureq.getLocale());
				String text = trans.translate("error.missing.map");
				ctrl = MessageUIFactory.createErrorMessage(ureq, wControl, "", text);
			} else {
				EPSecurityCallback secCallback = new EPSecurityCallbackImpl(true, true);
				ctrl = EPUIFactory.createMapViewController(ureq, wControl, map, secCallback);
			}
		}
		return ctrl;
	}
	
	public Controller createOpenMeetingsController(final UserRequest ureq, WindowControl wControl, final BusinessGroup group, boolean admin) {
		return new OpenMeetingsRunController(ureq, wControl, group, null, null, admin, admin, false);
	}

	/**
	 * @param toolToChange
	 * @param enable
	 */
	public void setToolEnabled(String toolToChange, boolean enable) {
		createOrUpdateProperty(toolToChange, enable);
	}

	/**
	 * reads from the internal cache. <b>Precondition </b> cache must be filled at
	 * CollaborationTools creation time.
	 * 
	 * @param enabledTool
	 * @return boolean
	 */
	public boolean isToolEnabled(String enabledTool) {
		//o_clusterOK as whole object gets invalidated if tool is added or deleted
		if (!cacheToolStates.containsKey(enabledTool)) {
			// not in cache yet, read property first (see getPropertyOf(..))
			getPropertyOf(enabledTool);
		}
		// POSTCONDITION: cacheToolStates.get(enabledTool) != null
		Boolean cachedValue = cacheToolStates.get(enabledTool);
		return cachedValue.booleanValue();
	}

	/**
	 * delete all CollaborationTools stuff from the database, which is related to
	 * the calling OLATResourceable.
	 */
	public void deleteTools(BusinessGroup businessGroupTodelete) {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		/*
		 * delete the forum, if existing
		 */
		Property forumKeyProperty = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_FORUM);
		if (forumKeyProperty != null) {
			// if there was a forum, delete it
			Long forumKey = forumKeyProperty.getLongValue();
			if (forumKey == null) throw new AssertException("property had no longValue, prop:" + forumKeyProperty);
			CoreSpringFactory.getImpl(ForumManager.class).deleteForum(forumKey);
		}
		/*
		 * delete the folder, if existing
		 */
		VFSContainer vfsContainer = VFSManager.olatRootContainer(getFolderRelPath(), null);
		if (vfsContainer.exists()) {
			vfsContainer.deleteSilently();
		}
		
		/*
		 * delete the wiki if existing
		 */
		VFSContainer rootContainer = WikiManager.getInstance().getWikiRootContainer(ores);
		if(rootContainer != null) {
			rootContainer.deleteSilently();
		}
		
		/*
		 * Delete calendar if exists
		 */
		if (businessGroupTodelete != null) {
			CoreSpringFactory.getImpl(ImportToCalendarManager.class).deleteGroupImportedCalendars(businessGroupTodelete);
			CoreSpringFactory.getImpl(CalendarManager.class).deleteGroupCalendar(businessGroupTodelete);
		}
		
		/*
		 * delete chatRoom
		 */
		// no cleanup needed, automatically done when last user exits the room
		/*
		 * delete all Properties defining enabled/disabled CollabTool XY and the
		 * news content
		 */
		npm.deleteProperties(null, null, PROP_CAT_BG_COLLABTOOLS, null);
		
		/*
		 * Delete OpenMeetings room
		 */
		OpenMeetingsModule omModule = CoreSpringFactory.getImpl(OpenMeetingsModule.class);
		if(omModule.isEnabled()) {
			try {
				CoreSpringFactory.getImpl(OpenMeetingsManager.class).deleteAll(ores, null, null);
			} catch (OpenMeetingsException e) {
				log.error("A room could not be deleted for group: " + ores, e);
			}
		}

		/*
		 * and last but not least the cache is reseted
		 */
		cacheToolStates.clear();
		this.dirty = true;
	}
	
	
	private void openOpenMeetingsRoom() {
		OpenMeetingsModule omModule = CoreSpringFactory.getImpl(OpenMeetingsModule.class);
		if(!omModule.isEnabled()) return;
		
		OpenMeetingsManager omm = CoreSpringFactory.getImpl(OpenMeetingsManager.class);
		Long roomId = omm.getRoomId(ores, null, null);
		if(roomId == null) {
			//create the room
			OpenMeetingsRoom room = new OpenMeetingsRoom();
			room.setComment(ores.getDescription());
			room.setModerated(true);
			room.setName(ores.getName());
			room.setAudioOnly(true);
			room.setResourceName(ores.getName());
			room.setSize(25);
			room.setType(RoomType.conference.type());
			omm.addRoom(ores, null, null, room);
		}
	}

	/**
	 * creates the property if non-existing, or updates the existing property to
	 * the supplied values. Real changes are made persistent immediately.
	 * 
	 * @param selectedTool
	 * @param toolValue
	 */
	private void createOrUpdateProperty(final String selectedTool, final boolean toolValue) {

		Boolean cv = cacheToolStates.get(selectedTool);
		if (cv != null && cv.booleanValue() == toolValue) {
			return; // nice, cache saved a needless update
		}

		// handle Boolean Values via String Field in Property DB Table
		final String toolValueStr = toolValue ? TRUE : FALSE;
		final PropertyManager pm = PropertyManager.getInstance();
		coordinatorManager.getCoordinator().getSyncer().doInSync(ores, new SyncerExecutor() {
			@Override
			public void execute() {				
				Property property = getPropertyOf(selectedTool);
				if (property == null) {
					// not existing -> create it
					property = pm.createPropertyInstance(null, null, ores, PROP_CAT_BG_COLLABTOOLS, selectedTool, null, null, toolValueStr, null);
				} else {
					// if existing -> update to desired value
					property.setStringValue(toolValueStr);
				}
				
				//create a room if needed
				if(toolValue && TOOL_OPENMEETINGS.equals(selectedTool)) {
					openOpenMeetingsRoom();
				}
				
				// property becomes persistent
				pm.saveProperty(property);
			}});
		this.dirty = true;
		cacheToolStates.put(selectedTool, Boolean.valueOf(toolValue));
	}

	Property getPropertyOf(String selectedTool) {
		PropertyManager pm = PropertyManager.getInstance();
		Property property = pm.findProperty(null, null, ores, PROP_CAT_BG_COLLABTOOLS, selectedTool);
		Boolean res;
		if (property == null) { // meaning false
			res = Boolean.FALSE;
		} else {
			String val = property.getStringValue();
			res = val.equals(TRUE) ? Boolean.TRUE : Boolean.FALSE;
		}
		cacheToolStates.put(selectedTool, res);
		return property;
	}

	/**
	 * create the Collaboration Tools Suite. This Controller handles the
	 * enabling/disabling of Collab Tools.
	 * 
	 * @param ureq
	 * @return a collaboration tools settings controller
	 */
	public CollaborationToolsSettingsController createCollaborationToolsSettingsController(UserRequest ureq, WindowControl wControl) {
		return new CollaborationToolsSettingsController(ureq, wControl, ores);
	}

	/**
	 * @return Gets the news access property
	 */
	public String getNewsAccessProperty() {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_NEWS_ACCESS);
		if (property == null) { // no entry
			return null;
		}
		// read the text value of the existing property
		return property.getStringValue();
	}
	
	/**
	 * @param Save news access property.
	 */
	public void saveNewsAccessProperty(String access) {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_NEWS_ACCESS);
		if (property == null) { // create a new one
			Property nP = npm.createPropertyInstance(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_NEWS_ACCESS, null, null, access, null);
			npm.saveProperty(nP);
		} else { // modify the existing one
			property.setStringValue(access);
			npm.updateProperty(property);
		}
	}

	/**
	 * @return the news; if there is no news yet: return null;
	 */
	public String lookupNews() {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_NEWS);
		if (property == null) { // no entry
			return null;
		}
		// read the text value of the existing property
		return property.getTextValue();
	}
	
	public Property lookupNewsDBEntry() {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_NEWS);
		if (property == null) { // no entry
			return null;
		}
		return property;
	}

	/**
	 * @param news
	 */
	public void saveNews(String news) {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_NEWS);
		if (property == null) { // create a new one
			Property nP = npm.createPropertyInstance(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_NEWS, null, null, null, news);
			npm.saveProperty(nP);
		} else { // modify the existing one
			property.setTextValue(news);
			npm.updateProperty(property);
		}
	}
	
	public void setToolAccess(String tool, Integer access) {
		if (TOOL_FOLDER.equals(tool) && access != null) {
			if (FOLDER_ACCESS_ALL == access.intValue()) {
				saveFolderAccess(Long.valueOf(FOLDER_ACCESS_ALL));
			} else if (FOLDER_ACCESS_OWNERS == access.intValue()) {
				saveFolderAccess(Long.valueOf(FOLDER_ACCESS_OWNERS));
			} 			
		} else if (TOOL_CALENDAR.equals(tool) && access != null) {
			if (CALENDAR_ACCESS_ALL == access.intValue()) {
				saveCalendarAccess(Long.valueOf(CALENDAR_ACCESS_ALL));
			} else if (CALENDAR_ACCESS_OWNERS == access.intValue()) {
				saveCalendarAccess(Long.valueOf(CALENDAR_ACCESS_OWNERS));
			} 						
		}
	}
	
	public Long lookupCalendarAccess() {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_CALENDAR_ACCESS);
		if (property == null) { // no entry
			return null;
		}
		// read the long value of the existing property
		return property.getLongValue();
	}

	
	public void saveCalendarAccess(Long calendarAccess) {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_CALENDAR_ACCESS);
		if (property == null) { // create a new one
			Property nP = npm.createPropertyInstance(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_CALENDAR_ACCESS, null, calendarAccess, null, null);
			npm.saveProperty(nP);
		} else { // modify the existing one
			property.setLongValue(calendarAccess);
			npm.updateProperty(property);
		}
	}
	
	public Long lookupFolderAccess() {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_FOLDER_ACCESS);
		if (property == null) { // no entry
			return null;
		}
		// read the long value of the existing property
		return property.getLongValue();
	}
	
	public void saveFolderAccess(Long folderrAccess) {
		NarrowedPropertyManager npm = NarrowedPropertyManager.getInstance(ores);
		Property property = npm.findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_FOLDER_ACCESS);
		if (property == null) { // create a new one
			Property nP = npm.createPropertyInstance(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_FOLDER_ACCESS, null, folderrAccess, null, null);
			npm.saveProperty(nP);
		} else { // modify the existing one
			property.setLongValue(folderrAccess);
			npm.updateProperty(property);
		}
	}
	
	public class CollabSecCallback implements VFSSecurityCallback {
		
		private final boolean write;
		private Quota folderQuota = null;
		private SubscriptionContext subsContext;

		public CollabSecCallback(boolean write, String relPath, SubscriptionContext subsContext) {
			this.subsContext = subsContext;
			initFolderQuota(relPath);
			this.write = write;
		}

		private void initFolderQuota(String relPath) {
			QuotaManager qm = CoreSpringFactory.getImpl(QuotaManager.class);
			folderQuota = qm.getCustomQuota(relPath);
			if (folderQuota == null) {
				Quota defQuota = qm.getDefaultQuota(QuotaConstants.IDENTIFIER_DEFAULT_GROUPS);
				folderQuota = qm.createQuota(relPath, defQuota.getQuotaKB(), defQuota.getUlLimitKB());
			}
		}

		@Override
		public boolean canRead() {
			return true;
		}

		@Override
		public boolean canWrite() {
			return write;
		}

		@Override
		public boolean canCreateFolder() {
			return write;
		}

		@Override
		public boolean canDelete() {
			return write;
		}

		@Override
		public boolean canList() {
			return true;
		}

		@Override
		public boolean canCopy() {
			return true;
		}

		@Override
		public boolean canDeleteRevisionsPermanently() {
			return write;
		}

		@Override
		public Quota getQuota() {
			return folderQuota;
		}

		@Override
		public void setQuota(Quota quota) {
			this.folderQuota = quota;
		}

		@Override
		public SubscriptionContext getSubscriptionContext() {
			return subsContext;
		}
	}

	/**
	 * It is assumed that this is only called by an administrator
	 * (e.g. at deleteGroup) 
	 * @param archivFilePath
	 */
	public void archive(String archivFilePath) {
		if (isToolEnabled(CollaborationTools.TOOL_FORUM)) {
			archiveForum(archivFilePath);
		}
		if (isToolEnabled(CollaborationTools.TOOL_WIKI)) {
			archiveWiki(archivFilePath);
		}
		if (isToolEnabled(CollaborationTools.TOOL_FOLDER)) {
			archiveFolder(archivFilePath);
		}
	}

	private void archiveForum(String archivFilePath) {
		Property forumKeyProperty = NarrowedPropertyManager.getInstance(ores).findProperty(null, null, PROP_CAT_BG_COLLABTOOLS, KEY_FORUM);
		if (forumKeyProperty != null) {
			VFSContainer archiveContainer = new LocalFolderImpl(new File(archivFilePath));
			String archiveForumName = "del_forum_" + forumKeyProperty.getLongValue();
			VFSContainer archiveForumContainer = archiveContainer.createChildContainer(archiveForumName);
			ForumFormatter ff = new ForumRTFFormatter(archiveForumContainer, false, I18nModule.getDefaultLocale());
			CoreSpringFactory.getImpl(ForumArchiveManager.class).applyFormatter(ff, forumKeyProperty.getLongValue(), null);
		}
	}

	private void archiveWiki(String archivFilePath) { 
		VFSContainer wikiContainer = WikiManager.getInstance().getWikiRootContainer(ores);
		VFSLeaf wikiZip = WikiToZipUtils.getWikiAsZip(wikiContainer);
		String exportFileName = "del_wiki_" + ores.getResourceableId() + ".zip";
		File archiveDir = new File(archivFilePath);
		if (!archiveDir.exists()) {
			archiveDir.mkdir();
		}
		String fullFilePath = archivFilePath + File.separator + exportFileName;
		
		try {
			FileUtils.bcopy(wikiZip.getInputStream(), new File(fullFilePath), "archive wiki");
		} catch (IOException ioe) {
			log.warn("Can not archive wiki repoEntry=" + ores.getResourceableId());
		}		
	}

	private void archiveFolder(String archiveFilePath) {
		LocalFolderImpl folderContainer = VFSManager.olatRootContainer(getFolderRelPath(), null);
		File fFolderRoot = folderContainer.getBasefile();
		if (fFolderRoot.exists()) {
			String zipFileName = "del_folder_" + ores.getResourceableId() + ".zip";
			String fullZipFilePath = archiveFilePath + File.separator + zipFileName;
			ZipUtil.zipAll(fFolderRoot, new File(fullZipFilePath), true);
		}
	}

	/**
	 * whole object gets cached, if tool gets added or deleted the object becomes dirty and will be removed from cache.
	 * @return
	 */
	protected boolean isDirty() {
		return dirty;
	}
}