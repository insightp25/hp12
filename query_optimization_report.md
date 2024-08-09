# 1. 들어가기 전에

* 대기열 설계는 은행 창구식(활성 토큰 수 상한 고정)으로 구현하였습니다. 

# 2. 개선을 고민한 테이블별 쿼리 목록

## 2-1. Concert
### 2-1-1. Query
```java
// `concertTitleId`와 `occasion`(timestamp)로 특정 콘서트를 조회
ConcertEntity findByConcertTitleEntity_IdAndOccasion(long concertTitleId, LocalDateTime occasion); 
```
* INDEX: `concertTitleId`(FK)
* `concertTitleId`의 카디널리티가 높기에 별도로 `occasion`을 복합인덱스에 추가하지 않았습니다.

## 2-2. Seat
### 2-2-1. Query
```java
// `concertId`와 `seatNumber`로 특정 좌석을 조회
SeatEntity findByConcertEntity_IdAndSeatNumber(long concertId, int seatNumber);
```
* INDEX: `concertId`(FK)
* `concertId`의 카디널리티가 높고, `seatNumber`는 콘서트별로 상수로 고정되어 있으므로 `concertId`만 인덱스로 설정하였습니다.

## 2-3. WaitingQueue
### 2-3-1. Query1
```java
// `status`가 `ACTIVE` 상태인 대기열토큰 수를 카운트
int countByStatus(WaitingQueueStatus status);
```
### 2-3-2. Query2
```java
// `status`가 `WAITING` 상태인 대기열토큰들 중 가장 첫 번째 토큰을 조회
WaitingQueueEntity findFirstByStatusOrderByIdAsc(WaitingQueueStatus status);
```
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

## 2-4. Reservation
### 2-4-1. Query
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
    * 선행 탐색 조건인 `createdAt`의 카디널리티가 매우 높고
    * `status`는 카디널리티가 매우 낮으며,
    * 탐색 대상인 `ON_HOLD`(임시예약중)상태의 컬럼의 수가 설계상 매우 작고(50건 내외)
    * `reservation` 데이터가 계속 쌓임에 따라 `status`의 인덱스를 유지하는 비용이 효용보다 훨씬 크다고 판단해 보류하였습니다.
    
