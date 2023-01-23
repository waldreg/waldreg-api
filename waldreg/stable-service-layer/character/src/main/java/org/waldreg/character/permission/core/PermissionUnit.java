package org.waldreg.character.permission.core;

import java.util.List;

public interface PermissionUnit{

    String getName();

    String getInfo();

    boolean verify(String status);

    List<String> getStatusList();

    boolean isPossibleStatus(String status);

}
