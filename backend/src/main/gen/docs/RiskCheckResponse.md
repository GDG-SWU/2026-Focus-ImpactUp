

# RiskCheckResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**riskDetected** | **Boolean** | 위험 감지 여부 |  [optional] |
|**riskLevel** | [**RiskLevelEnum**](#RiskLevelEnum) |  |  [optional] |
|**matchedRisks** | [**List&lt;MatchedRisk&gt;**](MatchedRisk.md) |  |  [optional] |
|**triggerHaptic** | **Boolean** | 클라이언트 햅틱 진동 트리거 여부 |  [optional] |
|**triggerAlertBanner** | **Boolean** | 화면 경고 배너 표시 여부 |  [optional] |
|**offline** | **Boolean** |  |  [optional] |



## Enum: RiskLevelEnum

| Name | Value |
|---- | -----|
| NONE | &quot;none&quot; |
| LOW | &quot;low&quot; |
| MEDIUM | &quot;medium&quot; |
| HIGH | &quot;high&quot; |
| CRITICAL | &quot;critical&quot; |



