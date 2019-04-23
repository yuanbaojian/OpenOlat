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
package org.olat.core.gui.components.form.flexible.impl.elements;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.olat.core.CoreSpringFactory;
import org.olat.core.dispatcher.mapper.Mapper;
import org.olat.core.dispatcher.mapper.MapperService;
import org.olat.core.dispatcher.mapper.manager.MapperKey;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.elements.AutoCompleter;
import org.olat.core.gui.control.Disposable;
import org.olat.core.gui.control.generic.ajax.autocompletion.AutoCompleterListReceiver;
import org.olat.core.gui.control.generic.ajax.autocompletion.ListProvider;
import org.olat.core.gui.media.JSONMediaResource;
import org.olat.core.gui.media.MediaResource;
import org.olat.core.util.StringHelper;
import org.olat.core.util.UserSession;

/**
 * 
 * Initial date: 20.11.2015<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AutoCompleterImpl extends AbstractTextElement implements AutoCompleter, Disposable {
	
	private static final String PARAM_QUERY = "term";
	
	private final AutoCompleterComponent component;
	
	private AutoCompleterMapper mapper;
	private MapperKey mapperKey;
	
	private String noResults;
	private String key;
	private int minLength = 3;
	
	public AutoCompleterImpl(String id, String name) {
		super(id, name, false);
		component = new AutoCompleterComponent(id, name, this);
	}

	@Override
	protected Component getFormItemComponent() {
		return component;
	}

	@Override
	public void setListProvider(ListProvider provider, UserSession usess) {
		mapper = new AutoCompleterMapper(provider);
		mapperKey = CoreSpringFactory.getImpl(MapperService.class).register(usess, mapper);
	}

	@Override
	public String getMapperUri() {
		return mapperKey.getUrl();
	}
	
	@Override
	public void setDomReplacementWrapperRequired(boolean required) {
		//
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
		if(component != null) {
			component.setDirty(true);
		}
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	@Override
	public void evalFormRequest(UserRequest ureq) {
		String paramId = component.getFormDispatchId();
		String paramValue = getRootForm().getRequestParameter(paramId);
		// normalize the values
		if(paramValue != null && paramValue.trim().length() == 0) {
			paramValue = null;
		}
		String currentValue = getValue();
		if(currentValue != null && currentValue.trim().length() == 0) {
			currentValue = null;
		}
		if ((paramValue == null && currentValue != null)
				|| (paramValue != null && currentValue == null)
				|| (paramValue != null && currentValue != null && !paramValue.equals(getValue()))) {
			setKey(null);
			setValue(paramValue);
		}
	}
	
	@Override
	public void dispose() {
		if(mapperKey != null) {
			CoreSpringFactory.getImpl(MapperService.class).cleanUp(Collections.singletonList(mapperKey));
		}
	}

	private class  AutoCompleterMapper implements Mapper {
		
		private final ListProvider provider;
		
		public AutoCompleterMapper(ListProvider provider) {
			this.provider = provider;
		}

		@Override
		public MediaResource handle(String relPath, HttpServletRequest request) {
			// Read query and generate JSON result
			String lastN = request.getParameter(PARAM_QUERY);
			JSONArray result;
			if(StringHelper.containsNonWhitespace(lastN)) {
				AutoCompleterListReceiver receiver = new AutoCompleterListReceiver(noResults, false);
				provider.getResult(lastN, receiver);
				result = receiver.getResult(); 
			} else {
				result = new JSONArray();
			}
			return new JSONMediaResource(result, "UTF-8");
		}
	}
}