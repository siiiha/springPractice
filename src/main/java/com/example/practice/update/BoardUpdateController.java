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
    // 수정할 게시글 번호와 내용이 담긴 DTO를 받고, Long 타입의 값을 반환하는 메서드
    public Long updateBoard(BoardUpdateDto dto) {
        // DTO를 Mapper에 전달해서 게시글 수정 SQL을 실행
        boardUpdateMapper.updateBoard(dto);
        // DTO에 담긴 게시글 번호를 꺼내서 호출한 컨트롤러에 반환
        return dto.getBoardId();
    }

//    public BoardUpdateDto getBoardDetail(Long boardId) {
//        return boardUpdateMapper.getBoardDetail(boardId);
//    }
}

@Mapper
interface BoardUpdateMapper {
    int updateBoard(BoardUpdateDto dto);
    BoardUpdateDto getBoardDetail(Long boardId);
}
