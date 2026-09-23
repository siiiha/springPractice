# 게시판 조회 파이프라인

## 학습 원칙
- AI에 바로 의존하지 않고 먼저 생각한다.
- 필요한 정보를 검색하거나 학습하는 용도로 사용한다.
- 약 10~15분 고민한 뒤 막히면 질문한다.

## 1. 게시글 조회에 필요한 데이터
- 제목(`title`)
- 카테고리(`category`)
- 본문(`content`) : 목록 조회의 경우 불필요
- 작성자(`writer`)
- 작성일(`createdAt`)

## 3. 요청 데이터 처리
- 목록 조회용/상세 페이지용 DTO분리
  - 필요 데이터와 Getter, Setter가 용도에 따라 다르다.
  - 
- @PageableDefault
  - size : 한 페이지에 나오는 게시글의 수
  - sort : 정렬의 기준
  - direction : 오름차순? 내림차순?
  - Pageable pageable : 변수 선언

## 4. Insert와의 차이

```
브라우저가 boardId 요청
        ↓
Controller가 boardId를 받음
        ↓
Service가 boardId를 Mapper에 전달
        ↓
Mapper가 DB에서 SELECT
        ↓
조회 결과를 DTO로 변환
        ↓
Service → Controller → 브라우저로 DTO 반환
```


## 5. CRUD 구현 순서
1. Create : 게시글이 없으면 조회, 수정, 삭제가 불가능함.
2. Read : 글 조회가 가능해야 수정, 삭제를 할 수 있음.
3. Update : 글쓰기와 유사한 기능. 이용하는 순서상 삭제보다 수정이 먼저
4. Delete : 깔끔하게 삭제로 끝내자구

## 6. 코드 작성 순서
- DTO > Controller > Service > Mapper interface > Mapper.xml
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
- [✓] 스키마 설계
- [ ] DTO 작성
- [ ] Controller 작성
- [ ] Service 작성
- [ ] Mapper 작성
- [ ] 등록 테스트



