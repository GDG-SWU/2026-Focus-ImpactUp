# MapApi

All URIs are relative to *http://localhost:8080/api/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**facilitiesGet**](MapApi.md#facilitiesGet) | **GET** /facilities | 기관 목록 조회 (카테고리 필터) |
| [**facilitiesIdGet**](MapApi.md#facilitiesIdGet) | **GET** /facilities/{id} | 기관 상세 조회 |
| [**facilitiesIdStatusGet**](MapApi.md#facilitiesIdStatusGet) | **GET** /facilities/{id}/status | 기관 가용 상태 조회 |
| [**locationCurrentGet**](MapApi.md#locationCurrentGet) | **GET** /location/current | 현재 위치 조회 |
| [**locationDeadReckoningPost**](MapApi.md#locationDeadReckoningPost) | **POST** /location/dead-reckoning | 추측 항법(Dead Reckoning) 위치 추정 |
| [**locationPinPut**](MapApi.md#locationPinPut) | **PUT** /location/pin | 위치 핀 수동 수정 |
| [**mapTilesGet**](MapApi.md#mapTilesGet) | **GET** /map/tiles | 오프라인 지도 타일 조회 |


<a id="facilitiesGet"></a>
# **facilitiesGet**
> FacilityListResponse facilitiesGet(category, lat, lng, radius)

기관 목록 조회 (카테고리 필터)

캠프·병원·NGO·식수 핀 필터링 뷰 제공. 오프라인 시 로컬 캐시 반환. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.MapApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    MapApi apiInstance = new MapApi(defaultClient);
    String category = "camp"; // String | 기관 카테고리 필터 (미입력 시 전체)
    Double lat = 15.5517D; // Double | 
    Double lng = 32.5324D; // Double | 
    Integer radius = 5000; // Integer | 검색 반경 (미터, 기본 5km)
    try {
      FacilityListResponse result = apiInstance.facilitiesGet(category, lat, lng, radius);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MapApi#facilitiesGet");
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
| **category** | **String**| 기관 카테고리 필터 (미입력 시 전체) | [optional] [enum: camp, hospital, ngo, water] |
| **lat** | **Double**|  | [optional] |
| **lng** | **Double**|  | [optional] |
| **radius** | **Integer**| 검색 반경 (미터, 기본 5km) | [optional] [default to 5000] |

### Return type

[**FacilityListResponse**](FacilityListResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 기관 목록 반환 |  * X-Offline-Cache -  <br>  |
| **401** | 인증 실패 |  -  |

<a id="facilitiesIdGet"></a>
# **facilitiesIdGet**
> FacilityDetailResponse facilitiesIdGet(id)

기관 상세 조회

기관명·위치·거리·운영 여부·수용 상태·서비스 목록 반환. Bottom Sheet 형태 UI용. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.MapApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    MapApi apiInstance = new MapApi(defaultClient);
    UUID id = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"); // UUID | 
    try {
      FacilityDetailResponse result = apiInstance.facilitiesIdGet(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MapApi#facilitiesIdGet");
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
| **id** | **UUID**|  | |

### Return type

[**FacilityDetailResponse**](FacilityDetailResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 기관 상세 정보 반환 |  -  |
| **401** | 인증 실패 |  -  |
| **404** | 기관 없음 |  -  |

<a id="facilitiesIdStatusGet"></a>
# **facilitiesIdStatusGet**
> FacilityStatusResponse facilitiesIdStatusGet(id)

기관 가용 상태 조회

온라인: 수용 상태 실시간 업데이트. 오프라인: \&quot;정보가 정확하지 않을 수 있음\&quot; 배너 포함 응답. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.MapApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    MapApi apiInstance = new MapApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      FacilityStatusResponse result = apiInstance.facilitiesIdStatusGet(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MapApi#facilitiesIdStatusGet");
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
| **id** | **UUID**|  | |

### Return type

[**FacilityStatusResponse**](FacilityStatusResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 기관 상태 반환 |  * X-Offline-Cache -  <br>  |
| **401** | 인증 실패 |  -  |
| **404** | 기관 없음 |  -  |

<a id="locationCurrentGet"></a>
# **locationCurrentGet**
> CurrentLocationResponse locationCurrentGet()

현재 위치 조회

GPS 신호 기반 현재 위치 반환. 오프라인 시 파란 점 렌더링용 캐시 반환.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.MapApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    MapApi apiInstance = new MapApi(defaultClient);
    try {
      CurrentLocationResponse result = apiInstance.locationCurrentGet();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MapApi#locationCurrentGet");
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

[**CurrentLocationResponse**](CurrentLocationResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 위치 정보 반환 |  -  |
| **401** | 인증 실패 |  -  |
| **503** | GPS 신호 유실 |  -  |

<a id="locationDeadReckoningPost"></a>
# **locationDeadReckoningPost**
> DeadReckoningResponse locationDeadReckoningPost(deadReckoningRequest)

추측 항법(Dead Reckoning) 위치 추정

GPS 신호 유실 시 나침반 + 가속도계(걸음 수) 데이터로 위치 추정. 오프라인 완벽 동작. 오프라인 시 화면 상단 나침반 UI 표시. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.MapApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    MapApi apiInstance = new MapApi(defaultClient);
    DeadReckoningRequest deadReckoningRequest = new DeadReckoningRequest(); // DeadReckoningRequest | 
    try {
      DeadReckoningResponse result = apiInstance.locationDeadReckoningPost(deadReckoningRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MapApi#locationDeadReckoningPost");
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
| **deadReckoningRequest** | [**DeadReckoningRequest**](DeadReckoningRequest.md)|  | |

### Return type

[**DeadReckoningResponse**](DeadReckoningResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 추정 위치 반환 |  -  |
| **401** | 인증 실패 |  -  |
| **422** | 요청 Body 유효성 검사 실패 |  -  |
| **503** | 추측 항법 데이터 부족 |  -  |

<a id="locationPinPut"></a>
# **locationPinPut**
> LocationPinResponse locationPinPut(locationPinRequest)

위치 핀 수동 수정

사용자가 직접 위치 핀을 수정. 오프라인 시 화면 상단 나침반 UI 표시. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.MapApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    MapApi apiInstance = new MapApi(defaultClient);
    LocationPinRequest locationPinRequest = new LocationPinRequest(); // LocationPinRequest | 
    try {
      LocationPinResponse result = apiInstance.locationPinPut(locationPinRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MapApi#locationPinPut");
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
| **locationPinRequest** | [**LocationPinRequest**](LocationPinRequest.md)|  | |

### Return type

[**LocationPinResponse**](LocationPinResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 위치 핀 수정 성공 |  -  |
| **401** | 인증 실패 |  -  |
| **422** | 요청 Body 유효성 검사 실패 |  -  |

<a id="mapTilesGet"></a>
# **mapTilesGet**
> MapTilesResponse mapTilesGet(bbox, zoom)

오프라인 지도 타일 조회

Mapbox 카나리아 지역 벡터 타일 URL 템플릿 및 주요 기관 위치 GeoJSON 반환. 앱 설치 시 사전 다운로드 기반 오프라인 타일. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.MapApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    MapApi apiInstance = new MapApi(defaultClient);
    String bbox = "32.4,15.4,32.7,15.7"; // String | Bounding box (minLng,minLat,maxLng,maxLat)
    Integer zoom = 14; // Integer | 
    try {
      MapTilesResponse result = apiInstance.mapTilesGet(bbox, zoom);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling MapApi#mapTilesGet");
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
| **bbox** | **String**| Bounding box (minLng,minLat,maxLng,maxLat) | [optional] |
| **zoom** | **Integer**|  | [optional] |

### Return type

[**MapTilesResponse**](MapTilesResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 지도 타일 정보 반환 |  -  |
| **401** | 인증 실패 |  -  |

