package org.waldreg.repository.board;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.board.reaction.spi.ReactionInBoardRepository;
import org.waldreg.board.reaction.spi.ReactionUserRepository;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.ReactionRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaReactionRepository;
import org.waldreg.repository.board.repository.JpaReactionUserRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@Repository
public class ReactionRepositoryServiceProvider implements ReactionUserRepository, ReactionInBoardRepository{

    private final JpaReactionRepository jpaReactionRepository;
    private final JpaReactionUserRepository jpaReactionUserRepository;
    private final ReactionRepositoryMapper reactionRepositoryMapper;
    private final JpaBoardRepository jpaBoardRepository;
    private final JpaUserRepository jpaUserRepository;

    @Autowired
    public ReactionRepositoryServiceProvider(JpaReactionRepository jpaReactionRepository, JpaReactionUserRepository jpaReactionUserRepository, ReactionRepositoryMapper reactionRepositoryMapper, JpaBoardRepository jpaBoardRepository, JpaUserRepository jpaUserRepository){
        this.jpaReactionRepository = jpaReactionRepository;
        this.jpaReactionUserRepository = jpaReactionUserRepository;
        this.reactionRepositoryMapper = reactionRepositoryMapper;
        this.jpaBoardRepository = jpaBoardRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionDto getReactionDto(int boardId){
        List<Reaction> reactionList = jpaReactionRepository.findByBoardId(boardId);
        return reactionRepositoryMapper.reactionDomainListToReactionDto(reactionList,boardId);
    }

    @Override
    @Transactional
    public void storeReactionDto(ReactionDto reactionDto){
        System.out.println("reaction Dto : " + reactionDto);
        List<Reaction> reactionList = jpaReactionRepository.findByBoardId(reactionDto.getBoardId());
        reactionList.forEach(r -> System.out.println("reaction List : " + r));
        Map<BoardServiceReactionType, List<UserDto>> map = reactionDto.getReactionMap();
        for (BoardServiceReactionType type : map.keySet()){
            for (Reaction reaction : reactionList){
                if (type.name().equals(reaction.getType())){
                    reaction.getReactionUserList().forEach(r -> jpaReactionUserRepository.deleteById(r.getId()));
                    List<UserDto> userDtoList = map.get(type);
                    reaction.setReactionUserList(reactionRepositoryMapper.userDtoListToReactionUserList(userDtoList, reaction));
                }
            }
        }
        reactionList.forEach(r -> System.out.println("reaction List : " + r));
        jpaReactionRepository.saveAll(reactionList);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistBoard(int boardId){
        return jpaBoardRepository.existsById(boardId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserInfoByUserId(String userId){
        User user = getUserByUserId(userId);
        return reactionRepositoryMapper.userToUserDto(user);
    }

    private User getUserByUserId(String userId){
        return jpaUserRepository.findByUserId(userId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user userId \"" + userId + "\"");}
        );
    }

}
