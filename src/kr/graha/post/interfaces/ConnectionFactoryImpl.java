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


package kr.graha.post.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import kr.graha.post.lib.Record;
import kr.graha.post.model.utility.TextParser;
import kr.graha.helper.STR;
import kr.graha.helper.DB;
import kr.graha.post.model.Jndi;
import kr.graha.post.model.Jdbc;
import kr.graha.helper.LOG;

/**
 * Graha(그라하) ConnectionFactory 인터페이스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public abstract class ConnectionFactoryImpl implements ConnectionFactory {
	private DatabaseMetaData dmd = null;
	private Connection conn = null;
	private Jndi jndi = null;
	private Jdbc jdbc = null;
	private Record params = null;
	public ConnectionFactoryImpl(Jndi jndi) {
		this.setJndi(jndi);
	}
	public ConnectionFactoryImpl(Jdbc jdbc, Record params) {
		this.setJdbc(jdbc, params);
	}
	public ConnectionFactoryImpl() {
	}
	private String getSqlListTemplate() {
		if(this.jndi != null) {
			return this.jndi.getSqlListTemplate();
		} else 	if(this.jdbc != null) {
			return this.jdbc.getSqlListTemplate();
		}
		return null;
	}
	private String getSqlCntTemplate() {
		if(this.jndi != null) {
			return this.jndi.getSqlCntTemplate();
		} else 	if(this.jdbc != null) {
			return this.jdbc.getSqlCntTemplate();
		}
		return null;
	}
	private String getSqlSequenceTemplate() {
		if(this.jndi != null) {
			return this.jndi.getSqlSequenceTemplate();
		} else 	if(this.jdbc != null) {
			return this.jdbc.getSqlSequenceTemplate();
		}
		return null;
	}
	private DatabaseMetaData getMetaData() throws SQLException {
		if(this.dmd == null) {
			if(this.conn == null) {
				this.connect();
			}
			if(this.conn != null) {
				this.dmd = this.conn.getMetaData();
			}
		}
		return this.dmd;
	}
	public void close() {
		if(this.conn != null) {
			this.commit();
			DB.close(this.conn);
			this.conn = null;
		}
	}
	public void abort() {
		if(this.conn != null) {
			this.rollback();
			DB.close(this.conn);
			this.conn = null;
		}
	}
	public void rollback() {
		try {
			if(this.conn != null) {
				this.conn.rollback();
			}
		} catch (SQLException e) {
			LOG.severe(e); 
		}
	}
	public void commit() {
		try {
			if(this.conn != null) {
				this.conn.commit();
			}
		} catch (SQLException e) {
			LOG.severe(e); 
		}
	}
	public void setJndi(Jndi jndi) {
		this.jndi = jndi;
	}
	public void setJdbc(Jdbc jdbc, Record params) {
		this.jdbc = jdbc;
		this.params = params;
	}
	private void connect() throws SQLException {
		if(this.conn == null) {
			if(this.jndi != null) {
				this.conn = this.jndi.getConnection();
				this.conn.setAutoCommit(false);
			} else if(this.jdbc != null) {
				this.conn = this.jdbc.getConnection(this.params);
				this.conn.setAutoCommit(false);
			}
		}
	}
	public Connection getConnection() throws SQLException {
		if(this.conn == null) {
			this.connect();
		}
		return this.conn;
	}
	public String sqlForList(String sql) throws SQLException {
		if(STR.valid(this.getSqlListTemplate())) {
			Record p = new Record();
			p.put(Record.key(Record.PREFIX_TYPE_NONE, "sql"), sql);
			return TextParser.parse(this.getSqlListTemplate(), p);
		}
		if(oracle()) {
			return "select * from (select a$.*, rownum as rnum$ from (" + sql + ")  a$ where rownum <= ?) where rnum$ >= ?";
		} else if(derby()) {
			return (sql + " { limit ? offset ? }");
		}
		return (sql + " limit ? offset ?");
	}
	public String sqlForCount(String sql) throws SQLException {
		if(STR.valid(this.getSqlCntTemplate())) {
			Record p = new Record();
			p.put(Record.key(Record.PREFIX_TYPE_NONE, "sql"), sql);
			return TextParser.parse(this.getSqlCntTemplate(), p);
		}
		if(oracle()) {
			return ("select count(*) from (" + sql + ")");
		}
		return ("select count(*) from (" + sql + ") as _");
	}
	public boolean oracle() throws SQLException {
		if(this.getMetaData() != null) {
			if(STR.vexistsIgnoreCase(this.getMetaData().getDatabaseProductName(), "Oracle", "Tibero")) {
				return true;
			}
		}
		return false;
	}
	public boolean derby() throws SQLException {
		if(this.getMetaData() != null) {
			if(STR.compareIgnoreCase(this.getMetaData().getDatabaseProductName(), "Apache Derby")) {
				return true;
			}
		}
		return false;
	}
	public boolean hsql() throws SQLException {
		if(this.getMetaData() != null) {
			if(STR.compareIgnoreCase(this.getMetaData().getDatabaseProductName(), "HSQL Database Engine")) {
				return true;
			}
		}
		return false;
	}
	public boolean sqlite() throws SQLException {
		if(this.getMetaData() != null) {
			if(STR.compareIgnoreCase(this.getMetaData().getDatabaseProductName(), "SQLite")) {
				return true;
			}
		}
		return false;
	}
	public String sqlForSequence(String sequenceName) throws SQLException {
		if(STR.valid(this.getSqlSequenceTemplate())) {
			Record p = new Record();
			p.put(Record.key(Record.PREFIX_TYPE_NONE, "name"), sequenceName);
			return TextParser.parse(this.getSqlSequenceTemplate(), p);
		}
		if(this.oracle()) {
			return ("select " + sequenceName + " from dual");
		} else if(this.derby()) {
			return ("select " + sequenceName + " from sysibm.sysdummy1");
		} else if(this.hsql()) {
			return ("select " + sequenceName + " from (values(0))");
		}
		return ("select " + sequenceName);
	}
}
