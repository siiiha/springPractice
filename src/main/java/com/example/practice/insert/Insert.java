package com.example.practice.insert;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
public class Insert {

    BoardInsertService sei = new BoardInsertService();
    public Long insert(){
        return null;
    }
        BoardInsertService boardInsertService;


        @PostMapping("/write")


        @GetMapping("/write")
        public String boardWritePage(){
            return "board/write";
        }

}

@Service
class BoardInsertService {
    public Object BoardInsertService(){
        return null;
    }
}

    @Getter
    @Setter
    class BoardInsertDto {
        private Long boardId;
        private String category;
        private String title;
        private String writer;
        private String content;
    }



//interface BoardInsertMapper{
//    Long board_id (board)
//
//
//
//}

// 게시글 생성
// INSERT
// 어떤 데이터 받을지 정의
// 그 데이터 어떻게 들어올건지
// 그럼 어떻게 처리할건지
// db 들어가는거 생각하고
// 잘 들어갔는지 어떻게 확인할건지