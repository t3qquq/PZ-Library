// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.world.moddata;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.world.WorldDictionary;

public final class GlobalModData {
    public static final String SAVE_EXT = ".bin";
    public static final String SAVE_FILE = "global_mod_data";
    public static GlobalModData instance = new GlobalModData();
    private Map<String, KahluaTable> modData = new HashMap<>();
    private static final int BLOCK_SIZE = 524288;
    private static int LAST_BLOCK_SIZE = -1;

    private KahluaTable createModDataTable() {
        return LuaManager.platform.newTable();
    }

    public GlobalModData() {
        this.reset();
    }

    public void init() throws IOException {
        this.reset();
        this.load();
        LuaEventManager.triggerEvent("OnInitGlobalModData", WorldDictionary.isIsNewGame());
    }

    public void reset() {
        LAST_BLOCK_SIZE = -1;
        this.modData.clear();
    }

    public void collectTableNames(List<String> list) {
        list.clear();

        for (Entry entry : this.modData.entrySet()) {
            list.add((String)entry.getKey());
        }
    }

    public boolean exists(String string) {
        return this.modData.containsKey(string);
    }

    public KahluaTable getOrCreate(String string) {
        KahluaTable table = this.get(string);
        if (table == null) {
            table = this.create(string);
        }

        return table;
    }

    public KahluaTable get(String string) {
        return this.modData.get(string);
    }

    public String create() {
        String string = UUID.randomUUID().toString();
        this.create(string);
        return string;
    }

    public KahluaTable create(String string) {
        if (this.exists(string)) {
            DebugLog.log("GlobalModData -> Cannot create table '" + string + "', already exists. Returning null.");
            return null;
        } else {
            KahluaTable table = this.createModDataTable();
            this.modData.put(string, table);
            return table;
        }
    }

    public KahluaTable remove(String string) {
        return this.modData.remove(string);
    }

    public void add(String string, KahluaTable table) {
        this.modData.put(string, table);
    }

    public void transmit(String string) {
        KahluaTable table = this.get(string);
        if (table != null) {
            if (GameClient.bClient) {
                ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
                PacketTypes.PacketType.GlobalModData.doPacket(byteBufferWriter0);
                ByteBuffer byteBuffer0 = byteBufferWriter0.bb;

                try {
                    GameWindow.WriteString(byteBuffer0, string);
                    byteBuffer0.put((byte)1);
                    table.save(byteBuffer0);
                } catch (Exception exception0) {
                    exception0.printStackTrace();
                    GameClient.connection.cancelPacket();
                } finally {
                    PacketTypes.PacketType.GlobalModData.send(GameClient.connection);
                }
            } else if (GameServer.bServer) {
                try {
                    for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                        UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                        ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                        PacketTypes.PacketType.GlobalModData.doPacket(byteBufferWriter1);
                        ByteBuffer byteBuffer1 = byteBufferWriter1.bb;

                        try {
                            GameWindow.WriteString(byteBuffer1, string);
                            byteBuffer1.put((byte)1);
                            table.save(byteBuffer1);
                        } catch (Exception exception1) {
                            exception1.printStackTrace();
                            udpConnection.cancelPacket();
                        } finally {
                            PacketTypes.PacketType.GlobalModData.send(udpConnection);
                        }
                    }
                } catch (Exception exception2) {
                    DebugLog.log(exception2.getMessage());
                }
            }
        } else {
            DebugLog.log("GlobalModData -> cannot transmit moddata not found: " + string);
        }
    }

    public void receive(ByteBuffer byteBuffer) {
        try {
            String string = GameWindow.ReadString(byteBuffer);
            if (byteBuffer.get() != 1) {
                LuaEventManager.triggerEvent("OnReceiveGlobalModData", string, false);
                return;
            }

            KahluaTable table = this.createModDataTable();
            table.load(byteBuffer, 195);
            LuaEventManager.triggerEvent("OnReceiveGlobalModData", string, table);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void request(String string) {
        if (GameClient.bClient) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.GlobalModDataRequest.doPacket(byteBufferWriter);
            ByteBuffer byteBuffer = byteBufferWriter.bb;

            try {
                GameWindow.WriteString(byteBuffer, string);
            } catch (Exception exception) {
                exception.printStackTrace();
                GameClient.connection.cancelPacket();
            } finally {
                PacketTypes.PacketType.GlobalModDataRequest.send(GameClient.connection);
            }
        } else {
            DebugLog.log("GlobalModData -> can only request from Client.");
        }
    }

    public void receiveRequest(ByteBuffer byteBuffer0, UdpConnection udpConnection1) {
        String string = GameWindow.ReadString(byteBuffer0);
        KahluaTable table = this.get(string);
        if (table == null) {
            DebugLog.log("GlobalModData -> received request for non-existing table, table: " + string);
        }

        if (GameServer.bServer) {
            try {
                for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                    UdpConnection udpConnection0 = GameServer.udpEngine.connections.get(int0);
                    if (udpConnection0 == udpConnection1) {
                        ByteBufferWriter byteBufferWriter = udpConnection0.startPacket();
                        PacketTypes.PacketType.GlobalModData.doPacket(byteBufferWriter);
                        ByteBuffer byteBuffer1 = byteBufferWriter.bb;

                        try {
                            GameWindow.WriteString(byteBuffer1, string);
                            byteBuffer1.put((byte)(table != null ? 1 : 0));
                            if (table != null) {
                                table.save(byteBuffer1);
                            }
                        } catch (Exception exception0) {
                            exception0.printStackTrace();
                            udpConnection0.cancelPacket();
                        } finally {
                            PacketTypes.PacketType.GlobalModData.send(udpConnection0);
                        }
                    }
                }
            } catch (Exception exception1) {
                DebugLog.log(exception1.getMessage());
            }
        }
    }

    private static ByteBuffer ensureCapacity(ByteBuffer byteBuffer0) {
        if (byteBuffer0 == null) {
            LAST_BLOCK_SIZE = 1048576;
            return ByteBuffer.allocate(LAST_BLOCK_SIZE);
        } else {
            LAST_BLOCK_SIZE = byteBuffer0.capacity() + 524288;
            ByteBuffer byteBuffer1 = ByteBuffer.allocate(LAST_BLOCK_SIZE);
            return byteBuffer1.put(byteBuffer0.array(), 0, byteBuffer0.position());
        }
    }

    public void save() throws IOException {
        if (!Core.getInstance().isNoSave()) {
            try {
                DebugLog.log("Saving GlobalModData");
                ByteBuffer byteBuffer = ByteBuffer.allocate(LAST_BLOCK_SIZE == -1 ? 1048576 : LAST_BLOCK_SIZE);
                byteBuffer.putInt(195);
                byteBuffer.putInt(this.modData.size());
                int int0 = 0;

                for (Entry entry : this.modData.entrySet()) {
                    if (byteBuffer.capacity() - byteBuffer.position() < 4) {
                        int0 = byteBuffer.position();
                        ensureCapacity(byteBuffer);
                        byteBuffer.position(int0);
                    }

                    int int1 = byteBuffer.position();
                    byteBuffer.putInt(0);
                    int int2 = byteBuffer.position();

                    while (true) {
                        try {
                            int0 = byteBuffer.position();
                            GameWindow.WriteString(byteBuffer, (String)entry.getKey());
                            ((KahluaTable)entry.getValue()).save(byteBuffer);
                        } catch (BufferOverflowException bufferOverflowException) {
                            byteBuffer = ensureCapacity(byteBuffer);
                            byteBuffer.position(int0);
                            continue;
                        }

                        int int3 = byteBuffer.position();
                        byteBuffer.position(int1);
                        byteBuffer.putInt(int3 - int2);
                        byteBuffer.position(int3);
                        break;
                    }
                }

                byteBuffer.flip();
                File file0 = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("global_mod_data.tmp"));
                FileOutputStream fileOutputStream = new FileOutputStream(file0);
                fileOutputStream.getChannel().truncate(0L);
                fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
                fileOutputStream.flush();
                fileOutputStream.close();
                File file1 = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("global_mod_data.bin"));
                Files.copy(file0, file1);
                file0.delete();
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new IOException("Error saving GlobalModData.", exception);
            }
        }
    }

    public void load() throws IOException {
        if (!Core.getInstance().isNoSave()) {
            String string0 = ZomboidFileSystem.instance.getFileNameInCurrentSave("global_mod_data.bin");
            File file = new File(string0);
            if (!file.exists()) {
                if (!WorldDictionary.isIsNewGame()) {
                }
            } else {
                try {
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        DebugLog.log("Loading GlobalModData:" + string0);
                        this.modData.clear();
                        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
                        byteBuffer.clear();
                        int int0 = fileInputStream.read(byteBuffer.array());
                        byteBuffer.limit(int0);
                        int int1 = byteBuffer.getInt();
                        int int2 = byteBuffer.getInt();

                        for (int int3 = 0; int3 < int2; int3++) {
                            int int4 = byteBuffer.getInt();
                            String string1 = GameWindow.ReadString(byteBuffer);
                            KahluaTable table = this.createModDataTable();
                            table.load(byteBuffer, int1);
                            this.modData.put(string1, table);
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw new IOException("Error loading GlobalModData.", exception);
                }
            }
        }
    }
}
