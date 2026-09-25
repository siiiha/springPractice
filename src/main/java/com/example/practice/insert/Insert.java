package com.example.practice.insert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
public class Insert {

    BoardInsertService sei = new BoardInsertService();
    public Long insert(){
        return null;
    }

        @Autowired
        BoardInsertService boardInsertService;

        @PostMapping("/write")
        Long boardInsert(@RequestBody BoardInsertDto dto) {
            return boardInsertService.board(dto);
        }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class BoardInsertDto {
    private Long boardId;
    private Long memberId;
    private String category;
    private String title;
    private String writer;
    private String content;
}

@Service
class BoardInsertService {

    @Autowired
    BoardInsertMapper boardInsertMapper;

    public Long BoardInsertService(){
        return null;
    }

    Long board(BoardInsertDto dto) {
        boardInsertMapper.insert(dto);
       return dto.getBoardId();
    }
}

@Mapper
interface BoardInsertMapper {
    Long insert(BoardInsertDto dto);
    };
