// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

public class NetworkVariables {
    public static enum PredictionTypes {
        None,
        Moving,
        Static,
        Thump,
        Climb,
        Lunge,
        LungeHalf,
        Walk,
        WalkHalf,
        PathFind;

        public static NetworkVariables.PredictionTypes fromByte(byte moveType) {
            for (NetworkVariables.PredictionTypes predictionTypes : values()) {
                if (predictionTypes.ordinal() == moveType) {
                    return predictionTypes;
                }
            }

            return None;
        }
    }

    public static enum ThumpType {
        TTNone(""),
        TTDoor("Door"),
        TTClaw("DoorClaw"),
        TTBang("DoorBang");

        private final String thumpType;

        private ThumpType(String string1) {
            this.thumpType = string1;
        }

        @Override
        public String toString() {
            return this.thumpType;
        }

        public static NetworkVariables.ThumpType fromString(String string) {
            for (NetworkVariables.ThumpType thumpTypex : values()) {
                if (thumpTypex.thumpType.equalsIgnoreCase(string)) {
                    return thumpTypex;
                }
            }

            return TTNone;
        }

        public static NetworkVariables.ThumpType fromByte(Byte byte0) {
            for (NetworkVariables.ThumpType thumpTypex : values()) {
                if (thumpTypex.ordinal() == byte0) {
                    return thumpTypex;
                }
            }

            return TTNone;
        }
    }

    public static enum WalkType {
        WT1("1"),
        WT2("2"),
        WT3("3"),
        WT4("4"),
        WT5("5"),
        WTSprint1("sprint1"),
        WTSprint2("sprint2"),
        WTSprint3("sprint3"),
        WTSprint4("sprint4"),
        WTSprint5("sprint5"),
        WTSlow1("slow1"),
        WTSlow2("slow2"),
        WTSlow3("slow3");

        private final String walkType;

        private WalkType(String string1) {
            this.walkType = string1;
        }

        @Override
        public String toString() {
            return this.walkType;
        }

        public static NetworkVariables.WalkType fromString(String _walkType) {
            for (NetworkVariables.WalkType walkTypex : values()) {
                if (walkTypex.walkType.equalsIgnoreCase(_walkType)) {
                    return walkTypex;
                }
            }

            return WT1;
        }

        public static NetworkVariables.WalkType fromByte(byte _walkType) {
            for (NetworkVariables.WalkType walkTypex : values()) {
                if (walkTypex.ordinal() == _walkType) {
                    return walkTypex;
                }
            }

            return WT1;
        }
    }

    public static enum ZombieState {
        Attack("attack"),
        AttackNetwork("attack-network"),
        AttackVehicle("attackvehicle"),
        AttackVehicleNetwork("attackvehicle-network"),
        Bumped("bumped"),
        ClimbFence("climbfence"),
        ClimbWindow("climbwindow"),
        EatBody("eatbody"),
        FaceTarget("face-target"),
        FakeDead("fakedead"),
        FakeDeadAttack("fakedead-attack"),
        FakeDeadAttackNetwork("fakedead-attack-network"),
        FallDown("falldown"),
        Falling("falling"),
        GetDown("getdown"),
        Getup("getup"),
        HitReaction("hitreaction"),
        HitReactionHit("hitreaction-hit"),
        HitWhileStaggered("hitwhilestaggered"),
        Idle("idle"),
        Lunge("lunge"),
        LungeNetwork("lunge-network"),
        OnGround("onground"),
        PathFind("pathfind"),
        Sitting("sitting"),
        StaggerBack("staggerback"),
        Thump("thump"),
        TurnAlerted("turnalerted"),
        WalkToward("walktoward"),
        WalkTowardNetwork("walktoward-network"),
        FakeZombieStay("fakezombie-stay"),
        FakeZombieNormal("fakezombie-normal"),
        FakeZombieAttack("fakezombie-attack");

        private final String zombieState;

        private ZombieState(String string1) {
            this.zombieState = string1;
        }

        @Override
        public String toString() {
            return this.zombieState;
        }

        public static NetworkVariables.ZombieState fromString(String _zombieState) {
            for (NetworkVariables.ZombieState zombieStatex : values()) {
                if (zombieStatex.zombieState.equalsIgnoreCase(_zombieState)) {
                    return zombieStatex;
                }
            }

            return Idle;
        }

        public static NetworkVariables.ZombieState fromByte(Byte _zombieState) {
            for (NetworkVariables.ZombieState zombieStatex : values()) {
                if (zombieStatex.ordinal() == _zombieState) {
                    return zombieStatex;
                }
            }

            return Idle;
        }
    }
}
