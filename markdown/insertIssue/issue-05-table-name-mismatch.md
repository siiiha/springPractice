# 이슈 05: Mapper와 DB의 테이블명 불일치

## 증상

```text
java.sql.SQLSyntaxErrorException: Table 'ptdb.board' doesn't exist
```

## 원인

애플리케이션은 `ptdb`에 연결되어 있지만 Mapper가 지정한 이름의 테이블이 존재하지 않았다.

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/ptdb
```

예를 들어 실제 테이블은 `board_table`인데 SQL이 `board`를 사용하면 오류가 발생한다.

## 해결

먼저 실제 테이블명을 확인한다.

```sql
USE ptdb;
SHOW TABLES;
```

Mapper의 SQL을 실제 테이블명과 일치시킨다.

```xml
INSERT INTO board_table (member_id, category, title, writer, content)
VALUES (#{memberId}, #{category}, #{title}, #{writer}, #{content})
```

## 확인

- [ ] 현재 연결한 데이터베이스가 `ptdb`인가?
- [ ] `SHOW TABLES` 결과에 대상 테이블이 있는가?
- [ ] 실제 테이블명과 Mapper의 `INSERT INTO` 이름이 같은가?

