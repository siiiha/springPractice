# 이슈 06: `writer` 컬럼이 존재하지 않음

## 증상

```text
java.sql.SQLSyntaxErrorException:
Unknown column 'writer' in 'field list'
```

실행된 SQL:

```sql
INSERT INTO board_table (category, title, writer, content)
VALUES (?, ?, ?, ?);
```

## 원인

Mapper는 `writer` 컬럼에 값을 저장하려 했지만 실제 `board_table`에는 해당 컬럼이 없었다.

## 해결

먼저 테이블 구조를 확인한다.

```sql
DESC board_table;
```

설계상 문자열 작성자 컬럼이 필요하다면 추가한다.

```sql
ALTER TABLE board_table
ADD COLUMN writer VARCHAR(50) NOT NULL;
```

이미 작성자를 나타내는 다른 컬럼이 있다면 새 컬럼을 중복 생성하지 말고 Mapper의 컬럼명을 실제 스키마에 맞춘다.

## 확인

- [ ] `DESC board_table` 결과에 `writer`가 있는가?
- [ ] SQL의 컬럼명과 실제 DB 컬럼명이 같은가?
- [ ] `writer`와 `member_id`를 모두 둘 것인지 데이터 모델을 결정했는가?

