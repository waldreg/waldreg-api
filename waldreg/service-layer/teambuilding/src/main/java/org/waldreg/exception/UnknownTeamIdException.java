package org.waldreg.exception;

public class UnknownTeamIdException extends RuntimeException{

    public UnknownTeamIdException(int teamId){super("Cannot find team id \""+teamId+"\"");}

}
