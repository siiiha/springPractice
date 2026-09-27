package com.example.practice.update;

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

@RestController
@RequestMapping("/boards")
public class BoardUpdateController {
    private final BoardUpdateService boardUpdateService;
    public BoardUpdateController(BoardUpdateService boardUpdateService) {
        this.boardUpdateService = boardUpdateService;
    }

    //수정 화면을 보여줌
    @ResponseBody
    @PostMapping("/{boardId}/edit")
    public Long edit(
            @PathVariable Long boardId,
            @RequestBody BoardUpdateDto boardDto) {
        boardDto.setBoardId(boardId);
        return boardUpdateService.updateBoard(boardDto);
    }

}

@Getter
@Setter
class BoardUpdateDto {
    private Long boardId;
    private Long memberId;
    private String category;
    private String title;
    private String writer;
    private String content;
}

@Service
class BoardUpdateService{
    private final BoardUpdateMapper boardUpdateMapper;

    public BoardUpdateService(BoardUpdateMapper boardUpdateMapper){
        this.boardUpdateMapper = boardUpdateMapper;
    }

    public Long updateBoard(BoardUpdateDto dto) {
        boardUpdateMapper.updateBoard(dto);
        return dto.getBoardId();
    }

    public BoardUpdateDto getBoardDetail(Long boardId) {
        return boardUpdateMapper.getBoardDetail(boardId);
    }
}

@Mapper
interface BoardUpdateMapper {
    int updateBoard(BoardUpdateDto dto);
    BoardUpdateDto getBoardDetail(Long boardId);
}
