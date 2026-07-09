// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

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
import se.krka.kahlua.integration.expose.caller.ConstructorCaller;
import se.krka.kahlua.integration.expose.caller.MethodCaller;
import se.krka.kahlua.integration.processor.ClassParameterInformation;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;

public class LuaJavaClassExposer {
    private static final Object DEBUGINFO_KEY = new Object();
    private final KahluaConverterManager manager;
    private final Platform platform;
    private final KahluaTable environment;
    private final KahluaTable classMetatables;
    private final Set<Type> visitedTypes = new HashSet<>();
    private final KahluaTable autoExposeBase;
    private final Map<Class<?>, Boolean> shouldExposeCache = new HashMap<>();
    public final HashMap<String, Class<?>> TypeMap = new HashMap<>();

    public LuaJavaClassExposer(KahluaConverterManager kahluaConverterManager, Platform platformx, KahluaTable table) {
        this(kahluaConverterManager, platformx, table, null);
    }

    public LuaJavaClassExposer(KahluaConverterManager kahluaConverterManager, Platform platformx, KahluaTable table0, KahluaTable table1) {
        this.manager = kahluaConverterManager;
        this.platform = platformx;
        this.environment = table0;
        this.autoExposeBase = table1;
        this.classMetatables = KahluaUtil.getClassMetatables(platformx, this.environment);
    }

    public Map<Class<?>, ClassDebugInformation> getClassDebugInformation() {
        Object object = this.environment.rawget(DEBUGINFO_KEY);
        if (object == null || !(object instanceof Map)) {
            object = new HashMap();
            this.environment.rawset(DEBUGINFO_KEY, object);
        }

        return (Map<Class<?>, ClassDebugInformation>)object;
    }

    private KahluaTable getMetaTable(Class<?> clazz) {
        return (KahluaTable)this.classMetatables.rawget(clazz);
    }

    private KahluaTable getIndexTable(KahluaTable table) {
        if (table == null) {
            return null;
        } else {
            Object object = table.rawget("__index");
            if (object == null) {
                return null;
            } else {
                return object instanceof KahluaTable ? (KahluaTable)object : null;
            }
        }
    }

    public void exposeGlobalObjectFunction(KahluaTable table, Object object, Method method) {
        this.exposeGlobalObjectFunction(table, object, method, method.getName());
    }

    public void exposeGlobalObjectFunction(KahluaTable table, Object object, Method method, String string) {
        Class clazz = object.getClass();
        this.readDebugData(clazz);
        LuaJavaInvoker luaJavaInvoker = this.getMethodInvoker(clazz, method, string, object, false);
        this.addInvoker(table, string, luaJavaInvoker);
    }

    public void exposeGlobalClassFunction(KahluaTable table, Class<?> clazz, Constructor<?> constructor, String string) {
        this.readDebugData(clazz);
        LuaJavaInvoker luaJavaInvoker = this.getConstructorInvoker(clazz, constructor, string);
        this.addInvoker(table, string, luaJavaInvoker);
    }

    private LuaJavaInvoker getMethodInvoker(Class<?> clazz, Method method, String string, Object object, boolean boolean0) {
        return new LuaJavaInvoker(this, this.manager, clazz, string, new MethodCaller(method, object, boolean0));
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

    public void exposeGlobalClassFunction(KahluaTable table, Class<?> clazz, Method method, String string) {
        this.readDebugData(clazz);
        if (Modifier.isStatic(method.getModifiers())) {
            this.addInvoker(table, string, this.getGlobalInvoker(clazz, method, string));
        }
    }

    public void exposeMethod(Class<?> clazz, Method method, KahluaTable table) {
        this.exposeMethod(clazz, method, method.getName(), table);
    }

    public void exposeMethod(Class<?> clazz, Method method, String string, KahluaTable table0) {
        this.readDebugData(clazz);
        if (!this.isExposed(clazz)) {
            this.setupMetaTables(clazz, table0);
        }

        KahluaTable table1 = this.getMetaTable(clazz);
        KahluaTable table2 = this.getIndexTable(table1);
        LuaJavaInvoker luaJavaInvoker = this.getMethodInvoker(clazz, method, string);
        this.addInvoker(table2, string, luaJavaInvoker);
    }

    private void addInvoker(KahluaTable table, String string, LuaJavaInvoker luaJavaInvoker) {
        if (string.equals("setDir")) {
            boolean boolean0 = false;
        }

        Object object = table.rawget(string);
        if (object != null) {
            if (object instanceof LuaJavaInvoker) {
                if (object.equals(luaJavaInvoker)) {
                    return;
                }

                MultiLuaJavaInvoker multiLuaJavaInvoker = new MultiLuaJavaInvoker();
                multiLuaJavaInvoker.addInvoker((LuaJavaInvoker)object);
                multiLuaJavaInvoker.addInvoker(luaJavaInvoker);
                table.rawset(string, multiLuaJavaInvoker);
            } else if (object instanceof MultiLuaJavaInvoker) {
                ((MultiLuaJavaInvoker)object).addInvoker(luaJavaInvoker);
            }
        } else {
            table.rawset(string, luaJavaInvoker);
        }
    }

    public boolean shouldExpose(Class<?> clazz0) {
        if (clazz0 == null) {
            return false;
        } else {
            Boolean boolean0 = this.shouldExposeCache.get(clazz0);
            if (boolean0 != null) {
                return boolean0;
            } else if (this.autoExposeBase != null) {
                this.exposeLikeJavaRecursively(clazz0, this.autoExposeBase);
                return true;
            } else if (this.isExposed(clazz0)) {
                this.shouldExposeCache.put(clazz0, Boolean.TRUE);
                return true;
            } else if (this.shouldExpose(clazz0.getSuperclass())) {
                this.shouldExposeCache.put(clazz0, Boolean.TRUE);
                return true;
            } else {
                for (Class clazz1 : clazz0.getInterfaces()) {
                    if (this.shouldExpose(clazz1)) {
                        this.shouldExposeCache.put(clazz0, Boolean.TRUE);
                        return true;
                    }
                }

                this.shouldExposeCache.put(clazz0, Boolean.FALSE);
                return false;
            }
        }
    }

    private void setupMetaTables(Class<?> clazz1, KahluaTable table0) {
        Class clazz0 = clazz1.getSuperclass();
        this.exposeLikeJavaRecursively(clazz0, table0);
        KahluaTable table1 = this.getMetaTable(clazz0);
        KahluaTable table2 = this.platform.newTable();
        KahluaTable table3 = this.platform.newTable();
        table2.rawset("__index", table3);
        if (table1 != null) {
            table2.rawset("__newindex", table1.rawget("__newindex"));
        }

        table3.setMetatable(table1);
        this.classMetatables.rawset(clazz1, table2);
    }

    private void addJavaEquals(KahluaTable table) {
        table.rawset("__eq", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                boolean boolean0 = luaCallFrame.get(0).equals(luaCallFrame.get(1));
                luaCallFrame.push(boolean0);
                return 1;
            }
        });
    }

    public void exposeGlobalFunctions(Object object) {
        Class clazz = object.getClass();
        this.readDebugData(clazz);

        for (Method method : clazz.getMethods()) {
            LuaMethod luaMethod = AnnotationUtil.getAnnotation(method, LuaMethod.class);
            if (luaMethod != null) {
                String string;
                if (luaMethod.name().equals("")) {
                    string = method.getName();
                } else {
                    string = luaMethod.name();
                }

                if (luaMethod.global()) {
                    this.exposeGlobalObjectFunction(this.environment, object, method, string);
                }
            }
        }
    }

    public void exposeLikeJava(Class clazz) {
        this.exposeLikeJava(clazz, this.autoExposeBase);
    }

    public void exposeLikeJava(Class clazz, KahluaTable table) {
        if (clazz != null && !this.isExposed(clazz) && this.shouldExpose(clazz)) {
            this.setupMetaTables(clazz, table);
            this.exposeMethods(clazz, table);
            if (!clazz.isSynthetic()
                && !clazz.isAnonymousClass()
                && !clazz.isPrimitive()
                && !Proxy.isProxyClass(clazz)
                && !clazz.getSimpleName().startsWith("$")) {
                this.exposeStatics(clazz, table);
            }
        }
    }

    private void exposeStatics(Class clazz, KahluaTable table1) {
        String[] strings = clazz.getName().split("\\.");
        KahluaTable table0 = this.createTableStructure(table1, strings);
        table0.rawset("class", clazz);
        if (table1.rawget(clazz.getSimpleName()) == null) {
            table1.rawset(clazz.getSimpleName(), table0);
        }

        for (Method method : clazz.getMethods()) {
            String string0 = method.getName();
            if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                this.exposeGlobalClassFunction(table0, clazz, method, string0);
            }
        }

        for (Field field : clazz.getFields()) {
            String string1 = field.getName();
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                try {
                    table0.rawset(string1, field.get(clazz));
                } catch (IllegalAccessException illegalAccessException) {
                }
            }
        }

        for (Constructor constructor : clazz.getConstructors()) {
            int int0 = constructor.getModifiers();
            if (!Modifier.isInterface(int0) && !Modifier.isAbstract(int0) && Modifier.isPublic(int0)) {
                this.addInvoker(table0, "new", this.getConstructorInvoker(clazz, constructor, "new"));
            }
        }
    }

    private void exposeMethods(Class clazz, KahluaTable table) {
        for (Method method : clazz.getMethods()) {
            String string = method.getName();
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                this.exposeMethod(clazz, method, string, table);
            }
        }
    }

    private KahluaTable createTableStructure(KahluaTable table, String[] strings) {
        for (String string : strings) {
            table = KahluaUtil.getOrCreateTable(this.platform, table, string);
        }

        return table;
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
            } catch (Exception exception) {
            }

            if (classParameterInformation == null) {
                classParameterInformation = new ClassParameterInformation(clazz);
            }

            ClassDebugInformation classDebugInformation = new ClassDebugInformation(clazz, classParameterInformation);
            Map map = this.getClassDebugInformation();
            map.put(clazz, classDebugInformation);
        }
    }

    private void readDebugData(Class<?> var1) {
    }

    @LuaMethod(
        global = true,
        name = "definition"
    )
    @Desc("returns a string that describes the object")
    public String getDefinition(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof LuaJavaInvoker) {
            MethodDebugInformation methodDebugInformation = ((LuaJavaInvoker)object).getMethodDebugData();
            return methodDebugInformation.toString();
        } else if (!(object instanceof MultiLuaJavaInvoker)) {
            return KahluaUtil.tostring(object, KahluaUtil.getWorkerThread(this.platform, this.environment));
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (LuaJavaInvoker luaJavaInvoker : ((MultiLuaJavaInvoker)object).getInvokers()) {
                stringBuilder.append(luaJavaInvoker.getMethodDebugData().toString());
            }

            return stringBuilder.toString();
        }
    }

    public void exposeLikeJavaRecursively(Type type) {
        this.exposeLikeJavaRecursively(type, this.autoExposeBase);
    }

    public void exposeLikeJavaRecursively(Type type, KahluaTable table) {
        this.exposeLikeJava(table, this.visitedTypes, type);
    }

    private void exposeLikeJava(KahluaTable table, Set<Type> set, Type type) {
        if (type != null) {
            if (!set.contains(type)) {
                set.add(type);
                if (type instanceof Class) {
                    if (!this.shouldExpose((Class<?>)type)) {
                        return;
                    }

                    this.exposeLikeJavaByClass(table, set, (Class<?>)type);
                } else if (type instanceof WildcardType wildcardType) {
                    this.exposeList(table, set, wildcardType.getLowerBounds());
                    this.exposeList(table, set, wildcardType.getUpperBounds());
                } else if (type instanceof ParameterizedType parameterizedType) {
                    this.exposeLikeJava(table, set, parameterizedType.getRawType());
                    this.exposeLikeJava(table, set, parameterizedType.getOwnerType());
                    this.exposeList(table, set, parameterizedType.getActualTypeArguments());
                } else if (type instanceof TypeVariable typeVariable) {
                    this.exposeList(table, set, typeVariable.getBounds());
                } else if (type instanceof GenericArrayType genericArrayType) {
                    this.exposeLikeJava(table, set, genericArrayType.getGenericComponentType());
                }
            }
        }
    }

    private void exposeList(KahluaTable table, Set<Type> set, Type[] types) {
        for (Type type : types) {
            this.exposeLikeJava(table, set, type);
        }
    }

    private void exposeLikeJavaByClass(KahluaTable table, Set<Type> set, Class<?> clazz) {
        String string = clazz.toString();
        string = string.substring(string.lastIndexOf(".") + 1);
        this.TypeMap.put(string, clazz);
        this.exposeList(table, set, clazz.getInterfaces());
        this.exposeLikeJava(table, set, clazz.getGenericSuperclass());
        if (clazz.isArray()) {
            this.exposeLikeJavaByClass(table, set, clazz.getComponentType());
        } else {
            this.exposeLikeJava(clazz, table);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            this.exposeList(table, set, method.getGenericParameterTypes());
            this.exposeList(table, set, method.getGenericExceptionTypes());
            this.exposeLikeJava(table, set, method.getGenericReturnType());
        }

        for (Field field : clazz.getDeclaredFields()) {
            this.exposeLikeJava(table, set, field.getGenericType());
        }

        for (Constructor constructor : clazz.getConstructors()) {
            this.exposeList(table, set, constructor.getParameterTypes());
            this.exposeList(table, set, constructor.getExceptionTypes());
        }
    }

    public void destroy() {
        this.shouldExposeCache.clear();
        this.TypeMap.clear();
        this.visitedTypes.clear();
    }
}
