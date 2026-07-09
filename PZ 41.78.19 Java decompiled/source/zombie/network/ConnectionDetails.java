// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.SGlobalObjects;
import zombie.iso.Vector3;
import zombie.world.WorldDictionary;

public class ConnectionDetails {
    public static void write(UdpConnection udpConnection, ServerWorldDatabase.LogonResult logonResult, ByteBuffer byteBuffer) {
        try {
            writeServerDetails(byteBuffer, udpConnection, logonResult);
            writeGameMap(byteBuffer);
            if (SteamUtils.isSteamModeEnabled()) {
                writeWorkshopItems(byteBuffer);
            }

            writeMods(byteBuffer);
            writeStartLocation(byteBuffer);
            writeServerOptions(byteBuffer);
            writeSandboxOptions(byteBuffer);
            writeGameTime(byteBuffer);
            writeErosionMain(byteBuffer);
            writeGlobalObjects(byteBuffer);
            writeResetID(byteBuffer);
            writeBerries(byteBuffer);
            writeWorldDictionary(byteBuffer);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void parse(ByteBuffer byteBuffer) {
        ConnectionManager.log("receive-packet", "connection-details", null);
        Calendar calendar = Calendar.getInstance();
        ConnectToServerState connectToServerState = new ConnectToServerState(byteBuffer);
        connectToServerState.enter();
        MainScreenState.getInstance().setConnectToServerState(connectToServerState);
        DebugLog.General.println("LOGGED INTO : %d millisecond", calendar.getTimeInMillis() - GameClient.startAuth.getTimeInMillis());
    }

    private static void writeServerDetails(ByteBuffer byteBuffer, UdpConnection udpConnection, ServerWorldDatabase.LogonResult logonResult) {
        byteBuffer.put((byte)(udpConnection.isCoopHost ? 1 : 0));
        byteBuffer.putInt(ServerOptions.getInstance().getMaxPlayers());
        if (SteamUtils.isSteamModeEnabled() && CoopSlave.instance != null && !udpConnection.isCoopHost) {
            byteBuffer.put((byte)1);
            byteBuffer.putLong(CoopSlave.instance.hostSteamID);
            GameWindow.WriteString(byteBuffer, GameServer.ServerName);
        } else {
            byteBuffer.put((byte)0);
        }

        int int0 = udpConnection.playerIDs[0] / 4;
        byteBuffer.put((byte)int0);
        GameWindow.WriteString(byteBuffer, logonResult.accessLevel);
    }

    private static void writeGameMap(ByteBuffer byteBuffer) {
        GameWindow.WriteString(byteBuffer, GameServer.GameMap);
    }

    private static void writeWorkshopItems(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)GameServer.WorkshopItems.size());

        for (int int0 = 0; int0 < GameServer.WorkshopItems.size(); int0++) {
            byteBuffer.putLong(GameServer.WorkshopItems.get(int0));
            byteBuffer.putLong(GameServer.WorkshopTimeStamps[int0]);
        }
    }

    private static void writeMods(ByteBuffer byteBuffer) {
        ArrayList arrayList = new ArrayList();

        for (String string0 : GameServer.ServerMods) {
            String string1 = ZomboidFileSystem.instance.getModDir(string0);
            ChooseGameInfo.Mod mod0;
            if (string1 != null) {
                try {
                    mod0 = ChooseGameInfo.readModInfo(string1);
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                    mod0 = new ChooseGameInfo.Mod(string0);
                    mod0.setId(string0);
                    mod0.setName(string0);
                }
            } else {
                mod0 = new ChooseGameInfo.Mod(string0);
                mod0.setId(string0);
                mod0.setName(string0);
            }

            arrayList.add(mod0);
        }

        byteBuffer.putInt(arrayList.size());

        for (ChooseGameInfo.Mod mod1 : arrayList) {
            GameWindow.WriteString(byteBuffer, mod1.getId());
            GameWindow.WriteString(byteBuffer, mod1.getUrl());
            GameWindow.WriteString(byteBuffer, mod1.getName());
        }
    }

    private static void writeStartLocation(ByteBuffer byteBuffer) {
        Object object = null;
        Vector3 vector = ServerMap.instance.getStartLocation((ServerWorldDatabase.LogonResult)object);
        byteBuffer.putInt((int)vector.x);
        byteBuffer.putInt((int)vector.y);
        byteBuffer.putInt((int)vector.z);
    }

    private static void writeServerOptions(ByteBuffer byteBuffer) {
        byteBuffer.putInt(ServerOptions.instance.getPublicOptions().size());

        for (String string : ServerOptions.instance.getPublicOptions()) {
            GameWindow.WriteString(byteBuffer, string);
            GameWindow.WriteString(byteBuffer, ServerOptions.instance.getOption(string));
        }
    }

    private static void writeSandboxOptions(ByteBuffer byteBuffer) throws IOException {
        SandboxOptions.instance.save(byteBuffer);
    }

    private static void writeGameTime(ByteBuffer byteBuffer) throws IOException {
        GameTime.getInstance().saveToPacket(byteBuffer);
    }

    private static void writeErosionMain(ByteBuffer byteBuffer) {
        ErosionMain.getInstance().getConfig().save(byteBuffer);
    }

    private static void writeGlobalObjects(ByteBuffer byteBuffer) throws IOException {
        SGlobalObjects.saveInitialStateForClient(byteBuffer);
    }

    private static void writeResetID(ByteBuffer byteBuffer) {
        byteBuffer.putInt(GameServer.ResetID);
    }

    private static void writeBerries(ByteBuffer byteBuffer) {
        GameWindow.WriteString(byteBuffer, Core.getInstance().getPoisonousBerry());
        GameWindow.WriteString(byteBuffer, Core.getInstance().getPoisonousMushroom());
    }

    private static void writeWorldDictionary(ByteBuffer byteBuffer) throws IOException {
        WorldDictionary.saveDataForClient(byteBuffer);
    }
}
