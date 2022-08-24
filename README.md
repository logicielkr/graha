# Graha

## 1. about

Graha는 데이타베이스에 기반한 웹 프로그램의 개발 생산성을 높이고 구조화하기 위한 프로그래밍 도구이다.

### 1.1. 배포하는 곳

* 소스코드 : GitHub Graha 프로젝트 (https://github.com/logicielkr/graha)
* 웹사이트 : Graha 홈페이지 (https://graha.kr)

### 1.2. Graha의 목표

설계서를 작성하듯 프로그램을 개발한다.

### 1.3. Graha의 특징

* Graha는 sql과 화면 레이아웃에 대한 정보를 xml 정의 파일에 기술하는 방식으로 서버 쪽 프로그램을 완성한다.
* 서버 쪽 프로그램을 작성하는데 Java 코드는 필요없다.
* javascript와 css는 프로그래머의 몫이다.

### 1.4. Graha의 장점

* Graha의 xml 정의 파일은 프로그램 동작에 대한 정보를 논리적으로 기술함으로써, 결과적으로 xml 파일로 프로그램 명세서를 작성하는 수준에서 프로그램을 완성할 수 있도록 한다.
* 논리적인 Graha의 xml 정의 파일은 프로그램의 유지보수를 쉽게 해주고, 유연하게 변경 할 수 있도록 해준다.
* Graha는 데이타베이스의 정보로부터 xml 정의 파일을 자동으로 생성하는 기능을 제공한다.
* Graha를 사용하는 프로그래머는 copy & paste와 같이 프로그래머를 손목터널증후근으로 인도하는 단순 반복적인 작업으로부터 벗어날 수 있다.
* 암호화나 부득이하게 Java 코드가 필요하면, Graha가 제공하는 Java Interface를 구현하면 된다.

### 1.5. 지원하는 데이타베이스

* org.postgresql.Driver
* oracle.jdbc.driver.OracleDriver
* org.mariadb.jdbc.Driver
* org.sqlite.JDBC
* org.apache.derby.jdbc.AutoloadedDriver
* org.hsqldb.jdbc.JDBCDriver
* org.h2.Driver
* 다른 데이타베이스도 방법은 있다.

## 2. 버전에 관하여

현재 Graha 는 버전 관리를 하고 있지 않다.

Graha 및 Graha 로 작성된 응용프로그램들은 각각 최신 버전을 사용하는 것을 권한다.

## 3. Graha 로 작성된 응용프로그램

- [WebMUA](https://github.com/logicielkr/WebMUA) : Web 기반의 이메일 클라이언트 프로그램
- [메모장](https://github.com/logicielkr/memo) : 메모장 프로그램
- [실시간 Java 실행기](https://github.com/logicielkr/JavaExecutor) : 실시간으로 Java 코드를 실행하고 그 결과를 확인할 수 있는 프로그램
- [실시간 Query 실행기](https://github.com/logicielkr/QueryExecutor) : 실시간으로 sql를 실행하고 그 결과를 확인할 수 있는 프로그램
- [실시간 Javascript 실행기](https://github.com/logicielkr/JavascriptExecutor) : 실시간으로 Javascript 코드를 실행하고 그 결과를 확인할 수 있는 프로그램
- [테이블 정의서](https://github.com/logicielkr/table) : 테이블 정의서 프로그램
