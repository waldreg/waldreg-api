package org.waldreg.character.core;

import java.util.List;
import org.waldreg.character.spi.PermissionVerifiable;

public final class PermissionUnit<P>{

    private final String name;
    private final PermissionVerifiable<P> permissionVerifiable;
    private final List<P> statusList;

    private PermissionUnit(){
        throw new UnsupportedOperationException
                ("Can not invoke constructor \"PermissionUnit()\"");
    }

    private PermissionUnit(Builder<P> builder){
        this.name = builder.name;
        this.permissionVerifiable = builder.permissionVerifiable;
        this.statusList = builder.statusList;
    }

    public static <P> Builder<P> builder(){
        return new Builder<>();
    }

    public String getName(){
        return this.name;
    }

    public boolean verify(P status){
        return permissionVerifiable.verify(status);
    }

    public List<P> getStatusList(){
        return statusList;
    }

    public static final class Builder<P>{

        private String name;
        private PermissionVerifiable<P> permissionVerifiable;
        private List<P> statusList;

        private Builder(){}

        public Builder<P> name(String name){
            this.name = name;
            return this;
        }

        public Builder<P> permissionVerifiable(PermissionVerifiable<P> permissionVerifiable){
            this.permissionVerifiable = permissionVerifiable;
            return this;
        }

        public Builder<P> statusList(List<P> statusList){
            this.statusList = statusList;
            return this;
        }

        public PermissionUnit<P> build(){
            return new PermissionUnit<>(this);
        }

    }

}
