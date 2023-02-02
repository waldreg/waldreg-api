package org.waldreg.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.calendar.Schedule;

@Repository
public class MemoryBoardStorage{

    private final AtomicInteger atomicInteger;

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

    public void deleteAllBoard(){
        storage.clear();
    }

}
