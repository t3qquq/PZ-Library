// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.integration.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassParameterInformation implements Serializable {
    private static final long serialVersionUID = 7634190901254143200L;
    private final String packageName;
    private final String simpleClassName;
    public Map<String, MethodParameterInformation> methods = new HashMap<>();

    private ClassParameterInformation() {
        this.packageName = null;
        this.simpleClassName = null;
    }

    public ClassParameterInformation(String packageName, String simpleClassName) {
        this.packageName = packageName;
        this.simpleClassName = simpleClassName;
    }

    public ClassParameterInformation(Class<?> clazz) {
        Package p = clazz.getPackage();
        this.packageName = p == null ? null : p.getName();
        this.simpleClassName = clazz.getSimpleName();

        for (Constructor<?> constructor : clazz.getConstructors()) {
            this.methods.put(DescriptorUtil.getDescriptor(constructor), MethodParameterInformation.EMPTY);
        }

        for (Method method : clazz.getMethods()) {
            this.methods.put(DescriptorUtil.getDescriptor(method), MethodParameterInformation.EMPTY);
        }
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getSimpleClassName() {
        return this.simpleClassName;
    }

    public String getFullClassName() {
        return this.packageName != null && !this.packageName.equals("") ? this.packageName + "." + this.simpleClassName : this.simpleClassName;
    }

    public static ClassParameterInformation getFromStream(Class<?> clazz) throws IOException, ClassNotFoundException {
        String fileName = getFileName(clazz);
        InputStream stream = clazz.getResourceAsStream(fileName);
        if (stream == null) {
            return null;
        }

        ObjectInputStream objectStream = new ObjectInputStream(stream);
        return (ClassParameterInformation)objectStream.readObject();
    }

    private static String getFileName(Class<?> clazz) {
        return "/" + clazz.getPackage().getName().replace('.', '/') + "/" + getSimpleName(clazz) + ".luadebugdata";
    }

    private static String getSimpleName(Class<?> clazz) {
        return clazz.getEnclosingClass() != null ? getSimpleName(clazz.getEnclosingClass()) + "_" + clazz.getSimpleName() : clazz.getSimpleName();
    }

    public void saveToStream(OutputStream stream) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(stream);
        outputStream.writeObject(this);
    }

    public String getFileName() {
        return getFileName(this.getClass());
    }
}
