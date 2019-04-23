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
package org.olat.modules.curriculum;

import org.olat.NewControllerFactory;
import org.olat.core.configuration.AbstractSpringModule;
import org.olat.core.configuration.ConfigOnOff;
import org.olat.core.util.StringHelper;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.olat.modules.curriculum.site.CurriculumManagementContextEntryControllerCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 9 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class CurriculumModule extends AbstractSpringModule implements ConfigOnOff {
	
	private static final String CURRICULUM_ENABLED = "curriculum.enabled";
	private static final String CURRICULUM_IN_MY_COURSES_ENABLED = "curriculum.in.my.courses.enabled";
	
	@Value("${curriculum.enabled:true}")
	private boolean enabled;
	@Value("${curriculum.in.my.courses.enabled:true}")
	private boolean curriculumInMyCourses;
	
	@Autowired
	private CurriculumModule(CoordinatorManager coordinateManager) {
		super(coordinateManager);
	}

	@Override
	public void init() {
		NewControllerFactory.getInstance().addContextEntryControllerCreator("CurriculumAdmin",
				new CurriculumManagementContextEntryControllerCreator(this));
		
		updateProperties();
	}

	@Override
	protected void initFromChangedProperties() {
		updateProperties();
	}
	
	private void updateProperties() {
		String enabledObj = getStringPropertyValue(CURRICULUM_ENABLED, true);
		if(StringHelper.containsNonWhitespace(enabledObj)) {
			enabled = "true".equals(enabledObj);
		}
		
		String enabledInMyCoursesObj = getStringPropertyValue(CURRICULUM_IN_MY_COURSES_ENABLED, true);
		if(StringHelper.containsNonWhitespace(enabledInMyCoursesObj)) {
			curriculumInMyCourses = "true".equals(enabledInMyCoursesObj);
		}
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		setStringProperty(CURRICULUM_ENABLED, Boolean.toString(enabled), true);
	}
	
	public boolean isCurriculumManaged() {
		return true;
	}

	public boolean isCurriculumInMyCourses() {
		return curriculumInMyCourses;
	}

	public void setCurriculumInMyCourses(boolean curriculumInMyCourses) {
		this.curriculumInMyCourses = curriculumInMyCourses;
		setStringProperty(CURRICULUM_IN_MY_COURSES_ENABLED, Boolean.toString(curriculumInMyCourses), true);
	}
	
	

}
