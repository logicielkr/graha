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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.graha.lib.Record;
import kr.graha.lib.XMLGenerator;
import kr.graha.helper.LOG;
import kr.graha.lib.FileHelper;
import org.w3c.dom.Element;


/**
 * Graha(그라하) 파일 다운로드 처리기
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class DownloadAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected DownloadAdapter() {
		LOG.setLogLevel(logger);
	}
	protected void execute(HttpServletRequest request, HttpServletResponse response, Element query, Record params) throws IOException{
		
		Enumeration<String> p = request.getParameterNames();
		while (p.hasMoreElements()) {
			String key = p.nextElement();
			params.puts(key + ".0", params.getString("param." + key));
		}

		String filePath = null;
		if(request.getPathInfo().trim().indexOf(".xml/download/") > 0) {
			filePath = request.getPathInfo().trim().substring(request.getPathInfo().trim().indexOf(".xml/download/") + ".xml/download/".length());
		} else if(request.getPathInfo().trim().indexOf(".html/download/") > 0) {
			filePath = request.getPathInfo().trim().substring(request.getPathInfo().trim().indexOf(".html/download/") + ".html/download/".length());
		}
		if(
			Charset.defaultCharset().equals(StandardCharsets.US_ASCII)
			|| !(
				request.getServletContext().getMajorVersion() > 3
				|| (
					request.getServletContext().getMajorVersion() >= 3
					&& request.getServletContext().getMinorVersion() > 0
				)
			)
		) {
			filePath = new String(filePath.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		}
		
		Record result = FileHelper.getFilePath2(filePath.substring(0, filePath.indexOf("/")) + ".0", params, query);
		if(result != null && !result.isEmpty()) {
			String basePath = null;
			if(result.isArray("_system.filepath")) {
				List paths = result.getArray("_system.filepath");
				if(paths != null) {
					for(int x = 0; x < paths.size(); x++) {
						Object path = paths.get(x);
						if(path != null && path instanceof String) {
							basePath = (String)path;
							break;
						}
					}
				}
			} else {
				basePath = result.getString("_system.filepath");
			}
			if(logger.isLoggable(Level.FINER)) { logger.finer(basePath + java.io.File.separator + filePath.substring(filePath.indexOf("/") + 1)); }
			File f = new File(basePath + java.io.File.separator + filePath.substring(filePath.indexOf("/") + 1));
			if(f.exists()) {
				if(logger.isLoggable(Level.CONFIG)) { logger.config("File Path = " + f.getPath()); }
				response.setContentLength((int)f.length());
				response.setDateHeader("Last-Modified", f.lastModified());
				response.setHeader("Accept-Ranges", "bytes");
				String mimeType = request.getServletContext().getMimeType(f.getName());
				if(mimeType != null && !mimeType.equals("")) {
					response.setContentType(request.getServletContext().getMimeType(f.getName()));
				}
				ServletOutputStream out = null;
				FileInputStream fis = null;
				try {
					out = response.getOutputStream();
					fis = new FileInputStream(f);
					byte[] buffer = new byte[8192];
					int len = 0;
					while((len = fis.read(buffer)) >= 0) {
						out.write(buffer, 0, len);
					}
					out.flush();
					out.close();
					out = null;
					fis.close();
					fis = null;
				} catch(IOException e) {
					if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
					throw e;
				} finally {
					if(out != null) {
						try {
							out.close();
						} catch (IOException e) {
							if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
						}
					}
					if(fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
						}
					}
				}
			} else {
				if(logger.isLoggable(Level.CONFIG)) { logger.config("[SC_NOT_FOUND]File Path = " + f.getPath()); }
				response.sendError(404);
			}
		} else {
			if(logger.isLoggable(Level.CONFIG)) { logger.config("[SC_INTERNAL_SERVER_ERROR]File Path = " + filePath); }
			response.sendError(500);
		}
	}
}