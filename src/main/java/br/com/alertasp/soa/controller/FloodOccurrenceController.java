package br.com.alertasp.soa.controller;

import br.com.alertasp.soa.dto.FloodOccurrenceCreateRequest;
import br.com.alertasp.soa.dto.FloodOccurrenceResponse;
import br.com.alertasp.soa.dto.OccurrenceStatusUpdateRequest;
import br.com.alertasp.soa.enums.OccurrenceStatus;
import br.com.alertasp.soa.service.FloodOccurrenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/flood-occurrences")
@Tag(name = "Ocorrências de alagamento", description = "Registro, consulta e acompanhamento das ocorrências.")
public class FloodOccurrenceController {

    private final FloodOccurrenceService service;

    public FloodOccurrenceController(FloodOccurrenceService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista ocorrências", description = "Filtros opcionais: status, bairro e intervalo de ocorrência. A lista é paginada.")
    public Page<FloodOccurrenceResponse> findAll(
            @RequestParam(required = false) OccurrenceStatus status,
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @PageableDefault(size = 20, sort = "reportedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100),
                pageable.getSort());
        return service.findAll(status, neighborhood, from, to, safePageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta uma ocorrência pelo identificador")
    public FloodOccurrenceResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @Operation(summary = "Registra uma ocorrência", description = "Toda nova ocorrência começa com status REPORTED.")
    public ResponseEntity<FloodOccurrenceResponse> create(@Valid @RequestBody FloodOccurrenceCreateRequest request) {
        FloodOccurrenceResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/flood-occurrences/" + response.id())).body(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza o status de uma ocorrência", description = "Apenas transições permitidas pelo domínio são aceitas.")
    public FloodOccurrenceResponse updateStatus(@PathVariable Long id,
                                                 @Valid @RequestBody OccurrenceStatusUpdateRequest request) {
        return service.updateStatus(id, request.status());
    }
}
