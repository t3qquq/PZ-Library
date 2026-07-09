// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class BSFurnace extends IsoObject {
    public float heat = 0.0F;
    public float heatDecrease = 0.005F;
    public float heatIncrease = 0.001F;
    public float fuelAmount = 0.0F;
    public float fuelDecrease = 0.001F;
    public boolean fireStarted = false;
    private IsoLightSource LightSource;
    public String sSprite;
    public String sLitSprite;

    public BSFurnace(IsoCell cell) {
        super(cell);
    }

    public BSFurnace(IsoCell cell, IsoGridSquare square, String string0, String string1) {
        super(cell, square, IsoSpriteManager.instance.getSprite(string0));
        this.sSprite = string0;
        this.sLitSprite = string1;
        this.sprite = IsoSpriteManager.instance.getSprite(string0);
        this.square = square;
        this.container = new ItemContainer();
        this.container.setType("stonefurnace");
        this.container.setParent(this);
        square.AddSpecialObject(this);
    }

    @Override
    public void update() {
        this.updateHeat();
        if (!GameClient.bClient) {
            DrainableComboItem drainableComboItem = null;
            InventoryItem item0 = null;

            for (int int0 = 0; int0 < this.getContainer().getItems().size(); int0++) {
                InventoryItem item1 = this.getContainer().getItems().get(int0);
                if (item1.getType().equals("IronIngot") && ((DrainableComboItem)item1).getUsedDelta() < 1.0F) {
                    drainableComboItem = (DrainableComboItem)item1;
                }

                if (item1.getMetalValue() > 0.0F) {
                    if (this.getHeat() > 15.0F) {
                        if (item1.getItemHeat() < 2.0F) {
                            item1.setItemHeat(item1.getItemHeat() + 0.001F * (this.getHeat() / 100.0F) * GameTime.instance.getMultiplier());
                        } else {
                            item1.setMeltingTime(
                                item1.getMeltingTime()
                                    + 0.1F * (this.getHeat() / 100.0F) * (1.0F + this.getMeltingSkill(item1) * 3 / 100.0F) * GameTime.instance.getMultiplier()
                            );
                        }

                        if (item1.getMeltingTime() == 100.0F) {
                            item0 = item1;
                        }
                    } else {
                        item1.setItemHeat(item1.getItemHeat() - 0.001F * (this.getHeat() / 100.0F) * GameTime.instance.getMultiplier());
                        item1.setMeltingTime(item1.getMeltingTime() - 0.1F * (this.getHeat() / 100.0F) * GameTime.instance.getMultiplier());
                    }
                }
            }

            if (item0 != null) {
                if (item0.getWorker() != null && !item0.getWorker().isEmpty()) {
                    for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                        IsoPlayer player = IsoPlayer.players[int1];
                        if (player != null && !player.isDead() && item0.getWorker().equals(player.getFullName())) {
                            break;
                        }
                    }
                }

                float float0 = item0.getMetalValue() + (item0.getMetalValue() * (1.0F + this.getMeltingSkill(item0) * 3 / 100.0F) - item0.getMetalValue());
                if (drainableComboItem != null) {
                    if (float0 + drainableComboItem.getUsedDelta() / drainableComboItem.getUseDelta() > 1.0F / drainableComboItem.getUseDelta()) {
                        float0 -= 1.0F / drainableComboItem.getUseDelta() - drainableComboItem.getUsedDelta() / drainableComboItem.getUseDelta();
                        drainableComboItem.setUsedDelta(1.0F);
                        drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
                        drainableComboItem.setUsedDelta(0.0F);
                        this.getContainer().addItem(drainableComboItem);
                    }
                } else {
                    drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
                    drainableComboItem.setUsedDelta(0.0F);
                    this.getContainer().addItem(drainableComboItem);
                }

                float float1 = 0.0F;
                float float2 = float0;

                while (float1 < float2) {
                    if (drainableComboItem.getUsedDelta() + float0 * drainableComboItem.getUseDelta() <= 1.0F) {
                        drainableComboItem.setUsedDelta(drainableComboItem.getUsedDelta() + float0 * drainableComboItem.getUseDelta());
                        float1 += float0;
                    } else {
                        float0 -= 1.0F / drainableComboItem.getUseDelta();
                        float1 += 1.0F / drainableComboItem.getUseDelta();
                        drainableComboItem.setUsedDelta(1.0F);
                        drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.IronIngot");
                        drainableComboItem.setUsedDelta(0.0F);
                        this.getContainer().addItem(drainableComboItem);
                    }
                }

                this.getContainer().Remove(item0);
            }
        }
    }

    private void updateHeat() {
        if (!this.isFireStarted()) {
            this.heat = this.heat - this.heatDecrease * GameTime.instance.getMultiplier();
        } else if (this.getFuelAmount() == 0.0F) {
            this.setFireStarted(false);
        } else {
            this.fuelAmount = this.fuelAmount - this.fuelDecrease * (0.2F + this.heatIncrease / 80.0F) * GameTime.instance.getMultiplier();
            if (this.getHeat() < 20.0F) {
                this.heat = this.heat + this.heatIncrease * GameTime.instance.getMultiplier();
            }

            this.heat = this.heat - this.heatDecrease * 0.05F * GameTime.instance.getMultiplier();
        }

        if (this.heat < 0.0F) {
            this.heat = 0.0F;
        }

        if (this.fuelAmount < 0.0F) {
            this.fuelAmount = 0.0F;
        }
    }

    public int getMeltingSkill(InventoryItem item) {
        if (item.getWorker() != null && !item.getWorker().isEmpty()) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && !player.isDead() && item.getWorker().equals(player.getFullName())) {
                    return player.getPerkLevel(PerkFactory.Perks.Melting);
                }
            }
        }

        return 0;
    }

    @Override
    public void load(ByteBuffer byteBuffer, int int0, boolean boolean0) throws IOException {
        super.load(byteBuffer, int0, boolean0);
        this.fireStarted = byteBuffer.get() == 1;
        this.heat = byteBuffer.getFloat();
        this.fuelAmount = byteBuffer.getFloat();
    }

    @Override
    public void save(ByteBuffer byteBuffer, boolean boolean0) throws IOException {
        super.save(byteBuffer, boolean0);
        byteBuffer.put((byte)(this.isFireStarted() ? 1 : 0));
        byteBuffer.putFloat(this.getHeat());
        byteBuffer.putFloat(this.getFuelAmount());
    }

    @Override
    public String getObjectName() {
        return "StoneFurnace";
    }

    public float getHeat() {
        return this.heat;
    }

    public void setHeat(float float0) {
        if (float0 > 100.0F) {
            float0 = 100.0F;
        }

        if (float0 < 0.0F) {
            float0 = 0.0F;
        }

        this.heat = float0;
    }

    public boolean isFireStarted() {
        return this.fireStarted;
    }

    public void updateLight() {
        if (this.fireStarted && this.LightSource == null) {
            this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.61F, 0.165F, 0.0F, 7);
            IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
        } else if (this.LightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
            this.LightSource = null;
        }
    }

    public void setFireStarted(boolean boolean0) {
        this.fireStarted = boolean0;
        this.updateLight();
        this.syncFurnace();
    }

    public void syncFurnace() {
        if (GameServer.bServer) {
            GameServer.sendFuranceChange(this, null);
        } else if (GameClient.bClient) {
            GameClient.sendFurnaceChange(this);
        }
    }

    public float getFuelAmount() {
        return this.fuelAmount;
    }

    public void setFuelAmount(float float0) {
        if (float0 > 100.0F) {
            float0 = 100.0F;
        }

        if (float0 < 0.0F) {
            float0 = 0.0F;
        }

        this.fuelAmount = float0;
    }

    public void addFuel(float float0) {
        this.setFuelAmount(this.getFuelAmount() + float0);
    }

    @Override
    public void addToWorld() {
        IsoWorld.instance.getCell().addToProcessIsoObject(this);
    }

    @Override
    public void removeFromWorld() {
        if (this.emitter != null) {
            this.emitter.stopAll();
            IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
            this.emitter = null;
        }

        super.removeFromWorld();
    }

    public float getFuelDecrease() {
        return this.fuelDecrease;
    }

    public void setFuelDecrease(float float0) {
        this.fuelDecrease = float0;
    }

    public float getHeatDecrease() {
        return this.heatDecrease;
    }

    public void setHeatDecrease(float float0) {
        this.heatDecrease = float0;
    }

    public float getHeatIncrease() {
        return this.heatIncrease;
    }

    public void setHeatIncrease(float float0) {
        this.heatIncrease = float0;
    }
}
