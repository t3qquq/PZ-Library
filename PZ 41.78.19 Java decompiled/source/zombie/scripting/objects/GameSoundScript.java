// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;

public final class GameSoundScript extends BaseScriptObject {
    public final GameSound gameSound = new GameSound();

    public void Load(String name, String totalFile) {
        this.gameSound.name = name;
        ScriptParser.Block block0 = ScriptParser.parse(totalFile);
        block0 = block0.children.get(0);

        for (ScriptParser.Value value : block0.values) {
            String[] strings = value.string.split("=");
            String string0 = strings[0].trim();
            String string1 = strings[1].trim();
            if ("category".equals(string0)) {
                this.gameSound.category = string1;
            } else if ("is3D".equals(string0)) {
                this.gameSound.is3D = Boolean.parseBoolean(string1);
            } else if ("loop".equals(string0)) {
                this.gameSound.loop = Boolean.parseBoolean(string1);
            } else if ("master".equals(string0)) {
                this.gameSound.master = GameSound.MasterVolume.valueOf(string1);
            } else if ("maxInstancesPerEmitter".equals(string0)) {
                this.gameSound.maxInstancesPerEmitter = PZMath.tryParseInt(string1, -1);
            }
        }

        for (ScriptParser.Block block1 : block0.children) {
            if ("clip".equals(block1.type)) {
                GameSoundClip gameSoundClip = this.LoadClip(block1);
                this.gameSound.clips.add(gameSoundClip);
            }
        }
    }

    private GameSoundClip LoadClip(ScriptParser.Block block) {
        GameSoundClip gameSoundClip = new GameSoundClip(this.gameSound);

        for (ScriptParser.Value value : block.values) {
            String[] strings = value.string.split("=");
            String string0 = strings[0].trim();
            String string1 = strings[1].trim();
            if ("distanceMax".equals(string0)) {
                gameSoundClip.distanceMax = Integer.parseInt(string1);
                gameSoundClip.initFlags = (short)(gameSoundClip.initFlags | GameSoundClip.INIT_FLAG_DISTANCE_MAX);
            } else if ("distanceMin".equals(string0)) {
                gameSoundClip.distanceMin = Integer.parseInt(string1);
                gameSoundClip.initFlags = (short)(gameSoundClip.initFlags | GameSoundClip.INIT_FLAG_DISTANCE_MIN);
            } else if ("event".equals(string0)) {
                gameSoundClip.event = string1;
            } else if ("file".equals(string0)) {
                gameSoundClip.file = string1;
            } else if ("pitch".equals(string0)) {
                gameSoundClip.pitch = Float.parseFloat(string1);
            } else if ("volume".equals(string0)) {
                gameSoundClip.volume = Float.parseFloat(string1);
            } else if ("reverbFactor".equals(string0)) {
                gameSoundClip.reverbFactor = Float.parseFloat(string1);
            } else if ("reverbMaxRange".equals(string0)) {
                gameSoundClip.reverbMaxRange = Float.parseFloat(string1);
            }
        }

        return gameSoundClip;
    }

    public void reset() {
        this.gameSound.reset();
    }
}
