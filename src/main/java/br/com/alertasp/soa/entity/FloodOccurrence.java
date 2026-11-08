package br.com.alertasp.soa.entity;

import br.com.alertasp.soa.enums.OccurrenceSource;
import br.com.alertasp.soa.enums.OccurrenceStatus;
import br.com.alertasp.soa.enums.Severity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "flood_occurrences")
public class FloodOccurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "monitoring_point_id", nullable = false)
    private MonitoringPoint monitoringPoint;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OccurrenceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private OccurrenceSource source;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "reported_at", nullable = false)
    private OffsetDateTime reportedAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected FloodOccurrence() {
        // Construtor exigido pelo JPA.
    }

    public FloodOccurrence(MonitoringPoint monitoringPoint, String description, Severity severity,
                           OccurrenceSource source, OffsetDateTime occurredAt, OffsetDateTime now) {
        this.monitoringPoint = monitoringPoint;
        this.description = description;
        this.severity = severity;
        this.source = source;
        this.occurredAt = occurredAt;
        this.status = OccurrenceStatus.REPORTED;
        this.reportedAt = now;
        this.updatedAt = now;
    }

    public void changeStatus(OccurrenceStatus newStatus, OffsetDateTime now) {
        this.status = newStatus;
        this.updatedAt = now;
    }

    public Long getId() { return id; }
    public MonitoringPoint getMonitoringPoint() { return monitoringPoint; }
    public String getDescription() { return description; }
    public Severity getSeverity() { return severity; }
    public OccurrenceStatus getStatus() { return status; }
    public OccurrenceSource getSource() { return source; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public OffsetDateTime getReportedAt() { return reportedAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
