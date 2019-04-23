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
package org.olat.modules.curriculum.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.olat.core.commons.persistence.DB;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.DateChooser;
import org.olat.core.gui.components.form.flexible.elements.RichTextElement;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.StringHelper;
import org.olat.modules.curriculum.Curriculum;
import org.olat.modules.curriculum.CurriculumCalendars;
import org.olat.modules.curriculum.CurriculumElement;
import org.olat.modules.curriculum.CurriculumElementManagedFlag;
import org.olat.modules.curriculum.CurriculumElementStatus;
import org.olat.modules.curriculum.CurriculumElementType;
import org.olat.modules.curriculum.CurriculumElementTypeRef;
import org.olat.modules.curriculum.CurriculumElementTypeToType;
import org.olat.modules.curriculum.CurriculumLectures;
import org.olat.modules.curriculum.CurriculumSecurityCallback;
import org.olat.modules.curriculum.CurriculumService;
import org.olat.modules.curriculum.model.CurriculumElementTypeRefImpl;
import org.olat.modules.taxonomy.TaxonomyLevel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 16 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class EditCurriculumElementController extends FormBasicController {
	
	private static final String[] calendarsKeys = new String[] {
			CurriculumCalendars.enabled.name(), CurriculumCalendars.disabled.name()
		};
	
	private static final String[] calendarsTypedKeys = new String[] {
			CurriculumCalendars.enabled.name(), CurriculumCalendars.disabled.name(), CurriculumCalendars.inherited.name()
		};
	
	private static final String[] lecturesKeys = new String[] {
			CurriculumLectures.enabled.name(), CurriculumLectures.disabled.name()
		};
	
	private static final String[] lecturesTypedKeys = new String[] {
			CurriculumLectures.enabled.name(), CurriculumLectures.disabled.name(), CurriculumLectures.inherited.name()
		};
	
	private static final String[] statusKey = new String[] {
			CurriculumElementStatus.active.name(), CurriculumElementStatus.inactive.name(), CurriculumElementStatus.deleted.name()
		};

	private DateChooser endEl;
	private DateChooser beginEl;
	private TextElement identifierEl;
	private TextElement displayNameEl;
	private RichTextElement descriptionEl;

	private SingleSelection statusEl;
	private SingleSelection lecturesEnabledEl;
	private SingleSelection calendarsEnabledEl;
	private SingleSelection curriculumElementTypeEl;
	
	private Curriculum curriculum;
	private CurriculumElement element;
	private CurriculumElement parentElement;
	private final CurriculumSecurityCallback secCallback;
	
	@Autowired
	private DB dbInstance;
	@Autowired
	private CurriculumService curriculumService;
	
	/**
	 * Create a new curriculum element.
	 * 
	 * @param ureq The user request
	 * @param wControl The window control
	 */
	public EditCurriculumElementController(UserRequest ureq, WindowControl wControl,
			CurriculumElement parentElement, Curriculum curriculum, CurriculumSecurityCallback secCallback) {
		super(ureq, wControl);
		this.curriculum = curriculum;
		this.parentElement = parentElement;
		this.secCallback = secCallback;
		initForm(ureq);
	}
	
	public EditCurriculumElementController(UserRequest ureq, WindowControl wControl,
			CurriculumElement element, CurriculumElement parentElement, Curriculum curriculum, CurriculumSecurityCallback secCallback) {
		super(ureq, wControl);
		this.curriculum = curriculum;
		this.element = element;
		this.parentElement = parentElement;
		this.secCallback = secCallback;
		initForm(ureq);
	}
	
	public CurriculumElement getCurriculumElement() {
		return element;
	}

	public CurriculumElement getParentElement() {
		return parentElement;
	}

	public void setParentElement(CurriculumElement parentElement) {
		this.parentElement = parentElement;
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		if(element != null) {
			String key = element.getKey().toString();
			uifactory.addStaticTextElement("curriculum.element.key", key, formLayout);
			String externalId = element.getExternalId();
			uifactory.addStaticTextElement("curriculum.element.external.id", externalId, formLayout);
		}
		
		String identifier = element == null ? "" : element.getIdentifier();
		identifierEl = uifactory.addTextElement("identifier", "curriculum.element.identifier", 255, identifier, formLayout);
		identifierEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.identifier) && secCallback.canEditCurriculumElement());
		identifierEl.setMandatory(true);
		if(identifierEl.isEnabled() && !StringHelper.containsNonWhitespace(identifier)) {
			identifierEl.setFocus(true);
		}

		String displayName = element == null ? "" : element.getDisplayName();
		displayNameEl = uifactory.addTextElement("displayName", "curriculum.element.displayName", 255, displayName, formLayout);
		displayNameEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.displayName) && secCallback.canEditCurriculumElement());
		displayNameEl.setMandatory(true);
		
		String[] statusValues = new String[] {
			translate("status.active"), translate("status.inactive"), translate("status.deleted")
		};
		statusEl = uifactory.addRadiosHorizontal("status", "curriculum.element.status", formLayout, statusKey, statusValues);
		statusEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.status) && secCallback.canEditCurriculumElement() );
		if(element == null || element.getElementStatus() == null) {
			statusEl.select(CurriculumElementStatus.active.name(), true);
		} else {
			statusEl.select(element.getElementStatus().name(), true);
		}
		
		List<CurriculumElementType> types = getTypes();
		String[] typeKeys = new String[types.size() + 1];
		String[] typeValues = new String[types.size() + 1];
		typeKeys[0] = "";
		typeValues[0] = "-";
		for(int i=types.size(); i-->0; ) {
			typeKeys[i+1] = types.get(i).getKey().toString();
			typeValues[i+1] = types.get(i).getDisplayName();
		}
		curriculumElementTypeEl = uifactory.addDropdownSingleselect("type", "curriculum.element.type", formLayout, typeKeys, typeValues, null);
		curriculumElementTypeEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.type) && secCallback.canEditCurriculumElement());
		curriculumElementTypeEl.addActionListener(FormEvent.ONCHANGE);
		boolean typeFound = false;
		CurriculumElementType elementType = element == null ? null : element.getType();
		if(elementType != null) {
			String selectedTypeKey = elementType.getKey().toString();
			for(String typeKey:typeKeys) {
				if(typeKey.equals(selectedTypeKey)) {
					curriculumElementTypeEl.select(selectedTypeKey, true);
					typeFound = true;
					break;
				}
			}
		}
		if(!typeFound) {
			curriculumElementTypeEl.select(typeKeys[0], true);
		}
		
		calendarsEnabledEl = uifactory.addRadiosHorizontal("type.calendars.enabled", formLayout, new String[0], new String[0]);
		calendarsEnabledEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.calendars) && secCallback.canEditCurriculumElement());
		CurriculumCalendars calendarsEnabled =  element == null ? CurriculumCalendars.inherited : element.getCalendars();
		updateCalendarsEnabled(calendarsEnabled, elementType);
		
		lecturesEnabledEl = uifactory.addRadiosHorizontal("type.lectures.enabled", formLayout, new String[0], new String[0]);
		lecturesEnabledEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.lectures) && secCallback.canEditCurriculumElement());
		CurriculumLectures lecturesEnabled =  element == null ? CurriculumLectures.inherited : element.getLectures();
		updateLecturesEnabled(lecturesEnabled, elementType);
		
		List<TaxonomyLevel> levels = curriculumService.getTaxonomy(element);
		if(!levels.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for(TaxonomyLevel level:levels) {
				if(sb.length() > 0) sb.append(", ");
				sb.append(level.getDisplayName());
			}
			uifactory.addStaticTextElement("curriculum.element.taxonomy", sb.toString(), formLayout);
		}
		
		Date begin = element == null ? null : element.getBeginDate();
		beginEl = uifactory.addDateChooser("start", "curriculum.element.begin", begin, formLayout);
		beginEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.dates) && secCallback.canEditCurriculumElement());

		Date end = element == null ? null : element.getEndDate();
		endEl = uifactory.addDateChooser("end", "curriculum.element.end", end, formLayout);
		endEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.dates) && secCallback.canEditCurriculumElement());
		endEl.setDefaultValue(beginEl);
		
		String description = element == null ? "" : element.getDescription();
		descriptionEl = uifactory.addRichTextElementForStringDataCompact("curriculum.description", "curriculum.description", description, 10, 60, null,
				formLayout, ureq.getUserSession(), getWindowControl());
		descriptionEl.setEnabled(!CurriculumElementManagedFlag.isManaged(element, CurriculumElementManagedFlag.description) && secCallback.canEditCurriculumElement());

		FormLayoutContainer buttonsCont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		formLayout.add(buttonsCont);
		uifactory.addFormCancelButton("cancel", buttonsCont, ureq, getWindowControl());
		if(secCallback.canEditCurriculumElement()) {
			uifactory.addFormSubmitButton("save", buttonsCont);
		}
	}
	
	private void updateCalendarsEnabled(CurriculumCalendars preferedEnabled, CurriculumElementType selectedType) {
		if(curriculumElementTypeEl.getSelected() == 0) {
			String[] onValues = new String[] {
					translate("type.calendars.enabled.enabled"), translate("type.calendars.enabled.disabled")
			};
			calendarsEnabledEl.setKeysAndValues(calendarsKeys, onValues, null);
			
			if(preferedEnabled == CurriculumCalendars.enabled || preferedEnabled == CurriculumCalendars.disabled) {
				calendarsEnabledEl.select(preferedEnabled.name(), true);
			} else {
				calendarsEnabledEl.select(CurriculumCalendars.disabled.name(), true);
			}
		} else {
			String typeVal = null;
			if(selectedType == null) {
				typeVal = "???";
			} else if(selectedType.getCalendars() == CurriculumCalendars.enabled) {
				typeVal = translate("type.calendars.enabled.enabled");
			} else if(selectedType.getCalendars() == CurriculumCalendars.disabled) {
				typeVal = translate("type.calendars.enabled.disabled");
			}

			String[] onValues = new String[] {
					translate("type.calendars.enabled.enabled"), translate("type.calendars.enabled.disabled"),
					translate("type.calendars.enabled.inherited", new String[] { typeVal })
			};
			calendarsEnabledEl.setKeysAndValues(calendarsTypedKeys, onValues, null);
			calendarsEnabledEl.select(preferedEnabled.name(), true);
		}
	}
	
	private void updateLecturesEnabled(CurriculumLectures preferedEnabled, CurriculumElementType selectedType) {
		if(curriculumElementTypeEl.getSelected() == 0) {
			String[] onValues = new String[] {
					translate("type.lectures.enabled.enabled"), translate("type.lectures.enabled.disabled")
			};
			lecturesEnabledEl.setKeysAndValues(lecturesKeys, onValues, null);
			
			if(preferedEnabled == CurriculumLectures.enabled || preferedEnabled == CurriculumLectures.disabled) {
				lecturesEnabledEl.select(preferedEnabled.name(), true);
			} else {
				lecturesEnabledEl.select(CurriculumLectures.disabled.name(), true);
			}
		} else {
			String typeVal = null;
			if(selectedType == null) {
				typeVal = "???";
			} else if(selectedType.getLectures() == CurriculumLectures.enabled) {
				typeVal = translate("type.lectures.enabled.enabled");
			} else if(selectedType.getLectures() == CurriculumLectures.disabled) {
				typeVal = translate("type.lectures.enabled.disabled");
			}

			String[] onValues = new String[] {
					translate("type.lectures.enabled.enabled"), translate("type.lectures.enabled.disabled"),
					translate("type.lectures.enabled.inherited", new String[] { typeVal })
			};
			lecturesEnabledEl.setKeysAndValues(lecturesTypedKeys, onValues, null);
			lecturesEnabledEl.select(preferedEnabled.name(), true);
		}
	}
	
	private List<CurriculumElementType> getTypes() {
		List<CurriculumElementType> types;
		if(element != null) {
			types = getTypes(element);
		} else if(parentElement != null) {
			types = getTypes(parentElement);
		} else {
			types = new ArrayList<>();
		}
		
		if(types.isEmpty()) {
			types.addAll(curriculumService.getCurriculumElementTypes());
		} else if(element != null && element.getType() != null && !types.contains(element.getType())) {
			types.add(element.getType());
		}
		return types;
	}
	
	private List<CurriculumElementType> getTypes(CurriculumElement curriculumElement)  {
		List<CurriculumElementType> types = new ArrayList<>();
		List<CurriculumElement> parentLine = curriculumService.getCurriculumElementParentLine(curriculumElement);
		for(int i=parentLine.size() - 1; i-->0; ) {
			CurriculumElement parent = parentLine.get(i);
			CurriculumElementType parentType = parent.getType();
			if(parentType != null) {
				Set<CurriculumElementTypeToType> typeToTypes = parentType.getAllowedSubTypes();
				for(CurriculumElementTypeToType typeToType:typeToTypes) {
					if(typeToType != null) {
						types.add(typeToType.getAllowedSubType());
					}
				}
				break;
			}
		}
		return types;
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = super.validateFormLogic(ureq);
		
		displayNameEl.clearError();
		if(!StringHelper.containsNonWhitespace(displayNameEl.getValue())) {
			displayNameEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}
		
		identifierEl.clearError();
		if(!StringHelper.containsNonWhitespace(identifierEl.getValue())) {
			identifierEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}
		
		calendarsEnabledEl.clearError();
		if(!calendarsEnabledEl.isOneSelected()) {
			calendarsEnabledEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}
		
		lecturesEnabledEl.clearError();
		if(!lecturesEnabledEl.isOneSelected()) {
			lecturesEnabledEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}
		
		statusEl.clearError();
		if(!statusEl.isOneSelected()) {
			statusEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}
		
		return allOk;
	}

	@Override
	protected void formOK(UserRequest ureq) {
		CurriculumElementType elementType = getSelectedType();
		CurriculumLectures lectures = CurriculumLectures.valueOf(lecturesEnabledEl.getSelectedKey());
		CurriculumCalendars calendars = CurriculumCalendars.valueOf(calendarsEnabledEl.getSelectedKey());
		CurriculumElementStatus status = CurriculumElementStatus.valueOf(statusEl.getSelectedKey());
		if(element == null) {
			//create a new one
			element = curriculumService.createCurriculumElement(identifierEl.getValue(), displayNameEl.getValue(),
					status, beginEl.getDate(), endEl.getDate(), parentElement, elementType, calendars, lectures, curriculum);
		} else {
			element = curriculumService.getCurriculumElement(element);
			element.setIdentifier(identifierEl.getValue());
			element.setDisplayName(displayNameEl.getValue());
			element.setDescription(descriptionEl.getValue());
			element.setBeginDate(beginEl.getDate());
			element.setEndDate(endEl.getDate());
			element.setType(elementType);
			element.setCalendars(calendars);
			element.setLectures(lectures);
			element.setElementStatus(status);
			element = curriculumService.updateCurriculumElement(element);
		}
		
		dbInstance.commitAndCloseSession(); // need to relaod properly the tree
		fireEvent(ureq, Event.DONE_EVENT);
	}
	
	private CurriculumElementType getSelectedType() {
		String selectedTypeKey = curriculumElementTypeEl.getSelectedKey();
		if(StringHelper.containsNonWhitespace(selectedTypeKey)) {
			CurriculumElementTypeRef ref = new CurriculumElementTypeRefImpl(Long.valueOf(selectedTypeKey));
			return curriculumService.getCurriculumElementType(ref);
		}
		return null;
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(source == curriculumElementTypeEl) {
			CurriculumElementType elementType = getSelectedType();
			if(calendarsEnabledEl.isOneSelected()) {
				CurriculumCalendars enabled = CurriculumCalendars.valueOf(calendarsEnabledEl.getSelectedKey());
				updateCalendarsEnabled(enabled, elementType);
			}
			if(lecturesEnabledEl.isOneSelected()) {
				CurriculumLectures enabled = CurriculumLectures.valueOf(lecturesEnabledEl.getSelectedKey());
				updateLecturesEnabled(enabled, elementType);
			}
		}
		super.formInnerEvent(ureq, source, event);
	}

	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}
}