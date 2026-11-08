package br.com.alertasp.soa.repository;

import br.com.alertasp.soa.entity.FloodOccurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FloodOccurrenceRepository extends JpaRepository<FloodOccurrence, Long>,
        JpaSpecificationExecutor<FloodOccurrence> {
}
