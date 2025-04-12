# 🚀 Reactive Event Tracker

Spring WebFlux + Redis + PostgreSQL 기반의 고성능 사용자 이벤트 수집 및 통계 시스템

## 📌 프로젝트 개요

대규모 트래픽 환경에서 유저의 행동 이벤트(클릭, 검색, 페이지 이동, 좋아요 등)를 수집하고 Redis를 통해 실시간 통계 처리 후 PostgreSQL에 적재하는 백엔드 시스템입니다. 실시간성과 확장성을 고려한 아키텍처로, 이벤트 흐름과 통계를 안정적으로 관리합니다.

---

## 🧱 기술 스택

| 영역       | 기술                                                   |
|------------|--------------------------------------------------------|
| Language   | Kotlin                                                 |
| Framework  | Spring Boot 3.x (WebFlux)                              |
| DB         | PostgreSQL 16 (R2DBC + JSONB + 파티셔닝)              |
| Cache/Queue| Redis 7.4 (Reactive, Lua Script 활용)                  |
| Build      | Gradle (Kotlin DSL, 멀티모듈)                          |
| Test       | Locust (부하 테스트), JUnit5, MockK                    |
| Infra      | Docker, Docker Compose                                 |
| Logging    | KotlinLogging (oshai), SLF4J                           |

---

## ⚙️ 아키텍처

```plaintext
[Client]
   ↓
[Spring WebFlux API] (/api/events, /api/posts/like 등)
   ↓
[Redis] (임시 저장, TTL, Lua 기반 증분 처리)
   ↓
[Scheduler or Kafka]
   ↓
[PostgreSQL] (R2DBC, JSONB, 통계 저장)
   ↓
[Statistics API] (/api/statistics 등)
```

---

## 🎯 주요 기능

- 모든 사용자 이벤트 수집 API 지원 (CLICK, PAGE_VIEW, SEARCH, LIKE 등)
- Redis를 통한 빠른 TTL 기반 임시 저장 및 통계 카운팅
- Redis 통계 데이터를 주기적으로 PostgreSQL로 저장 (Lua Script 활용)
- 최근 검색어 Redis 리스트 저장 및 중복 제거
- 통계 데이터 조회 API 제공
- 추후 Kafka 및 ELK, Prometheus 도입 준비

---

## 📊 성능 목표 (MVP 기준)

| 항목                   | 목표                |
|------------------------|---------------------|
| 초당 이벤트 처리량     | 1,000 EPS 이상       |
| 전체 테스트 이벤트 수  | 100만 건 이상        |
| 통계 API 응답 시간     | 평균 100ms 이하      |

---

## 🧪 테스트 시나리오

- [x] 게시물 좋아요 / 좋아요 취소
- [x] 게시물 조회 (Page View)
- [x] 클릭 이벤트
- [x] 검색 이벤트
- [x] 최근 검색어 중복 제거 및 TTL 유지
- [x] Redis → PostgreSQL 통계 저장
- [x] Lua 기반 증분 처리 + 스냅샷 키 분리 저장
- [x] 전체 Locust 시나리오 실행 (최대 사용자 수: 500명, 게시물: 100건 기준)

---

## 📁 주요 디렉토리 구조

```plaintext
event-tracker/
  ┣ api/             # WebFlux API (컨트롤러, 요청/응답 DTO)
  ┣ application/     # UseCase, Service, 비즈니스 로직
  ┣ core/            # 도메인, Command, 공통 클래스
  ┣ port/            # Port 인터페이스 (입력/출력)
  ┣ storage/         # Redis, PostgreSQL 접근 어댑터
  ┗ locust/          # Locust 시나리오 스크립트
```

---

## 🔄 향후 확장 계획

- Kafka 기반 이벤트 처리 구조 전환 (Redis → Kafka → Consumer)
- Elasticsearch + Kibana 도입하여 검색 인덱스 최적화 및 시각화
- Prometheus + Grafana → 실시간 성능 모니터링 대시보드 구축
- 이벤트 유형 다양화 (스크롤, 댓글, 공유 등)
- 대규모 트래픽 (200만 게시물 기준) 성능 테스트

---

## 📈 200만 건 데이터 테스트 계획

### 테스트 구성

| 항목       | 수치              |
|------------|-------------------|
| 게시물 수   | 최대 2,000,000건   |
| 사용자 수   | 최대 100,000명     |
| 테스트 시간 | 10~30분 지속 부하 |
| TPS 목표    | 1,000 EPS 이상     |

### 데이터 준비 방법

1. **초기 데이터 삽입 스크립트 작성 (게시물)**
  - 게시물: content + imageUrl + hashtags 조합 자동화

2. **postgreSQL** 프로시저 사용


3. **성능별 단계적 증가**
  - 10만건 → 50만건 → 100만건 → 200만건 순차 삽입