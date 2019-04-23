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
*/

package org.olat.course.nodes.fo;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.DefaultController;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Util;
import org.olat.course.run.userview.NodeEvaluation;

/**
 * Description: <br>
 * Initial Date: 10.02.2005 <br>
 * @author Mike Stock
 */
public class FOPreviewController extends DefaultController {
	private static final String VELOCITY_ROOT = Util.getPackageVelocityRoot(FOPreviewController.class);

	private Translator trans;
	private VelocityContainer previewVC;

	/**
	 * @param ureq
	 * @param wControl
	 * @param node
	 * @param ne
	 */
	public FOPreviewController(UserRequest ureq, WindowControl wControl, NodeEvaluation ne) {
		super(wControl);
		trans = Util.createPackageTranslator(FOPreviewController.class, ureq.getLocale());
		previewVC = new VelocityContainer("foPreviewVC", VELOCITY_ROOT + "/preview.html", trans, this);
		previewVC.contextPut("canRead", Boolean.valueOf(ne.isCapabilityAccessible("reader")));
		previewVC.contextPut("canPost", Boolean.valueOf(ne.isCapabilityAccessible("poster")));
		previewVC.contextPut("canModerate", Boolean.valueOf(ne.isCapabilityAccessible("moderator")));
		setInitialComponent(previewVC);
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#event(org.olat.core.gui.UserRequest,
	 *      org.olat.core.gui.components.Component, org.olat.core.gui.control.Event)
	 */
	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		//
	}

	/**
	 * @see org.olat.core.gui.control.DefaultController#doDispose(boolean)
	 */
	@Override
	protected void doDispose() {
		//
	}
}
