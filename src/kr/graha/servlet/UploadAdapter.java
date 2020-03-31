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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import kr.graha.lib.Record;
import kr.graha.lib.XMLGenerator;
import kr.graha.lib.LogHelper;
import kr.graha.lib.FileHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Element;


/**
 * Graha(그라하) 파일 업로드 처리기
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class UploadAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public UploadAdapter() {
		LogHelper.setLogLevel(logger);
	}
	public void execute(HttpServletRequest request, List<FileItem> fields, Element query, Record params) throws UnsupportedEncodingException, Exception {
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem fileItem = it.next();
			boolean isFormField = fileItem.isFormField();
			if(isFormField) {
				if(logger.isLoggable(Level.FINE)) {
					logger.fine(fileItem.getFieldName());
				}
				if(fileItem.getFieldName().startsWith("_deletefile_.")) {
					Record result = FileHelper.getFilePath2(fileItem.getFieldName().substring("_deletefile_.".length()), params, query);
					if(result == null) {
						continue;
					}
					String path = result.getString("_system.filepath");
					
					if(path == null) {
						continue;
					}

					File f = new File(path + java.io.File.separator + fileItem.getString(request.getCharacterEncoding()));
					if(f.exists()) {
						f.delete();
					}
				}
			}
		}

		it = fields.iterator();
		int idx = 0;
		while (it.hasNext()) {
			FileItem fileItem = it.next();
			boolean isFormField = fileItem.isFormField();
			if(!isFormField) {
				if(logger.isLoggable(Level.FINE)) {
					logger.fine(fileItem.getFieldName());
				}
				if(fileItem.getSize() == 0) {
					continue;
				}
				Record result = FileHelper.getFilePath2(fileItem.getFieldName(), params, query);
				if(result == null) {
					if(logger.isLoggable(Level.WARNING)) {
						logger.warning("result is null");
					}
					continue;
				}
				String path = result.getString("_system.filepath");
				
				if(path == null) {
					if(logger.isLoggable(Level.WARNING)) {
						logger.warning("path is null");
					}
					continue;
				}
				
				File f = new File(path + File.separator);
				
				if(!f.exists()) {
					f.mkdirs();
				}
				int index = 0;
				while(true) {
					if(index == 0) {
						f = new File(path + java.io.File.separator + FilenameUtils.getName(fileItem.getName()));
					} else {
						if(FilenameUtils.getExtension(FilenameUtils.getName(fileItem.getName())).equals("")) {
							f = new File(path + java.io.File.separator + FilenameUtils.removeExtension(FilenameUtils.getName(fileItem.getName())) + "-" + index);
						} else {
							f = new File(path + java.io.File.separator + FilenameUtils.removeExtension(FilenameUtils.getName(fileItem.getName())) + "-" + index + "." + FilenameUtils.getExtension(FilenameUtils.getName(fileItem.getName())));
						}
					}
					if(!f.exists()) {
						break;
					}
					index++;
				}
				fileItem.write(f);
				params.put("uploaded.file.path." + idx, f.getPath());
				idx++;
			}
		}
	}
}