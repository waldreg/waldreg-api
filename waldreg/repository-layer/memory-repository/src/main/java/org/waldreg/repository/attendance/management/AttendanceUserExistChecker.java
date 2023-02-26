package org.waldreg.repository.attendance.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.management.spi.UserExistChecker;
import org.waldreg.repository.user.MemoryUserRepository;

@Repository
public class AttendanceUserExistChecker implements UserExistChecker{

    private final MemoryUserRepository memoryUserRepository;

    @Autowired
    public AttendanceUserExistChecker(MemoryUserRepository memoryUserRepository){
        this.memoryUserRepository = memoryUserRepository;
    }

    @Override
    public boolean isExistUser(int id){
        return this.memoryUserRepository.isExistId(id);
    }

}
