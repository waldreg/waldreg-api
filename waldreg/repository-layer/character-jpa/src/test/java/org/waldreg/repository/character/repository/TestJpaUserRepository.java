package org.waldreg.repository.character.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository
interface TestJpaUserRepository extends JpaRepository<User, Integer>{

}
