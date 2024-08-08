package io.clean.tdd.hp12.infrastructure.point;

import io.clean.tdd.hp12.infrastructure.point.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointJpaRepository extends JpaRepository<PointEntity, Long> {

    PointEntity findByUser_Id(long userId);
}
