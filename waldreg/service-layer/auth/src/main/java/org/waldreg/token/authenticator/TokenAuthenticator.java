package org.waldreg.token.authenticator;

public interface TokenAuthenticator{

    boolean authenticate(String token);

}
