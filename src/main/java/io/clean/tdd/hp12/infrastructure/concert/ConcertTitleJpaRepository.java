package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.infrastructure.concert.entity.ConcertTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated
public interface ConcertTitleJpaRepository extends JpaRepository<ConcertTitleEntity, Long> {
}
