package org.waldreg.repository.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waldreg.domain.board.reaction.ReactionUser;

public interface JpaReactionUserRepository extends JpaRepository<ReactionUser,Integer>{
}
