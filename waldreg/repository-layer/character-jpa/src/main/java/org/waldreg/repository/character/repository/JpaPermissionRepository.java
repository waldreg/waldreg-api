package org.waldreg.repository.character.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Permission;

@Repository("characterJpaPermissionRepository")
public interface JpaPermissionRepository extends JpaRepository<Permission, Integer>{

    @Modifying
    @Query(value = "DELETE FROM PERMISSION WHERE CHARACTER_ID = :character_id", nativeQuery = true)
    void deleteByCharacterId(@Param("character_id") int characterId);

}
