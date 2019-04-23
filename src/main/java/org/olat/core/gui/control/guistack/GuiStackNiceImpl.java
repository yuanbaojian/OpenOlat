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
package org.olat.core.gui.control.guistack;

import java.util.List;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.panel.LayeredPanel;
import org.olat.core.gui.components.panel.Panel;
import org.olat.core.gui.components.panel.SimpleStackedPanel;
import org.olat.core.gui.components.panel.StackedPanel;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.WindowBackOffice;
import org.olat.core.gui.control.generic.closablewrapper.CalloutSettings;
import org.olat.core.gui.control.generic.closablewrapper.CalloutSettings.CalloutOrientation;
import org.olat.core.gui.control.util.ZIndexWrapper;
import org.olat.core.gui.control.winmgr.ScrollTopCommand;
import org.olat.core.gui.render.ValidationResult;
import org.olat.core.util.Util;

/**
 * Description: <br>
 * 
 * @author Felix Jost
 */
public class GuiStackNiceImpl implements GuiStack {
	private static final String VELOCITY_ROOT = Util.getPackageVelocityRoot(GuiStackNiceImpl.class);
	
	private StackedPanel panel;
	private StackedPanel modalPanel;
	private int modalLayers;

	private WindowBackOffice wbo;

	private GuiStackNiceImpl() {
		panel = new SimpleStackedPanel("guistackpanel");
		// Use a layered panel instead of a standard panel to support multiple modal layers
		modalPanel = new LayeredPanel("guistackmodalpanel", 900, 100);
		modalLayers = 0;
		
	}

	/**
	 * @param initialBaseComponent
	 */
	public GuiStackNiceImpl(WindowBackOffice wbo, Component initialBaseComponent) {
		this();
		this.wbo = wbo;
		setContent(initialBaseComponent);
	}

	/**
	 * @see org.olat.core.gui.control.GuiStackHandle#setContent(org.olat.core.gui.components.Component)
	 */
	private void setContent(Component newContent) {
		panel.setContent(newContent);
	}

	/**
	 * 
	 * @param title the title of the modal dialog, can be null
	 * @param content the component to push as modal dialog
	 */
	@Override
	public void pushModalDialog(Component content) {
		wbo.sendCommandTo(new ScrollTopCommand());
		
		// wrap the component into a modal foreground dialog with alpha-blended-background
		final Panel guiMsgPlace = new Panel("guimsgplace_for_modaldialog");
		VelocityContainer inset = new VelocityContainer("inset", VELOCITY_ROOT + "/modalDialog.html", null, null) {
			@Override
			public void validate(UserRequest ureq, ValidationResult vr) {
				super.validate(ureq, vr);
				// just before rendering, we need to tell the windowbackoffice that we are a favorite for accepting gui-messages.
				// the windowbackoffice doesn't know about guimessages, it is only a container that keeps them for one render cycle
				List<ZIndexWrapper> zindexed = wbo.getGuiMessages();
				zindexed.add(new ZIndexWrapper(guiMsgPlace, 10));
			}
		};
		inset.put("cont", content);
		inset.put("guimsgplace", guiMsgPlace);
		int zindex = 900 + (modalLayers * 100) + 5;
		inset.contextPut("zindexoverlay", zindex+1);
		inset.contextPut("zindexshim", zindex);
		inset.contextPut("zindexarea", zindex+5);
		inset.contextPut("zindexextwindows", zindex+50);
		
		
		modalPanel.pushContent(inset);
		// the links in the panel cannot be clicked because of the alpha-blended background over it, but if user chooses own css style ->
		// FIXME:fj:b panel.setEnabled(false) causes effects if there is an image component in the panel -> the component is not dispatched
		// and thus renders inline and wastes the timestamp.
		// Needed:solution (a) a flag (a bit of the mode indicator of the urlbuilder can be used) to indicate that a request always needs to be delivered even 
		// if the component or a parent is not enabled.
		// alternative solution(b): wrap the imagecomponent into a controller and use a mapper
		// alternative solution(c): introduce a flag to the component to say "dispatch always", even if a parent component is not enabled
		//
		// - solution a would be easy, but would allow for forced dispatching by manipulating the url's flag.
		// for e.g. a Link button ("make me admin") that is disabled this is a security breach.
		// - solution b needs some wrapping, the advantage (for images) would be that they are cached by the browser if requested more than once
		// within a controller
		// - solution c is a safe and easy way to allow dispatching (only in case a mediaresource is returned as a result of the dispatching) even
		// if parent elements are not enabled
		
		// proposal: fix for 5.1.0 with solution c; for 5.0.1 the uncommenting of the line below is okay.
		//if (modalLayers == 0) panel.setEnabled(false);
		modalLayers++;
	}
	
	
	
	@Override
	public void pushCallout(Component content, String targetId, CalloutSettings settings) {
		// wrap the component into a modal foreground dialog with alpha-blended-background
		final Panel guiMsgPlace = new Panel("guimsgplace_for_callout");
		VelocityContainer inset = new VelocityContainer("inset", VELOCITY_ROOT + "/callout.html", null, null) {
			public void validate(UserRequest ureq, ValidationResult vr) {
				super.validate(ureq, vr);
				// just before rendering, we need to tell the windowbackoffice that we are a favorite for accepting gui-messages.
				// the windowbackoffice doesn't know about guimessages, it is only a container that keeps them for one render cycle
				List<ZIndexWrapper> zindexed = wbo.getGuiMessages();
				zindexed.add(new ZIndexWrapper(guiMsgPlace, 10));
			}
		};
		inset.put("cont", content);
		inset.put("guimsgplace", guiMsgPlace);
		inset.contextPut("guimsgtarget", targetId);
		int zindex = 900 + (modalLayers * 100) + 5;
		inset.contextPut("zindexoverlay", zindex+1);
		inset.contextPut("zindexshim", zindex);
		inset.contextPut("zindexarea", zindex+5);
		inset.contextPut("zindexextwindows", zindex+50);
		if(settings != null) {
			inset.contextPut("arrow", settings.isArrow());
			inset.contextPut("orientation", settings.getOrientation().name());
		} else {
			inset.contextPut("arrow", Boolean.TRUE);
			inset.contextPut("orientation", CalloutOrientation.bottom.name());
		}
		modalPanel.pushContent(inset);
		modalLayers++;
	}

	/**
	 * @see org.olat.core.gui.control.GuiStackHandle#pushContent(org.olat.core.gui.components.Component)
	 */
	public void pushContent(Component newContent) {
		if (modalLayers > 0) {
			// if, in a modaldialog, a push-to-main-area is issued, put it on the modal stack.
			// e.g. a usersearch (in modal mode) offers some subfunctionality which needs the whole screen.
			// probably rarely the case, but we support it.
			pushModalDialog(newContent);
		} else {
			panel.pushContent(newContent);
		}
	}

	/**
	 * @see org.olat.core.gui.control.GuiStackHandle#popContent()
	 */
	public void popContent() {
		if (modalLayers > 0) {
			modalLayers--;
			modalPanel.popContent();
			if (modalLayers == 0) {
				// unblock background panel
				//panel.setEnabled(true);
			}
		} else {
			panel.popContent();
		}
	}

	/**
	 * @return
	 */
	public StackedPanel getPanel() {
		return panel;
	}

	/**
	 * @return Returns the modalPanel.
	 */
	public StackedPanel getModalPanel() {
		return modalPanel;
	}

}