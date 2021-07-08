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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Graha(그라하) Manager Connection Manager

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class CManager {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private ServletConfig c = null;
	private HttpServletRequest request = null;
	private String def = null;
	private String mapping = null;
	private String jndi = null;
	private String rjndi = null;
	private String sjndi = null;
	private String resource = null;
	private List<String> jndis = null;
	protected CManager(ServletConfig c, HttpServletRequest request) {
		this.c = c;
		this.request = request;
		this.parse();
	}
	private void parse() {
		this.def = this.c.getInitParameter("def");
		this.mapping = this.c.getInitParameter("mapping");
		this.jndi = this.c.getInitParameter("jndi");
		this.resource = this.c.getInitParameter("resource");
		this.rjndi = this.request.getParameter("jndi");
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
	protected String getResource() {
		return this.resource;
	}
	protected String getDef() {
		return this.def;
	}
	protected String getMapping() {
		return this.mapping;
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
}