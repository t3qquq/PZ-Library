// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.globalObjects;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoObject;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.util.Type;

public final class SGlobalObjectSystem extends GlobalObjectSystem {
    private static KahluaTable tempTable;
    protected int loadedWorldVersion = -1;
    protected final HashSet<String> modDataKeys = new HashSet<>();
    protected final HashSet<String> objectModDataKeys = new HashSet<>();
    protected final HashSet<String> objectSyncKeys = new HashSet<>();

    public SGlobalObjectSystem(String name) {
        super(name);
    }

    @Override
    protected GlobalObject makeObject(int int0, int int1, int int2) {
        return new SGlobalObject(this, int0, int1, int2);
    }

    public void setModDataKeys(KahluaTable keys) {
        this.modDataKeys.clear();
        if (keys != null) {
            KahluaTableIterator kahluaTableIterator = keys.iterator();

            while (kahluaTableIterator.advance()) {
                Object object = kahluaTableIterator.getValue();
                if (!(object instanceof String)) {
                    throw new IllegalArgumentException("expected string but got \"" + object + "\"");
                }

                this.modDataKeys.add((String)object);
            }
        }
    }

    public void setObjectModDataKeys(KahluaTable keys) {
        this.objectModDataKeys.clear();
        if (keys != null) {
            KahluaTableIterator kahluaTableIterator = keys.iterator();

            while (kahluaTableIterator.advance()) {
                Object object = kahluaTableIterator.getValue();
                if (!(object instanceof String)) {
                    throw new IllegalArgumentException("expected string but got \"" + object + "\"");
                }

                this.objectModDataKeys.add((String)object);
            }
        }
    }

    public void setObjectSyncKeys(KahluaTable keys) {
        this.objectSyncKeys.clear();
        if (keys != null) {
            KahluaTableIterator kahluaTableIterator = keys.iterator();

            while (kahluaTableIterator.advance()) {
                Object object = kahluaTableIterator.getValue();
                if (!(object instanceof String)) {
                    throw new IllegalArgumentException("expected string but got \"" + object + "\"");
                }

                this.objectSyncKeys.add((String)object);
            }
        }
    }

    public void update() {
    }

    public void chunkLoaded(int wx, int wy) {
        if (this.hasObjectsInChunk(wx, wy)) {
            Object object = this.modData.rawget("OnChunkLoaded");
            if (object == null) {
                throw new IllegalStateException("OnChunkLoaded method undefined for system '" + this.name + "'");
            } else {
                Double double0 = BoxedStaticValues.toDouble(wx);
                Double double1 = BoxedStaticValues.toDouble(wy);
                LuaManager.caller.pcall(LuaManager.thread, object, this.modData, double0, double1);
            }
        }
    }

    public void sendCommand(String command, KahluaTable args) {
        SGlobalObjectNetwork.sendServerCommand(this.name, command, args);
    }

    public void receiveClientCommand(String command, IsoPlayer playerObj, KahluaTable args) {
        Object object = this.modData.rawget("OnClientCommand");
        if (object == null) {
            throw new IllegalStateException("OnClientCommand method undefined for system '" + this.name + "'");
        } else {
            LuaManager.caller.pcall(LuaManager.thread, object, this.modData, command, playerObj, args);
        }
    }

    public void addGlobalObjectOnClient(SGlobalObject globalObject) throws IOException {
        if (globalObject == null) {
            throw new IllegalArgumentException("globalObject is null");
        } else if (globalObject.system != this) {
            throw new IllegalArgumentException("object not in this system");
        } else {
            SGlobalObjectNetwork.addGlobalObjectOnClient(globalObject);
        }
    }

    public void removeGlobalObjectOnClient(SGlobalObject globalObject) throws IOException {
        if (globalObject == null) {
            throw new IllegalArgumentException("globalObject is null");
        } else if (globalObject.system != this) {
            throw new IllegalArgumentException("object not in this system");
        } else {
            SGlobalObjectNetwork.removeGlobalObjectOnClient(globalObject);
        }
    }

    public void updateGlobalObjectOnClient(SGlobalObject globalObject) throws IOException {
        if (globalObject == null) {
            throw new IllegalArgumentException("globalObject is null");
        } else if (globalObject.system != this) {
            throw new IllegalArgumentException("object not in this system");
        } else {
            SGlobalObjectNetwork.updateGlobalObjectOnClient(globalObject);
        }
    }

    private String getFileName() {
        return ZomboidFileSystem.instance.getFileNameInCurrentSave("gos_" + this.name + ".bin");
    }

    public KahluaTable getInitialStateForClient() {
        Object object = this.modData.rawget("getInitialStateForClient");
        if (object == null) {
            throw new IllegalStateException("getInitialStateForClient method undefined for system '" + this.name + "'");
        } else {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, object, this.modData);
            return objects != null && objects[0].equals(Boolean.TRUE) && objects[1] instanceof KahluaTable ? (KahluaTable)objects[1] : null;
        }
    }

    public void OnIsoObjectChangedItself(IsoObject isoObject) {
        GlobalObject globalObject = this.getObjectAt(isoObject.getSquare().x, isoObject.getSquare().y, isoObject.getSquare().z);
        if (globalObject != null) {
            Object object = this.modData.rawget("OnIsoObjectChangedItself");
            if (object == null) {
                throw new IllegalStateException("OnIsoObjectChangedItself method undefined for system '" + this.name + "'");
            } else {
                LuaManager.caller.pcall(LuaManager.thread, object, this.modData, isoObject);
            }
        }
    }

    public int loadedWorldVersion() {
        return this.loadedWorldVersion;
    }

    public void load(ByteBuffer bb, int WorldVersion) throws IOException {
        boolean boolean0 = bb.get() == 0;
        if (!boolean0) {
            this.modData.load(bb, WorldVersion);
        }

        int int0 = bb.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = bb.getInt();
            int int3 = bb.getInt();
            byte byte0 = bb.get();
            SGlobalObject sGlobalObject = Type.tryCastTo(this.newObject(int2, int3, byte0), SGlobalObject.class);
            sGlobalObject.load(bb, WorldVersion);
        }

        this.loadedWorldVersion = WorldVersion;
    }

    public void save(ByteBuffer bb) throws IOException {
        if (tempTable == null) {
            tempTable = LuaManager.platform.newTable();
        }

        tempTable.wipe();
        KahluaTableIterator kahluaTableIterator = this.modData.iterator();

        while (kahluaTableIterator.advance()) {
            Object object = kahluaTableIterator.getKey();
            if (this.modDataKeys.contains(object)) {
                tempTable.rawset(object, this.modData.rawget(object));
            }
        }

        if (tempTable.isEmpty()) {
            bb.put((byte)0);
        } else {
            bb.put((byte)1);
            tempTable.save(bb);
        }

        bb.putInt(this.objects.size());

        for (int int0 = 0; int0 < this.objects.size(); int0++) {
            SGlobalObject sGlobalObject = Type.tryCastTo(this.objects.get(int0), SGlobalObject.class);
            sGlobalObject.save(bb);
        }
    }

    public void load() {
        File file = new File(this.getFileName());

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            synchronized (SliceY.SliceBufferLock) {
                ByteBuffer byteBuffer = SliceY.SliceBuffer;
                byteBuffer.clear();
                int int0 = bufferedInputStream.read(byteBuffer.array());
                byteBuffer.limit(int0);
                byte byte0 = byteBuffer.get();
                byte byte1 = byteBuffer.get();
                byte byte2 = byteBuffer.get();
                byte byte3 = byteBuffer.get();
                if (byte0 != 71 || byte1 != 76 || byte2 != 79 || byte3 != 83) {
                    throw new IOException("doesn't appear to be a GlobalObjectSystem file:" + file.getAbsolutePath());
                }

                int int1 = byteBuffer.getInt();
                if (int1 < 134) {
                    throw new IOException("invalid WorldVersion " + int1 + ": " + file.getAbsolutePath());
                }

                if (int1 > 195) {
                    throw new IOException("file is from a newer version " + int1 + " of the game: " + file.getAbsolutePath());
                }

                this.load(byteBuffer, int1);
            }
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
        }
    }

    public void save() {
        if (!Core.getInstance().isNoSave()) {
            if (!GameClient.bClient) {
                File file = new File(this.getFileName());

                try (
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                ) {
                    synchronized (SliceY.SliceBufferLock) {
                        ByteBuffer byteBuffer = SliceY.SliceBuffer;
                        byteBuffer.clear();
                        byteBuffer.put((byte)71);
                        byteBuffer.put((byte)76);
                        byteBuffer.put((byte)79);
                        byteBuffer.put((byte)83);
                        byteBuffer.putInt(195);
                        this.save(byteBuffer);
                        bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
                    }
                } catch (Throwable throwable) {
                    ExceptionLogger.logException(throwable);
                }
            }
        }
    }

    @Override
    public void Reset() {
        super.Reset();
        this.modDataKeys.clear();
        this.objectModDataKeys.clear();
        this.objectSyncKeys.clear();
    }
}
