// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;

public final class CrawlingZombieTurnState extends State {
    private static final CrawlingZombieTurnState _instance = new CrawlingZombieTurnState();
    private static final Vector2 tempVector2_1 = new Vector2();
    private static final Vector2 tempVector2_2 = new Vector2();

    public static CrawlingZombieTurnState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        ((IsoZombie)owner).AllowRepathDelay = 0.0F;
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (event.m_EventName.equalsIgnoreCase("TurnSome")) {
            Vector2 vector0 = tempVector2_1.set(owner.dir.ToVector());
            Vector2 vector1 = "left".equalsIgnoreCase(event.m_ParameterValue)
                ? IsoDirections.fromIndex(owner.dir.index() + 1).ToVector()
                : IsoDirections.fromIndex(owner.dir.index() - 1).ToVector();
            Vector2 vector2 = PZMath.lerp(tempVector2_2, vector0, vector1, event.m_TimePc);
            owner.setForwardDirection(vector2);
        } else {
            if (event.m_EventName.equalsIgnoreCase("TurnComplete")) {
                if ("left".equalsIgnoreCase(event.m_ParameterValue)) {
                    owner.dir = IsoDirections.fromIndex(owner.dir.index() + 1);
                } else {
                    owner.dir = IsoDirections.fromIndex(owner.dir.index() - 1);
                }

                owner.getVectorFromDirection(owner.getForwardDirection());
            }
        }
    }

    public static boolean calculateDir(IsoGameCharacter owner, IsoDirections targetDir) {
        return targetDir.index() > owner.dir.index() ? targetDir.index() - owner.dir.index() <= 4 : targetDir.index() - owner.dir.index() < -4;
    }
}
