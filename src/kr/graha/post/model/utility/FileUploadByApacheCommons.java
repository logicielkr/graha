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


package kr.graha.post.model.utility;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import kr.graha.post.lib.Record;
import kr.graha.helper.LOG;
import kr.graha.helper.STR;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import kr.graha.post.model.Files;
import java.io.UnsupportedEncodingException;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import java.util.Iterator;
import java.nio.charset.StandardCharsets;

/**
 * Graha(그라하) 파일 업로드 처리
 * Apache Commons File Upload 를 이용해서 처리
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class FileUploadByApacheCommons {
	public static List<FilePart> getFileParts(HttpServletRequest request, Record params, Files files)
		throws UnsupportedEncodingException
	{
		List<FilePart> fileParts = null;
		ServletRequestContext src = new ServletRequestContext(request);
		boolean isMultipartContent = ServletFileUpload.isMultipartContent(src);
		List<FileItem> fields = null;
		if(isMultipartContent) {
			request.setCharacterEncoding("UTF-8");
			Iterator<FileItem> it = null;
			DiskFileItemFactory factory = new DiskFileItemFactory();
			if(files != null) {
				if(STR.valid(files.getMaxMemorySize())) {
					factory.setSizeThreshold(Integer.valueOf(files.getMaxMemorySize()));
				}
				if(STR.valid(files.getTempDirectory())) {
					java.io.File f = new java.io.File(files.getTempDirectory());
					if(f.exists() && f.isDirectory()) {
						factory.setRepository(f);
					}
				}
			}
			ServletFileUpload upload = new ServletFileUpload(factory);
			if(files != null && STR.valid(files.getMaxRequestSize())) {
				upload.setSizeMax(Long.valueOf(files.getMaxRequestSize()));
			}
			upload.setHeaderEncoding(request.getCharacterEncoding());
			try {
				fields = upload.parseRequest(src);
			} catch (FileUploadException e1) {
				LOG.severe(e1);
			}
			if(fields != null) {
				it = fields.iterator();
				while (it.hasNext()) {
					FileItem fileItem = it.next();
					boolean isFormField = fileItem.isFormField();
					if(isFormField) {
						params.puts(Record.key(Record.PREFIX_TYPE_PARAM, fileItem.getFieldName()), fileItem.getString(StandardCharsets.UTF_8.name()));
					} else {
						if(fileParts == null) {
							fileParts = new ArrayList<FilePart>();
						}
						fileParts.add(new FilePart(fileItem));
					}
				}
			}
		}
		return fileParts;
	}
}
