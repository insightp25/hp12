package io.clean.tdd.hp12.domain.point;

import io.clean.tdd.hp12.domain.point.model.Point;
import io.clean.tdd.hp12.domain.point.model.PointHistory;
import io.clean.tdd.hp12.domain.point.port.PointHistoryRepository;
import io.clean.tdd.hp12.domain.point.port.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public Point getOf(long userId) {
        return pointRepository.getByUserId(userId);
    }

    public Point charge(long userId, long amount) {
        Point.validate(amount);

        Point point = pointRepository.getByUserId(userId);

        Point chargedPoint = pointRepository.save(point.charge(amount));

        pointHistoryRepository.save(PointHistory.generateChargeTypeOf(chargedPoint.user(), amount));

        return chargedPoint;
    }

    public Point use(long userId, long amount) {
        Point.validate(amount);

        Point point = pointRepository.getByUserId(userId);

        point.validateSufficient(amount);

        Point deductedPoint = pointRepository.save(point.use(amount));

        pointHistoryRepository.save(PointHistory.generateUseTypeOf(deductedPoint.user(), amount));

        return deductedPoint;
    }

    public List<PointHistory> getHistoriesOf(long userId) {
        return pointHistoryRepository.findAllByUserId(userId);
    }
}
