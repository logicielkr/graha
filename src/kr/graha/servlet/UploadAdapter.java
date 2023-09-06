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
import kr.graha.helper.LOG;
import kr.graha.lib.FileHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.w3c.dom.Element;
import java.nio.file.Files;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Graha(그라하) 파일 업로드 처리기
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class UploadAdapter {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected UploadAdapter() {
		LOG.setLogLevel(logger);
	}
	protected URI saveFile(HttpServletRequest request, String path, int idx, FileItem fileItem, URI firstFile, Record params) throws Exception {
		File f = new File(path + File.separator);
		
		if(!f.exists()) {
			f.mkdirs();
		}
		int index = 0;
		String fileName = fileItem.getName().substring(Math.max(fileItem.getName().lastIndexOf('/'), fileItem.getName().lastIndexOf('\\')) + 1);
		if(logger.isLoggable(Level.FINER)) { logger.finer("fileName = " + fileName); }
		URI uri = null;
		while(true) {
			if(index == 0) {
				uri = new URI("file://" + path + java.io.File.separator + java.net.URLEncoder.encode(fileName, "UTF-8"));
			} else {
				if(fileName.lastIndexOf(".") > 0) {
					uri = new URI("file://" + path + java.io.File.separator + java.net.URLEncoder.encode(fileName.substring(0, fileName.lastIndexOf(".")), "UTF-8")  + "-" + index + "." + java.net.URLEncoder.encode(fileName.substring(fileName.lastIndexOf(".") + 1), "UTF-8"));
				} else {
					uri = new URI("file://" + path + java.io.File.separator + java.net.URLEncoder.encode(fileName, "UTF-8") + "-" + index);
				}
			}
			if(Files.notExists(Paths.get(uri))) {
				break;
			}
			index++;
		}
		if(firstFile != null) {
			Files.copy(Paths.get(firstFile), Paths.get(uri));
		} else {
			if(fileItem.isInMemory()) {
				Files.write(Paths.get(firstFile), fileItem.get());
			} else {
				Files.move(((DiskFileItem)fileItem).getStoreLocation().toPath(), Paths.get(uri));
			}
			params.put("uploaded.file.path." + idx, Paths.get(uri).toString());
		}
		return uri;
	}
	protected void execute(HttpServletRequest request, List<FileItem> fields, Element query, Record params) throws UnsupportedEncodingException, Exception {
		Iterator<FileItem> it = fields.iterator();
		while (it.hasNext()) {
			FileItem fileItem = it.next();
			boolean isFormField = fileItem.isFormField();
			if(isFormField) {
				if(logger.isLoggable(Level.FINER)) { logger.finer(fileItem.getFieldName()); }
				if(fileItem.getFieldName().startsWith("_deletefile_.")) {
					Record result = FileHelper.getFilePath2(fileItem.getFieldName().substring("_deletefile_.".length()), params, query);
					if(result == null) {
						continue;
					}
					if(result.isArray("_system.filepath")) {
						List paths = result.getArray("_system.filepath");
						for(Object path : paths) {
							if(path != null && path instanceof String) {
								File f = new File(path + java.io.File.separator + fileItem.getString(StandardCharsets.UTF_8.name()));
								if(f.exists()) {
									f.delete();
								}
							}
						}
					} else {
						String path = result.getString("_system.filepath");
						if(path == null) {
							continue;
						}
						File f = new File(path + java.io.File.separator + fileItem.getString(StandardCharsets.UTF_8.name()));
						if(f.exists()) {
							f.delete();
						}
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
				if(logger.isLoggable(Level.FINER)) { logger.finer(fileItem.getFieldName()); }
				if(fileItem.getSize() == 0) {
					continue;
				}
				Record result = FileHelper.getFilePath2(fileItem.getFieldName(), params, query);
				if(result == null) {
					if(logger.isLoggable(Level.WARNING)) { logger.warning("result is null"); }
					continue;
				}
				if(result.isArray("_system.filepath")) {
					List paths = result.getArray("_system.filepath");
					if(paths != null) {
						URI firstFileURI = null;
						for(int i = 0; i < paths.size(); i++) {
							String path = (String)paths.get(i);
							if(path != null && path instanceof String) {
								if(i == 0) {
									firstFileURI = saveFile(request, (String)path, idx, fileItem, firstFileURI, params);
								} else {
									saveFile(request, (String)path, idx, fileItem, firstFileURI, params);
								}
							}
						}
					}
				} else {
					String path = result.getString("_system.filepath");
					if(path == null) {
						if(logger.isLoggable(Level.WARNING)) { logger.warning("path is null"); }
						continue;
					}
					saveFile(request, path, idx, fileItem, null, params);
				}
				idx++;
			}
		}
	}
	
}