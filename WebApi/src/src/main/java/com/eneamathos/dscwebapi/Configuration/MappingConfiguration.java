package com.eneamathos.dscwebapi.Configuration;

import com.eneamathos.dscwebapi.DAL.Entities.UserEntity;
import com.eneamathos.dscwebapi.Models.User;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;

@Configuration
public class MappingConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ModelMapper modelMapper()
    {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.getConfiguration().setDeepCopyEnabled(true);

        // PropertyMaps
        getPropertyMaps().forEach(mapper::addMappings);

        return mapper;
    }

    private ArrayList<PropertyMap> getPropertyMaps()
    {
        ArrayList<PropertyMap> mappings = new ArrayList<PropertyMap>();

        // User
        mappings.add(new PropertyMap<User, UserEntity>(){
            protected void configure() {
                map().setPasswordHash(source.getPassword());
            }
        });

        return mappings;
    }
}
