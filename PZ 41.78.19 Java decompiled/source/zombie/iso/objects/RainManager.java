// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import fmod.fmod.Audio;
import java.util.ArrayList;
import java.util.Stack;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;

public class RainManager {
    public static boolean IsRaining = false;
    public static int NumActiveRainSplashes = 0;
    public static int NumActiveRaindrops = 0;
    public static int MaxRainSplashObjects = 500;
    public static int MaxRaindropObjects = 500;
    public static float RainSplashAnimDelay = 0.2F;
    public static int AddNewSplashesDelay = 30;
    public static int AddNewSplashesTimer = AddNewSplashesDelay;
    public static float RaindropGravity = 0.065F;
    public static float GravModMin = 0.28F;
    public static float GravModMax = 0.5F;
    public static float RaindropStartDistance = 850.0F;
    public static IsoGridSquare[] PlayerLocation = new IsoGridSquare[4];
    public static IsoGridSquare[] PlayerOldLocation = new IsoGridSquare[4];
    public static boolean PlayerMoved = true;
    public static int RainRadius = 18;
    public static Audio RainAmbient;
    public static Audio ThunderAmbient = null;
    public static ColorInfo RainSplashTintMod = new ColorInfo(0.8F, 0.9F, 1.0F, 0.3F);
    public static ColorInfo RaindropTintMod = new ColorInfo(0.8F, 0.9F, 1.0F, 0.3F);
    public static ColorInfo DarkRaindropTintMod = new ColorInfo(0.8F, 0.9F, 1.0F, 0.3F);
    public static ArrayList<IsoRainSplash> RainSplashStack = new ArrayList<>(1600);
    public static ArrayList<IsoRaindrop> RaindropStack = new ArrayList<>(1600);
    public static Stack<IsoRainSplash> RainSplashReuseStack = new Stack<>();
    public static Stack<IsoRaindrop> RaindropReuseStack = new Stack<>();
    private static float RainChangeTimer = 1.0F;
    private static float RainChangeRate = 0.01F;
    private static float RainChangeRateMin = 0.006F;
    private static float RainChangeRateMax = 0.01F;
    public static float RainIntensity = 1.0F;
    public static float RainDesiredIntensity = 1.0F;
    private static int randRain = 0;
    public static int randRainMin = 0;
    public static int randRainMax = 0;
    private static boolean stopRain = false;
    static Audio OutsideAmbient = null;
    static Audio OutsideNightAmbient = null;
    static ColorInfo AdjustedRainSplashTintMod = new ColorInfo();

    public static void reset() {
        RainSplashStack.clear();
        RaindropStack.clear();
        RaindropReuseStack.clear();
        RainSplashReuseStack.clear();
        NumActiveRainSplashes = 0;
        NumActiveRaindrops = 0;

        for (int int0 = 0; int0 < 4; int0++) {
            PlayerLocation[int0] = null;
            PlayerOldLocation[int0] = null;
        }

        RainAmbient = null;
        ThunderAmbient = null;
        IsRaining = false;
        stopRain = false;
    }

    public static void AddRaindrop(IsoRaindrop NewRaindrop) {
        if (NumActiveRaindrops < MaxRaindropObjects) {
            RaindropStack.add(NewRaindrop);
            NumActiveRaindrops++;
        } else {
            IsoRaindrop raindrop = null;
            int int0 = -1;

            for (int int1 = 0; int1 < RaindropStack.size(); int1++) {
                if (RaindropStack.get(int1).Life > int0) {
                    int0 = RaindropStack.get(int1).Life;
                    raindrop = RaindropStack.get(int1);
                }
            }

            if (raindrop != null) {
                RemoveRaindrop(raindrop);
                RaindropStack.add(NewRaindrop);
                NumActiveRaindrops++;
            }
        }
    }

    public static void AddRainSplash(IsoRainSplash NewRainSplash) {
        if (NumActiveRainSplashes < MaxRainSplashObjects) {
            RainSplashStack.add(NewRainSplash);
            NumActiveRainSplashes++;
        } else {
            IsoRainSplash rainSplash = null;
            int int0 = -1;

            for (int int1 = 0; int1 < RainSplashStack.size(); int1++) {
                if (RainSplashStack.get(int1).Age > int0) {
                    int0 = RainSplashStack.get(int1).Age;
                    rainSplash = RainSplashStack.get(int1);
                }
            }

            RemoveRainSplash(rainSplash);
            RainSplashStack.add(NewRainSplash);
            NumActiveRainSplashes++;
        }
    }

    public static void AddSplashes() {
        if (AddNewSplashesTimer > 0) {
            AddNewSplashesTimer--;
        } else {
            AddNewSplashesTimer = (int)(AddNewSplashesDelay * (PerformanceSettings.getLockFPS() / 30.0F));
            Object object = null;
            if (!stopRain) {
                if (PlayerMoved) {
                    for (int int0 = RainSplashStack.size() - 1; int0 >= 0; int0--) {
                        IsoRainSplash rainSplash0 = RainSplashStack.get(int0);
                        if (!inBounds(rainSplash0.square)) {
                            RemoveRainSplash(rainSplash0);
                        }
                    }

                    for (int int1 = RaindropStack.size() - 1; int1 >= 0; int1--) {
                        IsoRaindrop raindrop0 = RaindropStack.get(int1);
                        if (!inBounds(raindrop0.square)) {
                            RemoveRaindrop(raindrop0);
                        }
                    }
                }

                int int2 = 0;

                for (int int3 = 0; int3 < IsoPlayer.numPlayers; int3++) {
                    if (IsoPlayer.players[int3] != null) {
                        int2++;
                    }
                }

                int int4 = RainRadius * 2 * RainRadius * 2;
                int int5 = int4 / (randRain + 1);
                int5 = Math.min(MaxRainSplashObjects, int5);

                while (NumActiveRainSplashes > int5 * int2) {
                    RemoveRainSplash(RainSplashStack.get(0));
                }

                while (NumActiveRaindrops > int5 * int2) {
                    RemoveRaindrop(RaindropStack.get(0));
                }

                IsoCell cell = IsoWorld.instance.CurrentCell;

                for (int int6 = 0; int6 < IsoPlayer.numPlayers; int6++) {
                    if (IsoPlayer.players[int6] != null && PlayerLocation[int6] != null) {
                        for (int int7 = 0; int7 < int5; int7++) {
                            int int8 = Rand.Next(-RainRadius, RainRadius);
                            int int9 = Rand.Next(-RainRadius, RainRadius);
                            object = cell.getGridSquare(PlayerLocation[int6].getX() + int8, PlayerLocation[int6].getY() + int9, 0);
                            if (object != null
                                && ((IsoGridSquare)object).isSeen(int6)
                                && !((IsoGridSquare)object).getProperties().Is(IsoFlagType.vegitation)
                                && ((IsoGridSquare)object).getProperties().Is(IsoFlagType.exterior)) {
                                StartRainSplash(cell, (IsoGridSquare)object, true);
                            }
                        }
                    }
                }
            }

            PlayerMoved = false;
            if (!stopRain) {
                randRain--;
                if (randRain < randRainMin) {
                    randRain = randRainMin;
                }
            } else {
                randRain = (int)(randRain - 1.0F * GameTime.instance.getMultiplier());
                if (randRain < randRainMin) {
                    removeAll();
                    randRain = randRainMin;
                } else {
                    for (int int10 = RainSplashStack.size() - 1; int10 >= 0; int10--) {
                        if (Rand.Next(randRain) == 0) {
                            IsoRainSplash rainSplash1 = RainSplashStack.get(int10);
                            RemoveRainSplash(rainSplash1);
                        }
                    }

                    for (int int11 = RaindropStack.size() - 1; int11 >= 0; int11--) {
                        if (Rand.Next(randRain) == 0) {
                            IsoRaindrop raindrop1 = RaindropStack.get(int11);
                            RemoveRaindrop(raindrop1);
                        }
                    }
                }
            }
        }
    }

    public static void RemoveRaindrop(IsoRaindrop DyingRaindrop) {
        if (DyingRaindrop.square != null) {
            DyingRaindrop.square.getProperties().UnSet(IsoFlagType.HasRaindrop);
            DyingRaindrop.square.setRainDrop(null);
            DyingRaindrop.square = null;
        }

        RaindropStack.remove(DyingRaindrop);
        NumActiveRaindrops--;
        RaindropReuseStack.push(DyingRaindrop);
    }

    public static void RemoveRainSplash(IsoRainSplash DyingRainSplash) {
        if (DyingRainSplash.square != null) {
            DyingRainSplash.square.getProperties().UnSet(IsoFlagType.HasRainSplashes);
            DyingRainSplash.square.setRainSplash(null);
            DyingRainSplash.square = null;
        }

        RainSplashStack.remove(DyingRainSplash);
        NumActiveRainSplashes--;
        RainSplashReuseStack.push(DyingRainSplash);
    }

    public static void SetPlayerLocation(int playerIndex, IsoGridSquare PlayerCurrentSquare) {
        PlayerOldLocation[playerIndex] = PlayerLocation[playerIndex];
        PlayerLocation[playerIndex] = PlayerCurrentSquare;
        if (PlayerOldLocation[playerIndex] != PlayerLocation[playerIndex]) {
            PlayerMoved = true;
        }
    }

    public static Boolean isRaining() {
        return ClimateManager.getInstance().isRaining();
    }

    public static void stopRaining() {
        stopRain = true;
        randRain = randRainMax;
        RainDesiredIntensity = 0.0F;
        if (GameServer.bServer) {
            GameServer.stopRain();
        }

        LuaEventManager.triggerEvent("OnRainStop");
    }

    public static void startRaining() {
    }

    public static void StartRaindrop(IsoCell cell, IsoGridSquare gridSquare, boolean CanSee) {
        if (!gridSquare.getProperties().Is(IsoFlagType.HasRaindrop)) {
            IsoRaindrop raindrop = null;
            if (!RaindropReuseStack.isEmpty()) {
                if (CanSee) {
                    if (gridSquare.getRainDrop() != null) {
                        return;
                    }

                    raindrop = RaindropReuseStack.pop();
                    raindrop.Reset(gridSquare, CanSee);
                    gridSquare.setRainDrop(raindrop);
                }
            } else if (CanSee) {
                if (gridSquare.getRainDrop() != null) {
                    return;
                }

                raindrop = new IsoRaindrop(cell, gridSquare, CanSee);
                gridSquare.setRainDrop(raindrop);
            }
        }
    }

    public static void StartRainSplash(IsoCell cell, IsoGridSquare gridSquare, boolean CanSee) {
    }

    public static void Update() {
        IsRaining = ClimateManager.getInstance().isRaining();
        RainIntensity = IsRaining ? ClimateManager.getInstance().getPrecipitationIntensity() : 0.0F;
        if (IsoPlayer.getInstance() != null) {
            if (IsoPlayer.getInstance().getCurrentSquare() != null) {
                if (!GameServer.bServer) {
                    AddSplashes();
                }
            }
        }
    }

    public static void UpdateServer() {
    }

    public static void setRandRainMax(int pRandRainMax) {
        randRainMax = pRandRainMax;
        randRain = randRainMax;
    }

    public static void setRandRainMin(int pRandRainMin) {
        randRainMin = pRandRainMin;
    }

    public static boolean inBounds(IsoGridSquare sq) {
        if (sq == null) {
            return false;
        } else {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && PlayerLocation[int0] != null) {
                    if (sq.getX() < PlayerLocation[int0].getX() - RainRadius || sq.getX() >= PlayerLocation[int0].getX() + RainRadius) {
                        return true;
                    }

                    if (sq.getY() < PlayerLocation[int0].getY() - RainRadius || sq.getY() >= PlayerLocation[int0].getY() + RainRadius) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static void RemoveAllOn(IsoGridSquare sq) {
        if (sq.getRainDrop() != null) {
            RemoveRaindrop(sq.getRainDrop());
        }

        if (sq.getRainSplash() != null) {
            RemoveRainSplash(sq.getRainSplash());
        }
    }

    public static float getRainIntensity() {
        return ClimateManager.getInstance().getPrecipitationIntensity();
    }

    private static void removeAll() {
        for (int int0 = RainSplashStack.size() - 1; int0 >= 0; int0--) {
            IsoRainSplash rainSplash = RainSplashStack.get(int0);
            RemoveRainSplash(rainSplash);
        }

        for (int int1 = RaindropStack.size() - 1; int1 >= 0; int1--) {
            IsoRaindrop raindrop = RaindropStack.get(int1);
            RemoveRaindrop(raindrop);
        }

        RaindropStack.clear();
        RainSplashStack.clear();
        NumActiveRainSplashes = 0;
        NumActiveRaindrops = 0;
    }

    private static boolean interruptSleep(IsoPlayer player) {
        if (player.isAsleep() && player.isOutside() && player.getBed() != null && !player.getBed().getName().equals("Tent")) {
            IsoObject object = player.getBed();
            if (object.getCell().getGridSquare((double)object.getX(), (double)object.getY(), (double)(object.getZ() + 1.0F)) == null
                || object.getCell().getGridSquare((double)object.getX(), (double)object.getY(), (double)(object.getZ() + 1.0F)).getFloor() == null) {
                return true;
            }
        }

        return false;
    }
}
