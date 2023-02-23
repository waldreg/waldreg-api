package org.waldreg.repository.character.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;

@Repository
public interface JpaCharacterRepository extends JpaRepository<Character, Integer>{

    @Query("select c from Character as c join fetch c.permissionList where c.id = :id")
    Optional<Character> findById(@Param("id") int id);

    @Query("select u.character from User as u join u.character where u.id = :id")
    Optional<Character> findByUserId(@Param("id") int id);

}
