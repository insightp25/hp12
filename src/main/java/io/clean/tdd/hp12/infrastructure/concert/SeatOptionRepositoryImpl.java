package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.domain.concert.model.SeatOption;
import io.clean.tdd.hp12.domain.concert.port.SeatOptionRepository;
import io.clean.tdd.hp12.infrastructure.concert.entity.SeatOptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Deprecated
@Repository
@RequiredArgsConstructor
public class SeatOptionRepositoryImpl implements SeatOptionRepository {

    private final SeatOptionJpaRepository seatOptionJpaRepository;

    @Override
    public SeatOption save(SeatOption seatOption) {
        return seatOptionJpaRepository.save(SeatOptionEntity.from(seatOption))
            .toModel();
    }
}
