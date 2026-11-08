package br.com.alertasp.soa.dto;

import br.com.alertasp.soa.enums.OccurrenceSource;
import br.com.alertasp.soa.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record FloodOccurrenceCreateRequest(
        @NotNull Long monitoringPointId,
        @NotBlank @Size(max = 500) String description,
        @NotNull Severity severity,
        @NotNull OccurrenceSource source,
        @NotNull OffsetDateTime occurredAt
) {
}
