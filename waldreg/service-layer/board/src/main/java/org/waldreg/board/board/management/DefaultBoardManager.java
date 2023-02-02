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

@Service
public class DefaultBoardManager implements BoardManager{

    private final int perPage = 20;

    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public DefaultBoardManager(BoardRepository boardRepository, CategoryRepository categoryRepository, UserRepository userRepository){
        this.boardRepository = boardRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
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
                .category(categoryDto)
                .user(userDto)
                .memberTier(request.getMemberTier())
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
        int maxIdx = boardRepository.getBoardMaxIndex();
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.inquiryAllBoard(from, to);
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
        int maxIdx = boardRepository.getBoardMaxIndex();
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.inquiryAllBoardByCategory(categoryId,from,to);
    }

}
