package org.waldreg.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendancePerDate;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.attendance.AttendanceUser;

@Repository
public class MemoryAttendanceStorage{

    private final List<AttendancePerDate> attendancePerDateList;
    private final Map<Integer, Attendance> attendanceMap;
    private final Map<String, AttendanceTypeReward> attendanceTypeMap;
    private final AtomicInteger atomicInteger;

    public MemoryAttendanceStorage(){
        this.attendancePerDateList = new ArrayList<>();
        this.attendanceMap = new ConcurrentHashMap<>();
        this.attendanceTypeMap = new ConcurrentHashMap<>();
        this.atomicInteger = new AtomicInteger(1);
    }

    public void addAttendanceTarget(AttendanceUser attendanceUser){
        attendanceUser.setAttendanceUserId(atomicInteger.getAndIncrement());
        Attendance attendance = Attendance.builder()
                .attendanceId(atomicInteger.getAndIncrement())
                .user(attendanceUser)
                .attendanceType(attendanceTypeMap.get(AttendanceType.ABSENCE.toString()))
                .build();
        attendance.setAttendanceId(atomicInteger.getAndIncrement());
        this.attendanceMap.put(attendanceUser.getUser().getId(), attendance);
    }

    public Attendance readAttendance(int id){
        return attendanceMap.get(id);
    }

    public void deleteAttendance(int id){
        attendanceMap.remove(id);
    }

    public void changeAttendance(int targetUserId, LocalDate targetDate, AttendanceType changedType){
        for(AttendancePerDate attendancePerDate : attendancePerDateList){
            if(isMatchedAttendanceDate(attendancePerDate, targetDate)){
                changeTargetUsersState(attendancePerDate.getAttendanceList(), targetUserId, changedType);
                return;
            }
        }
    }

    private boolean isMatchedAttendanceDate(AttendancePerDate attendancePerDate, LocalDate targetDate){
        return attendancePerDate.getAttendanceDate().equals(targetDate);
    }

    private void changeTargetUsersState(List<Attendance> attendanceList, int targetUserId, AttendanceType changedType){
        for(Attendance attendance : attendanceList){
            if(isEqualUser(attendance, targetUserId)){
                attendance.setAttendanceType(attendanceTypeMap.get(changedType.toString()));
                return;
            }
        }
    }

    private boolean isEqualUser(Attendance attendance, int userId){
        return attendance.getAttendanceUser().getUser().getId() == userId;
    }

    @PostConstruct
    private void initAttendanceRewardTag(){
        initAbsenceAttendance();
        initLateAttendance();
        initAttendanced();
        initAcknowledgeAttendance();
    }

    private void initAbsenceAttendance(){
        attendanceTypeMap.put(AttendanceType.ABSENCE.toString(), AttendanceTypeReward.builder()
                .rewardTag(null)
                .name(AttendanceType.ABSENCE.toString())
                .build());
    }

    private void initLateAttendance(){
        attendanceTypeMap.put(AttendanceType.LATE_ATTENDANCE.toString(), AttendanceTypeReward.builder()
                .rewardTag(null)
                .name(AttendanceType.LATE_ATTENDANCE.toString())
                .build());
    }

    private void initAttendanced(){
        attendanceTypeMap.put(AttendanceType.ATTENDANCED.toString(), AttendanceTypeReward.builder()
                .rewardTag(null)
                .name(AttendanceType.ATTENDANCED.toString())
                .build());
    }

    private void initAcknowledgeAttendance(){
        attendanceTypeMap.put(AttendanceType.ACKNOWLEDGE_ABSENCE.toString(), AttendanceTypeReward.builder()
                .rewardTag(null)
                .name(AttendanceType.ACKNOWLEDGE_ABSENCE.toString())
                .build());
    }

}
