package org.waldreg.repository.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;

@Repository
public interface JpaCharacterRepository extends JpaRepository<Character,Integer>{
}
