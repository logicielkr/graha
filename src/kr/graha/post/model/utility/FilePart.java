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
import javax.servlet.http.Part;
import javax.servlet.http.HttpServletRequest;

/**
 * Graha(그라하) FilePart
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class FilePart {
	private FileItem item = null;
	private Part part = null;
	private boolean legacyServletAPI = true;
	private String fileName;
	private String fieldName;
	public FilePart(FileItem item) {
		this.item = item;
	}
	public FilePart(Part part, boolean legacyServletAPI) {
		this.part = part;
		this.legacyServletAPI = legacyServletAPI;
	}
	public static boolean isFormField(Part part, HttpServletRequest request) {
		if(part.getHeader("Content-Disposition").indexOf("filename=") > 0) {
			return false;
		}
		return true;
	}
	public static boolean legacyServletAPI(HttpServletRequest request) {
		if(
			request.getServletContext().getMajorVersion() < 3
			|| (
				request.getServletContext().getMajorVersion() == 3
				&& request.getServletContext().getMinorVersion() == 0
			)
		) {
			return true;
		} else {
			return false;
		}
	}
	public Part getPart() {
		return this.part;
	}
	public void setPart(Part part) {
		this.part = part;
	}
	public FileItem getItem() {
		return this.item;
	}
	public void setItem(FileItem item) {
		this.item = item;
	}
	private void parseCD() {
		if(this.part == null) {
			return;
		}
		if(this.fieldName != null) {
			return;
		}
		String cd = this.part.getHeader("Content-Disposition");
		if(cd != null) {
			String[] cdp = cd.split(";");
			if(cdp != null) {
				for(int i = 0; i < cdp.length; i++) {
					if(cdp[i] != null) {
						String p = cdp[i].trim();
						if(p.startsWith("filename=")) {
							p = p.substring("filename=".length()).trim();
							if(p.startsWith("\"")) {
								p = p.substring(1);
							}
							if(p.endsWith("\"")) {
								p = p.substring(0, p.length() - 1);
							}
							if(p.lastIndexOf("/") > 0) {
								p = p.substring(p.lastIndexOf("/"));
							}
							if(p.lastIndexOf("\\") > 0) {
								p = p.substring(p.lastIndexOf("\\"));
							}
							this.fileName = p;
						} else if(p.startsWith("name=")) {
							p = p.substring("name=".length()).trim();
							if(p.startsWith("\"")) {
								p = p.substring(1);
							}
							if(p.endsWith("\"")) {
								p = p.substring(0, p.length() - 1);
							}
							this.fieldName = p;
						}
					}
				}
			}
		}
	}
	public String getFieldName() {
		if(this.fieldName == null) {
			if(this.item != null) {
				this.fieldName = this.item.getFieldName();
			}
			if(this.part != null) {
				this.parseCD();
			}
		}
		return this.fieldName;
	}
	public String getFileName() {
		if(this.fileName == null) {
			if(this.item != null) {
				this.fileName = this.item.getName().substring(Math.max(this.item.getName().lastIndexOf('/'), this.item.getName().lastIndexOf('\\')) + 1);
			}
			if(this.part != null) {
				if(this.legacyServletAPI) {
					this.parseCD();
				} else {
					this.fileName = this.part.getSubmittedFileName().substring(Math.max(this.part.getSubmittedFileName().lastIndexOf('/'), this.part.getSubmittedFileName().lastIndexOf('\\')) + 1);
				}
			}
		}
		return this.fileName;
	}
	public long getSize() {
		if(this.item != null) {
			return this.item.getSize();
		}
		if(this.part != null) {
			return this.part.getSize();
		}
		return 0;
	}
}
