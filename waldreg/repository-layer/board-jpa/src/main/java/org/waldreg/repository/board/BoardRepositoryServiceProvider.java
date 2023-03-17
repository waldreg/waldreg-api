package org.waldreg.repository.board;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.BoardRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCategoryRepository;
import org.waldreg.repository.board.repository.JpaReactionRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@Repository
public class BoardRepositoryServiceProvider implements BoardRepository{

    private final BoardRepositoryMapper boardRepositoryMapper;
    private final JpaBoardRepository jpaBoardRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaReactionRepository jpaReactionRepository;

    private final BoardCommander boardCommander;

    @Autowired
    public BoardRepositoryServiceProvider(BoardRepositoryMapper boardRepositoryMapper, JpaBoardRepository jpaBoardRepository, JpaUserRepository jpaUserRepository, JpaCategoryRepository jpaCategoryRepository, JpaReactionRepository jpaReactionRepository, BoardCommander boardCommander){
        this.boardRepositoryMapper = boardRepositoryMapper;
        this.jpaBoardRepository = jpaBoardRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaCategoryRepository = jpaCategoryRepository;
        this.jpaReactionRepository = jpaReactionRepository;
        this.boardCommander = boardCommander;
    }

    @Override
    @Transactional
    public BoardDto createBoard(BoardDto boardDto){
        Board board = boardRepositoryMapper.boardDtoToBoardDomain(boardDto);
        Category category = getCategoryById(boardDto.getCategoryId());
        User user = getUserById(boardDto.getUserDto().getId());
        board.setUser(user);
        board.setCategory(category);
        jpaBoardRepository.save(board);
        board.setReactions(setDefaultReaction(board));
        return boardRepositoryMapper.boardDomainToBoardDto(board);
    }

    private User getUserById(int id){
        return jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user id \"" + id + "\"");}
        );
    }

    private List<Reaction> setDefaultReaction(Board board){
        List<Reaction> reactionList = new ArrayList<>();
        for (BoardServiceReactionType type : BoardServiceReactionType.values()){
            reactionList.add(buildReaction(type, board));
        }
        jpaReactionRepository.saveAll(reactionList);
        return reactionList;
    }

    private Reaction buildReaction(BoardServiceReactionType type, Board board){
        return Reaction.builder()
                .board(board)
                .type(type.name())
                .reactionUserList(new ArrayList<>())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDto inquiryBoardById(int boardId){
        Board board = getBoardById(boardId);
        return boardRepositoryMapper.boardDomainToBoardDto(board);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistBoard(int id){
        return jpaBoardRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int getBoardMaxIdx(){
        return (int) jpaBoardRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> inquiryAllBoard(int from, int to){
        List<Board> boardList = boardCommander.inquiryAllBoard(from,to);
        return boardRepositoryMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to){
        List<Board> boardList = boardCommander.inquiryBoardByCategoryId(categoryId, from, to);
        return boardRepositoryMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    @Transactional(readOnly = true)
    public int getBoardMaxIdxByCategory(int categoryId){
        return jpaBoardRepository.getBoardMaxIdxByCategoryId(categoryId);
    }

    @Override
    @Transactional
    public void modifyBoard(BoardDto boardDto){
        Board board = getBoardById(boardDto.getId());
        Category category = getCategoryById(boardDto.getCategoryId());
        modifiedBoard(board, boardDto);
        board.setCategory(category);
    }

    private Board getBoardById(int boardId){
        return jpaBoardRepository.findById(boardId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find board id \"" + boardId + "\"");}
        );
    }

    private Category getCategoryById(int categoryId){
        return jpaCategoryRepository.findById(categoryId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find category id \"" + categoryId + "\"");}
        );
    }

    private Board modifiedBoard(Board board, BoardDto boardDto){
        board.setTitle(boardDto.getTitle());
        board.setContent(boardDto.getContent());
        board.setLastModifiedAt(boardDto.getLastModifiedAt());
        return board;
    }

    @Override
    @Transactional
    public void deleteBoard(int id){
        Board board = jpaBoardRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find board id \"" + id + "\"");}
        );
        board.getCategory().getBoardList().remove(board);
        jpaBoardRepository.delete(board);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> searchByTitle(String keyword, int from, int to){
        List<Board> boardList = boardCommander.searchBoardByTitle(keyword, from, to);
        return boardRepositoryMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> searchByContent(String keyword, int from, int to){
        List<Board> boardList = boardCommander.searchBoardByContent(keyword, from, to);
        return boardRepositoryMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> searchByAuthorUserId(String keyword, int from, int to){
        List<Board> boardList = boardCommander.searchBoardByUserId(keyword, from, to);
        return boardRepositoryMapper.boardDomainListToBoardDtoList(boardList);
    }

    @Override
    @Transactional(readOnly = true)
    public int getBoardMaxIdxByTitle(String keyword){
        return jpaBoardRepository.getBoardMaxIdxByTitle(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public int getBoardMaxIdxByContent(String keyword){
        return jpaBoardRepository.getBoardMaxIdxByContent(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public int getBoardMaxIdxByAuthorUserId(String keyword){
        return jpaBoardRepository.getBoardMaxIdxByUserId(keyword);
    }

}
