# 1. 개요

* 대기열 설계는 은행 창구식(활성 토큰 수 상한 고정)으로 구현하였습니다.
* 테스트는 쿼리 분석후 애플리케이션에서 가장 슬로우한 쿼리를 선정하였고, 해당 쿼리에 대한 인덱스 적용 전과 적용 후의 탐색 시간을 측정 및 비교하였습니다.

<br><br>

# 2. 개선을 고민한 테이블별 쿼리 목록

## 2-1. Concert
### 2-1-1. Query
```java
// `concertTitleId`와 `occasion`(timestamp)로 특정 콘서트를 조회
ConcertEntity findByConcertTitleEntity_IdAndOccasion(long concertTitleId, LocalDateTime occasion); 
```
* INDEX: `concertTitleId`(FK)
* `concertTitleId`의 카디널리티가 높기에 별도로 `occasion`을 복합인덱스에 추가하지 않았습니다.


<br><br>

## 2-2. Seat
### 2-2-1. Query
```java
// `concertId`와 `seatNumber`로 특정 좌석을 조회
SeatEntity findByConcertEntity_IdAndSeatNumber(long concertId, int seatNumber);
```
* INDEX: `concertId`(FK)
* `concertId`의 카디널리티가 높고, `seatNumber`는 콘서트별로 상수로 고정되어 있으므로 `concertId`만 인덱스로 설정하였습니다.



<br><br>

## 2-3. WaitingQueue
### 2-3-1. Query1
```java
// `status`가 `ACTIVE` 상태인 대기열토큰 수를 카운트
int countByStatus(WaitingQueueStatus status);
```

<br>

### 2-3-2. Query2
```java
// `status`가 `WAITING` 상태인 대기열토큰들 중 가장 첫 번째 토큰을 조회
WaitingQueueEntity findFirstByStatusOrderByIdAsc(WaitingQueueStatus status);
```

<br>

### 2-3-3. Query3
```java
// `status`가 `ACTIVE` 상태인 대기열토큰들 중 `expireAt`(timestamp)이 현재 시간 기준 경과한 토큰 목록 조회 
@Query("SELECT w FROM WaitingQueueEntity w WHERE w.status = :status AND w.expireAt <= :now")
List<WaitingQueueEntity> findAllByStatusAndExpireAtLessThanEqual(
        @Param("status") WaitingQueueStatus status,
        @Param("now") LocalDateTime now);
```
* INDEX: `status`, ~~`expireAt`~~
1. `status` -> 인덱스 결정
   * `status`는 빈번히 수정되어 인덱스 재조정 빈도가 높고, 카디널리티가 매우 낮으므로(`ACTIVE`, `WAITING`, `EXPIRED` 3가지 경우만 존재) 인덱스로 설정할지를 고민
   * 인덱스 결정 근거:
      * 설계상 `status`가 `ACTIVE`나 `WAITING` row들을 스케쥴러로 매우 잦은 빈도(매 5초)로 조회
      * 스케쥴러의 `cron` 설정 빈도(하루 1회)에 따라 만료된 row들은 주기적으로 삭제되므로 데이터 증가로 인한 인덱스 재조정 오버헤드 제한적
      * 대기열 사용자 경험상 빠른 조회와 실시간성의 중요성
2. `expiredAt` -> 인덱스 보류
   * `expireAt`의 범위질의는 `findAllByStatusAndExpireAtLessThanEqual()`의 호출(`ACTIVE` 상태인 row를 조회)할 때만 발생하는데,
      * 설계상 `status`가 `ACTIVE`한 로우의 수는 상한이 상수(50명~)로 고정되어 있어 매우 작은 모수를 유지합니다. -> 풀스캔시에도 최대 50개로 비용이 거의 발생하지 않습니다.
      * 반면 `expireAt` 복합 인덱스 추가시 `ACTIVE` 상태인 로우 외의 (테이블 전체의 거의 대부분을 차지하는)`WAITING`이나 `EXPIRED` 상태의 row에 대해서 까지 불필요한 인덱스 재조정이 일어납니다.



<br><br>

## 2-4. Reservation
### 2-4-1. Query(테스트 대상 선정)
```java
@Query("SELECT r FROM ReservationEntity r WHERE createdAt > abolishTimestampFrom AND r.createdAt <= :abolishTimestampUntil AND r.status = :status")
List<ReservationEntity> findAllByStatusAndCreatedAtLessThanEqual(
                @Param("abolishTimestampFrom") LocalDateTime abolishTimestampFrom,
                @Param("abolishTimestampUntil") LocalDateTime abolishTimestampUntil,
                @Param("status") ReservationStatus status);
```
* INDEX: `createdAt`, ~~`status`~~
1. `createdAt` -> 인덱스 결정
   * 인덱스 결정 근거:
      * timestamp와 `status`를 조건으로 탐색한다는 점과 극히 적은 모수의 타겟(50건 이하의 토큰 활성화중 or 임시예약중)을 탐색한다는 점이 2-3-3의 query와 유사하지만, 결정적으로 다른 점은 `reservation` 테이블은 데이터가 삭제되지 않고 계속해서 축적된다는 점이었습니다.
      * `createdAt`은 카디널리티가 높고 순차성이 있어 범위 탐색에 효과적이고
      * 향후 `reservation` 내역 조회나 데이터 분석 등 요구사항 확장시 효용성이 있을 것으로 판단했습니다.
2. `status` -> 인덱스 보류
   * `reservation` 데이터가 계속 쌓임에 따라 `status`의 인덱스를 유지하는 비용이 효용보다 크다고 판단해 보류하였습니다.

<br><br>

# 3. 테스트 결과

## 3-1. `reservation` 테이블과 테스트 수행
* 총 1,000,050 row로 구성
```
  +---------+----------------------------+-----------+------------+---------+------------+
  | id      | created_at                 | status    | user_id    | seat_id | payment_id |
  +---------+----------------------------+-----------+------------+---------+------------+
  ...
  | 446829  | 2024-07-27 08:45:44.000000 | FINALIZED |          1 |       1 |          1 |
  | 446830  | 2024-07-27 08:45:08.000000 | ABOLISHED |          1 |       1 |          1 |
  | 446831  | 2024-07-27 08:46:54.000000 | FINALIZED |          1 |       1 |          1 |
  | 446838  | 2024-07-27 08:43:16.000000 | FINALIZED |          1 |       1 |          1 |
  ...
  | 1000049 | 2024-08-01 12:59:11.000000 | ON_HOLD   |          1 |       1 |          1 |
  | 1000050 | 2024-08-01 12:59:07.000000 | ON_HOLD   |          1 |       1 |          1 |
  +---------+----------------------------+-----------+------------+---------+------------+
  ```

<br>

### 3-1-1. 인덱스 없이 batch 탐색 시간 측정
* 탐색 실행 시간: `584 milliseconds`
```java
// 테스트용 더미데이터 생성시 설정한 '현재시간'의 기준
private static final LocalDateTime BASE_DATE_TIME = LocalDateTime.of(2024, 8, 1, 13, 0, 0).truncatedTo(
        ChronoUnit.SECONDS);

@Test
void 임시예약_상태이고_예약시간이_만료된_모든_로우를_인덱스_없이_탐색하고_수행시간을_출력한다() {
    long startTime = System.currentTimeMillis();

    reservationRepository.findAllByStatusAndCreatedAtLessThanEqual(
        BASE_DATE_TIME
            .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES +
                BusinessPolicies.TEMPORARY_RESERVATION_ABOLISH_DEFER_MINUTES)
            .truncatedTo(ChronoUnit.SECONDS),
        BASE_DATE_TIME
            .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES)
            .truncatedTo(ChronoUnit.SECONDS),
        ReservationStatus.ON_HOLD);

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    //실행 시간: 584 milliseconds
    System.out.println("실행 시간: " + duration + " milliseconds");
}

```

<br>

### 3-1-2. 인덱스 추가후 batch 탐색 시간 측정
* 탐색 실행 시간: `117 milliseconds`

```java
//index 추가 적용 코드
@Entity
@Table(name = "reservation", indexes = {
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class ReservationEntity {
    ...
}
```
```
# MySQL 콘솔 reservation table index 조회 결과
+-------------+------------+-----------------------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
| Table       | Non_unique | Key_name                    | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment | Visible | Expression |
+-------------+------------+-----------------------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
| reservation |          0 | PRIMARY                     |            1 | id          | A         |      902970 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
| reservation |          1 | FK8g1s9tyunsjdv96dyiobv51bb |            1 | payment_id  | A         |           1 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
| reservation |          1 | FKewd3sohjspqf2sjjvdcmcefpb |            1 | seat_id     | A         |           1 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
| reservation |          1 | FKm4oimk0l1757o9pwavorj6ljg |            1 | user_id     | A         |           1 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
| reservation |          1 | idx_created_at              |            1 | created_at  | A         |      631892 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
+-------------+------------+-----------------------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
```
```java
// 테스트 코드
// 테스트용 더미데이터 생성시 설정한 '현재시간'의 기준
private static final LocalDateTime BASE_DATE_TIME = LocalDateTime.of(2024, 8, 1, 13, 0, 0).truncatedTo(
        ChronoUnit.SECONDS);

@Test
void 임시예약_상태이고_예약시간이_만료된_모든_로우를_인덱스를_추가한_후_탐색하고_수행시간을_출력한다() {
    long startTime = System.currentTimeMillis();

    reservationRepository.findAllByStatusAndCreatedAtLessThanEqual(
        BASE_DATE_TIME
            .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES +
                BusinessPolicies.TEMPORARY_RESERVATION_ABOLISH_DEFER_MINUTES)
            .truncatedTo(ChronoUnit.SECONDS),
        BASE_DATE_TIME
            .minusMinutes(BusinessPolicies.TEMPORARY_RESERVATION_DURATION_MINUTES)
            .truncatedTo(ChronoUnit.SECONDS),
        ReservationStatus.ON_HOLD);

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    //실행 시간: 117 milliseconds
    System.out.println("실행 시간: " + duration + " milliseconds");
}
```

<br><br>

## 3-2. 테스트 결과 및 결론

* 2-4-1의 가정대로 `reservation`을 batch 조회하는 query에 index를 설정한 후 동일 데이터, 동일 쿼리에 대하여
   * index 적용전 `584 milliseconds` 에서 -> 인덱스 적용후 `117 milliseconds`로, 읽기 속도 `4.99배` 증가, 성능을 `79.93%` 개션할  수 있었습니다.
