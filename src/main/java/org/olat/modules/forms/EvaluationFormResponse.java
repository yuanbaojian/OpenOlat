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
package org.olat.modules.forms;

import java.math.BigDecimal;
import java.nio.file.Path;

import org.olat.core.id.CreateInfo;
import org.olat.core.id.ModifiedInfo;

/**
 * 
 * Initial date: 12 déc. 2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public interface EvaluationFormResponse extends CreateInfo, ModifiedInfo {
	
	public Long getKey();
	
	/**
	 * The identifier of the evaluation form part / element. Several responses may
	 * have the same identifier.
	 * 
	 * @return
	 */
	public String getResponseIdentifier();
	
	/**
	 * A response is present but the user did explicitly not answer the question.
	 *
	 * @return
	 */
	public boolean isNoResponse();
	
	public BigDecimal getNumericalResponse();
	
	public String getStringuifiedResponse();
	
	public Path getFileResponse();
	
	/**
	 * The evaluation form session of this response.
	 * 
	 * @return
	 */
	public EvaluationFormSession getSession();

}
