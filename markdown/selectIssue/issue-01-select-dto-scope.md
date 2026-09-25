# 이슈 01: `SelectDto`를 찾을 수 없음

## 증상

```text
cannot find symbol
symbol: class SelectDto
location: class SelectService
```

`SelectService`와 `SelectMapper`에서 `SelectDto` 부분이 빨간색으로 표시되고 컴파일되지 않았다.

## 원인

Controller를 닫는 중괄호가 DTO 뒤에 있어서 `SelectDto`가 의도치 않게 `Select`의 내부 클래스가 됐다.

```java
public class Select {
    // Controller 코드

    class SelectDto {
        // Select 내부 클래스가 됨
    }
}

class SelectService {
    // 외부에 있어서 SelectDto를 직접 찾지 못함
}
```

## 해결

Controller 메서드 다음에서 `Select` 클래스를 닫고 DTO를 별도 클래스로 분리한다.

```java
@RestController
@RequestMapping("/list")
public class Select {

    @Autowired
    SelectService selectService;

    @GetMapping("/{boardId}")
    public SelectDto selectDetail(@PathVariable Long boardId) {
        return selectService.selectDetail(boardId);
    }
} // Controller는 여기서 끝

@Getter
@Setter
class SelectDto {
    private Long boardId;
    private String title;
    private String content;
}
```

## 확인

- [ ] `Select` Controller가 DTO 선언 전에 닫혔는가?
- [ ] `SelectDto`와 `SelectService`가 같은 패키지에 있는가?
- [ ] 중괄호의 시작과 끝이 올바르게 짝지어졌는가?
- [ ] `compileJava`가 성공하는가?

