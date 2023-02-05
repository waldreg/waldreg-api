package org.waldreg.repository.category;

import java.util.List;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.domain.board.Board;

public interface BoardInCategoryMapper{

    List<Board> boardDtoListToBoardDomainList(List<BoardDto> boardDtoList);

    List<BoardDto> boardDomainListToBoardDtoList(List<Board> boardList);

}
