package org.waldreg.board.board.management;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.board.exception.BoardDoesNotExistException;
import org.waldreg.board.board.exception.CategoryDoesNotExistException;
import org.waldreg.board.board.exception.InvalidRangeException;
import org.waldreg.board.board.exception.UserDoesNotExistException;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.board.spi.CategoryRepository;
import org.waldreg.board.board.spi.UserRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@Service
public class DefaultBoardManager implements BoardManager{

    private final int perPage = 20;

    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private DecryptedTokenContextGetter decryptedTokenContextGetter;

    @Autowired
    public DefaultBoardManager(BoardRepository boardRepository, CategoryRepository categoryRepository, UserRepository userRepository, DecryptedTokenContextGetter decryptedTokenContextGetter){
        this.boardRepository = boardRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
    }


    @Override
    public BoardDto createBoard(BoardRequest request){
        throwIfCategoryDoesNotExist(request.getCategoryId());
        throwIfUserDoesNotExist(request.getAuthorId());
        return boardRepository.createBoard(buildBoardDto(request));
    }

    private void throwIfCategoryDoesNotExist(int categoryId){
        if (!categoryRepository.isExistCategory(categoryId)){
            throw new CategoryDoesNotExistException(categoryId);
        }
    }

    private void throwIfUserDoesNotExist(int authorId){
        if (!userRepository.isExistUser(authorId)){
            throw new UserDoesNotExistException(authorId);
        }
    }

    private BoardDto buildBoardDto(BoardRequest request){
        CategoryDto categoryDto = categoryRepository.inquiryCategory(request.getCategoryId());
        UserDto userDto = userRepository.getUserInfo(request.getAuthorId());
        return BoardDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .categoryDto(categoryDto)
                .userDto(userDto)
                .boardServiceMemberTier(request.getMemberTier())
                .build();
    }

    @Override
    public BoardDto inquiryBoardById(int id){
        throwIfBoardDoesNotExist(id);
        return boardRepository.inquiryBoardById(id);
    }

    private void throwIfBoardDoesNotExist(int boardId){
        if (!boardRepository.isExistBoard(boardId)){
            throw new BoardDoesNotExistException(boardId);
        }
    }

    @Override
    public List<BoardDto> inquiryAllBoard(int from, int to){
        throwIfInvalidRangeDetected(from, to);
        String userTier = getUserTier();
        int maxIdx = boardRepository.getBoardMaxIdx(userTier);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.inquiryAllBoard(userTier, from, to);
    }

    private String getUserTier(){
        int id = decryptedTokenContextGetter.get();
        return userRepository.getUserTier(id);
    }

    private void throwIfInvalidRangeDetected(int from, int to){
        if (from > to || from < 0){
            throw new InvalidRangeException(from, to);
        }
    }

    private int adjustEndIdx(int from, int to, int maxIdx){
        if (maxIdx < to){
            to = maxIdx;
        }
        if (to - from + 1 > perPage){
            return from + perPage - 1;
        }
        return to;
    }

    @Override
    public List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to){
        throwIfCategoryDoesNotExist(categoryId);
        throwIfInvalidRangeDetected(from, to);
        String userTier = getUserTier();
        int maxIdx = boardRepository.getBoardMaxIdxByCategory(userTier, categoryId);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.inquiryAllBoardByCategory(userTier, categoryId, from, to);
    }

    @Override
    public List<BoardDto> searchBoardByTitle(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        String userTier = getUserTier();
        int maxIdx = boardRepository.getSearchMaxIdx(userTier, keyword);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.searchByTitle(userTier, keyword, from, to);
    }

    @Override
    public List<BoardDto> searchBoardByContent(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        String userTier = getUserTier();
        int maxIdx = boardRepository.getSearchMaxIdx(userTier, keyword);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.searchByContent(userTier, keyword, from, to);
    }

    @Override
    public List<BoardDto> searchBoardByAuthorUserId(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        String userTier = getUserTier();
        int maxIdx = boardRepository.getSearchMaxIdx(userTier, keyword);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.searchByAuthorUserId(userTier, keyword, from, to);
    }

    @Override
    public BoardDto modifyBoard(BoardDto boardDto){
        throwIfBoardDoesNotExist(boardDto.getId());
        throwIfCategoryDoesNotExist(boardDto.getCategoryDto().getId());
        return boardRepository.modifyBoard(boardDto);
    }

    @Override
    public void deleteBoard(int boardId){
        throwIfBoardDoesNotExist(boardId);
        boardRepository.deleteBoard(boardId);
    }

}
