package org.waldreg.controller.character.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.controller.character.request.PermissionRequest;
import org.waldreg.controller.character.response.PermissionResponse;
import org.waldreg.controller.character.response.SpecifyStatusPermissionResponse;

@Service
public class ControllerPermissionMapper{

    public List<PermissionResponse> permissionUnitToPermissionResponse(List<PermissionUnit> permissionUnitList){
        List<PermissionResponse> permissionResponseDtoList = new ArrayList<>();
        for(PermissionUnit permissionUnit : permissionUnitList){
            permissionResponseDtoList.add(
                    PermissionResponse.builder()
                            .id(permissionUnit.getId())
                            .name(permissionUnit.getName())
                            .info(permissionUnit.getInfo())
                            .statusList(permissionUnit.getStatusList())
                            .build()
            );
        }
        return permissionResponseDtoList;
    }

    public List<PermissionDto> permissionRequestListToPermissionDtoList(List<PermissionRequest> permissionRequestList){
        List<PermissionDto> permissionDtoList = new ArrayList<>();
        for(PermissionRequest permissionRequest : permissionRequestList){
            permissionDtoList.add(PermissionDto.builder()
                    .id(permissionRequest.getId())
                    .name(permissionRequest.getName())
                    .status(permissionRequest.getStatus())
                    .build());
        }
        return permissionDtoList;
    }

    public List<SpecifyStatusPermissionResponse> permissionDtoListToSpecifyPermissionResponseList(List<PermissionDto> permissionDtoList){
        List<SpecifyStatusPermissionResponse> permissionResponseList = new ArrayList<>();
        for(PermissionDto permissionDto : permissionDtoList){
            permissionResponseList.add(
                    SpecifyStatusPermissionResponse.builder()
                            .id(permissionDto.getId())
                            .name(permissionDto.getName())
                            .status(permissionDto.getStatus())
                            .build()
            );
        }
        return permissionResponseList;
    }

}
