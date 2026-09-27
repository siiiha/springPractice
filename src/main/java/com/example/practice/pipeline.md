# 기본 CRUD 흐름을 연습

## 학습 원칙

- AI에 바로 의존하지 않고 먼저 생각한다.
- 필요한 정보를 검색하거나 학습하는 용도로 사용한다.
- 약 10~15분 고민한 뒤 막히면 질문한다.

## 1. 필요한 데이터
- 제목(`title`)
- 카테고리(`category`)
- 본문(`content`)
- 작성자(`writer`)
- 멤버아이디(`memberId`)
- 작성일(`createdAt`)
- 삭제일(`deleteAt`)

## 2. 스키마 설계
```
board_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
category    VARCHAR(50)  NOT NULL,
title       VARCHAR(200) NOT NULL,
writer      VARCHAR(50)  NOT NULL,
content     TEXT         NOT NULL,
created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

## 3. 계층형 아키텍쳐

```
Client
  ↓ HTTP
Controller
  ↓ DTO
Service
  ↓ DTO
Mapper
  ↓
Mapper.xml
  ↓
DB (SQL)
  ↓
Mapper → Service → Controller
```


## 5. CRUD 구현 순서
1. Create : 게시글이 없으면 조회, 수정, 삭제가 불가능함.
2. Read : 글 조회가 가능해야 수정, 삭제를 할 수 있음.
3. Update : 글쓰기와 유사한 기능. 이용하는 순서상 삭제보다 수정이 먼저
4. Delete : 깔끔하게 삭제로 끝내자구

## 6. 코드 작성 순서
- DTO > Mapper interface > Mapper.xml > 
1. Controller : 요청 받을 주소, JSON데이터를 DTO로 받는 부분, 받은 DTO를 Service에 전달하는 부분
   - 어떤 HTTP 메서드를 사용할 것인가?
   - 요청 주소는 무엇인가?
   - 어떤 DTO를 받을 것인가?
   - 성공시 반환하는 것은 무엇인가?
2. Service : Controller의 요청을 받음, 비즈니스 로직 처리, Mapper를 호출함
3. DTO : 데이터를 담음
   - 사용자가 보내야하는 값
   - 서버나 DB가 자동으로 만드는 값
     - `created_at`
   - 반드시 필요한 값
   - 없어도 되는 값
3. Mapper : 반환값, 기능 이름 (입력값)
4. 데이터베이스 저장 확인

## 7. 할 일
- [✓] insert pipeline
- [✓] Board Insert
- [✓] select pipeline
- [✓] Board Select Detail
- [ ] Board Select List
- [✓] update pipeline
- [ ] Board Update
- [ ] delete pipeline
- [ ] Board Delete



