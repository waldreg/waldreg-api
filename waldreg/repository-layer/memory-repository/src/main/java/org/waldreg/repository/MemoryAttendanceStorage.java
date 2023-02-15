package org.waldreg.repository;

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

    private final Map<Integer, AttendancePerDate> attendancePerDateMap;
    private final Map<Integer, Attendance> attendanceMap;
    private final Map<String, AttendanceTypeReward> attendanceTypeMap;
    private final AtomicInteger atomicInteger;

    public MemoryAttendanceStorage(){
        this.attendancePerDateMap = new ConcurrentHashMap<>();
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
        this.attendanceMap.put(attendanceUser.getAttendanceUserId(), attendance);
    }

    public Attendance readAttendance(int id){
        return attendanceMap.get(id);
    }

    public void deleteAttendance(int id){
        attendanceMap.remove(id);
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
