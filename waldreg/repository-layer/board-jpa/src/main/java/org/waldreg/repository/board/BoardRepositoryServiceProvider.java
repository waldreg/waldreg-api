//package org.waldreg.repository.board;
//
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//import org.waldreg.board.board.spi.BoardRepository;
//import org.waldreg.board.dto.BoardDto;
//import org.waldreg.domain.board.Board;
//import org.waldreg.repository.board.mapper.BoardRepositoryMapper;
//import org.waldreg.repository.board.repository.JpaBoardRepository;
//
//@Repository
//public class BoardRepositoryServiceProvider implements BoardRepository{
//
//
//    private final JpaBoardRepository jpaBoardRepository;
//    private final BoardRepositoryMapper boardRepositoryMapper;
//
//    @Autowired
//    public BoardRepositoryServiceProvider(JpaBoardRepository jpaBoardRepository, BoardRepositoryMapper boardRepositoryMapper){
//        this.jpaBoardRepository = jpaBoardRepository;
//        this.boardRepositoryMapper = boardRepositoryMapper;
//    }
//
//    @Override
//    @Transactional
//    public BoardDto createBoard(BoardDto boardDto){
//        Board board = boardRepositoryMapper.boardDtoToBoardDomain(boardDto);
//        jpaBoardRepository.save(board);
//    }
//
//    @Override
//    public BoardDto inquiryBoardById(int boardId){
//        return null;
//    }
//
//    @Override
//    public boolean isExistBoard(int id){
//        return false;
//    }
//
//    @Override
//    public int getBoardMaxIdx(){
//        return 0;
//    }
//
//    @Override
//    public List<BoardDto> inquiryAllBoard(int from, int to){
//        return null;
//    }
//
//    @Override
//    public List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to){
//        return null;
//    }
//
//    @Override
//    public int getBoardMaxIdxByCategory(int categoryId){
//        return 0;
//    }
//
//    @Override
//    public void modifyBoard(BoardDto boardDto){
//
//    }
//
//    @Override
//    public void deleteBoard(int id){
//
//    }
//
//    @Override
//    public List<BoardDto> searchByTitle(String keyword, int from, int to){
//        return null;
//    }
//
//    @Override
//    public List<BoardDto> searchByContent(String keyword, int from, int to){
//        return null;
//    }
//
//    @Override
//    public List<BoardDto> searchByAuthorUserId(String keyword, int from, int to){
//        return null;
//    }
//
//    @Override
//    public int getBoardMaxIdxByTitle(String keyword){
//        return 0;
//    }
//
//    @Override
//    public int getBoardMaxIdxByContent(String keyword){
//        return 0;
//    }
//
//    @Override
//    public int getBoardMaxIdxByAuthorUserId(String keyword){
//        return 0;
//    }
//
//}
