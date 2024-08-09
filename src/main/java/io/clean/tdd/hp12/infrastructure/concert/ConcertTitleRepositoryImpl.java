package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.domain.concert.model.ConcertTitle;
import io.clean.tdd.hp12.domain.concert.port.ConcertTitleRepository;
import io.clean.tdd.hp12.infrastructure.concert.entity.ConcertTitleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Deprecated
@Repository
@RequiredArgsConstructor
public class ConcertTitleRepositoryImpl implements ConcertTitleRepository {

    private final ConcertTitleJpaRepository concertTitleJpaRepository;

    public ConcertTitle save(ConcertTitle concertTitle) {
        return concertTitleJpaRepository.save(ConcertTitleEntity.from(concertTitle))
            .toModel();
    }
}
