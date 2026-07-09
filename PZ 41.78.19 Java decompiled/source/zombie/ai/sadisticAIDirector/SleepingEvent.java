// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.sadisticAIDirector;

import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZombieSpawnRecorder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoWindow;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;

public final class SleepingEvent {
    public static final SleepingEvent instance = new SleepingEvent();
    public static boolean zombiesInvasion = false;

    public void setPlayerFallAsleep(IsoPlayer chr, int sleepingTime) {
        SleepingEventData sleepingEventData = chr.getOrCreateSleepingEventData();
        sleepingEventData.reset();
        if (!IsoWorld.getZombiesEnabled()) {
            if (ClimateManager.getInstance().isRaining() && this.isExposedToPrecipitation(chr)) {
                sleepingEventData.bRaining = true;
                sleepingEventData.bWasRainingAtStart = true;
                sleepingEventData.rainTimeStartHours = GameTime.getInstance().getWorldAgeHours();
            }

            sleepingEventData.sleepingTime = sleepingTime;
            chr.setTimeOfSleep(GameTime.instance.getTimeOfDay());
            this.doDelayToSleep(chr);
            this.checkNightmare(chr, sleepingTime);
            if (sleepingEventData.nightmareWakeUp <= -1) {
                if (SandboxOptions.instance.SleepingEvent.getValue() != 1 && zombiesInvasion) {
                    if (chr.getCurrentSquare() == null || chr.getCurrentSquare().getZone() == null || !chr.getCurrentSquare().getZone().haveConstruction) {
                        boolean boolean0 = false;
                        if ((GameTime.instance.getHour() >= 0 && GameTime.instance.getHour() < 5 || GameTime.instance.getHour() > 18) && sleepingTime >= 4) {
                            boolean0 = true;
                        }

                        byte byte0 = 20;
                        if (SandboxOptions.instance.SleepingEvent.getValue() == 3) {
                            byte0 = 45;
                        }

                        if (Rand.Next(100) <= byte0 && chr.getCell().getZombieList().size() >= 1 && sleepingTime >= 4) {
                            int int0 = 0;
                            if (chr.getCurrentBuilding() != null) {
                                IsoGridSquare square = null;
                                Object object0 = null;

                                for (int int1 = 0; int1 < 3; int1++) {
                                    for (int int2 = chr.getCurrentBuilding().getDef().getX() - 2; int2 < chr.getCurrentBuilding().getDef().getX2() + 2; int2++) {
                                        for (int int3 = chr.getCurrentBuilding().getDef().getY() - 2;
                                            int3 < chr.getCurrentBuilding().getDef().getY2() + 2;
                                            int3++
                                        ) {
                                            square = IsoWorld.instance.getCell().getGridSquare(int2, int3, int1);
                                            if (square != null) {
                                                boolean boolean1 = square.haveElectricity() || IsoWorld.instance.isHydroPowerOn();
                                                if (boolean1) {
                                                    for (int int4 = 0; int4 < square.getObjects().size(); int4++) {
                                                        IsoObject object1 = square.getObjects().get(int4);
                                                        if (object1.getContainer() != null
                                                            && (
                                                                object1.getContainer().getType().equals("fridge")
                                                                    || object1.getContainer().getType().equals("freezer")
                                                            )) {
                                                            int0 += 3;
                                                        }

                                                        if (object1 instanceof IsoStove && ((IsoStove)object1).Activated()) {
                                                            int0 += 5;
                                                        }

                                                        if (object1 instanceof IsoTelevision && ((IsoTelevision)object1).getDeviceData().getIsTurnedOn()) {
                                                            int0 += 30;
                                                        }

                                                        if (object1 instanceof IsoRadio && ((IsoRadio)object1).getDeviceData().getIsTurnedOn()) {
                                                            int0 += 30;
                                                        }
                                                    }
                                                }

                                                object0 = square.getWindow();
                                                if (object0 != null) {
                                                    int0 += this.checkWindowStatus((IsoWindow)object0);
                                                }

                                                IsoDoor door = square.getIsoDoor();
                                                if (door != null && door.isExteriorDoor(null) && door.IsOpen()) {
                                                    int0 += 25;
                                                    sleepingEventData.openDoor = door;
                                                }
                                            }
                                        }
                                    }
                                }

                                if (SandboxOptions.instance.SleepingEvent.getValue() == 3) {
                                    int0 = (int)(int0 * 1.5);
                                }

                                if (int0 > 70) {
                                    int0 = 70;
                                }

                                if (!boolean0) {
                                    int0 /= 2;
                                }

                                if (Rand.Next(100) <= int0) {
                                    sleepingEventData.forceWakeUpTime = Rand.Next(sleepingTime - 4, sleepingTime - 1);
                                    sleepingEventData.zombiesIntruders = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void doDelayToSleep(IsoPlayer player) {
        float float0 = 0.3F;
        float float1 = 2.0F;
        if (player.Traits.Insomniac.isSet()) {
            float0 = 1.0F;
        }

        if (player.getMoodles().getMoodleLevel(MoodleType.Pain) > 0) {
            float0 += 1.0F + player.getMoodles().getMoodleLevel(MoodleType.Pain) * 0.2F;
        }

        if (player.getMoodles().getMoodleLevel(MoodleType.Stress) > 0) {
            float0 *= 1.2F;
        }

        if ("badBed".equals(player.getBedType())) {
            float0 *= 1.3F;
        } else if ("goodBed".equals(player.getBedType())) {
            float0 *= 0.8F;
        } else if ("floor".equals(player.getBedType())) {
            float0 *= 1.6F;
        }

        if (player.Traits.NightOwl.isSet()) {
            float0 *= 0.5F;
        }

        if (player.getSleepingTabletEffect() > 1000.0F) {
            float0 = 0.1F;
        }

        if (float0 > float1) {
            float0 = float1;
        }

        float float2 = Rand.Next(0.0F, float0);
        player.setDelayToSleep(GameTime.instance.getTimeOfDay() + float2);
    }

    private void checkNightmare(IsoPlayer player, int int0) {
        if (!GameClient.bClient) {
            SleepingEventData sleepingEventData = player.getOrCreateSleepingEventData();
            if (int0 >= 3) {
                int int1 = 5 + player.getMoodles().getMoodleLevel(MoodleType.Stress) * 10;
                if (Rand.Next(100) < int1) {
                    sleepingEventData.nightmareWakeUp = Rand.Next(3, int0 - 2);
                }
            }
        }
    }

    private int checkWindowStatus(IsoWindow window) {
        IsoGridSquare square = window.getSquare();
        if (window.getSquare().getRoom() == null) {
            if (!window.north) {
                square = window.getSquare().getCell().getGridSquare(window.getSquare().getX() - 1, window.getSquare().getY(), window.getSquare().getZ());
            } else {
                square = window.getSquare().getCell().getGridSquare(window.getSquare().getX(), window.getSquare().getY() - 1, window.getSquare().getZ());
            }
        }

        byte byte0 = 0;
        boolean boolean0 = false;

        for (int int0 = 0; int0 < square.getRoom().lightSwitches.size(); int0++) {
            if (square.getRoom().lightSwitches.get(int0).isActivated()) {
                boolean0 = true;
                break;
            }
        }

        if (boolean0) {
            byte0 = 20;
            if (window.HasCurtains() != null && !window.HasCurtains().open) {
                byte0 -= 17;
            }

            IsoBarricade barricade0 = window.getBarricadeOnOppositeSquare();
            if (barricade0 == null) {
                barricade0 = window.getBarricadeOnSameSquare();
            }

            if (barricade0 != null && (barricade0.getNumPlanks() > 4 || barricade0.isMetal())) {
                byte0 -= 20;
            }

            if (byte0 < 0) {
                byte0 = 0;
            }

            if (square.getZ() > 0) {
                byte0 /= 2;
            }

            return byte0;
        } else {
            byte0 = 5;
            if (window.HasCurtains() != null && !window.HasCurtains().open) {
                byte0 -= 5;
            }

            IsoBarricade barricade1 = window.getBarricadeOnOppositeSquare();
            if (barricade1 == null) {
                barricade1 = window.getBarricadeOnSameSquare();
            }

            if (barricade1 != null && (barricade1.getNumPlanks() > 3 || barricade1.isMetal())) {
                byte0 -= 5;
            }

            if (byte0 < 0) {
                byte0 = 0;
            }

            if (square.getZ() > 0) {
                byte0 /= 2;
            }

            return byte0;
        }
    }

    public void update(IsoPlayer chr) {
        if (chr != null) {
            SleepingEventData sleepingEventData = chr.getOrCreateSleepingEventData();
            if (sleepingEventData.nightmareWakeUp == (int)chr.getAsleepTime()) {
                chr.getStats().Panic += 70.0F;
                chr.getStats().stress += 0.5F;
                WorldSoundManager.instance.addSound(chr, (int)chr.getX(), (int)chr.getY(), (int)chr.getZ(), 6, 1);
                SoundManager.instance.setMusicWakeState(chr, "WakeNightmare");
                this.wakeUp(chr);
            }

            if (sleepingEventData.forceWakeUpTime == (int)chr.getAsleepTime() && sleepingEventData.zombiesIntruders) {
                this.spawnZombieIntruders(chr);
                WorldSoundManager.instance.addSound(chr, (int)chr.getX(), (int)chr.getY(), (int)chr.getZ(), 6, 1);
                SoundManager.instance.setMusicWakeState(chr, "WakeZombies");
                this.wakeUp(chr);
            }

            this.updateRain(chr);
            this.updateSnow(chr);
            this.updateTemperature(chr);
            this.updateWetness(chr);
        }
    }

    private void updateRain(IsoPlayer player) {
        SleepingEventData sleepingEventData = player.getOrCreateSleepingEventData();
        if (!ClimateManager.getInstance().isRaining()) {
            sleepingEventData.bRaining = false;
            sleepingEventData.bWasRainingAtStart = false;
            sleepingEventData.rainTimeStartHours = -1.0;
        } else if (this.isExposedToPrecipitation(player)) {
            double double0 = GameTime.getInstance().getWorldAgeHours();
            if (!sleepingEventData.bWasRainingAtStart) {
                if (!sleepingEventData.bRaining) {
                    sleepingEventData.rainTimeStartHours = double0;
                }

                if (sleepingEventData.getHoursSinceRainStarted() >= 0.16666666666666666) {
                }
            }

            sleepingEventData.bRaining = true;
        }
    }

    private void updateSnow(IsoPlayer player) {
        if (ClimateManager.getInstance().isSnowing()) {
            if (this.isExposedToPrecipitation(player)) {
                ;
            }
        }
    }

    private void updateTemperature(IsoPlayer var1) {
    }

    private void updateWetness(IsoPlayer var1) {
    }

    private boolean isExposedToPrecipitation(IsoGameCharacter character) {
        if (character.getCurrentSquare() == null) {
            return false;
        } else if (character.getCurrentSquare().isInARoom() || character.getCurrentSquare().haveRoof) {
            return false;
        } else if (character.getBed() != null && "Tent".equals(character.getBed().getName())) {
            return false;
        } else {
            BaseVehicle vehicle = character.getVehicle();
            return vehicle == null || !vehicle.hasRoof(vehicle.getSeat(character));
        }
    }

    private void spawnZombieIntruders(IsoPlayer player) {
        SleepingEventData sleepingEventData = player.getOrCreateSleepingEventData();
        IsoGridSquare square = null;
        if (sleepingEventData.openDoor != null) {
            square = sleepingEventData.openDoor.getSquare();
        } else {
            sleepingEventData.weakestWindow = this.getWeakestWindow(player);
            if (sleepingEventData.weakestWindow != null && sleepingEventData.weakestWindow.getZ() == 0.0F) {
                if (!sleepingEventData.weakestWindow.north) {
                    if (sleepingEventData.weakestWindow.getSquare().getRoom() == null) {
                        square = sleepingEventData.weakestWindow.getSquare();
                    } else {
                        square = sleepingEventData.weakestWindow
                            .getSquare()
                            .getCell()
                            .getGridSquare(
                                sleepingEventData.weakestWindow.getSquare().getX() - 1,
                                sleepingEventData.weakestWindow.getSquare().getY(),
                                sleepingEventData.weakestWindow.getSquare().getZ()
                            );
                    }
                } else if (sleepingEventData.weakestWindow.getSquare().getRoom() == null) {
                    square = sleepingEventData.weakestWindow.getSquare();
                } else {
                    square = sleepingEventData.weakestWindow
                        .getSquare()
                        .getCell()
                        .getGridSquare(
                            sleepingEventData.weakestWindow.getSquare().getX(),
                            sleepingEventData.weakestWindow.getSquare().getY() + 1,
                            sleepingEventData.weakestWindow.getSquare().getZ()
                        );
                }

                IsoBarricade barricade = sleepingEventData.weakestWindow.getBarricadeOnOppositeSquare();
                if (barricade == null) {
                    barricade = sleepingEventData.weakestWindow.getBarricadeOnSameSquare();
                }

                if (barricade != null) {
                    barricade.Damage(Rand.Next(500, 900));
                } else {
                    sleepingEventData.weakestWindow.Damage(200.0F);
                    sleepingEventData.weakestWindow.smashWindow();
                    if (sleepingEventData.weakestWindow.HasCurtains() != null) {
                        sleepingEventData.weakestWindow.removeSheet(null);
                    }

                    if (square != null) {
                        square.addBrokenGlass();
                    }
                }
            }
        }

        player.getStats().setPanic(player.getStats().getPanic() + Rand.Next(30, 60));
        if (square != null) {
            if (IsoWorld.getZombiesEnabled()) {
                int int0 = Rand.Next(3) + 1;

                for (int int1 = 0; int1 < int0; int1++) {
                    VirtualZombieManager.instance.choices.clear();
                    VirtualZombieManager.instance.choices.add(square);
                    IsoZombie zombie0 = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(8)).index(), false);
                    if (zombie0 != null) {
                        zombie0.setTarget(player);
                        zombie0.pathToCharacter(player);
                        zombie0.spotted(player, true);
                        ZombieSpawnRecorder.instance.record(zombie0, this.getClass().getSimpleName());
                    }
                }
            }
        }
    }

    private IsoWindow getWeakestWindow(IsoPlayer player) {
        IsoGridSquare square = null;
        Object object = null;
        IsoWindow window = null;
        int int0 = 0;

        for (int int1 = player.getCurrentBuilding().getDef().getX() - 2; int1 < player.getCurrentBuilding().getDef().getX2() + 2; int1++) {
            for (int int2 = player.getCurrentBuilding().getDef().getY() - 2; int2 < player.getCurrentBuilding().getDef().getY2() + 2; int2++) {
                square = IsoWorld.instance.getCell().getGridSquare(int1, int2, 0);
                if (square != null) {
                    object = square.getWindow();
                    if (object != null) {
                        int int3 = this.checkWindowStatus((IsoWindow)object);
                        if (int3 > int0) {
                            int0 = int3;
                            window = (IsoWindow)object;
                        }
                    }
                }
            }
        }

        return window;
    }

    public void wakeUp(IsoGameCharacter chr) {
        if (chr != null) {
            this.wakeUp(chr, false);
        }
    }

    public void wakeUp(IsoGameCharacter chr, boolean remote) {
        SleepingEventData sleepingEventData = chr.getOrCreateSleepingEventData();
        if (GameClient.bClient && !remote) {
            GameClient.instance.wakeUpPlayer((IsoPlayer)chr);
        }

        boolean boolean0 = false;
        IsoPlayer player = Type.tryCastTo(chr, IsoPlayer.class);
        if (player != null && player.isLocalPlayer()) {
            UIManager.setFadeBeforeUI(player.getPlayerNum(), true);
            UIManager.FadeIn(player.getPlayerNum(), 2.0);
            if (!GameClient.bClient && IsoPlayer.allPlayersAsleep()) {
                UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                boolean0 = true;
            }

            chr.setLastHourSleeped((int)player.getHoursSurvived());
        }

        chr.setForceWakeUpTime(-1.0F);
        chr.setAsleep(false);
        if (boolean0) {
            try {
                GameWindow.save(true);
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }
        }

        BodyPart bodyPart = chr.getBodyDamage().getBodyPart(BodyPartType.Neck);
        float float0 = sleepingEventData.sleepingTime / 8.0F;
        if ("goodBed".equals(chr.getBedType())) {
            chr.getStats().setFatigue(chr.getStats().getFatigue() - Rand.Next(0.05F, 0.12F) * float0);
            if (chr.getStats().getFatigue() < 0.0F) {
                chr.getStats().setFatigue(0.0F);
            }
        } else if ("badBed".equals(chr.getBedType())) {
            chr.getStats().setFatigue(chr.getStats().getFatigue() + Rand.Next(0.1F, 0.2F) * float0);
            if (Rand.Next(5) == 0) {
                bodyPart.AddDamage(Rand.Next(5.0F, 15.0F));
                bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(30.0F, 50.0F));
            }
        } else if ("floor".equals(chr.getBedType())) {
            chr.getStats().setFatigue(chr.getStats().getFatigue() + Rand.Next(0.15F, 0.25F) * float0);
            if (Rand.Next(5) == 0) {
                bodyPart.AddDamage(Rand.Next(10.0F, 20.0F));
                bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(30.0F, 50.0F));
            }
        } else if (Rand.Next(10) == 0) {
            bodyPart.AddDamage(Rand.Next(3.0F, 12.0F));
            bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(10.0F, 30.0F));
        }

        sleepingEventData.reset();
    }
}
