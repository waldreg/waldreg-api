package org.waldreg.repository.board;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.comment.spi.CommentInBoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.reaction.spi.ReactionInBoardRepository;

@Repository
public class MemoryBoardRepository implements BoardRepository, CommentInBoardRepository, ReactionInBoardRepository{

    @Override
    public BoardDto createBoard(BoardDto boardDto){
        return null;
    }

    @Override
    public BoardDto inquiryBoardById(int boardId){
        return null;
    }

    @Override
    public boolean isExistBoard(int id){
        return false;
    }

    @Override
    public int getBoardMaxIdx(){
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
    public int getBoardMaxIdxByCategory(int categoryId){
        return 0;
    }

    @Override
    public void modifyBoard(BoardDto boardDto){

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

    @Override
    public int getBoardMaxIdxByTitle(String keyword){
        return 0;
    }

    @Override
    public int getBoardMaxIdxByContent(String keyword){
        return 0;
    }

    @Override
    public int getBoardMaxIdxByAuthorUserId(String keyword){
        return 0;
    }

    @Override
    public void addCommentInBoardCommentList(CommentDto commentDto){

    }

    @Override
    public ReactionDto getReactionDto(int boardId){
        return null;
    }

    @Override
    public void storeReactionDto(ReactionDto reactionDto){

    }

}
