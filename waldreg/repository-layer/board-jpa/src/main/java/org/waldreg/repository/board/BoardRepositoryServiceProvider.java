package org.waldreg.repository.board;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.domain.board.Board;
import org.waldreg.repository.board.mapper.BoardRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCategoryRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@Repository
public class BoardRepositoryServiceProvider implements BoardRepository{

    private final BoardRepositoryMapper boardRepositoryMapper;
    private final JpaBoardRepository jpaBoardRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    public BoardRepositoryServiceProvider(BoardRepositoryMapper boardRepositoryMapper, JpaBoardRepository jpaBoardRepository, JpaUserRepository jpaUserRepository, JpaCategoryRepository jpaCategoryRepository){
        this.boardRepositoryMapper = boardRepositoryMapper;
        this.jpaBoardRepository = jpaBoardRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaCategoryRepository = jpaCategoryRepository;
    }

    @Override
    @Transactional
    public BoardDto createBoard(BoardDto boardDto){
        Board board = boardRepositoryMapper.boardDtoToBoardDomain(boardDto);
        board.setUser(jpaUserRepository.findById(boardDto.getUserDto().getId()).get());
        board.setCategory(jpaCategoryRepository.findById(boardDto.getCategoryId()).get());
        jpaBoardRepository.save(board);
        return boardRepositoryMapper.boardDomainToBoardDto(board);
    }

    @Override
    public BoardDto inquiryBoardById(int boardId){
        Board board = jpaBoardRepository.findById(boardId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find board id \"" + boardId + "\"");}
        );
        return boardRepositoryMapper.boardDomainToBoardDto(board);
    }

    @Override
    public boolean isExistBoard(int id){
        return false;
    }

    @Override
    public int getBoardMaxIdx(){
        return 0;
    }

    @Override
    public List<BoardDto> inquiryAllBoard(int from, int to){

        return null;
    }

    @Override
    public List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to){
        return null;
    }

    @Override
    public int getBoardMaxIdxByCategory(int categoryId){
        return 0;
    }

    @Override
    public void modifyBoard(BoardDto boardDto){

    }

    @Override
    public void deleteBoard(int id){

    }

    @Override
    public List<BoardDto> searchByTitle(String keyword, int from, int to){
        return null;
    }

    @Override
    public List<BoardDto> searchByContent(String keyword, int from, int to){
        return null;
    }

    @Override
    public List<BoardDto> searchByAuthorUserId(String keyword, int from, int to){
        return null;
    }

    @Override
    public int getBoardMaxIdxByTitle(String keyword){
        return 0;
    }

    @Override
    public int getBoardMaxIdxByContent(String keyword){
        return 0;
    }

    @Override
    public int getBoardMaxIdxByAuthorUserId(String keyword){
        return 0;
    }

}
