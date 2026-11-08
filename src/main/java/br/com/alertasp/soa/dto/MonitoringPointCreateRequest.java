package br.com.alertasp.soa.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MonitoringPointCreateRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 160) String street,
        @NotBlank @Size(max = 100) String neighborhood,
        @NotBlank @Size(max = 80) String city,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        Boolean active
) {
}
