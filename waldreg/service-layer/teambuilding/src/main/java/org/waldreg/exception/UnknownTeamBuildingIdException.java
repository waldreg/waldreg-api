package org.waldreg.exception;

public class UnknownTeamBuildingIdException extends RuntimeException{

    public UnknownTeamBuildingIdException(int id){
        super("Cannot find teambuilding id \"" + id + "\"");
    }

}
