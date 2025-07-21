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
import java.nio.file.Paths;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.UnsupportedEncodingException;
import kr.graha.helper.LOG;

/**
 * Graha(그라하) File Path Translator 표준구현체

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public class FilePathTranslatorImpl implements FilePathTranslator {
	public Path getFilePath(
		String basePath,
		String fileName
	) {
		Path path = null;
		try {
			path = Paths.get(new URI("file://" + basePath + java.io.File.separator + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20")));
			return path;
		} catch (URISyntaxException | UnsupportedEncodingException e) {
			LOG.severe(e);
		}
		return null;
	}
}
