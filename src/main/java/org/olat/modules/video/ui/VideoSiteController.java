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
package org.olat.modules.video.ui;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;

/**
 * This site implements a YouTube stile video library for self-study. 
 * 
 * Initial date: 08.05.2016<br>
 * 
 * @author gnaegi, gnaegi@frentix.com, http://www.frentix.com
 *
 */
public class VideoSiteController extends BasicController implements Activateable2 {

	private final TooledStackedPanel toolbarPanel;
	private VideoListingController videoListingCtr;
	
	
	public VideoSiteController(UserRequest ureq, WindowControl wControl) {		
		super(ureq, wControl);
		
		toolbarPanel = new TooledStackedPanel("videosStackPanel", getTranslator(), this);
		toolbarPanel.setInvisibleCrumb(0); // show root level
		toolbarPanel.setShowCloseLink(true, false);
		toolbarPanel.setToolbarEnabled(false);
		putInitialPanel(toolbarPanel);

		videoListingCtr = new VideoListingController(ureq, wControl, toolbarPanel);
		listenTo(videoListingCtr);
		toolbarPanel.pushController(translate("topnav.video"), videoListingCtr);
	}


	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		// no events to catch
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		// delegate to video listing
		if (videoListingCtr != null) {
			videoListingCtr.activate(ureq, entries, state);						
		}
	}

	@Override
	protected void doDispose() {
		// nothing to dispose
	}

}
