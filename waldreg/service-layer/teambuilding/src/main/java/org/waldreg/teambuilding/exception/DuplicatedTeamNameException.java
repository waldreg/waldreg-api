package org.waldreg.teambuilding.exception;

public class DuplicatedTeamNameException extends RuntimeException{

    public DuplicatedTeamNameException(String teamName){super("Duplicated team name \"" + teamName + "\"");}

}
