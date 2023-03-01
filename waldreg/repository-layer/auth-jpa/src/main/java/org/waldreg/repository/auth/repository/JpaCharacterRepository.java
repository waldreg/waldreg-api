package org.waldreg.repository.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;

@Repository("authJpaCharacterRepository")
public interface JpaCharacterRepository extends JpaRepository<Character,Integer>{
}
