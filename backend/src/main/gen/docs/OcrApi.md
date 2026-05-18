# OcrApi

All URIs are relative to *http://localhost:8080/api/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**ocrRiskCheckPost**](OcrApi.md#ocrRiskCheckPost) | **POST** /ocr/risk-check | OCR 텍스트 위험 감지 경고 |
| [**ocrScanPost**](OcrApi.md#ocrScanPost) | **POST** /ocr/scan | 카메라 라벨 스캔 (OCR) |


<a id="ocrRiskCheckPost"></a>
# **ocrRiskCheckPost**
> RiskCheckResponse ocrRiskCheckPost(riskCheckRequest)

OCR 텍스트 위험 감지 경고

OCR 인식 텍스트를 사용자 건강 프로필(알레르기·기저질환)과 매칭. 페니실린·금기 키워드 감지 시 화면 경고 배너 + 햅틱 진동 강제 발생. 온디바이스 처리로 오프라인 완벽 동작. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.OcrApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    OcrApi apiInstance = new OcrApi(defaultClient);
    RiskCheckRequest riskCheckRequest = new RiskCheckRequest(); // RiskCheckRequest | 
    try {
      RiskCheckResponse result = apiInstance.ocrRiskCheckPost(riskCheckRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OcrApi#ocrRiskCheckPost");
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
| **riskCheckRequest** | [**RiskCheckRequest**](RiskCheckRequest.md)|  | |

### Return type

[**RiskCheckResponse**](RiskCheckResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | 위험 감지 결과 반환 |  -  |
| **401** | 인증 실패 |  -  |
| **404** | 건강 프로필 없음 (위험 감지 불가) |  -  |
| **422** | 요청 Body 유효성 검사 실패 |  -  |

<a id="ocrScanPost"></a>
# **ocrScanPost**
> OcrScanResponse ocrScanPost(image, targetLanguage)

카메라 라벨 스캔 (OCR)

ML Kit 비전 AI 기반 카메라 촬영. 의약품·안내판 텍스트 추출 → 온디바이스 국어 번역. **참고용 의료 판단 사용 금지 문구 항상 포함.** 온디바이스 처리로 오프라인 완벽 동작. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.OcrApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080/api/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    OcrApi apiInstance = new OcrApi(defaultClient);
    File image = new File("/path/to/file"); // File | 촬영 이미지 파일 (JPEG/PNG, 최대 10MB)
    String targetLanguage = "ar"; // String | 번역 대상 언어 (미입력 시 사용자 설정 언어)
    try {
      OcrScanResponse result = apiInstance.ocrScanPost(image, targetLanguage);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling OcrApi#ocrScanPost");
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
| **image** | **File**| 촬영 이미지 파일 (JPEG/PNG, 최대 10MB) | |
| **targetLanguage** | **String**| 번역 대상 언어 (미입력 시 사용자 설정 언어) | [optional] [enum: ar, fr, wo, ff, ha] |

### Return type

[**OcrScanResponse**](OcrScanResponse.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OCR 결과 반환 |  -  |
| **401** | 인증 실패 |  -  |
| **413** | 파일 크기 초과 |  -  |
| **415** | 지원하지 않는 이미지 형식 |  -  |
| **500** | OCR 처리 실패 |  -  |

