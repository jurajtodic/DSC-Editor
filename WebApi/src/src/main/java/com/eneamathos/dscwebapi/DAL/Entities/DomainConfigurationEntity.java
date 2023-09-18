package com.eneamathos.dscwebapi.DAL.Entities;

import javax.persistence.*;

@Entity(name = "configuration")
public class DomainConfigurationEntity extends EntityBase {

    private String type;
    private String name;
    private boolean active;
    private byte[] content;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
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

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
