package org.waldreg.repository.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.reaction.Reaction;

@Repository
public interface JpaReactionRepository extends JpaRepository<Reaction,Integer>{
}
