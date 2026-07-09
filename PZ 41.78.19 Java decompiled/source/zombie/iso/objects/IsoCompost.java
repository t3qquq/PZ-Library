// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class IsoCompost extends IsoObject {
    private float compost = 0.0F;
    private float LastUpdated = -1.0F;

    public IsoCompost(IsoCell cell) {
        super(cell);
    }

    public IsoCompost(IsoCell cell, IsoGridSquare sq) {
        super(cell, sq, IsoSpriteManager.instance.getSprite("camping_01_19"));
        this.sprite = IsoSpriteManager.instance.getSprite("camping_01_19");
        this.square = sq;
        this.container = new ItemContainer();
        this.container.setType("composter");
        this.container.setParent(this);
        this.container.bExplored = true;
    }

    @Override
    public void update() {
        if (!GameClient.bClient && this.container != null) {
            float float0 = (float)GameTime.getInstance().getWorldAgeHours();
            if (this.LastUpdated < 0.0F) {
                this.LastUpdated = float0;
            } else if (this.LastUpdated > float0) {
                this.LastUpdated = float0;
            }

            float float1 = float0 - this.LastUpdated;
            if (!(float1 <= 0.0F)) {
                this.LastUpdated = float0;
                int int0 = SandboxOptions.instance.getCompostHours();

                for (int int1 = 0; int1 < this.container.getItems().size(); int1++) {
                    InventoryItem item0 = this.container.getItems().get(int1);
                    if (item0 instanceof Food food) {
                        if (GameServer.bServer) {
                            food.updateAge();
                        }

                        if (food.isRotten()) {
                            if (this.getCompost() < 100.0F) {
                                food.setRottenTime(0.0F);
                                food.setCompostTime(food.getCompostTime() + float1);
                            }

                            if (food.getCompostTime() >= int0) {
                                this.setCompost(this.getCompost() + Math.abs(food.getHungChange()) * 2.0F);
                                if (this.getCompost() > 100.0F) {
                                    this.setCompost(100.0F);
                                }

                                if (GameServer.bServer) {
                                    GameServer.sendCompost(this, null);
                                    GameServer.sendRemoveItemFromContainer(this.container, item0);
                                }

                                if (Rand.Next(10) == 0) {
                                    InventoryItem item1 = this.container.AddItem("Base.Worm");
                                    if (GameServer.bServer && item1 != null) {
                                        GameServer.sendAddItemToContainer(this.container, item1);
                                    }
                                }

                                item0.Use();
                                IsoWorld.instance.CurrentCell.addToProcessItemsRemove(item0);
                            }
                        }
                    }
                }

                this.updateSprite();
            }
        }
    }

    public void updateSprite() {
        if (this.getCompost() >= 10.0F && this.sprite.getName().equals("camping_01_19")) {
            this.sprite = IsoSpriteManager.instance.getSprite("camping_01_20");
            this.transmitUpdatedSpriteToClients();
        } else if (this.getCompost() < 10.0F && this.sprite.getName().equals("camping_01_20")) {
            this.sprite = IsoSpriteManager.instance.getSprite("camping_01_19");
            this.transmitUpdatedSpriteToClients();
        }
    }

    public void syncCompost() {
        if (GameClient.bClient) {
            GameClient.sendCompost(this);
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        if (this.container != null) {
            this.container.setType("composter");
        }

        this.compost = input.getFloat();
        if (WorldVersion >= 130) {
            this.LastUpdated = input.getFloat();
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.putFloat(this.compost);
        output.putFloat(this.LastUpdated);
    }

    @Override
    public String getObjectName() {
        return "IsoCompost";
    }

    public float getCompost() {
        return this.compost;
    }

    public void setCompost(float _compost) {
        this.compost = PZMath.clamp(_compost, 0.0F, 100.0F);
    }

    public void remove() {
        if (this.getSquare() != null) {
            this.getSquare().transmitRemoveItemFromSquare(this);
        }
    }

    @Override
    public void addToWorld() {
        this.getCell().addToProcessIsoObject(this);
    }

    @Override
    public Thumpable getThumpableFor(IsoGameCharacter chr) {
        return this.isDestroyed() ? null : this;
    }
}
