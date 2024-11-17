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

import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import kr.graha.post.element.XmlElement;
import org.w3c.dom.NodeList;
import java.util.List;
import java.util.ArrayList;
import kr.graha.post.model.utility.AuthUtility;
import kr.graha.post.model.utility.AuthInfo;
import kr.graha.post.lib.Record;

/**
 * Graha(그라하) tile 정보 (param 앞에 붙어 있는)
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class Tile {
	private static final String nodeName = "tile";

	private String cond = null;
	private List param = null;
	private Boolean valid = null;
	private Integer paramSize = null;
	protected Tile() {
	}
	protected static Param getParam(List param, int index, Record record) {
		if(param == null) {
			return null;
		}
		int idx = 0;
		for(int i = 0; i < param.size(); i++) {
			if(param.get(i) instanceof Param) {
				if(idx == index) {
					return (Param)param.get(i);
				}
				idx++;
			} else {
				int tileParamSize = Tile.getParamSize((Tile)param.get(i), record);
				if(tileParamSize > 0 && (idx + tileParamSize) > index) {
					return Tile.getParam((Tile)param.get(i), index - idx, record);
				}
				idx += tileParamSize;
			}
		}
		throw new java.lang.IndexOutOfBoundsException("Index " + index + " out of bounds for length " + Tile.getParamSize(param, record));
	}
	private static Param getParam(Tile tile, int index, Record record) {
		if(tile == null) {
			return null;
		}
		if(tile.valid(record)) {
			return Tile.getParam(tile.param, index, record);
		} else {
			return null;
		}
	}
	protected static int getParamSize(List param, Record record) {
		if(param == null) {
			return 0;
		}
		int paramSize = 0;
		for(int i = 0; i < param.size(); i++) {
			if(param.get(i) instanceof Param) {
				paramSize++;
			} else {
				paramSize += Tile.getParamSize((Tile)param.get(i), record);
			}
		}
		return paramSize;
	}
	private static int getParamSize(Tile tile, Record record) {
		if(tile == null) {
			return 0;
		}
		if(tile.paramSize == null) {
			if(tile.valid(record)) {
				tile.paramSize = Tile.getParamSize(tile.param, record);
			} else {
				tile.paramSize = 0;
			}
		}
		return tile.paramSize;
	}
	private boolean valid(Record params) {
		if(this.valid == null) {
			this.valid = true;
			AuthInfo tabAuthInfo = null;
			if(STR.valid(this.getCond())) {
				tabAuthInfo = AuthUtility.parse(this.getCond());
			}
			if(tabAuthInfo != null && AuthUtility.testInServer(tabAuthInfo, params)) {
				if(!AuthUtility.auth(tabAuthInfo, params)) {
					this.valid = false;
				}
			}
		}
		return this.valid.booleanValue();
	}
	public String getCond() {            
		return this.cond;
	}
	private void setCond(String cond) {
		this.cond = cond;
	}
	private void add(Param param) {
		if(this.param == null) {
			this.param = new ArrayList();
		}
		this.param.add(param);
	}
	private void add(Tile tile) {
		if(this.param == null) {
			this.param = new ArrayList();
		}
		this.param.add(tile);
	}
	protected static String nodeName() {
		return Tile.nodeName;
	}
	protected static Tile load(Node element) {
		Tile tile = new Tile();
		if(element != null) {
			tile.loadAttr(element);
			tile.loadElement(element);
			return tile;
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
						node.getNodeValue() != null
					) {
						if(STR.compareIgnoreCase(node.getNodeName(), "cond")) {
							this.setCond(node.getNodeValue());
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
	private void loadElement(Node element) {
		NodeList nl = element.getChildNodes();
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					if(STR.valid(node.getNodeName())) {
						if(STR.compareIgnoreCase(node.getNodeName(), "tile")) {
							this.add(Tile.load(node));
						} else if(STR.compareIgnoreCase(node.getNodeName(), "param")) {
							this.add(Param.load(node));
						} else {
							LOG.warning("invalid nodeName(" + node.getNodeName() + ")");
						}
					}
				} else if(node.getNodeType() == Node.TEXT_NODE) {
				} else {
				}
			}
		}
	}
	protected XmlElement element() {
		XmlElement element = new XmlElement(Tile.nodeName());
		element.setAttribute("cond", this.getCond());
		if(this.param != null && this.param.size() > 0) {
			for(int i = 0; i < this.param.size(); i++) {
				if(this.param.get(i) instanceof Param) {
					element.appendChild(((Param)this.param.get(i)).element());
				} else {
					element.appendChild(((Tile)this.param.get(i)).element());
				}
			}
		}
		return element;
	}
}
