# Graha Quick Start Guide

## 1. 개요

### 1.1. 시스템 요구사항

* JDK 1.7 or above
* Apache Tomcat 7.x or above (혹은 Servlet API 3.1 이상을 지원하는 Web Application Server)
* 데이타베이스(lastest version) 및 JDBC 드라이버(4.1 or above)
* commons-fileupload-1.1.1.jar
* commons-io-1.1.jar

### 1.2. 데이타베이스 테이블에 물리적 구조에 대한 제약(입력/수정 기능에서만 적용)

* 테이블은 1개의 컬럼으로 구성된 Primary Key를 가져야 한다.
* Primary Key 컬럼은 시퀀스를 사용한다(sqlite 예외).
* 시퀀스의 이름은 테이블이름**$**컬럼이름 과 같은 형태이어야 한다(Graha가 제공하는 Manager 프로그램 사용시).

### 1.3. Graha를 이용한 프로그램 개발 절차

* Apache Tomcat 설정(이미 설정된 경우 생략 가능)
* Graha Manager를 이용해서 데이타베이스의 테이블 정보로부터 Graha xml 정의 파일 자동생성
* Graha xml 정의 파일 커스트마이징

## 2. Apache Tomcat 설정 (Apache Derby Embedded 기준)

### 2.1. conf/server.xml 파일에 데이타베이스 연결 정보를 추가(GlobalNamingResources 요소(Element) 아래)

```xml
<Resource name="jdbc/memo" 
	auth="Container"
	type="javax.sql.DataSource" 
	driverClassName="kr.xdbc.driver.GenericDriver"
	url="xdbc:jdbc:derby:memo;create=true"
	username="postgres"
	password="password" />
```

### 2.2. conf/context.xml 파일에서 ResourceLink(Element) 요소 추가

```xml
<ResourceLink global="jdbc/memo" name="jdbc/memo" type="javax.sql.DataSource"/>
```

### 2.3. lib/ 디렉토리에 다음 파일을 복사

* jdbc 드라이버(derby.jar)
* xdbc jdbc 드라이버(<https://xdbc.kr>)

### 2.4. WEB-INF/lib 디렉토리에 Graha 라이브러리 및 의존성 라이브러리 복사

* graha.0.5.0.0.jar (Graha 라이브러리)
* commons-fileupload-1.1.1.jar
* commons-io-1.1.jar

### 2.5. WEB-INF/web.xml 파일 설정

```xml
<servlet>
	<servlet-name>GrahaServlet</servlet-name>
	<servlet-class>kr.graha.servlet.GeneratorServlet</servlet-class>
</servlet>
<servlet-mapping>
	<servlet-name>GrahaServlet</servlet-name>
	<url-pattern>/graha/*</url-pattern>
</servlet-mapping>
<servlet>
	<servlet-name>GrahaManagerServlet</servlet-name>
	<servlet-class>kr.graha.assistant.Manager</servlet-class>
	<init-param>
		<param-name>jndi</param-name>
		<param-value>jdbc/memo</param-value>
	</init-param>
</servlet>
<servlet-mapping>
	<servlet-name>GrahaManagerServlet</servlet-name>
	<url-pattern>/graha-manager/*</url-pattern>
</servlet-mapping>
```

### 2.6. WEB-INF/graha 디렉토리 생성

매끈한 화면으로 확인하기 위해서 <https://github.com/logicielkr/graha> 에서 sample/base 에서 _base.xml 파일을 WEB-INF/graha/ 에 다운로드 받는다.
_base.xml은 css/javascript로 구성된 읿종의 화면 template 과 같은 것이다.

## 3. GrahaManager

GrahaManager는 자동으로 Graha xml 정의 파일을 생성하는 기능을 제공하는데,
부수적으로 SQL 실행기, Table 목록, Table Column 정보 조회, Table 데이타 조회(30건 제한) 및 Table, Column 에 comment 를 추가/변경하는 기능을 제공한다.

Graha는 전문적인 데이타베이스 관리 도구가 아니기 때문에 매우 기본적인 기능만 제공하고 특별한 상황에서 오류가 발생할 가능성도 있다.

### 3.1. SQL Runner

SQL Runner는 Web 기반의 sql 실행기이다.

웹브라우저 주소창에 http://${SERVER_NAME}/${CONTEXT_ROOT}/graha-manager/query 와 같은 형식의 URL을 입력한다. 

예를 들면 ```http://localhost/graha-manager/query``` 와 같은 식이다.

데이타베이스 설정에 문제가 없다면, 다음과 같은 화면을 볼 수 있을 것이다.

![Graha SQL Runner 화면](http://graha.kr/static-contents/images/manager.sql_runner.png)

* ❶ 테이블목록으로 이동하는 버튼
* ❷ sql 입력창
* ❸ sql 실행 버튼 (Ctrl + Enter을 입력해도 된다)

❷ sql 입력창에 다음과 같은 sql을 입력하고 ❸ sql 실행 버튼이나 Ctrl + Enter를 입력하면, 서버의 현재 날짜와 시간을 확인할 수 있다.

```sql
select current_timestamp from sysibm.sysdummy1
```
### 3.1.1. Sequence 및 Table 생성

다음과 같이 sequence 생성 sql 을 실행한다.

```sql
CREATE SEQUENCE "memo$memo_id"
start with 1
```

다음과 같이 table 생성 sql 을 실행한다.

```sql
create table memo (
memo_id integer not null,
title varchar(1000),
contents long varchar,
marked bool,
insert_date timestamp,
insert_id varchar(50),
insert_ip varchar(15),
update_date timestamp,
update_id varchar(50),
update_ip varchar(15),
PRIMARY KEY (memo_id)
)
```

### 3.2. Graha xml 정의 파일 생성하기

❶ 테이블목록으로 이동하는 버튼을 클릭하면 다음 화면이 기다리고 있다.

![Graha 테이블 목록 화면](http://graha.kr/static-contents/images/manager.table_list.png)

* ❶ SQL Runner로 이동하는 버튼
* ❷ Graha xml 정의 파일을 생성하는 버튼(먼저 테이블을 선택해야 한다)
* ❸ 테이블 선택
* ❹ 테이블 comment
* ❺ Table Column 정보 조회 기능으로 이동하는 버튼
* ❻ Table의 데이터를 조회하는 기능으로 이동하는 버튼(30개로 제한)
* ❼ 테이블 comment를 저장하는 기능

Graha xml 정의 파일을 생성하기 전에 table과 column의 comment를 각각 입력해서 저장하는 것을 추천한다.

> Apache Derby의 경우 comment on table 구문을 지원하지 않고, MariaDB의 경우 alter table 구문만을 지원한다.
> Graha는 Apache Derby와 MariaDB의 경우 graha_tab_comments 와 graha_col_comments 테이블을 자동으로 만든다.

table과 column의 comment를 모두 저장하고 나면, ❸ 테이블을 선택하고, ❷ Generation 버튼을 클릭하면 다음 절차로 넘어간다.

![Graha Master 테이블 선택 화면](http://graha.kr/static-contents/images/manager.select.png)

> 1개의 테이블을 선택한 경우 Generation 버튼을 클릭하면 된다.
> 여러개의 테이블을 선택한 경우 1개의 master 테이블을 선택해야 한다.  이 경우 나머지 테이블에는 master 테이블의 primary key와 동일한 이름을 갖는 column을 가지고 있어야 하고, 물리적으로 foreign 키 설정과는 관련이 없다.

## 4. Graha 를 이용한 프로그램 개발

### 4.1. 생성된 Graha xml 정의 파일 확인하기

조용히 테이블 목록 화면으로 돌아왔다면 무사히 Graha xml 정의 파일을 생성한 것이다.

WEB-INF/graha/ 디렉토리로 가면 memo.xml 혹은 memo.xml 파일이 이미 있었다면 memo-1.xml과 같이 일련번호가 붙은 파일을 확인 할 수 있다.

```xml
<querys>
	<header extends="_base.xml">
		<jndi name="jdbc/derby" />
	</header>
	<query id="list" funcType="list" label="메모">
		<header>
		</header>
		<commands>
			<command name="memo">
				<sql pageSize="15" pageGroupSize="10">
					select
						MEMO_ID
						, TITLE
						, CONTENTS
						, MARKED
					from MEMO
				</sql>
				<sql_cnt>
					select count(*) from MEMO
				</sql_cnt>
			</command>
		</commands>
		<layout>
			<top>
				<left>
					<link name="insert" label="추가" path="/memo/insert" />
				</left>
			</top>
			<middle>
				<tab name="memo">
					<column label="메모ID" name="memo_id">
						<link path="/memo/detail">
							<param name="memo_id" type="query" value="memo_id" />
						</link>
					</column>
					<column label="제목" name="title" />
					<column label="내용" name="contents" />
					<column label="마크다운여부" name="marked" />
				</tab>
			</middle>
			<bottom>
			<center>page</center>
			</bottom>
		</layout>
	</query>
	<query id="insert" funcType="insert" label="메모">
		<header>
		</header>
		<tables>
			<table tableName="MEMO" name="memo" label="메모">
				<column name="memo_id" value="param.memo_id" datatype="int"  primary="true"  insert="sequence.NEXT VALUE FOR &quot;memo$memo_id&quot;" />
				<column name="title" value="param.title" datatype="varchar" />
				<column name="contents" value="param.contents" datatype="varchar" />
				<column name="marked" value="param.marked" datatype="boolean" />
				<column name="insert_date" only="insert" value="sql.current_timestamp" datatype="timestamp" />
				<column name="insert_id" only="insert" value="header.remote_user" datatype="varchar" />
				<column name="insert_ip" only="insert" value="header.remote_addr" datatype="varchar" />
				<column name="update_date" value="sql.current_timestamp" datatype="timestamp" />
				<column name="update_id" value="header.remote_user" datatype="varchar" />
				<column name="update_ip" value="header.remote_addr" datatype="varchar" />
			</table>
		</tables>
		<layout msg="변경사항을 저장하시겠습니까?">
			<top>
				<left />
				<center />
				<right>
					<link name="list" label="목록" path="/memo/list" />
					<link name="save" label="저장" path="/memo/insert" method="post" type="submit" full="true">
						<params>
							<param name="memo_id" type="query" value="memo_id" />
						</params>
					</link>
				</right>
			</top>
			<middle>
				<tab name="memo" label="메모">
					<row>
						<column label="제목" name="title" value="title" />
					</row>
					<row>
						<column label="내용" name="contents" value="contents" />
					</row>
					<row>
						<column label="마크다운여부" name="marked" value="marked" />
					</row>
				</tab>
			</middle>
			<bottom>
				<right>
				</right>
			</bottom>
		</layout>
		<redirect path="/memo/list" />
	</query>
	<query id="detail" funcType="detail" label="메모(${/document/rows/row/title})">
		<header>
		</header>
		<commands>
			<command name="memo">
				<sql>
					select
						MEMO_ID
						, TITLE
						, CONTENTS
						, MARKED
					from MEMO
						where MEMO_ID = ?
				</sql>
				<params>
					<param default="null" name="memo_id" datatype="int" value="param.memo_id" />
				</params>
			</command>
		</commands>
		<layout>
			<top>
				<left />
				<center />
				<right>
					<link name="list" label="목록" path="/memo/list" />
					<link name="update" label="수정" path="/memo/insert">
						<params>
							<param name="memo_id" type="query" value="memo_id" />
						</params>
					</link>
				</right>
			</top>
			<middle>
				<tab name="memo" label="메모">
					<row>
						<column label="제목" name="title" />
					</row>
					<row>
						<column label="내용" name="contents" />
					</row>
					<row>
						<column label="마크다운여부" name="marked" />
					</row>
				</tab>
			</middle>
			<bottom>
				<left>
					<link label="삭제" path="/memo/delete" method="post" type="submit" msg="정말로 삭제하시겠습니까?">
						<params>
							<param name="memo_id" type="query" value="memo_id" />
						</params>
					</link>
				</left>
			</bottom>
			
		</layout>
	</query>
	<query id="delete" funcType="delete" label="메모">
		<tables>
			<table tableName="MEMO" name="memo">
				<column name="memo_id" primary="true" value="param.memo_id" datatype="int" />
			</table>
		</tables>
		<redirect path="/memo/list" />
	</query>
</querys>
```

### 4.2. 화면에서 확인하고 쓸모있게 변경하기

웹브라우저 주소창에 http://${SERVER_NAME}/${CONTEXT_ROOT}/graha/${확장자를 제외한 XML 파일이름}/${query 요소의 id 속성값}.xml 와 같은 형식의 URL을 입력한다. 

예를 들면 ```http://localhost/graha/memo/list.xml``` 와 같은 식이다.

#### 4.2.1 목록 화면

![자동생성된 Graha xml 정의 파일의 목록 화면](http://graha.kr/static-contents/images/memo001.list.png)

아직 완성형이 아니다.  Graha Manager 는 여기까지다.  이제부터는 프로그래머의 몫이다.

목록 화면에서는 일반적으로 내용, 마크다운여부 항목은 필요가 없고, 대신 최종수정일시를 추가해야 한다.

그리고 상세보기로 이동하는 링크는 Primary Key 컬럼인 memo_id에 걸려 있는데, 이걸 title로 옮겨야 하고, 정렬방식도 memo_id의 역순이어야 한다.

먼저 id 속성값이 list인 query 요소에서 sql 요소만 떼어내자.

```xml
<sql pageSize="15" pageGroupSize="10">
select
	MEMO_ID
	, TITLE
	, CONTENTS
	 MARKED
from MEMO
</sql>
```

이걸 다음과 같이 변경한다.

```xml
<sql pageSize="15" pageGroupSize="10">
select
	MEMO_ID
	, TITLE
	, cast(cast(update_date as date) as varchar(10)) || ' ' || cast(cast(update_date as time) as varchar(8)) as last_update_date
from MEMO
order by memo_id desc
</sql>
```

다음은 id 속성값이 list인 query 요소에서 tab 요소만 떼어내자.

```xml
<tab name="memo">
	<column label="메모ID" name="memo_id">
		<link path="/memo/detail">
			<param name="memo_id" type="query" value="memo_id" />
		</link>
	</column>
	<column label="제목" name="title" />
	<column label="내용" name="contents" />
	<column label="마크다운여부" name="marked" />
</tab>
```

이걸 다음과 같이 변경한다.

```xml
<tab name="memo">
	<column label="메모ID" name="memo_id" />
	<column label="제목" name="title" >
		<link path="/memo/detail">
			<param name="memo_id" type="query" value="memo_id" />
		</link>
	</column>
	<column label="최종수정일시" name="last_update_date" />
</tab>
```

다음은 css를 추가해서 각 항목의 넓이를 보기 좋게 만들기 위해서 id 속성값이 list인 query 요소에서 header 요소만 떼어내자.

```xml
<header>
</header>
```

이걸 다음과 같이 변경한다.

```xml
<header>
	<style>
td.memo_id {
	width:80px;
	text-align:center;
}
td.last_update_date {
	width:180px;
	text-align:center;
}
	</style>
</header>
```

![개선된 목록 화면](http://graha.kr/static-contents/images/memo002.list.png)

#### 4.2.2 추가/수정 화면

![자동생성된 Graha xml 정의 파일의 입력/수정 화면](http://graha.kr/static-contents/images/memo001.insert.png)

여기서 해야 할 일은 contents 를 textarea로 변경하고 marked을 checkbox로 변경하고 title 항목의 오른쪽 구석에 넣으면 된다.

먼저 id 속성값이 insert인 query 요소에서 tab 요소만 떼어내자.
```xml
<tab name="memo" label="메모">
	<row>
		<column label="제목" name="title" value="title" />
	</row>
	<row>
		<column label="내용" name="contents" value="contents" />
	</row>
	<row>
		<column label="마크다운여부" name="marked" value="marked" />
	</row>
</tab>
```

이걸 다음과 같이 변경한다.
```xml
<tab name="memo" label="메모">
	<row>
		<column label="제목" name="title" value="title" />
		<column label="마크다운여부" name="marked" value="marked" type="checkbox" islabel="false" val="true" />
	</row>
	<row>
		<column label="내용" name="contents" value="contents" type="textarea" islabel="false" colspan="3" />
	</row>
</tab>
```

다음은 css 를 추가해서 각 항목의 넓이를 보기 좋게 만들기 위해서 id 속성값이 insert인 query 요소에서 header 요소만 떼어내자.

```xml
<header>
</header>
```

이걸 다음과 같이 변경한다.

```xml
<header>
	<style>
td.marked {
	width:50px;
	text-align:center;
}
th.title {
	width:120px;
}
	</style>
</header>
```

![개선된 추가/수정 화면](http://graha.kr/static-contents/images/memo002.insert.png)

#### 4.2.3 상세보기 화면

![자동생성된 Graha xml 정의 파일의 상세보기 화면](http://graha.kr/static-contents/images/memo001.detail.png)

여기는 제목 등의 label의 넓이를 조정하는 등 화면을 정리해야 하고, 화면에 입력일시와 최종수정일시도 표기하기로 한다.

먼저 id 속성값이 detail인 query 요소에서 sql 요소만 떼어내자.

```xml
<sql>
	select
		MEMO_ID
		, TITLE
		, CONTENTS
		, MARKED
	from MEMO
		where MEMO_ID = ?
</sql>
```

이걸 다음과 같이 변경한다.

```xml
<sql>
	select
		MEMO_ID
		, TITLE
		, CONTENTS
		, MARKED
		, cast(cast(insert_date as date) as varchar(10)) || ' ' || cast(cast(insert_date as time) as varchar(8)) as last_insert_date
		, cast(cast(update_date as date) as varchar(10)) || ' ' || cast(cast(update_date as time) as varchar(8)) as last_update_date
	from MEMO
		where MEMO_ID = ?
</sql>
```

다음은 id 속성값이 detail인 query 요소에서 tab 요소만 떼어내자.

```xml
<tab name="memo" label="메모">
	<row>
		<column label="제목" name="title" />
	</row>
	<row>
		<column label="내용" name="contents" />
	</row>
	<row>
		<column label="마크다운여부" name="marked" />
	</row>
</tab>
```

이걸 다음과 같이 변경한다.

```xml
<tab name="memo" label="메모">
	<row>
		<column label="제목" name="title" />
	</row>
	<row>
		<column label="내용" name="contents" islabel="false" />
	</row>
	<row>
		<column label="입력" name="last_insert_date" />
	</row>
	<row>
		<column label="수정" name="last_update_date" />
	</row>
</tab>
```

다음은 css 를 추가해서 각 항목의 넓이를 보기 좋게 만들기 위해 id 속성값이 detail인 query 요소에서 header 요소만 떼어내자.

```xml
<header>
</header>
```

이걸 다음과 같이 변경한다.

```xml
<header>
	<style>
th {
	width:120px;
}
td.contents {
	white-space:pre-wrap;
	height: 200px;
}
	</style>
</header>
```

![개선된 상세보기 화면](http://graha.kr/static-contents/images/memo002.detail.png)

## 5. 결어

지금까지 Graha를 이용한 프로그램 개발에 대해 살펴보았다.

Graha Manager가 자동으로 생성한 Graha xml 정의 파일을 원문 그대로 인용하다 보니 본문의 내용이 길어졌지만, 서버쪽 코드를 거의 작성하지 않고 매우 짧은 시간에 기능의 중요 부분을 완성할 수 있었다.

이제부터는 주로 css/javascript 작업만이 남아 있는 셈이다.

물론 Graha는 완전히 새로운 라이브러리이기 때문에 Graha xml 정의 파일의 구문을 익히는데 많은 수고가 필요할 수는 있지만, 저자가 그동안 사용했던 다른 프레임웍이나 라이브러리에 비하면 구문이나 사용방법이 비교적 간단한 편이고, 서버 쪽 코드를 작성할 필요가 거의 없다는 것을 감안하면 충분한 가치가 있다고 생각한다.

저자가 생각하는 Graha 가장 큰 장점은 Graha xml 정의 파일이 프로그램 명세서 수준으로 간결하고 구조적이라는 것이다.

이 글을 쓰고 있는 현재 시점에서는 Graha에 관한 문서들이 부족하기 때문에 이 글을 넘어서는 사용법을 익히는 것이 쉽지 않은 일이지만, 앞으로 시간이 날 때마다 사례를 중심으로 문서화 작업을 진행할 예정이다.
