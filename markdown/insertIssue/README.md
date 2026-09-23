# 게시글 작성 API 이슈 목록

`POST /board/write` 구현과 테스트 중 발생한 이슈를 발생 순서대로 정리한다.

## 빠른 찾기

| 번호 | 오류 또는 증상 | 핵심 원인 | 문서 |
|---|---|---|---|
| 01 | `415 Unsupported Media Type` | 폼 데이터 전송과 `@RequestBody`의 JSON 형식 불일치 | [이슈 01](issue-01-form-data-and-request-body.md) |
| 02 | Mapper SQL을 찾거나 실행하지 못함 | `<insert>`에 `in` 속성을 사용함 | [이슈 02](issue-02-mybatis-insert-id.md) |
| 03 | SQL 파라미터 처리 실패 | `{#content}` 오타 | [이슈 03](issue-03-mybatis-parameter-syntax.md) |
| 04 | PK 입력 및 반환 문제 | `AUTO_INCREMENT` 컬럼을 직접 INSERT함 | [이슈 04](issue-04-auto-increment-primary-key.md) |
| 05 | `Table ... doesn't exist` | Mapper의 테이블명과 실제 테이블명이 다름 | [이슈 05](issue-05-table-name-mismatch.md) |
| 06 | `Unknown column 'writer'` | INSERT 컬럼과 실제 테이블 컬럼이 다름 | [이슈 06](issue-06-writer-column-missing.md) |
| 07 | `member_id doesn't have a default value` | 필수 컬럼이 INSERT 요청에서 빠짐 | [이슈 07](issue-07-member-id-required.md) |

## 오류가 발생한 위치

```text
클라이언트
  └─ 이슈 01: 요청 Content-Type 불일치
       ↓
Controller: JSON → DTO
       ↓
Service
       ↓
MyBatis Mapper
  ├─ 이슈 02: statement id 오타
  ├─ 이슈 03: 파라미터 표기 오타
  └─ 이슈 04: 자동 생성 PK 처리
       ↓
Database
  ├─ 이슈 05: 테이블명 불일치
  ├─ 이슈 06: writer 컬럼 없음
  └─ 이슈 07: member_id 값 누락
```

## 상태 코드 해석

- `415`: 서버가 요청 본문의 형식을 지원하지 않는다. `Content-Type`과 Controller 선언을 먼저 확인한다.
- `500`: 요청은 서버에 도착했지만 내부 처리에 실패했다. 실행 로그의 첫 번째 `Caused by:`를 확인한다.

