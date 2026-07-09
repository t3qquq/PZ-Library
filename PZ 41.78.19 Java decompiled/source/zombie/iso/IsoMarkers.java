// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import java.util.List;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.debug.LineDrawer;
import zombie.network.GameServer;
import zombie.util.Type;

public final class IsoMarkers {
    public static final IsoMarkers instance = new IsoMarkers();
    private static int NextIsoMarkerID = 0;
    private final List<IsoMarkers.IsoMarker> markers = new ArrayList<>();
    private final List<IsoMarkers.CircleIsoMarker> circlemarkers = new ArrayList<>();
    private static int NextCircleIsoMarkerID = 0;

    private IsoMarkers() {
    }

    public void init() {
    }

    public void reset() {
        this.markers.clear();
        this.circlemarkers.clear();
    }

    public void update() {
        if (!GameServer.bServer) {
            this.updateIsoMarkers();
            this.updateCircleIsoMarkers();
        }
    }

    private void updateIsoMarkers() {
        if (IsoCamera.frameState.playerIndex == 0) {
            if (this.markers.size() != 0) {
                for (int int0 = this.markers.size() - 1; int0 >= 0; int0--) {
                    if (this.markers.get(int0).isRemoved()) {
                        if (this.markers.get(int0).hasTempSquareObject()) {
                            this.markers.get(int0).removeTempSquareObjects();
                        }

                        this.markers.remove(int0);
                    }
                }

                for (int int1 = 0; int1 < this.markers.size(); int1++) {
                    IsoMarkers.IsoMarker marker = this.markers.get(int1);
                    if (marker.alphaInc) {
                        marker.alpha = marker.alpha + GameTime.getInstance().getMultiplier() * marker.fadeSpeed;
                        if (marker.alpha > marker.alphaMax) {
                            marker.alphaInc = false;
                            marker.alpha = marker.alphaMax;
                        }
                    } else {
                        marker.alpha = marker.alpha - GameTime.getInstance().getMultiplier() * marker.fadeSpeed;
                        if (marker.alpha < marker.alphaMin) {
                            marker.alphaInc = true;
                            marker.alpha = 0.3F;
                        }
                    }
                }
            }
        }
    }

    public boolean removeIsoMarker(IsoMarkers.IsoMarker marker) {
        return this.removeIsoMarker(marker.getID());
    }

    public boolean removeIsoMarker(int id) {
        for (int int0 = this.markers.size() - 1; int0 >= 0; int0--) {
            if (this.markers.get(int0).getID() == id) {
                this.markers.get(int0).remove();
                this.markers.remove(int0);
                return true;
            }
        }

        return false;
    }

    public IsoMarkers.IsoMarker getIsoMarker(int id) {
        for (int int0 = 0; int0 < this.markers.size(); int0++) {
            if (this.markers.get(int0).getID() == id) {
                return this.markers.get(int0);
            }
        }

        return null;
    }

    public IsoMarkers.IsoMarker addIsoMarker(String spriteName, IsoGridSquare gs, float r, float g, float b, boolean doAlpha, boolean doIsoObject) {
        if (GameServer.bServer) {
            return null;
        } else {
            IsoMarkers.IsoMarker marker = new IsoMarkers.IsoMarker();
            marker.setSquare(gs);
            marker.init(spriteName, gs.x, gs.y, gs.z, gs, doIsoObject);
            marker.setR(r);
            marker.setG(g);
            marker.setB(b);
            marker.setA(1.0F);
            marker.setDoAlpha(doAlpha);
            marker.setFadeSpeed(0.006F);
            marker.setAlpha(1.0F);
            marker.setAlphaMin(0.3F);
            marker.setAlphaMax(1.0F);
            this.markers.add(marker);
            return marker;
        }
    }

    public IsoMarkers.IsoMarker addIsoMarker(
        KahluaTable textureTable, KahluaTable textureOverlayTable, IsoGridSquare gs, float r, float g, float b, boolean doAlpha, boolean doIsoObject
    ) {
        return this.addIsoMarker(textureTable, textureOverlayTable, gs, r, g, b, doAlpha, doIsoObject, 0.006F, 0.3F, 1.0F);
    }

    public IsoMarkers.IsoMarker addIsoMarker(
        KahluaTable textureTable,
        KahluaTable textureOverlayTable,
        IsoGridSquare gs,
        float r,
        float g,
        float b,
        boolean doAlpha,
        boolean doIsoObject,
        float fadeSpeed,
        float fadeMin,
        float fadeMax
    ) {
        if (GameServer.bServer) {
            return null;
        } else {
            IsoMarkers.IsoMarker marker = new IsoMarkers.IsoMarker();
            marker.init(textureTable, textureOverlayTable, gs.x, gs.y, gs.z, gs, doIsoObject);
            marker.setSquare(gs);
            marker.setR(r);
            marker.setG(g);
            marker.setB(b);
            marker.setA(1.0F);
            marker.setDoAlpha(doAlpha);
            marker.setFadeSpeed(fadeSpeed);
            marker.setAlpha(0.0F);
            marker.setAlphaMin(fadeMin);
            marker.setAlphaMax(fadeMax);
            this.markers.add(marker);
            return marker;
        }
    }

    public void renderIsoMarkers(IsoCell.PerPlayerRender perPlayerRender, int zLayer, int playerIndex) {
        if (!GameServer.bServer && this.markers.size() != 0) {
            IsoPlayer player = IsoPlayer.players[playerIndex];
            if (player != null) {
                for (int int0 = 0; int0 < this.markers.size(); int0++) {
                    IsoMarkers.IsoMarker marker = this.markers.get(int0);
                    if (marker.z == zLayer && marker.z == player.getZ() && marker.active) {
                        for (int int1 = 0; int1 < marker.textures.size(); int1++) {
                            Texture texture = marker.textures.get(int1);
                            float float0 = IsoUtils.XToScreen(marker.x, marker.y, marker.z, 0)
                                - IsoCamera.cameras[playerIndex].getOffX()
                                - texture.getWidth() / 2.0F;
                            float float1 = IsoUtils.YToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[playerIndex].getOffY() - texture.getHeight();
                            SpriteRenderer.instance
                                .render(texture, float0, float1, texture.getWidth(), texture.getHeight(), marker.r, marker.g, marker.b, marker.alpha, null);
                        }
                    }
                }
            }
        }
    }

    public void renderIsoMarkersDeferred(IsoCell.PerPlayerRender perPlayerRender, int zLayer, int playerIndex) {
        if (!GameServer.bServer && this.markers.size() != 0) {
            IsoPlayer player = IsoPlayer.players[playerIndex];
            if (player != null) {
                for (int int0 = 0; int0 < this.markers.size(); int0++) {
                    IsoMarkers.IsoMarker marker = this.markers.get(int0);
                    if (marker.z == zLayer && marker.z == player.getZ() && marker.active) {
                        for (int int1 = 0; int1 < marker.overlayTextures.size(); int1++) {
                            Texture texture = marker.overlayTextures.get(int1);
                            float float0 = IsoUtils.XToScreen(marker.x, marker.y, marker.z, 0)
                                - IsoCamera.cameras[playerIndex].getOffX()
                                - texture.getWidth() / 2.0F;
                            float float1 = IsoUtils.YToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[playerIndex].getOffY() - texture.getHeight();
                            SpriteRenderer.instance
                                .render(texture, float0, float1, texture.getWidth(), texture.getHeight(), marker.r, marker.g, marker.b, marker.alpha, null);
                        }
                    }
                }
            }
        }
    }

    public void renderIsoMarkersOnSquare(IsoCell.PerPlayerRender perPlayerRender, int zLayer, int playerIndex) {
        if (!GameServer.bServer && this.markers.size() != 0) {
            IsoPlayer player = IsoPlayer.players[playerIndex];
            if (player != null) {
                for (int int0 = 0; int0 < this.markers.size(); int0++) {
                    IsoMarkers.IsoMarker marker = this.markers.get(int0);
                    if (marker.z == zLayer && marker.z == player.getZ() && marker.active) {
                        for (int int1 = 0; int1 < marker.overlayTextures.size(); int1++) {
                            Texture texture = marker.overlayTextures.get(int1);
                            float float0 = IsoUtils.XToScreen(marker.x, marker.y, marker.z, 0)
                                - IsoCamera.cameras[playerIndex].getOffX()
                                - texture.getWidth() / 2.0F;
                            float float1 = IsoUtils.YToScreen(marker.x, marker.y, marker.z, 0) - IsoCamera.cameras[playerIndex].getOffY() - texture.getHeight();
                            SpriteRenderer.instance
                                .render(texture, float0, float1, texture.getWidth(), texture.getHeight(), marker.r, marker.g, marker.b, marker.alpha, null);
                        }
                    }
                }
            }
        }
    }

    private void updateCircleIsoMarkers() {
        if (IsoCamera.frameState.playerIndex == 0) {
            if (this.circlemarkers.size() != 0) {
                for (int int0 = this.circlemarkers.size() - 1; int0 >= 0; int0--) {
                    if (this.circlemarkers.get(int0).isRemoved()) {
                        this.circlemarkers.remove(int0);
                    }
                }

                for (int int1 = 0; int1 < this.circlemarkers.size(); int1++) {
                    IsoMarkers.CircleIsoMarker circleIsoMarker = this.circlemarkers.get(int1);
                    if (circleIsoMarker.alphaInc) {
                        circleIsoMarker.alpha = circleIsoMarker.alpha + GameTime.getInstance().getMultiplier() * circleIsoMarker.fadeSpeed;
                        if (circleIsoMarker.alpha > circleIsoMarker.alphaMax) {
                            circleIsoMarker.alphaInc = false;
                            circleIsoMarker.alpha = circleIsoMarker.alphaMax;
                        }
                    } else {
                        circleIsoMarker.alpha = circleIsoMarker.alpha - GameTime.getInstance().getMultiplier() * circleIsoMarker.fadeSpeed;
                        if (circleIsoMarker.alpha < circleIsoMarker.alphaMin) {
                            circleIsoMarker.alphaInc = true;
                            circleIsoMarker.alpha = 0.3F;
                        }
                    }
                }
            }
        }
    }

    public boolean removeCircleIsoMarker(IsoMarkers.CircleIsoMarker marker) {
        return this.removeCircleIsoMarker(marker.getID());
    }

    public boolean removeCircleIsoMarker(int id) {
        for (int int0 = this.circlemarkers.size() - 1; int0 >= 0; int0--) {
            if (this.circlemarkers.get(int0).getID() == id) {
                this.circlemarkers.get(int0).remove();
                this.circlemarkers.remove(int0);
                return true;
            }
        }

        return false;
    }

    public IsoMarkers.CircleIsoMarker getCircleIsoMarker(int id) {
        for (int int0 = 0; int0 < this.circlemarkers.size(); int0++) {
            if (this.circlemarkers.get(int0).getID() == id) {
                return this.circlemarkers.get(int0);
            }
        }

        return null;
    }

    public IsoMarkers.CircleIsoMarker addCircleIsoMarker(IsoGridSquare gs, float r, float g, float b, float a) {
        if (GameServer.bServer) {
            return null;
        } else {
            IsoMarkers.CircleIsoMarker circleIsoMarker = new IsoMarkers.CircleIsoMarker();
            circleIsoMarker.init(gs.x, gs.y, gs.z, gs);
            circleIsoMarker.setSquare(gs);
            circleIsoMarker.setR(r);
            circleIsoMarker.setG(g);
            circleIsoMarker.setB(b);
            circleIsoMarker.setA(a);
            circleIsoMarker.setDoAlpha(false);
            circleIsoMarker.setFadeSpeed(0.006F);
            circleIsoMarker.setAlpha(1.0F);
            circleIsoMarker.setAlphaMin(1.0F);
            circleIsoMarker.setAlphaMax(1.0F);
            this.circlemarkers.add(circleIsoMarker);
            return circleIsoMarker;
        }
    }

    public void renderCircleIsoMarkers(IsoCell.PerPlayerRender perPlayerRender, int zLayer, int playerIndex) {
        if (!GameServer.bServer && this.circlemarkers.size() != 0) {
            IsoPlayer player = IsoPlayer.players[playerIndex];
            if (player != null) {
                for (int int0 = 0; int0 < this.circlemarkers.size(); int0++) {
                    IsoMarkers.CircleIsoMarker circleIsoMarker = this.circlemarkers.get(int0);
                    if (circleIsoMarker.z == zLayer && circleIsoMarker.z == player.getZ() && circleIsoMarker.active) {
                        LineDrawer.DrawIsoCircle(
                            circleIsoMarker.x,
                            circleIsoMarker.y,
                            circleIsoMarker.z,
                            circleIsoMarker.size,
                            32,
                            circleIsoMarker.r,
                            circleIsoMarker.g,
                            circleIsoMarker.b,
                            circleIsoMarker.a
                        );
                    }
                }
            }
        }
    }

    public void render() {
        this.update();
    }

    public static final class CircleIsoMarker {
        private int ID;
        private IsoGridSquare square;
        private float x;
        private float y;
        private float z;
        private float r;
        private float g;
        private float b;
        private float a;
        private float size;
        private boolean doAlpha;
        private float fadeSpeed = 0.006F;
        private float alpha = 0.0F;
        private float alphaMax = 1.0F;
        private float alphaMin = 0.3F;
        private boolean alphaInc = true;
        private boolean active = true;
        private boolean isRemoved = false;

        public CircleIsoMarker() {
            this.ID = IsoMarkers.NextCircleIsoMarkerID++;
        }

        public int getID() {
            return this.ID;
        }

        public void remove() {
            this.isRemoved = true;
        }

        public boolean isRemoved() {
            return this.isRemoved;
        }

        public void init(int _x, int _y, int _z, IsoGridSquare gs) {
            this.square = gs;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public float getZ() {
            return this.z;
        }

        public float getR() {
            return this.r;
        }

        public float getG() {
            return this.g;
        }

        public float getB() {
            return this.b;
        }

        public float getA() {
            return this.a;
        }

        public void setR(float _r) {
            this.r = _r;
        }

        public void setG(float _g) {
            this.g = _g;
        }

        public void setB(float _b) {
            this.b = _b;
        }

        public void setA(float _a) {
            this.a = _a;
        }

        public float getSize() {
            return this.size;
        }

        public void setSize(float _size) {
            this.size = _size;
        }

        public float getAlpha() {
            return this.alpha;
        }

        public void setAlpha(float _alpha) {
            this.alpha = _alpha;
        }

        public float getAlphaMax() {
            return this.alphaMax;
        }

        public void setAlphaMax(float _alphaMax) {
            this.alphaMax = _alphaMax;
        }

        public float getAlphaMin() {
            return this.alphaMin;
        }

        public void setAlphaMin(float _alphaMin) {
            this.alphaMin = _alphaMin;
        }

        public boolean isDoAlpha() {
            return this.doAlpha;
        }

        public void setDoAlpha(boolean _doAlpha) {
            this.doAlpha = _doAlpha;
        }

        public float getFadeSpeed() {
            return this.fadeSpeed;
        }

        public void setFadeSpeed(float _fadeSpeed) {
            this.fadeSpeed = _fadeSpeed;
        }

        public IsoGridSquare getSquare() {
            return this.square;
        }

        public void setSquare(IsoGridSquare _square) {
            this.square = _square;
        }

        public void setPos(int _x, int _y, int _z) {
            this.x = _x;
            this.y = _y;
            this.z = _z;
        }

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean _active) {
            this.active = _active;
        }
    }

    public static final class IsoMarker {
        private int ID;
        private ArrayList<Texture> textures = new ArrayList<>();
        private ArrayList<Texture> overlayTextures = new ArrayList<>();
        private ArrayList<IsoObject> tempObjects = new ArrayList<>();
        private IsoGridSquare square;
        private float x;
        private float y;
        private float z;
        private float r;
        private float g;
        private float b;
        private float a;
        private boolean doAlpha;
        private float fadeSpeed = 0.006F;
        private float alpha = 0.0F;
        private float alphaMax = 1.0F;
        private float alphaMin = 0.3F;
        private boolean alphaInc = true;
        private boolean active = true;
        private boolean isRemoved = false;

        public IsoMarker() {
            this.ID = IsoMarkers.NextIsoMarkerID++;
        }

        public int getID() {
            return this.ID;
        }

        public void remove() {
            this.isRemoved = true;
        }

        public boolean isRemoved() {
            return this.isRemoved;
        }

        public void init(KahluaTable textureTable, KahluaTable textureOverlayTable, int _x, int _y, int _z, IsoGridSquare gs) {
            this.square = gs;
            if (textureTable != null) {
                int int0 = textureTable.len();

                for (int int1 = 1; int1 <= int0; int1++) {
                    String string0 = Type.tryCastTo(textureTable.rawget(int1), String.class);
                    Texture texture0 = Texture.trygetTexture(string0);
                    if (texture0 != null) {
                        this.textures.add(texture0);
                        this.setPos(_x, _y, _z);
                    }
                }
            }

            if (textureOverlayTable != null) {
                int int2 = textureOverlayTable.len();

                for (int int3 = 1; int3 <= int2; int3++) {
                    String string1 = Type.tryCastTo(textureOverlayTable.rawget(int3), String.class);
                    Texture texture1 = Texture.trygetTexture(string1);
                    if (texture1 != null) {
                        this.overlayTextures.add(texture1);
                        this.setPos(_x, _y, _z);
                    }
                }
            }
        }

        public void init(KahluaTable textureTable, KahluaTable textureOverlayTable, int _x, int _y, int _z, IsoGridSquare gs, boolean doTempIsoObject) {
            this.square = gs;
            if (doTempIsoObject) {
                if (textureTable != null) {
                    int int0 = textureTable.len();

                    for (int int1 = 1; int1 <= int0; int1++) {
                        String string = Type.tryCastTo(textureTable.rawget(int1), String.class);
                        Texture texture = Texture.trygetTexture(string);
                        if (texture != null) {
                            IsoObject object = new IsoObject(gs.getCell(), gs, texture.getName());
                            this.tempObjects.add(object);
                            this.addTempSquareObject(object);
                            this.setPos(_x, _y, _z);
                        }
                    }
                }
            } else {
                this.init(textureTable, textureOverlayTable, _x, _y, _z, gs);
            }
        }

        public void init(String spriteName, int _x, int _y, int _z, IsoGridSquare gs, boolean doTempIsoObject) {
            this.square = gs;
            if (doTempIsoObject && spriteName != null) {
                IsoObject object = IsoObject.getNew(gs, spriteName, spriteName, false);
                this.tempObjects.add(object);
                this.addTempSquareObject(object);
                this.setPos(_x, _y, _z);
            }
        }

        public boolean hasTempSquareObject() {
            return this.tempObjects.size() > 0;
        }

        public void addTempSquareObject(IsoObject tempObject) {
            this.square.localTemporaryObjects.add(tempObject);
        }

        public void removeTempSquareObjects() {
            this.square.localTemporaryObjects.clear();
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public float getZ() {
            return this.z;
        }

        public float getR() {
            return this.r;
        }

        public float getG() {
            return this.g;
        }

        public float getB() {
            return this.b;
        }

        public float getA() {
            return this.a;
        }

        public void setR(float _r) {
            this.r = _r;
        }

        public void setG(float _g) {
            this.g = _g;
        }

        public void setB(float _b) {
            this.b = _b;
        }

        public void setA(float _a) {
            this.a = _a;
        }

        public float getAlpha() {
            return this.alpha;
        }

        public void setAlpha(float _alpha) {
            this.alpha = _alpha;
        }

        public float getAlphaMax() {
            return this.alphaMax;
        }

        public void setAlphaMax(float _alphaMax) {
            this.alphaMax = _alphaMax;
        }

        public float getAlphaMin() {
            return this.alphaMin;
        }

        public void setAlphaMin(float _alphaMin) {
            this.alphaMin = _alphaMin;
        }

        public boolean isDoAlpha() {
            return this.doAlpha;
        }

        public void setDoAlpha(boolean _doAlpha) {
            this.doAlpha = _doAlpha;
        }

        public float getFadeSpeed() {
            return this.fadeSpeed;
        }

        public void setFadeSpeed(float _fadeSpeed) {
            this.fadeSpeed = _fadeSpeed;
        }

        public IsoGridSquare getSquare() {
            return this.square;
        }

        public void setSquare(IsoGridSquare _square) {
            this.square = _square;
        }

        public void setPos(int _x, int _y, int _z) {
            this.x = _x + 0.5F;
            this.y = _y + 0.5F;
            this.z = _z;
        }

        public boolean isActive() {
            return this.active;
        }

        public void setActive(boolean _active) {
            this.active = _active;
        }
    }
}
