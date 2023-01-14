package org.waldreg.character.permission.management;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;
import org.waldreg.character.management.PermissionChecker;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.exception.DuplicatedPermissionNameException;
import org.waldreg.character.exception.UnknownPermissionException;

@Service
public class PermissionUnitManager implements PermissionChecker{

    private final ConcurrentMap<String, PermissionUnit> permissionUnitMap;

    {
        permissionUnitMap = new ConcurrentHashMap<>();
    }

    public void add(PermissionUnit permissionUnit){
        throwIfDuplicatedPermissionName(permissionUnit);
        permissionUnitMap.put(permissionUnit.getName(), permissionUnit);
    }

    private void throwIfDuplicatedPermissionName(PermissionUnit permissionUnit){
        if (permissionUnitMap.containsKey(permissionUnit.getName())){
            throw new DuplicatedPermissionNameException(permissionUnit.getName());
        }
    }

    public PermissionUnit getPermission(String name){
        throwIfDoesNotFindPermission(name);
        return permissionUnitMap.get(name);
    }

    public List<PermissionUnit> getPermissionUnitList(){
        return new ArrayList<>(permissionUnitMap.values());
    }

    @VisibleForTesting
    public void deleteAllPermission(){
        permissionUnitMap.clear();
    }

    @Override
    public boolean isPermissionExist(String name){
        return permissionUnitMap.containsKey(name);
    }

    @Override
    public boolean isPossiblePermissionStatus(String name, String status){
        throwIfDoesNotFindPermission(name);
        PermissionUnit permissionUnit = permissionUnitMap.get(name);
        return permissionUnit.isPossibleStatus(status);
    }

    private void throwIfDoesNotFindPermission(String name){
        if (permissionUnitMap.get(name) == null){
            throw new UnknownPermissionException(name);
        }
    }

}
