# SNS_MSA

마이크로서비스 아키텍처(MSA) 기반으로 구현된 SNS 플랫폼입니다.  
기존 모놀리식 구조로 개발된 SNS 프로젝트를 서비스 단위로 분리하고,  
Spring Cloud 기반 기술 스택을 활용해 유연하고 확장 가능한 구조로 재구성한 개인 프로젝트입니다.

---

## 프로젝트 문서 (Notion)

👉 [Notion 문서 바로가기](https://www.notion.so/SNS-9131d472ec2745659f969c0ed0350f23)

---

## 아키텍처 개요

- **Spring Cloud Gateway**를 통해 모든 외부 요청을 단일 진입점으로 통합
- **Eureka Server**를 통한 각 마이크로서비스 등록 및 서비스 디스커버리
- **Kafka**를 통해 게시글 작성, 팔로우 이벤트 등의 알림 처리를 비동기 방식으로 구성
- **WebSocket**을 이용한 실시간 채팅 구현
- **Redis**를 활용한 JWT 리프레시 토큰 저장 및 채팅 세션 캐시
- **서비스 간 통신**은 REST + FeignClient를 기반으로 구성

---

## 서비스 구성

| 서비스명         | 설명 |
|------------------|------|
| **gateway-service** | 클라이언트 요청을 각 서비스로 라우팅하고, JWT 필터를 통해 인증 처리 |
| **user-service**     | 회원가입, 로그인, 팔로우 등 사용자 관련 기능 담당 |
| **board-service**    | 게시글 CRUD, 좋아요 기능 등 SNS 콘텐츠 처리 |
| **chat-service**     | WebSocket 기반 1:1 및 오픈 채팅방 구현 |
| **alarm-service**    | Kafka 기반 알림 메시지 소비 및 저장 |
| **discovery-service**| Eureka 서버, 마이크로서비스 등록/탐색 |
| **common module**    | 공통 DTO, JWT 유틸, 커스텀 예외 등 포함 |

---

## 서비스 간 흐름 예시

### 게시글 작성 → 알림 전송

1. 사용자가 게시글 작성
2. `board-service`가 게시글 저장 후 Kafka로 `PostCreatedEvent` 전송
3. `alarm-service`가 이벤트 수신 후 알림 저장
4. 사용자는 알림 API 또는 WebSocket으로 알림 확인

### 팔로우 → 알림

1. 사용자가 팔로우 요청 (`user-service`)
2. Kafka로 `FollowEvent` 전송
3. `alarm-service`에서 알림 저장 및 푸시

---
## 포트상태


| 서비스명       | 포트 |
|----------------|------|
| gateway        | 8000 |
| user-service   | 8081 |
| chat-service   | 8082 |
| board-service  | 8083 |
| alarm-service  | 8084 |
| discovery      | 8761 |

---

## 💬 커밋 컨벤션

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 리팩토링 (기능 변경 없음) |
| `docs` | 문서 관련 수정 |
| `test` | 테스트 코드 추가/수정 |
| `chore` | 설정, 빌드, 기타 자잘한 작업 |
| `style` | 코드 스타일/포맷 변경 |
| `build` | 빌드 설정 관련 수정 |
| `ci` | GitHub Actions 등 CI 설정 변경 |
| `perf` | 성능 개선 작업 |
| `revert` | 이전 커밋 되돌리기 |

---
## 향후 확장 계획

- [ ] Prometheus + Grafana 기반 모니터링 도입 (`feature/monitoring-setup`)
- [ ] 서비스별 테스트 코드 작성 및 GitHub Actions 자동 테스트
- [ ] Elastic Stack 기반 로그 수집/분석 구조 적용
- [ ] 슬랙/이메일 기반 실시간 알림 Webhook 연동
- [ ] 쿠버네티스 Helm Chart를 통한 배포 실험

---
