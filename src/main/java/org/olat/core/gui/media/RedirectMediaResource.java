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
* <p>
*/ 

package org.olat.core.gui.media;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;

/**
 * @author Felix Jost
 */
public class RedirectMediaResource implements MediaResource {
	
	private static final OLog log = Tracing.createLoggerFor(RedirectMediaResource.class);

	private String redirectURL;

	/**
	 * @param redirectURL
	 */
	public RedirectMediaResource(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	
	@Override
	public long getCacheControlDuration() {
		return 0;
	}

	@Override
	public boolean acceptRanges() {
		return false;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public Long getSize() {
		return null;
	}

	@Override
	public InputStream getInputStream() {
		return null;
	}

	@Override
	public Long getLastModified() {
		return null;
	}

	@Override
	public void prepare(HttpServletResponse hres) {
		try {
			hres.sendRedirect(redirectURL);
		} catch (IOException e) {
			// if redirect failed, we do nothing; the browser may have stopped the
			// tcp/ip or whatever
			log.error("redirect failed: url=" + redirectURL, e);
		} catch (IllegalStateException ise){
			// redirect failed, to find out more about the strange null null exception
			// FIXME:pb:a decide if this catch has to be removed again, after finding problem.
			log.error("redirect failed: url=" + redirectURL, ise);
			//introduced only more debug information but behavior is still the same
			throw(ise);
		}
	}

	@Override
	public void release() {
	// nothing to do
	}

}