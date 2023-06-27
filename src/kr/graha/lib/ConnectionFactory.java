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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

/**
 * Graha(그라하) ConnectionFactory 인터페이스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public interface ConnectionFactory {
	Connection getConnection(Record info, Record parmas) throws SQLException;
	String getListSql(String sql, Record info, DatabaseMetaData dmd) throws SQLException;
	String getCountSql(String sql, Record info, DatabaseMetaData dmd) throws SQLException;
	String getSeqSql(String name, Record info, DatabaseMetaData dmd) throws SQLException;
}