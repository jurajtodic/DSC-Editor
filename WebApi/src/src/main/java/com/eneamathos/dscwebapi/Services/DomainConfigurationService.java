package com.eneamathos.dscwebapi.Services;

import com.eneamathos.dscwebapi.ANTLR4.ValidationResult;
import com.eneamathos.dscwebapi.Common.DomainConfigurationFilterParameters;
import com.eneamathos.dscwebapi.Common.PageModel;
import com.eneamathos.dscwebapi.Models.DomainConfiguration;
import com.eneamathos.dscwebapi.Models.DomainConfigurationServiceResult;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DomainConfigurationService {
    public CompletableFuture<DomainConfiguration> activateAsync(DomainConfiguration domainConfiguration)
            throws ExecutionException, InterruptedException;
    public CompletableFuture<Void> deleteAsync(DomainConfiguration domainConfiguration);
    public CompletableFuture<Page<DomainConfiguration>> findAsync(PageModel pageModel, DomainConfigurationFilterParameters filters)
            throws ExecutionException, InterruptedException;
    public CompletableFuture<Optional<DomainConfiguration>> getAsync(int id);
    public CompletableFuture<DomainConfigurationServiceResult> saveAsync(DomainConfiguration domainConfiguration)
            throws IOException, ExecutionException, InterruptedException;
    public CompletableFuture<List<ValidationResult>> validateAsync(byte[] bytes)
            throws IOException;

}
