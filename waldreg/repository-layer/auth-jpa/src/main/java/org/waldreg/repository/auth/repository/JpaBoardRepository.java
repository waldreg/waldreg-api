package org.waldreg.repository.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.Board;

@Repository
public interface JpaBoardRepository extends JpaRepository<Board, Integer>{
}
