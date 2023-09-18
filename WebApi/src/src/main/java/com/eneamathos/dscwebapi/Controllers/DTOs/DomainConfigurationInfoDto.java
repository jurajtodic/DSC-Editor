package com.eneamathos.dscwebapi.Controllers.DTOs;

public class DomainConfigurationInfoDto extends DtoBase
{
    private String type;
    private String name;
    private boolean active;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isActive(){
        return active;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
