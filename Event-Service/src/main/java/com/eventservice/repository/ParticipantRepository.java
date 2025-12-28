package com.eventservice.repository;

import com.eventservice.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByEventId(Long eventId);
    List<Participant> findByUserId(Long userId);
    Optional<Participant> findByEventIdAndUserId(Long eventId, Long userId);
    Integer countByEventIdAndStatus(Long eventId, String status);
}