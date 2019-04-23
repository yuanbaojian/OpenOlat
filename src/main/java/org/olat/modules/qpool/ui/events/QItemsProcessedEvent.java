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
package org.olat.modules.qpool.ui.events;

import java.util.List;

import org.olat.core.gui.control.Event;
import org.olat.modules.qpool.QuestionItem;

/**
 * 
 * Initial date: 26.01.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class QItemsProcessedEvent extends Event {

	private static final long serialVersionUID = -1492865526009837012L;
	
	private final List<QuestionItem> successfullItems;
	private final int numberOfItems;
	private final int numberOfFails;

	public QItemsProcessedEvent(List<QuestionItem> successfullItems, int numberOftems) {
		this(successfullItems, numberOftems, 0);
	}
	
	public QItemsProcessedEvent(List<QuestionItem> successfullItems, int numberOftems, int numberOfFails) {
		super("qitems-processed");
		this.successfullItems = successfullItems;
		this.numberOfItems = numberOftems;
		this.numberOfFails = numberOfFails;
	}

	public List<QuestionItem> getSuccessfullItems() {
		return successfullItems;
	}

	public int getNumberOfItems() {
		return numberOfItems;
	}

	public int getNumberOfFails() {
		return numberOfFails;
	}

}
