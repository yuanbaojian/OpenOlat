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
package org.olat.course.site.model;

/**
 * 
 * XStream mapping class
 * 
 * Initial date: 17.09.2013<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class LanguageConfiguration {
	
	private boolean defaultConfiguration;
	private String language;
	private String title;
	private String repoSoftKey;
	
	public LanguageConfiguration() {
		//
	}
	
	public LanguageConfiguration(String language) {
		this.language = language;
	}
	
	public LanguageConfiguration(String language, String repoSoftKey, boolean defaultConfiguration) {
		this.language = language;
		this.repoSoftKey = repoSoftKey;
		this.defaultConfiguration = defaultConfiguration;
	}
	
	public boolean isDefaultConfiguration() {
		return defaultConfiguration;
	}
	
	public void setDefaultConfiguration(boolean defaultConfiguration) {
		this.defaultConfiguration = defaultConfiguration;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getRepoSoftKey() {
		return repoSoftKey;
	}
	
	public void setRepoSoftKey(String repoSoftKey) {
		this.repoSoftKey = repoSoftKey;
	}
}
