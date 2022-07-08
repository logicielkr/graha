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

import org.w3c.dom.Element;

/**
 * Graha(그라하) XML 관련 유틸리티
 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */

public final class XML {
	private XML() {
	}
/**
 * CDATA 에 허용되지 않는 문자들을 처리한다.
 * xml 1.0 에서 cdata 내에서도 허용되지 않는 문자는 ""로 변경해서 없애버리고,
 * CDATA를 닫는 태그는 CDATA 태그를 닫았다가 다시 여는 방식으로 처리한다.
 * @param input CDATA에 들어갈 원본 데이타
 * @return CDATA 에 허용되지 않는 문자들을 적절히 처리한 결과
 */
	public static String fix(String input) {
		if(input == null || input.equals("")) {
			return input;
		}
/*
https://stackoverflow.com/questions/5742543/an-invalid-xml-character-unicode-0xc-was-found
*/
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFF]+"); 
		return p.matcher(input).replaceAll("").replace("]]>", "]]]]><![CDATA[>");
	}
/**
 * 속성값이 true 인지 검사한다.
 * 속성값이 (대소문자 구분없이) true, yes, t, y 인 경우 true 를 리턴하고,
 * 그렇지 않은 경우 (element 가 null 이거나 속성이 없는 경우를 포함해서) false 를 리턴한다.
 * @param element
 * @param attr 속성이름
 * @return 속성값이 true 로 평가될 경우 true 를 리턴한다.
 */
	public static boolean trueAttrValue(Element element, String attr) {
		if(
			element != null &&
			element.hasAttribute(attr) &&
			(
				element.getAttribute(attr).equalsIgnoreCase("true") ||
				element.getAttribute(attr).equalsIgnoreCase("yes") ||
				element.getAttribute(attr).equalsIgnoreCase("t") ||
				element.getAttribute(attr).equalsIgnoreCase("y")
			)
		) {
			return true;
		}
		return false;
	}
	
/**
 * 속성값이 false 인지 검사한다.
 * 속성값이 (대소문자 구분없이) false, no, f, n 인 경우 true 를 리턴하고,
 * 그렇지 않은 경우 (element 가 null 이거나 속성이 없는 경우를 포함해서) false 를 리턴한다.
 * @param element
 * @param attr 속성이름
 * @return 속성값이 false 로 평가될 경우 true 를 리턴한다.
 */
	public static boolean falseAttrValue(Element element, String attr) {
		if(
			element != null &&
			element.hasAttribute(attr) &&
			(
				element.getAttribute(attr).equalsIgnoreCase("false") ||
				element.getAttribute(attr).equalsIgnoreCase("no") ||
				element.getAttribute(attr).equalsIgnoreCase("f") ||
				element.getAttribute(attr).equalsIgnoreCase("n")
			)
		) {
			return true;
		}
		return false;
	}
/**
 * 속성값이 비어있는 경우 true 를 리턴한다.
 * element 가 null 이거나, 속성이 없는 경우, 속성값이 공백으로만 구성된 경우에도 true 를 리턴한다.
 * @param element
 * @param attr 속성이름
 * @return 속성값이 비어있는 경우 true 를 리턴한다.
 */
	public static boolean emptyAttrValue(Element element, String attr) {
		if(
			element != null &&
			element.hasAttribute(attr)
		) {
			String attrValue = element.getAttribute(attr);
			if(attrValue != null && !attrValue.trim().equals("")) {
				return false;
			}
		}
		return true;
	}
/**
 * 속성값이 유효한 경우,
 * element 가 null 이 아니고,
 * 속성이 있고,
 * 속성값이 null 이 아니고,
 * 속성값이 공백으로만 구성된 것도 아닌 경우
 * true 를 리턴한다.
 * @param element
 * @param attr 속성이름
 * @return 속성값이 유효한 경우 true 를 리턴한다.
 */
	public static boolean validAttrValue(Element element, String attr) {
		return !emptyAttrValue(element, attr);
	}
/**
 * 속성값이 주어진 값과 일치하는지 검사한다.
 * element 가 null 이거나,
 * 속성값이 없거나,
 * 속성값이 null 인 경우,
 * false 를 리턴한다.
 * @param element
 * @param attr 속성이름
 * @return 속성값이 비어있는 경우 true 를 리턴한다.
 */
	public static boolean equalsAttrValue(Element element, String attr, String value) {
		if(element != null && element.hasAttribute(attr) && element.getAttribute(attr) != null) {
			if(STR.compare(value, element.getAttribute(attr))) {
				return true;
			}
		}
		return false;
	}
/**
 * 속성값이 주어진 값과 일치하는지 검사한다.
 * element 가 null 이거나,
 * 속성값이 없거나,
 * 속성값이 null 인 경우,
 * false 를 리턴한다.
 * @param element
 * @param attr 속성이름
 * @return 속성값이 비어있는 경우 true 를 리턴한다.
 */
	public static boolean equalsIgnoreCaseAttrValue(Element element, String attr, String value) {
		if(element != null && element.hasAttribute(attr) && element.getAttribute(attr) != null) {
			if(STR.compareIgnoreCase(value, element.getAttribute(attr))) {
				return true;
			}
		}
		return false;
	}
/**
 * 첫 번째 파라미터로 공급된 속성값이, 2 번째 파라미터로 공급된 문자열 배열에 있는지 검사한다.
 * STR.exists 메소드를 호출하여 비교한다.
 * 속성값의 정의가 없는 경우 무조건 false 를 반환한다.
 * @param element
 * @param attr 속성이름
 * @param value
 * @return value 에서 attr 에 대항하는 속성값이 발견되면 true 를 리턴한다.
 */
	public static boolean existsAttrValue(Element element, String attr, String[] value) {
		if(element != null && element.hasAttribute(attr) && element.getAttribute(attr) != null) {
			if(value != null) {
				return STR.exists(element.getAttribute(attr), value);
			}
		}
		return false;
	}
/**
 * 첫 번째 파라미터로 공급된 속성값이, 2 번째 파라미터로 공급된 문자열 배열에 있는지 검사한다.
 * STR.exists 메소드를 호출하여 비교한다.
 * 속성값의 정의가 없는 경우 무조건 false 를 반환한다.
 * @param element
 * @param attr 속성이름
 * @param value
 * @return value 에서 attr 에 대항하는 속성값이 발견되면 true 를 리턴한다.
 */
	public static boolean existsIgnoreCaseAttrValue(Element element, String attr, String[] value) {
		if(element != null && element.hasAttribute(attr) && element.getAttribute(attr) != null) {
			if(value != null) {
				return STR.existsIgnoreCase(element.getAttribute(attr), value);
			}
		}
		return false;
	}
}

