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
package org.olat.core.commons.modules.bc;

import java.util.List;

import org.olat.core.CoreSpringFactory;
import org.olat.core.id.Identity;
import org.olat.core.id.Roles;
import org.olat.core.util.vfs.MergeSource;
import org.olat.core.util.vfs.NamedContainerImpl;
import org.olat.core.util.vfs.Quota;
import org.olat.core.util.vfs.QuotaManager;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSItem;
import org.olat.core.util.vfs.VFSManager;
import org.olat.core.util.vfs.callbacks.FullAccessWithQuotaCallback;
import org.olat.core.util.vfs.callbacks.VFSSecurityCallback;
import org.olat.core.util.vfs.filters.VFSItemFilter;
import org.olat.user.PersonalFolderManager;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class BriefcaseWebDAVMergeSource  extends MergeSource {
	private boolean init = false;
	private final Roles roles;
	private final Identity identity;
		
	public BriefcaseWebDAVMergeSource(Identity identity, Roles roles) {
		this(identity, roles, identity.getName());
	}
	
	public BriefcaseWebDAVMergeSource(Identity identity, Roles roles, String name) {
		super(null, name);
		this.roles = roles;
		this.identity = identity;
	}

	@Override
	public List<VFSItem> getItems() {
		if(!init) {
			init();
		}
		return super.getItems();
	}

	@Override
	public List<VFSItem> getItems(VFSItemFilter filter) {
		if(!init) {
			init();
		}
		return super.getItems(filter);
	}

	@Override
	public VFSItem resolve(String path) {
		if(!init) {
			init();
		}
		return super.resolve(path);
	}
	
	@Override
	public VFSSecurityCallback getLocalSecurityCallback() {
		if(super.getLocalSecurityCallback() == null) {
			//set quota for this merge source
			QuotaManager qm = CoreSpringFactory.getImpl(QuotaManager.class);
			String path = PersonalFolderManager.getRootPathFor(identity);
			Quota quota = qm.getCustomQuotaOrDefaultDependingOnRole(identity, roles, path);
			setLocalSecurityCallback(new FullAccessWithQuotaCallback(quota));
		}
		return super.getLocalSecurityCallback();
	}

	@Override
	protected void init() {
		super.init();
		// mount /public
		String rootPath = PersonalFolderManager.getRootPathFor(identity);
		VFSContainer vfsPublic = VFSManager.olatRootContainer(rootPath + "/public", this);
		//vfsPublic.getBasefile().mkdirs(); // lazy initialize folders
		// we do a little trick here and wrap it again in a NamedContainerImpl so
		// it doesn't show up as a OlatRootFolderImpl to prevent it from editing its MetaData
		VFSContainer vfsNamedPublic = new NamedContainerImpl("public", vfsPublic);
		addContainer(vfsNamedPublic);
		
		// mount /private
		VFSContainer vfsPrivate = VFSManager.olatRootContainer(rootPath + "/private", this);
		//vfsPrivate.getBasefile().mkdirs(); // lazy initialize folders
		// we do a little trick here and wrap it again in a NamedContainerImpl so
		// it doesn't show up as a OlatRootFolderImpl to prevent it from editing its MetaData
		VFSContainer vfsNamedPrivate = new NamedContainerImpl("private", vfsPrivate);
		addContainer(vfsNamedPrivate);
		
		init = true;
	}
}