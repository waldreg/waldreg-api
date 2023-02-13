package org.waldreg.attendance.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.attendance.management.spi.UserExistChecker;

@Service
public class DefaultAttendanceManager implements AttendanceManager{

    private final UserExistChecker userExistChecker;
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public DefaultAttendanceManager(UserExistChecker userExistChecker,
                                    AttendanceRepository attendanceRepository){
        this.userExistChecker = userExistChecker;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public void registerAttendanceTarget(int id){
        throwIfCannotFindUser(id);
        attendanceRepository.registerAttendanceTarget(id);
    }

    @Override
    public void deleteRegisteredAttendanceTarget(int id){
        throwIfCannotFindUser(id);
        attendanceRepository.deleteRegisteredAttendanceTarget(id);
    }

    private void throwIfCannotFindUser(int id){
        if(!userExistChecker.isExistUser(id)){
            throw new UnknownUsersIdException(id);
        }
    }

    @Override
    public AttendanceTargetDto getAttendanceTarget(int id){
        return attendanceRepository.getAttendanceTarget(id).orElseThrow(
                () -> {throw new DoesNotRegisteredAttendanceException();}
        );
    }

}
