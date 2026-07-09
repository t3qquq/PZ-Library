// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.WorldSimulation;
import zombie.debug.DebugLog;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.popman.ZombiePopulationManager;
import zombie.ui.UIManager;

public final class AddCoopPlayer {
    private AddCoopPlayer.Stage stage = AddCoopPlayer.Stage.Init;
    private IsoPlayer player;

    public AddCoopPlayer(IsoPlayer playerx) {
        this.player = playerx;
    }

    public void update() {
        switch (this.stage) {
            case Init:
                if (GameClient.bClient) {
                    ByteBufferWriter byteBufferWriter1 = GameClient.connection.startPacket();
                    PacketTypes.PacketType.AddCoopPlayer.doPacket(byteBufferWriter1);
                    byteBufferWriter1.putByte((byte)1);
                    byteBufferWriter1.putByte((byte)this.player.PlayerIndex);
                    byteBufferWriter1.putUTF(this.player.username != null ? this.player.username : "");
                    byteBufferWriter1.putFloat(this.player.x);
                    byteBufferWriter1.putFloat(this.player.y);
                    PacketTypes.PacketType.AddCoopPlayer.send(GameClient.connection);
                    this.stage = AddCoopPlayer.Stage.ReceiveClientConnect;
                } else {
                    this.stage = AddCoopPlayer.Stage.StartMapLoading;
                }
            case ReceiveClientConnect:
            case ReceivePlayerConnect:
            case Finished:
            default:
                break;
            case StartMapLoading:
                IsoCell cell2 = IsoWorld.instance.CurrentCell;
                int int2 = this.player.PlayerIndex;
                IsoChunkMap chunkMap1 = cell2.ChunkMap[int2];
                IsoChunkMap.bSettingChunk.lock();

                try {
                    chunkMap1.Unload();
                    chunkMap1.ignore = false;
                    int int3 = (int)(this.player.x / 10.0F);
                    int int4 = (int)(this.player.y / 10.0F);

                    try {
                        if (LightingJNI.init) {
                            LightingJNI.teleport(int2, int3 - IsoChunkMap.ChunkGridWidth / 2, int4 - IsoChunkMap.ChunkGridWidth / 2);
                        }
                    } catch (Exception exception) {
                    }

                    if (!GameServer.bServer && !GameClient.bClient) {
                        ZombiePopulationManager.instance.playerSpawnedAt((int)this.player.x, (int)this.player.y, (int)this.player.z);
                    }

                    chunkMap1.WorldX = int3;
                    chunkMap1.WorldY = int4;
                    if (!GameServer.bServer) {
                        WorldSimulation.instance.activateChunkMap(int2);
                    }

                    int int5 = int3 - IsoChunkMap.ChunkGridWidth / 2;
                    int int6 = int4 - IsoChunkMap.ChunkGridWidth / 2;
                    int int7 = int3 + IsoChunkMap.ChunkGridWidth / 2 + 1;
                    int int8 = int4 + IsoChunkMap.ChunkGridWidth / 2 + 1;

                    for (int int9 = int5; int9 < int7; int9++) {
                        for (int int10 = int6; int10 < int8; int10++) {
                            if (IsoWorld.instance.getMetaGrid().isValidChunk(int9, int10)) {
                                IsoChunk chunk = chunkMap1.LoadChunkForLater(int9, int10, int9 - int5, int10 - int6);
                                if (chunk != null && chunk.bLoaded) {
                                    cell2.setCacheChunk(chunk, int2);
                                }
                            }
                        }
                    }

                    chunkMap1.SwapChunkBuffers();
                } finally {
                    IsoChunkMap.bSettingChunk.unlock();
                }

                this.stage = AddCoopPlayer.Stage.CheckMapLoading;
                break;
            case CheckMapLoading:
                IsoCell cell1 = IsoWorld.instance.CurrentCell;
                IsoChunkMap chunkMap0 = cell1.ChunkMap[this.player.PlayerIndex];
                chunkMap0.update();

                for (int int0 = 0; int0 < IsoChunkMap.ChunkGridWidth; int0++) {
                    for (int int1 = 0; int1 < IsoChunkMap.ChunkGridWidth; int1++) {
                        if (IsoWorld.instance.getMetaGrid().isValidChunk(chunkMap0.getWorldXMin() + int1, chunkMap0.getWorldYMin() + int0)
                            && chunkMap0.getChunk(int1, int0) == null) {
                            return;
                        }
                    }
                }

                IsoGridSquare square = cell1.getGridSquare((int)this.player.x, (int)this.player.y, (int)this.player.z);
                if (square != null && square.getRoom() != null) {
                    square.getRoom().def.setExplored(true);
                    square.getRoom().building.setAllExplored(true);
                }

                this.stage = GameClient.bClient ? AddCoopPlayer.Stage.SendPlayerConnect : AddCoopPlayer.Stage.AddToWorld;
                break;
            case SendPlayerConnect:
                ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
                PacketTypes.PacketType.AddCoopPlayer.doPacket(byteBufferWriter0);
                byteBufferWriter0.putByte((byte)2);
                byteBufferWriter0.putByte((byte)this.player.PlayerIndex);
                GameClient.instance.writePlayerConnectData(byteBufferWriter0, this.player);
                PacketTypes.PacketType.AddCoopPlayer.send(GameClient.connection);
                this.stage = AddCoopPlayer.Stage.ReceivePlayerConnect;
                break;
            case AddToWorld:
                IsoPlayer.players[this.player.PlayerIndex] = this.player;
                LosUtil.cachecleared[this.player.PlayerIndex] = true;
                this.player.updateLightInfo();
                IsoCell cell0 = IsoWorld.instance.CurrentCell;
                this.player.setCurrent(cell0.getGridSquare((int)this.player.x, (int)this.player.y, (int)this.player.z));
                this.player.updateUsername();
                this.player.setSceneCulled(false);
                if (cell0.isSafeToAdd()) {
                    cell0.getObjectList().add(this.player);
                } else {
                    cell0.getAddList().add(this.player);
                }

                this.player.getInventory().addItemsToProcessItems();
                LuaEventManager.triggerEvent("OnCreatePlayer", this.player.PlayerIndex, this.player);
                if (this.player.isAsleep()) {
                    UIManager.setFadeBeforeUI(this.player.PlayerIndex, true);
                    UIManager.FadeOut(this.player.PlayerIndex, 2.0);
                    UIManager.setFadeTime(this.player.PlayerIndex, 0.0);
                }

                this.stage = AddCoopPlayer.Stage.Finished;
                SoundManager.instance.stopMusic(IsoPlayer.DEATH_MUSIC_NAME);
        }
    }

    public boolean isFinished() {
        return this.stage == AddCoopPlayer.Stage.Finished;
    }

    public void accessGranted(int int0) {
        if (this.player.PlayerIndex == int0) {
            DebugLog.log("coop player=" + (int0 + 1) + "/4 access granted");
            this.stage = AddCoopPlayer.Stage.StartMapLoading;
        }
    }

    public void accessDenied(int int0, String string) {
        if (this.player.PlayerIndex == int0) {
            DebugLog.log("coop player=" + (int0 + 1) + "/4 access denied: " + string);
            IsoCell cell = IsoWorld.instance.CurrentCell;
            int int1 = this.player.PlayerIndex;
            IsoChunkMap chunkMap = cell.ChunkMap[int1];
            chunkMap.Unload();
            chunkMap.ignore = true;
            this.stage = AddCoopPlayer.Stage.Finished;
            LuaEventManager.triggerEvent("OnCoopJoinFailed", int0);
        }
    }

    public void receivePlayerConnect(int int0) {
        if (this.player.PlayerIndex == int0) {
            this.stage = AddCoopPlayer.Stage.AddToWorld;
            this.update();
        }
    }

    public boolean isLoadingThisSquare(int int6, int int7) {
        int int0 = (int)(this.player.x / 10.0F);
        int int1 = (int)(this.player.y / 10.0F);
        int int2 = int0 - IsoChunkMap.ChunkGridWidth / 2;
        int int3 = int1 - IsoChunkMap.ChunkGridWidth / 2;
        int int4 = int2 + IsoChunkMap.ChunkGridWidth;
        int int5 = int3 + IsoChunkMap.ChunkGridWidth;
        int6 /= 10;
        int7 /= 10;
        return int6 >= int2 && int6 < int4 && int7 >= int3 && int7 < int5;
    }

    public static enum Stage {
        Init,
        ReceiveClientConnect,
        StartMapLoading,
        CheckMapLoading,
        SendPlayerConnect,
        ReceivePlayerConnect,
        AddToWorld,
        Finished;
    }
}
