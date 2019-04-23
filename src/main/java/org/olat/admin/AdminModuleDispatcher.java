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

package org.olat.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.olat.admin.sysinfo.InfoMessageManager;
import org.olat.core.CoreSpringFactory;
import org.olat.core.dispatcher.Dispatcher;
import org.olat.core.dispatcher.DispatcherModule;
import org.olat.core.gui.media.ServletUtil;
import org.olat.core.util.session.UserSessionManager;

/**
 * 
 * This dispatcher acts as proxy to receive the message.
 * @see org.olat.admin.AdminModule#setMaintenanceMessage(HttpServletRequest, HttpServletResponse) 
 * 
 * <P>
 * Initial Date:  13.06.2006 <br>
 * @author patrickb
 * @author christian guretzki
 */
public class AdminModuleDispatcher implements Dispatcher {
	
	private final static  String PARAMETER_CMD          = "cmd"; 
	private final static  String PARAMETER_MSG          = "msg";
	private final static  String PARAMETER_MAX_MESSAGE  = "maxsessions";
	private final static  String PARAMETER_NBR_SESSIONS = "nbrsessions";
	private final static  String PARAMETER_SESSIONTIMEOUT ="sec";
	
	private final static  String CMD_SET_MAINTENANCE_MESSAGE    = "setmaintenancemessage";
	private final static  String CMD_SET_INFO_MESSAGE    				= "setinfomessage"; 
	private final static  String CMD_SET_LOGIN_BLOCKED          = "setloginblocked";
	private final static  String CMD_SET_LOGIN_NOT_BLOCKED      = "setloginnotblocked";
	private final static  String CMD_SET_MAX_SESSIONS           = "setmaxsessions";
	private final static  String CMD_INVALIDATE_ALL_SESSIONS    = "invalidateallsessions";
	private final static  String CMD_INVALIDATE_OLDEST_SESSIONS = "invalidateoldestsessions";
	private final static  String CMD_SET_SESSIONTIMEOUT         = "sessiontimeout";
	
	
	/** 
	 * @see org.olat.core.dispatcher.Dispatcher#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		String cmd = request.getParameter(PARAMETER_CMD);
		if (cmd.equalsIgnoreCase(CMD_SET_MAINTENANCE_MESSAGE) || cmd.equalsIgnoreCase(CMD_SET_INFO_MESSAGE)) {
			handleSetMaintenanceOrInfoMessage(request, response, cmd);
		} else {
			if (CoreSpringFactory.getImpl(AdminModule.class).checkSessionAdminToken(request)) {
				handleSessionsCommand(request, response, cmd);
			} else {
				DispatcherModule.sendForbidden(request.getPathInfo(), response);
			}
		}
	}

	/**
	 * Handle session-administration commands (setLoginBlocked, setLoginNotBlocked, setMaxSession, invalidateAllSessions,
	 * ïnvalidateOldestSessions).
	 */
	private void handleSessionsCommand(HttpServletRequest request, HttpServletResponse response, String cmd) {
		if (cmd.equalsIgnoreCase(CMD_SET_LOGIN_BLOCKED)) {
			CoreSpringFactory.getImpl(AdminModule.class).setLoginBlocked(true, false);
			ServletUtil.serveStringResource(request, response, "Ok, login blocked");
		} else if (cmd.equalsIgnoreCase(CMD_SET_LOGIN_NOT_BLOCKED)) {
			CoreSpringFactory.getImpl(AdminModule.class).setLoginBlocked(false, false);
			ServletUtil.serveStringResource(request, response, "Ok, login no more blocked");
		}else if (cmd.equalsIgnoreCase(CMD_SET_MAX_SESSIONS)) {
			handleSetMaxSessions(request, response);
		}else if (cmd.equalsIgnoreCase(CMD_INVALIDATE_ALL_SESSIONS)) {
			CoreSpringFactory.getImpl(UserSessionManager.class).invalidateAllSessions();
			ServletUtil.serveStringResource(request, response, "Ok, Invalidated all sessions");
		}else if (cmd.equalsIgnoreCase(CMD_INVALIDATE_OLDEST_SESSIONS)) {
			handleInvidateOldestSessions(request, response);
		}else if(cmd.equalsIgnoreCase(CMD_SET_SESSIONTIMEOUT)) {
			handleSetSessiontimeout(request, response);
		} else {
			ServletUtil.serveStringResource(request, response, "NOT OK, unknown command=" + cmd);
		}
	}

	/**
	 * Handle setMaxSessions command, extract parameter maxsessions form request and call method on AdminModule.
	 * @param request
	 * @param response
	 */
	private void handleSetMaxSessions(HttpServletRequest request, HttpServletResponse response) {
		String maxSessionsString = request.getParameter(PARAMETER_MAX_MESSAGE);
		if (maxSessionsString == null || maxSessionsString.equals("")) {
			ServletUtil.serveStringResource(request, response, "NOT_OK, missing parameter " + PARAMETER_MAX_MESSAGE);
		} else {
			try {
				int maxSessions = Integer.parseInt(maxSessionsString);
				CoreSpringFactory.getImpl(AdminModule.class).setMaxSessions(maxSessions);
				ServletUtil.serveStringResource(request, response, "Ok, max-session=" + maxSessions);
			} catch (NumberFormatException nbrException) {
				ServletUtil.serveStringResource(request, response, "NOT_OK, parameter " + PARAMETER_MAX_MESSAGE + " must be a number");
			}
		}
	}
	
	private void handleSetSessiontimeout(HttpServletRequest request, HttpServletResponse response) {
		String paramStr = request.getParameter(PARAMETER_SESSIONTIMEOUT);
		if (paramStr == null || paramStr.equals("")) {
			ServletUtil.serveStringResource(request, response, "NOT_OK, missing parameter " + PARAMETER_SESSIONTIMEOUT);
		} else {
			try {
				int sessionTimeout = Integer.parseInt(paramStr);
				CoreSpringFactory.getImpl(UserSessionManager.class).setGlobalSessionTimeout(sessionTimeout);
				ServletUtil.serveStringResource(request, response, "Ok, sessiontimeout=" + sessionTimeout);
			} catch (NumberFormatException nbrException) {
				ServletUtil.serveStringResource(request, response, "NOT_OK, parameter " + PARAMETER_SESSIONTIMEOUT + " must be a number");
			}
		}
	}
	

	/**
	 * Handle invalidateOldestSessions command, extract parameter nbrsessions form request and call method on AdminModule.
	 * @param request
	 * @param response
	 */
	private void handleInvidateOldestSessions(HttpServletRequest request, HttpServletResponse response) {
		String nbrSessionsString = request.getParameter(PARAMETER_NBR_SESSIONS);
		if (nbrSessionsString == null || nbrSessionsString.equals("")) {
			ServletUtil.serveStringResource(request, response, "NOT_OK, missing parameter " + PARAMETER_NBR_SESSIONS);
		} else {
			try {
				int nbrSessions = Integer.parseInt(nbrSessionsString);
				CoreSpringFactory.getImpl(UserSessionManager.class).invalidateOldestSessions(nbrSessions);
				ServletUtil.serveStringResource(request, response, "Ok, Invalidated oldest sessions, nbrSessions=" + nbrSessions);
			} catch (NumberFormatException nbrException) {
				ServletUtil.serveStringResource(request, response, "NOT_OK, parameter " + PARAMETER_NBR_SESSIONS + " must be a number");
			}
		}
	}

	/**
	 * Handle setMaintenanceMessage command, extract parameter msg form request and call method on AdminModule.
	 * @param request
	 * @param response
	 */
	private void handleSetMaintenanceOrInfoMessage(HttpServletRequest request, HttpServletResponse response, String cmd) {
		AdminModule adminModule = CoreSpringFactory.getImpl(AdminModule.class);
		if (adminModule.checkMaintenanceMessageToken(request)) {
			String message = request.getParameter(PARAMETER_MSG);
			if (cmd.equalsIgnoreCase(CMD_SET_INFO_MESSAGE)){
				InfoMessageManager mrg = (InfoMessageManager) CoreSpringFactory.getBean(InfoMessageManager.class);
				mrg.setInfoMessage(message);
				ServletUtil.serveStringResource(request, response, "Ok, new infoMessage is::" + message);
			} else if (cmd.equalsIgnoreCase(CMD_SET_MAINTENANCE_MESSAGE)){
				adminModule.setMaintenanceMessage(message);
				ServletUtil.serveStringResource(request, response, "Ok, new maintenanceMessage is::" + message);
			}
		} else {
			DispatcherModule.sendForbidden(request.getPathInfo(), response);
		}
	}

	

}
