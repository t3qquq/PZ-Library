// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import zombie.GameSounds;
import zombie.audio.parameters.ParameterCurrentZone;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;
import zombie.popman.ObjectPool;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;

public final class ObjectAmbientEmitters {
    private final HashMap<String, ObjectAmbientEmitters.PowerPolicy> powerPolicyMap = new HashMap<>();
    private static ObjectAmbientEmitters instance = null;
    static final Vector2 tempVector2 = new Vector2();
    private final HashMap<IsoObject, ObjectAmbientEmitters.ObjectWithDistance> m_added = new HashMap<>();
    private final ObjectPool<ObjectAmbientEmitters.ObjectWithDistance> m_objectPool = new ObjectPool<>(ObjectAmbientEmitters.ObjectWithDistance::new);
    private final ArrayList<ObjectAmbientEmitters.ObjectWithDistance> m_objects = new ArrayList<>();
    private final ObjectAmbientEmitters.Slot[] m_slots;
    private final Comparator<ObjectAmbientEmitters.ObjectWithDistance> comp = new Comparator<ObjectAmbientEmitters.ObjectWithDistance>() {
        public int compare(ObjectAmbientEmitters.ObjectWithDistance objectWithDistance1, ObjectAmbientEmitters.ObjectWithDistance objectWithDistance0) {
            return Float.compare(objectWithDistance1.distSq, objectWithDistance0.distSq);
        }
    };

    public static ObjectAmbientEmitters getInstance() {
        if (instance == null) {
            instance = new ObjectAmbientEmitters();
        }

        return instance;
    }

    private ObjectAmbientEmitters() {
        byte byte0 = 16;
        this.m_slots = PZArrayUtil.newInstance(ObjectAmbientEmitters.Slot.class, byte0, ObjectAmbientEmitters.Slot::new);
        this.powerPolicyMap.put("FactoryMachineAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
        this.powerPolicyMap.put("HotdogMachineAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
        this.powerPolicyMap.put("PayPhoneAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
        this.powerPolicyMap.put("StreetLightAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
        this.powerPolicyMap.put("NeonLightAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
        this.powerPolicyMap.put("NeonSignAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
        this.powerPolicyMap.put("JukeboxAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
        this.powerPolicyMap.put("ControlStationAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
        this.powerPolicyMap.put("ClockAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
        this.powerPolicyMap.put("GasPumpAmbiance", ObjectAmbientEmitters.PowerPolicy.ExteriorOK);
        this.powerPolicyMap.put("LightBulbAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
        this.powerPolicyMap.put("ArcadeMachineAmbiance", ObjectAmbientEmitters.PowerPolicy.InteriorHydro);
    }

    private void addObject(IsoObject object, ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
        if (!GameServer.bServer) {
            if (!this.m_added.containsKey(object)) {
                boolean boolean0 = false;

                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player != null && object.getObjectIndex() != -1) {
                        byte byte0 = 15;
                        if (perObjectLogic instanceof ObjectAmbientEmitters.DoorLogic || perObjectLogic instanceof ObjectAmbientEmitters.WindowLogic) {
                            byte0 = 10;
                        }

                        if ((
                                object.square.z == PZMath.fastfloor(player.getZ())
                                    || !(perObjectLogic instanceof ObjectAmbientEmitters.DoorLogic)
                                        && !(perObjectLogic instanceof ObjectAmbientEmitters.WindowLogic)
                            )
                            && !(player.DistToSquared(object.square.x + 0.5F, object.square.y + 0.5F) > byte0 * byte0)) {
                            boolean0 = true;
                            break;
                        }
                    }
                }

                if (boolean0) {
                    ObjectAmbientEmitters.ObjectWithDistance objectWithDistance = this.m_objectPool.alloc();
                    objectWithDistance.object = object;
                    objectWithDistance.logic = perObjectLogic;
                    this.m_objects.add(objectWithDistance);
                    this.m_added.put(object, objectWithDistance);
                }
            }
        }
    }

    void removeObject(IsoObject object) {
        if (!GameServer.bServer) {
            ObjectAmbientEmitters.ObjectWithDistance objectWithDistance = this.m_added.remove(object);
            if (objectWithDistance != null) {
                this.m_objects.remove(objectWithDistance);
                this.m_objectPool.release(objectWithDistance);
            }
        }
    }

    public void update() {
        if (!GameServer.bServer) {
            this.addObjectsFromChunks();

            for (int int0 = 0; int0 < this.m_slots.length; int0++) {
                this.m_slots[int0].playing = false;
            }

            if (this.m_objects.isEmpty()) {
                this.stopNotPlaying();
            } else {
                for (int int1 = 0; int1 < this.m_objects.size(); int1++) {
                    ObjectAmbientEmitters.ObjectWithDistance objectWithDistance = this.m_objects.get(int1);
                    IsoObject object0 = objectWithDistance.object;
                    ObjectAmbientEmitters.PerObjectLogic perObjectLogic0 = this.m_objects.get(int1).logic;
                    if (!this.shouldPlay(object0, perObjectLogic0)) {
                        this.m_added.remove(object0);
                        this.m_objects.remove(int1--);
                        this.m_objectPool.release(objectWithDistance);
                    } else {
                        object0.getFacingPosition(tempVector2);
                        objectWithDistance.distSq = this.getClosestListener(tempVector2.x, tempVector2.y, object0.square.z);
                    }
                }

                this.m_objects.sort(this.comp);
                int int2 = Math.min(this.m_objects.size(), this.m_slots.length);

                for (int int3 = 0; int3 < int2; int3++) {
                    IsoObject object1 = this.m_objects.get(int3).object;
                    ObjectAmbientEmitters.PerObjectLogic perObjectLogic1 = this.m_objects.get(int3).logic;
                    if (this.shouldPlay(object1, perObjectLogic1)) {
                        int int4 = this.getExistingSlot(object1);
                        if (int4 != -1) {
                            this.m_slots[int4].playSound(object1, perObjectLogic1);
                        }
                    }
                }

                for (int int5 = 0; int5 < int2; int5++) {
                    IsoObject object2 = this.m_objects.get(int5).object;
                    ObjectAmbientEmitters.PerObjectLogic perObjectLogic2 = this.m_objects.get(int5).logic;
                    if (this.shouldPlay(object2, perObjectLogic2)) {
                        int int6 = this.getExistingSlot(object2);
                        if (int6 == -1) {
                            int6 = this.getFreeSlot();
                            if (this.m_slots[int6].object != null) {
                                this.m_slots[int6].stopPlaying();
                                this.m_slots[int6].object = null;
                            }

                            this.m_slots[int6].playSound(object2, perObjectLogic2);
                        }
                    }
                }

                this.stopNotPlaying();
                this.m_added.clear();
                this.m_objectPool.release(this.m_objects);
                this.m_objects.clear();
            }
        }
    }

    void addObjectsFromChunks() {
        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int0];
            if (!chunkMap.ignore) {
                int int1 = IsoChunkMap.ChunkGridWidth / 2;
                int int2 = IsoChunkMap.ChunkGridWidth / 2;

                for (int int3 = -1; int3 <= 1; int3++) {
                    for (int int4 = -1; int4 <= 1; int4++) {
                        IsoChunk chunk = chunkMap.getChunk(int1 + int4, int2 + int3);
                        if (chunk != null) {
                            for (IsoObject object : chunk.m_objectEmitterData.m_objects.keySet()) {
                                this.addObject(object, chunk.m_objectEmitterData.m_objects.get(object));
                            }
                        }
                    }
                }
            }
        }
    }

    float getClosestListener(float float5, float float6, float float7) {
        float float0 = Float.MAX_VALUE;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null && player.getCurrentSquare() != null) {
                float float1 = player.getX();
                float float2 = player.getY();
                float float3 = player.getZ();
                float float4 = IsoUtils.DistanceToSquared(float1, float2, float3 * 3.0F, float5, float6, float7 * 3.0F);
                if (player.Traits.HardOfHearing.isSet()) {
                    float4 *= 4.5F;
                }

                if (float4 < float0) {
                    float0 = float4;
                }
            }
        }

        return float0;
    }

    boolean shouldPlay(IsoObject object, ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
        if (object == null) {
            return false;
        } else {
            return object.getObjectIndex() == -1 ? false : perObjectLogic.shouldPlaySound();
        }
    }

    int getExistingSlot(IsoObject object) {
        for (int int0 = 0; int0 < this.m_slots.length; int0++) {
            if (this.m_slots[int0].object == object) {
                return int0;
            }
        }

        return -1;
    }

    int getFreeSlot() {
        for (int int0 = 0; int0 < this.m_slots.length; int0++) {
            if (!this.m_slots[int0].playing) {
                return int0;
            }
        }

        return -1;
    }

    void stopNotPlaying() {
        for (int int0 = 0; int0 < this.m_slots.length; int0++) {
            ObjectAmbientEmitters.Slot slot = this.m_slots[int0];
            if (!slot.playing) {
                slot.stopPlaying();
                slot.object = null;
            }
        }
    }

    public void render() {
        if (DebugOptions.instance.ObjectAmbientEmitterRender.getValue()) {
            IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[IsoCamera.frameState.playerIndex];
            if (!chunkMap.ignore) {
                int int0 = IsoChunkMap.ChunkGridWidth / 2;
                int int1 = IsoChunkMap.ChunkGridWidth / 2;

                for (int int2 = -1; int2 <= 1; int2++) {
                    for (int int3 = -1; int3 <= 1; int3++) {
                        IsoChunk chunk = chunkMap.getChunk(int0 + int3, int1 + int2);
                        if (chunk != null) {
                            for (IsoObject object0 : chunk.m_objectEmitterData.m_objects.keySet()) {
                                if (object0.square.z == (int)IsoCamera.frameState.CamCharacterZ) {
                                    object0.getFacingPosition(tempVector2);
                                    float float0 = tempVector2.x;
                                    float float1 = tempVector2.y;
                                    float float2 = object0.square.z;
                                    LineDrawer.addLine(
                                        float0 - 0.45F, float1 - 0.45F, float2, float0 + 0.45F, float1 + 0.45F, float2, 0.5F, 0.5F, 0.5F, null, false
                                    );
                                }
                            }
                        }
                    }
                }
            }

            for (int int4 = 0; int4 < this.m_slots.length; int4++) {
                ObjectAmbientEmitters.Slot slot = this.m_slots[int4];
                if (slot.playing) {
                    IsoObject object1 = slot.object;
                    object1.getFacingPosition(tempVector2);
                    float float3 = tempVector2.x;
                    float float4 = tempVector2.y;
                    float float5 = object1.square.z;
                    LineDrawer.addLine(float3 - 0.45F, float4 - 0.45F, float5, float3 + 0.45F, float4 + 0.45F, float5, 0.0F, 0.0F, 1.0F, null, false);
                }
            }
        }
    }

    public static void Reset() {
        if (instance != null) {
            for (int int0 = 0; int0 < instance.m_slots.length; int0++) {
                instance.m_slots[int0].stopPlaying();
                instance.m_slots[int0].object = null;
                instance.m_slots[int0].playing = false;
            }
        }
    }

    public static final class AmbientSoundLogic extends ObjectAmbientEmitters.PerObjectLogic {
        ObjectAmbientEmitters.PowerPolicy powerPolicy = ObjectAmbientEmitters.PowerPolicy.NotRequired;
        boolean bHasGeneratorParameter = false;

        @Override
        public ObjectAmbientEmitters.PerObjectLogic init(IsoObject object) {
            super.init(object);
            String string = this.getSoundName();
            this.powerPolicy = ObjectAmbientEmitters.getInstance().powerPolicyMap.getOrDefault(string, ObjectAmbientEmitters.PowerPolicy.NotRequired);
            if (this.powerPolicy != ObjectAmbientEmitters.PowerPolicy.NotRequired) {
                GameSound gameSound = GameSounds.getSound(string);
                this.bHasGeneratorParameter = gameSound != null && gameSound.numClipsUsingParameter("Generator") > 0;
            }

            return this;
        }

        @Override
        public boolean shouldPlaySound() {
            if (this.powerPolicy == ObjectAmbientEmitters.PowerPolicy.InteriorHydro) {
                boolean boolean0 = this.object.square.haveElectricity() || IsoWorld.instance.isHydroPowerOn() && this.object.square.getRoom() != null;
                if (!boolean0) {
                    return false;
                }
            }

            if (this.powerPolicy == ObjectAmbientEmitters.PowerPolicy.ExteriorOK) {
                boolean boolean1 = this.object.square.haveElectricity() || IsoWorld.instance.isHydroPowerOn();
                if (!boolean1) {
                    return false;
                }
            }

            if (this.powerPolicy != ObjectAmbientEmitters.PowerPolicy.NotRequired && !IsoWorld.instance.isHydroPowerOn() && !this.bHasGeneratorParameter) {
                return false;
            } else {
                PropertyContainer propertyContainer = this.object.getProperties();
                return propertyContainer != null && propertyContainer.Is("AmbientSound");
            }
        }

        @Override
        public String getSoundName() {
            return this.object.getProperties().Val("AmbientSound");
        }

        @Override
        public void startPlaying(BaseSoundEmitter var1, long var2) {
        }

        @Override
        public void stopPlaying(BaseSoundEmitter var1, long var2) {
            this.parameterValue1 = Float.NaN;
        }

        @Override
        public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long0) {
            if (this.powerPolicy != ObjectAmbientEmitters.PowerPolicy.NotRequired) {
                this.setParameterValue1(baseSoundEmitter, long0, "Generator", IsoWorld.instance.isHydroPowerOn() ? 0.0F : 1.0F);
            }
        }
    }

    public static final class ChunkData {
        final HashMap<IsoObject, ObjectAmbientEmitters.PerObjectLogic> m_objects = new HashMap<>();

        public boolean hasObject(IsoObject object) {
            return this.m_objects.containsKey(object);
        }

        public void addObject(IsoObject object, ObjectAmbientEmitters.PerObjectLogic logic) {
            if (!this.m_objects.containsKey(object)) {
                this.m_objects.put(object, logic);
            }
        }

        public void removeObject(IsoObject object) {
            this.m_objects.remove(object);
        }

        public void reset() {
            this.m_objects.clear();
        }
    }

    public static final class DoorLogic extends ObjectAmbientEmitters.PerObjectLogic {
        @Override
        public boolean shouldPlaySound() {
            return true;
        }

        @Override
        public String getSoundName() {
            return "DoorAmbiance";
        }

        @Override
        public void startPlaying(BaseSoundEmitter var1, long var2) {
        }

        @Override
        public void stopPlaying(BaseSoundEmitter var1, long var2) {
            this.parameterValue1 = Float.NaN;
        }

        @Override
        public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long0) {
            IsoDoor door = Type.tryCastTo(this.object, IsoDoor.class);
            float float0 = door.IsOpen() ? 1.0F : 0.0F;
            this.setParameterValue1(baseSoundEmitter, long0, "DoorWindowOpen", float0);
        }
    }

    public static final class FridgeHumLogic extends ObjectAmbientEmitters.PerObjectLogic {
        @Override
        public boolean shouldPlaySound() {
            ItemContainer container = this.object.getContainerByEitherType("fridge", "freezer");
            return container != null && container.isPowered();
        }

        @Override
        public String getSoundName() {
            return "FridgeHum";
        }

        @Override
        public void startPlaying(BaseSoundEmitter var1, long var2) {
        }

        @Override
        public void stopPlaying(BaseSoundEmitter var1, long var2) {
            this.parameterValue1 = Float.NaN;
        }

        @Override
        public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long0) {
            this.setParameterValue1(baseSoundEmitter, long0, "Generator", IsoWorld.instance.isHydroPowerOn() ? 0.0F : 1.0F);
        }
    }

    static final class ObjectWithDistance {
        IsoObject object;
        ObjectAmbientEmitters.PerObjectLogic logic;
        float distSq;
    }

    public abstract static class PerObjectLogic {
        public IsoObject object;
        public float parameterValue1 = Float.NaN;

        public ObjectAmbientEmitters.PerObjectLogic init(IsoObject _object) {
            this.object = _object;
            return this;
        }

        void setParameterValue1(BaseSoundEmitter baseSoundEmitter, long long0, String string, float float0) {
            if (float0 != this.parameterValue1) {
                this.parameterValue1 = float0;
                FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_studio_parameter_description = FMODManager.instance.getParameterDescription(string);
                baseSoundEmitter.setParameterValue(long0, fmod_studio_parameter_description, float0);
            }
        }

        void setParameterValue1(
            BaseSoundEmitter baseSoundEmitter, long long0, FMOD_STUDIO_PARAMETER_DESCRIPTION fmod_studio_parameter_description, float float0
        ) {
            if (float0 != this.parameterValue1) {
                this.parameterValue1 = float0;
                baseSoundEmitter.setParameterValue(long0, fmod_studio_parameter_description, float0);
            }
        }

        public abstract boolean shouldPlaySound();

        public abstract String getSoundName();

        public abstract void startPlaying(BaseSoundEmitter emitter, long instance);

        public abstract void stopPlaying(BaseSoundEmitter emitter, long instance);

        public abstract void checkParameters(BaseSoundEmitter emitter, long instance);
    }

    static enum PowerPolicy {
        NotRequired,
        InteriorHydro,
        ExteriorOK;
    }

    static final class Slot {
        IsoObject object = null;
        ObjectAmbientEmitters.PerObjectLogic logic = null;
        BaseSoundEmitter emitter = null;
        long instance = 0L;
        boolean playing = false;

        void playSound(IsoObject objectx, ObjectAmbientEmitters.PerObjectLogic perObjectLogic) {
            if (this.emitter == null) {
                this.emitter = (BaseSoundEmitter)(Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter());
            }

            objectx.getFacingPosition(ObjectAmbientEmitters.tempVector2);
            this.emitter.setPos(ObjectAmbientEmitters.tempVector2.getX(), ObjectAmbientEmitters.tempVector2.getY(), objectx.square.z);
            this.object = objectx;
            this.logic = perObjectLogic;
            String string = perObjectLogic.getSoundName();
            if (!this.emitter.isPlaying(string)) {
                this.emitter.stopAll();
                FMODSoundEmitter fMODSoundEmitter = Type.tryCastTo(this.emitter, FMODSoundEmitter.class);
                if (fMODSoundEmitter != null) {
                    fMODSoundEmitter.clearParameters();
                }

                this.instance = this.emitter.playSoundImpl(string, (IsoObject)null);
                perObjectLogic.startPlaying(this.emitter, this.instance);
            }

            perObjectLogic.checkParameters(this.emitter, this.instance);
            this.playing = true;
            this.emitter.tick();
        }

        void stopPlaying() {
            if (this.emitter != null && this.instance != 0L) {
                this.logic.stopPlaying(this.emitter, this.instance);
                if (this.emitter.hasSustainPoints(this.instance)) {
                    this.emitter.triggerCue(this.instance);
                    this.instance = 0L;
                } else {
                    this.emitter.stopAll();
                    this.instance = 0L;
                }
            }
        }
    }

    public static final class TentAmbianceLogic extends ObjectAmbientEmitters.PerObjectLogic {
        @Override
        public boolean shouldPlaySound() {
            return this.object.sprite != null
                && this.object.sprite.getName() != null
                && this.object.sprite.getName().startsWith("camping_01")
                && (this.object.sprite.tileSheetIndex == 0 || this.object.sprite.tileSheetIndex == 3);
        }

        @Override
        public String getSoundName() {
            return "TentAmbiance";
        }

        @Override
        public void startPlaying(BaseSoundEmitter var1, long var2) {
        }

        @Override
        public void stopPlaying(BaseSoundEmitter var1, long var2) {
        }

        @Override
        public void checkParameters(BaseSoundEmitter var1, long var2) {
        }
    }

    public static final class TreeAmbianceLogic extends ObjectAmbientEmitters.PerObjectLogic {
        @Override
        public boolean shouldPlaySound() {
            return true;
        }

        @Override
        public String getSoundName() {
            return "TreeAmbiance";
        }

        @Override
        public void startPlaying(BaseSoundEmitter baseSoundEmitter, long var2) {
            FMODSoundEmitter fMODSoundEmitter = Type.tryCastTo(baseSoundEmitter, FMODSoundEmitter.class);
            if (fMODSoundEmitter != null) {
                fMODSoundEmitter.addParameter(new ParameterCurrentZone(this.object));
            }

            baseSoundEmitter.playAmbientLoopedImpl("BirdInTree");
        }

        @Override
        public void stopPlaying(BaseSoundEmitter baseSoundEmitter, long var2) {
            baseSoundEmitter.stopOrTriggerSoundByName("BirdInTree");
        }

        @Override
        public void checkParameters(BaseSoundEmitter var1, long var2) {
        }
    }

    public static final class WaterDripLogic extends ObjectAmbientEmitters.PerObjectLogic {
        @Override
        public boolean shouldPlaySound() {
            return this.object.sprite != null && this.object.sprite.getProperties().Is(IsoFlagType.waterPiped) && this.object.getWaterAmount() > 0.0F;
        }

        @Override
        public String getSoundName() {
            return "WaterDrip";
        }

        @Override
        public void startPlaying(BaseSoundEmitter baseSoundEmitter, long long0) {
            if (this.object.sprite != null && this.object.sprite.getProperties().Is("SinkType")) {
                String string = this.object.sprite.getProperties().Val("SinkType");

                byte byte0 = switch (string) {
                    case "Ceramic" -> 1;
                    case "Metal" -> 2;
                    default -> 0;
                };
                this.setParameterValue1(baseSoundEmitter, long0, "SinkType", byte0);
            }
        }

        @Override
        public void stopPlaying(BaseSoundEmitter var1, long var2) {
            this.parameterValue1 = Float.NaN;
        }

        @Override
        public void checkParameters(BaseSoundEmitter var1, long var2) {
        }
    }

    public static final class WindowLogic extends ObjectAmbientEmitters.PerObjectLogic {
        @Override
        public boolean shouldPlaySound() {
            return true;
        }

        @Override
        public String getSoundName() {
            return "WindowAmbiance";
        }

        @Override
        public void startPlaying(BaseSoundEmitter var1, long var2) {
        }

        @Override
        public void stopPlaying(BaseSoundEmitter var1, long var2) {
            this.parameterValue1 = Float.NaN;
        }

        @Override
        public void checkParameters(BaseSoundEmitter baseSoundEmitter, long long0) {
            IsoWindow window = Type.tryCastTo(this.object, IsoWindow.class);
            float float0 = !window.IsOpen() && !window.isDestroyed() ? 0.0F : 1.0F;
            this.setParameterValue1(baseSoundEmitter, long0, "DoorWindowOpen", float0);
        }
    }
}
