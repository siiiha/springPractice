package com.example.practice.select;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.List;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/boards")
public class BoardSelectController {
    private final BoardSelectService boardSelectService;

    public BoardSelectController(BoardSelectService boardSelectService) {
        this.boardSelectService = boardSelectService;
    }

    // 게시판 전체 조회
    @GetMapping("/list")
    public String getBoardList(Model model) {
        List<BoardSelectDto> boardList = boardSelectService.getBoardList();
        model.addAttribute("selectList", boardList);
//        model.addAttribute("boardinfo", result.getPageList());
//        model.addAttribute("condition", condition);

        return "boards/list";
    }

    //게시판 상세 조회
    @GetMapping("/{boardId}")
    @ResponseBody
    public BoardSelectDto getBoardDetail(@PathVariable Long boardId) {
        System.out.println("요청들어옴!");
        return boardSelectService.getBoardDetail(boardId);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class BoardSelectDto {
    private Long boardId;
    private Long memberId;  //작성자
    private String category;
    private String title;
    private String content;
    private String writer;  //닉네임
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private int count; //조회수
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class SearchCondition {
//    //카테고리 필터
//    private String category;
//    //검색어
//    private String keyword;
//    //검색종류
//    private String searchType;
//    //사용자가 원하는 페이지 (기본 1로 설정)
    private int page = 1;
    //한 페이지당 보여줄 게시글의 개수 (기본 10으로 설정)
    private int size = 10;

    //몇개부터
    private int offset;
    //몇개까지 가져올지
    private int limit;
}


@Service
class BoardSelectService {
    private final BoardSelectMapper boardSelectMapper;

    public BoardSelectService(BoardSelectMapper boardSelectMapper) {
        this.boardSelectMapper = boardSelectMapper;
    }

    public BoardSelectDto getBoardDetail(Long boardId) {
        return boardSelectMapper.getBoardDetail(boardId);
    }

    public List<BoardSelectDto> getBoardList() {
        return boardSelectMapper.selectBoardList();
    }

//    public BoardListResult getBoardList(SearchCondition condition) {
//        int totalCount = boardSelectMapper.selectBoardListCount(condition);
////        PageInfo pageInfo = new PageInfo(condition.getPage(), condition.getSize(), totalCount);
//
//        List<BoardSelectDto> boardList = boardSelectMapper.selectBoardList(condition);
//        return new BoardListResult(boardList, pageInfo);
//    }
}

@Mapper
interface BoardSelectMapper {
    BoardSelectDto getBoardDetail(Long boardId);

    List<BoardSelectDto> selectBoardList(); //보드리스트
    int selectBoardListCount(SearchCondition condition);
    int increaseViewCount(Long boardId);
}


