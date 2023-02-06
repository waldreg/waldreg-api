
package org.waldreg.controller.board.board;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.waldreg.board.board.management.BoardManager;
import org.waldreg.board.board.management.BoardManager.BoardRequest;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.file.FileManager;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.board.board.mapper.ControllerBoardMapper;
import org.waldreg.controller.board.board.request.BoardCreateRequest;
import org.waldreg.controller.board.board.response.BoardResponse;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class BoardController{

    private final BoardManager boardManager;
    private final FileManager fileManager;
    private final ControllerBoardMapper controllerBoardMapper;


    public BoardController(BoardManager boardManager, FileManager fileManager, ControllerBoardMapper controllerBoardMapper){
        this.boardManager = boardManager;
        this.fileManager = fileManager;
        this.controllerBoardMapper = controllerBoardMapper;
    }

    @Authenticating
    @PermissionVerifying(value = "Board create manager")
    @PostMapping("/board")
    public void createBoard(@Validated @RequestPart(value = "boardCreateRequest") BoardCreateRequest boardCreateRequest,
                            @RequestPart(value = "image") List<MultipartFile> imageFileList,
                            @RequestPart(value = "file") List<MultipartFile> fileList
    ){


        BoardRequest boardRequest = controllerBoardMapper.BoardCreateRequestToBoardRequest(boardCreateRequest);


        boardManager.createBoard(boardRequest);
    }

    @Authenticating
    @GetMapping("/board/{board-id}")
    public BoardResponse getBoardById(@PathVariable("board-id") int id){
        BoardDto boardDto = boardManager.inquiryBoardById(id);
        return controllerBoardMapper.BoardDtoToBoardResponse(boardDto);
    }



}

