

# SurvivalActionsResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**stage** | [**StageEnum**](#StageEnum) | 현재 생존 단계 |  [optional] |
|**actions** | [**List&lt;SurvivalAction&gt;**](SurvivalAction.md) |  |  [optional] |
|**offline** | **Boolean** |  |  [optional] |
|**cachedAt** | **OffsetDateTime** |  |  [optional] |



## Enum: StageEnum

| Name | Value |
|---- | -----|
| DEHYDRATION_RISK | &quot;dehydration_risk&quot; |
| FIND_WATER | &quot;find_water&quot; |
| FIND_FOOD | &quot;find_food&quot; |
| FIND_SHELTER | &quot;find_shelter&quot; |
| MOVE_TO_FACILITY | &quot;move_to_facility&quot; |
| STABLE | &quot;stable&quot; |



