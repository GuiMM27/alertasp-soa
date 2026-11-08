package br.com.alertasp.soa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "monitoring_points")
public class MonitoringPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160)
    private String street;

    @Column(nullable = false, length = 100)
    private String neighborhood;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Boolean active;

    protected MonitoringPoint() {
        // Construtor exigido pelo JPA.
    }

    public MonitoringPoint(String name, String street, String neighborhood, String city,
                           Double latitude, Double longitude, Boolean active) {
        this.name = name;
        this.street = street;
        this.neighborhood = neighborhood;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active == null || active;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStreet() { return street; }
    public String getNeighborhood() { return neighborhood; }
    public String getCity() { return city; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Boolean getActive() { return active; }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
