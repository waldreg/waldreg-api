package org.waldreg.character.management;

public interface PermissionChecker{

    boolean isPermissionExist(String name);

    boolean isPossiblePermissionStatus(String name, String status);

}
