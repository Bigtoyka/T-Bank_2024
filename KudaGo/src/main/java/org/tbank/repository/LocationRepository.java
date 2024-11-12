package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tbank.models.Location;


public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findBySlug(String slug);

}

