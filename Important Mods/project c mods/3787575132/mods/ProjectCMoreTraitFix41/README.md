# More Trait Fix 41

Project Zomboid **b41.78.20** 전용 픽스 모드. [More Traits](https://steamcommunity.com/sharedfiles/filedetails/?id=1299328280) (`id=ToadTraits`) 사용 중 **멀티플레이 서버 재부팅 후 Trait 아이콘은 남고 효과가 멈추는** 문제를 고친다.

원본 More Traits를 덮어쓰지 않는다. 원본이 켜져 있어야 한다.

## 원인

More Traits는 커스텀 Trait 효과를 신규 캐릭터의 `OnNewGame`에서만 일부 초기화한다. 기존 캐릭터 재접속의 `OnCreatePlayer`는 ModData 기본값만 채운다.

다음 값은 세이브에 남지 않는다.

- 휴대 무게 `maxWeightBase` (Pack Mule / Pack Mouse)
- Unwavering 부위 치유속도 modifier
- 클라이언트 전역 `luckimpact` (Lucky / Unlucky 연동 배율)

재접속 때 새 `IsoPlayer`가 기본값으로 돌아가고, Unwavering용 ModData 플래그만 남아 원본 `EveryHours` 재적용도 막힌다.

[wyweight](https://steamcommunity.com/sharedfiles/filedetails/?id=2816675062) (`id=wyweight`)를 같이 쓰면 더 깨진다. 그 모드는 `OnGameStart`에서 당시 `maxWeightBase`를 `base`로 붙잡고, **extra가 0이어도** 매분 `setMaxWeightBase(base + extra)`로 덮어쓴다. 재부팅 직후 Pack Mule이 아직 없어서 `base`가 바닐라 8이 되고, More Traits `checkWeight`(10분)가 Pack Mule을 넣어도 다음 분에 다시 8로 돌아간다.

## 이 모드가 하는 일

로드/재접속 시 **한 번**:

- Sandbox `LuckImpact`를 `luckimpact`에 다시 넣음
- Pack Mule / Pack Mouse / 기본 휴대 무게를 원본과 같은 공식으로 재설정
- Unwavering 치유속도 modifier를 해부학 기본값 위에 `+30/+60` 적용하고, 원본이 한 시간 뒤 한 번 더 더하지 않게 플래그를 맞춤
- 무술(Martial) 맨손 무기 참조, Ideal Weight / Glass Body / Quick Rest / Motion Sickness / 컨테이너 / Gym Goer 세션 기준값만 초기화
- 전용 서버에서는 클라이언트가 숫자를 보내지 않고 재적용 요청만 보낸다. 서버가 Trait·Sandbox로 무게와 Unwavering을 다시 계산한다
- wyweight가 켜져 있으면 그 `EveryOneMinute`를 원본 `checkWeight` 공식 + `extraweight`로 교체한다. 새 틱 이벤트는 추가하지 않는다

## 하지 않는 일

- 세이브에서 이미 지워진 Trait 문자열 복구
- `TraitFactory`에 가짜 Trait 등록
- `applyTraits()` 재호출 (XP boost 중복)
- 부상 / 시작 아이템 / XP / Ingenuitive 레시피 / SuperImmune 진행도 / cooldown 리셋
- 원본 HasTrait 대소문자 오타, Specialization XP, 전반적 MP 스탯 역동기

## 설치

1. More Traits (Workshop `1299328280`, Mod ID `ToadTraits`)를 켠다. wyweight를 쓰면 Workshop `2816675062`, Mod ID `wyweight`도 켠다.
2. 이 폴더를 `C:\Users\kkyuk\Zomboid41\mods\ProjectCMoreTraitFix41`로 복사한다.
3. 클라이언트 모드 목록에서 **More Traits (그리고 wyweight) 다음**에 `More Trait Fix 41`을 켠다.
4. 서버 `*.ini` 예시:

```
WorkshopItems=1299328280;2816675062
Mods=ToadTraits;wyweight;ProjectCMoreTraitFix41
```

`require=ToadTraits`가 있으므로 이 모드만 켜면 More Traits가 빠질 때 로드가 거부된다. 서버 `Mods=`에 **ToadTraits와 이 픽스**를 넣는다. wyweight는 선택.

b42에 쓰지 말 것.

## 로그

디버그 모드에서 `DebugLog.log()` 출력:

- `ProjectCMoreTraitFix41: OK trait 'packmule'` — boot 시 TraitFactory 확인
- `ProjectCMoreTraitFix41: MISSING trait '...'` — 원본이 서버/클라에 안 올라온 상태. 이 픽스로 아이콘 소실까지는 못 고침
- `ProjectCMoreTraitFix41: luckimpact=...`
- `ProjectCMoreTraitFix41: weight A -> B`
- `ProjectCMoreTraitFix41: unwavering parts=...`
- `ProjectCMoreTraitFix41: session reinit done ...`
- `ProjectCMoreTraitFix41: server reapply done ...`
- `ProjectCMoreTraitFix41: wyweight EveryOneMinute replaced`
- `ProjectCMoreTraitFix41: weight A -> B wyweight extra=...` — 값이 바뀔 때만

## 수동 테스트

1. **신규 캐릭터** — Pack Mule 무게, Unwavering, Lucky 연동이 첫 접속과 같다. 시작 부상/아이템이 두 번 들어오지 않는다.
2. **기존 캐릭터, 서버만 재부팅 후 재접속** — C키 Trait 아이콘 유지. Pack Mule/Pack Mouse 무게가 즉시 원본 공식 값. Unwavering 치유속도 즉시 복구. `EveryHours` 한 시간 대기 없음.
2b. **wyweight와 같이, 서버 재부팅 후 재접속** — Pack Mule이 바닐라 8로 돌아가지 않음. `extraweight`는 Pack Mule 위에 더해짐. extra 0이어도 Pack Mule 유지.
3. **클라이언트까지 종료 후 재접속** — 2와 동일.
4. **같은 세션에서 중복 OnCreatePlayer** — 무게/Unwavering이 두 배로 쌓이지 않는다.
5. **재접속을 여러 번** — Unwavering modifier가 접속마다 누적되지 않는다 (새 객체 기본값 + 한 번).
6. **로그** — 위 문자열이 클라/서버 콘솔에 남는다.
7. **아이콘만 사라진 세이브** — 이 모드로 복구되지 않는다. 서버에 `ToadTraits`가 켜져 있는지, boot 로그에 `MISSING trait`이 있는지 본다.
