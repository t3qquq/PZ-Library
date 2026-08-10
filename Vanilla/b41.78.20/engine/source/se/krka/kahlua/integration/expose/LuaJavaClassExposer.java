/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zombie.core.Core
 *  zombie.network.DesktopBrowser
 */
package se.krka.kahlua.integration.expose;

import java.awt.Desktop;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.annotations.Desc;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.AnnotationUtil;
import se.krka.kahlua.integration.expose.ClassDebugInformation;
import se.krka.kahlua.integration.expose.LuaJavaInvoker;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import se.krka.kahlua.integration.expose.MultiLuaJavaInvoker;
import se.krka.kahlua.integration.expose.caller.ConstructorCaller;
import se.krka.kahlua.integration.expose.caller.MethodCaller;
import se.krka.kahlua.integration.processor.ClassParameterInformation;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;
import zombie.core.Core;
import zombie.network.DesktopBrowser;

public class LuaJavaClassExposer {
    private static final Object DEBUGINFO_KEY = new Object();
    private final KahluaConverterManager manager;
    private final Platform platform;
    private final KahluaTable environment;
    private final KahluaTable classMetatables;
    private final Set<Type> visitedTypes = new HashSet<Type>();
    private final KahluaTable autoExposeBase;
    private final Map<Class<?>, Boolean> shouldExposeCache = new HashMap();
    public final HashMap<String, Class<?>> TypeMap = new HashMap();
    private static final Set<Class<?>> DISALLOWED_CLASSES = Set.of(Class.class, ClassLoader.class, Module.class, Runtime.class, Desktop.class, DesktopBrowser.class);

    public static boolean isDisallowed(Object object) {
        Class<?> clazz;
        Object object2;
        if (object == null) {
            return false;
        }
        if (object instanceof Class) {
            object2 = (Class<?>)object;
            v0 = object2;
        } else {
            v0 = clazz = object.getClass();
        }
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        if (clazz.isPrimitive()) {
            return false;
        }
        if (Core.bDebug && (clazz.equals(Method.class) || clazz.equals(Field.class))) {
            return true;
        }
        if (DISALLOWED_CLASSES.contains(clazz)) {
            return true;
        }
        object2 = clazz.getPackageName();
        return ((String)object2).equals("java.lang.invoke") || ((String)object2).equals("java.lang.reflect") || ClassLoader.class.isAssignableFrom(clazz);
    }

    public LuaJavaClassExposer(KahluaConverterManager kahluaConverterManager, Platform platform, KahluaTable kahluaTable) {
        this(kahluaConverterManager, platform, kahluaTable, null);
    }

    public LuaJavaClassExposer(KahluaConverterManager kahluaConverterManager, Platform platform, KahluaTable kahluaTable, KahluaTable kahluaTable2) {
        this.manager = kahluaConverterManager;
        this.platform = platform;
        this.environment = kahluaTable;
        this.autoExposeBase = kahluaTable2;
        this.classMetatables = KahluaUtil.getClassMetatables(platform, this.environment);
    }

    public Map<Class<?>, ClassDebugInformation> getClassDebugInformation() {
        HashMap hashMap = this.environment.rawget(DEBUGINFO_KEY);
        if (hashMap == null || !(hashMap instanceof Map)) {
            hashMap = new HashMap();
            this.environment.rawset(DEBUGINFO_KEY, hashMap);
        }
        return hashMap;
    }

    private KahluaTable getMetaTable(Class<?> clazz) {
        return (KahluaTable)this.classMetatables.rawget(clazz);
    }

    private KahluaTable getIndexTable(KahluaTable kahluaTable) {
        if (kahluaTable == null) {
            return null;
        }
        Object object = kahluaTable.rawget("__index");
        if (object == null) {
            return null;
        }
        if (object instanceof KahluaTable) {
            return (KahluaTable)object;
        }
        return null;
    }

    public void exposeGlobalObjectFunction(KahluaTable kahluaTable, Object object, Method method) {
        this.exposeGlobalObjectFunction(kahluaTable, object, method, method.getName());
    }

    public void exposeGlobalObjectFunction(KahluaTable kahluaTable, Object object, Method method, String string) {
        Class<?> clazz = object.getClass();
        this.readDebugData(clazz);
        LuaJavaInvoker luaJavaInvoker = this.getMethodInvoker(clazz, method, string, object, false);
        this.addInvoker(kahluaTable, string, luaJavaInvoker);
    }

    public void exposeGlobalClassFunction(KahluaTable kahluaTable, Class<?> clazz, Constructor<?> constructor, String string) {
        this.readDebugData(clazz);
        LuaJavaInvoker luaJavaInvoker = this.getConstructorInvoker(clazz, constructor, string);
        this.addInvoker(kahluaTable, string, luaJavaInvoker);
    }

    private LuaJavaInvoker getMethodInvoker(Class<?> clazz, Method method, String string, Object object, boolean bl) {
        return new LuaJavaInvoker(this, this.manager, clazz, string, new MethodCaller(method, object, bl));
    }

    private LuaJavaInvoker getConstructorInvoker(Class<?> clazz, Constructor<?> constructor, String string) {
        return new LuaJavaInvoker(this, this.manager, clazz, string, new ConstructorCaller(constructor));
    }

    private LuaJavaInvoker getMethodInvoker(Class<?> clazz, Method method, String string) {
        return this.getMethodInvoker(clazz, method, string, null, true);
    }

    private LuaJavaInvoker getGlobalInvoker(Class<?> clazz, Method method, String string) {
        return this.getMethodInvoker(clazz, method, string, null, false);
    }

    public void exposeGlobalClassFunction(KahluaTable kahluaTable, Class<?> clazz, Method method, String string) {
        this.readDebugData(clazz);
        if (Modifier.isStatic(method.getModifiers())) {
            this.addInvoker(kahluaTable, string, this.getGlobalInvoker(clazz, method, string));
        }
    }

    public void exposeMethod(Class<?> clazz, Method method, KahluaTable kahluaTable) {
        this.exposeMethod(clazz, method, method.getName(), kahluaTable);
    }

    public void exposeMethod(Class<?> clazz, Method method, String string, KahluaTable kahluaTable) {
        this.readDebugData(clazz);
        if (!this.isExposed(clazz)) {
            this.setupMetaTables(clazz, kahluaTable);
        }
        KahluaTable kahluaTable2 = this.getMetaTable(clazz);
        KahluaTable kahluaTable3 = this.getIndexTable(kahluaTable2);
        LuaJavaInvoker luaJavaInvoker = this.getMethodInvoker(clazz, method, string);
        this.addInvoker(kahluaTable3, string, luaJavaInvoker);
    }

    private void addInvoker(KahluaTable kahluaTable, String string, LuaJavaInvoker luaJavaInvoker) {
        Object object;
        if (string.equals("setDir")) {
            boolean bl = false;
        }
        if ((object = kahluaTable.rawget(string)) != null) {
            if (object instanceof LuaJavaInvoker) {
                if (object.equals(luaJavaInvoker)) {
                    return;
                }
                MultiLuaJavaInvoker multiLuaJavaInvoker = new MultiLuaJavaInvoker();
                multiLuaJavaInvoker.addInvoker((LuaJavaInvoker)object);
                multiLuaJavaInvoker.addInvoker(luaJavaInvoker);
                kahluaTable.rawset(string, (Object)multiLuaJavaInvoker);
            } else if (object instanceof MultiLuaJavaInvoker) {
                ((MultiLuaJavaInvoker)object).addInvoker(luaJavaInvoker);
            }
        } else {
            kahluaTable.rawset(string, (Object)luaJavaInvoker);
        }
    }

    public boolean shouldExpose(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        Boolean bl = this.shouldExposeCache.get(clazz);
        if (bl != null) {
            return bl;
        }
        if (this.autoExposeBase != null) {
            this.exposeLikeJavaRecursively(clazz, this.autoExposeBase);
            return true;
        }
        if (this.isExposed(clazz)) {
            this.shouldExposeCache.put(clazz, Boolean.TRUE);
            return true;
        }
        if (this.shouldExpose(clazz.getSuperclass())) {
            this.shouldExposeCache.put(clazz, Boolean.TRUE);
            return true;
        }
        for (Class<?> clazz2 : clazz.getInterfaces()) {
            if (!this.shouldExpose(clazz2)) continue;
            this.shouldExposeCache.put(clazz, Boolean.TRUE);
            return true;
        }
        this.shouldExposeCache.put(clazz, Boolean.FALSE);
        return false;
    }

    private void setupMetaTables(Class<?> clazz, KahluaTable kahluaTable) {
        Class<?> clazz2 = clazz.getSuperclass();
        this.exposeLikeJavaRecursively(clazz2, kahluaTable);
        KahluaTable kahluaTable2 = this.getMetaTable(clazz2);
        KahluaTable kahluaTable3 = this.platform.newTable();
        KahluaTable kahluaTable4 = this.platform.newTable();
        kahluaTable3.rawset("__index", (Object)kahluaTable4);
        if (kahluaTable2 != null) {
            kahluaTable3.rawset("__newindex", kahluaTable2.rawget("__newindex"));
        }
        kahluaTable4.setMetatable(kahluaTable2);
        this.classMetatables.rawset(clazz, (Object)kahluaTable3);
    }

    private void addJavaEquals(KahluaTable kahluaTable) {
        kahluaTable.rawset("__eq", (Object)new JavaFunction(){

            @Override
            public int call(LuaCallFrame luaCallFrame, int n) {
                boolean bl = luaCallFrame.get(0).equals(luaCallFrame.get(1));
                luaCallFrame.push(bl);
                return 1;
            }
        });
    }

    public void exposeGlobalFunctions(Object object) {
        Class<?> clazz = object.getClass();
        if (LuaJavaClassExposer.isDisallowed(clazz)) {
            return;
        }
        this.readDebugData(clazz);
        for (Method method : clazz.getMethods()) {
            LuaMethod luaMethod = AnnotationUtil.getAnnotation(method, LuaMethod.class);
            if (luaMethod == null) continue;
            String string = luaMethod.name().equals("") ? method.getName() : luaMethod.name();
            if (!luaMethod.global()) continue;
            this.exposeGlobalObjectFunction(this.environment, object, method, string);
        }
    }

    public void exposeLikeJava(Class clazz) {
        this.exposeLikeJava(clazz, this.autoExposeBase);
    }

    public void exposeLikeJava(Class clazz, KahluaTable kahluaTable) {
        if (clazz == null || this.isExposed(clazz) || !this.shouldExpose(clazz) || LuaJavaClassExposer.isDisallowed(clazz)) {
            return;
        }
        this.setupMetaTables(clazz, kahluaTable);
        this.exposeMethods(clazz, kahluaTable);
        if (!(clazz.isSynthetic() || clazz.isAnonymousClass() || clazz.isPrimitive() || Proxy.isProxyClass(clazz) || clazz.getSimpleName().startsWith("$"))) {
            this.exposeStatics(clazz, kahluaTable);
        }
    }

    private void exposeStatics(Class clazz, KahluaTable kahluaTable) {
        String string;
        String[] stringArray = clazz.getName().split("\\.");
        KahluaTable kahluaTable2 = this.createTableStructure(kahluaTable, stringArray);
        kahluaTable2.rawset("class", (Object)clazz);
        if (kahluaTable.rawget(clazz.getSimpleName()) == null) {
            kahluaTable.rawset(clazz.getSimpleName(), (Object)kahluaTable2);
        }
        for (Method accessibleObject : clazz.getMethods()) {
            string = accessibleObject.getName();
            if (!Modifier.isPublic(accessibleObject.getModifiers()) || !Modifier.isStatic(accessibleObject.getModifiers())) continue;
            this.exposeGlobalClassFunction(kahluaTable2, clazz, accessibleObject, string);
        }
        for (AccessibleObject accessibleObject : clazz.getFields()) {
            string = ((Field)accessibleObject).getName();
            if (!Modifier.isPublic(((Field)accessibleObject).getModifiers()) || !Modifier.isStatic(((Field)accessibleObject).getModifiers())) continue;
            try {
                kahluaTable2.rawset(string, ((Field)accessibleObject).get(clazz));
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
        for (AccessibleObject accessibleObject : clazz.getConstructors()) {
            int n = ((Constructor)accessibleObject).getModifiers();
            if (Modifier.isInterface(n) || Modifier.isAbstract(n) || !Modifier.isPublic(n)) continue;
            this.addInvoker(kahluaTable2, "new", this.getConstructorInvoker(clazz, (Constructor<?>)accessibleObject, "new"));
        }
    }

    private void exposeMethods(Class clazz, KahluaTable kahluaTable) {
        for (Method method : clazz.getMethods()) {
            String string = method.getName();
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) continue;
            this.exposeMethod(clazz, method, string, kahluaTable);
        }
    }

    private KahluaTable createTableStructure(KahluaTable kahluaTable, String[] stringArray) {
        for (String string : stringArray) {
            kahluaTable = KahluaUtil.getOrCreateTable(this.platform, kahluaTable, string);
        }
        return kahluaTable;
    }

    public boolean isExposed(Class<?> clazz) {
        return clazz != null && this.getMetaTable(clazz) != null;
    }

    ClassDebugInformation getDebugdata(Class<?> clazz) {
        this.readDebugDataD(clazz);
        return this.getClassDebugInformation().get(clazz);
    }

    ClassDebugInformation getDebugdataA(Class<?> clazz) {
        return this.getClassDebugInformation().get(clazz);
    }

    private void readDebugDataD(Class<?> clazz) {
        if (this.getDebugdataA(clazz) == null) {
            ClassParameterInformation classParameterInformation = null;
            try {
                classParameterInformation = ClassParameterInformation.getFromStream(clazz);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (classParameterInformation == null) {
                classParameterInformation = new ClassParameterInformation(clazz);
            }
            ClassDebugInformation classDebugInformation = new ClassDebugInformation(clazz, classParameterInformation);
            Map<Class<?>, ClassDebugInformation> map = this.getClassDebugInformation();
            map.put(clazz, classDebugInformation);
        }
    }

    private void readDebugData(Class<?> clazz) {
    }

    @LuaMethod(global=true, name="definition")
    @Desc(value="returns a string that describes the object")
    public String getDefinition(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof LuaJavaInvoker) {
            MethodDebugInformation methodDebugInformation = ((LuaJavaInvoker)object).getMethodDebugData();
            return methodDebugInformation.toString();
        }
        if (object instanceof MultiLuaJavaInvoker) {
            StringBuilder stringBuilder = new StringBuilder();
            for (LuaJavaInvoker luaJavaInvoker : ((MultiLuaJavaInvoker)object).getInvokers()) {
                stringBuilder.append(luaJavaInvoker.getMethodDebugData().toString());
            }
            return stringBuilder.toString();
        }
        return KahluaUtil.tostring(object, KahluaUtil.getWorkerThread(this.platform, this.environment));
    }

    public void exposeLikeJavaRecursively(Type type) {
        this.exposeLikeJavaRecursively(type, this.autoExposeBase);
    }

    public void exposeLikeJavaRecursively(Type type, KahluaTable kahluaTable) {
        this.exposeLikeJava(kahluaTable, this.visitedTypes, type);
    }

    private void exposeLikeJava(KahluaTable kahluaTable, Set<Type> set, Type type) {
        if (type == null) {
            return;
        }
        if (set.contains(type)) {
            return;
        }
        set.add(type);
        if (type instanceof Class) {
            if (!this.shouldExpose((Class)type)) {
                return;
            }
            this.exposeLikeJavaByClass(kahluaTable, set, (Class)type);
        } else if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)type;
            this.exposeList(kahluaTable, set, wildcardType.getLowerBounds());
            this.exposeList(kahluaTable, set, wildcardType.getUpperBounds());
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            this.exposeLikeJava(kahluaTable, set, parameterizedType.getRawType());
            this.exposeLikeJava(kahluaTable, set, parameterizedType.getOwnerType());
            this.exposeList(kahluaTable, set, parameterizedType.getActualTypeArguments());
        } else if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable)type;
            this.exposeList(kahluaTable, set, typeVariable.getBounds());
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            this.exposeLikeJava(kahluaTable, set, genericArrayType.getGenericComponentType());
        }
    }

    private void exposeList(KahluaTable kahluaTable, Set<Type> set, Type[] typeArray) {
        for (Type type : typeArray) {
            this.exposeLikeJava(kahluaTable, set, type);
        }
    }

    private void exposeLikeJavaByClass(KahluaTable kahluaTable, Set<Type> set, Class<?> clazz) {
        String string = clazz.toString();
        string = string.substring(string.lastIndexOf(".") + 1);
        this.TypeMap.put(string, clazz);
        this.exposeList(kahluaTable, set, clazz.getInterfaces());
        this.exposeLikeJava(kahluaTable, set, clazz.getGenericSuperclass());
        if (clazz.isArray()) {
            this.exposeLikeJavaByClass(kahluaTable, set, clazz.getComponentType());
        } else {
            this.exposeLikeJava(clazz, kahluaTable);
        }
        for (Method accessibleObject : clazz.getDeclaredMethods()) {
            this.exposeList(kahluaTable, set, accessibleObject.getGenericParameterTypes());
            this.exposeList(kahluaTable, set, accessibleObject.getGenericExceptionTypes());
            this.exposeLikeJava(kahluaTable, set, accessibleObject.getGenericReturnType());
        }
        for (AccessibleObject accessibleObject : clazz.getDeclaredFields()) {
            this.exposeLikeJava(kahluaTable, set, ((Field)accessibleObject).getGenericType());
        }
        for (AccessibleObject accessibleObject : clazz.getConstructors()) {
            this.exposeList(kahluaTable, set, ((Constructor)accessibleObject).getParameterTypes());
            this.exposeList(kahluaTable, set, ((Constructor)accessibleObject).getExceptionTypes());
        }
    }

    public void destroy() {
        this.shouldExposeCache.clear();
        this.TypeMap.clear();
        this.visitedTypes.clear();
    }
}

