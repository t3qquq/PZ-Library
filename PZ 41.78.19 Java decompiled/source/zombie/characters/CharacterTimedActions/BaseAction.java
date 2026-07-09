// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.CharacterTimedActions;

import java.util.ArrayList;
import java.util.Arrays;
import zombie.GameTime;
import zombie.ai.states.PlayerActionsState;
import zombie.characters.CharacterActionAnims;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.EventPacket;
import zombie.ui.UIManager;
import zombie.util.StringUtils;
import zombie.util.Type;

public class BaseAction {
    public long SoundEffect = -1L;
    public float CurrentTime = -2.0F;
    public float LastTime = -1.0F;
    public int MaxTime = 60;
    public float PrevLastTime = 0.0F;
    public boolean UseProgressBar = true;
    public boolean ForceProgressBar = false;
    public IsoGameCharacter chr;
    public boolean StopOnWalk = true;
    public boolean StopOnRun = true;
    public boolean StopOnAim = false;
    public float caloriesModifier = 1.0F;
    public float delta = 0.0F;
    public boolean blockMovementEtc;
    public boolean overrideAnimation;
    public final ArrayList<String> animVariables = new ArrayList<>();
    public boolean loopAction = false;
    public boolean bStarted = false;
    public boolean forceStop = false;
    public boolean forceComplete = false;
    private static final ArrayList<String> specificNetworkAnim = new ArrayList<>(
        Arrays.asList("Reload", "Bandage", "Loot", "AttachItem", "Drink", "Eat", "Pour", "Read", "fill_container_tap", "drink_tap", "WearClothing")
    );
    private InventoryItem primaryHandItem = null;
    private InventoryItem secondaryHandItem = null;
    private String primaryHandMdl;
    private String secondaryHandMdl;
    public boolean overrideHandModels = false;

    public BaseAction(IsoGameCharacter _chr) {
        this.chr = _chr;
    }

    public void forceStop() {
        this.forceStop = true;
    }

    public void forceComplete() {
        this.forceComplete = true;
    }

    public void PlayLoopedSoundTillComplete(String name, int radius, float maxGain) {
        this.SoundEffect = this.chr.getEmitter().playSound(name);
    }

    public boolean hasStalled() {
        return !this.bStarted
            ? false
            : this.LastTime == this.CurrentTime && this.LastTime == this.PrevLastTime && this.LastTime < 0.0F || this.CurrentTime < 0.0F;
    }

    public float getJobDelta() {
        return this.delta;
    }

    public void resetJobDelta() {
        this.delta = 0.0F;
        this.CurrentTime = 0.0F;
    }

    public void waitToStart() {
        if (!this.chr.shouldWaitToStartTimedAction()) {
            this.bStarted = true;
            this.start();
        }
    }

    public void update() {
        this.PrevLastTime = this.LastTime;
        this.LastTime = this.CurrentTime;
        this.CurrentTime = this.CurrentTime + GameTime.instance.getMultiplier();
        if (this.CurrentTime < 0.0F) {
            this.CurrentTime = 0.0F;
        }

        boolean boolean0 = (Core.getInstance().isOptionProgressBar() || this.ForceProgressBar)
            && this.UseProgressBar
            && this.chr instanceof IsoPlayer
            && ((IsoPlayer)this.chr).isLocalPlayer();
        if (this.MaxTime == -1) {
            if (boolean0) {
                UIManager.getProgressBar(((IsoPlayer)this.chr).getPlayerNum()).setValue(Float.POSITIVE_INFINITY);
            }
        } else {
            if (this.MaxTime == 0) {
                this.delta = 0.0F;
            } else {
                this.delta = Math.min(this.CurrentTime / this.MaxTime, 1.0F);
            }

            if (boolean0) {
                UIManager.getProgressBar(((IsoPlayer)this.chr).getPlayerNum()).setValue(this.delta);
            }
        }
    }

    public void start() {
        this.forceComplete = false;
        this.forceStop = false;
        if (this.chr.isCurrentState(PlayerActionsState.instance())) {
            InventoryItem item0 = this.chr.getPrimaryHandItem();
            InventoryItem item1 = this.chr.getSecondaryHandItem();
            this.chr.setHideWeaponModel(!(item0 instanceof HandWeapon) && !(item1 instanceof HandWeapon));
        }
    }

    public void reset() {
        this.CurrentTime = 0.0F;
        this.forceComplete = false;
        this.forceStop = false;
    }

    public float getCurrentTime() {
        return this.CurrentTime;
    }

    public void stop() {
        UIManager.getProgressBar(((IsoPlayer)this.chr).getPlayerNum()).setValue(0.0F);
        if (this.SoundEffect > -1L) {
            this.chr.getEmitter().stopSound(this.SoundEffect);
            this.SoundEffect = -1L;
        }

        this.stopTimedActionAnim();
    }

    public boolean valid() {
        return true;
    }

    public boolean finished() {
        return this.CurrentTime >= this.MaxTime && this.MaxTime != -1;
    }

    public void perform() {
        UIManager.getProgressBar(((IsoPlayer)this.chr).getPlayerNum()).setValue(1.0F);
        if (!this.loopAction) {
            this.stopTimedActionAnim();
        }
    }

    public void setUseProgressBar(boolean use) {
        this.UseProgressBar = use;
    }

    public void setBlockMovementEtc(boolean block) {
        this.blockMovementEtc = block;
    }

    public void setOverrideAnimation(boolean override) {
        this.overrideAnimation = override;
    }

    public void stopTimedActionAnim() {
        for (int int0 = 0; int0 < this.animVariables.size(); int0++) {
            String string = this.animVariables.get(int0);
            this.chr.clearVariable(string);
        }

        this.chr.setVariable("IsPerformingAnAction", false);
        if (this.overrideHandModels) {
            this.overrideHandModels = false;
            this.chr.resetEquippedHandsModels();
        }
    }

    public void setAnimVariable(String key, String val) {
        if (!this.animVariables.contains(key)) {
            this.animVariables.add(key);
        }

        this.chr.setVariable(key, val);
    }

    public void setAnimVariable(String key, boolean val) {
        if (!this.animVariables.contains(key)) {
            this.animVariables.add(key);
        }

        this.chr.setVariable(key, String.valueOf(val));
    }

    public String getPrimaryHandMdl() {
        return this.primaryHandMdl;
    }

    public String getSecondaryHandMdl() {
        return this.secondaryHandMdl;
    }

    public InventoryItem getPrimaryHandItem() {
        return this.primaryHandItem;
    }

    public InventoryItem getSecondaryHandItem() {
        return this.secondaryHandItem;
    }

    public void setActionAnim(CharacterActionAnims act) {
        this.setActionAnim(act.toString());
    }

    public void setActionAnim(String animNode) {
        this.setAnimVariable("PerformingAction", animNode);
        this.chr.setVariable("IsPerformingAnAction", true);
        if (Core.bDebug) {
            this.chr.advancedAnimator.printDebugCharacterActions(animNode);
        }
    }

    public void setOverrideHandModels(InventoryItem primaryHand, InventoryItem secondaryHand) {
        this.setOverrideHandModels(primaryHand, secondaryHand, true);
    }

    public void setOverrideHandModels(InventoryItem primaryHand, InventoryItem secondaryHand, boolean resetModel) {
        this.setOverrideHandModelsObject(primaryHand, secondaryHand, resetModel);
    }

    public void setOverrideHandModelsString(String primaryHand, String secondaryHand) {
        this.setOverrideHandModelsString(primaryHand, secondaryHand, true);
    }

    public void setOverrideHandModelsString(String primaryHand, String secondaryHand, boolean resetModel) {
        this.setOverrideHandModelsObject(primaryHand, secondaryHand, resetModel);
    }

    public void setOverrideHandModelsObject(Object primaryHand, Object secondaryHand, boolean resetModel) {
        this.overrideHandModels = true;
        this.primaryHandItem = Type.tryCastTo(primaryHand, InventoryItem.class);
        this.secondaryHandItem = Type.tryCastTo(secondaryHand, InventoryItem.class);
        this.primaryHandMdl = StringUtils.discardNullOrWhitespace(Type.tryCastTo(primaryHand, String.class));
        this.secondaryHandMdl = StringUtils.discardNullOrWhitespace(Type.tryCastTo(secondaryHand, String.class));
        if (resetModel) {
            this.chr.resetEquippedHandsModels();
        }

        if (this.primaryHandItem != null || this.secondaryHandItem != null) {
            this.chr.reportEvent(EventPacket.EventType.EventOverrideItem.name());
        }
    }

    public void OnAnimEvent(AnimEvent event) {
    }

    public void setLoopedAction(boolean looped) {
        this.loopAction = looped;
    }
}
