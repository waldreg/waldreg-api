package org.waldreg.teambuilding.exception;

public class InvalidUserWeightException extends RuntimeException{

    public InvalidUserWeightException(int weight){super("User's weight value should be between 1 and 10, current weight \""+weight+"\"");}

}
