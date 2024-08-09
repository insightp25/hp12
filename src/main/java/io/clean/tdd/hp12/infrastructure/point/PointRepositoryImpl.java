package io.clean.tdd.hp12.infrastructure.point;

import io.clean.tdd.hp12.domain.point.model.Point;
import io.clean.tdd.hp12.domain.point.port.PointRepository;
import io.clean.tdd.hp12.infrastructure.point.entity.PointEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point getByUserId(long userId) {
        return pointJpaRepository.findByUserEntity_Id(userId)
            .toModel();
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(PointEntity.from(point))
            .toModel();
    }
}
