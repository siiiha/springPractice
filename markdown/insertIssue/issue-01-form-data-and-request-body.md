# 이슈 01: 폼 데이터와 `@RequestBody` 형식 불일치

## 증상

```text
415 Unsupported Media Type
```

`curl -F`로 폼 데이터를 보냈지만 Controller는 `@RequestBody`로 JSON을 기대했다.

## 원인

| 요청 방식 | Content-Type | Spring에서 받는 방식 |
|---|---|---|
| JSON | `application/json` | `@RequestBody` |
| `curl -F` | `multipart/form-data` | `@ModelAttribute`, `@RequestPart` |
| 일반 HTML 폼 | `application/x-www-form-urlencoded` | `@ModelAttribute` |

화면에 입력 폼이 있는지보다 실제 요청의 `Content-Type`이 중요하다.

## 해결

Controller의 `@RequestBody`를 유지하고 JSON으로 전송한다.

```java
@PostMapping("/write")
Long boardInsert(@RequestBody boardInsertDto dto) {
    return boardInsertService.board(dto);
}
```

```powershell
$body = @{
    memberId = 1
    category = "공지"
    title    = "첫 글"
    writer   = "juhyo"
    content  = "안녕하세요"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method POST `
  -Uri "http://localhost:8080/board/write" `
  -ContentType "application/json; charset=utf-8" `
  -Body $body
```

폼 데이터 자체를 받고 싶다면 `@RequestBody` 대신 `@ModelAttribute`를 사용한다.

## 확인

- [ ] 요청의 `Content-Type`이 `application/json`인가?
- [ ] Controller 파라미터에 `@RequestBody`가 있는가?
- [ ] JSON 속성명과 DTO 필드명이 같은가?

