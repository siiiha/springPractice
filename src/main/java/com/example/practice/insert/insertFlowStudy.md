# 게시글 등록 흐름 복습

등록 요청을 따라가며 풀었던 질문과 답을 정리했다. 현재 Java와 XML을 기준으로 한 학습 기록이며, 실제 DB 실행 검증 결과는 아니다.

## 1. 컨트롤러가 JSON을 DTO로 받는다

예시 요청:

```http
POST /boards
Content-Type: application/json
```

```json
{
  "memberId": 1,
  "title": "첫 게시글",
  "category": "자유",
  "writer": "시하",
  "content": "안녕하세요"
}
```

클래스의 `@RequestMapping("/boards")`와 메서드의 `@PostMapping`이 합쳐져 이 요청을 받는다.

```java
@PostMapping
public Long insertBoard(@RequestBody BoardInsertDto dto) {
    return boardInsertService.insertBoard(dto);
}
```

### 질문: dto의 title과 content에는 무엇이 들어갈까?

- `title`: `"첫 게시글"`
- `content`: `"안녕하세요"`

JSON 원문 자체가 DTO에 저장되는 것이 아니라 JSON의 값들이 DTO의 각 필드에 들어간다. 이번 요청에는 게시글 번호가 없으므로 처음에는 `boardId`가 null이다.

```text
dto
 ├ boardId = null
 ├ memberId = 1
 ├ title = "첫 게시글"
 ├ category = "자유"
 ├ writer = "시하"
 └ content = "안녕하세요"
```

## 2. 서비스가 DTO를 Mapper에 전달한다

컨트롤러가 서비스의 `insertBoard(dto)`를 호출한다.

```java
public Long insertBoard(BoardInsertDto dto) {
    boardInsertMapper.insertBoard(dto);
    return dto.getBoardId();
}
```

### 질문: boardInsertMapper.insertBoard(dto)는 어디에 무엇을 전달할까?

**Mapper의 insertBoard 메서드에 입력값들이 담긴 DTO를 전달한다.**

```java
@Mapper
interface BoardInsertMapper {
    Long insertBoard(BoardInsertDto dto);
}
```

이 Mapper 메서드는 MyBatis를 통해 XML의 등록 SQL과 연결된다. 현재 Mapper 반환 타입은 `Long`이지만, 이 등록 메서드가 반환하는 값의 의미는 생성된 게시글 번호가 아니라 **등록 SQL의 영향을 받은 행 수**다.

## 3. XML에서 DTO의 값을 등록 SQL에 사용한다

```xml
<mapper namespace="com.example.practice.insert.BoardInsertMapper">
    <insert id="insertBoard"
            useGeneratedKeys="true"
            parameterType="BoardInsertDto"
            keyProperty="boardId">
        INSERT INTO board_table
            (member_id, category, title, writer, content)
        VALUES
            (#{memberId}, #{category}, #{title}, #{writer}, #{content})
    </insert>
</mapper>
```

- `namespace`: 연결할 Mapper 인터페이스의 전체 이름.
- `id="insertBoard"`: 연결할 Mapper 메서드 이름.
- `parameterType`: 전달받는 DTO 타입. 현재처럼 짧은 이름을 쓰려면 해당 별칭이 등록되어 있어야 한다.
- `#{필드명}`: DTO에서 해당 속성의 값을 꺼내 SQL 파라미터로 바인딩한다.

### 질문: #{title}과 #{content}에는 어떤 값이 들어갈까?

각각 `"첫 게시글"`, `"안녕하세요"`가 들어간다. 이 값들로 새 행을 등록한다.

## 4. DB가 만든 게시글 번호를 DTO에 넣는다

```xml
useGeneratedKeys="true"
keyProperty="boardId"
```

DB가 게시글 번호를 자동 생성하도록 설정되어 있고 드라이버가 생성 키 반환을 지원한다면, MyBatis가 생성된 번호를 받아 **전달했던 DTO의 boardId 필드에 넣는다.**

예를 들어 DB가 새 번호로 11을 만들었다면:

```text
등록 전: dto.boardId = null
등록 후: dto.boardId = 11
```

이 설정 자체가 DB 컬럼에 자동 증가 기능을 만드는 것은 아니다. 실제 DB의 번호 생성 설정이 먼저 필요하다.

### 질문: 이후 return dto.getBoardId()는 무엇을 반환할까?

```java
return dto.getBoardId();
```

DTO에 채워진 새 게시글 번호 **11**을 서비스를 호출한 컨트롤러에 반환한다.

## 5. 컨트롤러가 번호를 응답한다

```java
return boardInsertService.insertBoard(dto);
```

서비스가 반환한 11이 이 호출 자리로 돌아온다. 컨트롤러도 그 값을 반환한다. `@RestController`이므로 사용자에게 응답 본문으로 11을 보낸다.

## 6. 행 수와 생성된 번호를 구분한다

### 마지막 질문: 새 게시글 한 개가 등록되고 번호가 11이면 각각 무엇을 반환할까?

학습자의 답: **“맵퍼는 1이고 서비스는 11일까?”** → 맞다.

| 위치 | 의미 | 이번 값 |
|---|---|---|
| Mapper의 반환값 | 등록된 행 수 | `1` |
| 등록 후 DTO의 boardId | DB가 생성한 게시글 번호 | `11` |
| 서비스의 반환값 | DTO에서 꺼낸 게시글 번호 | `11` |
| 컨트롤러의 응답 | 서비스가 반환한 번호 | `11` |

현재 서비스는 Mapper가 반환한 행 수를 변수에 저장하거나 검사하지 않는다.

```java
boardInsertMapper.insertBoard(dto); // 반환된 행 수는 사용하지 않음
return dto.getBoardId();            // DTO에 채워진 번호를 반환
```

**Mapper의 메서드 반환값과 DTO에 채워지는 생성 키는 서로 다른 경로로 전달된다.** Mapper가 11을 반환해서 DTO에 넣는 구조가 아니다.

## 수정과 등록의 차이

| 기능 | 게시글 번호를 얻는 곳 | DTO에 넣는 주체 |
|---|---|---|
| 수정 | 요청 URL의 기존 게시글 번호 | 컨트롤러의 setBoardId |
| 등록 | DB가 새로 생성한 번호 | 생성 키 설정을 사용하는 MyBatis |

## 전체 흐름

```text
POST /boards + JSON
    ↓
컨트롤러: JSON 값을 DTO로 받음
    ↓
서비스: 같은 DTO를 Mapper에 전달
    ↓
Mapper: XML의 insertBoard SQL과 연결
    ↓
DB: 새 행 등록, 새 번호 11 생성
    ↓
MyBatis: 생성된 번호 11을 DTO의 boardId에 설정
    ↓
Mapper → 서비스: 등록된 행 수 1 반환 (현재 사용하지 않음)
    ↓
서비스 → 컨트롤러: dto.getBoardId()로 11 반환
    ↓
컨트롤러 → 사용자: 응답 본문 11
```

## 안 보고 복습하기

1. @RequestBody는 JSON 원문을 담는가, DTO 필드에 값을 채우는가?
2. 서비스는 Mapper에 무엇을 전달하는가?
3. #{title}은 DB 컬럼 이름인가, DTO에서 가져올 값의 자리인가?
4. useGeneratedKeys와 keyProperty는 어떤 역할을 하는가?
5. 새 행 하나의 번호가 20이면 Mapper와 서비스는 각각 무엇을 반환하는가?
6. 등록과 수정에서 게시글 번호를 얻는 방식은 어떻게 다른가?
