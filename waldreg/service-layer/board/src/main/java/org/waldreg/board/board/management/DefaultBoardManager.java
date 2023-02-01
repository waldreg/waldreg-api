package org.waldreg.board.board.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.board.exception.BlankTitleException;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.dto.BoardDto;

@Service
public class DefaultBoardManager implements BoardManager{

    private BoardRepository boardRepository;

    @Autowired
    public DefaultBoardManager(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    @Override
    public void createBoard(BoardDto boardDto){
        try{
            throwIfTitleIsBlank(boardDto.getTitle());
            boardRepository.createBoard(boardDto);
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private void throwIfTitleIsBlank(String title){
        if (title.isBlank() || title.isEmpty()){
            throw new BlankTitleException();
        }
    }

}
