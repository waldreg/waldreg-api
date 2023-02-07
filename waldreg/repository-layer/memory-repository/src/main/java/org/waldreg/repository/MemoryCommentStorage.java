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

    private final MemoryBoardStorage memoryBoardStorage;

    private final AtomicInteger atomicInteger;

    private final Integer startIndex = 0;

    private final Map<Integer, Comment> storage;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public MemoryCommentStorage(MemoryBoardStorage memoryBoardStorage){this.memoryBoardStorage = memoryBoardStorage;}

    public Comment createComment(Comment comment){
        comment.setId(atomicInteger.getAndIncrement());
        storage.put(comment.getId(), comment);
        return comment;
    }

    public void deleteAllComment(){
        storage.clear();
    }

    public List<Comment> inquiryAllCommentByBoardId(int boardId, int from, int to){
        int index = startIndex;
        List<Comment> commentList = new ArrayList<>();
        for (Map.Entry<Integer, Comment> commentEntry : storage.entrySet()){
            if (isBoardIdEqual(commentEntry.getValue().getBoardId(), boardId) && isInRange(index, from, to)){
                commentList.add(commentEntry.getValue());
                index++;
            }
        }
        return commentList;
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
        int count = 0;
        List<Comment> commentList = new ArrayList<>();
        for (Map.Entry<Integer, Comment> commentEntry : storage.entrySet()){
            if (isBoardIdEqual(commentEntry.getValue().getBoardId(), boardId)){
                count++;
            }
        }
        return count;
    }

    public void updateBoardCommentList(int boardId){
        int startIdx = 0;
        int maxIdx = getCommentMaxIdxByBoardId(boardId) - 1;
        Board board = memoryBoardStorage.inquiryBoardById(boardId);
        board.setCommentList(inquiryAllCommentByBoardId(boardId, startIndex, maxIdx));
        memoryBoardStorage.modifyBoard(board);
    }

    public int getCommentBoardIdByCommentId(int id){
        return storage.get(id).getBoardId();
    }

}
