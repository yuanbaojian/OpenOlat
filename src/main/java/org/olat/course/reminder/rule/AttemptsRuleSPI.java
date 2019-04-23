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
package org.olat.course.reminder.rule;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.olat.core.id.Identity;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.course.CourseFactory;
import org.olat.course.ICourse;
import org.olat.course.export.CourseEnvironmentMapper;
import org.olat.course.nodes.CourseNode;
import org.olat.course.reminder.manager.ReminderRuleDAO;
import org.olat.course.reminder.ui.AttemptsRuleEditor;
import org.olat.modules.reminder.FilterRuleSPI;
import org.olat.modules.reminder.ReminderRule;
import org.olat.modules.reminder.RuleEditorFragment;
import org.olat.modules.reminder.model.ReminderRuleImpl;
import org.olat.repository.RepositoryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 09.04.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class AttemptsRuleSPI implements FilterRuleSPI {
	
	private static final OLog log = Tracing.createLoggerFor(AttemptsRuleSPI.class);
	
	@Autowired
	private ReminderRuleDAO helperDao;

	@Override
	public String getLabelI18nKey() {
		return "rule.attempts";
	}

	@Override
	public String getCategory() {
		return "assessment";
	}

	@Override
	public RuleEditorFragment getEditorFragment(ReminderRule rule, RepositoryEntry entry) {
		return new AttemptsRuleEditor(rule, entry);
	}
	
	@Override
	public ReminderRule clone(ReminderRule rule, CourseEnvironmentMapper envMapper) {
		//the node ident must be the same
		return rule.clone();
	}

	@Override
	public void filter(RepositoryEntry entry, List<Identity> identities, ReminderRule rule) {
		if(rule instanceof ReminderRuleImpl) {
			ReminderRuleImpl r = (ReminderRuleImpl)rule;
			String nodeIdent = r.getLeftOperand();
			String operator = r.getOperator();
			int value = Integer.parseInt(r.getRightOperand());
			
			ICourse course = CourseFactory.loadCourse(entry);
			CourseNode courseNode = course.getRunStructure().getNode(nodeIdent);
			if (courseNode == null) {
				identities.clear();
				log.error("Attempts rule in course " + entry.getKey() + " (" + entry.getDisplayname() + ") is missing a course element");
				return;
			}

			Map<Long, Integer> attempts = helperDao.getAttempts(entry, courseNode, identities);
			
			for(Iterator<Identity> identityIt=identities.iterator(); identityIt.hasNext(); ) {
				Identity identity = identityIt.next();
				Integer attempt = attempts.get(identity.getKey());
				if(attempt == null) {
					attempt = 0;
				}
				if(!evaluateAttempt(attempt.intValue(), operator, value)) {
					identityIt.remove();
				}
			}
		}
	}
	
	private boolean evaluateAttempt(int attempt, String operator, int value) {
		boolean eval = false;
		switch(operator) {
			case "<": eval = attempt < value; break;
			case "<=": eval = attempt <= value; break;
			case "=": eval = attempt == value; break;
			case "=>": eval = attempt >= value; break;
			case ">": eval = attempt > value;  break;
			case "!=": eval = attempt != value; break;
		}
		return eval;
	}
}
