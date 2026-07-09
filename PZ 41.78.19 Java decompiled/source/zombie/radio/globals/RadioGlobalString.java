// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.globals;

public final class RadioGlobalString extends RadioGlobal<String> {
    public RadioGlobalString(String string) {
        super(string, RadioGlobalType.String);
    }

    public RadioGlobalString(String string0, String string1) {
        super(string0, string1, RadioGlobalType.String);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String string) {
        this.value = string;
    }

    @Override
    public String getString() {
        return this.value;
    }

    @Override
    public CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod) {
        if (radioGlobal instanceof RadioGlobalString radioGlobalString0) {
            switch (compareMethod) {
                case equals:
                    return this.value.equals(radioGlobalString0.getValue()) ? CompareResult.True : CompareResult.False;
                case notequals:
                    return !this.value.equals(radioGlobalString0.getValue()) ? CompareResult.True : CompareResult.False;
                default:
                    return CompareResult.Invalid;
            }
        } else {
            return CompareResult.Invalid;
        }
    }

    @Override
    public boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
        if (editGlobalOps.equals(EditGlobalOps.set) && radioGlobal instanceof RadioGlobalString) {
            this.value = ((RadioGlobalString)radioGlobal).getValue();
            return true;
        } else {
            return false;
        }
    }
}
