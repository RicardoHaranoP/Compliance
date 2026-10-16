package org.compliance.infrastructure.persistence.repository;

import org.compliance.domain.Company;
import org.compliance.domain.CompanyRepository;
import org.compliance.infrastructure.persistence.entity.CompanyEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Repository
@Service 
@Component 
public class InMemoryCompanyRepository implements CompanyRepository {
    private final CompanyEntityRepository repository;

    public InMemoryCompanyRepository(CompanyEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Company save(Company company) {
        var entity = CompanyEntity.from(company);
        return repository.save(entity).toDomain();
    }
}
