package io.clean.tdd.hp12.domain.point.port;

import io.clean.tdd.hp12.domain.point.model.Point;

public interface PointRepository {
    Point getByUserId(long userId);

    Point save(Point point);
}
