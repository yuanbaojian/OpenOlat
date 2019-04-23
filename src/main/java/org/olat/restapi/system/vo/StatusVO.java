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
package org.olat.restapi.system.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * Initial date: 15.02.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "statusVO")
public class StatusVO {

	@XmlAttribute(name="writeFile", required=true)
	private boolean writeFile;
	@XmlAttribute(name="writeFileInMilliseconds", required=true)
	private long writeFileInMilliseconds;
	@XmlAttribute(name="writeDb", required=true)
	private boolean writeDb;
	@XmlAttribute(name="writeDbInMilliseconds", required=true)
	private long writeDbInMilliseconds;
	@XmlAttribute(name="secureAuthenticatedCount", required=true)
	private int secureAuthenticatedCount;
	@XmlAttribute(name="concurrentDispatchThreads", required=true)
	private long concurrentDispatchThreads;
	
	public boolean isWriteFile() {
		return writeFile;
	}
	
	public void setWriteFile(boolean writeFile) {
		this.writeFile = writeFile;
	}
	
	public long getWriteFileInMilliseconds() {
		return writeFileInMilliseconds;
	}

	public void setWriteFileInMilliseconds(long writeFileInMilliseconds) {
		this.writeFileInMilliseconds = writeFileInMilliseconds;
	}

	public boolean isWriteDb() {
		return writeDb;
	}
	
	public void setWriteDb(boolean writeDb) {
		this.writeDb = writeDb;
	}

	public long getWriteDbInMilliseconds() {
		return writeDbInMilliseconds;
	}

	public void setWriteDbInMilliseconds(long writeDbInMilliseconds) {
		this.writeDbInMilliseconds = writeDbInMilliseconds;
	}

	public int getSecureAuthenticatedCount() {
		return secureAuthenticatedCount;
	}

	public void setSecureAuthenticatedCount(int secureAuthenticatedCount) {
		this.secureAuthenticatedCount = secureAuthenticatedCount;
	}

	public long getConcurrentDispatchThreads() {
		return concurrentDispatchThreads;
	}

	public void setConcurrentDispatchThreads(long concurrentDispatchThreads) {
		this.concurrentDispatchThreads = concurrentDispatchThreads;
	}
	
}
