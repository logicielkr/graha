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

import javax.servlet.http.HttpServletRequest;
import kr.graha.lib.Record;

/**
 * Graha(그라하) Servlet 확장 인터페이스 구현 예제

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class ServletAdapterImpl implements ServletAdapter {
	public void execute(HttpServletRequest request, Record params) {
		params.put("graha.home", "myhome");
	}
}