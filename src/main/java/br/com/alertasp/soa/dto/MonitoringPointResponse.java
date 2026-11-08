package br.com.alertasp.soa.dto;

public record MonitoringPointResponse(
        Long id,
        String name,
        String street,
        String neighborhood,
        String city,
        Double latitude,
        Double longitude,
        Boolean active
) {
}
