package org.compliance.infrastructure.rest.client;

import java.util.List;

import org.compliance.infrastructure.rest.dto.SanctionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sanction-client", fallback= SanctionClient.Fallback.class)
public interface SanctionClient {
    
    @GetMapping("/sanctions/companies/{registrationNumber}")
    SanctionResult getCompanyRisk(@PathVariable("registrationNumber") String registrationNumber);

    @Component 
    class Fallback implements SanctionClient {
        public SanctionResult getCompanyRisk(String registrationNumber) {
            // Fallback logic here
            return new SanctionResult(List.of());
        }
    }
}
