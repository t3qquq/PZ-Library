// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class FakeDeadAttackState extends State {
    private static final FakeDeadAttackState _instance = new FakeDeadAttackState();

    public static FakeDeadAttackState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        IsoZombie zombie0 = (IsoZombie)owner;
        zombie0.DirectionFromVector(zombie0.vectorToTarget);
        zombie0.setFakeDead(false);
        owner.setVisibleToNPCs(true);
        owner.setCollidable(true);
        String string = "MaleZombieAttack";
        if (owner.isFemale()) {
            string = "FemaleZombieAttack";
        }

        owner.getEmitter().playSound(string);
        if (zombie0.target instanceof IsoPlayer && !((IsoPlayer)zombie0.target).getCharacterTraits().Desensitized.isSet()) {
            IsoPlayer player = (IsoPlayer)zombie0.target;
            Stats stats = player.getStats();
            stats.Panic = stats.Panic + player.getBodyDamage().getPanicIncreaseValue() * 3.0F;
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        IsoZombie zombie0 = (IsoZombie)owner;
        if (event.m_EventName.equalsIgnoreCase("AttackCollisionCheck")
            && owner.isAlive()
            && zombie0.isTargetInCone(1.5F, 0.9F)
            && zombie0.target instanceof IsoGameCharacter character
            && (character.getVehicle() == null || character.getVehicle().couldCrawlerAttackPassenger(character))) {
            character.getBodyDamage().AddRandomDamageFromZombie((IsoZombie)owner, null);
        }

        if (event.m_EventName.equalsIgnoreCase("FallOnFront")) {
            zombie0.setFallOnFront(Boolean.parseBoolean(event.m_ParameterValue));
        }

        if (event.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
            zombie0.setCrawler(true);
        }
    }
}
