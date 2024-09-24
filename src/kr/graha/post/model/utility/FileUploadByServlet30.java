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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import kr.graha.helper.STR;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import javax.servlet.ServletException;

/**
 * Graha(그라하) 파일 업로드 처리
 * Servlet API 3.0 이상에서 지원하는 파일 업로드를 이용하여 처리
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class FileUploadByServlet30 {
	public static List<FilePart> getFileParts(HttpServletRequest request, boolean legacyServletAPI)
		throws UnsupportedEncodingException, IOException, ServletException
	{
		List<FilePart> fileParts = null;
		if(STR.startsWithIgnoreCase(request.getContentType(), "multipart/")) {
			if(legacyServletAPI) {
				request.setCharacterEncoding("ISO-8859-1");
			} else {
				request.setCharacterEncoding("UTF-8");
			}
			Collection<Part> parts = request.getParts();
			if(parts != null && parts.size() > 0) {
				for(Part part : parts) {
					if(FilePart.isFormField(part, request)) {
					} else {
						if(fileParts == null) {
							fileParts = new ArrayList<FilePart>();
						}
						fileParts.add(new FilePart(part, legacyServletAPI));
					}
				}
			}
		}
		return fileParts;
	}
}
