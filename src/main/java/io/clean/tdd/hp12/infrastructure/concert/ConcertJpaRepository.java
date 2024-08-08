package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.infrastructure.concert.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertJpaRepository extends JpaRepository<ConcertEntity, Long> {
    List<ConcertEntity> findByConcertTitle_Id(long concertTitleId);

    ConcertEntity findByConcertTitle_IdAndOccasion(long concertTitleId, LocalDateTime occasion);
}
