package org.waldreg.repository.joiningpool.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Integer>{

    @Query(value = "select EXISTS (select U.* from USER U where U.USER_USER_ID = :userId)", nativeQuery = true)
    boolean isExistUserByUserId(@Param("userId") String userId);

    @Query(value = "select U.* from USER U where U.USER_USER_ID =:userId", nativeQuery = true)
    Optional<User> findUserByUserId(@Param("userId") String userId);

    @Query(value = "select U.* from USER U join COMMENT C where U.USER_ID = C.USER_ID and C.COMMENT_ID = :commentId", nativeQuery = true)
    Optional<User> findUserByCommentId(@Param("commentId") int commentId);

    @Query(value = "select U.* from USER U join BOARD B where U.USER_ID = B.USER_ID and B.BOARD_ID = :boardId", nativeQuery = true)
    Optional<User> findUserByBoardId(@Param("boardId") int boardId);

}

