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
}
