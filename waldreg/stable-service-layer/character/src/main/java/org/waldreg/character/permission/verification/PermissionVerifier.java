package org.waldreg.character.permission.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.management.PermissionUnitManager;

@Service
public class PermissionVerifier{

    private final PermissionUnitManager permissionUnitManager;

    public boolean verify(String name, String status){
        PermissionUnit permissionUnit = permissionUnitManager.getPermission(name);
        return permissionUnit.verify(status);
    }

    @Autowired
    private PermissionVerifier(PermissionUnitManager permissionUnitManager){
        this.permissionUnitManager = permissionUnitManager;
    }

}
