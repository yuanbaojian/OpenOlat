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
package org.olat.modules.curriculum.model;

import org.olat.core.gui.control.Event;
import org.olat.core.id.Identity;
import org.olat.modules.curriculum.CurriculumElement;

/**
 * 
 * Initial date: 8 juin 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class CurriculumElementMembershipChange extends Event {
	private static final long serialVersionUID = 8499004967313689825L;

	private final Identity member;
	private CurriculumElement element;
	private final Long groupKey;

	private Boolean curriculumManager;
	private Boolean repositoryEntryOwner;
	private Boolean coach;
	private Boolean participant;
	
	public CurriculumElementMembershipChange(Identity member, CurriculumElement element) {
		this(member, element.getKey());
		this.element = element;
	}
	
	public CurriculumElementMembershipChange(Identity member, CurriculumElementMembershipChange origin) {
		this(member, origin.getElement());
		curriculumManager = origin.curriculumManager;
		repositoryEntryOwner = origin.repositoryEntryOwner;
		participant = origin.participant;
		coach = origin.coach;
	}
	
	public CurriculumElementMembershipChange(Identity member, Long groupKey) {
		super("id-perm-changed");
		this.groupKey = groupKey;
		this.member = member;
	}

	public CurriculumElement getElement() {
		return element;
	}
	
	public Long getGroupKey() {
		return groupKey;
	}

	public Identity getMember() {
		return member;
	}

	public Boolean getParticipant() {
		return participant;
	}

	public void setParticipant(Boolean participant) {
		this.participant = participant;
	}

	public Boolean getCurriculumManager() {
		return curriculumManager;
	}

	public void setCurriculumManager(Boolean curriculumManager) {
		this.curriculumManager = curriculumManager;
	}

	public Boolean getRepositoryEntryOwner() {
		return repositoryEntryOwner;
	}

	public void setRepositoryEntryOwner(Boolean repositoryEntryOwner) {
		this.repositoryEntryOwner = repositoryEntryOwner;
	}

	public Boolean getCoach() {
		return coach;
	}

	public void setCoach(Boolean coach) {
		this.coach = coach;
	}




}
