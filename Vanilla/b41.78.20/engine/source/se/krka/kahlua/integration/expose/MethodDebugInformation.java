/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.expose;

import java.util.List;
import se.krka.kahlua.integration.expose.MethodParameter;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.krka.kahlua.integration.expose.TypeUtil;

public class MethodDebugInformation {
    private final String luaName;
    private final boolean isMethod;
    private final List<MethodParameter> parameters;
    private final String returnType;
    private final String returnDescription;

    public MethodDebugInformation(String string, boolean bl, List<MethodParameter> list, String string2, String string3) {
        this.parameters = list;
        this.luaName = string;
        this.isMethod = bl;
        this.returnDescription = string3;
        if (list.size() > 0 && list.get(0).getType().equals(ReturnValues.class.getName())) {
            string2 = "...";
            list.remove(0);
        }
        this.returnType = string2;
    }

    public String getLuaName() {
        return this.luaName;
    }

    public String getLuaDescription() {
        String string = this.isMethod ? "obj:" : "";
        String string2 = TypeUtil.removePackages(this.returnType) + " " + string + this.luaName + "(" + this.getLuaParameterList() + ")\n";
        if (this.getReturnDescription() != null) {
            string2 = string2 + this.getReturnDescription() + "\n";
        }
        return string2;
    }

    public boolean isMethod() {
        return this.isMethod;
    }

    public List<MethodParameter> getParameters() {
        return this.parameters;
    }

    public String getReturnDescription() {
        return this.returnDescription;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public String toString() {
        return this.getLuaDescription();
    }

    private String getLuaParameterList() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = true;
        for (MethodParameter methodParameter : this.parameters) {
            if (bl) {
                bl = false;
            } else {
                stringBuilder.append(", ");
            }
            String string = TypeUtil.removePackages(methodParameter.getType());
            stringBuilder.append(string).append(" ").append(methodParameter.getName());
        }
        return stringBuilder.toString();
    }

    private String getParameterList() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = true;
        for (MethodParameter methodParameter : this.parameters) {
            if (bl) {
                bl = false;
            } else {
                stringBuilder.append(", ");
            }
            stringBuilder.append(methodParameter.getType()).append(" ").append(methodParameter.getName());
        }
        return stringBuilder.toString();
    }
}

