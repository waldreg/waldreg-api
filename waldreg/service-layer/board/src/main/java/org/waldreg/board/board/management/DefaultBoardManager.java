/*
package org.waldreg.board.board.management;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        UserDto userDto = userRepository.getUserInfo(request.getAuthorId());
        return BoardDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .categoryId(request.getCategoryId())
                .userDto(userDto)
                .build();
    }

    @Override
    public BoardDto inquiryBoardById(int id){
        throwIfBoardDoesNotExist(id);
        BoardDto boardDto = boardRepository.inquiryBoardById(id);
        boardDto.setViews(boardDto.getViews() + 1);
        boardRepository.modifyBoard(boardDto);
        return boardDto;
    }

    private void throwIfBoardDoesNotExist(int boardId){
        if (!boardRepository.isExistBoard(boardId)){
            throw new BoardDoesNotExistException(boardId);
        }
    }

    @Override
    public List<BoardDto> inquiryAllBoard(int from, int to){
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getBoardMaxIdx();
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
        int maxIdx = boardRepository.getBoardMaxIdxByCategory(categoryId);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.inquiryAllBoardByCategory(categoryId, from, to);
    }

    @Override
    public List<BoardDto> searchBoardByTitle(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getSearchMaxIdx(keyword);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.searchByTitle(keyword, from, to);
    }

    @Override
    public List<BoardDto> searchBoardByContent(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getSearchMaxIdx(keyword);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.searchByContent(keyword, from, to);
    }

    @Override
    public List<BoardDto> searchBoardByAuthorUserId(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getSearchMaxIdx(keyword);
        to = adjustEndIdx(from, to, maxIdx);
        return boardRepository.searchByAuthorUserId(keyword, from, to);
    }

    @Override
    public BoardDto modifyBoard(BoardDto boardDtoRequest){
        throwIfBoardDoesNotExist(boardDtoRequest.getId());
        throwIfCategoryDoesNotExist(boardDtoRequest.getCategoryId());
        BoardDto boardDto = boardRepository.inquiryBoardById(boardDtoRequest.getId());
        setBoardDto(boardDto, boardDtoRequest);
        deleteFilePath(boardDto.getFileUrls(), boardDtoRequest.getFileUrls());
        deleteFilePath(boardDto.getImageUrls(), boardDtoRequest.getFileUrls());
        return boardRepository.modifyBoard(boardDto);
    }

    private BoardDto setBoardDto(BoardDto boardDto, BoardDto boardDtoRequest){
        boardDto.setCategoryId(boardDtoRequest.getCategoryId());
        boardDto.setTitle(boardDtoRequest.getTitle());
        boardDto.setContent(boardDtoRequest.getContent());
        boardDto.setLastModifiedAt(LocalDateTime.now());
        return boardDto;
    }

    private List<String> deleteFilePath(List<String> beforeFilePaths, List<String> requestFilePaths){
        for (String requestFilePath : requestFilePaths){
            if (beforeFilePaths.contains(requestFilePath)){
                beforeFilePaths.remove(requestFilePath);
            }
        }
        return beforeFilePaths;
    }

    @Override
    public void deleteBoard(int boardId){
        throwIfBoardDoesNotExist(boardId);
        boardRepository.deleteBoard(boardId);
    }

}
*/
