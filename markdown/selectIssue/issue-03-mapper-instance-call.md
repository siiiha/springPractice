# 이슈 03: Mapper를 인터페이스명으로 호출함

## 증상

Service의 다음 호출 부분이 빨간색으로 표시된다.

```java
return SelectMapper.selectDetail(boardId);
```

## 원인

`SelectMapper`는 인터페이스 이름이다. `selectDetail()`은 `static` 메서드가 아니므로 인터페이스명으로 직접 호출할 수 없다.

Service에는 MyBatis가 생성하여 Spring이 주입한 `selectMapper` 객체가 있다.

```java
@Autowired
SelectMapper selectMapper;
```

```text
SelectMapper  → Mapper 인터페이스 이름
selectMapper  → 실제로 SQL을 실행하는 주입 객체
```

## 해결

주입받은 Mapper 객체로 호출한다.

```java
@Service
class SelectService {

    @Autowired
    SelectMapper selectMapper;

    public SelectDto selectDetail(Long boardId) {
        return selectMapper.selectDetail(boardId);
    }
}
```

Mapper 인터페이스에는 호출할 메서드를 선언한다.

```java
@Mapper
interface SelectMapper {
    SelectDto selectDetail(Long boardId);
}
```

## 확인

- [ ] `SelectMapper`가 아니라 `selectMapper`로 호출했는가?
- [ ] Mapper 인터페이스에 `@Mapper`가 붙어 있는가?
- [ ] Mapper 메서드명과 XML의 `select id`가 같은가?
- [ ] Mapper XML의 `namespace`가 인터페이스 전체 경로와 같은가?
