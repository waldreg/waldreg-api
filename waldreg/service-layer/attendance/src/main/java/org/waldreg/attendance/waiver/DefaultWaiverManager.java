package org.waldreg.attendance.waiver;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.InvalidWaiverDateException;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.exception.UnknownWaiverIdException;
import org.waldreg.attendance.rule.valid.AttendanceDateValidator;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.attendance.waiver.spi.WaiverRepository;

@Service
public class DefaultWaiverManager implements WaiverManager{

    private final AttendanceDateValidator attendanceDateValidator;
    private final WaiverRepository waiverRepository;

    @Autowired
    public DefaultWaiverManager(AttendanceDateValidator attendanceDateValidator,
                                WaiverRepository waiverRepository){
        this.attendanceDateValidator = attendanceDateValidator;
        this.waiverRepository = waiverRepository;
    }

    @Override
    public void waive(WaiverDto waiverRequest){
        attendanceDateValidator.throwIfDateWasTooFar(waiverRequest.getWaiverDate());
        throwIfInvalidWaiverDate(waiverRequest.getWaiverDate());
        throwIfUserDoesNotAttendanceTarget(waiverRequest.getId());
        waiverRepository.waive(waiverRequest);
    }

    private void throwIfInvalidWaiverDate(LocalDate waiverDate){
        LocalDate now = LocalDate.now();
        if(now.isAfter(waiverDate)){
            throw new InvalidWaiverDateException();
        }
    }

    private void throwIfUserDoesNotAttendanceTarget(int id){
        if(!waiverRepository.isAttendanceTarget(id)){
            throw new DoesNotRegisteredAttendanceException();
        }
    }

    @Override
    public List<WaiverDto> readWaiverList(){
        return waiverRepository.readWaiverList();
    }

    @Override
    public void acceptWaiver(int waiverId, AttendanceType attendanceType){
        WaiverDto waiverDto = waiverRepository.readWaiverByWaiverId(waiverId).orElseThrow(
                () -> {throw new UnknownWaiverIdException(waiverId);}
        );
        throwIfUnknownAttendanceType(attendanceType);
        waiverRepository.acceptWaiver(waiverDto.getId(), waiverDto.getWaiverDate(), attendanceType);
        waiverRepository.deleteWaiver(waiverDto.getWaiverId());
    }

    private void throwIfUnknownAttendanceType(AttendanceType attendanceType){
        if(attendanceType == null){
            throw new UnknownAttendanceTypeException(null);
        }
    }

}
