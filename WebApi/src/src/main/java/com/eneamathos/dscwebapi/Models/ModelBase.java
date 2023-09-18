package com.eneamathos.dscwebapi.Models;

import com.eneamathos.dscwebapi.DAL.LocalDateTimeAttributeConverter;

import java.time.LocalDateTime;

public class ModelBase {

    private Integer Id;
    private LocalDateTime DateCreated;
    private LocalDateTime DateUpdated;

    public ModelBase()
    {

    }

    public ModelBase(int id)
    {
        Id = id;
    }

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
