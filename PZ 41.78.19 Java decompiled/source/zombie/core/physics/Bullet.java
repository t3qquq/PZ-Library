// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.physics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.vehicles.BaseVehicle;

public class Bullet {
    public static ByteBuffer cmdBuf;
    public static final byte TO_ADD_VEHICLE = 4;
    public static final byte TO_SCROLL_CHUNKMAP = 5;
    public static final byte TO_ACTIVATE_CHUNKMAP = 6;
    public static final byte TO_INIT_WORLD = 7;
    public static final byte TO_UPDATE_CHUNK = 8;
    public static final byte TO_DEBUG_DRAW_WORLD = 9;
    public static final byte TO_STEP_SIMULATION = 10;
    public static final byte TO_UPDATE_PLAYER_LIST = 12;
    public static final byte TO_END = -1;

    public static void init() {
        String string0 = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs.bullet"))) {
            DebugLog.log("***** Loading debug version of PZBullet");
            string0 = "d";
        }

        String string1 = "";
        if (GameServer.bServer && GameWindow.OSValidator.isUnix()) {
            string1 = "NoOpenGL";
        }

        if (System.getProperty("os.name").contains("OS X")) {
            System.loadLibrary("PZBullet");
        } else if (System.getProperty("sun.arch.data.model").equals("64")) {
            System.loadLibrary("PZBullet" + string1 + "64" + string0);
        } else {
            System.loadLibrary("PZBullet" + string1 + "32" + string0);
        }

        cmdBuf = ByteBuffer.allocateDirect(4096);
        cmdBuf.order(ByteOrder.LITTLE_ENDIAN);
    }

    private static native void ToBullet(ByteBuffer var0);

    public static void CatchToBullet(ByteBuffer byteBuffer) {
        try {
            MPStatistic.getInstance().Bullet.Start();
            ToBullet(byteBuffer);
            MPStatistic.getInstance().Bullet.End();
        } catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
        }
    }

    public static native void initWorld(int var0, int var1, boolean var2);

    public static native void destroyWorld();

    public static native void activateChunkMap(int var0, int var1, int var2, int var3);

    public static native void deactivateChunkMap(int var0);

    public static void initWorld(int int0, int int1, int int2, int int3, int int4) {
        MPStatistic.getInstance().Bullet.Start();
        initWorld(int0, int1, GameServer.bServer);
        activateChunkMap(0, int2, int3, int4);
        MPStatistic.getInstance().Bullet.End();
    }

    public static void updatePlayerList(ArrayList<IsoPlayer> arrayList) {
        cmdBuf.clear();
        cmdBuf.put((byte)12);
        cmdBuf.putShort((short)arrayList.size());

        for (IsoPlayer player : arrayList) {
            cmdBuf.putInt(player.OnlineID);
            cmdBuf.putInt((int)player.getX());
            cmdBuf.putInt((int)player.getY());
        }

        cmdBuf.put((byte)-1);
        cmdBuf.put((byte)-1);
        CatchToBullet(cmdBuf);
    }

    public static void beginUpdateChunk(IsoChunk chunk) {
        cmdBuf.clear();
        cmdBuf.put((byte)8);
        cmdBuf.putShort((short)chunk.wx);
        cmdBuf.putShort((short)chunk.wy);
    }

    public static void updateChunk(int int0, int int1, int int2, int int3, byte[] bytes) {
        cmdBuf.put((byte)int0);
        cmdBuf.put((byte)int1);
        cmdBuf.put((byte)int2);
        cmdBuf.put((byte)int3);

        for (int int4 = 0; int4 < int3; int4++) {
            cmdBuf.put(bytes[int4]);
        }
    }

    public static void endUpdateChunk() {
        if (cmdBuf.position() != 5) {
            cmdBuf.put((byte)-1);
            cmdBuf.put((byte)-1);
            CatchToBullet(cmdBuf);
        }
    }

    public static native void scrollChunkMap(int var0, int var1);

    public static void scrollChunkMapLeft(int int0) {
        MPStatistic.getInstance().Bullet.Start();
        scrollChunkMap(int0, 0);
        MPStatistic.getInstance().Bullet.End();
    }

    public static void scrollChunkMapRight(int int0) {
        MPStatistic.getInstance().Bullet.Start();
        scrollChunkMap(int0, 1);
        MPStatistic.getInstance().Bullet.End();
    }

    public static void scrollChunkMapUp(int int0) {
        MPStatistic.getInstance().Bullet.Start();
        scrollChunkMap(int0, 2);
        MPStatistic.getInstance().Bullet.End();
    }

    public static void scrollChunkMapDown(int int0) {
        MPStatistic.getInstance().Bullet.Start();
        scrollChunkMap(int0, 3);
        MPStatistic.getInstance().Bullet.End();
    }

    public static void setVehicleActive(BaseVehicle vehicle, boolean boolean0) {
        vehicle.isActive = boolean0;
        setVehicleActive(vehicle.getId(), boolean0);
    }

    public static int setVehicleStatic(BaseVehicle vehicle, boolean boolean0) {
        vehicle.isStatic = boolean0;
        return setVehicleStatic(vehicle.getId(), boolean0);
    }

    public static native void addVehicle(int var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, String var8);

    public static native void removeVehicle(int var0);

    public static native void controlVehicle(int var0, float var1, float var2, float var3);

    public static native void setVehicleActive(int var0, boolean var1);

    public static native void applyCentralForceToVehicle(int var0, float var1, float var2, float var3);

    public static native void applyTorqueToVehicle(int var0, float var1, float var2, float var3);

    public static native void teleportVehicle(int var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7);

    public static native void setTireInflation(int var0, int var1, float var2);

    public static native void setTireRemoved(int var0, int var1, boolean var2);

    public static native void stepSimulation(float var0, int var1, float var2);

    public static native int getVehicleCount();

    public static native int getVehiclePhysics(int var0, float[] var1);

    public static native int getOwnVehiclePhysics(int var0, float[] var1);

    public static native int setOwnVehiclePhysics(int var0, float[] var1);

    public static native int setVehicleParams(int var0, float[] var1);

    public static native int setVehicleMass(int var0, float var1);

    public static native int getObjectPhysics(float[] var0);

    public static native void createServerCell(int var0, int var1);

    public static native void removeServerCell(int var0, int var1);

    public static native int addPhysicsObject(float var0, float var1);

    public static native void defineVehicleScript(String var0, float[] var1);

    public static native void setVehicleVelocityMultiplier(int var0, float var1, float var2);

    public static native int setVehicleStatic(int var0, boolean var1);

    public static native int addHingeConstraint(int var0, int var1, float var2, float var3, float var4, float var5, float var6, float var7);

    public static native int addPointConstraint(int var0, int var1, float var2, float var3, float var4, float var5, float var6, float var7);

    public static native int addRopeConstraint(int var0, int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

    public static native void removeConstraint(int var0);
}
