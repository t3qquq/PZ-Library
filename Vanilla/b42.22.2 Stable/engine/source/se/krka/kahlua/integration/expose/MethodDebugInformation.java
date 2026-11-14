// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.integration.expose;

import java.util.List;

public class MethodDebugInformation {
    private final String luaName;
    private final boolean isMethod;
    private final List<MethodParameter> parameters;
    private final String returnType;
    private final String returnDescription;

    public MethodDebugInformation(String luaName, boolean isMethod, List<MethodParameter> parameters, String returnType, String returnDescription) {
        this.parameters = parameters;
        this.luaName = luaName;
        this.isMethod = isMethod;
        this.returnDescription = returnDescription;
        if (!parameters.isEmpty() && parameters.get(0).getType().equals(ReturnValues.class.getName())) {
            returnType = "...";
            parameters.remove(0);
        }

        this.returnType = returnType;
    }

    public String getLuaName() {
        return this.luaName;
    }

    public String getLuaDescription() {
        String separator = this.isMethod ? "obj:" : "";
        String msg = TypeUtil.removePackages(this.returnType) + " " + separator + this.luaName + "(" + this.getLuaParameterList() + ")\n";
        if (this.getReturnDescription() != null) {
            msg = msg + this.getReturnDescription() + "\n";
        }

        return msg;
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
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (MethodParameter parameter : this.parameters) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }

            String type = TypeUtil.removePackages(parameter.getType());
            builder.append(type).append(" ").append(parameter.getName());
        }

        return builder.toString();
    }

    private String getParameterList() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (MethodParameter parameter : this.parameters) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }

            builder.append(parameter.getType()).append(" ").append(parameter.getName());
        }

        return builder.toString();
    }
}
