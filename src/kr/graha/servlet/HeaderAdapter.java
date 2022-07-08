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

import kr.graha.lib.Record;
import kr.graha.helper.LOG;

/**
 * Graha(그라하) HTTP Header 처리기
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class HeaderAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected HeaderAdapter() {
		LOG.setLogLevel(logger);
	}
	protected void execute(HttpServletRequest request, Record params) {
		Enumeration<String> e = request.getHeaderNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			if(key == null) {continue;}
			if(key != null && (key.equals("method") || key.equals("remote_user") || key.equals("remote_addr") || key.equals("remote_host") || key.equals("remote_user_roles") || key.equals("scheme"))) {continue;}
			params.puts("header." + key, request.getHeader(key));
		}
		params.put("header.method", request.getMethod());
		params.put("header.remote_user", request.getRemoteUser());
		params.put("header.remote_addr", request.getRemoteAddr());
		params.put("header.remote_host", request.getRemoteHost());
		params.put("header.scheme", request.getScheme());
	}
}