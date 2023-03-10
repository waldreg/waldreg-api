package org.waldreg.repository.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.reaction.ReactionUser;

@Repository("boardJpaReactionUserRepository")
public interface JpaReactionUserRepository extends JpaRepository<ReactionUser, Integer>{


    boolean existsByUserUserInfoUserIdAndReactionId(String userId,int reactionId);

    @Query(value = "select RU.* from REACTION_USER RU join User U, REACTION R where RU.USER_ID = U.USER_ID and R.REACTION_ID = RU.REACTION_ID and U.USER_USER_ID = :userId and R.REACTION_ID = :reactionId", nativeQuery = true)
    ReactionUser findByUserIdAndReactionId(@Param("userId") String userId, @Param("reactionId") int reactionId);

    @Modifying
    @Query(value = "delete from REACTION_USER where REACTION_ID = :reactionId",nativeQuery = true)
    void deleteAllReactionUserByReactionId(@Param("reactionId") int reactionId);
}
