package org.waldreg.character.permission.core;

import java.util.List;

public interface PermissionUnit{

    int getId();

    String getService();

    String getName();

    String getInfo();

    boolean verify(String status);

    List<String> getStatusList();

    boolean isPossible(int id, String status);

}
