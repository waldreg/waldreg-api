package org.waldreg.attendance.rule.valid;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.exception.TooFarDateException;
import org.waldreg.attendance.rule.AttendanceRule;

@Service
public final class AttendanceDateValidator{

    public void throwIfDateWasTooFar(LocalDate localDate){
        LocalDate now = LocalDate.now();
        if(now.plusDays(AttendanceRule.ATTENDANCE_SAVED_DATE).isBefore(localDate)){
            throw new TooFarDateException(localDate);
        }
    }

}
