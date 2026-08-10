/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import se.krka.kahlua.integration.doc.ApiInformation;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import se.krka.kahlua.integration.expose.MethodParameter;

public class DokuWikiPrinter {
    private final ApiInformation information;
    private final PrintWriter writer;

    public DokuWikiPrinter(File file, ApiInformation apiInformation) throws IOException {
        this(new FileWriter(file), apiInformation);
    }

    public DokuWikiPrinter(Writer writer, ApiInformation apiInformation) {
        this.information = apiInformation;
        this.writer = new PrintWriter(writer);
    }

    public void process() {
        this.printClassHierarchy();
        this.printFunctions();
        this.writer.close();
    }

    private void printFunctions() {
        this.writer.println("====== Global functions ======");
        List<Class<?>> list = this.information.getAllClasses();
        for (Class<?> clazz : list) {
            this.printClassFunctions(clazz);
        }
    }

    private void printClassFunctions(Class<?> clazz) {
        List<MethodDebugInformation> list = this.information.getFunctionsForClass(clazz);
        if (list.size() > 0) {
            this.writer.printf("===== %s ====\n", clazz.getSimpleName());
            this.writer.printf("In package: %s\n", clazz.getPackage().getName());
            for (MethodDebugInformation methodDebugInformation : list) {
                this.printFunction(methodDebugInformation, "====");
            }
            this.writer.printf("\n----\n\n", new Object[0]);
        }
    }

    private void printFunction(MethodDebugInformation methodDebugInformation, String string) {
        this.writer.printf("%s %s %s\n", string, methodDebugInformation.getLuaName(), string);
        this.writer.printf("<code lua>%s</code>\n", methodDebugInformation.getLuaDescription());
        for (MethodParameter methodParameter : methodDebugInformation.getParameters()) {
            String string2 = methodParameter.getName();
            String string3 = methodParameter.getType();
            String string4 = methodParameter.getDescription();
            if (string4 == null) {
                this.writer.printf("  - **''%s''** ''%s''\n", string3, string2);
                continue;
            }
            this.writer.printf("  - **''%s''** ''%s'': %s\n", string3, string2, string4);
        }
        String string5 = methodDebugInformation.getReturnDescription();
        if (string5 == null) {
            this.writer.printf("  * returns ''%s''\n", methodDebugInformation.getReturnType());
        } else {
            this.writer.printf("  * returns ''%s'': %s\n", methodDebugInformation.getReturnType(), string5);
        }
    }

    private void printClassHierarchy() {
        this.writer.println("====== Class hierarchy ======");
        List<Class<?>> list = this.information.getRootClasses();
        for (Class<?> clazz : list) {
            this.printClassHierarchy(clazz, null);
        }
    }

    private void printClassHierarchy(Class<?> clazz, Class<?> clazz2) {
        List<Class<?>> list = this.information.getChildrenForClass(clazz);
        List<MethodDebugInformation> list2 = this.information.getMethodsForClass(clazz);
        if (list.size() > 0 || list2.size() > 0 || clazz2 != null) {
            this.writer.printf("===== %s =====\n", clazz.getSimpleName());
            this.writer.printf("In package: ''%s''\n", clazz.getPackage().getName());
            if (clazz2 != null) {
                this.writer.printf("\nSubclass of [[#%s|%s]]\n", clazz2.getSimpleName(), clazz2.getSimpleName());
            }
            if (list.size() > 0) {
                this.writer.printf("\nChildren: ", new Object[0]);
                boolean bl = false;
                for (Class<?> clazz3 : list) {
                    if (bl) {
                        this.writer.print(", ");
                    } else {
                        bl = true;
                    }
                    this.writer.printf("[[#%s|%s]]", clazz3.getSimpleName(), clazz3.getSimpleName());
                }
            }
            this.printMethods(clazz);
            this.writer.printf("\n----\n\n", new Object[0]);
            for (Class<?> clazz4 : list) {
                this.printClassHierarchy(clazz4, clazz);
            }
        }
    }

    private void printMethods(Class<?> clazz) {
        List<MethodDebugInformation> list = this.information.getMethodsForClass(clazz);
        if (list.size() > 0) {
            for (MethodDebugInformation methodDebugInformation : list) {
                this.printFunction(methodDebugInformation, "====");
            }
        }
    }
}

