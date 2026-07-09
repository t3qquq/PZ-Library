// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionRegions;
import zombie.erosion.season.ErosionIceQueen;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.IngameState;
import zombie.globalObjects.GlobalObjectLookup;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.CoopSlave;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.vehicles.VehicleManager;
import zombie.world.WorldDictionary;
import zombie.world.WorldDictionaryException;

public final class WorldConverter {
    public static final WorldConverter instance = new WorldConverter();
    public static boolean converting;
    public HashMap<Integer, Integer> TilesetConversions = null;
    int oldID = 0;

    public void convert(String string, IsoSpriteManager var2) throws IOException {
        File file = new File(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + string + File.separator + "map_ver.bin");
        if (file.exists()) {
            converting = true;
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            int int0 = dataInputStream.readInt();
            dataInputStream.close();
            if (int0 < 195) {
                if (int0 < 24) {
                    GameLoadingState.build23Stop = true;
                    return;
                }

                try {
                    this.convert(string, int0, 195);
                } catch (Exception exception) {
                    IngameState.createWorld(string);
                    IngameState.copyWorld(string + "_backup", string);
                    exception.printStackTrace();
                }
            }

            converting = false;
        }
    }

    private void convert(String string1, int int0, int int1) {
        if (!GameClient.bClient) {
            GameLoadingState.convertingWorld = true;
            String string0 = Core.GameSaveWorld;
            IngameState.createWorld(string1 + "_backup");
            IngameState.copyWorld(string1, Core.GameSaveWorld);
            Core.GameSaveWorld = string0;
            if (int1 >= 14 && int0 < 14) {
                try {
                    this.convertchunks(string1, 25, 25);
                } catch (IOException iOException0) {
                    iOException0.printStackTrace();
                }
            } else if (int0 == 7) {
                try {
                    this.convertchunks(string1);
                } catch (IOException iOException1) {
                    iOException1.printStackTrace();
                }
            }

            if (int0 <= 4) {
                this.loadconversionmap(int0, "tiledefinitions");
                this.loadconversionmap(int0, "newtiledefinitions");

                try {
                    this.convertchunks(string1);
                } catch (IOException iOException2) {
                    iOException2.printStackTrace();
                }
            }

            GameLoadingState.convertingWorld = false;
        }
    }

    private void convertchunks(String string0) throws IOException {
        IsoCell cell = new IsoCell(300, 300);
        IsoChunkMap chunkMap = new IsoChunkMap(cell);
        File file = new File(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + string0 + File.separator);
        if (!file.exists()) {
            file.mkdir();
        }

        String[] strings0 = file.list();

        for (String string1 : strings0) {
            if (string1.contains(".bin")
                && !string1.equals("map.bin")
                && !string1.equals("map_p.bin")
                && !string1.matches("map_p[0-9]+\\.bin")
                && !string1.equals("map_t.bin")
                && !string1.equals("map_c.bin")
                && !string1.equals("map_ver.bin")
                && !string1.equals("map_sand.bin")
                && !string1.equals("map_mov.bin")
                && !string1.equals("map_meta.bin")
                && !string1.equals("map_cm.bin")
                && !string1.equals("pc.bin")
                && !string1.startsWith("zpop_")
                && !string1.startsWith("chunkdata_")) {
                String[] strings1 = string1.replace(".bin", "").replace("map_", "").split("_");
                int int0 = Integer.parseInt(strings1[0]);
                int int1 = Integer.parseInt(strings1[1]);
                chunkMap.LoadChunkForLater(int0, int1, 0, 0);
                chunkMap.SwapChunkBuffers();
                chunkMap.getChunk(0, 0).Save(true);
            }
        }
    }

    private void convertchunks(String string0, int int0, int int1) throws IOException {
        IsoCell cell = new IsoCell(300, 300);
        new IsoChunkMap(cell);
        File file0 = new File(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + string0 + File.separator);
        if (!file0.exists()) {
            file0.mkdir();
        }

        String[] strings0 = file0.list();
        IsoWorld.saveoffsetx = int0;
        IsoWorld.saveoffsety = int1;
        IsoWorld.instance.MetaGrid.Create();
        WorldStreamer.instance.create();

        for (String string1 : strings0) {
            if (string1.contains(".bin")
                && !string1.equals("map.bin")
                && !string1.equals("map_p.bin")
                && !string1.matches("map_p[0-9]+\\.bin")
                && !string1.equals("map_t.bin")
                && !string1.equals("map_c.bin")
                && !string1.equals("map_ver.bin")
                && !string1.equals("map_sand.bin")
                && !string1.equals("map_mov.bin")
                && !string1.equals("map_meta.bin")
                && !string1.equals("map_cm.bin")
                && !string1.equals("pc.bin")
                && !string1.startsWith("zpop_")
                && !string1.startsWith("chunkdata_")) {
                String[] strings1 = string1.replace(".bin", "").replace("map_", "").split("_");
                int int2 = Integer.parseInt(strings1[0]);
                int int3 = Integer.parseInt(strings1[1]);
                IsoChunk chunk = new IsoChunk(cell);
                chunk.refs.add(cell.ChunkMap[0]);
                WorldStreamer.instance.addJobConvert(chunk, 0, 0, int2, int3);

                while (!chunk.bLoaded) {
                    try {
                        Thread.sleep(20L);
                    } catch (InterruptedException interruptedException0) {
                        interruptedException0.printStackTrace();
                    }
                }

                chunk.wx += int0 * 30;
                chunk.wy += int1 * 30;
                chunk.jobType = IsoChunk.JobType.Convert;
                chunk.Save(true);
                File file1 = new File(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + string0 + File.separator + string1);

                while (!ChunkSaveWorker.instance.toSaveQueue.isEmpty()) {
                    try {
                        Thread.sleep(13L);
                    } catch (InterruptedException interruptedException1) {
                        interruptedException1.printStackTrace();
                    }
                }

                file1.delete();
            }
        }
    }

    private void loadconversionmap(int int0, String string1) {
        String string0 = "media/" + string1 + "_" + int0 + ".tiles";
        File file = new File(string0);
        if (file.exists()) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "r");
                int int1 = IsoWorld.readInt(randomAccessFile);

                for (int int2 = 0; int2 < int1; int2++) {
                    Thread.sleep(4L);
                    String string2 = IsoWorld.readString(randomAccessFile);
                    String string3 = string2.trim();
                    IsoWorld.readString(randomAccessFile);
                    int int3 = IsoWorld.readInt(randomAccessFile);
                    int int4 = IsoWorld.readInt(randomAccessFile);
                    int int5 = IsoWorld.readInt(randomAccessFile);

                    for (int int6 = 0; int6 < int5; int6++) {
                        IsoSprite sprite = IsoSpriteManager.instance.NamedMap.get(string3 + "_" + int6);
                        if (this.TilesetConversions == null) {
                            this.TilesetConversions = new HashMap<>();
                        }

                        this.TilesetConversions.put(this.oldID, sprite.ID);
                        this.oldID++;
                        int int7 = IsoWorld.readInt(randomAccessFile);

                        for (int int8 = 0; int8 < int7; int8++) {
                            string2 = IsoWorld.readString(randomAccessFile);
                            String string4 = string2.trim();
                            string2 = IsoWorld.readString(randomAccessFile);
                            String string5 = string2.trim();
                        }
                    }
                }
            } catch (Exception exception) {
            }
        }
    }

    public void softreset() throws WorldDictionaryException {
        String string0 = GameServer.ServerName;
        Core.GameSaveWorld = string0;
        IsoCell cell = new IsoCell(300, 300);
        IsoChunk chunk = new IsoChunk(cell);
        File file = new File(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + string0 + File.separator);
        if (!file.exists()) {
            file.mkdir();
        }

        String[] strings0 = file.list();
        if (CoopSlave.instance != null) {
            CoopSlave.instance.sendMessage("softreset-count", null, Integer.toString(strings0.length));
        }

        IsoWorld.instance.MetaGrid.Create();
        ServerMap.instance.init(IsoWorld.instance.MetaGrid);
        new ErosionIceQueen(IsoSpriteManager.instance);
        ErosionRegions.init();
        WorldStreamer.instance.create();
        VehicleManager.instance = new VehicleManager();
        WorldDictionary.init();
        GlobalObjectLookup.init(IsoWorld.instance.getMetaGrid());
        LuaEventManager.triggerEvent("OnSGlobalObjectSystemInit");
        int int0 = strings0.length;
        DebugLog.log("processing " + int0 + " files");

        for (String string1 : strings0) {
            int0--;
            if (string1.startsWith("zpop_")) {
                deleteFile(string1);
            } else if (string1.equals("map_t.bin")) {
                deleteFile(string1);
            } else if (string1.equals("map_meta.bin") || string1.equals("map_zone.bin")) {
                deleteFile(string1);
            } else if (string1.equals("reanimated.bin")) {
                deleteFile(string1);
            } else if (string1.matches("map_[0-9]+_[0-9]+\\.bin")) {
                System.out.println("Soft clearing chunk: " + string1);
                String[] strings1 = string1.replace(".bin", "").replace("map_", "").split("_");
                int int1 = Integer.parseInt(strings1[0]);
                int int2 = Integer.parseInt(strings1[1]);
                chunk.refs.add(cell.ChunkMap[0]);
                chunk.wx = int1;
                chunk.wy = int2;
                ServerMap.instance.setSoftResetChunk(chunk);
                WorldStreamer.instance.addJobWipe(chunk, 0, 0, int1, int2);

                while (!chunk.bLoaded) {
                    try {
                        Thread.sleep(20L);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                chunk.jobType = IsoChunk.JobType.Convert;
                chunk.FloorBloodSplats.clear();

                try {
                    chunk.Save(true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                ServerMap.instance.clearSoftResetChunk(chunk);
                chunk.doReuseGridsquares();
                IsoChunkMap.chunkStore.remove(chunk);
                if (int0 % 100 == 0) {
                    DebugLog.log(int0 + " files to go");
                }

                if (CoopSlave.instance != null && int0 % 10 == 0) {
                    CoopSlave.instance.sendMessage("softreset-remaining", null, Integer.toString(int0));
                }
            }
        }

        GameServer.ResetID = Rand.Next(10000000);
        ServerOptions.instance.putSaveOption("ResetID", String.valueOf(GameServer.ResetID));
        IsoWorld.instance.CurrentCell = null;
        DebugLog.log("soft-reset complete, server terminated");
        if (CoopSlave.instance != null) {
            CoopSlave.instance.sendMessage("softreset-finished", null, "");
        }

        SteamUtils.shutdown();
        System.exit(0);
    }

    private static void deleteFile(String string) {
        File file = new File(ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + GameServer.ServerName + File.separator + string);
        file.delete();
    }
}
