// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.map.hash.TShortShortHashMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
import zombie.core.physics.Transform;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.packets.VehicleAuthorizationPacket;
import zombie.network.packets.vehicle.Physics;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Type;

public final class VehicleManager {
    public static VehicleManager instance;
    private final VehicleIDMap IDToVehicle = VehicleIDMap.instance;
    private final ArrayList<BaseVehicle> vehicles = new ArrayList<>();
    private boolean idMapDirty = true;
    private final Transform tempTransform = new Transform();
    private final ArrayList<BaseVehicle> sendReliable = new ArrayList<>();
    private final ArrayList<BaseVehicle> sendUnreliable = new ArrayList<>();
    private final TShortArrayList vehiclesWaitUpdates = new TShortArrayList(128);
    private final TShortShortHashMap towedVehicleMap = new TShortShortHashMap();
    public static HashMap<Byte, String> vehiclePacketTypes = new HashMap<>();
    public final UdpConnection[] connected = new UdpConnection[512];
    private final float[] tempFloats = new float[27];
    private final float[] engineSound = new float[2];
    private final VehicleManager.PosUpdateVars posUpdateVars = new VehicleManager.PosUpdateVars();
    private final UpdateLimit vehiclesWaitUpdatesFrequency = new UpdateLimit(1000L);
    private BaseVehicle tempVehicle;
    private final ArrayList<BaseVehicle.ModelInfo> oldModels = new ArrayList<>();
    private final ArrayList<BaseVehicle.ModelInfo> curModels = new ArrayList<>();
    private final UpdateLimit sendRequestGetPositionFrequency = new UpdateLimit(500L);
    private final UpdateLimit VehiclePhysicSyncPacketLimit = new UpdateLimit(500L);

    public void registerVehicle(BaseVehicle vehicle) {
        this.IDToVehicle.put(vehicle.VehicleID, vehicle);
        this.idMapDirty = true;
    }

    public void unregisterVehicle(BaseVehicle vehicle) {
        this.IDToVehicle.remove(vehicle.VehicleID);
        this.idMapDirty = true;
    }

    public BaseVehicle getVehicleByID(short short0) {
        return this.IDToVehicle.get(short0);
    }

    public ArrayList<BaseVehicle> getVehicles() {
        if (this.idMapDirty) {
            this.vehicles.clear();
            this.IDToVehicle.toArrayList(this.vehicles);
            this.idMapDirty = false;
        }

        return this.vehicles;
    }

    public void removeFromWorld(BaseVehicle vehicle) {
        if (vehicle.VehicleID != -1) {
            DebugLog.Vehicle.trace("removeFromWorld vehicle id=%d", vehicle.VehicleID);
            this.unregisterVehicle(vehicle);
            if (GameServer.bServer) {
                for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                    UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                    if (vehicle.connectionState[udpConnection.index] != null) {
                        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
                        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
                        byteBufferWriter.bb.put((byte)8);
                        byteBufferWriter.bb.putShort(vehicle.VehicleID);
                        PacketTypes.PacketType.Vehicles.send(udpConnection);
                    }
                }
            }

            if (GameClient.bClient) {
                vehicle.serverRemovedFromWorld = false;
                if (vehicle.interpolation != null) {
                    vehicle.interpolation.clear();
                }
            }
        }
    }

    public void serverUpdate() {
        ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();

        for (int int0 = 0; int0 < this.connected.length; int0++) {
            if (this.connected[int0] != null && !GameServer.udpEngine.connections.contains(this.connected[int0])) {
                DebugLog.Vehicle.trace("vehicles: dropped connection %d", int0);

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    ((BaseVehicle)arrayList.get(int1)).connectionState[int0] = null;
                }

                this.connected[int0] = null;
            } else {
                for (int int2 = 0; int2 < arrayList.size(); int2++) {
                    if (((BaseVehicle)arrayList.get(int2)).connectionState[int0] != null) {
                        BaseVehicle.ServerVehicleState serverVehicleState = ((BaseVehicle)arrayList.get(int2)).connectionState[int0];
                        serverVehicleState.flags = (short)(serverVehicleState.flags | ((BaseVehicle)arrayList.get(int2)).updateFlags);
                    }
                }
            }
        }

        for (int int3 = 0; int3 < GameServer.udpEngine.connections.size(); int3++) {
            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int3);
            this.sendVehicles(udpConnection, PacketTypes.PacketType.VehiclesUnreliable.getId());
            this.connected[udpConnection.index] = udpConnection;
        }

        for (int int4 = 0; int4 < arrayList.size(); int4++) {
            BaseVehicle vehicle = (BaseVehicle)arrayList.get(int4);
            if ((vehicle.updateFlags & 19440) != 0) {
                for (int int5 = 0; int5 < vehicle.getPartCount(); int5++) {
                    VehiclePart part = vehicle.getPartByIndex(int5);
                    part.updateFlags = 0;
                }
            }

            vehicle.updateFlags = 0;
        }
    }

    private void sendVehicles(UdpConnection udpConnection, short short0) {
        if (udpConnection.isFullyConnected()) {
            this.sendReliable.clear();
            this.sendUnreliable.clear();
            ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                BaseVehicle vehicle = (BaseVehicle)arrayList.get(int0);
                if (vehicle.VehicleID == -1) {
                    vehicle.VehicleID = this.IDToVehicle.allocateID();
                    this.registerVehicle(vehicle);
                }

                if (udpConnection.RelevantTo(vehicle.x, vehicle.y)) {
                    if (vehicle.connectionState[udpConnection.index] == null) {
                        vehicle.connectionState[udpConnection.index] = new BaseVehicle.ServerVehicleState();
                    }

                    BaseVehicle.ServerVehicleState serverVehicleState = vehicle.connectionState[udpConnection.index];
                    if (serverVehicleState.shouldSend(vehicle)) {
                        if (!vehicle.isReliable && PacketTypes.PacketType.Vehicles.getId() != short0) {
                            this.sendUnreliable.add(vehicle);
                        } else {
                            this.sendReliable.add(vehicle);
                        }
                    }
                }
            }

            this.sendVehiclesInternal(udpConnection, this.sendReliable, PacketTypes.PacketType.Vehicles);
            this.sendVehiclesInternal(udpConnection, this.sendUnreliable, PacketTypes.PacketType.VehiclesUnreliable);
        }
    }

    private void sendVehiclesInternal(UdpConnection udpConnection, ArrayList<BaseVehicle> arrayList, PacketTypes.PacketType packetType) {
        if (!arrayList.isEmpty()) {
            ByteBufferWriter byteBufferWriter0 = udpConnection.startPacket();
            packetType.doPacket(byteBufferWriter0);

            try {
                ByteBuffer byteBuffer = byteBufferWriter0.bb;
                byteBuffer.put((byte)5);
                byteBuffer.putShort((short)arrayList.size());

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    BaseVehicle vehicle0 = (BaseVehicle)arrayList.get(int0);
                    BaseVehicle.ServerVehicleState serverVehicleState0 = vehicle0.connectionState[udpConnection.index];
                    byteBuffer.putShort(vehicle0.VehicleID);
                    byteBuffer.putShort(serverVehicleState0.flags);
                    byteBuffer.putFloat(vehicle0.x);
                    byteBuffer.putFloat(vehicle0.y);
                    byteBuffer.putFloat(vehicle0.jniTransform.origin.y);
                    int int1 = byteBuffer.position();
                    byteBuffer.putShort((short)0);
                    int int2 = byteBuffer.position();
                    boolean boolean0 = (serverVehicleState0.flags & 1) != 0;
                    if (boolean0) {
                        serverVehicleState0.flags = (short)(serverVehicleState0.flags & -2);
                        vehicle0.netPlayerServerSendAuthorisation(byteBuffer);
                        serverVehicleState0.setAuthorization(vehicle0);
                        int int3 = byteBuffer.position();
                        byteBuffer.putShort((short)0);
                        vehicle0.save(byteBuffer);
                        int int4 = byteBuffer.position();
                        byteBuffer.position(int3);
                        byteBuffer.putShort((short)(int4 - int3));
                        byteBuffer.position(int4);
                        int int5 = byteBuffer.position();
                        int int6 = byteBuffer.position() - int2;
                        byteBuffer.position(int1);
                        byteBuffer.putShort((short)int6);
                        byteBuffer.position(int5);
                        this.writePositionOrientation(byteBuffer, vehicle0);
                        serverVehicleState0.x = vehicle0.x;
                        serverVehicleState0.y = vehicle0.y;
                        serverVehicleState0.z = vehicle0.jniTransform.origin.y;
                        serverVehicleState0.orient.set(vehicle0.savedRot);
                    } else {
                        if ((serverVehicleState0.flags & 2) != 0) {
                            this.writePositionOrientation(byteBuffer, vehicle0);
                            serverVehicleState0.x = vehicle0.x;
                            serverVehicleState0.y = vehicle0.y;
                            serverVehicleState0.z = vehicle0.jniTransform.origin.y;
                            serverVehicleState0.orient.set(vehicle0.savedRot);
                        }

                        if ((serverVehicleState0.flags & 4) != 0) {
                            byteBuffer.put((byte)vehicle0.engineState.ordinal());
                            byteBuffer.putInt(vehicle0.engineLoudness);
                            byteBuffer.putInt(vehicle0.enginePower);
                            byteBuffer.putInt(vehicle0.engineQuality);
                        }

                        if ((serverVehicleState0.flags & 4096) != 0) {
                            byteBuffer.put((byte)(vehicle0.isHotwired() ? 1 : 0));
                            byteBuffer.put((byte)(vehicle0.isHotwiredBroken() ? 1 : 0));
                            byteBuffer.putFloat(vehicle0.getRegulatorSpeed());
                            byteBuffer.put((byte)(vehicle0.isPreviouslyEntered() ? 1 : 0));
                            byteBuffer.put((byte)(vehicle0.isKeysInIgnition() ? 1 : 0));
                            byteBuffer.put((byte)(vehicle0.isKeyIsOnDoor() ? 1 : 0));
                            InventoryItem item0 = vehicle0.getCurrentKey();
                            if (item0 == null) {
                                byteBuffer.put((byte)0);
                            } else {
                                byteBuffer.put((byte)1);
                                item0.saveWithSize(byteBuffer, false);
                            }

                            byteBuffer.putFloat(vehicle0.getRust());
                            byteBuffer.putFloat(vehicle0.getBloodIntensity("Front"));
                            byteBuffer.putFloat(vehicle0.getBloodIntensity("Rear"));
                            byteBuffer.putFloat(vehicle0.getBloodIntensity("Left"));
                            byteBuffer.putFloat(vehicle0.getBloodIntensity("Right"));
                            byteBuffer.putFloat(vehicle0.getColorHue());
                            byteBuffer.putFloat(vehicle0.getColorSaturation());
                            byteBuffer.putFloat(vehicle0.getColorValue());
                            byteBuffer.putInt(vehicle0.getSkinIndex());
                        }

                        if ((serverVehicleState0.flags & 8) != 0) {
                            byteBuffer.put((byte)(vehicle0.getHeadlightsOn() ? 1 : 0));
                            byteBuffer.put((byte)(vehicle0.getStoplightsOn() ? 1 : 0));

                            for (int int7 = 0; int7 < vehicle0.getLightCount(); int7++) {
                                byteBuffer.put((byte)(vehicle0.getLightByIndex(int7).getLight().getActive() ? 1 : 0));
                            }
                        }

                        if ((serverVehicleState0.flags & 1024) != 0) {
                            byteBuffer.put((byte)(vehicle0.soundHornOn ? 1 : 0));
                            byteBuffer.put((byte)(vehicle0.soundBackMoveOn ? 1 : 0));
                            byteBuffer.put((byte)vehicle0.lightbarLightsMode.get());
                            byteBuffer.put((byte)vehicle0.lightbarSirenMode.get());
                        }

                        if ((serverVehicleState0.flags & 2048) != 0) {
                            for (int int8 = 0; int8 < vehicle0.getPartCount(); int8++) {
                                VehiclePart part0 = vehicle0.getPartByIndex(int8);
                                if ((part0.updateFlags & 2048) != 0) {
                                    byteBuffer.put((byte)int8);
                                    byteBuffer.putInt(part0.getCondition());
                                }
                            }

                            byteBuffer.put((byte)-1);
                        }

                        if ((serverVehicleState0.flags & 16) != 0) {
                            for (int int9 = 0; int9 < vehicle0.getPartCount(); int9++) {
                                VehiclePart part1 = vehicle0.getPartByIndex(int9);
                                if ((part1.updateFlags & 16) != 0) {
                                    byteBuffer.put((byte)int9);
                                    part1.getModData().save(byteBuffer);
                                }
                            }

                            byteBuffer.put((byte)-1);
                        }

                        if ((serverVehicleState0.flags & 32) != 0) {
                            for (int int10 = 0; int10 < vehicle0.getPartCount(); int10++) {
                                VehiclePart part2 = vehicle0.getPartByIndex(int10);
                                if ((part2.updateFlags & 32) != 0) {
                                    InventoryItem item1 = part2.getInventoryItem();
                                    if (item1 instanceof DrainableComboItem) {
                                        byteBuffer.put((byte)int10);
                                        byteBuffer.putFloat(((DrainableComboItem)item1).getUsedDelta());
                                    }
                                }
                            }

                            byteBuffer.put((byte)-1);
                        }

                        if ((serverVehicleState0.flags & 128) != 0) {
                            for (int int11 = 0; int11 < vehicle0.getPartCount(); int11++) {
                                VehiclePart part3 = vehicle0.getPartByIndex(int11);
                                if ((part3.updateFlags & 128) != 0) {
                                    byteBuffer.put((byte)int11);
                                    InventoryItem item2 = part3.getInventoryItem();
                                    if (item2 == null) {
                                        byteBuffer.put((byte)0);
                                    } else {
                                        byteBuffer.put((byte)1);

                                        try {
                                            part3.getInventoryItem().saveWithSize(byteBuffer, false);
                                        } catch (Exception exception0) {
                                            exception0.printStackTrace();
                                        }
                                    }
                                }
                            }

                            byteBuffer.put((byte)-1);
                        }

                        if ((serverVehicleState0.flags & 512) != 0) {
                            for (int int12 = 0; int12 < vehicle0.getPartCount(); int12++) {
                                VehiclePart part4 = vehicle0.getPartByIndex(int12);
                                if ((part4.updateFlags & 512) != 0) {
                                    byteBuffer.put((byte)int12);
                                    part4.getDoor().save(byteBuffer);
                                }
                            }

                            byteBuffer.put((byte)-1);
                        }

                        if ((serverVehicleState0.flags & 256) != 0) {
                            for (int int13 = 0; int13 < vehicle0.getPartCount(); int13++) {
                                VehiclePart part5 = vehicle0.getPartByIndex(int13);
                                if ((part5.updateFlags & 256) != 0) {
                                    byteBuffer.put((byte)int13);
                                    part5.getWindow().save(byteBuffer);
                                }
                            }

                            byteBuffer.put((byte)-1);
                        }

                        if ((serverVehicleState0.flags & 64) != 0) {
                            byteBuffer.put((byte)vehicle0.models.size());

                            for (int int14 = 0; int14 < vehicle0.models.size(); int14++) {
                                BaseVehicle.ModelInfo modelInfo = vehicle0.models.get(int14);
                                byteBuffer.put((byte)modelInfo.part.getIndex());
                                byteBuffer.put((byte)modelInfo.part.getScriptPart().models.indexOf(modelInfo.scriptModel));
                            }
                        }

                        int int15 = byteBuffer.position();
                        int int16 = byteBuffer.position() - int2;
                        byteBuffer.position(int1);
                        byteBuffer.putShort((short)int16);
                        byteBuffer.position(int15);
                    }
                }

                packetType.send(udpConnection);
            } catch (Exception exception1) {
                udpConnection.cancelPacket();
                exception1.printStackTrace();
            }

            for (int int17 = 0; int17 < arrayList.size(); int17++) {
                BaseVehicle vehicle1 = (BaseVehicle)arrayList.get(int17);
                BaseVehicle.ServerVehicleState serverVehicleState1 = vehicle1.connectionState[udpConnection.index];
                if ((serverVehicleState1.flags & 16384) != 0) {
                    VehicleAuthorizationPacket vehicleAuthorizationPacket = new VehicleAuthorizationPacket();
                    vehicleAuthorizationPacket.set(vehicle1, udpConnection);
                    ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                    PacketTypes.PacketType.VehicleAuthorization.doPacket(byteBufferWriter1);
                    vehicleAuthorizationPacket.write(byteBufferWriter1);
                    PacketTypes.PacketType.VehicleAuthorization.send(udpConnection);
                }
            }
        }
    }

    public void serverPacket(ByteBuffer byteBuffer, UdpConnection udpConnection0, short short1) {
        byte byte0 = byteBuffer.get();
        switch (byte0) {
            case 1:
                short short6 = byteBuffer.getShort();
                DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte0), short6);
                byte byte2 = byteBuffer.get();
                String string = GameWindow.ReadString(byteBuffer);
                BaseVehicle vehicle3 = this.IDToVehicle.get(short6);
                if (vehicle3 != null) {
                    IsoGameCharacter character = vehicle3.getCharacter(byte2);
                    if (character != null) {
                        vehicle3.setCharacterPosition(character, byte2, string);
                        this.sendPassengerPosition(vehicle3, byte2, string, udpConnection0);
                    }
                }
                break;
            case 2:
                short short10 = byteBuffer.getShort();
                short short11 = byteBuffer.getShort();
                byte byte4 = byteBuffer.get();
                DebugLog.Vehicle.trace("Vehicle enter vid=%d pid=%d seat=%d", short10, short11, Integer.valueOf(byte4));
                BaseVehicle vehicle6 = this.IDToVehicle.get(short10);
                if (vehicle6 == null) {
                    DebugLog.Vehicle.warn("Vehicle vid=%d not found", short10);
                } else {
                    IsoPlayer player1 = GameServer.IDToPlayerMap.get(short11);
                    if (player1 == null) {
                        DebugLog.Vehicle.warn("Player pid=%d not found", short11);
                    } else {
                        IsoPlayer player2 = Type.tryCastTo(vehicle6.getCharacter(byte4), IsoPlayer.class);
                        if (player2 != null && player2 != player1) {
                            DebugLog.Vehicle.warn(player1.getUsername() + " got in same seat as " + player2.getUsername());
                        } else {
                            vehicle6.enter(byte4, player1);
                            if (byte4 == 0 && vehicle6.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
                                vehicle6.authorizationServerOnSeat(player1, true);
                            }

                            this.sendEnter(vehicle6, player1, byte4);
                        }
                    }
                }
                break;
            case 3:
                short short2 = byteBuffer.getShort();
                short short3 = byteBuffer.getShort();
                byte byte1 = byteBuffer.get();
                DebugLog.Vehicle.trace("Vehicle exit vid=%d pid=%d seat=%d", short2, short3, Integer.valueOf(byte1));
                BaseVehicle vehicle1 = this.IDToVehicle.get(short2);
                if (vehicle1 == null) {
                    DebugLog.Vehicle.warn("Vehicle vid=%d not found", short2);
                } else {
                    IsoPlayer player0 = GameServer.IDToPlayerMap.get(short3);
                    if (player0 == null) {
                        DebugLog.Vehicle.warn("Player pid=%d not found", short3);
                    } else {
                        vehicle1.exit(player0);
                        if (byte1 == 0) {
                            vehicle1.authorizationServerOnSeat(player0, false);
                        }

                        this.sendExit(vehicle1, player0, byte1);
                    }
                }
                break;
            case 4:
                short short12 = byteBuffer.getShort();
                short short13 = byteBuffer.getShort();
                byte byte5 = byteBuffer.get();
                byte byte6 = byteBuffer.get();
                DebugLog.Vehicle.trace("Vehicle switch seat vid=%d pid=%d seats=%d=>%d", short12, short13, Integer.valueOf(byte5), Integer.valueOf(byte6));
                BaseVehicle vehicle7 = this.IDToVehicle.get(short12);
                if (vehicle7 == null) {
                    DebugLog.Vehicle.warn("Vehicle vid=%d not found", short12);
                } else {
                    IsoPlayer player3 = GameServer.IDToPlayerMap.get(short13);
                    if (player3 == null) {
                        DebugLog.Vehicle.warn("Player pid=%d not found", short13);
                    } else {
                        IsoPlayer player4 = Type.tryCastTo(vehicle7.getCharacter(byte6), IsoPlayer.class);
                        if (player4 != null && player4 != player3) {
                            DebugLog.Vehicle.warn(player3.getUsername() + " switched to same seat as " + player4.getUsername());
                        } else {
                            vehicle7.switchSeat(player3, byte6);
                            if (byte6 == 0 && vehicle7.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
                                vehicle7.authorizationServerOnSeat(player3, true);
                            } else if (byte5 == 0) {
                                vehicle7.authorizationServerOnSeat(player3, false);
                            }

                            this.sendSwitchSeat(vehicle7, player3, byte5, byte6);
                        }
                    }
                }
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 13:
            case 14:
            default:
                DebugLog.Vehicle.warn("Unknown vehicle packet %d", byte0);
                break;
            case 9:
                Physics physics = new Physics();
                physics.parse(byteBuffer, udpConnection0);
                physics.process();

                for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); int1++) {
                    UdpConnection udpConnection1 = GameServer.udpEngine.connections.get(int1);
                    if (udpConnection0 != udpConnection1 && physics.isRelevant(udpConnection1)) {
                        ByteBufferWriter byteBufferWriter0 = udpConnection1.startPacket();
                        PacketTypes.PacketType packetType = PacketTypes.packetTypes.get(short1);
                        packetType.doPacket(byteBufferWriter0);
                        byteBufferWriter0.bb.put((byte)9);
                        physics.write(byteBufferWriter0);
                        packetType.send(udpConnection1);
                    }
                }
                break;
            case 11:
                short short7 = byteBuffer.getShort();

                for (int int0 = 0; int0 < short7; int0++) {
                    short short8 = byteBuffer.getShort();
                    DebugLog.Vehicle.trace("Vehicle vid=%d full update response ", short8);
                    BaseVehicle vehicle4 = this.IDToVehicle.get(short8);
                    if (vehicle4 != null) {
                        if (vehicle4.connectionState[udpConnection0.index] == null) {
                            vehicle4.connectionState[udpConnection0.index] = new BaseVehicle.ServerVehicleState();
                        }

                        vehicle4.connectionState[udpConnection0.index].flags = (short)(vehicle4.connectionState[udpConnection0.index].flags | 1);
                        this.sendVehicles(udpConnection0, short1);
                    }
                }
                break;
            case 12:
                short short0 = byteBuffer.getShort();
                DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte0), short0);
                BaseVehicle vehicle0 = this.IDToVehicle.get(short0);
                if (vehicle0 != null) {
                    vehicle0.updateFlags = (short)(vehicle0.updateFlags | 2);
                    this.sendVehicles(udpConnection0, short1);
                }
                break;
            case 15:
                short short4 = byteBuffer.getShort();
                short short5 = byteBuffer.getShort();
                boolean boolean0 = byteBuffer.get() == 1;
                DebugLog.Vehicle.trace("%s vid=%d pid=%d %b", vehiclePacketTypes.get(byte0), short4, short5, boolean0);
                BaseVehicle vehicle2 = this.IDToVehicle.get(short4);
                if (vehicle2 != null) {
                    vehicle2.authorizationServerCollide(short5, boolean0);
                }
                break;
            case 16:
                short short9 = byteBuffer.getShort();
                DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte0), short9);
                byte byte3 = byteBuffer.get();
                BaseVehicle vehicle5 = this.IDToVehicle.get(short9);
                if (vehicle5 != null) {
                    for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); int2++) {
                        UdpConnection udpConnection2 = GameServer.udpEngine.connections.get(int2);
                        if (udpConnection2 != udpConnection0) {
                            ByteBufferWriter byteBufferWriter1 = udpConnection2.startPacket();
                            PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter1);
                            byteBufferWriter1.bb.put((byte)16);
                            byteBufferWriter1.bb.putShort(vehicle5.VehicleID);
                            byteBufferWriter1.bb.put(byte3);
                            PacketTypes.PacketType.Vehicles.send(udpConnection2);
                        }
                    }
                }
        }
    }

    public void serverSendInitialWorldState(UdpConnection udpConnection) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)19);
        byteBufferWriter.bb.putShort((short)this.towedVehicleMap.size());
        this.towedVehicleMap.forEachEntry((short0, short1) -> {
            byteBufferWriter.putShort(short0);
            byteBufferWriter.putShort(short1);
            return true;
        });
        PacketTypes.PacketType.Vehicles.send(udpConnection);
    }

    private void vehiclePosUpdate(BaseVehicle vehicle, float[] floats4) {
        int int0 = 0;
        Transform transform = this.posUpdateVars.transform;
        Vector3f vector3f0 = this.posUpdateVars.vector3f;
        Quaternionf quaternionf = this.posUpdateVars.quatf;
        float[] floats0 = this.posUpdateVars.wheelSteer;
        float[] floats1 = this.posUpdateVars.wheelRotation;
        float[] floats2 = this.posUpdateVars.wheelSkidInfo;
        float[] floats3 = this.posUpdateVars.wheelSuspensionLength;
        float float0 = floats4[int0++] - WorldSimulation.instance.offsetX;
        float float1 = floats4[int0++] - WorldSimulation.instance.offsetY;
        float float2 = floats4[int0++];
        transform.origin.set(float0, float2, float1);
        float float3 = floats4[int0++];
        float float4 = floats4[int0++];
        float float5 = floats4[int0++];
        float float6 = floats4[int0++];
        quaternionf.set(float3, float4, float5, float6);
        quaternionf.normalize();
        transform.setRotation(quaternionf);
        float float7 = floats4[int0++];
        float float8 = floats4[int0++];
        float float9 = floats4[int0++];
        vector3f0.set(float7, float8, float9);
        int int1 = (int)floats4[int0++];

        for (int int2 = 0; int2 < int1; int2++) {
            floats0[int2] = floats4[int0++];
            floats1[int2] = floats4[int0++];
            floats2[int2] = floats4[int0++];
            floats3[int2] = floats4[int0++];
        }

        vehicle.jniTransform.set(transform);
        vehicle.jniLinearVelocity.set(vector3f0);
        vehicle.jniTransform.basis.getScale(vector3f0);
        if (vector3f0.x < 0.99 || vector3f0.y < 0.99 || vector3f0.z < 0.99) {
            vehicle.jniTransform.basis.scale(1.0F / vector3f0.x, 1.0F / vector3f0.y, 1.0F / vector3f0.z);
        }

        vehicle.jniSpeed = vehicle.jniLinearVelocity.length() * 3.6F;
        Vector3f vector3f1 = vehicle.getForwardVector(BaseVehicle.allocVector3f());
        if (vector3f1.dot(vehicle.jniLinearVelocity) < 0.0F) {
            vehicle.jniSpeed *= -1.0F;
        }

        BaseVehicle.releaseVector3f(vector3f1);

        for (int int3 = 0; int3 < 4; int3++) {
            vehicle.wheelInfo[int3].steering = floats0[int3];
            vehicle.wheelInfo[int3].rotation = floats1[int3];
            vehicle.wheelInfo[int3].skidInfo = floats2[int3];
            vehicle.wheelInfo[int3].suspensionLength = floats3[int3];
        }

        vehicle.polyDirty = true;
    }

    public void clientUpdate() {
        if (this.vehiclesWaitUpdatesFrequency.Check()) {
            if (this.vehiclesWaitUpdates.size() > 0) {
                ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
                PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
                byteBufferWriter.bb.put((byte)11);
                byteBufferWriter.bb.putShort((short)this.vehiclesWaitUpdates.size());

                for (int int0 = 0; int0 < this.vehiclesWaitUpdates.size(); int0++) {
                    byteBufferWriter.bb.putShort(this.vehiclesWaitUpdates.get(int0));
                }

                PacketTypes.PacketType.Vehicles.send(GameClient.connection);
            }

            this.vehiclesWaitUpdates.clear();
        }

        ArrayList arrayList = this.getVehicles();

        for (int int1 = 0; int1 < arrayList.size(); int1++) {
            BaseVehicle vehicle = (BaseVehicle)arrayList.get(int1);
            if (GameClient.bClient) {
                if (vehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Local)
                    || vehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.LocalCollide)) {
                    vehicle.interpolation.clear();
                    continue;
                }
            } else if (vehicle.isKeyboardControlled() || vehicle.getJoypad() != -1) {
                vehicle.interpolation.clear();
                continue;
            }

            float[] floats = this.tempFloats;
            if (vehicle.interpolation.interpolationDataGet(floats, this.engineSound)) {
                if (!vehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Local)
                    && !vehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.LocalCollide)) {
                    Bullet.setOwnVehiclePhysics(vehicle.VehicleID, floats);
                    float float0 = floats[0];
                    float float1 = floats[1];
                    float float2 = floats[2];
                    IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float0, (double)float1, 0.0);
                    this.clientUpdateVehiclePos(vehicle, float0, float1, float2, square);
                    vehicle.limitPhysicValid.BlockCheck();
                    if (GameClient.bClient) {
                        this.vehiclePosUpdate(vehicle, floats);
                    }

                    vehicle.engineSpeed = this.engineSound[0];
                    vehicle.throttle = this.engineSound[1];
                }
            } else {
                vehicle.getController().control_NoControl();
                vehicle.throttle = 0.0F;
                vehicle.jniSpeed = 0.0F;
            }
        }
    }

    private void clientUpdateVehiclePos(BaseVehicle vehicle, float float0, float float1, float var4, IsoGridSquare square) {
        vehicle.setX(float0);
        vehicle.setY(float1);
        vehicle.setZ(0.0F);
        vehicle.square = square;
        vehicle.setCurrent(square);
        if (square != null) {
            if (vehicle.chunk != null && vehicle.chunk != square.chunk) {
                vehicle.chunk.vehicles.remove(vehicle);
            }

            vehicle.chunk = vehicle.square.chunk;
            if (!vehicle.chunk.vehicles.contains(vehicle)) {
                vehicle.chunk.vehicles.add(vehicle);
                IsoChunk.addFromCheckedVehicles(vehicle);
            }

            if (!vehicle.addedToWorld) {
                vehicle.addToWorld();
            }
        } else {
            vehicle.removeFromWorld();
            vehicle.removeFromSquare();
        }

        vehicle.polyDirty = true;
    }

    private void clientReceiveUpdateFull(ByteBuffer byteBuffer, short short2, float float1, float float0, float float2) throws IOException {
        BaseVehicle.Authorization authorization = BaseVehicle.Authorization.valueOf(byteBuffer.get());
        short short0 = byteBuffer.getShort();
        short short1 = byteBuffer.getShort();
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float0, 0.0);
        if (this.IDToVehicle.containsKey(short2)) {
            BaseVehicle vehicle0 = this.IDToVehicle.get(short2);
            DebugLog.Vehicle.noise("ERROR: got full update for KNOWN vehicle id=%d", short2);
            byteBuffer.get();
            byteBuffer.get();
            this.tempVehicle.parts.clear();
            this.tempVehicle.load(byteBuffer, 195);
            if (vehicle0.physics != null && (vehicle0.getDriver() == null || !vehicle0.getDriver().isLocal())) {
                this.tempTransform.setRotation(this.tempVehicle.savedRot);
                this.tempTransform.origin.set(float1 - WorldSimulation.instance.offsetX, float2, float0 - WorldSimulation.instance.offsetY);
                vehicle0.setWorldTransform(this.tempTransform);
            }

            vehicle0.netPlayerFromServerUpdate(authorization, short0);
            this.clientUpdateVehiclePos(vehicle0, float1, float0, float2, square);
        } else {
            boolean boolean0 = byteBuffer.get() != 0;
            byte byte0 = byteBuffer.get();
            if (!boolean0 || byte0 != IsoObject.getFactoryVehicle().getClassID()) {
                DebugLog.Vehicle.error("clientReceiveUpdateFull: packet broken");
            }

            BaseVehicle vehicle1 = new BaseVehicle(IsoWorld.instance.CurrentCell);
            vehicle1.VehicleID = short2;
            vehicle1.square = square;
            vehicle1.setCurrent(square);
            vehicle1.load(byteBuffer, 195);
            if (square != null) {
                vehicle1.chunk = vehicle1.square.chunk;
                vehicle1.chunk.vehicles.add(vehicle1);
                vehicle1.addToWorld();
            }

            IsoChunk.addFromCheckedVehicles(vehicle1);
            vehicle1.netPlayerFromServerUpdate(authorization, short0);
            this.registerVehicle(vehicle1);

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && !player.isDead() && player.getVehicle() == null) {
                    IsoWorld.instance.CurrentCell.putInVehicle(player);
                }
            }

            DebugLog.Vehicle.trace("added vehicle id=%d %s", vehicle1.VehicleID, square == null ? " (delayed)" : "");
        }
    }

    private void clientReceiveUpdate(ByteBuffer byteBuffer) throws IOException {
        short short0 = byteBuffer.getShort();
        DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get((byte)5), short0);
        short short1 = byteBuffer.getShort();
        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        float float2 = byteBuffer.getFloat();
        short short2 = byteBuffer.getShort();
        VehicleCache.vehicleUpdate(short0, float0, float1, 0.0F);
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float0, (double)float1, 0.0);
        BaseVehicle vehicle = this.IDToVehicle.get(short0);
        if (vehicle == null && square == null) {
            if (byteBuffer.limit() > byteBuffer.position() + short2) {
                byteBuffer.position(byteBuffer.position() + short2);
            }
        } else if (vehicle != null && square == null) {
            boolean boolean0 = true;

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && player.getVehicle() == vehicle) {
                    boolean0 = false;
                    player.setPosition(float0, float1, 0.0F);
                    this.sendRequestGetPosition(short0, PacketTypes.PacketType.VehiclesUnreliable);
                }
            }

            if (boolean0) {
                vehicle.removeFromWorld();
                vehicle.removeFromSquare();
            }

            if (byteBuffer.limit() > byteBuffer.position() + short2) {
                byteBuffer.position(byteBuffer.position() + short2);
            }
        } else if ((short1 & 1) != 0) {
            DebugLog.Vehicle.trace("Vehicle vid=%d full update received", short0);
            this.clientReceiveUpdateFull(byteBuffer, short0, float0, float1, float2);
            if (vehicle == null) {
                vehicle = this.IDToVehicle.get(short0);
            }

            if (!vehicle.isKeyboardControlled() && vehicle.getJoypad() == -1) {
                byteBuffer.getLong();
                int int1 = 0;
                float[] floats = this.tempFloats;
                floats[int1++] = float0;
                floats[int1++] = float1;
                floats[int1++] = float2;

                while (int1 < 10) {
                    floats[int1++] = byteBuffer.getFloat();
                }

                float float3 = byteBuffer.getFloat();
                float float4 = byteBuffer.getFloat();
                short short3 = byteBuffer.getShort();
                floats[int1++] = short3;

                for (int int2 = 0; int2 < short3; int2++) {
                    floats[int1++] = byteBuffer.getFloat();
                    floats[int1++] = byteBuffer.getFloat();
                    floats[int1++] = byteBuffer.getFloat();
                    floats[int1++] = byteBuffer.getFloat();
                }

                Bullet.setOwnVehiclePhysics(short0, floats);
            } else if (byteBuffer.limit() > byteBuffer.position() + 102) {
                byteBuffer.position(byteBuffer.position() + 102);
            }

            int int3 = this.vehiclesWaitUpdates.indexOf(short0);
            if (int3 >= 0) {
                this.vehiclesWaitUpdates.removeAt(int3);
            }
        } else if (vehicle == null && square != null) {
            this.sendRequestGetFull(short0, PacketTypes.PacketType.Vehicles);
            if (byteBuffer.limit() > byteBuffer.position() + short2) {
                byteBuffer.position(byteBuffer.position() + short2);
            }
        } else {
            if ((short1 & 2) != 0) {
                if (!vehicle.isKeyboardControlled() && vehicle.getJoypad() == -1) {
                    vehicle.interpolation.interpolationDataAdd(byteBuffer, byteBuffer.getLong(), float0, float1, float2, GameTime.getServerTimeMills());
                } else if (byteBuffer.limit() > byteBuffer.position() + 102) {
                    byteBuffer.position(byteBuffer.position() + 102);
                }
            }

            if ((short1 & 4) != 0) {
                DebugLog.Vehicle.trace("received update Engine id=%d", short0);
                byte byte0 = byteBuffer.get();
                if (byte0 >= 0 && byte0 < BaseVehicle.engineStateTypes.Values.length) {
                    switch (BaseVehicle.engineStateTypes.Values[byte0]) {
                        case Idle:
                            vehicle.engineDoIdle();
                        case Starting:
                        default:
                            break;
                        case RetryingStarting:
                            vehicle.engineDoRetryingStarting();
                            break;
                        case StartingSuccess:
                            vehicle.engineDoStartingSuccess();
                            break;
                        case StartingFailed:
                            vehicle.engineDoStartingFailed();
                            break;
                        case StartingFailedNoPower:
                            vehicle.engineDoStartingFailedNoPower();
                            break;
                        case Running:
                            vehicle.engineDoRunning();
                            break;
                        case Stalling:
                            vehicle.engineDoStalling();
                            break;
                        case ShutingDown:
                            vehicle.engineDoShuttingDown();
                    }

                    vehicle.engineLoudness = byteBuffer.getInt();
                    vehicle.enginePower = byteBuffer.getInt();
                    vehicle.engineQuality = byteBuffer.getInt();
                } else {
                    DebugLog.Vehicle.error("VehicleManager.clientReceiveUpdate get invalid data");
                }
            }

            if ((short1 & 4096) != 0) {
                DebugLog.Vehicle.trace("received car properties update id=%d", short0);
                vehicle.setHotwired(byteBuffer.get() == 1);
                vehicle.setHotwiredBroken(byteBuffer.get() == 1);
                vehicle.setRegulatorSpeed(byteBuffer.getFloat());
                vehicle.setPreviouslyEntered(byteBuffer.get() == 1);
                boolean boolean1 = byteBuffer.get() == 1;
                boolean boolean2 = byteBuffer.get() == 1;
                InventoryItem item0 = null;
                if (byteBuffer.get() == 1) {
                    try {
                        item0 = InventoryItem.loadItem(byteBuffer, 195);
                    } catch (Exception exception0) {
                        exception0.printStackTrace();
                    }
                }

                vehicle.syncKeyInIgnition(boolean1, boolean2, item0);
                vehicle.setRust(byteBuffer.getFloat());
                vehicle.setBloodIntensity("Front", byteBuffer.getFloat());
                vehicle.setBloodIntensity("Rear", byteBuffer.getFloat());
                vehicle.setBloodIntensity("Left", byteBuffer.getFloat());
                vehicle.setBloodIntensity("Right", byteBuffer.getFloat());
                vehicle.setColorHSV(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
                vehicle.setSkinIndex(byteBuffer.getInt());
                vehicle.updateSkin();
            }

            if ((short1 & 8) != 0) {
                DebugLog.Vehicle.trace("received update Lights id=%d", short0);
                vehicle.setHeadlightsOn(byteBuffer.get() == 1);
                vehicle.setStoplightsOn(byteBuffer.get() == 1);

                for (int int4 = 0; int4 < vehicle.getLightCount(); int4++) {
                    boolean boolean3 = byteBuffer.get() == 1;
                    vehicle.getLightByIndex(int4).getLight().setActive(boolean3);
                }
            }

            if ((short1 & 1024) != 0) {
                DebugLog.Vehicle.trace("received update Sounds id=%d", short0);
                boolean boolean4 = byteBuffer.get() == 1;
                boolean boolean5 = byteBuffer.get() == 1;
                byte byte1 = byteBuffer.get();
                byte byte2 = byteBuffer.get();
                if (boolean4 != vehicle.soundHornOn) {
                    if (boolean4) {
                        vehicle.onHornStart();
                    } else {
                        vehicle.onHornStop();
                    }
                }

                if (boolean5 != vehicle.soundBackMoveOn) {
                    if (boolean5) {
                        vehicle.onBackMoveSignalStart();
                    } else {
                        vehicle.onBackMoveSignalStop();
                    }
                }

                if (vehicle.lightbarLightsMode.get() != byte1) {
                    vehicle.setLightbarLightsMode(byte1);
                }

                if (vehicle.lightbarSirenMode.get() != byte2) {
                    vehicle.setLightbarSirenMode(byte2);
                }
            }

            if ((short1 & 2048) != 0) {
                for (byte byte3 = byteBuffer.get(); byte3 != -1; byte3 = byteBuffer.get()) {
                    VehiclePart part0 = vehicle.getPartByIndex(byte3);
                    DebugLog.Vehicle.trace("received update PartCondition id=%d part=%s", short0, part0.getId());
                    part0.updateFlags = (short)(part0.updateFlags | 2048);
                    part0.setCondition(byteBuffer.getInt());
                }

                vehicle.doDamageOverlay();
            }

            if ((short1 & 16) != 0) {
                for (byte byte4 = byteBuffer.get(); byte4 != -1; byte4 = byteBuffer.get()) {
                    VehiclePart part1 = vehicle.getPartByIndex(byte4);
                    DebugLog.Vehicle.trace("received update PartModData id=%d part=%s", short0, part1.getId());
                    part1.getModData().load(byteBuffer, 195);
                    if (part1.isContainer()) {
                        part1.setContainerContentAmount(part1.getContainerContentAmount());
                    }
                }
            }

            if ((short1 & 32) != 0) {
                for (byte byte5 = byteBuffer.get(); byte5 != -1; byte5 = byteBuffer.get()) {
                    float float5 = byteBuffer.getFloat();
                    VehiclePart part2 = vehicle.getPartByIndex(byte5);
                    DebugLog.Vehicle.trace("received update PartUsedDelta id=%d part=%s", short0, part2.getId());
                    InventoryItem item1 = part2.getInventoryItem();
                    if (item1 instanceof DrainableComboItem) {
                        ((DrainableComboItem)item1).setUsedDelta(float5);
                    }
                }
            }

            if ((short1 & 128) != 0) {
                for (byte byte6 = byteBuffer.get(); byte6 != -1; byte6 = byteBuffer.get()) {
                    VehiclePart part3 = vehicle.getPartByIndex(byte6);
                    DebugLog.Vehicle.trace("received update PartItem id=%d part=%s", short0, part3.getId());
                    part3.updateFlags = (short)(part3.updateFlags | 128);
                    boolean boolean6 = byteBuffer.get() != 0;
                    if (boolean6) {
                        InventoryItem item2;
                        try {
                            item2 = InventoryItem.loadItem(byteBuffer, 195);
                        } catch (Exception exception1) {
                            exception1.printStackTrace();
                            return;
                        }

                        if (item2 != null) {
                            part3.setInventoryItem(item2);
                        }
                    } else {
                        part3.setInventoryItem(null);
                    }

                    int int5 = part3.getWheelIndex();
                    if (int5 != -1) {
                        vehicle.setTireRemoved(int5, !boolean6);
                    }

                    if (part3.isContainer()) {
                        LuaEventManager.triggerEvent("OnContainerUpdate");
                    }
                }
            }

            if ((short1 & 512) != 0) {
                for (byte byte7 = byteBuffer.get(); byte7 != -1; byte7 = byteBuffer.get()) {
                    VehiclePart part4 = vehicle.getPartByIndex(byte7);
                    DebugLog.Vehicle.trace("received update PartDoor id=%d part=%s", short0, part4.getId());
                    part4.getDoor().load(byteBuffer, 195);
                }

                LuaEventManager.triggerEvent("OnContainerUpdate");
                vehicle.doDamageOverlay();
            }

            if ((short1 & 256) != 0) {
                for (byte byte8 = byteBuffer.get(); byte8 != -1; byte8 = byteBuffer.get()) {
                    VehiclePart part5 = vehicle.getPartByIndex(byte8);
                    DebugLog.Vehicle.trace("received update PartWindow id=%d part=%s", short0, part5.getId());
                    part5.getWindow().load(byteBuffer, 195);
                }

                vehicle.doDamageOverlay();
            }

            if ((short1 & 64) != 0) {
                this.oldModels.clear();
                this.oldModels.addAll(vehicle.models);
                this.curModels.clear();
                byte byte9 = byteBuffer.get();

                for (int int6 = 0; int6 < byte9; int6++) {
                    byte byte10 = byteBuffer.get();
                    byte byte11 = byteBuffer.get();
                    VehiclePart part6 = vehicle.getPartByIndex(byte10);
                    VehicleScript.Model model = part6.getScriptPart().models.get(byte11);
                    BaseVehicle.ModelInfo modelInfo0 = vehicle.setModelVisible(part6, model, true);
                    this.curModels.add(modelInfo0);
                }

                for (int int7 = 0; int7 < this.oldModels.size(); int7++) {
                    BaseVehicle.ModelInfo modelInfo1 = this.oldModels.get(int7);
                    if (!this.curModels.contains(modelInfo1)) {
                        vehicle.setModelVisible(modelInfo1.part, modelInfo1.scriptModel, false);
                    }
                }

                vehicle.doDamageOverlay();
            }

            boolean boolean7 = false;

            for (int int8 = 0; int8 < vehicle.getPartCount(); int8++) {
                VehiclePart part7 = vehicle.getPartByIndex(int8);
                if (part7.updateFlags != 0) {
                    if ((part7.updateFlags & 2048) != 0 && (part7.updateFlags & 128) == 0) {
                        part7.doInventoryItemStats(part7.getInventoryItem(), part7.getMechanicSkillInstaller());
                        boolean7 = true;
                    }

                    part7.updateFlags = 0;
                }
            }

            if (boolean7) {
                vehicle.updatePartStats();
                vehicle.updateBulletStats();
            }
        }
    }

    public void clientPacket(ByteBuffer byteBuffer) {
        byte byte0 = byteBuffer.get();
        switch (byte0) {
            case 1:
                short short8 = byteBuffer.getShort();
                DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte0), short8);
                byte byte3 = byteBuffer.get();
                String string2 = GameWindow.ReadString(byteBuffer);
                BaseVehicle vehicle7 = this.IDToVehicle.get(short8);
                if (vehicle7 != null) {
                    IsoGameCharacter character = vehicle7.getCharacter(byte3);
                    if (character != null) {
                        vehicle7.setCharacterPosition(character, byte3, string2);
                    }
                }
                break;
            case 2:
                short short15 = byteBuffer.getShort();
                short short16 = byteBuffer.getShort();
                byte byte6 = byteBuffer.get();
                DebugLog.Vehicle.trace("Vehicle enter vid=%d pid=%d seat=%d", short15, short16, Integer.valueOf(byte6));
                BaseVehicle vehicle10 = this.IDToVehicle.get(short15);
                if (vehicle10 == null) {
                    DebugLog.Vehicle.warn("Vehicle vid=%d not found", short15);
                } else {
                    IsoPlayer player3 = GameClient.IDToPlayerMap.get(short16);
                    if (player3 == null) {
                        DebugLog.Vehicle.warn("Player pid=%d not found", short16);
                    } else {
                        IsoPlayer player4 = Type.tryCastTo(vehicle10.getCharacter(byte6), IsoPlayer.class);
                        if (player4 != null && player4 != player3) {
                            DebugLog.Vehicle.warn(player3.getUsername() + " got in same seat as " + player4.getUsername());
                        } else {
                            vehicle10.enterRSync(byte6, player3, vehicle10);
                        }
                    }
                }
                break;
            case 3:
                short short1 = byteBuffer.getShort();
                short short2 = byteBuffer.getShort();
                byte byte1 = byteBuffer.get();
                DebugLog.Vehicle.trace("Vehicle exit vid=%d pid=%d seat=%d", short1, short2, Integer.valueOf(byte1));
                BaseVehicle vehicle1 = this.IDToVehicle.get(short1);
                if (vehicle1 == null) {
                    DebugLog.Vehicle.warn("Vehicle vid=%d not found", short1);
                } else {
                    IsoPlayer player0 = GameClient.IDToPlayerMap.get(short2);
                    if (player0 == null) {
                        DebugLog.Vehicle.warn("Player pid=%d not found", short2);
                    } else {
                        vehicle1.exitRSync(player0);
                    }
                }
                break;
            case 4:
                short short13 = byteBuffer.getShort();
                short short14 = byteBuffer.getShort();
                byte byte4 = byteBuffer.get();
                byte byte5 = byteBuffer.get();
                DebugLog.Vehicle.trace("Vehicle switch seat vid=%d pid=%d seats=%d=>%d", short13, short14, Integer.valueOf(byte4), Integer.valueOf(byte5));
                BaseVehicle vehicle9 = this.IDToVehicle.get(short13);
                if (vehicle9 == null) {
                    DebugLog.Vehicle.warn("Vehicle vid=%d not found", short13);
                } else {
                    IsoPlayer player1 = GameClient.IDToPlayerMap.get(short14);
                    if (player1 == null) {
                        DebugLog.Vehicle.warn("Player pid=%d not found", short14);
                    } else {
                        IsoPlayer player2 = Type.tryCastTo(vehicle9.getCharacter(byte5), IsoPlayer.class);
                        if (player2 != null && player2 != player1) {
                            DebugLog.Vehicle.warn(player1.getUsername() + " switched to same seat as " + player2.getUsername());
                        } else {
                            vehicle9.switchSeat(player1, byte5);
                        }
                    }
                }
                break;
            case 5:
                if (this.tempVehicle == null || this.tempVehicle.getCell() != IsoWorld.instance.CurrentCell) {
                    this.tempVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
                }

                short short17 = byteBuffer.getShort();

                for (int int1 = 0; int1 < short17; int1++) {
                    try {
                        this.clientReceiveUpdate(byteBuffer);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        return;
                    }
                }
                break;
            case 6:
            case 7:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
            default:
                DebugLog.Vehicle.warn("Unknown vehicle packet %d", byte0);
                break;
            case 8:
                short short9 = byteBuffer.getShort();
                DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte0), short9);
                if (this.IDToVehicle.containsKey(short9)) {
                    BaseVehicle vehicle8 = this.IDToVehicle.get(short9);
                    vehicle8.serverRemovedFromWorld = true;

                    try {
                        vehicle8.removeFromWorld();
                        vehicle8.removeFromSquare();
                    } finally {
                        if (this.IDToVehicle.containsKey(short9)) {
                            this.unregisterVehicle(vehicle8);
                        }
                    }
                }

                VehicleCache.remove(short9);
                break;
            case 9:
                Physics physics = new Physics();
                physics.parse(byteBuffer, GameClient.connection);
                physics.process();
                break;
            case 13:
                short short0 = byteBuffer.getShort();
                DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte0), short0);
                Vector3f vector3f0 = new Vector3f();
                Vector3f vector3f1 = new Vector3f();
                vector3f0.x = byteBuffer.getFloat();
                vector3f0.y = byteBuffer.getFloat();
                vector3f0.z = byteBuffer.getFloat();
                vector3f1.x = byteBuffer.getFloat();
                vector3f1.y = byteBuffer.getFloat();
                vector3f1.z = byteBuffer.getFloat();
                BaseVehicle vehicle0 = this.IDToVehicle.get(short0);
                if (vehicle0 != null) {
                    Bullet.applyCentralForceToVehicle(vehicle0.VehicleID, vector3f0.x, vector3f0.y, vector3f0.z);
                    Vector3f vector3f2 = vector3f1.cross(vector3f0);
                    Bullet.applyTorqueToVehicle(vehicle0.VehicleID, vector3f2.x, vector3f2.y, vector3f2.z);
                }
                break;
            case 16:
                short short7 = byteBuffer.getShort();
                DebugLog.Vehicle.trace("%s vid=%d", vehiclePacketTypes.get(byte0), short7);
                byte byte2 = byteBuffer.get();
                BaseVehicle vehicle6 = this.IDToVehicle.get(short7);
                if (vehicle6 != null) {
                    SoundManager.instance.PlayWorldSound("VehicleCrash", vehicle6.square, 1.0F, 20.0F, 1.0F, true);
                }
                break;
            case 17:
                short short3 = byteBuffer.getShort();
                short short4 = byteBuffer.getShort();
                String string0 = GameWindow.ReadString(byteBuffer);
                String string1 = GameWindow.ReadString(byteBuffer);
                DebugLog.Vehicle.trace("Vehicle attach A=%d/%s B=%d/%s", short3, string0, short4, string1);
                this.towedVehicleMap.put(short3, short4);
                BaseVehicle vehicle2 = this.IDToVehicle.get(short3);
                BaseVehicle vehicle3 = this.IDToVehicle.get(short4);
                if (vehicle2 != null && vehicle3 != null) {
                    vehicle2.addPointConstraint(null, vehicle3, string0, string1);
                }
                break;
            case 18:
                short short5 = byteBuffer.getShort();
                short short6 = byteBuffer.getShort();
                DebugLog.Vehicle.trace("Vehicle detach A=%d B=%d", short5, short6);
                if (this.towedVehicleMap.containsKey(short5)) {
                    this.towedVehicleMap.remove(short5);
                }

                if (this.towedVehicleMap.containsKey(short6)) {
                    this.towedVehicleMap.remove(short6);
                }

                BaseVehicle vehicle4 = this.IDToVehicle.get(short5);
                BaseVehicle vehicle5 = this.IDToVehicle.get(short6);
                if (vehicle4 != null) {
                    vehicle4.breakConstraint(true, true);
                }

                if (vehicle5 != null) {
                    vehicle5.breakConstraint(true, true);
                }
                break;
            case 19:
                short short10 = byteBuffer.getShort();

                for (int int0 = 0; int0 < short10; int0++) {
                    short short11 = byteBuffer.getShort();
                    short short12 = byteBuffer.getShort();
                    this.towedVehicleMap.put(short11, short12);
                }
        }
    }

    public void sendCollide(BaseVehicle vehicle, IsoGameCharacter character, boolean boolean0) {
        short short0 = character == null ? -1 : ((IsoPlayer)character).OnlineID;
        ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)15);
        byteBufferWriter.bb.putShort(vehicle.VehicleID);
        byteBufferWriter.bb.putShort(short0);
        byteBufferWriter.bb.put((byte)(boolean0 ? 1 : 0));
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
        DebugLog.Vehicle.trace("vid=%d pid=%d collide=%b", vehicle.VehicleID, short0, boolean0);
    }

    public static void sendSound(BaseVehicle vehicle, byte byte0) {
        ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)16);
        byteBufferWriter.bb.putShort(vehicle.VehicleID);
        byteBufferWriter.bb.put(byte0);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }

    public static void sendSoundFromServer(BaseVehicle vehicle, byte byte0) {
        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put((byte)16);
            byteBufferWriter.bb.putShort(vehicle.VehicleID);
            byteBufferWriter.bb.put(byte0);
            PacketTypes.PacketType.Vehicles.send(udpConnection);
        }
    }

    public void sendPassengerPosition(BaseVehicle vehicle, int int0, String string) {
        ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)1);
        byteBufferWriter.bb.putShort(vehicle.VehicleID);
        byteBufferWriter.bb.put((byte)int0);
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.Vehicles.send(GameClient.connection);
    }

    public void sendPassengerPosition(BaseVehicle vehicle, int int1, String string, UdpConnection udpConnection1) {
        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection0 = GameServer.udpEngine.connections.get(int0);
            if (udpConnection0 != udpConnection1) {
                ByteBufferWriter byteBufferWriter = udpConnection0.startPacket();
                PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
                byteBufferWriter.bb.put((byte)1);
                byteBufferWriter.bb.putShort(vehicle.VehicleID);
                byteBufferWriter.bb.put((byte)int1);
                byteBufferWriter.putUTF(string);
                PacketTypes.PacketType.Vehicles.send(udpConnection0);
            }
        }
    }

    public void sendRequestGetFull(short short0, PacketTypes.PacketType var2) {
        if (!this.vehiclesWaitUpdates.contains(short0)) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put((byte)11);
            byteBufferWriter.bb.putShort((short)1);
            byteBufferWriter.bb.putShort(short0);
            PacketTypes.PacketType.Vehicles.send(GameClient.connection);
            this.vehiclesWaitUpdates.add(short0);
        }
    }

    public void sendRequestGetFull(List<VehicleCache> list) {
        if (list != null && !list.isEmpty()) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put((byte)11);
            byteBufferWriter.bb.putShort((short)list.size());

            for (int int0 = 0; int0 < list.size(); int0++) {
                byteBufferWriter.bb.putShort(((VehicleCache)list.get(int0)).id);
                this.vehiclesWaitUpdates.add(((VehicleCache)list.get(int0)).id);
            }

            PacketTypes.PacketType.Vehicles.send(GameClient.connection);
        }
    }

    public void sendRequestGetPosition(short short0, PacketTypes.PacketType packetType) {
        if (this.sendRequestGetPositionFrequency.Check()) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            packetType.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put((byte)12);
            byteBufferWriter.bb.putShort(short0);
            packetType.send(GameClient.connection);
            this.vehiclesWaitUpdates.add(short0);
        }
    }

    public void sendAddImpulse(BaseVehicle vehicle, Vector3f vector3f0, Vector3f vector3f1) {
        UdpConnection udpConnection0 = null;

        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size() && udpConnection0 == null; int0++) {
            UdpConnection udpConnection1 = GameServer.udpEngine.connections.get(int0);

            for (int int1 = 0; int1 < udpConnection1.players.length; int1++) {
                IsoPlayer player = udpConnection1.players[int1];
                if (player != null && player.getVehicle() != null && player.getVehicle().VehicleID == vehicle.VehicleID) {
                    udpConnection0 = udpConnection1;
                    break;
                }
            }
        }

        if (udpConnection0 != null) {
            ByteBufferWriter byteBufferWriter = udpConnection0.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put((byte)13);
            byteBufferWriter.bb.putShort(vehicle.VehicleID);
            byteBufferWriter.bb.putFloat(vector3f0.x);
            byteBufferWriter.bb.putFloat(vector3f0.y);
            byteBufferWriter.bb.putFloat(vector3f0.z);
            byteBufferWriter.bb.putFloat(vector3f1.x);
            byteBufferWriter.bb.putFloat(vector3f1.y);
            byteBufferWriter.bb.putFloat(vector3f1.z);
            PacketTypes.PacketType.Vehicles.send(udpConnection0);
        }
    }

    public void sendSwitchSeat(UdpConnection udpConnection, BaseVehicle vehicle, IsoGameCharacter character, int int0, int int1) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)4);
        byteBufferWriter.bb.putShort(vehicle.getId());
        byteBufferWriter.bb.putShort(character.getOnlineID());
        byteBufferWriter.bb.put((byte)int0);
        byteBufferWriter.bb.put((byte)int1);
        PacketTypes.PacketType.Vehicles.send(udpConnection);
    }

    public void sendSwitchSeat(BaseVehicle vehicle, IsoGameCharacter character, int int0, int int1) {
        for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
            this.sendSwitchSeat(udpConnection, vehicle, character, int0, int1);
        }
    }

    public void sendEnter(UdpConnection udpConnection, BaseVehicle vehicle, IsoGameCharacter character, int int0) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)2);
        byteBufferWriter.bb.putShort(vehicle.getId());
        byteBufferWriter.bb.putShort(character.getOnlineID());
        byteBufferWriter.bb.put((byte)int0);
        PacketTypes.PacketType.Vehicles.send(udpConnection);
    }

    public void sendEnter(BaseVehicle vehicle, IsoGameCharacter character, int int0) {
        for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
            this.sendEnter(udpConnection, vehicle, character, int0);
        }
    }

    public void sendExit(UdpConnection udpConnection, BaseVehicle vehicle, IsoGameCharacter character, int int0) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)3);
        byteBufferWriter.bb.putShort(vehicle.getId());
        byteBufferWriter.bb.putShort(character.getOnlineID());
        byteBufferWriter.bb.put((byte)int0);
        PacketTypes.PacketType.Vehicles.send(udpConnection);
    }

    public void sendExit(BaseVehicle vehicle, IsoGameCharacter character, int int0) {
        for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
            this.sendExit(udpConnection, vehicle, character, (byte)int0);
        }
    }

    public void sendPhysic(BaseVehicle vehicle) {
        ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
        PacketTypes.PacketType packetType = vehicle.isReliable ? PacketTypes.PacketType.Vehicles : PacketTypes.PacketType.VehiclesUnreliable;
        packetType.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)9);
        Physics physics = new Physics();
        if (physics.set(vehicle)) {
            physics.write(byteBufferWriter);
            packetType.send(GameClient.connection);
        } else {
            GameClient.connection.cancelPacket();
        }
    }

    public void sendTowing(UdpConnection udpConnection, BaseVehicle vehicle1, BaseVehicle vehicle0, String string0, String string1) {
        DebugLog.Vehicle.trace("vidA=%d vidB=%d", vehicle1.VehicleID, vehicle0.VehicleID);
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)17);
        byteBufferWriter.bb.putShort(vehicle1.VehicleID);
        byteBufferWriter.bb.putShort(vehicle0.VehicleID);
        GameWindow.WriteString(byteBufferWriter.bb, string0);
        GameWindow.WriteString(byteBufferWriter.bb, string1);
        PacketTypes.PacketType.Vehicles.send(udpConnection);
    }

    public void sendTowing(BaseVehicle vehicle0, BaseVehicle vehicle1, String string0, String string1) {
        if (!this.towedVehicleMap.containsKey(vehicle0.VehicleID)) {
            this.towedVehicleMap.put(vehicle0.VehicleID, vehicle1.VehicleID);

            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                this.sendTowing(udpConnection, vehicle0, vehicle1, string0, string1);
            }
        }
    }

    public void sendDetachTowing(UdpConnection udpConnection, BaseVehicle vehicle0, BaseVehicle vehicle1) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.Vehicles.doPacket(byteBufferWriter);
        byteBufferWriter.bb.put((byte)18);
        byteBufferWriter.bb.putShort(vehicle0 == null ? -1 : vehicle0.VehicleID);
        byteBufferWriter.bb.putShort(vehicle1 == null ? -1 : vehicle1.VehicleID);
        PacketTypes.PacketType.Vehicles.send(udpConnection);
    }

    public void sendDetachTowing(BaseVehicle vehicle0, BaseVehicle vehicle1) {
        if (vehicle0 != null && this.towedVehicleMap.containsKey(vehicle0.VehicleID)) {
            this.towedVehicleMap.remove(vehicle0.VehicleID);
        }

        if (vehicle1 != null && this.towedVehicleMap.containsKey(vehicle1.VehicleID)) {
            this.towedVehicleMap.remove(vehicle1.VehicleID);
        }

        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
            UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
            this.sendDetachTowing(udpConnection, vehicle0, vehicle1);
        }
    }

    public short getTowedVehicleID(short short0) {
        return this.towedVehicleMap.containsKey(short0) ? this.towedVehicleMap.get(short0) : -1;
    }

    private void writePositionOrientation(ByteBuffer byteBuffer, BaseVehicle vehicle) {
        byteBuffer.putLong(WorldSimulation.instance.time);
        Quaternionf quaternionf = vehicle.savedRot;
        Transform transform = vehicle.getWorldTransform(this.tempTransform);
        transform.getRotation(quaternionf);
        byteBuffer.putFloat(quaternionf.x);
        byteBuffer.putFloat(quaternionf.y);
        byteBuffer.putFloat(quaternionf.z);
        byteBuffer.putFloat(quaternionf.w);
        byteBuffer.putFloat(vehicle.jniLinearVelocity.x);
        byteBuffer.putFloat(vehicle.jniLinearVelocity.y);
        byteBuffer.putFloat(vehicle.jniLinearVelocity.z);
        byteBuffer.putFloat((float)vehicle.engineSpeed);
        byteBuffer.putFloat(vehicle.throttle);
        byteBuffer.putShort((short)vehicle.wheelInfo.length);

        for (int int0 = 0; int0 < vehicle.wheelInfo.length; int0++) {
            byteBuffer.putFloat(vehicle.wheelInfo[int0].steering);
            byteBuffer.putFloat(vehicle.wheelInfo[int0].rotation);
            byteBuffer.putFloat(vehicle.wheelInfo[int0].skidInfo);
            byteBuffer.putFloat(vehicle.wheelInfo[int0].suspensionLength);
        }
    }

    static {
        vehiclePacketTypes.put((byte)1, "PassengerPosition");
        vehiclePacketTypes.put((byte)2, "Enter");
        vehiclePacketTypes.put((byte)3, "Exit");
        vehiclePacketTypes.put((byte)4, "SwitchSeat");
        vehiclePacketTypes.put((byte)5, "Update");
        vehiclePacketTypes.put((byte)8, "Remove");
        vehiclePacketTypes.put((byte)9, "Physic");
        vehiclePacketTypes.put((byte)10, "Config");
        vehiclePacketTypes.put((byte)11, "RequestGetFull");
        vehiclePacketTypes.put((byte)12, "RequestGetPosition");
        vehiclePacketTypes.put((byte)13, "AddImpulse");
        vehiclePacketTypes.put((byte)15, "Collide");
        vehiclePacketTypes.put((byte)16, "Sound");
        vehiclePacketTypes.put((byte)17, "TowingCar");
        vehiclePacketTypes.put((byte)18, "DetachTowingCar");
        vehiclePacketTypes.put((byte)19, "InitialWorldState");
    }

    public static final class PosUpdateVars {
        final Transform transform = new Transform();
        final Vector3f vector3f = new Vector3f();
        final Quaternionf quatf = new Quaternionf();
        final float[] wheelSteer = new float[4];
        final float[] wheelRotation = new float[4];
        final float[] wheelSkidInfo = new float[4];
        final float[] wheelSuspensionLength = new float[4];
    }

    public static final class VehiclePacket {
        public static final byte PassengerPosition = 1;
        public static final byte Enter = 2;
        public static final byte Exit = 3;
        public static final byte SwitchSeat = 4;
        public static final byte Update = 5;
        public static final byte Remove = 8;
        public static final byte Physic = 9;
        public static final byte Config = 10;
        public static final byte RequestGetFull = 11;
        public static final byte RequestGetPosition = 12;
        public static final byte AddImpulse = 13;
        public static final byte Collide = 15;
        public static final byte Sound = 16;
        public static final byte TowingCar = 17;
        public static final byte DetachTowingCar = 18;
        public static final byte InitialWorldState = 19;
        public static final byte Sound_Crash = 1;
    }
}
