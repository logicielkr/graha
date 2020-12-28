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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Graha(그라하) 데이타바인딩 추상클레스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public abstract class DataBinderImpl implements DataBinder {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public DataBinderImpl() {
		LogHelper.setLogLevel(logger);
	}
	public void setString(PreparedStatement stmt, int index, String value) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = " + value);
		}
		stmt.setString(index, value);
	}
	public void setBoolean(PreparedStatement stmt, int index, boolean value) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = " + value);
		}
		stmt.setBoolean(index, value);
	}
	public void setInt(PreparedStatement stmt, int index, int value) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = " + value);
		}
		stmt.setInt(index, value);
	}
	public void setFloat(PreparedStatement stmt, int index, float value) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = " + value);
		}
		stmt.setFloat(index, value);
	}
	public void setLong(PreparedStatement stmt, int index, Long value) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = " + value);
		}
		stmt.setLong(index, value);
	}
	public void setDouble(PreparedStatement stmt, int index, double value) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = " + value);
		}
		stmt.setDouble(index, value);
	}
	public void setDate(PreparedStatement stmt, int index, java.sql.Date value) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = " + value);
		}
		stmt.setDate(index, value);
	}
	public void setTimestamp(PreparedStatement stmt, int index, java.sql.Timestamp value) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = " + value);
		}
		stmt.setTimestamp(index, value);
	}
	public void setNull(PreparedStatement stmt, int index, int type) throws SQLException {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(index + " = null");
		}
		stmt.setNull(index, type);
	}
	public abstract void bind(
		PreparedStatement stmt, 
		String datatype,
		int index, 
		String[] value, 
		int idx, 
		String defaultValue, 
		String pattern, 
		String table, 
		String column, 
		java.util.Map<String, Encryptor> encryptor, 
		String encrypt, 
		Buffer sb,
		Record params,
		XMLTag tags, 
		Record info,
		DatabaseMetaData dmd
	) throws SQLException, java.security.NoSuchProviderException;
}