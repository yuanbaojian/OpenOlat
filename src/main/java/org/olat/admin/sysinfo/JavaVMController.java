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
package org.olat.admin.sysinfo;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.segmentedview.SegmentViewComponent;
import org.olat.core.gui.components.segmentedview.SegmentViewEvent;
import org.olat.core.gui.components.segmentedview.SegmentViewFactory;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;

/**
 * 
 * Initial date: 16.11.2012<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class JavaVMController extends BasicController {
	
	private final Link memoryLink, threadsLink, envPropsLink;
	private final SegmentViewComponent segmentView;
	private final VelocityContainer mainVC;
	
	private JavaMemoryController memoryCtrl;
	private JavaThreadsController threadsCtrl;
	private JavaEnvironmmentPropertiesController envPropsCtrl;
	
	public JavaVMController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);

		mainVC = createVelocityContainer("segments");
		
		segmentView = SegmentViewFactory.createSegmentView("segments", mainVC, this);
		memoryLink = LinkFactory.createLink("java.memory", mainVC, this);
		segmentView.addSegment(memoryLink, true);
		
		threadsLink = LinkFactory.createLink("java.threads", mainVC, this);
		segmentView.addSegment(threadsLink, false);

		envPropsLink = LinkFactory.createLink("java.envProps", mainVC, this);
		segmentView.addSegment(envPropsLink, false);
		
		mainVC.put("segments", segmentView);
		doOpenMemory(ureq);
		putInitialPanel(mainVC);
	}
	
	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if(source == segmentView) {
			if(event instanceof SegmentViewEvent) {
				SegmentViewEvent sve = (SegmentViewEvent)event;
				String segmentCName = sve.getComponentName();
				Component clickedLink = mainVC.getComponent(segmentCName);
				if (clickedLink == memoryLink) {
					doOpenMemory(ureq);
				} else if (clickedLink == threadsLink) {
					doOpenThreads(ureq);
				} else if (clickedLink == envPropsLink) {
					doOpenEnvProps(ureq);
				}
			}
		}
	}
	
	private void doOpenMemory(UserRequest ureq) {
		if(memoryCtrl == null) {
			memoryCtrl = new JavaMemoryController(ureq, getWindowControl());
			listenTo(memoryCtrl);
		}
		mainVC.put("segmentCmp", memoryCtrl.getInitialComponent());
	}

	private void doOpenThreads(UserRequest ureq) {
		if(threadsCtrl == null) {
			threadsCtrl = new JavaThreadsController(ureq, getWindowControl());
			listenTo(threadsCtrl);
		}
		mainVC.put("segmentCmp", threadsCtrl.getInitialComponent());
	}

	private void doOpenEnvProps(UserRequest ureq) {
		if(envPropsCtrl == null) {
			envPropsCtrl = new JavaEnvironmmentPropertiesController(ureq, getWindowControl());
			listenTo(envPropsCtrl);
		}
		mainVC.put("segmentCmp", envPropsCtrl.getInitialComponent());
	}
}
