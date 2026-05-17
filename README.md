# QOS — 72시간 난민 생존 앱

> 카나리아 제도에 도착한 난민을 위한 오프라인 우선 긴급 생존 가이드

---

## 프로젝트 소개

카나리아 제도(스페인)는 서아프리카에서 출발하는 난민들의 주요 도착지입니다.  
대부분의 난민은 언어 장벽, 네트워크 단절, 의료 정보 부재 상태로 상륙합니다.

팀 focus의 앱은 도착 직후 72시간, 인터넷 없이도 작동하는 생존 지원 앱입니다.

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| 🚨 긴급 모드 | 72시간 생존 미션 체크리스트 및 타이머 |
| 🗺 오프라인 지도 | 캠프·병원·NGO·식수 위치 (인터넷 없이 동작) |
| 🔤 AI 번역 카드 | 의료·긴급 상황 핵심 문장 다국어 제공 |
| 📷 OCR 약물 스캐너 | 약 라벨 촬영 → 알레르기 위험 자동 감지 |
| 🪪 SOS 카드 | 건강 정보를 구조대에게 보여주는 의료 카드 |
| ⚙️ 프로필 설정 | 언어·알레르기·혈액형·동반자 정보 저장 |

---

## 지원 언어

`English` `Français` `العربية` `Wolof` `Mandinka` `Fulfulde`

---

## 기술 스택

- Language : Kotlin
- UI : Jetpack Compose + Material3
- Navigation : Navigation Compose
- Storage : SharedPreferences (프로필), Room DB
- Camera : CameraX
- OCR : ML Kit Text Recognition
- 지도 : Mapbox (오프라인 타일)
- Min SDK : API 26 (Android 8.0)

---

## 프로젝트 구조

```
app/src/main/java/com/gdgswu/qos/
├── data/
│   ├── local/          # SharedPreferences (UserProfilePrefs)
│   └── model/          # TranslationData, SupportedLanguage
└── ui/
    ├── home/           # 긴급 홈 화면
    ├── guide/          # 72h 생존 가이드
    ├── map/            # 오프라인 지도
    ├── ocr/            # OCR 약물 스캐너
    ├── onboarding/     # 온보딩 (언어·건강·동반자)
    ├── profile/        # 설정
    ├── translation/    # 번역 카드 + SOS 카드
    ├── navigation/     # NavGraph, BottomNavBar
    └── theme/          # 색상, 타이포그래피

