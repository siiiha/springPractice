# 이슈 07: 필수 `member_id` 값 누락

## 증상

```text
java.sql.SQLException:
Field 'member_id' doesn't have a default value
```

## 원인

`member_id`가 `NOT NULL`이고 기본값도 없지만 INSERT 컬럼과 요청 DTO에서 값이 빠졌다.

## 해결

DTO에 필드를 추가한다.

```java
private Long memberId;
```

Mapper XML의 컬럼과 값에 `member_id`를 추가한다.

```xml
<insert id="insert"
        useGeneratedKeys="true"
        keyProperty="boardId">
    INSERT INTO board_table (member_id, category, title, writer, content)
    VALUES (#{memberId}, #{category}, #{title}, #{writer}, #{content})
</insert>
```

테스트 요청에도 실제 존재하는 회원 ID를 넣는다.

```json
{
  "memberId": 1,
  "category": "공지",
  "title": "첫 글",
  "writer": "juhyo",
  "content": "안녕하세요"
}
```

`member_id`가 외래 키라면 참조하는 회원 데이터가 DB에 실제로 존재해야 한다.

## 확인

- [ ] DTO에 `memberId`가 있는가?
- [ ] INSERT 컬럼에 `member_id`가 있는가?
- [ ] VALUES에 `#{memberId}`가 있는가?
- [ ] 요청 JSON에 `memberId`가 있는가?
- [ ] 해당 회원 ID가 참조 테이블에 실제로 존재하는가?

## 이후 개선

실제 인증 기능을 적용한 뒤에는 클라이언트가 `memberId`를 임의로 보내게 하지 않고 로그인한 사용자 정보에서 가져오는 것이 안전하다.
