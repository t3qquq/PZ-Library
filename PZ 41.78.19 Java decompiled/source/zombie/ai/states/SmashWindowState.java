// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.objects.IsoWindow;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleWindow;

public final class SmashWindowState extends State {
    private static final SmashWindowState _instance = new SmashWindowState();

    public static SmashWindowState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        owner.setVariable("bSmashWindow", true);
        HandWeapon weapon = Type.tryCastTo(owner.getPrimaryHandItem(), HandWeapon.class);
        if (weapon != null && weapon.isRanged()) {
            owner.playSound("AttackShove");
        } else if (weapon != null && !StringUtils.isNullOrWhitespace(weapon.getSwingSound())) {
            owner.playSound(weapon.getSwingSound());
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        if (!(hashMap.get(0) instanceof IsoWindow) && !(hashMap.get(0) instanceof VehicleWindow)) {
            owner.setVariable("bSmashWindow", false);
        } else {
            IsoPlayer player = Type.tryCastTo(owner, IsoPlayer.class);
            if (!player.pressedMovement(false) && !player.pressedCancelAction()) {
                if (hashMap.get(0) instanceof IsoWindow) {
                    IsoWindow window = (IsoWindow)hashMap.get(0);
                    if (window.getObjectIndex() == -1 || window.isDestroyed() && !"true".equals(owner.getVariableString("OwnerSmashedIt"))) {
                        owner.setVariable("bSmashWindow", false);
                        return;
                    }

                    if (window.north) {
                        if (window.getSquare().getY() < owner.getY()) {
                            owner.setDir(IsoDirections.N);
                        } else {
                            owner.setDir(IsoDirections.S);
                        }
                    } else if (window.getSquare().getX() < owner.getX()) {
                        owner.setDir(IsoDirections.W);
                    } else {
                        owner.setDir(IsoDirections.E);
                    }
                } else if (hashMap.get(0) instanceof VehicleWindow) {
                    VehicleWindow vehicleWindow = (VehicleWindow)hashMap.get(0);
                    owner.faceThisObject((BaseVehicle)hashMap.get(1));
                    if (vehicleWindow.isDestroyed() && !"true".equals(owner.getVariableString("OwnerSmashedIt"))) {
                        owner.setVariable("bSmashWindow", false);
                        return;
                    }
                }
            } else {
                owner.setVariable("bSmashWindow", false);
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setIgnoreMovement(false);
        owner.clearVariable("bSmashWindow");
        owner.clearVariable("OwnerSmashedIt");
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        HashMap hashMap = owner.getStateMachineParams(this);
        if (hashMap.get(0) instanceof IsoWindow) {
            IsoWindow window = (IsoWindow)hashMap.get(0);
            if (event.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
                owner.setVariable("OwnerSmashedIt", true);
                IsoPlayer.getInstance().ContextPanic = 0.0F;
                window.WeaponHit(owner, null);
                if (!(owner.getPrimaryHandItem() instanceof HandWeapon) && !(owner.getSecondaryHandItem() instanceof HandWeapon)) {
                    owner.getBodyDamage().setScratchedWindow();
                }
            } else if (event.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
                owner.setVariable("bSmashWindow", false);
                if (Boolean.TRUE == hashMap.get(3)) {
                    owner.climbThroughWindow(window);
                }
            }
        } else if (hashMap.get(0) instanceof VehicleWindow) {
            VehicleWindow vehicleWindow = (VehicleWindow)hashMap.get(0);
            if (event.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
                owner.setVariable("OwnerSmashedIt", true);
                IsoPlayer.getInstance().ContextPanic = 0.0F;
                vehicleWindow.hit(owner);
                if (!(owner.getPrimaryHandItem() instanceof HandWeapon) && !(owner.getSecondaryHandItem() instanceof HandWeapon)) {
                    owner.getBodyDamage().setScratchedWindow();
                }
            } else if (event.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
                owner.setVariable("bSmashWindow", false);
            }
        }
    }

    /**
     * @return TRUE if this state handles the "Cancel Action" key or the B controller button.
     */
    @Override
    public boolean isDoingActionThatCanBeCancelled() {
        return true;
    }
}
