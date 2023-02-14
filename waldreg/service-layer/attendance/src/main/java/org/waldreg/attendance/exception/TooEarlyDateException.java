package org.waldreg.attendance.exception;

import java.time.LocalDate;

public final class TooEarlyDateException extends RuntimeException{

    public TooEarlyDateException(LocalDate localDate){
        super("Date is too early \"" + localDate.toString() + "\"");

    }

}
