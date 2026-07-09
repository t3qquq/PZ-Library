// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.globals;

public final class RadioGlobalFloat extends RadioGlobal<Float> {
    public RadioGlobalFloat(float float0) {
        super(float0, RadioGlobalType.Float);
    }

    public RadioGlobalFloat(String string, float float0) {
        super(string, float0, RadioGlobalType.Float);
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float float0) {
        this.value = float0;
    }

    @Override
    public String getString() {
        return this.value.toString();
    }

    @Override
    public CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod) {
        if (radioGlobal instanceof RadioGlobalFloat radioGlobalFloat0) {
            switch (compareMethod) {
                case equals:
                    return this.value == radioGlobalFloat0.getValue() ? CompareResult.True : CompareResult.False;
                case notequals:
                    return this.value != radioGlobalFloat0.getValue() ? CompareResult.True : CompareResult.False;
                case lessthan:
                    return this.value < radioGlobalFloat0.getValue() ? CompareResult.True : CompareResult.False;
                case morethan:
                    return this.value > radioGlobalFloat0.getValue() ? CompareResult.True : CompareResult.False;
                case lessthanorequals:
                    return this.value <= radioGlobalFloat0.getValue() ? CompareResult.True : CompareResult.False;
                case morethanorequals:
                    return this.value >= radioGlobalFloat0.getValue() ? CompareResult.True : CompareResult.False;
                default:
                    return CompareResult.Invalid;
            }
        } else {
            return CompareResult.Invalid;
        }
    }

    @Override
    public boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
        if (radioGlobal instanceof RadioGlobalFloat radioGlobalFloat0) {
            switch (editGlobalOps) {
                case set:
                    this.value = radioGlobalFloat0.getValue();
                    return true;
                case add:
                    this.value = this.value + radioGlobalFloat0.getValue();
                    return true;
                case sub:
                    this.value = this.value - radioGlobalFloat0.getValue();
                    return true;
            }
        }

        return false;
    }
}
