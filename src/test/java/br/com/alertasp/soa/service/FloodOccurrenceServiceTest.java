package br.com.alertasp.soa.service;

import br.com.alertasp.soa.dto.FloodOccurrenceCreateRequest;
import br.com.alertasp.soa.enums.OccurrenceSource;
import br.com.alertasp.soa.enums.OccurrenceStatus;
import br.com.alertasp.soa.enums.Severity;
import br.com.alertasp.soa.entity.FloodOccurrence;
import br.com.alertasp.soa.entity.MonitoringPoint;
import br.com.alertasp.soa.exception.BusinessRuleException;
import br.com.alertasp.soa.exception.ResourceNotFoundException;
import br.com.alertasp.soa.repository.FloodOccurrenceRepository;
import br.com.alertasp.soa.repository.MonitoringPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FloodOccurrenceServiceTest {

    @Mock
    private FloodOccurrenceRepository occurrenceRepository;

    @Mock
    private MonitoringPointRepository pointRepository;

    private FloodOccurrenceService service;
    private Clock clock;
    private MonitoringPoint point;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-11-05T12:00:00Z"), ZoneOffset.UTC);
        service = new FloodOccurrenceService(occurrenceRepository, pointRepository, clock);
        point = new MonitoringPoint("Ponto de teste", "Rua de teste", "Centro", "São Paulo",
                -23.55, -46.63, true);
    }

    @Test
    void creatingOccurrenceStartsWithReportedStatus() {
        when(pointRepository.findById(1L)).thenReturn(Optional.of(point));
        when(occurrenceRepository.save(any(FloodOccurrence.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FloodOccurrenceCreateRequest request = new FloodOccurrenceCreateRequest(
                1L, "Água acumulada na via", Severity.HIGH, OccurrenceSource.CITIZEN_REPORT,
                OffsetDateTime.parse("2026-11-05T11:45:00-03:00"));

        var response = service.create(request);

        assertEquals(OccurrenceStatus.REPORTED, response.status());
        assertEquals(OccurrenceSource.CITIZEN_REPORT, response.source());
        assertEquals("Ponto de teste", response.monitoringPoint().name());
        verify(occurrenceRepository).save(any(FloodOccurrence.class));
    }

    @Test
    void creatingOccurrenceForUnknownPointFails() {
        when(pointRepository.findById(999L)).thenReturn(Optional.empty());
        FloodOccurrenceCreateRequest request = new FloodOccurrenceCreateRequest(
                999L, "Registro", Severity.LOW, OccurrenceSource.CITIZEN_REPORT,
                OffsetDateTime.parse("2026-11-05T11:45:00-03:00"));

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
        verify(occurrenceRepository, never()).save(any());
    }

    @Test
    void invalidStatusTransitionFails() {
        FloodOccurrence occurrence = new FloodOccurrence(point, "Registro", Severity.MEDIUM,
                OccurrenceSource.DEMONSTRATION, OffsetDateTime.now(clock), OffsetDateTime.now(clock));
        when(occurrenceRepository.findById(1L)).thenReturn(Optional.of(occurrence));

        assertThrows(BusinessRuleException.class,
                () -> service.updateStatus(1L, OccurrenceStatus.IN_PROGRESS));
        verify(occurrenceRepository, never()).save(any());
    }
}
