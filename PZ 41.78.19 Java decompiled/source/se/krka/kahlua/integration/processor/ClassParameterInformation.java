// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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

    public ClassParameterInformation(String string0, String string1) {
        this.packageName = string0;
        this.simpleClassName = string1;
    }

    public ClassParameterInformation(Class<?> clazz) {
        Package _package = clazz.getPackage();
        this.packageName = _package == null ? null : _package.getName();
        this.simpleClassName = clazz.getSimpleName();

        for (Constructor constructor : clazz.getConstructors()) {
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
        String string = getFileName(clazz);
        InputStream inputStream = clazz.getResourceAsStream(string);
        if (inputStream == null) {
            return null;
        } else {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (ClassParameterInformation)objectInputStream.readObject();
        }
    }

    private static String getFileName(Class<?> clazz) {
        return "/" + clazz.getPackage().getName().replace('.', '/') + "/" + getSimpleName(clazz) + ".luadebugdata";
    }

    private static String getSimpleName(Class<?> clazz) {
        return clazz.getEnclosingClass() != null ? getSimpleName(clazz.getEnclosingClass()) + "_" + clazz.getSimpleName() : clazz.getSimpleName();
    }

    public void saveToStream(OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(this);
    }

    public String getFileName() {
        return getFileName(this.getClass());
    }
}
