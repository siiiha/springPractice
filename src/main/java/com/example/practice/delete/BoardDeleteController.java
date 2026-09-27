package com.example.practice.delete;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RequestMapping("/boards")
@RestController
public class BoardDeleteController {
    private final BoardDeleteService boardDeleteService;

    public BoardDeleteController (BoardDeleteService boardDeleteService) {
        this.boardDeleteService = boardDeleteService;
    }

    @DeleteMapping("/{boardId}")
    public void deleteBoard(
            @PathVariable Long boardId,
            @RequestParam Long memberId) {
        boardDeleteService.deleteBoard(boardId, memberId);
    }

}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class BoardDeleteDto {
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

@Service
class BoardDeleteService{
    private final BoardDeleteMapper boardDeleteMapper;

    public BoardDeleteService (BoardDeleteMapper boardDeleteMapper) {
        this.boardDeleteMapper = boardDeleteMapper;
    }

    public void deleteBoard(Long boardId, Long memberId){
        BoardDeleteDto boardDeleteDto = new BoardDeleteDto();
        boardDeleteDto.setBoardId(boardId);
        boardDeleteDto.setMemberId(memberId);

        boardDeleteMapper.deleteBoard(boardDeleteDto);
    }
}

@Mapper
interface BoardDeleteMapper{
    int deleteBoard(BoardDeleteDto boardDeleteDto);
}