

# FacilityStatusResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**facilityId** | **UUID** |  |  [optional] |
|**availability** | [**AvailabilityEnum**](#AvailabilityEnum) |  |  [optional] |
|**capacityCurrent** | **Integer** | 현재 수용 인원 |  [optional] |
|**capacityMax** | **Integer** | 최대 수용 인원 |  [optional] |
|**waitTimeMin** | **Integer** | 예상 대기 시간 (분) |  [optional] |
|**lastUpdated** | **OffsetDateTime** |  |  [optional] |
|**offline** | **Boolean** |  |  [optional] |
|**staleWarning** | **Boolean** | 오프라인 시 데이터 부정확 경고 여부 |  [optional] |



## Enum: AvailabilityEnum

| Name | Value |
|---- | -----|
| AVAILABLE | &quot;available&quot; |
| CROWDED | &quot;crowded&quot; |
| UNAVAILABLE | &quot;unavailable&quot; |
| UNKNOWN | &quot;unknown&quot; |



