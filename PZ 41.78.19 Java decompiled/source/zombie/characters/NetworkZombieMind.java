// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.packets.ZombiePacket;
import zombie.vehicles.PathFindBehavior2;

public class NetworkZombieMind {
    private final IsoZombie zombie;
    private byte pfbType = 0;
    private float pfbTargetX;
    private float pfbTargetY;
    private float pfbTargetZ;
    private boolean pfbIsCanceled = false;
    private boolean shouldRestorePFBTarget = false;
    private IsoPlayer pfbTargetCharacter = null;

    public NetworkZombieMind(IsoZombie _zombie) {
        this.zombie = _zombie;
    }

    public void set(ZombiePacket packet) {
        PathFindBehavior2 pathFindBehavior2 = this.zombie.getPathFindBehavior2();
        if (pathFindBehavior2.getIsCancelled()
            || pathFindBehavior2.isGoalNone()
            || pathFindBehavior2.bStopping
            || this.zombie.realState == null
            || NetworkVariables.ZombieState.Idle.equals(this.zombie.realState)) {
            packet.pfbType = 0;
        } else if (pathFindBehavior2.isGoalCharacter()) {
            IsoGameCharacter character = pathFindBehavior2.getTargetChar();
            if (character instanceof IsoPlayer) {
                packet.pfbType = 1;
                packet.pfbTarget = character.getOnlineID();
            } else {
                packet.pfbType = 0;
                DebugLog.Multiplayer.error("NetworkZombieMind: goal character is not set");
            }
        } else if (pathFindBehavior2.isGoalLocation()) {
            packet.pfbType = 2;
            packet.pfbTargetX = pathFindBehavior2.getTargetX();
            packet.pfbTargetY = pathFindBehavior2.getTargetY();
            packet.pfbTargetZ = (byte)pathFindBehavior2.getTargetZ();
        } else if (pathFindBehavior2.isGoalSound()) {
            packet.pfbType = 3;
            packet.pfbTargetX = pathFindBehavior2.getTargetX();
            packet.pfbTargetY = pathFindBehavior2.getTargetY();
            packet.pfbTargetZ = (byte)pathFindBehavior2.getTargetZ();
        }
    }

    public void parse(ZombiePacket packet) {
        this.pfbIsCanceled = packet.pfbType == 0;
        if (!this.pfbIsCanceled) {
            this.pfbType = packet.pfbType;
            if (this.pfbType == 1) {
                if (GameServer.bServer) {
                    this.pfbTargetCharacter = GameServer.IDToPlayerMap.get(packet.pfbTarget);
                } else if (GameClient.bClient) {
                    this.pfbTargetCharacter = GameClient.IDToPlayerMap.get(packet.pfbTarget);
                }
            } else if (this.pfbType > 1) {
                this.pfbTargetX = packet.pfbTargetX;
                this.pfbTargetY = packet.pfbTargetY;
                this.pfbTargetZ = packet.pfbTargetZ;
            }
        }
    }

    public void restorePFBTarget() {
        this.shouldRestorePFBTarget = true;
    }

    public void zombieIdleUpdate() {
        if (this.shouldRestorePFBTarget) {
            this.doRestorePFBTarget();
            this.shouldRestorePFBTarget = false;
        }
    }

    public void doRestorePFBTarget() {
        if (!this.pfbIsCanceled) {
            if (this.pfbType == 1 && this.pfbTargetCharacter != null) {
                this.zombie.pathToCharacter(this.pfbTargetCharacter);
                this.zombie.spotted(this.pfbTargetCharacter, true);
            } else if (this.pfbType == 2) {
                this.zombie.pathToLocationF(this.pfbTargetX, this.pfbTargetY, this.pfbTargetZ);
            } else if (this.pfbType == 3) {
                this.zombie.pathToSound((int)this.pfbTargetX, (int)this.pfbTargetY, (int)this.pfbTargetZ);
                this.zombie.alerted = false;
                this.zombie.setLastHeardSound((int)this.pfbTargetX, (int)this.pfbTargetY, (int)this.pfbTargetZ);
                this.zombie.AllowRepathDelay = 120.0F;
                this.zombie.timeSinceRespondToSound = 0.0F;
            }
        }
    }
}
