// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.WorldSoundManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.interfaces.IClothingWasherDryerLogic;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class ClothingDryerLogic implements IClothingWasherDryerLogic {
    private final IsoObject m_object;
    private boolean bActivated;
    private long soundInstance = -1L;
    private float lastUpdate = -1.0F;
    private boolean cycleFinished = false;
    private float startTime = 0.0F;
    private float cycleLengthMinutes = 90.0F;
    private boolean alreadyExecuted = false;

    public ClothingDryerLogic(IsoObject object) {
        this.m_object = object;
    }

    public IsoObject getObject() {
        return this.m_object;
    }

    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        this.bActivated = input.get() == 1;
    }

    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        output.put((byte)(this.isActivated() ? 1 : 0));
    }

    @Override
    public void update() {
        if (this.getObject().getObjectIndex() != -1) {
            if (this.getContainer() != null) {
                if (!this.getContainer().isPowered()) {
                    this.setActivated(false);
                }

                this.cycleFinished();
                this.updateSound();
                if (GameClient.bClient) {
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

                        for (int int1 = 0; int1 < this.getContainer().getItems().size(); int1++) {
                            InventoryItem item0 = this.getContainer().getItems().get(int1);
                            if (item0 instanceof Clothing clothing) {
                                float float2 = clothing.getWetness();
                                if (float2 > 0.0F) {
                                    float2 -= int0;
                                    clothing.setWetness(float2);
                                    if (GameServer.bServer) {
                                    }
                                }
                            }

                            if (item0.isWet() && item0.getItemWhenDry() != null) {
                                item0.setWetCooldown(item0.getWetCooldown() - int0 * 250);
                                if (item0.getWetCooldown() <= 0.0F) {
                                    InventoryItem item1 = InventoryItemFactory.CreateItem(item0.getItemWhenDry());
                                    this.getContainer().addItem(item1);
                                    this.getContainer().Remove(item0);
                                    int1--;
                                    item0.setWet(false);
                                    IsoWorld.instance.CurrentCell.addToProcessItemsRemove(item0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        if ("dryer.state".equals(change)) {
            bb.put((byte)(this.isActivated() ? 1 : 0));
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("dryer.state".equals(change)) {
            this.setActivated(bb.get() == 1);
        }
    }

    @Override
    public ItemContainer getContainer() {
        return this.getObject().getContainerByType("clothingdryer");
    }

    private void updateSound() {
        if (this.isActivated()) {
            if (!GameServer.bServer) {
                if (this.getObject().emitter != null && this.getObject().emitter.isPlaying("ClothingDryerFinished")) {
                    this.getObject().emitter.stopOrTriggerSoundByName("ClothingDryerFinished");
                }

                if (this.soundInstance == -1L) {
                    this.getObject().emitter = IsoWorld.instance
                        .getFreeEmitter(this.getObject().getX() + 0.5F, this.getObject().getY() + 0.5F, (int)this.getObject().getZ());
                    IsoWorld.instance.setEmitterOwner(this.getObject().emitter, this.getObject());
                    this.soundInstance = this.getObject().emitter.playSoundLoopedImpl("ClothingDryerRunning");
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
                this.getObject().emitter.playSoundImpl("ClothingDryerFinished", this.getObject());
            }
        }
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
    public boolean isItemAllowedInContainer(ItemContainer container, InventoryItem item) {
        return this.isActivated() ? false : this.getContainer() == container;
    }

    @Override
    public boolean isRemoveItemAllowedFromContainer(ItemContainer container, InventoryItem item) {
        return !this.getContainer().isEmpty() && this.isActivated() ? false : this.getContainer() == container;
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
