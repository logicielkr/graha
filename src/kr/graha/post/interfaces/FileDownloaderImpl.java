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


package kr.graha.post.interfaces;

import java.nio.file.Path;
import kr.graha.helper.LOG;
import javax.servlet.http.HttpServletRequest;
import kr.graha.post.lib.Record;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Graha(그라하) FileDownloader 표준구현체

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public class FileDownloaderImpl implements FileDownloader {
	public void execute(
		Path path,
		String fileName,
		HttpServletRequest request,
		HttpServletResponse response,
		Record params
	) throws IOException {
		LOG.config("File Path = " + path.toUri().getPath());
		response.setContentLength((int)Files.size(path));
		response.setDateHeader("Last-Modified", Files.getLastModifiedTime(path).toMillis());
		response.setHeader("Accept-Ranges", "bytes");
		String mimeType = request.getServletContext().getMimeType(fileName);
		if(mimeType != null && !mimeType.equals("")) {
			response.setContentType(request.getServletContext().getMimeType(fileName));
		}
		ServletOutputStream out = null;
		InputStream fis = null;
		try {
			out = response.getOutputStream();
			fis = Files.newInputStream(path);
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
			LOG.severe(e);
			throw e;
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOG.severe(e);
				}
			}
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOG.severe(e);
				}
			}
		}
	}
}
