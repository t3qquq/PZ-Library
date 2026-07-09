// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.Lua.LuaEventManager;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoPushableObject;

public final class IsoSurvivor extends IsoLivingCharacter {
    public boolean NoGoreDeath = false;
    public boolean Draggable = false;
    public IsoGameCharacter following = null;
    boolean Dragging;
    int repathDelay = 0;
    public int nightsSurvived = 0;
    public int ping = 0;
    public IsoPushableObject collidePushable;
    private boolean tryToTeamUp = true;
    int NeightbourUpdate = 20;
    int NeightbourUpdateMax = 20;

    @Override
    public void Despawn() {
        if (this.descriptor != null) {
            this.descriptor.Instance = null;
        }
    }

    @Override
    public String getObjectName() {
        return "Survivor";
    }

    public IsoSurvivor(IsoCell cell) {
        super(cell, 0.0F, 0.0F, 0.0F);
        this.OutlineOnMouseover = true;
        this.getCell().getSurvivorList().add(this);
        LuaEventManager.triggerEvent("OnCreateSurvivor", this);
        this.initWornItems("Human");
        this.initAttachedItems("Human");
    }

    public IsoSurvivor(IsoCell cell, int x, int y, int z) {
        super(cell, x, y, z);
        this.getCell().getSurvivorList().add(this);
        this.OutlineOnMouseover = true;
        this.descriptor = new SurvivorDesc();
        this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
        this.sprite.LoadFramesPcx("Wife", "death", 1);
        this.sprite.LoadFramesPcx("Wife", "dragged", 1);
        this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
        this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
        this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
        this.name = "Kate";
        this.solid = false;
        this.IgnoreStaggerBack = true;
        this.SpeakColour = new Color(204, 100, 100);
        this.dir = IsoDirections.S;
        this.OutlineOnMouseover = true;
        this.finder.maxSearchDistance = 120;
        LuaEventManager.triggerEvent("OnCreateSurvivor", this);
        LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
        this.initWornItems("Human");
        this.initAttachedItems("Human");
    }

    public IsoSurvivor(SurvivorDesc desc, IsoCell cell, int x, int y, int z) {
        super(cell, x, y, z);
        this.setFemale(desc.isFemale());
        this.descriptor = desc;
        desc.setInstance(this);
        this.OutlineOnMouseover = true;
        String string = "Zombie_palette";
        string = string + "01";
        this.InitSpriteParts(desc);
        this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
        this.finder.maxSearchDistance = 120;
        this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
        this.Dressup(desc);
        LuaEventManager.triggerEventGarbage("OnCreateSurvivor", this);
        LuaEventManager.triggerEventGarbage("OnCreateLivingCharacter", this, this.descriptor);
        this.initWornItems("Human");
        this.initAttachedItems("Human");
    }

    public void reloadSpritePart() {
    }

    public IsoSurvivor(SurvivorDesc desc, IsoCell cell, int x, int y, int z, boolean bSetInstance) {
        super(cell, x, y, z);
        this.setFemale(desc.isFemale());
        this.descriptor = desc;
        if (bSetInstance) {
            desc.setInstance(this);
        }

        this.OutlineOnMouseover = true;
        this.InitSpriteParts(desc);
        this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
        this.finder.maxSearchDistance = 120;
        this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
        this.Dressup(desc);
        LuaEventManager.triggerEvent("OnCreateSurvivor", this);
    }
}
