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
package org.olat.ims.qti21.model.xml.interactions;

import static org.olat.ims.qti21.model.xml.AssessmentItemFactory.appendDefaultItemBody;
import static org.olat.ims.qti21.model.xml.AssessmentItemFactory.appendDefaultOutcomeDeclarations;
import static org.olat.ims.qti21.model.xml.AssessmentItemFactory.appendUploadInteraction;
import static org.olat.ims.qti21.model.xml.AssessmentItemFactory.createResponseProcessing;
import static org.olat.ims.qti21.model.xml.AssessmentItemFactory.createUploadResponseDeclaration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.olat.core.gui.render.StringOutput;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.ims.qti21.model.QTI21QuestionType;
import org.olat.ims.qti21.model.xml.AssessmentItemFactory;

import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.UploadInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

/**
 * 
 * Initial date: 8 nov. 2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class UploadAssessmentItemBuilder extends LobAssessmentItemBuilder {
	
	private static final OLog log = Tracing.createLoggerFor(UploadAssessmentItemBuilder.class);

	private UploadInteraction uploadInteraction;
	
	public UploadAssessmentItemBuilder(String title, QtiSerializer qtiSerializer) {
		super(createAssessmentItem(title), qtiSerializer);
	}
	
	public UploadAssessmentItemBuilder(AssessmentItem assessmentItem, QtiSerializer qtiSerializer) {
		super(assessmentItem, qtiSerializer);
	}
	
	private static AssessmentItem createAssessmentItem(String title) {
		AssessmentItem assessmentItem = AssessmentItemFactory.createAssessmentItem(QTI21QuestionType.upload, title);
		
		//define the response
		Identifier responseDeclarationId = Identifier.assumedLegal("RESPONSE_1");
		ResponseDeclaration responseDeclaration = createUploadResponseDeclaration(assessmentItem, responseDeclarationId);
		assessmentItem.getNodeGroups().getResponseDeclarationGroup().getResponseDeclarations().add(responseDeclaration);
	
		//outcomes
		appendDefaultOutcomeDeclarations(assessmentItem, 1.0d);
		
		ItemBody itemBody = appendDefaultItemBody(assessmentItem);
		appendUploadInteraction(itemBody, responseDeclarationId);
		
		//response processing
		ResponseProcessing responseProcessing = createResponseProcessing(assessmentItem, responseDeclarationId);
		assessmentItem.getNodeGroups().getResponseProcessingGroup().setResponseProcessing(responseProcessing);
		return assessmentItem;
	}
	
	@Override
	public QTI21QuestionType getQuestionType() {
		return QTI21QuestionType.upload;
	}
	
	/**
	 * @return A copy of the list of blocks which make the question.
	 * 		The list is a copy and modification will not be persisted.
	 */
	public List<Block> getQuestionBlocks() {
		List<Block> blocks = assessmentItem.getItemBody().getBlocks();
		List<Block> questionBlocks = new ArrayList<>(blocks.size());
		for(Block block:blocks) {
			if(block instanceof UploadInteraction) {
				break;
			} else {
				questionBlocks.add(block);
			}
		}
		return questionBlocks;
	}
	
	@Override
	public void extract() {
		super.extract();
		extractExtendedTextInteraction();
	}
	
	private void extractExtendedTextInteraction() {
		try(StringOutput sb = new StringOutput()) {
			List<Block> blocks = assessmentItem.getItemBody().getBlocks();
			for(Block block:blocks) {
				if(block instanceof UploadInteraction) {
					uploadInteraction = (UploadInteraction)block;
					responseIdentifier = uploadInteraction.getResponseIdentifier();
					break;
				} else {
					serializeJqtiObject(block, sb);
				}
			}
			question = sb.toString();
		} catch(IOException e) {
			log.error("", e);
		}
	}

	@Override
	protected void buildResponseAndOutcomeDeclarations() {
		ResponseDeclaration responseDeclaration =
				createUploadResponseDeclaration(assessmentItem, responseIdentifier);
		assessmentItem.getResponseDeclarations().add(responseDeclaration);
	}
	
	@Override
	protected void buildItemBody() {
		//remove current blocks
		List<Block> blocks = assessmentItem.getItemBody().getBlocks();
		blocks.clear();

		//add question
		getHtmlHelper().appendHtml(assessmentItem.getItemBody(), question);
		
		//add interaction
		blocks.add(uploadInteraction);
	}
}