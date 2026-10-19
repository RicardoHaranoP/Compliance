package org.compliance.application;

import org.compliance.domain.Company;
import org.compliance.infrastructure.rest.client.SanctionClient;
import org.springframework.stereotype.Service;

@Service 
public class AnalyzeCompanyRiskUseCase {

    private final SanctionClient sanctionClient;

    public AnalyzeCompanyRiskUseCase(SanctionClient sanctionClient) {
        this.sanctionClient = sanctionClient;
    }

    public void execute(Company domain) {
        var sanctions =sanctionClient.getCompanyRisk(domain.getRegistrationNumber());
    }
}
