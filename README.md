# Graha

## 1. about

Graha는 데이타베이스에 기반한 웹 프로그램의 개발 생산성을 극대화하는 프로그래밍 도구이다.

### 1.1. Graha의 특징

* Graha는 sql과 화면 레이아웃에 대한 정보를 xml 정의 파일에 기술하는 방식으로 서버 쪽 프로그램을 완성한다.
* 서버 쪽 프로그램을 작성하는데 단 한줄의 Java 코드도 필요하지 않다.
* javascript와 css는 프로그래머의 몫이다.

### 1.2. Graha의 장점

* Graha의 xml 정의 파일은 프로그램의 동작에 대한 정보를 논리적으로 기술함으로써, 결과적으로 xml 파일로 프로그램 명세서를 작성함으로써 프로그램을 완성할 수 있도록 한다.
* 논리적인 Graha의 xml 정의 파일은 프로그램의 유지보수를 쉽게 해주고, 유연하게 변경 할 수 있도록 해준다.
* Graha는 데이타베이스의 정보로부터 xml 정의 파일을 자동으로 생성하는 기능을 제공한다.
* Graha를 사용하는 프로그래머는 copy & paste와 같이 프로그래머를 손목터널증후근으로 인도하는 단순 반복적인 작업으로부터 벗어날 수 있다.
* 암호화나 부득이하게 Java 코드가 필요한 곳에서는 Graha가 제공하는 Java Interface를 구현하면 된다.

### 1.3. 지원하는 데이타베이스

* org.postgresql.Driver
* oracle.jdbc.driver.OracleDriver
* org.mariadb.jdbc.Driver
* org.sqlite.JDBC
* org.apache.derby.jdbc.AutoloadedDriver
* org.hsqldb.jdbc.JDBCDriver
* org.h2.Driver
* 다른 데이타베이스도 사용할 수 있는 방법은 있다.

### 1.4. 배포하는 곳

* 소스코드 : <https://github.com/logicielkr/graha>
* 웹사이트 : <https://graha.kr>

## 2. Overview

### 2.1. 목록형

#### 2.1.1. xml 정의파일

```xml
<query id="list" funcType="list" label="메모">
	<header>
		<style src="/app/css/memo.list.css" />
		<style name="nav.small.screen" override="true" src="/app/css/memo.list-nav.small.screen.css" />
	</header>
	<commands>
		<command name="memo">
			<sql pageSize="15" pageGroupSize="10">
select
	memo_id
	, title
	, to_char(update_date, 'YYYY-MM-DD HH24:MI:SS') as update_date
from memo where insert_id = ?
order by memo_id desc
			</sql>
			<sql_cnt>
select count(*) from memo where insert_id = ?
			</sql_cnt>
			<params>
				<param name="insert_id" datatype="varchar" value="header.remote_user" />
			</params>
		</command>
	</commands>
	<layout>
		<top>
			<right>
				<link name="insert" label="추가" path="/memo/insert" />
			</right>
		</top>
		<middle>
			<tab name="memo">
				<column label="고유번호" name="memo_id" />
				<column label="제목" name="title">
					<link path="/memo/detail">
						<param name="memo_id" type="query" value="memo_id" />
					</link>
				</column>
				<column label="최종수정일시" name="update_date" />
			</tab>
		</middle>
		<bottom>
			<center>page</center>
		</bottom>
	</layout>
</query>
```

#### 2.1.2. 화면출력

![목록형의 예제화면](http://graha.kr/static-contents/images/memo.list.png)

### 2.2. 입력/수정형

#### 2.2.1. xml 정의파일

```xml
<query id="insert" funcType="insert">
	<header>
		<labels>
			<label cond="${param.memo_id} isNotEmpty" text="메모(${/document/rows/row/title})" xText="메모(${//RDF:RDF/RDF:Seq/RDF:li/RDF:item/uc:title})" />
			<label cond="${param.memo_id} isEmpty" text="메모" xText="메모" />
		</labels>
		<style name="nav.small.screen" override="true"  src="/app/css/memo.insert-nav.small.screen.css" />
		<style src="/app/css/memo.insert.css" />
		<script name="check_submit" override="true" src="/app/js/memo.insert.js" />
	</header>
	<validation method="POST">
		<param name="title" not-null="true" msg="제목은 필수 항목입니다!" />
	</validation>
	<tables>
		<table tableName="memo" name="memo" label="메모">
			<column name="memo_id" value="param.memo_id" datatype="int"  primary="true"  insert="sequence.nextval('memo$memo_id')" />
			<column name="title" value="param.title" datatype="varchar" />
			<column name="contents" value="param.contents" datatype="varchar" />
			<column name="insert_date" value="sql.now()" only="insert" datatype="timestamp" />
			<column name="insert_ip" value="header.remote_addr" only="insert" datatype="varchar" />
			<column name="insert_id" value="header.remote_user" only="insert" datatype="varchar" />
			<column name="update_date" value="sql.now()" datatype="timestamp" />
			<column name="update_ip" value="header.remote_addr" datatype="varchar" />
			<column name="update_id" value="header.remote_user" datatype="varchar" />
			<column name="marked" value="param.marked" datatype="boolean" />
			<where>
				<sql>
insert_id = ?
				</sql>
				<params>
					<param name="insert_id" datatype="varchar" value="header.remote_user" />
				</params>
			</where>
		</table>
	</tables>
	<layout msg="변경사항을 저장하시겠습니까?">
		<top>
			<right>
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
					<column label="mark" name="marked" value="marked" type="checkbox" val="t" islabel="false" />
				</row>
				<row>
					<column label="내용" name="contents" value="contents" type="textarea" islabel="false" colspan="3" />
				</row>
			</tab>
		</middle>
		<bottom>
			<left>
				<link name="list" label="목록" path="/memo/list" />
			</left>
		</bottom>
	</layout>
	<redirect path="/memo/list" />
</query>
```

#### 2.2.2. 화면출력

![입력/수정형의 예제화면](http://graha.kr/static-contents/images/memo.insert.png)

### 2.3. 다른 유형들

* 상세보기형
* 삭제형
* sql 실행형(혹은 Java Class 실행형)
