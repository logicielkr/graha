/*
 *
 * Copyright (C) HeonJik, KIM
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */


package kr.graha.servlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import kr.graha.lib.Record;
import kr.graha.helper.LOG;

/**
 * Graha(그라하) UserRole 처리기
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class UserRoleAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected UserRoleAdapter() {
		LOG.setLogLevel(logger);
	}
	protected void execute(HttpServletRequest request, Record params) {
		if(request.getUserPrincipal() instanceof org.apache.catalina.realm.GenericPrincipal) {
			String[] roles = ((org.apache.catalina.realm.GenericPrincipal)request.getUserPrincipal()).getRoles();
			if(roles != null) {
				params.put("header.remote_user_roles", roles);
			}
		} else if(request.getUserPrincipal() instanceof org.apache.catalina.User) {
			Iterator<org.apache.catalina.Role> it = ((org.apache.catalina.User)request.getUserPrincipal()).getRoles();
			ArrayList<String> roles = new ArrayList<String>();
			while(it.hasNext()) {
				roles.add(((org.apache.catalina.Role)it.next()).getRolename());
			}
			params.put("header.remote_user_roles", roles.toArray(new String[]{}));
		} else {
			if(request.getUserPrincipal() != null) {
				logger.fine(request.getUserPrincipal().getClass().getName());
			}
		}
	}
}