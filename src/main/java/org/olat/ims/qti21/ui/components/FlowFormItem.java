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
package org.olat.ims.qti21.ui.components;

import java.io.File;
import java.util.List;

import org.olat.core.gui.UserRequest;

import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.content.basic.InlineStatic;

/**
 * 
 * Initial date: 10 juil. 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class FlowFormItem extends AssessmentObjectFormItem {
	
	private final FlowComponent component;

	public FlowFormItem(String name, File assessmentItemFile) {
		super(name, null);
		component = new FlowComponent(name, assessmentItemFile, this);
	}
	
	public List<Block> getBlocks() {
		return component.getBlocks();
	}

	public void setBlocks(List<Block> blocks) {
		component.setBlocks(blocks);
	}
	
	public List<FlowStatic> getFlowStatics() {
		return component.getFlowStatics();
	}

	public void setFlowStatics(List<FlowStatic> flowStatics) {
		component.setFlowStatics(flowStatics);
	}
	
	public List<InlineStatic> getInlineStatics() {
		return component.getInlineStatics();
	}

	public void setInlineStatics(List<InlineStatic> inlineStatics) {
		component.setInlineStatics(inlineStatics);
	}

	@Override
	public FlowComponent getComponent() {
		return component;
	}

	@Override
	protected FlowComponent getFormItemComponent() {
		return component;
	}

	@Override
	protected void rootFormAvailable() {
		// 
	}

	@Override
	public void evalFormRequest(UserRequest ureq) {
		//
	}

	@Override
	public void reset() {
		//
	}
	
	
}
