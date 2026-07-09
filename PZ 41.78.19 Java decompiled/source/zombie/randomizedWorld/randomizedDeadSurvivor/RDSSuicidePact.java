// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.characters.IsoGameCharacter;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

/**
 * Create 2 dead survivor with 1 gunshot, one handle a loaded gun
 */
public final class RDSSuicidePact extends RandomizedDeadSurvivorBase {
    public RDSSuicidePact() {
        this.name = "Suicide Pact";
        this.setChance(7);
        this.setMinimumDays(60);
    }

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef = this.getLivingRoomOrKitchen(def);
        IsoGameCharacter character = RandomizedDeadSurvivorBase.createRandomZombieForCorpse(roomDef);
        if (character != null) {
            character.addVisualDamage("ZedDmg_HEAD_Bullet");
            IsoDeadBody deadBody = RandomizedDeadSurvivorBase.createBodyFromZombie(character);
            if (deadBody != null) {
                this.addBloodSplat(deadBody.getSquare(), 4);
                deadBody.setPrimaryHandItem(this.addWeapon("Base.Pistol", true));
                character = RandomizedDeadSurvivorBase.createRandomZombieForCorpse(roomDef);
                if (character != null) {
                    character.addVisualDamage("ZedDmg_HEAD_Bullet");
                    deadBody = RandomizedDeadSurvivorBase.createBodyFromZombie(character);
                    if (deadBody != null) {
                        this.addBloodSplat(deadBody.getSquare(), 4);
                    }
                }
            }
        }
    }
}
