# 빌드 & 실행 방법

# 설계한 REST API 명세 요약

# ./gradlew clean test 성공 스크린샷

# Swagger UI 또는 OpenAPI JSON 노출 확인 방법

> swagger 설정은 [이전에 작성한 swagger 설정글](https://velog.io/@eora21/restdocs-swaggerUI-설정하기)을 토대로 구성하였습니다.

build를 수행하면 `src/main/resources/static/docs` 경로에 swagger 파일이 생성됩니다.

스프링이 실행된 상태에서 확인하시려면 http://localhost:8080/docs/index.html 주소로 접속하시면 됩니다.

# 구현 과정 및 고민

## 외부 API 살펴보기

### 국가목록

국가목록 API는 국가별 국가코드와 이름이 응답되는 것을 확인했습니다.

### 특정 연도 공휴일

> [공식 문서](https://date.nager.at/Api)를 참조하였습니다.

특정 연도 공휴일 API는 다음과 같은 정보들이 응답되는 것을 확인했습니다.

- date: `yyyy-MM-dd` 형식의 날짜
- localName: 해당 국가 기준 공휴일명
- name: 영문 공휴일명
- countryCode: 해당 국가 코드(ISO 3166-1 alpha-2)
- fixed: 매년 같은 날짜의 공휴일 유무, 공식문서 취소선 및 true인 예시 데이터도 false로 반환되는 것을 보아 현재는 쓰이지 않는 것으로 보입니다.
- global: 국가적 공휴일 유무
- counties: 행정 구역(ISO-3166-2) 배열, global이 false인 경우
- launchYear: 공휴일 시작 연도, 공식문서 취소선은 없으나 시작 연도가 명시된 예시 데이터를 요청해보니 null로 반환되고 있었습니다.
- types: 공휴일 유형
  - Public: 공공
  - Bank: 은행
  - School: 학교
  - Authorities: 관공서
  - Optional: 선택 사항, 회사나 개인 재량으로 쉴 수 있는 날로 보입니다.
  - Observance: 기념일이나 축제 등, 법적으로 쉴 수 있는 날은 아닌 것으로 보입니다.

## ERD

> 공식문서와 실제 데이터 상 차이가 있는 `fixed`, `launchYear`은 고려하지 않기로 했습니다.
>
> 아래의 erd 구조는 [mermaid](https://mermaid.js.org/syntax/entityRelationshipDiagram.html)를 사용하였습니다.

```mermaid
erDiagram
    countries {
        VARCHAR(2) country_code PK
        VARCHAR(100) name
    }

    countries || .. |{ holidays: country_code

    holidays {
        BIGINT holiday_id PK
        DATE date
        VARCHAR(100) name
        VARCHAR(100) local_name
        VARCHAR(2) country_code FK
    }

    holidays || -- o{ holiday_counties: holiday_id

    holiday_counties {
        BIGINT holiday_id PK
        BIGINT county_code PK
    }

    counties || -- |{ holiday_counties: county_code

    counties {
        VARCHAR(6) county_code PK
    }

    holidays || -- o{ holiday_types: holiday_id
    type || -- |{ holiday_types: type_code

    holiday_types {
        BIGINT holiday_id PK
        VARCHAR(20) type_code PK
    }

    type {
        VARCHAR(20) type_code PK
    } 
```

## 외부 api 호출을 비동기로 해보는 건 어떨까

최근 Webflux를 학습하며 Spring에서 이벤트 루프 및 비동기에 대해 학습하고 있었습니다.

여러 국가의 5년치 공휴일 데이터를 수신하기 위해서는 외부 api를 호출해야 했습니다.

이 과정에서 네트워크로 인한 I/O 블로킹이 있을 것이고, 시간을 줄이기 위해 Webflux를 사용해보면 어떨까 생각했습니다.

다만 다음과 같은 고민이 있었습니다.

### 아직 netty 이벤트 루프에 대한 이해도가 높지 않음

튜토리얼을 통해 Mono와 Flux, 백프레셔에 대해 학습했습니다.

그러나 아직 이벤트 루프에 대한 지식까지 도달하지 못했습니다.

### 일회용 R2DBC

H2 I/O 블로킹을 막기 위해서는 R2DBC를 설정해야 했습니다.

그러나 필수 스택으로 JPA(Hibernate)가 있음으로, Webflux를 위한 R2DBC와 JPA를 위한 JDBC를 함께 구성해야 했습니다.

또한 api를 통해 DB를 구성한 이후에는 R2DBC를 사용하지 않게 될 것이기에, 이는 오히려 로직의 복잡성 및 리소스 관점에서 좋지 않다고 판단하였습니다.

### 중재안

외부 api 호출만 비동기로, 이후 DB 저장 과정은 JPA를 통한 블로킹으로 구성하는 방법이 현실성도 있으면서 구현 가능할 것이라 판단되었습니다.

그러나 이 역시 Webflux에 의한 로직의 복잡성과 코드 가독성이 우려되었고, '데드라인 내에 완성하는 게 0순위'라는 판단에 의거하여 Webflux를 사용하지 않는 것으로 결정하였습니다.

대신 아이디어를 실현화하기 위해 parallelStream과 CompletableFuture로 병렬 작업 및 비동기 작업을 구성하여 시간을 최소한으로 줄일 수 있도록 했습니다.

## 외부 데이터 획득은 반복되어야 한다

기능 명세에는 최초 실행 시 외에도 특정 연도/국가 파라미터를 통해 데이터 덮어쓰기가 가능해야 한다는 조건이 있습니다.

선택 사항이지만, 배치 작업도 수행되어야 합니다.

따라서 해당 로직은 컨트롤러 또는 배치를 통해 동작되어야 하지만, 비즈니스 로직과는 거리가 있습니다. 외부 요청을 통한 데이터 획득이기 때문입니다.

따라서 `fetcher`라는, 컨트롤러 이후의 계층을 구성하려 했습니다.

해당 계층은 컨트롤러에서 호출할 수 있으며, 데이터를 가져와 리포지토리에 구성하는 역할로 생각했습니다.

즉 위치로 따지면 서비스와 같으나, 서로 맡은 역할이 다르기에 이를 구분짓고자 하는 의도였습니다.

그러나 데이터를 가져오고 이를 사용하는 곳은 한정되어 있으며, 요청할 api도 이미 정해져 있었습니다.

따라서 '차라리 nager API를 이용하는 객체를 만들고, 이를 각 컨트롤러 계층에서 호출하여 서비스로 넘겨주는 게 더 깔끔할 것 같다'는 결론을 내렸습니다.

## REST Clients

이전에 사용했었던 `RestTemplate`를 사용하려 했으나, 6.1버전부터는 `RestClient`를 추천한다는 주석을 읽게 되었습니다.

이에 [공식문서](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html)를 참조하여 RestClient를 사용하였습니다.

## 넓은 트랜잭션 + 병렬 작업 시 발생한 문제들

Fetcher를 통해 하나의 트랜잭션을 걸고, 여러 api 호출을 CompletableFuture를 통해 비동기로 동작시킨 후 저장하니 10초가 걸렸습니다.

시간을 줄이기 위해 ParallelStream을 통해 병렬 처리를 시도했더니 Country code가 null이라는 예외가 발생했습니다.

code 자체를 넘겨서 getReferenceById를 적용해봐도 'DB에 없는 code'라는 예외가 발생했습니다.

병렬 코드는 살리되 최상위 트랜잭션을 제거하여 save, saveAll 동작 시에만 트랜잭션을 동작하게 구성했더니 문제가 사라졌으며 5초가 걸렸습니다.

시간도 줄고, 문제가 사라졌지만 적절한 해결책은 아니었습니다. '기존에 왜 문제가 발생했는지 이유를 모르기 때문'입니다.

우선적으로 CompletableFuture에 대한 이해도가 낮은 상태라는 것을 인지했고, 쉬는 시간마다 '모던 자바 인 액션'을 읽으며 CompletableFuture를 학습하고 있습니다.

모든 체크포인트를 달성한 후, `NagerFetcher`에서 발생했던 문제를 완벽하게 이해하고 리팩토링해보는 것을 목표로 삼았습니다.

## 복합키 Entity 저장 시 Select 쿼리 발생시키지 않게 만들기

복합키를 사용하는 경우, JPA 특성 상 isNew를 확인하기 위해 Select 쿼리가 발생합니다.

데이터를 넣을 때는 isNew가 항상 false이기 때문에, 이를 확인할 필요가 없습니다.

따라서 isNew를 판단할 기준을 세우고, Select 쿼리 발생을 없애 더 나은 성능을 끌어내보기로 했습니다.

## from, to 날짜를 더 쉽게 받고 싶은데.. + 예외 발생 시 BindException으로 구성할 수는 없나?

필터를 위한 사용자의 입력을 좀 더 손쉽게 입력받고 싶었습니다.

예를 들어 year를 25라고만 입력해도 2025가 되고, year가 입력된 경우에는 from과 to에 월만 입력해도 잘 동작되게 만들고 싶었습니다.

만약 year가 존재하는 경우, from 혹은 to에 10만 입력해도 2025년 10월을 뜻하도록 구성하고 싶었습니다.

거기에 더해 from이라면 시작일인 2025-10-01을, to라면 종료일인 2025-10-31로 자동 완성되게끔 구현하고자 했습니다.

따라서 Request를 받는 dto에 String -> LocalDate 변경 로직을 잔뜩 입력했습니다. 그러나 제가 미처 잡지 못한 예외가 발생하는 경우, 서버는 500 에러를 발생시켰습니다.

`DateTimeParseException`을 잡아 예외 핸들링을 하고 싶었으나, 다른 BindException과 합쳐지지 않는 문제가 있었습니다.

물론 컨트롤러에서 `bindingResult`를 추가하고 에러를 입력하면 해결되는 문제였으나, '컨트롤러 메서드 가독성이 떨어진다'는 생각이 들었습니다.

이에 Validator 어노테이션을 만들고, dto의 완성된 String을 검증하도록 구성했습니다.

Validator는 LocalDate로 변경이 가능한지 확인하고, 불가능한 경우 false를 반환하여 BindException에 모든 정보가 담기게끔 구성했습니다.

# 체크리스트

- [ ] 기능 명세
  - [ ] 데이터 적재
    - [x] 최초 실행 시 외부 API 통해 N개 국가에 대한 최근 5년(2020 ~ 2025)의 공휴일을 수집 및 일괄 적재
      - [x] N개 국가 저장
      - [x] 행정 구역 저장
      - [x] 공휴일 저장
    - [ ] 리팩토링 목표
      - [ ] CompletableFuture와 parallelStream을 같이 사용했을 때 문제가 발생한 이유 찾기
      - [ ] CompletableFuture 사용 부분을 가독성있게 구성하기
      - [ ] 복합키를 지닌 Entity 저장 시 Select 쿼리 발생하지 않도록 하기
      - [ ] 효율적인 batch가 가능하도록 saveAll 구간들을 나누기
  - [ ] 검색
    - [ ] 연도별/국가별 필터 기반 공휴일 조회
    - [ ] 필터 자유 확장
      - [ ] 기간 from ~ to
      - [ ] 공휴일 타입
    - [ ] 결과는 페이징 형태로 응답
  - [ ] 재동기화
    - [ ] 특정 연도/국가 데이터 재호출하여 덮어쓰기 가능
  - [ ] 삭제
    - [ ] 특정 연도/국가 공휴일 레코드 전체 삭제
  - [ ] 배치 자동화(선택)
    - [ ] 매년 1월 2일 01:00 KST에 전년도/금년도 데이터 자동 동기화
- [ ] README
  - [ ] 빌드 & 실행 방법
  - [ ] 설계한 REST API 명세 요약(엔드포인트, 파라미터, 응답 예시)
  - [ ] ./gradlew clean test 성공 스크린샷 (테스트 작성 시)
  - [x] Swagger UI 또는 OpenAPI JSON 노출 확인 방법
