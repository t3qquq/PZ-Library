// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.util.List;

public class MethodDebugInformation {
    private final String luaName;
    private final boolean isMethod;
    private final List<MethodParameter> parameters;
    private final String returnType;
    private final String returnDescription;

    public MethodDebugInformation(String string0, boolean boolean0, List<MethodParameter> list, String string2, String string1) {
        this.parameters = list;
        this.luaName = string0;
        this.isMethod = boolean0;
        this.returnDescription = string1;
        if (list.size() > 0 && ((MethodParameter)list.get(0)).getType().equals(ReturnValues.class.getName())) {
            string2 = "...";
            list.remove(0);
        }

        this.returnType = string2;
    }

    public String getLuaName() {
        return this.luaName;
    }

    public String getLuaDescription() {
        String string0 = this.isMethod ? "obj:" : "";
        String string1 = TypeUtil.removePackages(this.returnType) + " " + string0 + this.luaName + "(" + this.getLuaParameterList() + ")\n";
        if (this.getReturnDescription() != null) {
            string1 = string1 + this.getReturnDescription() + "\n";
        }

        return string1;
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

    @Override
    public String toString() {
        return this.getLuaDescription();
    }

    private String getLuaParameterList() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean boolean0 = true;

        for (MethodParameter methodParameter : this.parameters) {
            if (boolean0) {
                boolean0 = false;
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
        boolean boolean0 = true;

        for (MethodParameter methodParameter : this.parameters) {
            if (boolean0) {
                boolean0 = false;
            } else {
                stringBuilder.append(", ");
            }

            stringBuilder.append(methodParameter.getType()).append(" ").append(methodParameter.getName());
        }

        return stringBuilder.toString();
    }
}
