# StatusApi

All URIs are relative to *http://localhost:8080/api/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**guideSurvivalActionsGet**](StatusApi.md#guideSurvivalActionsGet) | **GET** /guide/survival-actions | 생존 행동 제안 조회 |
| [**homeQuickCategoriesGet**](StatusApi.md#homeQuickCategoriesGet) | **GET** /home/quick-categories | 긴급 퀵 버튼 카테고리 조회 |
| [**statusNetworkGet**](StatusApi.md#statusNetworkGet) | **GET** /status/network | 네트워크 상태 배너 조회 |


<a id="guideSurvivalActionsGet"></a>
# **guideSurvivalActionsGet**
> SurvivalActionsResponse guideSurvivalActionsGet(stage, locationLat, locationLng)

생존 행동 제안 조회

현재 생존 단계 기반 다음 할 일 안내. 로컬 캐시 기반 제안 표시 (오프라인 동작). 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.StatusApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    StatusApi apiInstance = new StatusApi(defaultClient);
    String stage = "dehydration_risk"; // String | 현재 생존 단계 (미입력 시 서버에서 자동 판단)
    Double locationLat = 15.5517D; // Double | 
    Double locationLng = 32.5324D; // Double | 
    try {
      SurvivalActionsResponse result = apiInstance.guideSurvivalActionsGet(stage, locationLat, locationLng);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling StatusApi#guideSurvivalActionsGet");
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
| **stage** | **String**| 현재 생존 단계 (미입력 시 서버에서 자동 판단) | [optional] [enum: dehydration_risk, find_water, find_food, find_shelter, move_to_facility, stable] |
| **locationLat** | **Double**|  | [optional] |
| **locationLng** | **Double**|  | [optional] |

### Return type

[**SurvivalActionsResponse**](SurvivalActionsResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 생존 행동 제안 반환 |  -  |
| **401** | 인증 실패 |  -  |

<a id="homeQuickCategoriesGet"></a>
# **homeQuickCategoriesGet**
> QuickCategoriesResponse homeQuickCategoriesGet()

긴급 퀵 버튼 카테고리 조회

의료·음식·식수·쉼터 4가지 핵심 카테고리 아이콘 및 탭 지도 딥링크 반환.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.StatusApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    StatusApi apiInstance = new StatusApi(defaultClient);
    try {
      QuickCategoriesResponse result = apiInstance.homeQuickCategoriesGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling StatusApi#homeQuickCategoriesGet");
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

[**QuickCategoriesResponse**](QuickCategoriesResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 카테고리 목록 반환 |  -  |
| **401** | 인증 실패 |  -  |

<a id="statusNetworkGet"></a>
# **statusNetworkGet**
> NetworkStatusResponse statusNetworkGet()

네트워크 상태 배너 조회

온라인/오프라인 연결 상태 및 마지막으로 확인된 위치 정보 반환. 오프라인에서도 항상 표시 (로컬 캐시 기반). 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.StatusApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    StatusApi apiInstance = new StatusApi(defaultClient);
    try {
      NetworkStatusResponse result = apiInstance.statusNetworkGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling StatusApi#statusNetworkGet");
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

[**NetworkStatusResponse**](NetworkStatusResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 상태 조회 성공 |  * X-Offline-Cache -  <br>  |
| **401** | 인증 실패 |  -  |

