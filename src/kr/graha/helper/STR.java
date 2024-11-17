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

import java.util.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.util.List;
import java.text.ParseException;

/**
 * Graha(그라하) String 관련 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public final class STR {
	private STR() {
	}
/**
 * 2개의 문자열이 같은 지 평가한다.
 * 2개의 문자열이 모두 null 인 경우이거나,
 * 2개의 문자열이 같은 경우
 * true를 리턴한다.
 * @param s1
 * @param s2
 * @return 2개의 문자열이 같은 경우 true 를 리턴한다.
 */
	public static boolean compare(String s1, String s2) {
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
/**
 * 2개의 문자열이 같은 지 평가한다.
 * 2개의 문자열이 모두 null 인 경우이거나,
 * 2개의 문자열이 같은 경우
 * true를 리턴한다.
 * @param s1
 * @param s2
 * @return 2개의 문자열이 같은 경우 true 를 리턴한다.
 */
	public static boolean compareIgnoreCase(String s1, String s2) {
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
			result = s1.equalsIgnoreCase(s2);
		}
		return result;
	}
/**
 * 첫번째 문자열이 두번째 문자열로 시작하는지 검사한다.
 * @param s1
 * @param s2
 * @return 첫번째 문자열이 두번째 문자열로 시작하면 true를 리턴한다.
 */
	public static boolean startsWithIgnoreCase(String s1, String s2) {
		if(s1 == null || s2 == null) {
			return false;
		}
		if(s1.toLowerCase().startsWith(s2.toLowerCase())) {
			return true;
		}
		return false;
	}
/**
 * 첫번째 문자열이 두번째 문자열로 시작하는지 검사한다.
 * @param s1
 * @param s2
 * @return 첫번째 문자열이 두번째 문자열로 시작하면 true를 리턴한다.
 */
	public static boolean startsWith(String s1, String s2) {
		if(s1 == null || s2 == null) {
			return false;
		}
		if(s1.startsWith(s2)) {
			return true;
		}
		return false;
	}
/**
 * 첫 번째 파라미터로 공급된 문자열이, 2 번째 파라미터로 공급된 문자열 배열에 있는지 검사한다.
 * s2 에 null 이 포함되고 있고, s1 이 null 이라면 true 를 리턴함에 주의한다.
 * 문자열 비교는 compare 메소드를 호출한다.
 * @param s1
 * @param s2
 * @return s2 에서 s1 이 발견되면 true 를 리턴한다.
 */
	public static boolean exists(String s1, String[] s2) {
		if(s2 != null) {
			for (String s : s2) {
				if(STR.compare(s1, s)) {
					return true;
				}
			}
		}
		return false;
	}
/**
 * 첫 번째 파라미터로 공급된 문자열이, 2 번째 이후의 파라미터와 1개라도 일치하는지 검사한다.
 * s2 에 null 이 포함되고 있고, s1 이 null 이라면 true 를 리턴함에 주의한다.
 * 문자열 비교는 compare 메소드를 호출한다.
 * @param s1
 * @param s2
 * @return s2 에서 s1 이 발견되면 true 를 리턴한다.
 */
	public static boolean vexists(String s1, String... s2) {
		return STR.exists(s1, s2);
	}
/**
 * 첫 번째 파라미터로 공급된 문자열이, 2 번째 파라미터로 공급된 문자열 배열에 있는지 검사한다.
 * s2 에 null 이 포함되고 있고, s1 이 null 이라면 true 를 리턴함에 주의한다.
 * 문자열 비교는 compare 메소드를 호출한다.
 * @param s1
 * @param s2
 * @return s2 에서 s1 이 발견되면 true 를 리턴한다.
 */
	public static boolean existsIgnoreCase(String s1, String[] s2) {
		if(s2 != null) {
			for (String s : s2) {
				if(STR.compareIgnoreCase(s1, s)) {
					return true;
				}
			}
		}
		return false;
	}
/**
 * 첫 번째 파라미터로 공급된 문자열이, 2 번째 이후의 파라미터와 1개라도 일치하는지 검사한다.
 * s2 에 null 이 포함되고 있고, s1 이 null 이라면 true 를 리턴함에 주의한다.
 * 문자열 비교는 compare 메소드를 호출한다.
 * @param s1
 * @param s2
 * @return s2 에서 s1 이 발견되면 true 를 리턴한다.
 */
	public static boolean vexistsIgnoreCase(String s1, String... s2) {
		return STR.existsIgnoreCase(s1, s2);
	}
/**
 * Timestamp 를 주어진 pattern 에 따라 문자열로 변경한다.
 * SimpleDateFormat 을 사용한다.
 * @param d
 * @param pattern
 * @return Timestamp 를 주어진 pattern 에 따라 변경한 문자열
 */
	public static String formatDate(Timestamp d, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		return df.format(d);
	}
/**
 * Date 를 주어진 pattern 에 따라 문자열로 변경한다.
 * SimpleDateFormat 을 사용한다.
 * @param d
 * @param pattern
 * @return Date 를 주어진 pattern 에 따라 변경한 문자열
 */
	public static String formatDate(Date d, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		return df.format(d);
	}
/**
 * int 를 주어진 pattern 에 따라 문자열로 변경한다.
 * DecimalFormat 을 사용한다.
 * @param d
 * @param pattern
 * @return int 를 주어진 pattern 에 따라 변경한 문자열
 */
	public static String formatNumber(int d, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
/**
 * long 을 주어진 pattern 에 따라 문자열로 변경한다.
 * DecimalFormat 을 사용한다.
 * @param d
 * @param pattern
 * @return long 를 주어진 pattern 에 따라 변경한 문자열
 */
	public static String formatNumber(long d, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
/**
 * double 을 주어진 pattern 에 따라 문자열로 변경한다.
 * DecimalFormat 을 사용한다.
 * @param d
 * @param pattern
 * @return double 를 주어진 pattern 에 따라 변경한 문자열
 */
	public static String formatNumber(double d, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
/**
 * float 을 주어진 pattern 에 따라 문자열로 변경한다.
 * DecimalFormat 을 사용한다.
 * @param d
 * @param pattern
 * @return float 를 주어진 pattern 에 따라 변경한 문자열
 */
	public static String formatNumber(float d, String pattern) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
/**
 * 문자열이 true 로 평가되는지 검사한다.
 * 문자열이 (대소문자 구분없이) true, yes, t, y 인 경우 true 를 리턴하고,
 * 그렇지 않은 경우 false 를 리턴한다.
 * @param value 문자열
 * @return 문자열이 true 로 평가될 경우 true 를 리턴한다.
 */
	public static boolean trueValue(String value) {
		if(STR.valid(value) && STR.vexistsIgnoreCase(value, "true", "yes", "t", "y")) {
			return true;
		}
		return false;
	}
	
/**
 * 문자열이 false 로 평가되는지 검사한다.
 * 문자열이 (대소문자 구분없이) false, no, f, n 인 경우 true 를 리턴하고,
 * 그렇지 않은 경우 false 를 리턴한다.
 * @param attr 문자열
 * @return 문자열이 false 로 평가될 경우 true 를 리턴한다.
 */
	public static boolean falseValue(String value) {
		if(STR.valid(value) && STR.vexistsIgnoreCase(value, "false", "no", "f", "n")) {
			return true;
		}
		return false;
	}
/**
 * 문자열이 유효한 경우,
 * 문자열이 null 이 아니고,
 * 문자열이 공백으로만 구성된 것도 아닌 경우(문자열에 trim 함수를 적용한 후에 검사한다),
 * true 를 리턴한다.
 * @param str 문자열
 * @return 문자열이 유효한 경우 true 를 리턴한다.
 */
	public static boolean valid(String str) {
		if(str != null && !str.trim().equals("")) {
			return true;
		}
		return false;
	}
/**
 * 문자열이 유효하지 않은 경우,
 * 문자열이 null 이거나,
 * 문자열이 공백으로만 구성된 경우(문자열에 trim 함수를 적용한 후에 검사한다),
 * true 를 리턴한다.
 * @param str 문자열
 * @return 문자열이 유효한 경우 true 를 리턴한다.
 */
	public static boolean invalid(String str) {
		return !STR.valid(str);
	}
/**
 * 문자열이 유효한 경우,
 * 문자열이 null 이 아니고,
 * 문자열이 비어있지 않은 경우,
 * true 를 리턴한다.
 * @param str 문자열
 * @return 문자열이 유효한 경우 true 를 리턴한다.
 */
	public static boolean nonempty(String str) {
		if(str != null && !str.equals("")) {
			return true;
		}
		return false;
	}
/**
 * 문자열이 유효하지 않은 경우,
 * 문자열이 null 이거나,
 * 문자열이 비어있는 경우,
 * true 를 리턴한다.
 * @param str 문자열
 * @return 문자열이 유효한 경우 true 를 리턴한다.
 */
	public static boolean empty(String str) {
		return !STR.nonempty(str);
	}
/**
 * List가 유효한 경우,
 * List가 null 이 아니고, size() 가 0 보다 큰 경우,
 * true 를 리턴한다.
 * @param list 문자열
 * @return List 가 유효한 경우 true 를 리턴한다.
 */
	public static boolean valid(List list) {
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
/**
 * 문자열을 java.util.Date 로 변경한다.
 * 내부적으로 SimpleDateFormat 를 사용한다.
 * ParseException 이 발생하면, warning 로그를 남기고, null 을 반환한다.
 * @param value 문자열
 * @param pattern 패턴
 * @return java.util.Date 로 변경된 것
 */
	public static java.util.Date parseDate(String value, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
		try {
			return df.parse(value);
		} catch (ParseException e) {
			LOG.warning(e);
		}
		return null;
	}
/**
 * 문자열을 java.sql.Timestamp 로 변경한다.
 * 내부적으로 SimpleDateFormat 를 사용한다.
 * ParseException 이 발생하면, warning 로그를 남기고, null 을 반환한다.
 * @param value 문자열
 * @param pattern 패턴
 * @return java.sql.Timestamp 로 변경된 것
 */
	public static java.sql.Timestamp parseTimestamp(String value, String pattern) {
		java.util.Date d = parseDate(value, pattern);
		if(d != null) {
			 return new Timestamp(d.getTime());
		}
		return null;
	}
/**
 * 문자열을 java.sql.Date 로 변경한다.
 * 내부적으로 SimpleDateFormat 를 사용한다.
 * ParseException 이 발생하면, warning 로그를 남기고, null 을 반환한다.
 * @param value 문자열
 * @param pattern 패턴
 * @return java.sql.Date 로 변경된 것
 */
	public static java.sql.Date parseSqlDate(String value, String pattern) {
		java.util.Date d = parseDate(value, pattern);
		if(d != null) {
			 return new java.sql.Date(d.getTime());
		}
		return null;
	}
}

