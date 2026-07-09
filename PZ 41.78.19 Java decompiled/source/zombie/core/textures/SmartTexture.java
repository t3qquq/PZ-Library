// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import org.lwjgl.opengl.GL11;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.CharacterSmartTexture;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.SmartShader;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.utils.WrappedBuffer;
import zombie.debug.DebugLog;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;

public class SmartTexture extends Texture {
    public final ArrayList<TextureCombinerCommand> commands = new ArrayList<>();
    public Texture result;
    private boolean dirty = true;
    private static SmartShader hue;
    private static SmartShader tint;
    private static SmartShader masked;
    private static SmartShader dirtMask;
    private final HashMap<Integer, ArrayList<Integer>> categoryMap = new HashMap<>();
    private static SmartShader bodyMask;
    private static SmartShader bodyMaskTint;
    private static SmartShader bodyMaskHue;
    private static final ArrayList<TextureCombinerShaderParam> bodyMaskParams = new ArrayList<>();
    private static SmartShader addHole;
    private static final ArrayList<TextureCombinerShaderParam> addHoleParams = new ArrayList<>();
    private static SmartShader removeHole;
    private static final ArrayList<TextureCombinerShaderParam> removeHoleParams = new ArrayList<>();
    private static SmartShader blit;

    public SmartTexture() {
        this.name = "SmartTexture";
    }

    void addToCat(int int0) {
        ArrayList arrayList = null;
        if (!this.categoryMap.containsKey(int0)) {
            arrayList = new ArrayList();
            this.categoryMap.put(int0, arrayList);
        } else {
            arrayList = this.categoryMap.get(int0);
        }

        arrayList.add(this.commands.size());
    }

    public TextureCombinerCommand getFirstFromCategory(int int0) {
        return !this.categoryMap.containsKey(int0) ? null : this.commands.get(this.categoryMap.get(int0).get(0));
    }

    public void addOverlayPatches(String string0, String string1, int int0) {
        if (blit == null) {
            this.create();
        }

        this.addToCat(int0);
        ArrayList arrayList = new ArrayList();
        this.add(string0, blit, string1, arrayList, 770, 771);
    }

    public void addOverlay(String string0, String string1, float float0, int int0) {
        if (masked == null) {
            this.create();
        }

        this.addToCat(int0);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TextureCombinerShaderParam("intensity", float0));
        arrayList.add(new TextureCombinerShaderParam("bloodDark", 0.5F, 0.5F));
        this.addSeparate(string0, masked, string1, arrayList, 774, 771, 772, 771);
    }

    public void addDirtOverlay(String string0, String string1, float float0, int int0) {
        if (dirtMask == null) {
            this.create();
        }

        this.addToCat(int0);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TextureCombinerShaderParam("intensity", float0));
        this.addSeparate(string0, dirtMask, string1, arrayList, 774, 771, 772, 771);
    }

    public void addOverlay(String string) {
        if (tint == null) {
            this.create();
        }

        this.addSeparate(string, 774, 771, 772, 771);
    }

    public void addRect(String string, int int0, int int1, int int2, int int3) {
        this.commands.add(TextureCombinerCommand.get().init(getTextureWithFlags(string), int0, int1, int2, int3));
        this.dirty = true;
    }

    @Override
    public void destroy() {
        if (this.result != null) {
            TextureCombiner.instance.releaseTexture(this.result);
        }

        this.clear();
        this.dirty = false;
    }

    public void addTint(String string, int int0, float float0, float float1, float float2) {
        this.addTint(getTextureWithFlags(string), int0, float0, float1, float2);
    }

    public void addTint(Texture texture, int int0, float float0, float float1, float float2) {
        if (tint == null) {
            this.create();
        }

        this.addToCat(int0);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TextureCombinerShaderParam("R", float0));
        arrayList.add(new TextureCombinerShaderParam("G", float1));
        arrayList.add(new TextureCombinerShaderParam("B", float2));
        this.add(texture, tint, arrayList);
    }

    public void addHue(String string, int int0, float float0) {
        this.addHue(getTextureWithFlags(string), int0, float0);
    }

    public void addHue(Texture texture, int int0, float float0) {
        if (hue == null) {
            this.create();
        }

        this.addToCat(int0);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TextureCombinerShaderParam("HueChange", float0));
        this.add(texture, hue, arrayList);
    }

    public Texture addHole(BloodBodyPartType bloodBodyPartType) {
        String string = "media/textures/HoleTextures/" + CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()] + ".png";
        if (addHole == null) {
            this.create();
        }

        this.addToCat(CharacterSmartTexture.ClothingItemCategory);
        this.calculate();
        Texture texture = this.result;
        this.clear();
        this.result = null;
        this.commands.add(TextureCombinerCommand.get().initSeparate(texture, addHole, addHoleParams, getTextureWithFlags(string), 770, 0, 1, 771));
        this.dirty = true;
        return texture;
    }

    public void removeHole(String string1, BloodBodyPartType bloodBodyPartType) {
        String string0 = "media/textures/HoleTextures/" + CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()] + ".png";
        this.removeHole(getTextureWithFlags(string1), getTextureWithFlags(string0), bloodBodyPartType);
    }

    public void removeHole(Texture texture, BloodBodyPartType bloodBodyPartType) {
        String string = "media/textures/HoleTextures/" + CharacterSmartTexture.MaskFiles[bloodBodyPartType.index()] + ".png";
        this.removeHole(texture, getTextureWithFlags(string), bloodBodyPartType);
    }

    public void removeHole(Texture texture0, Texture texture1, BloodBodyPartType var3) {
        if (removeHole == null) {
            this.create();
        }

        this.addToCat(CharacterSmartTexture.ClothingItemCategory);
        this.commands.add(TextureCombinerCommand.get().init(texture0, removeHole, removeHoleParams, texture1, 770, 771));
        this.dirty = true;
    }

    public void mask(String string1, String string0, int int0) {
        this.mask(getTextureWithFlags(string1), getTextureWithFlags(string0), int0);
    }

    public void mask(Texture texture0, Texture texture1, int int0) {
        if (bodyMask == null) {
            this.create();
        }

        this.addToCat(int0);
        this.commands.add(TextureCombinerCommand.get().init(texture0, bodyMask, bodyMaskParams, texture1, 770, 771));
        this.dirty = true;
    }

    public void maskHue(String string1, String string0, int int0, float float0) {
        this.maskHue(getTextureWithFlags(string1), getTextureWithFlags(string0), int0, float0);
    }

    public void maskHue(Texture texture0, Texture texture1, int int0, float float0) {
        if (bodyMask == null) {
            this.create();
        }

        this.addToCat(int0);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TextureCombinerShaderParam("HueChange", float0));
        this.commands.add(TextureCombinerCommand.get().init(texture0, bodyMaskHue, arrayList, texture1, 770, 771));
        this.dirty = true;
    }

    public void maskTint(String string1, String string0, int int0, float float0, float float1, float float2) {
        this.maskTint(getTextureWithFlags(string1), getTextureWithFlags(string0), int0, float0, float1, float2);
    }

    public void maskTint(Texture texture0, Texture texture1, int int0, float float0, float float1, float float2) {
        if (bodyMask == null) {
            this.create();
        }

        this.addToCat(int0);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TextureCombinerShaderParam("R", float0));
        arrayList.add(new TextureCombinerShaderParam("G", float1));
        arrayList.add(new TextureCombinerShaderParam("B", float2));
        this.commands.add(TextureCombinerCommand.get().init(texture0, bodyMaskTint, arrayList, texture1, 770, 771));
        this.dirty = true;
    }

    public void addMaskedTexture(CharacterMask characterMask, String string0, String string1, int int0, ImmutableColor immutableColor, float float0) {
        addMaskedTexture(this, characterMask, string0, getTextureWithFlags(string1), int0, immutableColor, float0);
    }

    public void addMaskedTexture(CharacterMask characterMask, String string, Texture texture, int int0, ImmutableColor immutableColor, float float0) {
        addMaskedTexture(this, characterMask, string, texture, int0, immutableColor, float0);
    }

    private static void addMaskFlags(SmartTexture smartTexture, CharacterMask characterMask, String string, Texture texture, int int0) {
        Consumer consumer = Lambda.consumer(
            smartTexture,
            string,
            texture,
            int0,
            (part, smartTexturex, stringx, texturex, integer) -> smartTexturex.mask(texturex, getTextureWithFlags(stringx + "/" + part + ".png"), integer)
        );
        characterMask.forEachVisible(consumer);
    }

    private static void addMaskFlagsHue(SmartTexture smartTexture, CharacterMask characterMask, String string, Texture texture, int int0, float float0) {
        Consumer consumer = Lambda.consumer(
            smartTexture,
            string,
            texture,
            int0,
            float0,
            (part, smartTexturex, stringx, texturex, integer, float0x) -> smartTexturex.maskHue(
                texturex, getTextureWithFlags(stringx + "/" + part + ".png"), integer, float0x
            )
        );
        characterMask.forEachVisible(consumer);
    }

    private static void addMaskFlagsTint(
        SmartTexture smartTexture, CharacterMask characterMask, String string, Texture texture, int int0, ImmutableColor immutableColor
    ) {
        Consumer consumer = Lambda.consumer(
            smartTexture,
            string,
            texture,
            int0,
            immutableColor,
            (part, smartTexturex, stringx, texturex, integer, immutableColorx) -> smartTexturex.maskTint(
                texturex, getTextureWithFlags(stringx + "/" + part + ".png"), integer, immutableColorx.r, immutableColorx.g, immutableColorx.b
            )
        );
        characterMask.forEachVisible(consumer);
    }

    private static void addMaskedTexture(
        SmartTexture smartTexture, CharacterMask characterMask, String string, Texture texture, int int0, ImmutableColor immutableColor, float float0
    ) {
        if (!characterMask.isNothingVisible()) {
            if (characterMask.isAllVisible()) {
                if (!ImmutableColor.white.equals(immutableColor)) {
                    smartTexture.addTint(texture, int0, immutableColor.r, immutableColor.g, immutableColor.b);
                } else if (!(float0 < -1.0E-4F) && !(float0 > 1.0E-4F)) {
                    smartTexture.add(texture);
                } else {
                    smartTexture.addHue(texture, int0, float0);
                }
            } else {
                if (!ImmutableColor.white.equals(immutableColor)) {
                    addMaskFlagsTint(smartTexture, characterMask, string, texture, int0, immutableColor);
                } else if (!(float0 < -1.0E-4F) && !(float0 > 1.0E-4F)) {
                    addMaskFlags(smartTexture, characterMask, string, texture, int0);
                } else {
                    addMaskFlagsHue(smartTexture, characterMask, string, texture, int0, float0);
                }
            }
        }
    }

    public void addTexture(String string, int int0, ImmutableColor immutableColor, float float0) {
        addTexture(this, string, int0, immutableColor, float0);
    }

    private static void addTexture(SmartTexture smartTexture, String string, int int0, ImmutableColor immutableColor, float float0) {
        if (!ImmutableColor.white.equals(immutableColor)) {
            smartTexture.addTint(string, int0, immutableColor.r, immutableColor.g, immutableColor.b);
        } else if (!(float0 < -1.0E-4F) && !(float0 > 1.0E-4F)) {
            smartTexture.add(string);
        } else {
            smartTexture.addHue(string, int0, float0);
        }
    }

    private void create() {
        tint = new SmartShader("hueChange");
        hue = new SmartShader("hueChange");
        masked = new SmartShader("overlayMask");
        dirtMask = new SmartShader("dirtMask");
        bodyMask = new SmartShader("bodyMask");
        bodyMaskHue = new SmartShader("bodyMaskHue");
        bodyMaskTint = new SmartShader("bodyMaskTint");
        addHole = new SmartShader("addHole");
        removeHole = new SmartShader("removeHole");
        blit = new SmartShader("blit");
    }

    @Override
    public WrappedBuffer getData() {
        synchronized (this) {
            if (this.dirty) {
                this.calculate();
            }

            return this.result.dataid.getData();
        }
    }

    @Override
    public synchronized void bind() {
        if (this.dirty) {
            this.calculate();
        }

        this.result.bind(3553);
    }

    @Override
    public int getID() {
        synchronized (this) {
            if (this.dirty) {
                this.calculate();
            }
        }

        return this.result.dataid.id;
    }

    public void calculate() {
        synchronized (this) {
            if (Core.bDebug) {
                GL11.glGetError();
            }

            try {
                this.result = TextureCombiner.instance.combine(this.commands);
            } catch (Exception exception) {
                DebugLog.General.error(exception.getClass().getSimpleName() + " encountered while combining texture.");
                DebugLog.General.error("Intended width : " + TextureCombiner.getResultingWidth(this.commands));
                DebugLog.General.error("Intended height: " + TextureCombiner.getResultingHeight(this.commands));
                DebugLog.General.error("");
                DebugLog.General.error("Commands list: " + PZArrayUtil.arrayToString(this.commands));
                DebugLog.General.error("");
                DebugLog.General.error("Stack trace: ");
                ExceptionLogger.logException(exception);
                DebugLog.General.error("This SmartTexture will no longer be valid.");
                this.width = -1;
                this.height = -1;
                this.dirty = false;
                return;
            }

            this.width = this.result.width;
            this.height = this.result.height;
            this.dirty = false;
        }
    }

    public void clear() {
        TextureCombinerCommand.pool.release(this.commands);
        this.commands.clear();
        this.categoryMap.clear();
        this.dirty = false;
    }

    public void add(String string) {
        this.add(getTextureWithFlags(string));
    }

    public void add(Texture texture) {
        if (blit == null) {
            this.create();
        }

        this.commands.add(TextureCombinerCommand.get().init(texture, blit));
        this.dirty = true;
    }

    public void add(String string, SmartShader smartShader, ArrayList<TextureCombinerShaderParam> arrayList) {
        this.add(getTextureWithFlags(string), smartShader, arrayList);
    }

    public void add(Texture texture, SmartShader smartShader, ArrayList<TextureCombinerShaderParam> arrayList) {
        this.commands.add(TextureCombinerCommand.get().init(texture, smartShader, arrayList));
        this.dirty = true;
    }

    public void add(String string1, SmartShader smartShader, String string0, int int0, int int1) {
        this.add(getTextureWithFlags(string1), smartShader, getTextureWithFlags(string0), int0, int1);
    }

    public void add(Texture texture0, SmartShader smartShader, Texture texture1, int int0, int int1) {
        this.commands.add(TextureCombinerCommand.get().init(texture0, smartShader, texture1, int0, int1));
        this.dirty = true;
    }

    public void add(String string, int int0, int int1) {
        this.add(getTextureWithFlags(string), int0, int1);
    }

    public void add(Texture texture, int int0, int int1) {
        this.addSeparate(texture, int0, int1, 1, 771);
    }

    public void addSeparate(String string, int int0, int int1, int int2, int int3) {
        this.addSeparate(getTextureWithFlags(string), int0, int1, int2, int3);
    }

    public void addSeparate(Texture texture, int int0, int int1, int int2, int int3) {
        this.commands.add(TextureCombinerCommand.get().initSeparate(texture, int0, int1, int2, int3));
        this.dirty = true;
    }

    public void add(String string1, SmartShader smartShader, String string0, ArrayList<TextureCombinerShaderParam> arrayList, int int0, int int1) {
        this.add(getTextureWithFlags(string1), smartShader, getTextureWithFlags(string0), arrayList, int0, int1);
    }

    public void add(Texture texture0, SmartShader smartShader, Texture texture1, ArrayList<TextureCombinerShaderParam> arrayList, int int0, int int1) {
        this.addSeparate(texture0, smartShader, texture1, arrayList, int0, int1, 1, 771);
    }

    public void addSeparate(
        String string1, SmartShader smartShader, String string0, ArrayList<TextureCombinerShaderParam> arrayList, int int0, int int1, int int2, int int3
    ) {
        this.addSeparate(getTextureWithFlags(string1), smartShader, getTextureWithFlags(string0), arrayList, int0, int1, int2, int3);
    }

    public void addSeparate(
        Texture texture0, SmartShader smartShader, Texture texture1, ArrayList<TextureCombinerShaderParam> arrayList, int int0, int int1, int int2, int int3
    ) {
        this.commands.add(TextureCombinerCommand.get().initSeparate(texture0, smartShader, arrayList, texture1, int0, int1, int2, int3));
        this.dirty = true;
    }

    private static Texture getTextureWithFlags(String string) {
        return Texture.getSharedTexture(string, ModelManager.instance.getTextureFlags());
    }

    @Override
    public void saveOnRenderThread(String string) {
        if (this.dirty) {
            this.calculate();
        }

        this.result.saveOnRenderThread(string);
    }

    protected void setDirty() {
        this.dirty = true;
    }

    @Override
    public boolean isEmpty() {
        return this.result == null ? true : this.result.isEmpty();
    }

    @Override
    public boolean isFailure() {
        return this.result == null ? false : this.result.isFailure();
    }

    @Override
    public boolean isReady() {
        return this.result == null ? false : this.result.isReady();
    }
}
