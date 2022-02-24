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

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import kr.graha.helper.LOG;


/**
 * Graha(그라하) XML 태그 처리기
 * XML/RDF를 구분하여 출력하기 위해 사용된다.
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class XMLTag {
	public boolean isRDF = true;
	private String uc;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	protected XMLTag(boolean isRDF, String uc) {
		this.isRDF = isRDF;
		this.uc = uc;
		LOG.setLogLevel(logger);
	}
	protected XMLTag(String output, String uc, HttpServletRequest request) {
		if(output != null && output.equals("rdf")) {
			this.isRDF = true;
		} else {
			this.isRDF = false;
		}
		if(uc == null || uc.trim().equals("")) {
			this.uc = parseUC(request);
		} else {
			this.uc = uc;
		}
		LOG.setLogLevel(logger);
	}
	private String parseUC(HttpServletRequest request) {
		String id = request.getPathInfo().trim();
		if(id.startsWith("/")) {
			id = id.substring(1);
		}
		return (request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort() + "" + request.getContextPath() + request.getServletPath() + "/" + id.substring(0, id.indexOf("/")+1));
	}
	protected String path(String tagName, String name) {
		if(tagName != null && tagName.equals("stylesheet")) {
			if(isRDF) {
				return "<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:RDF=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:uc=\"" + this.uc + "\">";
			} else {
				return "<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">";
			}
		}
		if(tagName != null && tagName.equals("row")) {
			if(isRDF) {
				if(name == null || name.equals("")) {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:default']/RDF:li/RDF:item";
				} else {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:" + name + "']/RDF:li/RDF:item";
				}
			} else {
				if(name == null || name.equals("")) {
					return "/document/rows/row";
				} else {
					return "/document/rows[@id='" + name + "']/row";
				}
			}
		}
		if(tagName != null && tagName.equals("file")) {
			if(isRDF) {
				if(name == null || name.equals("")) {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:files']/RDF:li/RDF:item";
				} else {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:files:" + name + "']/RDF:li/RDF:item";
				}
			} else {
				if(name == null || name.equals("")) {
					return "/document/files/file";
				} else {
					return "/document/files[@id='" + name + "']/file";
				}
			}
		}
		if(tagName != null && tagName.equals("page")) {
			if(isRDF) {
				if(name == null || name.equals("")) {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:pages']/RDF:li/RDF:item";
				} else {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:pages:" + name + "']/RDF:li/RDF:item";
				}
			} else {
				if(name == null || name.equals("")) {
					return "/document/pages/page";
				} else {
					return "/document/pages[@id='" + name + "']/page";
				}
			}
		}
		if(tagName != null && tagName.equals("message")) {
			if(isRDF) {
				return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:messages']/RDF:li/RDF:item";
			} else {
				return "/document/messages/message";
			}
		}
		
		if(tagName != null && tagName.equals("fileparam")) {
			if(isRDF) {
				if(name == null || name.equals("")) {
					return "/RDF:RDF/RDF:Description[@uc:for='urn:root:files']/uc:params/uc:param";
				} else {
					return "/RDF:RDF/RDF:Description[@uc:for='urn:root:files:" + name + "']/uc:params/uc:param";
				}
			} else {
				if(name == null || name.equals("")) {
					return "/document/params[@for='files']/param";
				} else {
					return "/document/params[@for='files." + name + "']/param";
				}
			}
		}
		if(tagName != null && tagName.equals("error")) {
			if(isRDF) {
				return "/RDF:RDF/RDF:Description/uc:errors/uc:error";
			} else {
				return "/document/errors/error";
			}
		}
		return "";
	}
	
	protected String path(String upperName, String tagName, String name, boolean isFull) {
		if(upperName != null && upperName.equals("row")) {
			if(isRDF) {
				if(isFull) {
					if(name == null || name.equals("")) {
						return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:default']/RDF:li/RDF:item/uc:" + tagName;
					} else {
						return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:" + name + "']/RDF:li/RDF:item/uc:" + tagName;
					}
				} else {
					if(tagName != null && tagName.equals("position()")) {
						return tagName;
					} else {
						return "uc:" + tagName;
					}
				}
			} else {
				if(isFull) {
					if(name != null && !name.equals("")) {
						return "/document/rows[@id = '" + name + "']/row/" + tagName;
					} else {
						return "/document/rows/row/" + tagName;
					}
				} else {
					return tagName;
				}
			}
		}
		if(upperName != null && upperName.equals("param")) {
			if(isRDF) {
				return "/RDF:RDF/RDF:Description/uc:params/uc:" + tagName;
			} else {
				return "/document/params/" + tagName;
			}
		}
		if(upperName != null && upperName.equals("prop")) {
			if(isRDF) {
				return "/RDF:RDF/RDF:Description/uc:props/uc:" + tagName;
			} else {
				return "/document/props/" + tagName;
			}
		}
		if(upperName != null && upperName.equals("query")) {
			if(isRDF) {
				if(name == null || name.equals("")) {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:default']/RDF:li/RDF:item/uc:" + tagName;
				} else {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:data:" + name + "']/RDF:li/RDF:item/uc:" + tagName;
				}
			} else {
				if(name == null || name.equals("")) {
					return "/document/rows/row/" + tagName;
				} else {
					return "/document/rows[@id='" + name + "']/row/" + tagName;
				}
			}
		}
		
		if(upperName != null && upperName.equals("code")) {
			if(tagName != null && tagName.equals("option")) {
				if(isRDF) {
					return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:code:" + name + "']/RDF:li/RDF:item";
				} else {
					return "/document/code[@name='" + name + "']/option";
				}
			} else {
				if(isRDF) {
					return "uc:" + tagName;
				} else {
					return tagName;
				}
			}
		}
		if(upperName != null && (upperName.equals("page") || upperName.equals("file") || upperName.equals("message"))) {
			if(isRDF) {
				return "uc:" + tagName;
			} else {
				return tagName;
			}
		}
		if(upperName != null && upperName.equals("fileparam")) {
			if(isFull) {
				if(isRDF) {
					if(name == null || name.equals("")) {
						return "/RDF:RDF/RDF:Seq/RDF:li/RDF:item/RDF:Description/uc:params/uc:param/uc:" + tagName;
					} else {
						return "/RDF:RDF/RDF:Seq[@RDF:about='urn:root:files:" + name + "']/RDF:li/RDF:item/RDF:Description/uc:params/uc:param/uc:" + tagName;
					}
				} else {
					if(name == null || name.equals("")) {
						return "/document/files/file/params/param/" + tagName;
					} else {
						return "/document/files[@id='" + name + "']/file/params/param/" + tagName;
					}
				}
			} else {
				if(isRDF) {
					return "uc:" + tagName;
				} else {
					return tagName;
				}
			}
		}
		return "";
	}
	
	protected String tag(String tagName, String name, boolean isStart) {
		if(tagName != null && tagName.equals("document")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:RDF xmlns:RDF=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:uc=\"" + this.uc + "\">";
				} else {
					return "</RDF:RDF>";
				}
			} else {
				if(isStart) {
					return "<document>";
				} else {
					return "</document>";
				}
			}
		}
		if(tagName != null && tagName.equals("params")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:Description uc:for=\"urn:root:params\"><uc:params>";
				} else {
					return "</uc:params></RDF:Description>";
				}
			} else {
				if(isStart) {
					return "<params for=\"data\">";
				} else {
					return "</params>";
				}
			}
		}
		if(tagName != null && tagName.equals("results")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:Description uc:for=\"urn:root:results\"><uc:results>";
				} else {
					return "</uc:results></RDF:Description>";
				}
			} else {
				if(isStart) {
					return "<results>";
				} else {
					return "</results>";
				}
			}
		}
		if(tagName != null && tagName.equals("props")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:Description uc:for=\"urn:root:props\"><uc:results>";
				} else {
					return "</uc:props></RDF:Description>";
				}
			} else {
				if(isStart) {
					return "<props>";
				} else {
					return "</props>";
				}
			}
		}
		if(tagName != null && tagName.equals("errors")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:Description uc:for=\"urn:root:errors\"><uc:errors>";
				} else {
					return "</uc:errors></RDF:Description>";
				}
			} else {
				if(isStart) {
					return "<errors for=\"data\">";
				} else {
					return "</errors>";
				}
			}
		}
		if(tagName != null && tagName.equals("fileparams")) {
			if(isRDF) {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<RDF:Description uc:for=\"urn:root:files\"><uc:params>";
					} else {
						return "<RDF:Description uc:for=\"urn:root:files:" + name + "\"><uc:params>";
					}
				} else {
					return "</uc:params></RDF:Description>";
				}
			} else {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<params for=\"files\">";
					} else {
						return "<params for=\"files." + name + "\">";
					}
				} else {
					return "</params>";
				}
			}
		}
		if(tagName != null && tagName.equals("param")) {
			if(isRDF) {
				if(isStart) {
					return "<uc:param>";
				} else {
					return "</uc:param>";
				}
			} else {
				if(isStart) {
					return "<param>";
				} else {
					return "</param>";
				}
			}
		}
		if(tagName != null && tagName.equals("result")) {
			if(isRDF) {
				if(isStart) {
					return "<uc:result>";
				} else {
					return "</uc:result>";
				}
			} else {
				if(isStart) {
					return "<result>";
				} else {
					return "</result>";
				}
			}
		}
		if(tagName != null && tagName.equals("prop")) {
			if(isRDF) {
				if(isStart) {
					return "<uc:prop>";
				} else {
					return "</uc:prop>";
				}
			} else {
				if(isStart) {
					return "<prop>";
				} else {
					return "</prop>";
				}
			}
		}
		if(tagName != null && tagName.equals("error")) {
			if(isRDF) {
				if(isStart) {
					return "<uc:error>";
				} else {
					return "</uc:error>";
				}
			} else {
				if(isStart) {
					return "<error>";
				} else {
					return "</error>";
				}
			}
		}
		if(tagName != null && tagName.equals("rows")) {
			if(isRDF) {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<RDF:Seq RDF:about=\"urn:root:data\">";
					} else {
						return "<RDF:Seq RDF:about=\"urn:root:data:" + name + "\">";
					}
				} else {
					return "</RDF:Seq>";
				}
			} else {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<rows>";
					} else {
						return "<rows id=\"" + name + "\">";
					}
				} else {
					return "</rows>";
				}
			}
		}
		if(tagName != null && tagName.equals("pages")) {
			if(isRDF) {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<RDF:Seq RDF:about=\"urn:root:pages\">";
					} else {
						return "<RDF:Seq RDF:about=\"urn:root:pages:" + name + "\">";
					}
				} else {
					return "</RDF:Seq>";
				}
			} else {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<pages>";
					} else {
						return "<pages name=\"" + name + "\">";
					}
				} else {
					return "</pages>";
				}
			}
		}
		if(tagName != null && tagName.equals("messages")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:Seq RDF:about=\"urn:root:messages\">";
				} else {
					return "</RDF:Seq>";
				}
			} else {
				if(isStart) {
					return "<messages>";
				} else {
					return "</messages>";
				}
			}
		}
		if(tagName != null && tagName.equals("code")) {
			if(isRDF) {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<RDF:Seq RDF:about=\"urn:root:code\">";
					} else {
						return "<RDF:Seq RDF:about=\"urn:root:code:" + name + "\">";
					}
				} else {
					return "</RDF:Seq>";
				}
			} else {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<code>";
					} else {
						return "<code name=\"" + name + "\">";
					}
				} else {
					return "</code>";
				}
			}
		}
		
		if(tagName != null && tagName.equals("files")) {
			if(isRDF) {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<RDF:Seq RDF:about=\"urn:root:files\">";
					} else {
						return "<RDF:Seq RDF:about=\"urn:root:files:" + name + "\">";
					}
				} else {
					return "</RDF:Seq>";
				}
			} else {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<files>";
					} else {
						return "<files id=\"" + name + "\">";
					}
				} else {
					return "</files>";
				}
			}
		}
		if(tagName != null && tagName.equals("row")) {
			if(isRDF) {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<RDF:li><RDF:item>";
					} else {
						
						return "<RDF:li><RDF:item RDF:about=\"urn:root:data:" + name + "\">";
					}
				} else {
					return "</RDF:item></RDF:li>";
				}
			} else {
				if(isStart) {
					if(name == null || name.equals("")) {
						return "<row>";
					} else {
						return "<row name=\"" + name + "\">";
					}
				} else {
					return "</row>";
				}
			}
		}
		if(tagName != null && tagName.equals("file")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:li><RDF:item>";
				} else {
					return "</RDF:item></RDF:li>";
				}
			} else {
				if(isStart) {
					return "<file>";
				} else {
					return "</file>";
				}
			}
		}
		if(tagName != null && tagName.equals("page")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:li><RDF:item>";
				} else {
					return "</RDF:item></RDF:li>";
				}
			} else {
				if(isStart) {
					return "<page>";
				} else {
					return "</page>";
				}
			}
		}
		if(tagName != null && tagName.equals("message")) {
			if(isRDF) {
				if(isStart) {
					return "<RDF:li><RDF:item>";
				} else {
					return "</RDF:item></RDF:li>";
				}
			} else {
				if(isStart) {
					return "<message>";
				} else {
					return "</message>";
				}
			}
		}
		return "";
	}
	
	protected String tag(String upperName, String tagName, String name, boolean isStart) {
		if(upperName != null 
			&& (
				upperName.equals("param")
				|| upperName.equals("result")
				|| upperName.equals("error")
				|| upperName.equals("prop")
				|| upperName.equals("page")
				|| upperName.equals("message")
				|| upperName.equals("file") 
				|| upperName.equals("files")
				|| (upperName.equals("row") && tagName.equals("rowcount"))
			)
		) {
			if(isRDF) {
				if(isStart) {
					return "<uc:" + tagName + ">";
				} else {
					return "</uc:" + tagName + ">";
				}
			} else {
				if(isStart) {
					return "<" + tagName + ">";
				} else {
					return "</" + tagName + ">";
				}
			}
		}
		if(upperName != null && (upperName.equals("row") || upperName.equals("code"))) {
			if(isRDF) {
				if(isStart) {
					return "uc:" + tagName + "";
				} else {
					return "uc:" + tagName + "";
				}
			} else {
				if(isStart) {
					return "" + tagName + "";
				} else {
					return "" + tagName + "";
				}
			}
		}
		return "";
	}
	protected String tag(String upperName, String tagName, String value, String label) {
		if(isRDF) {
			return "<RDF:li><RDF:item uc:value=\"" + value + "\" uc:label=\"" + label + "\" /></RDF:li>";
		} else {
			return "<" + tagName + " value=\"" + value + "\" label=\"" + label + "\" />";
		}
	}
}
