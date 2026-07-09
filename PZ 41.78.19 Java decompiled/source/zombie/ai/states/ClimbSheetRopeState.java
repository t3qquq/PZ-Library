// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;

public final class ClimbSheetRopeState extends State {
    public static final float CLIMB_SPEED = 0.16F;
    private static final float CLIMB_SLOWDOWN = 0.5F;
    private static final ClimbSheetRopeState _instance = new ClimbSheetRopeState();

    public static ClimbSheetRopeState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        owner.setbClimbing(true);
        owner.setVariable("ClimbRope", true);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        float float0 = 0.0F;
        float float1 = 0.0F;
        if (owner.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetN) || owner.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopN)) {
            owner.setDir(IsoDirections.N);
            float0 = 0.54F;
            float1 = 0.39F;
        }

        if (owner.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetS) || owner.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopS)) {
            owner.setDir(IsoDirections.S);
            float0 = 0.118F;
            float1 = 0.5756F;
        }

        if (owner.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetW) || owner.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopW)) {
            owner.setDir(IsoDirections.W);
            float0 = 0.4F;
            float1 = 0.7F;
        }

        if (owner.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetE) || owner.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopE)) {
            owner.setDir(IsoDirections.E);
            float0 = 0.5417F;
            float1 = 0.3144F;
        }

        float float2 = owner.x - (int)owner.x;
        float float3 = owner.y - (int)owner.y;
        if (float2 != float0) {
            float float4 = (float0 - float2) / 4.0F;
            float2 += float4;
            owner.x = (int)owner.x + float2;
        }

        if (float3 != float1) {
            float float5 = (float1 - float3) / 4.0F;
            float3 += float5;
            owner.y = (int)owner.y + float3;
        }

        owner.nx = owner.x;
        owner.ny = owner.y;
        float float6 = this.getClimbSheetRopeSpeed(owner);
        owner.getSpriteDef().AnimFrameIncrease = float6;
        float float7 = owner.z + float6 / 10.0F * GameTime.instance.getMultiplier();
        float7 = Math.min(float7, 7.0F);

        for (int int0 = (int)owner.z; int0 <= float7; int0++) {
            IsoCell cell = IsoWorld.instance.getCell();
            IsoGridSquare square0 = cell.getGridSquare((double)owner.getX(), (double)owner.getY(), (double)int0);
            if (IsoWindow.isTopOfSheetRopeHere(square0)) {
                owner.z = int0;
                owner.setCurrent(square0);
                owner.setCollidable(true);
                IsoGridSquare square1 = square0.nav[owner.dir.index()];
                if (square1 != null) {
                    if (!square1.TreatAsSolidFloor()) {
                        owner.climbDownSheetRope();
                        return;
                    }

                    IsoWindow window = square0.getWindowTo(square1);
                    if (window != null) {
                        if (!window.open) {
                            window.ToggleWindow(owner);
                        }

                        if (!window.canClimbThrough(owner)) {
                            owner.climbDownSheetRope();
                            return;
                        }

                        owner.climbThroughWindow(window, 4);
                        return;
                    }

                    IsoThumpable thumpable = square0.getWindowThumpableTo(square1);
                    if (thumpable != null) {
                        if (!thumpable.canClimbThrough(owner)) {
                            owner.climbDownSheetRope();
                            return;
                        }

                        owner.climbThroughWindow(thumpable, 4);
                        return;
                    }

                    thumpable = square0.getHoppableThumpableTo(square1);
                    if (thumpable != null) {
                        if (!IsoWindow.canClimbThroughHelper(owner, square0, square1, owner.dir == IsoDirections.N || owner.dir == IsoDirections.S)) {
                            owner.climbDownSheetRope();
                            return;
                        }

                        owner.climbOverFence(owner.dir);
                        return;
                    }

                    IsoObject object0 = square0.getWindowFrameTo(square1);
                    if (object0 != null) {
                        if (!IsoWindowFrame.canClimbThrough(object0, owner)) {
                            owner.climbDownSheetRope();
                            return;
                        }

                        owner.climbThroughWindowFrame(object0);
                        return;
                    }

                    IsoObject object1 = square0.getWallHoppableTo(square1);
                    if (object1 != null) {
                        if (!IsoWindow.canClimbThroughHelper(owner, square0, square1, owner.dir == IsoDirections.N || owner.dir == IsoDirections.S)) {
                            owner.climbDownSheetRope();
                            return;
                        }

                        owner.climbOverFence(owner.dir);
                        return;
                    }
                }

                return;
            }
        }

        owner.z = float7;
        if (owner.z >= 7.0F) {
            owner.setCollidable(true);
            owner.clearVariable("ClimbRope");
        }

        if (!IsoWindow.isSheetRopeHere(owner.getCurrentSquare())) {
            owner.setCollidable(true);
            owner.setbClimbing(false);
            owner.setbFalling(true);
            owner.clearVariable("ClimbRope");
        }

        if (owner instanceof IsoPlayer && ((IsoPlayer)owner).isLocalPlayer()) {
            ((IsoPlayer)owner).dirtyRecalcGridStackTime = 2.0F;
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setIgnoreMovement(false);
        owner.setbClimbing(false);
        owner.clearVariable("ClimbRope");
    }

    public float getClimbSheetRopeSpeed(IsoGameCharacter owner) {
        float float0 = 0.16F;
        switch (owner.getPerkLevel(PerkFactory.Perks.Strength)) {
            case 0:
                float0 -= 0.12F;
                break;
            case 1:
            case 2:
            case 3:
                float0 -= 0.09F;
            case 4:
            case 5:
            default:
                break;
            case 6:
            case 7:
                float0 += 0.05F;
                break;
            case 8:
            case 9:
                float0 += 0.09F;
                break;
            case 10:
                float0 += 0.12F;
        }

        return float0 * 0.5F;
    }
}
