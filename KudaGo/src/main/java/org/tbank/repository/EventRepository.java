package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.tbank.models.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event>  {
    @Query("SELECT  e FROM Event e JOIN FETCH e.location WHERE e.location.id = :locationId")
    List<Event> findByLocationId(Long locationId);

}
