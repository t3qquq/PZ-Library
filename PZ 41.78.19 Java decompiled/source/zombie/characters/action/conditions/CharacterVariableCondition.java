// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action.conditions;

import org.w3c.dom.Element;
import zombie.characters.action.ActionContext;
import zombie.characters.action.IActionCondition;
import zombie.core.Core;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.util.StringUtils;

public final class CharacterVariableCondition implements IActionCondition {
    private CharacterVariableCondition.Operator op;
    private Object lhsValue;
    private Object rhsValue;

    private static Object parseValue(String string, boolean boolean0) {
        if (string.length() <= 0) {
            return string;
        } else {
            char char0 = string.charAt(0);
            if (char0 == '-' || char0 == '+' || char0 >= '0' && char0 <= '9') {
                int int0 = 0;
                if (char0 >= '0' && char0 <= '9') {
                    int0 = char0 - '0';
                }

                int int1;
                for (int1 = 1; int1 < string.length(); int1++) {
                    char char1 = string.charAt(int1);
                    if (char1 >= '0' && char1 <= '9') {
                        int0 = int0 * 10 + (char1 - '0');
                    } else if (char1 != ',') {
                        if (char1 != '.') {
                            return string;
                        }

                        int1++;
                        break;
                    }
                }

                if (int1 == string.length()) {
                    return int0;
                } else {
                    float float0 = int0;

                    for (float float1 = 10.0F; int1 < string.length(); int1++) {
                        char char2 = string.charAt(int1);
                        if (char2 >= '0' && char2 <= '9') {
                            float0 += (char2 - '0') / float1;
                            float1 *= 10.0F;
                        } else if (char2 != ',') {
                            return string;
                        }
                    }

                    if (char0 == '-') {
                        float0 *= -1.0F;
                    }

                    return float0;
                }
            } else if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("yes")) {
                return true;
            } else if (!string.equalsIgnoreCase("false") && !string.equalsIgnoreCase("no")) {
                if (boolean0) {
                    if (char0 != '\'' && char0 != '"') {
                        return new CharacterVariableCondition.CharacterVariableLookup(string);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder(string.length() - 2);

                        for (int int2 = 1; int2 < string.length(); int2++) {
                            char char3 = string.charAt(int2);
                            switch (char3) {
                                case '"':
                                case '\'':
                                    if (char3 == char0) {
                                        return stringBuilder.toString();
                                    }
                                default:
                                    stringBuilder.append(char3);
                                    break;
                                case '\\':
                                    stringBuilder.append(string.charAt(int2));
                            }
                        }

                        return stringBuilder.toString();
                    }
                } else {
                    return string;
                }
            } else {
                return false;
            }
        }
    }

    private boolean load(Element element) {
        String string0 = element.getNodeName();
        switch (string0) {
            case "isTrue":
                this.op = CharacterVariableCondition.Operator.Equal;
                this.lhsValue = new CharacterVariableCondition.CharacterVariableLookup(element.getTextContent().trim());
                this.rhsValue = true;
                return true;
            case "isFalse":
                this.op = CharacterVariableCondition.Operator.Equal;
                this.lhsValue = new CharacterVariableCondition.CharacterVariableLookup(element.getTextContent().trim());
                this.rhsValue = false;
                return true;
            case "compare":
                String string1 = element.getAttribute("op").trim();
                switch (string1) {
                    case "=":
                    case "==":
                        this.op = CharacterVariableCondition.Operator.Equal;
                        break;
                    case "!=":
                    case "<>":
                        this.op = CharacterVariableCondition.Operator.NotEqual;
                        break;
                    case "<":
                        this.op = CharacterVariableCondition.Operator.Less;
                        break;
                    case ">":
                        this.op = CharacterVariableCondition.Operator.Greater;
                        break;
                    case "<=":
                        this.op = CharacterVariableCondition.Operator.LessEqual;
                        break;
                    case ">=":
                        this.op = CharacterVariableCondition.Operator.GreaterEqual;
                        break;
                    default:
                        return false;
                }

                this.loadCompareValues(element);
                return true;
            case "gtr":
                this.op = CharacterVariableCondition.Operator.Greater;
                this.loadCompareValues(element);
                return true;
            case "less":
                this.op = CharacterVariableCondition.Operator.Less;
                this.loadCompareValues(element);
                return true;
            case "equals":
                this.op = CharacterVariableCondition.Operator.Equal;
                this.loadCompareValues(element);
                return true;
            case "notEquals":
                this.op = CharacterVariableCondition.Operator.NotEqual;
                this.loadCompareValues(element);
                return true;
            case "lessEqual":
                this.op = CharacterVariableCondition.Operator.LessEqual;
                this.loadCompareValues(element);
                return true;
            case "gtrEqual":
                this.op = CharacterVariableCondition.Operator.GreaterEqual;
                this.loadCompareValues(element);
                return true;
            default:
                return false;
        }
    }

    private void loadCompareValues(Element element) {
        String string0 = element.getAttribute("a").trim();
        String string1 = element.getAttribute("b").trim();
        this.lhsValue = parseValue(string0, true);
        this.rhsValue = parseValue(string1, false);
    }

    private static Object resolveValue(Object object, IAnimationVariableSource iAnimationVariableSource) {
        if (object instanceof CharacterVariableCondition.CharacterVariableLookup) {
            String string = iAnimationVariableSource.getVariableString(((CharacterVariableCondition.CharacterVariableLookup)object).variableName);
            return string != null ? parseValue(string, false) : null;
        } else {
            return object;
        }
    }

    private boolean resolveCompareTo(int int0) {
        switch (this.op) {
            case Equal:
                return int0 == 0;
            case NotEqual:
                return int0 != 0;
            case Less:
                return int0 < 0;
            case LessEqual:
                return int0 <= 0;
            case Greater:
                return int0 > 0;
            case GreaterEqual:
                return int0 >= 0;
            default:
                return false;
        }
    }

    @Override
    public boolean passes(ActionContext actionContext, int var2) {
        IAnimatable iAnimatable = actionContext.getOwner();
        Object object0 = resolveValue(this.lhsValue, iAnimatable);
        Object object1 = resolveValue(this.rhsValue, iAnimatable);
        if (object0 == null && object1 instanceof String && StringUtils.isNullOrEmpty((String)object1)) {
            if (this.op == CharacterVariableCondition.Operator.Equal) {
                return true;
            }

            if (this.op == CharacterVariableCondition.Operator.NotEqual) {
                return false;
            }

            boolean boolean0 = true;
        }

        if (object0 != null && object1 != null) {
            if (object0.getClass().equals(object1.getClass())) {
                if (object0 instanceof String) {
                    return this.resolveCompareTo(((String)object0).compareTo((String)object1));
                }

                if (object0 instanceof Integer) {
                    return this.resolveCompareTo(((Integer)object0).compareTo((Integer)object1));
                }

                if (object0 instanceof Float) {
                    return this.resolveCompareTo(((Float)object0).compareTo((Float)object1));
                }

                if (object0 instanceof Boolean) {
                    return this.resolveCompareTo(((Boolean)object0).compareTo((Boolean)object1));
                }
            }

            boolean boolean1 = object0 instanceof Integer;
            boolean boolean2 = object0 instanceof Float;
            boolean boolean3 = object1 instanceof Integer;
            boolean boolean4 = object1 instanceof Float;
            if ((boolean1 || boolean2) && (boolean3 || boolean4)) {
                boolean boolean5 = this.lhsValue instanceof CharacterVariableCondition.CharacterVariableLookup;
                boolean boolean6 = this.rhsValue instanceof CharacterVariableCondition.CharacterVariableLookup;
                if (boolean5 == boolean6) {
                    float float0 = boolean2 ? (Float)object0 : ((Integer)object0).intValue();
                    float float1 = boolean4 ? (Float)object1 : ((Integer)object1).intValue();
                    return this.resolveCompareTo(Float.compare(float0, float1));
                } else if (boolean5) {
                    if (boolean4) {
                        float float2 = boolean2 ? (Float)object0 : ((Integer)object0).intValue();
                        float float3 = (Float)object1;
                        return this.resolveCompareTo(Float.compare(float2, float3));
                    } else {
                        int int0 = boolean2 ? (int)((Float)object0).floatValue() : (Integer)object0;
                        int int1 = (Integer)object1;
                        return this.resolveCompareTo(Integer.compare(int0, int1));
                    }
                } else if (boolean2) {
                    float float4 = (Float)object0;
                    float float5 = boolean4 ? (Float)object1 : ((Integer)object1).intValue();
                    return this.resolveCompareTo(Float.compare(float4, float5));
                } else {
                    int int2 = (Integer)object0;
                    int int3 = boolean4 ? (int)((Float)object1).floatValue() : (Integer)object1;
                    return this.resolveCompareTo(Integer.compare(int2, int3));
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public IActionCondition clone() {
        return this;
    }

    private static String getOpString(CharacterVariableCondition.Operator operator) {
        switch (operator) {
            case Equal:
                return " == ";
            case NotEqual:
                return " != ";
            case Less:
                return " < ";
            case LessEqual:
                return " <= ";
            case Greater:
                return " > ";
            case GreaterEqual:
                return " >=";
            default:
                return " ?? ";
        }
    }

    private static String valueToString(Object object) {
        return object instanceof String ? "\"" + (String)object + "\"" : object.toString();
    }

    @Override
    public String getDescription() {
        return valueToString(this.lhsValue) + getOpString(this.op) + valueToString(this.rhsValue);
    }

    private static class CharacterVariableLookup {
        public String variableName;

        public CharacterVariableLookup(String string) {
            this.variableName = string;
            if (Core.bDebug) {
                AnimatorDebugMonitor.registerVariable(string);
            }
        }

        @Override
        public String toString() {
            return this.variableName;
        }
    }

    public static class Factory implements IActionCondition.IFactory {
        @Override
        public IActionCondition create(Element element) {
            CharacterVariableCondition characterVariableCondition = new CharacterVariableCondition();
            return characterVariableCondition.load(element) ? characterVariableCondition : null;
        }
    }

    static enum Operator {
        Equal,
        NotEqual,
        Less,
        Greater,
        LessEqual,
        GreaterEqual;
    }
}
