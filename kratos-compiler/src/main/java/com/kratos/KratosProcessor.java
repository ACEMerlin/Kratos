package com.kratos;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

@AutoService(Processor.class)
public class KratosProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private static final String BINDING_CLASS_SUFFIX = "$$KBinder";
    private static final String NULLABLE_ANNOTATION_NAME = "Nullable";
    static final String VIEW_TYPE = "android.view.View";


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        //支持的annotation类型
        types.add(BindText.class.getCanonicalName());
        types.add(BindString.class.getCanonicalName());
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Map<TypeElement, BindingClass> targetClassMap = findAndParseTargets(env);

        for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BindingClass BindingClass = entry.getValue();

            try {
                BindingClass.brewJava().writeTo(filer);
            } catch (IOException e) {
                error(typeElement, "Unable to write view binder for type %s: %s", typeElement,
                        e.getMessage());
            }
        }

        return true;
    }

    private Map<TypeElement, BindingClass> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<>();
        Set<String> erasedTargetNames = new LinkedHashSet<>();

        // Process each @Bind element.
        for (Element element : env.getElementsAnnotatedWith(BindText.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseBindText(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {
                logParsingError(element, BindText.class, e);
            }
        }

        for (Element element : env.getElementsAnnotatedWith(BindString.class)) {
            processingEnv.getMessager().printMessage(NOTE, "shit!!!!!");
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseBindString(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {
                logParsingError(element, BindString.class, e);
            }
        }

        // Try to find a parent binder for each.
        for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
            String parentClassFqcn = findParentFqcn(entry.getKey(), erasedTargetNames);
            if (parentClassFqcn != null) {
                entry.getValue().setParentViewBinder(parentClassFqcn + BINDING_CLASS_SUFFIX);
            }
        }

        return targetClassMap;
    }

    private void parseBindString(Element element, Map<TypeElement, BindingClass> targetClassMap,
                               Set<String> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        TypeMirror elementType = element.asType();
        if (elementType.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }
        // Assemble information on the field.
        String[] ids = element.getAnnotation(BindString.class).value();
        BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        for (String id : ids) {
            if (bindingClass != null) {
                KBindings bindings = bindingClass.getKBindings(String.valueOf(id));
                if (bindings != null) {
                    Iterator<FieldViewBinding> iterator = bindings.getFieldBindings().iterator();
                    if (iterator.hasNext()) {
                        FieldViewBinding existingBinding = iterator.next();
                        error(element, "Attempt to use @%s for an already bound ID %s on '%s'. (%s.%s)",
                                BindString.class.getSimpleName(), id, existingBinding.getName(),
                                enclosingElement.getQualifiedName(), element.getSimpleName());
                        return;
                    }
                }
            } else {
                bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
            }
            String name = element.getSimpleName().toString();
            TypeName type = TypeName.get(elementType);
            boolean required = isRequiredBinding(element);

            FieldViewBinding binding = new FieldViewBinding(name, type, required);
            bindingClass.addField(String.valueOf(id), binding);
        }

        // Add the type-erased version to the valid binding targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private void parseBindText(Element element, Map<TypeElement, BindingClass> targetClassMap,
                               Set<String> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        TypeMirror elementType = element.asType();
        if (elementType.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }
        // Assemble information on the field.
        int[] ids = element.getAnnotation(BindText.class).value();
        BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        for (int id : ids) {
            if (bindingClass != null) {
                KBindings bindings = bindingClass.getKBindings(String.valueOf(id));
                if (bindings != null) {
                    Iterator<FieldViewBinding> iterator = bindings.getFieldBindings().iterator();
                    if (iterator.hasNext()) {
                        FieldViewBinding existingBinding = iterator.next();
                        error(element, "Attempt to use @%s for an already bound ID %s on '%s'. (%s.%s)",
                                BindText.class.getSimpleName(), id, existingBinding.getName(),
                                enclosingElement.getQualifiedName(), element.getSimpleName());
                        return;
                    }
                }
            } else {
                bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
            }
            String name = element.getSimpleName().toString();
            TypeName type = TypeName.get(elementType);
            boolean required = isRequiredBinding(element);

            FieldViewBinding binding = new FieldViewBinding(name, type, required);
            bindingClass.addField(String.valueOf(id), binding);
        }

        // Add the type-erased version to the valid binding targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private static boolean isRequiredBinding(Element element) {
        return !hasAnnotationWithName(element, NULLABLE_ANNOTATION_NAME);
    }

    private static boolean hasAnnotationWithName(Element element, String simpleName) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
            if (simpleName.equals(annotationName)) {
                return true;
            }
        }
        return false;
    }

    private String findParentFqcn(TypeElement typeElement, Set<String> parents) {
        TypeMirror type;
        while (true) {
            type = typeElement.getSuperclass();
            if (type.getKind() == TypeKind.NONE) {
                return null;
            }
            typeElement = (TypeElement) ((DeclaredType) type).asElement();
            if (parents.contains(typeElement.toString())) {
                String packageName = getPackageName(typeElement);
                return packageName + "." + getClassName(typeElement, packageName);
            }
        }
    }

    private BindingClass getOrCreateTargetClass(Map<TypeElement, BindingClass> targetClassMap,
                                               TypeElement enclosingElement) {
        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + BINDING_CLASS_SUFFIX;
            String[] three = classPackage.split("\\.");
            bindingClass = new BindingClass(classPackage, className, targetType, three[0] + "." + three[1] + "." + three[2]);
            targetClassMap.put(enclosingElement, bindingClass);
        }
        return bindingClass;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }

    private void logParsingError(Element element, Class<? extends Annotation> annotation,
                                 Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        error(element, "Unable to parse @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
    }

}
