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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Graha(그라하) 로그 관련 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public final class LOG {
	private LOG() {
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(Exception e) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			if(sw != null) {
				return sw.toString();
			} else {
				return null;
			}
		} catch(Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
			if(sw != null) {
				try {
					sw.close();
				} catch(IOException e1) {
					e1.printStackTrace();
				}
			}
			if(pw != null) {
				pw.close();
			}
		}
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(RuntimeException e) {
		return toString((Exception)e);
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(IllegalAccessException e) {
		return toString((Exception)e);
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(NoSuchMethodException e) {
		return toString((Exception)e);
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(InvocationTargetException e) {
		return toString((Exception)e);
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(SQLException e) {
		return toString((Exception)e);
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(ClassNotFoundException e) {
		return toString((Exception)e);
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(XPathExpressionException e) {
		return toString((Exception)e);
	}
/**
 * 예외(Exception) 가 발생한 StackTrace 를 반환한다.
 * @param e 예외(Exception)
 * @return 예외(Exception) 가 발생한 StackTrace
 */
	public static String toString(ParserConfigurationException e) {
		return toString((Exception)e);
	}
/**
 * HashMap 객체를 보기 좋은 형태로 변경한다.
 * @param data HashMap 객체
 * @return 보기 좋은 형태로 변경한 것
 */
	public static String toString(HashMap data) {
		return toString(data, null);
	}
/**
 * HashMap 객체를 보기 좋은 형태로 변경한다.
 * @param data HashMap 객체
 * @param prefix 
 * @return 보기 좋은 형태로 변경한 것
 */
	private static String toString(HashMap data, String prefix) {
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = data.keySet().iterator();
		if(prefix != null) {
			sb.append(prefix);
		}
		sb.append("{\n");
		int index = 0;
		while(it.hasNext()) {
			String key = (String)it.next();
			if(index > 0) {
				sb.append(",\n");
			}
			if(prefix != null) {
				sb.append(prefix);
			}
			sb.append("\t");
			sb.append(key);
			if(data.get(key) != null) {
				sb.append("=");
				sb.append(data.get(key).toString());
			}
			index++;
		}
		sb.append("\n");
		if(prefix != null) {
			sb.append(prefix);
		}
		sb.append("}\n");
		return sb.toString();
	}

	public static void setLogLevel(Logger logger) {
	}
	public static void setLogLevel2(Logger logger) {
		Level level = null;
		if(System.getProperty("kr.graha.LogLevel") != null) {
			String logLevel = System.getProperty("kr.graha.LogLevel");
			
			if(logLevel != null && logLevel.equals("ALL")) {
				level = Level.ALL;
			} else if(logLevel != null && logLevel.equals("CONFIG")) {
				level = Level.CONFIG;
			} else if(logLevel != null && logLevel.equals("FINE")) {
				level = Level.FINE;
			} else if(logLevel != null && logLevel.equals("FINER")) {
				level = Level.FINER;
			} else if(logLevel != null && logLevel.equals("FINEST")) {
				level = Level.FINEST;
			} else if(logLevel != null && logLevel.equals("INFO")) {
				level = Level.INFO;
			} else if(logLevel != null && logLevel.equals("OFF")) {
				level = Level.OFF;
			} else if(logLevel != null && logLevel.equals("SEVERE")) {
				level = Level.SEVERE;
			} else if(logLevel != null && logLevel.equals("WARNING")) {
				level = Level.WARNING;
			}
		}
		if(level == null) {
			level = Level.WARNING;
		}
		
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(level);
		logger.addHandler(handler);
		logger.setLevel(level);
		Handler[] handlers = logger.getHandlers();
		for(int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(level);
			if(i > 0) {
				logger.removeHandler(handlers[i]);
			}
		}
	}
	
}
