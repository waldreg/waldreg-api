package org.waldreg.character.permission.core;

import java.util.List;
import org.waldreg.character.permission.spi.PermissionVerifiable;

public final class PermissionUnit{

    private final String name;
    private final PermissionVerifiable permissionVerifiable;
    private final List<String> statusList;

    private PermissionUnit(){
        throw new UnsupportedOperationException
                ("Can not invoke constructor \"PermissionUnit()\"");
    }

    private PermissionUnit(Builder builder){
        this.name = builder.name;
        this.permissionVerifiable = builder.permissionVerifiable;
        this.statusList = builder.statusList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getName(){
        return this.name;
    }

    public boolean verify(String status){
        return permissionVerifiable.verify(status);
    }

    public List<String> getStatusList(){
        return statusList;
    }

    public static final class Builder{

        private String name;
        private PermissionVerifiable permissionVerifiable;
        private List<String> statusList;

        private Builder(){}

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder permissionVerifiable(PermissionVerifiable permissionVerifiable){
            this.permissionVerifiable = permissionVerifiable;
            return this;
        }

        public Builder statusList(List<String> statusList){
            this.statusList = statusList;
            return this;
        }

        public PermissionUnit build(){
            return new PermissionUnit(this);
        }

    }

}
