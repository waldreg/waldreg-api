package org.waldreg.character.permission.extension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.permission.core.PermissionUnit;

@Service
public class DefaultPermissionExtension implements PermissionExtension{

    private final PermissionUnitAddable permissionUnitAddable;

    @Override
    public void extend(PermissionUnit permissionUnit){
        permissionUnitAddable.add(permissionUnit);
    }

    @Autowired
    private DefaultPermissionExtension(PermissionUnitAddable permissionUnitAddable){
        this.permissionUnitAddable = permissionUnitAddable;
    }

}
