# PZ-Library

Project Zomboid **바닐라 원본 자산 아카이브**. 모드를 만들 때 "이 API가 실제로 존재하는가"를
추측이 아니라 **원본 소스로 확인**하기 위해 존재한다.

Build 41과 Build 42를 각각 통째로 보관한다.

| 빌드 | 디렉토리 | 버전 |
|---|---|---|
| B41 | `Vanilla/b41.78.20/` | 41.78.20 |
| B42 | `Vanilla/b42.20.2/` | 42.20.2 |

---

## 왜 필요한가

PZ 모딩에는 공식 API 레퍼런스가 없다. 위키는 낡았고, LLM이 알고 있는 지식에는
**다른 빌드의 API, 다른 모드의 헬퍼 함수, 아예 존재하지 않는 그럴듯한 메서드**가 섞여 있다.
그럴듯한 이름을 지어내면 `attempt to call nil` 또는 조용한 오동작으로 끝난다.

이 레포는 41.78.20 / 42.20.2 **실제 게임 파일에서 추출한 것**이므로 유일한 근거다.
여기 없으면 없는 것이고, 여기 있는 대로만 동작한다.

> **원칙: 대상 빌드의 바닐라에 없는 API는 쓰지 않는다.
> 쓰기 전에 확인하고, 확인한 근거(빌드 + 파일:줄)를 남긴다.**

---

## 가장 먼저: 대상 빌드를 확정한다

B41과 B42는 같은 게임의 다른 엔진에 가깝다. 클래스가 다른 패키지로 옮겨졌고,
스크립트 포맷이 갈아엎였고, Lua에 노출된 클래스 수가 두 배 가까이 늘었다.

**한 빌드에서 확인한 것을 다른 빌드의 근거로 쓰면 안 된다.**
이 문서의 모든 절은 B41/B42를 구분해서 서술한다. 어느 쪽을 보고 있는지 항상 의식할 것.

빌드를 모르겠으면 모드의 `mod.info`에서 확인한다.

```
# B41 모드
Contents/mods/<모드명>/mod.info   또는   mods/<모드명>/mod.info

# B42 모드 (버전 폴더가 한 겹 더 있다)
Contents/mods/<모드명>/42/mod.info
```

---

## 레포 구조

레포는 **실제 게임 설치 디렉토리를 그대로 미러링**한다. 게임 폴더에서 보던 경로가
그대로 유지되므로, 게임 설치본과 대조하기 쉽다.

```
PZ-Library/
├── Vanilla/
│   ├── b41.78.20/
│   │   ├── engine/source/zombie/...        # 디컴파일된 자바 엔진 소스
│   │   └── media/
│   │       ├── lua/{client,server,shared}/ # 바닐라 Lua 라이브러리
│   │       ├── scripts/                    # 아이템·차량·레시피·사운드 정의
│   │       ├── AnimSets/                   # 애니메이션 클립
│   │       ├── actiongroups/               # 상태 전이 (transitions.xml 구조)
│   │       ├── animscript/, animstates/    # 근접전투 애니 스크립트
│   │       ├── clothing/                   # 의복 정의
│   │       ├── luaexamples/, shaders/
│   │       └── fileGuidTable.xml
│   └── b42.20.2/
│       ├── engine/source/zombie/...
│       └── media/
│           ├── lua/{client,server,shared}/
│           ├── scripts/{generated,entities,ragdolls,xui}/
│           ├── AnimSets/                   # 애니메이션 클립
│           ├── actiongroups/               # 상태 전이 (to_<상태>.xml 구조)
│           └── (이하 B41과 동일 구성)
└── Important Mods/
    └── ARSENAL(26)GunFighter[MOD 2.0]/     # 연동 검증용 서드파티 모드
```

### 규모 비교

| | B41 (41.78.20) | B42 (42.20.2) |
|---|---:|---:|
| `engine/source` `.java` | 1,597 | 3,076 |
| `media/lua` `.lua` | 888 | 1,395 |
| └ client / server / shared | 692 / 130 / 66 | 728 / 294 / 373 |
| `media/AnimSets` `.xml` | 1,652 | 2,949 |
| `media/actiongroups` `.xml` | 177 | 3,203 |
| `media/scripts` 파일 | 153 | 1,004 |
| Lua 노출 클래스 (`setExposed`) | 562 | 1,001 |
| Lua 전역 함수 (`@LuaMethod global`) | 537 | 753 |
| 이벤트 선언 (`AddEvent`) | 224 | 262 |

B42의 `server/`, `shared/` Lua가 크게 늘어난 점에 주목할 것 —
서버 권위 로직이 B41보다 훨씬 많이 Lua로 내려와 있다.

---

## 받는 법

전체 클론은 **약 390MB**다(`Important Mods` 169MB, 바닐라 `Translate` 131MB 포함).
대부분의 작업에는 필요 없으므로 **sparse checkout으로 필요한 것만 받는 것을 권장**한다.

### 용량 (디렉토리별)

| 디렉토리 | B41 | B42 |
|---|---:|---:|
| `engine/` | 16.6 MB | 28.1 MB |
| `media/lua/` (Translate 포함) | 36.6 MB | 120.9 MB |
| └ 그중 `shared/Translate/` | 25.6 MB | 105.2 MB |
| `media/AnimSets/` | 1.1 MB | 2.2 MB |
| `media/actiongroups/` | 0.1 MB | 0.6 MB |
| `media/scripts/` | 1.7 MB | 6.7 MB |
| **API 검증용 합계** (Translate 제외) | **약 31 MB** | **약 53 MB** |

바닐라 `Translate/`는 한글화 대조가 필요할 때 말고는 쓸 일이 없는데 용량의 대부분을 차지한다.
기본적으로 제외할 것.

### B41만 받기

```bash
git clone --depth 1 --filter=blob:none --sparse \
  https://github.com/t3qquq/PZ-Library.git
cd PZ-Library
git sparse-checkout init --no-cone
git sparse-checkout set --no-cone \
  "/Vanilla/b41.78.20/engine/" \
  "/Vanilla/b41.78.20/media/lua/" \
  "/Vanilla/b41.78.20/media/AnimSets/" \
  "/Vanilla/b41.78.20/media/actiongroups/" \
  "/Vanilla/b41.78.20/media/scripts/" \
  "!/Vanilla/b41.78.20/media/lua/shared/Translate/"
```

### B42만 받기

```bash
git sparse-checkout set --no-cone \
  "/Vanilla/b42.20.2/engine/" \
  "/Vanilla/b42.20.2/media/lua/" \
  "/Vanilla/b42.20.2/media/AnimSets/" \
  "/Vanilla/b42.20.2/media/actiongroups/" \
  "/Vanilla/b42.20.2/media/scripts/" \
  "!/Vanilla/b42.20.2/media/lua/shared/Translate/"
```

`--no-cone`을 쓰는 이유는 `!` 부정 패턴(Translate 제외)이 cone 모드에서 동작하지 않기 때문이다.
경로에 공백이 없으므로 따옴표는 편의상만 붙였다.

### 최신화

```bash
git fetch --depth 1 origin && git reset --hard FETCH_HEAD
```

---

## 1. `engine/source/` — 존재 여부 판정

**가장 많이 틀리는 지점: 자바에 `public` 메서드가 있다고 Lua에서 부를 수 있는 게 아니다.**
두 개의 독립된 관문이 있고, 이 구조는 B41과 B42가 동일하다.

### 관문 1 — 전역 함수

`zombie/Lua/LuaManager.java`의 `LuaManager.GlobalObject`에
`@LuaMethod(global = true)`로 등록된 것만 Lua에서 전역 함수로 부를 수 있다.

```bash
# B41
grep -n 'name = "getSpecificPlayer"' \
  Vanilla/b41.78.20/engine/source/zombie/Lua/LuaManager.java
# -> 2582:            name = "getSpecificPlayer",

# B42
grep -n 'name = "getSpecificPlayer"' \
  Vanilla/b42.20.2/engine/source/zombie/Lua/LuaManager.java
# -> 3788:        @LuaMethod(name = "getSpecificPlayer", global = true)
```

> **주의 — 어노테이션 표기가 빌드마다 다르다.**
> 두 빌드는 서로 다른 디컴파일러 출력이라 B41은 어노테이션이 여러 줄로 펼쳐져 있고,
> B42는 한 줄로 붙어 있다. `@LuaMethod(name="X", global=true)`를 한 줄 정규식으로 찾으면
> **B41에서만 0건이 나온다.** 여러 줄을 걸치는 검색(`grep -A2`, `pcregrep -M`, 파이썬 `re.S`)을 쓸 것.

### 관문 2 — 객체 메서드

그 클래스가 `LuaManager.Exposer.exposeAll()`의 화이트리스트(`setExposed(Foo.class)`)에
있어야 한다. 화이트리스트에 없으면 자바에 public 메서드가 아무리 많아도
Lua에서 그 객체의 메서드를 호출할 수 없다.

```bash
grep -n "setExposed(IsoZombie.class)" \
  Vanilla/b41.78.20/engine/source/zombie/Lua/LuaManager.java   # -> 1236
grep -n "setExposed(IsoZombie.class)" \
  Vanilla/b42.20.2/engine/source/zombie/Lua/LuaManager.java    # -> 1830
```

**B41 562개 → B42 1001개.** B41에서 "미등록"이던 클래스가 B42에서는 노출돼 있을 수 있고,
그 반대도 가능하다. 빌드를 바꾸면 반드시 재확인한다.

### 가장 강한 증거는 바닐라 실사용 빈도

자바에 있고 노출도 돼 있어도, 최종 확인은 `media/lua/`에서 바닐라가 실제로 쓰는지 보는 것이다.

```bash
grep -rn "getSpecificPlayer(" Vanilla/b41.78.20/media/lua/ | wc -l   # 591건 -> 확실
```

수백 건 쓰이고 있으면 확실히 동작한다. 반대로 자바에는 있는데 바닐라 사용이 0건이면
노출 여부를 반드시 별도로 확인한다.

### 이름이 맞아도 형태가 다를 수 있다

전역 함수와 객체 메서드는 별개다. 예를 들어 B41에서 `zombie:setAggroTarget(...)`은 없지만
전역 `setAggroTarget(playerNum, x, y)`는 있다. **이름만 확인하고 객체 메서드로 부르면 죽는다.**
관문 1과 관문 2를 항상 같이 볼 것.

### 시그니처가 맞아도 실패하는 경우 (Kahlua 타입 변환)

PZ는 진짜 Lua가 아니라 **Kahlua**(자바로 구현된 Lua VM)를 쓴다. Kahlua의 Lua↔자바
타입 변환기에는 구멍이 있어서, **primitive `short`를 파라미터로 받는 자바 메서드는
Lua에서 호출하면 변환에 실패한다.** (`KahluaConverterManager`가 `short.class`를
박싱 클래스가 아닌 자기 자신으로 잘못 매핑한다.)

그래서 `authorizationServerCollide(short, boolean)` 같은 건 시그니처가 멀쩡해 보여도 못 쓴다.
대안 메서드(`authorizationChanged(player)`)를 찾아야 한다.

> **2026-08 업데이트: 이제 재검증됐다.** `se/krka/kahlua/`(양쪽 빌드)와 `stdlib.lua`/`stdlib.lbc`가
> 레포에 있다 — 정확한 경로와 줄 번호는 뒤쪽 "Kahlua 소스" 섹션 참고. 이 항목은 과거 추정이 아니라
> `Vanilla/b41.78.20/engine/source/se/krka/kahlua/converter/KahluaConverterManager.java:178`,
> `Vanilla/b42.20.2/.../KahluaConverterManager.java:180`에서 직접 확인한 것이다(코드 동일).

**교훈: 시그니처만 보지 말고 파라미터 타입도 볼 것.** `short`가 보이면 대안을 찾는다.

### 이벤트 검증

이벤트는 **선언**과 **발화**가 별개다. `zombie/Lua/LuaEventManager.java`에
`AddEvent("Foo")`로 선언돼 있어도, 엔진 어디서도 `triggerEvent("Foo", ...)`를 안 부르면
영원히 안 온다. 더 중요한 건 **어디서 발화하느냐**다.

```bash
B=Vanilla/b41.78.20/engine/source
grep -rn 'AddEvent("OnZombieDead")'       $B/zombie/Lua/LuaEventManager.java
grep -rn 'triggerEvent("OnZombieDead"'    $B/zombie/
```

B41에서 `OnZombieDead`는 `IsoGameCharacter.java:5226`, `:5743`, `IsoZombie.java:4344`에서
발화한다. **`IsoZombie`는 클라이언트 권위 객체이므로, 멀티플레이 서버에서는 이 이벤트가 오지 않는다.**
서버 로직을 이 이벤트에 걸면 조용히 죽는다.

이벤트 목록 자체가 빌드마다 다르다(B41 224건 → B42 262건). 새 이벤트가 생겼을 수도,
발화 지점이 옮겨졌을 수도 있으므로 빌드별로 각각 확인한다.

### 네트워크 동기화 확인 (멀티플레이)

멀티플레이에서 캐릭터 상태를 읽을 때, 자바 필드를 직접 읽는 접근자는
**네트워크로 동기화되지 않을 수 있다.**

B41에서 `bKnockedDown`은 `IsoGameCharacter.java:357`의 private 로컬 필드이고
`ZombiePacket`의 전송 필드 목록에 없다. 그래서 **서버에서 `isKnockedDown()`은
원격 좀비에 대해 항상 false다.**

**패킷 클래스 경로가 빌드마다 다르므로 하드코딩하지 말고 찾아서 열 것:**

| | 경로 |
|---|---|
| B41 | `zombie/network/packets/ZombiePacket.java` |
| B42 | `zombie/network/packets/character/ZombiePacket.java` ← `character/`가 추가됨 |

```bash
f=$(find Vanilla/b41.78.20/engine/source -name ZombiePacket.java)
grep -nE "^\s+public .*;" "$f"
```

B41 `ZombiePacket`이 실제로 나르는 것은 좌표(`x/y/z`, `realX/realY/realZ`),
`booleanVariables`(short 비트필드), `realState`, `realHealth`, `target`, `walkType`, `speedMod` 정도다.
B42는 뼈대는 같지만 필드가 늘었다(`outfitId`, `dirAngleRads`, `predictionType`,
`grappledBy`, `reanimatedBodyId` 등). 이 목록에 없는 상태는 서버에서 신뢰할 수 없으므로
`getRealState()`나 동기화되는 애니메이션 변수로 우회한다.

---

## 2. `media/lua/` — 관용 패턴 배우기

"존재하는가"는 자바가 답하지만, **"어떻게 쓰는가"는 바닐라 Lua가 훨씬 잘 알려준다.**
타임드액션 상속, 컨텍스트 메뉴 등록, 네트워크 커맨드 송수신, 아이템 스폰 —
전부 바닐라에 살아 있는 예제가 있다.

```bash
grep -rn "sendServerCommand" Vanilla/b41.78.20/media/lua/
grep -rn "ISBaseTimedAction:derive" Vanilla/b41.78.20/media/lua/
```

**디렉토리가 곧 실행 컨텍스트 힌트다:**

| 디렉토리 | 의미 |
|---|---|
| `lua/client/` | 클라이언트 |
| `lua/server/` | 서버 |
| `lua/shared/` | 양쪽 |

다만 **`server/` 디렉토리 코드도 MP 클라이언트에서 로드된다.**
실행 여부는 `isServer()` / `isClient()` 가드로 갈린다.
바닐라가 어떤 가드를 쓰는지 함께 볼 것.

찾은 패턴은 그대로 베끼지 말고 **왜 그렇게 돼 있는지** 확인한다.
바닐라에는 하위호환 때문에 남은 낡은 패턴도, 싱글플레이 전용 코드도 섞여 있다.

---

## 3. `media/scripts/` — 아이템 / 차량 / 레시피 정의

아이템 이름, 차량 스크립트명, 레시피 구조가 필요할 때 자바를 뒤지지 말고 여기를 본다.

**B41과 B42의 구조가 완전히 다르다. 이 절은 특히 주의할 것.**

### B41 (`Vanilla/b41.78.20/media/scripts/`)

평평한 구조. 153개 파일.

```
items.txt, items_weapons.txt, items_food.txt, newitems.txt,
recipes.txt, evolvedrecipes.txt, uniquerecipes.txt, fixing.txt,
multistagebuild.txt, moveables.txt, models_items.txt,
sounds_*.txt, clothing/, vehicles/, weapons/
```

블록 키워드 빈도:

| 키워드 | 건수 |
|---|---:|
| `item` | 2,283 |
| `model` | 1,686 |
| `sound` | 1,343 |
| `template` | 1,075 |
| `recipe` | 386 |
| `vehicle` | 128 |
| `fixing` | 86 |
| `evolvedrecipe` | 38 |
| `multistagebuild` | 20 |

### B42 (`Vanilla/b42.20.2/media/scripts/`)

카테고리별로 재편됐다. 1,004개 파일.

```
generated/     items/, weapons/, vehicles/, recipes/, sounds/, characters/,
               physics/, entities/, fluids*.txt, models_*.txt, timedactions.txt ...
entities/      건설/제작 엔티티 (walls, furniture, blacksmith, animals ...)
ragdolls/
xui/
```

블록 키워드 빈도:

| 키워드 | 건수 |
|---|---:|
| `item` | 5,105 |
| `model` | 3,849 |
| `sound` | 3,038 |
| `attachment` | 2,839 |
| `craftRecipe` | 969 |
| `component` | 867 |
| `entity` | 457 |
| `vehicle` | 241 |
| `itemMapper` | 225 |
| `timedAction` | 119 |

### 핵심 차이

| | B41 | B42 |
|---|---|---|
| 레이아웃 | `scripts/` 밑에 평평하게 | `generated/`, `entities/`, `ragdolls/`, `xui/`로 분류 |
| 레시피 | `recipe <이름 공백 포함>` | **`craftRecipe <이름>`** (B41식 `recipe` 블록 자체가 없음) |
| 아이템 | `item Axe { Type = Weapon, ... }` | `item Axe { ItemType = base:weapon, ... }` — 타입 체계가 바뀜 |
| 신규 개념 | — | `entity`(건설·제작 엔티티), `component`, `attachment`, `itemMapper`, `physicsShape` |

**B41의 `recipe`를 B42에서 찾으면 안 나온다.** 블록 종류를 모르겠으면
이름으로 raw grep을 걸어서 뭐가 걸리는지 먼저 본다.

---

## 4. 애니메이션 — 파일 구조가 빌드마다 다르다

양쪽 빌드 모두 `AnimSets/`(클립)와 `actiongroups/`(상태 전이)를 갖고 있다.
**다만 전이 파일을 쪼개는 방식이 완전히 다르다.**

| | B41 | B42 |
|---|---|---|
| 애니메이션 클립 | `media/AnimSets/` (1,652 XML) | `media/AnimSets/` (2,949 XML) |
| 상태 전이 | `media/actiongroups/` (177 XML) | `media/actiongroups/` (3,203 XML) |
| 전이 파일 구성 | `transitions.xml` **한 파일에 여러 전이**<br>(+ `movementTransitions.xml`, `otherTransitions.xml`, `timedActionsTransitions.xml`, `childTags.xml`, `tags.xml`) | `to_<대상상태>.xml` **전이 하나당 파일 하나** |
| 캐릭터 타입 | 6종<br>`player`, `player-avatar`, `player-editor`, `player-vehicle`, `zombie`, `zombie-crawler` | 28종<br>위 + `animal-editor` + 동물 21종 (`cow`, `pig`, `rabbit`, `raccoon`, `rat`, `turkey`, `ewe`, `ram`, `doe`, `buck`, `hen` 등) |
| `player` 상태 수 | 41 | 92 |
| `zombie` 상태 수 | 30 | 125 |
| 초기 상태 (`<initial>`) | 전 타입 `idle` | 전 타입 `idle` |

> **주의: B42의 `to_*.xml` 파일명 규칙을 B41에 적용하면 안 된다.**
> B41 `actiongroups/`에는 `to_`로 시작하는 파일이 하나도 없다.
> 파일명이 아니라 **내용(변수명·이벤트명)으로 검색**해야 한다.

B41 `AnimSets/`에는 캐릭터 타입 디렉토리가 7종 있는데(`mannequin` 포함),
`actiongroups/`에는 6종만 있다. `mannequin`은 상태머신이 없다.

### 읽는 법

- **`AnimSets/`** — 실제 애니메이션 클립. `<animNode>`의 `m_AnimName`이 재생할 클립,
  `m_Conditions`가 이 클립이 선택될 조건, `m_Events`가 재생 중 발생하는 이벤트
  (`SetVariable`, `PlaySound` 등)다.
- **`actiongroups/`** — 상태 전환 로직. "어떤 조건이면 어느 상태로 가는가"를 정의한다.
  `<타입>/actionGroup.xml`의 `<initial>`이 시작 상태고,
  `x_include`로 타입 루트의 `defaultTransitions.xml`을 상속받는 상태들이 있다.

```
# B41
media/actiongroups/player/actionGroup.xml           # initial=idle, 상태 목록
media/actiongroups/player/defaultTransitions.xml    # 모든 상태 공통 전이
media/actiongroups/player/movement/transitions.xml  # movement 상태에서 나가는 전이 전부

# B42
media/actiongroups/player/actionGroup.xml
media/actiongroups/player/defaultTransitions.xml
media/actiongroups/player/movement/to_climbFence.xml   # 전이 하나당 파일 하나
media/actiongroups/player/movement/to_falling.xml
```

### Lua와의 연결고리는 두 종류다 — 헷갈리면 아무 일도 안 일어난다

| 종류 | XML에서의 모습 | Lua에서 다루는 법 |
|---|---|---|
| **애니메이션 변수** | `<isTrue>`, `<isFalse>`, `<compare>` 조건의 이름 (`bFalling`, `sitonground`, `FishingStage`) | `character:setVariable(name, value)` / `getVariableString`, `getVariableBoolean`, `isVariable` |
| **애니메이션 이벤트** | `<eventOccurred>` 안의 이름 (`EventClimbFence`, `EventSmashWindow`) | `character:reportEvent("EventXxx")` — **`setVariable`로는 아무 일도 안 일어난다** |

변수명은 `zombie/core/skinnedmodel/advancedanimation/AnimationVariableSource.java`에서
`toLowerCase()`로 정규화되므로 **대소문자를 가리지 않는다.**
자바가 `setVariable("bKnockedDown", ...)`으로 쓰고 XML은 `bknockeddown`으로 참조해도 같은 변수다.

**자바 쪽에서 세팅하는 변수는 주의:** 엔진이 매 틱 관리하는 변수라
Lua에서 덮어써도 즉시 되돌려질 수 있다.

```bash
# 이 이름이 어디서 세팅되고 어떤 전이를 트리거하는지 확인
grep -rn "bFalling" Vanilla/b41.78.20/media/actiongroups/
grep -rn 'setVariable("bFalling"' Vanilla/b41.78.20/engine/source/
grep -rn 'setVariable("bFalling"' Vanilla/b41.78.20/media/lua/
```

---

## 5. `Important Mods/`

서드파티 모드 연동을 검증할 때만 받는다. 약 169MB로 레포에서 가장 큰 덩어리이므로
sparse checkout에서 기본 제외할 것.

현재 포함: `ARSENAL(26)GunFighter[MOD 2.0]` (총기 시스템 오버홀 — 인벤토리 저장/복원,
무기 스폰 등에서 필드 구조를 맞춰야 할 때 참조)

---

## B41 ↔ B42 Java 배포 방식 자체가 다르다

Kahlua 소스는 이미 레포에 추가돼 있다(아래 "Kahlua 소스" 섹션). 이 절은 그 소스를 어떻게 얻었는지, 앞으로 같은 작업을 또 해야 할 때 참고할 기록이다.

| | B41 | B42 |
|---|---|---|
| Java 코드 형태 | **jar로 묶여 있지 않다.** 설치 루트에 `zombie/`, `se/`, `com/`, `de/`, `org/`, `javax/`, `fmod/`, `astar/`, `N3D/`가 압축 안 풀린 `.class` 파일로 그대로 나열돼 있다 | `projectzomboid.jar` 하나(약 64MB)에 전부 묶여 있다. 설치 루트엔 `jre64/`, `launcher/`, `license/`, `media/`, `mods/`, `Workshop/`만 있다 |
| `se/krka/kahlua/` 얻는 법 | 설치 루트에서 바로 디컴파일. **압축 해제 단계 자체가 없다** | jar를 압축 해제한 뒤 그 안에서 디컴파일 |
| `zombie/` 패키지 | 마찬가지로 설치 루트에 압축 안 풀린 채로 있다 (현재 레포의 B41 덤프가 여기서 나온 것으로 보임 — `se/`, `com/` 등 바로 옆에 있는 패키지는 그냥 안 가져간 것) | jar 안에 있다 |
| `ZombieBuddy.jar` | 없음 | 설치 루트에 있음 — 바닐라 아닌 서드파티 도구로 보임, 무관 |

즉 **B41은 Kahlua 소스를 얻는 데 추가 작업이 거의 없다.** `zombie/`를 디컴파일할 때 쓴 것과 같은 도구로 `se/krka/kahlua/`를 대상만 바꿔서 돌리면 끝이다. B42는 jar 압축 해제가 한 단계 더 필요하다.

두 빌드 모두 설치 루트(버전 폴더 구분 없이 같은 파일명)에 `stdlib.lua`, `stdlib.lbc`, `serialize.lua`가 있다. `stdlib.lua`는 컴파일이나 디컴파일 없이 바로 텍스트로 열리므로, 표준 Lua 함수 구현 여부만 급히 확인할 땐 이쪽이 제일 빠르다.

---

## Kahlua 소스 (2026-08 추가)

위 내용을 실제로 실행해서 `engine/source/se/krka/kahlua/`(양쪽 빌드)와 `stdlib.lua`/`stdlib.lbc`/`serialize.lua`(양쪽 빌드)를 레포에 추가했다. CFR 0.152로 디컴파일.

```
Vanilla/b41.78.20/engine/source/se/krka/kahlua/...   (.java 79개)
Vanilla/b42.20.2/engine/source/se/krka/kahlua/...    (.java 70개)
Vanilla/b41.78.20/stdlib.lua, stdlib.lbc, serialize.lua
Vanilla/b42.20.2/stdlib.lua, stdlib.lbc, serialize.lua
```

> **참고**: B41 `se/krka/kahlua/`엔 `.java`뿐 아니라 원본 `.class`도 같이 커밋돼 있다(B42나 기존 `zombie/` 폴더는 `.java`만). 의도적으로 남긴 게 아니라면 정리 대상.

### B41 ↔ B42 실측 차이 (자바 등록 기준)

| | B41 | B42 |
|---|---|---|
| `se.krka.kahlua.converter.KahluaEnumConverter`/`KahluaNumberConverter`/`KahluaTableConverter` | 있음 | **없음** — 레포에 이미 있는 PZ 자체 구현 `zombie.Lua.Kahlua*Converter`로 대체된 것으로 추정(상세 동작 미검증) |
| `TableLib`의 `table.ipairs` | 없음 | **새로 등록됨** |
| 전역 `ipairs` 구현 (`stdlib.lua`) | Lua로 직접 구현한 반복자 | `ipairs = table.ipairs`로 위 신규 등록에 위임 (구 구현은 주석 처리) |

`ipairs` 구현이 바뀐 이유가 `table.ipairs` 신규 등록 때문이라는 게 `stdlib.lua` diff로 명확히 확인된다 — 두 발견이 서로 맞아떨어진다.

### `short` 프리미티브 변환 버그 — B41·B42 재검증 완료

```java
// Vanilla/b41.78.20/engine/source/se/krka/kahlua/converter/KahluaConverterManager.java:178
// Vanilla/b42.20.2/engine/source/se/krka/kahlua/converter/KahluaConverterManager.java:180
PRIMITIVE_CLASS.put(Boolean.TYPE, Boolean.class);
PRIMITIVE_CLASS.put(Byte.TYPE, Byte.class);
PRIMITIVE_CLASS.put(Character.TYPE, Character.class);
PRIMITIVE_CLASS.put(Short.TYPE, Short.TYPE);      // <- 여기만 자기 자신으로 매핑됨
PRIMITIVE_CLASS.put(Integer.TYPE, Integer.class);
```
코드는 두 빌드 완전히 동일. `authorizationServerCollide(short, boolean)` 같은 `short` 파라미터 메서드가 Lua에서 호출 안 되는 이유가 이거다 — 더 이상 "과거 41.78.19 덤프에서만 확인, 재검증 불가"가 아니다.

### `stdlib.lua` 표준함수 목록 — 전수 확정

컴파일 전 원본 텍스트를 직접 읽었다. 정의된 게 정확히 이 11개가 전부다(B41/B42 공통, `ipairs`만 위 표처럼 예외):

```
assert, ipairs, pairs, table.sort,
string.len, string.rep, string.gmatch,
math.max, math.min,
coroutine.wrap
```

**정정된 것**: `coroutine.wrap`이 예전엔 "없음"으로 잘못 알려져 있었다. 실제로는 `stdlib.lua`에 그대로 구현돼 있다(`create`+`resume`을 감싼 클로저, B41 141줄·B42 144줄). `next`/`xpcall`은 자바에도 이 파일에도 없는 게 최종 확인됐다 — 진짜로 없다.

이 표준함수 가용성 판정은 별도 스킬(`kahlua-lua-compat`)이 전담한다. 전 함수 목록·검증 근거·자동 스캔 스크립트는 그쪽 `references/kahlua-api.md`에 있다. 이 README는 요약만 다룬다.

---

## 이 레포에 없는 것

포함 범위를 착각하면 "없으니까 없는 것"이라고 잘못 결론짓게 된다. 명시해 둔다.

| 없는 것 | 영향 | 대안 |
|---|---|---|
| **기타 서드파티 패키지** (B41: `com/evildevil/`, `de/jarnbjo/ogg/`, `fmod/`, `astar/`, `N3D/`, `org/joml/`, `javax/`) | 오디오 코덱·수학 라이브러리·경로탐색 등, 모딩에는 거의 무관 | — |
| **`media/anims/`(빈 폴더), `animsold/`(구버전 텍스트 애니 정의), `anims_X/`(DirectX `.X` 바이너리 애니 데이터)** — B41·B42 양쪽에 다 있음 | **확인 완료: 모딩 API 검증에 무관.** `animsold/`는 AnimSets XML로 대체된 레거시, `anims_X/`는 바이너리 애셋 | 레포에 넣을 필요 없음 |
| **맵·텍스처·모델·사운드 바이너리** (`media/maps`, `texturepacks`, `models_X`, `binary.dat`, `sound/`, `music/` 등) | 스프라이트·타일 ID·사운드 확인 불가 | 게임 설치 폴더 |
| **API 요약본 텍스트** | 예전 덤프에 있던 시그니처 큐레이션 없음 | 디렉토리 직접 검색 |

`engine/source/`에는 이제 **`zombie/`뿐 아니라 `se/krka/kahlua/`(Kahlua VM 본체)도** 있다 — 아래 "Kahlua 소스" 섹션 참고. 그 외 이름 검색 시 서드파티 오탐은 여전히 거의 없다.

---

## B41 ↔ B42 차이 요약

빌드를 옮겨 붙일 때 실제로 물리는 것들:

1. **클래스 패키지 이동** — `ZombiePacket`, `DeadZombiePacket` 등이 B42에서 `packets/character/`로 내려갔다. 경로 하드코딩 금지, 항상 `find`로 찾을 것
2. **노출/전역 등록 목록 변경** — 노출 562→1001, 전역 537→753. "B41에서 안 되던 것"이 B42에서 될 수 있다
3. **스크립트 포맷 전면 개편** — `recipe` → `craftRecipe`, `entity`/`component`/`attachment` 시스템 신설, 아이템 타입 체계 변경
4. **애니메이션 전이 파일 구조 변경** — B41은 `transitions.xml` 한 파일에 여러 전이, B42는 `to_<상태>.xml` 전이당 한 파일. 동물 21종 추가로 상태 수도 2~4배(`player` 41→92, `zombie` 30→125)
5. **Lua 코드 무게중심 이동** — B42는 `server/`(130→294), `shared/`(66→373)가 대폭 증가
6. **디컴파일러 출력 포맷 차이** — 어노테이션 줄바꿈이 다르므로 검색 정규식을 그대로 재사용하면 안 된다
7. **Kahlua 컨버터 정리** — B41의 `se.krka.kahlua.converter.KahluaEnumConverter`/`KahluaNumberConverter`/`KahluaTableConverter`가 B42엔 없다(PZ 자체 구현으로 대체 추정). B42가 `table.ipairs`(TableLib)를 새로 등록하면서 `stdlib.lua`의 전역 `ipairs` 구현도 그걸 위임하는 방식으로 바뀌었다
8. **미검증** — B41의 좀비 클라이언트 권위 모델이 B42에서도 그대로인지는 확인하지 않았다. B42 MP 로직을 짤 거면 `IsoZombie` / `NetworkZombieAI`를 직접 읽고 판단할 것

---

## 작업 흐름 요약

1. **빌드를 확정한다** — 애매하면 `mod.info` 경로로 확인
2. **쓰려는 API를 나열한다** — 함수·메서드·이벤트·애니메이션 변수·스크립트 이름 전부
3. **각각 검증한다** — 관문 1(전역) / 관문 2(노출) / 바닐라 실사용 빈도. 확인 안 된 건 쓰지 않는다
4. **바닐라 사용례를 찾는다** — `media/lua/`에서 관용 패턴과 MP 가드 확인
5. **서버/클라 실행 위치를 정한다** — (B41 기준) 좀비 조작은 소유 클라이언트, 시체 조작은 서버
6. **근거를 남긴다** — "IsoZombie에 있음"이 아니라 "B41 `IsoZombie.java:4344`에서 발화, `LuaManager.java:1236`에서 노출 등록"처럼

---

## 자주 하는 실수

- **빌드를 섞는다.** 가장 흔하고 가장 비싼 실수다. 확인한 빌드를 항상 명시한다
- **자바에 public이면 Lua에서 된다고 가정한다.** `exposeAll()` 화이트리스트를 확인한다
- **이름이 맞으면 형태도 맞을 거라 가정한다.** 전역 함수와 객체 메서드는 별개다
- **이벤트가 어디서나 발화한다고 가정한다.** 발화 지점의 권위를 확인한다
- **한 빌드에서 안 나오면 없는 거라고 단정한다.** 이름이 바뀌었거나 패키지가 옮겨졌을 수 있다. 반대 빌드도 한 번 보고, 그래도 없으면 없는 것이다
- **바닐라 코드를 맥락 없이 복사한다.** 싱글플레이 전용 코드가 많다. MP 가드를 확인한다
- **어노테이션 검색을 한 줄 정규식으로 한다.** B41에서만 0건이 나와서 "없다"고 오판한다

---

## 라이선스 / 주의

이 레포의 내용물은 The Indie Stone의 저작물이다. **모드 개발 시 참조 목적으로만** 사용한다.
재배포하거나 바닐라 코드를 그대로 모드에 포함시키지 말 것.
