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
package org.olat.login.oauth.spi;

import org.json.JSONException;
import org.json.JSONObject;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.StringHelper;
import org.olat.login.oauth.OAuthLoginModule;
import org.olat.login.oauth.OAuthSPI;
import org.olat.login.oauth.model.OAuthUser;
import org.scribe.builder.api.Api;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 15.07.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class OpenIdConnectProvider implements OAuthSPI {
	
	private static final OLog log = Tracing.createLoggerFor(Google2Provider.class);

	@Autowired
	private OAuthLoginModule oauthModule;
	
	@Override
	public boolean isEnabled() {
		return oauthModule.isOpenIdConnectIFEnabled();
	}
	
	@Override
	public boolean isRootEnabled() {
		return oauthModule.isOpenIdConnectIFRootEnabled();
	}
	
	@Override
	public boolean isImplicitWorkflow() {
		return true;
	}

	@Override
	public Api getScribeProvider() {
		return new OpenIdConnectApi();
	}

	@Override
	public String getName() {
		return "OpenIDConnect";
	}

	@Override
	public String getProviderName() {
		return "OPENIDCO";
	}

	@Override
	public String getIconCSS() {
		return "o_icon o_icon_provider_openid";
	}

	@Override
	public String getAppKey() {
		return oauthModule.getOpenIdConnectIFApiKey();
	}

	@Override
	public String getAppSecret() {
		return oauthModule.getOpenIdConnectIFApiSecret();
	}

	@Override
	public String[] getScopes() {
		return new String[] { "openid", "email" };
	}

	@Override
	public OAuthUser getUser(OAuthService service, Token accessToken) {
		try {
			String idToken = accessToken.getToken();
			JSONWebToken token = JSONWebToken.parse(idToken);
			return parseInfos(token.getPayload());
		} catch (JSONException e) {
			log.error("", e);
			return null;
		}
	}
	
	public OAuthUser parseInfos(String body) {
		OAuthUser user = new OAuthUser();
		
		try {
			JSONObject obj = new JSONObject(body);
			user.setId(getValue(obj, "sub"));
			user.setEmail(getValue(obj, "sub"));
		} catch (JSONException e) {
			log.error("", e);
		}
		
		return user;
	}
	
	private String getValue(JSONObject obj, String property) {
		String value = obj.optString(property);
		return StringHelper.containsNonWhitespace(value) ? value : null;
	}

	@Override
	public String getIssuerIdentifier() {
		return oauthModule.getOpenIdConnectIFIssuer();
	}
}
