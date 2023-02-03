package org.waldreg.exception;

public class NoSuchAlgorithmException extends RuntimeException{
    public NoSuchAlgorithmException(){
        super("No such algorithm in encrypt");
    }
}
