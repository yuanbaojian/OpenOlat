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
package org.olat.modules.coach.ui;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.olat.core.commons.persistence.SortKey;
import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiTableDataModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiSortableColumnDef;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SortableFlexiTableDataModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.SortableFlexiTableModelDelegate;
import org.olat.course.assessment.UserEfficiencyStatement;
import org.olat.course.certificate.CertificateLight;
import org.olat.modules.coach.model.EfficiencyStatementEntry;
import org.olat.modules.coach.model.IdentityRepositoryEntryKey;
import org.olat.modules.coach.model.IdentityResourceKey;
import org.olat.modules.lecture.model.LectureBlockStatistics;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Description:<br>
 * 
 * <P>
 * Initial Date:  8 févr. 2012 <br>
 *
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class EfficiencyStatementEntryTableDataModel extends DefaultFlexiTableDataModel<EfficiencyStatementEntry> implements SortableFlexiTableDataModel<EfficiencyStatementEntry> {
	
	private ConcurrentMap<IdentityResourceKey, CertificateLight> certificateMap;
	private ConcurrentMap<IdentityRepositoryEntryKey, LectureBlockStatistics> lecturesStatisticsMap;
	
	public EfficiencyStatementEntryTableDataModel(FlexiTableColumnModel columnModel) {
		super(columnModel);
	}
	
	public boolean contains(IdentityResourceKey key) {
		return certificateMap != null && certificateMap.containsKey(key);
	}
	
	public void putCertificate(CertificateLight certificate) {
		if(certificateMap != null && certificate != null) {
			IdentityResourceKey key = new IdentityResourceKey(certificate.getIdentityKey(), certificate.getOlatResourceKey());
			certificateMap.put(key, certificate);
		}
	}

	@Override
	public void sort(SortKey orderBy) {
		setObjects(new SortableFlexiTableModelDelegate<>(orderBy, this, null).sort());
	}
	@Override
	public Object getValueAt(int row, int col) {
		EfficiencyStatementEntry entry = getObject(row);
		return getValueAt(entry, col);
	}
	
	@Override
	public Object getValueAt(EfficiencyStatementEntry entry, int col) {

		if(col >= 0 && col < Columns.values().length) {
			switch(Columns.getValueAt(col)) {
				case name: return entry.getIdentityName();
				case repoName: {
					RepositoryEntry re = entry.getCourse();
					return re.getDisplayname();
				}
				case score: {
					UserEfficiencyStatement s = entry.getUserEfficencyStatement();
					return s == null ? null : s.getScore();
				}
				case passed: {
					UserEfficiencyStatement s = entry.getUserEfficencyStatement();
					return s == null ? null : s.getPassed();
				}
				case certificate: {
					CertificateLight certificate = getCertificate(entry);
					return certificate;
				}
				case recertification: {
					CertificateLight certificate = getCertificate(entry);
					return certificate == null ? null : certificate.getNextRecertificationDate();
				}
				case progress: {
					UserEfficiencyStatement s = entry.getUserEfficencyStatement();
					if(s == null || s.getTotalNodes() == null) {
						ProgressValue val = new ProgressValue();
						val.setTotal(100);
						val.setGreen(0);
						return val;
					}
					
					ProgressValue val = new ProgressValue();
					val.setTotal(s.getTotalNodes().intValue());
					val.setGreen(s.getAttemptedNodes() == null ? 0 : s.getAttemptedNodes().intValue());
					return val;
				}
				case lastModification: {
					UserEfficiencyStatement s = entry.getUserEfficencyStatement();
					return s == null ? null : s.getLastModified();
				}
				case lastUserModified: {
					UserEfficiencyStatement s = entry.getUserEfficencyStatement();
					return s == null ? null : s.getLastUserModified();
				}
				case lastCoachModified: {
					UserEfficiencyStatement s = entry.getUserEfficencyStatement();
					return s == null ? null : s.getLastCoachModified();
				}
				case plannedLectures: {
					LectureBlockStatistics statistics = getLectureBlockStatistics(entry);
					return statistics == null ? null : statistics.getTotalPersonalPlannedLectures();
				}
				case attendedLectures: {
					LectureBlockStatistics statistics = getLectureBlockStatistics(entry);
					return statistics == null ? null : statistics.getTotalAttendedLectures();
				}
				case unauthorizedAbsenceLectures:
				case absentLectures: {
					LectureBlockStatistics statistics = getLectureBlockStatistics(entry);
					return statistics == null ? null : statistics.getTotalAbsentLectures();
				}
				case authorizedAbsenceLectures: {
					LectureBlockStatistics statistics = getLectureBlockStatistics(entry);
					return statistics == null ? null : statistics.getTotalAuthorizedAbsentLectures();
				}
			}
		}
		
		int propPos = col - UserListController.USER_PROPS_OFFSET;
		return entry.getIdentityProp(propPos);
	}
	
	private CertificateLight getCertificate(EfficiencyStatementEntry entry) {
		if(certificateMap != null) {
			IdentityResourceKey key = new IdentityResourceKey(entry.getIdentityKey(), entry.getCourse().getOlatResource().getKey());
			return certificateMap.get(key);
		}
		return null;
	}
	
	private LectureBlockStatistics getLectureBlockStatistics(EfficiencyStatementEntry entry) {
		if(lecturesStatisticsMap != null) {
			IdentityRepositoryEntryKey key = new IdentityRepositoryEntryKey(entry);
			return lecturesStatisticsMap.get(key);
		}
		return null;
	}

	public void setObjects(List<EfficiencyStatementEntry> objects, ConcurrentMap<IdentityResourceKey, CertificateLight> certificates) {
		setObjects(objects, certificates, null);
	}
	
	public void setObjects(List<EfficiencyStatementEntry> objects,
			ConcurrentMap<IdentityResourceKey, CertificateLight> certificates,
			ConcurrentMap<IdentityRepositoryEntryKey, LectureBlockStatistics> lecturesStatisticsMap) {
		setObjects(objects);
		this.certificateMap = certificates;
		this.lecturesStatisticsMap = lecturesStatisticsMap;
	}

	@Override
	public EfficiencyStatementEntryTableDataModel createCopyWithEmptyList() {
		return new EfficiencyStatementEntryTableDataModel(getTableColumnModel());
	}
	
	public enum Columns implements FlexiSortableColumnDef {
		name("student.name"), 
		repoName("table.header.course.name"),
		score("table.header.score"),
		passed("table.header.passed"),
		certificate("table.header.certificate"),
		recertification("table.header.recertification"),
		progress("table.header.progress"),
		lastModification("table.header.lastScoreDate"),
		lastUserModified("table.header.lastUserModificationDate"),
		lastCoachModified("table.header.lastCoachModificationDate"),
		plannedLectures("table.header.planned.lectures"),
		attendedLectures("table.header.attended.lectures"),
		absentLectures("table.header.absent.lectures"),
		unauthorizedAbsenceLectures("table.header.unauthorized.absence"),
		authorizedAbsenceLectures("table.header.authorized.absence");
		
		private final String i18nKey;
		
		private Columns(String i18nKey) {
			this.i18nKey = i18nKey;
		}
		
		@Override
		public String i18nHeaderKey() {
			return i18nKey;
		}

		@Override
		public boolean sortable() {
			return true;
		}

		@Override
		public String sortKey() {
			return name();
		}

		public static Columns getValueAt(int ordinal) {
			if(ordinal >= 0 && ordinal < values().length) {
				return values()[ordinal];
			}
			return name;
		}
	}
}
