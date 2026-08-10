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
│   │       ├── AnimSets/                   # 애니메이션 클립 + 전이 정보
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
│           ├── actiongroups/               # ★ 상태 전이 (B42에만 있음)
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
| `media/actiongroups` `.xml` | **0 (없음)** | 3,203 |
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
| `media/actiongroups/` | — | 0.6 MB |
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

> **한계:** 이 레포의 `engine/source/`에는 `zombie/` 패키지만 있고
> **Kahlua 라이브러리 자체(`se/krka/kahlua/`)는 포함돼 있지 않다.**
> 위 내용은 과거 41.78.19 덤프에서 확인된 사실이며, 현재 레포에서는 직접 재검증할 수 없다.
> Kahlua 표준 라이브러리 구현 여부(`next`, `xpcall`, `table.getn` 등)를 확인해야 하면
> 게임 설치 폴더의 `zombie.jar`를 별도로 디컴파일해야 한다.

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

## 4. 애니메이션 — 여기가 B41/B42 차이가 가장 크다

### 구성

| | B41 | B42 |
|---|---|---|
| 애니메이션 클립 | `media/AnimSets/` (1,652 XML) | `media/AnimSets/` (2,949 XML) |
| 상태 전이 | **레포에 없음** | `media/actiongroups/` (3,203 XML) |
| 캐릭터 타입 | 7종 (`player`, `zombie`, `zombie-crawler`, `player-vehicle`, `player-avatar`, `player-editor`, `mannequin`) | 28종 (위 + 동물 21종: `cow`, `pig`, `rabbit`, `raccoon`, `rat`, `turkey`, `ewe`, `ram`, `doe`, `buck`, `hen` 등) |
| `player` 상태(하위 디렉토리) 수 | 42 (AnimSets 기준) | 92 (actiongroups 기준) |
| `zombie` 상태(하위 디렉토리) 수 | 29 (AnimSets 기준) | 125 (actiongroups 기준) |

> **B41 주의:** 이 레포의 B41 덤프에는 **상태 전이 XML(`actiongroups/`, `transitions.xml`)이
> 포함돼 있지 않다.** AnimSets의 애니메이션 클립 정의만 있다.
> B41 상태 전이 그래프가 필요하면 게임 설치 폴더의 `media/actiongroups/`를 직접 열거나,
> `pz-modding` 스킬의 `references/anims-actiongroups-b41.md`를 참조한다.

### 읽는 법

- **`AnimSets/`** — 실제 애니메이션 클립. `<animNode>`의 `m_AnimName`이 재생할 클립,
  `m_Conditions`가 이 클립이 선택될 조건, `m_Events`가 재생 중 발생하는 이벤트
  (`SetVariable`, `PlaySound` 등)다.
- **`actiongroups/` (B42)** — 상태 전환 로직. `<캐릭터타입>/<상태>/to_*.xml`이
  "어떤 조건이면 어느 상태로 가는가"를 정의한다. `actionGroup.xml`의 `<initial>`이 시작 상태고,
  `defaultTransitions.xml`을 상속받는 상태들이 있다.

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
# 이 이름이 어디서 세팅되는지 확인
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

## 이 레포에 없는 것

포함 범위를 착각하면 "없으니까 없는 것"이라고 잘못 결론짓게 된다. 명시해 둔다.

| 없는 것 | 영향 | 대안 |
|---|---|---|
| **Kahlua 라이브러리 소스** (`se/krka/kahlua/`) | 표준 Lua 함수 구현 여부, 타입 변환 동작을 확인 불가 | `zombie.jar`를 직접 디컴파일 |
| **기타 서드파티 패키지** (`javax/`, `org/`, `com/`, `fmod/`) | LWJGL·vecmath 등 내부 동작 확인 불가 | 모딩에는 거의 무관 |
| **B41 `actiongroups/`** | B41 상태 전이 그래프 확인 불가 | 게임 설치 폴더 / `pz-modding` 스킬의 reference 문서 |
| **맵·텍스처·모델 바이너리** (`media/maps`, `texturepacks`, `models_X` 등) | 스프라이트·타일 ID 확인 불가 | 게임 설치 폴더 |
| **API 요약본 텍스트** | 예전 덤프에 있던 시그니처 큐레이션 없음 | 디렉토리 직접 검색 |

`engine/source/` 아래에는 **양쪽 빌드 모두 `zombie/` 패키지만** 있다.
바꿔 말하면 이름 검색 시 서드파티 오탐이 없다는 장점도 있다.

---

## B41 ↔ B42 차이 요약

빌드를 옮겨 붙일 때 실제로 물리는 것들:

1. **클래스 패키지 이동** — `ZombiePacket`, `DeadZombiePacket` 등이 B42에서 `packets/character/`로 내려갔다. 경로 하드코딩 금지, 항상 `find`로 찾을 것
2. **노출/전역 등록 목록 변경** — 노출 562→1001, 전역 537→753. "B41에서 안 되던 것"이 B42에서 될 수 있다
3. **스크립트 포맷 전면 개편** — `recipe` → `craftRecipe`, `entity`/`component`/`attachment` 시스템 신설, 아이템 타입 체계 변경
4. **애니메이션 구조 분리** — B42는 전이가 `actiongroups/`로 분리, 동물 21종 추가, 상태 수 2~4배(`player` 42→92, `zombie` 29→125)
5. **Lua 코드 무게중심 이동** — B42는 `server/`(130→294), `shared/`(66→373)가 대폭 증가
6. **디컴파일러 출력 포맷 차이** — 어노테이션 줄바꿈이 다르므로 검색 정규식을 그대로 재사용하면 안 된다
7. **미검증** — B41의 좀비 클라이언트 권위 모델이 B42에서도 그대로인지는 확인하지 않았다. B42 MP 로직을 짤 거면 `IsoZombie` / `NetworkZombieAI`를 직접 읽고 판단할 것

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
