package org.waldreg.repository.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waldreg.domain.user.User;

public interface TestJpaUserRepository extends JpaRepository<User, Integer>{
}
