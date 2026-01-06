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


package kr.graha.post.model;

import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.Record;
import kr.graha.helper.STR;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.model.utility.TextParser;
import kr.graha.post.element.XmlElement;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.net.URISyntaxException;
import kr.graha.helper.LOG;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import kr.graha.post.xml.GDocument;
import kr.graha.post.xml.GFile;
import kr.graha.post.model.utility.FilePart;
import kr.graha.post.interfaces.FilePathTranslator;
import kr.graha.post.interfaces.FilePathTranslatorImpl;
import kr.graha.post.interfaces.FileDownloader;
import kr.graha.post.interfaces.FileDownloaderImpl;
import java.lang.reflect.InvocationTargetException;
import kr.graha.post.lib.GrahaRuntimeException;

/**
 * Graha(그라하) file 정보
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class File {
	private static final String nodeName = "file";
	private File() {
	}
	
	private String name = null;
	private String label = null;
	private String path = null;
	private String total = null;
	private String append = null;
	private String before = null;
	private String after = null;
	private String backup = null;
	private String translator = null;
	private String downloader = null;
	protected String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	private String getPath() {
		return this.path;
	}
	private void setPath(String path) {
		this.path = path;
	}
	private String getTotal() {
		return this.total;
	}
	private void setTotal(String total) {
		this.total = total;
	}
	private String getAppend() {
		return this.append;
	}
	private void setAppend(String append) {
		this.append = append;
	}
	private String getBefore() {
		return this.before;
	}
	private void setBefore(String before) {
		this.before = before;
	}
	private String getAfter() {
		return this.after;
	}
	private void setAfter(String after) {
		this.after = after;
	}
	private String getBackup() {
		return this.backup;
	}
	private void setBackup(String backup) {
		this.backup = backup;
	}
	private String getTranslator() {
		return this.translator;
	}
	private void setTranslator(String translator) {
		this.translator = translator;
	}
	private String getDownloader() {
		return this.downloader;
	}
	private void setDownloader(String downloader) {
		this.downloader = downloader;
	}
	protected static String nodeName() {
		return File.nodeName;
	}
	protected static File load(Node element) {
		File file = new File();
		if(element != null) {
			file.loadAttr(element);
			return file;
		}
		return null;
	}
	private void loadAttr(Node element) {
		NamedNodeMap nnm = element.getAttributes();
		if(nnm != null && nnm.getLength() > 0) {
			for(int i = 0; i < nnm.getLength(); i++) {
				Node node = nnm.item(i);
				if(node.getNodeType() == Node.ATTRIBUTE_NODE) {
					if(
						STR.valid(node.getNodeName()) &&
						STR.valid(node.getNodeValue())
					) {
						if(STR.compareIgnoreCase(node.getNodeName(), "name")) {
							this.setName(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "label")) {
							this.setLabel(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "path")) {
							this.setPath(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "total")) {
							this.setTotal(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "append")) {
							this.setAppend(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "before")) {
							this.setBefore(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "after")) {
							this.setAfter(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "backup")) {
							this.setBackup(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "translator")) {
							this.setTranslator(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "downloader")) {
							this.setDownloader(node.getNodeValue());
						} else if(STR.compareIgnoreCase(node.getNodeName(), "xml:base")) {
						} else {
							LOG.warning("invalid attrName(" + node.getNodeName() + ")");
						}
					}
				} else if(node.getNodeType() == Node.TEXT_NODE) {
				} else {
				}
			}
		}
	}
	protected XmlElement element() {
		XmlElement element = new XmlElement(File.nodeName());
		element.setAttribute("name", this.getName());
		element.setAttribute("label", this.getLabel());
		element.setAttribute("path", this.getPath());
		element.setAttribute("total", this.getTotal());
		element.setAttribute("append", this.getAppend());
		element.setAttribute("before", this.getBefore());
		element.setAttribute("after", this.getAfter());
		element.setAttribute("backup", this.getBackup());
		element.setAttribute("translator", this.getTranslator());
		element.setAttribute("downloader", this.getDownloader());
		return element;
	}
	private boolean print(String before, String after) {
		if(
			(
				STR.valid(before) &&
				STR.compareIgnoreCase(before, this.getBefore())
			) ||
			(
				STR.valid(after) &&
				STR.compareIgnoreCase(after, this.getAfter())
			) ||
			(
				!STR.valid(before) &&
				!STR.valid(after) &&
				!STR.valid(this.getBefore()) &&
				!STR.valid(this.getAfter())
			)
		) {
			return true;
		} else {
			return false;
		}
	}
	protected Buffer li(Record param, int indent, String before, String after, boolean rdf) {
		if(print(before, after)) {
			if(STR.valid(this.getLabel())) {
				Buffer xsl = new Buffer();
				if(STR.valid(this.getName())) {
					xsl.appendL(indent, "<li class=\"" + this.getName() + "\">");
				} else {
					xsl.appendL(indent, "<li>");
				}
				xsl.append(TextParser.parseForXSL(this.getLabel(), param, rdf));
				xsl.append("</li>");
				return xsl;
			}
		}
		return null;
	}
	protected Buffer file(Record param, int indent, String before, String after, boolean rdf, String queryId, int queryFuncType, boolean title) {
		if(print(before, after)) {
			Buffer xsl = new Buffer();
			if(title) {
				if(STR.valid(this.getLabel())) {
					if(STR.valid(this.getName())) {
						xsl.append(indent, "<h3 class=\"" + this.getName() + "\">");
					} else {
						xsl.append(indent, "<h3>");
					}
					xsl.append(TextParser.parseForXSL(this.getLabel(), param, rdf));
					xsl.appendL("</h3>");
				}
			}
			if(STR.valid(this.getName())) {
				xsl.appendL(indent, "<ul id=\"" + this.getName() + "\">");
			} else {
				xsl.appendL(indent, "<ul>");
			}
			xsl.appendL(indent + 1, "<xsl:for-each select=\"" + kr.graha.post.xml.GFile.nodePath(this.getName(), rdf) + "\">");
			xsl.appendL(indent + 2, "<xsl:sort select=\"node() = false()\"/>");
			xsl.appendL(indent + 2, "<xsl:sort select=\"" + kr.graha.post.xml.GFile.childNodeName("name", rdf) + "\" />");
			int indent2 = indent + 2;
			if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
				xsl.appendL(indent2, "<xsl:choose>");
				xsl.appendL(indent2 + 1, "<xsl:when test=\"not(" + kr.graha.post.xml.GFile.childNodeName("name", rdf) + ")\">");
				xsl.appendL(indent2 + 2, "<li><input type=\"file\" class=\"" + this.getName() + "\" name=\"" + this.getName() + ".{position()}\"  multiple=\"multiple\" /></li>");
				xsl.appendL(indent2 + 1, "</xsl:when>");
				xsl.appendL(indent2 + 1, "<xsl:otherwise>");
				indent2 = indent2 + 2;
			}
			xsl.appendL(indent2, "<xsl:variable name=\"downloadparam\">");
			xsl.appendL(indent2 + 1, "<xsl:for-each select=\"" + kr.graha.post.xml.GParam.nodePathForFileParam(this.getName(), rdf) + "\"><xsl:value-of select=\"" + kr.graha.post.xml.GParam.childNodeNameForFileParam("key", rdf) + "\" /><xsl:text>=</xsl:text><xsl:value-of select=\"" + kr.graha.post.xml.GParam.childNodeNameForFileParam("value", rdf) + "\" /><xsl:text>&amp;</xsl:text></xsl:for-each>");
			xsl.appendL(indent2, "</xsl:variable>");
			xsl.appendL(indent2, "<li length=\"{" + kr.graha.post.xml.GFile.childNodeName("length", rdf) + "}\" lastModified=\"{" + kr.graha.post.xml.GFile.childNodeName("lastModified", rdf) + "}\">");
			if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
				xsl.appendL(indent2 + 1, "<input type=\"checkbox\" class=\"_deletefile_." + this.getName() + "\" name=\"_deletefile_." + this.getName() + ".{position()}\" value=\"{" + kr.graha.post.xml.GFile.childNodeName("name", rdf) + "}\" />");
			}
			xsl.appendL(indent2 + 1, "<a>");
			xsl.appendL(indent + 1, "<xsl:attribute name=\"href\">" + Link.getPath(queryId, param) + "/download/" + this.getName() + "/<xsl:value-of select=\"" + kr.graha.post.xml.GFile.childNodeName("name2", rdf) + "\" />?<xsl:value-of select=\"$downloadparam\" /></xsl:attribute>");
			xsl.appendL(indent2 + 2, "<xsl:value-of select=\"" + kr.graha.post.xml.GFile.childNodeName("name", rdf) + "\" />");
			xsl.appendL(indent2 + 1, "</a>");
			xsl.appendL(indent2, "</li>");
			if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
				xsl.appendL(indent + 3, "</xsl:otherwise>");
				xsl.appendL(indent + 2, "</xsl:choose>");
			}
			xsl.appendL(indent + 1, "</xsl:for-each>");
			xsl.appendL(indent, "</ul>");
			return xsl;
		}
		return null;
	}
	private Path getFilePath(String basePath, String fileName, HttpServletRequest request, Record params) {
		FilePathTranslator translator = null;
		if(STR.valid(this.getTranslator())) {
			try {
				translator = (FilePathTranslator) Class.forName(this.getTranslator()).getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
				LOG.severe(e);
				translator = null;
			}
		} else {
			translator = new FilePathTranslatorImpl();
		}
		if(translator != null) {
			return translator.getFilePath(basePath, fileName, request, params);
		}
		return null;
	}
	protected void download(String filePath, HttpServletRequest request, HttpServletResponse response, Record params) throws IOException {
		String basePath = null;
		if(STR.valid(this.getPath())) {
			basePath = TextParser.parse(this.getPath(), params);
			String fileName = filePath.substring(filePath.indexOf("/") + 1);
			LOG.finer(basePath + java.io.File.separator + fileName);
			Path path = this.getFilePath(basePath, fileName, request, params);
			if(path != null && Files.exists(path)) {
				FileDownloader downloader = null;
				if(STR.valid(this.getDownloader())) {
					try {
						downloader = (FileDownloader) Class.forName(this.getDownloader()).getConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
						LOG.severe(e);
						downloader = null;
					}
				} else {
					downloader = new FileDownloaderImpl();
				}
				if(downloader != null) {
					downloader.execute(path, fileName, request, response, params);
				} else {
					LOG.config("[SC_NOT_FOUND]FileDownloader = " + this.getDownloader());
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			} else {
				LOG.config("[SC_NOT_FOUND]File Path = " + path.toString());
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		} else {
			LOG.config("[SC_INTERNAL_SERVER_ERROR]File Path = " + filePath);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
	
	private void deleteFile(String basePath, String fileName) throws IOException, URISyntaxException {
		if(STR.valid(basePath) && Files.exists(Paths.get(basePath))) {
			Path path = this.getPath(basePath, fileName);
			if(path != null && Files.exists(path)) {
				Files.delete(path);
			} else {
				LOG.finer(path.toString());
			}
		}
	}
	
	protected void deleteFile(String fileName, Record params) throws IOException, URISyntaxException {
		if(STR.valid(this.getPath())) {
			String basePath = TextParser.parse(this.getPath(), params);
			this.deleteFile(basePath, fileName);
		}
		if(STR.valid(this.getBackup())) {
			String basePath = TextParser.parse(this.getBackup(), params);
			this.deleteFile(basePath, fileName);
		}
	}
	private Path getPath(String basePath, String fileName) throws UnsupportedEncodingException, URISyntaxException {
		if(STR.valid(basePath)) {
			Path path = null;
			try {
				if(fileName == null) {
					path = Paths.get(new URI("file://" + basePath));
				} else {
					path = Paths.get(new URI("file://" + basePath + java.io.File.separator + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20")));
				}
			} catch (URISyntaxException e) {
				LOG.severe(e);
				throw e;
			}
			if(path != null) {
				return path;
			}
		}
		return null;
	}
/**
 * 유일한 파일이름을 URI로 가져온다.
 *
 * basePath 에 fileName 과 동일한 파일이 있는 경우,
 * 확장자가 있는 경우 확장자 앞에 확장자가 없는 경우 파일이름의 끝에 "-일련번호" 를 붙인다.
 *
 * 확장자는 fileName 에서 "." 이 있는 경우, 마지막 "." 뒷부분을 확장자로 한다.
 * 확장자가 유효한지 여부를 따지지 않고, 파일이름에서 마지막 "." 뒷부분을 확장자로 취급된다.
 * 또한 ".tar.gz" 혹은 ".tar.bz2" 와 같은 경우에도 "gz", "bz2" 가 확장자가 된다(이 부분은 향후에 개선할 의향이 있고, 만약 그렇게 된다면, kr.graha.helper 아래에 위치하게 될 가능성이 크다).
 *
 * 이 메소드는 kr.graha.sample.webmua.ForwardMailProcessorImpl 에 그대로 복사되었다.
 *
 * @param basePath 디렉토리 경로
 * @param fileName 파일이름
 * @return 디렉토리 경로(basePath) 에서 중복되지 않은 파일이름(fileName)
 */
	private URI getUniqueFileURI(String basePath, String fileName) throws UnsupportedEncodingException, URISyntaxException {
		URI uri = null;
		int index = 0;
		while(true) {
			if(index == 0) {
				uri = new URI("file://" + basePath + java.io.File.separator + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));
			} else {
				if(fileName.lastIndexOf(".") > 0) {
					uri = new URI("file://" + basePath + java.io.File.separator + java.net.URLEncoder.encode(fileName.substring(0, fileName.lastIndexOf(".")), "UTF-8").replaceAll("\\+", "%20")  + "-" + index + "." + java.net.URLEncoder.encode(fileName.substring(fileName.lastIndexOf(".") + 1), "UTF-8").replaceAll("\\+", "%20"));
				} else {
					uri = new URI("file://" + basePath + java.io.File.separator + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20") + "-" + index);
				}
			}
			if(Files.notExists(Paths.get(uri))) {
				break;
			} else if(Files.isReadable(Paths.get(uri))) {
			} else {
				LOG.warning(Paths.get(uri).toString());
				throw new GrahaRuntimeException(basePath + " (Permission denied)");
			}
			index++;
		}
		return uri;
	}
	private boolean checkPermission(String basePath) throws URISyntaxException {
		URI uri = null;
		uri = new URI("file://" + basePath);
		Path parent = Paths.get(uri);
		while(!Files.exists(parent)) {
			parent = parent.getParent();
		}
		if(Files.isWritable(parent)) {
			return true;
		}
		return false;
	}
	protected URI saveFileUsingServletFileUpload(FilePart filePart, Record params) throws IOException, URISyntaxException {
		URI uri = null;
		String fileName = filePart.getFileName();
		if(STR.valid(this.getPath())) {
			String basePath = TextParser.parse(this.getPath(), params);
			if(STR.valid(basePath)) {
				if(checkPermission(basePath)) {
					if(!Files.exists(Paths.get(basePath))) {
						Files.createDirectories(Paths.get(basePath));
					}
					uri = this.getUniqueFileURI(basePath, fileName);
					if(filePart.getItem() != null) {
						if(filePart.getItem().isInMemory()) {
							Files.write(Paths.get(uri), filePart.getItem().get());
						} else {
							Files.move(((DiskFileItem)filePart.getItem()).getStoreLocation().toPath(), Paths.get(uri));
						}
					} else if(filePart.getPart() != null && filePart.getPart().getInputStream() != null) {
						Files.copy(filePart.getPart().getInputStream(), Paths.get(uri));
						filePart.getPart().delete();
					}
				} else {
					throw new GrahaRuntimeException(basePath + " (Permission denied)");
				}
			}
		}
		if(uri != null && STR.valid(this.getBackup())) {
			String basePath = TextParser.parse(this.getBackup(), params);
			if(STR.valid(basePath)) {
				if(!Files.exists(Paths.get(basePath))) {
					Files.createDirectories(Paths.get(basePath));
				}
				URI backupURI = this.getUniqueFileURI(basePath, fileName);
				Files.copy(Paths.get(uri), Paths.get(backupURI));
			}
		}
		return uri;
	}
	private int delete(String basePath) {
		if(STR.valid(basePath)) {
			java.io.File dir = new java.io.File(basePath);
			if(dir.exists() && dir.isDirectory()) {
				int index = 0;
				java.io.File[] files = dir.listFiles();
				if(files != null) {
					for (java.io.File file : files) {
						file.delete();
						LOG.finer("delete file (" + file.getPath() + ")");
						index++;
					}
				}
				dir.delete();
				LOG.finer("delete directory (" + dir.getPath() + ")");
				return index;
			} else {
				LOG.warning("file paths is not exists (basePath = " + basePath + "), (path = " + this.getPath() + "), (backup=" + this.getBackup() + ")");
			}
		} else {
			LOG.warning("file paths is not exists (basePath = " + basePath + "), (path = " + this.getPath() + "), (backup=" + this.getBackup() + ")");
		}
		return 0;
	}
	protected void delete(GDocument document, Record params) {
		if(STR.valid(this.getPath())) {
			String basePath = TextParser.parse(this.getPath(), params);
			int result = this.delete(basePath);
			document.add(new GFile(this.getName(), result));
		}
		if(STR.valid(this.getBackup())) {
			String basePath = TextParser.parse(this.getBackup(), params);
			this.delete(basePath);
		}
	}
	protected void list(GDocument document, Record params, int queryFuncType) throws IOException {
		if(STR.valid(this.getPath())) {
			Record result = new Record();
			if(
				params.hasKey(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")) &&
				params.getInt(Record.key(Record.PREFIX_TYPE_QUERY_ROW, "total_fetch_count")) > 0
			) {
				TextParser.parse(this.getPath(), params, result);
				LOG.finest(this.getPath(), result.getString(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "filepath")));
			} else {
				result = null;
			}
			GFile gfile = GFile.load(this.getName(), result, params);
			if(queryFuncType == Query.QUERY_FUNC_TYPE_INSERT) {
				if(STR.valid(this.getTotal()) && Integer.parseInt(this.getTotal()) > 0) {
					gfile.setTotal(Integer.parseInt(this.getTotal()));
				} else if(STR.valid(this.getAppend()) && Integer.parseInt(this.getAppend()) > 0) {
					gfile.setAppend(Integer.parseInt(this.getAppend()));
				} else {
					LOG.warning("total : " + this.getTotal() + ", append : " + this.getAppend());
				}
			}
			document.add(gfile);
		}
	}
}
