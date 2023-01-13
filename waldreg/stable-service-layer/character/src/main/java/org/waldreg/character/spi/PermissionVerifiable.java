package org.waldreg.character.spi;

@FunctionalInterface
public interface PermissionVerifiable{

    boolean verify(String status);

}