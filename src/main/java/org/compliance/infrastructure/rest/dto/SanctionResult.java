package org.compliance.infrastructure.rest.dto;
import java.util.List;

import org.compliance.domain.ComplianceScreening;

public record SanctionResult(List<SanctionMatch> matches) {
    public record SanctionMatch(
        String entity,
        String list,
        String reason,
        Double confidenceScore
    ) {}

    public List<ComplianceScreening.SanctionIdentity> toDomain() {
        if (matches == null) {
            return List.of();
        }
        return matches.stream()
            .map(match -> new ComplianceScreening.SanctionIdentity(
                match.entity(),
                match.list(),
                match.reason(),
                match.confidenceScore()
            ))
            .toList();
    }
}
