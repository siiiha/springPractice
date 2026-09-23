# 이슈 04: `SelectDto` 결과 타입 별칭 문제

## 증상

Mapper XML을 읽는 과정에서 다음 형태의 오류가 발생할 수 있다.

```text
Could not resolve type alias 'SelectDto'
ClassNotFoundException: Cannot find class: SelectDto
```

## 원인

`resultType`에 DTO의 단축 이름을 사용했지만 MyBatis에 해당 별칭을 등록하지 않았다.

```xml
<select id="selectDetail"
        resultType="SelectDto">
```

`resultType`은 SELECT 결과 한 행을 어떤 Java 객체로 만들지 지정한다.

## 해결

DTO의 패키지를 포함한 전체 이름을 사용한다.

```xml
<select id="selectDetail"
        resultType="com.example.practice.select.SelectDto">
    SELECT board_id, member_id, category, title, writer, content
    FROM board_table
    WHERE board_id = #{boardId}
</select>
```

## 확인

- [ ] `resultType`에 DTO의 전체 클래스 경로를 사용했는가?
- [ ] DTO의 실제 package 선언과 경로가 같은가?
- [ ] 조회 컬럼명이 DTO 필드와 연결되는가?
- [ ] `map-underscore-to-camel-case=true`가 설정되어 있는가?
