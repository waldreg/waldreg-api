package org.waldreg.repository.teambuilding.teamuser.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository("teamuserJpaTeamUserRepository")
public interface JpaTeamUserRepository extends JpaRepository<User, Integer>{

    @Query("select u from User as u join u.character where u.userInfo.userId = :userId")
    Optional<User> findByUserId(@Param("userId") String userId);

    boolean existsByUserInfoUserId(String userId);

}
