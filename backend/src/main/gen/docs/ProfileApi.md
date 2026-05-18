# ProfileApi

All URIs are relative to *http://localhost:8080/api/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**usersOnboardPost**](ProfileApi.md#usersOnboardPost) | **POST** /users/onboard | 초기 언어 설정 (온보딩) |
| [**usersProfileCompanionsPost**](ProfileApi.md#usersProfileCompanionsPost) | **POST** /users/profile/companions | 동반자 등록 |
| [**usersProfileGet**](ProfileApi.md#usersProfileGet) | **GET** /users/profile | 프로필 전체 조회 |
| [**usersProfileHealthPost**](ProfileApi.md#usersProfileHealthPost) | **POST** /users/profile/health | 건강 프로필 등록 |
| [**usersProfileHealthPut**](ProfileApi.md#usersProfileHealthPut) | **PUT** /users/profile/health | 건강 프로필 수정 |


<a id="usersOnboardPost"></a>
# **usersOnboardPost**
> OnboardResponse usersOnboardPost(onboardRequest)

초기 언어 설정 (온보딩)

앱 최초 실행 시 디바이스 언어 감지 및 사용자 선택 언어 등록. JWT 발급.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ProfileApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    ProfileApi apiInstance = new ProfileApi(defaultClient);
    OnboardRequest onboardRequest = new OnboardRequest(); // OnboardRequest | 
    try {
      OnboardResponse result = apiInstance.usersOnboardPost(onboardRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ProfileApi#usersOnboardPost");
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
| **onboardRequest** | [**OnboardRequest**](OnboardRequest.md)|  | |

### Return type

[**OnboardResponse**](OnboardResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | 온보딩 완료, JWT 발급 |  -  |
| **400** | 유효성 검사 실패 또는 지원하지 않는 언어 |  -  |

<a id="usersProfileCompanionsPost"></a>
# **usersProfileCompanionsPost**
> CompanionResponse usersProfileCompanionsPost(companionRequest)

동반자 등록

아동·암산부 등 동반 가족 유형 등록. 추후 쉼터 필터링 시 &#39;가족/여성 전용&#39; 우선 안내에 활용. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ProfileApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    ProfileApi apiInstance = new ProfileApi(defaultClient);
    CompanionRequest companionRequest = new CompanionRequest(); // CompanionRequest | 
    try {
      CompanionResponse result = apiInstance.usersProfileCompanionsPost(companionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ProfileApi#usersProfileCompanionsPost");
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
| **companionRequest** | [**CompanionRequest**](CompanionRequest.md)|  | |

### Return type

[**CompanionResponse**](CompanionResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | 동반자 등록 성공 |  -  |
| **401** | 인증 실패 |  -  |
| **422** | 요청 Body 유효성 검사 실패 |  -  |

<a id="usersProfileGet"></a>
# **usersProfileGet**
> UserProfileResponse usersProfileGet()

프로필 전체 조회

언어, 기저질환, 알레르기, 동반자 여부 전체 조회.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ProfileApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    ProfileApi apiInstance = new ProfileApi(defaultClient);
    try {
      UserProfileResponse result = apiInstance.usersProfileGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ProfileApi#usersProfileGet");
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

[**UserProfileResponse**](UserProfileResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 프로필 조회 성공 |  * X-Offline-Cache -  <br>  |
| **401** | 인증 실패 |  -  |
| **404** | 사용자 없음 |  -  |

<a id="usersProfileHealthPost"></a>
# **usersProfileHealthPost**
> HealthProfileResponse usersProfileHealthPost(healthProfileRequest)

건강 프로필 등록

기저질환(당뇨·고혈압 등), 알레르기(페니실린 등) 정보 등록. 입력값은 OCR 위험 감지 판단 로직과 연계됨. 오프라인 시 로컬 SQLite 저장. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ProfileApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    ProfileApi apiInstance = new ProfileApi(defaultClient);
    HealthProfileRequest healthProfileRequest = new HealthProfileRequest(); // HealthProfileRequest | 
    try {
      HealthProfileResponse result = apiInstance.usersProfileHealthPost(healthProfileRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ProfileApi#usersProfileHealthPost");
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
| **healthProfileRequest** | [**HealthProfileRequest**](HealthProfileRequest.md)|  | |

### Return type

[**HealthProfileResponse**](HealthProfileResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | 건강 프로필 등록 성공 |  -  |
| **401** | 인증 실패 |  -  |
| **422** | 요청 Body 유효성 검사 실패 |  -  |

<a id="usersProfileHealthPut"></a>
# **usersProfileHealthPut**
> HealthProfileResponse usersProfileHealthPut(healthProfileRequest)

건강 프로필 수정

기저질환·알레르기·복용 약물 수정.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ProfileApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    ProfileApi apiInstance = new ProfileApi(defaultClient);
    HealthProfileRequest healthProfileRequest = new HealthProfileRequest(); // HealthProfileRequest | 
    try {
      HealthProfileResponse result = apiInstance.usersProfileHealthPut(healthProfileRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ProfileApi#usersProfileHealthPut");
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
| **healthProfileRequest** | [**HealthProfileRequest**](HealthProfileRequest.md)|  | |

### Return type

[**HealthProfileResponse**](HealthProfileResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 수정 성공 |  -  |
| **401** | 인증 실패 |  -  |
| **404** | 건강 프로필 미등록 |  -  |

