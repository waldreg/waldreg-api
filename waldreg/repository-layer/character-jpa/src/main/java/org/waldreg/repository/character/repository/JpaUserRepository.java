package org.waldreg.repository.character.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository("characterJpaUserRepository")
public interface JpaUserRepository extends JpaRepository<User, Integer>{

    @Modifying
    @Query(value = "UPDATE USER SET USER.CHARACTER_ID = :characterId WHERE USER.USER_ID = :id", nativeQuery = true)
    void updateUserCharacter(@Param("characterId") int characterId, @Param("id") int id);

    @Query("select u from User as u where u.character.id = :characterId")
    List<User> findByCharacterId(@Param("characterId") int characterId);

}
