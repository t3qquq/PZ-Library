// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.integration.processor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MethodParameterInformation implements Serializable {
    public static final MethodParameterInformation EMPTY = new MethodParameterInformation(Collections.EMPTY_LIST);
    private static final long serialVersionUID = -3059552311721486815L;
    private final List<String> parameterNames;

    public MethodParameterInformation(List<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public String getName(int index) {
        return index >= this.parameterNames.size() ? "arg" + (index + 1) : this.parameterNames.get(index);
    }
}
