package org.waldreg.repository.attendance.valid;

import org.springframework.stereotype.Repository;
import org.waldreg.attendance.valid.spi.AttendanceValidatorRepository;

@Repository
public class MemoryAttendanceValidatorRepository implements AttendanceValidatorRepository{

    @Override
    public void confirm(int id){

    }

}