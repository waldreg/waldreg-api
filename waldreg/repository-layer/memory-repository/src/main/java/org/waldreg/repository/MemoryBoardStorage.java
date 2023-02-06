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
public class MemoryBoardStorage{

    private final AtomicInteger atomicInteger;
    private final int startIndex = 0;

    private final Map<Integer, Board> storage;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public Board createBoard(Board board){
        board.setId(atomicInteger.getAndIncrement());
        storage.put(board.getId(), board);
        return board;
    }

    public Board inquiryBoardById(int id){
        return storage.get(id);
    }

    public void deleteAllBoard(){
        storage.clear();
    }

    public void deleteBoardById(int id){
        storage.remove(id);
    }

    public void modifyBoard(Board board){
        storage.replace(board.getId(), board);
    }

    public int getBoardMaxIdx(){
        return storage.size();
    }

    public List<Board> inquiryAllBoard(int from, int to){
        int index = startIndex;
        List<Board> boardList = new ArrayList<>();
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isInRange(index, from, to)){
                boardList.add(boardEntry.getValue());
                index++;
            }
        }
        return boardList;
    }

    public List<Board> inquiryAllBoardByCategory(int categoryId, int from, int to){
        int index = startIndex;
        List<Board> boardList = new ArrayList<>();
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isCategoryIdEqual(boardEntry.getValue().getCategoryId(), categoryId) && isInRange(index, from, to)){
                boardList.add(boardEntry.getValue());
                index++;
            }
        }
        return boardList;
    }

    public int getBoardMaxIdxByCategory(int categoryId){
        int count = 0;
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isCategoryIdEqual(boardEntry.getValue().getCategoryId(), categoryId)){
                count++;
            }
        }
        return count;
    }

    private boolean isCategoryIdEqual(int boardCategoryId, int categoryId){
        return boardCategoryId == categoryId;
    }

    public List<Board> searchByTitle(String keyword, int from, int to){
        int index = startIndex;
        List<Board> boardList = new ArrayList<>();
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isKeywordContained(boardEntry.getValue().getTitle(), keyword) && isInRange(index, from, to)){
                boardList.add(boardEntry.getValue());
            }
        }
        return boardList;
    }

    public List<Board> searchByContent(String keyword, int from, int to){
        int index = startIndex;
        List<Board> boardList = new ArrayList<>();
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isKeywordContained(boardEntry.getValue().getContent(), keyword) && isInRange(index, from, to)){
                boardList.add(boardEntry.getValue());
            }
        }
        return boardList;
    }

    public List<Board> searchByAuthorUserId(String keyword, int from, int to){
        int index = startIndex;
        List<Board> boardList = new ArrayList<>();
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isKeywordContained(boardEntry.getValue().getUser().getUserId(), keyword) && isInRange(index, from, to)){
                boardList.add(boardEntry.getValue());
            }
        }
        return boardList;
    }

    private boolean isInRange(int index, int from, int to){
        return index >= from && index <= to;
    }

    public int getBoardMaxIdxByTitle(String keyword){
        int count = 0;
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isKeywordContained(boardEntry.getValue().getTitle(), keyword)){
                count++;
            }
        }
        return count;
    }

    public int getBoardMaxIdxByContent(String keyword){
        int count = 0;
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isKeywordContained(boardEntry.getValue().getContent(), keyword)){
                count++;
            }
        }
        return count;
    }

    public int getBoardMaxIdxByAuthorUserId(String keyword){
        int count = 0;
        for (Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if (isKeywordContained(boardEntry.getValue().getUser().getUserId(), keyword)){
                count++;
            }
        }
        return count;
    }

    private boolean isKeywordContained(String word, String keyword){
        return word.contains(keyword);
    }

    public void addCommentInBoardCommentList(Comment comment){
        int boardId = comment.getBoardId();
        storage.get(boardId).addComment(comment);
    }

}
