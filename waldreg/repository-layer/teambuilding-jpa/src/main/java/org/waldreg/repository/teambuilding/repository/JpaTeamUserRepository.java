package org.waldreg.repository.teambuilding.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository
public interface JpaTeamUserRepository extends JpaRepository<User, Integer>{

    @Query("select u from User as u join u.character where u.userInfo.userId = :userId")
    Optional<User> findByUserId(@Param("userId") String userId);

    @Query("select case when count(u) > 0 then true else false end from User as u where u.userInfo.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);

}
