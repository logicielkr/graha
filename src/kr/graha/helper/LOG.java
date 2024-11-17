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
	private static Logger logger = Logger.getLogger("kr.graha.helper.LOG");
	
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
			}
		} catch(Exception e1) {
			e1.printStackTrace();
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
		return null;
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
		StringBuilder sb = new StringBuilder();
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
			} else if(logLevel != null && logLevel.equals("FINEST")) {
				level = Level.FINEST;
			} else if(logLevel != null && logLevel.equals("FINER")) {
				level = Level.FINER;
			} else if(logLevel != null && logLevel.equals("FINE")) {
				level = Level.FINE;
			} else if(logLevel != null && logLevel.equals("CONFIG")) {
				level = Level.CONFIG;
			} else if(logLevel != null && logLevel.equals("INFO")) {
				level = Level.INFO;
			} else if(logLevel != null && logLevel.equals("WARNING")) {
				level = Level.WARNING;
			} else if(logLevel != null && logLevel.equals("SEVERE")) {
				level = Level.SEVERE;
			} else if(logLevel != null && logLevel.equals("OFF")) {
				level = Level.OFF;
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
/**
 * 이 메소드를 호출한 소스코드의 Line Number 를 반환한다.
 * @return 이 메소드를 호출한 소스코드의 Line Number
 */
	public static int lineNumber() {
		return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}
/**
 * 이 메소드를 호출한 소스코드의 StackTrace 를 반환한다.
 * 이 메소드는 getMsg 를 통해서만 호출되고, getMsg 메소드는 severe 등의 메소드를 통해 호출된다고 전제하고, 4번째 StackTrace를 반환한다.
 * @return 이 메소드를 호출한 소스코드의 StackTrace
 */
	private static StackTraceElement getStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}
/**
 * Exception 이 발생한 소스코드의 StackTrace 를 반환한다.
 * @return 이 메소드를 호출한 소스코드의 StackTrace
 */
	private static StackTraceElement getStackTraceElement(Exception e) {
		return e.getStackTrace()[1];
	}
/**
 * 로그 메시지를 조합한다.
 * Class 이름, Method 이름, 소스코드의 Line Number, 메시지로 구성된다.
 * @return 로그 메시지
 */
	private static String getMsg(String msg) {
		return LOG.getStackTraceElement().getClassName() + "." + LOG.getStackTraceElement().getMethodName() + "(" + LOG.getStackTraceElement().getLineNumber() + ") : " + msg;
	}
/**
 * 로그 메시지를 조합한다.
 * Class 이름, Method 이름, 소스코드의 Line Number, 메시지로 구성된다.
 * @return 로그 메시지
 */
	private static String getMsg(Exception e) {
		return LOG.getStackTraceElement(e).getClassName() + "." + LOG.getStackTraceElement(e).getMethodName() + "(" + LOG.getStackTraceElement(e).getLineNumber() + ") : " + LOG.toString(e);
	}
	public static void severe(Logger logger, Exception e) {
		if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.getMsg(e)); }
	}
	public static void severe(Exception e) {
		if(logger.isLoggable(Level.SEVERE)) { logger.severe(LOG.getMsg(e)); }
	}
	public static void severe(Logger logger, String... msgs) {
		if(logger.isLoggable(Level.SEVERE)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.severe(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void severe(String... msgs) {
		if(logger.isLoggable(Level.SEVERE)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.severe(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void info(Logger logger, Exception e) {
		if(logger.isLoggable(Level.INFO)) { logger.info(LOG.getMsg(e)); }
	}
	public static void info(Exception e) {
		if(logger.isLoggable(Level.INFO)) { logger.info(LOG.getMsg(e)); }
	}
	public static void info(Logger logger, String... msgs) {
		if(logger.isLoggable(Level.INFO)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.info(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void info(String... msgs) {
		if(logger.isLoggable(Level.INFO)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.info(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void config(Logger logger, Exception e) {
		if(logger.isLoggable(Level.CONFIG)) { logger.config(LOG.getMsg(e)); }
	}
	public static void config(Exception e) {
		if(logger.isLoggable(Level.CONFIG)) { logger.config(LOG.getMsg(e)); }
	}
	public static void config(Logger logger, String... msgs) {
		if(logger.isLoggable(Level.CONFIG)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.config(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void config(String... msgs) {
		if(logger.isLoggable(Level.CONFIG)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.config(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void finer(Logger logger, Exception e) {
		if(logger.isLoggable(Level.FINER)) { logger.finer(LOG.getMsg(e)); }
	}
	public static void finer(Exception e) {
		if(logger.isLoggable(Level.FINER)) { logger.finer(LOG.getMsg(e)); }
	}
	public static void finer(Logger logger, String... msgs) {
		if(logger.isLoggable(Level.FINER)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.finer(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void finer(String... msgs) {
		if(logger.isLoggable(Level.FINER)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.finer(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void finest(String... msgs) {
		if(logger.isLoggable(Level.FINEST)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.finest(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void warning(Logger logger, Exception e) {
		if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.getMsg(e)); }
	}
	
	public static void warning(Exception e) {
		if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.getMsg(e)); }
	}
	public static void warning(Logger logger, String... msgs) {
		if(logger.isLoggable(Level.WARNING)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.warning(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void warning(String... msgs) {
		if(logger.isLoggable(Level.WARNING)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.warning(LOG.getMsg(msgs[i]));
				}
			}
		}
	}
	public static void log(Level level, String... msgs) {
		if(logger.isLoggable(level)) {
			if(msgs != null) {
				for(int i = 0; i < msgs.length; i++) {
					logger.log(level, LOG.getMsg(msgs[i]));
				}
			}
		}
	}
/**
 * 여기서부터는 테스트가 끝나면 지워버려야 하는 것들
 */
	public static void debug(Logger logger, String... msgs) {
		if(msgs != null) {
			for(int i = 0; i < msgs.length; i++) {
				logger.finest(LOG.getMsg(msgs[i]));
			}
		}
	}
	public static void debug(String... msgs) {
		if(msgs != null) {
			for(int i = 0; i < msgs.length; i++) {
				logger.finest(LOG.getMsg(msgs[i]));
			}
		}
	}
	public static void out(Logger logger, String... msgs) {
		if(msgs != null) {
			for(int i = 0; i < msgs.length; i++) {
				logger.finest(LOG.getMsg(msgs[i]));
			}
		}
	}
	public static void out(String... msgs) {
		if(msgs != null) {
			for(int i = 0; i < msgs.length; i++) {
				logger.finest(LOG.getMsg(msgs[i]));
			}
		}
	}
}
