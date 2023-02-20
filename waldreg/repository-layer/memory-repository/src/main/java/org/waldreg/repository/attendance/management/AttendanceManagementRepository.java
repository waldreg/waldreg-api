package org.waldreg.repository.attendance.management;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.attendance.management.mapper.AttendanceManagementMapper;

@Repository
public final class AttendanceManagementRepository implements AttendanceRepository{

    private final MemoryAttendanceStorage memoryAttendanceStorage;
    private final MemoryUserStorage memoryUserStorage;
    private final AttendanceManagementMapper attendanceManagementMapper;

    @Autowired
    public AttendanceManagementRepository(MemoryAttendanceStorage memoryAttendanceStorage,
                                            MemoryUserStorage memoryUserStorage,
                                            AttendanceManagementMapper attendanceManagementMapper){
        this.memoryAttendanceStorage = memoryAttendanceStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.attendanceManagementMapper = attendanceManagementMapper;
    }

    @Override
    public void registerAttendanceTarget(int id){
        memoryAttendanceStorage.addAttendanceTarget(AttendanceUser.builder()
                .user(memoryUserStorage.readUserById(id))
                .build());
    }

    @Override
    public void deleteRegisteredAttendanceTarget(int id){
        memoryAttendanceStorage.deleteAttendanceTarget(id);
    }

    @Override
    public Optional<AttendanceTargetDto> readAttendanceTarget(int id){
        Attendance attendance = memoryAttendanceStorage.readAttendance(id);
        if(attendance == null){
            return Optional.empty();
        }
        AttendanceTargetDto attendanceTargetDto = attendanceManagementMapper.attendanceToAttendanceTargetDto(attendance);
        return Optional.of(attendanceTargetDto);
    }

    @Override
    public void changeAttendanceStatus(AttendanceStatusChangeDto attendanceStatusChangeDto){
        memoryAttendanceStorage.changeAttendance(attendanceStatusChangeDto.getId(),
                                                    attendanceStatusChangeDto.getAttendanceDate(),
                                                    attendanceStatusChangeDto.getAttendanceType());
    }

    @Override
    public List<AttendanceDayDto> readAttendanceStatusList(LocalDate from, LocalDate to){
        List<Attendance> attendanceList = memoryAttendanceStorage.readAllAttendance(from, to);
        return attendanceManagementMapper.attendanceListToAttendanceDayDtoList(attendanceList);
    }

    @Override
    public AttendanceUserDto readSpecificAttendanceStatusList(int id, LocalDate from, LocalDate to){
        List<Attendance> attendanceList = memoryAttendanceStorage.readSpecificUsersAttendance(id, from, to);
        return attendanceManagementMapper.attendanceListToAttendanceUserDto(attendanceList);
    }

    @Override
    public void createNewAttendanceCalendarIfAbsent(LocalDate current){
        memoryAttendanceStorage.stageAttendanceUser();
    }

}
