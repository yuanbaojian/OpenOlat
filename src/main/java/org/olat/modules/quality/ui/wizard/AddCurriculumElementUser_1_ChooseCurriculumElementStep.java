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
package org.olat.modules.quality.ui.wizard;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.BasicStep;
import org.olat.core.gui.control.generic.wizard.PrevNextFinishConfig;
import org.olat.core.gui.control.generic.wizard.StepFormController;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.core.id.OrganisationRef;
import org.olat.core.util.Util;
import org.olat.modules.quality.ui.ParticipationListController;

/**
 * 
 * Initial date: 02.07.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class AddCurriculumElementUser_1_ChooseCurriculumElementStep extends BasicStep {
	
	private final List<? extends OrganisationRef> organisationRefs;

	public AddCurriculumElementUser_1_ChooseCurriculumElementStep(UserRequest ureq,
			List<? extends OrganisationRef> organisationRefs) {
		super(ureq);
		this.organisationRefs = organisationRefs;
		setNextStep(new AddCurriculumElementUser_2_ChooseRolesStep(ureq));
		setTranslator(Util.createPackageTranslator(ParticipationListController.class, getLocale(), getTranslator()));
		setI18nTitleAndDescr("participation.user.curele.add.choose.curele.title", "participation.user.curele.add.choose.curele.title");
	}

	@Override
	public PrevNextFinishConfig getInitialPrevNextFinishConfig() {
		return new PrevNextFinishConfig(false, true, false);
	}

	@Override
	public StepFormController getStepController(UserRequest ureq, WindowControl wControl, StepsRunContext runContext, Form form) {
		if(!runContext.containsKey("context")) {
			runContext.put("context", new CurriculumElementContext());
		}
		return new AddCurriculumElementUserSelectionController(ureq, wControl, form, runContext, organisationRefs);
	}
}