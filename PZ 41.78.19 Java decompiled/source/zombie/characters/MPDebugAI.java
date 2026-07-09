// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.ai.states.PathFindState;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.vehicles.PathFindBehavior2;

public class MPDebugAI {
    private static final Vector2 tempo = new Vector2();
    private static final Vector2 tempo2 = new Vector2();

    public static IsoPlayer getNearestPlayer(IsoPlayer player2) {
        IsoPlayer player0 = null;

        for (IsoPlayer player1 : GameClient.IDToPlayerMap.values()) {
            if (player1 != player2 && (player0 == null || player0.getDistanceSq(player2) > player1.getDistanceSq(player2))) {
                player0 = player1;
            }
        }

        return player0;
    }

    public static boolean updateMovementFromInput(IsoPlayer player0, IsoPlayer.MoveVars moveVars) {
        if (GameClient.bClient
            && player0.isLocalPlayer()
            && (DebugOptions.instance.MultiplayerAttackPlayer.getValue() || DebugOptions.instance.MultiplayerFollowPlayer.getValue())) {
            IsoPlayer player1 = getNearestPlayer(player0);
            if (player1 != null) {
                Vector2 vector0 = new Vector2(player1.x - player0.x, player0.y - player1.y);
                vector0.rotate((float) (-Math.PI / 4));
                vector0.normalize();
                moveVars.moveX = vector0.x;
                moveVars.moveY = vector0.y;
                moveVars.NewFacing = IsoDirections.fromAngle(vector0);
                if (player1.isTeleporting() || player1.getDistanceSq(player0) > 10.0F) {
                    player0.removeFromSquare();
                    player0.setX(player1.realx);
                    player0.setY(player1.realy);
                    player0.setZ(player1.realz);
                    player0.setLx(player1.realx);
                    player0.setLy(player1.realy);
                    player0.setLz(player1.realz);
                    player0.ensureOnTile();
                } else if (player1.getDistanceSq(player0) > 5.0F) {
                    player0.setRunning(true);
                    player0.setSprinting(true);
                } else if (player1.getDistanceSq(player0) > 2.5F) {
                    player0.setRunning(true);
                } else if (player1.getDistanceSq(player0) < 1.25F) {
                    moveVars.moveX = 0.0F;
                    moveVars.moveY = 0.0F;
                }
            }

            PathFindBehavior2 pathFindBehavior2 = player0.getPathFindBehavior2();
            if (moveVars.moveX == 0.0F
                && moveVars.moveY == 0.0F
                && player0.getPath2() != null
                && pathFindBehavior2.isStrafing()
                && !pathFindBehavior2.bStopping) {
                Vector2 vector1 = tempo.set(pathFindBehavior2.getTargetX() - player0.x, pathFindBehavior2.getTargetY() - player0.y);
                Vector2 vector2 = tempo2.set(-1.0F, 0.0F);
                float float0 = 1.0F;
                float float1 = vector1.dot(vector2);
                float float2 = float1 / float0;
                vector2 = tempo2.set(0.0F, -1.0F);
                float1 = vector1.dot(vector2);
                float float3 = float1 / float0;
                tempo.set(float3, float2);
                tempo.normalize();
                tempo.rotate((float) (Math.PI / 4));
                moveVars.moveX = tempo.x;
                moveVars.moveY = tempo.y;
            }

            if (moveVars.moveX != 0.0F || moveVars.moveY != 0.0F) {
                if (player0.stateMachine.getCurrent() == PathFindState.instance()) {
                    player0.setDefaultState();
                }

                player0.setJustMoved(true);
                player0.setMoveDelta(1.0F);
                if (player0.isStrafing()) {
                    tempo.set(moveVars.moveX, moveVars.moveY);
                    tempo.normalize();
                    float float4 = player0.legsSprite.modelSlot.model.AnimPlayer.getRenderedAngle();
                    float4 = (float)(float4 + (Math.PI / 4));
                    if (float4 > Math.PI * 2) {
                        float4 = (float)(float4 - (Math.PI * 2));
                    }

                    if (float4 < 0.0F) {
                        float4 = (float)(float4 + (Math.PI * 2));
                    }

                    tempo.rotate(float4);
                    moveVars.strafeX = tempo.x;
                    moveVars.strafeY = tempo.y;
                } else {
                    tempo.set(moveVars.moveX, -moveVars.moveY);
                    tempo.normalize();
                    tempo.rotate((float) (-Math.PI / 4));
                    player0.setForwardDirection(tempo);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean updateInputState(IsoPlayer player0, IsoPlayer.InputState inputState) {
        if (GameClient.bClient && player0.isLocalPlayer() && DebugOptions.instance.MultiplayerAttackPlayer.getValue()) {
            IsoPlayer player1 = getNearestPlayer(player0);
            inputState.bMelee = false;
            inputState.isAttacking = false;
            inputState.isCharging = false;
            inputState.isAiming = false;
            inputState.bRunning = false;
            inputState.bSprinting = false;
            if (player1 != null) {
                inputState.isCharging = true;
                inputState.isAiming = false;
                if (player1.getDistanceSq(player0) < 0.5F) {
                    inputState.bMelee = true;
                    inputState.isAttacking = true;
                }
            }

            return true;
        } else if (GameClient.bClient && player0.isLocalPlayer() && DebugOptions.instance.MultiplayerFollowPlayer.getValue()) {
            IsoPlayer player2 = getNearestPlayer(player0);
            inputState.bMelee = false;
            inputState.isAttacking = false;
            inputState.isCharging = false;
            inputState.isAiming = false;
            inputState.bRunning = false;
            inputState.bSprinting = false;
            return true;
        } else {
            return false;
        }
    }
}
