// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import se.krka.kahlua.integration.expose.MethodDebugInformation;
import se.krka.kahlua.integration.expose.MethodParameter;

public class DokuWikiPrinter {
    private final ApiInformation information;
    private final PrintWriter writer;

    public DokuWikiPrinter(File file, ApiInformation apiInformation) throws IOException {
        this(new FileWriter(file), apiInformation);
    }

    public DokuWikiPrinter(Writer writerx, ApiInformation apiInformation) {
        this.information = apiInformation;
        this.writer = new PrintWriter(writerx);
    }

    public void process() {
        this.printClassHierarchy();
        this.printFunctions();
        this.writer.close();
    }

    private void printFunctions() {
        this.writer.println("====== Global functions ======");

        for (Class clazz : this.information.getAllClasses()) {
            this.printClassFunctions(clazz);
        }
    }

    private void printClassFunctions(Class<?> clazz) {
        List list = this.information.getFunctionsForClass(clazz);
        if (list.size() > 0) {
            this.writer.printf("===== %s ====\n", clazz.getSimpleName());
            this.writer.printf("In package: %s\n", clazz.getPackage().getName());

            for (MethodDebugInformation methodDebugInformation : list) {
                this.printFunction(methodDebugInformation, "====");
            }

            this.writer.printf("\n----\n\n");
        }
    }

    private void printFunction(MethodDebugInformation methodDebugInformation, String string0) {
        this.writer.printf("%s %s %s\n", string0, methodDebugInformation.getLuaName(), string0);
        this.writer.printf("<code lua>%s</code>\n", methodDebugInformation.getLuaDescription());

        for (MethodParameter methodParameter : methodDebugInformation.getParameters()) {
            String string1 = methodParameter.getName();
            String string2 = methodParameter.getType();
            String string3 = methodParameter.getDescription();
            if (string3 == null) {
                this.writer.printf("  - **''%s''** ''%s''\n", string2, string1);
            } else {
                this.writer.printf("  - **''%s''** ''%s'': %s\n", string2, string1, string3);
            }
        }

        String string4 = methodDebugInformation.getReturnDescription();
        if (string4 == null) {
            this.writer.printf("  * returns ''%s''\n", methodDebugInformation.getReturnType());
        } else {
            this.writer.printf("  * returns ''%s'': %s\n", methodDebugInformation.getReturnType(), string4);
        }
    }

    private void printClassHierarchy() {
        this.writer.println("====== Class hierarchy ======");

        for (Class clazz : this.information.getRootClasses()) {
            this.printClassHierarchy(clazz, null);
        }
    }

    private void printClassHierarchy(Class<?> clazz0, Class<?> clazz1) {
        List list0 = this.information.getChildrenForClass(clazz0);
        List list1 = this.information.getMethodsForClass(clazz0);
        if (list0.size() > 0 || list1.size() > 0 || clazz1 != null) {
            this.writer.printf("===== %s =====\n", clazz0.getSimpleName());
            this.writer.printf("In package: ''%s''\n", clazz0.getPackage().getName());
            if (clazz1 != null) {
                this.writer.printf("\nSubclass of [[#%s|%s]]\n", clazz1.getSimpleName(), clazz1.getSimpleName());
            }

            if (list0.size() > 0) {
                this.writer.printf("\nChildren: ");
                boolean boolean0 = false;

                for (Class clazz2 : list0) {
                    if (boolean0) {
                        this.writer.print(", ");
                    } else {
                        boolean0 = true;
                    }

                    this.writer.printf("[[#%s|%s]]", clazz2.getSimpleName(), clazz2.getSimpleName());
                }
            }

            this.printMethods(clazz0);
            this.writer.printf("\n----\n\n");

            for (Class clazz3 : list0) {
                this.printClassHierarchy(clazz3, clazz0);
            }
        }
    }

    private void printMethods(Class<?> clazz) {
        List list = this.information.getMethodsForClass(clazz);
        if (list.size() > 0) {
            for (MethodDebugInformation methodDebugInformation : list) {
                this.printFunction(methodDebugInformation, "====");
            }
        }
    }
}
