// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.utils.ImageUtils;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.PlayerCamera;
import zombie.iso.sprite.IsoCursor;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.util.Type;

public final class MultiTextureFBO2 {
    private final float[] zoomLevelsDefault = new float[]{2.5F, 2.25F, 2.0F, 1.75F, 1.5F, 1.25F, 1.0F, 0.75F, 0.5F};
    private float[] zoomLevels;
    public TextureFBO Current;
    public volatile TextureFBO FBOrendered = null;
    public final float[] zoom = new float[4];
    public final float[] targetZoom = new float[4];
    public final float[] startZoom = new float[4];
    private float zoomedInLevel;
    private float zoomedOutLevel;
    public final boolean[] bAutoZoom = new boolean[4];
    public boolean bZoomEnabled = true;

    public MultiTextureFBO2() {
        for (int int0 = 0; int0 < 4; int0++) {
            this.zoom[int0] = this.targetZoom[int0] = this.startZoom[int0] = 1.0F;
        }
    }

    public int getWidth(int playerIndex) {
        return (int)(IsoCamera.getScreenWidth(playerIndex) * this.zoom[playerIndex] * (Core.TileScale / 2.0F));
    }

    public int getHeight(int playerIndex) {
        return (int)(IsoCamera.getScreenHeight(playerIndex) * this.zoom[playerIndex] * (Core.TileScale / 2.0F));
    }

    public void setTargetZoom(int playerIndex, float target) {
        if (this.targetZoom[playerIndex] != target) {
            this.targetZoom[playerIndex] = target;
            this.startZoom[playerIndex] = this.zoom[playerIndex];
        }
    }

    public ArrayList<Integer> getDefaultZoomLevels() {
        ArrayList arrayList = new ArrayList();
        float[] floats = this.zoomLevelsDefault;

        for (int int0 = 0; int0 < floats.length; int0++) {
            arrayList.add(Math.round(floats[int0] * 100.0F));
        }

        return arrayList;
    }

    public void setZoomLevelsFromOption(String levels) {
        this.zoomLevels = this.zoomLevelsDefault;
        if (levels != null && !levels.isEmpty()) {
            String[] strings = levels.split(";");
            if (strings.length != 0) {
                ArrayList arrayList = new ArrayList();

                for (String string : strings) {
                    if (!string.isEmpty()) {
                        try {
                            int int0 = Integer.parseInt(string);

                            for (float float0 : this.zoomLevels) {
                                if (Math.round(float0 * 100.0F) == int0) {
                                    if (!arrayList.contains(int0)) {
                                        arrayList.add(int0);
                                    }
                                    break;
                                }
                            }
                        } catch (NumberFormatException numberFormatException) {
                        }
                    }
                }

                if (!arrayList.contains(100)) {
                    arrayList.add(100);
                }

                arrayList.sort((integer0, integer1) -> integer1 - integer0);
                this.zoomLevels = new float[arrayList.size()];

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    int int2 = IsoPlayer.getPlayerIndex();
                    if (Core.getInstance().getOffscreenHeight(int2) > 1440) {
                        this.zoomLevels[int1] = ((Integer)arrayList.get(int1)).intValue() / 100.0F - 0.25F;
                    } else {
                        this.zoomLevels[int1] = ((Integer)arrayList.get(int1)).intValue() / 100.0F;
                    }
                }
            }
        }
    }

    public void destroy() {
        if (this.Current != null) {
            this.Current.destroy();
            this.Current = null;
            this.FBOrendered = null;

            for (int int0 = 0; int0 < 4; int0++) {
                this.zoom[int0] = this.targetZoom[int0] = 1.0F;
            }
        }
    }

    public void create(int xres, int yres) throws Exception {
        if (this.bZoomEnabled) {
            if (this.zoomLevels == null) {
                this.zoomLevels = this.zoomLevelsDefault;
            }

            this.zoomedInLevel = this.zoomLevels[this.zoomLevels.length - 1];
            this.zoomedOutLevel = this.zoomLevels[0];
            int int0 = ImageUtils.getNextPowerOfTwoHW(xres);
            int int1 = ImageUtils.getNextPowerOfTwoHW(yres);
            this.Current = this.createTexture(int0, int1, false);
        }
    }

    public void update() {
        int int0 = IsoPlayer.getPlayerIndex();
        if (!this.bZoomEnabled) {
            this.zoom[int0] = this.targetZoom[int0] = 1.0F;
        }

        if (this.bAutoZoom[int0] && IsoCamera.CamCharacter != null && this.bZoomEnabled) {
            float float0 = IsoUtils.DistanceTo(IsoCamera.getRightClickOffX(), IsoCamera.getRightClickOffY(), 0.0F, 0.0F);
            float float1 = float0 / 300.0F;
            if (float1 > 1.0F) {
                float1 = 1.0F;
            }

            float float2 = this.shouldAutoZoomIn() ? this.zoomedInLevel : this.zoomedOutLevel;
            float2 += float1;
            if (float2 > this.zoomLevels[0]) {
                float2 = this.zoomLevels[0];
            }

            if (IsoCamera.CamCharacter.getVehicle() != null) {
                float2 = this.getMaxZoom();
            }

            this.setTargetZoom(int0, float2);
        }

        float float3 = 0.004F * GameTime.instance.getMultiplier() / GameTime.instance.getTrueMultiplier() * (Core.TileScale == 2 ? 1.5F : 1.5F);
        if (!this.bAutoZoom[int0]) {
            float3 *= 5.0F;
        } else if (this.targetZoom[int0] > this.zoom[int0]) {
            float3 *= 1.0F;
        }

        if (this.targetZoom[int0] > this.zoom[int0]) {
            this.zoom[int0] = this.zoom[int0] + float3;
            IsoPlayer.players[int0].dirtyRecalcGridStackTime = 2.0F;
            if (this.zoom[int0] > this.targetZoom[int0] || Math.abs(this.zoom[int0] - this.targetZoom[int0]) < 0.001F) {
                this.zoom[int0] = this.targetZoom[int0];
            }
        }

        if (this.targetZoom[int0] < this.zoom[int0]) {
            this.zoom[int0] = this.zoom[int0] - float3;
            IsoPlayer.players[int0].dirtyRecalcGridStackTime = 2.0F;
            if (this.zoom[int0] < this.targetZoom[int0] || Math.abs(this.zoom[int0] - this.targetZoom[int0]) < 0.001F) {
                this.zoom[int0] = this.targetZoom[int0];
            }
        }

        this.setCameraToCentre();
    }

    private boolean shouldAutoZoomIn() {
        if (IsoCamera.CamCharacter == null) {
            return false;
        } else {
            IsoGridSquare square = IsoCamera.CamCharacter.getCurrentSquare();
            if (square != null && !square.isOutside()) {
                return true;
            } else {
                IsoPlayer player = Type.tryCastTo(IsoCamera.CamCharacter, IsoPlayer.class);
                if (player == null) {
                    return false;
                } else if (player.isRunning() || player.isSprinting()) {
                    return false;
                } else {
                    return player.closestZombie < 6.0F && player.isTargetedByZombie() ? true : player.lastTargeted < PerformanceSettings.getLockFPS() * 4;
                }
            }
        }
    }

    private void setCameraToCentre() {
        PlayerCamera playerCamera = IsoCamera.cameras[IsoPlayer.getPlayerIndex()];
        playerCamera.center();
    }

    private TextureFBO createTexture(int int0, int int1, boolean boolean0) {
        if (boolean0) {
            Texture texture0 = new Texture(int0, int1, 16);
            TextureFBO textureFBO = new TextureFBO(texture0);
            textureFBO.destroy();
            return null;
        } else {
            Texture texture1 = new Texture(int0, int1, 19);
            return new TextureFBO(texture1);
        }
    }

    public void render() {
        if (this.Current != null) {
            int int0 = 0;

            for (int int1 = 3; int1 >= 0; int1--) {
                if (IsoPlayer.players[int1] != null) {
                    int0 = int1 > 1 ? 3 : int1;
                    break;
                }
            }

            int0 = Math.max(int0, IsoPlayer.numPlayers - 1);

            for (int int2 = 0; int2 <= int0; int2++) {
                if (Core.getInstance().RenderShader != null) {
                    IndieGL.StartShader(Core.getInstance().RenderShader, int2);
                }

                int int3 = IsoCamera.getScreenLeft(int2);
                int int4 = IsoCamera.getScreenTop(int2);
                int int5 = IsoCamera.getScreenWidth(int2);
                int int6 = IsoCamera.getScreenHeight(int2);
                if (IsoPlayer.players[int2] != null || GameServer.bServer && ServerGUI.isCreated()) {
                    ((Texture)this.Current.getTexture()).rendershader2(int3, int4, int5, int6, int3, int4, int5, int6, 1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    SpriteRenderer.instance.renderi(null, int3, int4, int5, int6, 0.0F, 0.0F, 0.0F, 1.0F, null);
                }
            }

            if (Core.getInstance().RenderShader != null) {
                IndieGL.EndShader();
            }

            IsoCursor.getInstance().render(0);
        }
    }

    public TextureFBO getCurrent(int nPlayer) {
        return this.Current;
    }

    public Texture getTexture(int nPlayer) {
        return (Texture)this.Current.getTexture();
    }

    public void doZoomScroll(int playerIndex, int del) {
        this.targetZoom[playerIndex] = this.getNextZoom(playerIndex, del);
    }

    public float getNextZoom(int playerIndex, int del) {
        if (this.bZoomEnabled && this.zoomLevels != null) {
            if (del > 0) {
                for (int int0 = this.zoomLevels.length - 1; int0 > 0; int0--) {
                    if (this.targetZoom[playerIndex] == this.zoomLevels[int0]) {
                        return this.zoomLevels[int0 - 1];
                    }
                }
            } else if (del < 0) {
                for (int int1 = 0; int1 < this.zoomLevels.length - 1; int1++) {
                    if (this.targetZoom[playerIndex] == this.zoomLevels[int1]) {
                        return this.zoomLevels[int1 + 1];
                    }
                }
            }

            return this.targetZoom[playerIndex];
        } else {
            return 1.0F;
        }
    }

    public float getMinZoom() {
        return this.bZoomEnabled && this.zoomLevels != null && this.zoomLevels.length != 0 ? this.zoomLevels[this.zoomLevels.length - 1] : 1.0F;
    }

    public float getMaxZoom() {
        return this.bZoomEnabled && this.zoomLevels != null && this.zoomLevels.length != 0 ? this.zoomLevels[0] : 1.0F;
    }

    public boolean test() {
        try {
            this.createTexture(16, 16, true);
            return true;
        } catch (Exception exception) {
            DebugLog.General.error("Failed to create Test FBO");
            exception.printStackTrace();
            Core.SafeMode = true;
            return false;
        }
    }
}
