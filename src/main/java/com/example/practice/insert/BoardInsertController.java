package com.example.practice.insert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
public class BoardInsertController {

    private final BoardInsertService boardInsertService;

    public BoardInsertController(BoardInsertService boardInsertService) {
        this.boardInsertService = boardInsertService;
    }

    @PostMapping
    public Long insertBoard(@RequestBody BoardInsertDto dto) {
        return boardInsertService.insertBoard(dto);
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

    private final BoardInsertMapper boardInsertMapper;

    public BoardInsertService(BoardInsertMapper boardInsertMapper) {
        this.boardInsertMapper = boardInsertMapper;
    }

    public Long insertBoard(BoardInsertDto dto) {
        boardInsertMapper.insertBoard(dto);
        return dto.getBoardId();
    }
}

@Mapper
interface BoardInsertMapper {
    Long insertBoard(BoardInsertDto dto);
}
