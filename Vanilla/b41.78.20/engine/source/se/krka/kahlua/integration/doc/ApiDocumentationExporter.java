/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import se.krka.kahlua.integration.doc.ApiInformation;
import se.krka.kahlua.integration.expose.ClassDebugInformation;
import se.krka.kahlua.integration.expose.MethodDebugInformation;

public class ApiDocumentationExporter
implements ApiInformation {
    private final Map<Class<?>, ClassDebugInformation> classes;
    private final Map<Class<?>, List<Class<?>>> classHierarchy = new HashMap();
    private final List<Class<?>> rootClasses = new ArrayList();
    private final List<Class<?>> allClasses = new ArrayList();
    private Comparator<Class<?>> classSorter = new Comparator<Class<?>>(){

        @Override
        public int compare(Class<?> clazz, Class<?> clazz2) {
            int n = clazz.getSimpleName().compareTo(clazz2.getSimpleName());
            if (n != 0) {
                return n;
            }
            return clazz.getName().compareTo(clazz2.getName());
        }
    };
    private Comparator<MethodDebugInformation> methodSorter = new Comparator<MethodDebugInformation>(){

        @Override
        public int compare(MethodDebugInformation methodDebugInformation, MethodDebugInformation methodDebugInformation2) {
            return methodDebugInformation.getLuaName().compareTo(methodDebugInformation2.getLuaName());
        }
    };

    public ApiDocumentationExporter(Map<Class<?>, ClassDebugInformation> map) {
        this.classes = map;
        this.setupHierarchy();
    }

    public void setupHierarchy() {
        for (Map.Entry<Class<?>, ClassDebugInformation> entry : this.classes.entrySet()) {
            Class<?> clazz = entry.getKey();
            Class<?> clazz2 = clazz.getSuperclass();
            if (this.classes.get(clazz2) != null) {
                List<Class<?>> list = this.classHierarchy.get(clazz2);
                if (list == null) {
                    list = new ArrayList();
                    this.classHierarchy.put(clazz2, list);
                }
                list.add(clazz);
            } else {
                this.rootClasses.add(clazz);
            }
            this.allClasses.add(clazz);
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
        List<Class<?>> list = this.classHierarchy.get(clazz);
        if (list != null) {
            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public List<Class<?>> getRootClasses() {
        return this.rootClasses;
    }

    private List<MethodDebugInformation> getMethods(Class<?> clazz, boolean bl) {
        ArrayList<MethodDebugInformation> arrayList = new ArrayList<MethodDebugInformation>();
        ClassDebugInformation classDebugInformation = this.classes.get(clazz);
        for (MethodDebugInformation methodDebugInformation : classDebugInformation.getMethods().values()) {
            if (methodDebugInformation.isMethod() != bl) continue;
            arrayList.add(methodDebugInformation);
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

