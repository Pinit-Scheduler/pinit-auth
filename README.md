# PinIt Auth

> **핀잇(PinIt) 서비스의 인증·식별을 담당하는 전용 Auth 서버**

PinIt 전체 시스템에서 **인증·민감정보**를 전담하는 마이크로 서비스입니다.  
일정/작업 도메인을 담당하는 `pinit-be` 와 분리된 **별도 바운디드 컨텍스트**로 동작하며,  
**서로 다른 데이터소스**를 사용해 사용자의 민감한 인증 정보와 사용자가 의도적으로 저장한 일정 정보를 나누어 관리합니다.

---

## 🎯 Purpose

### 1. 바운디드 컨텍스트 관점에서의 분리

핀잇에서는 `member` 라는 용어를 쓰더라도,  
**일정 도메인에서 보는 member** 와 **인증 도메인에서 보는 member** 의 관심사가 전혀 다릅니다.

- **작업/일정 도메인(`pinit-be`)의 Member**
    - “이 사람이 어떤 일정을 가지고 있는가?”
    - “이번 주에 얼마나 집중했는가?”
    - “어떤 프로젝트에 참여하고 있는가?”

- **인증 도메인(`pinit-auth`)의 Member**
    - “이 사용자는 어떤 OAuth Provider(구글/네이버 등)를 통해 로그인했는가?”
    - “이 사람의 식별자는 무엇인가? (subject, email 등)”
    - “어떤 기기에서 로그인했으며, 현재 유효한 토큰은 무엇인가?”

이처럼 **관심사와 모델링 기준이 다른 도메인**을 강제로 하나의 서비스/DB에 우겨 넣으면

- 엔티티가 비대해지고, 서로 다른 요구사항이 섞여서 **변경 영향 범위**가 커지며
- 인증 로직 수정이 일정 도메인까지 침투하거나, 반대로 일정 도메인 변경이 인증 모델에 영향을 주게 됩니다.

이를 피하기 위해 PinIt은:

> **“인증”이라는 바운디드 컨텍스트를 `pinit-auth` 라는 별도의 마이크로서비스로 분리**  
> 하고, 일정/작업 도메인(`pinit-be`)과는 **ID/토큰 수준으로만 느슨하게 연결**합니다.

---

### 2. 민감 정보와 의도적 일정 데이터의 데이터소스 분리

PinIt이 다루는 데이터는 크게 두 종류입니다.

1. **사용자가 직접 설정하지 못하는 민감 정보**
    - 소셜 로그인 Provider 정보 (Google, Naver 등)
    - Refresh Token, 인증 이력, 세션 정보
    - 알림 채널 정보 (FCM 토큰, 디바이스 정보 등)
    - 이메일, 전화번호 등 보안상 민감할 수 있는 식별 정보

2. **사용자가 의도적으로 저장한 일정/작업 정보**
    - 오늘 할 일, 프로젝트 일정, 마감일, 예상 소요 시간
    - 중요도/긴급도, 집중 시간, 통계용 로그 등

PinIt의 설계 원칙은 다음과 같습니다.

> **인증/알림 등 민감 정보는 `pinit-auth` 의 전용 DB 에만 저장하고,  
> 사용자가 “시간 관리”를 위해 의도적으로 남긴 일정 정보는 `pinit-be` 의 DB에서만 관리한다.**

이렇게 데이터 소스를 분리함으로써 얻는 이점은:

- 보안·프라이버시 관점에서
    - 일정 서비스 DB만 별도로 접근하더라도  
      **Refresh Token / Provider Credential / 알림 토큰 등은 노출되지 않음**
- 장애/운영 관점에서
    - 인증 서버의 스키마 변경·마이그레이션이 일정 도메인 DB에 영향을 주지 않음
    - 반대로 일정/통계 확장 작업이 인증/알림 데이터에 영향을 주지 않음
- DDD/아키텍처 관점에서
    - **“인증/식별” 과 “일정/작업” 의 변화 속도와 책임을 분리**
    - 각각 독립적인 배포/확장/버전업이 가능

---

### 3. Spring Boot 4.0 기반의 최신 기술 스택 활용

저는 스프링도 제대로 못 배운 상태에서 스프링부트를 공부하기 시작습니다.

- 이번 스프링부트 4.0은 는 자동 설정/스타터들을 더 잘게 쪼갠 모듈 구조로 재편되었습
    - “starter 하나 넣으면 이것저것 왕창 딸려오는 느낌”이 제거되었습니다.

- 따라서, 좀 더 가볍고 명확한 의존성 관리가 가능해진 4.0기반으로
    - 인증 서버를 구축하여, 향후 유지보수/확장에 유리한 구조를 만듭니다.
    - 각 모듈의 기능과 의존성을 분석하여 필요한 부분만 선택적으로 도입합니다.

---

## 🧱 Architecture Overview

핀잇의 인증 구조는 대략 다음과 같은 관계를 가집니다.

```mermaid
flowchart LR
    subgraph Client
        App[PinIt Web / PWA / Mobile]
    end

    subgraph AuthService[PinIt Auth Service]
        AuthAPI[/Auth REST API/]
        AuthDB[(Auth DB)]
    end

    subgraph CoreService[PinIt Core Service]
        CoreAPI[/Schedule & Stats API/]
        CoreDB[(Core DB)]
    end

    App -->|OAuth 로그인 요청| AuthAPI
    AuthAPI -->|토큰/MemberId 발급| App
    App -->|인증 토큰 포함| CoreAPI
    AuthAPI --> AuthDB
    CoreAPI --> CoreDB
````

* 클라이언트는 **소셜 로그인 → `pinit-auth` 에서 PinIt 전용 토큰 발급**을 받습니다.
* 이후 **모든 일정/통계 관련 호출은 `pinit-be` 로**, `Authorization` 헤더에 `pinit-auth` 가 발급한 토큰을 포함합니다.
* `pinit-be` 는 토큰의 **서명/만료/스코프만 검증**하고,
  `memberId` 와 같은 최소한의 식별 정보만 사용해 자신의 도메인 모델을 구성합니다.

---

## 🧩 Domain Model (개념)

주요 도메인 개념은 다음과 같이 나뉩니다.

### Auth Member (인증 관점의 사용자)

* 인증 관점에서의 “사용자 식별”을 담당하는 엔티티
    * `authMemberId` (Auth 서비스 내부 PK)
    * `provider` (GOOGLE, NAVER, …)
    * `providerUserId` (sub, id 등)
    * `status` (ACTIVE, BLOCKED 등)
* `pinit-be` 의 member 와는 1:1 혹은 1:N 관계로 매핑될 수 있으며
  필요시 `externalMemberId` 같은 필드로 연동합니다.

### Token / Session

* Access Token / Refresh Token 발급 및 관리
* 토큰 만료/재발급 처리

* 이 정보들은 모두 **Auth DB에만 저장**되며,
  일정/작업 DB에는 저장되지 않습니다.

---

## 🧰 Tech Stack

* **Language**: Java
* **Framework**

    * Spring Boot
    * Spring Security
    * Spring OAuth2 Client / Resource Server (또는 직접 구현한 OAuth 연동)
* **Database**

    * Auth 전용 RDB (예: MySQL)
* **Token**

    * JWT 기반 Access Token
    * Refresh Token (DB/캐시/쿠키 전략은 실제 구현에 맞게 사용)
* **Build**: Gradle
* **Infra (Optional)**: Docker, Kubernetes, etc.

> 실제 버전/의존성은 `build.gradle` 과 `application.yml` 을 기준으로 확인하세요.

---

## 🔑 Main Features

### 1. OAuth 로그인 & 회원 연동

* 지원 Provider 예시: Google, Naver, …
* 플로우

    1. 클라이언트가 `/auth/login/{provider}` 로 리다이렉트 또는 요청
    2. Provider 인증 성공 후, Auth 서버가 사용자 정보를 조회
    3. 기존 `AuthMember` 가 있으면 로그인, 없으면 신규 생성
    4. PinIt 전용 Access Token / Refresh Token 발급
    5. Response 로 토큰 및 최소한의 사용자 정보(`memberId` 등) 반환

### 2. 토큰 재발급

* **토큰 재발급**

    * Refresh Token 검증 후 새로운 Access Token 발급

### 3. 인증 정보 조회

* `/auth/me` 등 엔드포인트를 통해

    * 인증된 사용자의 기본 정보(예: email, provider, memberId 등) 반환
    * 일정 도메인이 필요로 하는 최소한의 식별 정보만 노출

---

## 🧭 Design Principles

핵심 설계 원칙은 다음과 같습니다.

1. **Bounded Context 우선**

    * `pinit-auth` 의 Member 모델은
      **“이 사람을 어떻게 인증하고 알림을 보낼 것인가”** 에만 집중합니다.
    * 일정/작업, 통계, 목표 관리 등은 **모두 `pinit-be` 의 책임**입니다.

2. **데이터 소스 분리**

    * 인증/알림/민감 정보는 **Auth DB** 에만 저장
    * 일정/작업/통계 정보는 **Core DB (pinit-be)** 에만 저장
    * 두 DB 사이에는 오직 `memberId` 등 최소한의 식별자 수준만 공유

3. **느슨한 결합**

    * `pinit-be` 는 Auth 도메인의 내부 구현을 알지 못합니다.
    * Auth 서버는 일정/작업 모델을 알지 못하고,
      단지 “이 사용자가 누구인지” 만 책임집니다.

---

## 📡 Integration with PinIt Core (`pinit-be`)

* 클라이언트는 먼저 `pinit-auth` 를 통해 **로그인/토큰 발급**을 받습니다.
* 이후 `Authorization: Bearer <access-token>` 형태로
  `pinit-be` 의 API 를 호출합니다.
* `pinit-be` 는 토큰에서

    * `memberId`
    * 필요한 최소 claim (예: roles, scope)
      만을 읽어 도메인 로직을 수행합니다.
* 인증 도메인의 변경(Provider 추가, 토큰 구조 변경 등)은
  가능한 한 토큰 포맷/검증 규약을 유지하면서 **일정 도메인에 영향을 최소화**하는 방향으로 진행합니다.

---