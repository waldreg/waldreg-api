package org.waldreg.repository.board.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.reaction.Reaction;

@Repository("boardJpaReactionRepository")
public interface JpaReactionRepository extends JpaRepository<Reaction,Integer>{

    @Query(value = "select R.* from REACTION R where R.BOARD_ID = :boardId",nativeQuery = true)
    List<Reaction> findByBoardId(@Param("boardId") int boardId);

}
