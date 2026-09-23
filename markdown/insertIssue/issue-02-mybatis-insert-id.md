# 이슈 02: MyBatis `<insert>`의 `id` 속성 오타

## 증상

Mapper 메서드와 XML의 SQL이 연결되지 않거나 Mapper XML 처리 중 오류가 발생한다.

## 원인

`<insert>` 태그에 존재하지 않는 `in` 속성을 사용했다.

```xml
<!-- 잘못된 코드 -->
<insert in="insert">
```

MyBatis는 `namespace + id`로 Mapper 메서드와 SQL을 연결한다.

```text
com.example.practice.insert.boardInsertMapper.insert
└──────────────────── namespace ────────────────┘ └ id ┘
```

## 해결

```xml
<mapper namespace="com.example.practice.insert.boardInsertMapper">
    <insert id="insert">
        <!-- SQL -->
    </insert>
</mapper>
```

Mapper 메서드명도 `insert`로 일치시킨다.

```java
@Mapper
interface boardInsertMapper {
    int insert(boardInsertDto dto);
}
```

## 확인

- [ ] `<insert id="insert">`로 작성했는가?
- [ ] XML의 `namespace`가 Mapper 인터페이스의 전체 경로와 같은가?
- [ ] XML의 `id`와 Mapper 메서드명이 같은가?

