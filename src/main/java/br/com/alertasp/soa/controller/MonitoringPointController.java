package br.com.alertasp.soa.controller;

import br.com.alertasp.soa.dto.ApiError;
import br.com.alertasp.soa.dto.MonitoringPointCreateRequest;
import br.com.alertasp.soa.dto.MonitoringPointResponse;
import br.com.alertasp.soa.service.MonitoringPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/monitoring-points")
@Tag(name = "Pontos de monitoramento", description = "Cadastro e consulta dos locais acompanhados pelo ALERTASP.")
public class MonitoringPointController {

    private final MonitoringPointService service;

    public MonitoringPointController(MonitoringPointService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista pontos", description = "Permite filtrar por bairro e situação ativa, com paginação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de pontos retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de consulta inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public Page<MonitoringPointResponse> findAll(
            @RequestParam(required = false) String neighborhood,
            @RequestParam(required = false) Boolean active,
            @ParameterObject @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100),
                pageable.getSort());
        return service.findAll(neighborhood, active, safePageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta um ponto pelo identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ponto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MonitoringPointResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Ponto não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public MonitoringPointResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um ponto de monitoramento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ponto criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MonitoringPointResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Violação de integridade do banco de dados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<MonitoringPointResponse> create(@Valid @RequestBody MonitoringPointCreateRequest request) {
        MonitoringPointResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/monitoring-points/" + response.id())).body(response);
    }
}
