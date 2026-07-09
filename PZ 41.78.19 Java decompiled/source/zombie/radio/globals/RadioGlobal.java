// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.globals;

public abstract class RadioGlobal<T> {
    protected String name;
    protected T value;
    protected RadioGlobalType type = RadioGlobalType.Invalid;

    protected RadioGlobal(T object, RadioGlobalType radioGlobalType) {
        this(null, (T)object, radioGlobalType);
    }

    protected RadioGlobal(String string, T object, RadioGlobalType radioGlobalType) {
        this.name = string;
        this.value = (T)object;
        this.type = radioGlobalType;
    }

    public final RadioGlobalType getType() {
        return this.type;
    }

    public final String getName() {
        return this.name;
    }

    public abstract String getString();

    public abstract CompareResult compare(RadioGlobal var1, CompareMethod var2);

    public abstract boolean setValue(RadioGlobal var1, EditGlobalOps var2);
}
