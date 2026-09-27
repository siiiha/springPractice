# 삭제 요청 흐름 복습

현재 `BoardDeleteController.java`와 `deleteMapper.xml`을 기준으로, 대화에서 풀었던 질문과 답을 정리했다. 실제 DB 실행 결과가 아니라 코드의 동작 흐름을 따라간 학습 기록이다.

## 1. 요청이 컨트롤러에 도착한다

예시 요청:

```http
DELETE /boards/3?memberId=1
```

```java
@RestController
@RequestMapping("/boards")
public class BoardDeleteController {
    // 필드와 생성자는 생략

    @DeleteMapping("/{boardId}")
    public void deleteBoard(
            @PathVariable Long boardId,
            @RequestParam Long memberId) {
        boardDeleteService.deleteBoard(boardId, memberId);
    }
}
```

### 질문: 왜 이 메서드가 실행될까?

요청의 **HTTP 방식과 주소 패턴이 모두 맞기 때문**이다.

- 클래스의 `@RequestMapping("/boards")`와 메서드의 `@DeleteMapping("/{boardId}")`가 합쳐진다.
- 따라서 DELETE 방식의 `/boards/3` 요청이 이 메서드와 연결된다.
- `/{boardId}`는 숫자 3으로 고정된 주소가 아니다. `/boards/10`으로 요청하면 10을 받는다.
- 같은 주소라도 GET이나 POST 요청이면 이 DELETE 메서드는 실행되지 않는다.

### 질문: 매개변수에는 어떤 값이 들어갈까?

| 코드 | 값을 가져오는 곳 | 예시 값 |
|---|---|---|
| `@PathVariable Long boardId` | 주소 경로 `/boards/3` | `3` |
| `@RequestParam Long memberId` | 쿼리 파라미터 `?memberId=1` | `1` |

### 질문: 마지막 줄은 어디로 값을 전달할까?

서비스 객체의 `deleteBoard()`를 호출하면서 두 값을 전달한다.

```java
boardDeleteService.deleteBoard(boardId, memberId);
```

이번 요청에서는 의미상 `deleteBoard(3L, 1L)`을 호출한다. Java의 `L`은 `long` 정수 리터럴을 나타낸다.

## 2. 서비스가 DTO에 값을 담는다

```java
public void deleteBoard(Long boardId, Long memberId) {
    BoardDeleteDto boardDeleteDto = new BoardDeleteDto();
    boardDeleteDto.setBoardId(boardId);
    boardDeleteDto.setMemberId(memberId);

    boardDeleteMapper.deleteBoard(boardDeleteDto);
}
```

### 질문: DTO에는 어떤 값이 담길까?

```text
boardDeleteDto
 ├ boardId = 3
 └ memberId = 1
```

실제 DTO에는 다른 필드도 있지만, 이 삭제 흐름에서는 두 필드만 설정한다.

### 질문: 마지막 줄은 무엇을 할까?

`boardDeleteMapper`의 `deleteBoard()` 메서드에 **두 값이 담긴 DTO 객체 하나**를 전달한다. DTO에 값을 넣는 것만으로 SQL이 실행되지는 않는다. Mapper 호출까지 해야 한다.

## 3. Mapper 메서드가 XML의 SQL과 연결된다

```java
@Mapper
interface BoardDeleteMapper {
    int deleteBoard(BoardDeleteDto boardDeleteDto);
}
```

```xml
<mapper namespace="com.example.practice.delete.BoardDeleteMapper">
    <update id="deleteBoard"
            parameterType="com.example.practice.delete.BoardDeleteDto">
        UPDATE board_table
        SET deleted_at = NOW()
        WHERE board_id = #{boardId}
          AND member_id = #{memberId}
          AND deleted_at IS NULL
    </update>
</mapper>
```

- `namespace`: 연결할 Mapper 인터페이스의 전체 이름이다.
- `id="deleteBoard"`: Mapper 메서드 이름과 일치해야 한다.
- `parameterType`: 전달받는 DTO의 타입이다.

서비스 메서드와 Mapper 메서드의 이름이 같아서 자동으로 연결되는 것은 아니다. 서비스가 `boardDeleteMapper.deleteBoard(...)`를 직접 호출한다. 그다음 MyBatis가 Mapper 메서드와 XML을 연결한다.

### 질문: `#{boardId}`와 `#{memberId}`에는 무엇이 들어갈까?

처음에는 `board_id`, `member_id`라고 답했지만, **DB 컬럼 이름과 DTO에서 가져오는 값을 구분해야 한다.**

```sql
WHERE board_id = #{boardId}
  AND member_id = #{memberId}
```

| 표현 | 의미 |
|---|---|
| `board_id`, `member_id` | DB 컬럼 이름 |
| `#{boardId}`, `#{memberId}` | DTO의 해당 속성에서 가져와 바인딩하는 값 |

이번 요청은 의미상 다음 조건으로 실행된다. 아래 숫자는 이해를 위한 예시이며, 실제로는 MyBatis가 파라미터를 바인딩한다.

```sql
WHERE board_id = 3
  AND member_id = 1
  AND deleted_at IS NULL
```

즉, **3번 게시글이면서 작성자 번호가 1이고, 아직 삭제되지 않은 행**을 대상으로 한다.

## 4. DB에서 소프트 삭제를 한다

### 질문: 행을 완전히 지울까, 값을 바꿀까?

```sql
SET deleted_at = NOW()
```

행을 완전히 지우는 하드 삭제가 아니라, **삭제 시간에 현재 시간을 기록하는 소프트 삭제(논리 삭제)**다. 따라서 XML에서도 `<delete>`가 아닌 `<update>`를 사용한다.

| 상태 | `deleted_at` |
|---|---|
| 삭제 전 | `NULL` |
| 삭제 후 | 삭제 처리한 시간 |

DB에는 `deleted_at` 컬럼이 있어야 한다.

## 5. 변경된 행 수가 돌아온다

```java
int deleteBoard(BoardDeleteDto boardDeleteDto);
```

### 질문: 한 행이 삭제 처리됐다면 무엇을 반환할까?

**`1`을 반환한다.** 반환값은 게시글 번호가 아니라 SQL로 변경된 행의 개수다.

- `1`: 한 행에 삭제 시간을 기록했다.
- `0`: 조건에 맞는 행이 없어 변경하지 못했다. 게시글이 없거나, 회원 번호가 다르거나, 이미 삭제된 경우 등이 해당한다.
- SQL 실행 자체에 오류가 나면 일반적으로 예외가 발생한다. 모든 실패가 `0`으로 반환되는 것은 아니다.

현재 서비스는 이 값을 변수에 저장하거나 검사하지 않는다.

```java
boardDeleteMapper.deleteBoard(boardDeleteDto);
```

서비스와 컨트롤러의 반환 타입은 모두 `void`다. 따라서 Mapper가 반환한 `1`을 사용자에게 전달하지 않는다. 정상 종료되면 컨트롤러는 응답 본문 없이 요청을 마친다. 현재 코드는 `0`이어도 별도로 실패 응답을 만들지 않는다.

## 6. 목록에서 삭제한 글이 제외된다

### 질문: 삭제한 글이 목록에서 안 보이는 이유는?

조회 SQL에 다음 조건이 있기 때문이다.

```sql
WHERE deleted_at IS NULL
```

삭제 전에는 `NULL`이라 조회되지만, 삭제 후에는 시간이 들어 있어 조회에서 제외된다. 정확한 컬럼 이름은 `delete_at`이 아닌 **`deleted_at`**이다.

이미 다른 조건이 있다면 `AND`로 추가한다.

```sql
WHERE board_id = #{boardId}
  AND deleted_at IS NULL
```

**삭제 SQL이 시간을 기록하고, 조회 SQL이 삭제 시간이 없는 글만 가져오는 구조**다.

## 전체 흐름

```text
DELETE /boards/3?memberId=1
    ↓
컨트롤러: boardId=3, memberId=1을 받음
    ↓
서비스: 두 값을 DTO에 담음
    ↓
Mapper: DTO를 받아 XML의 deleteBoard SQL과 연결
    ↓
DB: 조건에 맞는 행의 deleted_at에 현재 시간 기록
    ↓
Mapper: 변경된 행 수 반환 (예: 1)
    ↓
서비스: 현재는 반환값을 검사하지 않고 종료
    ↓
컨트롤러: 응답 본문 없이 종료
```

이후 목록 조회에서는 `deleted_at IS NULL` 조건 때문에 삭제한 글이 제외된다.

## 현재 연습 코드의 범위

`memberId`는 요청에서 받은 값이다. SQL이 회원 번호까지 비교하더라도, 요청한 사람이 실제 그 회원인지 인증한 것은 아니다. 로그인 기능을 연결할 때는 로그인 세션 등 서버에서 확인한 사용자 정보를 사용해야 한다. 현재는 기본 CRUD 전달 흐름을 연습하는 단계다.

## 안 보고 복습할 질문

1. DELETE와 GET이 같은 주소로 요청되면 같은 메서드가 실행될까?
2. `@PathVariable`과 `@RequestParam`은 각각 어디서 값을 가져올까?
3. 서비스에서 DTO에 값을 넣은 뒤 어떤 호출을 해야 SQL이 실행될까?
4. XML의 `namespace`와 `id`는 각각 무엇과 연결될까?
5. `board_id`와 `#{boardId}`는 어떻게 다를까?
6. Mapper가 반환하는 `1`은 게시글 번호일까, 변경된 행 수일까?
7. 현재 서비스는 그 반환값을 사용하는가?
8. 삭제한 글이 조회되지 않는 이유는 무엇일까?
