package main.java.org.compliance.domain;

import java.util.List;

public record ComplianceScreening (List<SanctionIdentity> sanctions, AMLProfile amlProfile){
    public record SanctionIdentity(
        String name,
        String sourceList,
        String reason,
        double confidence
    ){
        
    }

    public record AmlProfile(
        int riskScore,
        List<String> riskFlags,
        boolean isPepPresent,
        List<PoliticalExposure> exposures
    ){
        public record PoliticalExposures(
            String personName,
            String publicOffice
        ) {}
    }
}
