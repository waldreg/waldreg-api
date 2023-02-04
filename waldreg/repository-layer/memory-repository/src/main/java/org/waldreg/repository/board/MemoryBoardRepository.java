package org.waldreg.repository.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryUserStorage;

@Repository
public class MemoryBoardRepository implements BoardRepository{

    private final MemoryBoardStorage memoryBoardStorage;
    private final MemoryUserStorage memoryUserStorage;
    private final BoardMapper boardMapper;

    private final int startIndex = 0;

    @Autowired
    public MemoryBoardRepository(MemoryBoardStorage memoryBoardStorage, MemoryUserStorage memoryUserStorage, BoardMapper boardMapper){
        this.memoryBoardStorage = memoryBoardStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.boardMapper = boardMapper;
    }

    @Override
    public void createBoard(BoardDto boardDto){
        Board board = boardMapper.boardDtoToBoardDomain(boardDto);
        memoryBoardStorage.createBoard(board);
    }

    @Override
    public BoardDto inquiryBoardById(int boardId){
        Board board = memoryBoardStorage.inquiryBoardById(boardId);
        return boardMapper.boardDomainToBoardDto(board);
    }

    @Override
    public boolean isExistBoard(int id){
        return memoryBoardStorage.inquiryBoardById(id) != null;
    }

    @Override
    public int getBoardMaxIdx(){
        return memoryBoardStorage.getBoardMaxIdx();
    }

    @Override
    public List<BoardDto> inquiryAllBoard(int from, int to){
        return null;
    }

    @Override
    public List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to){
        return null;
    }

    @Override
    public int getBoardMaxIdxByCategory(int categoryId){
        return 0;
    }

    @Override
    public void modifyBoard(BoardDto boardDto){
        Board board = boardMapper.boardDtoToBoardDomain(boardDto);
        board.setLastModifiedAt(LocalDateTime.now());
        memoryBoardStorage.modifyBoard(board);
    }

    @Override
    public void deleteBoard(int id){

    }

    @Override
    public List<BoardDto> searchByTitle(String keyword){
        List<Board> boardList = memoryBoardStorage.searchByTitle(keyword);
        return boardMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    public List<BoardDto> searchByContent(String keyword){
        List<Board> boardList = memoryBoardStorage.searchByContent(keyword);
        return boardMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    public List<BoardDto> searchByAuthorUserId(String keyword){
        List<Board> boardList = memoryBoardStorage.searchByAuthorUserId(keyword);
        return boardMapper.boardDomainListToBoardDtoList(boardList);
    }

}
