package org.waldreg.repository.board;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.comment.spi.CommentInBoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.reaction.spi.ReactionInBoardRepository;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.board.category.Category;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCategoryStorage;
import org.waldreg.repository.MemoryCommentStorage;

@Repository
public class MemoryBoardRepository implements BoardRepository, CommentInBoardRepository, ReactionInBoardRepository{

    private final MemoryBoardStorage memoryBoardStorage;
    private final MemoryCommentStorage memoryCommentStorage;
    private final MemoryCategoryStorage memoryCategoryStorage;
    private final BoardMapper boardMapper;
    private final CommentInBoardMapper commentInBoardMapper;

    @Autowired
    public MemoryBoardRepository(MemoryBoardStorage memoryBoardStorage, MemoryCommentStorage memoryCommentStorage, MemoryCategoryStorage memoryCategoryStorage, BoardMapper boardMapper, CommentInBoardMapper commentInBoardMapper){
        this.memoryBoardStorage = memoryBoardStorage;
        this.memoryCommentStorage = memoryCommentStorage;
        this.memoryCategoryStorage = memoryCategoryStorage;
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
        int commentCount = memoryCommentStorage.getCommentMaxIdxByBoardId(boardId);
        return boardMapper.boardDomainToBoardDto(board);
    }

    @Override
    public void addCommentInBoardCommentList(CommentDto commentDto){
        Comment comment = commentInBoardMapper.commentDtoToCommentDomain(commentDto);
        memoryBoardStorage.addCommentInBoardCommentList(comment);
    }

    @Override
    public ReactionDto getReactionDto(int boardId){
        Board board = memoryBoardStorage.inquiryBoardById(boardId);
        Reaction reaction = board.getReactions();
        return boardMapper.reactionDomainToReactionDto(reaction);
    }

    @Override
    public void storeReactionDto(ReactionDto reactionDto){
        int boardId = reactionDto.getBoardId();
        Board board = memoryBoardStorage.inquiryBoardById(boardId);
        Reaction reaction = boardMapper.reactionDtoToReactionDomain(reactionDto);
        board.setReactions(reaction);
        memoryBoardStorage.modifyBoard(board);
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
        int categoryId = boardDto.getCategoryId();
        Board board = boardMapper.boardDtoToBoardDomain(boardDto);
        memoryBoardStorage.modifyBoard(board);
        updateCategoryBoardList(categoryId);
    }

    @Override
    public void deleteBoard(int id){
        Board board = memoryBoardStorage.inquiryBoardById(id);
        int categoryId = board.getCategoryId();
        deleteCommentInBoardCommentList(board.getCommentList());
        memoryBoardStorage.deleteBoardById(id);
        updateCategoryBoardList(categoryId);
    }

    private void deleteCommentInBoardCommentList(List<Comment> commentList){
        for (Comment comment : commentList){
            memoryCommentStorage.deleteComment(comment.getId());
        }
    }

    private void updateCategoryBoardList(int categoryId){
        int startIdx = 1;
        int maxIdx = getBoardMaxIdxByCategory(categoryId);
        Category category = memoryCategoryStorage.inquiryCategoryById(categoryId);
        List<BoardDto> boardDtoList = inquiryAllBoardByCategory(categoryId, startIdx, maxIdx);
        category.setBoardList(boardMapper.boardDtoListToBoardDomainList(boardDtoList));
        memoryCategoryStorage.modifyCategory(category);
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
