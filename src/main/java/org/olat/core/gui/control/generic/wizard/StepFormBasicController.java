/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.  
* <p>
*/
package org.olat.core.gui.control.generic.wizard;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;

/**
 * @author patrickb
 *
 */
public abstract class StepFormBasicController extends FormBasicController implements
		StepFormController {
	
	private StepsRunContext runContext;
	private boolean usedInStepWizzard = true;
	
	/**
	 * 
	 * @param ureq
	 * @param wControl
	 * @param rootForm
	 * @param runContext
	 * @param layout The layout used as form layouter container. Use the public
	 *          static variables of this class LAYOUT_DEFAULT, LAYOUT_HORIZONTAL
	 *          and LAYOUT_VERTICAL
	 * @param customLayoutPageName The page name if layout is set to LAYOUT_CUSTOM
	 */
	public StepFormBasicController(UserRequest ureq, WindowControl wControl, Form rootForm, StepsRunContext runContext, int layout, String customLayoutPageName){
		super(ureq, wControl, layout, customLayoutPageName, rootForm);
		this.runContext = runContext;
	}
	
	/**
	 * @param ureq
	 * @param wControl
	 * @param pageName
	 */
	public StepFormBasicController(UserRequest ureq, WindowControl wControl, String pageName) {
		super(ureq, wControl, pageName);
		usedInStepWizzard = false;
		runContext = null;
	}
	
	/**
	 * @param ureq
	 * @param wControl
	 * @param pageName
	 */
	public StepFormBasicController(UserRequest ureq, WindowControl wControl, int layout) {
		super(ureq, wControl, layout);
		usedInStepWizzard = false;
		runContext = null;
	}

	/**
	 * @param ureq
	 * @param wControl
	 */
	public StepFormBasicController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		usedInStepWizzard = false;
		runContext = null;
	}

	protected void addToRunContext(String key, Object value){
		runContext.put(key, value);
	}
	protected boolean containsRunContextKey(String key){
		return runContext.containsKey(key);
	}
	protected Object getFromRunContext(String key){
		return runContext.get(key);
	}
	
	public void back() {
		
	}
	
	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		if (source == mainForm.getInitialComponent()) {
			// general form events
			if (event == org.olat.core.gui.components.form.Form.EVNT_VALIDATION_OK) {
				formOK(ureq);
				// set container dirty to remove potentially rendered error messages
				this.flc.setDirty(true);
			} else if (event == org.olat.core.gui.components.form.Form.EVNT_VALIDATION_NEXT) {
				formNext(ureq);
				// set container dirty to remove potentially rendered error messages
				this.flc.setDirty(true);
			} else if (event == org.olat.core.gui.components.form.Form.EVNT_VALIDATION_FINISH) {
				formFinish(ureq);
				// set container dirty to remove potentially rendered error messages
				this.flc.setDirty(true);
			} else if (event == org.olat.core.gui.components.form.Form.EVNT_VALIDATION_NOK) {
				formNOK(ureq);
				// set container dirty to rendered error messages
				this.flc.setDirty(true);
			} else if (event == FormEvent.RESET) {
				formResetted(ureq);
				// set container dirty to render everything from scratch, remove error messages
				this.flc.setDirty(true);
			} else if (event instanceof FormEvent) {
				/*
				 * evaluate inner form events
				 */
				FormEvent fe = (FormEvent) event;
				FormItem fiSrc = fe.getFormItemSource();
				//
				formInnerEvent(ureq, fiSrc, fe);
				// no need to set container dirty, up to controller code if something is dirty
			}
		}
	}
	
	abstract protected void doDispose();

	abstract protected void formOK(UserRequest ureq);

	/**
	 * @return Returns the usedInStepWizzard.
	 */
	public boolean isUsedInStepWizzard() {
		return usedInStepWizzard;
	}

	abstract protected void initForm(FormItemContainer formLayout, Controller listener,
			UserRequest ureq);

	/* (non-Javadoc)
	 * @see org.olat.core.gui.control.generic.wizard.StepFormController#getStepFormItem()
	 */
	public FormItem getStepFormItem() {
		return flc;
	}


}
