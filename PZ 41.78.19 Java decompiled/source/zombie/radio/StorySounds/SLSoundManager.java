// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.StorySounds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.input.GameKeyboard;
import zombie.iso.Vector2;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

/**
 * Turbo   Story line sound manager
 */
public final class SLSoundManager {
    public static boolean ENABLED = false;
    public static boolean DEBUG = false;
    public static boolean LUA_DEBUG = false;
    public static StoryEmitter Emitter = new StoryEmitter();
    private static SLSoundManager instance;
    private HashMap<Integer, Boolean> state = new HashMap<>();
    private ArrayList<StorySound> storySounds = new ArrayList<>();
    private int nextTick = 0;
    private float borderCenterX = 10500.0F;
    private float borderCenterY = 9000.0F;
    private float borderRadiusMin = 12000.0F;
    private float borderRadiusMax = 16000.0F;
    private float borderScale = 1.0F;

    public static SLSoundManager getInstance() {
        if (instance == null) {
            instance = new SLSoundManager();
        }

        return instance;
    }

    private SLSoundManager() {
        this.state.put(12, false);
        this.state.put(13, false);
    }

    public boolean getDebug() {
        return DEBUG;
    }

    public boolean getLuaDebug() {
        return LUA_DEBUG;
    }

    public ArrayList<StorySound> getStorySounds() {
        return this.storySounds;
    }

    public void print(String line) {
        if (DEBUG) {
            System.out.println(line);
        }
    }

    public void init() {
        this.loadSounds();
    }

    public void loadSounds() {
        this.storySounds.clear();

        try {
            File file = ZomboidFileSystem.instance.getMediaFile("sound" + File.separator);
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();

                for (int int0 = 0; int0 < files.length; int0++) {
                    if (files[int0].isFile()) {
                        String string0 = files[int0].getName();
                        if (string0.lastIndexOf(".") != -1 && string0.lastIndexOf(".") != 0 && string0.substring(string0.lastIndexOf(".") + 1).equals("ogg")) {
                            String string1 = string0.substring(0, string0.lastIndexOf("."));
                            this.print("Adding sound: " + string1);
                            this.addStorySound(new StorySound(string1, 1.0F));
                        }
                    }
                }
            }
        } catch (Exception exception) {
            System.out.print(exception.getMessage());
        }
    }

    private void addStorySound(StorySound storySound) {
        this.storySounds.add(storySound);
    }

    public void updateKeys() {
        for (Entry entry : this.state.entrySet()) {
            boolean boolean0 = GameKeyboard.isKeyDown((Integer)entry.getKey());
            if (boolean0 && (Boolean)entry.getValue() != boolean0) {
                switch (entry.getKey()) {
                    case 12:
                    case 26:
                    case 53:
                    default:
                        break;
                    case 13:
                        Emitter.coordinate3D = !Emitter.coordinate3D;
                }
            }

            entry.setValue(boolean0);
        }
    }

    public void update(int storylineDay, int hour, int min) {
        this.updateKeys();
        Emitter.tick();
    }

    public void thunderTest() {
        this.nextTick--;
        if (this.nextTick <= 0) {
            this.nextTick = Rand.Next(10, 180);
            float float0 = Rand.Next(0.0F, 8000.0F);
            double double0 = Math.random() * Math.PI * 2.0;
            float float1 = this.borderCenterX + (float)(Math.cos(double0) * float0);
            float float2 = this.borderCenterY + (float)(Math.sin(double0) * float0);
            if (Rand.Next(0, 100) < 60) {
                Emitter.playSound("thunder", 1.0F, float1, float2, 0.0F, 100.0F, 8500.0F);
            } else {
                Emitter.playSound("thundereffect", 1.0F, float1, float2, 0.0F, 100.0F, 8500.0F);
            }
        }
    }

    public void render() {
        this.renderDebug();
    }

    public void renderDebug() {
        if (DEBUG) {
            String string = Emitter.coordinate3D ? "3D coordinates, X-Z-Y" : "2D coordinates X-Y-Z";
            int int0 = TextManager.instance.MeasureStringX(UIFont.Large, string) / 2;
            int int1 = TextManager.instance.MeasureStringY(UIFont.Large, string);
            int int2 = Core.getInstance().getScreenWidth() / 2;
            int int3 = Core.getInstance().getScreenHeight() / 2;
            this.renderLine(UIFont.Large, string, int2 - int0, int3);
        }
    }

    private void renderLine(UIFont uIFont, String string, int int1, int int0) {
        TextManager.instance.DrawString(uIFont, int1 + 1, int0 + 1, string, 0.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawString(uIFont, int1 - 1, int0 - 1, string, 0.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawString(uIFont, int1 + 1, int0 - 1, string, 0.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawString(uIFont, int1 - 1, int0 + 1, string, 0.0, 0.0, 0.0, 1.0);
        TextManager.instance.DrawString(uIFont, int1, int0, string, 1.0, 1.0, 1.0, 1.0);
    }

    public Vector2 getRandomBorderPosition() {
        float float0 = Rand.Next(this.borderRadiusMin * this.borderScale, this.borderRadiusMax * this.borderScale);
        double double0 = Math.random() * Math.PI * 2.0;
        float float1 = this.borderCenterX + (float)(Math.cos(double0) * float0);
        float float2 = this.borderCenterY + (float)(Math.sin(double0) * float0);
        return new Vector2(float1, float2);
    }

    public float getRandomBorderRange() {
        return Rand.Next(this.borderRadiusMin * this.borderScale * 1.5F, this.borderRadiusMax * this.borderScale * 1.5F);
    }
}
