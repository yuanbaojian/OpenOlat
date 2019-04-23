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
 * frentix GmbH, Switzerland, http://www.frentix.com
 * <p>
 */

package org.olat.core.util.i18n.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Util;
import org.olat.core.util.i18n.I18nItem;
import org.olat.core.util.i18n.I18nManager;
import org.olat.core.util.i18n.I18nModule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Description:<br>
 * This controller provides a small gui that allows the user to translate one
 * single i18n-key to all currently enabled languages
 * 
 * <P>
 * Initial Date: 25.08.2011 <br>
 * 
 * @author strentini
 */
public class SingleKeyTranslatorController extends FormBasicController {

	private String[] i18nItemKeys;
	private List<I18nRowBundle> bundles;
	private Class<?> translatorBaseClass;
	private static final String TXT_NAME_PREFIX = "text.";
	private static final String LBL_NAME_PREFIX = "lbl.";
	private Map<String, TextElement> textElements;

	@Autowired
	private I18nManager i18nMng;
	@Autowired
	private I18nModule i18nModule;

	public SingleKeyTranslatorController(UserRequest ureq, WindowControl wControl, String keyToTranslate, Class<?> translatorBaseClass) {
		this(ureq,wControl,new String[]{keyToTranslate},translatorBaseClass);
	}

	/**
	 * package protected getter for the translator base class (used only by inner
	 * class <code>i18nRowBundle</code>)
	 * 
	 * @return
	 */
	Class<?> getTranslatorBaseClass() {
		return this.translatorBaseClass;
	}

	/**
	 * 
	 * @param ureq
	 * @param wControl
	 * @param keysToTranslate array of keys to translate (each key will have the
	 *          same value, translation is only done once (for each language) !)
	 * @param translatorBaseClass
	 */
	public SingleKeyTranslatorController(UserRequest ureq, WindowControl wControl, String[] keysToTranslate, Class<?> translatorBaseClass) {
		super(ureq, wControl, FormBasicController.LAYOUT_VERTICAL);
		i18nItemKeys = keysToTranslate;
		this.translatorBaseClass = translatorBaseClass;
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		Map<Locale, Locale> allOverlays = i18nModule.getOverlayLocales();

		Collection<String> enabledKeys = i18nModule.getEnabledLanguageKeys();
		bundles = new ArrayList<>();
		for (String key : enabledKeys) {
			Locale loc = i18nMng.getLocaleOrNull(key);
			if (loc != null) bundles.add(new I18nRowBundle(key, allOverlays.get(loc), loc));
		}

		// build the form
		textElements = new HashMap<>();
		for (I18nRowBundle i18nRowBundle : bundles) {
			uifactory.addStaticTextElement(LBL_NAME_PREFIX + i18nRowBundle.getLanguageKey(), null, i18nRowBundle.getKeyTranslator().getLocale()
					.getDisplayLanguage(getLocale()), formLayout);

			String value = "";
			if (i18nRowBundle.hasTranslationForValue(i18nItemKeys[0])) {
				value = i18nRowBundle.getKeyTranslator().translate(i18nItemKeys[0]);
			}
			TextElement te = uifactory.addTextElement(TXT_NAME_PREFIX + i18nRowBundle.getLanguageKey(), null, 255, value, formLayout);
			te.setMandatory(true);
			te.setDisplaySize(60);
			textElements.put(i18nRowBundle.getLanguageKey(), te);

		}

		FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("ok_cancel", getTranslator());
		buttonLayout.setRootForm(mainForm);
		uifactory.addFormSubmitButton("ok", buttonLayout);
		uifactory.addFormCancelButton("cancel", buttonLayout, ureq, getWindowControl());
		formLayout.add(buttonLayout);
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		return true;
	}

	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}
	
	/**
	 * delete a key that is no longer used.
	 * 
	 * @param key
	 */
	public void deleteI18nKey(String key){
		for (I18nRowBundle bundle : bundles) {
			I18nItem item = i18nMng.getI18nItem(translatorBaseClass.getPackage().getName(), key, bundle.getOverlayLocale());
			i18nMng.saveOrUpdateI18nItem(item, "");
		}
	}

	@Override
	protected void formOK(UserRequest ureq) {
		// save new values
		for (I18nRowBundle bundle : bundles) {
			String newValue = textElements.get(bundle.getLanguageKey()).getValue();
			for (String itemKey : this.i18nItemKeys) {
				I18nItem item = i18nMng.getI18nItem(translatorBaseClass.getPackage().getName(), itemKey, bundle.getOverlayLocale());
				i18nMng.saveOrUpdateI18nItem(item, newValue);
			}
		}

		fireEvent(ureq, Event.DONE_EVENT);
	}

	@Override
	protected void doDispose() {
		//
	}

	/**
	 * Description: inner helper-class that bundles the language-key, a translator
	 * and the two locales for that language (overlay and normal) <br>
	 * 
	 * <P>
	 * Initial Date: 25.08.2011 <br>
	 * 
	 * @author strentini
	 */
	class I18nRowBundle {
		private Locale overlayLocale;
		private Locale locale;
		private String languageKey;
		private Translator keyTranslator;

		/**
		 * 
		 * @param LanguageKey
		 * @param overlay
		 */
		public I18nRowBundle(String languageKey, Locale overlayLocale, Locale locale) {
			this.languageKey = languageKey;
			this.overlayLocale = overlayLocale;
			this.locale = locale;
			this.keyTranslator = Util.createPackageTranslator(getTranslatorBaseClass(), locale);
		}

		public boolean hasTranslationForValue(String v) {
			String translation = i18nMng.getLocalizedString(getTranslatorBaseClass().getPackage().getName(), v, null, locale, true, false);
			return (translation != null);
		}

		public Locale getOverlayLocale() {
			return overlayLocale;
		}

		public Locale getLocale() {
			return locale;
		}

		public String getLanguageKey() {
			return languageKey;
		}

		public Translator getKeyTranslator() {
			return keyTranslator;
		}

	}

}
