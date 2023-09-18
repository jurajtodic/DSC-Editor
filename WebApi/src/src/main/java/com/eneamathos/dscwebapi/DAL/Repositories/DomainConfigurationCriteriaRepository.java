package com.eneamathos.dscwebapi.DAL.Repositories;

import com.eneamathos.dscwebapi.Common.DomainConfigurationFilterParameters;
import com.eneamathos.dscwebapi.Common.PageModel;
import com.eneamathos.dscwebapi.Configuration.AsyncConfiguration;
import com.eneamathos.dscwebapi.DAL.Entities.DomainConfigurationEntity;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Repository
public class DomainConfigurationCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public DomainConfigurationCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Async(AsyncConfiguration.AsyncExecutorName)
    public CompletableFuture<Page<DomainConfigurationEntity>> findAllWithFiltersAsync(PageModel pageModel, DomainConfigurationFilterParameters filters){

        CriteriaQuery<DomainConfigurationEntity> criteriaQuery = criteriaBuilder.createQuery(DomainConfigurationEntity.class);
        Root<DomainConfigurationEntity> entityRoot = criteriaQuery.from(DomainConfigurationEntity.class);

        Predicate predicate = getPredicate(filters, entityRoot);

        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(getOrders(pageModel.getSortBy(), entityRoot));
        

        TypedQuery<DomainConfigurationEntity> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(pageModel.getPageNumber() * pageModel.getPageSize());
        typedQuery.setMaxResults(pageModel.getPageSize());

        Pageable pageable = PageRequest.of(
                pageModel.getPageNumber(),
                pageModel.getPageSize(),
                Sort.by(getOrders(pageModel.getSortBy())));

        Long totalCount = getTotalCount(predicate);

        return CompletableFuture.completedFuture(new PageImpl<>(typedQuery.getResultList(), pageable, totalCount));
    }

    private Predicate getPredicate(DomainConfigurationFilterParameters filter, Root<DomainConfigurationEntity> domainConfigurationEntityRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filter.getType())){
            predicates.add(criteriaBuilder.like(domainConfigurationEntityRoot.get("type"), "%" + filter.getType() + "%"));
        }

        if (Objects.nonNull(filter.getName())){
            predicates.add(criteriaBuilder.like(domainConfigurationEntityRoot.get("name"), "%" + filter.getName() + "%"));
        }

        if (Objects.nonNull(filter.isActive())){
            predicates.add(criteriaBuilder.equal(domainConfigurationEntityRoot.get("active"), filter.isActive()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Long getTotalCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<DomainConfigurationEntity> countRoot = countQuery.from(DomainConfigurationEntity.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Sort.Order> getOrders(String[] sortBy) {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();

        String[] sort = sortBy;
        for (int i = 0; i < sort.length; i += 2)
        {
            String propertyName = sort[i];
            Sort.Direction direction = sort[i + 1].equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

            orders.add(new Sort.Order(direction, propertyName));
        }

        return orders;
    }

    private List<Order> getOrders(String[] sortBy, Root<DomainConfigurationEntity> entityRoot) {
        List<Order> orders = new ArrayList<Order>();

        String[] sort = sortBy;
        for (int i = 0; i < sort.length; i += 2)
        {
            String propertyName = sort[i];
            Sort.Direction direction = sort[i + 1].equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

            if (direction.equals(Sort.Direction.ASC))
            {
                orders.add(criteriaBuilder.asc(entityRoot.get(propertyName)));
            }
            else
            {
                orders.add(criteriaBuilder.desc(entityRoot.get(propertyName)));
            }
        }

        return orders;
    }
}
