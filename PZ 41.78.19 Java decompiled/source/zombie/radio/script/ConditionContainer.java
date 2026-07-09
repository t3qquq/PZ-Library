// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.script;

import java.util.ArrayList;
import java.util.List;
import zombie.radio.globals.CompareMethod;
import zombie.radio.globals.CompareResult;
import zombie.radio.globals.RadioGlobal;

public final class ConditionContainer implements ConditionIter {
    private List<ConditionIter> conditions = new ArrayList<>();
    private OperatorType operatorType = OperatorType.NONE;

    public ConditionContainer() {
        this(OperatorType.NONE);
    }

    public ConditionContainer(OperatorType operatorTypex) {
        this.operatorType = operatorTypex;
    }

    @Override
    public CompareResult Evaluate() {
        boolean boolean0 = false;

        for (int int0 = 0; int0 < this.conditions.size(); int0++) {
            ConditionIter conditionIter = this.conditions.get(int0);
            CompareResult compareResult = conditionIter != null ? conditionIter.Evaluate() : CompareResult.Invalid;
            if (compareResult.equals(CompareResult.Invalid)) {
                return compareResult;
            }

            OperatorType operatorTypex = conditionIter.getNextOperator();
            if (int0 == this.conditions.size() - 1) {
                return !operatorTypex.equals(OperatorType.NONE) ? CompareResult.Invalid : (!boolean0 ? compareResult : CompareResult.False);
            }

            if (operatorTypex.equals(OperatorType.OR)) {
                if (!boolean0 && compareResult.equals(CompareResult.True)) {
                    return compareResult;
                }

                boolean0 = false;
            } else if (operatorTypex.equals(OperatorType.AND)) {
                boolean0 = boolean0 || compareResult.equals(CompareResult.False);
            } else if (operatorTypex.equals(OperatorType.NONE)) {
                return CompareResult.Invalid;
            }
        }

        return CompareResult.Invalid;
    }

    @Override
    public OperatorType getNextOperator() {
        return this.operatorType;
    }

    @Override
    public void setNextOperator(OperatorType operatorTypex) {
        this.operatorType = operatorTypex;
    }

    public void Add(ConditionContainer conditionContainer0) {
        this.conditions.add(conditionContainer0);
    }

    public void Add(RadioGlobal radioGlobal0, RadioGlobal radioGlobal1, CompareMethod compareMethod, OperatorType operatorTypex) {
        ConditionContainer.Condition condition = new ConditionContainer.Condition(radioGlobal0, radioGlobal1, compareMethod, operatorTypex);
        this.conditions.add(condition);
    }

    private static final class Condition implements ConditionIter {
        private OperatorType operatorType = OperatorType.NONE;
        private CompareMethod compareMethod;
        private RadioGlobal valueA;
        private RadioGlobal valueB;

        public Condition(RadioGlobal radioGlobal0, RadioGlobal radioGlobal1, CompareMethod compareMethodx) {
            this(radioGlobal0, radioGlobal1, compareMethodx, OperatorType.NONE);
        }

        public Condition(RadioGlobal radioGlobal0, RadioGlobal radioGlobal1, CompareMethod compareMethodx, OperatorType operatorTypex) {
            this.valueA = radioGlobal0;
            this.valueB = radioGlobal1;
            this.operatorType = operatorTypex;
            this.compareMethod = compareMethodx;
        }

        @Override
        public CompareResult Evaluate() {
            return this.valueA != null && this.valueB != null ? this.valueA.compare(this.valueB, this.compareMethod) : CompareResult.Invalid;
        }

        @Override
        public OperatorType getNextOperator() {
            return this.operatorType;
        }

        @Override
        public void setNextOperator(OperatorType operatorTypex) {
            this.operatorType = operatorTypex;
        }
    }
}
