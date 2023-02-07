package org.waldreg.repository.board;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.comment.spi.CommentInBoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryUserStorage;

@Repository
public class MemoryBoardRepository implements BoardRepository, CommentInBoardRepository{

    private final MemoryBoardStorage memoryBoardStorage;
    private final MemoryUserStorage memoryUserStorage;
    private final BoardMapper boardMapper;

    private final CommentInBoardMapper commentInBoardMapper;

    @Autowired
    public MemoryBoardRepository(MemoryBoardStorage memoryBoardStorage, MemoryUserStorage memoryUserStorage, BoardMapper boardMapper, CommentInBoardMapper commentInBoardMapper){
        this.memoryBoardStorage = memoryBoardStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.boardMapper = boardMapper;
        this.commentInBoardMapper = commentInBoardMapper;
    }

    @Override
    public BoardDto createBoard(BoardDto boardDto){
        Board board = boardMapper.boardDtoToBoardDomain(boardDto);
        board = memoryBoardStorage.createBoard(board);
        return boardMapper.boardDomainToBoardDto(board);
    }

    @Override
    public BoardDto inquiryBoardById(int boardId){
        Board board = memoryBoardStorage.inquiryBoardById(boardId);
        return boardMapper.boardDomainToBoardDto(board);
    }

    @Override
    public void addCommentInBoardCommentList(CommentDto commentDto){
        Comment comment = commentInBoardMapper.commentDtoToCommentDomain(commentDto);
        memoryBoardStorage.addCommentInBoardCommentList(comment);
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
        List<Board> boardList = memoryBoardStorage.inquiryAllBoard(from - 1, to - 1);
        return boardMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    public List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to){
        List<Board> boardList = memoryBoardStorage.inquiryAllBoardByCategory(categoryId, from - 1, to - 1);
        return boardMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    public int getBoardMaxIdxByCategory(int categoryId){
        return memoryBoardStorage.getBoardMaxIdxByCategory(categoryId);
    }

    @Override
    public void modifyBoard(BoardDto boardDto){
        Board board = boardMapper.boardDtoToBoardDomain(boardDto);
        memoryBoardStorage.modifyBoard(board);
    }

    @Override
    public void deleteBoard(int id){
        memoryBoardStorage.deleteBoardById(id);
    }

    @Override
    public List<BoardDto> searchByTitle(String keyword, int from, int to){
        List<Board> boardList = memoryBoardStorage.searchByTitle(keyword, from - 1, to - 1);
        return boardMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    public List<BoardDto> searchByContent(String keyword, int from, int to){
        List<Board> boardList = memoryBoardStorage.searchByContent(keyword, from - 1, to - 1);
        return boardMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    public List<BoardDto> searchByAuthorUserId(String keyword, int from, int to){
        List<Board> boardList = memoryBoardStorage.searchByAuthorUserId(keyword, from - 1, to - 1);
        return boardMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    public int getBoardMaxIdxByTitle(String keyword){
        return memoryBoardStorage.getBoardMaxIdxByTitle(keyword);
    }

    @Override
    public int getBoardMaxIdxByContent(String keyword){
        return memoryBoardStorage.getBoardMaxIdxByContent(keyword);
    }

    @Override
    public int getBoardMaxIdxByAuthorUserId(String keyword){
        return memoryBoardStorage.getBoardMaxIdxByAuthorUserId(keyword);
    }

}
