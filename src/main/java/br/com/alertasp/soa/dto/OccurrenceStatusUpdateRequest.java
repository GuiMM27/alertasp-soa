package br.com.alertasp.soa.dto;

import br.com.alertasp.soa.enums.OccurrenceStatus;
import jakarta.validation.constraints.NotNull;

public record OccurrenceStatusUpdateRequest(@NotNull OccurrenceStatus status) {
}
