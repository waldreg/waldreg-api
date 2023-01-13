package org.waldreg.character.management;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;
import org.waldreg.character.core.PermissionUnit;
import org.waldreg.character.exception.DuplicatedPermissionNameException;
import org.waldreg.character.exception.UnknownPermissionException;

@Service
public class PermissionUnitManager{

    private final ConcurrentMap<String, PermissionUnit<?>> permissionUnitMap;
    {
        permissionUnitMap = new ConcurrentHashMap<>();
    }

    public void add(PermissionUnit<?> permissionUnit){
        throwIfDuplicatedPermissionName(permissionUnit);
        permissionUnitMap.put(permissionUnit.getName(), permissionUnit);
    }

    private void throwIfDuplicatedPermissionName(PermissionUnit<?> permissionUnit){
        if(permissionUnitMap.containsKey(permissionUnit.getName())){
            throw new DuplicatedPermissionNameException(permissionUnit.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public <P> PermissionUnit<P> getPermission(String name){
        throwIfDoesNotFindPermission(name);
        return (PermissionUnit<P>) permissionUnitMap.get(name);
    }

    private void throwIfDoesNotFindPermission(String name){
        if(permissionUnitMap.get(name) == null){
            throw new UnknownPermissionException(name);
        }
    }

    public List<PermissionUnit<?>> getPermissionUnitList(){
        return new ArrayList<>(permissionUnitMap.values());
    }

    void deleteAllPermission(){
        this.permissionUnitMap.clear();
    }

}
