package main.java.org.compliance.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyId {
    private CompanyId id;
    private String name;
    private String registrationNumber;
    private Optional<RiskAssessment> riskAssessment;
}
