package kratos.compiler.binding;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * Created by merlin on 15/12/24.
 */
public class KBooleanBinding implements KBindingGeneric {

    private String methodName;
    private String[] parameterTypes;
    private static final ClassName ONUPDATELISTENER_KBOOLEAN = ClassName.get("kratos.internal.KBoolean", "OnUpdateListener");
    private static final ClassName VIEW = ClassName.get("android.view", "View");

    public KBooleanBinding(String methodName, String[] parameterTypes) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public void addGenericCode(MethodSpec.Builder result, String key) {
        TypeSpec update = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ONUPDATELISTENER_KBOOLEAN)
                .addMethod(MethodSpec.methodBuilder("update")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(VIEW, "v")
                        .addParameter(boolean.class, "s")
                        .returns(void.class)
                        .addStatement("target.$L(($L)$N, $N)", getMethodName(), getParameterTypes()[0], "v", "s")
                        .build())
                .build();
        result.addStatement("target.getData().$L.setOnUpdateListener($L)", key, update);
    }
}
