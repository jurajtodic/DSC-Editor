package com.eneamathos.dscwebapi.DAL.Repositories;

import com.eneamathos.dscwebapi.DAL.Entities.DomainConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDomainConfigurationRepository extends JpaRepository<DomainConfigurationEntity, Integer> {

}
