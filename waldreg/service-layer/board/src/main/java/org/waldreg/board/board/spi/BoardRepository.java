package org.waldreg.board.board.spi;

import java.util.List;
import org.waldreg.board.dto.BoardDto;

public interface BoardRepository{

    BoardDto createBoard(BoardDto boardDto);

    BoardDto inquiryBoardById(int boardId);

    boolean isExistBoard(int id);

    int getBoardMaxIdx();

    List<BoardDto> inquiryAllBoard(int from, int to);

    List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to);

    int getBoardMaxIdxByCategory(int categoryId);

    void modifyBoard(BoardDto boardDto);

    void deleteBoard(int id);

    List<BoardDto> searchByTitle(String keyword, int from, int to);

    List<BoardDto> searchByContent(String keyword, int from, int to);

    List<BoardDto> searchByAuthorUserId(String keyword, int from, int to);

    int getBoardMaxIdxByTitle(String keyword);

    int getBoardMaxIdxByContent(String keyword);

    int getBoardMaxIdxByAuthorUserId(String keyword);

}
