package org.waldreg.board.board.spi;

import java.util.List;
import org.waldreg.board.dto.BoardDto;

public interface BoardRepository{

    BoardDto createBoard(BoardDto boardDto);

    BoardDto inquiryBoardById(int boardId);

    boolean isExistBoard(int id);

    int getBoardMaxIdx(String userTier);

    List<BoardDto> inquiryAllBoard(String userTier, int from, int to);

    List<BoardDto> inquiryAllBoardByCategory(String userTier, int categoryId, int from, int to);

    int getBoardMaxIdxByCategory(String userTier,int categoryId);

    BoardDto modifyBoard(BoardDto boardDto);

    void deleteBoard(int id);

    List<BoardDto> searchByTitle(String userTier, String keyword, int from, int to);

    List<BoardDto> searchByContent(String userTier, String keyword, int from, int to);

    List<BoardDto> searchByAuthorUserId(String userTier, String keyword, int from, int to);

    int getSearchMaxIdx(String userTier, String keyword);

}
