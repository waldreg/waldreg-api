package org.waldreg.board.board.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.board.exception.BlankTitleException;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.board.spi.CategoryRepository;
import org.waldreg.board.board.spi.UserRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.UserDto;

@Service
public class DefaultBoardManager implements BoardManager{

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
        try{
            throwIfTitleIsBlank(request.getTitle());
            return boardRepository.createBoard(buildBoardDto(request));
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private void throwIfTitleIsBlank(String title){
        if (title.isBlank() || title.isEmpty()){
            throw new BlankTitleException();
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

}
