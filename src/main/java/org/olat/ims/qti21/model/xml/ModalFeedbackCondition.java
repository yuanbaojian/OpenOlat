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
package org.olat.ims.qti21.model.xml;

/**
 * 
 * Initial date: 31 août 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class ModalFeedbackCondition {
	
	private Variable variable;
	private Operator operator;
	private String value;

	public ModalFeedbackCondition() {
		//
	}
	
	public ModalFeedbackCondition(Variable variable, Operator operator, String value) {
		this.variable = variable;
		this.operator = operator;
		this.value = value;
	}
	
	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public enum Variable {
		score,
		attempts,
		response
	}
	
	public enum Operator {
		bigger,
		biggerEquals,
		equals,
		notEquals,
		smaller,
		smallerEquals
	}
}
