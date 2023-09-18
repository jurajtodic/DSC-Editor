package com.eneamathos.dscwebapi.Common;

import java.util.Optional;

public class DomainConfigurationFilterParameters {
    private String type;
    private String name;
    private Boolean active;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Boolean isActive() {
        return active;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
