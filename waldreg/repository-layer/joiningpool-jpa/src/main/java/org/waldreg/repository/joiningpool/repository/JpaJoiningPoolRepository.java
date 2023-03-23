package org.waldreg.repository.joiningpool.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.UserJoiningPool;

@Repository("joiningpoolJpaJoiningPoolRepository")
public interface JpaJoiningPoolRepository extends JpaRepository<UserJoiningPool, Integer>{

    @Query(value = "select JU.* from USER_JOINING_POOL as JU LIMIT :count OFFSET :start", nativeQuery = true)
    List<UserJoiningPool> readAllJoiningPoolUser(@Param("start") int start, @Param("count") int count);

    @Query(value = "select JU from UserJoiningPool as JU where JU.userInfo.userId =:userId")
    Optional<UserJoiningPool> findByUserId(@Param("userId") String userId);

    @Modifying
    @Query(value = "DELETE FROM USER_JOINING_POOL as JU where JU.USER_USER_ID =:userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") String userId);

    boolean existsByUserInfoUserId(String userId);

}

