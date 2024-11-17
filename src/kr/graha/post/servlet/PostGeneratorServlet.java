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

package kr.graha.post.servlet;

import kr.graha.post.model.Querys;
import kr.graha.post.model.QueryImpl;
import kr.graha.post.model.QueryXMLImpl;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.graha.helper.LOG;
import javax.servlet.ServletException;
import kr.graha.post.lib.Record;

/**
 * Graha(그라하) 서블릿
 * HTTP 요청(GET/POST)을 처리한다.
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class PostGeneratorServlet extends HttpServlet {
	public void init() {
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.execute(request, response);
	}
	private void sendError(HttpServletResponse response, int sc) {
		try {
			response.sendError(sc);
		} catch (IOException e) {
			LOG.severe(e); 
		}
	}
	protected void execute(HttpServletRequest request, HttpServletResponse response) {
		Record params = new Record();
		QueryXMLImpl query = Querys.load(request, params);
		if(query == null) {
			this.sendError(response, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if(
			query.getRequestType() == QueryImpl.REQUEST_TYPE_XML_DOWNLOAD ||
			query.getRequestType() == QueryImpl.REQUEST_TYPE_HTML_DOWNLOAD
		) {
			try {
				int result = query.download(request, response, this.getServletConfig(), params);
				if(result != HttpServletResponse.SC_OK) {
					this.sendError(response, result);
					return;
				}
			} catch (Exception e) {
				LOG.severe(e); 
				this.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
		} else if(query.getRequestType() == QueryImpl.REQUEST_TYPE_XSL) {
			try {
				int result = query.xsl(request, response, this.getServletConfig(), params);
				if(result != HttpServletResponse.SC_OK) {
					this.sendError(response, result);
					return;
				}
			} catch (Exception e) {
				LOG.severe(e); 
				this.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
		} else {
			try {
				int result = query.execute(request, response, this.getServletConfig(), params);
				if(result != HttpServletResponse.SC_OK) {
					this.sendError(response, result);
					return;
				}
			} catch (Exception e) {
				LOG.severe(e); 
				this.sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
		}
	}
}
