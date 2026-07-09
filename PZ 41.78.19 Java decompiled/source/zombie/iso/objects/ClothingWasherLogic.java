// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.WorldSoundManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.interfaces.IClothingWasherDryerLogic;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class ClothingWasherLogic implements IClothingWasherDryerLogic {
    private final IsoObject m_object;
    private boolean bActivated;
    private long soundInstance = -1L;
    private float lastUpdate = -1.0F;
    private boolean cycleFinished = false;
    private float startTime = 0.0F;
    private float cycleLengthMinutes = 90.0F;
    private boolean alreadyExecuted = false;

    public ClothingWasherLogic(IsoObject object) {
        this.m_object = object;
    }

    public IsoObject getObject() {
        return this.m_object;
    }

    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        this.bActivated = input.get() == 1;
        this.lastUpdate = input.getFloat();
    }

    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        output.put((byte)(this.isActivated() ? 1 : 0));
        output.putFloat(this.lastUpdate);
    }

    @Override
    public void update() {
        if (this.getObject().getObjectIndex() != -1) {
            if (!this.getContainer().isPowered()) {
                this.setActivated(false);
            }

            this.updateSound();
            this.cycleFinished();
            if (GameClient.bClient) {
            }

            if (this.getObject().getWaterAmount() <= 0) {
                this.setActivated(false);
            }

            if (!this.isActivated()) {
                this.lastUpdate = -1.0F;
            } else {
                float float0 = (float)GameTime.getInstance().getWorldAgeHours();
                if (this.lastUpdate < 0.0F) {
                    this.lastUpdate = float0;
                } else if (this.lastUpdate > float0) {
                    this.lastUpdate = float0;
                }

                float float1 = float0 - this.lastUpdate;
                int int0 = (int)(float1 * 60.0F);
                if (int0 >= 1) {
                    this.lastUpdate = float0;
                    this.getObject().useWater(1 * int0);

                    for (int int1 = 0; int1 < this.getContainer().getItems().size(); int1++) {
                        InventoryItem item = this.getContainer().getItems().get(int1);
                        if (item instanceof Clothing clothing) {
                            float float2 = clothing.getBloodlevel();
                            if (float2 > 0.0F) {
                                this.removeBlood(clothing, int0 * 2);
                            }

                            float float3 = clothing.getDirtyness();
                            if (float3 > 0.0F) {
                                this.removeDirt(clothing, int0 * 2);
                            }

                            clothing.setWetness(100.0F);
                        }
                    }
                }
            }
        }
    }

    private void removeBlood(Clothing clothing, float float1) {
        ItemVisual itemVisual = clothing.getVisual();
        if (itemVisual != null) {
            for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
                BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int0);
                float float0 = itemVisual.getBlood(bloodBodyPartType);
                if (float0 > 0.0F) {
                    itemVisual.setBlood(bloodBodyPartType, float0 - float1 / 100.0F);
                }
            }

            BloodClothingType.calcTotalBloodLevel(clothing);
        }
    }

    private void removeDirt(Clothing clothing, float float1) {
        ItemVisual itemVisual = clothing.getVisual();
        if (itemVisual != null) {
            for (int int0 = 0; int0 < BloodBodyPartType.MAX.index(); int0++) {
                BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int0);
                float float0 = itemVisual.getDirt(bloodBodyPartType);
                if (float0 > 0.0F) {
                    itemVisual.setDirt(bloodBodyPartType, float0 - float1 / 100.0F);
                }
            }

            BloodClothingType.calcTotalDirtLevel(clothing);
        }
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        if ("washer.state".equals(change)) {
            bb.put((byte)(this.isActivated() ? 1 : 0));
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("washer.state".equals(change)) {
            this.setActivated(bb.get() == 1);
        }
    }

    @Override
    public ItemContainer getContainer() {
        return this.getObject().getContainerByType("clothingwasher");
    }

    private void updateSound() {
        if (this.isActivated()) {
            if (!GameServer.bServer) {
                if (this.getObject().emitter != null && this.getObject().emitter.isPlaying("ClothingWasherFinished")) {
                    this.getObject().emitter.stopOrTriggerSoundByName("ClothingWasherFinished");
                }

                if (this.soundInstance == -1L) {
                    this.getObject().emitter = IsoWorld.instance
                        .getFreeEmitter(this.getObject().getX() + 0.5F, this.getObject().getY() + 0.5F, (int)this.getObject().getZ());
                    IsoWorld.instance.setEmitterOwner(this.getObject().emitter, this.getObject());
                    this.soundInstance = this.getObject().emitter.playSoundLoopedImpl("ClothingWasherRunning");
                }
            }

            if (!GameClient.bClient) {
                WorldSoundManager.instance
                    .addSoundRepeating(this, this.getObject().square.x, this.getObject().square.y, this.getObject().square.z, 10, 10, false);
            }
        } else if (this.soundInstance != -1L) {
            this.getObject().emitter.stopOrTriggerSound(this.soundInstance);
            this.soundInstance = -1L;
            if (this.cycleFinished) {
                this.cycleFinished = false;
                this.getObject().emitter.playSoundImpl("ClothingWasherFinished", this.getObject());
            }
        }
    }

    @Override
    public boolean isItemAllowedInContainer(ItemContainer container, InventoryItem item) {
        return container != this.getContainer() ? false : !this.isActivated();
    }

    @Override
    public boolean isRemoveItemAllowedFromContainer(ItemContainer container, InventoryItem item) {
        return container != this.getContainer() ? false : container.isEmpty() || !this.isActivated();
    }

    private boolean cycleFinished() {
        if (this.isActivated()) {
            if (!this.alreadyExecuted) {
                this.startTime = (float)GameTime.getInstance().getWorldAgeHours();
                this.alreadyExecuted = true;
            }

            float float0 = (float)GameTime.getInstance().getWorldAgeHours() - this.startTime;
            int int0 = (int)(float0 * 60.0F);
            if (int0 < this.cycleLengthMinutes) {
                return false;
            }

            this.cycleFinished = true;
            this.setActivated(false);
        }

        return true;
    }

    @Override
    public boolean isActivated() {
        return this.bActivated;
    }

    @Override
    public void setActivated(boolean activated) {
        boolean boolean0 = activated != this.bActivated;
        this.bActivated = activated;
        this.alreadyExecuted = false;
        if (boolean0) {
            Thread thread = Thread.currentThread();
            if (thread == GameWindow.GameThread || thread == GameServer.MainThread) {
                IsoGenerator.updateGenerator(this.getObject().getSquare());
            }
        }
    }

    @Override
    public void switchModeOn() {
    }

    @Override
    public void switchModeOff() {
        this.setActivated(false);
        this.updateSound();
        this.cycleFinished = false;
    }
}
