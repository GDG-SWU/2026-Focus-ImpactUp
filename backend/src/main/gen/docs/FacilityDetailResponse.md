

# FacilityDetailResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**name** | **String** |  |  [optional] |
|**category** | [**CategoryEnum**](#CategoryEnum) |  |  [optional] |
|**lat** | **Double** |  |  [optional] |
|**lng** | **Double** |  |  [optional] |
|**distanceM** | **Integer** |  |  [optional] |
|**address** | **String** |  |  [optional] |
|**operating** | **Boolean** |  |  [optional] |
|**operatingHours** | **String** |  |  [optional] |
|**availability** | [**AvailabilityEnum**](#AvailabilityEnum) |  |  [optional] |
|**services** | **List&lt;String&gt;** |  |  [optional] |
|**contactPhone** | **String** |  |  [optional] |
|**offline** | **Boolean** |  |  [optional] |
|**cachedAt** | **OffsetDateTime** |  |  [optional] |



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



