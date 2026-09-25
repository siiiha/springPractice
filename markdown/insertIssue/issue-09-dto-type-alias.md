# 이슈 09: `BoardInsertDto` 타입 별칭을 찾지 못함

## 증상

애플리케이션 시작 중 `SqlSessionFactory` 생성에 실패했다.

```text
Failed to parse mapping resource: InsertMapper.xml
Could not resolve type alias 'BoardInsertDto'
ClassNotFoundException: Cannot find class: BoardInsertDto
```

상단에는 `boardInsertService`, `boardInsertMapper`, `sqlSessionTemplate` 생성 실패가 함께 표시됐지만 실제 원인은 가장 아래쪽의 타입 별칭 오류였다.

## 원인

Mapper XML에서 DTO의 단축 이름을 사용했지만 MyBatis에 해당 별칭을 등록하지 않았다.

```xml
<insert id="insert"
        parameterType="BoardInsertDto">
```

MyBatis는 설정 없이 `BoardInsertDto`라는 이름만 보고 실제 패키지 위치를 알 수 없다.

## 해결

DTO의 클래스명만 `parameterType`에 사용하려면 `application.properties`에 타입 별칭을 검색할 패키지를 등록한다.

```properties
mybatis.type-aliases-package=com.example.practice.insert
```

MyBatis는 지정한 패키지에서 `BoardInsertDto`를 찾아 클래스명을 타입 별칭으로 등록한다. 그러면 Mapper XML에서 전체 패키지 경로 없이 다음처럼 사용할 수 있다.

```xml
<insert id="insert"
        parameterType="BoardInsertDto"
        useGeneratedKeys="true"
        keyProperty="boardId">
    INSERT INTO board_table (member_id, category, title, writer, content)
    VALUES (#{memberId}, #{category}, #{title}, #{writer}, #{content})
</insert>
```

처리 과정은 다음과 같다.

```text
com.example.practice.insert 패키지 검색
    ↓
BoardInsertDto 클래스 발견
    ↓
"BoardInsertDto" 타입 별칭 등록
    ↓
parameterType="BoardInsertDto"와 연결
```

패키지는 와일드카드인 `com.example.practice.*`보다 DTO가 실제로 들어 있는 `com.example.practice.insert`로 명확하게 지정한다.

다른 해결 방법으로는 `parameterType`을 제거하거나 클래스의 전체 경로를 사용할 수도 있다.

```
xml
parameterType="com.example.practice.insert.BoardInsertDto"
```

## 오류 읽는 순서

```text
Service 생성 실패
    ↓
Mapper 생성 실패
    ↓
SqlSessionFactory 생성 실패
    ↓
InsertMapper.xml 파싱 실패
    ↓
BoardInsertDto 타입을 찾지 못함 ← 실제 원인
```

## 확인

- [ ] `application.properties`에 `mybatis.type-aliases-package=com.example.practice.insert`를 추가했는가?
- [ ] 설정한 패키지에 `BoardInsertDto`가 실제로 존재하는가?
- [ ] Mapper XML에서 `parameterType="BoardInsertDto"`를 사용했는가?
- [ ] Mapper XML이 정상적으로 파싱되는가?
- [ ] 설정을 변경한 뒤 애플리케이션을 완전히 재시작했는가?
- [ ] 서버 시작 로그에 `Started PracticeApplication`이 출력되는가?
