# Naver-API-Prac
패스트 캠퍼스 네이버 검색 API 활용

## 공부하면서 배운 점 기록
1. [DTO 필요성](#dto-필요성)
2. 환경변수 관리
3. API 호출하는 클라이언트 클래스 작성 및 테스트
4. [JSON 포맷터](#json-포맷터)

### DTO 필요성
DB에서 조회한 엔티티 정보들 중에서는 실제 클라이언트에 필요한 정보와 그렇지 않은 정보가 섞여있을 수 있다.
또한 외부에 공개되어선 안되는 자료일 경우, 이들을 캡슐화하는 과정이 필요하다. 따라서
클라이언트 단에 전송되는 데이터는 실제 객체가 아닌 DTO 형태로 변환된다. DTO를 설계하기 위해선
해당 로직에서 필요한 정보들이 무엇인지를 파악해야 한다. 외부 API(네이버 검색 API)의 정보를 가져오기
위해서 사용하는 DTO의 경우, 실제 API 문서에서 명시한 필드들의 정보를 갖는 DTO로 정의된다.


### JSON 포맷터
API 호출결과를 확인하기 위해 JSON 포맷터를 활용하는 실습을 진행하였다.
기존의 편집기에서 호출된 결과는 한 눈에 잘 보이지 않고 오류를 검증하기 어려웠다.
하지만 [JSON 포맷터](https://jsonformatter.curiousconcept.com/)를 사용하면
값을 JSON 형태에 맞춰서 보여준다.

![JSON Validation 1](https://github.com/whxogus215/Naver-API-Prac/assets/70999462/c7da8ee2-d981-4f45-8d8d-dc7dcbc673b1)
![JSON Validation 2](https://github.com/whxogus215/Naver-API-Prac/assets/70999462/554b6207-2907-4db2-a613-c01338c81c90)

