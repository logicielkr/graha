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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import kr.graha.helper.STR;
import kr.graha.helper.LOG;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Date;
import java.security.NoSuchProviderException;
import kr.graha.post.lib.Buffer;
import kr.graha.post.interfaces.ConnectionFactory;
import kr.graha.post.interfaces.Encryptor;
import java.util.Map;

/**
 * GRows
 * @author HeonJik, KIM
 * @version 0.9
 * @since 0.9
 */

public class GRows {
	private String name;
	private List<GColumn> columns = null;
	private List<GRow> rows = null;
	private boolean columnAuto;
	private int total = 0;
	private int append = 0;
	public GRows() {
	}
	public GRows(String name, GColumn column, int value) {
		this.name = name;
		this.add(column);
		this.add(new GRow(value));
	}
	public GRows(String name) {
		this.name = name;
	}
	public boolean containsKey(String columnName) {
		if(STR.valid(this.rows)) {
			for(int i = 0; i < this.rows.size(); i++) {
				if(this.rows.get(i) != null) {
					if(this.rows.get(i).containsKey(columnName, this.columns)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public Object get(String columnName) {
		if(STR.valid(this.rows)) {
			for(int i = 0; i < this.rows.size(); i++) {
				if(this.rows.get(i) != null) {
					Object value = this.rows.get(i).get(columnName, this.columns);
					if(value != null) {
						return value;
					}
				}
			}
		}
		return null;
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
	public void add(GRow row) {
		if(this.rows == null) {
			this.rows = new ArrayList<GRow>();
		}
		this.rows.add(row);
	}
	public void add(GColumn column) {
		if(this.columns == null) {
			this.columns = new ArrayList<GColumn>();
		}
		this.columns.add(column);
	}
	protected String getName() {
		return this.name;
	}
	public int size() {
		if(this.rows != null) {
			return this.rows.size();
		} else {
			return 0;
		}
	}
	public static GRows load(
		ResultSet rs,
		String commandOrTableName,
		Map<String, Encryptor> encryptor,
		java.util.Map<String, String> encrypted,
		java.util.Map<String, String> pattern,
		ConnectionFactory connectionFactory,
		boolean multi,
		boolean columnAuto
	) throws SQLException {
		GRows rows = new GRows(commandOrTableName);
		rows.columnAuto = columnAuto;
//		int index = 0;
		ResultSetMetaData rsmd = rs.getMetaData();
		for(int x = 1; x <= rsmd.getColumnCount(); x++) {
			rows.add(new GColumn(rsmd, x));
		}
		while(rs.next()) {
			GRow row = new GRow();
			for(int x = 1; x <= rsmd.getColumnCount(); x++) {
				if(connectionFactory.oracle()) {
					if(STR.compareIgnoreCase(rsmd.getColumnName(x), "RNUM$")) {
						continue;
					}
				}
				String value = null;
				if(
					connectionFactory.sqlite() &&
					rsmd.getColumnType(x) == java.sql.Types.DATE
				) {
					if(
						STR.compare(rsmd.getColumnTypeName(x), "DATETIME") &&
						pattern != null &&
						pattern.containsKey(rsmd.getColumnName(x).toLowerCase())
					) {
						value = rows.getSQLiteTimestampOrDateValue(rs, x, pattern.get(rsmd.getColumnName(x).toLowerCase()), true);
					} else if(
						pattern != null &&
						pattern.containsKey(rsmd.getColumnName(x).toLowerCase())
					) {
						value = rows.getSQLiteTimestampOrDateValue(rs, x, pattern.get(rsmd.getColumnName(x).toLowerCase()), false);
					} else {
						value = rs.getString(x);
					}
				} else if(
					rsmd.getColumnType(x) == java.sql.Types.DATE &&
					rs.getDate(x) != null &&
					pattern != null &&
					pattern.containsKey(rsmd.getColumnName(x).toLowerCase())
				) {
					value = STR.formatDate(rs.getDate(x), pattern.get(rsmd.getColumnName(x).toLowerCase()));
				} else if(rsmd.getColumnType(x) == java.sql.Types.TIMESTAMP &&
					rs.getTimestamp(x) != null &&
					pattern != null &&
					pattern.containsKey(rsmd.getColumnName(x).toLowerCase())
				) {
					value = STR.formatDate(rs.getTimestamp(x), pattern.get(rsmd.getColumnName(x).toLowerCase()));
				} else {
					value = rs.getString(x);
				}
				if(value != null) {
					if(encrypted != null && encryptor != null && encrypted.containsKey(rsmd.getColumnName(x).toLowerCase())) {
						try {
							value = encryptor.get(encrypted.get(rsmd.getColumnName(x).toLowerCase())).decrypt(value);
						} catch (NoSuchProviderException e) {
							LOG.severe(e);
						}
					}
				}
				row.add(value);
			}
			rows.add(row);
//			index++;
		}
		return rows;
	}
	private String getSQLiteTimestampOrDateValue(ResultSet rs, int index, String pattern, boolean isTimestamp) throws SQLException {
/*
Caused by: java.text.ParseException: Unparseable date: "2015-08-05 15:11:16" does not match (\p{Nd}++)\Q-\E(\p{Nd}++)\Q-\E(\p{Nd}++)\Q \E(\p{Nd}++)\Q:\E(\p{Nd}++)\Q:\E(\p{Nd}++)\Q.\E(\p{Nd}++)
*/
		Timestamp timestampValue = null;
		Date dateValue = null;
		try {
			if(isTimestamp) {
				timestampValue = rs.getTimestamp(index);
				if(pattern == null) {
					return timestampValue.toString();
				} else {
					return STR.formatDate(timestampValue, pattern);
				}
			} else {
				dateValue = rs.getTimestamp(index);
				if(pattern == null) {
					return dateValue.toString();
				} else {
					return STR.formatDate(dateValue, pattern);
				}
			}
		} catch (SQLException e) {
			LOG.severe(e);
		}
		String str = null;
		try {
			str = rs.getString(index);
			if(str != null) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				if(isTimestamp) {
					if(pattern == null) {
						return (new Timestamp(df.parse(str).getTime())).toString();
					} else {
						return STR.formatDate(new Timestamp(df.parse(str).getTime()), pattern);
					}
				} else {
					if(pattern == null) {
						return df.parse(str).toString();
					} else {
						return STR.formatDate(df.parse(str), pattern);
					}
				}
			}
		} catch (SQLException e) {
			LOG.severe(e);
			throw e;
		} catch (java.text.ParseException e) {
			LOG.severe(e);
		}
		return str;
	}
	protected void toXML(Buffer xml, boolean rdf) {
		if(
			(this.rows != null && this.rows.size() > 0) ||
			this.getTotal() > 0 ||
			this.getAppend() > 0
		) {
			if(rdf) {
				if(STR.valid(this.getName())) {
					xml.appendL(1, "<RDF:Seq RDF:about=\"urn:root:data:" + this.getName() + "\">");
				} else {
					xml.appendL(1, "<RDF:Seq RDF:about=\"urn:root:data\">");
				}
			} else {
				if(STR.valid(this.getName())) {
					xml.appendL(1, "<rows id=\"" + this.getName() + "\">");
				} else {
					xml.appendL(1, "<rows>");
				}
			}
		}
		if(this.rows != null && this.rows.size() > 0) {
			for(int i = 0; i < this.rows.size(); i++) {
				if(this.rows.get(i) == null) {
					continue;
				}
				this.rows.get(i).toXML(xml, this.columns, this.columnAuto, rdf);
			}
		}
		if(this.getTotal() > 0) {
			int size = 0;
			if(this.rows != null) {
				size = this.rows.size();
			}
			for(int i = size; i < this.getTotal(); i++) {
				new GRow().toXML(xml, this.columns, false, rdf);
			}
		} else if(this.getAppend() > 0) {
			for(int i = 0; i < this.getAppend(); i++) {
				new GRow().toXML(xml, this.columns, false, rdf);
			}
		}
		if(
			(this.rows != null && this.rows.size() > 0) ||
			this.getTotal() > 0 ||
			this.getAppend() > 0
		) {
			if(rdf) {
				xml.appendL(1, "</RDF:Seq>");
			} else {
				xml.appendL(1, "</rows>");
			}
		}
	}
}
