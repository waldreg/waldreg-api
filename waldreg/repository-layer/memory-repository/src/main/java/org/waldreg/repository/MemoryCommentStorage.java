package org.waldreg.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.comment.Comment;

@Repository
public class MemoryCommentStorage{

    private final AtomicInteger atomicInteger;

    private final Integer startIndex = 0;

    private final Map<Integer, Comment> storage;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public Comment createComment(Comment comment){
        return null;
    }

    public void deleteAllComment(){
        storage.clear();
    }

    public List<Comment> inquiryAllCommentByBoardId(int boardId, int from, int to){
        return null;
    }

    private boolean isBoardIdEqual(int commentBoardId, int boardId){return commentBoardId == boardId;}

    private boolean isInRange(int index, int from, int to){return index >= from && index <= to;}

    public void modifyComment(Comment comment){
        storage.replace(comment.getId(), comment);
    }

    public boolean isExistComment(int commentId){
        return storage.get(commentId) != null;
    }

    public void deleteComment(int id){
        storage.remove(id);
    }

    public int getCommentMaxIdxByBoardId(int boardId){
        return 1;
    }

    public int getCommentBoardIdByCommentId(int id){
        return 1;
    }

    public Comment inquiryCommentById(int commentId){
        return storage.get(commentId);
    }

}
