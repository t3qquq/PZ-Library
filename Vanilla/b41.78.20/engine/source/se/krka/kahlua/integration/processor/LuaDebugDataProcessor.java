/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.integration.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import se.krka.kahlua.integration.processor.ClassParameterInformation;
import se.krka.kahlua.integration.processor.DescriptorUtil;
import se.krka.kahlua.integration.processor.MethodParameterInformation;

public class LuaDebugDataProcessor
implements Processor,
ElementVisitor<Void, Void> {
    private HashMap<String, ClassParameterInformation> classes;
    private Filer filer;

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String string) {
        return new HashSet();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add(LuaMethod.class.getName());
        hashSet.add(LuaConstructor.class.getName());
        return hashSet;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public void init(ProcessingEnvironment processingEnvironment) {
        this.filer = processingEnvironment.getFiler();
        this.classes = new HashMap();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (TypeElement typeElement : set) {
            Set<? extends Element> set2 = roundEnvironment.getElementsAnnotatedWith(typeElement);
            for (Element element : set2) {
                element.accept(this, null);
            }
        }
        if (roundEnvironment.processingOver()) {
            try {
                this.store();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public Void visit(Element element) {
        return null;
    }

    @Override
    public Void visit(Element element, Void void_) {
        return null;
    }

    @Override
    public Void visitExecutable(ExecutableElement executableElement, Void void_) {
        String string = this.findClass(executableElement);
        String string2 = this.findPackage(executableElement);
        ClassParameterInformation classParameterInformation = this.getOrCreate(this.classes, string, string2, this.findSimpleClassName(executableElement));
        String string3 = executableElement.getSimpleName().toString();
        String string4 = DescriptorUtil.getDescriptor(string3, executableElement.getParameters());
        ArrayList<String> arrayList = new ArrayList<String>();
        for (VariableElement variableElement : executableElement.getParameters()) {
            arrayList.add(variableElement.getSimpleName().toString());
        }
        MethodParameterInformation methodParameterInformation = new MethodParameterInformation(arrayList);
        classParameterInformation.methods.put(string4, methodParameterInformation);
        return null;
    }

    private ClassParameterInformation getOrCreate(HashMap<String, ClassParameterInformation> hashMap, String string, String string2, String string3) {
        ClassParameterInformation classParameterInformation = hashMap.get(string);
        if (classParameterInformation == null) {
            classParameterInformation = new ClassParameterInformation(string2, string3);
            hashMap.put(string, classParameterInformation);
        }
        return classParameterInformation;
    }

    private String findClass(Element element) {
        if (element.getKind() == ElementKind.CLASS) {
            return element.toString();
        }
        return this.findClass(element.getEnclosingElement());
    }

    private String findSimpleClassName(Element element) {
        if (element.getKind() == ElementKind.CLASS) {
            String string = element.getSimpleName().toString();
            if (element.getEnclosingElement().getKind() == ElementKind.CLASS) {
                return this.findSimpleClassName(element.getEnclosingElement()) + "_" + string;
            }
            return string;
        }
        return this.findSimpleClassName(element.getEnclosingElement());
    }

    private String findPackage(Element element) {
        if (element.getKind() == ElementKind.PACKAGE) {
            return element.toString();
        }
        return this.findPackage(element.getEnclosingElement());
    }

    @Override
    public Void visitPackage(PackageElement packageElement, Void void_) {
        return null;
    }

    @Override
    public Void visitType(TypeElement typeElement, Void void_) {
        return null;
    }

    @Override
    public Void visitVariable(VariableElement variableElement, Void void_) {
        return null;
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement typeParameterElement, Void void_) {
        return null;
    }

    @Override
    public Void visitUnknown(Element element, Void void_) {
        return null;
    }

    private void store() throws IOException {
        for (Map.Entry<String, ClassParameterInformation> entry : this.classes.entrySet()) {
            ClassParameterInformation classParameterInformation = entry.getValue();
            Element[] elementArray = null;
            FileObject fileObject = this.filer.createResource(StandardLocation.CLASS_OUTPUT, classParameterInformation.getPackageName(), classParameterInformation.getSimpleClassName() + ".luadebugdata", elementArray);
            OutputStream outputStream = fileObject.openOutputStream();
            classParameterInformation.saveToStream(outputStream);
            outputStream.close();
        }
    }
}

