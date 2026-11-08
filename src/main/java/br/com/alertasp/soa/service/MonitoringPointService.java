package br.com.alertasp.soa.service;

import br.com.alertasp.soa.dto.MonitoringPointCreateRequest;
import br.com.alertasp.soa.dto.MonitoringPointResponse;
import br.com.alertasp.soa.entity.MonitoringPoint;
import br.com.alertasp.soa.exception.ResourceNotFoundException;
import br.com.alertasp.soa.repository.MonitoringPointRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MonitoringPointService {

    private static final Logger log = LoggerFactory.getLogger(MonitoringPointService.class);

    private final MonitoringPointRepository repository;

    public MonitoringPointService(MonitoringPointRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<MonitoringPointResponse> findAll(String neighborhood, Boolean active, Pageable pageable) {
        Specification<MonitoringPoint> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();

        if (neighborhood != null && !neighborhood.isBlank()) {
            String normalized = "%" + neighborhood.trim().toLowerCase() + "%";
            specification = specification.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("neighborhood")), normalized));
        }
        if (active != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("active"), active));
        }

        return repository.findAll(specification, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MonitoringPointResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public MonitoringPointResponse create(MonitoringPointCreateRequest request) {
        MonitoringPoint point = new MonitoringPoint(
                request.name().trim(), request.street().trim(), request.neighborhood().trim(),
                request.city().trim(), request.latitude(), request.longitude(), request.active());
        MonitoringPoint saved = repository.save(point);
        log.info("Ponto de monitoramento cadastrado. id={}", saved.getId());
        return toResponse(saved);
    }

    private MonitoringPoint getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de monitoramento não encontrado: " + id));
    }

    private MonitoringPointResponse toResponse(MonitoringPoint point) {
        return new MonitoringPointResponse(point.getId(), point.getName(), point.getStreet(),
                point.getNeighborhood(), point.getCity(), point.getLatitude(), point.getLongitude(), point.getActive());
    }
}
