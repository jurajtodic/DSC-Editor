package com.eneamathos.dscwebapi.Controllers.DTOs;

import java.time.LocalDateTime;

public class DtoBase {
    private Integer Id;
    private LocalDateTime DateCreated;
    private LocalDateTime DateUpdated;

    public Integer getId() {
        return Id;
    }

    public LocalDateTime getDateCreated() {
        return DateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return DateUpdated;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        DateCreated = dateCreated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        DateUpdated = dateUpdated;
    }
}
