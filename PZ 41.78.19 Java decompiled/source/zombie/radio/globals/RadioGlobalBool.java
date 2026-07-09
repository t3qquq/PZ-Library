// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.globals;

public final class RadioGlobalBool extends RadioGlobal<Boolean> {
    public RadioGlobalBool(boolean boolean0) {
        super(boolean0, RadioGlobalType.Boolean);
    }

    public RadioGlobalBool(String string, boolean boolean0) {
        super(string, boolean0, RadioGlobalType.Boolean);
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean boolean0) {
        this.value = boolean0;
    }

    @Override
    public String getString() {
        return this.value.toString();
    }

    @Override
    public CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod) {
        if (radioGlobal instanceof RadioGlobalBool radioGlobalBool0) {
            switch (compareMethod) {
                case equals:
                    return this.value.equals(radioGlobalBool0.getValue()) ? CompareResult.True : CompareResult.False;
                case notequals:
                    return !this.value.equals(radioGlobalBool0.getValue()) ? CompareResult.True : CompareResult.False;
                default:
                    return CompareResult.Invalid;
            }
        } else {
            return CompareResult.Invalid;
        }
    }

    @Override
    public boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
        if (editGlobalOps.equals(EditGlobalOps.set) && radioGlobal instanceof RadioGlobalBool) {
            this.value = ((RadioGlobalBool)radioGlobal).getValue();
            return true;
        } else {
            return false;
        }
    }
}
