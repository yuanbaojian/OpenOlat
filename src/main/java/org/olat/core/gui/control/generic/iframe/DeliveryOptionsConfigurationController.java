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
package org.olat.core.gui.control.generic.iframe;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.StringHelper;
import org.olat.course.editor.NodeEditController;

/**
 * 
 * Initial date: 29.05.2013<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class DeliveryOptionsConfigurationController extends FormBasicController {
	
	private DeliveryOptions config, parentConfig;
	
	private SingleSelection standardModeEl, inheritEl;
	private SingleSelection jsOptionEl, cssOptionEl;
	private SingleSelection encodingContentEl, encodingJSEl, heightEl;
	private MultipleSelectionElement glossarEl;
	private String helpPage;

	private static final String[] jsKeys = new String[] {"none", "jQuery", "prototypejs" };
	private static final String[] cssKeys = new String[] {"none", "openolat" };
	
	private static final String[] keys = new String[]{ DeliveryOptions.CONFIG_HEIGHT_AUTO, "460", "480", 
			"500", "520", "540", "560", "580",
			"600", "620", "640", "660", "680",
			"700", "720", "730", "760", "780",
			"800", "820", "840", "860", "880",
			"900", "920", "940", "960", "980",
			"1000", "1020", "1040", "1060", "1080",
			"1100", "1120", "1140", "1160", "1180",
			"1200", "1220", "1240", "1260", "1280",
			"1300", "1320", "1340", "1360", "1380"
	};

	private String[] encodingContentKeys;
	private String[] encodingJSKeys;
	
	private static final String[] standardModeKeys = new String[]{ "standard", "configured" };
	private static final String[] inheritKeys = new String[]{ "inherit", "custom"};

	public DeliveryOptionsConfigurationController(UserRequest ureq, WindowControl wControl, DeliveryOptions config, String helpPage) {
		this(ureq, wControl, config, helpPage, null);
	}

	public DeliveryOptionsConfigurationController(UserRequest ureq, WindowControl wControl, DeliveryOptions config, String helpPage, DeliveryOptions parentConfig) {
		super(ureq, wControl);
		this.config = (config == null ? new DeliveryOptions() : config.clone());
		this.parentConfig = parentConfig;
		this.helpPage = helpPage;
		initForm(ureq);

		if(parentConfig != null && config != null && config.getInherit() != null && config.getInherit().booleanValue()) {
			setValues(parentConfig);
		} else {
			setValues(config);
		}
		updateEnabled();
	}
	
	/**
	 * Return null if the configuration parameter was null and the settings
	 * were not changed.
	 * @return
	 */
	public DeliveryOptions getDeliveryOptions() {
		return config;
	}
	
	public DeliveryOptions getParentDeliveryOptions() {
		return parentConfig;
	}
	
	public void setParentDeliveryOptions(DeliveryOptions parentConfig) {
		this.parentConfig = parentConfig;
		if(parentConfig == null) {
			inheritEl.select(inheritKeys[1], true);
			inheritEl.setVisible(false);
			setValues(config);
		} else if(inheritEl.isVisible()) {
			if(inheritEl.isSelected(0)) {
				//update inherit values
				setValues(parentConfig);
			}
		} else {
			inheritEl.setVisible(true);
			inheritEl.select(inheritKeys[0], true);
			persistRawOptions(config);
			persistEncoding(config);
			setValues(parentConfig);
		}
		updateEnabled();
		flc.setDirty(true);
	}
	
	public DeliveryOptions getOptionsForPreview() {
		if(isInherit()) {
			return parentConfig;
		} else {
			DeliveryOptions previewOptions = new DeliveryOptions();
			persistValues(previewOptions);
			return previewOptions;
		}
	}
	
	public boolean isInherit() {
		return parentConfig != null && config != null && config.getInherit() != null && config.getInherit().booleanValue();
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		setFormTitle("option.desc");
		if(StringHelper.containsNonWhitespace(helpPage)){
			setFormContextHelp(helpPage);
		}

		String[] inheritValues = new String[]{
				translate("inherit"), translate("custom")	
		};
		inheritEl = uifactory.addRadiosVertical("inherit.label", formLayout, inheritKeys, inheritValues);
		inheritEl.addActionListener(FormEvent.ONCHANGE);
		if(config != null && config.getInherit() != null && config.getInherit().booleanValue()) {
			inheritEl.select(inheritKeys[0], true);
		} else {
			inheritEl.select(inheritKeys[1], true);
		}
		if (parentConfig == null) {
			inheritEl.setVisible(false);			
		} else {
			inheritEl.setVisible(true);			
			uifactory.addSpacerElement("spacer.mode", formLayout, false);			
		}

		String[] standardModeValues = new String[]{
			translate("mode.standard"), translate("mode.configured")	
		};
		standardModeEl = uifactory.addRadiosVertical("mode", formLayout, standardModeKeys, standardModeValues);
		standardModeEl.setHelpTextKey("mode.hover", null);
		standardModeEl.setHelpUrlForManualPage("Knowledge Transfer#_splayout");
		standardModeEl.addActionListener(FormEvent.ONCHANGE);
		
		uifactory.addSpacerElement("spacer.js", formLayout, false);

		String[] jsValues = new String[] {
				translate("option.js.none"), translate("option.js.jQuery"), translate("option.js.prototypejs")
		};
		jsOptionEl = uifactory.addRadiosVertical("option.js", formLayout, jsKeys, jsValues);
		jsOptionEl.addActionListener(FormEvent.ONCHANGE);
		
		glossarEl = uifactory.addCheckboxesHorizontal("option.glossary", formLayout, new String[]{"on"}, new String[]{""});

		String[] values = new String[]{ translate("height.auto"), "460px", "480px", 
				"500px", "520px", "540px", "560px", "580px",
				"600px", "620px", "640px", "660px", "680px",
				"700px", "720px", "730px", "760px", "780px",
				"800px", "820px", "840px", "860px", "880px",
				"900px", "920px", "940px", "960px", "980px",
				"1000px", "1020px", "1040px", "1060px", "1080px",
				"1100px", "1120px", "1140px", "1160px", "1180px",
				"1200px", "1220px", "1240px", "1260px", "1280px",
				"1300px", "1320px", "1340px", "1360px", "1380px"
		};
		heightEl = uifactory.addDropdownSingleselect("height", "height.label", formLayout, keys, values, null);

		String[] cssValues = new String[] {
				translate("option.css.none"), translate("option.css.openolat")
		};
		cssOptionEl = uifactory.addRadiosVertical("option.css", formLayout, cssKeys, cssValues);

		uifactory.addSpacerElement("spaceman", formLayout, false);
		
		Map<String,Charset> charsets = Charset.availableCharsets();
		int numOfCharsets = charsets.size() + 1;
		
		encodingContentKeys = new String[numOfCharsets];
		encodingContentKeys[0] = NodeEditController.CONFIG_CONTENT_ENCODING_AUTO;

		String[] encodingContentValues = new String[numOfCharsets];
		encodingContentValues[0] = translate("encoding.auto");
		
		encodingJSKeys = new String[numOfCharsets];
		encodingJSKeys[0] = NodeEditController.CONFIG_JS_ENCODING_AUTO;

		String[] encodingJSValues = new String[numOfCharsets];
		encodingJSValues[0] =	translate("encoding.same");
		
		int count = 1;
		Locale locale = getLocale();
		for(Map.Entry<String, Charset> charset:charsets.entrySet()) {
			encodingContentKeys[count] = charset.getKey();
			encodingContentValues[count] = charset.getValue().displayName(locale);
			encodingJSKeys[count] = charset.getKey();
			encodingJSValues[count] = charset.getValue().displayName(locale);
			count++;
		}
		
		encodingContentEl = uifactory.addDropdownSingleselect("encoContent", "encoding.content", formLayout, encodingContentKeys, encodingContentValues, null);
		encodingJSEl = uifactory.addDropdownSingleselect("encoJS", "encoding.js", formLayout, encodingJSKeys, encodingJSValues, null);

		FormLayoutContainer buttonsLayout = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		buttonsLayout.setRootForm(mainForm);
		formLayout.add(buttonsLayout);
		uifactory.addFormSubmitButton("save", buttonsLayout);
	}
	
	private void updateEnabled() {
		boolean inherit = (inheritEl.isVisible() && inheritEl.isSelected(0));
		
		encodingContentEl.setEnabled(!inherit);
		encodingJSEl.setEnabled(!inherit);
		standardModeEl.setEnabled(!inherit);
		heightEl.setEnabled(!inherit);
		if(inherit) {
			//disabled all
			jsOptionEl.setEnabled(false);
			cssOptionEl.setEnabled(false);
			glossarEl.setEnabled(false);
			//set inherited values
		} else {
			boolean standard = standardModeEl.isSelected(0);
			boolean jQueryEnabled = jsOptionEl.isSelected(1);
			jsOptionEl.setEnabled(!standard);
			cssOptionEl.setEnabled(!standard);
			glossarEl.setEnabled(!standard && jQueryEnabled);
		}
	}
	
	private void updateStandardMode() {
		boolean standard = standardModeEl.isSelected(0);
		if(standard) {
			jsOptionEl.select(jsKeys[0], true);
			cssOptionEl.select(cssKeys[0], true);
			glossarEl.select("on", false);
			if(heightEl.isSelected(0)) {
				heightEl.select("600", true);
			}
		}
	}
	
	private void setValues(DeliveryOptions cfg) {
		Boolean mode = (cfg == null ? null : cfg.getStandardMode());
		if(mode == null || mode.booleanValue()) {
			standardModeEl.select("standard", true);
		} else {
			standardModeEl.select("configured", true);
		}
		
		if(cfg != null) {
			if(cfg.getjQueryEnabled() != null && cfg.getjQueryEnabled().booleanValue()) {
				jsOptionEl.select(jsKeys[1], true);//jQuery
			} else if(cfg.getPrototypeEnabled() != null && cfg.getPrototypeEnabled().booleanValue()) {
				jsOptionEl.select(jsKeys[2], true);//prototype
			} else {
				jsOptionEl.select(jsKeys[0], true);//default is none
			}
		} else {
			jsOptionEl.select(jsKeys[0], true);//default is none
		}
		
		Boolean glossarEnabled = (cfg == null ? null : cfg.getGlossaryEnabled());
		if(glossarEnabled != null && glossarEnabled.booleanValue()) {
			glossarEl.select("on", true);
		}
		
		String height = cfg == null ? null :  cfg.getHeight();
		if (height != null && Arrays.asList(keys).contains(height)) {
			heightEl.select(height, true);
		} else {
			heightEl.select(DeliveryOptions.CONFIG_HEIGHT_AUTO, true);
		}
		
		if(cfg != null && cfg.getOpenolatCss() != null && cfg.getOpenolatCss().booleanValue()) {
			cssOptionEl.select(cssKeys[1], true);
		} else {
			cssOptionEl.select(cssKeys[0], false);//default none
		}
		
		String encodingContent = (cfg == null ? null : cfg.getContentEncoding());
		if (encodingContent != null && Arrays.asList(encodingContentKeys).contains(encodingContent)) {
			encodingContentEl.select(encodingContent, true);
		} else {
			encodingContentEl.select(NodeEditController.CONFIG_CONTENT_ENCODING_AUTO, true);
		}
		
		String encodingJS = (cfg == null ? null : cfg.getJavascriptEncoding());
		if (encodingJS != null && Arrays.asList(encodingJSKeys).contains(encodingJS)) {
			encodingJSEl.select(encodingJS, true);
		} else {
			encodingJSEl.select(NodeEditController.CONFIG_JS_ENCODING_AUTO, true);
		}
	}
	
	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		
		if(!isInherit()) {
			glossarEl.clearError();
			if(glossarEl.isAtLeastSelected(1)) {
				if(!jsOptionEl.isSelected(1)) {
					allOk &= false;
					glossarEl.setErrorKey("glossary.need.jQuery", null);
				}	
			}
			
			heightEl.clearError();
			if(heightEl.isSelected(0)
					&& (standardModeEl.isSelected(0) || jsOptionEl.isSelected(0))) {
				heightEl.setErrorKey("automatic.need.js", null);
				allOk = false;
			}
		}
		
		return allOk & super.validateFormLogic(ureq);
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(standardModeEl == source) {
			updateEnabled();
			updateStandardMode();
		} else if(jsOptionEl == source) {
			updateEnabled();
		} else if(inheritEl == source) {
			if(parentConfig != null ) {
				if(inheritEl.isSelected(0)) {
					persistRawOptions(config);
					persistEncoding(config);
					setValues(parentConfig);
				} else {
					setValues(config);
				}
			}
			updateEnabled();
			flc.setDirty(true);
		}
		super.formInnerEvent(ureq, source, event);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		if(parentConfig != null && inheritEl.isVisible() && inheritEl.isSelected(0)) {
			config = new DeliveryOptions();
			config.setInherit(Boolean.TRUE);
		} else {
			if(config == null) {
				config = new DeliveryOptions();
			}
			
			persistValues(config); 
		}
		
		
		fireEvent(ureq, Event.DONE_EVENT);
	}
	
	private void persistValues(DeliveryOptions options) {
		options.setInherit(Boolean.FALSE);
		if(standardModeEl.isSelected(0)) {
			//standard mode
			options.setStandardMode(Boolean.TRUE);
			options.setjQueryEnabled(Boolean.FALSE);
			options.setPrototypeEnabled(Boolean.FALSE);
			options.setGlossaryEnabled(Boolean.FALSE);
			options.setHeight(heightEl.getSelectedKey());
			options.setOpenolatCss(Boolean.FALSE);
		} else {
			options.setStandardMode(Boolean.FALSE);
			//js
			if(jsOptionEl.isSelected(0)) {
				options.setjQueryEnabled(Boolean.FALSE);
				options.setPrototypeEnabled(Boolean.FALSE);
				options.setGlossaryEnabled(Boolean.FALSE);
			} else {
				options.setjQueryEnabled(jsOptionEl.isSelected(1));
				options.setPrototypeEnabled(jsOptionEl.isSelected(2));
				options.setGlossaryEnabled(glossarEl.isAtLeastSelected(1));
			}
			//css
			options.setHeight(heightEl.getSelectedKey());
			options.setOpenolatCss(cssOptionEl.isSelected(1));
		}
		persistEncoding(config);
	}
	
	private void persistRawOptions(DeliveryOptions options) {
		options.setStandardMode(standardModeEl.isSelected(0));
		options.setjQueryEnabled(jsOptionEl.isSelected(1));
		options.setPrototypeEnabled(jsOptionEl.isSelected(2));
		options.setGlossaryEnabled(glossarEl.isAtLeastSelected(1));
		options.setHeight(heightEl.getSelectedKey());
	}
	
	private void persistEncoding(DeliveryOptions options) {
		String contentEncoding = encodingContentEl.getSelectedKey();
		if(NodeEditController.CONFIG_CONTENT_ENCODING_AUTO.equals(contentEncoding)) {
			options.setContentEncoding(null);
		} else {
			options.setContentEncoding(contentEncoding);
		}
		
		String javascriptEncoding = encodingJSEl.getSelectedKey();
		if(NodeEditController.CONFIG_JS_ENCODING_AUTO.equals(javascriptEncoding)) {
			options.setJavascriptEncoding(null);
		} else {
			options.setJavascriptEncoding(javascriptEncoding);
		}
	}
}
