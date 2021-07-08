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


package kr.graha.helper;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import javax.naming.NamingException;
import java.util.StringTokenizer;
import java.sql.ResultSetMetaData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Graha(그라하) 데이타베이스(DB) 관련 유틸리티

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.5.0.6
 */

public final class DB {
	private static Logger logger = Logger.getLogger("kr.graha.helper.DB");
	private DB() {
	}
/**
 * JNDI 이름으로 데이타베이스 연결을 얻는다.
 * @param jndi 데이타베이스 연결에 필요한 JNDI 이름
 * @return 데이타베이스 연결 객체(java.sql.Connection)
 */
	public static Connection getConnection(String jndi) {
		Connection con = null;
		try {
			javax.naming.InitialContext cxt = new javax.naming.InitialContext();
			String source = null;
			if(jndi.startsWith("java:")) {
				source = jndi;
			} else {
				source = "java:/comp/env/" + jndi;
			}
			javax.sql.DataSource ds = (javax.sql.DataSource)cxt.lookup(source);
			con = ds.getConnection();
		} catch (SQLException | NamingException e2) {
			if(logger.isLoggable(Level.SEVERE)) {
				logger.severe(LOG.toString(e2));
			}
		}
		return con;
	}
/**
 * 데이타베이스 연결을 닫는다.
 * @param con 데이타베이스 연결
 */
	public static void close(Connection con) {
		if(con != null) {
			try {
				if(!con.isClosed()) {
					con.close();
					con = null;
				}
			} catch (SQLException e) {
				logger.warning(LOG.toString(e));
			}
		}
	}
/**
 * PreparedStatement 객체를 닫는다.
 * @param pstmt PreparedStatement 객체
 */
	public static void close(PreparedStatement pstmt) {
		if(pstmt != null) {
			try {
				pstmt.close();
				pstmt = null;
			} catch (SQLException e) {
				logger.warning(LOG.toString(e));
			}
		}
	}
/**
 * Statement를 닫는다.
 * @param stmt Statement 객체
 */
	public static void close(Statement stmt) {
		if(stmt != null) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException e) {
				logger.warning(LOG.toString(e));
			}
		}
	}
/**
 * ResultSet 객체를 닫는다.
 * @param rs ResultSet 객체
 */
	public static void close(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				logger.warning(LOG.toString(e));
			}
		}
	}
/**
 * 문자열의 첫 번째 문자는 대문자로 하고, 나머지 문자는 소문자로 한다.
 * 중간에 "_" 가 있는 경우 이를 공백처럼 취급하고, 공백은 무시한다.
 * 이 메소드는 데이타베이스의 테이블 혹은 컬럼 이름으로부터 Java class, field, method 이를을 얻기 위한 것이다.
 * @param text 변환할 문자열
 * @param firstLetter 가장 첫번째 문자를 대문자로 할지 여부(class, method 이름은 대문자로 하고, field 이름은 소문자로 한다)
 * @return 첫 번째 문자를 대문자로 변환한 문자열
 */
	private static String initcap(String text, boolean firstLetter) {
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(text, "_");
		int index = 0;
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if(index == 0 && !firstLetter) {
				sb.append(s.toLowerCase());
			} else {
				sb.append(s.substring(0, 1).toUpperCase());
				sb.append(s.substring(1).toLowerCase());
			}
			index++;
		}
		return sb.toString();
	}
/**
 * 문자열의 첫 번째 문자는 대문자로 하고, 나머지 문자는 소문자로 한다.
 * 중간에 "_" 가 있는 경우 이를 공백처럼 취급하고, 공백은 무시한다.
 * 이 메소드는 데이타베이스의 테이블 혹은 컬럼 이름으로부터 Java class, method 이를을 얻기 위한 것이다.
 * @param text 변환할 문자열
 * @return 첫 번째 문자를 대문자로 변환한 문자열
 * @see initcap
 */
	public static String getClassOrMethodName(String text) {
		return initcap(text, true);
	}
/**
 * 문자열의 첫 번째 문자는 대문자로 하고, 나머지 문자는 소문자로 한다.
 * 가장 첫 번째 문자를 소문자로 한다.
 * 중간에 "_" 가 있는 경우 이를 공백처럼 취급하고, 공백은 무시한다.
 * 이 메소드는 데이타베이스의 테이블 혹은 컬럼 이름으로부터 Java field 이를을 얻기 위한 것이다.
 * @param text 변환할 문자열
 * @return 첫 번째 문자를 대문자로 변환한 문자열
 * @see initcap
 */
	public static String getFieldName(String text) {
		return initcap(text, false);
	}
/**
 * java.sql.Types 으로부터 Java 자료형을 구해서, 그 이름을 문자열로 반환한다.
 * Primitive type 이 사용되지 않음에 주의하라.
 * java.sql.Types.VARCHAR -> String
 * java.sql.Types.INTEGER -> Integer
 * java.sql.Types.FLOAT -> Float
 * java.sql.Types.DOUBLE -> Double
 * java.sql.Types.BIGINT -> Long
 * java.sql.Types.DATE -> java.util.Date
 * java.sql.Types.TIMESTAMP -> java.sql.Timestamp
 * 위에 해당하지 않는 경우 -> Object
 * @param dataType java.sql.Types 이하에 정의된 자료형(ResultSetMetaData.getColumnType 혹은 DatabaseMetaData.getColumns 으로 얻은 ResultSet 의 DATA_TYPE)
 * @param typeName 자료형의 문자 표현(ResultSetMetaData.getColumnTypeName 혹은 DatabaseMetaData.getColumns 으로 얻은 ResultSet 의 TYPE_NAME)
 * @return 문자열로 변환된 Java 자료형
 */
	private static String getJavaDataType(int dataType, String typeName) {
		if(dataType == java.sql.Types.VARCHAR) {
			return "String";
		} else if(dataType == java.sql.Types.INTEGER) {
			return "Integer";
		} else if(dataType == java.sql.Types.FLOAT) {
			return "Float";
		} else if(dataType == java.sql.Types.DOUBLE) {
			return "Double";
		} else if(dataType == java.sql.Types.BIGINT) {
			return "Long";
		} else if(dataType == java.sql.Types.DATE) {
			return "java.util.Date";
		} else if(dataType == java.sql.Types.TIMESTAMP) {
			return "java.sql.Timestamp";
		} else {
			return "Object";
		}
	}
/**
 * PreparedStatement 에 파라미터를 바인딩한다.
 * null 은 허용되지 않는다.
 * String -> pstmt.setString
 * Integer -> pstmt.setInt
 * Long -> pstmt.setLong
 * Float -> pstmt.setLong
 * Double -> pstmt.setDouble
 * java.sql.Date -> pstmt.setDate
 * java.sql.Timestamp -> pstmt.setTimestamp
 * 위에 해당하지 않는 경우 -> pstmt.setObject
 * @param pstmt PreparedStatement 객체
 * @param index 바인딩할 위치
 * @param param 파라미터 값
 */
	public static void bind(PreparedStatement pstmt, int index, Object param) throws SQLException {
		if(param instanceof String) {
			pstmt.setString(index, (String)param);
		} else if(param instanceof Integer) {
			pstmt.setInt(index, ((Integer)param).intValue());
		} else if(param instanceof Long) {
			pstmt.setLong(index, ((Long)param).longValue());
		} else if(param instanceof Float) {
			pstmt.setFloat(index, ((Float)param).floatValue());
		} else if(param instanceof Double) {
			pstmt.setDouble(index, ((Double)param).doubleValue());
		} else if(param instanceof java.sql.Date) {
			pstmt.setDate(index, (java.sql.Date)param);
		} else if(param instanceof java.sql.Timestamp) {
			pstmt.setTimestamp(index, (java.sql.Timestamp)param);
		} else {
			pstmt.setObject(index, param);
		}
	}
/**
 * 주어진 sql을 실행하여 그 실행결과로부터 Java class 소소크드를 생성한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param table_name 테이블 이름.  class 이름이지만, ("_" 로 구분된) 테이블 이름 형식으로 한다.
 * @param sql 실행할 sql 구문
 * @param params sql 을 실행할 때 바인딩할 파라미터
 * @param out 결과를 돌려주는 대신 이 스트림에 출력한다.
 */
	public static void generateClassSource(Connection con, String table_name, String sql, Object[] params, java.io.PrintStream out) throws SQLException {
		out.println("class " + getClassOrMethodName(table_name) + " {");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sql);
			if(params != null) {
				for(int i = 0; i < params.length; i++) {
					bind(pstmt, i + 1, params[i]);
				}
			}
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			
			for(int x = 1; x <= rsmd.getColumnCount(); x++) {
				out.println("	private " + getJavaDataType(rsmd.getColumnType(x), rsmd.getColumnTypeName(x)) + " " + getFieldName(rsmd.getColumnName(x)) + ";");
				out.println("	public void set" + getClassOrMethodName(rsmd.getColumnName(x)) + "(" + getJavaDataType(rsmd.getColumnType(x), rsmd.getColumnTypeName(x)) + " " + getFieldName(rsmd.getColumnName(x)) + ") {");
				out.println("		this." + getFieldName(rsmd.getColumnName(x)) + " = " + getFieldName(rsmd.getColumnName(x)) + ";");
				out.println("	}");
				out.println("	public " + getJavaDataType(rsmd.getColumnType(x), rsmd.getColumnTypeName(x)) + " get" + getClassOrMethodName(rsmd.getColumnName(x)) + "() {");
				out.println("		return this." + getFieldName(rsmd.getColumnName(x)) + ";");
				out.println("	}");
			}
			rs.close();
			rs = null;
		} catch (SQLException e) {
			logger.warning(LOG.toString(e));
			throw e;
		} finally {
			close(rs);
		}
		out.println("	public " + getClassOrMethodName(table_name) + "() {");
		out.println("	}");
		out.println("}");
	}
/**
 * 주어진 sql을 실행하여 그 실행결과로부터 Java class 소소크드를 생성하여 반환한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param table_name 테이블 이름.  class 이름이지만, ("_" 로 구분된) 테이블 이름 형식으로 한다.
 * @param sql 실행할 sql 구문
 * @param params sql 을 실행할 때 바인딩할 파라미터
 * @return Java class 소스코드
 */
	public static String generateClassSource(Connection con, String table_name, String sql, Object[] params) throws SQLException {
		return generateClassSource(con, table_name, sql, params, (String)null);
	}
/**
 * 주어진 sql을 실행하여 그 실행결과로부터 Java class 소소크드를 생성하여 반환한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param table_name 테이블 이름.  class 이름이지만, ("_" 로 구분된) 테이블 이름 형식으로 한다.
 * @param sql 실행할 sql 구문
 * @param params sql 을 실행할 때 바인딩할 파라미터
 * @param charsetName charset 이름
 * @return Java class 소스코드
 */
	public static String generateClassSource(Connection con, String table_name, String sql, Object[] params, String charsetName) throws SQLException {
		String result = null;
		ByteArrayOutputStream baos = null;
		PrintStream out = null;
		try {
			baos = new ByteArrayOutputStream();
			out = new PrintStream(baos);
			generateClassSource(con, table_name, sql, params, out);
			if(charsetName == null) {
				result = baos.toString();
			} else {
				result = baos.toString(charsetName);
			}
			out.close();
			out = null;
			baos.close();
			baos = null;
		} catch (SQLException e) {	
			logger.warning(LOG.toString(e));
			throw e;
		} catch (IOException e) {
			logger.warning(LOG.toString(e));
		} finally {
			if(out != null) {
				out.close();
				out = null;
			}
			if(baos != null) {
				try {
					baos.close();
					baos = null;
				} catch (IOException e) {logger.warning(LOG.toString(e));}
			}
		}
		return result;
	}
/**
 * 주어진 테이블 이름으로부터 Java class 소소크드를 생성한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param schemaName 스키마 이름 (이 파라미터는 DatabaseMetaData.getColumns 메소드의 2번째 파라미터로 전달된다)
 * @param table_name 테이블 이름.  ("_" 로 구분된) 형식으로 전달한다.
 * @param out 결과를 돌려주는 대신 이 스트림에 출력한다.
 */
	public static void generateClassSource(Connection con, String schemaName, String table_name, java.io.PrintStream out) throws SQLException {
		out.println("class " + getClassOrMethodName(table_name) + " {");
		ResultSet rs = null;
		try {
			DatabaseMetaData m = con.getMetaData();
			rs = m.getColumns(con.getCatalog(), schemaName, table_name, "%");
			while(rs.next()) {
				out.println("	private " + getJavaDataType(rs.getInt("DATA_TYPE"), rs.getString("TYPE_NAME")) + " " + getFieldName(rs.getString("COLUMN_NAME")) + ";");
				out.println("	public void set" + getClassOrMethodName(rs.getString("COLUMN_NAME")) + "(" + getJavaDataType(rs.getInt("DATA_TYPE"), rs.getString("TYPE_NAME")) + " " + getFieldName(rs.getString("COLUMN_NAME")) + ") {");
				out.println("		this." + getFieldName(rs.getString("COLUMN_NAME")) + " = " + getFieldName(rs.getString("COLUMN_NAME")) + ";");
				out.println("	}");
				out.println("	public " + getJavaDataType(rs.getInt("DATA_TYPE"), rs.getString("TYPE_NAME")) + " get" + getClassOrMethodName(rs.getString("COLUMN_NAME")) + "() {");
				out.println("		return this." + getFieldName(rs.getString("COLUMN_NAME")) + ";");
				out.println("	}");
			}
			close(rs);
		} catch (SQLException e) {
			logger.warning(LOG.toString(e));
			throw e;
		} finally {
			close(rs);
		}
		out.println("	public " + getClassOrMethodName(table_name) + "() {");
		out.println("	}");
		out.println("}");
	}
/**
 * 주어진 테이블 이름으로부터 Java class 소소크드를 생성하여 반환한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param schemaName 스키마 이름 (이 파라미터는 DatabaseMetaData.getColumns 메소드의 2번째 파라미터로 전달된다)
 * @param table_name 테이블 이름.  ("_" 로 구분된) 형식으로 전달한다.
 * @return Java class 소스코드
 */
	public static String generateClassSource(Connection con, String schemaName, String table_name) throws SQLException {
		return generateClassSource(con, schemaName, table_name, (String)null);
	}
/**
 * 주어진 테이블 이름으로부터 Java class 소소크드를 생성하여 반환한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param schemaName 스키마 이름 (이 파라미터는 DatabaseMetaData.getColumns 메소드의 2번째 파라미터로 전달된다)
 * @param table_name 테이블 이름.  ("_" 로 구분된) 형식으로 전달한다.
 * @param charsetName charset 이름
 * @return Java class 소스코드
 */
	public static String generateClassSource(Connection con, String schemaName, String table_name, String charsetName) throws SQLException {
		String result = null;
		ByteArrayOutputStream baos = null;
		PrintStream out = null;
		try {
			baos = new ByteArrayOutputStream();
			out = new PrintStream(baos);
			generateClassSource(con, schemaName, table_name, out);
			if(charsetName == null) {
				result = baos.toString();
			} else {
				result = baos.toString(charsetName);
			}
			out.close();
			out = null;
			baos.close();
			baos = null;
		} catch (SQLException e) {	
			logger.warning(LOG.toString(e));
			throw e;
		} catch (IOException e) {
			logger.warning(LOG.toString(e));
		} finally {
			if(out != null) {
				out.close();
				out = null;
			}
			if(baos != null) {
				try {
					baos.close();
					baos = null;
				} catch (IOException e) {logger.warning(LOG.toString(e));}
			}
		}
		return result;
	}
/**
 * Java class 의 field 이름 형태의 문자열을 입력받아, 데이타베이스 컬럼 이름 형태의 문자열을 반환한다.
 * 문자열을 모두 소문자로 바꾸고, 문자열 중간에 대문자가 있는 경우 대문자 앞에 "_" 를 붙인다. 
 * @param fieldName 필드이름
 * @return 데이타베이스 컬럼 이름 형태의 문자열
 */
	private static String getColumnName(String fieldName) {
		StringBuffer result = new StringBuffer();
		if(fieldName != null) {
			char[] charArray = fieldName.toCharArray();
			if(charArray != null) {
				for(int i = 0; i < charArray.length; i++) {
					if(i > 0 && Character.isUpperCase(charArray[i])) {
						result.append("_");
					}
					result.append(Character.toLowerCase(charArray[i]));
				}
			}
		}
		return result.toString();
	}
/**
 * 데이타베이스로부터 데이타를 가져온다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * obj 객체로부터 바인딩할 파라미터를 조합하는데 bind 메소드를 참조한다.  null 은 허용되지 않는다.
 * obj 객체는 generateClassSource 만들어진 것이 좋지만, 그렇지 않을 경우 Reference type 중 Class Type 으로만 구성되어야 하는데, Primitive type 은 null 값을 가질 수 없기 때문이다.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param obj 바인딩할 파라미터. 반드시 generateClassSource 로 만들어진 Java 클레스로부터 생성된 객체이어야 한다.
 * @return 데이타베이스로부터 가져온 데이타 (obj 파라미터와 같은 형태로 List 객체에 담아 반환한다)
 */
	public static List fetch(Connection con, Object obj) throws SQLException {
		if(obj instanceof HashMap) {
			throw new SQLException("if second parameter is java.util.HashMap then call fetch(Connection con, Object obj, String table_name) ");
		}
		return fetch(con, obj);
	}
/**
 * 데이타베이스로부터 데이타를 가져온다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * obj 객체로부터 바인딩할 파라미터를 조합하는데 bind 메소드를 참조한다.  null 은 허용되지 않는다.
 * obj 객체는 java.util.HashMap 이거나 generateClassSource 만들어진 것이 좋지만, 그렇지 않을 경우 Reference type 중 Class Type 으로만 구성되어야 하는데, Primitive type 은 null 값을 가질 수 없기 때문이다.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param obj 바인딩할 파라미터. java.util.HashMap 나, generateClassSource 로 만들어진 Java 클레스로부터 생성된 객체.
 * @param table_name 테이블 이름.  ("_" 로 구분된) 형식으로 전달한다.  obj 파라미터가 java.util.HashMap 인 경우 반드시 입력해야 하고, 그렇지 않은 경우 class 이름을 사용한다.
 * @return 데이타베이스로부터 가져온 데이타 (obj 파라미터와 같은 형태로 List 객체에 담아 반환한다)
 */
	public static List fetch(Connection con, Object obj, String table_name) throws SQLException {
		if(obj instanceof HashMap) {
			if(table_name == null) {
				throw new SQLException("table_name paramter is null.  if second parameter is java.util.HashMap then table_name parameter is required");
			}
			String sql = "select * from " + table_name + "";
			Object[] param = new Object[((HashMap)obj).size()];
			Iterator<String> it = ((HashMap)obj).keySet().iterator();
			int index = 0;
			while(it.hasNext()) {
				String key = (String)it.next();
				if(index > 0) {
					sql += " and ";
				} else {
					sql += " where ";
				}
				sql += key + " = ?";
				param[index] = ((HashMap)obj).get(key);
				index++;
			}
			return fetch(con, obj.getClass(), sql, param);
		} else {
			int paramSize = 0;
			String sql = "select * from ";
			if(table_name == null) {
				sql += table_name + "";
			} else {
				sql += getColumnName(obj.getClass().getSimpleName()) + "";
			}
			Object[] param = null;
			Field[] filed = obj.getClass().getDeclaredFields();
			if(filed != null) {
				for(int i = 0; i < filed.length; i++) {
					String fieldName = filed[i].getName();
					try {
						Object value = obj.getClass().getMethod("get" + (fieldName.substring(0, 1)).toUpperCase() + fieldName.substring(1), (Class[])null).invoke(obj, (Object[])null);
						if(value != null) {
							paramSize++;
						}
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						logger.warning(LOG.toString(e));
					}
				}
				if(paramSize > 0) {
					param = new Object[paramSize];
					int index = 0;
					for(int i = 0; i < filed.length; i++) {
						String fieldName = filed[i].getName();
						try {
							Object value = obj.getClass().getMethod("get" + (fieldName.substring(0, 1)).toUpperCase() + fieldName.substring(1), (Class[])null).invoke(obj, (Object[])null);
							if(value != null) {
								if(index > 0) {
									sql += " and ";
								} else {
									sql += " where ";
								}
								sql += getColumnName(fieldName) + " = ?";
								param[index] = value;
								index++;
							}
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							logger.warning(LOG.toString(e));
						}
					}
				}
			}
			return fetch(con, obj.getClass(), sql, param);
		}
	}
/**
 * sql 실행결과로부터 HashMap 에 데이타를 담는다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * @param rs sql 실행결과
 * @param rsmd ResultSetMetaData 객체
 * @param data 데이타를 담을 HashMap 객체
 * @param columnIndex 컬럼 순서
 */
	private static void put(ResultSet rs, ResultSetMetaData rsmd, HashMap data, int columnIndex) throws SQLException {
		if(rsmd.getColumnType(columnIndex) == java.sql.Types.VARCHAR) {
			data.put(rsmd.getColumnName(columnIndex), rs.getString(columnIndex));
		} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.INTEGER) {
			data.put(rsmd.getColumnName(columnIndex), rs.getInt(columnIndex));
		} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.FLOAT) {
			data.put(rsmd.getColumnName(columnIndex), rs.getFloat(columnIndex));
		} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.DOUBLE) {
			data.put(rsmd.getColumnName(columnIndex), rs.getDouble(columnIndex));
		} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.BIGINT) {
			data.put(rsmd.getColumnName(columnIndex), rs.getLong(columnIndex));
		} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.DATE) {
			data.put(rsmd.getColumnName(columnIndex), rs.getDate(columnIndex));
		} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.TIMESTAMP) {
			data.put(rsmd.getColumnName(columnIndex), rs.getTimestamp(columnIndex));
		} else {
			logger.warning("not support (rsmd.getColumnType = " + Integer.toString(rsmd.getColumnType(columnIndex)) + ")");
			data.put(rsmd.getColumnName(columnIndex), rs.getObject(columnIndex));
		}
	}
/**
 * sql 실행결과로부터 generateClassSource 로 만들어진 Java 클레스로부터 생성된 객체에 데이타를 담는다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * @param rs sql 실행결과
 * @param rsmd ResultSetMetaData 객체
 * @param data generateClassSource 로 만들어진 Java 클레스로부터 생성된 객체
 * @param columnIndex 컬럼 순서
 * @param c obj 파라미터의 class (내부적으로 obj.getClass() 를 사용해도 되겠지만, fetch 함수를 호출할 때 같은 파라미터가 전달되므로)
 */
	private static void put(ResultSet rs, ResultSetMetaData rsmd, Object data, int columnIndex, Class c) throws SQLException {
		try {
			if(rsmd.getColumnType(columnIndex) == java.sql.Types.VARCHAR) {
				Method m = c.getMethod("set" + DB.getClassOrMethodName(rsmd.getColumnName(columnIndex)), new Class[]{String.class});
				if(m != null) {
					m.invoke(data, new Object[]{rs.getString(columnIndex)});
				}
			} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.INTEGER) {
				Method m = c.getMethod("set" + DB.getClassOrMethodName(rsmd.getColumnName(columnIndex)), new Class[]{Integer.class});
				if(m != null) {
					m.invoke(data, new Object[]{Integer.valueOf(rs.getInt(columnIndex))});
				}
			} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.FLOAT) {
				Method m = c.getMethod("set" + DB.getClassOrMethodName(rsmd.getColumnName(columnIndex)), new Class[]{Float.class});
				if(m != null) {
					m.invoke(data, new Object[]{Float.valueOf(rs.getFloat(columnIndex))});
				}
			} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.DOUBLE) {
				Method m = c.getMethod("set" + DB.getClassOrMethodName(rsmd.getColumnName(columnIndex)), new Class[]{Double.class});
				if(m != null) {
					m.invoke(data, new Object[]{Double.valueOf(rs.getDouble(columnIndex))});
				}
			} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.BIGINT) {
				Method m = c.getMethod("set" + DB.getClassOrMethodName(rsmd.getColumnName(columnIndex)), new Class[]{Long.class});
				if(m != null) {
					m.invoke(data, new Object[]{Long.valueOf(rs.getLong(columnIndex))});
				}
			} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.DATE) {
				Method m = c.getMethod("set" + DB.getClassOrMethodName(rsmd.getColumnName(columnIndex)), new Class[]{java.util.Date.class});
				if(m != null) {
					m.invoke(data, new Object[]{rs.getDate(columnIndex)});
				}
			} else if(rsmd.getColumnType(columnIndex) == java.sql.Types.TIMESTAMP) {
				Method m = c.getMethod("set" + DB.getClassOrMethodName(rsmd.getColumnName(columnIndex)), new Class[]{java.sql.Timestamp.class});
				if(m != null) {
					m.invoke(data, new Object[]{rs.getTimestamp(columnIndex)});
				}
			} else {
				Method m = c.getMethod("set" + DB.getClassOrMethodName(rsmd.getColumnName(columnIndex)), new Class[]{Object.class});
				if(m != null) {
					m.invoke(data, new Object[]{rs.getObject(columnIndex)});
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			logger.warning(LOG.toString(e));
		}
	}
/**
 * sql 구문을 실행하여 데이타를 가져온다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * params 파라미터를 바인딩 하는 것과 관련해서 bind 메소드를 참조한다.  null 은 허용되지 않는다.
 * c 파라미터는 java.util.HashMap 이거나 generateClassSource 만들어진 것이 좋지만, 그렇지 않을 경우 Reference type 중 Class Type 으로만 구성되어야 하는데, Primitive type 은 null 값을 가질 수 없기 때문이다.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param c 데이타를 반환한 class
 * @param sql 실행할 sql 구문
 * @param params 바인딩할 파라미터
 * @return 데이타베이스로부터 가져온 데이타 (c 파라미터로 전달된 객체를 List 객체에 담아 반환한다)
 */
	public static List fetch(Connection con, Class c, String sql, Object[] params) throws SQLException {
		List result = new ArrayList();
		execute(con, c, sql, params, result);
		return result;
	}
/**
 * sql 구문을 실행한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * params 파라미터를 바인딩 하는 것과 관련해서 bind 메소드를 참조한다.  null 은 허용되지 않는다.
 * c 파라미터는 java.util.HashMap 이거나 generateClassSource 만들어진 것이 좋지만, 그렇지 않을 경우 Reference type 중 Class Type 으로만 구성되어야 하는데, Primitive type 은 null 값을 가질 수 없기 때문이다.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param c 데이타를 반환한 class
 * @param sql 실행할 sql 구문
 * @param params 바인딩할 파라미터
 * @return 갱신된 카운트(PreparedStatement.getUpdateCount)를 반환한다.
 */
	public static int execute(Connection con, Class c, String sql, Object[] params) throws SQLException {
		return execute(con, c, sql, params, null);
	}
/**
 * sql 구문을 실행한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * params 파라미터를 바인딩 하는 것과 관련해서 bind 메소드를 참조한다.  null 은 허용되지 않는다.
 * c 파라미터는 java.util.HashMap 이거나 generateClassSource 만들어진 것이 좋지만, 그렇지 않을 경우 Reference type 중 Class Type 으로만 구성되어야 하는데, Primitive type 은 null 값을 가질 수 없기 때문이다.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param c 데이타를 반환한 class
 * @param sql 실행할 sql 구문
 * @param params 바인딩할 파라미터
 * @param result ResultSet 객체를 반환하는 select 구문이 입력된 경우, 이 객체에 실행결과를 담는다.
 * @return 가져온 row 의 갯수나 갱신된 카운트(PreparedStatement.getUpdateCount)를 반환한다.
 */
	public static int execute(Connection con, Class c, String sql, Object[] params, List result) throws SQLException {
		int fetchedOrUpdateCount = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sql);
			if(params != null) {
				for(int i = 0; i < params.length; i++) {
					DB.bind(pstmt, i + 1, params[i]);
				}
			}
			if(pstmt.execute()) {
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				while(rs.next()) {
					if((c.getName()).equals("java.util.HashMap")) {
						HashMap data = (java.util.HashMap)c.getConstructor().newInstance();
						for(int x = 1; x <= rsmd.getColumnCount(); x++) {
							put(rs, rsmd, data, x);
						}
						result.add(data);
					} else {
						Object data = c.getConstructor().newInstance();
						for(int x = 1; x <= rsmd.getColumnCount(); x++) {
							put(rs, rsmd, data, x, c);
						}
						result.add(data);
					}
				}
				DB.close(rs);
				fetchedOrUpdateCount = result.size();
			} else {
				fetchedOrUpdateCount = pstmt.getUpdateCount();
			}
			DB.close(pstmt);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
			logger.warning(LOG.toString(e));
		} catch (SQLException e) {
			logger.warning(LOG.toString(e));
			throw e;
		} finally {
			DB.close(rs);
			DB.close(pstmt);
		}
		return fetchedOrUpdateCount;
	}
/**
 * 데이타를 데이타베이스에 추가한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * obj 객체로부터 바인딩할 파라미터를 조합하는데 bind 메소드를 참조한다.  null 은 허용되지 않는다.
 * obj 객체는 java.util.HashMap 이거나 generateClassSource 만들어진 것이 좋지만, 그렇지 않을 경우 Reference type 중 Class Type 으로만 구성되어야 하는데, Primitive type 은 null 값을 가질 수 없기 때문이다.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param obj 바인딩할 파라미터. java.util.HashMap 나, generateClassSource 로 만들어진 Java 클레스로부터 생성된 객체.
 * @param table_name 테이블 이름.  ("_" 로 구분된) 형식으로 전달한다.  obj 파라미터가 java.util.HashMap 인 경우 반드시 입력해야 하고, 그렇지 않은 경우 class 이름을 사용한다. 
 * @return 갱신된 카운트(PreparedStatement.getUpdateCount)를 반환한다.
 */
	public int insert(Connection con, Object obj, String table_name) throws SQLException {
		if(obj instanceof HashMap) {
			if(table_name == null) {
				throw new SQLException("table_name paramter is null.  if second parameter is java.util.HashMap then table_name parameter is required");
			}
			String sql = "insert into " + table_name + " (";
			String valueCause = "";
			Object[] param = new Object[((HashMap)obj).size()];
			Iterator<String> it = ((HashMap)obj).keySet().iterator();
			int index = 0;
			while(it.hasNext()) {
				String key = (String)it.next();
				if(((HashMap)obj).get(key) != null) {
					throw new SQLException("if obj parameter type java.util.HashMap then null not allowed");
				}
				if(index > 0) {
					sql += ", ";
					valueCause += ", ";
				}
				sql += key;
				valueCause += "?";
				param[index] = ((HashMap)obj).get(key);
				index++;
			}
			if(index == 0) {
				throw new SQLException("data is empty");
			}
			sql += ") values (" + valueCause + ")";
			return execute(con, obj.getClass(), sql, param);
		} else {
			int paramSize = 0;
			Field[] filed = obj.getClass().getDeclaredFields();
			if(filed == null) {
				throw new SQLException("data class field size is 0");
			}
			for(int i = 0; i < filed.length; i++) {
				String fieldName = filed[i].getName();
				try {
					Object value = obj.getClass().getMethod("get" + (fieldName.substring(0, 1)).toUpperCase() + fieldName.substring(1), (Class[])null).invoke(obj, (Object[])null);
					if(value != null) {
						paramSize++;
					}
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					logger.warning(LOG.toString(e));
				}
			}
			if(paramSize > 0) {
				String sql = "insert into ";
				if(table_name == null) {
					sql += table_name + "";
				} else {
					sql += getColumnName(obj.getClass().getSimpleName()) + "";
				}
				sql += " (";
				Object[] param = null;
				param = new Object[paramSize];
				int index = 0;
				String valueCause = "";
				for(int i = 0; i < filed.length; i++) {
					String fieldName = filed[i].getName();
					try {
						Object value = obj.getClass().getMethod("get" + (fieldName.substring(0, 1)).toUpperCase() + fieldName.substring(1), (Class[])null).invoke(obj, (Object[])null);
						if(value != null) {
							if(index > 0) {
								sql += ", ";
								valueCause += ", ";
							}
							sql += getColumnName(fieldName);
							valueCause += "?";
							param[index] = value;
							index++;
						}
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						logger.warning(LOG.toString(e));
					}
				}
				sql += ") values (" + valueCause + ")";
				return execute(con, obj.getClass(), sql, param);
			} else {
				throw new SQLException("data is empty");
			}
		}
	}
/**
 * 데이타를 데이타베이스에 추가한다.
 * 지원하는 데이타 유형의 범위와 방식에 대해서는 getJavaDataType 메소드의 설명을 참조하라.
 * obj 객체로부터 바인딩할 파라미터를 조합하는데 bind 메소드를 참조한다.  null 은 허용되지 않는다.
 * obj 객체는 generateClassSource 만들어진 것이 좋지만, 그렇지 않을 경우 Reference type 중 Class Type 으로만 구성되어야 하는데, Primitive type 은 null 값을 가질 수 없기 때문이다.
 * @param con 데이타베이스 연결(Connection) 객체
 * @param obj 바인딩할 파라미터. generateClassSource 로 만들어진 Java 클레스로부터 생성된 객체.
 * @return 갱신된 카운트(PreparedStatement.getUpdateCount)를 반환한다.
 */
	public int insert(Connection con, Object obj) throws SQLException {
		if(obj instanceof HashMap) {
			throw new SQLException("if second parameter is java.util.HashMap then call fetch(Connection con, Object obj, String table_name) ");
		}
		return insert(con, obj);
	}
}