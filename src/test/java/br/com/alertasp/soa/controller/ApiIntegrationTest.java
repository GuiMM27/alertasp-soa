package br.com.alertasp.soa.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsDemonstrationMonitoringPointsFromDatabase() throws Exception {
        mockMvc.perform(get("/api/v1/monitoring-points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    void createsOccurrenceLinkedToExistingPoint() throws Exception {
        String request = """
                {
                  "monitoringPointId": 1,
                  "description": "Teste automatizado de ocorrência",
                  "severity": "MEDIUM",
                  "source": "DEMONSTRATION",
                  "occurredAt": "2026-11-05T11:30:00-03:00"
                }
                """;

        mockMvc.perform(post("/api/v1/flood-occurrences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REPORTED"))
                .andExpect(jsonPath("$.monitoringPoint.id").value(1));
    }

    @Test
    void rejectsOccurrenceWhenPointDoesNotExist() throws Exception {
        String request = """
                {
                  "monitoringPointId": 9999,
                  "description": "Ponto inexistente",
                  "severity": "LOW",
                  "source": "DEMONSTRATION",
                  "occurredAt": "2026-11-05T11:30:00-03:00"
                }
                """;

        mockMvc.perform(post("/api/v1/flood-occurrences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void openApiDocumentationIsAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/flood-occurrences']").exists());
    }
}
