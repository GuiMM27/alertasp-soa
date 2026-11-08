package br.com.alertasp.soa.service;

import br.com.alertasp.soa.dto.FloodOccurrenceCreateRequest;
import br.com.alertasp.soa.dto.FloodOccurrenceResponse;
import br.com.alertasp.soa.dto.MonitoringPointSummary;
import br.com.alertasp.soa.entity.FloodOccurrence;
import br.com.alertasp.soa.entity.MonitoringPoint;
import br.com.alertasp.soa.enums.OccurrenceStatus;
import br.com.alertasp.soa.exception.BusinessRuleException;
import br.com.alertasp.soa.exception.ResourceNotFoundException;
import br.com.alertasp.soa.repository.FloodOccurrenceRepository;
import br.com.alertasp.soa.repository.MonitoringPointRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class FloodOccurrenceService {

    private static final Logger log = LoggerFactory.getLogger(FloodOccurrenceService.class);

    private final FloodOccurrenceRepository occurrenceRepository;
    private final MonitoringPointRepository pointRepository;
    private final Clock clock;

    public FloodOccurrenceService(FloodOccurrenceRepository occurrenceRepository,
                                  MonitoringPointRepository pointRepository,
                                  Clock clock) {
        this.occurrenceRepository = occurrenceRepository;
        this.pointRepository = pointRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public Page<FloodOccurrenceResponse> findAll(OccurrenceStatus status, String neighborhood,
                                                  OffsetDateTime from, OffsetDateTime to,
                                                  Pageable pageable) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("O parâmetro 'from' deve ser anterior ou igual a 'to'.");
        }

        Specification<FloodOccurrence> specification = (root, query, cb) -> cb.conjunction();

        if (status != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (neighborhood != null && !neighborhood.isBlank()) {
            String normalized = "%" + neighborhood.trim().toLowerCase() + "%";
            specification = specification.and((root, query, cb) ->
                    cb.like(cb.lower(root.join("monitoringPoint").get("neighborhood")), normalized));
        }
        if (from != null) {
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("occurredAt"), from));
        }
        if (to != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("occurredAt"), to));
        }

        return occurrenceRepository.findAll(specification, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public FloodOccurrenceResponse findById(Long id) {
        return toResponse(getOccurrence(id));
    }

    @Transactional
    public FloodOccurrenceResponse create(FloodOccurrenceCreateRequest request) {
        MonitoringPoint point = pointRepository.findById(request.monitoringPointId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ponto de monitoramento não encontrado: " + request.monitoringPointId()));

        if (!Boolean.TRUE.equals(point.getActive())) {
            throw new BusinessRuleException("Não é possível registrar ocorrência em um ponto inativo.");
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        FloodOccurrence occurrence = new FloodOccurrence(point, request.description().trim(),
                request.severity(), request.source(), request.occurredAt(), now);

        FloodOccurrence saved = occurrenceRepository.save(occurrence);
        log.info("Ocorrência registrada. id={}, pontoId={}, status={}", saved.getId(),
                point.getId(), saved.getStatus());
        return toResponse(saved);
    }

    @Transactional
    public FloodOccurrenceResponse updateStatus(Long id, OccurrenceStatus newStatus) {
        FloodOccurrence occurrence = getOccurrence(id);
        if (!occurrence.getStatus().canTransitionTo(newStatus)) {
            throw new BusinessRuleException("Transição de status não permitida: "
                    + occurrence.getStatus() + " -> " + newStatus);
        }

        occurrence.changeStatus(newStatus, OffsetDateTime.now(clock));
        FloodOccurrence saved = occurrenceRepository.save(occurrence);
        log.info("Status da ocorrência atualizado. id={}, status={}", saved.getId(), saved.getStatus());
        return toResponse(saved);
    }

    private FloodOccurrence getOccurrence(Long id) {
        return occurrenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada: " + id));
    }

    private FloodOccurrenceResponse toResponse(FloodOccurrence occurrence) {
        MonitoringPoint point = occurrence.getMonitoringPoint();
        MonitoringPointSummary summary = new MonitoringPointSummary(point.getId(), point.getName(),
                point.getStreet(), point.getNeighborhood(), point.getCity());

        return new FloodOccurrenceResponse(occurrence.getId(), summary, occurrence.getDescription(),
                occurrence.getSeverity(), occurrence.getStatus(), occurrence.getSource(),
                occurrence.getOccurredAt(), occurrence.getReportedAt(), occurrence.getUpdatedAt());
    }
}
