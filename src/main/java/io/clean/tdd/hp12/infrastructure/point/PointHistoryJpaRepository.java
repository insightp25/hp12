package io.clean.tdd.hp12.infrastructure.point;

import io.clean.tdd.hp12.infrastructure.point.model.PointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryEntity, Long> {

    List<PointHistoryEntity> findAllByUser_Id(long userId);
}
