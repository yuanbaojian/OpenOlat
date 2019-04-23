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
package org.olat.modules.gotomeeting.oauth;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.olat.core.dispatcher.Dispatcher;
import org.olat.core.dispatcher.DispatcherModule;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.UserRequestImpl;
import org.olat.core.id.context.BusinessControlFactory;
import org.olat.core.id.context.HistoryPoint;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.StringHelper;
import org.olat.core.util.UserSession;
import org.olat.core.util.WebappHelper;
import org.olat.login.oauth.OAuthConstants;
import org.olat.modules.gotomeeting.GoToMeetingManager;
import org.olat.modules.gotomeeting.manager.GoToJsonUtil;
import org.olat.modules.gotomeeting.model.GoToOrganizerG2T;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 14 janv. 2019<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class GetGoOAuthDispatcher implements Dispatcher {
	
	private static final OLog log = Tracing.createLoggerFor(GetGoOAuthDispatcher.class);
	
	@Autowired
	private GoToMeetingManager goToMeetingManager;

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		String uriPrefix = DispatcherModule.getLegacyUriPrefix(request);
		UserRequest ureq = null;
		try{
			//upon creation URL is checked for 
			ureq = new UserRequestImpl(uriPrefix, request, response);
		} catch(NumberFormatException nfe) {
			if(log.isDebug()){
				log.debug("Bad Request "+request.getPathInfo());
			}
			DispatcherModule.sendBadRequest(request.getPathInfo(), response);
			return;
		}
		
		try {
			HttpSession sess = request.getSession();
			//OAuth 2.0 hasn't any request token
			Token requestToken = (Token)sess.getAttribute(OAuthConstants.REQUEST_TOKEN);
			OAuthService service = (OAuthService)sess.getAttribute(OAuthConstants.OAUTH_SERVICE);

			// request access token
			String code = request.getParameter("code");
			Token accessToken = service.getAccessToken(requestToken, new Verifier(code));
			String token = accessToken.getToken();
			
			boolean success = false;
			if(StringHelper.containsNonWhitespace(token)) {
				String body = accessToken.getRawResponse();
				GoToOrganizerG2T organizer = GoToJsonUtil.parseToken(body);
				success |= goToMeetingManager.createOrUpdateOrganizer(organizer);
			}
			
			register(ureq, response, success);
		} catch(Exception e) {
			log.error("", e);
		}
	}
	
	private final void register(UserRequest ureq, HttpServletResponse response, boolean success) {
		try {
			UserSession usess = ureq.getUserSession();
			List<HistoryPoint> history = usess.getHistoryStack();
			if(history.isEmpty()) {
				DispatcherModule.redirectToDefaultDispatcher(response);
			} else {
				HistoryPoint point = history.get(history.size() - 1);
				String businessPath = point.getBusinessPath();
				String url = BusinessControlFactory.getInstance()
						.getAuthenticatedURLFromBusinessPathString(businessPath);
				usess.putEntryInNonClearedStore("GETGO_STATUS", Boolean.valueOf(success));
				DispatcherModule.redirectTo(response, url);
			}
		} catch (Exception e) {
			log.error("Redirect failed: url=" + WebappHelper.getServletContextPath() + DispatcherModule.getPathDefault(),e);
		}
	}
}
