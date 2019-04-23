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
package org.olat.portfolio.ui.artefacts.collect;

import java.util.List;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.BasicStep;
import org.olat.core.gui.control.generic.wizard.PrevNextFinishConfig;
import org.olat.core.gui.control.generic.wizard.StepFormController;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.portfolio.manager.EPFrontendManager;
import org.olat.portfolio.model.artefacts.AbstractArtefact;
import org.olat.portfolio.model.structel.PortfolioStructure;

/**
 * Description:<br>
 * Step to collect reflexion
 * 
 * <P>
 * Initial Date: 28.07.2010 <br>
 * 
 * @author Roman Haag, roman.haag@frentix.com, http://www.frentix.com
 */
public class EPCollectStep03 extends BasicStep {

	private AbstractArtefact artefact;
	private boolean hasNextStep;

	public EPCollectStep03(UserRequest ureq, AbstractArtefact artefact) {
		super(ureq);
		this.artefact = artefact;
		EPFrontendManager ePFMgr = (EPFrontendManager) CoreSpringFactory.getBean("epFrontendManager");
		List<PortfolioStructure> structs = ePFMgr.getStructureElementsForUser(getIdentity());
		this.hasNextStep = false;
		if (structs != null && structs.size() != 0) { 
			setNextStep(new EPCollectStep04(ureq));
			hasNextStep = true;
		} else {
			setNextStep(NOSTEP);
		}
		setI18nTitleAndDescr("step3.description", "step3.short.descr");
	}

	/**
	 * @see org.olat.core.gui.control.generic.wizard.BasicStep#getInitialPrevNextFinishConfig()
	 */
	@Override
	public PrevNextFinishConfig getInitialPrevNextFinishConfig() {
		if (hasNextStep){
			return new PrevNextFinishConfig(true, true, true);
		} else {
			return new PrevNextFinishConfig(true, false, true);
		}
	}

	/**
	 * @see org.olat.core.gui.control.generic.wizard.BasicStep#getStepController(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.control.WindowControl,
	 *      org.olat.core.gui.control.generic.wizard.StepsRunContext,
	 *      org.olat.core.gui.components.form.flexible.impl.Form)
	 */
	@Override
	public StepFormController getStepController(UserRequest ureq, WindowControl windowControl, StepsRunContext stepsRunContext, Form form) {
		StepFormController step = new EPCollectStepForm03(ureq, windowControl, form, stepsRunContext, FormBasicController.LAYOUT_VERTICAL,
				null, artefact);
		return step;
	}

}
