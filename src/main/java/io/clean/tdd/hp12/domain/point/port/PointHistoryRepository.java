package io.clean.tdd.hp12.domain.point.port;

import io.clean.tdd.hp12.domain.point.model.PointHistory;

import java.util.List;

public interface PointHistoryRepository {
    List<PointHistory> findAllByUserId(long userId);

    PointHistory save(PointHistory pointHistory);
}
