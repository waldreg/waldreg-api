package org.waldreg.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;

@Repository
public class MemoryBoardStorage{

    private final AtomicInteger atomicInteger;

    private final Map<Integer, Board> storage;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public void createBoard(Board board){
        board.setId(atomicInteger.getAndIncrement());
        storage.put(board.getId(), board);
    }

    public Board inquiryBoardById(int id){
        return storage.get(id);
    }

    public void deleteAllBoard(){
        storage.clear();
    }

    public Board modifyBoard(Board board){
        return storage.replace(board.getId(), board);
    }

    public int getBoardMaxIdx(){
        return storage.size();
    }

    public List<Board> searchByTitle(String keyword){
        List<Board> boardList = new ArrayList<>();
        for(Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if(isKeywordContained(boardEntry.getValue().getTitle(),keyword)){
                boardList.add(boardEntry.getValue());
            }
        }
        return boardList;
    }

    public List<Board> searchByContent(String keyword){
        List<Board> boardList = new ArrayList<>();
        for(Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if(isKeywordContained(boardEntry.getValue().getContent(),keyword)){
                boardList.add(boardEntry.getValue());
            }
        }
        return boardList;
    }

    public List<Board> searchByAuthorUserId(String keyword){
        List<Board> boardList = new ArrayList<>();
        for(Map.Entry<Integer, Board> boardEntry : storage.entrySet()){
            if(isKeywordContained(boardEntry.getValue().getUser().getUserId(),keyword)){
                boardList.add(boardEntry.getValue());
            }
        }
        return boardList;
    }

    private boolean isKeywordContained(String word, String keyword){
        return word.contains(keyword);
    }



}
