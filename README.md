# Naver-API-Prac
패스트 캠퍼스 네이버 검색 API 활용

## 공부하면서 배운 점 기록
1. [DTO 필요성](#dto-필요성)
2. [환경변수 관리](#환경변수-관리)
3. [API 호출하는 클라이언트 클래스 작성 및 테스트](#api-호출하는-클라이언트-클래스-작성-및-테스트)
4. [JSON 포맷터](#json-포맷터)
5. [요구사항 분석의 필요성](#요구사항-분석의-필요성)
6. [Swagger 활용](#swagger)
7. [Spring과 Vue.js의 연동방법](#spring과-vuejs의-연동방법)
8. [아키텍쳐 구조](#아키텍쳐-구조)

### DTO 필요성
DB에서 조회한 엔티티 정보들 중에서는 실제 클라이언트에 필요한 정보와 그렇지 않은 정보가 섞여있을 수 있다.
또한 외부에 공개되어선 안되는 자료일 경우, 이들을 캡슐화하는 과정이 필요하다. 따라서
클라이언트 단에 전송되는 데이터는 실제 객체가 아닌 DTO 형태로 변환된다. DTO를 설계하기 위해선
해당 로직에서 필요한 정보들이 무엇인지를 파악해야 한다. 외부 API(네이버 검색 API)의 정보를 가져오기
위해서 사용하는 DTO의 경우, 실제 API 문서에서 명시한 필드들의 정보를 갖는 DTO로 정의된다.

### API 호출하는 클라이언트 클래스 작성 및 테스트
해당 프로젝트는 네이버 검색 API를 통해 JSON 데이터를 가져오기 때문에 별도의 클라이언트 객체가 존재한다.
또한 네이버 API와 통신하기 위해 별도의 dto를 생성하여 관리하고 있다. 이 dto의 필드들 또한 네이버 API 문서에서
명시한 필드들과 타입 및 변수명이 동일하다.
![API 요청](https://github.com/whxogus215/Naver-API-Prac/assets/70999462/2698a5c6-6880-4316-bd22-6fbc8c9560df)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchImageReq {
    private String query = "";
    private int display = 1;
    private int start = 1;
    private String sort = "sim";
    private String filter = "all";

    // UriComponentsBuilder의 queryparams()가 매개변수로 MultiValueMap 타입을 받기 때문에 만든 메서드
    public MultiValueMap<String, String> toMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();

        map.add("query", query);
        map.add("display", String.valueOf(display));
        map.add("start", String.valueOf(start));
        map.add("sort", sort);
        map.add("filter", filter);

        return map;
    }
}
```
![API 응답](https://github.com/whxogus215/Naver-API-Prac/assets/70999462/16de8184-1d68-4502-b58e-a7d349634e21)

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchImageRes {

    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<SearchImageItem> items;

    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchImageItem {
        private String title;
        private String link;
        private String thumbnail;
        private String sizeheight;
        private String sizewidth;
    }
}
```
또한 해당 API의 결과값을 확인하기 위해 JUnit 기반 테스트 코드를 작성하여 테스트하였다. 기존에는 테스트 코드 작성법을 몰라서
해당 API 홈페이지에서 직접 테스트를 했었다. 하지만 이제는 테스트 코드를 통해 응답 결과를 확인하고
테스트하는 방법을 배웠다.
```java
@SpringBootTest
public class NaverClientTest {

    @Autowired
    private NaverClient naverClient;


    @Test
    public void searchLocalTest() {

        var search = new SearchLocalReq();
        search.setQuery("갈비집");

        var result = naverClient.searchLocal(search);

        System.out.println(result);
        Assertions.assertNotNull(result.getItems().stream().findFirst().get().getCategory());
    }

    @Test
    public void searchImageTest() {
        var search = new SearchImageReq();
        search.setQuery("갈비집");

        var result = naverClient.searchImage(search);
        System.out.println(result);
    }
}
```

### 환경변수 관리
외부 API와의 연동을 위해 API KEY 및 SECRET KEY가 필요한 경우가 있다. 필자 또한 이전에 Node JS로 프로젝트를
진행했을 때 외부 API를 사용하였기에 API KEY를 관리하는 법을 배웠다. 환경변수는 프로젝트의 환경에 관계없이
일정하게 관리될 수 있도록 하나의 파일에 관리하는 것이다. 또한 외부로 노출되는 것을 방지할 수도 있다. 만약 원격 저장소인
Github에 올리는 경우, 공개되어서는 안되는 API KEY를 환경변수로 관리한 뒤 `.gitignore`를 통해 원격 저장소에 올라가지 않도록
하면 되는 것이다.


### JSON 포맷터
API 호출결과를 확인하기 위해 JSON 포맷터를 활용하는 실습을 진행하였다.
기존의 편집기에서 호출된 결과는 한 눈에 잘 보이지 않고 오류를 검증하기 어려웠다.
하지만 [JSON 포맷터](https://jsonformatter.curiousconcept.com/)를 사용하면
값을 JSON 형태에 맞춰서 보여준다.

![JSON Validation 1](https://github.com/whxogus215/Naver-API-Prac/assets/70999462/c7da8ee2-d981-4f45-8d8d-dc7dcbc673b1)
![JSON Validation 2](https://github.com/whxogus215/Naver-API-Prac/assets/70999462/554b6207-2907-4db2-a613-c01338c81c90)

### 요구사항 분석의 필요성
해당 프로젝트에서는 정해진 웹 화면을 기준으로 기능들을 설계하였다. 즉, 설계된 웹 화면에 있는
추가, 삭제, 조회 등 기능들을 하나씩 구현해나갔다. 따라서 웹 개발 과정에서 프론트엔드와 백엔드 그리고 기획 간
소통이 얼마나 중요한 것인지를 간접적으로 체험할 수 있었다. 필자가 앞으로 진행하게 될 개인 프로젝트 및
소모임 웹 게시판 프로젝트에도 많은 도움이 될 것 같다. 그동안은 요구사항 분석이라는 것이 정확하게
뭘 정의하는 것인지 몰랐는데 이번 실습을 통해 갈피를 잡을 수 있었다.

### Swagger
Swagger라는 것이 무엇인지만 알고 있었고 실제로 사용해본 적이 없었다. 하지만 이번 기회에
실제로 적용하고 확인한 결과, 웹 개발 과정에서 매우 유용한 라이브러리임을 알 수 있었다.
실제 API 테스트 또한 가능하며, 설정에 따라 다양한 정보들을 한 눈에 알아볼 수 있도록 보여주는
기능이다. 이는 단순히 백엔드 개발자에게만 유용한 것이 아니라 프론트엔드 개발자에게도 유용한 API 문서
개발 툴인 것 같다. 앞으로 진행할 프로젝트에도 적용해 볼 예정이다.

**스프링부트 2.6.x 버전 부터 지원이 가능한 Springdoc 라이브러리를 사용하였다. (강의에서는 Springfox를 사용)**

![스웨거](https://github.com/whxogus215/Naver-API-Prac/assets/70999462/0bf94e9d-bb26-4149-966c-83c78693e6aa)

### Spring과 Vue.js의 연동방법
해당 강의에서는 Vue 및 프론트 설계에 대해 전혀 다루지 않는다. 하지만 이전에 공부한 경험을 바탕으로
해당 소스파일들을 분석해보았다. 먼저 클라이언트에서 호출하는 경로는 우리가 설계한 API URL이 아니다.
단순한 페이지를 조회하는 URL이었기에 `templates` 하위에 존재하는 `main.html` 파일을 보여준다.
```html
<!DOCTYPE html>
<html lang="ko" xmlns:v-bind="http://www.w3.org/1999/xhtml" xmlns:v-on="http://www.w3.org/1999/xhtml">

<head>
  <meta charset="UTF-8">
  <title>맛집 WISH LIST</title>
</head>

<body>
<br/>

<div class="container">
  <!-- search -->
  <div class="row">
    <div class="col-sm-6 col-md-8">
      <input id="searchBox" style="height: 46px" class="form-control form-control-lg" type="text" placeholder="맛집을 검색해주세요 ex.(판교 갈비집)" value="갈비집">
    </div>
    <div class="col-sm-6 col-md-4">
      <button id="searchButton" type="button" class="btn btn-primary btn-lg" style="width: 100%">검색</button>
    </div>
  </div>

  <br/>
  <!-- search result -->
  <div class="row" id="search-result" style="visibility: hidden">
    <div class="col-sm-6 col-md-8">
      <img id="wish_image" v-bind:src="search_result.imageLink" alt="..." class="img-thumbnail" style="min-width: 100%; min-height: 100%;">
    </div>
    <div class="col-sm-6 col-md-4">
      <ul class="list-group list-group-flush">
        <li class="list-group-item" id="wish_title">{{search_result.title}}</li>
        <li class="list-group-item" id="wish_category">{{search_result.category}}</li>
        <li class="list-group-item" id="wish_address">{{search_result.address}}</li>
        <li class="list-group-item" id="wish_road_address">{{search_result.roadAddress}}</li>
        <li class="list-group-item"><a id="wish_link" target="_blank" v-bind:href="search_result.homePageLink">홈페이지</a> </li>
      </ul>
      <button id="wishButton" type="button" class="btn btn-primary btn-lg" style="width: 96%; position: absolute; bottom: 0">위시리스트 추가</button>
    </div>
  </div>

  <br/><br/><br/>

  <div class="row">
    <div class="alert alert-info col-sm-12 col-md-12" style="text-align: center">
      나의 맛집 리스트
    </div>
  </div>

  <br/>
  <div id="wish-list">
    <div v-for="wish in wish_list">
      <br/><hr/>
      <div class="row">
        <div class="col-sm-6 col-md-8">
          <img v-bind:src="wish.imageLink"
               alt="..."
               class="img-thumbnail"
               style="min-width: 100%;
                         min-height: 100%;"
          >
        </div>
        <div class="col-sm-6 col-md-4">
          <ul class="list-group list-group-flush">
            <li class="list-group-item">장소 : {{wish.title}}</li>
            <li class="list-group-item">Category : {{wish.category}}</li>
            <li class="list-group-item">주소 : {{wish.address}}</li>
            <li class="list-group-item">도로명 : {{wish.roadAddress}}</li>
            <li class="list-group-item">방명여부 : {{wish.visit}}</li>
            <li class="list-group-item">마지막 방문일자 : {{wish.lastVisitDate}}</li>
            <li class="list-group-item">방문횟수 : {{wish.visitCount}}</li>
            <li class="list-group-item">
              <a href="http://imf0010.cafe24.com/m/imf0020">홈페이지</a>
            </li>
            <li class="list-group-item">
              <button v-on:click="addVisit(wish.index)" type="button" class="btn btn-primary btn-lg" style="width: 100%;">방문 추가</button>
              <br/><br/>
              <button v-on:click="deleteWish(wish.index)" type="button" class="btn btn-primary btn-lg" style="width: 100%;">위시리스트 삭제</button>
            </li>
            <li class="list-group-item"></li>
          </ul>
        </div>
        <br/>
      </div>
      <hr>
    </div>
  </div>
</div>  <!-- container end -->



</body>


<!-- jQuery (부트스트랩의 자바스크립트 플러그인을 위해 필요합니다) -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

<!-- CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" integrity="sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2" crossorigin="anonymous">

<!-- 합쳐지고 최소화된 최신 자바스크립트 -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>

<!-- 개발버전, 도움되는 콘솔 경고를 포함. -->
<script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>

<script src="/js/main.js"></script>
</html>
```
해당 파일에는 Form이 존재하지 않기 때문에 직접 API를 호출하지 않는다. 그저 버튼을 눌렀을 때
우리가 설계한 API가 호출되어야 하는 것이다. 즉, 이를 담당하는 것이 JS로 설계된 Vue.js의 역할이다.
```javascript
(function ($) {

    // 검색 결과 vue object
    var search_result = new Vue({
        el: '#search-result',
        data: {
            search_result : {}
        },
        method: {
            wishButton: function (event) {
                console.log("add");
            }
        }
    });

    // 맛집 목록 vue object
    var wish_list = new Vue({
        el: '#wish-list',
        data: {
            wish_list : {}
        },
        methods: {
            addVisit: function (index) {
                $.ajax({
                    type: "POST" ,
                    async: true ,
                    url: `/api/restaurant/${index}`,
                    timeout: 3000
                });

                getWishList();
            },
            deleteWish: function (index) {
                $.ajax({
                    type: "DELETE" ,
                    async: true ,
                    url: `/api/restaurant/${index}`,
                    timeout: 3000
                });
                getWishList();
            }
        }
    });

    // search
    $("#searchButton").click(function () {
        const query = $("#searchBox").val();
        $.get(`/api/restaurant/search?query=${query}`, function (response) {
            search_result.search_result = response;
            $('#search-result').attr('style','visible');
        });
    });

    // Enter
    $("#searchBox").keydown(function(key) {
        if (key.keyCode === 13) {
            const query = $("#searchBox").val();
            $.get(`/api/restaurant/search?query=${query}`, function (response) {
                search_result.search_result = response;
                $('#search-result').attr('style','visible');
            });
        }
    });

    $("#wishButton").click(function () {
        $.ajax({
            type: "POST" ,
            async: true ,
            url: "/api/restaurant",
            timeout: 3000,
            data: JSON.stringify(search_result.search_result),
            contentType: "application/json",
            error: function (request, status, error) {

            },
            success: function (response, status, request) {
                getWishList();
            }
        });
    });

    function getWishList(){
        $.get(`/api/restaurant/all`, function (response) {
            wish_list.wish_list = response;
        });
    }

    $(document).ready(function () {
        console.log("init")
    });

})(jQuery);

```
해당 코드에서 알 수 있듯이, 특정 버튼을 눌렀을 때 어떤 URL 경로를 호출하는지, 어떤 호출 메서드를 사용하는지 까지
정의가 되어 있다. 따라서 우리가 설계한 API는 단순히 사용자에게 보여주기 위한 API로 네이버 API와 마찬가지의 역할을 한다.

### 아키텍쳐 구조
![패스트 캠퍼스 실습 예제 아키텍쳐](https://github.com/whxogus215/Naver-API-Prac/assets/70999462/dbf27b36-ddd1-41d6-8e8b-9d3df3633558)

출처 :  
HTML - https://www.flaticon.com/kr/free-icon/html_180891?related_id=172519&origin=search  
JSON - https://www.flaticon.com/kr/free-icon/json-file_6394065?term=json&page=1&position=3&origin=search&related_id=6394065

