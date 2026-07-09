// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.processor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MethodParameterInformation implements Serializable {
    public static final MethodParameterInformation EMPTY = new MethodParameterInformation(Collections.EMPTY_LIST);
    private static final long serialVersionUID = -3059552311721486815L;
    private final List<String> parameterNames;

    public MethodParameterInformation(List<String> list) {
        this.parameterNames = list;
    }

    public String getName(int int0) {
        return int0 >= this.parameterNames.size() ? "arg" + (int0 + 1) : this.parameterNames.get(int0);
    }
}
