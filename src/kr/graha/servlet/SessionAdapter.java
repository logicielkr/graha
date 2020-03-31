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

import java.util.Enumeration;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import kr.graha.lib.Record;
import kr.graha.lib.LogHelper;

/**
 * Graha(그라하) HTTP Session 처리기
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class SessionAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public SessionAdapter() {
		LogHelper.setLogLevel(logger);
	}
	public void execute(HttpServletRequest request, Record params) {
		HttpSession session = request.getSession();
		Enumeration<String> e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			params.put("session." + key, session.getAttribute(key));
		}
	}
}