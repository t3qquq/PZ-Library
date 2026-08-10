// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

public final class IsoLuaCharacter extends IsoGameCharacter {
    public IsoLuaCharacter(float float0, float float1, float float2) {
        super(null, float0, float1, float2);
        this.descriptor = SurvivorFactory.CreateSurvivor();
        this.descriptor.setInstance(this);
        SurvivorDesc survivorDesc = this.descriptor;
        this.InitSpriteParts(survivorDesc);
    }

    @Override
    public void update() {
    }
}
