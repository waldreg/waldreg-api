package org.waldreg.character.permission.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.permission.core.PermissionUnit;

@Service
public class DefaultPermissionVerifier implements PermissionVerifier{

    private final PermissionUnitInquiryable permissionUnitInquiryable;

    @Override
    public boolean verify(String name, String status){
        PermissionUnit permissionUnit = permissionUnitInquiryable.getPermission(name);
        return permissionUnit.verify(status);
    }

    @Autowired
    private DefaultPermissionVerifier(PermissionUnitInquiryable permissionUnitInquiryable){
        this.permissionUnitInquiryable = permissionUnitInquiryable;
    }

}
