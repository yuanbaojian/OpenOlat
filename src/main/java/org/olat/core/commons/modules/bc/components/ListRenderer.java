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
* <p>
*/ 

package org.olat.core.commons.modules.bc.components;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.modules.bc.FileSelection;
import org.olat.core.commons.modules.bc.FolderConfig;
import org.olat.core.commons.modules.bc.FolderLicenseHandler;
import org.olat.core.commons.modules.bc.FolderManager;
import org.olat.core.commons.services.license.License;
import org.olat.core.commons.services.license.LicenseHandler;
import org.olat.core.commons.services.license.LicenseModule;
import org.olat.core.commons.services.license.ui.LicenseRenderer;
import org.olat.core.gui.components.form.flexible.impl.NameValuePair;
import org.olat.core.gui.control.winmgr.AJAXFlags;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.render.URLBuilder;
import org.olat.core.gui.translator.Translator;
import org.olat.core.gui.util.CSSHelper;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.Formatter;
import org.olat.core.util.StringHelper;
import org.olat.core.util.vfs.AbstractVirtualContainer;
import org.olat.core.util.vfs.NamedContainerImpl;
import org.olat.core.util.vfs.VFSConstants;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSItem;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.core.util.vfs.VFSLockManager;
import org.olat.core.util.vfs.VirtualContainer;
import org.olat.core.util.vfs.filters.SystemItemFilter;
import org.olat.core.util.vfs.lock.LockInfo;
import org.olat.core.util.vfs.meta.MetaInfo;
import org.olat.core.util.vfs.meta.MetaInfoFactory;
import org.olat.core.util.vfs.version.Versionable;
import org.olat.core.util.vfs.version.Versions;
import org.olat.user.UserManager;

/**
 * Initial Date:  Feb 12, 2004
 *
 * @author Mike Stock
 */
public class ListRenderer {
	
	private static final OLog log = Tracing.createLoggerFor(ListRenderer.class);

	/** Edit parameter identifier. */
	public static final String PARAM_EDTID = "fcedt";
	/** Edit parameter identifier. */
 	public static final String PARAM_CONTENTEDITID = "contentedit";
 	/** Serve resource identifier */
 	public static final String PARAM_SERV = "serv";
	/** Sort parameter identifier. */
	public static final String PARAM_SORTID = "fcsrt";
	/** View version parameter identifier. */
	public static final String PARAM_VERID = "fcver";
	/** Add to ePortfolio parameter identifier. */
	public static final String PARAM_EPORT = "epadd";
	/** View thumbnail */
	public static final String PARAM_SERV_THUMBNAIL = "servthumb";

	private VFSLockManager lockManager;
	private UserManager userManager;
	boolean licensesEnabled ;
 	
	/**
	 * Default constructor.
	 */
	public ListRenderer() {
		//
	} 

	/**
	 * Render contents of directory to a html table.
	 * 
	 * @param dir
	 * @param secCallback
	 * @param ubu
	 * @param translator
	 * @param iframePostEnabled
	 * @return Render results.
	 */
	public void render(FolderComponent fc, StringOutput sb, URLBuilder ubu, Translator translator, boolean iframePostEnabled) {
		if(lockManager == null) {
			lockManager = CoreSpringFactory.getImpl(VFSLockManager.class);
		}
		if(userManager == null) {
			userManager = CoreSpringFactory.getImpl(UserManager.class);
		}
		LicenseModule licenseModule = CoreSpringFactory.getImpl(LicenseModule.class);
		LicenseHandler licenseHandler = CoreSpringFactory.getImpl(FolderLicenseHandler.class);
		licensesEnabled = licenseModule.isEnabled(licenseHandler);

		List<VFSItem> children = fc.getCurrentContainerChildren();
		// folder empty?
		if (children.isEmpty()) {
			sb.append("<div class=\"o_bc_empty\"><i class='o_icon o_icon_warn'></i> ")
			  .append(translator.translate("NoFiles"))
			  .append("</div>");
			return;
		}

		boolean canVersion = FolderConfig.versionsEnabled(fc.getCurrentContainer());
		String sortOrder = fc.getCurrentSortOrder();
		boolean sortAsc = fc.isCurrentSortAsc();
		String sortCss = (sortAsc ? "o_orderby_asc" : "o_orderby_desc");
				
		sb.append("<table class=\"table table-condensed table-striped table-hover o_bc_table\">")
		  .append("<thead><tr><th><a class='o_orderby ").append(sortCss,FolderComponent.SORT_NAME.equals(sortOrder)).append("' ");
		ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_SORTID, FolderComponent.SORT_NAME))
		   .append(">").append(translator.translate("header.Name")).append("</a>").append("</th>");
		sb.append("<th><a class='o_orderby ").append(sortCss,FolderComponent.SORT_SIZE.equals(sortOrder)).append("' ");
		ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_SORTID, FolderComponent.SORT_SIZE))
		   .append(">").append(translator.translate("header.Size")).append("</a>")
		   .append("</th><th><a class='o_orderby ").append(sortCss,FolderComponent.SORT_DATE.equals(sortOrder)).append("' ");	
		ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_SORTID, FolderComponent.SORT_DATE))
		   .append(">").append(translator.translate("header.Modified")).append("</a>");
		if (licensesEnabled) {
			sb.append("<th>").append(translator.translate("header.license")).append("</th>");
		}
		if(canVersion) {
			sb.append("</th><th><a class='o_orderby ").append(sortCss,FolderComponent.SORT_REV.equals(sortOrder)).append("' ");		
			ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_SORTID, FolderComponent.SORT_REV))																																					// file size column
			   .append("><i class=\"o_icon o_icon_version  o_icon-lg\" title=\"")
			   .append(translator.translate("versions")).append("\"></i></a>");
		}

		sb.append("</th><th><a class='o_orderby ").append(sortCss,FolderComponent.SORT_LOCK.equals(sortOrder)).append("' ");
		ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_SORTID, FolderComponent.SORT_LOCK))
		   .append("><i class=\"o_icon o_icon_locked  o_icon-lg\" title=\"")
		   .append(translator.translate("lock.title")).append("\"></i></a>")
		// meta data column
		  .append("</th><th><i class=\"o_icon o_icon_edit_metadata o_icon-lg\" title=\"")
		  .append(translator.translate("mf.edit")).append("\"></i></th></tr></thead>");
				
		// render directory contents
		String currentContainerPath = fc.getCurrentContainerPath();
		if (currentContainerPath.length() > 0 && currentContainerPath.charAt(0) == '/') {
			currentContainerPath = currentContainerPath.substring(1);
		}

		sb.append("<tbody>");
		
		for (int i = 0; i < children.size(); i++) {
			VFSItem child = children.get(i);
			appendRenderedFile(fc, child, currentContainerPath, sb, ubu, translator, iframePostEnabled, canVersion, i);
		}		
		sb.append("</tbody></table>");
	} // getRenderedDirectoryContent
	
	/**
	 * Render a single file or folder.
	 * 
	 * @param	f			The file or folder to render
	 * @param	sb		StringOutput to append generated html code
	 */
	private void appendRenderedFile(FolderComponent fc, VFSItem child, String currentContainerPath, StringOutput sb, URLBuilder ubu, Translator translator,
			boolean iframePostEnabled, boolean canContainerVersion, int pos) {
	
		// assume full access unless security callback tells us something different.
		boolean canWrite = child.getParentContainer().canWrite() == VFSConstants.YES;
		// special case: virtual folders are always read only. parent of child =! the current container
		canWrite = canWrite && !(fc.getCurrentContainer() instanceof VirtualContainer);
		boolean isAbstract = (child instanceof AbstractVirtualContainer);

		Versions versions = null;
		if(canContainerVersion && child instanceof Versionable) {
			Versionable versionable = (Versionable)child;
			Versions possibleVersions = versionable.getVersions();
			if(possibleVersions.isVersioned()) {
				versions = possibleVersions;
			}
		}
		boolean canVersion = versions != null && !versions.getRevisions().isEmpty();

		VFSLeaf leaf = null;
		if (child instanceof VFSLeaf) {
			leaf = (VFSLeaf)child;
		}
		boolean isContainer = (leaf == null); // if not a leaf, it must be a container...
		
		MetaInfo metaInfo = null;
		if(child.canMeta() == VFSConstants.YES) {
			metaInfo = child.getMetaInfo();
		}
		
		boolean lockedForUser = lockManager.isLockedForMe(child, metaInfo, fc.getIdentityEnvironnement().getIdentity(), fc.getIdentityEnvironnement().getRoles());
		
		String name = child.getName();
		boolean xssErrors = StringHelper.xssScanForErrors(name);
		
		String pathAndName;
		if(xssErrors) {
			pathAndName = null;
		} else {
			pathAndName = currentContainerPath;
			if (pathAndName.length() > 0 && !pathAndName.endsWith("/")) {
				pathAndName += "/";
			}
			pathAndName += name;
		}
				
		// tr begin
		sb.append("<tr><td>")
		// add checkbox for actions if user can write, delete or email this directory
		  .append("<input type=\"checkbox\" name=\"")
		  .append(FileSelection.FORM_ID)
		  .append("\" value=\"");
		if(xssErrors) {
			sb.append(StringHelper.escapeHtml(name))
			  .append("\" disabled=\"disabled\"");
		} else {
			sb.append(name).append("\" ");
		}
		sb.append("/> ");
		// browse link pre
		if(xssErrors) {
			sb.append("<i class='o_icon o_icon-fw o_icon_banned'> </i> ");
			sb.append(StringHelper.escapeHtml(name));
			log.error("XSS Scan found something suspicious in: " + child);
		} else {
			sb.append("<a id='o_sel_doc_").append(pos).append("'");
		
			if (isContainer) { // for directories... normal module URIs
				// needs encoding, not done in buildHrefAndOnclick!
				//FIXME: SR: refactor encode: move to ubu.buildHrefAndOnclick
				String pathAndNameEncoded = ubu.encodeUrl(pathAndName);
				ubu.buildHrefAndOnclick(sb, pathAndNameEncoded, iframePostEnabled, false, true);
			} else { // for files, add PARAM_SERV command
				sb.append(" href=\"");
				ubu.buildURI(sb, new String[] { PARAM_SERV }, new String[] { "x" }, pathAndName, AJAXFlags.MODE_NORMAL);
				sb.append("\"");

				boolean download = FolderManager.isDownloadForcedFileType(name);
				if (download) {
					sb.append(" download=\"").append(StringHelper.escapeHtml(name)).append("\"");					
				} else {					
					sb.append(" target=\"_blank\"");
				}
				if(fc.getAnalyticsSPI() != null) {
					sb.append(" onclick=\"");
					fc.getAnalyticsSPI().analyticsCountOnclickJavaScript(sb);
					sb.append("\"");
				}
			}
			sb.append(">");

			// icon css
			sb.append("<i class=\"o_icon o_icon-fw ");
			if (isContainer) sb.append(CSSHelper.CSS_CLASS_FILETYPE_FOLDER);
			else sb.append(CSSHelper.createFiletypeIconCssClassFor(name));
			sb.append("\"></i> ");
	
			// name
			if (isAbstract) sb.append("<i>");
			sb.append(StringHelper.escapeHtml(name));
			if (isAbstract) sb.append("</i>");
			sb.append("</a>");
		}

		//file metadata as tooltip
		if (metaInfo != null) {
			boolean hasMeta = false;
			sb.append("<div id='o_sel_doc_tooltip_").append(pos).append("' class='o_bc_meta' style='display:none;'>");
			if (StringHelper.containsNonWhitespace(metaInfo.getTitle())) {
				String title = StringHelper.escapeHtml(metaInfo.getTitle());
				sb.append("<h5>").append(Formatter.escapeDoubleQuotes(title)).append("</h5>");
				hasMeta = true;
			}
			if (StringHelper.containsNonWhitespace(metaInfo.getComment())) {
				sb.append("<div class=\"o_comment\">");
				String comment = StringHelper.escapeHtml(metaInfo.getComment());
				sb.append(Formatter.escapeDoubleQuotes(comment));			
				sb.append("</div>");
				hasMeta = true;
			}
			
			if(metaInfo.isThumbnailAvailable() && !xssErrors) {
				sb.append("<div class='o_thumbnail' style='background-image:url("); 
				ubu.buildURI(sb, new String[] { PARAM_SERV_THUMBNAIL}, new String[] { "x" }, pathAndName, AJAXFlags.MODE_NORMAL);
				sb.append("); background-repeat:no-repeat; background-position:50% 50%;'></div>");
				hasMeta = true;
			}

			// first try author info from metadata (creator)
			String author = metaInfo.getCreator();
			// fallback use file author (uploader)
			if(StringHelper.containsNonWhitespace(author)) {
				//
			} else {
				author = metaInfo.getAuthor();
				if(!"-".equals(author)) {
					author = UserManager.getInstance().getUserDisplayName(author);
				} else {
					author = null;
				}		
			}
			author = StringHelper.escapeHtml(author);
			if (StringHelper.containsNonWhitespace(author)) {
				sb.append("<p class=\"o_author\">").append(Formatter.escapeDoubleQuotes(translator.translate("mf.author")));
				sb.append(": ").append(Formatter.escapeDoubleQuotes(author)).append("</p>");			
				hasMeta = true;
			}
			sb.append("</div>");
			if (hasMeta) {
				// render tooltip only when it contains something
				sb.append("<script type='text/javascript'>")
			      .append("/* <![CDATA[ */")
				  .append("jQuery(function() {\n")
				  .append("  jQuery('#o_sel_doc_").append(pos).append("').tooltip({\n")
				  .append("	   html: true,\n")
				  .append("	   container: 'body',\n")
				  .append("    title: function(){ return jQuery('#o_sel_doc_tooltip_").append(pos).append("').html(); }\n")
				  .append("  });\n")
				  .append("  jQuery('#o_sel_doc_").append(pos).append("').on('click', function(){\n")
				  .append("	   jQuery('#o_sel_doc_").append(pos).append("').tooltip('hide');\n")
				  .append("  });\n")
				  .append("});")
				  .append("/* ]]> */")
				  .append("</script>");
			}
		}
		sb.append("</td><td>");
		// filesize
		if (!isContainer) {
			// append filesize
			sb.append("<span class='text-muted small'>")
			  .append(Formatter.formatBytes(leaf.getSize()))
			  .append("</span>");
		} else if (child instanceof VFSContainer) {
			try {
				sb.append("<span class='text-muted small'>")
				  .append(((VFSContainer) child).getItems(new SystemItemFilter()).size())
				  .append(" ").append(translator.translate("mf.elements")).append("</span>");
			} catch (Exception e) {
				log.error("", e);
			}
		}
		sb.append("</td><td>");
		
		// last modified
		long lastModified = child.getLastModified();
		sb.append("<span class='text-muted small'>");
		if (lastModified != VFSConstants.UNDEFINED)
			sb.append(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, translator.getLocale()).format(new Date(lastModified)));
		else
			sb.append("-");
		sb.append("</span></td><td>");
		
		// license
		if (licensesEnabled) {
			MetaInfoFactory metaInfoFactory = CoreSpringFactory.getImpl(MetaInfoFactory.class);
			License license = metaInfoFactory.getLicense(metaInfo);
			LicenseRenderer licenseRenderer = new LicenseRenderer(translator.getLocale());
			licenseRenderer.render(sb, license, true);
			sb.append("</td><td>");
		}

		if(canContainerVersion) {
			if (canVersion)
				if (versions != null) {
					sb.append("<span class='text-muted small'>");
					sb.append(versions.getRevisionNr());
					sb.append("</span>");					
				}
			sb.append("</td><td>");
		}
		
		//locked
		boolean locked = lockManager.isLocked(child, metaInfo);
		if(locked) {
			LockInfo lock = lockManager.getLock(child);
			sb.append("<i class=\"o_icon o_icon_locked\" title=\"");
			if(lock != null && lock.getLockedBy() != null) {
				String fullname = userManager.getUserDisplayName(lock.getLockedBy());
				String date = "";
				if(lock.getCreationDate() != null) {
					date = fc.getDateTimeFormat().format(lock.getCreationDate());
				}
				String msg = translator.translate("Locked", new String[]{fullname, date});
				if(lock.isWebDAVLock()) {
					msg += " (WebDAV)";
				}
				sb.append(msg);
			}
			sb.append("\">&#160;</i>");
		}
		sb.append("</td><td>");

		// Info link
		if (canWrite) {
			int actionCount = 0;
			if(canVersion) {
				actionCount++;
			}
			
			String nameLowerCase = name.toLowerCase();
			boolean isLeaf= (child instanceof VFSLeaf); // OO-57 only display edit link if it's not a folder
			boolean isEditable =  (isLeaf && !lockedForUser && !xssErrors &&
					(nameLowerCase.endsWith(".html") || nameLowerCase.endsWith(".htm")
							|| nameLowerCase.endsWith(".txt") || nameLowerCase.endsWith(".css")
							|| nameLowerCase.endsWith(".csv	")));
			if(isEditable) actionCount++;

			boolean canMetaData = canMetaInfo(child);
			if (canMetaData) actionCount++;

			if (actionCount == 1 && canMetaData) {
				// when only one action is available, don't render menu
				sb.append("<a ");
				ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_EDTID, pos))
				   .append(" title=\"").append(StringHelper.escapeHtml(translator.translate("mf.edit")))
				   .append("\"><i class=\"o_icon o_icon-fw o_icon_edit_metadata\"></i></a>");

			} else if (actionCount > 1) {
				// add actions to menu if multiple actions available
				sb.append("<a id='o_sel_actions_").append(pos).append("' href='javascript:;'><i class='o_icon o_icon-lg o_icon_actions'></i></a>")
				  .append("<div id='o_sel_actions_pop_").append(pos).append("' style='display:none;'><ul class='list-unstyled'>");
				
				// meta edit action (rename etc)
				if (canMetaData) {
					// Metadata edit link... also handles rename for non-OlatRelPathImpls
					sb.append("<li><a ");
					ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_EDTID, pos))
					   .append("><i class=\"o_icon o_icon-fw o_icon_edit_metadata\"></i> ").append(StringHelper.escapeHtml(translator.translate("mf.edit"))).append("</a></li>");
				}
				// content edit action
				if (isEditable) {
					sb.append("<li><a ");
					ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_CONTENTEDITID, pos))
					   .append("><i class=\"o_icon o_icon-fw o_icon_edit_file\"></i> ").append(StringHelper.escapeHtml(translator.translate("editor"))).append("</a></li>");
				}
			
				// versions action
				if (canVersion) {
					// Versions link
					sb.append("<li><a ");
					ubu.buildHrefAndOnclick(sb, null, iframePostEnabled, false, false, new NameValuePair(PARAM_VERID, pos))
					   .append("><i class=\"o_icon o_icon-fw o_icon_version\"></i> ").append(StringHelper.escapeHtml(translator.translate("versions"))).append("</a></li>");
				}

				sb.append("</ul></div>")
				  .append("<script type='text/javascript'>")
			      .append("/* <![CDATA[ */")
				  .append("jQuery(function() {\n")
				  .append("  o_popover('o_sel_actions_").append(pos).append("','o_sel_actions_pop_").append(pos).append("','left');\n")
				  .append("});")
				  .append("/* ]]> */")
				  .append("</script>");
			}
		}

		sb.append("</td></tr>");
	}
	
	private boolean canMetaInfo(VFSItem item) {
		if (item instanceof NamedContainerImpl) {
			item = ((NamedContainerImpl)item).getDelegate();
		}
		if(item instanceof VFSContainer) {
			String name = item.getName();
			if(name.equals("_sharedfolder_") || name.equals("_courseelementdata")) {
				return false;
			}			
		}
		return true;
	}
}