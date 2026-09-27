# 게시글 조회 흐름 복습

상세 조회, 목록 조회, 개수 조회를 따라가며 풀었던 질문과 답을 정리했다. 코드를 읽으며 학습한 내용이며 실제 DB 실행 검증 결과는 아니다.

## 1. 상세 조회 요청을 받는다

```http
GET /boards/3
```

클래스의 `@RequestMapping("/boards")`와 다음 메서드가 연결된다.

```java
@GetMapping("/{boardId}")
@ResponseBody
public BoardSelectDto getBoardDetail(@PathVariable Long boardId) {
    return boardSelectService.getBoardDetail(boardId);
}
```

### 질문: boardId에는 어떤 값이 들어갈까?

주소 경로에서 가져온 **3**이 들어간다.

### 질문: 마지막 줄은 어디에 무엇을 전달할까?

서비스의 `getBoardDetail()`에 게시글 번호 3을 전달한다.

## 2. 서비스가 Mapper에 번호를 전달한다

```java
public BoardSelectDto getBoardDetail(Long boardId) {
    return boardSelectMapper.getBoardDetail(boardId);
}
```

서비스는 받은 번호를 Mapper에 그대로 전달하고, Mapper에서 돌아온 결과를 컨트롤러에 반환한다.

### 질문: 이 메서드는 숫자를 반환할까, DTO를 반환할까?

반환 타입이 `BoardSelectDto`이므로 **조회한 게시글 정보가 담긴 DTO**를 반환한다. 조건에 맞는 게시글이 없으면 이 단건 조회에서는 null이 돌아올 수 있다.

```java
BoardSelectDto getBoardDetail(Long boardId);
// 반환 타입                  전달받는 번호
```

## 3. 상세 조회 SQL을 실행한다

```xml
<select id="getBoardDetail" resultType="BoardSelectDto">
    SELECT board_id, category, title, writer, content
    FROM board_table
    WHERE board_id = #{boardId}
      AND deleted_at IS NULL
</select>
```

Mapper 인터페이스는 XML의 `namespace`와, 메서드는 XML의 `id`와 연결된다.

- `#{boardId}`: 전달받은 번호 3을 바인딩한다.
- `deleted_at IS NULL`: 삭제되지 않은 글만 조회한다.
- `resultType="BoardSelectDto"`: 결과 행을 담을 객체 타입을 지정한다.

### 질문: DB의 제목이 '첫 게시글', 내용이 '안녕하세요'라면?

```text
BoardSelectDto
 ├ title = "첫 게시글"
 └ content = "안녕하세요"
```

조회한 컬럼 값이 DTO의 해당 필드로 매핑된다. `board_id`처럼 필드명 `boardId`와 표기가 다른 컬럼은 별칭이나 MyBatis 매핑 설정이 필요하다. 현재 XML의 짧은 DTO 이름도 별칭 설정이 필요하며, 이 설정들은 실행 준비 때 확인할 부분이다.

## 4. DTO가 응답으로 돌아간다

```text
DB 조회 결과
    ↓
Mapper: 조회 결과를 DTO로 반환
    ↓
서비스: DTO를 컨트롤러에 반환
    ↓
컨트롤러: @ResponseBody를 통해 JSON 응답
```

제목과 내용 부분만 예시로 보면 다음과 같다. 실제 응답에는 DTO의 다른 속성도 포함될 수 있다.

```json
{
  "title": "첫 게시글",
  "content": "안녕하세요"
}
```

## 5. 목록 조회는 DTO 여러 개를 반환한다

```http
GET /boards/list
```

컨트롤러:

```java
@GetMapping("/list")
@ResponseBody
public List<BoardSelectDto> getBoardList() {
    return boardSelectService.getBoardList();
}
```

서비스:

```java
public List<BoardSelectDto> getBoardList() {
    return boardSelectMapper.selectBoardList();
}
```

Mapper:

```java
List<BoardSelectDto> selectBoardList();
```

SQL:

```xml
<select id="selectBoardList" resultType="BoardSelectDto">
    SELECT board_id, member_id, category, title, writer, content,
           create_at, update_at
    FROM board_table
    WHERE deleted_at IS NULL
    ORDER BY board_id DESC
</select>
```

XML의 `resultType`은 **한 행을 담을 타입**이고, Mapper의 `List<BoardSelectDto>`는 그 DTO 여러 개를 받는 목록 타입이다.

### 질문: 게시글 3개가 조회되면 목록에는 무엇이 들어갈까?

DTO가 3개 들어간다.

```text
List<BoardSelectDto>
 ├ 첫 번째 게시글 DTO
 ├ 두 번째 게시글 DTO
 └ 세 번째 게시글 DTO
```

이 목록이 Mapper → 서비스 → 컨트롤러로 돌아가고, JSON 배열로 응답된다.

### 헷갈렸던 질문: Mapper가 숫자 3을 반환하는 것 아닐까?

**아니다. 이 메서드는 DTO 3개가 담긴 목록을 반환한다.**

수정·등록 Mapper가 행 수를 반환했다고 해서 모든 Mapper가 숫자를 반환하는 것은 아니다. 반환 타입과 연결된 SQL을 함께 봐야 한다.

```java
int updateBoard(BoardUpdateDto dto);
// 수정 SQL로 영향을 받은 행 수

List<BoardSelectDto> selectBoardList();
// 조회한 게시글 정보가 담긴 DTO 목록
```

숫자 3만으로는 게시글 제목이나 내용을 보여 줄 수 없다. 게시글 정보를 보여 주려면 그 정보가 들어 있는 DTO 목록이 필요하다.

## 6. 개수 조회는 숫자를 반환한다

```java
int selectBoardListCount(SearchCondition condition);
```

```xml
<select id="selectBoardListCount" resultType="int">
    SELECT COUNT(*)
    FROM board_table
    WHERE deleted_at IS NULL
</select>
```

`COUNT(*)`는 조건에 맞는 행이 몇 개인지 센다. 삭제되지 않은 글이 3개라면 숫자 3이 반환된다.

현재 코드에서는 개수 조회를 호출하던 서비스 코드가 주석 처리되어 있다. 목록을 조회한다고 이 개수 조회도 자동 실행되는 것은 아니다. 현재 SQL에는 SearchCondition을 사용하는 조건도 없다.

| 목적 | 반환 타입 | 결과 예시 |
|---|---|---|
| 게시글 한 개 조회 | `BoardSelectDto` | 제목·내용 등이 담긴 DTO 하나 |
| 게시글 목록 조회 | `List<BoardSelectDto>` | DTO 3개가 담긴 목록 |
| 게시글 개수 조회 | `int` | 숫자 3 |

### 마지막 질문: 제목과 내용을 보여 주려면 어떤 메서드가 필요할까?

학습자의 답: **“목록 가져오는 메서드”** → 맞다. 여러 게시글의 제목과 내용을 보여 주려면 DTO 목록이 필요하다. 개수 조회 결과에는 제목과 내용이 없다.

## 전체 흐름

```text
상세 조회
GET /boards/3
→ 컨트롤러: 번호 3을 받음
→ 서비스: 번호를 Mapper에 전달
→ Mapper: 삭제되지 않은 3번 글 조회
→ DTO 하나가 서비스와 컨트롤러로 돌아옴
→ JSON 객체 응답

목록 조회
GET /boards/list
→ 컨트롤러 → 서비스 → Mapper
→ 삭제되지 않은 글들을 번호 내림차순으로 조회
→ DTO 목록이 서비스와 컨트롤러로 돌아옴
→ JSON 배열 응답
```

## 안 보고 복습하기

1. GET /boards/3의 3은 어떤 어노테이션으로 받는가?
2. 상세 조회의 반환 타입은 무엇인가?
3. resultType은 무엇을 지정하는가?
4. DTO 목록과 숫자로 된 게시글 개수는 어떻게 다른가?
5. 글이 3개 조회되면 selectBoardList는 무엇을 반환하는가?
6. SELECT COUNT(*)는 제목과 내용도 반환하는가?
7. 목록 조회 시 개수 조회 메서드도 자동 실행되는가?
8. @ResponseBody가 있는 컨트롤러에서 DTO 목록은 어떤 응답 형태가 되는가?
