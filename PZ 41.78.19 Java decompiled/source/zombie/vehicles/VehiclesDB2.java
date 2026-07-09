// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.WorldStreamer;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.ZombiePopulationRenderer;
import zombie.util.ByteBufferBackedInputStream;
import zombie.util.ByteBufferOutputStream;
import zombie.util.PZSQLUtils;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.Type;

public final class VehiclesDB2 {
    public static final int INVALID_ID = -1;
    private static final int MIN_ID = 1;
    public static final VehiclesDB2 instance = new VehiclesDB2();
    private static final ThreadLocal<ByteBuffer> TL_SliceBuffer = ThreadLocal.withInitial(() -> ByteBuffer.allocate(32768));
    private static final ThreadLocal<byte[]> TL_Bytes = ThreadLocal.withInitial(() -> new byte[1024]);
    private final VehiclesDB2.MainThread m_main = new VehiclesDB2.MainThread();
    private final VehiclesDB2.WorldStreamerThread m_worldStreamer = new VehiclesDB2.WorldStreamerThread();

    public void init() {
        this.m_worldStreamer.m_store.init(this.m_main.m_usedIDs, this.m_main.m_seenChunks);
    }

    public void Reset() {
        assert WorldStreamer.instance.worldStreamer == null;

        this.updateWorldStreamer();

        for (VehiclesDB2.QueueItem queueItem = this.m_main.m_queue.poll(); queueItem != null; queueItem = this.m_main.m_queue.poll()) {
            queueItem.release();
        }

        this.m_main.Reset();
        this.m_worldStreamer.Reset();
    }

    public void updateMain() throws IOException {
        this.m_main.update();
    }

    public void updateWorldStreamer() {
        this.m_worldStreamer.update();
    }

    public void setForceSave() {
        this.m_main.m_forceSave = true;
    }

    public void renderDebug(ZombiePopulationRenderer var1) {
    }

    public void setChunkSeen(int int0, int int1) {
        this.m_main.setChunkSeen(int0, int1);
    }

    public boolean isChunkSeen(int int0, int int1) {
        return this.m_main.isChunkSeen(int0, int1);
    }

    public void setVehicleLoaded(BaseVehicle vehicle) {
        this.m_main.setVehicleLoaded(vehicle);
    }

    public void setVehicleUnloaded(BaseVehicle vehicle) {
        this.m_main.setVehicleUnloaded(vehicle);
    }

    public boolean isVehicleLoaded(BaseVehicle vehicle) {
        return this.m_main.m_loadedIDs.contains(vehicle.sqlID);
    }

    public void loadChunkMain(IsoChunk chunk) {
        this.m_main.loadChunk(chunk);
    }

    public void loadChunk(IsoChunk chunk) throws IOException {
        this.m_worldStreamer.loadChunk(chunk);
    }

    public void unloadChunk(IsoChunk chunk) {
        if (Thread.currentThread() != WorldStreamer.instance.worldStreamer) {
            boolean boolean0 = true;
        }

        this.m_worldStreamer.unloadChunk(chunk);
    }

    public void addVehicle(BaseVehicle vehicle) {
        try {
            this.m_main.addVehicle(vehicle);
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public void removeVehicle(BaseVehicle vehicle) {
        this.m_main.removeVehicle(vehicle);
    }

    public void updateVehicle(BaseVehicle vehicle) {
        try {
            this.m_main.updateVehicle(vehicle);
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public void updateVehicleAndTrailer(BaseVehicle vehicle0) {
        if (vehicle0 != null) {
            this.updateVehicle(vehicle0);
            BaseVehicle vehicle1 = vehicle0.getVehicleTowing();
            if (vehicle1 != null) {
                this.updateVehicle(vehicle1);
            }
        }
    }

    public void importPlayersFromOldDB(VehiclesDB2.IImportPlayerFromOldDB iImportPlayerFromOldDB) {
        VehiclesDB2.SQLStore sQLStore = Type.tryCastTo(this.m_worldStreamer.m_store, VehiclesDB2.SQLStore.class);
        if (sQLStore != null && sQLStore.m_conn != null) {
            try {
                label100: {
                    DatabaseMetaData databaseMetaData = sQLStore.m_conn.getMetaData();

                    try (ResultSet resultSet0 = databaseMetaData.getTables(null, null, "localPlayers", null)) {
                        if (resultSet0.next()) {
                            break label100;
                        }
                    }

                    return;
                }
            } catch (Exception exception0) {
                ExceptionLogger.logException(exception0);
                return;
            }

            String string0 = "SELECT id, name, wx, wy, x, y, z, worldversion, data, isDead FROM localPlayers";

            try (PreparedStatement preparedStatement = sQLStore.m_conn.prepareStatement(string0)) {
                ResultSet resultSet1 = preparedStatement.executeQuery();

                while (resultSet1.next()) {
                    int int0 = resultSet1.getInt(1);
                    String string1 = resultSet1.getString(2);
                    int int1 = resultSet1.getInt(3);
                    int int2 = resultSet1.getInt(4);
                    float float0 = resultSet1.getFloat(5);
                    float float1 = resultSet1.getFloat(6);
                    float float2 = resultSet1.getFloat(7);
                    int int3 = resultSet1.getInt(8);
                    byte[] bytes = resultSet1.getBytes(9);
                    boolean boolean0 = resultSet1.getBoolean(10);
                    iImportPlayerFromOldDB.accept(int0, string1, int1, int2, float0, float1, float2, int3, bytes, boolean0);
                }
            } catch (Exception exception1) {
                ExceptionLogger.logException(exception1);
            }

            try {
                Statement statement = sQLStore.m_conn.createStatement();
                statement.executeUpdate("DROP TABLE localPlayers");
                statement.executeUpdate("DROP TABLE networkPlayers");
                sQLStore.m_conn.commit();
            } catch (Exception exception2) {
                ExceptionLogger.logException(exception2);
            }
        }
    }

    public interface IImportPlayerFromOldDB {
        void accept(int var1, String var2, int var3, int var4, float var5, float var6, float var7, int var8, byte[] var9, boolean var10);
    }

    private abstract static class IVehicleStore {
        abstract void init(TIntHashSet var1, TIntHashSet var2);

        abstract void Reset();

        abstract void loadChunk(IsoChunk var1, VehiclesDB2.ThrowingBiConsumer<IsoChunk, VehiclesDB2.VehicleBuffer, IOException> var2) throws IOException;

        abstract void loadChunk(int var1, int var2, VehiclesDB2.ThrowingConsumer<VehiclesDB2.VehicleBuffer, IOException> var3) throws IOException;

        abstract void updateVehicle(VehiclesDB2.VehicleBuffer var1);

        abstract void removeVehicle(int var1);
    }

    private static final class MainThread {
        final TIntHashSet m_seenChunks = new TIntHashSet();
        final TIntHashSet m_usedIDs = new TIntHashSet();
        final TIntHashSet m_loadedIDs = new TIntHashSet();
        boolean m_forceSave = false;
        final ConcurrentLinkedQueue<VehiclesDB2.QueueItem> m_queue = new ConcurrentLinkedQueue<>();

        MainThread() {
            this.m_seenChunks.setAutoCompactionFactor(0.0F);
            this.m_usedIDs.setAutoCompactionFactor(0.0F);
            this.m_loadedIDs.setAutoCompactionFactor(0.0F);
        }

        void Reset() {
            this.m_seenChunks.clear();
            this.m_usedIDs.clear();
            this.m_loadedIDs.clear();

            assert this.m_queue.isEmpty();

            this.m_queue.clear();
            this.m_forceSave = false;
        }

        void update() throws IOException {
            if (!GameClient.bClient && !GameServer.bServer && this.m_forceSave) {
                this.m_forceSave = false;

                for (int int0 = 0; int0 < 4; int0++) {
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player != null && player.getVehicle() != null && player.getVehicle().isEngineRunning()) {
                        this.updateVehicle(player.getVehicle());
                        BaseVehicle vehicle = player.getVehicle().getVehicleTowing();
                        if (vehicle != null) {
                            this.updateVehicle(vehicle);
                        }
                    }
                }
            }

            for (VehiclesDB2.QueueItem queueItem = this.m_queue.poll(); queueItem != null; queueItem = this.m_queue.poll()) {
                try {
                    queueItem.processMain();
                } finally {
                    queueItem.release();
                }
            }
        }

        void setChunkSeen(int int1, int int2) {
            int int0 = int2 << 16 | int1;
            this.m_seenChunks.add(int0);
        }

        boolean isChunkSeen(int int1, int int2) {
            int int0 = int2 << 16 | int1;
            return this.m_seenChunks.contains(int0);
        }

        int allocateID() {
            synchronized (this.m_usedIDs) {
                for (int int0 = 1; int0 < Integer.MAX_VALUE; int0++) {
                    if (!this.m_usedIDs.contains(int0)) {
                        this.m_usedIDs.add(int0);
                        return int0;
                    }
                }
            }

            throw new RuntimeException("ran out of unused vehicle ids");
        }

        void setVehicleLoaded(BaseVehicle vehicle) {
            if (vehicle.sqlID == -1) {
                vehicle.sqlID = this.allocateID();
            }

            assert !this.m_loadedIDs.contains(vehicle.sqlID);

            this.m_loadedIDs.add(vehicle.sqlID);
        }

        void setVehicleUnloaded(BaseVehicle vehicle) {
            if (vehicle.sqlID != -1) {
                this.m_loadedIDs.remove(vehicle.sqlID);
            }
        }

        void addVehicle(BaseVehicle vehicle) throws IOException {
            if (vehicle.sqlID == -1) {
                vehicle.sqlID = this.allocateID();
            }

            VehiclesDB2.QueueAddVehicle queueAddVehicle = VehiclesDB2.QueueAddVehicle.s_pool.alloc();
            queueAddVehicle.init(vehicle);
            VehiclesDB2.instance.m_worldStreamer.m_queue.add(queueAddVehicle);
        }

        void removeVehicle(BaseVehicle vehicle) {
            VehiclesDB2.QueueRemoveVehicle queueRemoveVehicle = VehiclesDB2.QueueRemoveVehicle.s_pool.alloc();
            queueRemoveVehicle.init(vehicle);
            VehiclesDB2.instance.m_worldStreamer.m_queue.add(queueRemoveVehicle);
        }

        void updateVehicle(BaseVehicle vehicle) throws IOException {
            if (vehicle.sqlID == -1) {
                vehicle.sqlID = this.allocateID();
            }

            VehiclesDB2.QueueUpdateVehicle queueUpdateVehicle = VehiclesDB2.QueueUpdateVehicle.s_pool.alloc();
            queueUpdateVehicle.init(vehicle);
            VehiclesDB2.instance.m_worldStreamer.m_queue.add(queueUpdateVehicle);
        }

        void loadChunk(IsoChunk chunk) {
            VehiclesDB2.QueueLoadChunk queueLoadChunk = VehiclesDB2.QueueLoadChunk.s_pool.alloc();
            queueLoadChunk.init(chunk.wx, chunk.wy);
            chunk.m_loadVehiclesObject = queueLoadChunk;
            VehiclesDB2.instance.m_worldStreamer.m_queue.add(queueLoadChunk);
        }
    }

    private static class MemoryStore extends VehiclesDB2.IVehicleStore {
        final TIntObjectHashMap<VehiclesDB2.VehicleBuffer> m_IDToVehicle = new TIntObjectHashMap<>();
        final TIntObjectHashMap<ArrayList<VehiclesDB2.VehicleBuffer>> m_ChunkToVehicles = new TIntObjectHashMap<>();

        @Override
        void init(TIntHashSet tIntHashSet0, TIntHashSet tIntHashSet1) {
            tIntHashSet0.clear();
            tIntHashSet1.clear();
        }

        @Override
        void Reset() {
            this.m_IDToVehicle.clear();
            this.m_ChunkToVehicles.clear();
        }

        @Override
        void loadChunk(IsoChunk chunk, VehiclesDB2.ThrowingBiConsumer<IsoChunk, VehiclesDB2.VehicleBuffer, IOException> throwingBiConsumer) throws IOException {
            int int0 = chunk.wy << 16 | chunk.wx;
            ArrayList arrayList = this.m_ChunkToVehicles.get(int0);
            if (arrayList != null) {
                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    VehiclesDB2.VehicleBuffer vehicleBuffer = (VehiclesDB2.VehicleBuffer)arrayList.get(int1);
                    vehicleBuffer.m_bb.rewind();
                    boolean boolean0 = vehicleBuffer.m_bb.get() == 1;
                    int int2 = vehicleBuffer.m_bb.getInt();
                    throwingBiConsumer.accept(chunk, vehicleBuffer);
                }
            }
        }

        @Override
        void loadChunk(int int1, int int2, VehiclesDB2.ThrowingConsumer<VehiclesDB2.VehicleBuffer, IOException> throwingConsumer) throws IOException {
            int int0 = int2 << 16 | int1;
            ArrayList arrayList = this.m_ChunkToVehicles.get(int0);
            if (arrayList != null) {
                for (int int3 = 0; int3 < arrayList.size(); int3++) {
                    VehiclesDB2.VehicleBuffer vehicleBuffer = (VehiclesDB2.VehicleBuffer)arrayList.get(int3);
                    vehicleBuffer.m_bb.rewind();
                    boolean boolean0 = vehicleBuffer.m_bb.get() == 1;
                    int int4 = vehicleBuffer.m_bb.getInt();
                    throwingConsumer.accept(vehicleBuffer);
                }
            }
        }

        @Override
        void updateVehicle(VehiclesDB2.VehicleBuffer vehicleBuffer0) {
            assert vehicleBuffer0.m_id >= 1;

            synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
                assert VehiclesDB2.instance.m_main.m_usedIDs.contains(vehicleBuffer0.m_id);
            }

            vehicleBuffer0.m_bb.rewind();
            VehiclesDB2.VehicleBuffer vehicleBuffer1 = this.m_IDToVehicle.get(vehicleBuffer0.m_id);
            if (vehicleBuffer1 == null) {
                vehicleBuffer1 = new VehiclesDB2.VehicleBuffer();
                vehicleBuffer1.m_id = vehicleBuffer0.m_id;
                this.m_IDToVehicle.put(vehicleBuffer0.m_id, vehicleBuffer1);
            } else {
                int int0 = vehicleBuffer1.m_wy << 16 | vehicleBuffer1.m_wx;
                this.m_ChunkToVehicles.get(int0).remove(vehicleBuffer1);
            }

            vehicleBuffer1.m_wx = vehicleBuffer0.m_wx;
            vehicleBuffer1.m_wy = vehicleBuffer0.m_wy;
            vehicleBuffer1.m_x = vehicleBuffer0.m_x;
            vehicleBuffer1.m_y = vehicleBuffer0.m_y;
            vehicleBuffer1.m_WorldVersion = vehicleBuffer0.m_WorldVersion;
            vehicleBuffer1.setBytes(vehicleBuffer0.m_bb);
            int int1 = vehicleBuffer1.m_wy << 16 | vehicleBuffer1.m_wx;
            if (this.m_ChunkToVehicles.get(int1) == null) {
                this.m_ChunkToVehicles.put(int1, new ArrayList<>());
            }

            this.m_ChunkToVehicles.get(int1).add(vehicleBuffer1);
        }

        @Override
        void removeVehicle(int int0) {
            VehiclesDB2.VehicleBuffer vehicleBuffer = this.m_IDToVehicle.remove(int0);
            if (vehicleBuffer != null) {
                int int1 = vehicleBuffer.m_wy << 16 | vehicleBuffer.m_wx;
                this.m_ChunkToVehicles.get(int1).remove(vehicleBuffer);
            }
        }
    }

    private static final class QueueAddVehicle extends VehiclesDB2.QueueItem {
        static final Pool<VehiclesDB2.QueueAddVehicle> s_pool = new Pool<>(VehiclesDB2.QueueAddVehicle::new);
        final VehiclesDB2.VehicleBuffer m_vehicleBuffer = new VehiclesDB2.VehicleBuffer();

        void init(BaseVehicle vehicle) throws IOException {
            this.m_vehicleBuffer.set(vehicle);
        }

        @Override
        void processMain() {
        }

        @Override
        void processWorldStreamer() {
            VehiclesDB2.instance.m_worldStreamer.m_store.updateVehicle(this.m_vehicleBuffer);
        }
    }

    private abstract static class QueueItem extends PooledObject {
        abstract void processMain();

        abstract void processWorldStreamer();
    }

    private static class QueueLoadChunk extends VehiclesDB2.QueueItem {
        static final Pool<VehiclesDB2.QueueLoadChunk> s_pool = new Pool<>(VehiclesDB2.QueueLoadChunk::new);
        int m_wx;
        int m_wy;
        final ArrayList<BaseVehicle> m_vehicles = new ArrayList<>();
        IsoGridSquare m_dummySquare;

        void init(int int0, int int1) {
            this.m_wx = int0;
            this.m_wy = int1;
            this.m_vehicles.clear();
            if (this.m_dummySquare == null) {
                this.m_dummySquare = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, null, 0, 0, 0);
            }
        }

        @Override
        void processMain() {
            IsoChunk chunk = ServerMap.instance.getChunk(this.m_wx, this.m_wy);
            if (chunk == null) {
                this.m_vehicles.clear();
            } else if (chunk.m_loadVehiclesObject != this) {
                this.m_vehicles.clear();
            } else {
                chunk.m_loadVehiclesObject = null;

                for (int int0 = 0; int0 < this.m_vehicles.size(); int0++) {
                    BaseVehicle vehicle = this.m_vehicles.get(int0);
                    IsoGridSquare square = chunk.getGridSquare((int)(vehicle.x - chunk.wx * 10), (int)(vehicle.y - chunk.wy * 10), 0);
                    vehicle.setSquare(square);
                    vehicle.setCurrent(square);
                    vehicle.chunk = chunk;
                    if (chunk.jobType == IsoChunk.JobType.SoftReset) {
                        vehicle.softReset();
                    }

                    if (!vehicle.addedToWorld && VehiclesDB2.instance.isVehicleLoaded(vehicle)) {
                        vehicle.removeFromSquare();
                        this.m_vehicles.remove(int0);
                        int0--;
                    } else {
                        chunk.vehicles.add(vehicle);
                        if (!vehicle.addedToWorld) {
                            vehicle.addToWorld();
                        }
                    }
                }

                this.m_vehicles.clear();
            }
        }

        @Override
        void processWorldStreamer() {
            try {
                VehiclesDB2.instance.m_worldStreamer.m_store.loadChunk(this.m_wx, this.m_wy, this::vehicleLoaded);
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }

        void vehicleLoaded(VehiclesDB2.VehicleBuffer vehicleBuffer) throws IOException {
            assert vehicleBuffer.m_id >= 1;

            int int0 = (int)(vehicleBuffer.m_x - this.m_wx * 10);
            int int1 = (int)(vehicleBuffer.m_y - this.m_wy * 10);
            this.m_dummySquare.x = int0;
            this.m_dummySquare.y = int1;
            IsoGridSquare square = this.m_dummySquare;
            BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
            vehicle.setSquare(square);
            vehicle.setCurrent(square);

            try {
                vehicle.load(vehicleBuffer.m_bb, vehicleBuffer.m_WorldVersion);
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                DebugLog.General.error("vehicle %d is being deleted because an error occurred loading it", vehicleBuffer.m_id);
                VehiclesDB2.instance.m_worldStreamer.m_store.removeVehicle(vehicleBuffer.m_id);
                return;
            }

            vehicle.sqlID = vehicleBuffer.m_id;
            this.m_vehicles.add(vehicle);
        }
    }

    private static class QueueRemoveVehicle extends VehiclesDB2.QueueItem {
        static final Pool<VehiclesDB2.QueueRemoveVehicle> s_pool = new Pool<>(VehiclesDB2.QueueRemoveVehicle::new);
        int m_id;

        void init(BaseVehicle vehicle) {
            this.m_id = vehicle.sqlID;
        }

        @Override
        void processMain() {
        }

        @Override
        void processWorldStreamer() {
            VehiclesDB2.instance.m_worldStreamer.m_store.removeVehicle(this.m_id);
        }
    }

    private static final class QueueUpdateVehicle extends VehiclesDB2.QueueItem {
        static final Pool<VehiclesDB2.QueueUpdateVehicle> s_pool = new Pool<>(VehiclesDB2.QueueUpdateVehicle::new);
        final VehiclesDB2.VehicleBuffer m_vehicleBuffer = new VehiclesDB2.VehicleBuffer();

        void init(BaseVehicle vehicle) throws IOException {
            this.m_vehicleBuffer.set(vehicle);
        }

        @Override
        void processMain() {
        }

        @Override
        void processWorldStreamer() {
            VehiclesDB2.instance.m_worldStreamer.m_store.updateVehicle(this.m_vehicleBuffer);
        }
    }

    private static final class SQLStore extends VehiclesDB2.IVehicleStore {
        Connection m_conn = null;
        final VehiclesDB2.VehicleBuffer m_vehicleBuffer = new VehiclesDB2.VehicleBuffer();

        @Override
        void init(TIntHashSet tIntHashSet0, TIntHashSet tIntHashSet1) {
            tIntHashSet0.clear();
            tIntHashSet1.clear();
            if (!Core.getInstance().isNoSave()) {
                this.create();

                try {
                    this.initUsedIDs(tIntHashSet0, tIntHashSet1);
                } catch (SQLException sQLException) {
                    ExceptionLogger.logException(sQLException);
                }
            }
        }

        @Override
        void Reset() {
            if (this.m_conn != null) {
                try {
                    this.m_conn.close();
                } catch (SQLException sQLException) {
                    ExceptionLogger.logException(sQLException);
                }

                this.m_conn = null;
            }
        }

        @Override
        void loadChunk(IsoChunk chunk, VehiclesDB2.ThrowingBiConsumer<IsoChunk, VehiclesDB2.VehicleBuffer, IOException> throwingBiConsumer) throws IOException {
            if (this.m_conn != null && chunk != null) {
                String string = "SELECT id, x, y, data, worldversion FROM vehicles WHERE wx=? AND wy=?";

                try (PreparedStatement preparedStatement = this.m_conn.prepareStatement(string)) {
                    preparedStatement.setInt(1, chunk.wx);
                    preparedStatement.setInt(2, chunk.wy);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        this.m_vehicleBuffer.m_id = resultSet.getInt(1);
                        this.m_vehicleBuffer.m_wx = chunk.wx;
                        this.m_vehicleBuffer.m_wy = chunk.wy;
                        this.m_vehicleBuffer.m_x = resultSet.getFloat(2);
                        this.m_vehicleBuffer.m_y = resultSet.getFloat(3);
                        InputStream inputStream = resultSet.getBinaryStream(4);
                        this.m_vehicleBuffer.setBytes(inputStream);
                        this.m_vehicleBuffer.m_WorldVersion = resultSet.getInt(5);
                        boolean boolean0 = this.m_vehicleBuffer.m_bb.get() != 0;
                        byte byte0 = this.m_vehicleBuffer.m_bb.get();
                        if (byte0 == IsoObject.getFactoryVehicle().getClassID() && boolean0) {
                            throwingBiConsumer.accept(chunk, this.m_vehicleBuffer);
                        }
                    }
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }
            }
        }

        @Override
        void loadChunk(int int0, int int1, VehiclesDB2.ThrowingConsumer<VehiclesDB2.VehicleBuffer, IOException> throwingConsumer) throws IOException {
            if (this.m_conn != null) {
                String string = "SELECT id, x, y, data, worldversion FROM vehicles WHERE wx=? AND wy=?";

                try (PreparedStatement preparedStatement = this.m_conn.prepareStatement(string)) {
                    preparedStatement.setInt(1, int0);
                    preparedStatement.setInt(2, int1);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        this.m_vehicleBuffer.m_id = resultSet.getInt(1);
                        this.m_vehicleBuffer.m_wx = int0;
                        this.m_vehicleBuffer.m_wy = int1;
                        this.m_vehicleBuffer.m_x = resultSet.getFloat(2);
                        this.m_vehicleBuffer.m_y = resultSet.getFloat(3);
                        InputStream inputStream = resultSet.getBinaryStream(4);
                        this.m_vehicleBuffer.setBytes(inputStream);
                        this.m_vehicleBuffer.m_WorldVersion = resultSet.getInt(5);
                        boolean boolean0 = this.m_vehicleBuffer.m_bb.get() != 0;
                        byte byte0 = this.m_vehicleBuffer.m_bb.get();
                        if (byte0 == IsoObject.getFactoryVehicle().getClassID() && boolean0) {
                            throwingConsumer.accept(this.m_vehicleBuffer);
                        }
                    }
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }
            }
        }

        @Override
        void updateVehicle(VehiclesDB2.VehicleBuffer vehicleBuffer) {
            if (this.m_conn != null) {
                assert vehicleBuffer.m_id >= 1;

                synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
                    assert VehiclesDB2.instance.m_main.m_usedIDs.contains(vehicleBuffer.m_id);
                }

                try {
                    if (this.isInDB(vehicleBuffer.m_id)) {
                        this.updateDB(vehicleBuffer);
                    } else {
                        this.addToDB(vehicleBuffer);
                    }
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                    this.rollback();
                }
            }
        }

        boolean isInDB(int int0) throws SQLException {
            String string = "SELECT 1 FROM vehicles WHERE id=?";

            boolean boolean0;
            try (PreparedStatement preparedStatement = this.m_conn.prepareStatement(string)) {
                preparedStatement.setInt(1, int0);
                ResultSet resultSet = preparedStatement.executeQuery();
                boolean0 = resultSet.next();
            }

            return boolean0;
        }

        void addToDB(VehiclesDB2.VehicleBuffer vehicleBuffer) throws SQLException {
            String string = "INSERT INTO vehicles(wx,wy,x,y,worldversion,data,id) VALUES(?,?,?,?,?,?,?)";

            try {
                try (PreparedStatement preparedStatement = this.m_conn.prepareStatement(string)) {
                    preparedStatement.setInt(1, vehicleBuffer.m_wx);
                    preparedStatement.setInt(2, vehicleBuffer.m_wy);
                    preparedStatement.setFloat(3, vehicleBuffer.m_x);
                    preparedStatement.setFloat(4, vehicleBuffer.m_y);
                    preparedStatement.setInt(5, vehicleBuffer.m_WorldVersion);
                    ByteBuffer byteBuffer = vehicleBuffer.m_bb;
                    byteBuffer.rewind();
                    preparedStatement.setBinaryStream(6, new ByteBufferBackedInputStream(byteBuffer), byteBuffer.remaining());
                    preparedStatement.setInt(7, vehicleBuffer.m_id);
                    int int0 = preparedStatement.executeUpdate();
                    this.m_conn.commit();
                }
            } catch (Exception exception) {
                this.rollback();
                throw exception;
            }
        }

        void updateDB(VehiclesDB2.VehicleBuffer vehicleBuffer) throws SQLException {
            String string = "UPDATE vehicles SET wx = ?, wy = ?, x = ?, y = ?, worldversion = ?, data = ? WHERE id=?";

            try {
                try (PreparedStatement preparedStatement = this.m_conn.prepareStatement(string)) {
                    preparedStatement.setInt(1, vehicleBuffer.m_wx);
                    preparedStatement.setInt(2, vehicleBuffer.m_wy);
                    preparedStatement.setFloat(3, vehicleBuffer.m_x);
                    preparedStatement.setFloat(4, vehicleBuffer.m_y);
                    preparedStatement.setInt(5, vehicleBuffer.m_WorldVersion);
                    ByteBuffer byteBuffer = vehicleBuffer.m_bb;
                    byteBuffer.rewind();
                    preparedStatement.setBinaryStream(6, new ByteBufferBackedInputStream(byteBuffer), byteBuffer.remaining());
                    preparedStatement.setInt(7, vehicleBuffer.m_id);
                    int int0 = preparedStatement.executeUpdate();
                    this.m_conn.commit();
                }
            } catch (Exception exception) {
                this.rollback();
                throw exception;
            }
        }

        @Override
        void removeVehicle(int int0) {
            if (this.m_conn != null && int0 >= 1) {
                String string = "DELETE FROM vehicles WHERE id=?";

                try (PreparedStatement preparedStatement = this.m_conn.prepareStatement(string)) {
                    preparedStatement.setInt(1, int0);
                    int int1 = preparedStatement.executeUpdate();
                    this.m_conn.commit();
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                    this.rollback();
                }
            }
        }

        void create() {
            String string = ZomboidFileSystem.instance.getCurrentSaveDir();
            File file0 = new File(string);
            if (!file0.exists()) {
                file0.mkdirs();
            }

            File file1 = new File(string + File.separator + "vehicles.db");
            file1.setReadable(true, false);
            file1.setExecutable(true, false);
            file1.setWritable(true, false);
            if (!file1.exists()) {
                try {
                    file1.createNewFile();
                    this.m_conn = PZSQLUtils.getConnection(file1.getAbsolutePath());
                    Statement statement0 = this.m_conn.createStatement();
                    statement0.executeUpdate(
                        "CREATE TABLE vehicles (id   INTEGER PRIMARY KEY NOT NULL,wx    INTEGER,wy    INTEGER,x    FLOAT,y    FLOAT,worldversion    INTEGER,data BLOB);"
                    );
                    statement0.executeUpdate("CREATE INDEX ivwx ON vehicles (wx);");
                    statement0.executeUpdate("CREATE INDEX ivwy ON vehicles (wy);");
                    statement0.close();
                } catch (Exception exception0) {
                    ExceptionLogger.logException(exception0);
                    DebugLog.log("failed to create vehicles database");
                    System.exit(1);
                }
            }

            if (this.m_conn == null) {
                try {
                    this.m_conn = PZSQLUtils.getConnection(file1.getAbsolutePath());
                } catch (Exception exception1) {
                    DebugLog.log("failed to create vehicles database");
                    ExceptionLogger.logException(exception1);
                    System.exit(1);
                }
            }

            try {
                Statement statement1 = this.m_conn.createStatement();
                statement1.executeQuery("PRAGMA JOURNAL_MODE=TRUNCATE;");
                statement1.close();
            } catch (Exception exception2) {
                ExceptionLogger.logException(exception2);
                System.exit(1);
            }

            try {
                this.m_conn.setAutoCommit(false);
            } catch (SQLException sQLException) {
                ExceptionLogger.logException(sQLException);
            }
        }

        private String searchPathForSqliteLib(String string1) {
            for (String string0 : System.getProperty("java.library.path", "").split(File.pathSeparator)) {
                File file = new File(string0, string1);
                if (file.exists()) {
                    return string0;
                }
            }

            return "";
        }

        void initUsedIDs(TIntHashSet tIntHashSet1, TIntHashSet tIntHashSet0) throws SQLException {
            String string = "SELECT wx,wy,id FROM vehicles";

            try (PreparedStatement preparedStatement = this.m_conn.prepareStatement(string)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int int0 = resultSet.getInt(1);
                    int int1 = resultSet.getInt(2);
                    tIntHashSet0.add(int1 << 16 | int0);
                    tIntHashSet1.add(resultSet.getInt(3));
                }
            }
        }

        private void rollback() {
            if (this.m_conn != null) {
                try {
                    this.m_conn.rollback();
                } catch (SQLException sQLException) {
                    ExceptionLogger.logException(sQLException);
                }
            }
        }
    }

    @FunctionalInterface
    public interface ThrowingBiConsumer<T1, T2, E extends Exception> {
        void accept(T1 var1, T2 var2) throws E;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T1, E extends Exception> {
        void accept(T1 var1) throws E;
    }

    private static final class VehicleBuffer {
        int m_id = -1;
        int m_wx;
        int m_wy;
        float m_x;
        float m_y;
        int m_WorldVersion;
        ByteBuffer m_bb = ByteBuffer.allocate(32768);

        void set(BaseVehicle vehicle) throws IOException {
            assert vehicle.sqlID >= 1;

            synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
                assert VehiclesDB2.instance.m_main.m_usedIDs.contains(vehicle.sqlID);
            }

            this.m_id = vehicle.sqlID;
            this.m_wx = vehicle.chunk.wx;
            this.m_wy = vehicle.chunk.wy;
            this.m_x = vehicle.getX();
            this.m_y = vehicle.getY();
            this.m_WorldVersion = IsoWorld.getWorldVersion();
            ByteBuffer byteBuffer = VehiclesDB2.TL_SliceBuffer.get();
            byteBuffer.clear();

            while (true) {
                try {
                    vehicle.save(byteBuffer);
                    break;
                } catch (BufferOverflowException bufferOverflowException) {
                    if (byteBuffer.capacity() >= 2097152) {
                        DebugLog.General.error("the vehicle %d cannot be saved", vehicle.sqlID);
                        throw bufferOverflowException;
                    }

                    byteBuffer = ByteBuffer.allocate(byteBuffer.capacity() + 32768);
                    VehiclesDB2.TL_SliceBuffer.set(byteBuffer);
                }
            }

            byteBuffer.flip();
            this.setBytes(byteBuffer);
        }

        void setBytes(ByteBuffer byteBuffer) {
            byteBuffer.rewind();
            ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
            byteBufferOutputStream.clear();
            byte[] bytes = VehiclesDB2.TL_Bytes.get();
            int int0 = byteBuffer.limit();

            while (int0 > 0) {
                int int1 = Math.min(bytes.length, int0);
                byteBuffer.get(bytes, 0, int1);
                byteBufferOutputStream.write(bytes, 0, int1);
                int0 -= int1;
            }

            byteBufferOutputStream.flip();
            this.m_bb = byteBufferOutputStream.getWrappedBuffer();
        }

        void setBytes(byte[] bytes) {
            ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
            byteBufferOutputStream.clear();
            byteBufferOutputStream.write(bytes);
            byteBufferOutputStream.flip();
            this.m_bb = byteBufferOutputStream.getWrappedBuffer();
        }

        void setBytes(InputStream inputStream) throws IOException {
            ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
            byteBufferOutputStream.clear();
            byte[] bytes = VehiclesDB2.TL_Bytes.get();

            while (true) {
                int int0 = inputStream.read(bytes);
                if (int0 < 1) {
                    byteBufferOutputStream.flip();
                    this.m_bb = byteBufferOutputStream.getWrappedBuffer();
                    return;
                }

                byteBufferOutputStream.write(bytes, 0, int0);
            }
        }
    }

    private static final class WorldStreamerThread {
        final VehiclesDB2.IVehicleStore m_store = new VehiclesDB2.SQLStore();
        final ConcurrentLinkedQueue<VehiclesDB2.QueueItem> m_queue = new ConcurrentLinkedQueue<>();
        final VehiclesDB2.VehicleBuffer m_vehicleBuffer = new VehiclesDB2.VehicleBuffer();

        void Reset() {
            this.m_store.Reset();

            assert this.m_queue.isEmpty();

            this.m_queue.clear();
        }

        void update() {
            for (VehiclesDB2.QueueItem queueItem = this.m_queue.poll(); queueItem != null; queueItem = this.m_queue.poll()) {
                try {
                    queueItem.processWorldStreamer();
                } finally {
                    VehiclesDB2.instance.m_main.m_queue.add(queueItem);
                }
            }
        }

        void loadChunk(IsoChunk chunk) throws IOException {
            this.m_store.loadChunk(chunk, this::vehicleLoaded);
        }

        void vehicleLoaded(IsoChunk chunk, VehiclesDB2.VehicleBuffer vehicleBuffer) throws IOException {
            assert vehicleBuffer.m_id >= 1;

            IsoGridSquare square = chunk.getGridSquare((int)(vehicleBuffer.m_x - chunk.wx * 10), (int)(vehicleBuffer.m_y - chunk.wy * 10), 0);
            BaseVehicle vehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
            vehicle.setSquare(square);
            vehicle.setCurrent(square);

            try {
                vehicle.load(vehicleBuffer.m_bb, vehicleBuffer.m_WorldVersion);
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                DebugLog.General.error("vehicle %d is being deleted because an error occurred loading it", vehicleBuffer.m_id);
                this.m_store.removeVehicle(vehicleBuffer.m_id);
                return;
            }

            vehicle.sqlID = vehicleBuffer.m_id;
            vehicle.chunk = chunk;
            if (chunk.jobType == IsoChunk.JobType.SoftReset) {
                vehicle.softReset();
            }

            chunk.vehicles.add(vehicle);
        }

        void unloadChunk(IsoChunk chunk) {
            for (int int0 = 0; int0 < chunk.vehicles.size(); int0++) {
                try {
                    BaseVehicle vehicle = chunk.vehicles.get(int0);
                    this.m_vehicleBuffer.set(vehicle);
                    this.m_store.updateVehicle(this.m_vehicleBuffer);
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }
            }
        }
    }
}
