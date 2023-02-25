package org.waldreg.repository.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;

@Repository
interface TestJpaCharacterRepository extends JpaRepository<Character,Integer>{
}
