// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.globals;

public final class EditGlobal {
    private RadioGlobal global;
    private RadioGlobal value;
    private EditGlobalOps operator;

    public EditGlobal(RadioGlobal radioGlobal0, EditGlobalOps editGlobalOps, RadioGlobal radioGlobal1) {
        this.global = radioGlobal0;
        this.operator = editGlobalOps;
        this.value = radioGlobal1;
    }

    public RadioGlobal getGlobal() {
        return this.global;
    }

    public EditGlobalOps getOperator() {
        return this.operator;
    }

    public RadioGlobal getValue() {
        return this.value;
    }
}
