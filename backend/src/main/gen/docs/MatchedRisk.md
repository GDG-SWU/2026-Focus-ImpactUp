

# MatchedRisk


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**keyword** | **String** | OCR 텍스트에서 감지된 위험 키워드 |  [optional] |
|**matchedProfileField** | [**MatchedProfileFieldEnum**](#MatchedProfileFieldEnum) | 사용자 건강 프로필에서 매칭된 항목 유형 |  [optional] |
|**matchedValue** | **String** | 매칭된 프로필 값 |  [optional] |
|**warningMessage** | **String** | 현지어 경고 메시지 |  [optional] |



## Enum: MatchedProfileFieldEnum

| Name | Value |
|---- | -----|
| ALLERGY | &quot;allergy&quot; |
| CONDITION | &quot;condition&quot; |
| MEDICATION_INTERACTION | &quot;medication_interaction&quot; |



