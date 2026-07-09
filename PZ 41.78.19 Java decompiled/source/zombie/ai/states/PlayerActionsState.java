// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameClient;
import zombie.util.StringUtils;

public final class PlayerActionsState extends State {
    private static final PlayerActionsState _instance = new PlayerActionsState();

    public static PlayerActionsState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        InventoryItem item0 = owner.getPrimaryHandItem();
        InventoryItem item1 = owner.getSecondaryHandItem();
        if (!(item0 instanceof HandWeapon) && !(item1 instanceof HandWeapon)) {
            owner.setHideWeaponModel(true);
        }

        String string = owner.getVariableString("PerformingAction");
        if (GameClient.bClient
            && owner instanceof IsoPlayer
            && owner.isLocal()
            && !owner.getCharacterActions().isEmpty()
            && owner.getNetworkCharacterAI().getAction() == null) {
            owner.getNetworkCharacterAI().setAction(owner.getCharacterActions().get(0));
            GameClient.sendAction(owner.getNetworkCharacterAI().getAction(), true);
            owner.getNetworkCharacterAI().setPerformingAction(string);
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        if (GameClient.bClient && owner instanceof IsoPlayer && owner.isLocal()) {
            String string = owner.getVariableString("PerformingAction");
            if (!owner.getCharacterActions().isEmpty()
                && (
                    owner.getNetworkCharacterAI().getAction() != owner.getCharacterActions().get(0)
                        || string != null && !string.equals(owner.getNetworkCharacterAI().getPerformingAction())
                )) {
                GameClient.sendAction(owner.getNetworkCharacterAI().getAction(), false);
                owner.getNetworkCharacterAI().setAction(owner.getCharacterActions().get(0));
                GameClient.sendAction(owner.getNetworkCharacterAI().getAction(), true);
                owner.getNetworkCharacterAI().setPerformingAction(string);
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setHideWeaponModel(false);
        if (GameClient.bClient && owner instanceof IsoPlayer && owner.isLocal() && owner.getNetworkCharacterAI().getAction() != null) {
            GameClient.sendAction(owner.getNetworkCharacterAI().getAction(), false);
            owner.getNetworkCharacterAI().setAction(null);
        }
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (GameClient.bClient
            && event != null
            && owner instanceof IsoPlayer
            && owner.getNetworkCharacterAI().getAction() != null
            && !owner.isLocal()
            && "changeWeaponSprite".equalsIgnoreCase(event.m_EventName)
            && !StringUtils.isNullOrEmpty(event.m_ParameterValue)) {
            if ("original".equals(event.m_ParameterValue)) {
                owner.getNetworkCharacterAI().setOverride(false, null, null);
            } else {
                owner.getNetworkCharacterAI().setOverride(true, event.m_ParameterValue, null);
            }
        }
    }
}
