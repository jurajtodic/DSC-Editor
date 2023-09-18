package com.eneamathos.dscwebapi.DAL.Entities;

import javax.persistence.Entity;

@Entity(name = "user")
public class UserEntity extends EntityBase {

    private String username;
    private String email;

    private String passwordHash;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //TODO: Hash password before saving
    public void setPasswordHash(String password) {
        passwordHash = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }
}
