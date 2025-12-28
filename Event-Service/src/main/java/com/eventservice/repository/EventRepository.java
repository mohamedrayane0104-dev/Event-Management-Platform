package com.eventservice.repository;

import com.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByActive(Boolean active);
    List<Event> findByOrganizerId(Long organizerId);
    Optional<Event> findByIdAndActive(Long id, Boolean active);
}