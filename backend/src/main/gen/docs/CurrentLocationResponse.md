

# CurrentLocationResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**lat** | **Double** |  |  [optional] |
|**lng** | **Double** |  |  [optional] |
|**accuracyM** | **Double** | GPS 정확도 (미터) |  [optional] |
|**source** | [**SourceEnum**](#SourceEnum) | 위치 데이터 출처 |  [optional] |
|**offline** | **Boolean** |  |  [optional] |



## Enum: SourceEnum

| Name | Value |
|---- | -----|
| GPS | &quot;gps&quot; |
| DEAD_RECKONING | &quot;dead_reckoning&quot; |
| MANUAL_PIN | &quot;manual_pin&quot; |
| CACHED | &quot;cached&quot; |



