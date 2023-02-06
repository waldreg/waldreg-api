
package org.waldreg.controller.board.board;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.waldreg.board.board.management.BoardManager;
import org.waldreg.board.board.management.BoardManager.BoardRequest;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.exception.BoardDeletePermissionException;
import org.waldreg.board.exception.BoardModifyPermissionException;
import org.waldreg.board.file.FileManager;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.controller.board.board.mapper.ControllerBoardMapper;
import org.waldreg.controller.board.board.request.BoardCreateRequest;
import org.waldreg.controller.board.board.request.BoardUpdateRequest;
import org.waldreg.controller.board.board.response.BoardListResponse;
import org.waldreg.controller.board.board.response.BoardResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.BoardIdAuthenticating;
import org.waldreg.token.aop.behavior.AuthFailBehavior;
import org.waldreg.token.aop.parameter.AuthenticateVerifyState;

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
        saveAllFile(imageFileList);
        saveAllFile(fileList);
        BoardRequest boardRequest = controllerBoardMapper.boardCreateRequestToBoardRequest(boardCreateRequest);
        boardManager.createBoard(boardRequest);
    }

    private void saveAllFile(List<MultipartFile> multipartFileList){
        for (MultipartFile file : multipartFileList){
            fileManager.saveFile(file);
        }
    }

    @Authenticating
    @GetMapping("/board/{board-id}")
    public BoardResponse getBoardById(@PathVariable("board-id") int id){
        BoardDto boardDto = boardManager.inquiryBoardById(id);
        return controllerBoardMapper.boardDtoToBoardResponse(boardDto);
    }

    @Authenticating
    @GetMapping("/boards")
    public BoardListResponse getBoardList(@RequestParam("category-id") int categoryId, @RequestParam("from") int from, @RequestParam("to") int to){
        List<BoardDto> boardDtoList;
        if (ObjectUtils.isEmpty(categoryId)){
            boardDtoList = boardManager.inquiryAllBoard(from, to);
        } else{
            boardDtoList = boardManager.inquiryAllBoardByCategory(categoryId, from, to);
        }
        return controllerBoardMapper.boardDtoListToBoardListResponse(boardDtoList);
    }

    @BoardIdAuthenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Board modify manager", fail = VerifyingFailBehavior.PASS)
    @PostMapping("/board/{board-id}")
    public void modifyBoard(@PathVariable("board-id") int boardId,
                            AuthenticateVerifyState authenticateVerifyState,
                            PermissionVerifyState permissionVerifyState,
                            @Validated @RequestPart(value = "boardUpdateRequest") BoardUpdateRequest boardUpdateRequest,
                            @RequestPart(value = "image") List<MultipartFile> imageFileList,
                            @RequestPart(value = "file") List<MultipartFile> fileList
    ){
        throwIfDoseNotHaveBoardModifyPermission(authenticateVerifyState, permissionVerifyState);
        saveAllFile(imageFileList);
        saveAllFile(fileList);
        BoardRequest boardRequest = controllerBoardMapper.boardUpdateRequestToBoardRequest(boardUpdateRequest);
        boardRequest.setId(boardId);
        boardManager.modifyBoard(boardRequest);
    }

    private void throwIfDoseNotHaveBoardModifyPermission(AuthenticateVerifyState authenticateVerifyState, PermissionVerifyState permissionVerifyState){
        if (!authenticateVerifyState.isVerified() && !permissionVerifyState.isVerified()){
            throw new BoardModifyPermissionException();
        }
    }

    @BoardIdAuthenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Board delete manager", fail = VerifyingFailBehavior.PASS)
    @DeleteMapping("/board/{board-id}")
    public void deleteBoardById(@PathVariable("board-id") int boardId,
                                AuthenticateVerifyState authenticateVerifyState,
                                PermissionVerifyState permissionVerifyState){
        throwIfDoseNotHaveBoardDeletePermission(authenticateVerifyState, permissionVerifyState);
        boardManager.deleteBoard(boardId);
    }

    private void throwIfDoseNotHaveBoardDeletePermission(AuthenticateVerifyState authenticateVerifyState, PermissionVerifyState permissionVerifyState){
        if (!authenticateVerifyState.isVerified() && !permissionVerifyState.isVerified()){
            throw new BoardDeletePermissionException();
        }
    }

    @Authenticating
    @GetMapping("/board/search")
    public BoardListResponse searchBoard(@RequestParam("type") Type type, @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, @RequestParam("from") int from, @RequestParam("to") int to){
        List<BoardDto> boardDtoList = type.searchRunnable.search(keyword, from, to);
        return controllerBoardMapper.boardDtoListToBoardListResponse(boardDtoList);
    }

    public enum Type{

        title,
        content,
        author;

        private SearchRunnable searchRunnable;

        @FunctionalInterface
        public interface SearchRunnable{

            List<BoardDto> search(String keyword, int from, int to);

        }

        @Component
        public static final class Constitutor{

            @Autowired
            public Constitutor(BoardManager boardManager){
                Type.title.searchRunnable = boardManager::searchBoardByTitle;
                Type.content.searchRunnable = boardManager::searchBoardByContent;
                Type.author.searchRunnable = boardManager::searchBoardByAuthorUserId;
            }

        }


    }


}
