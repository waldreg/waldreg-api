package org.waldreg.character.permission.core;

import java.util.List;
import org.waldreg.character.permission.spi.PermissionVerifiable;

public final class DefaultPermissionUnit implements PermissionUnit{

    private final String name;
    private final String info;
    private final PermissionVerifiable permissionVerifiable;
    private final List<String> statusList;

    private DefaultPermissionUnit(){
        throw new UnsupportedOperationException("Can not invoke constructor \"PermissionUnit()\"");
    }

    private DefaultPermissionUnit(Builder builder){
        this.name = builder.name;
        this.info = builder.info;
        this.permissionVerifiable = builder.permissionVerifiable;
        this.statusList = builder.statusList;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public String getInfo(){
        return this.info;
    }

    @Override
    public boolean verify(String status){
        return permissionVerifiable.verify(status);
    }

    @Override
    public List<String> getStatusList(){
        return statusList;
    }

    @Override
    public boolean isPossibleStatus(String status){
        return statusList.contains(status);
    }

    public static final class Builder{

        private String name;
        private String info;
        private PermissionVerifiable permissionVerifiable;
        private List<String> statusList;

        private Builder(){}

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder info(String info){
            this.info = info;
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

        public DefaultPermissionUnit build(){
            return new DefaultPermissionUnit(this);
        }

    }

}
