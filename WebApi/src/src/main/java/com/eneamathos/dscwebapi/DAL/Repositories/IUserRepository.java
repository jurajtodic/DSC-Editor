package com.eneamathos.dscwebapi.DAL.Repositories;

import org.springframework.data.repository.CrudRepository;
import com.eneamathos.dscwebapi.DAL.Entities.UserEntity;

public interface IUserRepository extends CrudRepository<UserEntity, Integer> {

}
