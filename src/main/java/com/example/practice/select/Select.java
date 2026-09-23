package com.example.practice.select;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.awt.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/list")
public class Select {
    SelectService sei = new SelectService();
     public Long select() {return null;}

    @Autowired
    SelectService selectService;

    //게시판 전체 조회
//    @GetMapping("/list")
//    public String list(@ModelAttribute SearchCondition condition, Model model) {
//        ListResult result = selectService.getSelectList(condition);
//        model.addAttribute("SelectList", result.getSelectList());
//
//        return "board/list";
//    }

    //게시판 상세 조회
    @GetMapping("/{boardId}")
    public SelectDto selectDetail(@PathVariable Long boardId) {
        System.out.println("요청들어옴!");
        return selectService.selectDetail(boardId);
    }
}

@Getter
@Setter
class SelectDto{
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
class SearchCondition {
    //카테고리 필터
    private String category;
    //검색어
    private String keyword;
    //검색종류
    private String searchType;
    //사용자가 원하는 페이지 (기본 1로 설정)
    private int page = 1;
    //한 페이지당 보여줄 게시글의 개수 (기본 10으로 설정)
    private int size = 10;

    //몇개부터
    private int offset;
    //몇개까지 가져올지
    private int limit;
}


@Service
class SelectService {
    @Autowired
    SelectMapper selectMapper;

    public Long SelectService() {
        return null;
    }

    public SelectDto selectDetail (Long boardId){
        return selectMapper.selectDetail(boardId);
    }
}

@Mapper
interface SelectMapper {
    SelectDto selectDetail(Long boardId);
}


