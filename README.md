# 🚀 Reactive Event Tracker

**Spring WebFlux + Redis + PostgreSQL 기반의 고성능 사용자 이벤트 수집 시스템**

---

## 📌 프로젝트 개요

대규모 트래픽 환경에서 발생하는 **사용자 이벤트(클릭, 검색, 페이지뷰, 좋아요 등)** 를  
**비동기/비차단 방식**으로 실시간 수집하고, Redis를 활용해 임시 저장 및 통계를 처리하며,  
PostgreSQL로 필요한 데이터를 안정적으로 영속화하는 **MVP 수준 이벤트 트래커 백엔드 시스템**입니다.

---

## 🧱 주요 기술 스택

| 영역            | 기술                                       |
|-----------------|--------------------------------------------|
| Language        | Kotlin                                     |
| Framework       | Spring Boot 3.x, Spring WebFlux            |
| DB              | PostgreSQL 16 (R2DBC)                      |
| Cache / Queue   | Redis 7.4 (Reactive)                       |
| Build           | Gradle (Kotlin DSL, 멀티 모듈)             |
| Logging         | KotlinLogging (oshai)                      |
| Test            | Locust (부하 테스트), JUnit5 + mockk       |
| Infra           | Docker, Docker Compose                     |

---

## ⚙️ 아키텍처 구조

```plaintext
[Client] 
   ↓ POST /api/events
[Spring WebFlux API] 
   ↓
[Redis (TTL + LPUSH + Key-based 구조)]
   ↓
[통계 처리 및 이벤트 저장 처리]
   ↓
[PostgreSQL (R2DBC + JSONB 저장)]
```

---

## 🎯 주요 기능 요약

- ✅ **이벤트 수집 API**: `/api/events`
- ✅ **Redis 기반 TTL + 리스트 저장**  
- ✅ **좋아요 상태 토글 → 이벤트 트래킹 로그화 (LIKE / UNLIKE)**
- ✅ **통계 처리: 클릭, 페이지뷰, 검색, 좋아요 수**
- ✅ **사용자별 최근 검색어 저장 (중복 제거 + TTL + LPUSH)**
- ✅ **post_like 테이블과 연동된 정합성 보장 토글 처리**
- ✅ **단일 유저 기반 부하 테스트 + Redis 통계 정확성 검증**

---

## 📊 MVP 성능 목표

| 항목                    | 목표 수치              |
|-------------------------|------------------------|
| 초당 이벤트 처리량       | 500 ~ 1000+ EPS        |
| API 평균 응답 속도       | 50 ~ 100ms 이하        |
| 부하 테스트 도구         | Locust                 |
| 처리 이벤트 종류         | CLICK / PAGE_VIEW / SEARCH / LIKE / UNLIKE |

---

## 🧪 부하 테스트 시나리오

- `Locust` 기반 사용자 시뮬레이션
  - 유저 수: 100, 300, 500명
  - 증가 속도: 초당 10, 30, 50명
  - 시나리오:
    - 좋아요 토글 (post_like + 트래커 기록)
    - 검색 → 트래커 기록 → 최근 검색어 저장
    - 클릭 / 페이지뷰 등 이벤트 트래킹

---

## 📂 모듈 구조

```plaintext
event-tracker/
 ┣ core/               # 도메인 및 유틸 정의
 ┣ api/                # WebFlux Controller
 ┣ application/        # UseCase, Service 정의
 ┣ port/               # 헥사고날 아키텍처 Port 정의
 ┣ storage/            # Redis, PostgreSQL 어댑터
 ┣ statistics/         # 통계 전용 모듈
 ┣ locust/             # 테스트 시나리오 코드
 ┣ docker-compose.yml
 ┗ README.md
```

---

## 📦 Redis 활용 방식

- 이벤트 TTL 관리
  - CLICK / PAGE_VIEW / SEARCH 이벤트는 10분 TTL
  - LIKE / UNLIKE 이벤트는 TTL 없음 (트래킹 목적)
- 사용자 최근 검색어
  - LPUSH + LTRIM(10개 유지) + Key TTL(1시간)
  - 중복 제거: LREM으로 기존 키워드 제거 후 저장

---

## 📁 PostgreSQL 구조

- 이벤트 트래킹이 아닌, 상태 추적을 위한 데이터 정합성용 테이블 존재
- post_like, post, event_statistics 테이블 정의
- JSONB 필드를 통한 유연한 이벤트 저장 확장 가능

---

## 🚀 향후 확장 계획

| 항목                        | 설명 |
|-----------------------------|------|
| Kafka 도입                   | Redis → Kafka 교체 및 Consumer 처리 |
| PostgreSQL 배치 통계 집계    | Redis 데이터 → DB 주기적 적재 |
| ELK 연동                    | Kibana 시각화 기반 모니터링 구축 |
| Prometheus + Grafana        | 실시간 시스템 성능 대시보드 구성 |
| 사용자 세션 흐름 저장       | 행동 분석 기반 추천 알고리즘 확장 |

---

## ✅ 완료된 테스트 및 보장 항목

- 이벤트 저장 로직 단위 테스트
- Redis 트래킹 테스트 및 TTL 검증
- 좋아요 중복 방지 테스트 (ON CONFLICT DO NOTHING + 트래킹)
- 최근 검색어 10개 제한 + 중복 제거 확인
- 부하 테스트 100~500명 기준 안정성 검증
- 트래커 전송 API 비동기 병렬 처리 확인

---

## 🔍 기타

- 모듈 내 모든 의존성은 명확하게 분리 (core ↔ adapter)
- 헥사고날 아키텍처 기반
- 모든 모듈 WebFlux 대응
- 비동기 테스트는 단위 중심, 통합 테스트는 Locust로 대체
