# Solar Playground 개발 가이드라인

## 1. 개발 가이드라인

이 문서는 프로젝트의 성공적인 완수를 위해 각 개발 단계를 정의하고, Spring Modulith를 활용한 모듈러 모놀리식 아키텍처의 이점을 극대화하는 데 초점을 맞춥니다.

### Phase 0: 프로젝트 초기 설정 및 아키텍처 설계

1.  **프로젝트 생성 및 의존성 설정:**
    *   `start.spring.io`를 통해 Spring Boot 3.x, Java 17 기반 프로젝트를 생성합니다.
    *   `build.gradle`에 핵심 의존성을 추가합니다.
        *   `spring-boot-starter-web`: 웹 애플리케이션 개발
        *   `spring-boot-starter-data-jpa`: 데이터베이스 연동
        *   `spring-boot-starter-thymeleaf`: 서버 사이드 렌더링
        *   `spring-boot-starter-security`: 보안
        *   `spring-boot-starter-oauth2-client`: 소셜 로그인
        *   `org.springframework.modulith:spring-modulith-starter-core`: 모듈러 모놀리식 아키텍처 지원
        *   `org.springframework.modulith:spring-modulith-starter-test`: 모듈 테스트 지원
        *   `org.postgresql:postgresql`: PostgreSQL 드라이버
        *   `com.pgvector:pgvector`: pgvector 지원 (JPA 연동 방식은 별도 확인 필요)
        *   `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson`: JWT 처리

2.  **모듈 식별 및 설계:**
    *   Spring Modulith의 핵심은 기능적 경계를 명확히 하는 것입니다. 프로젝트 기능을 기반으로 다음과 같이 주 모듈을 식별합니다.
        *   `user`: 사용자 인증, 인가, 정보 관리
        *   `file`: 파일 업로드, 삭제, 메타데이터 관리
        *   `document`: AI 모델 연동, 문서 파싱, 임베딩, pgvector 저장
        *   `chat`: RAG 파이프라인 실행, 채팅 이력 관리

### Phase 1: 사용자 인증 모듈 (User Module)

1.  **GitHub OAuth2 연동:**
    *   `application.properties` (또는 `yml`)에 GitHub OAuth2 Client ID와 Secret을 설정합니다.
    *   Spring Security 설정을 통해 `/oauth2/authorization/github` 엔드포인트를 활성화합니다.
    *   `CustomOAuth2UserService`를 구현하여 GitHub으로부터 사용자 정보를 받아오고, 우리 시스템의 `User` 엔티티와 매핑하여 데이터베이스에 저장/업데이트합니다.

2.  **JWT 기반 인증 구현:**
    *   OAuth2 로그인 성공 후, `AuthenticationSuccessHandler`를 구현합니다.
    *   해당 핸들러에서 사용자의 ID 또는 이메일을 기반으로 JWT(Access Token, Refresh Token)를 생성합니다.
    *   생성된 토큰을 클라이언트(Thymeleaf + JavaScript)에게 전달합니다. (예: 쿠키 또는 응답 바디)
    *   `JwtAuthenticationFilter`를 구현하여 이후의 모든 요청에 대해 헤더(또는 쿠키)의 JWT를 검증하고, 유효하다면 Spring Security의 `SecurityContext`에 인증 정보를 설정합니다.

### Phase 2: 파일 관리 및 AI 처리 모듈 (File & Document Modules)

1.  **파일 관리 (File Module):**
    *   `File` 엔티티를 설계하여 파일 메타데이터(파일명, UUID, 경로, 크기, 업로더 정보 등)를 관리합니다.
    *   파일 업로드/삭제를 처리하는 `FileController`와 `FileService`를 구현합니다.
    *   업로드된 파일은 서버의 특정 디렉토리에 UUID와 같은 고유한 이름으로 저장합니다.

2.  **비동기 문서 처리 (Document Module):**
    *   **핵심:** 파일 업로드 후의 AI 처리 과정은 시간이 오래 걸릴 수 있으므로 반드시 비동기적으로 처리해야 합니다. Spring Modulith의 `@ApplicationModuleListener` 또는 Spring의 `@Async`를 활용합니다.
    *   `File` 모듈에서 파일 업로드가 성공하면 이벤트를 발행(Publish)합니다. (예: `FileUploadedEvent`)
    *   `Document` 모듈에서 해당 이벤트를 구독(Listen)하여 다음 프로세스를 실행합니다.
        1.  **Document Parse:** Upstage Document Parse API를 호출하는 `UpstageClient`를 구현합니다. 저장된 파일을 API에 전송하여 HTML 또는 Markdown 형태의 텍스트를 받습니다.
        2.  **Embedding:** Gemini Embedding API를 호출하는 `GeminiClient`를 구현합니다. 파싱된 텍스트를 API에 전송하여 벡터(Vector) 값을 받습니다.
        3.  **pgvector 저장:** 파싱된 텍스트와 임베딩된 벡터를 `DocumentChunk`와 같은 엔티티로 묶어 PostgreSQL에 저장합니다. JPA에서 pgvector 타입을 처리하기 위해 `hibernate-types` 라이브러리를 사용하거나, 직접 커스텀 타입을 정의해야 할 수 있습니다.

### Phase 3: RAG 챗봇 모듈 (Chat Module)

1.  **RAG 파이프라인 구현:**
    *   `ChatService` 내에 RAG 파이프라인 로직을 구현합니다.
        1.  **Query Embedding:** 사용자의 질문을 Gemini Embedding API를 통해 벡터로 변환합니다.
        2.  **Similarity Search:** 변환된 쿼리 벡터를 사용하여 pgvector에 저장된 `DocumentChunk` 중에서 가장 유사도가 높은 문서를 검색합니다. (JPA Repository에 네이티브 쿼리로 `<->` 연산자 사용)
        3.  **Prompt Engineering:** 검색된 문서 청크(Context)와 사용자의 원본 질문을 조합하여 Upstage Solar Pro 2 모델에게 전달할 프롬프트를 구성합니다.
        4.  **LLM Call:** 구성된 프롬프트를 Solar Pro 2 API에 전송하여 최종 답변을 받습니다.
        5.  **Response Generation:** 받은 답변과 함께, 참조된 문서(Context)의 출처 정보를 함께 DTO로 만들어 반환합니다.

2.  **채팅 인터페이스 및 흐름 시각화:**
    *   채팅은 실시간 상호작용이 중요하므로 **WebSocket** (with STOMP)을 사용하는 것을 강력히 추천합니다.
    *   `ChatController`에 WebSocket 엔드포인트(`@MessageMapping`)를 설정합니다.
    *   RAG 파이프라인의 각 단계(임베딩, 검색, 생성 등)가 끝날 때마다 WebSocket을 통해 클라이언트에게 진행 상태와 중간 결과를 전송합니다.
    *   클라이언트(Thymeleaf + JavaScript)는 이 메시지를 받아 사용자에게 "문서 검색 중...", "답변 생성 중..." 과 같은 흐름을 시각적으로 보여줍니다.

---

## 2. Gemini Code Assistant 활용 개발 팁

Gemini Code Assistant를 활용하면 반복적인 작업을 자동화하고 복잡한 로직 초안을 빠르게 작성할 수 있습니다. 다음과 같이 활용해 보세요.

*   **Boilerplate 코드 생성:**
    *   "Create a JPA entity named 'User' with fields for id, email, name, and role. Use Lombok annotations."
    *   "Generate a Spring Data JPA repository for the 'User' entity."
    *   "Create a DTO record for user login response with accessToken and refreshToken fields."

*   **API 클라이언트 구현:**
    *   "Write a Spring WebClient bean to call an external API. The base URL is 'https://api.upstage.ai/v1/document-ai/parse'. It should handle multipart/form-data for file uploads and include an API key in the Authorization header."

*   **복잡한 로직 초안 작성:**
    *   "Show me an example of a Spring Security configuration for OAuth2 login with GitHub and JWT token generation upon success."
    *   "Write a native query for a Spring Data JPA repository to perform a cosine similarity search on a pgvector column named 'embedding'."

*   **테스트 코드 생성:**
    *   "Write a JUnit 5 test for my 'FileService' class. Use Mockito to mock the 'FileRepository' and verify that the save method is called when a file is uploaded."

*   **Thymeleaf 및 JavaScript 코드 생성:**
    *   "Create a Thymeleaf template for a chat interface with a message list, a text input, and a send button."
    *   "Write JavaScript code using SockJS and Stomp.js to connect to a Spring WebSocket endpoint at '/ws-chat' and handle incoming messages."

**Pro-Tip:** 단순히 "코드 짜줘"라고 하기보다, **현재 내 코드의 일부(Context)를 제공**하고 **명확한 요구사항(Instruction)**을 전달하면 훨씬 더 정확하고 유용한 코드를 얻을 수 있습니다.

---

## 3. 프로젝트 아키텍처 및 패키지 구조

본 프로젝트는 **Gradle 멀티 모듈** 기반의 **모듈러 모놀리스 아키텍처**를 채택합니다. 각 도메인 기능은 물리적으로 분리된 Gradle 하위 프로젝트(`modules/*`)로 관리하여 높은 응집도와 낮은 결합도를 지향합니다.

### 3.1. 전체 모듈 구조

전체 프로젝트는 여러 모듈로 구성되며, 각 모듈의 역할은 다음과 같습니다.

```
solar-playground/
│
├── app/                              // 🚀 메인 애플리케이션 모듈, 전역 설정
│
├── modules/                          // 📦 도메인 및 공통 모듈
│   │
│   ├── common/                       // ✨ 여러 모듈이 공유하는 공통 코드
│   │
│   ├── user/                         // 👤 사용자 인증 및 관리
│   │
│   ├── file/                         // 📁 파일 업로드 및 관리
│   │
│   ├── document/                     // 🤖 AI 문서 처리 및 임베딩
│   │
│   └── chat/                         // 💬 RAG 파이프라인 및 채팅
│
├── build.gradle                      // 루트 빌드 설정
└── settings.gradle                   // 멀티 모듈 구성
```

### 3.2. 모듈 내 패키지 구조 원칙

향후 헥사고날 아키텍처로의 전환 용이성과 관심사 분리를 위해, 각 모듈은 다음과 같은 레이어드 아키텍처 기반의 패키지 구조를 따릅니다.

```
└── modules/user/ (예시)
    └── src/main/java/cloud/luigi99/solar/playground/user/
        │
        ├── presentation/   // 1. '표현' 계층 (외부에 기능을 노출하는 모든 방식)
        │   ├── web/        // - REST API 컨트롤러
        │   └── event/      // - 다른 모듈의 이벤트를 수신하는 리스너
        │
        ├── application/    // 2. '응용' 계층 (핵심 비즈니스 로직)
        │   ├── UserService.java
        │   └── UserServiceImpl.java
        │
        ├── domain/         // 3. '도메인' 계층
        │   ├── entity/
        │   ├── dto/
        │   └── event/      // - 해당 도메인에서 발생하는 이벤트 정의
        │
        └── infrastructure/ // 4. '인프라' 계층 (외부 기술과의 연동)
            └── persistence/  // - Spring Data JPA Repository 인터페이스 등
```

#### 각 계층의 역할과 의존성 규칙

- **`presentation`**: 외부의 요청/신호를 받아 `application` 계층을 호출하는 진입점입니다. (예: `@RestController`, `@EventListener`). 이 계층은 `application` 계층에만 의존할 수 있습니다.
- **`application`**: 핵심 비즈니스 로직을 처리하는 서비스 계층입니다. 트랜잭션 관리, 이벤트 발행 등이 여기서 이루어집니다. `domain`과 `infrastructure` 계층에 의존할 수 있습니다.
- **`domain`**: 가장 순수한 핵심 영역으로, 데이터(Entity)와 비즈니스 규칙, 도메인 이벤트를 정의합니다. **다른 어떤 계층에도 의존해서는 안 됩니다.**
- **`infrastructure`**: 데이터베이스, 외부 API 등 외부 기술과 연동하는 부분을 책임집니다. `application` 계층의 서비스가 필요로 하는 기능을 구현합니다.

이러한 의존성 규칙을 통해 각 계층의 책임이 명확해지고, 향후 유지보수 및 아키텍처 전환이 용이해집니다.
