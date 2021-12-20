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

import java.util.HashMap;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import kr.graha.helper.LOG;

/**
 * Graha(그라하)에서 파라미터를 관리하기 위해 사용하는 라이브러리

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public class Record extends HashMap {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	public Record() {
		super();
		LOG.setLogLevel(logger);
	}
	public void put(String key, String value) {
		super.put(key, value);
	}
	public void puts(String key, String value) {
		this.puts(key, (Object)value);
	}
	public void puts(String key, Object value) {
		if(super.containsKey(key)) {
			if(logger.isLoggable(Level.FINE)) { logger.fine("key exists!!! : " + key); }
			List l = null;
			if(super.get(key) instanceof List) {
				l = (List)super.get(key);
			} else {
				l = new ArrayList();
				l.add(super.get(key));
			}
			l.add(value);
			super.put(key, l);
		} else {
			super.put(key, value);
		}
	}
	public void putDate(
		String key, 
		String year, 
		String month, 
		String day
	) {
		this.putDate(key, year, month, day, null, null, null);
	}
	public void putDate(
		String key, 
		String year, 
		String month, 
		String day, 
		String hour, 
		String minute, 
		String second
	) {
		int y = this.intValue(year, -1);
		int m = this.intValue(month, -1);
		int d = this.intValue(day, -1);
		if(y > 0 && m > 0 && m <= 12 && d > 0 && d <= 31) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, y);
			cal.set(Calendar.MONTH, m);
			cal.set(Calendar.DAY_OF_MONTH, d);
			if(hour != null && !hour.equals("")) {
				int x = this.intValue(hour, -1);
				if(x >= 0 && x <= 24) {
					cal.set(Calendar.HOUR_OF_DAY, x);
				}
			}
			if(minute != null && !minute.equals("")) {
				int x = this.intValue(minute, -1);
				if(x >= 0 && x <= 60) {
					cal.set(Calendar.MINUTE, x);
				}
			}
			if(second != null && !second.equals("")) {
				int x = this.intValue(second, -1);
				if(x >= 0 && x <= 60) {
					cal.set(Calendar.SECOND, x);
				}
			}
			this.puts(key, new Date(cal.getTime().getTime()));
		}
		this.puts(key, null);
	}
	
	public int intValue(String value) {
		return this.intValue(value, 0);
	}
	public Integer intObject(String value) {
		if(value == null) {
			return null;
		}
		value = value.replace(" ", "");
		value = value.replace(",", "");
		if(value != null && value.equals("")) {
			return null;
		}
		try {
			/*
			return new Integer(Integer.parseInt(value));
			*/
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	public int intValue(String value, int defaultValue) {
		if(value == null) {
			return defaultValue;
		}
		value = value.replace(" ", "");
		value = value.replace(",", "");
		if(value != null && value.equals("")) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	public int getInt(String key) {
		return this.getInt(key, 0);
	}
	public int getInt(String key, int defaultValue) {
		Integer value = this.getIntObject(key);
		if(value == null) {
			return defaultValue;
		}
		return value.intValue();
	}
	public Object getValueOfList(List l) {
		return l.get(l.size() - 1);
	}
	public Integer getIntObject(String key) {
		if(super.containsKey(key)) {
			if(super.get(key) instanceof Integer) {
				return (Integer)super.get(key);
			}
			if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof Integer) {
					return (Integer)this.getValueOfList(l);
				}
			}
			String value = null;
			if(super.get(key) instanceof String) {
				value = (String)super.get(key);
			} else if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
			if(value == null) {return null;}
			value = value.replace(" ", "");
			value = value.replace(",", "");
			if(value != null && value.equals("")) {return null;}
			try {
				/*
				return new Integer(Integer.parseInt(value));
				*/
				return Integer.valueOf(value);
			} catch (NumberFormatException e) {
				if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
			}
		}
		return null;
	}
	public float getFloat(String key) {
		return this.getFloat(key, 0);
	}
	public float getFloat(String key, float defaultValue) {
		Float value = this.getFloatObject(key);
		if(value == null) {
			return defaultValue;
		}
		return value.floatValue();
	}
	public Float getFloatObject(String key) {
		if(super.containsKey(key)) {
			if(super.get(key) instanceof Float) {
				return (Float)super.get(key);
			}
			if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof Float) {
					return (Float)this.getValueOfList(l);
				}
			}
			String value = null;
			if(super.get(key) instanceof String) {
				value = (String)super.get(key);
			} else if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
			if(value == null) {return null;}
			value = value.replace(" ", "");
			value = value.replace(",", "");
			if(value != null && value.equals("")) {return null;}
			try {
				/*
				return new Float(Float.parseFloat(value));
				*/
				return Float.valueOf(value);
			} catch (NumberFormatException e) {
				if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
			}
		}
		return null;
	}
	public double getDouble(String key) {
		return this.getDouble(key, 0);
	}
	public double getDouble(String key, double defaultValue) {
		Double value = this.getDoubleObject(key);
		if(value == null) {
			return defaultValue;
		}
		return value.doubleValue();
	}
	public Double getDoubleObject(String key) {
		if(super.containsKey(key)) {
			if(super.get(key) instanceof Double) {
				return (Double)super.get(key);
			}
			if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof Double) {
					return (Double)this.getValueOfList(l);
				}
			}
			String value = null;
			if(super.get(key) instanceof String) {
				value = (String)super.get(key);
			} else if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
			if(value == null) {return null;}
			value = value.replace(" ", "");
			value = value.replace(",", "");
			if(value != null && value.equals("")) {return null;}
			try {
				/*
				return new Double(Double.parseDouble(value));
				*/
				return Double.valueOf(value);
			} catch (NumberFormatException e) {
				if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
			}
		}
		return null;
	}
	public long getLong(String key) {
		return this.getLong(key, 0);
	}
	public long getLong(String key, long defaultValue) {
		Long value = this.getLongObject(key);
		if(value == null) {
			return defaultValue;
		}
		return value.longValue();
	}
	public Long getLongObject(String key) {
		if(super.containsKey(key)) {
			if(super.get(key) instanceof Long) {
				return (Long)super.get(key);
			}
			if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof Long) {
					return (Long)this.getValueOfList(l);
				}
			}
			String value = null;
			if(super.get(key) instanceof String) {
				value = (String)super.get(key);
			} else if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty() && this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
			if(value == null) {return null;}
			value = value.replace(" ", "");
			value = value.replace(",", "");
			if(value != null && value.equals("")) {return null;}
			try {
				/*
				return new Long(Long.parseLong(value));
				*/
				return Long.valueOf(value);
			} catch (NumberFormatException e) {
				if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
			}
		}
		return null;
	}
	public String getString(String key) {
		if(!super.containsKey(key)) {
			return null;
		}
		if(super.get(key) instanceof String) {
			return (String)super.get(key);
		} else if(super.get(key) instanceof List) {
			List l = (List)super.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof String) {
					return (String)this.getValueOfList(l);
				} else {
					return ("" + this.getValueOfList(l));
				}
			} else {
				return null;
			}
		} else {
			return ("" + super.get(key));
		}
	}
	public boolean getBoolean(String key) {
		boolean result = false;
		if(!super.containsKey(key)) {
			result = false;
		} else if(
			this.getString(key).equalsIgnoreCase("y")
			|| this.getString(key).equalsIgnoreCase("t")
			|| this.getString(key).equalsIgnoreCase("true")
		) {
			result = true;
		}
		return result;
	}
	public Date getDate(String key, String pattern) {
		if(!super.containsKey(key)) {
			return null;
		}
		String value = null;
		if(super.get(key) instanceof String) {
			value = (String)super.get(key);
		} else if(super.get(key) instanceof List) {
			List l = (List)super.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		value = value.trim();
		DateFormat df = new SimpleDateFormat(pattern);
		
		Date result = null;
		try {
			result = new Date(df.parse(value).getTime());
		} catch (ParseException e) {
			if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
		}
		return result;
	}
	public Date getDate(String key) {
		if(!super.containsKey(key)) {
			return null;
		}
		Object value = super.get(key);
		if(value instanceof Date) {
			return (Date)value;
		} else if(value instanceof List) {
			List l = (List)super.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof Date) {
					return (Date)this.getValueOfList(l);
				}
			}
		}
		return null;
	}
	public Timestamp getTimestamp(String key) {
		if(!super.containsKey(key)) {
			return null;
		}
		Object value = super.get(key);
		if(value instanceof Timestamp) {
			return (Timestamp)value;
		} else if(value instanceof List) {
			List l = (List)super.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof Timestamp) {
					return (Timestamp)this.getValueOfList(l);
				}
			}
		}
		return null;
	}
	public Timestamp getTimestamp(String key, String pattern) {
		if(!super.containsKey(key)) {
			return null;
		}
		String value = null;
		if(super.get(key) instanceof String) {
			value = (String)super.get(key);
		} else if(super.get(key) instanceof List) {
			List l = (List)super.get(key);
			if(!l.isEmpty()) {
				if(this.getValueOfList(l) instanceof String) {
					value = (String)this.getValueOfList(l);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		
		value = value.trim();
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		
		Timestamp result = null;
		try {
			result = new Timestamp(df.parse(value).getTime());
		} catch (ParseException e) {
			if(logger.isLoggable(Level.WARNING)) { logger.warning(LOG.toString(e)); }
		}
		return result;
	}
	public String formatDate(Timestamp d, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		return df.format(d);
	}
	public String formatDate(Date d, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		return df.format(d);
	}
	public String formatNumber(int d, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
	public String formatNumber(long d, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
	public String formatNumber(double d, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
	public String formatNumber(float d, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
/**
 * 키에 해당하는 값이 있는지 검사한다.
 * 이 메소드는 containsKey와 다르다.
 * 키에 해당하는 값이 있다고 하더라도, 값이 null 이거나 공백("") 인 경우 fasle 를 반환한다.
 * @param key 키
 * @return 키에 해당하는 값이 있는지 여부
 */
	public boolean hasKey(String key) {
		boolean result = false;
		if(super.containsKey(key)) {
			if(super.get(key) == null) {
				result = false;
			} else if(super.get(key) instanceof String && ((String)super.get(key)).trim().equals("")) {
				result = false;
			} else if(this.get(key) instanceof List) {
				result = true;
				List vv = (List)this.get(key);
				for (Object v : vv) {
					if(v instanceof String) {
						if(!((String)v).trim().equals("")) {
							result = true;
							break;
						}
					} else {
						result = true;
					}
				}
			} else {
				result = true;
			}
		}
		return result;
	}
	public boolean isNotEmpty(Element col, String s1) {
		if(col != null && col.hasAttribute(s1)) {
			if(col.getAttribute(s1) != null && !(col.getAttribute(s1)).equals("")) {
				return true;
			}
		}
		return false;
	}
	public boolean in(Element col, String s1, String[] s2) {
		if(col != null && col.hasAttribute(s1)) {
			if(s2 != null) {
				for (String s : s2) {
					if(this.compare(col.getAttribute(s1), s)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean equals(Element col, String s1, String s2) {
		if(col != null && col.hasAttribute(s1)) {
			if(this.compare(s2, col.getAttribute(s1))) {
				return true;
			}
		}
		return false;
	}
	public boolean in(String s1, String[] s2) {
		if(s2 != null) {
			for (String s : s2) {
				if(this.compare(s1, s)) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean in(String key, String value) {
		if(super.containsKey(key) && value != null && !value.trim().equals("")) {
			if(this.get(key) == null) {
				return false;
			}
			if(this.get(key) instanceof String[]) {
				String[] vv = (String[])this.get(key);
				for (String v : vv) {
					
					if(v != null && v.equals(value)) {
						return true;
					}
				}
			} else if(this.get(key) instanceof List) {
				List<String> vv = (List)this.get(key);
				for (String v : vv) {
					if(v != null && v.equals(value)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean equals(String key, String value) {
		if(this.containsKey(key) && value != null && !value.trim().equals("")) {
			if(this.get(key) == null) {
				return false;
			}
			if(this.get(key) instanceof String) {
				if(value.equals(this.getString(key))) {
					return true;
				}
			} else if(super.get(key) instanceof List) {
				List l = (List)super.get(key);
				if(!l.isEmpty()) {
					if(this.getValueOfList(l) instanceof String) {
						if((String)this.getValueOfList(l) != null && value.equals((String)this.getValueOfList(l))) {
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	public boolean isempty(String key) {
		if(this.containsKey(key)) {
			if(this.hasKey(key)) {
				return false;
			}
		}
		return true;
	}
	public boolean isnotempty(String key) {
		return !this.isempty(key);
	}
	public boolean check(String key, String value, int check) {
		if(this.containsKey(key) && value != null && !value.trim().equals("")) {
			if(this.get(key) == null) {
				return false;
			}
			Integer i1 = this.getIntObject(key);
			Integer i2 = this.intObject(value);
			if(i1 == null || i2 == null) {
				return false;
			}
			if(check == AuthParser.GreaterThan && i1.intValue() > i2.intValue()) {
				return true;
			} else if(check == AuthParser.GreaterThanOrEqualTo && i1.intValue() >= i2.intValue()) {
				return true;
			} else if(check == AuthParser.LessThan && i1.intValue() < i2.intValue()) {
				return true;
			} else if(check == AuthParser.LessThanOrEqualTo && i1.intValue() <= i2.intValue()) {
				return true;
			}
		}
		return false;
	}
	public boolean compare(String s1, String s2) {
		boolean result = false;
		if(s1 == null) {
			if(s2 == null) {
				result = true;
			} else {
				result = false;
			}
		} else if(s2 == null) {
			result = false;
		} else {
			result = s1.equals(s2);
		}
		return result;
	}
	public boolean notNull(String key) {
		return !this.isempty(key);
	}
	public boolean maxLength(String key, String length) {
		boolean result = false;
		int len = Integer.parseInt(length);
		if(this.isempty(key)) {
			result = true;
		} else if(!(super.get(key) instanceof String)) {
			result = false;
		} else if(this.getString(key) == null) {
			result = true;
		} else if(this.getString(key).length() <= len) {
			result = true;
		}
		return result;
	}
	public boolean minLength(String key, String length) {
		boolean result = false;
		int len = Integer.parseInt(length);
		if(this.isempty(key)) {
			result = true;
		} else if(!(super.get(key) instanceof String)) {
			result = false;
		} else if(this.getString(key) == null) {
			result = true;
		} else if(this.getString(key).length() == 0) {
			result = true;
		} else if(this.getString(key).length() >= len) {
			result = true;
		}
		return result;
	}
	public boolean numberFormat(String key, String type) {
		if(this.isempty(key)) {
			return true;
		} else {
			if(!(super.get(key) instanceof String)) {
				return true;
			}
			String value = (String)super.get(key);
			
			if(type != null && type.equals("int")) {
				try {
					Integer.parseInt(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			} else if(type != null && type.equals("float")) {
				try {
					Float.parseFloat(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			} else if(type != null && type.equals("double")) {
				try {
					Double.parseDouble(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			} else if(type != null && type.equals("long")) {
				try {
					Long.parseLong(value);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return false;
		}
	}
	public boolean dateFormat(String key, String pattern) {
		if(!super.containsKey(key)) {
			return false;
		}
		if(!(super.get(key) instanceof String)) {
			return false;
		}
		String value = (String)super.get(key);
		
		value = value.trim();
		
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		try {
			df.parse(value);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	public boolean format(String key, String regexp) {
		if(!super.containsKey(key)) {
			return false;
		}
		if(!(super.get(key) instanceof String)) {
			return false;
		}
		String value = (String)super.get(key);
		
		value = value.trim();
		return value.matches(regexp);
	}
	public boolean isArray(String key) {
		boolean result = false;
		if(!super.containsKey(key)) {
			result = false;
		} else if(super.get(key) == null) {
			result = false;
		} else if((super.get(key) instanceof List)) {
			result = true;
		}
		return result;
	}
	public List getArray(String key) {
		if(this.isArray(key)) {
			return (List)super.get(key);
		} else {
			return null;
		}
	}
	public void dump(Logger logger, Level level) {
		if(!super.isEmpty()) {
			Iterator<String> it = super.keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				if(this.get(key) instanceof String[]) {
					for(int i = 0; i < ((String[])this.get(key)).length; i++) {
						if(logger.isLoggable(level)) { logger.log(level, key + "." + i + "(SA)=" + ((String[])this.get(key))[i]); }
					}
				} else if(this.isArray(key)) {
					for (Object v : (List)this.get(key)) {
						if(logger.isLoggable(level)) { logger.log(level, key + "(A)=" + v); }
					}
				} else {
					if(logger.isLoggable(level)) { logger.log(level, key + "(NA)=" + this.get(key)); }
				}
			}
		}
	}
}
