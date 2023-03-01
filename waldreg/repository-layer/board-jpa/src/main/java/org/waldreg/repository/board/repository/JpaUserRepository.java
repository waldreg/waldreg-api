package org.waldreg.repository.board.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository
public interface JpaUserRepository extends JpaRepository<User,Integer>{

    @Query(value = "select U.* from USER U where U.USER_USER_ID = :userId",nativeQuery = true)
    Optional<User> findByUserId(@Param("userId") String userId);

}

