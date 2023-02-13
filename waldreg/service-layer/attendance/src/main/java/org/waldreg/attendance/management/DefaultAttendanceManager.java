package org.waldreg.attendance.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.attendance.management.spi.UserExistChecker;
import org.waldreg.attendance.rule.valid.AttendanceDateValidator;
import org.waldreg.attendance.type.AttendanceType;

@Service
public class DefaultAttendanceManager implements AttendanceManager{

    private final UserExistChecker userExistChecker;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceDateValidator attendanceDateValidator;

    @Autowired
    public DefaultAttendanceManager(UserExistChecker userExistChecker,
                                    AttendanceRepository attendanceRepository,
                                    AttendanceDateValidator attendanceDateValidator){
        this.userExistChecker = userExistChecker;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDateValidator = attendanceDateValidator;
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

    @Override
    public void changeAttendanceStatus(AttendanceStatusChangeDto attendanceStatusChangeDto){
        throwIfCannotFindUser(attendanceStatusChangeDto.getId());
        getAttendanceTarget(attendanceStatusChangeDto.getId());
        throwIfUnknownAttendanceType(attendanceStatusChangeDto.getAttendanceType());
        attendanceDateValidator.throwIfDateWasTooFar(attendanceStatusChangeDto.getAttendanceDate());
        attendanceDateValidator.throwIfDateWasTooEarly(attendanceStatusChangeDto.getAttendanceDate());
        attendanceRepository.changeAttendanceStatus(attendanceStatusChangeDto);
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

    private void throwIfUnknownAttendanceType(AttendanceType attendanceType){
        if(attendanceType == null){
            throw new UnknownAttendanceTypeException(null);
        }
    }

}
