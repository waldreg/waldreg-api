package org.waldreg.repository.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.user.User;
import org.waldreg.repository.category.BoardInCategoryMapper;

@Service
public class BoardMapper implements BoardInCategoryMapper{

    @Override
    public List<Board> boardDtoListToBoardDomainList(List<BoardDto> boardDtoList){
        return null;
    }

    @Override
    public List<BoardDto> boardDomainListToBoardDtoList(List<Board> boardList){
        return null;
    }

    @Override
    public Board boardDtoToBoardDomain(BoardDto boardDto){
        return null;
    }

}
