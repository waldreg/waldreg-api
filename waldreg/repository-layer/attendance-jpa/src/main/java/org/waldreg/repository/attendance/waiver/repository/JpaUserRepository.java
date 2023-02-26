package org.waldreg.repository.attendance.waiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Integer>{
}
