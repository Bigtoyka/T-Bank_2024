package org.tbank.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.tbank.models.Event;

import java.time.LocalDateTime;
@Slf4j
public class EventSpecifications {
    public static Specification<Event> findByName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        };
    }

    public static Specification<Event> findByLocationId(Long locationId) {
        return (root, query, criteriaBuilder) -> {
            if (locationId == null) {
                return criteriaBuilder.conjunction();
            }
            log.info("Filtering by locationId: {}", locationId);
            return criteriaBuilder.equal(root.get("location").get("id"), locationId);
        };
    }

    public static Specification<Event> findByDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null || toDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.between(root.get("date"), fromDate, toDate);
        };
    }
}
