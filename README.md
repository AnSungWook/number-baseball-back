# ⚾ 숫자 야구 게임

Spring Boot 3 + Java 21 + React로 구현한 웹 기반 숫자 야구 게임입니다.

(업데이트: 메모리 관리, 동시성, 힌트 UI 관련 변경 반영)

## 🎯 게임 규칙

- 컴퓨터가 생각한 4자리 숫자를 맞춰보세요!
- 각 자리는 0~9의 서로 다른 숫자입니다
- **스트라이크**: 숫자와 위치가 모두 맞음
- **볼**: 숫자는 맞지만 위치가 틀림
- 최대 10번의 기회가 있습니다

## 🛠️ 기술 스택

### Backend
- Spring Boot 3.2.0
- Java 21
- Gradle
- Lombok

### Frontend
- React 18
- TypeScript
- CSS3 (Gradient, Flexbox)

## 🚀 실행 방법

### 1. 백엔드 실행

```bash
# 프로젝트 루트 디렉토리에서
./gradlew bootRun
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

엔드포인트 테스트 예시:
- 새 게임 시작:
  ```bash
  curl -X POST http://localhost:8080/api/game/start
  ```
- 숫자 예측(guess):
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"guess":"1234"}' http://localhost:8080/api/game/{gameId}/guess
  ```
- 게임 상태 조회:
  ```bash
  curl http://localhost:8080/api/game/{gameId}/state
  ```
- 힌트 요청:
  ```bash
  curl -X POST -H "Content-Type: application/json" -d '{"position":1}' http://localhost:8080/api/game/{gameId}/hint
  ```

### 2. 프론트엔드 실행

프로젝트 루트의 frontend 폴더로 이동:

```bash
# frontend 디렉토리에서
cd frontend
npm install
npm start
```

프론트엔드는 `http://localhost:3000`에서 실행됩니다.

프론트엔드와 백엔드 통신 주의
- 개발 중에는 package.json에 proxy 설정(`"proxy": "http://localhost:8080"`)이 있으면 `/api/*` 호출이 자동 프록시 됩니다.
- proxy가 없을 경우 프론트에서 직접 `http://localhost:8080`으로 API를 호출하거나 CORS 설정을 확인하세요. (컨트롤러에는 localhost:3000을 허용하는 @CrossOrigin이 적용되어 있습니다.)

## 📁 프로젝트 구조

```
number-baseball-game/
├── src/main/java/com/example/numberbaseball/
│   ├── NumberBaseballApplication.java     # 메인 애플리케이션
│   ├── controller/
│   │   └── NumberBaseballController.java  # REST API 컨트롤러
│   ├── service/
│   │   └── NumberBaseballService.java     # 게임 로직 서비스
│   └── model/
│       ├── GameResult.java               # 게임 결과 모델
│       └── GameState.java                # 게임 상태 모델
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── GameStart.tsx             # 게임 시작 화면
│   │   │   ├── GamePlay.tsx              # 게임 진행 화면
│   │   │   └── GameResult.tsx            # 게임 결과 화면
│   │   ├── App.tsx                       # 메인 앱 컴포넌트
│   │   └── index.tsx                     # 엔트리 포인트
│   └── package.json
└── build.gradle
```

## 🎮 게임 화면

1. **게임 시작 화면**: 게임 규칙 설명과 시작 버튼
2. **게임 진행 화면**: 4자리 숫자 입력과 시도 기록 표시
3. **게임 결과 화면**: 승/패 결과와 최종 기록 표시

## 🔧 API 엔드포인트

- `POST /api/game/start` - 새 게임 시작
- `POST /api/game/{gameId}/guess` - 숫자 예측
- `GET /api/game/{gameId}/state` - 게임 상태 조회

## 🎨 UI/UX 특징

- 반응형 디자인
- 그라데이션 배경
- 직관적인 숫자 입력 인터페이스
- 실시간 게임 상태 업데이트
- 시각적 피드백 (스트라이크/볼 표시)

## 🔧 운영 및 추가 권장 사항

- 메모리/스케일링
  - 현재는 메모리(서버 힙)에 게임 상태를 보관합니다. 프로덕션/다중 인스턴스 환경에서는 Redis 같은 외부 저장소나 Caffeine 캐시 도입을 권장합니다.
  - Caffeine 도입 시 만료(expireAfterAccess) 및 maximumSize 정책을 설정하면 메모리 사용을 안전하게 제어할 수 있습니다.

- 동시성
  - games 맵을 ConcurrentHashMap으로 바꿨지만, GameState 내부의 컬렉션(예: attempts)은 여전히 mutable입니다. 필요 시 동기화 또는 컬렉션 접근을 원자화하세요.

- 로깅/모니터링
  - 활성 게임 수, 평균 게임 지속 시간, 힙/GC 메트릭을 수집하면 문제를 조기에 발견하기 좋습니다(Prometheus + Grafana 추천).

- 테스트
  - makeGuess / getState / getHint 시나리오에 대한 단위 및 통합 테스트 추가 권장

## 📝 개발 노트 (문제 해결 히스토리)
- 초기: 게임 상태를 HashMap에 무제한 저장 → 메모리 누수 위험 및 동시성 문제 우려
- 변경: ConcurrentHashMap 적용, 게임 종료 처리 흐름 수정(getState에서 반환 후 제거), GameState 편의 생성자/복사 생성자 추가
- 프론트 변경: 힌트 UI 개선 및 콘솔 정리
