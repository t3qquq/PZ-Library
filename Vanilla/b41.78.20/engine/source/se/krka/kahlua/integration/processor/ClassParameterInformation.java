/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import se.krka.kahlua.integration.processor.MethodParameterInformation;

public class ClassParameterInformation
implements Serializable {
    private static final long serialVersionUID = 7634190901254143200L;
    private final String packageName;
    private final String simpleClassName;
    public Map<String, MethodParameterInformation> methods = new HashMap<String, MethodParameterInformation>();

    private ClassParameterInformation() {
        this.packageName = null;
        this.simpleClassName = null;
    }

    public ClassParameterInformation(String string, String string2) {
        this.packageName = string;
        this.simpleClassName = string2;
    }

    public ClassParameterInformation(Class<?> clazz) {
        Package package_ = clazz.getPackage();
        this.packageName = package_ == null ? null : package_.getName();
        this.simpleClassName = clazz.getSimpleName();
        for (Constructor<?> executable : clazz.getConstructors()) {
            this.methods.put(DescriptorUtil.getDescriptor(executable), MethodParameterInformation.EMPTY);
        }
        for (Executable executable : clazz.getMethods()) {
            this.methods.put(DescriptorUtil.getDescriptor((Method)executable), MethodParameterInformation.EMPTY);
        }
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getSimpleClassName() {
        return this.simpleClassName;
    }

    public String getFullClassName() {
        if (this.packageName == null || this.packageName.equals("")) {
            return this.simpleClassName;
        }
        return this.packageName + "." + this.simpleClassName;
    }

    public static ClassParameterInformation getFromStream(Class<?> clazz) throws IOException, ClassNotFoundException {
        String string = ClassParameterInformation.getFileName(clazz);
        InputStream inputStream = clazz.getResourceAsStream(string);
        if (inputStream == null) {
            return null;
        }
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (ClassParameterInformation)objectInputStream.readObject();
    }

    private static String getFileName(Class<?> clazz) {
        return "/" + clazz.getPackage().getName().replace('.', '/') + "/" + ClassParameterInformation.getSimpleName(clazz) + ".luadebugdata";
    }

    private static String getSimpleName(Class<?> clazz) {
        if (clazz.getEnclosingClass() != null) {
            return ClassParameterInformation.getSimpleName(clazz.getEnclosingClass()) + "_" + clazz.getSimpleName();
        }
        return clazz.getSimpleName();
    }

    public void saveToStream(OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(this);
    }

    public String getFileName() {
        return ClassParameterInformation.getFileName(this.getClass());
    }
}

