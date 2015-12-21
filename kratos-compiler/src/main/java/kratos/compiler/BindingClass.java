package kratos.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by merlin on 15/12/7.
 */
public class BindingClass {

    private final String classPackage;
    private final String className;
    private final String targetClass;
    private final ClassName resClass;
    private String parentViewBinder;
    private static final ClassName KBINDER = ClassName.get("kratos.internal", "KBinder");
    private static final ClassName KFINDER = ClassName.get("kratos.internal", "KFinder");
    static final int NO_ID = -1;
    private final boolean isLibrary;
    private final boolean isKotlin;

    private final Map<String, KBindings> viewIdMap = new LinkedHashMap<>();
    private String layoutId;

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    BindingClass(String classPackage, String className, String targetClass, String resPackage, Boolean isLibrary, Boolean isKotlin) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
        this.resClass = ClassName.get(resPackage, "R");
        this.isLibrary = isLibrary;
        this.isKotlin = isKotlin;
    }

    void setParentViewBinder(String parentViewBinder) {
        this.parentViewBinder = parentViewBinder;
    }

    JavaFile brewJava() {
        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC)
                .addTypeVariable(TypeVariableName.get("T", ClassName.bestGuess(targetClass)));

        if (parentViewBinder != null) {
            result.superclass(ParameterizedTypeName.get(ClassName.bestGuess(parentViewBinder),
                    TypeVariableName.get("T")));
        } else {
            result.addSuperinterface(ParameterizedTypeName.get(KBINDER, TypeVariableName.get("T")));
        }

        result.addMethod(createBindMethod());

        return JavaFile.builder(classPackage, result.build())
                .addFileComment("Generated code from Kratos. Do not modify!")
                .build();
    }

    private MethodSpec createBindMethod() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("bind")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeVariableName.get("T"), "target", FINAL)
                .addParameter(KFINDER, "finder", FINAL);
        if (parentViewBinder != null) {
            result.addStatement("super.bind(target, finder)");
        }
        if (layoutId != null) {
            if (!isLibrary)
                result.addStatement("target.setLayoutId($L)", Integer.parseInt(layoutId));
            else
                result.addStatement("target.setLayoutId($T.layout.$L)", resClass, layoutId);
        }
        if (!viewIdMap.isEmpty()) {
            result.addStatement("$T view", ClassName.get("android.view", "View"));
            // Loop over each view bindings and emit it.
            for (KBindings bindings : viewIdMap.values()) {
                addKBindings(result, bindings);
            }
        }
        return result.build();
    }
    private void addKBindings(MethodSpec.Builder result, KBindings bindings) {
        List<KBinding> requiredViewBindings = bindings.getRequiredBindings();
        if (!isLibrary) {
            if (requiredViewBindings.isEmpty()) {
                result.addStatement("view = finder.findOptionalView(target, $L)", bindings.getId());
            } else {
                int id = Integer.parseInt(bindings.getId());
                if (id == NO_ID) {
                    result.addStatement("view = target");
                } else {
                    result.addStatement("view = finder.findRequiredView(target, $L)", id);
                }
            }
        } else {
            if (requiredViewBindings.isEmpty()) {
                result.addStatement("view = finder.findOptionalView(target, $T.id.$L)", resClass, bindings.getId());
            } else {
                if (bindings.getId() == null) {
                    result.addStatement("view = target");
                } else {
                    result.addStatement("view = finder.findRequiredView(target, $T.id.$L)", resClass, bindings.getId());
                }
            }
        }
        addFieldBindings(result, bindings);
    }

    private void addFieldBindings(MethodSpec.Builder result, KBindings bindings) {
        Collection<FieldViewBinding> fieldBindings = bindings.getFieldBindings();
        for (FieldViewBinding fieldBinding : fieldBindings) {
            if (fieldBinding.requiresCast()) {
                result.addStatement("$T $L = finder.castView(view)", ClassName.get("android.view", "View"), fieldBinding.getName() + bindings.getId());
            } else {
                result.addStatement("$T $L = view", ClassName.get("android.view", "View"), fieldBinding.getName() + bindings.getId());
            }
            if (isKotlin) {
                String name = fieldBinding.getName();
                result.addStatement("target.get$L().bind($L)", name.substring(0, 1).toUpperCase() + name.substring(1, name.length()), name + bindings.getId());
            } else {
                result.addStatement("target.$L.bind($L)", fieldBinding.getName(), fieldBinding.getName() + bindings.getId());
            }
        }
    }

    public KBindings getKBindings(String id) {
        return viewIdMap.get(id);
    }

    void addField(String id, FieldViewBinding binding) {
        getOrCreateViewBindings(id).addFieldBinding(binding);
    }

    private KBindings getOrCreateViewBindings(String id) {
        KBindings viewId = viewIdMap.get(id);
        if (viewId == null) {
            viewId = new KBindings(id);
            viewIdMap.put(id, viewId);
        }
        return viewId;
    }
}
