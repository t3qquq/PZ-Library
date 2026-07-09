// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characterTextures;

import zombie.core.textures.SmartTexture;
import zombie.core.textures.TextureCombinerCommand;
import zombie.core.textures.TextureCombinerShaderParam;
import zombie.util.StringUtils;

public final class ItemSmartTexture extends SmartTexture {
    public static final int DecalOverlayCategory = 300;
    private String m_texName = null;

    public ItemSmartTexture(String tex) {
        if (tex != null) {
            this.add(tex);
            this.m_texName = tex;
        }
    }

    public ItemSmartTexture(String tex, float hue) {
        this.addHue("media/textures/" + tex + ".png", 300, hue);
        this.m_texName = tex;
    }

    public void setDenimPatches(BloodBodyPartType bodyPart) {
        if (!StringUtils.isNullOrEmpty(CharacterSmartTexture.DenimPatchesMaskFiles[bodyPart.index()])) {
            String string = "media/textures/patches/" + CharacterSmartTexture.DenimPatchesMaskFiles[bodyPart.index()] + ".png";
            int int0 = CharacterSmartTexture.DecalOverlayCategory + bodyPart.index();
            this.addOverlayPatches(string, "media/textures/patches/patchesmask.png", int0);
        }
    }

    public void setLeatherPatches(BloodBodyPartType bodyPart) {
        if (!StringUtils.isNullOrEmpty(CharacterSmartTexture.LeatherPatchesMaskFiles[bodyPart.index()])) {
            String string = "media/textures/patches/" + CharacterSmartTexture.LeatherPatchesMaskFiles[bodyPart.index()] + ".png";
            int int0 = CharacterSmartTexture.DecalOverlayCategory + bodyPart.index();
            this.addOverlayPatches(string, "media/textures/patches/patchesmask.png", int0);
        }
    }

    public void setBasicPatches(BloodBodyPartType bodyPart) {
        if (!StringUtils.isNullOrEmpty(CharacterSmartTexture.BasicPatchesMaskFiles[bodyPart.index()])) {
            String string = "media/textures/patches/" + CharacterSmartTexture.BasicPatchesMaskFiles[bodyPart.index()] + ".png";
            int int0 = CharacterSmartTexture.DecalOverlayCategory + bodyPart.index();
            this.addOverlayPatches(string, "media/textures/patches/patchesmask.png", int0);
        }
    }

    public void setBlood(String tex, BloodBodyPartType bodyPart, float intensity) {
        String string = "media/textures/BloodTextures/" + CharacterSmartTexture.MaskFiles[bodyPart.index()] + ".png";
        int int0 = CharacterSmartTexture.DecalOverlayCategory + bodyPart.index();
        this.setBlood(tex, string, intensity, int0);
    }

    public void setBlood(String tex, String mask, float intensity, int category) {
        intensity = Math.max(0.0F, Math.min(1.0F, intensity));
        TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(category);
        if (textureCombinerCommand != null) {
            for (int int0 = 0; int0 < textureCombinerCommand.shaderParams.size(); int0++) {
                TextureCombinerShaderParam textureCombinerShaderParam = textureCombinerCommand.shaderParams.get(int0);
                if (textureCombinerShaderParam.name.equals("intensity")
                    && (textureCombinerShaderParam.min != intensity || textureCombinerShaderParam.max != intensity)) {
                    textureCombinerShaderParam.min = textureCombinerShaderParam.max = intensity;
                    this.setDirty();
                }
            }
        } else if (intensity > 0.0F) {
            this.addOverlay(tex, mask, intensity, category);
        }
    }

    public float addBlood(String tex, BloodBodyPartType bodyPart, float intensity) {
        String string = "media/textures/BloodTextures/" + CharacterSmartTexture.MaskFiles[bodyPart.index()] + ".png";
        int int0 = CharacterSmartTexture.DecalOverlayCategory + bodyPart.index();
        return this.addBlood(tex, string, intensity, int0);
    }

    public float addDirt(String tex, BloodBodyPartType bodyPart, float intensity) {
        String string = "media/textures/BloodTextures/" + CharacterSmartTexture.MaskFiles[bodyPart.index()] + ".png";
        int int0 = CharacterSmartTexture.DirtOverlayCategory + bodyPart.index();
        return this.addDirt(tex, string, intensity, int0);
    }

    public float addBlood(String tex, String mask, float intensity, int category) {
        TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(category);
        if (textureCombinerCommand == null) {
            this.addOverlay(tex, mask, intensity, category);
            return intensity;
        } else {
            for (int int0 = 0; int0 < textureCombinerCommand.shaderParams.size(); int0++) {
                TextureCombinerShaderParam textureCombinerShaderParam = textureCombinerCommand.shaderParams.get(int0);
                if (textureCombinerShaderParam.name.equals("intensity")) {
                    float float0 = textureCombinerShaderParam.min;
                    float0 += intensity;
                    float0 = Math.min(1.0F, float0);
                    if (textureCombinerShaderParam.min != float0 || textureCombinerShaderParam.max != float0) {
                        textureCombinerShaderParam.min = textureCombinerShaderParam.max = float0;
                        this.setDirty();
                    }

                    return float0;
                }
            }

            this.addOverlay(tex, mask, intensity, category);
            return intensity;
        }
    }

    public float addDirt(String tex, String mask, float intensity, int category) {
        TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(category);
        if (textureCombinerCommand == null) {
            this.addDirtOverlay(tex, mask, intensity, category);
            return intensity;
        } else {
            for (int int0 = 0; int0 < textureCombinerCommand.shaderParams.size(); int0++) {
                TextureCombinerShaderParam textureCombinerShaderParam = textureCombinerCommand.shaderParams.get(int0);
                if (textureCombinerShaderParam.name.equals("intensity")) {
                    float float0 = textureCombinerShaderParam.min;
                    float0 += intensity;
                    float0 = Math.min(1.0F, float0);
                    if (textureCombinerShaderParam.min != float0 || textureCombinerShaderParam.max != float0) {
                        textureCombinerShaderParam.min = textureCombinerShaderParam.max = float0;
                        this.setDirty();
                    }

                    return float0;
                }
            }

            this.addOverlay(tex, mask, intensity, category);
            return intensity;
        }
    }

    public void removeBlood() {
        for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
            this.removeBlood(BloodBodyPartType.FromIndex(int0));
        }
    }

    public void removeDirt() {
        for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
            this.removeDirt(BloodBodyPartType.FromIndex(int0));
        }
    }

    public void removeBlood(BloodBodyPartType bodyPart) {
        TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(CharacterSmartTexture.DecalOverlayCategory + bodyPart.index());
        if (textureCombinerCommand != null) {
            for (int int0 = 0; int0 < textureCombinerCommand.shaderParams.size(); int0++) {
                TextureCombinerShaderParam textureCombinerShaderParam = textureCombinerCommand.shaderParams.get(int0);
                if (textureCombinerShaderParam.name.equals("intensity") && (textureCombinerShaderParam.min != 0.0F || textureCombinerShaderParam.max != 0.0F)) {
                    textureCombinerShaderParam.min = textureCombinerShaderParam.max = 0.0F;
                    this.setDirty();
                }
            }
        }
    }

    public void removeDirt(BloodBodyPartType bodyPart) {
        TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(CharacterSmartTexture.DirtOverlayCategory + bodyPart.index());
        if (textureCombinerCommand != null) {
            for (int int0 = 0; int0 < textureCombinerCommand.shaderParams.size(); int0++) {
                TextureCombinerShaderParam textureCombinerShaderParam = textureCombinerCommand.shaderParams.get(int0);
                if (textureCombinerShaderParam.name.equals("intensity") && (textureCombinerShaderParam.min != 0.0F || textureCombinerShaderParam.max != 0.0F)) {
                    textureCombinerShaderParam.min = textureCombinerShaderParam.max = 0.0F;
                    this.setDirty();
                }
            }
        }
    }

    public String getTexName() {
        return this.m_texName;
    }
}
