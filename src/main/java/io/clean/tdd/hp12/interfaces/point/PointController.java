package io.clean.tdd.hp12.interfaces.point;

import io.clean.tdd.hp12.domain.point.PointService;
import io.clean.tdd.hp12.domain.point.model.Point;
import io.clean.tdd.hp12.domain.point.model.PointHistory;
import io.clean.tdd.hp12.interfaces.point.request.PointRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/points")
    public ResponseEntity<Point> point(@RequestParam("userId") long userId) {
        return ResponseEntity
            .ok()
            .body(pointService.getOf(userId));
    }

    @GetMapping("/point_history")
    public ResponseEntity<List<PointHistory>> pointHistory(@RequestParam("userId") long userId) {
        return ResponseEntity
            .ok()
            .body(pointService.getHistoriesOf(userId));
    }

    @PostMapping("/points/charge")
    public ResponseEntity<Point> charge(@RequestBody PointRequest pointRequest) {
        return ResponseEntity
            .ok()
            .body(pointService.charge(pointRequest.userId(), pointRequest.amount()));
    }

    @PostMapping("/points/use")
    public ResponseEntity<Point> use(@RequestBody PointRequest pointRequest) {
        return ResponseEntity
            .ok()
            .body(pointService.use(pointRequest.userId(), pointRequest.amount()));
    }
}
