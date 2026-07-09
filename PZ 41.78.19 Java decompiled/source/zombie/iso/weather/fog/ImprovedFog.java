// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather.fog;

import org.joml.Vector2i;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.input.GameKeyboard;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.PlayerCamera;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.fx.SteppedUpdateFloat;

/**
 * TurboTuTone.
 */
public class ImprovedFog {
    private static final ImprovedFog.RectangleIterator rectangleIter = new ImprovedFog.RectangleIterator();
    private static final Vector2i rectangleMatrixPos = new Vector2i();
    private static IsoChunkMap chunkMap;
    private static int minY;
    private static int maxY;
    private static int minX;
    private static int maxX;
    private static int zLayer;
    private static Vector2i lastIterPos = new Vector2i();
    private static ImprovedFog.FogRectangle fogRectangle = new ImprovedFog.FogRectangle();
    private static boolean drawingThisLayer = false;
    private static float ZOOM = 1.0F;
    private static int PlayerIndex;
    private static int playerRow;
    private static float screenWidth;
    private static float screenHeight;
    private static float worldOffsetX;
    private static float worldOffsetY;
    private static float topAlphaHeight = 0.38F;
    private static float bottomAlphaHeight = 0.24F;
    private static float secondLayerAlpha = 0.5F;
    private static float scalingX = 1.0F;
    private static float scalingY = 1.0F;
    private static float colorR = 1.0F;
    private static float colorG = 1.0F;
    private static float colorB = 1.0F;
    private static boolean drawDebugColors = false;
    private static float octaves = 6.0F;
    private static boolean highQuality = true;
    private static boolean enableEditing = false;
    private static float alphaCircleAlpha = 0.3F;
    private static float alphaCircleRad = 2.25F;
    private static int lastRow = -1;
    private static ClimateManager climateManager;
    private static Texture noiseTexture;
    private static boolean renderOnlyOneRow = false;
    private static float baseAlpha = 0.0F;
    private static int renderEveryXRow = 1;
    private static int renderXRowsFromCenter = 0;
    private static boolean renderCurrentLayerOnly = false;
    private static float rightClickOffX = 0.0F;
    private static float rightClickOffY = 0.0F;
    private static float cameraOffscreenLeft = 0.0F;
    private static float cameraOffscreenTop = 0.0F;
    private static float cameraZoom = 0.0F;
    private static int minXOffset = -2;
    private static int maxXOffset = 12;
    private static int maxYOffset = -5;
    private static boolean renderEndOnly = false;
    private static final SteppedUpdateFloat fogIntensity = new SteppedUpdateFloat(0.0F, 0.005F, 0.0F, 1.0F);
    private static int keyPause = 0;
    private static final float[] offsets = new float[]{
        0.3F,
        0.8F,
        0.0F,
        0.6F,
        0.3F,
        0.1F,
        0.5F,
        0.9F,
        0.2F,
        0.0F,
        0.7F,
        0.1F,
        0.4F,
        0.2F,
        0.5F,
        0.3F,
        0.8F,
        0.4F,
        0.9F,
        0.5F,
        0.8F,
        0.4F,
        0.7F,
        0.2F,
        0.0F,
        0.6F,
        0.1F,
        0.6F,
        0.9F,
        0.7F
    };

    public static int getMinXOffset() {
        return minXOffset;
    }

    public static void setMinXOffset(int _minXOffset) {
        minXOffset = _minXOffset;
    }

    public static int getMaxXOffset() {
        return maxXOffset;
    }

    public static void setMaxXOffset(int _maxXOffset) {
        maxXOffset = _maxXOffset;
    }

    public static int getMaxYOffset() {
        return maxYOffset;
    }

    public static void setMaxYOffset(int _maxYOffset) {
        maxYOffset = _maxYOffset;
    }

    public static boolean isRenderEndOnly() {
        return renderEndOnly;
    }

    public static void setRenderEndOnly(boolean _renderEndOnly) {
        renderEndOnly = _renderEndOnly;
    }

    public static float getAlphaCircleAlpha() {
        return alphaCircleAlpha;
    }

    public static void setAlphaCircleAlpha(float _alphaCircleAlpha) {
        alphaCircleAlpha = _alphaCircleAlpha;
    }

    public static float getAlphaCircleRad() {
        return alphaCircleRad;
    }

    public static void setAlphaCircleRad(float _alphaCircleRad) {
        alphaCircleRad = _alphaCircleRad;
    }

    public static boolean isHighQuality() {
        return highQuality;
    }

    public static void setHighQuality(boolean _highQuality) {
        highQuality = _highQuality;
    }

    public static boolean isEnableEditing() {
        return enableEditing;
    }

    public static void setEnableEditing(boolean _enableEditing) {
        enableEditing = _enableEditing;
    }

    public static float getTopAlphaHeight() {
        return topAlphaHeight;
    }

    public static void setTopAlphaHeight(float _topAlphaHeight) {
        topAlphaHeight = _topAlphaHeight;
    }

    public static float getBottomAlphaHeight() {
        return bottomAlphaHeight;
    }

    public static void setBottomAlphaHeight(float _bottomAlphaHeight) {
        bottomAlphaHeight = _bottomAlphaHeight;
    }

    public static boolean isDrawDebugColors() {
        return drawDebugColors;
    }

    public static void setDrawDebugColors(boolean _drawDebugColors) {
        drawDebugColors = _drawDebugColors;
    }

    public static float getOctaves() {
        return octaves;
    }

    public static void setOctaves(float _octaves) {
        octaves = _octaves;
    }

    public static float getColorR() {
        return colorR;
    }

    public static void setColorR(float _colorR) {
        colorR = _colorR;
    }

    public static float getColorG() {
        return colorG;
    }

    public static void setColorG(float _colorG) {
        colorG = _colorG;
    }

    public static float getColorB() {
        return colorB;
    }

    public static void setColorB(float _colorB) {
        colorB = _colorB;
    }

    public static float getSecondLayerAlpha() {
        return secondLayerAlpha;
    }

    public static void setSecondLayerAlpha(float _secondLayerAlpha) {
        secondLayerAlpha = _secondLayerAlpha;
    }

    public static float getScalingX() {
        return scalingX;
    }

    public static void setScalingX(float _scalingX) {
        scalingX = _scalingX;
    }

    public static float getScalingY() {
        return scalingY;
    }

    public static void setScalingY(float _scalingY) {
        scalingY = _scalingY;
    }

    public static boolean isRenderOnlyOneRow() {
        return renderOnlyOneRow;
    }

    public static void setRenderOnlyOneRow(boolean _renderOnlyOneRow) {
        renderOnlyOneRow = _renderOnlyOneRow;
    }

    public static float getBaseAlpha() {
        return baseAlpha;
    }

    public static void setBaseAlpha(float _baseAlpha) {
        baseAlpha = _baseAlpha;
    }

    public static int getRenderEveryXRow() {
        return renderEveryXRow;
    }

    public static void setRenderEveryXRow(int _renderEveryXRow) {
        renderEveryXRow = _renderEveryXRow;
    }

    public static boolean isRenderCurrentLayerOnly() {
        return renderCurrentLayerOnly;
    }

    public static void setRenderCurrentLayerOnly(boolean _renderCurrentLayerOnly) {
        renderCurrentLayerOnly = _renderCurrentLayerOnly;
    }

    public static int getRenderXRowsFromCenter() {
        return renderXRowsFromCenter;
    }

    public static void setRenderXRowsFromCenter(int _renderXRowsFromCenter) {
        renderXRowsFromCenter = _renderXRowsFromCenter;
    }

    public static void update() {
        updateKeys();
        if (noiseTexture == null) {
            noiseTexture = Texture.getSharedTexture("media/textures/weather/fognew/fog_noise.png");
        }

        climateManager = ClimateManager.getInstance();
        if (!enableEditing) {
            highQuality = PerformanceSettings.FogQuality == 0;
            fogIntensity.setTarget(climateManager.getFogIntensity());
            fogIntensity.update(GameTime.getInstance().getMultiplier());
            baseAlpha = fogIntensity.value();
            if (highQuality) {
                renderEveryXRow = 1;
                topAlphaHeight = 0.38F;
                bottomAlphaHeight = 0.24F;
                octaves = 6.0F;
                secondLayerAlpha = 0.5F;
            } else {
                renderEveryXRow = 2;
                topAlphaHeight = 0.32F;
                bottomAlphaHeight = 0.32F;
                octaves = 3.0F;
                secondLayerAlpha = 1.0F;
            }

            colorR = climateManager.getColorNewFog().getExterior().r;
            colorG = climateManager.getColorNewFog().getExterior().g;
            colorB = climateManager.getColorNewFog().getExterior().b;
        }

        if (baseAlpha <= 0.0F) {
            scalingX = 0.0F;
            scalingY = 0.0F;
        } else {
            double double0 = climateManager.getWindAngleRadians();
            double0 -= Math.PI * 3.0 / 4.0;
            double0 = Math.PI - double0;
            float float0 = (float)Math.cos(double0);
            float float1 = (float)Math.sin(double0);
            scalingX = scalingX + float0 * climateManager.getWindIntensity() * GameTime.getInstance().getMultiplier();
            scalingY = scalingY + float1 * climateManager.getWindIntensity() * GameTime.getInstance().getMultiplier();
        }
    }

    public static void startRender(int nPlayer, int z) {
        climateManager = ClimateManager.getInstance();
        if (z < 2 && !(baseAlpha <= 0.0F) && PerformanceSettings.FogQuality != 2) {
            drawingThisLayer = true;
            IsoPlayer player = IsoPlayer.players[nPlayer];
            if (renderCurrentLayerOnly && player.getZ() != z) {
                drawingThisLayer = false;
            } else if (player.isInARoom() && z > 0) {
                drawingThisLayer = false;
            } else {
                playerRow = (int)player.getX() + (int)player.getY();
                ZOOM = Core.getInstance().getZoom(nPlayer);
                zLayer = z;
                PlayerIndex = nPlayer;
                PlayerCamera playerCamera = IsoCamera.cameras[nPlayer];
                screenWidth = IsoCamera.getOffscreenWidth(nPlayer);
                screenHeight = IsoCamera.getOffscreenHeight(nPlayer);
                worldOffsetX = playerCamera.getOffX() - IsoCamera.getOffscreenLeft(PlayerIndex) * ZOOM;
                worldOffsetY = playerCamera.getOffY() + IsoCamera.getOffscreenTop(PlayerIndex) * ZOOM;
                rightClickOffX = playerCamera.RightClickX;
                rightClickOffY = playerCamera.RightClickY;
                cameraOffscreenLeft = IsoCamera.getOffscreenLeft(nPlayer);
                cameraOffscreenTop = IsoCamera.getOffscreenTop(nPlayer);
                cameraZoom = ZOOM;
                if (!enableEditing) {
                    if (player.getVehicle() != null) {
                        alphaCircleAlpha = 0.0F;
                        alphaCircleRad = highQuality ? 2.0F : 2.6F;
                    } else if (player.isInARoom()) {
                        alphaCircleAlpha = 0.0F;
                        alphaCircleRad = highQuality ? 1.25F : 1.5F;
                    } else {
                        alphaCircleAlpha = highQuality ? 0.1F : 0.16F;
                        alphaCircleRad = highQuality ? 2.5F : 3.0F;
                        if (climateManager.getWeatherPeriod().isRunning()
                            && (climateManager.getWeatherPeriod().isTropicalStorm() || climateManager.getWeatherPeriod().isThunderStorm())) {
                            alphaCircleRad *= 0.6F;
                        }
                    }
                }

                byte byte0 = 0;
                byte byte1 = 0;
                int int0 = byte0 + IsoCamera.getOffscreenWidth(nPlayer);
                int int1 = byte1 + IsoCamera.getOffscreenHeight(nPlayer);
                float float0 = IsoUtils.XToIso(byte0, byte1, zLayer);
                float float1 = IsoUtils.YToIso(byte0, byte1, zLayer);
                float float2 = IsoUtils.XToIso(int0, int1, zLayer);
                float float3 = IsoUtils.YToIso(int0, int1, zLayer);
                float float4 = IsoUtils.YToIso(byte0, int1, zLayer);
                minY = (int)float1;
                maxY = (int)float3;
                minX = (int)float0;
                maxX = (int)float2;
                if (IsoPlayer.numPlayers > 1) {
                    maxX = Math.max(maxX, IsoWorld.instance.CurrentCell.getMaxX());
                    maxY = Math.max(maxY, IsoWorld.instance.CurrentCell.getMaxY());
                }

                minX = minX + minXOffset;
                maxX = maxX + maxXOffset;
                maxY = maxY + maxYOffset;
                int int2 = maxX - minX;
                int int3 = int2;
                if (minY != maxY) {
                    int3 = (int)(int2 + PZMath.abs(minY - maxY));
                }

                rectangleIter.reset(int2, int3);
                lastRow = -1;
                fogRectangle.hasStarted = false;
                chunkMap = IsoWorld.instance.getCell().getChunkMap(nPlayer);
            }
        } else {
            drawingThisLayer = false;
        }
    }

    public static void renderRowsBehind(IsoGridSquare squareMax) {
        if (drawingThisLayer) {
            int int0 = -1;
            if (squareMax != null) {
                int0 = squareMax.getX() + squareMax.getY();
                if (int0 < minX + minY) {
                    return;
                }
            }

            if (lastRow < 0 || lastRow != int0) {
                Vector2i vector2i = rectangleMatrixPos;

                while (rectangleIter.next(vector2i)) {
                    if (vector2i != null) {
                        int int1 = vector2i.x + minX;
                        int int2 = vector2i.y + minY;
                        int int3 = int1 + int2;
                        if (int3 != lastRow) {
                            if (lastRow >= 0 && (!renderEndOnly || squareMax == null)) {
                                endFogRectangle(lastIterPos.x, lastIterPos.y, zLayer);
                            }

                            lastRow = int3;
                        }

                        IsoGridSquare square = chunkMap.getGridSquare(int1, int2, zLayer);
                        boolean boolean0 = true;
                        if (square != null && (!square.isExteriorCache || square.isInARoom())) {
                            boolean0 = false;
                        }

                        if (boolean0) {
                            if (!renderEndOnly || squareMax == null) {
                                startFogRectangle(int1, int2, zLayer);
                            }
                        } else if (!renderEndOnly || squareMax == null) {
                            endFogRectangle(lastIterPos.x, lastIterPos.y, zLayer);
                        }

                        lastIterPos.set(int1, int2);
                        if (int0 != -1 && int3 == int0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void endRender() {
        if (drawingThisLayer) {
            renderRowsBehind(null);
            if (fogRectangle.hasStarted) {
                endFogRectangle(lastIterPos.x, lastIterPos.y, zLayer);
            }
        }
    }

    private static void startFogRectangle(int int0, int int1, int int2) {
        if (!fogRectangle.hasStarted) {
            fogRectangle.hasStarted = true;
            fogRectangle.startX = int0;
            fogRectangle.startY = int1;
            fogRectangle.Z = int2;
        }
    }

    private static void endFogRectangle(int int0, int int1, int int2) {
        if (fogRectangle.hasStarted) {
            fogRectangle.hasStarted = false;
            fogRectangle.endX = int0;
            fogRectangle.endY = int1;
            fogRectangle.Z = int2;
            renderFogSegment();
        }
    }

    private static void renderFogSegment() {
        int int0 = fogRectangle.startX + fogRectangle.startY;
        int int1 = fogRectangle.endX + fogRectangle.endY;
        if (Core.bDebug && int0 != int1) {
            DebugLog.log("ROWS NOT EQUAL");
        }

        if (renderOnlyOneRow) {
            if (int0 != playerRow) {
                return;
            }
        } else if (int0 % renderEveryXRow != 0) {
            return;
        }

        if (!Core.bDebug || renderXRowsFromCenter < 1 || int0 >= playerRow - renderXRowsFromCenter && int0 <= playerRow + renderXRowsFromCenter) {
            float float0 = baseAlpha;
            ImprovedFog.FogRectangle fogRectanglex = fogRectangle;
            float float1 = IsoUtils.XToScreenExact(fogRectanglex.startX, fogRectanglex.startY, fogRectanglex.Z, 0);
            float float2 = IsoUtils.YToScreenExact(fogRectanglex.startX, fogRectanglex.startY, fogRectanglex.Z, 0);
            float float3 = IsoUtils.XToScreenExact(fogRectanglex.endX, fogRectanglex.endY, fogRectanglex.Z, 0);
            float float4 = IsoUtils.YToScreenExact(fogRectanglex.endX, fogRectanglex.endY, fogRectanglex.Z, 0);
            float1 -= 32.0F * Core.TileScale;
            float2 -= 80.0F * Core.TileScale;
            float3 += 32.0F * Core.TileScale;
            float float5 = 96.0F * Core.TileScale;
            float float6 = (float3 - float1) / (64.0F * Core.TileScale);
            float float7 = fogRectanglex.startX % 6.0F;
            float float8 = float7 / 6.0F;
            float float9 = float6 / 6.0F;
            float float10 = float9 + float8;
            if (FogShader.instance.StartShader()) {
                FogShader.instance.setScreenInfo(screenWidth, screenHeight, ZOOM, zLayer > 0 ? secondLayerAlpha : 1.0F);
                FogShader.instance.setTextureInfo(drawDebugColors ? 1.0F : 0.0F, octaves, float0, Core.TileScale);
                FogShader.instance.setRectangleInfo((int)float1, (int)float2, (int)(float3 - float1), (int)float5);
                FogShader.instance.setWorldOffset(worldOffsetX, worldOffsetY, rightClickOffX, rightClickOffY);
                FogShader.instance.setScalingInfo(scalingX, scalingY, zLayer, highQuality ? 0.0F : 1.0F);
                FogShader.instance.setColorInfo(colorR, colorG, colorB, 1.0F);
                FogShader.instance.setParamInfo(topAlphaHeight, bottomAlphaHeight, alphaCircleAlpha, alphaCircleRad);
                FogShader.instance.setCameraInfo(cameraOffscreenLeft, cameraOffscreenTop, cameraZoom, offsets[int0 % offsets.length]);
                SpriteRenderer.instance
                    .render(
                        noiseTexture,
                        (int)float1,
                        (int)float2,
                        (int)(float3 - float1),
                        (int)float5,
                        1.0F,
                        1.0F,
                        1.0F,
                        float0,
                        float8,
                        0.0F,
                        float10,
                        0.0F,
                        float10,
                        1.0F,
                        float8,
                        1.0F
                    );
                IndieGL.EndShader();
            }
        }
    }

    public static void DrawSubTextureRGBA(
        Texture tex, double subX, double subY, double subW, double subH, double x, double y, double w, double h, double r, double g, double b, double a
    ) {
        if (tex != null && !(subW <= 0.0) && !(subH <= 0.0) && !(w <= 0.0) && !(h <= 0.0)) {
            double double0 = x + tex.offsetX;
            double double1 = y + tex.offsetY;
            if (!(double1 + h < 0.0) && !(double1 > 4096.0)) {
                float float0 = PZMath.clamp((float)subX, 0.0F, (float)tex.getWidth());
                float float1 = PZMath.clamp((float)subY, 0.0F, (float)tex.getHeight());
                float float2 = PZMath.clamp((float)(float0 + subW), 0.0F, (float)tex.getWidth()) - float0;
                float float3 = PZMath.clamp((float)(float1 + subH), 0.0F, (float)tex.getHeight()) - float1;
                float float4 = float0 / tex.getWidth();
                float float5 = float1 / tex.getHeight();
                float float6 = (float0 + float2) / tex.getWidth();
                float float7 = (float1 + float3) / tex.getHeight();
                float float8 = tex.getXEnd() - tex.getXStart();
                float float9 = tex.getYEnd() - tex.getYStart();
                float4 = tex.getXStart() + float4 * float8;
                float6 = tex.getXStart() + float6 * float8;
                float5 = tex.getYStart() + float5 * float9;
                float7 = tex.getYStart() + float7 * float9;
                SpriteRenderer.instance
                    .render(
                        tex,
                        (float)double0,
                        (float)double1,
                        (float)w,
                        (float)h,
                        (float)r,
                        (float)g,
                        (float)b,
                        (float)a,
                        float4,
                        float5,
                        float6,
                        float5,
                        float6,
                        float7,
                        float4,
                        float7
                    );
            }
        }
    }

    public static void updateKeys() {
        if (Core.bDebug) {
            if (keyPause > 0) {
                keyPause--;
            }

            if (keyPause <= 0 && GameKeyboard.isKeyDown(72)) {
                DebugLog.log("Reloading fog shader...");
                keyPause = 30;
                FogShader.instance.reloadShader();
            }
        }
    }

    public static void init() {
        climateManager = ClimateManager.getInstance();
        fogIntensity.setTarget(climateManager.getFogIntensity());
        fogIntensity.overrideCurrentValue(climateManager.getFogIntensity());
        baseAlpha = fogIntensity.value();
    }

    private static class FogRectangle {
        int startX;
        int startY;
        int endX;
        int endY;
        int Z;
        boolean hasStarted = false;
    }

    private static class RectangleIterator {
        private int curX = 0;
        private int curY = 0;
        private int sX;
        private int sY;
        private int rowLen = 0;
        private boolean altRow = false;
        private int curRow = 0;
        private int rowIndex = 0;
        private int maxRows = 0;

        public void reset(int arg0, int arg1) {
            this.sX = 0;
            this.sY = 0;
            this.curX = 0;
            this.curY = 0;
            this.curRow = 0;
            this.altRow = false;
            this.rowIndex = 0;
            this.rowLen = (int)PZMath.ceil(arg1 / 2.0F);
            this.maxRows = arg0;
        }

        public boolean next(Vector2i arg0) {
            if (this.rowLen > 0 && this.maxRows > 0 && this.curRow < this.maxRows) {
                arg0.set(this.curX, this.curY);
                this.rowIndex++;
                if (this.rowIndex == this.rowLen) {
                    this.rowLen = this.altRow ? this.rowLen - 1 : this.rowLen + 1;
                    this.rowIndex = 0;
                    this.sX = this.altRow ? this.sX + 1 : this.sX;
                    this.sY = this.altRow ? this.sY : this.sY + 1;
                    this.altRow = !this.altRow;
                    this.curX = this.sX;
                    this.curY = this.sY;
                    this.curRow++;
                    return this.curRow != this.maxRows;
                } else {
                    this.curX++;
                    this.curY--;
                    return true;
                }
            } else {
                arg0.set(0, 0);
                return false;
            }
        }
    }
}
