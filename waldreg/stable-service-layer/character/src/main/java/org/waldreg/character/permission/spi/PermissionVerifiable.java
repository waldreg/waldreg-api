package org.waldreg.character.permission.spi;

@FunctionalInterface
public interface PermissionVerifiable{

    boolean verify(String status);

}