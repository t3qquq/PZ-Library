// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoThumpable;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;

public final class LootRespawn {
    private static int LastRespawnHour = -1;
    private static final ArrayList<InventoryItem> existingItems = new ArrayList<>();
    private static final ArrayList<InventoryItem> newItems = new ArrayList<>();

    public static void update() {
        if (!GameClient.bClient) {
            int int0 = getRespawnInterval();
            if (int0 > 0) {
                int int1 = 7 + (int)(GameTime.getInstance().getWorldAgeHours() / int0) * int0;
                if (LastRespawnHour < int1) {
                    LastRespawnHour = int1;
                    if (GameServer.bServer) {
                        for (int int2 = 0; int2 < ServerMap.instance.LoadedCells.size(); int2++) {
                            ServerMap.ServerCell serverCell = ServerMap.instance.LoadedCells.get(int2);
                            if (serverCell.bLoaded) {
                                for (int int3 = 0; int3 < 5; int3++) {
                                    for (int int4 = 0; int4 < 5; int4++) {
                                        IsoChunk chunk0 = serverCell.chunks[int4][int3];
                                        checkChunk(chunk0);
                                    }
                                }
                            }
                        }
                    } else {
                        for (int int5 = 0; int5 < IsoPlayer.numPlayers; int5++) {
                            IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int5];
                            if (!chunkMap.ignore) {
                                for (int int6 = 0; int6 < IsoChunkMap.ChunkGridWidth; int6++) {
                                    for (int int7 = 0; int7 < IsoChunkMap.ChunkGridWidth; int7++) {
                                        IsoChunk chunk1 = chunkMap.getChunk(int7, int6);
                                        checkChunk(chunk1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void Reset() {
        LastRespawnHour = -1;
    }

    public static void chunkLoaded(IsoChunk chunk) {
        if (!GameClient.bClient) {
            checkChunk(chunk);
        }
    }

    private static void checkChunk(IsoChunk chunk) {
        if (chunk != null) {
            int int0 = getRespawnInterval();
            if (int0 > 0) {
                if (!(GameTime.getInstance().getWorldAgeHours() < int0)) {
                    int int1 = 7 + (int)(GameTime.getInstance().getWorldAgeHours() / int0) * int0;
                    if (chunk.lootRespawnHour > int1) {
                        chunk.lootRespawnHour = int1;
                    }

                    if (chunk.lootRespawnHour < int1) {
                        chunk.lootRespawnHour = int1;
                        respawnInChunk(chunk);
                    }
                }
            }
        }
    }

    private static int getRespawnInterval() {
        if (GameServer.bServer) {
            return ServerOptions.instance.HoursForLootRespawn.getValue();
        } else {
            if (!GameClient.bClient) {
                int int0 = SandboxOptions.instance.LootRespawn.getValue();
                if (int0 == 1) {
                    return 0;
                }

                if (int0 == 2) {
                    return 24;
                }

                if (int0 == 3) {
                    return 168;
                }

                if (int0 == 4) {
                    return 720;
                }

                if (int0 == 5) {
                    return 1440;
                }
            }

            return 0;
        }
    }

    private static void respawnInChunk(IsoChunk chunk) {
        boolean boolean0 = GameServer.bServer && ServerOptions.instance.ConstructionPreventsLootRespawn.getValue();
        int int0 = SandboxOptions.instance.SeenHoursPreventLootRespawn.getValue();
        double double0 = GameTime.getInstance().getWorldAgeHours();

        for (int int1 = 0; int1 < 10; int1++) {
            for (int int2 = 0; int2 < 10; int2++) {
                IsoGridSquare square = chunk.getGridSquare(int2, int1, 0);
                IsoMetaGrid.Zone zone = square == null ? null : square.getZone();
                if (zone != null
                    && ("TownZone".equals(zone.getType()) || "TownZones".equals(zone.getType()) || "TrailerPark".equals(zone.getType()))
                    && (!boolean0 || !zone.haveConstruction)
                    && (int0 <= 0 || !(zone.getHoursSinceLastSeen() <= int0))) {
                    if (square.getBuilding() != null) {
                        BuildingDef buildingDef = square.getBuilding().getDef();
                        if (buildingDef != null) {
                            if (buildingDef.lootRespawnHour > double0) {
                                buildingDef.lootRespawnHour = 0;
                            }

                            if (buildingDef.lootRespawnHour < chunk.lootRespawnHour) {
                                buildingDef.setKeySpawned(0);
                                buildingDef.lootRespawnHour = chunk.lootRespawnHour;
                            }
                        }
                    }

                    for (int int3 = 0; int3 < 8; int3++) {
                        square = chunk.getGridSquare(int2, int1, int3);
                        if (square != null) {
                            int int4 = square.getObjects().size();
                            IsoObject[] objects = square.getObjects().getElements();

                            for (int int5 = 0; int5 < int4; int5++) {
                                IsoObject object = objects[int5];
                                if (!(object instanceof IsoDeadBody) && !(object instanceof IsoThumpable) && !(object instanceof IsoCompost)) {
                                    for (int int6 = 0; int6 < object.getContainerCount(); int6++) {
                                        ItemContainer container = object.getContainerByIndex(int6);
                                        if (container.bExplored && container.isHasBeenLooted()) {
                                            respawnInContainer(object, container);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void respawnInContainer(IsoObject object, ItemContainer container) {
        if (container != null && container.getItems() != null) {
            int int0 = container.getItems().size();
            int int1 = 5;
            if (GameServer.bServer) {
                int1 = ServerOptions.instance.MaxItemsForLootRespawn.getValue();
            }

            if (int0 < int1) {
                existingItems.clear();
                existingItems.addAll(container.getItems());
                ItemPickerJava.fillContainer(container, null);
                ArrayList arrayList = container.getItems();
                if (arrayList != null && int0 != arrayList.size()) {
                    container.setHasBeenLooted(false);
                    newItems.clear();

                    for (int int2 = 0; int2 < arrayList.size(); int2++) {
                        InventoryItem item = (InventoryItem)arrayList.get(int2);
                        if (!existingItems.contains(item)) {
                            newItems.add(item);
                            item.setAge(0.0F);
                        }
                    }

                    ItemPickerJava.updateOverlaySprite(object);
                    if (GameServer.bServer) {
                        for (int int3 = 0; int3 < GameServer.udpEngine.connections.size(); int3++) {
                            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int3);
                            if (udpConnection.RelevantTo(object.square.x, object.square.y)) {
                                ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
                                PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter);
                                byteBufferWriter.putShort((short)2);
                                byteBufferWriter.putInt((int)object.getX());
                                byteBufferWriter.putInt((int)object.getY());
                                byteBufferWriter.putInt((int)object.getZ());
                                byteBufferWriter.putByte((byte)object.getObjectIndex());
                                byteBufferWriter.putByte((byte)object.getContainerIndex(container));

                                try {
                                    CompressIdenticalItems.save(byteBufferWriter.bb, newItems, null);
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }

                                PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection);
                            }
                        }
                    }
                }
            }
        }
    }
}
