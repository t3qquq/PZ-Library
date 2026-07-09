// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.iso.areas.IsoRoom;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.MoodlesUI;
import zombie.ui.UIManager;

public class IsoCamera {
    public static final PlayerCamera[] cameras = new PlayerCamera[4];
    public static IsoGameCharacter CamCharacter;
    public static Vector2 FakePos;
    public static Vector2 FakePosVec;
    public static int TargetTileX;
    public static int TargetTileY;
    public static int PLAYER_OFFSET_X;
    public static int PLAYER_OFFSET_Y;
    public static final IsoCamera.FrameState frameState;

    public static void init() {
        PLAYER_OFFSET_Y = -56 / (2 / Core.TileScale);
    }

    public static void update() {
        int int0 = IsoPlayer.getPlayerIndex();
        cameras[int0].update();
    }

    public static void updateAll() {
        for (int int0 = 0; int0 < 4; int0++) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null) {
                CamCharacter = player;
                cameras[int0].update();
            }
        }
    }

    public static void SetCharacterToFollow(IsoGameCharacter GameChar) {
        if (!GameClient.bClient && !GameServer.bServer) {
            CamCharacter = GameChar;
            if (CamCharacter instanceof IsoPlayer
                && ((IsoPlayer)CamCharacter).isLocalPlayer()
                && UIManager.getMoodleUI(((IsoPlayer)CamCharacter).getPlayerNum()) != null) {
                int int0 = ((IsoPlayer)CamCharacter).getPlayerNum();
                UIManager.getUI().remove(UIManager.getMoodleUI(int0));
                UIManager.setMoodleUI(int0, new MoodlesUI());
                UIManager.getMoodleUI(int0).setCharacter(CamCharacter);
                UIManager.getUI().add(UIManager.getMoodleUI(int0));
            }
        }
    }

    public static float getRightClickOffX() {
        return (int)cameras[IsoPlayer.getPlayerIndex()].RightClickX;
    }

    public static float getRightClickOffY() {
        return (int)cameras[IsoPlayer.getPlayerIndex()].RightClickY;
    }

    /**
     * @return the OffX
     */
    public static float getOffX() {
        return cameras[IsoPlayer.getPlayerIndex()].getOffX();
    }

    public static float getTOffX() {
        return cameras[IsoPlayer.getPlayerIndex()].getTOffX();
    }

    /**
     * 
     * @param aOffX the OffX to set
     */
    public static void setOffX(float aOffX) {
        cameras[IsoPlayer.getPlayerIndex()].OffX = aOffX;
    }

    /**
     * @return the OffY
     */
    public static float getOffY() {
        return cameras[IsoPlayer.getPlayerIndex()].getOffY();
    }

    public static float getTOffY() {
        return cameras[IsoPlayer.getPlayerIndex()].getTOffY();
    }

    /**
     * 
     * @param aOffY the OffY to set
     */
    public static void setOffY(float aOffY) {
        cameras[IsoPlayer.getPlayerIndex()].OffY = aOffY;
    }

    /**
     * @return the lastOffX
     */
    public static float getLastOffX() {
        return cameras[IsoPlayer.getPlayerIndex()].getLastOffX();
    }

    /**
     * 
     * @param aLastOffX the lastOffX to set
     */
    public static void setLastOffX(float aLastOffX) {
        cameras[IsoPlayer.getPlayerIndex()].lastOffX = aLastOffX;
    }

    /**
     * @return the lastOffY
     */
    public static float getLastOffY() {
        return cameras[IsoPlayer.getPlayerIndex()].getLastOffY();
    }

    /**
     * 
     * @param aLastOffY the lastOffY to set
     */
    public static void setLastOffY(float aLastOffY) {
        cameras[IsoPlayer.getPlayerIndex()].lastOffY = aLastOffY;
    }

    /**
     * @return the CamCharacter
     */
    public static IsoGameCharacter getCamCharacter() {
        return CamCharacter;
    }

    /**
     * 
     * @param aCamCharacter the CamCharacter to set
     */
    public static void setCamCharacter(IsoGameCharacter aCamCharacter) {
        CamCharacter = aCamCharacter;
    }

    /**
     * @return the FakePos
     */
    public static Vector2 getFakePos() {
        return FakePos;
    }

    /**
     * 
     * @param aFakePos the FakePos to set
     */
    public static void setFakePos(Vector2 aFakePos) {
        FakePos = aFakePos;
    }

    /**
     * @return the FakePosVec
     */
    public static Vector2 getFakePosVec() {
        return FakePosVec;
    }

    /**
     * 
     * @param aFakePosVec the FakePosVec to set
     */
    public static void setFakePosVec(Vector2 aFakePosVec) {
        FakePosVec = aFakePosVec;
    }

    /**
     * @return the TargetTileX
     */
    public static int getTargetTileX() {
        return TargetTileX;
    }

    /**
     * 
     * @param aTargetTileX the TargetTileX to set
     */
    public static void setTargetTileX(int aTargetTileX) {
        TargetTileX = aTargetTileX;
    }

    /**
     * @return the TargetTileY
     */
    public static int getTargetTileY() {
        return TargetTileY;
    }

    /**
     * 
     * @param aTargetTileY the TargetTileY to set
     */
    public static void setTargetTileY(int aTargetTileY) {
        TargetTileY = aTargetTileY;
    }

    public static int getScreenLeft(int playerIndex) {
        return playerIndex != 1 && playerIndex != 3 ? 0 : Core.getInstance().getScreenWidth() / 2;
    }

    public static int getScreenWidth(int playerIndex) {
        return IsoPlayer.numPlayers > 1 ? Core.getInstance().getScreenWidth() / 2 : Core.getInstance().getScreenWidth();
    }

    public static int getScreenTop(int playerIndex) {
        return playerIndex != 2 && playerIndex != 3 ? 0 : Core.getInstance().getScreenHeight() / 2;
    }

    public static int getScreenHeight(int playerIndex) {
        return IsoPlayer.numPlayers > 2 ? Core.getInstance().getScreenHeight() / 2 : Core.getInstance().getScreenHeight();
    }

    public static int getOffscreenLeft(int playerIndex) {
        return playerIndex != 1 && playerIndex != 3 ? 0 : Core.getInstance().getScreenWidth() / 2;
    }

    public static int getOffscreenWidth(int playerIndex) {
        return Core.getInstance().getOffscreenWidth(playerIndex);
    }

    public static int getOffscreenTop(int playerIndex) {
        return playerIndex >= 2 ? Core.getInstance().getScreenHeight() / 2 : 0;
    }

    public static int getOffscreenHeight(int playerIndex) {
        return Core.getInstance().getOffscreenHeight(playerIndex);
    }

    static {
        for (int int0 = 0; int0 < cameras.length; int0++) {
            cameras[int0] = new PlayerCamera(int0);
        }

        CamCharacter = null;
        FakePos = new Vector2();
        FakePosVec = new Vector2();
        TargetTileX = 0;
        TargetTileY = 0;
        PLAYER_OFFSET_X = 0;
        PLAYER_OFFSET_Y = -56 / (2 / Core.TileScale);
        frameState = new IsoCamera.FrameState();
    }

    public static class FrameState {
        public int frameCount;
        public boolean Paused;
        public int playerIndex;
        public float CamCharacterX;
        public float CamCharacterY;
        public float CamCharacterZ;
        public IsoGameCharacter CamCharacter;
        public IsoGridSquare CamCharacterSquare;
        public IsoRoom CamCharacterRoom;
        public float OffX;
        public float OffY;
        public int OffscreenWidth;
        public int OffscreenHeight;

        public void set(int _playerIndex) {
            this.Paused = GameTime.isGamePaused();
            this.playerIndex = _playerIndex;
            this.CamCharacter = IsoPlayer.players[_playerIndex];
            this.CamCharacterX = this.CamCharacter.getX();
            this.CamCharacterY = this.CamCharacter.getY();
            this.CamCharacterZ = this.CamCharacter.getZ();
            this.CamCharacterSquare = this.CamCharacter.getCurrentSquare();
            this.CamCharacterRoom = this.CamCharacterSquare == null ? null : this.CamCharacterSquare.getRoom();
            this.OffX = IsoCamera.getOffX();
            this.OffY = IsoCamera.getOffY();
            this.OffscreenWidth = IsoCamera.getOffscreenWidth(_playerIndex);
            this.OffscreenHeight = IsoCamera.getOffscreenHeight(_playerIndex);
        }
    }
}
