// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoMovingObject;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class ZombieEatBodyState extends State {
    private static final ZombieEatBodyState _instance = new ZombieEatBodyState();

    public static ZombieEatBodyState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        IsoZombie zombie0 = (IsoZombie)character;
        zombie0.setStateEventDelayTimer(Rand.Next(1800.0F, 3600.0F));
        zombie0.setVariable("onknees", Rand.Next(3) != 0);
        if (zombie0.getEatBodyTarget() instanceof IsoDeadBody) {
            IsoDeadBody deadBody = (IsoDeadBody)zombie0.eatBodyTarget;
            if (!zombie0.isEatingOther(deadBody)) {
                HashMap hashMap = character.getStateMachineParams(this);
                hashMap.put(0, deadBody);
                deadBody.getEatingZombies().add(zombie0);
            }

            if (GameClient.bClient && zombie0.isLocal()) {
                GameClient.sendEatBody(zombie0, zombie0.getEatBodyTarget());
            }
        } else if (zombie0.getEatBodyTarget() instanceof IsoPlayer && GameClient.bClient && zombie0.isLocal()) {
            GameClient.sendEatBody(zombie0, zombie0.getEatBodyTarget());
        }
    }

    @Override
    public void execute(IsoGameCharacter character) {
        IsoZombie zombie0 = (IsoZombie)character;
        IsoMovingObject movingObject = zombie0.getEatBodyTarget();
        if (zombie0.getStateEventDelayTimer() <= 0.0F) {
            zombie0.setEatBodyTarget(null, false);
        } else if (!GameServer.bServer && !Core.SoundDisabled && Rand.Next(Rand.AdjustForFramerate(15)) == 0) {
            zombie0.parameterZombieState.setState(ParameterZombieState.State.Eating);
        }

        zombie0.TimeSinceSeenFlesh = 0.0F;
        if (movingObject != null) {
            zombie0.faceThisObject(movingObject);
        }

        if (Rand.Next(Rand.AdjustForFramerate(450)) == 0) {
            zombie0.getCurrentSquare()
                .getChunk()
                .addBloodSplat(zombie0.x + Rand.Next(-0.5F, 0.5F), zombie0.y + Rand.Next(-0.5F, 0.5F), zombie0.z, Rand.Next(8));
            if (Rand.Next(6) == 0) {
                new IsoZombieGiblets(
                    IsoZombieGiblets.GibletType.B,
                    zombie0.getCell(),
                    zombie0.getX(),
                    zombie0.getY(),
                    zombie0.getZ() + 0.3F,
                    Rand.Next(-0.2F, 0.2F) * 1.5F,
                    Rand.Next(-0.2F, 0.2F) * 1.5F
                );
            } else {
                new IsoZombieGiblets(
                    IsoZombieGiblets.GibletType.A,
                    zombie0.getCell(),
                    zombie0.getX(),
                    zombie0.getY(),
                    zombie0.getZ() + 0.3F,
                    Rand.Next(-0.2F, 0.2F) * 1.5F,
                    Rand.Next(-0.2F, 0.2F) * 1.5F
                );
            }

            if (Rand.Next(4) == 0) {
                zombie0.addBlood(null, true, false, false);
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter character) {
        IsoZombie zombie0 = (IsoZombie)character;
        HashMap hashMap = character.getStateMachineParams(this);
        if (hashMap.get(0) instanceof IsoDeadBody) {
            ((IsoDeadBody)hashMap.get(0)).getEatingZombies().remove(zombie0);
        }

        if (zombie0.parameterZombieState.isState(ParameterZombieState.State.Eating)) {
            zombie0.parameterZombieState.setState(ParameterZombieState.State.Idle);
        }

        if (GameClient.bClient && zombie0.isLocal()) {
            GameClient.sendEatBody(zombie0, null);
        }
    }
}
