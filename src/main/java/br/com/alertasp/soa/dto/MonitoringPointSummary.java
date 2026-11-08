package br.com.alertasp.soa.dto;

public record MonitoringPointSummary(
        Long id,
        String name,
        String street,
        String neighborhood,
        String city
) {
}
