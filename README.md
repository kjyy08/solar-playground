# Solar Playground

Upstage사의 AI 모델(Solar Pro 2, Document Parse)을 체험할 수 있는 RAG(Retrieval-Augmented Generation) 기반의 Playground 웹 사이트입니다.

## 🚀 주요 기능

- **AI 챗봇**: Solar Pro 2 모델을 기반으로 한 챗봇과 대화할 수 있습니다.
- **문서/이미지 처리**: 사용자가 업로드한 파일을 Upstage Document Parse 모델로 분석하고, Gemini Embedding 모델로 벡터화하여 PostgreSQL(pgvector)에 저장합니다.
- **RAG 파이프라인**: 저장된 문서 내용을 기반으로 RAG 파이프라인을 거쳐, 정보의 출처와 함께 정확도 높은 답변을 제공합니다.
- **파이프라인 시각화**: 챗봇이 답변을 생성하는 RAG 파이프라인의 각 단계를 실시간으로 확인할 수 있습니다.
- **파일 관리**: 업로드한 문서를 관리(삭제)할 수 있습니다.
- **보안**: GitHub 소셜 로그인(OAuth2) 및 JWT를 통한 안전한 인증/인가를 지원합니다.

## 🛠️ 아키텍처 및 기술 스택

### 아키텍처
- **모듈러 모놀리식 아키텍처**: `Spring Modulith`를 활용하여 기능별 모듈(사용자, 파일, 문서, 채팅)로 명확하게 분리된 구조를 지향합니다.
- **서버 사이드 렌더링**: `Thymeleaf`를 사용하여 동적인 웹 페이지를 구성합니다.

### 기술 스택
- **Backend**: Java 17, Spring Boot 3.x
- **Database**: PostgreSQL, pgvector
- **AI**: 
  - Upstage Solar Pro 2
  - Upstage Document Parse
  - Gemini Embedding
- **Libraries**: Spring Data JPA, Spring Security, Spring Modulith, Thymeleaf, JJWT
- **CI/CD**: Github Actions, K3S, ArgoCD

## 🏁 시작하기

### 사전 요구사항
- JDK 17
- PostgreSQL 데이터베이스 및 [pgvector extension](https://github.com/pgvector/pgvector) 활성화
- Upstage & Gemini API Key

### 실행 방법
1. **프로젝트 클론**
   ```bash
   git clone https://github.com/kjyy08/solar-playground.git
   cd solar-playground
   ```

2. **설정 파일 구성**
   `src/main/resources/application.properties` (또는 `application.yml`) 파일을 열고 다음 정보를 입력합니다.
   ```properties
   # Database
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
   spring.datasource.username=your_user
   spring.datasource.password=your_password

   # OAuth2 (GitHub)
   spring.security.oauth2.client.registration.github.client-id=your_github_client_id
   spring.security.oauth2.client.registration.github.client-secret=your_github_client_secret

   # JWT
   jwt.secret.key=your_super_secret_key_for_jwt_token_generation

   # API Keys
   upstage.api.key=your_upstage_api_key
   gemini.api.key=your_gemini_api_key
   ```

3. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

## 📦 프로젝트 구조

본 프로젝트는 `Spring Modulith`를 기반으로 **모듈러 모놀리스 아키텍처**를 적용했습니다.

```
solar-playground/
├── app/                           // 🚀 메인 애플리케이션 모듈
│   └── src/main/java/            // 애플리케이션 진입점 및 전역 설정
├── modules/                       // 📦 도메인 모듈들
│   ├── user/                      // 👤 사용자 인증 및 관리 모듈
│   │   └── src/main/java/
│   ├── file/                      // 📁 파일 업로드 및 관리 모듈
│   │   └── src/main/java/
│   ├── document/                  // 🤖 AI 문서 처리 및 임베딩 모듈
│   │   └── src/main/java/
│   └── chat/                      // 💬 RAG 파이프라인 및 채팅 모듈
│       └── src/main/java/
├── gradle/
│   └── libs.versions.toml        // 의존성 버전 중앙 관리
├── build.gradle                   // 루트 빌드 설정
└── settings.gradle               // 멀티 모듈 설정
```

### 특징
- **멀티 모듈 구조**: Gradle 멀티 프로젝트로 각 도메인을 독립 모듈로 분리
- **의존성 관리**: Version Catalog를 통한 중앙화된 버전 관리
- **모듈 간 통신**: 각 모듈은 독립적인 도메인 기능을 수행하며, 필요시 이벤트를 통해 비동기적으로 통신
- 자세한 내용은 `docs/development_guideline.md` 문서를 참고해 주세요.
