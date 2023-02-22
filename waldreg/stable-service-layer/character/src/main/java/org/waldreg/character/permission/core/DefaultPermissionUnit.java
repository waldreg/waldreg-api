package org.waldreg.character.permission.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.waldreg.character.permission.spi.PermissionVerifiable;

public final class DefaultPermissionUnit implements PermissionUnit{

    private static final AtomicInteger PERMISSION_UNIT_ID_GENERATOR = new AtomicInteger(1);

    private final int id;
    private final String service;
    private final String name;
    private final String info;
    private final PermissionVerifiable permissionVerifiable;
    private final List<String> statusList;

    private DefaultPermissionUnit(){
        throw new UnsupportedOperationException("Can not invoke constructor \"PermissionUnit()\"");
    }

    private DefaultPermissionUnit(Builder builder){
        this.id = PERMISSION_UNIT_ID_GENERATOR.getAndIncrement();
        this.service = builder.service;
        this.name = builder.name;
        this.info = builder.info;
        this.permissionVerifiable = builder.permissionVerifiable;
        this.statusList = builder.statusList;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public int getId(){
        return this.id;
    }

    @Override
    public String getService(){
        return service;
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
    public boolean isPossible(int id, String status){
        return this.id == id && statusList.contains(status);
    }

    public static final class Builder{

        private String name;
        private String service;
        private String info;
        private PermissionVerifiable permissionVerifiable;
        private List<String> statusList;

        private Builder(){}

        public Builder service(String service){
            this.service = service;
            return this;
        }

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
