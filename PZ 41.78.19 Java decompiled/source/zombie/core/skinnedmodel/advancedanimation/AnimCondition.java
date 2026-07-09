// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.List;

public final class AnimCondition {
    public String m_Name = "";
    public AnimCondition.Type m_Type = AnimCondition.Type.STRING;
    public float m_FloatValue = 0.0F;
    public boolean m_BoolValue = false;
    public String m_StringValue = "";
    private AnimationVariableHandle m_variableHandle;

    @Override
    public String toString() {
        return String.format("AnimCondition{name:%s type:%s value:%s }", this.m_Name, this.m_Type.toString(), this.getValueString());
    }

    public String getConditionString() {
        return this.m_Type == AnimCondition.Type.OR ? "OR" : String.format("( %s %s %s )", this.m_Name, this.m_Type.toString(), this.getValueString());
    }

    public String getValueString() {
        switch (this.m_Type) {
            case EQU:
            case NEQ:
            case LESS:
            case GTR:
                return String.valueOf(this.m_FloatValue);
            case BOOL:
                return this.m_BoolValue ? "true" : "false";
            case STRING:
            case STRNEQ:
                return this.m_StringValue;
            case OR:
                return " -- OR -- ";
            default:
                throw new RuntimeException("Unexpected internal type:" + this.m_Type);
        }
    }

    public boolean check(IAnimationVariableSource varSource) {
        return this.checkInternal(varSource);
    }

    private boolean checkInternal(IAnimationVariableSource iAnimationVariableSource) {
        AnimCondition.Type type = this.m_Type;
        if (type == AnimCondition.Type.OR) {
            return false;
        } else {
            if (this.m_variableHandle == null) {
                this.m_variableHandle = AnimationVariableHandle.alloc(this.m_Name);
            }

            IAnimationVariableSlot iAnimationVariableSlot = iAnimationVariableSource.getVariable(this.m_variableHandle);
            switch (type) {
                case EQU:
                    return iAnimationVariableSlot != null && this.m_FloatValue == iAnimationVariableSlot.getValueFloat();
                case NEQ:
                    return iAnimationVariableSlot != null && this.m_FloatValue != iAnimationVariableSlot.getValueFloat();
                case LESS:
                    return iAnimationVariableSlot != null && iAnimationVariableSlot.getValueFloat() < this.m_FloatValue;
                case GTR:
                    return iAnimationVariableSlot != null && iAnimationVariableSlot.getValueFloat() > this.m_FloatValue;
                case BOOL:
                    return (iAnimationVariableSlot != null && iAnimationVariableSlot.getValueBool()) == this.m_BoolValue;
                case STRING:
                    return this.m_StringValue.equalsIgnoreCase(iAnimationVariableSlot != null ? iAnimationVariableSlot.getValueString() : "");
                case STRNEQ:
                    return !this.m_StringValue.equalsIgnoreCase(iAnimationVariableSlot != null ? iAnimationVariableSlot.getValueString() : "");
                case OR:
                    return false;
                default:
                    throw new RuntimeException("Unexpected internal type:" + this.m_Type);
            }
        }
    }

    public static boolean pass(IAnimationVariableSource varSource, List<AnimCondition> conditions) {
        boolean boolean0 = true;

        for (int int0 = 0; int0 < conditions.size(); int0++) {
            AnimCondition animCondition = (AnimCondition)conditions.get(int0);
            if (animCondition.m_Type == AnimCondition.Type.OR) {
                if (boolean0) {
                    break;
                }

                boolean0 = true;
            } else {
                boolean0 = boolean0 && animCondition.check(varSource);
            }
        }

        return boolean0;
    }

    public static enum Type {
        STRING,
        STRNEQ,
        BOOL,
        EQU,
        NEQ,
        LESS,
        GTR,
        OR;
    }
}
