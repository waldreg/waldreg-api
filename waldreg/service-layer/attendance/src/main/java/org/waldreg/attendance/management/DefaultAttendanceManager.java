package org.waldreg.attendance.management;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.InvalidDateException;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.attendance.management.spi.UserExistChecker;
import org.waldreg.attendance.rule.valid.AttendanceDateValidator;
import org.waldreg.attendance.type.AttendanceType;

@Service
public class DefaultAttendanceManager implements AttendanceManager{

    private static final int MAXIMUM_DIFFERENCE_BETWEEN_DAY = 60;
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
        readAttendanceTarget(attendanceStatusChangeDto.getId());
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
    public AttendanceTargetDto readAttendanceTarget(int id){
        return attendanceRepository.readAttendanceTarget(id).orElseThrow(
                () -> {throw new DoesNotRegisteredAttendanceException();}
        );
    }

    private void throwIfUnknownAttendanceType(AttendanceType attendanceType){
        if(attendanceType == null){
            throw new UnknownAttendanceTypeException(null);
        }
    }

    @Override
    public List<AttendanceDayDto> readAttendanceStatusList(LocalDate from, LocalDate to){
        attendanceDateValidator.throwIfDateWasTooEarly(from);
        attendanceDateValidator.throwIfDateWasTooFar(to);
        throwIfInvalidDateDetected(from, to);
        return attendanceRepository.readAttendanceStatusList(from, to);
    }

    private void throwIfInvalidDateDetected(LocalDate from, LocalDate to){
        throwIfToEarlyThanFrom(from, to);
        throwIfDateDifferenceTooGreat(from, to);
    }

    private void throwIfToEarlyThanFrom(LocalDate from, LocalDate to){
        if(to.isBefore(from)){
            throw new InvalidDateException();
        }
    }

    private void throwIfDateDifferenceTooGreat(LocalDate from, LocalDate to){
        if(from.plusDays(MAXIMUM_DIFFERENCE_BETWEEN_DAY).isBefore(to)){
            throw new InvalidDateException();
        }
    }

}