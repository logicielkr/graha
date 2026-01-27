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
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;

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
 * 비어있는 Document 를 생성한다.
 */
	public static Document createDocument() throws ParserConfigurationException {
		Document document = null;
		DocumentBuilderFactory dbf = null;
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setXIncludeAware(true);
		document = dbf.newDocumentBuilder().newDocument();
		return document;
	}

/**
 * CDATA 에서 허용되지 않는 2가지 유형의 문자들을 처리한다.
 
 * 1. CDATA Section 을 닫는 문자열("]]>") 이 포함되어 있는 경우,
 * "]]>" 를 "]]]]><![CDATA[>" 으로 replace 해서,
 * CDATA Section 을 닫았다가 다시 여는 방식으로 처리한다. 
 
 * Unicode: 0xc 같은 문자가 포함되어 있다면,
 * An invalid XML character (Unicode: 0xc) 예외가 발생하므로,
 * XML 1.0 의 Character Range 를 벗어나는 문자는 "" 으로 replace 한다.
 
 * XML 1.0 의 Character Range 는 다음과 같다.
 
 * https://www.w3.org/TR/xml/#charsets
 * Character Range
 * Char	   ::=   	#x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
 
 * XML 1.1 의 Character Range 는 다음과 같은데, Char 와 RestrictedChar 가 분리되어 있을 뿐 XML 1.0 과 동일하다.
 
 * https://www.w3.org/TR/xml11/#charsets
 * Character Range
 * Char	   ::=   	[#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
 * RestrictedChar	   ::=   	[#x1-#x8] | [#xB-#xC] | [#xE-#x1F] | [#x7F-#x84] | [#x86-#x9F]
 
 * @see XML.fixUsingCodePoint
 * @see XML.fixUsingCharacterRange
 * @see XML.fixUsingRestrictedChar
 * @param input CDATA에 들어갈 원본 데이타
 * @return CDATA 에 허용되지 않는 문자들을 처리한 문자열
 */
	public static String fix(String input) {
		return XML.fixUsingCodePoint(input);
//		return XML.fixUsingCharacterRange(input);
//		return XML.fixUsingRestrictedChar(input);
	}

/**
 * CDATA 에서 허용되지 않는 2가지 유형의 문자들을 처리한다.
 
 * XML.fix() 에서 호출된다.
 
 * 문자열을 탐색하면서, 유효한 문자인 경우 StringBuffer 에 담은 후에 StringBuffer.toString() 을 리턴한다.
 
 * 1. CDATA Section 을 닫는 문자열("]]>") 을 만나면, ">" 대신 "]]><![CDATA[>" 을 추가한다.
 * 2. String.codePointAt 으로 구한 codePoint 가 XML 에서 허용하는 문자가 아닌 경우 반환하는 문자열에서 제외한다.
 
 * @param input CDATA에 들어갈 원본 데이타
 * @return CDATA 에 허용되지 않는 문자들을 처리한 문자열
 */
	public static String fixUsingCodePoint(String input) {
		if(input == null || input.equals("")) {
			return input;
		}
		boolean modified = false;
		StringBuffer sb = new StringBuffer();
		int length = input.length();
		for(int index = 0; index < length; index++) {
			if(index > 1 && input.charAt(index) == '>' && input.charAt(index - 1) == ']' && input.charAt(index - 2) == ']') {
				sb.append("]]><![CDATA[>");
				modified = true;
			} else {
				int codePoint = input.codePointAt(index);
				if(
					codePoint == 0x9 ||
					codePoint == 0xA ||
					codePoint == 0xD ||
					(codePoint >= 0x20 && codePoint <= 0xD7FF) ||
					(codePoint >= 0xE000 && codePoint <= 0xFFFD) ||
					(codePoint >= 0x10000 && codePoint <= 0x10FFFF)
				) {
					sb.appendCodePoint(codePoint);
				} else {
					modified = true;
				}
			}
			if(input.codePointCount(index, Math.min(index + 2, length)) == 1) {
				index++;
			}
		}
		if(modified) {
			return sb.toString();
		} else {
			if(input.equals(sb.toString())) {
				return sb.toString();
			} else {
				throw new RuntimeException("result not equals input(result = /" + sb.toString() + "/, input = /" + input + "/)");
			}
		}
	}
	
	public static String regexp = "[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD" + String.valueOf(new char[]{(char)0xD800, (char)0xDC00}) + "-" + String.valueOf(new char[]{(char)0xDBFF, (char)0xDFFF}) + "]+";
/**
 * CDATA 에서 허용되지 않는 2가지 유형의 문자들을 처리한다.
 
 * 종전의 XML.fix() 함수의 버그를 수정한 method 이다.
 
 * 종전에 XML 에서 허용하지 않는 문자를 제거하기 위한 정규표현식은 다음과 같았다.
 
 * "[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]+"
 
 * 출처 : https://stackoverflow.com/questions/5742543/an-invalid-xml-character-unicode-0xc-was-found
 
 * U+1F150 내지 U+1F168 (음각 원문자)와 U+1F170 내지 U+1F188 (음각 네모문자) 의 경우
 * [#x10000-#x10FFFF] 범위내에 있음에도 불구하고,
 * U+1F150 는 55356(0xD83C) 와 56656(0xDD50) 의 char array 로 구성되므로 [\\x{D800}-\\x{DFFF}] 의 범위에 들어가는 것으로 오인되어 제거되는 버그가 있었다.
 
 * 정규식을 다음과 같이 해도 마찬가지였다.
 
 * "[\\x{0}-\\x{8}]|[\\x{B}-\\x{C}]|[\\x{E}-\\x{1F}]|[\\x{D800}-\\x{DFFF}]|[\\x{FFFE}-\\x{FFFF}]"
 
 * 정규식을 다음과 같이 수정했다.
 
 * "[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD" + String.valueOf(new char[]{(char)0xD800, (char)0xDC00}) + "-" + String.valueOf(new char[]{(char)0xDBFF, (char)0xDFFF}) + "]+"
 
 * @param input CDATA에 들어갈 원본 데이타
 * @return CDATA 에 허용되지 않는 문자들을 처리한 문자열
 */
	public static String fixUsingCharacterRange(String input) {
		if(input == null || input.equals("")) {
			return input;
		}
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(XML.regexp, java.util.regex.Pattern.UNICODE_CHARACTER_CLASS);
		return p.matcher(input).replaceAll("").replaceAll("]]>", "]]]]><![CDATA[>");
	}

/**
 * CDATA 에서 허용되지 않는 2가지 유형의 문자들을 처리한다.
 
 * XML 1.1 의 RestrictedChar 만 제거한다.
 * XML 에서 허용하지 않는 U+D800 부터 U+DFFF 까지, U+FFFE 부터 U_FFFF 까지의 문자는 처리하지 않는다.
 
 * 구현이 정확하지 않으므로 추후에 삭제할 예정이다. 
 
 * @param input CDATA에 들어갈 원본 데이타
 * @return CDATA 에 허용되지 않는 문자들을 처리한 문자열
 */
	public static String fixUsingRestrictedChar(String input) {
		if(input == null || input.equals("")) {
			return input;
		}
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("[\\u0001-\\u0008]|[\\u000B-\\u000C]|[\\u000E-\\u001F]|[\\u007F-\\u0084]|[\\u0086-\\u009F]", java.util.regex.Pattern.UNICODE_CHARACTER_CLASS);
		return p.matcher(input).replaceAll("").replaceAll("]]>", "]]]]><![CDATA[>");
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
 * @return value 에서 attr 에 대응하는 속성값이 발견되면 true 를 리턴한다.
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
 * 첫 번째 파라미터로 공급된 속성값이, 2 번째 이후의 파라미터와 1개라도 일치하는지 검사한다.
 * STR.exists 메소드를 호출하여 비교한다.
 * 속성값의 정의가 없는 경우 무조건 false 를 반환한다.
 * @param element
 * @param attr 속성이름
 * @param value
 * @return value 에서 attr 에 대응하는 속성값이 발견되면 true 를 리턴한다.
 */
	public static boolean vexistsAttrValue(Element element, String attr, String... value) {
		return existsAttrValue(element, attr, value);
	}
/**
 * 첫 번째 파라미터로 공급된 속성값이, 2 번째 파라미터로 공급된 문자열 배열에 있는지 검사한다.
 * STR.existsIgnoreCase 메소드를 호출하여 비교한다.
 * 속성값의 정의가 없는 경우 무조건 false 를 반환한다.
 * @param element
 * @param attr 속성이름
 * @param value
 * @return value 에서 attr 에 대응하는 속성값이 발견되면 true 를 리턴한다.
 */
	public static boolean existsIgnoreCaseAttrValue(Element element, String attr, String[] value) {
		if(element != null && element.hasAttribute(attr) && element.getAttribute(attr) != null) {
			if(value != null) {
				return STR.existsIgnoreCase(element.getAttribute(attr), value);
			}
		}
		return false;
	}
/**
 * 첫 번째 파라미터로 공급된 속성값이, 2 번째 이후의 파라미터와 1개라도 일치하는지 검사한다.
 * STR.existsIgnoreCase 메소드를 호출하여 비교한다.
 * 속성값의 정의가 없는 경우 무조건 false 를 반환한다.
 * @param element
 * @param attr 속성이름
 * @param value
 * @return value 에서 attr 에 대응하는 속성값이 발견되면 true 를 리턴한다.
 */
	public static boolean vexistsIgnoreCaseAttrValue(Element element, String attr, String[] value) {
		return existsIgnoreCaseAttrValue(element, attr, value);
	}
}

