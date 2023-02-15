package org.waldreg.attendance.management;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.exception.AlreadyAttendanceException;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.InvalidDateException;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.exception.UnknownRewardTagIdException;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.attendance.management.spi.AttendanceRewardRepository;
import org.waldreg.attendance.management.spi.UserExistChecker;
import org.waldreg.attendance.management.valid.AttendanceIdentifyValidable;
import org.waldreg.attendance.rule.valid.AttendanceDateValidator;
import org.waldreg.attendance.type.AttendanceType;

@Service
public class DefaultAttendanceManager implements AttendanceManager{

    private static final int MAXIMUM_DIFFERENCE_BETWEEN_DAY = 60;
    private final UserExistChecker userExistChecker;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceDateValidator attendanceDateValidator;
    private final AttendanceIdentifyValidable attendanceIdentifyValidable;
    private final AttendanceRewardRepository attendanceRewardRepository;

    @Autowired
    public DefaultAttendanceManager(UserExistChecker userExistChecker,
                                    AttendanceRepository attendanceRepository,
                                    AttendanceDateValidator attendanceDateValidator,
                                    AttendanceIdentifyValidable attendanceIdentifyValidable,
                                    AttendanceRewardRepository attendanceRewardRepository){
        this.userExistChecker = userExistChecker;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDateValidator = attendanceDateValidator;
        this.attendanceIdentifyValidable = attendanceIdentifyValidable;
        this.attendanceRewardRepository = attendanceRewardRepository;
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
        throwIfDoesNotAttendanceTarget(attendanceStatusChangeDto.getId());
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
    public void setRewardTag(int rewardTagId, AttendanceType attendanceType){
        throwIfRewardTagDoesNotExist(rewardTagId);
        throwIfUnknownAttendanceType(attendanceType);
        attendanceRewardRepository.setRewardTag(rewardTagId, attendanceType);
    }

    private void throwIfRewardTagDoesNotExist(int rewardTagId){
        if(!attendanceRewardRepository.isRewardTagExist(rewardTagId)){
            throw new UnknownRewardTagIdException(rewardTagId);
        }
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

    @Override
    public AttendanceUserDto readSpecificAttendanceStatusList(int id, LocalDate from, LocalDate to){
        attendanceDateValidator.throwIfDateWasTooEarly(from);
        attendanceDateValidator.throwIfDateWasTooFar(to);
        throwIfInvalidDateDetected(from, to);
        throwIfDoesNotAttendanceTarget(id);
        return attendanceRepository.readSpecificAttendanceStatusList(id, from, to);
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

    @Override
    public void confirm(int id, String attendanceIdentify){
        throwIfDoesNotAttendanceTarget(id);
        throwIfDoesNotNeedAttendance(id);
        attendanceIdentifyValidable.valid(attendanceIdentify);
        attendanceRepository.confirm(id);
    }

    private void throwIfDoesNotAttendanceTarget(int id){
        readAttendanceTarget(id);
    }

    private void throwIfDoesNotNeedAttendance(int id){
        AttendanceTargetDto attendanceTargetDto = readAttendanceTarget(id);
        if(!attendanceTargetDto.isAttendanceRequired()){
            throw new AlreadyAttendanceException();
        }
    }

    @Override
    public AttendanceTargetDto readAttendanceTarget(int id){
        return attendanceRepository.readAttendanceTarget(id).orElseThrow(
                () -> {throw new DoesNotRegisteredAttendanceException();}
        );
    }

}
