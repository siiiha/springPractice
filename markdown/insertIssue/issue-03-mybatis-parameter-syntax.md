# 이슈 03: MyBatis 파라미터 표기 오타

## 증상

SQL 실행 과정에서 파라미터 처리 또는 SQL 문법 오류가 발생한다.

## 원인

`content` 파라미터의 중괄호와 `#` 순서를 반대로 작성했다.

```xml
<!-- 잘못된 코드 -->
{#content}
```

MyBatis의 일반적인 값 바인딩 문법은 `#{필드명}`이다.

## 해결

```xml
VALUES (#{category}, #{title}, #{writer}, #{content})
```

`#{content}`의 `content`는 DTO의 필드명 또는 getter 이름과 일치해야 한다.

```java
private String content;
```

## 확인

- [ ] 모든 파라미터가 `#{필드명}` 형태인가?
- [ ] XML에서 사용한 이름이 DTO 필드명과 같은가?
- [ ] 오타를 수정한 뒤 애플리케이션을 재시작했는가?

