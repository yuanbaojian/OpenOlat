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
package org.olat.restapi.repository.course.config;

import org.olat.course.ICourse;
import org.olat.course.nodes.CourseNode;
import org.olat.course.nodes.iq.IQEditController;
import org.olat.course.nodes.iq.QTIResourceTypeModule;
import org.olat.ims.qti.process.AssessmentInstance;
import org.olat.modules.ModuleConfiguration;
import org.olat.repository.RepositoryEntry;
import org.olat.restapi.repository.course.AbstractCourseNodeWebService.CustomConfigDelegate;

public class CustomConfigFactory {
	


	public static CustomConfigDelegate getTestCustomConfig(RepositoryEntry repoEntry) {
		return new OlatTestCustomConfig(repoEntry);
	}
	
	public static CustomConfigDelegate getSurveyCustomConfig(RepositoryEntry repoEntry) {
		return new OlatSurveyCustomConfig(repoEntry);
	}
	
	/* CustomConfigDelegate implementations */
	public static class OlatTestCustomConfig implements CustomConfigDelegate {
		private RepositoryEntry testRepoEntry;

		@Override
		public boolean isValid() {
			return testRepoEntry != null;
		}

		public OlatTestCustomConfig(RepositoryEntry testRepoEntry) {
			this.testRepoEntry = testRepoEntry;
		}

		@Override
		public void configure(ICourse course, CourseNode newNode, ModuleConfiguration moduleConfig) {
			moduleConfig.set(IQEditController.CONFIG_KEY_REPOSITORY_SOFTKEY, testRepoEntry.getSoftkey());
			if (QTIResourceTypeModule.isOnyxTest(testRepoEntry.getOlatResource())) {
				moduleConfig.set(IQEditController.CONFIG_KEY_TYPE_QTI, IQEditController.CONFIG_VALUE_QTI2);
			} else if (QTIResourceTypeModule.isQtiWorks(testRepoEntry.getOlatResource())) {
				moduleConfig.set(IQEditController.CONFIG_KEY_TYPE_QTI, IQEditController.CONFIG_VALUE_QTI21);
			} else {
				moduleConfig.set(IQEditController.CONFIG_KEY_TYPE_QTI, IQEditController.CONFIG_VALUE_QTI1);
			}

		}
	}
	
	public static class OlatSurveyCustomConfig implements CustomConfigDelegate {
		private RepositoryEntry surveyRepoEntry;

		public OlatSurveyCustomConfig(RepositoryEntry surveyRepoEntry) {
			this.surveyRepoEntry = surveyRepoEntry;
		}

		@Override
		public boolean isValid() {
			return true;
		}

		@Override
		public void configure(ICourse course, CourseNode newNode, ModuleConfiguration moduleConfig) {
			moduleConfig.set(IQEditController.CONFIG_KEY_REPOSITORY_SOFTKEY, surveyRepoEntry.getSoftkey());
			moduleConfig.set(IQEditController.CONFIG_KEY_ENABLEMENU, new Boolean(true));
			moduleConfig.set(IQEditController.CONFIG_KEY_SEQUENCE, AssessmentInstance.QMD_ENTRY_SEQUENCE_ITEM);
			moduleConfig.set(IQEditController.CONFIG_KEY_TYPE, AssessmentInstance.QMD_ENTRY_TYPE_SURVEY);
			moduleConfig.set(IQEditController.CONFIG_KEY_SUMMARY, AssessmentInstance.QMD_ENTRY_SUMMARY_NONE);
			if (QTIResourceTypeModule.isOnyxTest(surveyRepoEntry)) {
				moduleConfig.set(IQEditController.CONFIG_KEY_TYPE_QTI, IQEditController.CONFIG_VALUE_QTI2);
			} else {
				moduleConfig.set(IQEditController.CONFIG_KEY_TYPE_QTI, IQEditController.CONFIG_VALUE_QTI1);
			}
		}
	}
}