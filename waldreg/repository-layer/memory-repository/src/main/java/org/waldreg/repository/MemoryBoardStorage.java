package org.waldreg.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.calendar.Schedule;

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

}
