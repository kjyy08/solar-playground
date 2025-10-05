# 프로젝트 개요

이 프로젝트는 Java와 Spring Boot로 구축된 모듈형 모놀리식 웹 애플리케이션입니다. Upstage의 AI 모델(Solar Pro 2, Document Parse)과 Google의 Gemini 임베딩 모델의 기능을 RAG(Retrieval-Augmented Generation) 파이프라인에서 보여주는 플레이그라운드 역할을 합니다.

이 애플리케이션은 여러 도메인 간의 관심사를 명확하게 분리하는 멀티 모듈 Gradle 프로젝트로 구성되어 있습니다.

- **app**: 다른 모든 모듈을 통합하고 웹 애플리케이션을 실행하는 기본 애플리케이션 모듈입니다.
- **modules**:
    - **common**: 다른 모듈에서 사용되는 공유 유틸리티, 도메인 클래스 및 인프라 코드를 포함합니다.
    - **user**: 사용자 인증(GitHub OAuth2를 통해) 및 관리를 처리합니다.
    - **file**: 파일 업로드 및 저장을 관리합니다.
    - **document**: AI 모델을 사용하여 업로드된 문서를 처리하고, 임베딩을 생성하고, pgvector 확장 기능이 있는 PostgreSQL 데이터베이스에 저장하는 역할을 합니다.
    - **chat**: RAG 파이프라인을 구현하고 사용자가 AI와 상호 작용할 수 있는 채팅 인터페이스를 제공합니다.

## 빌드 및 실행

### 사전 요구 사항

- JDK 17
- [pgvector 확장](https://github.com/pgvector/pgvector)이 활성화된 PostgreSQL
- Upstage 및 Gemini용 API 키

### 주요 명령어

- **프로젝트 빌드:**
  ```bash
  ./gradlew build
  ```
- **애플리케이션 실행:**
  ```bash
  ./gradlew bootRun
  ```
- **테스트 실행:**
  ```bash
  ./gradlew test
  ```

## 개발 규칙

- **모듈형 모놀리스:** 이 프로젝트는 Spring Modulith를 사용하는 모듈형 모놀리식 아키텍처를 따릅니다. 각 모듈은 자체 도메인 로직을 가진 독립적인 단위입니다.
- **의존성 관리:** 의존성은 버전 카탈로그(`gradle/libs.versions.toml`)를 사용하여 중앙에서 관리됩니다.
- **모듈 통신:** 모듈은 이벤트를 통해 비동기적으로 서로 통신합니다.
- **코드 스타일:** 이 프로젝트는 표준 Java 및 Spring Boot 코딩 규칙을 따릅니다.
- **테스트:** 각 모듈에는 자체 단위 및 통합 테스트 세트가 있어야 합니다. 테스트 코드는 시나리오에 따라 중첩 구조를 사용하고, `given-when-then` 패턴을 준수하여 명확하게 작성해야 합니다.

## Git 컨벤션

### 브랜치 규칙

- **형식**: `type/brief-description`
- **브랜치 타입**:
    - `feature/`: 새로운 기능 개발
    - `bugfix/`: 버그 수정
    - `hotfix/`: 긴급 수정
    - `docs/`: 문서 작업
    - `refactor/`: 리팩토링 작업

### 커밋 메시지 규칙

- **형식**: `type: 설명 #Issue_Number` 또는 `type: 설명 - 세부사항(필요 시) #Issue_Number`
- **사용 가능한 타입**:
    - `feat`: 새로운 기능 추가
    - `fix`: 버그 수정
    - `design`: CSS 등 사용자 UI 디자인 변경
    - `docs`: 문서 변경 (README, 가이드, 주석 등)
    - `chore`: 빌드 설정, 의존성, 환경 설정 변경
    - `refactor`: 코드 리팩토링 혹은 성능 개선
    - `test`: 테스트 코드 추가 또는 수정
    - `comment`: 필요한 주석 추가 및 수정
    - `style`: 코드 포맷팅, 세미콜론 등 (기능 변경 없음)
    - `remove`: 파일, 기능, 의존성 제거
    - `ci`: CI 설정 변경
    - `cd`: CD 설정 변경