# 대기열 기반 대용량 트래픽 티켓 예매 서비스

(🚧 현재 리뉴얼중입니다 🚧)

</br>

## Tech Stacks

`Java` `Spring` `Spring Boot` `Gradle` `JPA` `MySQL` `Redis` `Kafka` `Docker` `Jenkins` `Prometheus` `Grafana` `K6` `Swagger`


</br>

## 키워드 목록
* TDD
  - unit test
  - integration test
  - E2E test
* 클린 아키텍처
  - 확장성 있는 애플리케이션을 위한 도메인 중심의 아키텍처 설계
  - 변경에 유연하고 테스트가 용이하도록 외부 기술에 의존하지 않는 도메인 핵심 로직 구현
* API 설계
  - DB 모델링
  - 시퀀스 다이어그램
  - API 문서화
* Logging, Exception Handling
  - 성능에 영향을 주지 않으면서 핵심 정보를 전달하도록 한 logging 처리
  - 균일한 Exception 처리
* 대용량 트래픽 동시성 처리
  - Optimistic Lock, Pessimistic Lock, Distributed Lock
  - 동시성과 성능 사이 트레이드오프 고려, 제어 방법 개별 선택
* 성능 최적화
  - 트랜잭션 범위 조정
  - Index, Query Optimization
  - Caching, Eviction Policy
* 객체 지향
  - 도메인, 계층간 간각자의 역할에 충실한 기능 분배
  - 이벤트를 활용한 책임 및 역할 분리, 동기/비동기 처리
* 대용량 트래픽 처리를 위한 메시지 브로커 도입
  - Kafka의 데이터 분산과 메시지 브로커를 활용한 안정적 이벤트 처리
  - 메시지 정상 발행 확인을 위한 Outbox Pattern 구현
* 부하 테스트
  - K6, Prometheus, Grafana를 활용한 부하 테스트 및 시각화

