package org.waldreg.repository.attendance.management;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.management.spi.UserExistChecker;
import org.waldreg.repository.attendance.repository.JpaUserRepository;

@Repository
public class UserExistCheckerServiceProvider implements UserExistChecker{

    private final JpaUserRepository jpaUserRepository;

    public UserExistCheckerServiceProvider(@Qualifier("attendanceJpaUserRepository") JpaUserRepository jpaUserRepository){
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistUser(int id){
        return jpaUserRepository.existsById(id);
    }

}
