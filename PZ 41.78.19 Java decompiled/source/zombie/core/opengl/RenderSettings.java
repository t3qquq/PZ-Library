// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.opengl;

import zombie.GameTime;
import zombie.IndieGL;
import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SearchMode;
import zombie.iso.weather.ClimateColorInfo;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.ClimateMoon;
import zombie.iso.weather.WorldFlares;
import zombie.network.GameServer;

public final class RenderSettings {
    private static RenderSettings instance;
    private static Texture texture;
    private static final float AMBIENT_MIN_SHADER = 0.4F;
    private static final float AMBIENT_MAX_SHADER = 1.0F;
    private static final float AMBIENT_MIN_LEGACY = 0.4F;
    private static final float AMBIENT_MAX_LEGACY = 1.0F;
    private final RenderSettings.PlayerRenderSettings[] playerSettings = new RenderSettings.PlayerRenderSettings[4];
    private Color defaultClear = new Color(0, 0, 0, 1);

    public static RenderSettings getInstance() {
        if (instance == null) {
            instance = new RenderSettings();
        }

        return instance;
    }

    public RenderSettings() {
        for (int int0 = 0; int0 < this.playerSettings.length; int0++) {
            this.playerSettings[int0] = new RenderSettings.PlayerRenderSettings();
        }

        texture = Texture.getSharedTexture("media/textures/weather/fogwhite.png");
        if (texture == null) {
            DebugLog.log("Missing texture: media/textures/weather/fogwhite.png");
        }
    }

    public RenderSettings.PlayerRenderSettings getPlayerSettings(int int0) {
        return this.playerSettings[int0];
    }

    public void update() {
        if (!GameServer.bServer) {
            for (int int0 = 0; int0 < 4; int0++) {
                if (IsoPlayer.players[int0] != null) {
                    this.playerSettings[int0].updateRenderSettings(int0, IsoPlayer.players[int0]);
                }
            }
        }
    }

    public void applyRenderSettings(int int0) {
        if (!GameServer.bServer) {
            this.getPlayerSettings(int0).applyRenderSettings(int0);
        }
    }

    public void legacyPostRender(int int0) {
        if (!GameServer.bServer) {
            if (Core.getInstance().RenderShader == null || Core.getInstance().getOffscreenBuffer() == null) {
                this.getPlayerSettings(int0).legacyPostRender(int0);
            }
        }
    }

    public float getAmbientForPlayer(int int0) {
        RenderSettings.PlayerRenderSettings playerRenderSettings = this.getPlayerSettings(int0);
        return playerRenderSettings != null ? playerRenderSettings.getAmbient() : 0.0F;
    }

    public Color getMaskClearColorForPlayer(int int0) {
        RenderSettings.PlayerRenderSettings playerRenderSettings = this.getPlayerSettings(int0);
        return playerRenderSettings != null ? playerRenderSettings.getMaskClearColor() : this.defaultClear;
    }

    public static class PlayerRenderSettings {
        public ClimateColorInfo CM_GlobalLight = new ClimateColorInfo();
        public float CM_NightStrength = 0.0F;
        public float CM_Desaturation = 0.0F;
        public float CM_GlobalLightIntensity = 0.0F;
        public float CM_Ambient = 0.0F;
        public float CM_ViewDistance = 0.0F;
        public float CM_DayLightStrength = 0.0F;
        public float CM_FogIntensity = 0.0F;
        private Color blendColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
        private ColorInfo blendInfo = new ColorInfo();
        private float blendIntensity = 0.0F;
        private float desaturation = 0.0F;
        private float darkness = 0.0F;
        private float night = 0.0F;
        private float viewDistance = 0.0F;
        private float ambient = 0.0F;
        private boolean applyNightVisionGoggles = false;
        private float goggleMod = 0.0F;
        private boolean isExterior = false;
        private float fogMod = 1.0F;
        private float rmod;
        private float gmod;
        private float bmod;
        private float SM_Radius = 0.0F;
        private float SM_Alpha = 0.0F;
        private Color maskClearColor = new Color(0, 0, 0, 1);

        private void updateRenderSettings(int int0, IsoPlayer player) {
            SearchMode searchMode = SearchMode.getInstance();
            this.SM_Alpha = 0.0F;
            this.SM_Radius = 0.0F;
            ClimateManager climateManager = ClimateManager.getInstance();
            this.CM_GlobalLight = climateManager.getGlobalLight();
            this.CM_GlobalLightIntensity = climateManager.getGlobalLightIntensity();
            this.CM_Ambient = climateManager.getAmbient();
            this.CM_DayLightStrength = climateManager.getDayLightStrength();
            this.CM_NightStrength = climateManager.getNightStrength();
            this.CM_Desaturation = climateManager.getDesaturation();
            this.CM_ViewDistance = climateManager.getViewDistance();
            this.CM_FogIntensity = climateManager.getFogIntensity();
            climateManager.getThunderStorm().applyLightningForPlayer(this, int0, player);
            WorldFlares.applyFlaresForPlayer(this, int0, player);
            int int1 = SandboxOptions.instance.NightDarkness.getValue();
            this.desaturation = this.CM_Desaturation;
            this.viewDistance = this.CM_ViewDistance;
            this.applyNightVisionGoggles = player != null && player.isWearingNightVisionGoggles();
            this.isExterior = player != null && (player.isDead() || player.getCurrentSquare() != null && !player.getCurrentSquare().isInARoom());
            this.fogMod = 1.0F - this.CM_FogIntensity * 0.5F;
            this.night = this.CM_NightStrength;
            this.darkness = 1.0F - this.CM_DayLightStrength;
            if (this.isExterior) {
                this.setBlendColor(this.CM_GlobalLight.getExterior());
                this.blendIntensity = this.CM_GlobalLight.getExterior().a;
            } else {
                this.setBlendColor(this.CM_GlobalLight.getInterior());
                this.blendIntensity = this.CM_GlobalLight.getInterior().a;
            }

            this.ambient = this.CM_Ambient;
            this.viewDistance = this.CM_ViewDistance;
            float float0 = 0.2F + 0.1F * --int1;
            float0 += 0.075F * ClimateMoon.getMoonFloat() * this.night;
            if (!this.isExterior) {
                float0 *= 0.925F - 0.075F * this.darkness;
                this.desaturation *= 0.25F;
            }

            if (this.ambient < 0.2F && player.getCharacterTraits().NightVision.isSet()) {
                this.ambient = 0.2F;
            }

            this.ambient = float0 + (1.0F - float0) * this.ambient;
            if (Core.bLastStand) {
                this.ambient = 0.65F;
                this.darkness = 0.25F;
                this.night = 0.25F;
            }

            if (Core.getInstance().RenderShader == null || Core.getInstance().getOffscreenBuffer() == null) {
                this.desaturation = this.desaturation * (1.0F - this.darkness);
                this.blendInfo.r = this.blendColor.r;
                this.blendInfo.g = this.blendColor.g;
                this.blendInfo.b = this.blendColor.b;
                this.blendInfo.desaturate(this.desaturation);
                this.rmod = GameTime.getInstance().Lerp(1.0F, this.blendInfo.r, this.blendIntensity);
                this.gmod = GameTime.getInstance().Lerp(1.0F, this.blendInfo.g, this.blendIntensity);
                this.bmod = GameTime.getInstance().Lerp(1.0F, this.blendInfo.b, this.blendIntensity);
                if (this.applyNightVisionGoggles) {
                    this.goggleMod = 1.0F - 0.9F * this.darkness;
                    this.blendIntensity = 0.0F;
                    this.night = 0.0F;
                    this.ambient = 0.8F;
                    this.rmod = 1.0F;
                    this.gmod = 1.0F;
                    this.bmod = 1.0F;
                }
            } else if (this.applyNightVisionGoggles) {
                this.ambient = 1.0F;
                this.rmod = GameTime.getInstance().Lerp(1.0F, 0.7F, this.darkness);
                this.gmod = GameTime.getInstance().Lerp(1.0F, 0.7F, this.darkness);
                this.bmod = GameTime.getInstance().Lerp(1.0F, 0.7F, this.darkness);
                this.maskClearColor.r = 0.0F;
                this.maskClearColor.g = 0.0F;
                this.maskClearColor.b = 0.0F;
                this.maskClearColor.a = 0.0F;
            } else {
                this.rmod = 1.0F;
                this.gmod = 1.0F;
                this.bmod = 1.0F;
                if (!this.isExterior) {
                    this.maskClearColor.r = this.CM_GlobalLight.getInterior().r;
                    this.maskClearColor.g = this.CM_GlobalLight.getInterior().g;
                    this.maskClearColor.b = this.CM_GlobalLight.getInterior().b;
                    this.maskClearColor.a = this.CM_GlobalLight.getInterior().a;
                } else {
                    this.maskClearColor.r = 0.0F;
                    this.maskClearColor.g = 0.0F;
                    this.maskClearColor.b = 0.0F;
                    this.maskClearColor.a = 0.0F;
                }
            }
        }

        private void applyRenderSettings(int var1) {
            IsoGridSquare.rmod = this.rmod;
            IsoGridSquare.gmod = this.gmod;
            IsoGridSquare.bmod = this.bmod;
            IsoObject.rmod = this.rmod;
            IsoObject.gmod = this.gmod;
            IsoObject.bmod = this.bmod;
        }

        private void legacyPostRender(int int0) {
            SpriteRenderer.instance.glIgnoreStyles(true);
            if (this.applyNightVisionGoggles) {
                IndieGL.glBlendFunc(770, 768);
                SpriteRenderer.instance
                    .render(
                        RenderSettings.texture,
                        0.0F,
                        0.0F,
                        Core.getInstance().getOffscreenWidth(int0),
                        Core.getInstance().getOffscreenHeight(int0),
                        0.05F,
                        0.95F,
                        0.05F,
                        this.goggleMod,
                        null
                    );
                IndieGL.glBlendFunc(770, 771);
            } else {
                IndieGL.glBlendFunc(774, 774);
                SpriteRenderer.instance
                    .render(
                        RenderSettings.texture,
                        0.0F,
                        0.0F,
                        Core.getInstance().getOffscreenWidth(int0),
                        Core.getInstance().getOffscreenHeight(int0),
                        this.blendInfo.r,
                        this.blendInfo.g,
                        this.blendInfo.b,
                        1.0F,
                        null
                    );
                IndieGL.glBlendFunc(770, 771);
            }

            SpriteRenderer.instance.glIgnoreStyles(false);
        }

        public Color getBlendColor() {
            return this.blendColor;
        }

        public float getBlendIntensity() {
            return this.blendIntensity;
        }

        public float getDesaturation() {
            return this.desaturation;
        }

        public float getDarkness() {
            return this.darkness;
        }

        public float getNight() {
            return this.night;
        }

        public float getViewDistance() {
            return this.viewDistance;
        }

        public float getAmbient() {
            return this.ambient;
        }

        public boolean isApplyNightVisionGoggles() {
            return this.applyNightVisionGoggles;
        }

        public float getRmod() {
            return this.rmod;
        }

        public float getGmod() {
            return this.gmod;
        }

        public float getBmod() {
            return this.bmod;
        }

        public boolean isExterior() {
            return this.isExterior;
        }

        public float getFogMod() {
            return this.fogMod;
        }

        private void setBlendColor(Color color) {
            this.blendColor.a = color.a;
            this.blendColor.r = color.r;
            this.blendColor.g = color.g;
            this.blendColor.b = color.b;
        }

        public Color getMaskClearColor() {
            return this.maskClearColor;
        }

        public float getSM_Radius() {
            return this.SM_Radius;
        }

        public float getSM_Alpha() {
            return this.SM_Alpha;
        }
    }
}
