package com.eneamathos.dscwebapi.Controllers;

import com.eneamathos.dscwebapi.ANTLR4.ValidationResult;
import com.eneamathos.dscwebapi.Common.ApiConstants;
import com.eneamathos.dscwebapi.Common.DomainConfigurationFilterParameters;
import com.eneamathos.dscwebapi.Common.PageModel;
import com.eneamathos.dscwebapi.Configuration.AsyncConfiguration;
import com.eneamathos.dscwebapi.Controllers.DTOs.DomainConfigurationInfoDto;
import com.eneamathos.dscwebapi.Models.DomainConfiguration;
import com.eneamathos.dscwebapi.Models.DomainConfigurationServiceResult;
import com.eneamathos.dscwebapi.Services.DomainConfigurationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@RestController
@RequestMapping(path = ApiConstants.ApiPathV1 + "/configurations")
public class DomainConfigurationController {

    private final DomainConfigurationService domainConfigurationService;
    private final ModelMapper mapper;
    private final Logger logger;

    @Autowired
    public DomainConfigurationController(
            DomainConfigurationService domainConfigurationService,
            ModelMapper mapper,
            Logger logger){
        this.domainConfigurationService = domainConfigurationService;
        this.mapper = mapper;
        this.logger = logger;
    }

    @PostMapping(path = "/activate")
    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<ResponseEntity> activateAsync(@RequestBody DomainConfigurationInfoDto configurationInfoDto)
            throws ExecutionException, InterruptedException
    {
        if (Objects.isNull(configurationInfoDto.getId()))
        {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(configurationInfoDto));
        }

        Optional<DomainConfiguration> domainConfiguration = domainConfigurationService.getAsync(configurationInfoDto.getId()).get();

        if (domainConfiguration.isEmpty())
        {
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }

        if (domainConfiguration.get().isActive())
        {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Configuration is already active"));
        }

        DomainConfiguration result = domainConfigurationService.activateAsync(domainConfiguration.get()).get();

        return CompletableFuture.completedFuture(ResponseEntity.ok(mapper.map(result, DomainConfigurationInfoDto.class)));
    }

    @GetMapping()
    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<ResponseEntity> findAsync(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required=false) String type,
            @RequestParam(required=false) String name,
            @RequestParam(required=false) Boolean active
    ) throws ExecutionException, InterruptedException {

        String[] _sort = sort.split(",");

        if (_sort.length % 2 != 0)
        {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Sort parameter is invalid."));
        }

        PageModel pageModel = new PageModel();
        pageModel.setPageNumber(pageNumber);
        pageModel.setPageSize(pageSize);
        pageModel.setSortBy(_sort);

        DomainConfigurationFilterParameters filters = new DomainConfigurationFilterParameters();
        filters.setType(type);
        filters.setName(name);
        filters.setActive(active);

        Page<DomainConfiguration> result = domainConfigurationService.findAsync(pageModel, filters).get();
        Page<DomainConfigurationInfoDto> response = result.map(model -> mapper.map(model, DomainConfigurationInfoDto.class));

        return CompletableFuture.completedFuture(ResponseEntity.ok(response));
    }

    @GetMapping(path = "/{id}")
    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<ResponseEntity> getAsync(@PathVariable("id") int id)
            throws ExecutionException, InterruptedException
    {
        if (id <= 0)
        {
            logger.error("Id is not valid.", id);
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Id is not valid."));
        }

        Optional<DomainConfiguration> domainConfiguration = domainConfigurationService.getAsync(id).get();

        if (domainConfiguration.isEmpty())
        {
            logger.info("Configuration with given id ({}) is not found", id);
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }

        logger.info("Domain configuration retrieved from database {}", domainConfiguration);

        return CompletableFuture.completedFuture(ResponseEntity.ok(domainConfiguration));
    }

    @PostMapping()
    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<ResponseEntity> saveAsync(
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "false") boolean active
    ) throws IOException, ExecutionException, InterruptedException {

        if (file.isEmpty() || type.isEmpty() || name.isEmpty()) {
            logger.info("Request is invalid: {}, {}, {}, {}", type, name, file, active);
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body("Request is invalid."));
        }

        DomainConfiguration domainConfiguration = new DomainConfiguration(type, name, active, file.getBytes());

        DomainConfigurationServiceResult result = domainConfigurationService.saveAsync(domainConfiguration).get();

        if (!result.getValidationResultList().isEmpty()
                && !result.getValidationResultList().get(0).getIsValid()) {
            logger.info("Validator returns SyntaxError {}", result.getValidationResultList());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(result.getValidationResultList()));
        }

        URI location = URI.create(String.format("/configurations/%d", result.getDomainConfiguration().getId()));

        logger.info("Configuration has been created {}", result.getDomainConfiguration());
        return CompletableFuture.completedFuture(ResponseEntity.created(location).build());
    }

    @PostMapping(path = "/validate")
    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<ResponseEntity> validateAsync(@RequestParam MultipartFile file)
            throws IOException, ExecutionException, InterruptedException {

        if (file.isEmpty()) {
            logger.info("File is empty");
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("File is empty"));
        }

        logger.info("File has been accepted");

        Future<List<ValidationResult>> result = domainConfigurationService.validateAsync(file.getBytes());

        logger.info("File has been validated");

        return CompletableFuture.completedFuture(ResponseEntity.ok().body(result.get()));
    }

    @DeleteMapping(path = "/{id}")
    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<ResponseEntity> deleteAsync(@PathVariable("id") int id)
            throws IOException, ExecutionException, InterruptedException {

        if (id <= 0)
        {
            logger.error("Id is not valid.", id);
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Id is not valid."));
        }

        Optional<DomainConfiguration> domainConfiguration = domainConfigurationService.getAsync(id).get();

        if (domainConfiguration.isEmpty())
        {
            logger.info("Configuration with given id ({}) is not found", id);
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }

        if (domainConfiguration.get().isActive())
        {
            logger.info("Configuration with given id ({}) is active and can not be removed.", id);
            return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                    .body("Configuration with given id is active and can not be removed."));
        }

        domainConfigurationService.deleteAsync(domainConfiguration.get());

        logger.info("Domain configuration has been deleted from database {}", domainConfiguration);

        return CompletableFuture.completedFuture(ResponseEntity.ok(domainConfiguration));
    }



    /*@PutMapping(path = "/update")
    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<ResponseEntity> updateAsync(
            @RequestParam int id,
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam MultipartFile file,
            @RequestParam boolean active
    ) throws IOException, ExecutionException, InterruptedException {

        if (file.isEmpty() || type.isEmpty() || name.isEmpty() || !(id > 0))
        {
            logger.info("Request is invalid.", id, type, name, file, active);
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body("Request is invalid."));
        }

        DomainConfiguration oldConfiguration = domainConfigurationService.getAsync(id).get();

        if (oldConfiguration == null)
        {
            logger.info("Configuration with given id is not found", id);
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }

        DomainConfiguration newConfiguration = new DomainConfiguration(id, type, name, active, file.getBytes());

        DomainConfigurationServiceResult result = domainConfigurationService.updateAsync(oldConfiguration, newConfiguration).get();

        if (!result.getValidationResult().getIsValid())
        {
            logger.info("Validator returns SyntaxError", result.getValidationResult());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(result.getValidationResult()));
        }

        logger.info("Configuration has been created.", result.getDomainConfiguration());
        return CompletableFuture.completedFuture(ResponseEntity.ok(result.getDomainConfiguration()));
    }*/
}
