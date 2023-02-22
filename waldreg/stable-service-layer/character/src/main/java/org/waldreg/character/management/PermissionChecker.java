package org.waldreg.character.management;

public interface PermissionChecker{

    boolean isPermissionExist(String name);

    boolean isPossiblePermission(int permissionId, String name, String status);

}
