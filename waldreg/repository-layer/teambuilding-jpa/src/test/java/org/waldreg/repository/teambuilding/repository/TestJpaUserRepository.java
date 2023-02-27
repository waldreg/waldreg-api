package org.waldreg.repository.teambuilding.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;

@Repository
public interface TestJpaUserRepository extends JpaRepository<User, Integer>{
}
