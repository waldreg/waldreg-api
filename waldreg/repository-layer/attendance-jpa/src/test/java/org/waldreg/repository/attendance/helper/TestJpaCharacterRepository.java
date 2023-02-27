package org.waldreg.repository.attendance.helper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;

@Repository
public interface TestJpaCharacterRepository extends JpaRepository<Character, Integer>{
}