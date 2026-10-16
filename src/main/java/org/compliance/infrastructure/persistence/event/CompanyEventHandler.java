package org.compliance.infrastructure.persistence.event;

import org.compliance.application.AnalyzeCompanyRiskUseCase;
import org.compliance.infrastructure.persistence.entity.CompanyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component 
@RepositoryEventHandler 
public class CompanyEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CompanyEventHandler.class);
    
    private final AnalyzeCompanyRiskUseCase analyzeCompanyRiskUseCase;

    public CompanyEventHandler(AnalyzeCompanyRiskUseCase analyzeCompanyRiskUseCase) {
        this.analyzeCompanyRiskUseCase = analyzeCompanyRiskUseCase;
    }

    @HandleAfterCreate 
    public void handleAfterCreateEvent(CompanyEntity entity) {
        LOG.info("Company entity created: {}", entity);
        this.analyzeCompanyRiskUseCase.execute(entity.toDomain());
    }
}
 