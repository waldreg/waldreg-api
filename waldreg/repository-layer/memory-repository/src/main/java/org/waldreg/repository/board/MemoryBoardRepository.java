package org.waldreg.repository.board;

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

    @Autowired
    public MemoryBoardRepository(MemoryBoardStorage memoryBoardStorage, MemoryUserStorage memoryUserStorage, BoardMapper boardMapper){
        this.memoryBoardStorage = memoryBoardStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.boardMapper = boardMapper;
    }

    @Override
    public BoardDto createBoard(BoardDto boardDto){
        Board board = boardMapper.boardDtoToBoardDomain(boardDto);
        User user = memoryUserStorage.readUserById(boardDto.getUser().getId());
        board.setUser(user);
        board = memoryBoardStorage.createBoard(board);
        return boardMapper.boardDomainToBoardDto(board);
    }

    @Override
    public BoardDto inquiryBoardById(int boardId){
        return null;
    }

    @Override
    public int getBoardMaxIndex(){
        return 0;
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
    public BoardDto modifyBoard(BoardDto boardDto){
        return null;
    }

    @Override
    public void deleteBoard(int id){

    }

    @Override
    public List<BoardDto> searchByTitle(String keyword, int from, int to){
        return null;
    }

    @Override
    public List<BoardDto> searchByContent(String keyword, int from, int to){
        return null;
    }

    @Override
    public List<BoardDto> searchByAuthorUserId(String keyword, int from, int to){
        return null;
    }

}
