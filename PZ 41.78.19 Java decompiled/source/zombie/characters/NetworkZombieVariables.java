// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.ai.states.ZombieTurnAlerted;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class NetworkZombieVariables {
    public static int getInt(IsoZombie zombie0, short short0) {
        switch (short0) {
            case 0:
                return (int)(zombie0.Health * 1000.0F);
            case 1:
                if (zombie0.target == null) {
                    return -1;
                }

                return ((IAnimatable)zombie0.target).getOnlineID();
            case 2:
                return (int)(zombie0.speedMod * 1000.0F);
            case 3:
                return (int)zombie0.TimeSinceSeenFlesh;
            case 4:
                Float float0 = (Float)zombie0.getStateMachineParams(ZombieTurnAlerted.instance()).get(ZombieTurnAlerted.PARAM_TARGET_ANGLE);
                if (float0 == null) {
                    return 0;
                }

                return float0.intValue();
            default:
                return 0;
        }
    }

    public static void setInt(IsoZombie zombie0, short short0, int int0) {
        switch (short0) {
            case 0:
                zombie0.Health = int0 / 1000.0F;
                break;
            case 1:
                if (int0 == -1) {
                    zombie0.setTargetSeenTime(0.0F);
                    zombie0.target = null;
                } else {
                    IsoPlayer player = GameClient.IDToPlayerMap.get((short)int0);
                    if (GameServer.bServer) {
                        player = GameServer.IDToPlayerMap.get((short)int0);
                    }

                    if (player != zombie0.target) {
                        zombie0.setTargetSeenTime(0.0F);
                        zombie0.target = player;
                    }
                }
                break;
            case 2:
                zombie0.speedMod = int0 / 1000.0F;
                break;
            case 3:
                zombie0.TimeSinceSeenFlesh = int0;
                break;
            case 4:
                zombie0.getStateMachineParams(ZombieTurnAlerted.instance()).put(ZombieTurnAlerted.PARAM_TARGET_ANGLE, (float)int0);
        }
    }

    public static short getBooleanVariables(IsoZombie zombie0) {
        short short0 = 0;
        short0 = (short)(short0 | (zombie0.isFakeDead() ? 1 : 0));
        short0 = (short)(short0 | (zombie0.bLunger ? 2 : 0));
        short0 = (short)(short0 | (zombie0.bRunning ? 4 : 0));
        short0 = (short)(short0 | (zombie0.isCrawling() ? 8 : 0));
        short0 = (short)(short0 | (zombie0.isSitAgainstWall() ? 16 : 0));
        short0 = (short)(short0 | (zombie0.isReanimatedPlayer() ? 32 : 0));
        short0 = (short)(short0 | (zombie0.isOnFire() ? 64 : 0));
        short0 = (short)(short0 | (zombie0.isUseless() ? 128 : 0));
        return (short)(short0 | (zombie0.isOnFloor() ? 256 : 0));
    }

    public static void setBooleanVariables(IsoZombie zombie0, short short0) {
        zombie0.setFakeDead((short0 & 1) != 0);
        zombie0.bLunger = (short0 & 2) != 0;
        zombie0.bRunning = (short0 & 4) != 0;
        zombie0.setCrawler((short0 & 8) != 0);
        zombie0.setSitAgainstWall((short0 & 16) != 0);
        zombie0.setReanimatedPlayer((short0 & 32) != 0);
        if ((short0 & 64) != 0) {
            zombie0.SetOnFire();
        } else {
            zombie0.StopBurning();
        }

        zombie0.setUseless((short0 & 128) != 0);
        if (zombie0.isReanimatedPlayer()) {
            zombie0.setOnFloor((short0 & 256) != 0);
        }
    }

    public static class VariablesInt {
        public static final short health = 0;
        public static final short target = 1;
        public static final short speedMod = 2;
        public static final short timeSinceSeenFlesh = 3;
        public static final short smParamTargetAngle = 4;
        public static final short MAX = 5;
    }
}
