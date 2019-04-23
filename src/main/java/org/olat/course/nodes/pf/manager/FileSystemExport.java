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
package org.olat.course.nodes.pf.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.media.MediaResource;
import org.olat.core.gui.media.ServletUtil;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.course.nodes.PFCourseNode;
import org.olat.course.nodes.pf.ui.PFRunController;
import org.olat.course.run.environment.CourseEnvironment;
import org.olat.user.UserManager;
/**
*
* Initial date: 15.12.2016<br>
* @author Fabian Kiefer, fabian.kiefer@frentix.com, http://www.frentix.com
*
*/
public class FileSystemExport implements MediaResource {
	
	private static final OLog log = Tracing.createLoggerFor(FileSystemExport.class);
	
	private List<Identity> identities;
	private PFCourseNode pfNode;
	private CourseEnvironment courseEnv;
	private Translator translator;

	public FileSystemExport(List<Identity> identities, PFCourseNode pfNode, CourseEnvironment courseEnv, Locale locale) {
		super();
		this.identities = identities;
		this.pfNode = pfNode;
		this.courseEnv = courseEnv;
		this.translator = Util.createPackageTranslator(PFRunController.class, locale);

	}
	
	@Override
	public long getCacheControlDuration() {
		return ServletUtil.CACHE_NO_CACHE;
	}

	@Override
	public boolean acceptRanges() {
		return false;
	}

	@Override
	public String getContentType() {
		return "application/zip";
	}

	@Override
	public Long getSize() {
		return null;
	}

	@Override
	public InputStream getInputStream() {
		return null;
	}

	@Override
	public Long getLastModified() {
		return null;
	}

	@Override
	public void prepare(HttpServletResponse hres) {
		try (ZipOutputStream zout = new ZipOutputStream(hres.getOutputStream())) {
			zout.setLevel(9);
			
			Path relPath = Paths.get(courseEnv.getCourseBaseContainer().getBasefile().getAbsolutePath(),
					PFManager.FILENAME_PARTICIPANTFOLDER, pfNode.getIdent()); 
						
			fsToZip(zout, relPath, pfNode, identities, translator);			
			
		} catch (IOException e) {
			log.error("", e);
		}
	}

	@Override
	public void release() {
		//
	}
	
	/**
	 * Exports a given filesystem as Zip-Outputstream
	 *
	 * @param zout the Zip-Outputstream
	 * @param sourceFolder the source folder
	 * @param pfNode the PFCourseNode
	 * @param identities
	 * @param translator
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static boolean fsToZip(ZipOutputStream zout, final Path sourceFolder, PFCourseNode pfNode,
			List<Identity> identities, Translator translator) {
		String targetPath = translator.translate("participant.folder") + "/";
		UserManager userManager = CoreSpringFactory.getImpl(UserManager.class);
		Set<String> idKeys = new HashSet<>();
		if (identities != null) {
			for (Identity identity : identities) {
				idKeys.add(identity.getKey().toString());
			}
		} else {
			File[] listOfFiles = sourceFolder.toFile().listFiles();
			if(listOfFiles != null) {
				for (File file : listOfFiles) {
					if (file.isDirectory()) {
						idKeys.add(file.getName());
					}
				}
			}
		}
		try {
			Files.walkFileTree(sourceFolder, new SimpleFileVisitor<Path>() {
				//contains identity check  and changes identity key to user display name
				private String containsID (String relPath) {
					for (String key : idKeys) {
						//additional check if folder is a identity-key (coming from fs)
						if (relPath.contains(key) && StringHelper.isLong(key)) {
							String exportFolderName = userManager.getUserDisplayName(Long.parseLong(key)).replace(", ", "_")
									+ "_" + key;
							return relPath.replace(key.toString(), exportFolderName);
						}
					}
					return null;
				}
				//checks module config and translates folder name
				private String boxesEnabled(String relPath) {
					if (pfNode.hasParticipantBoxConfigured() && relPath.contains(PFManager.FILENAME_DROPBOX)) {
						return relPath.replace(PFManager.FILENAME_DROPBOX, translator.translate("drop.box"));
					} else if (pfNode.hasCoachBoxConfigured() && relPath.contains(PFManager.FILENAME_RETURNBOX)) {
						return relPath.replace(PFManager.FILENAME_RETURNBOX, translator.translate("return.box"));
					} else {
						return null;
					}
				}			
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String relPath = sourceFolder.relativize(file).toString();
					if ((relPath = containsID(relPath)) != null && (relPath = boxesEnabled(relPath)) != null) {
						zout.putNextEntry(new ZipEntry(targetPath + relPath));
						Files.copy(file, zout);
						zout.closeEntry();
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					String relPath = sourceFolder.relativize(dir).toString() + "/";
					if ((relPath = containsID(relPath)) != null && (relPath = boxesEnabled(relPath)) != null) {
						zout.putNextEntry(new ZipEntry(targetPath + relPath));
						zout.closeEntry();
					}
					return FileVisitResult.CONTINUE;
				}
			});
			zout.close();
			return true;
		} catch (IOException e) {
			log.error("Unable to export zip",e);
			return false;
		}
	}
	
}