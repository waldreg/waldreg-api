
package org.waldreg.controller.board.board;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.waldreg.board.board.management.BoardManager;
import org.waldreg.board.board.management.BoardManager.BoardRequest;
import org.waldreg.board.board.management.PerPage;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.file.FileManager;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.controller.board.board.mapper.ControllerBoardMapper;
import org.waldreg.controller.board.board.request.BoardCreateRequest;
import org.waldreg.controller.board.board.request.BoardUpdateRequest;
import org.waldreg.controller.board.board.response.BoardListResponse;
import org.waldreg.controller.board.board.response.BoardResponse;
import org.waldreg.core.template.exception.ExceptionTemplate;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.BoardIdAuthenticating;
import org.waldreg.token.aop.behavior.AuthFailBehavior;
import org.waldreg.token.aop.parameter.AuthenticateVerifyState;

@RestController
public class BoardController{

    private final BoardManager boardManager;
    private final FileManager fileManager;
    private final ControllerBoardMapper controllerBoardMapper;

    @Autowired
    public BoardController(BoardManager boardManager, FileManager fileManager, ControllerBoardMapper controllerBoardMapper){
        this.boardManager = boardManager;
        this.fileManager = fileManager;
        this.controllerBoardMapper = controllerBoardMapper;
    }

    @Authenticating
    @PermissionVerifying("Board create manager")
    @RequestMapping(value = "/board", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void createBoard(@Validated @RequestPart(value = "boardCreateRequest") BoardCreateRequest boardCreateRequest,
                            @RequestPart(value = "image", required = false) List<MultipartFile> imageFileList,
                            @RequestPart(value = "file", required = false) List<MultipartFile> fileList
    ){
        if (imageFileList != null){
            saveAllFile(imageFileList);
        }
        if (fileList != null){
            saveAllFile(fileList);
        }
        BoardRequest boardRequest = controllerBoardMapper.boardCreateRequestToBoardRequest(boardCreateRequest);
        boardManager.createBoard(boardRequest);
    }

    @Authenticating
    @PermissionVerifying("Board read manager")
    @GetMapping("/board/{board-id}")
    public BoardResponse getBoardById(@PathVariable("board-id") int id){
        BoardDto boardDto = boardManager.inquiryBoardById(id);
        return controllerBoardMapper.boardDtoToBoardResponse(boardDto);
    }

    @Authenticating
    @GetMapping("/boards")
    public BoardListResponse getBoardList(@RequestParam(value = "category-id", required = false) Integer categoryId, @RequestParam(value = "from", required = false) Integer from, @RequestParam(value = "to", required = false) Integer to){
        List<BoardDto> boardDtoList;
        if (isInvalidRange(from, to)){
            from = 1;
            to = PerPage.PER_PAGE;
        }
        if (isCategoryIdNull(categoryId)){
            boardDtoList = boardManager.inquiryAllBoard(from, to);
            return controllerBoardMapper.boardDtoListToBoardListResponse(boardDtoList);
        }
        boardDtoList = boardManager.inquiryAllBoardByCategory(categoryId, from, to);
        return controllerBoardMapper.boardDtoListToBoardListResponse(boardDtoList);
    }

    private boolean isCategoryIdNull(Integer categoryId){
        return categoryId == null;
    }

    @BoardIdAuthenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Board modify manager", fail = VerifyingFailBehavior.PASS)
    @PostMapping("/board/{board-id}")
    public void modifyBoard(@PathVariable("board-id") int boardId,
                            AuthenticateVerifyState authenticateVerifyState,
                            PermissionVerifyState permissionVerifyState,
                            @Validated @RequestPart(value = "boardUpdateRequest") BoardUpdateRequest boardUpdateRequest,
                            @RequestPart(value = "image", required = false) List<MultipartFile> imageFileList,
                            @RequestPart(value = "file", required = false) List<MultipartFile> fileList
    ){
        throwIfDoseNotHaveBoardModifyPermission(authenticateVerifyState, permissionVerifyState);
        BoardRequest boardRequest = controllerBoardMapper.boardUpdateRequestToBoardRequest(boardUpdateRequest);
        boardRequest.setId(boardId);
        boardManager.modifyBoard(boardRequest);
        if (imageFileList != null){
            saveAllFile(imageFileList);
        }
        if (fileList != null){
            saveAllFile(fileList);
        }
        deleteFile(boardUpdateRequest.getDeleteFileUrls());
        boardManager.modifyBoardFileList(boardRequest);
    }

    private void saveAllFile(List<MultipartFile> multipartFileList){
        for (MultipartFile file : multipartFileList){
            fileManager.saveFile(file);
        }
    }

    private void throwIfDoseNotHaveBoardModifyPermission(AuthenticateVerifyState authenticateVerifyState, PermissionVerifyState permissionVerifyState){
        if (!authenticateVerifyState.isVerified() && !permissionVerifyState.isVerified()){
            throw new NoPermissionException();
        }
    }

    @BoardIdAuthenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Board delete manager", fail = VerifyingFailBehavior.PASS)
    @DeleteMapping("/board/{board-id}")
    public void deleteBoardById(@PathVariable("board-id") int boardId,
                                AuthenticateVerifyState authenticateVerifyState,
                                PermissionVerifyState permissionVerifyState){
        throwIfDoseNotHaveBoardDeletePermission(authenticateVerifyState, permissionVerifyState);
        deleteFileByBoardId(boardId);
        boardManager.deleteBoard(boardId);
    }

    private void deleteFileByBoardId(int boardId){
        BoardDto boardDto = boardManager.inquiryBoardById(boardId);
        deleteFile(boardDto.getFileUrls());
        deleteFile(boardDto.getImageUrls());
    }

    private void deleteFile(List<String> urlList){
        for (String url : urlList){
            fileManager.deleteFile(url);
        }
    }

    private void throwIfDoseNotHaveBoardDeletePermission(AuthenticateVerifyState authenticateVerifyState, PermissionVerifyState permissionVerifyState){
        if (!authenticateVerifyState.isVerified() && !permissionVerifyState.isVerified()){
            throw new NoPermissionException();
        }
    }

    @Authenticating
    @GetMapping("/board/search")
    public BoardListResponse searchBoard(@RequestParam("type") Type type, @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, @RequestParam(value = "from", required = false) Integer from, @RequestParam(value = "to", required = false) Integer to){
        if (isInvalidRange(from, to)){
            from = 1;
            to = PerPage.PER_PAGE;
        }
        List<BoardDto> boardDtoList = type.searchRunnable.search(keyword, from, to);
        return controllerBoardMapper.boardDtoListToBoardListResponse(boardDtoList);
    }

    private boolean isInvalidRange(Integer from, Integer to){
        return from == null || to == null;
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ExceptionTemplate> catchMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-407")
                .message("Unknown type")
                .documentUrl("docs.waldreg.org")
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
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

