

# FacilitySummary


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**name** | **String** |  |  [optional] |
|**category** | [**CategoryEnum**](#CategoryEnum) |  |  [optional] |
|**lat** | **Double** |  |  [optional] |
|**lng** | **Double** |  |  [optional] |
|**distanceM** | **Integer** | 현재 위치에서 거리 (미터) |  [optional] |
|**availability** | [**AvailabilityEnum**](#AvailabilityEnum) | 가용 상태 (🟢 available / 🟡 crowded / 🔴 unavailable) |  [optional] |
|**operating** | **Boolean** |  |  [optional] |



## Enum: CategoryEnum

| Name | Value |
|---- | -----|
| CAMP | &quot;camp&quot; |
| HOSPITAL | &quot;hospital&quot; |
| NGO | &quot;ngo&quot; |
| WATER | &quot;water&quot; |



## Enum: AvailabilityEnum

| Name | Value |
|---- | -----|
| AVAILABLE | &quot;available&quot; |
| CROWDED | &quot;crowded&quot; |
| UNAVAILABLE | &quot;unavailable&quot; |
| UNKNOWN | &quot;unknown&quot; |



