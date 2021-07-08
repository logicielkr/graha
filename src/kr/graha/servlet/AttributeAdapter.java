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
 * Graha(그라하) Attribute 처리기
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class AttributeAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected AttributeAdapter() {
		LOG.setLogLevel(logger);
	}
	protected void execute(HttpServletRequest request, Record params) {
		Enumeration<String> e = request.getAttributeNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			params.put("att." + key, request.getAttribute(key));
		}
	}
}