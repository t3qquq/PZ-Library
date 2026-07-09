// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.globals;

public final class RadioGlobalInt extends RadioGlobal<Integer> {
    public RadioGlobalInt(int int0) {
        super(int0, RadioGlobalType.Integer);
    }

    public RadioGlobalInt(String string, int int0) {
        super(string, int0, RadioGlobalType.Integer);
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int int0) {
        this.value = int0;
    }

    @Override
    public String getString() {
        return this.value.toString();
    }

    @Override
    public CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod) {
        if (radioGlobal instanceof RadioGlobalInt radioGlobalInt0) {
            switch (compareMethod) {
                case equals:
                    return this.value == radioGlobalInt0.getValue() ? CompareResult.True : CompareResult.False;
                case notequals:
                    return this.value != radioGlobalInt0.getValue() ? CompareResult.True : CompareResult.False;
                case lessthan:
                    return this.value < radioGlobalInt0.getValue() ? CompareResult.True : CompareResult.False;
                case morethan:
                    return this.value > radioGlobalInt0.getValue() ? CompareResult.True : CompareResult.False;
                case lessthanorequals:
                    return this.value <= radioGlobalInt0.getValue() ? CompareResult.True : CompareResult.False;
                case morethanorequals:
                    return this.value >= radioGlobalInt0.getValue() ? CompareResult.True : CompareResult.False;
                default:
                    return CompareResult.Invalid;
            }
        } else {
            return CompareResult.Invalid;
        }
    }

    @Override
    public boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
        if (radioGlobal instanceof RadioGlobalInt radioGlobalInt0) {
            switch (editGlobalOps) {
                case set:
                    this.value = radioGlobalInt0.getValue();
                    return true;
                case add:
                    this.value = this.value + radioGlobalInt0.getValue();
                    return true;
                case sub:
                    this.value = this.value - radioGlobalInt0.getValue();
                    return true;
            }
        }

        return false;
    }
}
