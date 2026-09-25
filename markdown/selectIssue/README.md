# 게시글 조회 API 이슈 목록

`GET /list/{boardId}` 구현 중 발생한 컴파일 오류를 원인별로 정리한다.

## 빠른 찾기

| 번호 | 오류 또는 증상 | 핵심 원인 | 문서 |
|---|---|---|---|
| 01 | `cannot find symbol: SelectDto` | Controller의 닫는 중괄호 위치가 잘못되어 DTO가 내부 클래스가 됨 | [이슈 01](issue-01-select-dto-scope.md) |
| 02 | Service 메서드 호출 부분이 빨간색 | 클래스명 `SelectService`로 인스턴스 메서드를 호출함 | [이슈 02](issue-02-service-instance-call.md) |
| 03 | Mapper 메서드 호출 부분이 빨간색 | 인터페이스명 `SelectMapper`로 인스턴스 메서드를 호출함 | [이슈 03](issue-03-mapper-instance-call.md) |
| 04 | `Could not resolve type alias 'SelectDto'` 가능성 | 등록하지 않은 단축 이름을 `resultType`에 사용함 | [이슈 04](issue-04-result-type-alias.md) |

## 정상적인 조회 흐름

```text
GET /list/1
    ↓
Controller: boardId를 받음
    ↓ selectService.selectDetail(boardId)
Service
    ↓ selectMapper.selectDetail(boardId)
Mapper XML
    ├─ 이슈 04: SELECT 결과를 SelectDto로 변환
    ↓ SELECT ... WHERE board_id = 1
Database
    ↓ 조회 결과
SelectDto
    ↓
Controller가 JSON으로 응답
```

## 객체 연결 관계

```text
Controller가 주입받은 객체: selectService
Service가 주입받은 객체:    selectMapper
```

메서드를 호출할 때 클래스명이나 인터페이스명이 아니라, Spring이 주입한 객체 변수를 사용한다.

현재 `resultType="SelectDto"`도 별칭 등록 없이 사용하고 있어, Insert Mapper 문제를 고친 다음 동일한 타입 해석 오류가 발생할 수 있다.

