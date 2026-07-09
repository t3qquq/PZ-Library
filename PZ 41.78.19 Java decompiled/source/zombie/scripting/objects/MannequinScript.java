// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public final class MannequinScript extends BaseScriptObject {
    private String name;
    private boolean bFemale = true;
    private String modelScriptName;
    private String texture;
    private String animSet;
    private String animState;
    private String pose;
    private String outfit;

    public String getName() {
        return this.name;
    }

    public boolean isFemale() {
        return this.bFemale;
    }

    public void setFemale(boolean b) {
        this.bFemale = b;
    }

    public String getModelScriptName() {
        return this.modelScriptName;
    }

    public void setModelScriptName(String str) {
        this.modelScriptName = StringUtils.discardNullOrWhitespace(str);
    }

    public String getTexture() {
        return this.texture;
    }

    public void setTexture(String str) {
        this.texture = StringUtils.discardNullOrWhitespace(str);
    }

    public String getAnimSet() {
        return this.animSet;
    }

    public void setAnimSet(String str) {
        this.animSet = StringUtils.discardNullOrWhitespace(str);
    }

    public String getAnimState() {
        return this.animState;
    }

    public void setAnimState(String str) {
        this.animState = StringUtils.discardNullOrWhitespace(str);
    }

    public String getPose() {
        return this.pose;
    }

    public void setPose(String str) {
        this.pose = StringUtils.discardNullOrWhitespace(str);
    }

    public String getOutfit() {
        return this.outfit;
    }

    public void setOutfit(String str) {
        this.outfit = StringUtils.discardNullOrWhitespace(str);
    }

    public void Load(String _name, String totalFile) {
        this.name = _name;
        ScriptParser.Block block = ScriptParser.parse(totalFile);
        block = block.children.get(0);

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("female".equalsIgnoreCase(string0)) {
                this.bFemale = StringUtils.tryParseBoolean(string1);
            } else if ("model".equalsIgnoreCase(string0)) {
                this.modelScriptName = StringUtils.discardNullOrWhitespace(string1);
            } else if ("texture".equalsIgnoreCase(string0)) {
                this.texture = StringUtils.discardNullOrWhitespace(string1);
            } else if ("animSet".equalsIgnoreCase(string0)) {
                this.animSet = StringUtils.discardNullOrWhitespace(string1);
            } else if ("animState".equalsIgnoreCase(string0)) {
                this.animState = StringUtils.discardNullOrWhitespace(string1);
            } else if ("pose".equalsIgnoreCase(string0)) {
                this.pose = StringUtils.discardNullOrWhitespace(string1);
            } else if ("outfit".equalsIgnoreCase(string0)) {
                this.outfit = StringUtils.discardNullOrWhitespace(string1);
            }
        }
    }

    public void reset() {
        this.modelScriptName = null;
        this.texture = null;
        this.animSet = null;
        this.animState = null;
        this.pose = null;
        this.outfit = null;
    }
}
