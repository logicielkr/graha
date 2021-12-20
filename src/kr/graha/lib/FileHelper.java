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


package kr.graha.lib;

import org.w3c.dom.Element;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import kr.graha.helper.LOG;

/**
 * Graha(그라하) 파일(File) 관련 유틸리티

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public final class FileHelper {
	private static Logger logger = Logger.getLogger("kr.graha.lib.FileHelper");
	private FileHelper() {
	}
	protected static String escapeFileName(URI uri) {
		return decodeFileName(uri).replace("%", "%25").replace("#", "%23").replace(";", "%3b").replace("|", "%7c").replace("?", "%3F").replace("[", "%5B").replace("]", "%5D").replace("+", "%2B");
	}
	protected static String decodeFileName(URI uri) {
		try {
			return URLDecoder.decode(uri.toString().substring(uri.toString().lastIndexOf("/") + 1).replace("+", "%2B"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return uri.toString().substring(uri.toString().lastIndexOf("/")+1);
		}
	}
	/*
	protected static String escapeFileName(String fileName) {
		return decodeFileName(fileName).replace("%", "%25").replace("#", "%23").replace(";", "%3b").replace("|", "%7c").replace("?", "%3F").replace("[", "%5B").replace("]", "%5D").replace("+", "%2B");
	}
	protected static String decodeFileName(String fileName) {
		if(Charset.defaultCharset().equals(StandardCharsets.UTF_8)) {
			return fileName;
		} else {
			return new String(fileName.getBytes(Charset.defaultCharset()), StandardCharsets.UTF_8);
		}
	}
*/
	public static Record getFileInfo(Element query) {
		Record info = new Record();
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			XPathExpression expr = xpath.compile("files");
			Element node = (Element)expr.evaluate(query, XPathConstants.NODE);
			if(node != null) {
				if(node.hasAttribute("maxMemorySize")) {
					info.put("maxMemorySize", node.getAttribute("maxMemorySize"));
				}
				if(node.hasAttribute("tempDirectory")) {
					info.put("tempDirectory", node.getAttribute("tempDirectory"));
				}
				if(node.hasAttribute("maxRequestSize")) {
					info.put("maxRequestSize", node.getAttribute("maxRequestSize"));
				}
			}
			
		} catch (XPathExpressionException | DOMException e) {
			e.printStackTrace();
		}
		return info;
	}
	public static Record getFilePath2(String text, Record p, Element query) {
		String path = new String(text);
		if(path.lastIndexOf(".") >= 0) {
			path = path.substring(0, path.lastIndexOf("."));
			try {
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile("files/file[@name = '" + path + "']");
				Element file = (Element)expr.evaluate(query, XPathConstants.NODE);
				if(file != null && file.hasAttribute("path")) {
//					return FileHelper.getFilePath(file.getAttribute("path"), p);
					if(file.hasAttribute("backup")) {
						return parse(p, file.getAttribute("path"), file.getAttribute("backup"));
					} else {
						return parse(p, file.getAttribute("path"));
					}
				}
			} catch (XPathExpressionException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	protected static Record getFilePath(String text, Record p) {
		return parse(text, p);
	}
	private static Record parse(Record p, String... strs) {
		Record result = new Record();
		for(String text : strs) {
			parse(text, p, result);
		}
		return result;
	}
	private static void parse(String text, Record p, Record result) {
		String path = new String(text);
		Buffer sb = new Buffer();
//		Record result = new Record();
		while(true) {
			if(path.indexOf("${") >= 0) {
				sb.append(path.substring(0, path.indexOf("${")));
				path = path.substring(path.indexOf("${") + 2);
				if(path.indexOf("}") > 0) {
					String val = path.substring(0, path.indexOf("}"));
					if(p.hasKey(val + ".0")) {
						sb.append(p.getString(val + ".0"));
						result.put(val, p.getString(val + ".0"));
					} else if(p.hasKey(val)) {
						sb.append(p.getString(val));
						result.put(val, p.getString(val));
					} else {
//						return null;
					}
					path = path.substring(path.indexOf("}") + 1);
				} else {
					sb.append(path);
					break;
				}
			} else {
				sb.append(path);
				break;
			}
		}
		result.puts("_system.filepath", sb.toString());
//		return result;
	}
	public static Record parse(String text, Record p) {
		Record result = new Record();
		parse(text, p, result);
		return result;
	}
	public static boolean isAllow(Element query, Record p) {
		boolean result = true;
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			XPathExpression expr = xpath.compile("files");
			Element node = (Element)expr.evaluate(query, XPathConstants.NODE);
			if(node == null) {
				result = false;
			} else {
				if(node.hasAttribute("auth")) {
					result = AuthParser.auth(node.getAttribute("auth"), p);
				}
			}
		} catch (XPathExpressionException | DOMException e) {
			if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.toString(e)); }
			
		}
		return result;
	}
	public static boolean isRequireConnection(Element query, String name) {
		boolean result = true;
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			
			XPathExpression expr = xpath.compile(name);
			Element node = (Element)expr.evaluate(query, XPathConstants.NODE);
			if(node == null) {
				result = false;
			} else {
				if(
					node.hasAttribute("require_connection") &&
					node.getAttribute("require_connection") != null &&
					node.getAttribute("require_connection").equals("false")
				) {
					result = false;
				} else {
					result = true;
				}
			}
		} catch (XPathExpressionException | DOMException e) {
			e.printStackTrace();
		}
		return result;
	}
}