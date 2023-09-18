package com.eneamathos.dscwebapi.Controllers.DTOs;

public class UserResponseDto extends DtoBase{
    private String Username;
    private String Email;



    public void setEmail(String email) {
        Email = email;
    }

    public void setUsername(String username) {
        Username = username;
    }

    
    public String getEmail() {
        return Email;
    }


    public String getUsername() {
        return Username;
    }
}