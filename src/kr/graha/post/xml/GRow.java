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
import kr.graha.helper.XML;
import kr.graha.helper.STR;
import kr.graha.post.lib.Buffer;
import java.util.Date;
import java.sql.Timestamp;

/**
 * GRow
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GRow {
	private String name;
	private List<Object> values = null;
	private List<GColumn> columns = null;
	public GRow() {
	}
	public GRow(int value) {
		this.add(value);
	}
	public GRow(String name, int value) {
		this.name = name;
		this.add(value);
	}
	public boolean containsKey(String columnName, List<GColumn> columns) {
		List<GColumn> gcolumns = null;
		if(STR.valid(this.columns)) {
			gcolumns = this.columns;
		} else {
			gcolumns = columns;
		}
		if(STR.valid(gcolumns) && STR.valid(this.values)) {
			for(int i = 0; i < gcolumns.size(); i++) {
				GColumn column = (GColumn)gcolumns.get(i);
				if(STR.compareIgnoreCase(column.getName(), columnName)) {
					if(this.values.get(i) != null) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public Object get(String columnName, List<GColumn> columns) {
		List<GColumn> gcolumns = null;
		if(STR.valid(this.columns)) {
			gcolumns = this.columns;
		} else {
			gcolumns = columns;
		}
		if(STR.valid(gcolumns) && STR.valid(this.values)) {
			for(int i = 0; i < gcolumns.size(); i++) {
				GColumn column = (GColumn)gcolumns.get(i);
				if(STR.compareIgnoreCase(column.getName(), columnName)) {
					if(this.values.get(i) != null) {
						return this.values.get(i);
					}
				}
			}
		}
		return false;
	}
	protected void add(String value) {
		this.add((Object)value);
	}
	protected void add(Object value) {
		if(this.values == null) {
			this.values = new ArrayList<Object>();
		}
		this.values.add(value);
	}
	public void add(GColumn column) {
		if(this.columns == null) {
			this.columns = new ArrayList<GColumn>();
		}
		this.columns.add(column);
	}
	public void add(GColumn column, int value) {
		this.add(column, (Object)value);
	}
	public void add(GColumn column, long value) {
		this.add(column, (Object)value);
	}
	public void add(GColumn column, float value) {
		this.add(column, (Object)value);
	}
	public void add(GColumn column, double value) {
		this.add(column, (Object)value);
	}
	public void add(GColumn column, Date value) {
		this.add(column, (Object)value);
	}
	public void add(GColumn column, Timestamp value) {
		this.add(column, (Object)value);
	}
	public void add(GColumn column, String value) {
		this.add(column, (Object)value);
	}
	public void add(GColumn column, boolean value) {
		this.add(column, (Object)value);
	}
	public void add(GColumn column, Object value) {
		this.add(column);
		this.add(value);
	}
	private void add(int value) {
		this.add((Object)value);
	}
	private String getName() {
		return this.name;
	}
	protected void toXML(Buffer xml, List<GColumn> columns, boolean columnAuto, boolean rdf) {
		if(rdf) {
			if(STR.valid(this.getName())) {
				xml.append(2, "<RDF:li RDF:about=\"urn:root:data:" + this.getName() + "\"><RDF:item>");
			} else {
				xml.append(2, "<RDF:li RDF:about=\"urn:root:data\"><RDF:item>");
			}
		} else {
			if(STR.valid(this.getName())) {
				xml.append(2, "<row id=\"" + this.getName() + "\">");
			} else {
				xml.append(2, "<row>");
			}
		}
		if(this.values != null && this.values.size() > 0) {
			xml.appendL("");
		}
		if(this.values != null && this.values.size() > 0) {
			for(int i = 0; i < this.values.size(); i++) {
				if(this.values.get(i) == null && !columnAuto) {
					continue;
				}
				GColumn column = null;
				if(this.columns != null && this.columns.size() > 0) {
					column = (GColumn)this.columns.get(i);
				} else {
					column = (GColumn)columns.get(i);
				}
				if(rdf) {
					xml.append(3, "<uc:" + column.getName() + ">");
				} else {
					xml.append(3, "<" + column.getName() + ">");
				}
				if(this.values.get(i) != null) {
					if(column.getType() == java.sql.Types.VARCHAR || column.getType() == java.sql.Types.OTHER) {
						xml.append("<![CDATA[");
						xml.append(XML.fix((String)this.values.get(i)));
						xml.append("]]>");
					} else {
						xml.append(this.values.get(i));
					}
				}
				if(rdf) {
					xml.appendL("</uc:" + column.getName() + ">");
				} else {
					xml.appendL("</" + column.getName() + ">");
				}
			}
		}
		if(this.values != null && this.values.size() > 0) {
			xml.append(2, "");
		}
		if(rdf) {
			xml.appendL("</RDF:item></RDF:li>");
		} else {
			xml.appendL("</row>");
		}
	}
	public static String nodePath(String gRowsName, boolean rdf) {
		if(rdf) {
			if(STR.valid(gRowsName)) {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:" + gRowsName + "']/RDF:li";
			} else {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data']/RDF:li";
			}
		} else {
			if(STR.valid(gRowsName)) {
				return "/document/rows[@id='" + gRowsName + "']/row";
			} else {
				return "/document/rows/row";
			}
		}
	}
	public static String firstNodePath(String gRowsName, boolean rdf) {
		if(rdf) {
			if(STR.valid(gRowsName)) {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:" + gRowsName + "']/RDF:li[position() = 1]/RDF:item";
			} else {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data']/RDF:li[position() = 1]/RDF:item";
			}
		} else {
			if(STR.valid(gRowsName)) {
				return "/document/rows[@id='" + gRowsName + "']/row[position() = 1]";
			} else {
				return "/document/rows/row[position() = 1]";
			}
		}
	}
	public static String firstNodeName(boolean rdf) {
		if(rdf) {
			return "./RDF:item/*";
		} else {
			return "./*";
		}
	}
	public static String childNodePath(String gRowsName, String childNodeName, boolean rdf) {
		if(rdf) {
			if(STR.valid(gRowsName)) {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:" + gRowsName + "']/RDF:li/RDF:item/uc:" + childNodeName;
			} else {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data']/RDF:li/RDF:item/uc:" + childNodeName;
			}
		} else {
			if(STR.valid(gRowsName)) {
				return "/document/rows[@id='" + gRowsName + "']/row/" + childNodeName;
			} else {
				return "/document/rows/row/" + childNodeName;
			}
		}
	}
	
	public static String childNodeName(String childNodeName, boolean rdf) {
		if(rdf) {
			if(STR.compareIgnoreCase(childNodeName, "position()")) {
				return childNodeName;
			} else {
				return "RDF:item/uc:" + childNodeName;
			}
		} else {
			return childNodeName;
		}
	}	
}
