package org.waldreg.repository.attendance.valid;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.valid.spi.AttendanceValidatorRepository;
import org.waldreg.repository.MemoryAttendanceStorage;

@Repository
public final class MemoryAttendanceValidatorRepository implements AttendanceValidatorRepository{

    private final MemoryAttendanceStorage memoryAttendanceStorage;

    @Autowired
    public MemoryAttendanceValidatorRepository(MemoryAttendanceStorage memoryAttendanceStorage){
        this.memoryAttendanceStorage = memoryAttendanceStorage;
    }

    @Override
    public void confirm(int id){
        LocalDate now = LocalDate.now();
        memoryAttendanceStorage.changeAttendance(id, now, AttendanceType.ATTENDANCED);
    }

}
