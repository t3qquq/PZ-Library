// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import fmod.fmod.FMODSoundEmitter;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.ai.State;
import zombie.ai.states.ZombieIdleState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.network.GameServer;

public final class AttackVehicleState extends State {
    private static final AttackVehicleState _instance = new AttackVehicleState();
    private BaseSoundEmitter emitter;
    private final Vector3f worldPos = new Vector3f();

    public static AttackVehicleState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter var1) {
    }

    @Override
    public void execute(IsoGameCharacter character0) {
        IsoZombie zombie0 = (IsoZombie)character0;
        if (zombie0.target instanceof IsoGameCharacter character1) {
            if (character1.isDead()) {
                if (character1.getLeaveBodyTimedown() > 3600.0F) {
                    zombie0.changeState(ZombieIdleState.instance());
                    zombie0.setTarget(null);
                } else {
                    character1.setLeaveBodyTimedown(character1.getLeaveBodyTimedown() + GameTime.getInstance().getMultiplier() / 1.6F);
                    if (!GameServer.bServer && !Core.SoundDisabled && Rand.Next(Rand.AdjustForFramerate(15)) == 0) {
                        if (this.emitter == null) {
                            this.emitter = new FMODSoundEmitter();
                        }

                        String string = zombie0.isFemale() ? "FemaleZombieEating" : "MaleZombieEating";
                        if (!this.emitter.isPlaying(string)) {
                            this.emitter.playSound(string);
                        }
                    }
                }

                zombie0.TimeSinceSeenFlesh = 0.0F;
            } else {
                BaseVehicle vehicle = character1.getVehicle();
                if (vehicle != null && vehicle.isCharacterAdjacentTo(character0)) {
                    Vector3f vector3f = vehicle.chooseBestAttackPosition(character1, character0, this.worldPos);
                    if (vector3f == null) {
                        if (zombie0.AllowRepathDelay <= 0.0F) {
                            character0.pathToCharacter(character1);
                            zombie0.AllowRepathDelay = 6.25F;
                        }
                    } else if (vector3f != null && (Math.abs(vector3f.x - character0.x) > 0.1F || Math.abs(vector3f.y - character0.y) > 0.1F)) {
                        if (!(Math.abs(vehicle.getCurrentSpeedKmHour()) > 0.8F)
                            || !vehicle.isCharacterAdjacentTo(character0) && !(vehicle.DistToSquared(character0) < 16.0F)) {
                            if (zombie0.AllowRepathDelay <= 0.0F) {
                                character0.pathToCharacter(character1);
                                zombie0.AllowRepathDelay = 6.25F;
                            }
                        }
                    } else {
                        character0.faceThisObject(character1);
                    }
                }
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter var1) {
    }

    @Override
    public void animEvent(IsoGameCharacter character0, AnimEvent animEvent) {
        IsoZombie zombie0 = (IsoZombie)character0;
        if (zombie0.target instanceof IsoGameCharacter character1) {
            BaseVehicle vehicle = character1.getVehicle();
            if (vehicle != null) {
                if (!character1.isDead()) {
                    if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
                        character1.getBodyDamage().AddRandomDamageFromZombie(zombie0, null);
                        character1.getBodyDamage().Update();
                        if (character1.isDead()) {
                            if (character1.isFemale()) {
                                zombie0.getEmitter().playVocals("FemaleBeingEatenDeath");
                            } else {
                                zombie0.getEmitter().playVocals("MaleBeingEatenDeath");
                            }

                            character1.setHealth(0.0F);
                        } else if (character1.isAsleep()) {
                            if (GameServer.bServer) {
                                character1.sendObjectChange("wakeUp");
                                character1.setAsleep(false);
                            } else {
                                character1.forceAwake();
                            }
                        }
                    } else if (animEvent.m_EventName.equalsIgnoreCase("ThumpFrame")) {
                        VehicleWindow vehicleWindow = null;
                        VehiclePart part0 = null;
                        int int0 = vehicle.getSeat(character1);
                        String string = vehicle.getPassengerArea(int0);
                        if (vehicle.isInArea(string, character0)) {
                            VehiclePart part1 = vehicle.getPassengerDoor(int0);
                            if (part1 != null && part1.getDoor() != null && part1.getInventoryItem() != null && !part1.getDoor().isOpen()) {
                                vehicleWindow = part1.findWindow();
                                if (vehicleWindow != null && !vehicleWindow.isHittable()) {
                                    vehicleWindow = null;
                                }

                                if (vehicleWindow == null) {
                                    part0 = part1;
                                }
                            }
                        } else {
                            part0 = vehicle.getNearestBodyworkPart(character0);
                            if (part0 != null) {
                                vehicleWindow = part0.getWindow();
                                if (vehicleWindow == null) {
                                    vehicleWindow = part0.findWindow();
                                }

                                if (vehicleWindow != null && !vehicleWindow.isHittable()) {
                                    vehicleWindow = null;
                                }

                                if (vehicleWindow != null) {
                                    part0 = null;
                                }
                            }
                        }

                        if (vehicleWindow != null) {
                            vehicleWindow.damage(zombie0.strength);
                            vehicle.setBloodIntensity(vehicleWindow.part.getId(), vehicle.getBloodIntensity(vehicleWindow.part.getId()) + 0.025F);
                            if (!GameServer.bServer) {
                                zombie0.setVehicleHitLocation(vehicle);
                                character0.getEmitter().playSound("ZombieThumpVehicleWindow", vehicle);
                            }

                            zombie0.setThumpFlag(3);
                        } else {
                            if (!GameServer.bServer) {
                                zombie0.setVehicleHitLocation(vehicle);
                                character0.getEmitter().playSound("ZombieThumpVehicle", vehicle);
                            }

                            zombie0.setThumpFlag(1);
                        }

                        vehicle.setAddThumpWorldSound(true);
                        if (part0 != null && part0.getWindow() == null && part0.getCondition() > 0) {
                            part0.setCondition(part0.getCondition() - zombie0.strength);
                            part0.doInventoryItemStats(part0.getInventoryItem(), 0);
                            vehicle.transmitPartCondition(part0);
                        }

                        if (character1.isAsleep()) {
                            if (GameServer.bServer) {
                                character1.sendObjectChange("wakeUp");
                                character1.setAsleep(false);
                            } else {
                                character1.forceAwake();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isAttacking(IsoGameCharacter var1) {
        return true;
    }

    public boolean isPassengerExposed(IsoGameCharacter character0) {
        if (!(character0 instanceof IsoZombie zombie0)) {
            return false;
        } else if (!(zombie0.target instanceof IsoGameCharacter character1)) {
            return false;
        } else {
            BaseVehicle vehicle = character1.getVehicle();
            if (vehicle == null) {
                return false;
            } else {
                boolean boolean0 = false;
                Object object = null;
                int int0 = vehicle.getSeat(character1);
                String string = vehicle.getPassengerArea(int0);
                VehiclePart part = null;
                if (vehicle.isInArea(string, character0)) {
                    part = vehicle.getPassengerDoor(int0);
                    if (part != null && part.getDoor() != null) {
                        if (part.getInventoryItem() != null && !part.getDoor().isOpen()) {
                            object = part.findWindow();
                            if (object != null) {
                                if (!((VehicleWindow)object).isHittable()) {
                                    object = null;
                                }

                                boolean0 = object == null;
                            } else {
                                boolean0 = false;
                            }
                        } else {
                            boolean0 = true;
                        }
                    }
                } else {
                    part = vehicle.getNearestBodyworkPart(character0);
                    if (part != null) {
                        object = part.findWindow();
                        if (object != null && !((VehicleWindow)object).isHittable()) {
                            object = null;
                        }
                    }
                }

                return boolean0;
            }
        }
    }
}
