package org.waldreg.board.board.spi;

import java.util.List;
import org.waldreg.board.dto.BoardDto;

public interface BoardRepository{

    BoardDto createBoard(BoardDto boardDto);

    BoardDto inquiryBoardById(int boardId);

    int getBoardMaxIndex();

    List<BoardDto> inquiryAllBoard(int from, int to);

    List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to);

    BoardDto modifyBoard(BoardDto boardDto);

    void deleteBoard(int id);

    List<BoardDto> searchByTitle(String keyword, int from, int to);

    List<BoardDto> searchByContent(String keyword, int from, int to);

    List<BoardDto> searchByAuthorUserId(String keyword, int from, int to);

}
