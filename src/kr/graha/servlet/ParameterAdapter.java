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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import kr.graha.helper.LOG;
import kr.graha.lib.Record;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

/**
 * Graha(그라하) HTTP Parameter 처리기
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class ParameterAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected ParameterAdapter() {
		LOG.setLogLevel(logger);
	}
	private ServletRequestContext getServletRequestContext(HttpServletRequest request) {
		return new ServletRequestContext(request);
	}
	private List<FileItem> parseRequest(ServletFileUpload upload, ServletRequestContext src) throws FileUploadException {
		return upload.parseRequest(src);
	}
	protected List<FileItem> execute(HttpServletRequest request, Record params, Record info) throws UnsupportedEncodingException {
		ServletContext c = request.getServletContext();
		if(c != null) {
			Enumeration<String> p = c.getInitParameterNames();
			while (p.hasMoreElements()) {
				String key = p.nextElement();
				params.puts("init-param.", c.getInitParameter(key));
			}

		}
		ServletRequestContext src = getServletRequestContext(request);
		boolean isMultipartContent = ServletFileUpload.isMultipartContent(src);
		List<FileItem> fields = null;
		if(isMultipartContent) {
			request.setCharacterEncoding("UTF-8");
			Iterator<FileItem> it = null;
			DiskFileItemFactory factory = new DiskFileItemFactory();
			if(info.hasKey("maxMemorySize")) {
				factory.setSizeThreshold(info.getInt("maxMemorySize"));
			}
			if(info.hasKey("tempDirectory")) {
				File f = new File(info.getString("tempDirectory"));
				if(f.exists() && f.isDirectory()) {
					factory.setRepository(f);
				}
			}
			
			ServletFileUpload upload = new ServletFileUpload(factory);
			if(info.hasKey("maxRequestSize")) {
				upload.setSizeMax(info.getLong("maxRequestSize"));
			}
			
			upload.setHeaderEncoding(request.getCharacterEncoding());
			try {
//				fields = upload.parseRequest(request);
				fields = parseRequest(upload, src);
			} catch (FileUploadException e1) {
				if(logger.isLoggable(Level.SEVERE)) {
					logger.severe(LOG.toString(e1));
				}
			}
			if(fields != null) {
				it = fields.iterator();
				while (it.hasNext()) {
					FileItem fileItem = it.next();
					boolean isFormField = fileItem.isFormField();
					if(isFormField) {
						params.puts("param." + fileItem.getFieldName(), fileItem.getString(request.getCharacterEncoding()));
						if(logger.isLoggable(Level.FINE)) {
							logger.fine("param." + fileItem.getFieldName() + " : " + fileItem.getString(request.getCharacterEncoding()));
						}
					}
				}
			}
		} else {
			if(
				request.getServletContext().getMajorVersion() < 3
				|| (
					request.getServletContext().getMajorVersion() == 3
					&& request.getServletContext().getMinorVersion() == 0
				)
			) {
				request.setCharacterEncoding("ISO-8859-1");
			} else {
				request.setCharacterEncoding("UTF-8");
			}
			Enumeration<String> e = request.getParameterNames();
			
			while (e.hasMoreElements()) {
				String key = e.nextElement();
				String[] values = request.getParameterValues(key);
				if(values != null) {
					for (String value : values) {
						if(value == null) {continue;}
						if(
							request.getServletContext().getMajorVersion() < 3
							|| (
								request.getServletContext().getMajorVersion() == 3
								&& request.getServletContext().getMinorVersion() == 0
							)
						) {
							params.puts("param." + key, new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
							if(logger.isLoggable(Level.FINE)) {
								logger.fine("param." + key + " : " + new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
							}
						} else {
							params.puts("param." + key, value);
							if(logger.isLoggable(Level.FINE)) {
								logger.fine("param." + key + " : " + value);
							}
						}
					}
				}
			}
		}
		return fields;
	}
}