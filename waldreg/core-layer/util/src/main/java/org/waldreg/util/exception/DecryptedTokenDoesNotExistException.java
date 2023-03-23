package org.waldreg.util.exception;

public class DecryptedTokenDoesNotExistException extends RuntimeException{

    public DecryptedTokenDoesNotExistException(){
        super("Can not find decrypted token");
    }

}
