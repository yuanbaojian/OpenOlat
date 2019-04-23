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
package org.olat.ims.qti21.model;

import java.util.ArrayList;
import java.util.List;

import org.olat.basesecurity.Group;
import org.olat.core.id.Identity;
import org.olat.course.nodes.ArchiveOptions;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 24.07.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class QTI21StatisticSearchParams {

	private final String nodeIdent;
	private final RepositoryEntry courseEntry;
	private final RepositoryEntry testEntry;

	private List<Group> limitToGroups;
	private List<Identity> limitToIdentities;
	
	private ArchiveOptions archiveOptions;
	
	private boolean viewAnonymUsers;
	private boolean viewAllUsers;
	private boolean viewNonMembers;
	
	public QTI21StatisticSearchParams(ArchiveOptions options, RepositoryEntry testEntry, RepositoryEntry courseEntry, String nodeIdent) {
		this.testEntry = testEntry;
		this.courseEntry = courseEntry;
		this.nodeIdent = nodeIdent;
		this.archiveOptions = options;
		
		if(options == null) {
			viewAnonymUsers = true;
			viewAllUsers = true;
		} else if(options.getGroup() != null) {
			limitToGroups = new ArrayList<>(2);
			limitToGroups.add(options.getGroup().getBaseGroup());
		} else if(options.getIdentities() != null) {
			limitToIdentities = new ArrayList<>(options.getIdentities());
		} else {
			viewAnonymUsers = true;
			viewAllUsers = true;
		}
	}
	
	public QTI21StatisticSearchParams(RepositoryEntry testEntry, RepositoryEntry courseEntry, String nodeIdent) {
		this.nodeIdent = nodeIdent;
		this.courseEntry = courseEntry;
		this.testEntry = testEntry;
	}
	
	public QTI21StatisticSearchParams(RepositoryEntry testEntry, RepositoryEntry courseEntry, String nodeIdent,
			boolean viewAllUsers, boolean viewAnonymUsers) {
		this.nodeIdent = nodeIdent;
		this.courseEntry = courseEntry;
		this.testEntry = testEntry;
		this.viewAllUsers = viewAllUsers;
		this.viewAnonymUsers = viewAnonymUsers;
	}
	
	public RepositoryEntry getTestEntry() {
		return testEntry;
	}
	
	public RepositoryEntry getCourseEntry() {
		return courseEntry;
	}

	public String getNodeIdent() {
		return nodeIdent;
	}

	public List<Group> getLimitToGroups() {
		return limitToGroups;
	}
	
	public void setLimitToGroups(List<Group> limitToGroups) {
		this.limitToGroups = limitToGroups;
	}
	
	public List<Identity> getLimitToIdentities() {
		return limitToIdentities;
	}

	public void setLimitToIdentities(List<Identity> limitToIdentities) {
		this.limitToIdentities = limitToIdentities;
	}
	
	public boolean isViewAllUsers() {
		return viewAllUsers;
	}
	
	public void setViewAllUsers(boolean view) {
		this.viewAllUsers = view;
	}

	public boolean isViewNonMembers() {
		return viewNonMembers;
	}

	public void setViewNonMembers(boolean viewNonMembers) {
		this.viewNonMembers = viewNonMembers;
	}

	public boolean isViewAnonymUsers() {
		return viewAnonymUsers;
	}

	public void setViewAnonymUsers(boolean viewAnonymUsers) {
		this.viewAnonymUsers = viewAnonymUsers;
	}

	public ArchiveOptions getArchiveOptions() {
		return archiveOptions;
	}

	public void setArchiveOptions(ArchiveOptions archiveOptions) {
		this.archiveOptions = archiveOptions;
	}
	
	
}
