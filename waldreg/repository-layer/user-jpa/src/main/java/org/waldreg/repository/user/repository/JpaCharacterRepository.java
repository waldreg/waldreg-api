package org.waldreg.repository.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;

@Repository("userJpaCharacterRepository")
public interface JpaCharacterRepository extends JpaRepository<Character, Integer>{

    boolean existsByCharacterName(String characterName);

    @Query("select c from Character as c where characterName = :characterName")
    Optional<Character> getCharacterByCharacterName(@Param("characterName") String characterName);

}
