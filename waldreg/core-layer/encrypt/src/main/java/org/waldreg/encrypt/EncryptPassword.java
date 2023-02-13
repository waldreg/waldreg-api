package org.waldreg.encrypt;

public interface EncryptPassword{

    String createSalt();

    String getEncryptPassword(String password, String salt);

}
