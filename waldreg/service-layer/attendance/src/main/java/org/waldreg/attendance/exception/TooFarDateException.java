package org.waldreg.attendance.exception;

import java.time.LocalDate;

public final class TooFarDateException extends RuntimeException{

    public TooFarDateException(LocalDate localDate){
        super("Date is too far \"" + localDate.toString() + "\"");
    }

}
