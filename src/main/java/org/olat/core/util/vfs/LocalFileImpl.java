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

package org.olat.core.util.vfs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.modules.bc.FolderConfig;
import org.olat.core.logging.AssertException;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.vfs.meta.MetaInfo;
import org.olat.core.util.vfs.version.Versionable;
import org.olat.core.util.vfs.version.Versions;
import org.olat.core.util.vfs.version.VersionsManager;

/**
 * Description:<br>
 * VFSLeaf implementation that is based on a java.io.File from a local filesystem 
 * 
 * <P>
 * Initial Date:  23.06.2005 <br>
 *
 * @author Felix Jost
 */
public class LocalFileImpl extends LocalImpl implements VFSLeaf, Versionable {
	private static final OLog log = Tracing.createLoggerFor(LocalFileImpl.class);
	
	private Versions versions;

	private LocalFileImpl() {
		super(null, null);
		throw new AssertException("Cannot instantiate LocalFileImpl().");
	}

	/**
	 * Constructor
	 * @param file The real file wrapped by this VFSLeaf
	 */
	public LocalFileImpl(File file) {
		this(file, null);
	}
	
	/**
	 * @param file
	 */
	protected LocalFileImpl(File file, VFSContainer parentContainer) {
		super(file, parentContainer);
	}

	@Override
	public InputStream getInputStream() {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream( new FileInputStream(getBasefile()) );
		} catch (FileNotFoundException e) {
			log.warn("Could not create input stream for file::" + getBasefile().getAbsolutePath(), e);
		}
		return bis;
	}

	@Override
	public long getSize() {
		return getBasefile().length();
	}

	@Override
	public String getRelPath() {
		Path bFile = getBasefile().toPath();
		Path bcRoot = FolderConfig.getCanonicalRootPath();
		if(bFile.startsWith(bcRoot)) {
			String relPath = bcRoot.relativize(bFile).toString();
			return "/" + relPath;
		}
		return null;
	}

	@Override
	public OutputStream getOutputStream(boolean append) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(getBasefile(), append);
		} catch (FileNotFoundException e) {
			log.warn("Could not create output stream for file::" + getBasefile().getAbsolutePath(), e);
		}
		return os;
	}

	@Override
	public Versions getVersions() {
		if(versions == null) {
			versions = CoreSpringFactory.getImpl(VersionsManager.class).createVersionsFor(this);
		}
		return versions;
	}

	@Override
	public VFSStatus rename(String newname) {
		File f = getBasefile();
		if(!f.exists()) {
			return VFSConstants.NO;
		}
		
		File par = f.getParentFile();
		File nf = new File(par, newname);
		if(getVersions().isVersioned()) {
			//rename the versions
			CoreSpringFactory.getImpl(VersionsManager.class).rename(this, newname);
		}
		if(canMeta() == VFSConstants.YES) {
			getMetaInfo().rename(newname);
		}
		boolean ren = f.renameTo(nf);
		if (ren) {
			// f.renameTo() does NOT modify the path contained in the object f!!
			// The guys at sun consider this a feature and not a bug...
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4094022
			// We need to manually reload the new basefile
			super.setBasefile(new File(nf.getAbsolutePath()));
			return VFSConstants.YES; 
		} else {
			return VFSConstants.NO;
		}
	}

	@Override
	public VFSStatus delete() {
		if(getVersions().isVersioned()) {
			CoreSpringFactory.getImpl(VersionsManager.class).delete(this, false);
		}
		// Versioning makes a copy of the metadata, delete metadata after it
		if(canMeta() == VFSConstants.YES) {
			MetaInfo meta = getMetaInfo();
			if(meta != null) {// Meta can be null if the file is already deleted
				meta.delete();
			}
		}
		return deleteBasefile();
	}
	
	@Override
	public VFSStatus deleteSilently() {
		if(canMeta() == VFSConstants.YES) {
			MetaInfo meta = getMetaInfo();
			if(meta != null) {
				meta.delete();
			}
		}
		CoreSpringFactory.getImpl(VersionsManager.class).delete(this, true);
		return deleteBasefile();
	}
	
	private VFSStatus deleteBasefile() {
		VFSStatus status = VFSConstants.NO;
		try {
			Files.delete(getBasefile().toPath());
			status = VFSConstants.YES;
		} catch(IOException e) {
			log.error("Cannot delete base file: " + this, e);
		}
		return status;
	}

	@Override
	public VFSItem resolve(String path) {
		path = VFSManager.sanitizePath(path);
		if (path.equals("/")) return this;
		String name = VFSManager.extractChild(path);
		if (path.equals(name)) {
			return this;
		} else  {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "LFile [file="+getBasefile()+"] ";
	}
}

