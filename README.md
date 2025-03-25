# 🌀 Reactive Event Tracker

> **Spring WebFlux + Redis + PostgreSQL 기반의 고성능 사용자 이벤트 수집 시스템**

---

## 📌 프로젝트 개요

**대규모 트래픽 환경에서 유저 이벤트(클릭, 검색, 페이지 이동 등)를 실시간으로 수집, 저장, 통계 분석하기 위한 백엔드 시스템입니다.**  
Spring WebFlux 기반으로 비동기 처리와 고성능을 지원하며, Redis를 큐로 사용해 PostgreSQL로 데이터를 안정적으로 적재합니다.

---

## 🧱 주요 기술 스택

| 영역 | 기술 |
|------|------|
| Language | Kotlin |
| Framework | Spring Boot 3.x (WebFlux) |
| DB | PostgreSQL 16 (R2DBC) |
| Cache / Queue | Redis 7.4 (Reactive) |
| Build | Gradle (Kotlin DSL, 멀티모듈) |
| Logging | KotlinLogging (oshai) |
| Test | Locust (부하 테스트), JUnit5 |
| Infra | Docker (PostgreSQL, Redis) |

---

## ⚙️ 아키텍처

```plaintext
[Client]
   ↓ POST /api/events
[Spring WebFlux API]
   ↓
[Redis (Stream/List)]
   ↓
[Consumer]
   ↓
[PostgreSQL (R2DBC + 파티셔닝)]
   ↓
[통계 API or Dashboard]
```

---

## 🎯 주요 기능

- [ ] 이벤트 수집 API (`/api/events`)
- [ ] Redis를 통한 임시 저장 (Reactive 방식)
- [ ] Redis → PostgreSQL 비동기 적재
- [ ] PostgreSQL JSONB + 파티셔닝 기반 이벤트 저장
- [ ] 검색어/페이지 인기 순위 통계 API
- [ ] Kafka로 큐 교체 (확장 계획)
- [ ] ELK 연동을 통한 로그 시각화 (최종 단계)

---

## 📊 처리 목표 (MVP 기준)

| 항목 | 목표 |
|------|------|
| 초당 이벤트 수신 처리량 | 1,000+ EPS |
| 전체 이벤트 처리 | 100만 건 이상 |
| 통계 API 평균 응답 시간 | 100ms 이하 |
| 부하 테스트 도구 | Locust |

---

## 🧪 테스트 시나리오

- Locust로 실시간 이벤트 API 부하 테스트
- Redis → PostgreSQL 적재 처리량 측정
- 인기 페이지, 검색어 통계 정확성 검증

---

## 🧩 디렉토리 구조

```bash
event-tracker/
 ┣ core/           # 도메인, 유틸, 공통 확장 함수
 ┣ api/            # WebFlux 기반 API (Controller, Handler)
 ┣ storage/        # Redis/DB 저장소 모듈
 ┣ statistics/     # 통계 API 모듈
 ┣ docker-compose.yml
 ┣ README.md
```

---

## 🚀 향후 확장 계획

- Redis → Kafka로 교체하여 대규모 분산처리 구조로 확장
- ELK Stack 연동 → Kibana 기반 실시간 모니터링 시각화
- Prometheus + Grafana로 실시간 성능 대시보드 구성