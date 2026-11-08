package br.com.alertasp.soa.repository;

import br.com.alertasp.soa.entity.MonitoringPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MonitoringPointRepository extends JpaRepository<MonitoringPoint, Long>,
        JpaSpecificationExecutor<MonitoringPoint> {
}
