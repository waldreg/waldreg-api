package org.waldreg.attendance.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.exception.UnknownRewardTagIdException;
import org.waldreg.attendance.reward.spi.AttendanceRewardRepository;
import org.waldreg.attendance.type.AttendanceType;

@Service
public final class DefaultAttendanceRewardManager implements AttendanceRewardManager{

    private final AttendanceRewardRepository attendanceRewardRepository;

    @Autowired
    public DefaultAttendanceRewardManager(AttendanceRewardRepository attendanceRewardRepository){
        this.attendanceRewardRepository = attendanceRewardRepository;
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

}
