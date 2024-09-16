package io.clean.tdd.hp12.infrastructure.point;

import io.clean.tdd.hp12.domain.point.model.PointHistory;
import io.clean.tdd.hp12.domain.point.port.PointHistoryRepository;
import io.clean.tdd.hp12.infrastructure.point.entity.PointHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public List<PointHistory> findAllByUserId(long userId) {
        return pointHistoryJpaRepository.findAllByUserEntity_Id(userId).stream()
            .map(PointHistoryEntity::toModel)
            .toList();
    }

    @Override
    public PointHistory save(PointHistory pointHistory) {
        return pointHistoryJpaRepository.save(PointHistoryEntity.from(pointHistory))
            .toModel();
    }
}
