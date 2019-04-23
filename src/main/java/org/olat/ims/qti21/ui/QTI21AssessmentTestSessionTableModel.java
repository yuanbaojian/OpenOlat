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
package org.olat.ims.qti21.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.olat.core.gui.components.form.flexible.impl.elements.table.DefaultFlexiTableDataModel;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiColumnDef;
import org.olat.core.gui.components.form.flexible.impl.elements.table.FlexiTableColumnModel;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Formatter;
import org.olat.course.assessment.AssessmentHelper;
import org.olat.ims.qti21.AssessmentTestSession;
import org.olat.ims.qti21.ui.QTI21AssessmentDetailsController.AssessmentTestSessionDetailsComparator;

/**
 * 
 * Initial date: 21.05.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class QTI21AssessmentTestSessionTableModel extends DefaultFlexiTableDataModel<QTI21AssessmentTestSessionDetails> {
	
	private final Translator translator;
	private AssessmentTestSession lastSession;
	
	public QTI21AssessmentTestSessionTableModel(FlexiTableColumnModel columnModel, Translator translator) {
		super(columnModel);
		this.translator = translator;
	}

	@Override
	public DefaultFlexiTableDataModel<QTI21AssessmentTestSessionDetails> createCopyWithEmptyList() {
		return new QTI21AssessmentTestSessionTableModel(getTableColumnModel(), translator);
	}

	@Override
	public Object getValueAt(int row, int col) {
		QTI21AssessmentTestSessionDetails session = getObject(row);

		switch(TSCols.values()[col]) {
			case terminationTime: {
				Date endTime = session.getTestSession().getTerminationTime();
				if(endTime == null) {
					endTime = session.getTestSession().getFinishTime();
				}
				return endTime;
			}
			case lastModified: return session.getTestSession().getLastModified();
			case duration: {
				if(session.getTestSession().getFinishTime() != null) {
					return Formatter.formatDuration(session.getTestSession().getDuration().longValue());
				}
				return "<span class='o_ochre'>" + translator.translate("assessment.test.open") + "</span>";
			}
			case test: return session.getTestSession().getTestEntry().getDisplayname();
			case numOfItemSessions: {
				return session.getNumOfItems();
			}
			case responded: {
				return session.getNumOfItemsResponded();
			}
			case corrected: {
				return session.getNumOfItemsCorrected();
			}
			case score: {
				if(session.getTestSession().getFinishTime() != null) {
					return AssessmentHelper.getRoundedScore(session.getTestSession().getScore());
				}
				return "<span class='o_ochre'>" + translator.translate("assessment.test.notReleased") + "</span>";
			}
			case manualScore: {
				if(session.getTestSession().getFinishTime() != null) {
					return AssessmentHelper.getRoundedScore(session.getTestSession().getManualScore());
				}
				return "";
			}
			case finalScore: {
				if(session.getTestSession().getFinishTime() != null) {
					BigDecimal finalScore = session.getTestSession().getFinalScore();
					return AssessmentHelper.getRoundedScore(finalScore);
				}
				return "";
			}
			case open: {
				Date finished = session.getTestSession().getFinishTime();
				return finished == null ? Boolean.FALSE : Boolean.TRUE;
			}
			case correction: return (lastSession != null && lastSession.equals(session.getTestSession()));
			case tools: return session.getToolsLink();
			default: return "ERROR";
		}
	}

	@Override
	public void setObjects(List<QTI21AssessmentTestSessionDetails> objects) {
		super.setObjects(objects);
		
		List<QTI21AssessmentTestSessionDetails> sessions = new ArrayList<>(objects);
		Collections.sort(sessions, new AssessmentTestSessionDetailsComparator());
		if(sessions.size() > 0) {
			lastSession = sessions.get(0).getTestSession();
		}
	}

	public enum TSCols implements FlexiColumnDef {
		terminationTime("table.header.terminationTime"),
		lastModified("table.header.lastModified"),
		duration("table.header.duration"),
		test("table.header.test"),
		numOfItemSessions("table.header.itemSessions"),
		responded("table.header.responded"),
		corrected("table.header.corrected"),
		score("table.header.score"),
		manualScore("table.header.manualScore"),
		finalScore("table.header.finalScore"),
		open("table.header.action"),
		correction("table.header.correction"),
		tools("table.header.tools");
		
		private final String i18nKey;
		
		private TSCols(String i18nKey) {
			this.i18nKey = i18nKey;
		}
		
		@Override
		public String i18nHeaderKey() {
			return i18nKey;
		}
	}
}
