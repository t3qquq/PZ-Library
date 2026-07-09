// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import se.krka.kahlua.integration.annotations.LuaConstructor;
import se.krka.kahlua.integration.annotations.LuaMethod;

public class LuaDebugDataProcessor implements Processor, ElementVisitor<Void, Void> {
    private HashMap<String, ClassParameterInformation> classes;
    private Filer filer;

    @Override
    public Iterable<? extends Completion> getCompletions(Element var1, AnnotationMirror var2, ExecutableElement var3, String var4) {
        return new HashSet<>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet hashSet = new HashSet();
        hashSet.add(LuaMethod.class.getName());
        hashSet.add(LuaConstructor.class.getName());
        return hashSet;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<>();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public void init(ProcessingEnvironment processingEnvironment) {
        this.filer = processingEnvironment.getFiler();
        this.classes = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (TypeElement typeElement : set) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(typeElement)) {
                element.accept(this, null);
            }
        }

        if (roundEnvironment.processingOver()) {
            try {
                this.store();
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }

        return true;
    }

    public Void visit(Element var1) {
        return null;
    }

    public Void visit(Element var1, Void var2) {
        return null;
    }

    public Void visitExecutable(ExecutableElement executableElement, Void var2) {
        String string0 = this.findClass(executableElement);
        String string1 = this.findPackage(executableElement);
        ClassParameterInformation classParameterInformation = this.getOrCreate(this.classes, string0, string1, this.findSimpleClassName(executableElement));
        String string2 = executableElement.getSimpleName().toString();
        String string3 = DescriptorUtil.getDescriptor(string2, executableElement.getParameters());
        ArrayList arrayList = new ArrayList();

        for (VariableElement variableElement : executableElement.getParameters()) {
            arrayList.add(variableElement.getSimpleName().toString());
        }

        MethodParameterInformation methodParameterInformation = new MethodParameterInformation(arrayList);
        classParameterInformation.methods.put(string3, methodParameterInformation);
        return null;
    }

    private ClassParameterInformation getOrCreate(HashMap<String, ClassParameterInformation> hashMap, String string0, String string1, String string2) {
        ClassParameterInformation classParameterInformation = (ClassParameterInformation)hashMap.get(string0);
        if (classParameterInformation == null) {
            classParameterInformation = new ClassParameterInformation(string1, string2);
            hashMap.put(string0, classParameterInformation);
        }

        return classParameterInformation;
    }

    private String findClass(Element element) {
        return element.getKind() == ElementKind.CLASS ? element.toString() : this.findClass(element.getEnclosingElement());
    }

    private String findSimpleClassName(Element element) {
        if (element.getKind() == ElementKind.CLASS) {
            String string = element.getSimpleName().toString();
            return element.getEnclosingElement().getKind() == ElementKind.CLASS
                ? this.findSimpleClassName(element.getEnclosingElement()) + "_" + string
                : string;
        } else {
            return this.findSimpleClassName(element.getEnclosingElement());
        }
    }

    private String findPackage(Element element) {
        return element.getKind() == ElementKind.PACKAGE ? element.toString() : this.findPackage(element.getEnclosingElement());
    }

    public Void visitPackage(PackageElement var1, Void var2) {
        return null;
    }

    public Void visitType(TypeElement var1, Void var2) {
        return null;
    }

    public Void visitVariable(VariableElement var1, Void var2) {
        return null;
    }

    public Void visitTypeParameter(TypeParameterElement var1, Void var2) {
        return null;
    }

    public Void visitUnknown(Element var1, Void var2) {
        return null;
    }

    private void store() throws IOException {
        for (Entry entry : this.classes.entrySet()) {
            ClassParameterInformation classParameterInformation = (ClassParameterInformation)entry.getValue();
            Object object = null;
            FileObject fileObject = this.filer
                .createResource(
                    StandardLocation.CLASS_OUTPUT,
                    classParameterInformation.getPackageName(),
                    classParameterInformation.getSimpleClassName() + ".luadebugdata",
                    (Element[])object
                );
            OutputStream outputStream = fileObject.openOutputStream();
            classParameterInformation.saveToStream(outputStream);
            outputStream.close();
        }
    }
}
