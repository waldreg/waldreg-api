package org.waldreg.repository.board.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.board.reaction.ReactionUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.repository.JpaReactionUserRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@Component
public class ReactionRepositoryMapper{

    private final JpaReactionUserRepository jpaReactionUserRepository;
    private final JpaUserRepository jpaUserRepository;

    public ReactionRepositoryMapper(JpaReactionUserRepository jpaReactionUserRepository, JpaUserRepository jpaUserRepository){
        this.jpaReactionUserRepository = jpaReactionUserRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    public List<ReactionUser> userDtoListToReactionUserList(List<UserDto> userDtoList, Reaction reaction){
        List<ReactionUser> reactionUserList = new ArrayList<>();
        for (UserDto userDto : userDtoList){
            reactionUserList.add(getReactionUserByUserIdAndReaction(userDto, reaction));
        }
        return reactionUserList;
    }

    private ReactionUser getReactionUserByUserIdAndReaction(UserDto userDto, Reaction reaction){
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

    public ReactionDto reactionDomainListToReactionDto(List<Reaction> reactionList, int boardId){
        Map<BoardServiceReactionType, List<UserDto>> map = new HashMap<>();
        for (Reaction reaction : reactionList){
            map.put(BoardServiceReactionType.valueOf(reaction.getType().toUpperCase()), reactionUserListToUserDtoList(reaction.getReactionUserList()));
        }
        return ReactionDto.builder().boardId(boardId).reactionMap(map).build();
    }

    public List<UserDto> reactionUserListToUserDtoList(List<ReactionUser> reactionUserList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (ReactionUser reactionUser : reactionUserList){
            userDtoList.add(userToUserDto(reactionUser.getUser()));
        }
        return userDtoList;
    }

    public UserDto userToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }


}
