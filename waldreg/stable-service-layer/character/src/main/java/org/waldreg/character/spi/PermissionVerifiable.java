package org.waldreg.character.spi;

@FunctionalInterface
public interface PermissionVerifiable<P>{

    boolean verify(P status);

}