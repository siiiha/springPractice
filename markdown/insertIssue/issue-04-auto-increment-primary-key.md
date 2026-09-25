# 이슈 04: `AUTO_INCREMENT` 기본 키 처리

## 증상

신규 게시글을 저장할 때 `board_id`를 직접 전달해야 하는 것처럼 구현했거나, 저장 후 생성된 ID를 DTO에서 확인할 수 없다.

## 원인

`board_id`는 DB가 자동으로 생성하는 컬럼인데 INSERT 대상에 포함했다.

```sql
board_id BIGINT AUTO_INCREMENT PRIMARY KEY
```

## 해결

`board_id`를 INSERT 컬럼에서 제외하고 생성된 값을 DTO로 돌려받는다.

```xml
<insert id="insert"
        useGeneratedKeys="true"
        keyProperty="boardId">
    INSERT INTO board_table (member_id, category, title, writer, content)
    VALUES (#{memberId}, #{category}, #{title}, #{writer}, #{content})
</insert>
```

- `useGeneratedKeys="true"`: DB가 생성한 키를 가져온다.
- `keyProperty="boardId"`: 가져온 키를 DTO의 `boardId`에 저장한다.

```java
Long board(boardInsertDto dto) {
    boardInsertMapper.insert(dto);
    return dto.getBoardId();
}
```

## 확인

- [ ] `board_id`가 실제로 `AUTO_INCREMENT`인가?
- [ ] INSERT 컬럼에서 `board_id`를 제외했는가?
- [ ] `useGeneratedKeys`와 `keyProperty`를 설정했는가?

