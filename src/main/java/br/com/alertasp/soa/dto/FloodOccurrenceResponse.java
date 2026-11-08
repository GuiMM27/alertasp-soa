package br.com.alertasp.soa.dto;

import br.com.alertasp.soa.enums.OccurrenceSource;
import br.com.alertasp.soa.enums.OccurrenceStatus;
import br.com.alertasp.soa.enums.Severity;

import java.time.OffsetDateTime;

public record FloodOccurrenceResponse(
        Long id,
        MonitoringPointSummary monitoringPoint,
        String description,
        Severity severity,
        OccurrenceStatus status,
        OccurrenceSource source,
        OffsetDateTime occurredAt,
        OffsetDateTime reportedAt,
        OffsetDateTime updatedAt
) {
}
