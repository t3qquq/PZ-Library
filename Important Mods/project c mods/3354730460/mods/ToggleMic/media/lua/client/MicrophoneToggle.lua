 -- 키바인딩 추가
MicCon = MicCon or {}
MicCon.memo = {}
MicCon.key = 'Mic_Toggle'


function MicCon.addKeyBinding(category,nameValue,defaultKey)
    if not defaultKey then defaultKey = Keyboard.KEY_NONE end

    local catKeys = MicCon.memo[category]
    if not catKeys then
        MicCon.memo[category] = {};
        catKeys = MicCon.memo[category]
    end

    table.insert(catKeys,{value=nameValue, key=defaultKey})--there can be duplicates
end

MicCon.loadKeys = MainOptions.loadKeys
MicCon.moddedKeysInserted = false
function MainOptions.loadKeys()
    if not MicCon.moddedKeysInserted then
        MicCon.insertModdedKeys()
        MicCon.moddedKeysInserted = true
    end
    
    MicCon.loadKeys()
end

function MicCon.insertModdedKeys()
    local newKeyBind = {}
    local moddedCategory = nil
    for i=1, #keyBinding do
        local binding = keyBinding[i]
        if not binding.key and binding.value then
            if moddedCategory then
                for modKBIt=1, #moddedCategory do
                    table.insert(newKeyBind,moddedCategory[modKBIt])
                end
            end
            table.insert(newKeyBind,binding)
            moddedCategory = MicCon.memo[binding.value]
            if moddedCategory then
                MicCon.memo[binding.value] = nil
            end
        else
            table.insert(newKeyBind,binding)
        end
    end
    if moddedCategory then
        for modKBIt=1, #moddedCategory do
            table.insert(newKeyBind,moddedCategory[modKBIt])
        end
        moddedCategory = nil--OCD
    end
    
    for category, moddedKeyBinds in pairs(MicCon.memo) do
        table.insert(newKeyBind,{value=moddedKeyBinds.value})
        for modKBIt=1, #moddedKeyBinds do
            table.insert(newKeyBind,moddedKeyBinds[modKBIt])
        end
    end
    MicCon.memo = {}

    keyBinding = newKeyBind
end

MicCon.addKeyBinding('[Voice]', MicCon.key, 20)







-- 마이크 상태 조회
function MicCon.micMODE()
    return getCore():getOptionVoiceMode();
end

-- 마이크 상태 설정
function MicCon.toggle(mode)
    if mode == 1 then
        print("MIC mode: "..MicCon.micMODE().." → unable to toggle")
        getPlayer():Say(string.format(getText("IGUI_nope")))
    end 
    if mode == 2 then
        getCore():setOptionVoiceMode(3)
        print("MIC mode: 2 → 3")
    end
    if mode == 3 then
        getCore():setOptionVoiceMode(2)
        print("MIC mode: 3 → 2")
    end
    MicCon.isOn = isOn;
end


-- 단축키 입력 처리
function MicCon.OnKeyStartPressed(key)
    -- if MicCon.key and keynum == MicCon.key then
    if key == getCore():getKey(MicCon.key) then
        MicCon.toggle(MicCon.micMODE()) -- 상태 토글
    end
end

-- 단축키 이벤트 핸들러 추가
Events.OnKeyStartPressed.Add(MicCon.OnKeyStartPressed)