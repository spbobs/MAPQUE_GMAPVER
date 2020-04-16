# MAPQUE_GMAPVER

[![HitCount](http://hits.dwyl.com/waterbobs/MAPQUE_GMAPVER.svg)](http://hits.dwyl.com/waterbobs/MAPQUE_GMAPVER)
 
 - 2-2번째 개인 프로젝트
 - 개발자 소개 : https://github.com/waterbobs/ME


# 특징
 
 1. 구글 맵을 이용한 주소 검색
 2. 내 위치, 검색한 위치 두가지 위치 확인 가능
 3. 검색한 위치는 검색 목록 화면에 저장됨
 
# 사용 기술

- Kotlin + Coroutines

- Architecture
  - MVVM (Model - ViewModel - DataBinding - View)
  - Repository
  - Koin - Dependency Injection 
  - RxJava2 + RxAndroid
  
- JetPack
  - LiveData
  - LifeCycle
  - ViewModel
  - Room 
  
- Retrofit2 & GSon & Rxjava2
- OkHttp3

- Library
  - live
    - [TedPermission](https://github.com/ParkSangGwon/TedPermission)
    - [Logger](https://github.com/orhanobut/logger)
    - [android-flat-button](https://github.com/hoang8f/android-flat-button)
    - [joda-time](https://github.com/JodaOrg/joda-time)
    - [search-dialog](https://github.com/mirrajabi/search-dialog)
    - [CircularProgressBar](https://github.com/lopspower/CircularProgressBar)
    - [android-floating-action-button](https://github.com/fstech/android-floating-action-button)
    - [material-dialogs](https://github.com/afollestad/material-dialogs)
    
  - Debug
    - [Android-Debug-Database](https://github.com/amitshekhariitbhu/Android-Debug-Database)
    - [leakcanary](https://square.github.io/leakcanary/)
  
- SDK
  - Google Map
  - Firebase analytics
  - Firebase crashlytics
  - Admob UnifiedNativeAd


# 스크린샷

<div>
  <img width=200 height=400 src="https://user-images.githubusercontent.com/8046850/79453372-967a5900-8024-11ea-81ae-2ba80c3f8253.PNG"> 
  <img width=200 height=400 src="https://user-images.githubusercontent.com/8046850/79453364-94b09580-8024-11ea-90ea-c5920d38eba3.PNG">  
  <img width=200 height=400 src="https://user-images.githubusercontent.com/8046850/79453368-95e1c280-8024-11ea-9b40-5784efb6999e.PNG">
<div>
 
# License

   Copyright 2020 BOBS

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
