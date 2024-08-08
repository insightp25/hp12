package io.clean.tdd.hp12.infrastructure.point;

import io.clean.tdd.hp12.domain.point.model.PointHistory;
import io.clean.tdd.hp12.domain.point.port.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public List<PointHistory> findAllByUserId(long userId) {
        return pointHistoryJpaRepository.findAllByUser_Id(userId);
    }

    @Override
    public void save(PointHistory pointHistory) {
        pointHistoryJpaRepository.save(pointHistory);
    }
}
