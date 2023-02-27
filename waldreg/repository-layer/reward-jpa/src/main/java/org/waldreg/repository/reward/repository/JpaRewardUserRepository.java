package org.waldreg.repository.reward.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository
public interface JpaRewardUserRepository extends JpaRepository<User, Integer>{
}
