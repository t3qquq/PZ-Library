// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.BodyDamage;

public class BodyPartLast {
    private boolean bandaged;
    private boolean bitten;
    private boolean scratched;
    private boolean cut = false;

    public boolean bandaged() {
        return this.bandaged;
    }

    public boolean bitten() {
        return this.bitten;
    }

    public boolean scratched() {
        return this.scratched;
    }

    public boolean isCut() {
        return this.cut;
    }

    public void copy(BodyPart other) {
        this.bandaged = other.bandaged();
        this.bitten = other.bitten();
        this.scratched = other.scratched();
        this.cut = other.isCut();
    }
}
