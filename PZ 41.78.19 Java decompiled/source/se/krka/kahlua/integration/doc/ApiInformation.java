// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.doc;

import java.util.List;
import se.krka.kahlua.integration.expose.MethodDebugInformation;

public interface ApiInformation {
    List<Class<?>> getAllClasses();

    List<Class<?>> getRootClasses();

    List<Class<?>> getChildrenForClass(Class<?> var1);

    List<MethodDebugInformation> getMethodsForClass(Class<?> var1);

    List<MethodDebugInformation> getFunctionsForClass(Class<?> var1);
}
