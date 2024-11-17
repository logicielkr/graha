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


package kr.graha.post.xml;

import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Files;
import java.net.URI;
import java.io.IOException;
import kr.graha.post.lib.Record;
import java.util.Iterator;
import kr.graha.helper.LOG;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;
import kr.graha.post.lib.Buffer;
import kr.graha.helper.STR;
import kr.graha.post.lib.Key;

/**
 * GFile
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GFile {
	
	private String name;
	private int append = 0;
	private int total = 0;
	private int rowcount = -1;
	private List<Path> paths = null;
	private List<GParam> params = null;
	/*
	protected XmlElement element() {
		XmlElement element = new XmlElement("files");
		element.setAttribute("name", this.getName());
		element.setAttribute("append", Integer.toString(this.getAppend()));
		element.setAttribute("total", Integer.toString(this.getTotal()));
		element.setAttribute("rowcount", Integer.toString(this.getRowcount()));
		return element;
	}
	*/
	private GFile(String name, int append, int total) {
		this.setName(name);
		this.setAppend(append);
		this.setTotal(total);
	}
	public GFile(String name, int rowcount) {
		this.setName(name);
		this.setRowcount(rowcount);
	}
	private int getRowcount() {
		return this.rowcount;
	}
	private void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}
	private void addParam(GParam param) {
		if(this.params == null) {
			this.params = new ArrayList<GParam>();
		}
		this.params.add(param);
	}
	private void add(Path path) {
		if(this.paths == null) {
			this.paths = new ArrayList<Path>();
		}
		this.paths.add(path);
	}
	private String getName() {
		return this.name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private int getTotal() {
		return this.total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	private int getAppend() {
		return this.append;
	}
	public void setAppend(int append) {
		this.append = append;
	}
	private String escapeFileName(URI uri) {
//		return decodeFileName(uri).replace("%", "%25").replace("#", "%23").replace(";", "%3b").replace("|", "%7c").replace("?", "%3F").replace("[", "%5B").replace("]", "%5D").replace("+", "%2B");
		return decodeFileName(uri).replace("%", "%25").replace("#", "%23").replace(";", "%3b").replace("|", "%7c").replace("?", "%3F").replace("[", "%5B").replace("]", "%5D");
	}
/**
 * URI 객체로부터 파일이름만 가져온다.
 *
 * 이 메소드는 다음과 같은 곳으로 복제되었다.
 * kr/graha/sample/webmua/ForwardMailProcessorImpl.java
 *
 * @param uri URI 객체(Path.toURI() 메소드의 결과)
 * @return 파일이름
 */
	private String decodeFileName(URI uri) {
		return uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
		/*
		try {
//			return URLDecoder.decode(uri.toString().substring(uri.toString().lastIndexOf("/") + 1).replace("+", "%2B"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return uri.toString().substring(uri.toString().lastIndexOf("/")+1);
		}
		*/
	}
	public static GFile load(String name, Record result, Record params) throws IOException {
		return GFile.load(name, result, params, 0, 0);
	}
	public static GFile load(String name, Record result, Record params, boolean isNew) throws IOException {
		return GFile.load(name, result, params, 0, 0, isNew);
	}
	public static GFile load(String name, Record result, Record params, int append, int total) throws IOException {
		return GFile.load(name, result, params, append, total, false);
	}
	public static GFile load(String name, Record result, Record params, int append, int total, boolean isNew) throws IOException {
		GFile file = new GFile(name, append, total);
		if(result != null && !result.isEmpty()) {
			if(!isNew) {
				String filePath = result.getString(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "filepath"));
				if(STR.valid(filePath)) {
					if(Files.exists(Paths.get(filePath)) && Files.isDirectory(Paths.get(filePath))) {
						DirectoryStream<Path> stream = null;
						try {
							stream = Files.newDirectoryStream(Paths.get(filePath));
							for(Path path : stream) {
								if(Files.isRegularFile(path)) {
									file.add(path);
								} else {
									LOG.warning(path.toString());
								}
							}
							stream.close();
							stream = null;
						} catch(IOException e) {
							LOG.severe(e);
							throw e;
						} finally {
							if(stream != null) {
								try {
									stream.close();
									stream = null;
								} catch(IOException e) {
								}
							}
						}
					}
				}
			}
			Iterator<Key> it = result.keySet().iterator();
			while(it.hasNext()) {
				Key key = (Key)it.next();
				if(key.equals(Record.key(Record.PREFIX_TYPE_U_SYSTEM, "filepath"))) {
					continue;
				}
				if(key != null && key.startsWith("prop.")) {
					if(key.endsWith(".public") || !params.equals(Record.key(key, "public"), "true")) {
						continue;
					}
				}
				file.addParam(new GParam(key, result.getString(key)));
			}
		}
		return file;
	}
	protected void toXML(Buffer xml, boolean rdf) throws IOException {
		if(rdf) {
			if(STR.valid(this.getName())) {
				xml.appendL(1, "<RDF:Seq RDF:about=\"urn:root:files:" + this.getName() + "\">");
			} else {
				xml.appendL(1, "<RDF:Seq RDF:about=\"urn:root:files\">");
			}
			if(this.paths != null && this.paths.size() > 0) {
				for(int i = 0; i < this.paths.size(); i++) {
					Path path = (Path)this.paths.get(i);
					xml.appendL(2, "<RDF:li>");
					xml.appendL(3, "<RDF:item>");
					xml.appendL(4, "<uc:name><![CDATA[" + this.decodeFileName(path.toUri()) + "]]></uc:name>");
					xml.appendL(4, "<uc:name2><![CDATA[" + this.escapeFileName(path.toUri()) + "]]></uc:name2>");
					xml.appendL(4, "<uc:length>" + Files.size(path) + "</uc:length>");
					xml.appendL(4, "<uc:lastModified>" + Files.getLastModifiedTime(path).toMillis() + "</uc:lastModified>");
					xml.appendL(3, "</RDF:item>");
					xml.appendL(2, "</RDF:li>");
				}
			}
			if(this.getRowcount() >= 0) {
				xml.appendL(2, "<RDF:li><RDF:item><uc:rowcount>" + this.getRowcount() + "</uc:rowcount></RDF:item></RDF:li>");
			} else {
				if(this.getTotal() > 0) {
					int initial = 0;
					if(this.paths != null) {
						initial = this.paths.size();
					}
					for(int i = initial; i < this.getTotal(); i++) {
						xml.appendL(2, "<RDF:li><RDF:item></RDF:item></RDF:li>");
					}
				} else if(this.getAppend() > 0) {
					for(int i = 0; i < this.getAppend(); i++) {
						xml.appendL(2, "<RDF:li><RDF:item></RDF:item></RDF:li>");
					}
				}
			}
			xml.appendL(1, "</RDF:Seq>");
			if(this.paths != null && this.paths.size() > 0) {
				if(this.params != null && this.params.size() > 0) {
					if(STR.valid(this.getName())) {
						xml.appendL(1, "<RDF:Description uc:for=\"urn:root:files:" + this.getName() + "\">");
					} else {
						xml.appendL(1, "<RDF:Description uc:for=\"urn:root:files\">");
					}
					xml.appendL(2, "<uc:params>");
					for(int i = 0; i < this.params.size(); i++) {
						GParam param = (GParam)params.get(i);
						param.toXMLForFileParam(xml, rdf);
					}
					xml.appendL(2, "</uc:params>");
					xml.appendL(1, "</RDF:Description>");
				}
			}
		} else {
			if(STR.valid(this.getName())) {
				xml.appendL(1, "<files id=\"" + this.getName() + "\">");
			} else {
				xml.appendL(1, "<files>");
			}
			if(this.paths != null && this.paths.size() > 0) {
				for(int i = 0; i < this.paths.size(); i++) {
					Path path = (Path)this.paths.get(i);
					xml.appendL(2, "<file>");
					xml.appendL(3, "<name><![CDATA[" + this.decodeFileName(path.toUri()) + "]]></name>");
					xml.appendL(3, "<name2><![CDATA[" + this.escapeFileName(path.toUri()) + "]]></name2>");
					xml.appendL(3, "<length>" + Files.size(path) + "</length>");
					xml.appendL(3, "<lastModified>" + Files.getLastModifiedTime(path).toMillis() + "</lastModified>");
					xml.appendL(2, "</file>");
				}
			}
			if(this.getRowcount() >= 0) {
				xml.appendL(2, "<file><rowcount>" + this.getRowcount() + "</rowcount></file>");
			} else {
				if(this.getTotal() > 0) {
					int initial = 0;
					if(this.paths != null) {
						initial = this.paths.size();
					}
					for(int i = initial; i < this.getTotal(); i++) {
						xml.appendL(2, "<file></file>");
					}
				} else if(this.getAppend() > 0) {
					for(int i = 0; i < this.getAppend(); i++) {
						xml.appendL(2, "<file></file>");
					}
				}
			}
			xml.appendL(1, "</files>");
			if(this.paths != null && this.paths.size() > 0) {
				if(this.params != null && this.params.size() > 0) {
					if(STR.valid(this.getName())) {
						xml.appendL(1, "<params for=\"files." + this.getName() + "\">");
					} else {
						xml.appendL(1, "<params for=\"files\">");
					}
					for(int i = 0; i < this.params.size(); i++) {
						GParam param = (GParam)params.get(i);
						param.toXMLForFileParam(xml, rdf);
					}
					xml.appendL(1, "</params>");
				}
			}
		}
	}
	public static String nodePath(String fileName, boolean rdf) {
		if(rdf) {
			if(STR.valid(fileName)) {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:files:" + fileName + "']/RDF:li";
			} else {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:files']/RDF:li";
			}
		} else {
			if(STR.valid(fileName)) {
				return "/document/files[@id='" + fileName + "']/file";
			} else {
				return "/document/files/file";
			}
		}
	}
	public static String childNodeName(String childNodeName, boolean rdf) {
		if(rdf) {
			return "RDF:item/uc:" + childNodeName;
		} else {
			return childNodeName;
		}
	}
}
