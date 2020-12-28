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

import java.util.logging.Logger;

/**
 * Graha(그라하) XML 태그 처리기
 * XML/RDF를 구분하여 출력하기 위해 사용된다.
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class HTMLTag {
	public String htmlType;
	public boolean div = false;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public HTMLTag(String htmlType) {
		this.htmlType = htmlType;
		if(htmlType == null) {
			div = false;
		} else if(htmlType != null && htmlType.equals("div")) {
			div = true;
		} else {
			div = false;
		}
		LogHelper.setLogLevel(logger);
	}
	public String table(String id) {
		if(id == null || id.equals("")) {
			if(div) {
				return "<div class=\"graha table\">";
			} else {
				return "<table class=\"graha\">";
			}
		} else {
			if(div) {
				return "<div id=\"" + id + "\" class=\"graha table\">";
			} else {
				return "<table id=\"" + id + "\" class=\"graha\">";
			}
		}
	}
	public String tableE() {
		if(div) {
			return "</div>";
		} else {
			return "</table>";
		}
	}
	public String thead() {
		if(div) {
			return "<div class=\"graha thead\">";
		} else {
			return "<thead class=\"graha\">";
		}
	}
	public String theadE() {
		if(div) {
			return "</div>";
		} else {
			return "</thead>";
		}
	}
	public String tbody() {
		if(div) {
			return "<div class=\"graha tbody\">";
		} else {
			return "<tbody class=\"graha\">";
		}
	}
	public String tbodyE() {
		if(div) {
			return "</div>";
		} else {
			return "</tbody>";
		}
	}
	public String tr() {
		if(div) {
			return "<div class=\"graha tr\">";
		} else {
			return "<tr class=\"graha\">";
		}
	}
	public String trS() {
		if(div) {
			return "<div class=\"graha tr\"";
		} else {
			return "<tr class=\"graha\"";
		}
	}
	public String trE() {
		if(div) {
			return "</div>";
		} else {
			return "</tr>";
		}
	}
	public String th(String name) {
		if(name == null || name.equals("")) {
			if(div) {
				return "<div class=\"graha th\"";
			} else {
				return "<th  class=\"graha\"";
			}
		} else {
			if(div) {
				return "<div class=\"graha th " + name + "\"";
			} else {
				return "<th class=\"graha " + name + "\"";
			}
		}
	}
	public String thG(String name) {
		/*
		if(name == null || name.equals("")) {
			if(div) {
				return "<div class=\"thtd\">";
			} else {
				return "";
			}
		} else {
			if(div) {
				return "<div class=\"thtd " + name + "\">";
			} else {
				return "";
			}
		}
		*/
		return "";
	}
	public String thE() {
		if(div) {
			return "</div>";
		} else {
			return "</th>";
		}
	}
	public String td(String name) {
		if(name == null || name.equals("")) {
			if(div) {
				return "<div class=\"graha td\"";
			} else {
				return "<td  class=\"graha\"";
			}
		} else {
			if(div) {
				return "<div class=\"graha td " + name + "\"";
			} else {
				return "<td class=\"graha " + name + "\"";
			}
		}
	}
	public String tdE() {
		if(div) {
			return "</div>";
		} else {
			return "</td>";
		}
	}
	public String tdGE() {
		return "";
		/*
		if(div) {
			return "</div>";
		} else {
			return "";
		}
		*/
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}