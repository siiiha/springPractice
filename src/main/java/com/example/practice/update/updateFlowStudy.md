# 게시글 수정 흐름 복습

대화에서 수정 요청을 따라가며 풀었던 질문과 헷갈렸던 부분을 정리했다. 현재 코드를 읽으며 학습한 내용이며, 실제 DB 실행 검증 기록은 아니다.

## 1. 컨트롤러가 요청을 받는다

```http
POST /boards/3/edit
Content-Type: application/json
```

```json
{
  "title": "수정한 제목",
  "content": "수정한 내용"
}
```

```java
@PostMapping("/{boardId}/edit")
public Long edit(
        @PathVariable Long boardId,
        @RequestBody BoardUpdateDto boardDto) {
    boardDto.setBoardId(boardId);
    return boardUpdateService.updateBoard(boardDto);
}
```

클래스의 `@RequestMapping("/boards")`와 합쳐져 위 요청을 받는다.

### 질문: boardId에는 무엇이 들어갈까?

주소 경로의 `3`이 들어간다. `@PathVariable`은 경로에서 값을 받는다.

### 질문: @RequestBody로 받는 DTO에는 JSON이 들어갈까?

정확히는 JSON 원문 자체가 아니라 **JSON의 값들이 DTO 필드에 들어간다.**

```text
요청 본문을 받은 직후
boardDto
 ├ boardId = null
 ├ title = "수정한 제목"
 ├ content = "수정한 내용"
 └ category = null
```

### 질문: setBoardId는 왜 필요할까?

```java
boardDto.setBoardId(boardId);
```

URL에서 받은 게시글 번호 `3`을 DTO에 넣는다. JSON에 없는 번호를 채워 **어떤 글을 어떤 내용으로 수정할지** 한 객체에 담는다.

## 2. 컨트롤러가 서비스에 같은 객체를 전달한다

```java
return boardUpdateService.updateBoard(boardDto);
```

서비스는 다음 매개변수로 받는다.

```java
public Long updateBoard(BoardUpdateDto dto)
```

| 표현 | 의미 |
|---|---|
| `BoardUpdateDto` | 매개변수의 타입 |
| `dto` | 서비스 안에서 사용하는 매개변수 이름 |
| `boardDto` | 컨트롤러 안에서 사용하는 변수 이름 |

### 질문: boardDto와 dto는 같은 것일까?

두 변수는 **같은 DTO 객체를 가리킨다.** 매개변수 이름이 달라도 새 객체를 만드는 것은 아니다.

```text
컨트롤러의 boardDto → 같은 DTO 객체 ← 서비스의 dto
                     boardId = 3
                     title = "수정한 제목"
                     content = "수정한 내용"
```

서비스의 매개변수 이름을 `boardUpdateDto`로 정해도 된다. 그 경우 서비스 안에서 사용하는 이름도 일치시켜야 한다.

```java
public Long updateBoard(BoardUpdateDto boardUpdateDto) {
    boardUpdateMapper.updateBoard(boardUpdateDto);
    return boardUpdateDto.getBoardId();
}
```

## 3. 서비스는 호출된 메서드의 본문만 실행한다

```java
public Long updateBoard(BoardUpdateDto dto) {
    // 같은 DTO를 Mapper에 전달하고 수정 SQL을 실행한다.
    boardUpdateMapper.updateBoard(dto);

    // DTO의 게시글 번호를 호출한 컨트롤러에 반환하고 종료한다.
    return dto.getBoardId();
}
```

`updateBoard()`를 호출하면 해당 메서드의 중괄호 안을 위에서 아래로 실행한다. 파일 아래에 있는 다른 메서드까지 이어서 실행하지 않는다.

- `BoardUpdateService(...)` 생성자: 서비스 객체를 만들 때 Mapper를 전달받아 필드에 저장한다.
- `updateBoard(...)`: 수정 요청을 처리한다.
- `getBoardDetail(...)`: 별도로 호출할 때 기존 게시글을 조회한다.

수정 화면을 없애면서 서비스의 `getBoardDetail()`은 사용하지 않게 되었고, 현재 주석 처리되어 있다. Mapper 선언과 XML의 조회 SQL이 남아 있어도 수정 호출 시 자동으로 실행되지는 않는다.

## 4. Mapper와 XML을 연결한다

```java
int updateBoard(BoardUpdateDto dto);
```

이 선언은 **DTO를 받아 정수를 반환하는 메서드**라는 뜻이다. 실행 구현은 MyBatis가 맡는다.

```xml
<mapper namespace="com.example.practice.update.BoardUpdateMapper">
    <update id="updateBoard"
            parameterType="com.example.practice.update.BoardUpdateDto">
        UPDATE board_table
        <set>
            <if test="title != null">title = #{title},</if>
            <if test="category != null">category = #{category},</if>
            <if test="content != null">content = #{content},</if>
        </set>
        WHERE board_id = #{boardId}
          AND deleted_at IS NULL
    </update>
</mapper>
```

`namespace`는 Mapper 인터페이스를, `id`는 그 메서드를 연결한다.

### 질문: int면 무조건 수정된 행 수일까?

아니다. **타입은 값의 종류를, 연결된 SQL은 값의 의미를 알려 준다.**

- 현재 `<update>`와 연결된 `int updateBoard(...)`: SQL 실행으로 영향을 받은 행 수.
- `SELECT COUNT(*)`와 연결된 `int` 조회 메서드: 조회한 개수.
- DTO를 반환하는 조회 메서드: 조회된 객체.

## 5. 값이 있는 항목만 SQL에 포함한다

### 질문: title 조건은 포함될까?

```xml
<if test="title != null">title = #{title},</if>
```

`title`에 `"수정한 제목"`이 있으므로 포함된다. `#{title}`에 DTO의 제목 값이 바인딩된다.

### 질문: category가 null이면 기존 값은 어떻게 될까?

카테고리 수정 구문은 포함되지 않는다. 따라서 **기존 카테고리는 유지된다.**

`content`도 값이 있으므로, 이번 요청에서는 제목과 내용이 수정 대상이다.

```sql
UPDATE board_table
SET title = #{title},
    content = #{content}
WHERE board_id = #{boardId}
  AND deleted_at IS NULL
```

`<set>`은 필요한 `SET`을 붙이고 마지막 쉼표를 제거한다. 세 항목이 전부 null이면 정상적인 수정 SQL이 만들어지지 않으므로, 현재 연습 요청에는 최소 하나의 수정 값을 넣는다.

### 질문: WHERE는 어떤 글을 지정할까?

`boardId = 3`이라면 **3번 게시글이면서 삭제되지 않은 글**을 수정한다. `deleted_at IS NULL` 조건까지 함께 확인해야 한다.

## 6. 반환값 두 개를 구분한다

가장 헷갈렸던 부분은 Mapper의 반환값과 서비스의 반환값이었다.

```java
public Long updateBoard(BoardUpdateDto dto) {
    boardUpdateMapper.updateBoard(dto);
    return dto.getBoardId();
}
```

### 질문: return은 하나뿐인데 Mapper도 반환한다고?

서비스 코드의 `return`은 하나지만, 서비스가 **호출한 Mapper 메서드도 자신의 반환값이 있다.** Mapper 선언의 `int`가 그 반환 타입이다. MyBatis가 SQL을 실행하고 그 결과를 호출 자리로 돌려준다.

현재 서비스는 Mapper 반환값을 저장하거나 검사하지 않는다.

```java
boardUpdateMapper.updateBoard(dto); // 실행하지만 반환값은 사용하지 않음
```

반환값을 변수에 담고 싶다면 다음처럼 작성할 수 있다. 아래는 설명용 예시이며 현재 코드에는 이 변수가 없다.

```java
int updatedCount = boardUpdateMapper.updateBoard(dto);
```

여기서 '저장'은 **반환값을 Java 변수에 담는 것**이다. DB 수정 여부와는 별개이며, 변수에 받지 않아도 SQL은 실행된다.

### 질문: 서비스의 return은 어디로 돌아갈까?

```java
return dto.getBoardId();
```

서비스를 호출한 컨트롤러의 다음 호출 자리로 게시글 번호가 돌아간다.

```java
return boardUpdateService.updateBoard(boardDto);
```

번호가 3이면 컨트롤러는 의미상 `return 3L;`을 수행한다. `@RestController`이므로 최종 응답 본문으로 숫자 3을 보낸다.

**return은 호출한 곳에 값을 돌려주고 현재 메서드를 종료한다.** 값을 반드시 중간 변수에 저장해야 반환할 수 있는 것은 아니다.

### 마지막 복습 문제

10번 게시글 한 행이 수정되었다면?

| 반환하는 곳 | 의미 | 값 |
|---|---|---|
| Mapper | 수정 SQL로 영향을 받은 행 수 | `1` |
| 서비스 | DTO에 담긴 게시글 번호 | `10` |
| 컨트롤러 | 서비스에서 받은 번호를 응답 | `10` |

학습자가 마지막에 답한 내용: **“맵퍼는 1을 반환하고 서비스는 10을 반환해.”**

현재 서비스는 Mapper의 행 수를 검사하지 않는다. 따라서 행 수가 0이어도 예외 없이 다음 줄로 진행하면 게시글 번호를 반환한다. 번호 응답만으로 실제 수정 성공을 확인할 수는 없다.

## 전체 흐름

```text
POST /boards/3/edit + 제목·내용 JSON
    ↓
컨트롤러: JSON 값을 DTO로 받고 URL의 번호 3을 DTO에 설정
    ↓
서비스: 같은 DTO를 Mapper에 전달
    ↓
Mapper: XML의 updateBoard와 연결
    ↓
SQL: 삭제되지 않은 3번 글의 제목·내용 수정, 카테고리 유지
    ↓
Mapper → 서비스: 행 수 반환 (이번 예시: 1, 현재 사용하지 않음)
    ↓
서비스 → 컨트롤러: DTO의 게시글 번호 3 반환
    ↓
컨트롤러 → 클라이언트: 응답 본문 3
```

## 안 보고 복습하기

1. @PathVariable과 @RequestBody는 각각 어디서 값을 가져오는가?
2. boardDto와 dto라는 이름이 달라도 같은 객체를 전달할 수 있는가?
3. updateBoard를 호출하면 바로 아래 getBoardDetail도 실행되는가?
4. category가 null이면 SQL과 기존 카테고리는 어떻게 되는가?
5. int만 보고 반환값의 의미까지 알 수 있는가?
6. Mapper 반환값을 저장하는 코드와 사용하지 않는 코드의 차이는?
7. return dto.getBoardId()의 값은 어디로 돌아가는가?
8. 10번 게시글 한 행 수정 시 Mapper와 서비스의 반환값은 각각 무엇인가?
