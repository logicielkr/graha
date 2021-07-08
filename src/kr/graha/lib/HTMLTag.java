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
import kr.graha.helper.LOG;

/**
 * Graha(그라하) XML 태그 처리기
 * XML/RDF를 구분하여 출력하기 위해 사용된다.
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class HTMLTag {
	private String htmlType;
	private boolean div = false;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected HTMLTag(String htmlType) {
		this.htmlType = htmlType;
		if(htmlType == null) {
			div = false;
		} else if(htmlType != null && htmlType.equals("div")) {
			div = true;
		} else {
			div = false;
		}
		LOG.setLogLevel(logger);
	}
	protected String table(String id) {
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
	protected String tableE() {
		if(div) {
			return "</div>";
		} else {
			return "</table>";
		}
	}
	protected String thead() {
		if(div) {
			return "<div class=\"graha thead\">";
		} else {
			return "<thead class=\"graha\">";
		}
	}
	protected String theadE() {
		if(div) {
			return "</div>";
		} else {
			return "</thead>";
		}
	}
	protected String tbody() {
		if(div) {
			return "<div class=\"graha tbody\">";
		} else {
			return "<tbody class=\"graha\">";
		}
	}
	protected String tbodyE() {
		if(div) {
			return "</div>";
		} else {
			return "</tbody>";
		}
	}
	protected String tr() {
		if(div) {
			return "<div class=\"graha tr\">";
		} else {
			return "<tr class=\"graha\">";
		}
	}
	protected String trS() {
		if(div) {
			return "<div class=\"graha tr\"";
		} else {
			return "<tr class=\"graha\"";
		}
	}
	protected String trE() {
		if(div) {
			return "</div>";
		} else {
			return "</tr>";
		}
	}
	protected String th(String name) {
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
	protected String thG(String name) {
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
	protected String thE() {
		if(div) {
			return "</div>";
		} else {
			return "</th>";
		}
	}
	protected String td(String name) {
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
	protected String tdE() {
		if(div) {
			return "</div>";
		} else {
			return "</td>";
		}
	}
	protected String tdGE() {
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