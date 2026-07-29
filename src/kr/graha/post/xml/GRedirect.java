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
import kr.graha.post.lib.Buffer;
import kr.graha.post.lib.GrahaRuntimeException;
import kr.graha.helper.STR;

/**
 * GRedirect
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GRedirect {
	
	private String path = null;
	private List<String> msgs = null;
	private List<GRedirectParam> params = null;
	private Boolean autoredirect = null;
	private String label = null;
	private String type = null;
	
	public static int NODE_OF_MSG = 71;
	public static int NODE_OF_PARAM = 72;
	
	public GRedirect(String path, Boolean autoredirect, String label, String type) {
		this.setPath(path);
		this.setAutoredirect(autoredirect);
		this.setLabel(label);
		this.setType(type);
	}
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<String> getMsgs() {
		return this.msgs;
	}
	private Boolean getAutoredirect() {
		return this.autoredirect;
	}
	private void setAutoredirect(Boolean autoredirect) {
		this.autoredirect = autoredirect;
	}
	public String getLabel() {
		return this.label;
	}
	private void setLabel(String label) {
		this.label = label;
	}
	private String getType() {
		return this.type;
	}
	private void setType(String type) {
		this.type = type;
	}
	public void add(String msg) {
		if(this.msgs == null) {
			this.msgs = new ArrayList<String>();
		}
		this.msgs.add(msg);
	}
	public void add(GRedirectParam param) {
		if(this.params == null) {
			this.params = new ArrayList<GRedirectParam>();
		}
		this.params.add(param);
	}
	public boolean forRedirect() {
		if(this.getAutoredirect() == null || this.getAutoredirect()) {
			if(STR.compareIgnoreCase(this.getType(), "redirect")) {
				return true;
			}
		}
		return false;
	}
	public String getRedirectURI() {
		if(this.forRedirect()) {
			String redirectURI = this.getPath();
			if(this.params != null && this.params.size() > 0) {
				int index = 0;
				for(int i = 0; i < this.params.size(); i++) {
					GRedirectParam param = (GRedirectParam)this.params.get(i);
					if(param.validValue()) {
						if(index == 0) {
							redirectURI += "?";
						} else {
							redirectURI += "&";
						}
						redirectURI += param.getName() + "=" + param.getValue();
						index++;
					}
				}
			}
			return redirectURI;
		}
		return null;
	}
	protected void toXML(Buffer xml, boolean rdf) {
		if(rdf) {
			xml.appendL(2, "<RDF:li><RDF:item>");
			xml.appendL(3, "<uc:path>" + this.getPath() + "</uc:path>");
			xml.appendL(3, "<uc:autoredirect>" + this.getAutoredirect() + "</uc:autoredirect>");
			
			xml.appendL(3, "<uc:label>" + this.getLabel() + "</uc:label>");
			xml.appendL(3, "<uc:type>" + this.getType() + "</uc:type>");
			
			if(this.msgs != null && this.msgs.size() > 0) {
				xml.appendL(3, "<uc:msgs>");
				for(int i = 0; i < this.msgs.size(); i++) {
					xml.appendL(4, "<uc:msg>" + this.msgs.get(i) + "</uc:msg>");
				}
				xml.appendL(3, "</uc:msgs>");
			}
			if(this.params != null && this.params.size() > 0) {
				xml.appendL(3, "<uc:params>");
				for(int i = 0; i < this.params.size(); i++) {
					((GRedirectParam)this.params.get(i)).toXML(xml, rdf);
				}
				xml.appendL(3, "</uc:params>");
			}
			xml.appendL(2, "</RDF:item></RDF:li>");
		} else {
			xml.appendL(2, "<redirect>");
			xml.appendL(3, "<path>" + this.getPath() + "</path>");
			xml.appendL(3, "<autoredirect>" + this.getAutoredirect() + "</autoredirect>");
			if(this.msgs != null && this.msgs.size() > 0) {
				xml.appendL(3, "<msgs>");
				for(int i = 0; i < this.msgs.size(); i++) {
					xml.appendL(4, "<msg>" + this.msgs.get(i) + "</msg>");
				}
				xml.appendL(3, "</msgs>");
			}
			if(this.params != null && this.params.size() > 0) {
				xml.appendL(3, "<params>");
				for(int i = 0; i < this.params.size(); i++) {
					((GRedirectParam)this.params.get(i)).toXML(xml, rdf);
				}
				xml.appendL(3, "</params>");
			}
			xml.appendL(2, "</redirect>");
		}
	}
	public static String nodePath(boolean rdf) {
		if(rdf) {
			return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:redirects']/RDF:li/RDF:item";
		} else {
			return "/document/redirects/redirect";
		}
	}
	public static String childNodeName(String childNodeName, boolean rdf) {
		if(rdf) {
			return "uc:" + childNodeName;
		} else {
			return childNodeName;
		}
	}
	public static String childNodePath(int childNode, boolean rdf) {
		if(childNode == GRedirect.NODE_OF_MSG) {
			if(rdf) {
				return "uc:msgs/uc:msg";
			} else {
				return "msgs/msg";
			}
		} else if(childNode == GRedirect.NODE_OF_PARAM) {
			if(rdf) {
				return "uc:params/uc:param";
			} else {
				return "params/param";
			}
		} else {
			throw new GrahaRuntimeException("childNode must be GRedirect.NODE_OF_PARAM(" + GRedirect.NODE_OF_PARAM + ") or GRedirect.NODE_OF_MSG(" + GRedirect.NODE_OF_MSG + ")");
		}
	}
}
