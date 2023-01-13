package org.waldreg.character.extension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.core.PermissionUnit;
import org.waldreg.character.management.PermissionUnitManager;

@Service
public class PermissionExtension{

    private final PermissionUnitManager permissionUnitManager;

    public void extend(PermissionUnit permissionUnit){
        permissionUnitManager.add(permissionUnit);
    }

    @Autowired
    private PermissionExtension(PermissionUnitManager permissionUnitManager){
        this.permissionUnitManager = permissionUnitManager;
    }

}
