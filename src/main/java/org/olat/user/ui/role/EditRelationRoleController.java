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
package org.olat.user.ui.role;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.olat.basesecurity.IdentityRelationshipService;
import org.olat.basesecurity.RelationRight;
import org.olat.basesecurity.RelationRole;
import org.olat.basesecurity.RelationRoleManagedFlag;
import org.olat.basesecurity.RelationRoleToRight;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.core.util.i18n.I18nItem;
import org.olat.core.util.i18n.I18nManager;
import org.olat.core.util.i18n.I18nModule;
import org.olat.user.UserModule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 29 janv. 2019<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class EditRelationRoleController extends FormBasicController {
	
	private final String[] predefinedLabelKeys = new String[] {
		"", "supervisor", "legalRepresentative", "tutor", "parent",
		"teacher", "expert", "legalGardian", "employer", "sportsClub"
	};
	
	private TextElement roleEl;
	private SingleSelection predefinedLabelEl;
	private MultipleSelectionElement rightsEl;
	
	private RelationRole relationRole;
	private List<RelationRight> rights;

	@Autowired
	private I18nModule i18nModule;
	@Autowired
	private I18nManager i18nManager;
	@Autowired
	private IdentityRelationshipService identityRelationsService;

	public EditRelationRoleController(UserRequest ureq, WindowControl wControl) {
		this(ureq, wControl,null);
	}
	
	public EditRelationRoleController(UserRequest ureq, WindowControl wControl, RelationRole relationRole) {
		super(ureq, wControl, Util.createPackageTranslator(UserModule.class, ureq.getLocale()));
		rights = identityRelationsService.getAvailableRights();
		this.relationRole = relationRole;
		initForm(ureq);
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		String name = relationRole == null ? null : relationRole.getRole();
		roleEl = uifactory.addTextElement("role.identifier", 128, name, formLayout);
		roleEl.setEnabled(!RelationRoleManagedFlag.isManaged(relationRole, RelationRoleManagedFlag.name));
		
		String[] predefinedLabelValues = new String[predefinedLabelKeys.length];
		predefinedLabelValues[0] = translate("no.predefined.label");
		for(int i=1; i<predefinedLabelKeys.length; i++) {
			predefinedLabelValues[i] = translate(RelationRolesAndRightsUIFactory.TRANS_ROLE_PREFIX.concat(predefinedLabelKeys[i]));
		}
		
		if(relationRole == null || relationRole.getKey() == null) {
			predefinedLabelEl = uifactory.addDropdownSingleselect("predefined.labels", formLayout,
					predefinedLabelKeys, predefinedLabelValues);
			predefinedLabelEl.select(predefinedLabelKeys[0], true);
		}
		
		String[] rightKeys = new String[rights.size()];
		String[] rightValues = new String[rights.size()];
		for(int i=rights.size(); i-->0; ) {
			rightKeys[i] = rights.get(i).getRight();
			rightValues[i] = RelationRolesAndRightsUIFactory.getTranslatedRight(rights.get(i), getLocale());
		}
		rightsEl = uifactory.addCheckboxesVertical("role.rights", formLayout, rightKeys, rightValues, 2);
		rightsEl.setEnabled(!RelationRoleManagedFlag.isManaged(relationRole, RelationRoleManagedFlag.rights));
		if(relationRole != null) {
			Set<RelationRoleToRight> roleToRights = relationRole.getRights();
			for(RelationRoleToRight roleToRight:roleToRights) {
				String right = roleToRight.getRight().getRight();
				for(String rightKey:rightKeys) {
					if(rightKey.equals(right)) {
						rightsEl.select(rightKey, true);
					}
				}
			}	
		}
		
		FormLayoutContainer buttonsCont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		formLayout.add(buttonsCont);
		uifactory.addFormCancelButton("cancel", buttonsCont, ureq, getWindowControl());
		uifactory.addFormSubmitButton("save", buttonsCont);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = super.validateFormLogic(ureq);
		
		roleEl.clearError();
		if(!StringHelper.containsNonWhitespace(roleEl.getValue())) {
			roleEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}
		
		return allOk;
	}

	@Override
	protected void formOK(UserRequest ureq) {
		Collection<String> selectedRightKeys = rightsEl.getSelectedKeys();
		List<RelationRight> selectedRights = rights.stream()
				.filter(r -> selectedRightKeys.contains(r.getRight())).collect(Collectors.toList());
		if(relationRole == null) {
			relationRole = identityRelationsService.createRole(roleEl.getValue(), selectedRights);
			copyTranslations();
		} else {
			relationRole = identityRelationsService.updateRole(relationRole, selectedRights);
		}
		
		fireEvent(ureq, Event.DONE_EVENT);
	}
	
	private void copyTranslations() {
		if(predefinedLabelEl == null || !predefinedLabelEl.isOneSelected()
				|| !StringHelper.containsNonWhitespace(predefinedLabelEl.getSelectedKey())) return;
		
		String i18nPartialKey = predefinedLabelEl.getSelectedKey();
		
		String[] prefix = new String[] {
				RelationRolesAndRightsUIFactory.TRANS_ROLE_PREFIX,
				RelationRolesAndRightsUIFactory.TRANS_ROLE_CONTRA_PREFIX,
				RelationRolesAndRightsUIFactory.TRANS_ROLE_DESCRIPTION_PREFIX,
				RelationRolesAndRightsUIFactory.TRANS_ROLE_CONTRA_DESCRIPTION_PREFIX
		};

		Collection<String> enabledLocaleKeys = i18nModule.getEnabledLanguageKeys();
		Map<Locale, Locale> allOverlays = i18nModule.getOverlayLocales();

		for(String p:prefix) {
			String predfinedI18nKey = p.concat(i18nPartialKey);
			String customI18nKey = p.concat(relationRole.getKey().toString());
			for(String enabledLocaleKey:enabledLocaleKeys) {
				Locale loc = i18nManager.getLocaleOrNull(enabledLocaleKey);
				String translation = Util.createPackageTranslator(UserModule.class, loc).translate(predfinedI18nKey);
				if(translation.length() < 256 && !translation.startsWith(p)) {
					Locale overlayLocale = allOverlays.get(loc);
					I18nItem item = i18nManager.getI18nItem(UserModule.class.getPackage().getName(), customI18nKey, overlayLocale);
					i18nManager.saveOrUpdateI18nItem(item, translation);
				}
			}
		}
	}

	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}
}
