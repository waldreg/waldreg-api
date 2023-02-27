package org.waldreg.repository.board;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.waldreg.domain.board.reaction.ReactionUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.ReactionRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaReactionRepository;
import org.waldreg.repository.board.repository.JpaReactionUserRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@Repository
public class ReactionRepositoryServiceProvider implements ReactionUserRepository, ReactionInBoardRepository{

    private final JpaReactionRepository jpaReactionRepository;
    private final ReactionRepositoryMapper reactionRepositoryMapper;
    private final JpaReactionUserRepository jpaReactionUserRepository;
    private final JpaBoardRepository jpaBoardRepository;
    private final JpaUserRepository jpaUserRepository;

    @Autowired
    public ReactionRepositoryServiceProvider(JpaReactionRepository jpaReactionRepository, ReactionRepositoryMapper reactionRepositoryMapper, JpaReactionUserRepository jpaReactionUserRepository, JpaBoardRepository jpaBoardRepository, JpaUserRepository jpaUserRepository){
        this.jpaReactionRepository = jpaReactionRepository;
        this.reactionRepositoryMapper = reactionRepositoryMapper;
        this.jpaReactionUserRepository = jpaReactionUserRepository;
        this.jpaBoardRepository = jpaBoardRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionDto getReactionDto(int boardId){
        List<Reaction> reactionList = jpaReactionRepository.findByBoardId(boardId);
        Map<BoardServiceReactionType, List<UserDto>> map = new HashMap<>();
        for (Reaction reaction : reactionList){
            map.put(BoardServiceReactionType.valueOf(reaction.getType().toUpperCase()), reactionRepositoryMapper.reactionUserListToUserDtoList(reaction.getReactionUserList()));
        }
        return ReactionDto.builder().boardId(boardId).reactionMap(map).build();
    }

    @Override
    @Transactional
    public void storeReactionDto(ReactionDto reactionDto){
        List<Reaction> reactionList = jpaReactionRepository.findByBoardId(reactionDto.getBoardId());
        Map<BoardServiceReactionType, List<UserDto>> map = reactionDto.getReactionMap();
        for (BoardServiceReactionType type : map.keySet()){
            for (Reaction reaction : reactionList){
                if (type.name().equals(reaction.getType())){
                    List<UserDto> userDtoList = map.get(type);
                    reaction.setReactionUserList(userDtoListToReactionUserList(userDtoList, reaction));
                }

            }
        }
        jpaReactionRepository.saveAll(reactionList);
    }

    private List<ReactionUser> userDtoListToReactionUserList(List<UserDto> userDtoList, Reaction reaction){
        List<ReactionUser> reactionUserList = new ArrayList<>();
        for (UserDto userDto : userDtoList){
            reactionUserList.add(getReactionUserByUserIdAndReaction(userDto, reaction));
        }
        return reactionUserList;
    }

    private ReactionUser getReactionUserByUserIdAndReaction(UserDto userDto, Reaction reaction){
        boolean isExist = jpaReactionUserRepository.existsByUserIdAndReactionId(userDto.getUserId(), reaction.getId());
        if (isExist){
            return jpaReactionUserRepository.findByUserIdAndReactionId(userDto.getUserId(), reaction.getId());
        }
        User user = getUserById(userDto.getId());
        ReactionUser reactionUser = buildReactionUser(user, reaction);
        jpaReactionUserRepository.save(reactionUser);
        return reactionUser;
    }

    private User getUserById(int id){
        return jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user id \"" + id + "\"");}
        );
    }

    private ReactionUser buildReactionUser(User user, Reaction reaction){
        return ReactionUser.builder()
                .user(user)
                .reaction(reaction)
                .build();
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
