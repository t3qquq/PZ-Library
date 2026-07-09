// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import se.krka.kahlua.integration.expose.ClassDebugInformation;
import se.krka.kahlua.integration.expose.MethodDebugInformation;

public class ApiDocumentationExporter implements ApiInformation {
    private final Map<Class<?>, ClassDebugInformation> classes;
    private final Map<Class<?>, List<Class<?>>> classHierarchy = new HashMap<>();
    private final List<Class<?>> rootClasses = new ArrayList<>();
    private final List<Class<?>> allClasses = new ArrayList<>();
    private Comparator<Class<?>> classSorter = new Comparator<Class<?>>() {
        public int compare(Class<?> clazz1, Class<?> clazz0) {
            int int0 = clazz1.getSimpleName().compareTo(clazz0.getSimpleName());
            return int0 != 0 ? int0 : clazz1.getName().compareTo(clazz0.getName());
        }
    };
    private Comparator<MethodDebugInformation> methodSorter = new Comparator<MethodDebugInformation>() {
        public int compare(MethodDebugInformation methodDebugInformation1, MethodDebugInformation methodDebugInformation0) {
            return methodDebugInformation1.getLuaName().compareTo(methodDebugInformation0.getLuaName());
        }
    };

    public ApiDocumentationExporter(Map<Class<?>, ClassDebugInformation> map) {
        this.classes = map;
        this.setupHierarchy();
    }

    public void setupHierarchy() {
        for (Entry entry : this.classes.entrySet()) {
            Class clazz0 = (Class)entry.getKey();
            Class clazz1 = clazz0.getSuperclass();
            if (this.classes.get(clazz1) != null) {
                Object object = this.classHierarchy.get(clazz1);
                if (object == null) {
                    object = new ArrayList();
                    this.classHierarchy.put(clazz1, (List<Class<?>>)object);
                }

                object.add(clazz0);
            } else {
                this.rootClasses.add(clazz0);
            }

            this.allClasses.add(clazz0);
        }

        Collections.sort(this.allClasses, this.classSorter);
        Collections.sort(this.rootClasses, this.classSorter);

        for (List list : this.classHierarchy.values()) {
            Collections.sort(list, this.classSorter);
        }
    }

    @Override
    public List<Class<?>> getAllClasses() {
        return this.allClasses;
    }

    @Override
    public List<Class<?>> getChildrenForClass(Class<?> clazz) {
        List list = this.classHierarchy.get(clazz);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<Class<?>> getRootClasses() {
        return this.rootClasses;
    }

    private List<MethodDebugInformation> getMethods(Class<?> clazz, boolean boolean0) {
        ArrayList arrayList = new ArrayList();
        ClassDebugInformation classDebugInformation = this.classes.get(clazz);

        for (MethodDebugInformation methodDebugInformation : classDebugInformation.getMethods().values()) {
            if (methodDebugInformation.isMethod() == boolean0) {
                arrayList.add(methodDebugInformation);
            }
        }

        Collections.sort(arrayList, this.methodSorter);
        return arrayList;
    }

    @Override
    public List<MethodDebugInformation> getFunctionsForClass(Class<?> clazz) {
        return this.getMethods(clazz, false);
    }

    @Override
    public List<MethodDebugInformation> getMethodsForClass(Class<?> clazz) {
        return this.getMethods(clazz, true);
    }
}
