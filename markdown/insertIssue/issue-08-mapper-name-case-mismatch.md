# 이슈 08: Mapper 이름의 대소문자 불일치

## 증상

애플리케이션 시작 중 다음 오류가 발생했다.

```text
NoClassDefFoundError:
com/example/practice/insert/BoardInsertMapper
(wrong name: com/example/practice/insert/boardInsertMapper)
```

## 원인

Java의 Mapper 인터페이스는 대문자로 시작하도록 변경했지만, Mapper XML 또는 이전 빌드 결과에는 소문자 이름이 남아 있었다.

```java
interface BoardInsertMapper {
}
```

```xml
<!-- 잘못된 namespace -->
<mapper namespace="com.example.practice.insert.boardInsertMapper">
```

Java는 클래스 이름의 대소문자를 구분한다. `BoardInsertMapper`와 `boardInsertMapper`는 서로 다른 이름이다.

## 해결

XML의 namespace를 Java 인터페이스 전체 이름과 정확히 일치시킨다.

```xml
<mapper namespace="com.example.practice.insert.BoardInsertMapper">
```

대소문자만 바꾼 경우 이전 `.class` 파일이 남을 수 있으므로 깨끗하게 다시 빌드한다.

```powershell
.\gradlew.bat clean build
```

## 확인

- [ ] Java 인터페이스명이 `BoardInsertMapper`인가?
- [ ] XML namespace의 마지막 이름도 `BoardInsertMapper`인가?
- [ ] 패키지 경로까지 완전히 같은가?
- [ ] `clean build` 후 서버를 다시 실행했는가?

