// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import gnu.trove.list.array.TFloatArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SystemDisabler;
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.ImmutableColor;
import zombie.core.math.PZMath;
import zombie.core.physics.Bullet;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;

public final class VehicleScript extends BaseScriptObject {
    private String fileName;
    private String name;
    private final ArrayList<VehicleScript.Model> models = new ArrayList<>();
    public final ArrayList<ModelAttachment> m_attachments = new ArrayList<>();
    private float mass = 800.0F;
    private final Vector3f centerOfMassOffset = new Vector3f();
    private float engineForce = 3000.0F;
    private float engineIdleSpeed = 750.0F;
    private float steeringIncrement = 0.04F;
    private float steeringClamp = 0.4F;
    private float steeringClampMax = 0.9F;
    private float wheelFriction = 800.0F;
    private float stoppingMovementForce = 1.0F;
    private float suspensionStiffness = 20.0F;
    private float suspensionDamping = 2.3F;
    private float suspensionCompression = 4.4F;
    private float suspensionRestLength = 0.6F;
    private float maxSuspensionTravelCm = 500.0F;
    private float rollInfluence = 0.1F;
    private final Vector3f extents = new Vector3f(0.75F, 0.5F, 2.0F);
    private final Vector2f shadowExtents = new Vector2f(0.0F, 0.0F);
    private final Vector2f shadowOffset = new Vector2f(0.0F, 0.0F);
    private boolean bHadShadowOExtents = false;
    private boolean bHadShadowOffset = false;
    private final Vector2f extentsOffset = new Vector2f(0.5F, 0.5F);
    private final Vector3f physicsChassisShape = new Vector3f(0.75F, 0.5F, 1.0F);
    private final ArrayList<VehicleScript.PhysicsShape> m_physicsShapes = new ArrayList<>();
    private final ArrayList<VehicleScript.Wheel> wheels = new ArrayList<>();
    private final ArrayList<VehicleScript.Passenger> passengers = new ArrayList<>();
    public float maxSpeed = 20.0F;
    public boolean isSmallVehicle = true;
    public float spawnOffsetY = 0.0F;
    private int frontEndHealth = 100;
    private int rearEndHealth = 100;
    private int storageCapacity = 100;
    private int engineLoudness = 100;
    private int engineQuality = 100;
    private int seats = 2;
    private int mechanicType;
    private int engineRepairLevel;
    private float playerDamageProtection;
    private float forcedHue = -1.0F;
    private float forcedSat = -1.0F;
    private float forcedVal = -1.0F;
    public ImmutableColor leftSirenCol;
    public ImmutableColor rightSirenCol;
    private String engineRPMType = "jeep";
    private float offroadEfficiency = 1.0F;
    private final TFloatArrayList crawlOffsets = new TFloatArrayList();
    public int gearRatioCount = 0;
    public final float[] gearRatio = new float[9];
    private final VehicleScript.Skin textures = new VehicleScript.Skin();
    private final ArrayList<VehicleScript.Skin> skins = new ArrayList<>();
    private final ArrayList<VehicleScript.Area> areas = new ArrayList<>();
    private final ArrayList<VehicleScript.Part> parts = new ArrayList<>();
    private boolean hasSiren = false;
    private final VehicleScript.LightBar lightbar = new VehicleScript.LightBar();
    private final VehicleScript.Sounds sound = new VehicleScript.Sounds();
    public boolean textureMaskEnable = false;
    private static final int PHYSICS_SHAPE_BOX = 1;
    private static final int PHYSICS_SHAPE_SPHERE = 2;

    public VehicleScript() {
        this.gearRatioCount = 4;
        this.gearRatio[0] = 7.09F;
        this.gearRatio[1] = 6.44F;
        this.gearRatio[2] = 4.1F;
        this.gearRatio[3] = 2.29F;
        this.gearRatio[4] = 1.47F;
        this.gearRatio[5] = 1.0F;
    }

    public void Load(String _name, String totalFile) {
        ScriptManager scriptManager = ScriptManager.instance;
        this.fileName = scriptManager.currentFileName;
        if (!scriptManager.scriptsWithVehicles.contains(this.fileName)) {
            scriptManager.scriptsWithVehicles.add(this.fileName);
        }

        this.name = _name;
        ScriptParser.Block block0 = ScriptParser.parse(totalFile);
        block0 = block0.children.get(0);

        for (ScriptParser.BlockElement blockElement : block0.elements) {
            if (blockElement.asValue() != null) {
                String[] strings0 = blockElement.asValue().string.split("=");
                String string0 = strings0[0].trim();
                String string1 = strings0[1].trim();
                if ("extents".equals(string0)) {
                    this.LoadVector3f(string1, this.extents);
                } else if ("shadowExtents".equals(string0)) {
                    this.LoadVector2f(string1, this.shadowExtents);
                    this.bHadShadowOExtents = true;
                } else if ("shadowOffset".equals(string0)) {
                    this.LoadVector2f(string1, this.shadowOffset);
                    this.bHadShadowOffset = true;
                } else if ("physicsChassisShape".equals(string0)) {
                    this.LoadVector3f(string1, this.physicsChassisShape);
                } else if ("extentsOffset".equals(string0)) {
                    this.LoadVector2f(string1, this.extentsOffset);
                } else if ("mass".equals(string0)) {
                    this.mass = Float.parseFloat(string1);
                } else if ("offRoadEfficiency".equalsIgnoreCase(string0)) {
                    this.offroadEfficiency = Float.parseFloat(string1);
                } else if ("centerOfMassOffset".equals(string0)) {
                    this.LoadVector3f(string1, this.centerOfMassOffset);
                } else if ("engineForce".equals(string0)) {
                    this.engineForce = Float.parseFloat(string1);
                } else if ("engineIdleSpeed".equals(string0)) {
                    this.engineIdleSpeed = Float.parseFloat(string1);
                } else if ("gearRatioCount".equals(string0)) {
                    this.gearRatioCount = Integer.parseInt(string1);
                } else if ("gearRatioR".equals(string0)) {
                    this.gearRatio[0] = Float.parseFloat(string1);
                } else if ("gearRatio1".equals(string0)) {
                    this.gearRatio[1] = Float.parseFloat(string1);
                } else if ("gearRatio2".equals(string0)) {
                    this.gearRatio[2] = Float.parseFloat(string1);
                } else if ("gearRatio3".equals(string0)) {
                    this.gearRatio[3] = Float.parseFloat(string1);
                } else if ("gearRatio4".equals(string0)) {
                    this.gearRatio[4] = Float.parseFloat(string1);
                } else if ("gearRatio5".equals(string0)) {
                    this.gearRatio[5] = Float.parseFloat(string1);
                } else if ("gearRatio6".equals(string0)) {
                    this.gearRatio[6] = Float.parseFloat(string1);
                } else if ("gearRatio7".equals(string0)) {
                    this.gearRatio[7] = Float.parseFloat(string1);
                } else if ("gearRatio8".equals(string0)) {
                    this.gearRatio[8] = Float.parseFloat(string1);
                } else if ("textureMaskEnable".equals(string0)) {
                    this.textureMaskEnable = Boolean.parseBoolean(string1);
                } else if ("textureRust".equals(string0)) {
                    this.textures.textureRust = StringUtils.discardNullOrWhitespace(string1);
                } else if ("textureMask".equals(string0)) {
                    this.textures.textureMask = StringUtils.discardNullOrWhitespace(string1);
                } else if ("textureLights".equals(string0)) {
                    this.textures.textureLights = StringUtils.discardNullOrWhitespace(string1);
                } else if ("textureDamage1Overlay".equals(string0)) {
                    this.textures.textureDamage1Overlay = StringUtils.discardNullOrWhitespace(string1);
                } else if ("textureDamage1Shell".equals(string0)) {
                    this.textures.textureDamage1Shell = StringUtils.discardNullOrWhitespace(string1);
                } else if ("textureDamage2Overlay".equals(string0)) {
                    this.textures.textureDamage2Overlay = StringUtils.discardNullOrWhitespace(string1);
                } else if ("textureDamage2Shell".equals(string0)) {
                    this.textures.textureDamage2Shell = StringUtils.discardNullOrWhitespace(string1);
                } else if ("textureShadow".equals(string0)) {
                    this.textures.textureShadow = StringUtils.discardNullOrWhitespace(string1);
                } else if ("rollInfluence".equals(string0)) {
                    this.rollInfluence = Float.parseFloat(string1);
                } else if ("steeringIncrement".equals(string0)) {
                    this.steeringIncrement = Float.parseFloat(string1);
                } else if ("steeringClamp".equals(string0)) {
                    this.steeringClamp = Float.parseFloat(string1);
                } else if ("suspensionStiffness".equals(string0)) {
                    this.suspensionStiffness = Float.parseFloat(string1);
                } else if ("suspensionDamping".equals(string0)) {
                    this.suspensionDamping = Float.parseFloat(string1);
                } else if ("suspensionCompression".equals(string0)) {
                    this.suspensionCompression = Float.parseFloat(string1);
                } else if ("suspensionRestLength".equals(string0)) {
                    this.suspensionRestLength = Float.parseFloat(string1);
                } else if ("maxSuspensionTravelCm".equals(string0)) {
                    this.maxSuspensionTravelCm = Float.parseFloat(string1);
                } else if ("wheelFriction".equals(string0)) {
                    this.wheelFriction = Float.parseFloat(string1);
                } else if ("stoppingMovementForce".equals(string0)) {
                    this.stoppingMovementForce = Float.parseFloat(string1);
                } else if ("maxSpeed".equals(string0)) {
                    this.maxSpeed = Float.parseFloat(string1);
                } else if ("isSmallVehicle".equals(string0)) {
                    this.isSmallVehicle = Boolean.parseBoolean(string1);
                } else if ("spawnOffsetY".equals(string0)) {
                    this.spawnOffsetY = Float.parseFloat(string1) - 0.995F;
                } else if ("frontEndDurability".equals(string0)) {
                    this.frontEndHealth = Integer.parseInt(string1);
                } else if ("rearEndDurability".equals(string0)) {
                    this.rearEndHealth = Integer.parseInt(string1);
                } else if ("storageCapacity".equals(string0)) {
                    this.storageCapacity = Integer.parseInt(string1);
                } else if ("engineLoudness".equals(string0)) {
                    this.engineLoudness = Integer.parseInt(string1);
                } else if ("engineQuality".equals(string0)) {
                    this.engineQuality = Integer.parseInt(string1);
                } else if ("seats".equals(string0)) {
                    this.seats = Integer.parseInt(string1);
                } else if ("hasSiren".equals(string0)) {
                    this.hasSiren = Boolean.parseBoolean(string1);
                } else if ("mechanicType".equals(string0)) {
                    this.mechanicType = Integer.parseInt(string1);
                } else if ("forcedColor".equals(string0)) {
                    String[] strings1 = string1.split(" ");
                    this.setForcedHue(Float.parseFloat(strings1[0]));
                    this.setForcedSat(Float.parseFloat(strings1[1]));
                    this.setForcedVal(Float.parseFloat(strings1[2]));
                } else if ("engineRPMType".equals(string0)) {
                    this.engineRPMType = string1.trim();
                } else if ("template".equals(string0)) {
                    this.LoadTemplate(string1);
                } else if ("template!".equals(string0)) {
                    VehicleTemplate vehicleTemplate = ScriptManager.instance.getVehicleTemplate(string1);
                    if (vehicleTemplate == null) {
                        DebugLog.log("ERROR: template \"" + string1 + "\" not found");
                    } else {
                        this.Load(_name, vehicleTemplate.body);
                    }
                } else if ("engineRepairLevel".equals(string0)) {
                    this.engineRepairLevel = Integer.parseInt(string1);
                } else if ("playerDamageProtection".equals(string0)) {
                    this.setPlayerDamageProtection(Float.parseFloat(string1));
                }
            } else {
                ScriptParser.Block block1 = blockElement.asBlock();
                if ("area".equals(block1.type)) {
                    this.LoadArea(block1);
                } else if ("attachment".equals(block1.type)) {
                    this.LoadAttachment(block1);
                } else if ("model".equals(block1.type)) {
                    this.LoadModel(block1, this.models);
                } else if ("part".equals(block1.type)) {
                    if (block1.id != null && block1.id.contains("*")) {
                        String string2 = block1.id;

                        for (VehicleScript.Part part0 : this.parts) {
                            if (this.globMatch(string2, part0.id)) {
                                block1.id = part0.id;
                                this.LoadPart(block1);
                            }
                        }
                    } else {
                        this.LoadPart(block1);
                    }
                } else if ("passenger".equals(block1.type)) {
                    if (block1.id != null && block1.id.contains("*")) {
                        String string3 = block1.id;

                        for (VehicleScript.Passenger passenger : this.passengers) {
                            if (this.globMatch(string3, passenger.id)) {
                                block1.id = passenger.id;
                                this.LoadPassenger(block1);
                            }
                        }
                    } else {
                        this.LoadPassenger(block1);
                    }
                } else if ("physics".equals(block1.type)) {
                    VehicleScript.PhysicsShape physicsShape = this.LoadPhysicsShape(block1);
                    if (physicsShape != null && this.m_physicsShapes.size() < 10) {
                        this.m_physicsShapes.add(physicsShape);
                    }
                } else if ("skin".equals(block1.type)) {
                    VehicleScript.Skin skin = this.LoadSkin(block1);
                    if (!StringUtils.isNullOrWhitespace(skin.texture)) {
                        this.skins.add(skin);
                    }
                } else if ("wheel".equals(block1.type)) {
                    this.LoadWheel(block1);
                } else if ("lightbar".equals(block1.type)) {
                    for (ScriptParser.Value value0 : block1.values) {
                        String string4 = value0.getKey().trim();
                        String string5 = value0.getValue().trim();
                        if ("soundSiren".equals(string4)) {
                            this.lightbar.soundSiren0 = string5 + "Yelp";
                            this.lightbar.soundSiren1 = string5 + "Wall";
                            this.lightbar.soundSiren2 = string5 + "Alarm";
                        }

                        if ("soundSiren0".equals(string4)) {
                            this.lightbar.soundSiren0 = string5;
                        }

                        if ("soundSiren1".equals(string4)) {
                            this.lightbar.soundSiren1 = string5;
                        }

                        if ("soundSiren2".equals(string4)) {
                            this.lightbar.soundSiren2 = string5;
                        }

                        if ("leftCol".equals(string4)) {
                            String[] strings2 = string5.split(";");
                            this.leftSirenCol = new ImmutableColor(Float.parseFloat(strings2[0]), Float.parseFloat(strings2[1]), Float.parseFloat(strings2[2]));
                        }

                        if ("rightCol".equals(string4)) {
                            String[] strings3 = string5.split(";");
                            this.rightSirenCol = new ImmutableColor(Float.parseFloat(strings3[0]), Float.parseFloat(strings3[1]), Float.parseFloat(strings3[2]));
                        }

                        this.lightbar.enable = true;
                        if (this.getPartById("lightbar") == null) {
                            VehicleScript.Part part1 = new VehicleScript.Part();
                            part1.id = "lightbar";
                            this.parts.add(part1);
                        }
                    }
                } else if ("sound".equals(block1.type)) {
                    for (ScriptParser.Value value1 : block1.values) {
                        String string6 = value1.getKey().trim();
                        String string7 = value1.getValue().trim();
                        if ("backSignal".equals(string6)) {
                            this.sound.backSignal = StringUtils.discardNullOrWhitespace(string7);
                            this.sound.backSignalEnable = this.sound.backSignal != null;
                        } else if ("engine".equals(string6)) {
                            this.sound.engine = StringUtils.discardNullOrWhitespace(string7);
                        } else if ("engineStart".equals(string6)) {
                            this.sound.engineStart = StringUtils.discardNullOrWhitespace(string7);
                        } else if ("engineTurnOff".equals(string6)) {
                            this.sound.engineTurnOff = StringUtils.discardNullOrWhitespace(string7);
                        } else if ("horn".equals(string6)) {
                            this.sound.horn = StringUtils.discardNullOrWhitespace(string7);
                            this.sound.hornEnable = this.sound.horn != null;
                        } else if ("ignitionFail".equals(string6)) {
                            this.sound.ignitionFail = StringUtils.discardNullOrWhitespace(string7);
                        } else if ("ignitionFailNoPower".equals(string6)) {
                            this.sound.ignitionFailNoPower = StringUtils.discardNullOrWhitespace(string7);
                        }
                    }
                }
            }
        }
    }

    public String getFileName() {
        return this.fileName;
    }

    public void Loaded() {
        float float0 = this.getModelScale();
        this.extents.mul(float0);
        this.maxSuspensionTravelCm *= float0;
        this.suspensionRestLength *= float0;
        this.centerOfMassOffset.mul(float0);
        this.physicsChassisShape.mul(float0);
        if (this.bHadShadowOExtents) {
            this.shadowExtents.mul(float0);
        } else {
            this.shadowExtents.set(this.extents.x(), this.extents.z());
        }

        if (this.bHadShadowOffset) {
            this.shadowOffset.mul(float0);
        } else {
            this.shadowOffset.set(this.centerOfMassOffset.x(), this.centerOfMassOffset.z());
        }

        for (VehicleScript.Model model : this.models) {
            model.offset.mul(float0);
        }

        for (ModelAttachment modelAttachment : this.m_attachments) {
            modelAttachment.getOffset().mul(float0);
        }

        for (VehicleScript.PhysicsShape physicsShape : this.m_physicsShapes) {
            physicsShape.offset.mul(float0);
            switch (physicsShape.type) {
                case 1:
                    physicsShape.extents.mul(float0);
                    break;
                case 2:
                    physicsShape.radius *= float0;
            }
        }

        for (VehicleScript.Wheel wheel : this.wheels) {
            wheel.radius *= float0;
            wheel.offset.mul(float0);
        }

        for (VehicleScript.Area area : this.areas) {
            area.x *= float0;
            area.y *= float0;
            area.w *= float0;
            area.h *= float0;
        }

        if (!this.extents.equals(this.physicsChassisShape)) {
            DebugLog.Script.warn("vehicle \"" + this.name + "\" extents != physicsChassisShape");
        }

        for (int int0 = 0; int0 < this.passengers.size(); int0++) {
            VehicleScript.Passenger passenger = this.passengers.get(int0);

            for (int int1 = 0; int1 < passenger.getPositionCount(); int1++) {
                VehicleScript.Position position = passenger.getPosition(int1);
                position.getOffset().mul(float0);
            }

            for (int int2 = 0; int2 < passenger.switchSeats.size(); int2++) {
                VehicleScript.Passenger.SwitchSeat switchSeat = passenger.switchSeats.get(int2);
                switchSeat.seat = this.getPassengerIndex(switchSeat.id);

                assert switchSeat.seat != -1;
            }
        }

        for (int int3 = 0; int3 < this.parts.size(); int3++) {
            VehicleScript.Part part = this.parts.get(int3);
            if (part.container != null && part.container.seatID != null && !part.container.seatID.isEmpty()) {
                part.container.seat = this.getPassengerIndex(part.container.seatID);
            }

            if (part.specificItem && part.itemType != null) {
                for (int int4 = 0; int4 < part.itemType.size(); int4++) {
                    part.itemType.set(int4, part.itemType.get(int4) + this.mechanicType);
                }
            }
        }

        this.initCrawlOffsets();
        if (!GameServer.bServer) {
            this.toBullet();
        }
    }

    public void toBullet() {
        float[] floats = new float[200];
        int int0 = 0;
        floats[int0++] = this.getModelScale();
        floats[int0++] = this.extents.x;
        floats[int0++] = this.extents.y;
        floats[int0++] = this.extents.z;
        floats[int0++] = this.physicsChassisShape.x;
        floats[int0++] = this.physicsChassisShape.y;
        floats[int0++] = this.physicsChassisShape.z;
        floats[int0++] = this.mass;
        floats[int0++] = this.centerOfMassOffset.x;
        floats[int0++] = this.centerOfMassOffset.y;
        floats[int0++] = this.centerOfMassOffset.z;
        floats[int0++] = this.rollInfluence;
        floats[int0++] = this.suspensionStiffness;
        floats[int0++] = this.suspensionCompression;
        floats[int0++] = this.suspensionDamping;
        floats[int0++] = this.maxSuspensionTravelCm;
        floats[int0++] = this.suspensionRestLength;
        if (SystemDisabler.getdoHighFriction()) {
            floats[int0++] = this.wheelFriction * 100.0F;
        } else {
            floats[int0++] = this.wheelFriction;
        }

        floats[int0++] = this.stoppingMovementForce;
        floats[int0++] = this.getWheelCount();

        for (int int1 = 0; int1 < this.getWheelCount(); int1++) {
            VehicleScript.Wheel wheel = this.getWheel(int1);
            floats[int0++] = wheel.front ? 1.0F : 0.0F;
            floats[int0++] = wheel.offset.x + this.getModel().offset.x - 0.0F * this.centerOfMassOffset.x;
            floats[int0++] = wheel.offset.y + this.getModel().offset.y - 0.0F * this.centerOfMassOffset.y + 1.0F * this.suspensionRestLength;
            floats[int0++] = wheel.offset.z + this.getModel().offset.z - 0.0F * this.centerOfMassOffset.z;
            floats[int0++] = wheel.radius;
        }

        floats[int0++] = this.m_physicsShapes.size() + 1;
        floats[int0++] = 1.0F;
        floats[int0++] = this.centerOfMassOffset.x;
        floats[int0++] = this.centerOfMassOffset.y;
        floats[int0++] = this.centerOfMassOffset.z;
        floats[int0++] = this.physicsChassisShape.x;
        floats[int0++] = this.physicsChassisShape.y;
        floats[int0++] = this.physicsChassisShape.z;
        floats[int0++] = 0.0F;
        floats[int0++] = 0.0F;
        floats[int0++] = 0.0F;

        for (int int2 = 0; int2 < this.m_physicsShapes.size(); int2++) {
            VehicleScript.PhysicsShape physicsShape = this.m_physicsShapes.get(int2);
            floats[int0++] = physicsShape.type;
            floats[int0++] = physicsShape.offset.x;
            floats[int0++] = physicsShape.offset.y;
            floats[int0++] = physicsShape.offset.z;
            if (physicsShape.type == 1) {
                floats[int0++] = physicsShape.extents.x;
                floats[int0++] = physicsShape.extents.y;
                floats[int0++] = physicsShape.extents.z;
                floats[int0++] = physicsShape.rotate.x;
                floats[int0++] = physicsShape.rotate.y;
                floats[int0++] = physicsShape.rotate.z;
            } else if (physicsShape.type == 2) {
                floats[int0++] = physicsShape.radius;
            }
        }

        Bullet.defineVehicleScript(this.getFullName(), floats);
    }

    private void LoadVector2f(String string, Vector2f vector2f) {
        String[] strings = string.split(" ");
        vector2f.set(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]));
    }

    private void LoadVector3f(String string, Vector3f vector3f) {
        String[] strings = string.split(" ");
        vector3f.set(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]), Float.parseFloat(strings[2]));
    }

    private void LoadVector4f(String string, Vector4f vector4f) {
        String[] strings = string.split(" ");
        vector4f.set(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]), Float.parseFloat(strings[2]), Float.parseFloat(strings[3]));
    }

    private void LoadVector2i(String string, Vector2i vector2i) {
        String[] strings = string.split(" ");
        vector2i.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
    }

    private ModelAttachment LoadAttachment(ScriptParser.Block block) {
        ModelAttachment modelAttachment = this.getAttachmentById(block.id);
        if (modelAttachment == null) {
            modelAttachment = new ModelAttachment(block.id);
            this.m_attachments.add(modelAttachment);
        }

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("bone".equals(string0)) {
                modelAttachment.setBone(string1);
            } else if ("offset".equals(string0)) {
                this.LoadVector3f(string1, modelAttachment.getOffset());
            } else if ("rotate".equals(string0)) {
                this.LoadVector3f(string1, modelAttachment.getRotate());
            } else if ("canAttach".equals(string0)) {
                modelAttachment.setCanAttach(new ArrayList<>(Arrays.asList(string1.split(","))));
            } else if ("zoffset".equals(string0)) {
                modelAttachment.setZOffset(Float.parseFloat(string1));
            } else if ("updateconstraint".equals(string0)) {
                modelAttachment.setUpdateConstraint(Boolean.parseBoolean(string1));
            }
        }

        return modelAttachment;
    }

    private VehicleScript.Model LoadModel(ScriptParser.Block block, ArrayList<VehicleScript.Model> arrayList) {
        VehicleScript.Model model = this.getModelById(block.id, arrayList);
        if (model == null) {
            model = new VehicleScript.Model();
            model.id = block.id;
            arrayList.add(model);
        }

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("file".equals(string0)) {
                model.file = string1;
            } else if ("offset".equals(string0)) {
                this.LoadVector3f(string1, model.offset);
            } else if ("rotate".equals(string0)) {
                this.LoadVector3f(string1, model.rotate);
            } else if ("scale".equals(string0)) {
                model.scale = Float.parseFloat(string1);
            }
        }

        return model;
    }

    private VehicleScript.Skin LoadSkin(ScriptParser.Block block) {
        VehicleScript.Skin skin = new VehicleScript.Skin();

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("texture".equals(string0)) {
                skin.texture = StringUtils.discardNullOrWhitespace(string1);
            } else if ("textureRust".equals(string0)) {
                skin.textureRust = StringUtils.discardNullOrWhitespace(string1);
            } else if ("textureMask".equals(string0)) {
                skin.textureMask = StringUtils.discardNullOrWhitespace(string1);
            } else if ("textureLights".equals(string0)) {
                skin.textureLights = StringUtils.discardNullOrWhitespace(string1);
            } else if ("textureDamage1Overlay".equals(string0)) {
                skin.textureDamage1Overlay = StringUtils.discardNullOrWhitespace(string1);
            } else if ("textureDamage1Shell".equals(string0)) {
                skin.textureDamage1Shell = StringUtils.discardNullOrWhitespace(string1);
            } else if ("textureDamage2Overlay".equals(string0)) {
                skin.textureDamage2Overlay = StringUtils.discardNullOrWhitespace(string1);
            } else if ("textureDamage2Shell".equals(string0)) {
                skin.textureDamage2Shell = StringUtils.discardNullOrWhitespace(string1);
            } else if ("textureShadow".equals(string0)) {
                skin.textureShadow = StringUtils.discardNullOrWhitespace(string1);
            }
        }

        return skin;
    }

    private VehicleScript.Wheel LoadWheel(ScriptParser.Block block) {
        VehicleScript.Wheel wheel = this.getWheelById(block.id);
        if (wheel == null) {
            wheel = new VehicleScript.Wheel();
            wheel.id = block.id;
            this.wheels.add(wheel);
        }

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("model".equals(string0)) {
                wheel.model = string1;
            } else if ("front".equals(string0)) {
                wheel.front = Boolean.parseBoolean(string1);
            } else if ("offset".equals(string0)) {
                this.LoadVector3f(string1, wheel.offset);
            } else if ("radius".equals(string0)) {
                wheel.radius = Float.parseFloat(string1);
            } else if ("width".equals(string0)) {
                wheel.width = Float.parseFloat(string1);
            }
        }

        return wheel;
    }

    private VehicleScript.Passenger LoadPassenger(ScriptParser.Block block0) {
        VehicleScript.Passenger passenger = this.getPassengerById(block0.id);
        if (passenger == null) {
            passenger = new VehicleScript.Passenger();
            passenger.id = block0.id;
            this.passengers.add(passenger);
        }

        for (ScriptParser.Value value : block0.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("area".equals(string0)) {
                passenger.area = string1;
            } else if ("door".equals(string0)) {
                passenger.door = string1;
            } else if ("door2".equals(string0)) {
                passenger.door2 = string1;
            } else if ("hasRoof".equals(string0)) {
                passenger.hasRoof = Boolean.parseBoolean(string1);
            } else if ("showPassenger".equals(string0)) {
                passenger.showPassenger = Boolean.parseBoolean(string1);
            }
        }

        for (ScriptParser.Block block1 : block0.children) {
            if ("anim".equals(block1.type)) {
                this.LoadAnim(block1, passenger.anims);
            } else if ("position".equals(block1.type)) {
                this.LoadPosition(block1, passenger.positions);
            } else if ("switchSeat".equals(block1.type)) {
                this.LoadPassengerSwitchSeat(block1, passenger);
            }
        }

        return passenger;
    }

    private VehicleScript.Anim LoadAnim(ScriptParser.Block block, ArrayList<VehicleScript.Anim> arrayList) {
        VehicleScript.Anim anim = this.getAnimationById(block.id, arrayList);
        if (anim == null) {
            anim = new VehicleScript.Anim();
            anim.id = block.id;
            arrayList.add(anim);
        }

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("angle".equals(string0)) {
                this.LoadVector3f(string1, anim.angle);
            } else if ("anim".equals(string0)) {
                anim.anim = string1;
            } else if ("animate".equals(string0)) {
                anim.bAnimate = Boolean.parseBoolean(string1);
            } else if ("loop".equals(string0)) {
                anim.bLoop = Boolean.parseBoolean(string1);
            } else if ("reverse".equals(string0)) {
                anim.bReverse = Boolean.parseBoolean(string1);
            } else if ("rate".equals(string0)) {
                anim.rate = Float.parseFloat(string1);
            } else if ("offset".equals(string0)) {
                this.LoadVector3f(string1, anim.offset);
            } else if ("sound".equals(string0)) {
                anim.sound = string1;
            }
        }

        return anim;
    }

    private VehicleScript.Passenger.SwitchSeat LoadPassengerSwitchSeat(ScriptParser.Block block, VehicleScript.Passenger passenger) {
        VehicleScript.Passenger.SwitchSeat switchSeat = passenger.getSwitchSeatById(block.id);
        if (block.isEmpty()) {
            if (switchSeat != null) {
                passenger.switchSeats.remove(switchSeat);
            }

            return null;
        } else {
            if (switchSeat == null) {
                switchSeat = new VehicleScript.Passenger.SwitchSeat();
                switchSeat.id = block.id;
                passenger.switchSeats.add(switchSeat);
            }

            for (ScriptParser.Value value : block.values) {
                String string0 = value.getKey().trim();
                String string1 = value.getValue().trim();
                if ("anim".equals(string0)) {
                    switchSeat.anim = string1;
                } else if ("rate".equals(string0)) {
                    switchSeat.rate = Float.parseFloat(string1);
                } else if ("sound".equals(string0)) {
                    switchSeat.sound = string1.isEmpty() ? null : string1;
                }
            }

            return switchSeat;
        }
    }

    private VehicleScript.Area LoadArea(ScriptParser.Block block) {
        VehicleScript.Area area = this.getAreaById(block.id);
        if (area == null) {
            area = new VehicleScript.Area();
            area.id = block.id;
            this.areas.add(area);
        }

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("xywh".equals(string0)) {
                String[] strings = string1.split(" ");
                area.x = Float.parseFloat(strings[0]);
                area.y = Float.parseFloat(strings[1]);
                area.w = Float.parseFloat(strings[2]);
                area.h = Float.parseFloat(strings[3]);
            }
        }

        return area;
    }

    private VehicleScript.Part LoadPart(ScriptParser.Block block0) {
        VehicleScript.Part part = this.getPartById(block0.id);
        if (part == null) {
            part = new VehicleScript.Part();
            part.id = block0.id;
            this.parts.add(part);
        }

        for (ScriptParser.Value value : block0.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("area".equals(string0)) {
                part.area = string1.isEmpty() ? null : string1;
            } else if ("itemType".equals(string0)) {
                part.itemType = new ArrayList<>();
                String[] strings = string1.split(";");

                for (String string2 : strings) {
                    part.itemType.add(string2);
                }
            } else if ("parent".equals(string0)) {
                part.parent = string1.isEmpty() ? null : string1;
            } else if ("mechanicRequireKey".equals(string0)) {
                part.mechanicRequireKey = Boolean.parseBoolean(string1);
            } else if ("repairMechanic".equals(string0)) {
                part.setRepairMechanic(Boolean.parseBoolean(string1));
            } else if ("setAllModelsVisible".equals(string0)) {
                part.bSetAllModelsVisible = Boolean.parseBoolean(string1);
            } else if ("wheel".equals(string0)) {
                part.wheel = string1;
            } else if ("category".equals(string0)) {
                part.category = string1;
            } else if ("specificItem".equals(string0)) {
                part.specificItem = Boolean.parseBoolean(string1);
            } else if ("hasLightsRear".equals(string0)) {
                part.hasLightsRear = Boolean.parseBoolean(string1);
            }
        }

        for (ScriptParser.Block block1 : block0.children) {
            if ("anim".equals(block1.type)) {
                if (part.anims == null) {
                    part.anims = new ArrayList<>();
                }

                this.LoadAnim(block1, part.anims);
            } else if ("container".equals(block1.type)) {
                part.container = this.LoadContainer(block1, part.container);
            } else if ("door".equals(block1.type)) {
                part.door = this.LoadDoor(block1);
            } else if ("lua".equals(block1.type)) {
                part.luaFunctions = this.LoadLuaFunctions(block1);
            } else if ("model".equals(block1.type)) {
                if (part.models == null) {
                    part.models = new ArrayList<>();
                }

                this.LoadModel(block1, part.models);
            } else if ("table".equals(block1.type)) {
                Object object = part.tables == null ? null : part.tables.get(block1.id);
                KahluaTable table = this.LoadTable(block1, object instanceof KahluaTable ? (KahluaTable)object : null);
                if (part.tables == null) {
                    part.tables = new HashMap<>();
                }

                part.tables.put(block1.id, table);
            } else if ("window".equals(block1.type)) {
                part.window = this.LoadWindow(block1);
            }
        }

        return part;
    }

    private VehicleScript.PhysicsShape LoadPhysicsShape(ScriptParser.Block block) {
        byte byte0 = -1;
        String string0 = block.id;
        switch (string0) {
            case "box":
                byte0 = 1;
                break;
            case "sphere":
                byte0 = 2;
                break;
            default:
                return null;
        }

        VehicleScript.PhysicsShape physicsShape = new VehicleScript.PhysicsShape();
        physicsShape.type = byte0;

        for (ScriptParser.Value value : block.values) {
            String string1 = value.getKey().trim();
            String string2 = value.getValue().trim();
            if ("extents".equalsIgnoreCase(string1)) {
                this.LoadVector3f(string2, physicsShape.extents);
            } else if ("offset".equalsIgnoreCase(string1)) {
                this.LoadVector3f(string2, physicsShape.offset);
            } else if ("radius".equalsIgnoreCase(string1)) {
                physicsShape.radius = Float.parseFloat(string2);
            } else if ("rotate".equalsIgnoreCase(string1)) {
                this.LoadVector3f(string2, physicsShape.rotate);
            }
        }

        switch (physicsShape.type) {
            case 1:
                if (physicsShape.extents.x() <= 0.0F || physicsShape.extents.y() <= 0.0F || physicsShape.extents.z() <= 0.0F) {
                    return null;
                }
                break;
            case 2:
                if (physicsShape.radius <= 0.0F) {
                    return null;
                }
        }

        return physicsShape;
    }

    private VehicleScript.Door LoadDoor(ScriptParser.Block block) {
        VehicleScript.Door door = new VehicleScript.Door();

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
        }

        return door;
    }

    private VehicleScript.Window LoadWindow(ScriptParser.Block block) {
        VehicleScript.Window window = new VehicleScript.Window();

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("openable".equals(string0)) {
                window.openable = Boolean.parseBoolean(string1);
            }
        }

        return window;
    }

    private VehicleScript.Container LoadContainer(ScriptParser.Block block, VehicleScript.Container container1) {
        VehicleScript.Container container0 = container1 == null ? new VehicleScript.Container() : container1;

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("capacity".equals(string0)) {
                container0.capacity = Integer.parseInt(string1);
            } else if ("conditionAffectsCapacity".equals(string0)) {
                container0.conditionAffectsCapacity = Boolean.parseBoolean(string1);
            } else if ("contentType".equals(string0)) {
                container0.contentType = string1;
            } else if ("seat".equals(string0)) {
                container0.seatID = string1;
            } else if ("test".equals(string0)) {
                container0.luaTest = string1;
            }
        }

        return container0;
    }

    private HashMap<String, String> LoadLuaFunctions(ScriptParser.Block block) {
        HashMap hashMap = new HashMap();

        for (ScriptParser.Value value : block.values) {
            if (value.string.indexOf(61) == -1) {
                throw new RuntimeException("expected \"key = value\", got \"" + value.string.trim() + "\" in " + this.getFullName());
            }

            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            hashMap.put(string0, string1);
        }

        return hashMap;
    }

    private Object checkIntegerKey(Object object) {
        if (!(object instanceof String string)) {
            return object;
        } else {
            for (int int0 = 0; int0 < string.length(); int0++) {
                if (!Character.isDigit(string.charAt(int0))) {
                    return object;
                }
            }

            return Double.valueOf(string);
        }
    }

    private KahluaTable LoadTable(ScriptParser.Block block0, KahluaTable table1) {
        KahluaTable table0 = table1 == null ? LuaManager.platform.newTable() : table1;

        for (ScriptParser.Value value : block0.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if (string1.isEmpty()) {
                string1 = null;
            }

            table0.rawset(this.checkIntegerKey(string0), string1);
        }

        for (ScriptParser.Block block1 : block0.children) {
            Object object = table0.rawget(block1.type);
            KahluaTable table2 = this.LoadTable(block1, object instanceof KahluaTable ? (KahluaTable)object : null);
            table0.rawset(this.checkIntegerKey(block1.type), table2);
        }

        return table0;
    }

    private void LoadTemplate(String string0) {
        if (string0.contains("/")) {
            String[] strings = string0.split("/");
            if (strings.length == 0 || strings.length > 3) {
                DebugLog.log("ERROR: template \"" + string0 + "\"");
                return;
            }

            for (int int0 = 0; int0 < strings.length; int0++) {
                strings[int0] = strings[int0].trim();
                if (strings[int0].isEmpty()) {
                    DebugLog.log("ERROR: template \"" + string0 + "\"");
                    return;
                }
            }

            String string1 = strings[0];
            VehicleTemplate vehicleTemplate0 = ScriptManager.instance.getVehicleTemplate(string1);
            if (vehicleTemplate0 == null) {
                DebugLog.log("ERROR: template \"" + string0 + "\" not found");
                return;
            }

            VehicleScript vehicleScript0 = vehicleTemplate0.getScript();
            String string2 = strings[1];
            switch (string2) {
                case "area":
                    if (strings.length == 2) {
                        DebugLog.log("ERROR: template \"" + string0 + "\"");
                        return;
                    }

                    this.copyAreasFrom(vehicleScript0, strings[2]);
                    break;
                case "part":
                    if (strings.length == 2) {
                        DebugLog.log("ERROR: template \"" + string0 + "\"");
                        return;
                    }

                    this.copyPartsFrom(vehicleScript0, strings[2]);
                    break;
                case "passenger":
                    if (strings.length == 2) {
                        DebugLog.log("ERROR: template \"" + string0 + "\"");
                        return;
                    }

                    this.copyPassengersFrom(vehicleScript0, strings[2]);
                    break;
                case "wheel":
                    if (strings.length == 2) {
                        DebugLog.log("ERROR: template \"" + string0 + "\"");
                        return;
                    }

                    this.copyWheelsFrom(vehicleScript0, strings[2]);
                    break;
                default:
                    DebugLog.log("ERROR: template \"" + string0 + "\"");
                    return;
            }
        } else {
            String string3 = string0.trim();
            VehicleTemplate vehicleTemplate1 = ScriptManager.instance.getVehicleTemplate(string3);
            if (vehicleTemplate1 == null) {
                DebugLog.log("ERROR: template \"" + string0 + "\" not found");
                return;
            }

            VehicleScript vehicleScript2 = vehicleTemplate1.getScript();
            this.copyAreasFrom(vehicleScript2, "*");
            this.copyPartsFrom(vehicleScript2, "*");
            this.copyPassengersFrom(vehicleScript2, "*");
            this.copyWheelsFrom(vehicleScript2, "*");
        }
    }

    public void copyAreasFrom(VehicleScript other, String spec) {
        if ("*".equals(spec)) {
            for (int int0 = 0; int0 < other.getAreaCount(); int0++) {
                VehicleScript.Area area0 = other.getArea(int0);
                int int1 = this.getIndexOfAreaById(area0.id);
                if (int1 == -1) {
                    this.areas.add(area0.makeCopy());
                } else {
                    this.areas.set(int1, area0.makeCopy());
                }
            }
        } else {
            VehicleScript.Area area1 = other.getAreaById(spec);
            if (area1 == null) {
                DebugLog.log("ERROR: area \"" + spec + "\" not found");
                return;
            }

            int int2 = this.getIndexOfAreaById(area1.id);
            if (int2 == -1) {
                this.areas.add(area1.makeCopy());
            } else {
                this.areas.set(int2, area1.makeCopy());
            }
        }
    }

    public void copyPartsFrom(VehicleScript other, String spec) {
        if ("*".equals(spec)) {
            for (int int0 = 0; int0 < other.getPartCount(); int0++) {
                VehicleScript.Part part0 = other.getPart(int0);
                int int1 = this.getIndexOfPartById(part0.id);
                if (int1 == -1) {
                    this.parts.add(part0.makeCopy());
                } else {
                    this.parts.set(int1, part0.makeCopy());
                }
            }
        } else {
            VehicleScript.Part part1 = other.getPartById(spec);
            if (part1 == null) {
                DebugLog.log("ERROR: part \"" + spec + "\" not found");
                return;
            }

            int int2 = this.getIndexOfPartById(part1.id);
            if (int2 == -1) {
                this.parts.add(part1.makeCopy());
            } else {
                this.parts.set(int2, part1.makeCopy());
            }
        }
    }

    public void copyPassengersFrom(VehicleScript other, String spec) {
        if ("*".equals(spec)) {
            for (int int0 = 0; int0 < other.getPassengerCount(); int0++) {
                VehicleScript.Passenger passenger0 = other.getPassenger(int0);
                int int1 = this.getPassengerIndex(passenger0.id);
                if (int1 == -1) {
                    this.passengers.add(passenger0.makeCopy());
                } else {
                    this.passengers.set(int1, passenger0.makeCopy());
                }
            }
        } else {
            VehicleScript.Passenger passenger1 = other.getPassengerById(spec);
            if (passenger1 == null) {
                DebugLog.log("ERROR: passenger \"" + spec + "\" not found");
                return;
            }

            int int2 = this.getPassengerIndex(passenger1.id);
            if (int2 == -1) {
                this.passengers.add(passenger1.makeCopy());
            } else {
                this.passengers.set(int2, passenger1.makeCopy());
            }
        }
    }

    public void copyWheelsFrom(VehicleScript other, String spec) {
        if ("*".equals(spec)) {
            for (int int0 = 0; int0 < other.getWheelCount(); int0++) {
                VehicleScript.Wheel wheel0 = other.getWheel(int0);
                int int1 = this.getIndexOfWheelById(wheel0.id);
                if (int1 == -1) {
                    this.wheels.add(wheel0.makeCopy());
                } else {
                    this.wheels.set(int1, wheel0.makeCopy());
                }
            }
        } else {
            VehicleScript.Wheel wheel1 = other.getWheelById(spec);
            if (wheel1 == null) {
                DebugLog.log("ERROR: wheel \"" + spec + "\" not found");
                return;
            }

            int int2 = this.getIndexOfWheelById(wheel1.id);
            if (int2 == -1) {
                this.wheels.add(wheel1.makeCopy());
            } else {
                this.wheels.set(int2, wheel1.makeCopy());
            }
        }
    }

    private VehicleScript.Position LoadPosition(ScriptParser.Block block, ArrayList<VehicleScript.Position> arrayList) {
        VehicleScript.Position position = this.getPositionById(block.id, arrayList);
        if (block.isEmpty()) {
            if (position != null) {
                arrayList.remove(position);
            }

            return null;
        } else {
            if (position == null) {
                position = new VehicleScript.Position();
                position.id = block.id;
                arrayList.add(position);
            }

            for (ScriptParser.Value value : block.values) {
                String string0 = value.getKey().trim();
                String string1 = value.getValue().trim();
                if ("rotate".equals(string0)) {
                    this.LoadVector3f(string1, position.rotate);
                } else if ("offset".equals(string0)) {
                    this.LoadVector3f(string1, position.offset);
                } else if ("area".equals(string0)) {
                    position.area = string1.isEmpty() ? null : string1;
                }
            }

            return position;
        }
    }

    private void initCrawlOffsets() {
        for (int int0 = 0; int0 < this.getWheelCount(); int0++) {
            VehicleScript.Wheel wheel = this.getWheel(int0);
            if (wheel.id.contains("Left")) {
                this.initCrawlOffsets(wheel);
            }
        }

        float float0 = this.extents.z + BaseVehicle.PLUS_RADIUS * 2.0F;

        for (int int1 = 0; int1 < this.crawlOffsets.size(); int1++) {
            this.crawlOffsets.set(int1, (this.extents.z / 2.0F + BaseVehicle.PLUS_RADIUS + this.crawlOffsets.get(int1) - this.centerOfMassOffset.z) / float0);
        }

        this.crawlOffsets.sort();

        for (int int2 = 0; int2 < this.crawlOffsets.size(); int2++) {
            float float1 = this.crawlOffsets.get(int2);

            for (int int3 = int2 + 1; int3 < this.crawlOffsets.size(); int3++) {
                float float2 = this.crawlOffsets.get(int3);
                if ((float2 - float1) * float0 < 0.15F) {
                    this.crawlOffsets.removeAt(int3--);
                }
            }
        }
    }

    private void initCrawlOffsets(VehicleScript.Wheel wheel) {
        float float0 = 0.3F;
        float float1 = this.getModel() == null ? 0.0F : this.getModel().getOffset().z;
        float float2 = this.centerOfMassOffset.z + this.extents.z / 2.0F;
        float float3 = this.centerOfMassOffset.z - this.extents.z / 2.0F;

        for (int int0 = 0; int0 < 10; int0++) {
            float float4 = float1 + wheel.offset.z + wheel.radius + float0 + float0 * int0;
            if (float4 + float0 <= float2 && !this.isOverlappingWheel(float4)) {
                this.crawlOffsets.add(float4);
            }

            float4 = float1 + wheel.offset.z - wheel.radius - float0 - float0 * int0;
            if (float4 - float0 >= float3 && !this.isOverlappingWheel(float4)) {
                this.crawlOffsets.add(float4);
            }
        }
    }

    private boolean isOverlappingWheel(float float2) {
        float float0 = 0.3F;
        float float1 = this.getModel() == null ? 0.0F : this.getModel().getOffset().z;

        for (int int0 = 0; int0 < this.getWheelCount(); int0++) {
            VehicleScript.Wheel wheel = this.getWheel(int0);
            if (wheel.id.contains("Left") && Math.abs(float1 + wheel.offset.z - float2) < (wheel.radius + float0) * 0.99F) {
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return this.name;
    }

    public String getFullName() {
        return this.getModule().getName() + "." + this.getName();
    }

    public VehicleScript.Model getModel() {
        return this.models.isEmpty() ? null : this.models.get(0);
    }

    public Vector3f getModelOffset() {
        return this.getModel() == null ? null : this.getModel().getOffset();
    }

    public float getModelScale() {
        return this.getModel() == null ? 1.0F : this.getModel().scale;
    }

    public void setModelScale(float scale) {
        VehicleScript.Model model = this.getModel();
        if (model != null) {
            float float0 = model.scale;
            model.scale = 1.0F / float0;
            this.Loaded();
            model.scale = PZMath.clamp(scale, 0.01F, 100.0F);
            this.Loaded();
        }
    }

    public int getModelCount() {
        return this.models.size();
    }

    public VehicleScript.Model getModelByIndex(int index) {
        return this.models.get(index);
    }

    public VehicleScript.Model getModelById(String id, ArrayList<VehicleScript.Model> _models) {
        for (int int0 = 0; int0 < _models.size(); int0++) {
            VehicleScript.Model model = (VehicleScript.Model)_models.get(int0);
            if (StringUtils.isNullOrWhitespace(model.id) && StringUtils.isNullOrWhitespace(id)) {
                return model;
            }

            if (model.id != null && model.id.equals(id)) {
                return model;
            }
        }

        return null;
    }

    public VehicleScript.Model getModelById(String id) {
        return this.getModelById(id, this.models);
    }

    public int getAttachmentCount() {
        return this.m_attachments.size();
    }

    public ModelAttachment getAttachment(int index) {
        return this.m_attachments.get(index);
    }

    public ModelAttachment getAttachmentById(String id) {
        for (int int0 = 0; int0 < this.m_attachments.size(); int0++) {
            ModelAttachment modelAttachment = this.m_attachments.get(int0);
            if (modelAttachment.getId().equals(id)) {
                return modelAttachment;
            }
        }

        return null;
    }

    public ModelAttachment addAttachment(ModelAttachment attach) {
        this.m_attachments.add(attach);
        return attach;
    }

    public ModelAttachment removeAttachment(ModelAttachment attach) {
        this.m_attachments.remove(attach);
        return attach;
    }

    public ModelAttachment addAttachmentAt(int index, ModelAttachment attach) {
        this.m_attachments.add(index, attach);
        return attach;
    }

    public ModelAttachment removeAttachment(int index) {
        return this.m_attachments.remove(index);
    }

    public VehicleScript.LightBar getLightbar() {
        return this.lightbar;
    }

    public VehicleScript.Sounds getSounds() {
        return this.sound;
    }

    public boolean getHasSiren() {
        return this.hasSiren;
    }

    public Vector3f getExtents() {
        return this.extents;
    }

    public Vector3f getPhysicsChassisShape() {
        return this.physicsChassisShape;
    }

    public Vector2f getShadowExtents() {
        return this.shadowExtents;
    }

    public Vector2f getShadowOffset() {
        return this.shadowOffset;
    }

    public Vector2f getExtentsOffset() {
        return this.extentsOffset;
    }

    public float getMass() {
        return this.mass;
    }

    public Vector3f getCenterOfMassOffset() {
        return this.centerOfMassOffset;
    }

    public float getEngineForce() {
        return this.engineForce;
    }

    public float getEngineIdleSpeed() {
        return this.engineIdleSpeed;
    }

    public int getEngineQuality() {
        return this.engineQuality;
    }

    public int getEngineLoudness() {
        return this.engineLoudness;
    }

    public float getRollInfluence() {
        return this.rollInfluence;
    }

    public float getSteeringIncrement() {
        return this.steeringIncrement;
    }

    public float getSteeringClamp(float speed) {
        speed = Math.abs(speed);
        float float0 = speed / this.maxSpeed;
        if (float0 > 1.0F) {
            float0 = 1.0F;
        }

        float0 = 1.0F - float0;
        return (this.steeringClampMax - this.steeringClamp) * float0 + this.steeringClamp;
    }

    public float getSuspensionStiffness() {
        return this.suspensionStiffness;
    }

    public float getSuspensionDamping() {
        return this.suspensionDamping;
    }

    public float getSuspensionCompression() {
        return this.suspensionCompression;
    }

    public float getSuspensionRestLength() {
        return this.suspensionRestLength;
    }

    public float getSuspensionTravel() {
        return this.maxSuspensionTravelCm;
    }

    public float getWheelFriction() {
        return this.wheelFriction;
    }

    public int getWheelCount() {
        return this.wheels.size();
    }

    public VehicleScript.Wheel getWheel(int index) {
        return this.wheels.get(index);
    }

    public VehicleScript.Wheel getWheelById(String id) {
        for (int int0 = 0; int0 < this.wheels.size(); int0++) {
            VehicleScript.Wheel wheel = this.wheels.get(int0);
            if (wheel.id != null && wheel.id.equals(id)) {
                return wheel;
            }
        }

        return null;
    }

    public int getIndexOfWheelById(String id) {
        for (int int0 = 0; int0 < this.wheels.size(); int0++) {
            VehicleScript.Wheel wheel = this.wheels.get(int0);
            if (wheel.id != null && wheel.id.equals(id)) {
                return int0;
            }
        }

        return -1;
    }

    public int getPassengerCount() {
        return this.passengers.size();
    }

    public VehicleScript.Passenger getPassenger(int index) {
        return this.passengers.get(index);
    }

    public VehicleScript.Passenger getPassengerById(String id) {
        for (int int0 = 0; int0 < this.passengers.size(); int0++) {
            VehicleScript.Passenger passenger = this.passengers.get(int0);
            if (passenger.id != null && passenger.id.equals(id)) {
                return passenger;
            }
        }

        return null;
    }

    public int getPassengerIndex(String id) {
        for (int int0 = 0; int0 < this.passengers.size(); int0++) {
            VehicleScript.Passenger passenger = this.passengers.get(int0);
            if (passenger.id != null && passenger.id.equals(id)) {
                return int0;
            }
        }

        return -1;
    }

    public int getPhysicsShapeCount() {
        return this.m_physicsShapes.size();
    }

    public VehicleScript.PhysicsShape getPhysicsShape(int index) {
        return index >= 0 && index < this.m_physicsShapes.size() ? this.m_physicsShapes.get(index) : null;
    }

    public int getFrontEndHealth() {
        return this.frontEndHealth;
    }

    public int getRearEndHealth() {
        return this.rearEndHealth;
    }

    public int getStorageCapacity() {
        return this.storageCapacity;
    }

    public VehicleScript.Skin getTextures() {
        return this.textures;
    }

    public int getSkinCount() {
        return this.skins.size();
    }

    public VehicleScript.Skin getSkin(int index) {
        return this.skins.get(index);
    }

    public int getAreaCount() {
        return this.areas.size();
    }

    public VehicleScript.Area getArea(int index) {
        return this.areas.get(index);
    }

    public VehicleScript.Area getAreaById(String id) {
        for (int int0 = 0; int0 < this.areas.size(); int0++) {
            VehicleScript.Area area = this.areas.get(int0);
            if (area.id != null && area.id.equals(id)) {
                return area;
            }
        }

        return null;
    }

    public int getIndexOfAreaById(String id) {
        for (int int0 = 0; int0 < this.areas.size(); int0++) {
            VehicleScript.Area area = this.areas.get(int0);
            if (area.id != null && area.id.equals(id)) {
                return int0;
            }
        }

        return -1;
    }

    public int getPartCount() {
        return this.parts.size();
    }

    public VehicleScript.Part getPart(int index) {
        return this.parts.get(index);
    }

    public VehicleScript.Part getPartById(String id) {
        for (int int0 = 0; int0 < this.parts.size(); int0++) {
            VehicleScript.Part part = this.parts.get(int0);
            if (part.id != null && part.id.equals(id)) {
                return part;
            }
        }

        return null;
    }

    public int getIndexOfPartById(String id) {
        for (int int0 = 0; int0 < this.parts.size(); int0++) {
            VehicleScript.Part part = this.parts.get(int0);
            if (part.id != null && part.id.equals(id)) {
                return int0;
            }
        }

        return -1;
    }

    private VehicleScript.Anim getAnimationById(String string, ArrayList<VehicleScript.Anim> arrayList) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            VehicleScript.Anim anim = (VehicleScript.Anim)arrayList.get(int0);
            if (anim.id != null && anim.id.equals(string)) {
                return anim;
            }
        }

        return null;
    }

    private VehicleScript.Position getPositionById(String string, ArrayList<VehicleScript.Position> arrayList) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            VehicleScript.Position position = (VehicleScript.Position)arrayList.get(int0);
            if (position.id != null && position.id.equals(string)) {
                return position;
            }
        }

        return null;
    }

    public boolean globMatch(String pattern, String str) {
        Pattern _pattern = Pattern.compile(pattern.replaceAll("\\*", ".*"));
        return _pattern.matcher(str).matches();
    }

    public int getGearRatioCount() {
        return this.gearRatioCount;
    }

    public int getSeats() {
        return this.seats;
    }

    public void setSeats(int _seats) {
        this.seats = _seats;
    }

    public int getMechanicType() {
        return this.mechanicType;
    }

    public void setMechanicType(int _mechanicType) {
        this.mechanicType = _mechanicType;
    }

    public int getEngineRepairLevel() {
        return this.engineRepairLevel;
    }

    public int getHeadlightConfigLevel() {
        return 2;
    }

    public void setEngineRepairLevel(int _engineRepairLevel) {
        this.engineRepairLevel = _engineRepairLevel;
    }

    public float getPlayerDamageProtection() {
        return this.playerDamageProtection;
    }

    public void setPlayerDamageProtection(float _playerDamageProtection) {
        this.playerDamageProtection = _playerDamageProtection;
    }

    public float getForcedHue() {
        return this.forcedHue;
    }

    public void setForcedHue(float _forcedHue) {
        this.forcedHue = _forcedHue;
    }

    public float getForcedSat() {
        return this.forcedSat;
    }

    public void setForcedSat(float _forcedSat) {
        this.forcedSat = _forcedSat;
    }

    public float getForcedVal() {
        return this.forcedVal;
    }

    public void setForcedVal(float _forcedVal) {
        this.forcedVal = _forcedVal;
    }

    public String getEngineRPMType() {
        return this.engineRPMType;
    }

    public void setEngineRPMType(String _engineRPMType) {
        this.engineRPMType = _engineRPMType;
    }

    public float getOffroadEfficiency() {
        return this.offroadEfficiency;
    }

    public void setOffroadEfficiency(float _offroadEfficiency) {
        this.offroadEfficiency = _offroadEfficiency;
    }

    public TFloatArrayList getCrawlOffsets() {
        return this.crawlOffsets;
    }

    public static final class Anim {
        public String id;
        public String anim;
        public float rate = 1.0F;
        public boolean bAnimate = true;
        public boolean bLoop = false;
        public boolean bReverse = false;
        public final Vector3f offset = new Vector3f();
        public final Vector3f angle = new Vector3f();
        public String sound;

        VehicleScript.Anim makeCopy() {
            VehicleScript.Anim anim0 = new VehicleScript.Anim();
            anim0.id = this.id;
            anim0.anim = this.anim;
            anim0.rate = this.rate;
            anim0.bAnimate = this.bAnimate;
            anim0.bLoop = this.bLoop;
            anim0.bReverse = this.bReverse;
            anim0.offset.set(this.offset);
            anim0.angle.set(this.angle);
            anim0.sound = this.sound;
            return anim0;
        }
    }

    public static final class Area {
        public String id;
        public float x;
        public float y;
        public float w;
        public float h;

        public String getId() {
            return this.id;
        }

        public Double getX() {
            return BoxedStaticValues.toDouble(this.x);
        }

        public Double getY() {
            return BoxedStaticValues.toDouble(this.y);
        }

        public Double getW() {
            return BoxedStaticValues.toDouble(this.w);
        }

        public Double getH() {
            return BoxedStaticValues.toDouble(this.h);
        }

        public void setX(Double d) {
            this.x = d.floatValue();
        }

        public void setY(Double d) {
            this.y = d.floatValue();
        }

        public void setW(Double d) {
            this.w = d.floatValue();
        }

        public void setH(Double d) {
            this.h = d.floatValue();
        }

        private VehicleScript.Area makeCopy() {
            VehicleScript.Area area0 = new VehicleScript.Area();
            area0.id = this.id;
            area0.x = this.x;
            area0.y = this.y;
            area0.w = this.w;
            area0.h = this.h;
            return area0;
        }
    }

    public static final class Container {
        public int capacity;
        public int seat = -1;
        public String seatID;
        public String luaTest;
        public String contentType;
        public boolean conditionAffectsCapacity = false;

        VehicleScript.Container makeCopy() {
            VehicleScript.Container container0 = new VehicleScript.Container();
            container0.capacity = this.capacity;
            container0.seat = this.seat;
            container0.seatID = this.seatID;
            container0.luaTest = this.luaTest;
            container0.contentType = this.contentType;
            container0.conditionAffectsCapacity = this.conditionAffectsCapacity;
            return container0;
        }
    }

    public static final class Door {
        VehicleScript.Door makeCopy() {
            return new VehicleScript.Door();
        }
    }

    public static final class LightBar {
        public boolean enable = false;
        public String soundSiren0 = "";
        public String soundSiren1 = "";
        public String soundSiren2 = "";
    }

    public static final class Model {
        public String id;
        public String file;
        public float scale = 1.0F;
        public final Vector3f offset = new Vector3f();
        public final Vector3f rotate = new Vector3f();

        public String getId() {
            return this.id;
        }

        public Vector3f getOffset() {
            return this.offset;
        }

        public Vector3f getRotate() {
            return this.rotate;
        }

        VehicleScript.Model makeCopy() {
            VehicleScript.Model model0 = new VehicleScript.Model();
            model0.id = this.id;
            model0.file = this.file;
            model0.scale = this.scale;
            model0.offset.set(this.offset);
            model0.rotate.set(this.rotate);
            return model0;
        }
    }

    public static final class Part {
        public String id = "Unknown";
        public String parent;
        public ArrayList<String> itemType;
        public VehicleScript.Container container;
        public String area;
        public String wheel;
        public HashMap<String, KahluaTable> tables;
        public HashMap<String, String> luaFunctions;
        public ArrayList<VehicleScript.Model> models;
        public boolean bSetAllModelsVisible = true;
        public VehicleScript.Door door;
        public VehicleScript.Window window;
        public ArrayList<VehicleScript.Anim> anims;
        public String category;
        public boolean specificItem = true;
        public boolean mechanicRequireKey = false;
        public boolean repairMechanic = false;
        public boolean hasLightsRear = false;

        public boolean isMechanicRequireKey() {
            return this.mechanicRequireKey;
        }

        public void setMechanicRequireKey(boolean _mechanicRequireKey) {
            this.mechanicRequireKey = _mechanicRequireKey;
        }

        public boolean isRepairMechanic() {
            return this.repairMechanic;
        }

        public void setRepairMechanic(boolean _repairMechanic) {
            this.repairMechanic = _repairMechanic;
        }

        VehicleScript.Part makeCopy() {
            VehicleScript.Part part0 = new VehicleScript.Part();
            part0.id = this.id;
            part0.parent = this.parent;
            if (this.itemType != null) {
                part0.itemType = new ArrayList<>();
                part0.itemType.addAll(this.itemType);
            }

            if (this.container != null) {
                part0.container = this.container.makeCopy();
            }

            part0.area = this.area;
            part0.wheel = this.wheel;
            if (this.tables != null) {
                part0.tables = new HashMap<>();

                for (Entry entry : this.tables.entrySet()) {
                    KahluaTable table = LuaManager.copyTable((KahluaTable)entry.getValue());
                    part0.tables.put((String)entry.getKey(), table);
                }
            }

            if (this.luaFunctions != null) {
                part0.luaFunctions = new HashMap<>();
                part0.luaFunctions.putAll(this.luaFunctions);
            }

            if (this.models != null) {
                part0.models = new ArrayList<>();

                for (int int0 = 0; int0 < this.models.size(); int0++) {
                    part0.models.add(this.models.get(int0).makeCopy());
                }
            }

            part0.bSetAllModelsVisible = this.bSetAllModelsVisible;
            if (this.door != null) {
                part0.door = this.door.makeCopy();
            }

            if (this.window != null) {
                part0.window = this.window.makeCopy();
            }

            if (this.anims != null) {
                part0.anims = new ArrayList<>();

                for (int int1 = 0; int1 < this.anims.size(); int1++) {
                    part0.anims.add(this.anims.get(int1).makeCopy());
                }
            }

            part0.category = this.category;
            part0.specificItem = this.specificItem;
            part0.mechanicRequireKey = this.mechanicRequireKey;
            part0.repairMechanic = this.repairMechanic;
            part0.hasLightsRear = this.hasLightsRear;
            return part0;
        }
    }

    public static final class Passenger {
        public String id;
        public final ArrayList<VehicleScript.Anim> anims = new ArrayList<>();
        public final ArrayList<VehicleScript.Passenger.SwitchSeat> switchSeats = new ArrayList<>();
        public boolean hasRoof = true;
        public boolean showPassenger = false;
        public String door;
        public String door2;
        public String area;
        public final ArrayList<VehicleScript.Position> positions = new ArrayList<>();

        public String getId() {
            return this.id;
        }

        public VehicleScript.Passenger makeCopy() {
            VehicleScript.Passenger passenger0 = new VehicleScript.Passenger();
            passenger0.id = this.id;

            for (int int0 = 0; int0 < this.anims.size(); int0++) {
                passenger0.anims.add(this.anims.get(int0).makeCopy());
            }

            for (int int1 = 0; int1 < this.switchSeats.size(); int1++) {
                passenger0.switchSeats.add(this.switchSeats.get(int1).makeCopy());
            }

            passenger0.hasRoof = this.hasRoof;
            passenger0.showPassenger = this.showPassenger;
            passenger0.door = this.door;
            passenger0.door2 = this.door2;
            passenger0.area = this.area;

            for (int int2 = 0; int2 < this.positions.size(); int2++) {
                passenger0.positions.add(this.positions.get(int2).makeCopy());
            }

            return passenger0;
        }

        public int getPositionCount() {
            return this.positions.size();
        }

        public VehicleScript.Position getPosition(int index) {
            return this.positions.get(index);
        }

        public VehicleScript.Position getPositionById(String _id) {
            for (int int0 = 0; int0 < this.positions.size(); int0++) {
                VehicleScript.Position position = this.positions.get(int0);
                if (position.id != null && position.id.equals(_id)) {
                    return position;
                }
            }

            return null;
        }

        public VehicleScript.Passenger.SwitchSeat getSwitchSeatById(String _id) {
            for (int int0 = 0; int0 < this.switchSeats.size(); int0++) {
                VehicleScript.Passenger.SwitchSeat switchSeat = this.switchSeats.get(int0);
                if (switchSeat.id != null && switchSeat.id.equals(_id)) {
                    return switchSeat;
                }
            }

            return null;
        }

        public static final class SwitchSeat {
            public String id;
            public int seat;
            public String anim;
            public float rate = 1.0F;
            public String sound;

            public String getId() {
                return this.id;
            }

            public VehicleScript.Passenger.SwitchSeat makeCopy() {
                VehicleScript.Passenger.SwitchSeat switchSeat0 = new VehicleScript.Passenger.SwitchSeat();
                switchSeat0.id = this.id;
                switchSeat0.seat = this.seat;
                switchSeat0.anim = this.anim;
                switchSeat0.rate = this.rate;
                switchSeat0.sound = this.sound;
                return switchSeat0;
            }
        }
    }

    public static final class PhysicsShape {
        public int type;
        public final Vector3f offset = new Vector3f();
        public final Vector3f rotate = new Vector3f();
        public final Vector3f extents = new Vector3f();
        public float radius;

        public String getTypeString() {
            switch (this.type) {
                case 1:
                    return "box";
                case 2:
                    return "sphere";
                default:
                    throw new RuntimeException("unhandled VehicleScript.PhysicsShape");
            }
        }

        public Vector3f getOffset() {
            return this.offset;
        }

        public Vector3f getExtents() {
            return this.extents;
        }

        public Vector3f getRotate() {
            return this.rotate;
        }

        public float getRadius() {
            return this.radius;
        }

        public void setRadius(float _radius) {
            this.radius = PZMath.clamp(_radius, 0.05F, 5.0F);
        }
    }

    public static final class Position {
        public String id;
        public final Vector3f offset = new Vector3f();
        public final Vector3f rotate = new Vector3f();
        public String area = null;

        public String getId() {
            return this.id;
        }

        public Vector3f getOffset() {
            return this.offset;
        }

        public Vector3f getRotate() {
            return this.rotate;
        }

        public String getArea() {
            return this.area;
        }

        VehicleScript.Position makeCopy() {
            VehicleScript.Position position0 = new VehicleScript.Position();
            position0.id = this.id;
            position0.offset.set(this.offset);
            position0.rotate.set(this.rotate);
            return position0;
        }
    }

    public static final class Skin {
        public String texture;
        public String textureRust = null;
        public String textureMask = null;
        public String textureLights = null;
        public String textureDamage1Overlay = null;
        public String textureDamage1Shell = null;
        public String textureDamage2Overlay = null;
        public String textureDamage2Shell = null;
        public String textureShadow = null;
        public Texture textureData;
        public Texture textureDataRust;
        public Texture textureDataMask;
        public Texture textureDataLights;
        public Texture textureDataDamage1Overlay;
        public Texture textureDataDamage1Shell;
        public Texture textureDataDamage2Overlay;
        public Texture textureDataDamage2Shell;
        public Texture textureDataShadow;

        public void copyMissingFrom(VehicleScript.Skin other) {
            if (this.textureRust == null) {
                this.textureRust = other.textureRust;
            }

            if (this.textureMask == null) {
                this.textureMask = other.textureMask;
            }

            if (this.textureLights == null) {
                this.textureLights = other.textureLights;
            }

            if (this.textureDamage1Overlay == null) {
                this.textureDamage1Overlay = other.textureDamage1Overlay;
            }

            if (this.textureDamage1Shell == null) {
                this.textureDamage1Shell = other.textureDamage1Shell;
            }

            if (this.textureDamage2Overlay == null) {
                this.textureDamage2Overlay = other.textureDamage2Overlay;
            }

            if (this.textureDamage2Shell == null) {
                this.textureDamage2Shell = other.textureDamage2Shell;
            }

            if (this.textureShadow == null) {
                this.textureShadow = other.textureShadow;
            }
        }
    }

    public static final class Sounds {
        public boolean hornEnable = false;
        public String horn = "";
        public boolean backSignalEnable = false;
        public String backSignal = "";
        public String engine = null;
        public String engineStart = null;
        public String engineTurnOff = null;
        public String ignitionFail = null;
        public String ignitionFailNoPower = null;
    }

    public static final class Wheel {
        public String id;
        public String model;
        public boolean front;
        public final Vector3f offset = new Vector3f();
        public float radius = 0.5F;
        public float width = 0.4F;

        public String getId() {
            return this.id;
        }

        public Vector3f getOffset() {
            return this.offset;
        }

        VehicleScript.Wheel makeCopy() {
            VehicleScript.Wheel wheel0 = new VehicleScript.Wheel();
            wheel0.id = this.id;
            wheel0.model = this.model;
            wheel0.front = this.front;
            wheel0.offset.set(this.offset);
            wheel0.radius = this.radius;
            wheel0.width = this.width;
            return wheel0;
        }
    }

    public static final class Window {
        public boolean openable;

        VehicleScript.Window makeCopy() {
            VehicleScript.Window window0 = new VehicleScript.Window();
            window0.openable = this.openable;
            return window0;
        }
    }
}
