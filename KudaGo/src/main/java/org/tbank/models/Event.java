package org.tbank.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String title;
    @JsonProperty("price")
    private String price;
    @Column(name = "date")
    private LocalDateTime startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @JsonSetter("dates")
    public void setDates(List<Map<String, Object>> dates) {
        if (dates != null && !dates.isEmpty()) {
            Map<String, Object> firstDate = dates.get(0);
            Long startTimestamp = ((Number) firstDate.get("start")).longValue();
            this.startDate = Instant.ofEpochSecond(startTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
    }
}
