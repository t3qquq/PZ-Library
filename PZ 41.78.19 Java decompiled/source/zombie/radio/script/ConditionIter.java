// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.script;

import zombie.radio.globals.CompareResult;

public interface ConditionIter {
    CompareResult Evaluate();

    OperatorType getNextOperator();

    void setNextOperator(OperatorType var1);
}
