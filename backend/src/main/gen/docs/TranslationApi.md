# TranslationApi

All URIs are relative to *http://localhost:8080/api/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**cardsPhrasebookGet**](TranslationApi.md#cardsPhrasebookGet) | **GET** /cards/phrasebook | 오프라인 번역 카드 목록 조회 |
| [**cardsRecentGet**](TranslationApi.md#cardsRecentGet) | **GET** /cards/recent | 최근 사용 카드 조회 |
| [**cardsSosGet**](TranslationApi.md#cardsSosGet) | **GET** /cards/sos | 내 정보 SOS 카드 조회 |
| [**ttsCardPost**](TranslationApi.md#ttsCardPost) | **POST** /tts/card | 번역 카드 TTS 재생 |


<a id="cardsPhrasebookGet"></a>
# **cardsPhrasebookGet**
> PhrasebookResponse cardsPhrasebookGet(category)

오프라인 번역 카드 목록 조회

상황별 자주 쓰는 카드 5개 국어 무장 목록 전체 로컬 제공. 로컬 DB 활용. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.TranslationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    TranslationApi apiInstance = new TranslationApi(defaultClient);
    String category = "medical"; // String | 카드 카테고리 (미입력 시 전체)
    try {
      PhrasebookResponse result = apiInstance.cardsPhrasebookGet(category);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TranslationApi#cardsPhrasebookGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **category** | **String**| 카드 카테고리 (미입력 시 전체) | [optional] [enum: medical, shelter, food, sos] |

### Return type

[**PhrasebookResponse**](PhrasebookResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 번역 카드 목록 반환 |  -  |
| **401** | 인증 실패 |  -  |

<a id="cardsRecentGet"></a>
# **cardsRecentGet**
> RecentCardsResponse cardsRecentGet()

최근 사용 카드 조회

자주 쓰는 카드 상단 노출 로직 + 최근 사용 카드 목록 반환.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.TranslationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    TranslationApi apiInstance = new TranslationApi(defaultClient);
    try {
      RecentCardsResponse result = apiInstance.cardsRecentGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TranslationApi#cardsRecentGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**RecentCardsResponse**](RecentCardsResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 최근 사용 카드 목록 반환 |  -  |
| **401** | 인증 실패 |  -  |

<a id="cardsSosGet"></a>
# **cardsSosGet**
> SosCardResponse cardsSosGet()

내 정보 SOS 카드 조회

0.1.2에서 입력한 건강 프로필(기저질환·알레르기·복용약)을 현지어로 자동 번역하여 전화 카드 형태 반환. 텍스트+음성 발화 지원. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.TranslationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    TranslationApi apiInstance = new TranslationApi(defaultClient);
    try {
      SosCardResponse result = apiInstance.cardsSosGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TranslationApi#cardsSosGet");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**SosCardResponse**](SosCardResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | SOS 카드 반환 |  -  |
| **401** | 인증 실패 |  -  |
| **404** | 건강 프로필 미등록 |  -  |

<a id="ttsCardPost"></a>
# **ttsCardPost**
> TtsResponse ttsCardPost(ttsRequest)

번역 카드 TTS 재생

번역 카드 탭 시 현지어 음성 출력. - ar·fr → Google TTS API (온라인 필요) - wo·ff·ha → 사전 녹음 오디오 파일 (오프라인 동작 보장) 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.TranslationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    TranslationApi apiInstance = new TranslationApi(defaultClient);
    TtsRequest ttsRequest = new TtsRequest(); // TtsRequest | 
    try {
      TtsResponse result = apiInstance.ttsCardPost(ttsRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling TranslationApi#ttsCardPost");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **ttsRequest** | [**TtsRequest**](TtsRequest.md)|  | |

### Return type

[**TtsResponse**](TtsResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | TTS 오디오 URL 반환 |  -  |
| **400** | 지원하지 않는 언어 |  -  |
| **401** | 인증 실패 |  -  |
| **503** | TTS 서비스 응답 불가 |  -  |

