package org.waldreg.controller.board.comment.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.controller.board.comment.request.CommentRequest;
import org.waldreg.controller.board.comment.response.CommentListResponse;
import org.waldreg.controller.board.comment.response.CommentResponse;

@Service
public class ControllerCommentMapper{

    public CommentDto commentRequestToCommentDto(CommentRequest commentRequest){
        return CommentDto.builder()
                .content(commentRequest.getContent())
                .build();
    }

    public CommentListResponse commentDtoListToCommentListResponse(List<CommentDto> commentDtoList){
        List<CommentResponse> commentResponseList = new ArrayList<>();
        for (CommentDto commentDto : commentDtoList){
            commentResponseList.add(commentDtoToCommentResponse(commentDto));
        }
        return CommentListResponse.builder().maxIdx(commentDtoList.size())
                .commentResponseList(commentResponseList)
                .build();
    }

    private CommentResponse commentDtoToCommentResponse(CommentDto commentDto){
        return CommentResponse.builder()
                .id(commentDto.getId())
                .userId(commentDto.getUserDto().getUserId())
                .name(commentDto.getUserDto().getName())
                .createdAt(commentDto.getCreatedAt())
                .lastModifiedAt(commentDto.getLastModifiedAt())
                .content(commentDto.getContent())
                .build();
    }

}
