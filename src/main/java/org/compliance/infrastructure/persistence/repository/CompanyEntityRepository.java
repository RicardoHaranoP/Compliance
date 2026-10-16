package org.compliance.infrastructure.persistence.repository;

import java.util.UUID;

import org.compliance.infrastructure.persistence.entity.CompanyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "companies")
public interface CompanyEntityRepository extends CrudRepository<CompanyEntity, UUID>{
    
}
