package org.compliance.infrastructure.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sanction-client", url = "http://sanction-service")
public interface SanctionClient {
    
    @GetMapping("/sanctions/companies/{registrationNumber}")
    void getCompanyRisk(@PathVariable("registrationNumber") String registrationNumber);
}
