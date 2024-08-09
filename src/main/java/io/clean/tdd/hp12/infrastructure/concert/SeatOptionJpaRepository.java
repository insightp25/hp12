package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.infrastructure.concert.entity.SeatOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated
public interface SeatOptionJpaRepository extends JpaRepository<SeatOptionEntity, Long> {
}
