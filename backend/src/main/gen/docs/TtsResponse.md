

# TtsResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**audioUrl** | **String** | 오디오 파일 URL (사전 녹음 언어) 또는 스트림 URL (Google TTS) |  [optional] |
|**ttsEngine** | [**TtsEngineEnum**](#TtsEngineEnum) | ar·fr → google_tts / wo·ff·ha → pre_recorded |  [optional] |
|**durationMs** | **Integer** | 오디오 재생 시간 (밀리초) |  [optional] |
|**offline** | **Boolean** |  |  [optional] |



## Enum: TtsEngineEnum

| Name | Value |
|---- | -----|
| GOOGLE_TTS | &quot;google_tts&quot; |
| PRE_RECORDED | &quot;pre_recorded&quot; |



