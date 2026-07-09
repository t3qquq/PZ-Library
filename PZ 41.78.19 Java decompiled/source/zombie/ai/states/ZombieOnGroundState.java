// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.model.Model;
import zombie.iso.Vector3;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;

public final class ZombieOnGroundState extends State {
    private static final ZombieOnGroundState _instance = new ZombieOnGroundState();
    static Vector3 tempVector = new Vector3();
    static Vector3 tempVectorBonePos = new Vector3();

    public static ZombieOnGroundState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        owner.setCollidable(false);
        if (!owner.isDead()) {
            owner.setOnFloor(true);
        }

        if (owner.isDead() || zombie0.isFakeDead()) {
            owner.die();
        } else if (!zombie0.isBecomeCrawler()) {
            if (!"Tutorial".equals(Core.GameMode)) {
                owner.setReanimateTimer(Rand.Next(60) + 30);
            }

            if (GameClient.bClient && zombie0.isReanimatedPlayer()) {
                IsoDeadBody.removeDeadBody(zombie0.networkAI.reanimatedBodyID);
            }

            zombie0.parameterZombieState.setState(ParameterZombieState.State.Idle);
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        if (owner.isDead() || zombie0.isFakeDead()) {
            owner.die();
        } else if (zombie0.isBecomeCrawler()) {
            if (!zombie0.isBeingSteppedOn() && !zombie0.isUnderVehicle()) {
                zombie0.setCrawler(true);
                zombie0.setCanWalk(false);
                zombie0.setReanimate(true);
                zombie0.setBecomeCrawler(false);
            }
        } else {
            if (owner.hasAnimationPlayer()) {
                owner.getAnimationPlayer().setTargetToAngle();
            }

            owner.setReanimateTimer(owner.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
            if (owner.getReanimateTimer() <= 2.0F) {
                if (GameClient.bClient) {
                    if (owner.isBeingSteppedOn() && !zombie0.isReanimatedPlayer()) {
                        owner.setReanimateTimer(Rand.Next(60) + 30);
                    }
                } else if (owner.isBeingSteppedOn() && zombie0.getReanimatedPlayer() == null) {
                    owner.setReanimateTimer(Rand.Next(60) + 30);
                }
            }
        }
    }

    public static boolean isCharacterStandingOnOther(IsoGameCharacter chrStanding, IsoGameCharacter chrProne) {
        AnimationPlayer animationPlayer = chrProne.getAnimationPlayer();
        int int0 = DoCollisionBoneCheck(chrStanding, chrProne, animationPlayer.getSkinningBoneIndex("Bip01_Spine", -1), 0.32F);
        if (int0 == -1) {
            int0 = DoCollisionBoneCheck(chrStanding, chrProne, animationPlayer.getSkinningBoneIndex("Bip01_L_Calf", -1), 0.18F);
        }

        if (int0 == -1) {
            int0 = DoCollisionBoneCheck(chrStanding, chrProne, animationPlayer.getSkinningBoneIndex("Bip01_R_Calf", -1), 0.18F);
        }

        if (int0 == -1) {
            int0 = DoCollisionBoneCheck(chrStanding, chrProne, animationPlayer.getSkinningBoneIndex("Bip01_Head", -1), 0.28F);
        }

        return int0 > -1;
    }

    private static int DoCollisionBoneCheck(IsoGameCharacter character1, IsoGameCharacter character0, int int0, float float2) {
        float float0 = 0.3F;
        Model.BoneToWorldCoords(character0, int0, tempVectorBonePos);

        for (int int1 = 1; int1 <= 10; int1++) {
            float float1 = int1 / 10.0F;
            tempVector.x = character1.x;
            tempVector.y = character1.y;
            tempVector.z = character1.z;
            tempVector.x = tempVector.x + character1.getForwardDirection().x * float0 * float1;
            tempVector.y = tempVector.y + character1.getForwardDirection().y * float0 * float1;
            tempVector.x = tempVectorBonePos.x - tempVector.x;
            tempVector.y = tempVectorBonePos.y - tempVector.y;
            tempVector.z = 0.0F;
            boolean boolean0 = tempVector.getLength() < float2;
            if (boolean0) {
                return int0;
            }
        }

        return -1;
    }

    @Override
    public void exit(IsoGameCharacter owner) {
    }
}
