package org.waldreg.controller.board.comment;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.board.comment.management.CommentManager;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.exception.BoardDeletePermissionException;
import org.waldreg.board.exception.CommentDeletePermissionException;
import org.waldreg.board.exception.CommentModifyPermissionException;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.controller.board.comment.mapper.ControllerCommentMapper;
import org.waldreg.controller.board.comment.request.CommentRequest;
import org.waldreg.controller.board.comment.response.CommentListResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.CommentIdAuthenticating;
import org.waldreg.token.aop.parameter.AuthenticateVerifyState;

@RestController
public class CommentController{

    private final CommentManager commentManager;

    private final ControllerCommentMapper controllerCommentMapper;

    public CommentController(CommentManager commentManager, ControllerCommentMapper controllerCommentMapper){
        this.commentManager = commentManager;
        this.controllerCommentMapper = controllerCommentMapper;
    }

    @Authenticating
    @PostMapping("/comment/{board-id}")
    public void createComment(@PathVariable("board-id") int boardId, @RequestBody @Validated CommentRequest commentRequest){
        CommentDto commentDto = controllerCommentMapper.commentRequestToCommentDto(commentRequest);
        commentDto.setBoardId(boardId);
        commentManager.createComment(commentDto);
    }

    @Authenticating
    @GetMapping("/board/comment/{board-id}")
    public CommentListResponse getCommentList(@PathVariable("board-id") int boardId, @RequestParam("from") int from, @RequestParam("to") int to){
        List<CommentDto> commentDtoList = commentManager.inquiryAllCommentByBoardId(boardId, from, to);
        return controllerCommentMapper.commentDtoListToCommentListResponse(commentDtoList);
    }

    @CommentIdAuthenticating
    @PermissionVerifying(value = "Comment manager", fail = VerifyingFailBehavior.PASS)
    @PutMapping("/comment/{comment-id}")
    public void modifyComment(@PathVariable("comment-id") int commentId,
                              AuthenticateVerifyState authenticateVerifyState,
                              PermissionVerifyState permissionVerifyState,
                              @RequestBody @Validated CommentRequest commentRequest){
        throwIfDoseNotHaveCommentModifyPermission(authenticateVerifyState, permissionVerifyState);
        CommentDto commentDto = controllerCommentMapper.commentRequestToCommentDto(commentRequest);
        commentDto.setId(commentId);
        commentManager.modifyComment(commentDto);
    }

    private void throwIfDoseNotHaveCommentModifyPermission(AuthenticateVerifyState authenticateVerifyState, PermissionVerifyState permissionVerifyState){
        if (!authenticateVerifyState.isVerified() && !permissionVerifyState.isVerified()){
            throw new CommentModifyPermissionException();
        }
    }

    @CommentIdAuthenticating
    @PermissionVerifying(value = "Comment manager", fail = VerifyingFailBehavior.PASS)
    @DeleteMapping("/comment/{comment-id}")
    public void deleteComment(@PathVariable("comment-id") int commentId,
                              AuthenticateVerifyState authenticateVerifyState,
                              PermissionVerifyState permissionVerifyState){
        throwIfDoseNotHaveCommentDeletePermission(authenticateVerifyState, permissionVerifyState);
        commentManager.deleteComment(commentId);
    }

    private void throwIfDoseNotHaveCommentDeletePermission(AuthenticateVerifyState authenticateVerifyState, PermissionVerifyState permissionVerifyState){
        if (!authenticateVerifyState.isVerified() && !permissionVerifyState.isVerified()){
            throw new CommentDeletePermissionException();
        }
    }

}
