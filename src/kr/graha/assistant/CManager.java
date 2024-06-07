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


package kr.graha.assistant;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRegistration;
import java.sql.Connection;
import java.sql.SQLException;
import kr.graha.helper.LOG;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Graha(그라하) Manager Connection Manager

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class CManager {
	private ServletConfig c = null;
	private HttpServletRequest request = null;
	private String def = null;
	private String mapping = null;
	private String jndi = null;
	private String rjndi = null;
	private String sjndi = null;
	private String resource = null;
	private String grahaCommonCodeTableName = null;
//	private String grahaMenuTableName = null;
	private List<String> jndis = null;
	private int majorVersion;
	private int minorVersion;
	private Properties _messges = null;
	protected CManager(ServletConfig c, HttpServletRequest request) {
		this.c = c;
		this.request = request;
		this.parse();
	}
	private void parse() {
		this.majorVersion = this.c.getServletContext().getMajorVersion();
		this.minorVersion = this.c.getServletContext().getMinorVersion();
		this.def = this.c.getInitParameter("def");
		this.mapping = this.c.getInitParameter("mapping");
		this.jndi = this.c.getInitParameter("jndi");
		this.resource = this.c.getInitParameter("resource");
		this.rjndi = this.request.getParameter("jndi");
		this.grahaCommonCodeTableName = this.c.getInitParameter("graha_common_code_table_name");
//		this.grahaMenuTableName = this.c.getInitParameter("graha_menu_table_name");
		
		if(this.rjndi != null) {
			this.rjndi = this.rjndi.trim();
		}
		if(this.jndi != null) {
			this.jndis = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(this.jndi, ",");
			while (st.hasMoreTokens()) {
				String s = (st.nextToken()).trim();
				if(this.rjndi != null && this.rjndi.equals(s)) {
					this.sjndi = s;
				}
				this.jndis.add(s);
			}
			if(this.sjndi == null && this.jndis.size() > 0) {
				this.sjndi = this.jndis.get(0);
			}
		}
	}
	protected String getJndi() {
		if(!this.valid()) {
			return null;
		} else {
			return this.sjndi;
		}
	}
	protected int getJndiSize() {
		if(!this.valid()) {
			return 0;
		} else {
			return this.jndis.size();
		}
	}
	protected String[] getJndis() {
		if(!this.valid()) {
			return null;
		} else {
			return this.jndis.toArray(new String[]{});
		}
	}
	private String getResource() {
		return this.resource;
	}
	protected Properties getMessages() throws IOException {
		if(this._messges == null) {
			this._messges = new Properties();
			if(this.getResource() == null) {
				this._messges.load(getClass().getResourceAsStream("resource/ko.properties"));
			} else {
				this._messges.load(getClass().getResourceAsStream(this.getResource()));
			}
		}
		return this._messges;
	}
	protected StringBuilder getPropertyList(String prefix) throws IOException {
		Properties messges = this.getMessages();
		Enumeration names = messges.propertyNames();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		while(names.hasMoreElements()) {
			String name = (String)names.nextElement();
			if(name.startsWith(prefix + ".")) {
				if(index == 0) {
					sb.append("<props>\n");
				}
				sb.append("<");
				sb.append(name);
				sb.append(">");
				sb.append("<![CDATA[");
				sb.append(this.getProperty(messges, name));
				sb.append("]]>");
				sb.append("</");
				sb.append(name);
				sb.append(">\n");
				index++;
			}
		}
		if(index > 0) {
			sb.append("</props>\n");
		}
		return sb;
	}
	protected String getProperty(Properties messges, String key) {
		String value = messges.getProperty(key);
		if(value != null) {
			try {
				return new String(value.getBytes("iso-8859-1"), "UTF-8");
			} catch (java.io.UnsupportedEncodingException e) {
				LOG.severe(e);
			}
		}
		return null;
	}

	protected String getDef() {
		return this.def;
	}
	protected String getMapping() {
		return this.mapping;
	}
	protected String getGrahaCommonCodeTableName() {
		if(this.grahaCommonCodeTableName == null || this.grahaCommonCodeTableName.trim().equals("")) {
			return null;
		}
		return this.grahaCommonCodeTableName;
	}
	protected boolean valid() {
		if(this.jndi == null || this.jndis == null || this.jndis.size() == 0 || this.sjndi == null) {
			return false;
		} else if(this.rjndi != null && !this.rjndi.equals(this.sjndi)) {
			return false;
		} else {
			return true;
		}
	}
	protected Connection getConnection() throws NamingException, SQLException {
		if(!this.valid()) {
			return null;
		} else {
			InitialContext cxt = new InitialContext();
			String source = null;
			if(this.sjndi.startsWith("java:")) {
				source = this.sjndi;
			} else {
				source = "java:/comp/env/" + this.sjndi;
			}
			DataSource ds = (DataSource)cxt.lookup(source);
			Connection con = ds.getConnection();
			return con;
		}
	}
	protected String value(String value) {
		if(value == null) {
			return "";
		} else if(
			this.majorVersion < 3
			|| (
				this.majorVersion == 3
				&& this.minorVersion == 0
			)
		) {
			return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		} else {
			return value;
		}
	}
	protected List getGrahaAppRootPath() {
		List grahaAppRootPath = new ArrayList();
		java.util.Map map = this.request.getServletContext().getServletRegistrations();
		java.util.Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()) {
			String key = (String)keys.next();
			ServletRegistration sr = (ServletRegistration)map.get(key);
			if(sr.getClassName().equals("kr.graha.servlet.GeneratorServlet")) {
				java.util.Iterator it = sr.getMappings().iterator();
				while(it.hasNext()) {
					String path = (String)it.next();
					if(path.endsWith("/*")) {
						grahaAppRootPath.add(this.request.getContextPath() + path.substring(0, path.lastIndexOf("/*") + 1));
					}
				}
			}
		}
		return grahaAppRootPath;
	}
	protected String param(String key) {
		return this.value(this.request.getParameter(key));
	}
	protected String param(String key, String defaultValue) {
		if(this.request.getParameter(key) == null) {
			return defaultValue;
		} else if(this.request.getParameter(key).equals("")) {
			return defaultValue;
		} else {
			return this.value(this.request.getParameter(key));
		}
	}
	protected java.util.Enumeration<java.lang.String> getParameterNames() {
		return this.request.getParameterNames();
	}
	protected String[] getParameterValues(String key) {
		return this.request.getParameterValues(key);
	}
	protected String getParameter(String key) {
		return this.request.getParameter(key);
	}
	protected String getRealPath(String path) {
		return this.c.getServletContext().getRealPath(path);
	}
}