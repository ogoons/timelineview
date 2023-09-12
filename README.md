# TimelineView
[![](https://jitpack.io/v/ogoons/timelineview.svg)](https://jitpack.io/#ogoons/timelineview)

- Android TimelineView used with RecyclerView and ListView
- Embedded in [ALGO](https://play.google.com/store/apps/details?id=com.ogoons.algo)

## Overview
일반적인 실선(Solid) 형태와 점선(Dashed) 형태의 2 style을 지원하는 안드로이드 커스텀 뷰 입니다.

ListView, RecyclerView 적용 시에는 각 아이템에 padding 이나 margin 없이 붙여서 뿌려주시면 됩니다.

## Features
- Marker Style
```
- XML (Default)
- Drawable
```

- Line Orientation
 ```
- HORIZONTAL
- VERTICAL
```

- Line Style
```
- SOLID
- DASHED
```

## Demo
<img src="./demo/demo.png" width="540" height="960">

## Dependency
Step 1. Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```
dependencies {
    implementation 'com.github.ogoons:timelineview:1.0.2'
}
```

## License
```
Copyright 2017 Oh, Su-cheol <ogoons@hanmail.net>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
