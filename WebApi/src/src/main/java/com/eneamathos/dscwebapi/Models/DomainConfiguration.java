package com.eneamathos.dscwebapi.Models;

public class DomainConfiguration extends ModelBase {
    private String type;
    private String name;
    private boolean active = false;
    private byte[] content;

    public DomainConfiguration()
    {
    }

    public DomainConfiguration(String type, String name, boolean active, byte[] content)
    {
        this.type = type;
        this.name = name;
        this.active = active;
        this.content = content;
    }

    public DomainConfiguration(int id, String type, String name, boolean active, byte[] content)
    {
        super(id);
        this.type = type;
        this.name = name;
        this.active = active;
        this.content = content;
    }


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

    @Override
    public String toString() {
        return String.format("Id: %d\nType: %s\nName: %s\nActive: %b", getId(), getType(), getName(), isActive());
    }
}
