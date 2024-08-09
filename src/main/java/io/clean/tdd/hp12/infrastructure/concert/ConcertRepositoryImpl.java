package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.domain.concert.model.Concert;
import io.clean.tdd.hp12.domain.concert.port.ConcertRepository;
import io.clean.tdd.hp12.infrastructure.concert.entity.ConcertEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public List<Concert> findByConcertTitleId(long concertTitleId) {
        return concertJpaRepository.findByConcertTitleEntity_Id(concertTitleId).stream()
            .map(ConcertEntity::toModel)
            .toList();
    }

    @Override
    public Concert findByConcertTitleIdAndOccasion(long concertTitleId, LocalDateTime occasion) {
        return concertJpaRepository.findByConcertTitleEntity_IdAndOccasion(concertTitleId, occasion)
            .toModel();
    }

    @Deprecated
    public Concert save(Concert concert) {
        return concertJpaRepository.save(ConcertEntity.from(concert))
            .toModel();
    }
}
