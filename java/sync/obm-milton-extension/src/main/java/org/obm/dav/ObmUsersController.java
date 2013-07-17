/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2013  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.dav;

import fr.aliacom.obm.common.session.SessionManagement;
import fr.aliacom.obm.common.user.ObmUser;
import fr.aliacom.obm.common.user.UserService;
import io.milton.annotations.AccessControlList;
import io.milton.annotations.Authenticate;
import io.milton.annotations.ChildOf;
import io.milton.annotations.ChildrenOf;
import io.milton.annotations.ResourceController;
import io.milton.annotations.Users;
import io.milton.http.HttpManager;
import io.milton.http.Request;
import io.milton.resource.AccessControlledResource;

import java.util.Collections;
import java.util.List;

import org.obm.dav.ObmRootController.UsersHome;
import org.obm.sync.auth.AccessToken;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

@ResourceController
public class ObmUsersController {

	public static AccessToken getAccessToken() {
		return (AccessToken) HttpManager.request().getAttributes().get("accessToken");
	}

	@Inject
	private UserService userService;

	@Inject
	private SessionManagement sessionManagement;

	@ChildrenOf
	@Users
	// ties in with the @AccessControlList and @Authenticate methods below
	public List<ObmUser> getUsers(UsersHome home) {
		return Collections.EMPTY_LIST;
	}

	@ChildOf
	@Users
	public ObmUser findUserByName(UsersHome home, String userName) {
		Iterable<String> loginParts = Splitter.on('@').split(userName);
		String login = Iterables.get(loginParts, 0);
		String domain = Iterables.get(loginParts, 1);
		return userService.getUserFromLogin(login, domain);
	}

	@Authenticate
	public Boolean checkPassword(ObmUser user, String password) {
		Request request = HttpManager.request();
		String clientIp = request.getHeaders().get("X-Forwarded-For");
		String remoteIp = request.getRemoteAddr();
		
		AccessToken accessToken = sessionManagement.login(user.getLogin(),
				password, "MiltonDav", clientIp, remoteIp, null, null, false);
		setLoggedUser(accessToken, request);
		return accessToken != null;
	}

	@AccessControlList
	public List<AccessControlledResource.Priviledge> getAccessControlList(ObmUser target, ObmUser currentUser) {
		if (currentUser != null) {
			return AccessControlledResource.READ_WRITE;
		}
		return ImmutableList.of();
	}

	private void setLoggedUser(AccessToken accessToken, Request request) {
		request.getAttributes().put("accessToken", accessToken);
	}
}
