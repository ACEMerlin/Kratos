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

import javax.lang.model.element.Modifier;

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
    private static final ClassName VIEW = ClassName.get("android.view", "View");
    private static final ClassName ONUPDATELISTENER = ClassName.get("kratos.internal.KString", "OnUpdateListener");
    static final int NO_ID = -1;
    private final boolean isLibrary;
    private final boolean isKotlin;

    private final Map<String, KBindings> viewIdMap = new LinkedHashMap<>();
    private final Map<String, UpdateKStringBinding> updateKStringBindingMap = new LinkedHashMap<>();
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
            result.addStatement("target.init()");
        }
        if (!updateKStringBindingMap.isEmpty()) {
            for (Map.Entry<String, UpdateKStringBinding> entry : updateKStringBindingMap.entrySet()) {
                addKStringUpdateBindings(result, entry);
            }
        }
        if (!viewIdMap.isEmpty()) {
            result.addStatement("$T view", VIEW);
            // Loop over each view bindings and emit it.
            for (KBindings bindings : viewIdMap.values()) {
                addKBindings(result, bindings);
            }
        }
        if (!doubleBindingMap.isEmpty()) {
            if (viewIdMap.isEmpty()) {
                result.addStatement("$T view", VIEW);
            }
            for (Map.Entry<Integer, String> entry : doubleBindingMap.entrySet()) {
                addDoubleBindings(result, entry);
            }
        }
        return result.build();
    }

    private void addKStringUpdateBindings(MethodSpec.Builder result, Map.Entry<String, UpdateKStringBinding> entry) {
        UpdateKStringBinding binding = entry.getValue();
        TypeSpec update = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ONUPDATELISTENER)
                .addMethod(MethodSpec.methodBuilder("update")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(VIEW, "v")
                        .addParameter(String.class, "s")
                        .returns(void.class)
                        .addStatement("target.$L(($L)$N, $N)", binding.getMethodName(), binding.getParameterTypes()[0], "v", "s")
                        .build())
                .build();
        result.addStatement("target.getData().$L.setOnUpdateListener($L)", entry.getKey(), update);
    }

    private void addDoubleBindings(MethodSpec.Builder result, Map.Entry<Integer, String> entry) {
        result.addStatement("view = finder.findRequiredView(target, $L)", entry.getKey());
        result.addStatement("target.getData().$L.bind(view)", entry.getValue());
        result.addStatement("target.getData().$L.set(target.getData().$L.getInitData())", entry.getValue(), entry.getValue());
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
                result.addStatement("$T $L = finder.castView(view)", VIEW, fieldBinding.getName() + bindings.getId());
            } else {
                result.addStatement("$T $L = view", VIEW, fieldBinding.getName() + bindings.getId());
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

    private Map<Integer, String> doubleBindingMap = new LinkedHashMap<>();

    public void addDoubleBinding(int id, String data) {
        String mData = doubleBindingMap.get(id);
        if (mData == null) {
            doubleBindingMap.put(id, data);
        }
    }

    public void addKStringUpdateBinding(String kstring, UpdateKStringBinding binding) {
        UpdateKStringBinding mData = updateKStringBindingMap.get(kstring);
        if (mData == null) {
            updateKStringBindingMap.put(kstring, binding);
        }
    }
}
