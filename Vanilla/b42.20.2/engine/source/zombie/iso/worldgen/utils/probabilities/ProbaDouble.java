// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.iso.worldgen.utils.probabilities;

public class ProbaDouble implements Probability {
    private final Double value;

    public ProbaDouble(Double value) {
        this.value = value;
    }

    @Override
    public float getValue() {
        return this.value.floatValue();
    }
}
