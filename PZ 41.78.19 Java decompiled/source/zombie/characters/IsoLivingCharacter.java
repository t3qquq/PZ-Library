// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.WorldSoundManager;
import zombie.Lua.LuaHookManager;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector2;
import zombie.network.packets.hit.AttackVars;
import zombie.ui.UIManager;

public class IsoLivingCharacter extends IsoGameCharacter {
    public float useChargeDelta = 0.0F;
    public final HandWeapon bareHands;
    public boolean bDoShove = false;
    public boolean bCollidedWithPushable = false;
    public IsoGameCharacter targetOnGround;

    public IsoLivingCharacter(IsoCell cell, float x, float y, float z) {
        super(cell, x, y, z);
        this.bareHands = (HandWeapon)InventoryItemFactory.CreateItem("Base.BareHands");
    }

    public boolean isCollidedWithPushableThisFrame() {
        return this.bCollidedWithPushable;
    }

    public boolean AttemptAttack(float ChargeDelta) {
        Object object = null;
        if (!(this.leftHandItem instanceof HandWeapon weapon)) {
            weapon = this.bareHands;
        }

        if (weapon != this.bareHands && this instanceof IsoPlayer) {
            AttackVars attackVars = new AttackVars();
            SwipeStatePlayer.instance().CalcAttackVars(this, attackVars);
            this.setDoShove(attackVars.bDoShove);
            if (LuaHookManager.TriggerHook("Attack", this, ChargeDelta, weapon)) {
                return false;
            }
        }

        return this.DoAttack(ChargeDelta);
    }

    public boolean DoAttack(float ChargeDelta) {
        if (this.isDead()) {
            return false;
        } else {
            if (this.leftHandItem != null) {
                InventoryItem item = this.leftHandItem;
                if (item instanceof HandWeapon) {
                    this.useHandWeapon = (HandWeapon)item;
                    if (this.useHandWeapon.getCondition() <= 0) {
                        return false;
                    }

                    int int0 = this.Moodles.getMoodleLevel(MoodleType.Endurance);
                    if (this.useHandWeapon.isCantAttackWithLowestEndurance() && int0 == 4) {
                        return false;
                    }

                    int int1 = 0;
                    if (this.useHandWeapon.isRanged()) {
                        int int2 = this.useHandWeapon.getRecoilDelay();
                        Float float0 = int2 * (1.0F - this.getPerkLevel(PerkFactory.Perks.Aiming) / 30.0F);
                        this.setRecoilDelay(float0.intValue());
                    }

                    if (this instanceof IsoSurvivor && this.useHandWeapon.isRanged() && int1 < this.useHandWeapon.getMaxHitCount()) {
                        for (int int3 = 0; int3 < this.getCell().getObjectList().size(); int3++) {
                            IsoMovingObject movingObject = this.getCell().getObjectList().get(int3);
                            if (movingObject != this
                                && movingObject.isShootable()
                                && this.IsAttackRange(movingObject.getX(), movingObject.getY(), movingObject.getZ())) {
                                float float1 = 1.0F;
                                if (float1 > 0.0F) {
                                    Vector2 vector0 = new Vector2(this.getX(), this.getY());
                                    Vector2 vector1 = new Vector2(movingObject.getX(), movingObject.getY());
                                    vector1.x = vector1.x - vector0.x;
                                    vector1.y = vector1.y - vector0.y;
                                    boolean boolean0 = false;
                                    if (vector1.x == 0.0F && vector1.y == 0.0F) {
                                        boolean0 = true;
                                    }

                                    Vector2 vector2 = this.getForwardDirection();
                                    this.DirectionFromVector(vector2);
                                    vector1.normalize();
                                    float float2 = vector1.dot(vector2);
                                    if (boolean0) {
                                        float2 = 1.0F;
                                    }

                                    if (float2 > 1.0F) {
                                        float2 = 1.0F;
                                    }

                                    if (float2 < -1.0F) {
                                        float2 = -1.0F;
                                    }

                                    if (float2 >= this.useHandWeapon.getMinAngle() && float2 <= this.useHandWeapon.getMaxAngle()) {
                                        int1++;
                                    }

                                    if (int1 >= this.useHandWeapon.getMaxHitCount()) {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (UIManager.getPicked() != null) {
                        this.attackTargetSquare = UIManager.getPicked().square;
                        if (UIManager.getPicked().tile instanceof IsoMovingObject) {
                            this.attackTargetSquare = ((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare();
                        }
                    }

                    if (this.useHandWeapon.getAmmoType() != null && !this.inventory.contains(this.useHandWeapon.getAmmoType())) {
                        return false;
                    }

                    if (this.useHandWeapon.getOtherHandRequire() == null
                        || this.rightHandItem != null && this.rightHandItem.getType().equals(this.useHandWeapon.getOtherHandRequire())) {
                        if (!this.useHandWeapon.isRanged()) {
                            this.getEmitter().playSound(this.useHandWeapon.getSwingSound(), this);
                            WorldSoundManager.instance
                                .addSound(
                                    this,
                                    (int)this.getX(),
                                    (int)this.getY(),
                                    (int)this.getZ(),
                                    this.useHandWeapon.getSoundRadius(),
                                    this.useHandWeapon.getSoundVolume()
                                );
                        }

                        this.AttackWasSuperAttack = this.superAttack;
                        this.changeState(SwipeStatePlayer.instance());
                        if (this.useHandWeapon.getAmmoType() != null) {
                            if (this instanceof IsoPlayer) {
                                IsoPlayer.getInstance().inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
                            } else {
                                this.inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
                            }
                        }

                        if (this.useHandWeapon.isUseSelf() && this.leftHandItem != null) {
                            this.leftHandItem.Use();
                        }

                        if (this.useHandWeapon.isOtherHandUse() && this.rightHandItem != null) {
                            this.rightHandItem.Use();
                        }

                        return true;
                    }

                    return false;
                }
            }

            return false;
        }
    }

    public boolean isDoShove() {
        return this.bDoShove;
    }

    public void setDoShove(boolean _bDoShove) {
        this.bDoShove = _bDoShove;
    }
}
