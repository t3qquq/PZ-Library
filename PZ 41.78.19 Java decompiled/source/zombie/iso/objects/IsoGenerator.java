// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;

public class IsoGenerator extends IsoObject {
    public float fuel = 0.0F;
    public boolean activated = false;
    public int condition = 0;
    private int lastHour = -1;
    public boolean connected = false;
    private int numberOfElectricalItems = 0;
    private boolean updateSurrounding = false;
    private final HashMap<String, String> itemsPowered = new HashMap<>();
    private float totalPowerUsing = 0.0F;
    private static final ArrayList<IsoGenerator> AllGenerators = new ArrayList<>();
    private static final int GENERATOR_RADIUS = 20;

    public IsoGenerator(IsoCell cell) {
        super(cell);
    }

    public IsoGenerator(InventoryItem item, IsoCell cell, IsoGridSquare sq) {
        super(cell, sq, IsoSpriteManager.instance.getSprite("appliances_misc_01_0"));
        if (item != null) {
            this.setInfoFromItem(item);
        }

        this.sprite = IsoSpriteManager.instance.getSprite("appliances_misc_01_0");
        this.square = sq;
        sq.AddSpecialObject(this);
        if (GameClient.bClient) {
            this.transmitCompleteItemToServer();
        }
    }

    public IsoGenerator(InventoryItem item, IsoCell cell, IsoGridSquare sq, boolean remote) {
        super(cell, sq, IsoSpriteManager.instance.getSprite("appliances_misc_01_0"));
        if (item != null) {
            this.setInfoFromItem(item);
        }

        this.sprite = IsoSpriteManager.instance.getSprite("appliances_misc_01_0");
        this.square = sq;
        sq.AddSpecialObject(this);
        if (GameClient.bClient && !remote) {
            this.transmitCompleteItemToServer();
        }
    }

    public void setInfoFromItem(InventoryItem item) {
        this.condition = item.getCondition();
        if (item.getModData().rawget("fuel") instanceof Double) {
            this.fuel = ((Double)item.getModData().rawget("fuel")).floatValue();
        }
    }

    @Override
    public void update() {
        if (this.updateSurrounding && this.getSquare() != null) {
            this.setSurroundingElectricity();
            this.updateSurrounding = false;
        }

        if (this.isActivated()) {
            if (!GameServer.bServer && (this.emitter == null || !this.emitter.isPlaying("GeneratorLoop"))) {
                if (this.emitter == null) {
                    this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ());
                    IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
                }

                this.emitter.playSoundLoopedImpl("GeneratorLoop");
            }

            if (GameClient.bClient) {
                this.emitter.tick();
                return;
            }

            WorldSoundManager.instance.addSoundRepeating(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 20, 1, false);
            if ((int)GameTime.getInstance().getWorldAgeHours() != this.lastHour) {
                if (!this.getSquare().getProperties().Is(IsoFlagType.exterior) && this.getSquare().getBuilding() != null) {
                    this.getSquare().getBuilding().setToxic(false);
                    this.getSquare().getBuilding().setToxic(this.isActivated());
                }

                int int0 = (int)GameTime.getInstance().getWorldAgeHours() - this.lastHour;
                float float0 = 0.0F;
                int int1 = 0;

                for (int int2 = 0; int2 < int0; int2++) {
                    float float1 = this.totalPowerUsing;
                    float1 = (float)(float1 * SandboxOptions.instance.GeneratorFuelConsumption.getValue());
                    float0 += float1;
                    if (Rand.Next(30) == 0) {
                        int1 += Rand.Next(2) + 1;
                    }

                    if (this.fuel - float0 <= 0.0F || this.condition - int1 <= 0) {
                        break;
                    }
                }

                this.fuel -= float0;
                if (this.fuel <= 0.0F) {
                    this.setActivated(false);
                    this.fuel = 0.0F;
                }

                this.condition -= int1;
                if (this.condition <= 0) {
                    this.setActivated(false);
                    this.condition = 0;
                }

                if (this.condition <= 20) {
                    if (Rand.Next(10) == 0) {
                        IsoFireManager.StartFire(this.getCell(), this.square, true, 1000);
                        this.condition = 0;
                        this.setActivated(false);
                    } else if (Rand.Next(20) == 0) {
                        this.square.explode();
                        this.condition = 0;
                        this.setActivated(false);
                    }
                }

                this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
                if (GameServer.bServer) {
                    this.syncIsoObject(false, (byte)0, null, null);
                }
            }
        }

        if (this.emitter != null) {
            this.emitter.tick();
        }
    }

    public void setSurroundingElectricity() {
        this.itemsPowered.clear();
        this.totalPowerUsing = 0.02F;
        this.numberOfElectricalItems = 1;
        if (this.square != null && this.square.chunk != null) {
            int int0 = this.square.chunk.wx;
            int int1 = this.square.chunk.wy;

            for (int int2 = -2; int2 <= 2; int2++) {
                for (int int3 = -2; int3 <= 2; int3++) {
                    IsoChunk chunk = GameServer.bServer
                        ? ServerMap.instance.getChunk(int0 + int3, int1 + int2)
                        : IsoWorld.instance.CurrentCell.getChunk(int0 + int3, int1 + int2);
                    if (chunk != null && this.touchesChunk(chunk)) {
                        if (this.isActivated()) {
                            chunk.addGeneratorPos(this.square.x, this.square.y, this.square.z);
                        } else {
                            chunk.removeGeneratorPos(this.square.x, this.square.y, this.square.z);
                        }
                    }
                }
            }

            boolean boolean0 = SandboxOptions.getInstance().AllowExteriorGenerator.getValue();
            int int4 = this.square.getX() - 20;
            int int5 = this.square.getX() + 20;
            int int6 = this.square.getY() - 20;
            int int7 = this.square.getY() + 20;
            int int8 = Math.max(0, this.getSquare().getZ() - 3);
            int int9 = Math.min(8, this.getSquare().getZ() + 3);

            for (int int10 = int8; int10 < int9; int10++) {
                for (int int11 = int4; int11 <= int5; int11++) {
                    for (int int12 = int6; int12 <= int7; int12++) {
                        if (!(IsoUtils.DistanceToSquared(int11 + 0.5F, int12 + 0.5F, this.getSquare().getX() + 0.5F, this.getSquare().getY() + 0.5F) > 400.0F)) {
                            IsoGridSquare square = this.getCell().getGridSquare(int11, int12, int10);
                            if (square != null) {
                                boolean boolean1 = this.isActivated();
                                if (!boolean0 && square.Is(IsoFlagType.exterior)) {
                                    boolean1 = false;
                                }

                                square.setHaveElectricity(boolean1);

                                for (int int13 = 0; int13 < square.getObjects().size(); int13++) {
                                    IsoObject object = square.getObjects().get(int13);
                                    if (object != null && !(object instanceof IsoWorldInventoryObject)) {
                                        if (object instanceof IsoClothingDryer && ((IsoClothingDryer)object).isActivated()) {
                                            this.addPoweredItem(object, 0.09F);
                                        }

                                        if (object instanceof IsoClothingWasher && ((IsoClothingWasher)object).isActivated()) {
                                            this.addPoweredItem(object, 0.09F);
                                        }

                                        if (object instanceof IsoCombinationWasherDryer && ((IsoCombinationWasherDryer)object).isActivated()) {
                                            this.addPoweredItem(object, 0.09F);
                                        }

                                        if (object instanceof IsoStackedWasherDryer stackedWasherDryer) {
                                            float float0 = 0.0F;
                                            if (stackedWasherDryer.isDryerActivated()) {
                                                float0 += 0.9F;
                                            }

                                            if (stackedWasherDryer.isWasherActivated()) {
                                                float0 += 0.9F;
                                            }

                                            if (float0 > 0.0F) {
                                                this.addPoweredItem(object, float0);
                                            }
                                        }

                                        if (object instanceof IsoTelevision && ((IsoTelevision)object).getDeviceData().getIsTurnedOn()) {
                                            this.addPoweredItem(object, 0.03F);
                                        }

                                        if (object instanceof IsoRadio
                                            && ((IsoRadio)object).getDeviceData().getIsTurnedOn()
                                            && !((IsoRadio)object).getDeviceData().getIsBatteryPowered()) {
                                            this.addPoweredItem(object, 0.01F);
                                        }

                                        if (object instanceof IsoStove && ((IsoStove)object).Activated()) {
                                            this.addPoweredItem(object, 0.09F);
                                        }

                                        boolean boolean2 = object.getContainerByType("fridge") != null;
                                        boolean boolean3 = object.getContainerByType("freezer") != null;
                                        if (boolean2 && boolean3) {
                                            this.addPoweredItem(object, 0.13F);
                                        } else if (boolean2 || boolean3) {
                                            this.addPoweredItem(object, 0.08F);
                                        }

                                        if (object instanceof IsoLightSwitch && ((IsoLightSwitch)object).Activated) {
                                            this.addPoweredItem(object, 0.002F);
                                        }

                                        object.checkHaveElectricity();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addPoweredItem(IsoObject object, float float0) {
        String string0 = Translator.getText("IGUI_VehiclePartCatOther");
        PropertyContainer propertyContainer = object.getProperties();
        if (propertyContainer != null && propertyContainer.Is("CustomName")) {
            String string1 = "Moveable Object";
            if (propertyContainer.Is("CustomName")) {
                if (propertyContainer.Is("GroupName")) {
                    string1 = propertyContainer.Val("GroupName") + " " + propertyContainer.Val("CustomName");
                } else {
                    string1 = propertyContainer.Val("CustomName");
                }
            }

            string0 = Translator.getMoveableDisplayName(string1);
        }

        if (object instanceof IsoLightSwitch) {
            string0 = Translator.getText("IGUI_Lights");
        }

        this.totalPowerUsing -= float0;
        int int0 = 1;

        for (String string2 : this.itemsPowered.keySet()) {
            if (string2.startsWith(string0)) {
                int0 = Integer.parseInt(string2.replaceAll("[\\D]", ""));
                int0++;
                this.itemsPowered.remove(string2);
                break;
            }
        }

        this.itemsPowered.put(string0 + " x" + int0, String.format(" (%.2f L/h)", float0 * int0));
        if (int0 == 1) {
            this.totalPowerUsing += float0 * (int0 + 1);
        } else {
            this.totalPowerUsing += float0 * int0;
        }
    }

    private void updateFridgeFreezerItems(IsoObject object) {
        for (int int0 = 0; int0 < object.getContainerCount(); int0++) {
            ItemContainer container = object.getContainerByIndex(int0);
            if ("fridge".equals(container.getType()) || "freezer".equals(container.getType())) {
                ArrayList arrayList = container.getItems();

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    InventoryItem item = (InventoryItem)arrayList.get(int1);
                    if (item instanceof Food) {
                        item.updateAge();
                    }
                }
            }
        }
    }

    private void updateFridgeFreezerItems(IsoGridSquare square) {
        int int0 = square.getObjects().size();
        IsoObject[] objects = square.getObjects().getElements();

        for (int int1 = 0; int1 < int0; int1++) {
            IsoObject object = objects[int1];
            this.updateFridgeFreezerItems(object);
        }
    }

    private void updateFridgeFreezerItems() {
        if (this.square != null) {
            int int0 = this.square.getX() - 20;
            int int1 = this.square.getX() + 20;
            int int2 = this.square.getY() - 20;
            int int3 = this.square.getY() + 20;
            int int4 = Math.max(0, this.square.getZ() - 3);
            int int5 = Math.min(8, this.square.getZ() + 3);

            for (int int6 = int4; int6 < int5; int6++) {
                for (int int7 = int0; int7 <= int1; int7++) {
                    for (int int8 = int2; int8 <= int3; int8++) {
                        if (IsoUtils.DistanceToSquared(int7, int8, this.square.x, this.square.y) <= 400.0F) {
                            IsoGridSquare square = this.getCell().getGridSquare(int7, int8, int6);
                            if (square != null) {
                                this.updateFridgeFreezerItems(square);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.connected = input.get() == 1;
        this.activated = input.get() == 1;
        if (WorldVersion < 138) {
            this.fuel = input.getInt();
        } else {
            this.fuel = input.getFloat();
        }

        this.condition = input.getInt();
        this.lastHour = input.getInt();
        this.numberOfElectricalItems = input.getInt();
        this.updateSurrounding = true;
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.put((byte)(this.isConnected() ? 1 : 0));
        output.put((byte)(this.isActivated() ? 1 : 0));
        output.putFloat(this.getFuel());
        output.putInt(this.getCondition());
        output.putInt(this.lastHour);
        output.putInt(this.numberOfElectricalItems);
    }

    public void remove() {
        if (this.getSquare() != null) {
            this.getSquare().transmitRemoveItemFromSquare(this);
        }
    }

    @Override
    public void addToWorld() {
        this.getCell().addToProcessIsoObject(this);
        if (!AllGenerators.contains(this)) {
            AllGenerators.add(this);
        }

        if (GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequest(this.square, this);
        }
    }

    @Override
    public void removeFromWorld() {
        AllGenerators.remove(this);
        if (this.emitter != null) {
            this.emitter.stopAll();
            IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
            this.emitter = null;
        }

        super.removeFromWorld();
    }

    @Override
    public String getObjectName() {
        return "IsoGenerator";
    }

    public float getFuel() {
        return this.fuel;
    }

    public void setFuel(float _fuel) {
        this.fuel = _fuel;
        if (this.fuel > 100.0F) {
            this.fuel = 100.0F;
        }

        if (this.fuel < 0.0F) {
            this.fuel = 0.0F;
        }

        if (GameServer.bServer) {
            this.syncIsoObject(false, (byte)0, null, null);
        }

        if (GameClient.bClient) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean _activated) {
        if (_activated != this.activated) {
            if (!this.getSquare().getProperties().Is(IsoFlagType.exterior) && this.getSquare().getBuilding() != null) {
                this.getSquare().getBuilding().setToxic(false);
                this.getSquare().getBuilding().setToxic(_activated);
            }

            if (!GameServer.bServer && this.emitter == null) {
                this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, this.getZ());
                IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
            }

            if (_activated) {
                this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
                if (this.emitter != null) {
                    this.emitter.playSound("GeneratorStarting");
                }
            } else if (this.emitter != null) {
                if (!this.emitter.isEmpty()) {
                    this.emitter.stopAll();
                }

                this.emitter.playSound("GeneratorStopping");
            }

            try {
                this.updateFridgeFreezerItems();
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }

            this.activated = _activated;
            this.setSurroundingElectricity();
            if (GameClient.bClient) {
                this.syncIsoObject(false, (byte)0, null, null);
            }

            if (GameServer.bServer) {
                this.syncIsoObject(false, (byte)0, null, null);
            }
        }
    }

    public void failToStart() {
        if (!GameServer.bServer) {
            if (this.emitter == null) {
                this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, this.getZ());
                IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
            }

            this.emitter.playSound("GeneratorFailedToStart");
        }
    }

    public int getCondition() {
        return this.condition;
    }

    public void setCondition(int _condition) {
        this.condition = _condition;
        if (this.condition > 100) {
            this.condition = 100;
        }

        if (this.condition < 0) {
            this.condition = 0;
        }

        if (GameServer.bServer) {
            this.syncIsoObject(false, (byte)0, null, null);
        }

        if (GameClient.bClient) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void setConnected(boolean _connected) {
        this.connected = _connected;
        if (GameClient.bClient) {
            this.syncIsoObject(false, (byte)0, null, null);
        }
    }

    @Override
    public void syncIsoObjectSend(ByteBufferWriter b) {
        byte byte0 = (byte)this.getObjectIndex();
        b.putInt(this.square.getX());
        b.putInt(this.square.getY());
        b.putInt(this.square.getZ());
        b.putByte(byte0);
        b.putByte((byte)1);
        b.putByte((byte)0);
        b.putFloat(this.fuel);
        b.putInt(this.condition);
        b.putByte((byte)(this.activated ? 1 : 0));
        b.putByte((byte)(this.connected ? 1 : 0));
    }

    @Override
    public void syncIsoObject(boolean bRemote, byte val, UdpConnection source, ByteBuffer bb) {
        if (this.square == null) {
            System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
        } else if (this.getObjectIndex() == -1) {
            System.out
                .println(
                    "ERROR: "
                        + this.getClass().getSimpleName()
                        + " not found on square "
                        + this.square.getX()
                        + ","
                        + this.square.getY()
                        + ","
                        + this.square.getZ()
                );
        } else {
            if (GameClient.bClient && !bRemote) {
                ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter0);
                this.syncIsoObjectSend(byteBufferWriter0);
                PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
            } else if (GameServer.bServer && !bRemote) {
                for (UdpConnection udpConnection0 : GameServer.udpEngine.connections) {
                    ByteBufferWriter byteBufferWriter1 = udpConnection0.startPacket();
                    PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter1);
                    this.syncIsoObjectSend(byteBufferWriter1);
                    PacketTypes.PacketType.SyncIsoObject.send(udpConnection0);
                }
            } else if (bRemote) {
                float float0 = bb.getFloat();
                int int0 = bb.getInt();
                boolean boolean0 = bb.get() == 1;
                boolean boolean1 = bb.get() == 1;
                this.sync(float0, int0, boolean1, boolean0);
                if (GameServer.bServer) {
                    for (UdpConnection udpConnection1 : GameServer.udpEngine.connections) {
                        if (source != null && udpConnection1.getConnectedGUID() != source.getConnectedGUID()) {
                            ByteBufferWriter byteBufferWriter2 = udpConnection1.startPacket();
                            PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter2);
                            this.syncIsoObjectSend(byteBufferWriter2);
                            PacketTypes.PacketType.SyncIsoObject.send(udpConnection1);
                        }
                    }
                }
            }
        }
    }

    public void sync(float _fuel, int _condition, boolean _connected, boolean _activated) {
        this.fuel = _fuel;
        this.condition = _condition;
        this.connected = _connected;
        if (this.activated != _activated) {
            try {
                this.updateFridgeFreezerItems();
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }

            this.activated = _activated;
            if (_activated) {
                this.lastHour = (int)GameTime.getInstance().getWorldAgeHours();
            } else if (this.emitter != null) {
                this.emitter.stopAll();
            }

            this.setSurroundingElectricity();
        }
    }

    private boolean touchesChunk(IsoChunk chunk) {
        IsoGridSquare square = this.getSquare();

        assert square != null;

        if (square == null) {
            return false;
        } else {
            int int0 = chunk.wx * 10;
            int int1 = chunk.wy * 10;
            int int2 = int0 + 10 - 1;
            int int3 = int1 + 10 - 1;
            if (square.x - 20 > int2) {
                return false;
            } else if (square.x + 20 < int0) {
                return false;
            } else {
                return square.y - 20 > int3 ? false : square.y + 20 >= int1;
            }
        }
    }

    public static void chunkLoaded(IsoChunk chunk) {
        chunk.checkForMissingGenerators();

        for (int int0 = -2; int0 <= 2; int0++) {
            for (int int1 = -2; int1 <= 2; int1++) {
                if (int1 != 0 || int0 != 0) {
                    IsoChunk _chunk = GameServer.bServer
                        ? ServerMap.instance.getChunk(chunk.wx + int1, chunk.wy + int0)
                        : IsoWorld.instance.CurrentCell.getChunk(chunk.wx + int1, chunk.wy + int0);
                    if (_chunk != null) {
                        _chunk.checkForMissingGenerators();
                    }
                }
            }
        }

        for (int int2 = 0; int2 < AllGenerators.size(); int2++) {
            IsoGenerator generator = AllGenerators.get(int2);
            if (!generator.updateSurrounding && generator.touchesChunk(chunk)) {
                generator.updateSurrounding = true;
            }
        }
    }

    public static void updateSurroundingNow() {
        for (int int0 = 0; int0 < AllGenerators.size(); int0++) {
            IsoGenerator generator = AllGenerators.get(int0);
            if (generator.updateSurrounding && generator.getSquare() != null) {
                generator.updateSurrounding = false;
                generator.setSurroundingElectricity();
            }
        }
    }

    public static void updateGenerator(IsoGridSquare sq) {
        if (sq != null) {
            for (int int0 = 0; int0 < AllGenerators.size(); int0++) {
                IsoGenerator generator = AllGenerators.get(int0);
                if (generator.getSquare() != null) {
                    float float0 = IsoUtils.DistanceToSquared(
                        sq.x + 0.5F, sq.y + 0.5F, generator.getSquare().getX() + 0.5F, generator.getSquare().getY() + 0.5F
                    );
                    if (float0 <= 400.0F) {
                        generator.updateSurrounding = true;
                    }
                }
            }
        }
    }

    public static void Reset() {
        assert AllGenerators.isEmpty();

        AllGenerators.clear();
    }

    public static boolean isPoweringSquare(int generatorX, int generatorY, int generatorZ, int x, int y, int z) {
        int int0 = Math.max(0, generatorZ - 3);
        int int1 = Math.min(8, generatorZ + 3);
        return z >= int0 && z < int1 ? IsoUtils.DistanceToSquared(generatorX + 0.5F, generatorY + 0.5F, x + 0.5F, y + 0.5F) <= 400.0F : false;
    }

    public ArrayList<String> getItemsPowered() {
        ArrayList arrayList = new ArrayList();

        for (String string : this.itemsPowered.keySet()) {
            arrayList.add(string + this.itemsPowered.get(string));
        }

        arrayList.sort(String::compareToIgnoreCase);
        return arrayList;
    }

    public float getTotalPowerUsing() {
        return this.totalPowerUsing;
    }

    public void setTotalPowerUsing(float _totalPowerUsing) {
        this.totalPowerUsing = _totalPowerUsing;
    }
}
