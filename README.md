# 📊 Event Tracker

Reactive 기반 이벤트 수집 시스템  
**Kotlin + Spring WebFlux + Redis + PostgreSQL 구조로, 최대 RPS 340 이상 처리 가능**

---

## 🧩 Overview

이 프로젝트는 사용자 이벤트(클릭, 좋아요, 검색 등)를 수집하고, 실시간 통계 및 인기 콘텐츠를 분석하는 **이벤트 트래킹 시스템**입니다.  
핵심 목표는 **WebFlux 기반 Reactive 아키텍처**, **Redis 캐싱**, **PostgreSQL 집계 처리**를 통해 대규모 트래픽 상황에서도 견고한 성능을 확보하는 것입니다.

---

## 🏗 Architecture

```
┌────────────────────┐
│     event-api      │ ◀────────────┐
└────────────────────┘              │
         │ WebFlux API              │
         ▼                         API 요청/응답
┌────────────────────┐              │
│ event-application  │◀─────────────┘
│  - UseCase / 전략패턴│
└────────────────────┘
         ▼
┌────────────────────┐      ┌────────────────────┐
│    event-port      │◀────▶│   event-storage    │
│ (in: usecase port) │      │ Redis / PostgreSQL │
└────────────────────┘      └────────────────────┘
         ▲
     Domain Logic
         ▲
┌────────────────────┐
│    event-core      │
│ Event / Command 등  │
└────────────────────┘
```

---

## ⚙️ 기술 스택

| 분야 | 기술 |
|------|------|
| Language | Kotlin 1.9 |
| Framework | Spring Boot 3.x, Spring WebFlux |
| Data | Redis (cache), PostgreSQL (persistent) |
| Test | MockK, WebFluxTest |
| Load Test | Locust |
| Infra | Docker, Jib |
| Arch | Hexagonal + Multi-Module 구조 |

---

## 🔥 주요 기능

### ✅ 이벤트 수집 API
- 클릭, 페이지뷰, 검색, 좋아요 이벤트 저장
- `SaveEventService`는 전략 패턴으로 확장성 확보

### 🧠 실시간 통계
- Redis 기반 TTL + Sorted Set + Lua Script 활용
- `SEARCH`, `CLICK` 등 이벤트 통계를 Redis로 관리
- 인기도 기준으로 `Top 5 키워드`, `인기 게시물` 조회 가능

### 💾 PostgreSQL 연동
- 좋아요 상태 관리: `post_like` 테이블로 정합성 확보
- 인기 게시물 선정 로직은 DB에서 처리하여 정확도 강화

### 🧪 부하 테스트
- Locust 기반 최대 **RPS 340+**까지 테스트 완료
- M1 Mac 기준, CPU 97% 사용률 도달 시 성능 리밋 감지

---

## 🧪 성능 테스트 결과 (Locust)

- **시나리오**: 500명 사용자, 초당 10명 증가, 총 5분 진행
- **최대 RPS**: `340.8`
- **평균 응답속도**: `125.42ms`
- **95% 응답 지연**: `660ms`
- **에러율**: `0%`
- **CPU 사용률**: 평균 약 60~97%

(※ 참고: Redis 키 TTL을 1분으로 설정하여 실시간 재캐싱 상황도 시뮬레이션함)

---

## 🧰 실행 방법

```bash
# 빌드 및 실행
./gradlew clean build jibDockerBuild
docker-compose up -d
```

---

## 🎯 향후 계획

| 기능 | 상태 |
|------|------|
| Kafka 기반 비동기 확장 | 🔜 예정 |
| Elasticsearch 기반 검색 최적화 | 🔜 예정 |
| Prometheus + Grafana 모니터링 | 🔜 예정 |

---

## 📎 기타 참고

- Redis 캐시 미스 시 Redisson 락 기반으로 DB 조회 후 캐싱
    - 락 실패 시에도 fallback 없이 DB 응답 보장
- TTL로 키 만료 시 Redis 락 재시도 구간에서 일시적 RPS 감소 발생