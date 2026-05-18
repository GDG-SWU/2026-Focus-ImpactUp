

# SurvivalAction


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**priority** | **Integer** | 우선순위 (1 &#x3D; 최우선) |  [optional] |
|**label** | **String** | 행동 안내 텍스트 (요청 언어 기반) |  [optional] |
|**actionType** | [**ActionTypeEnum**](#ActionTypeEnum) |  |  [optional] |
|**targetFacilityCategory** | **String** | navigate 타입 시 이동 대상 기관 카테고리 |  [optional] |



## Enum: ActionTypeEnum

| Name | Value |
|---- | -----|
| NAVIGATE | &quot;navigate&quot; |
| CALL | &quot;call&quot; |
| INFO | &quot;info&quot; |



