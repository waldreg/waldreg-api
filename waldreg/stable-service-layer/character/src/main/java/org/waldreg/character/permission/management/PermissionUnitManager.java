package org.waldreg.character.permission.management;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;
import org.waldreg.character.exception.DuplicatedPermissionNameException;
import org.waldreg.character.exception.UnknownPermissionException;
import org.waldreg.character.management.PermissionChecker;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.extension.PermissionUnitAddable;
import org.waldreg.character.permission.verification.PermissionUnitReadable;

@Service
public class PermissionUnitManager implements PermissionChecker, PermissionUnitAddable, PermissionUnitReadable, PermissionUnitListReadable{

    private final ConcurrentMap<String, PermissionUnit> permissionUnitMap;

    {
        permissionUnitMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(PermissionUnit permissionUnit){
        throwIfDuplicatedPermissionName(permissionUnit);
        permissionUnitMap.put(permissionUnit.getName(), permissionUnit);
    }

    private void throwIfDuplicatedPermissionName(PermissionUnit permissionUnit){
        if (permissionUnitMap.containsKey(permissionUnit.getName())){
            throw new DuplicatedPermissionNameException(permissionUnit.getName());
        }
    }

    @Override
    public PermissionUnit getPermission(String name){
        throwIfDoesNotFindPermission(name);
        return permissionUnitMap.get(name);
    }

    @Override
    public List<PermissionUnit> getPermissionUnitList(){
        return new ArrayList<>(permissionUnitMap.values());
    }

    @Override
    public boolean isPermissionExist(String name){
        return permissionUnitMap.containsKey(name);
    }

    @Override
    public boolean isPossiblePermission(int id, String name, String status){
        throwIfDoesNotFindPermission(name);
        PermissionUnit permissionUnit = permissionUnitMap.get(name);
        return permissionUnit.isPossible(id, status);
    }

    private void throwIfDoesNotFindPermission(String name){
        if (permissionUnitMap.get(name) == null){
            throw new UnknownPermissionException(name);
        }
    }

    public void deleteAllPermission(){
        permissionUnitMap.clear();
    }

}
