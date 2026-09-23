# 이슈 02: Service를 클래스명으로 호출함

## 증상

Controller의 다음 호출 부분이 빨간색으로 표시된다.

```java
return SelectService.selectDetail(boardId);
```

## 원인

`selectDetail()`은 `static` 메서드가 아닌 인스턴스 메서드다. 따라서 클래스명 `SelectService`로 직접 호출할 수 없다.

Controller에는 Spring이 주입한 `selectService` 객체가 이미 있다.

```java
@Autowired
SelectService selectService;
```

Java는 대소문자를 구분한다.

```text
SelectService  → 클래스 이름
selectService  → 주입받은 객체 변수
```

## 해결

주입받은 객체 변수로 호출한다.

```java
@GetMapping("/{boardId}")
public SelectDto selectDetail(@PathVariable Long boardId) {
    return selectService.selectDetail(boardId);
}
```

## 확인

- [ ] `SelectService`가 아니라 `selectService`로 호출했는가?
- [ ] `SelectService`에 `@Service`가 붙어 있는가?
- [ ] Controller에 `SelectService`가 주입되어 있는가?

