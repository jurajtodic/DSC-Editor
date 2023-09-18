package com.eneamathos.dscwebapi.Services;

import com.eneamathos.dscwebapi.ANTLR4.DSCValidator;
import com.eneamathos.dscwebapi.ANTLR4.ValidationResult;
import com.eneamathos.dscwebapi.Common.DomainConfigurationFilterParameters;
import com.eneamathos.dscwebapi.Common.PageModel;
import com.eneamathos.dscwebapi.Configuration.AsyncConfiguration;
import com.eneamathos.dscwebapi.DAL.Entities.DomainConfigurationEntity;
import com.eneamathos.dscwebapi.DAL.Repositories.DomainConfigurationCriteriaRepository;
import com.eneamathos.dscwebapi.DAL.Repositories.IDomainConfigurationRepository;
import com.eneamathos.dscwebapi.Models.DomainConfiguration;
import com.eneamathos.dscwebapi.Models.DomainConfigurationServiceResult;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DomainConfigurationServiceImpl implements DomainConfigurationService{

    private final IDomainConfigurationRepository domainConfigurationRepository;
    private final DomainConfigurationCriteriaRepository domainConfigurationCriteriaRepository;
    private final DSCValidator dscValidator;
    private final ModelMapper mapper;
    private final Logger logger;

    @Autowired
    public DomainConfigurationServiceImpl(
            IDomainConfigurationRepository domainConfigurationRepository,
            DomainConfigurationCriteriaRepository domainConfigurationCriteriaRepository,
            DSCValidator dscValidator,
            ModelMapper mapper,
            Logger logger
    ){
        this.domainConfigurationRepository = domainConfigurationRepository;
        this.domainConfigurationCriteriaRepository = domainConfigurationCriteriaRepository;
        this.dscValidator = dscValidator;
        this.mapper = mapper;
        this.logger = logger;
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    @Transactional
    public CompletableFuture<DomainConfiguration> activateAsync(DomainConfiguration domainConfiguration)
            throws ExecutionException, InterruptedException
    {
        PageModel page = new PageModel();

        DomainConfigurationFilterParameters filters = new DomainConfigurationFilterParameters();
        filters.setType(domainConfiguration.getType());
        filters.setActive(true);

        List<DomainConfigurationEntity> entitiesForUpdate = new ArrayList<DomainConfigurationEntity>();

        Page<DomainConfigurationEntity> entities = domainConfigurationCriteriaRepository.findAllWithFiltersAsync(page, filters).get();
        Optional<DomainConfigurationEntity> oldEntity = entities.stream().findFirst();
        oldEntity.get().setActive(false);
        entitiesForUpdate.add(oldEntity.get());

        Optional<DomainConfigurationEntity> entity = domainConfigurationRepository.findById(domainConfiguration.getId());
        entity.get().setActive(true);
        entitiesForUpdate.add(entity.get());

        try
        {
            domainConfigurationRepository.saveAll(entitiesForUpdate);

            logger.info("Domain configuration has been activated\n {}", domainConfiguration.toString());

        } catch (UnsupportedOperationException e)
        {
            logger.error(e.getMessage());

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return CompletableFuture.completedFuture(mapper.map(entity.get(), DomainConfiguration.class));
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<Void> deleteAsync(DomainConfiguration domainConfiguration) {
        Optional<DomainConfigurationEntity> entityToDelete = Optional.of(domainConfigurationRepository.getById(domainConfiguration.getId()));
        domainConfigurationRepository.delete(entityToDelete.get());
        return new CompletableFuture<>();
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<Page<DomainConfiguration>> findAsync(PageModel pageModel, DomainConfigurationFilterParameters filters)
            throws ExecutionException, InterruptedException {

        Page<DomainConfigurationEntity> entities = domainConfigurationCriteriaRepository.findAllWithFiltersAsync(pageModel, filters).get();
        Page<DomainConfiguration> result = entities.map(entity -> mapper.map(entity, DomainConfiguration.class));

        return CompletableFuture.completedFuture(result);
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<Optional<DomainConfiguration>> getAsync(int id)
    {
        Optional<DomainConfigurationEntity> entity = domainConfigurationRepository.findById(id);
        Optional<DomainConfiguration> model = Optional.ofNullable(mapper.map(entity, DomainConfiguration.class));

        return CompletableFuture.completedFuture(model);
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    @Transactional
    public CompletableFuture<DomainConfigurationServiceResult> saveAsync(DomainConfiguration domainConfiguration)
            throws IOException, ExecutionException, InterruptedException
    {
        DomainConfigurationServiceResult serviceResult = new DomainConfigurationServiceResult();

        List<ValidationResult> result = validateAsync(domainConfiguration.getContent()).get();
        serviceResult.setValidationResultList(result);

        if (!result.isEmpty() && !result.get(0).getIsValid()){
            return CompletableFuture.completedFuture(serviceResult);
        }

        List<DomainConfigurationEntity> entitiesToSave = new ArrayList<DomainConfigurationEntity>();
        if (domainConfiguration.isActive())
        {
            PageModel pageModel = new PageModel();
            pageModel.setPageSize(100);

            DomainConfigurationFilterParameters filters = new DomainConfigurationFilterParameters();
            filters.setActive(true);
            filters.setType(domainConfiguration.getType());

            Page<DomainConfigurationEntity> entities = domainConfigurationCriteriaRepository.findAllWithFiltersAsync(pageModel, filters).get();

            if (!entities.isEmpty())
            {
                entities.forEach(entity -> entity.setActive(false));
            }

            // NOTE: entities.ToList() method throws UnsupportedOperationException on add()
            entities.forEach(entitiesToSave::add);
        }

        DomainConfigurationEntity entity = mapper.map(domainConfiguration, DomainConfigurationEntity.class);

        entitiesToSave.add(entity);

        try
        {
            domainConfigurationRepository.saveAll(entitiesToSave);

            logger.info("Domain configuration has been saved\n {}", domainConfiguration.toString());

        } catch (UnsupportedOperationException e)
        {
            logger.error(e.getMessage());

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        serviceResult.setDomainConfiguration(mapper.map(entity, DomainConfiguration.class));

        return CompletableFuture.completedFuture(serviceResult);
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<List<ValidationResult>> validateAsync(byte[] bytes) throws IOException {
        return CompletableFuture.completedFuture(dscValidator.validate(bytes));
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<DomainConfigurationServiceResult> updateAsync(
            DomainConfiguration oldConfiguration,
            DomainConfiguration newConfiguration
    ) throws IOException, ExecutionException, InterruptedException {
        DomainConfigurationServiceResult serviceResult = new DomainConfigurationServiceResult();

        List<ValidationResult> result = validateAsync(newConfiguration.getContent()).get();
        serviceResult.setValidationResultList(result);

        if (!result.get(0).getIsValid()){
            logger.info("SyntaxError\n{}", result);
            return CompletableFuture.completedFuture(serviceResult);
        }

        mapper.map(newConfiguration, oldConfiguration);
        DomainConfigurationEntity newEntity = mapper.map(newConfiguration, DomainConfigurationEntity.class);

        try
        {
            domainConfigurationRepository.save(newEntity);
            logger.info("Configuration has been updated\n{}", newEntity);
        }
        catch (Exception e)
        {
            logger.error(String.format("Something goes wrong while updating configuration: %s", e.getMessage()));
        }

        serviceResult.setDomainConfiguration(mapper.map(newEntity, DomainConfiguration.class));

        return CompletableFuture.completedFuture(serviceResult);
    }
}
